package com.nuppin.company.pos.produto.list;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.DialogFragment;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.AsyncTaskLoader;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.gson.Gson;
import com.google.gson.JsonParser;
import com.nuppin.company.Loja.dialogs.InfoDialogFragment;
import com.nuppin.company.R;
import com.nuppin.company.Util.AppConstants;
import com.nuppin.company.Util.Util;
import com.nuppin.company.Util.UtilShaPre;
import com.nuppin.company.connection.ConnectApi;
import com.nuppin.company.model.CollectionExtra;
import com.nuppin.company.model.Product;
import com.nuppin.company.model.Size;
import com.nuppin.company.model.Cart;
import com.nuppin.company.pos.produto.dialog.DialogUserAddress;
import com.stfalcon.frescoimageviewer.ImageViewer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class FrDetalheProdutoManual extends DialogFragment implements
        LoaderManager.LoaderCallbacks<Map<String, Object>>,
        InfoDialogFragment.InfoDialogListener,
        ConjuctHeaderAdapterManual.ConjuctHeaderAdapterListener,
        ProducSizeAdapterManual.ConjuctBodyExtrasAdapterListener {

    private Product product;
    private LoaderManager loaderManager;
    private Cart cart;
    private Button buyButton;
    private TextView txtQtd, linearEmpty;
    private ConjuctHeaderAdapterManual adapter;
    private ProducSizeAdapterManual adapterSize;
    private RecyclerView recyclerView, recyclerViewSize;
    private EditText obs;
    private NestedScrollView nested;
    private TextView descricao, qtdMais, qtdMenos, preco, nome, txtObs, indisponivel;
    private SimpleDraweeView draweeView;
    private String proId, stoId, catId, cartId;
    private static final String PRO_ID = "PRO_ID";
    private static final String STO_ID = "STO_ID";
    private static final String CAT_ID = "CAT_ID";
    private static final String CART_ID = "CART_ID";
    private boolean temItem = false;
    private LottieAnimationView dots;
    private ConstraintLayout errorLayout, linearBottom;
    private FloatingActionButton fabError;
    private CardView progress, cardSizes;
    private List<CollectionExtra> collectionExtra;
    private List<Size> sizes;
    private String TAG;
    private FrProductListener listener;

    public static FrDetalheProdutoManual novaInstancia(String proId, String stoId, String categoria) {
        Bundle parametros = new Bundle();
        parametros.putString(PRO_ID, proId);
        parametros.putString(STO_ID, stoId);
        parametros.putString(CAT_ID, categoria);
        parametros.putString("TAG", "add");
        FrDetalheProdutoManual fragment = new FrDetalheProdutoManual();
        fragment.setArguments(parametros);
        return fragment;
    }

    public static FrDetalheProdutoManual novaInstanciaEdit(String proId, String stoId, String categoria, String cartId) {
        Bundle parametros = new Bundle();
        parametros.putString(PRO_ID, proId);
        parametros.putString(STO_ID, stoId);
        parametros.putString(CAT_ID, categoria);
        parametros.putString(CART_ID, cartId);
        parametros.putString("TAG", "update");
        FrDetalheProdutoManual fragment = new FrDetalheProdutoManual();
        fragment.setArguments(parametros);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null && getArguments().containsKey(PRO_ID)) {
            proId = getArguments().getString(PRO_ID);
        }
        if (getArguments() != null && getArguments().containsKey(STO_ID)) {
            stoId = getArguments().getString(STO_ID);
        }
        if (getArguments() != null && getArguments().containsKey(CAT_ID)) {
            catId = getArguments().getString(CAT_ID);
        }
        if (getArguments() != null && getArguments().containsKey("TAG")) {
            TAG = getArguments().getString("TAG");
        }
        cart = new Cart(UtilShaPre.getDefaultsString(AppConstants.USER_ID, getActivity()), proId, stoId);
        if (getArguments() != null && getArguments().containsKey(CART_ID)) {
            cartId = getArguments().getString(CART_ID);
            cart.setCart_id(cartId);
        }

        try {
            listener = (FrProductListener) getParentFragment();
        } catch (ClassCastException e) {
            FirebaseCrashlytics.getInstance().log(UtilShaPre.getDefaultsString(AppConstants.USER_ID, getContext()));
            FirebaseCrashlytics.getInstance().recordException(e);
            throw new ClassCastException("must implement InfoListener");
        }

        loaderManager = LoaderManager.getInstance(this);
    }

    @Nullable
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View v = inflater.inflate(R.layout.manual_fr_product_detail, null);
        dots = v.findViewById(R.id.dots);
        errorLayout = v.findViewById(R.id.error_layout);
        fabError = v.findViewById(R.id.fabError);
        fabError.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dots.setVisibility(View.VISIBLE);
                errorLayout.setVisibility(View.GONE);
                loaderManager.restartLoader(1, null, FrDetalheProdutoManual.this);
            }
        });

        nested = v.findViewById(R.id.nested);
        linearEmpty = v.findViewById(R.id.linearEmpty);
        cardSizes = v.findViewById(R.id.cardSizes);
        descricao = v.findViewById(R.id.desc);
        preco = v.findViewById(R.id.preco);
        nome = v.findViewById(R.id.nome);
        draweeView = v.findViewById(R.id.imagem);
        qtdMais = v.findViewById(R.id.qtdMais);
        qtdMenos = v.findViewById(R.id.qtdMenos);
        txtQtd = v.findViewById(R.id.txtQtd);
        buyButton = v.findViewById(R.id.buyButton);
        obs = v.findViewById(R.id.obs);
        txtObs = v.findViewById(R.id.txtObs);
        indisponivel = v.findViewById(R.id.indisponivel);
        linearBottom = v.findViewById(R.id.linearBottom);
        progress = v.findViewById(R.id.progress);

        draweeView.setOnClickListener(new View.OnClickListener() {
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

        if (!catId.equals("1")) {
            obs.setVisibility(View.GONE);
            txtObs.setVisibility(View.GONE);
        }


        buyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickedButButton();
            }
        });

        qtdMais.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (product != null) {
                    if (product.getIs_stock() == 1) {
                        if (Integer.parseInt(txtQtd.getText().toString()) < product.getStock_quantity()) {
                            txtQtd.setText(String.valueOf(Integer.parseInt(txtQtd.getText().toString()) + 1));
                        }
                    } else {
                        txtQtd.setText(String.valueOf(Integer.parseInt(txtQtd.getText().toString()) + 1));
                    }
                    updateButtonValue();
                }
            }
        });

        qtdMenos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Integer.parseInt(txtQtd.getText().toString()) > 1) {
                    txtQtd.setText(String.valueOf(Integer.parseInt(String.valueOf(txtQtd.getText())) - 1));
                    updateButtonValue();
                }
            }
        });

        recyclerView = v.findViewById(R.id.conjuct_recycler);
        recyclerViewSize = v.findViewById(R.id.stock_recycler);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false);
        LinearLayoutManager layoutManager2 = new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerViewSize.setLayoutManager(layoutManager2);
        adapter = new ConjuctHeaderAdapterManual(this);
        recyclerView.setAdapter(adapter);
        adapterSize = new ProducSizeAdapterManual(this);
        recyclerViewSize.setAdapter(adapterSize);

        loaderManager.restartLoader(1, null, FrDetalheProdutoManual.this);
        builder.setView(v);
        return builder.create();
    }

    private void clickedButButton() {
        progress.setVisibility(View.VISIBLE);
        cart.setNote(obs.getText().toString());
        buyButton.setClickable(false);
        cart.setCart_extras(collectionExtra, UtilShaPre.getDefaultsString(AppConstants.USER_ID, getContext()), product.getProduct_id());
        loaderManager.restartLoader(0, null, FrDetalheProdutoManual.this);
    }

    @Override
    public void onDialogOKClick(View view, InfoDialogFragment info) {
        switch (view.getId()) {
            case R.id.btnPositive:
                info.dismiss();
                clickedButButton();
                break;
            case R.id.btnNegative:
                info.dismiss();
                break;
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        loaderManager.destroyLoader(1);
    }

    @NonNull
    @Override
    public Loader<Map<String, Object>> onCreateLoader(int id, Bundle args) {
        if (id == 0) {
            return new AddUpdateToCart(getActivity(), cart, temItem, Integer.parseInt(txtQtd.getText().toString()), TAG);
        } else {
            if (TAG.equals("update")) {
                return new LoaderCartItem(getActivity(), proId, cart.getUser_id(), cartId);
            } else {
                return new LoaderProductDetail(getActivity(), proId);
            }
        }
    }

    public void toast(String string) {
        Toast.makeText(getActivity(), string, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Map<String, Object>> loader, Map<String, Object> data) {
        dots.setVisibility(View.GONE);
        buyButton.setClickable(true);
        progress.setVisibility(View.GONE);
        if (data != null) {
            buyButton.setVisibility(View.VISIBLE);
            if (loader.getId() == 0 && data.get("add") instanceof Cart) {
                if (temItem) {
                    toast(getResources().getString(R.string.atualizado_manual));
                } else {
                    toast(getResources().getString(R.string.adicionado_manual));
                }
                dismiss();
                listener.added();
            } else if (loader.getId() == 1) {
                buyButton.setVisibility(View.VISIBLE);
                if (data.get(AppConstants.PRODUCT) instanceof Product) {
                    nested.setVisibility(View.VISIBLE);

                    product = (Product) data.get(AppConstants.PRODUCT);

                    if (data.get(AppConstants.COLLECTION) instanceof List) {
                        collectionExtra = (List) data.get(AppConstants.COLLECTION);
                        adapter.setConjuct((List) data.get(AppConstants.COLLECTION));
                    }

                    if (data.get(AppConstants.SIZE) instanceof List && product.getIs_multi_stock() == 1) {
                        sizes = (List) data.get(AppConstants.SIZE);
                        cardSizes.setVisibility(View.VISIBLE);

                        if (sizes.size() > 0) {
                            recyclerViewSize.setVisibility(View.VISIBLE);
                            linearEmpty.setVisibility(View.GONE);
                        } else {
                            recyclerViewSize.setVisibility(View.GONE);
                            linearEmpty.setVisibility(View.VISIBLE);
                        }

                        adapterSize.setSizes((List) data.get(AppConstants.SIZE));
                    }

                    preco.setText(Util.formaterPrice(product.getPrice()));
                    nome.setText(product.getName());
                    if (product.getDescription() != null && product.getDescription().trim().equals("")) {
                        descricao.setText(getString(R.string.sem_descricao));
                    } else {
                        descricao.setText(product.getDescription());
                    }
                    Util.hasPhoto(product, draweeView);

                    if (product.getCart_quantity() > 0) {
                        temItem = true;
                        cart.setCartQuantidade(product.getCart_quantity());
                        cart.setNote(product.getCart_note());
                        obs.setText(cart.getNote());
                        if (product.getIs_stock() == 1 && product.getStock_quantity() > cart.getCartQuantidade()) {
                            txtQtd.setText(String.valueOf(cart.getCartQuantidade()));
                        } else if (product.getIs_stock() == 1 && product.getStock_quantity() > 0) {
                            txtQtd.setText(String.valueOf(product.getStock_quantity()));
                        } else if (product.getIs_stock() != 1) {
                            txtQtd.setText(String.valueOf(cart.getCartQuantidade()));
                        } else {
                            indisponivel.setVisibility(View.VISIBLE);
                            linearBottom.setVisibility(View.GONE);
                        }

                    } else {
                        temItem = false;
                        if (product.getIs_stock() != 1 || product.getStock_quantity() > 0) {
                            txtQtd.setText(String.valueOf(1));
                        } else {
                            indisponivel.setVisibility(View.VISIBLE);
                            linearBottom.setVisibility(View.GONE);
                        }
                    }
                    updateButtonValue();
                    nested.fullScroll(View.FOCUS_UP);
                }
            } else {
                UtilShaPre.setDefaults(AppConstants.ORDER_TYPE, "", getContext());
                loaderManager.destroyLoader(loader.getId());
                Toast.makeText(getContext(), R.string.adicionado_manual, Toast.LENGTH_SHORT).show();
                dismiss();
            }
            errorLayout.setVisibility(View.GONE);
        } else {
            if (loader.getId() == 0) {
                loaderManager.destroyLoader(loader.getId());
            }
            buyButton.setVisibility(View.GONE);
            errorLayout.setVisibility(View.VISIBLE);
            Toast.makeText(getContext(), R.string.error, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Map<String, Object>> loader) {

    }

    private void updateButtonValue() {
        double value = product.getPrice() * Integer.parseInt(txtQtd.getText().toString());
        boolean is_ok = true;

        if ((product.getIs_multi_stock() == 1 && sizes.size() < 1) || (product.getIs_multi_stock() == 1 && product.getMulti_stock_quantity() < 1)) {
            is_ok = false;
            recyclerView.setVisibility(View.GONE);
            indisponivel.setVisibility(View.VISIBLE);
            linearBottom.setVisibility(View.GONE);

        } else if (product.getIs_multi_stock() == 1) {
            is_ok = false;
            if (sizes != null && sizes.size() > 0) {
                for (Size sizes : sizes) {
                    if (sizes.getIs_selected() == 1) {
                        if (Integer.parseInt(txtQtd.getText().toString()) > sizes.getStock_quantity()) {
                            txtQtd.setText(String.valueOf(sizes.getStock_quantity()));
                            value = product.getPrice() * Integer.parseInt(txtQtd.getText().toString());
                        }
                        cart.setSize_id(sizes.getSize_id());
                        cart.setSize_name(sizes.getName());
                        value += sizes.getPrice() * Integer.parseInt(txtQtd.getText().toString());
                        is_ok = true;
                    }
                }
            }
        }

        if (collectionExtra != null && collectionExtra.size() > 0) {
            for (CollectionExtra extra : collectionExtra) {
                value += extra.getTotal_price() * Integer.parseInt(txtQtd.getText().toString());
                ;
                if (extra.getMin_quantity() > extra.getQuantity_selected()) {
                    is_ok = false;
                }
            }
        }
        if (temItem) {
            buyButton.setText("Atualizar •" + Util.formaterPrice(value));
        } else {
            buyButton.setText("Adicionar •" + Util.formaterPrice(value));
        }

        if (is_ok) {
            buyButton.setEnabled(true);
            buyButton.setClickable(true);
        } else {
            buyButton.setEnabled(false);
            buyButton.setClickable(false);
        }
    }

    @Override
    public void onHeaderClick(List<CollectionExtra> conjuct) {
        this.collectionExtra = conjuct;
        updateButtonValue();
    }

    @Override
    public void onClickProducSize(List<Size> sizes) {
        this.sizes = sizes;
        updateButtonValue();
    }

    private static class AddUpdateToCart extends AsyncTaskLoader<Map<String, Object>> {

        Cart cart;
        boolean temItem;
        int qtd;
        String TAG;

        private AddUpdateToCart(Context context, Cart cart, boolean temItem, int qtd, String TAG) {
            super(context);
            this.cart = cart;
            this.temItem = temItem;
            this.qtd = qtd;
            this.TAG = TAG;
        }

        @Override
        protected void onStartLoading() {
            forceLoad();
        }

        @Override
        public Map<String, Object> loadInBackground() {
            Gson gson = new Gson();
            cart.setCartQuantidade(qtd);
            Map<String, Object> mapOrdPro;
            mapOrdPro = new HashMap<>();
            try {
                if (temItem && TAG.equals("update")) {
                    mapOrdPro.put("add", gson.fromJson(ConnectApi.PATCH(cart, ConnectApi.CART_ATUALIZA_ITEM, getContext()), Cart.class));
                } else {
                    mapOrdPro.put("add", gson.fromJson(ConnectApi.POST(cart, ConnectApi.CART, getContext()), Cart.class));
                }
            } catch (Exception e) {
                FirebaseCrashlytics.getInstance().log(UtilShaPre.getDefaultsString(AppConstants.USER_ID, getContext()));
                FirebaseCrashlytics.getInstance().recordException(e);
                return null;
            }
            return mapOrdPro;
        }
    }

    private static class LoaderCartItem extends AsyncTaskLoader<Map<String, Object>> {

        String proId, userId, cartId;

        LoaderCartItem(Context context, String proId, String userId, String cartId) {
            super(context);
            this.proId = proId;
            this.userId = userId;
            this.cartId = cartId;
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
            JsonParser parser = new JsonParser();

            try {
                String json = ConnectApi.GET(ConnectApi.CART_ITEM + "/" + proId + "," + userId + "," + cartId, getContext());
                mapOrdPro.put(AppConstants.PRODUCT, gson.fromJson(parser.parse(json).getAsJsonObject().getAsJsonObject(AppConstants.PRODUCT), Product.class));

                try {
                    CollectionExtra[] extras = gson.fromJson(parser.parse(json).getAsJsonObject().getAsJsonArray(AppConstants.COLLECTION), CollectionExtra[].class);
                    mapOrdPro.put(AppConstants.COLLECTION, new ArrayList<>(Arrays.asList(extras)));
                } catch (Exception e) {
                    FirebaseCrashlytics.getInstance().log(UtilShaPre.getDefaultsString(AppConstants.USER_ID, getContext()));
                    FirebaseCrashlytics.getInstance().recordException(e);
                }
                try {
                    Size[] sizes = gson.fromJson(parser.parse(json).getAsJsonObject().getAsJsonArray(AppConstants.SIZE), Size[].class);
                    mapOrdPro.put(AppConstants.SIZE, new ArrayList<>(Arrays.asList(sizes)));
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

    private static class LoaderProductDetail extends AsyncTaskLoader<Map<String, Object>> {

        String proId;

        LoaderProductDetail(Context context, String proId) {
            super(context);
            this.proId = proId;
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
            JsonParser parser = new JsonParser();

            try {
                String json = ConnectApi.GET(ConnectApi.PRODUCT_DETAIL + "/" + proId, getContext());
                mapOrdPro.put(AppConstants.PRODUCT, gson.fromJson(parser.parse(json).getAsJsonObject().getAsJsonObject(AppConstants.PRODUCT), Product.class));
                try {
                    CollectionExtra[] extras = gson.fromJson(parser.parse(json).getAsJsonObject().getAsJsonArray(AppConstants.COLLECTION), CollectionExtra[].class);
                    mapOrdPro.put(AppConstants.COLLECTION, new ArrayList<>(Arrays.asList(extras)));
                } catch (Exception e) {
                    FirebaseCrashlytics.getInstance().log(UtilShaPre.getDefaultsString(AppConstants.USER_ID, getContext()));
                    FirebaseCrashlytics.getInstance().recordException(e);
                }
                try {
                    Size[] sizes = gson.fromJson(parser.parse(json).getAsJsonObject().getAsJsonArray(AppConstants.SIZE), Size[].class);
                    mapOrdPro.put(AppConstants.SIZE, new ArrayList<>(Arrays.asList(sizes)));
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

    public interface FrProductListener {
        void added();
    }
}