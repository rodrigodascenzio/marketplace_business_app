package com.nuppin.company.Loja.produto;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.AsyncTaskLoader;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.gson.Gson;
import com.nuppin.company.Util.AppConstants;
import com.nuppin.company.Util.UtilShaPre;
import com.nuppin.company.connection.ConnectApi;
import com.nuppin.company.Loja.produto.Alimentos.RvAlimentosAdapter;
import com.nuppin.company.model.Company;
import com.nuppin.company.model.Product;
import com.nuppin.company.R;
import com.nuppin.company.Util.Util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import me.toptas.fancyshowcase.FancyShowCaseView;
import me.toptas.fancyshowcase.listener.DismissListener;

public class FrListaProdutosLoja extends Fragment implements
        LoaderManager.LoaderCallbacks<List<Product>>,
        RvProdutosAdapterLoja.RvProdutosLojaOnClickListener,
        RvAlimentosAdapter.RvProdutosOnClickListener {

    private RvProdutosAdapterLoja adapter;
    private RecyclerView mRecyclerView;
    private Company company;
    private LoaderManager loaderManager;
    private static final String EXTRA_COMPANY = "COMPANY";
    private List<Product> data;
    private LottieAnimationView dots;
    private LinearLayout linearEmpty;
    private ConstraintLayout errorLayout;
    private FloatingActionButton fabError, fab;
    private TextView iconCollection;
    private CardView progress;


    private FancyShowCaseView fancyShowCaseView1;
    private FancyShowCaseView fancyShowCaseView2;
    private DismissListener listener = new DismissListener() {
        @Override
        public void onDismiss(String s) {
            chainTutorial();
        }

        @Override
        public void onSkipped(String s) {

        }
    };

    private void chainCreatedTutorial() {
        if (fancyShowCaseView1 == null) {
            fancyShowCaseView1 = new FancyShowCaseView.Builder(requireActivity())
                    .focusOn(fab)
                    .delay(500)
                    .title(getString(R.string.unique_tutorial_lista_produto_fab_string))
                    .showOnce(getString(R.string.unique_tutorial_lista_produto_fab))
                    .backgroundColor(getResources().getColor(R.color.primary_light))
                    .enableAutoTextPosition()
                    .closeOnTouch(true)
                    .dismissListener(listener)
                    .build();
        }

        if (fancyShowCaseView2 == null) {
            fancyShowCaseView2 = new FancyShowCaseView.Builder(requireActivity())
                    .focusOn(iconCollection)
                    .title(getString(R.string.unique_tutorial_lista_produto_icon_collection_string))
                    .backgroundColor(getResources().getColor(R.color.primary_light))
                    .enableAutoTextPosition()
                    .showOnce(getString(R.string.unique_tutorial_lista_produto_icon_collection))
                    .dismissListener(listener)
                    .closeOnTouch(true)
                    .build();
        }
    }

    private void chainTutorial() {
        if (!fancyShowCaseView1.isShownBefore()) {
            fancyShowCaseView1.show();
            return;
        }
        if (!fancyShowCaseView2.isShownBefore()) {
            fancyShowCaseView2.show();
            return;
        }
    }


    private RvAlimentosAdapter alimentosAdapter;

    public static FrListaProdutosLoja novaInstancia(Company company) {
        Bundle parametros = new Bundle();
        parametros.putParcelable(EXTRA_COMPANY, company);
        FrListaProdutosLoja fragment = new FrListaProdutosLoja();
        fragment.setArguments(parametros);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null && getArguments().containsKey(EXTRA_COMPANY)) {
            company = getArguments().getParcelable(EXTRA_COMPANY);
        }
        loaderManager = LoaderManager.getInstance(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        loaderManager.destroyLoader(0);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fr_list_product, container, false);

        progress = view.findViewById(R.id.progress);
        dots = view.findViewById(R.id.dots);
        linearEmpty = view.findViewById(R.id.linearEmpty);
        errorLayout = view.findViewById(R.id.error_layout);
        fabError = view.findViewById(R.id.fabError);
        fabError.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dots.setVisibility(View.VISIBLE);
                errorLayout.setVisibility(View.GONE);
                loaderManager.restartLoader(0, null, FrListaProdutosLoja.this);
            }
        });


        Toolbar toolbar = view.findViewById(R.id.toolbar_top);
        Util.setaToolbar(this, R.string.produtos_toolbar, toolbar, getActivity(), false, 0);

        fab = view.findViewById(R.id.fab);

        fab.hide();
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getActivity() instanceof FrListaProdutosLoja.ToActivity) {
                    FrListaProdutosLoja.ToActivity listener = (FrListaProdutosLoja.ToActivity) getActivity();
                    listener.ListaProdutosBottom(R.id.fab, company);
                }
            }
        });

        iconCollection = view.findViewById(R.id.fab2);
        iconCollection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getActivity() instanceof FrListaProdutosLoja.ToActivity) {
                    FrListaProdutosLoja.ToActivity listener = (FrListaProdutosLoja.ToActivity) getActivity();
                    listener.ListaProdutosBottom(R.id.fab2, company);
                }
            }
        });

        mRecyclerView = view.findViewById(R.id.recyclerview_products);

        if (company.getCategory_company_id().equals("1")) {
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false);
            mRecyclerView.setLayoutManager(linearLayoutManager);
            alimentosAdapter = new RvAlimentosAdapter(this, requireActivity());
            mRecyclerView.setAdapter(alimentosAdapter);
        } else {
            GridLayoutManager gridLayoutManager;
            if (getResources().getBoolean(R.bool.isTablet10)) {
                gridLayoutManager = new GridLayoutManager(getActivity(), 3);
            } else {
                gridLayoutManager = new GridLayoutManager(getActivity(), 2);
            }
            mRecyclerView.setLayoutManager(gridLayoutManager);
            adapter = new RvProdutosAdapterLoja(this,requireActivity());
            mRecyclerView.setAdapter(adapter);
        }

        loaderManager.restartLoader(0, null, this);

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(mRecyclerView);

        chainCreatedTutorial();

        return view;
    }

    @NonNull
    @Override
    public Loader<List<Product>> onCreateLoader(int id, Bundle args) {
        if (id == 0) {
            return new ProductsLoader(getActivity(), company.getCompany_id(), data);

        } else {
            return new UpdateProductPosition(getActivity(), data);
        }
    }

    @Override
    public void onLoadFinished(@NonNull Loader<List<Product>> loader, List<Product> data) {
        dots.setVisibility(View.GONE);
        progress.setVisibility(View.GONE);

        if (data != null) {
            fab.show();
            chainTutorial();

            this.data = data;
            if (loader.getId() == 0) {
                if (data.size() > 0) {
                    mRecyclerView.setVisibility(View.VISIBLE);
                    linearEmpty.setVisibility(View.GONE);
                    if (company.getCategory_company_id().equals("1")) {
                        alimentosAdapter.setProducts(data);
                    } else {
                        adapter.setProducts(data);
                    }
                } else {
                    mRecyclerView.setVisibility(View.GONE);
                    linearEmpty.setVisibility(View.VISIBLE);
                }
            }
            errorLayout.setVisibility(View.GONE);
        } else {
            errorLayout.setVisibility(View.VISIBLE);
        }

        if (loader.getId() != 0) {
            if (loader.getId() == 1) {
                loaderManager.restartLoader(0, null, this);
            }
            loaderManager.destroyLoader(loader.getId());
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<List<Product>> loader) {

    }

    ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.START | ItemTouchHelper.END, 0) {
        private boolean mOrderChanged;

        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {

            int fromPosition = viewHolder.getAdapterPosition();
            int toPosition = target.getAdapterPosition();
            Collections.swap(data, fromPosition, toPosition);
            if (recyclerView.getAdapter() != null) {
                recyclerView.getAdapter().notifyItemMoved(fromPosition, toPosition);
            }
            mOrderChanged = true;
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

        }

        @Override
        public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
            super.onSelectedChanged(viewHolder, actionState);

            if (actionState == ItemTouchHelper.ACTION_STATE_IDLE && mOrderChanged) {
                progress.setVisibility(View.VISIBLE);
                for (int i = 0; i < data.size(); i++) {
                    data.get(i).setPosition(i);
                }
                loaderManager.restartLoader(1, null, FrListaProdutosLoja.this);
                mOrderChanged = false;
            }
        }

    };

    @Override
    public void onClick(Product product) {
        Activity activity = getActivity();
        if (activity instanceof RvProdutosAdapterLoja.RvProdutosLojaOnClickListener) {
            RvProdutosAdapterLoja.RvProdutosLojaOnClickListener listener =
                    (RvProdutosAdapterLoja.RvProdutosLojaOnClickListener) activity;
            listener.onClick(product);
        }
    }

    private static class ProductsLoader extends AsyncTaskLoader<List<Product>> {

        String storeId;
        Context ctx;
        List<Product> data;

        ProductsLoader(Context context, String storeId, List<Product> data) {
            super(context);
            ctx = context;
            this.storeId = storeId;
            this.data = data;
        }

        @Override
        protected void onStartLoading() {
            if (data != null && !data.isEmpty()) {
                deliverResult(data);
                forceLoad();
            } else {
                forceLoad();
            }
        }


        @Override
        public List<Product> loadInBackground() {
            Gson gson = new Gson();
            try {
                Product[] products = gson.fromJson(ConnectApi.GET(ConnectApi.PRODUCT_COMPANY + "/" + storeId, getContext()), Product[].class);
                return new ArrayList<>(Arrays.asList(products));
            } catch (Exception e) {
                FirebaseCrashlytics.getInstance().log(UtilShaPre.getDefaultsString(AppConstants.USER_ID, getContext()));
                FirebaseCrashlytics.getInstance().recordException(e);
                return null;
            }
        }
    }

    private static class UpdateProductPosition extends AsyncTaskLoader<List<Product>> {

        Context ctx;
        List<Product> product;

        UpdateProductPosition(Context context, List<Product> product) {
            super(context);
            ctx = context;
            this.product = product;
        }

        @Override
        protected void onStartLoading() {
            forceLoad();
        }

        @Override
        public List<Product> loadInBackground() {
            try {
                Gson gson = new Gson();
                return new ArrayList<>(Arrays.asList(gson.fromJson(ConnectApi.PATCH(product, ConnectApi.PRODUCT_POSITION, getContext()), Product[].class)));
            } catch (Exception e) {
                FirebaseCrashlytics.getInstance().log(UtilShaPre.getDefaultsString(AppConstants.USER_ID, getContext()));
                FirebaseCrashlytics.getInstance().recordException(e);
                return null;
            }
        }
    }

    public interface ToActivity {
        void ListaProdutosBottom(int id, Company company);
    }
}
