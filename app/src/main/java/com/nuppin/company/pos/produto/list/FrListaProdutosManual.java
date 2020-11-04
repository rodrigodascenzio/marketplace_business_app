package com.nuppin.company.pos.produto.list;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.AsyncTaskLoader;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.nuppin.company.Loja.dialogs.InfoDialogFragment;
import com.nuppin.company.R;
import com.nuppin.company.Util.AppConstants;
import com.nuppin.company.Util.Util;
import com.nuppin.company.Util.UtilShaPre;
import com.nuppin.company.connection.ConnectApi;
import com.nuppin.company.model.Product;
import com.nuppin.company.pos.produto.carrinho.FrCarrinhoManual;
import com.nuppin.company.pos.produto.dialog.TrocoDialogFragmentManual;
import com.nuppin.company.pos.produto.list.Alimentos.RvAlimentosAdapterManual;
import com.nuppin.company.model.CartCompany;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FrListaProdutosManual extends Fragment implements
        LoaderManager.LoaderCallbacks<Map<String, Object>>,
        RvProdutosAdapterManual.RvProdutosOnClickListener,
        RvAlimentosAdapterManual.RvProdutosOnClickListener,
        InfoDialogFragment.InfoDialogListener,
        FrDetalheProdutoManual.FrProductListener{
    private LoaderManager loaderManager;
    private RvAlimentosAdapterManual alimentosAdapter;
    private RvProdutosAdapterManual produtosAdapter;
    private RecyclerView mRecyclerView;
    private Toolbar toolbar;
    private static final String STORE = "STORE";
    private Map<String, Object> data;
    private LottieAnimationView dots;
    private NestedScrollView linearEmpty;
    private ConstraintLayout errorLayout;
    private FloatingActionButton fabError;
    private LinearLayoutManager linearLayoutManager;
    private GridLayoutManager gridLayoutManager;
    private CartCompany company;


    public static FrListaProdutosManual newInstance(CartCompany company) {
        Bundle parametros = new Bundle();
        parametros.putParcelable(STORE, company);
        FrListaProdutosManual fragment = new FrListaProdutosManual();
        fragment.setArguments(parametros);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null && getArguments().containsKey(STORE)) {
            company = getArguments().getParcelable(STORE);
        }
        loaderManager = LoaderManager.getInstance(this);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.manual_fr_list_product, container, false);

        dots = view.findViewById(R.id.dots);
        linearEmpty = view.findViewById(R.id.nested);
        errorLayout = view.findViewById(R.id.error_layout);
        fabError = view.findViewById(R.id.fabError);
        fabError.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dots.setVisibility(View.VISIBLE);
                errorLayout.setVisibility(View.GONE);
                loaderManager.restartLoader(0, null, FrListaProdutosManual.this);
            }
        });

        toolbar = view.findViewById(R.id.toolbar_top);
        Util.setaToolbar(this,R.string.produtos_toolbar,toolbar,getActivity(),false,0);

        mRecyclerView = view.findViewById(R.id.recyclerview_products);

        linearLayoutManager = new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false);
        alimentosAdapter = new RvAlimentosAdapterManual(this);

        gridLayoutManager = new GridLayoutManager(getActivity(), 2);
        produtosAdapter = new RvProdutosAdapterManual(this);


        if (company != null) {
            if (company.getCategory_company_id().equals("1")) {
                if (getResources().getBoolean(R.bool.isTablet10) || getResources().getBoolean(R.bool.isTablet7)) {
                    mRecyclerView.setLayoutManager(gridLayoutManager);
                } else {
                    mRecyclerView.setLayoutManager(linearLayoutManager);
                }
                mRecyclerView.setAdapter(alimentosAdapter);
            } else {
                if (getResources().getBoolean(R.bool.isTablet10) || getResources().getBoolean(R.bool.isTablet7)) {
                    mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));
                } else {
                    mRecyclerView.setLayoutManager(gridLayoutManager);
                }
                mRecyclerView.setAdapter(produtosAdapter);
            }

        }

        mRecyclerView.setHasFixedSize(true);

        loaderManager.restartLoader(0, null, this);
        return view;
    }


    @Override
    public void onPause() {
        super.onPause();
        loaderManager.destroyLoader(0);
    }

    @NonNull
    @Override
    public Loader<Map<String, Object>> onCreateLoader(int id, Bundle args) {
            return new ProductsLoader(getActivity(), UtilShaPre.getDefaultsString(AppConstants.COMPANY, getContext()), data);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Map<String, Object>> loader, Map<String, Object> data) {
        dots.setVisibility(View.GONE);

        this.data = data;
        if (data != null) {
            if (data.get(AppConstants.PRODUCT) instanceof List) {
                if (((List)data.get(AppConstants.PRODUCT)).size()>0){
                    if (company.getCategory_company_id().equals("1")) {
                        alimentosAdapter.setProducts((List) data.get(AppConstants.PRODUCT));
                    } else {
                        produtosAdapter.setProducts((List) data.get(AppConstants.PRODUCT));
                    }
                    mRecyclerView.setVisibility(View.VISIBLE);
                    linearEmpty.setVisibility(View.GONE);
                }else {
                    mRecyclerView.setVisibility(View.GONE);
                    linearEmpty.setVisibility(View.VISIBLE);
                }
            }
            errorLayout.setVisibility(View.GONE);
        }else {
            errorLayout.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Map<String, Object>> loader) {

    }

    @Override
    public void onClick(Product product) {
        DialogFragment dialogFrag = FrDetalheProdutoManual.novaInstancia(product.getProduct_id(), product.getCompany_id(), company.getCategory_company_id());
        dialogFrag.show(FrListaProdutosManual.this.getChildFragmentManager(), "added");
    }

    @Override
    public void onDialogOKClick(View view, InfoDialogFragment info) {
        switch (view.getId()) {
            case R.id.btnPositive:
                info.dismiss();
                break;
            case R.id.btnNegative:
                Util.backFragmentFunction(this);
                info.dismiss();
                break;
        }
    }

    @Override
    public void added() {
        DialogFragment dialogFrag = InfoDialogFragment.newInstance(getResources().getString(R.string.adicionar_mais_produtos), getResources().getString(R.string.sim),getResources().getString(R.string.nao));
        dialogFrag.show(FrListaProdutosManual.this.getChildFragmentManager(), "added");
    }

    private static class ProductsLoader extends AsyncTaskLoader<Map<String, Object>> {

        String idStore;
        Map<String, Object> data;

        ProductsLoader(Context context, String idStore, Map<String, Object> data) {
            super(context);
            this.idStore = idStore;
            this.data = data;
        }

        @Override
        protected void onStartLoading() {
            if ( data != null) {
                deliverResult(data);
            }
            forceLoad();
        }


        @Override
        public Map<String, Object> loadInBackground() {
            Gson gson = new Gson();
            List<Product> p;
            Map<String, Object> mapOrdPro;
            mapOrdPro = new HashMap<>();
            JsonParser parser = new JsonParser();

            try {
                String stringJson = ConnectApi.GET(ConnectApi.PRODUCT_COMPANY_POS + "/" + idStore +","+ UtilShaPre.getDefaultsString(AppConstants.USER_ID,getContext()),getContext());
                JsonObject jObj = parser.parse(stringJson).getAsJsonObject();
                Product[] products = gson.fromJson(jObj.getAsJsonArray(AppConstants.PRODUCT), Product[].class);
                p = new ArrayList<>(Arrays.asList(products));
                mapOrdPro.put(AppConstants.PRODUCT, p);
                return mapOrdPro;
            } catch (Exception e) {
                    FirebaseCrashlytics.getInstance().log(UtilShaPre.getDefaultsString(AppConstants.USER_ID, getContext()));
                    FirebaseCrashlytics.getInstance().recordException(e);
                return null;
            }
        }
    }
}
