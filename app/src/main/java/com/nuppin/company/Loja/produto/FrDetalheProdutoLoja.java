package com.nuppin.company.Loja.produto;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.AsyncTaskLoader;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.gson.Gson;
import com.google.gson.JsonParser;
import com.nuppin.company.Loja.dialogs.InfoDialogFragment;
import com.nuppin.company.Loja.produto.size.ProductSizeDialog;
import com.nuppin.company.Util.AppConstants;
import com.nuppin.company.Util.UtilShaPre;
import com.nuppin.company.connection.ConnectApi;
import com.nuppin.company.model.Collection;
import com.nuppin.company.model.CollectionExtra;
import com.nuppin.company.model.Product;
import com.nuppin.company.R;
import com.nuppin.company.Util.Util;
import com.nuppin.company.model.ProductCollection;
import com.nuppin.company.model.Size;
import com.stfalcon.frescoimageviewer.ImageViewer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.toptas.fancyshowcase.FancyShowCaseView;

public class FrDetalheProdutoLoja extends Fragment
        implements LoaderManager.LoaderCallbacks<Map<String, Object>>,
        ConjuctHeaderAdapter.ConjuctHeaderAdapterListener,
        ProductConjuctDialog.SeekbarDialogListener,
        ProductSizeDialog.SeekbarDialogListener,
        ProducSizeAdapter.ConjuctBodyExtrasAdapterListener,
        InfoDialogFragment.InfoDialogListener {

    private LoaderManager loaderManager;
    private static final String EXTRA_PRODUCT = "product";
    private Product product;
    private SimpleDraweeView image;
    private ConjuctHeaderAdapter adapter;
    private ProducSizeAdapter sizeAdapter;
    private RecyclerView recyclerView, recyclerViewSizes;
    private CardView cardSizes;
    private Collection collection;
    private List<ProductCollection> productCollection = new ArrayList<>();
    private List<CollectionExtra> collectionExtra;
    private LottieAnimationView dots;
    private LinearLayout linearEdit, linearExtra, linearStock;
    private FloatingActionButton fab, fabEdit, fabExtra, fabStock;
    private boolean isFabOpen = false;
    private ConstraintLayout constFab;
    private TextView linearEmpty;
    private CardView progress;
    private Map<String, Object> data;
    View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.fabEdit:
                    if (getActivity() instanceof ToActivity) {
                        ToActivity listener = (ToActivity) getActivity();
                        listener.DetalheProdutoBottom(R.id.fab, product);
                    }
                    break;
                case R.id.fabStock:
                    if (getActivity() instanceof ToActivity) {
                        ToActivity listener = (ToActivity) getActivity();
                        listener.addSize(product);
                    }
                    break;
                case R.id.fabExtra:
                    DialogFragment dialogFrag = ProductConjuctDialog.newInstance(product, getResources().getString(R.string.confirmar), getResources().getString(R.string.revisar));
                    dialogFrag.show(FrDetalheProdutoLoja.this.getChildFragmentManager(), "confirm");
                    break;
            }
            closeFab();
        }
    };


    private FancyShowCaseView fancyShowCaseView1;

    private void chainCreatedTutorial() {
        try {
            if (fancyShowCaseView1 == null) {
                fancyShowCaseView1 = new FancyShowCaseView.Builder(requireActivity())
                        .focusOn(fab)
                        .delay(500)
                        .title(getString(R.string.unique_tutorial_detalhe_produto_fab_string))
                        .showOnce(getString(R.string.unique_tutorial_detalhe_produto_fab))
                        .backgroundColor(getResources().getColor(R.color.primary_light))
                        .enableAutoTextPosition()
                        .closeOnTouch(true)
                        .build();
            }
        } catch (Exception e) {
            return;
        }
    }

    private void chainTutorial() {
        try {
            if (!fancyShowCaseView1.isShownBefore()) {
                fancyShowCaseView1.show();
                return;
            }
        } catch (Exception e) {
            return;
        }
    }


    public static FrDetalheProdutoLoja novaIntancia(Product product) {
        Bundle parametros = new Bundle();
        parametros.putParcelable(EXTRA_PRODUCT, product);
        FrDetalheProdutoLoja fragment = new FrDetalheProdutoLoja();
        fragment.setArguments(parametros);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null && getArguments().containsKey(EXTRA_PRODUCT)) {
            product = getArguments().getParcelable(EXTRA_PRODUCT);
            loaderManager = LoaderManager.getInstance(this);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fr_product_detail, container, false);
        Toolbar toolbar = v.findViewById(R.id.toolbar_top);
        Util.setaToolbar(this, R.string.detalhe_toolbar, toolbar, getActivity(), false, 0);

        if (product.getProduct_id() == null) {
            Toast.makeText(getContext(), "Produto deletado", Toast.LENGTH_SHORT).show();
            Util.backFragmentFunction(this);
        }

        linearEmpty = v.findViewById(R.id.linearEmpty);
        progress = v.findViewById(R.id.progress);
        dots = v.findViewById(R.id.dots);
        TextView nome = v.findViewById(R.id.nome);
        TextView descricao = v.findViewById(R.id.desc);
        TextView preco = v.findViewById(R.id.preco);
        image = v.findViewById(R.id.imagem);
        cardSizes = v.findViewById(R.id.cardSizes);

        constFab = v.findViewById(R.id.constFab);
        linearEdit = v.findViewById(R.id.linearEdit);
        linearExtra = v.findViewById(R.id.linearExtra);
        linearStock = v.findViewById(R.id.linearStock);

        fab = v.findViewById(R.id.fab);
        fabEdit = v.findViewById(R.id.fabEdit);
        fabExtra = v.findViewById(R.id.fabExtra);
        fabStock = v.findViewById(R.id.fabStock);

        fabEdit.setOnClickListener(listener);
        fabExtra.setOnClickListener(listener);
        fabStock.setOnClickListener(listener);

        Util.hasPhoto(product, image);
        nome.setText(product.getName());
        preco.setText(Util.formaterPrice(product.getPrice()));

        if (product.getDescription() != null && product.getDescription().trim().equals("")) {
            descricao.setText(getString(R.string.sem_descricao));
        } else {
            descricao.setText(product.getDescription());
        }

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isFabOpen) {
                    openFab();
                } else {
                    closeFab();
                }
            }
        });

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    String[] listimages = {product.getphoto()};
                    if (product.getphoto() != null && !product.getphoto().equals("")) {
                        new ImageViewer.Builder(getContext(), listimages)
                                .show();
                    }
                } catch (Exception e) {
                    Toast.makeText(getContext(), "Erro ao abrir a foto. Tente novamente", Toast.LENGTH_SHORT).show();
                }

            }
        });

        recyclerView = v.findViewById(R.id.conjuct_recycler);
        recyclerViewSizes = v.findViewById(R.id.stock_recycler);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false);
        LinearLayoutManager layoutManager2 = new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerViewSizes.setLayoutManager(layoutManager2);

        adapter = new ConjuctHeaderAdapter(this);
        recyclerView.setAdapter(adapter);
        sizeAdapter = new ProducSizeAdapter(this);
        recyclerViewSizes.setAdapter(sizeAdapter);

        closeFab();
        loaderManager.restartLoader(0, null, this);

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);

        chainCreatedTutorial();

        return v;
    }

    @Override
    public void onPause() {
        super.onPause();
        loaderManager.destroyLoader(0);
    }

    private void openFab() {
        constFab.setVisibility(View.VISIBLE);
        fab.setImageResource(R.drawable.ic_keyboard_arrow_down_black_24dp);
        isFabOpen = true;
    }

    private void closeFab() {
        constFab.setVisibility(View.GONE);
        fab.setImageResource(R.drawable.ic_keyboard_arrow_up_black_24dp);
        isFabOpen = false;
    }

    @NonNull
    @Override
    public Loader<Map<String, Object>> onCreateLoader(int id, Bundle args) {
        if (id == 0) {
            return new GetProduct(getActivity(), product, data);
        } else if (id == 1) {
            return new DeleteProductConjuct(getActivity(), collection);
        } else {
            return new UpdateProductConjuctPosition(getActivity(), productCollection);
        }
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Map<String, Object>> loader, Map<String, Object> data) {
        dots.setVisibility(View.GONE);
        progress.setVisibility(View.GONE);

        if (data != null) {
            switch (loader.getId()) {
                case 0:
                    this.data = data;
                    if (data.get(AppConstants.PRODUCT) instanceof Product) {
                        product = (Product) data.get(AppConstants.PRODUCT);
                        Util.hasPhoto(product, image);
                        if (product.getIs_multi_stock() == 1) {
                            cardSizes.setVisibility(View.VISIBLE);
                            linearStock.setVisibility(View.VISIBLE);
                        }else {
                            cardSizes.setVisibility(View.GONE);
                            linearStock.setVisibility(View.GONE);
                        }
                    }
                    if (data.get(AppConstants.COLLECTION) instanceof List) {
                        collectionExtra = (List) data.get(AppConstants.COLLECTION);
                        adapter.setConjuct((List) data.get(AppConstants.COLLECTION));
                    }

                    if (data.get(AppConstants.MULTI_STOCK) instanceof List) {
                        if (((List) data.get(AppConstants.MULTI_STOCK)).size() > 0) {
                            recyclerViewSizes.setVisibility(View.VISIBLE);
                            linearEmpty.setVisibility(View.GONE);
                        }else{
                            recyclerViewSizes.setVisibility(View.GONE);
                            linearEmpty.setVisibility(View.VISIBLE);
                        }
                        sizeAdapter.setSizes((List) data.get(AppConstants.MULTI_STOCK));
                    }
                    chainTutorial();
                    break;
                case 1:
                    if (data.get("delete") instanceof Integer) {
                        if ((Integer) data.get("delete") > 0) {
                            Toast.makeText(getContext(), "Excluiu", Toast.LENGTH_SHORT).show();
                            loaderManager.restartLoader(0, null, this);
                        } else {
                            Toast.makeText(getContext(), R.string.error, Toast.LENGTH_SHORT).show();
                        }
                    }
                    break;
                case 2:
                    loaderManager.restartLoader(0, null, this);
                    break;
            }

        } else {
            Toast.makeText(getContext(), "error", Toast.LENGTH_SHORT).show();
        }
        if (loader.getId() != 0) {
            loaderManager.destroyLoader(loader.getId());
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Map<String, Object>> loader) {
    }

    @Override
    public void onDialogOKClick(View view, String type, ProductConjuctDialog infoDialogFragment) {
        if (!type.equals(AppConstants.COLLECTION)) {
            if (view.getId() == R.id.btnPositive) {
                loaderManager.restartLoader(0, null, this);
                infoDialogFragment.dismiss();
            } else {
                infoDialogFragment.dismiss();
            }
        } else {
            infoDialogFragment.dismiss();
            DialogFragment dialogFrag = InfoDialogFragment.newInstance("Deseja criar um novo grupo?", getResources().getString(R.string.confirmar), getResources().getString(R.string.revisar));
            dialogFrag.show(FrDetalheProdutoLoja.this.getChildFragmentManager(), "item");
        }
    }

    @Override
    public void onDialogOKClick(View view, InfoDialogFragment infoDialogFragment) {
        if (view.getId() == R.id.btnPositive) {
            if (infoDialogFragment.getTag().equals("item")) {
                if (getActivity() instanceof ToActivity) {
                    ToActivity listener = (ToActivity) getActivity();
                    listener.DetalheProdutoBottom(1, product);
                }
            } else if(infoDialogFragment.equals("tamanho")){
                if (getActivity() instanceof ToActivity) {
                    ToActivity listener = (ToActivity) getActivity();
                    listener.DetalheProdutoBottom(2, product);
                }
            }else if(infoDialogFragment.getTag().equals("collection")){
                progress.setVisibility(View.VISIBLE);
                loaderManager.restartLoader(1, null, this);
            }
            infoDialogFragment.dismiss();
        } else {
            infoDialogFragment.dismiss();
        }
    }

    @Override
    public void onHeaderClick(int index, Collection collection) {
        this.collection = collection;
        DialogFragment dialogFrag = InfoDialogFragment.newInstance("Deseja mesmo exluir?", getResources().getString(R.string.confirmar), getResources().getString(R.string.revisar));
        dialogFrag.show(FrDetalheProdutoLoja.this.getChildFragmentManager(), "collection");
    }

    @Override
    public void onDialogOKClick(View view, String type, Size size, ProductSizeDialog infoDialogFragment) {
        if (!type.equals(AppConstants.COLLECTION)) {
            if (view.getId() == R.id.btnPositive) {
                if (getActivity() instanceof ToActivity) {
                    ToActivity listener = (ToActivity) getActivity();
                    listener.addSize(product);
                }
            } else {
                infoDialogFragment.dismiss();
            }
        } else {
            infoDialogFragment.dismiss();
            DialogFragment dialogFrag = InfoDialogFragment.newInstance("Deseja criar um novo tamanho?", getResources().getString(R.string.confirmar), getResources().getString(R.string.revisar));
            dialogFrag.show(FrDetalheProdutoLoja.this.getChildFragmentManager(), "tamanho");
        }
    }

    @Override
    public void onClickProducSize(Size size) {
        if (getActivity() instanceof ToActivity) {
            ToActivity listener = (ToActivity) getActivity();
            listener.editSize(size);
        }
    }



    ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.START | ItemTouchHelper.END, 0) {
        private boolean mOrderChanged;

        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {

            int fromPosition = viewHolder.getAdapterPosition();
            int toPosition = target.getAdapterPosition();
            Collections.swap(collectionExtra, fromPosition, toPosition);
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
                for (int i = 0; i < collectionExtra.size(); i++) {
                    ProductCollection p = new ProductCollection();
                    p.setProduct_id(collectionExtra.get(i).getProduct_id());
                    p.setCollection_id(collectionExtra.get(i).getCollection_id());
                    p.setPosition(i);
                    p.setCompany_id(collectionExtra.get(i).getCompany_id());
                    productCollection.add(p);
                }
                loaderManager.restartLoader(2, null, FrDetalheProdutoLoja.this);
                mOrderChanged = false;
            }
        }
    };



    private static class GetProduct extends AsyncTaskLoader<Map<String, Object>> {

        Context ctx;
        Product product;
        Map<String, Object> data;

        GetProduct(Context context, Product product, Map<String, Object> data) {
            super(context);
            ctx = context;
            this.product = product;
            this.data = data;
        }


        @Override
        protected void onStartLoading() {
            if (data != null) {
                deliverResult(data);
            }
            forceLoad();
        }


        @Override
        public Map<String, Object> loadInBackground() {
            Gson gson = new Gson();
            Map<String, Object> mapOrdPro;
            mapOrdPro = new HashMap<>();
            JsonParser parser = new JsonParser();

            try {
                String json = ConnectApi.GET(ConnectApi.PRODUCT_ITEM + "/" + product.getProduct_id(), getContext());
                mapOrdPro.put(AppConstants.PRODUCT, gson.fromJson(parser.parse(json).getAsJsonObject().getAsJsonObject(AppConstants.PRODUCT), Product.class));
                try {
                    CollectionExtra[] extras = gson.fromJson(parser.parse(json).getAsJsonObject().getAsJsonArray(AppConstants.COLLECTION), CollectionExtra[].class);
                    mapOrdPro.put(AppConstants.COLLECTION, new ArrayList<>(Arrays.asList(extras)));
                } catch (Exception e) {
                    FirebaseCrashlytics.getInstance().log(UtilShaPre.getDefaultsString(AppConstants.USER_ID, getContext()));
                    FirebaseCrashlytics.getInstance().recordException(e);
                }
                try {
                    Size[] sizes = gson.fromJson(parser.parse(json).getAsJsonObject().getAsJsonArray(AppConstants.MULTI_STOCK), Size[].class);
                    mapOrdPro.put(AppConstants.MULTI_STOCK, new ArrayList<>(Arrays.asList(sizes)));
                } catch (Exception e) {
                    FirebaseCrashlytics.getInstance().log(UtilShaPre.getDefaultsString(AppConstants.USER_ID, getContext()));
                    FirebaseCrashlytics.getInstance().recordException(e);
                }
            } catch (Exception e) {
                FirebaseCrashlytics.getInstance().log(UtilShaPre.getDefaultsString(AppConstants.USER_ID, getContext()));
                FirebaseCrashlytics.getInstance().recordException(e);
                return null;
            }
            return mapOrdPro;
        }
    }

    private static class DeleteProductConjuct extends AsyncTaskLoader<Map<String, Object>> {

        Activity ctx;
        Collection collection;

        DeleteProductConjuct(Activity context, Collection collection) {
            super(context);
            ctx = context;
            this.collection = collection;
        }


        @Override
        protected void onStartLoading() {
            forceLoad();
        }


        @Override
        public Map<String, Object> loadInBackground() {
            Map<String, Object> mapOrdPro;
            mapOrdPro = new HashMap<>();
            try {
                Gson gson = new Gson();
                mapOrdPro.put("delete", gson.fromJson(ConnectApi.DELETE(collection, ConnectApi.COLLECTION_PRODUCT, ctx), Integer.class));
                return mapOrdPro;
            } catch (Exception e) {
                FirebaseCrashlytics.getInstance().log(UtilShaPre.getDefaultsString(AppConstants.USER_ID, getContext()));
                FirebaseCrashlytics.getInstance().recordException(e);
                return null;
            }
        }
    }

    private static class UpdateProductConjuctPosition extends AsyncTaskLoader<Map<String, Object>> {

        Context ctx;
        List<ProductCollection> productCollection;

        UpdateProductConjuctPosition(Context context, List<ProductCollection> productCollection) {
            super(context);
            ctx = context;
            this.productCollection = productCollection;
        }

        @Override
        protected void onStartLoading() {
            forceLoad();
        }

        @Override
        public Map<String, Object> loadInBackground() {
            Gson gson = new Gson();
            Map<String, Object> mapOrdPro;
            mapOrdPro = new HashMap<>();

            try {
                mapOrdPro.put("position", gson.fromJson(ConnectApi.PATCH(productCollection, ConnectApi.COLLECTION_POSITION, getContext()), ProductCollection[].class));
                return mapOrdPro;
            } catch (Exception e) {
                FirebaseCrashlytics.getInstance().log(UtilShaPre.getDefaultsString(AppConstants.USER_ID, getContext()));
                FirebaseCrashlytics.getInstance().recordException(e);
                return null;
            }
        }
    }


    public interface ToActivity {
        void DetalheProdutoBottom(int index, Product product);

        void addSize(Product product);

        void editSize(Size product);
    }
}
