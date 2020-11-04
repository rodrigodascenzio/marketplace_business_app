package com.nuppin.company.pos.produto.carrinho;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import com.nuppin.company.Loja.dialogs.InfoDialogFragment;
import com.nuppin.company.Loja.dialogs.ManualSchedulingNameDialog;
import com.nuppin.company.R;
import com.nuppin.company.Util.AppConstants;
import com.nuppin.company.Util.Util;
import com.nuppin.company.Util.UtilShaPre;
import com.nuppin.company.connection.ConnectApi;
import com.nuppin.company.model.Coupon;
import com.nuppin.company.model.ErrorCode;
import com.nuppin.company.model.Order;
import com.nuppin.company.pos.produto.dialog.DialogUserAddress;
import com.nuppin.company.pos.produto.dialog.TrocoDialogFragmentManual;
import com.nuppin.company.model.Cart;
import com.nuppin.company.model.CartCompany;
import com.nuppin.company.model.CartProduct;
import com.nuppin.company.pos.produto.list.FrDetalheProdutoManual;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FrCarrinhoManual extends Fragment implements
        LoaderManager.LoaderCallbacks<Object>,
        RvCarrinhoAdapterManual.RvCartOnClickListener,
        InfoDialogFragment.InfoDialogListener,
        TrocoDialogFragmentManual.TrocoDialogListener,
        DialogUserAddress.UsuarioAddresLatLon,
        ManualSchedulingNameDialog.CustomerName,
        FrDetalheProdutoManual.FrProductListener {

    private LoaderManager loaderManager;
    private RvCarrinhoAdapterManual adapter;
    private RecyclerView mRecyclerView;
    private Button btn;
    private TextView tSubtotal, entrega, tEntrega, tTotal, tEnderecoEntrega, trocarEndereco, txtCupom, pagamentoEscolhido, desconto, descontotxt;
    private SimpleDraweeView fotoLoja;
    private CartCompany cartCompany;
    private ConstraintLayout lCarrinho;
    private CartProduct cartProduct;
    private Toolbar toolbar;
    private LottieAnimationView dots;
    private LinearLayout linearEmpty;
    private Map mapOrdPro;
    private ConstraintLayout errorLayout;
    private FloatingActionButton fabError;
    private CardView progress;
    private TextView infoEntrega;
    private MaterialButton adicionarCupom, alterarMeioPagamento, addItem, addItemProd, addAddress;
    private RadioButton btnEntrega, btnRetirada, btnPos;
    private RadioGroup radioGroup;

    public FrCarrinhoManual() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loaderManager = LoaderManager.getInstance(this);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.manual_fr_cart, container, false);

        dots = view.findViewById(R.id.dots);
        linearEmpty = view.findViewById(R.id.linearEmpty);
        errorLayout = view.findViewById(R.id.error_layout);
        fabError = view.findViewById(R.id.fabError);
        fabError.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dots.setVisibility(View.VISIBLE);
                lCarrinho.setVisibility(View.GONE);
                errorLayout.setVisibility(View.GONE);
                loaderManager.restartLoader(0, null, FrCarrinhoManual.this);
            }
        });

        progress = view.findViewById(R.id.progress);

        toolbar = view.findViewById(R.id.toolbar_top);
        Util.setaToolbar(this, R.string.carrinho_toolbar, toolbar, getActivity(), false, 0);

        final EditText obs = view.findViewById(R.id.obs);

        obs.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                cartCompany.setCart_note(obs.getText().toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        addAddress = view.findViewById(R.id.addAddress);
        infoEntrega = view.findViewById(R.id.infoEntrega);
        alterarMeioPagamento = view.findViewById(R.id.alterar);
        adicionarCupom = view.findViewById(R.id.adicionar);
        fotoLoja = view.findViewById(R.id.fotoLoja);
        mRecyclerView = view.findViewById(R.id.recyclerview_cart);
        tSubtotal = view.findViewById(R.id.resultSubtotal);
        tEntrega = view.findViewById(R.id.resultEntrega);
        entrega = view.findViewById(R.id.Entrega);
        tTotal = view.findViewById(R.id.resultTotal);
        btnEntrega = view.findViewById(R.id.btnEntregar);
        btnRetirada = view.findViewById(R.id.btnRetirada);
        btnPos = view.findViewById(R.id.btnPos);
        tEnderecoEntrega = view.findViewById(R.id.txtEndereco);
        lCarrinho = view.findViewById(R.id.layoutCarrinho);
        txtCupom = view.findViewById(R.id.txtCupom);
        btn = view.findViewById(R.id.btnComprar);
        trocarEndereco = view.findViewById(R.id.trocarEndereco);
        pagamentoEscolhido = view.findViewById(R.id.meioPagamentoEscolhido);
        desconto = view.findViewById(R.id.resulDesconto);
        descontotxt = view.findViewById(R.id.desconto);
        addItem = view.findViewById(R.id.addItem);
        addItemProd = view.findViewById(R.id.addItemProd);
        radioGroup = view.findViewById(R.id.radioGroup);

        addItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getActivity() instanceof FrtoActivity) {
                    FrtoActivity listener = (FrtoActivity) getActivity();
                    listener.companyCartManual(cartCompany);
                }
            }
        });

        addItemProd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getActivity() instanceof FrtoActivity) {
                    FrtoActivity listener = (FrtoActivity) getActivity();
                    listener.companyCartManual(cartCompany);
                }
            }
        });

        radioGroup.setOnCheckedChangeListener(listener);
        if (cartCompany != null) {
            if (cartCompany.getCart_payment_type() != null) {
                if (!cartCompany.getCart_payment_type().equals("")) {
                    pagamentoEscolhido.setText(cartCompany.getCart_payment_type());
                }
            }

            obs.setText(cartCompany.getCart_note());
        }

        adicionarCupom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (cartCompany.getCart_discount_value() == 0) {
                    DialogFragment dialogFrag = TrocoDialogFragmentManual.newInstance(getResources().getString(R.string.quanto_de_desconto), getResources().getString(R.string.confirmar), "");
                    dialogFrag.show(FrCarrinhoManual.this.getChildFragmentManager(), "desconto");
                } else {
                    cartCompany.setCart_discount_value(0);
                    mainGetFunction();
                }
            }
        });

        addAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (cartCompany.getUser_latitude() == 0) {
                    DialogFragment dialogFrag = new DialogUserAddress();
                    dialogFrag.show(FrCarrinhoManual.this.getChildFragmentManager(), "address");
                }
            }
        });

        alterarMeioPagamento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getActivity() instanceof FrtoActivity) {
                    FrtoActivity listener = (FrtoActivity) getActivity();
                    listener.meioPagamentoManual(cartCompany);
                }
            }
        });

        trocarEndereco.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment dialogFrag = DialogUserAddress.instance(cartCompany.getUser_address());
                dialogFrag.show(FrCarrinhoManual.this.getChildFragmentManager(), "address");
            }
        });

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UtilShaPre.setDefaults("min_purchase_exception", false, getContext());
                UtilShaPre.setDefaults("is_available_exception", false, getContext());
                confirm();
            }
        });

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);
        adapter = new RvCarrinhoAdapterManual(this, getContext());
        mRecyclerView.setAdapter(adapter);
        loaderManager.restartLoader(0, null, this);
        return view;
    }

    private void confirm() {

        if (cartCompany.getCart_order_type() == null || cartCompany.getCart_order_type().isEmpty()) {
            Toast.makeText(getContext(), "Defina o tipo do pedido", Toast.LENGTH_SHORT).show();
            return;
        } else if (cartCompany.getCart_order_type().equals("delivery") && (cartCompany.getUser_address() == null || cartCompany.getUser_address().isEmpty())) {
            Toast.makeText(getContext(), "Defina o endereço do cliente", Toast.LENGTH_SHORT).show();
            return;
        }

        if (cartCompany.getCart_payment_type() == null) {
            Toast.makeText(getContext(), R.string.selecione_metodo_pagamento, Toast.LENGTH_SHORT).show();
            return;
        }

        if (cartCompany.getIs_available() != 1 && !UtilShaPre.getDefaultsBool("is_available_exception", getContext()) && UtilShaPre.getDefaultsString(AppConstants.ORDER_TYPE,getContext()).equals("delivery")) {
            DialogFragment dialogFrag = InfoDialogFragment.newInstance(getResources().getString(R.string.cart_warning_location), getResources().getString(R.string.liberar), getResources().getString(R.string.apagar_pedido));
            dialogFrag.show(FrCarrinhoManual.this.getChildFragmentManager(), "info");
            return;
        }

        if (cartCompany.getMin_purchase() > (cartCompany.getSubtotal_amount() - cartCompany.getCart_discount_value()) && (cartCompany.getDelivery_type_value() == 4 && UtilShaPre.getDefaultsString(AppConstants.ORDER_TYPE, getContext()).equals("delivery")) && !UtilShaPre.getDefaultsBool("min_purchase_exception", getContext())) {
            DialogFragment dialogFrag = InfoDialogFragment.newInstance(getResources().getString(R.string.aviso_min_pedido_gratis), getResources().getString(R.string.liberar), getResources().getString(R.string.apagar_pedido));
            dialogFrag.show(FrCarrinhoManual.this.getChildFragmentManager(), "pedido_minimo");
            return;
        }

        if ((cartCompany.getUser_name() == null || cartCompany.getUser_name().isEmpty())) {
            DialogFragment dialogFrag = ManualSchedulingNameDialog.newInstance(getResources().getString(R.string.confirmar), getResources().getString(R.string.revisar));
            dialogFrag.show(FrCarrinhoManual.this.getChildFragmentManager(), "customer");
            return;
        }

        DialogFragment dialogFrag = InfoDialogFragment.newInstance("Confirmar pedido?", getResources().getString(R.string.confirmar), getResources().getString(R.string.revisar));
        dialogFrag.show(FrCarrinhoManual.this.getChildFragmentManager(), "confirm");
    }

    private RadioGroup.OnCheckedChangeListener listener = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup radioGroup, int i) {
            switch (i) {
                case R.id.btnEntregar:
                    setDelivery();
                    mainGetFunction();
                    break;
                case R.id.btnRetirada:
                    setLocal();
                    mainGetFunction();
                    break;
                case R.id.btnPos:
                    setPos();
                    mainGetFunction();
                    break;
            }
        }
    };

    private void setPos() {
        btnPos.setTextColor(requireContext().getResources().getColor(R.color.colorPrimary));
        btnEntrega.setTextColor(requireContext().getResources().getColor(R.color.shadow_color));
        btnRetirada.setTextColor(requireContext().getResources().getColor(R.color.shadow_color));
        trocarEndereco.setVisibility(View.GONE);
        tEnderecoEntrega.setVisibility(View.GONE);
        addAddress.setVisibility(View.GONE);
        cartCompany.setCart_order_type("pos");
        UtilShaPre.setDefaults(AppConstants.ORDER_TYPE, "pos", getContext());
        entrega.setVisibility(View.GONE);
        tEntrega.setVisibility(View.GONE);
    }

    private void setLocal() {
        btnRetirada.setTextColor(requireContext().getResources().getColor(R.color.colorPrimary));
        btnEntrega.setTextColor(requireContext().getResources().getColor(R.color.shadow_color));
        btnPos.setTextColor(requireContext().getResources().getColor(R.color.shadow_color));
        trocarEndereco.setVisibility(View.GONE);
        tEnderecoEntrega.setVisibility(View.GONE);
        addAddress.setVisibility(View.GONE);
        cartCompany.setCart_order_type("local");
        UtilShaPre.setDefaults(AppConstants.ORDER_TYPE, "local", getContext());
        entrega.setVisibility(View.GONE);
        tEntrega.setVisibility(View.GONE);
    }

    private void setDelivery() {
        btnEntrega.setTextColor(requireContext().getResources().getColor(R.color.colorPrimary));
        btnRetirada.setTextColor(requireContext().getResources().getColor(R.color.shadow_color));
        btnPos.setTextColor(requireContext().getResources().getColor(R.color.shadow_color));
        if (cartCompany.getUser_address() != null && !cartCompany.getUser_address().isEmpty()) {
            tEnderecoEntrega.setText(cartCompany.getUser_address());
            tEnderecoEntrega.setVisibility(View.VISIBLE);
            trocarEndereco.setVisibility(View.VISIBLE);
            addAddress.setVisibility(View.GONE);
        } else {
            tEnderecoEntrega.setVisibility(View.GONE);
            trocarEndereco.setVisibility(View.GONE);
            addAddress.setVisibility(View.VISIBLE);
        }
        cartCompany.setCart_order_type("delivery");
        UtilShaPre.setDefaults(AppConstants.ORDER_TYPE, "delivery", getContext());
    }

    @Override
    public void onPause() {
        super.onPause();
        loaderManager.destroyLoader(0);
    }

    @NonNull
    @Override
    public Loader<Object> onCreateLoader(int id, Bundle args) {
        if (id == 0) {
            return new CartLoader(getActivity(), cartCompany, mapOrdPro);
        } else if (id == 1) {
            return new Cart2Order(getActivity(), cartCompany);
        } else if (id == 2) {
            return new DeleteItem(getActivity(), cartProduct);
        } else if (id == 3) {
            return new LimpaCarrinho(getActivity());
        }
        return null;
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Object> loader, Object data) {
        dots.setVisibility(View.GONE);
        progress.setVisibility(View.GONE);
        infoEntrega.setVisibility(View.GONE);
        btn.setClickable(true);
        if (data != null) {
            switch (loader.getId()) {
                case 0:
                    mapOrdPro = (Map) data;
                    if (((Map) data).get(AppConstants.CART_COMPANY) instanceof CartCompany) {
                        cartCompany = (CartCompany) ((Map) data).get(AppConstants.CART_COMPANY);

                        if ((Boolean) ((Map) data).get(AppConstants.CART_COMPANY_EMPTY)) {
                            lCarrinho.setVisibility(View.GONE);
                            linearEmpty.setVisibility(View.VISIBLE);
                            return;
                        }

                        adapter.setCart(cartCompany.getProduct());
                        lCarrinho.setVisibility(View.VISIBLE);
                        linearEmpty.setVisibility(View.GONE);
                        Util.hasPhoto(cartCompany, fotoLoja);

                        mainGetFunction();
                    }

                    break;
                case 1:
                    if (data instanceof ErrorCode) {
                        loaderManager.destroyLoader(loader.getId());
                        Toast.makeText(getContext(), ((ErrorCode) data).getError_message(), Toast.LENGTH_SHORT).show();
                        loaderManager.restartLoader(0, null, FrCarrinhoManual.this);
                    } else {
                        UtilShaPre.setDefaults(AppConstants.ORDER_TYPE, "", getContext());
                        //todo - abrir um toast perguntando se vai ter mais algum pedido
                        Toast.makeText(getContext(), "Concluido", Toast.LENGTH_SHORT).show();
                        Util.backFragmentFunction(this);
                    }
                    break;
                case 2:
                    loaderManager.destroyLoader(loader.getId());
                    loaderManager.restartLoader(0, null, FrCarrinhoManual.this);
                    break;
                case 3:
                    Toast.makeText(getContext(), "Itens excluidos", Toast.LENGTH_SHORT).show();
                    loaderManager.destroyLoader(loader.getId());
                    UtilShaPre.setDefaults(AppConstants.ORDER_TYPE, "", getContext());
                    loaderManager.restartLoader(0, null, FrCarrinhoManual.this);
                    break;
                default:
                    break;
            }
            errorLayout.setVisibility(View.GONE);
        } else {
            if (loader.getId() != 0) {
                loaderManager.destroyLoader(loader.getId());
            }
            errorLayout.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Object> loader) {
    }

    private void mainGetFunction() {

        radioGroup.setOnCheckedChangeListener(null);

        if (cartCompany.getIs_delivery() != 1) {
            btnEntrega.setVisibility(View.GONE);
        } else {
            if (btnRetirada.getVisibility() == View.GONE && btnPos.getVisibility() == View.GONE) {
                setDelivery();
                radioGroup.check(R.id.btnEntregar);
            }
        }

        if (cartCompany.getIs_local() != 1) {
            btnRetirada.setVisibility(View.GONE);
        }
        if (btnEntrega.getVisibility() == View.GONE && btnPos.getVisibility() == View.GONE) {
            setLocal();
            radioGroup.check(R.id.btnRetirada);
        }

        if (cartCompany.getIs_pos() != 1) {
            btnPos.setVisibility(View.GONE);
        }
        if (btnRetirada.getVisibility() == View.GONE && btnEntrega.getVisibility() == View.GONE) {
            setPos();
            radioGroup.check(R.id.btnPos);
        }

        switch (UtilShaPre.getDefaultsString(AppConstants.ORDER_TYPE, getContext())) {
            case "delivery":
                setDelivery();
                radioGroup.check(R.id.btnEntregar);
                break;
            case "local":
                setLocal();
                radioGroup.check(R.id.btnRetirada);
                break;
            case "pos":
                setPos();
                radioGroup.check(R.id.btnPos);
                break;
        }
        radioGroup.setOnCheckedChangeListener(listener);


        tSubtotal.setText(Util.formaterPrice(cartCompany.getSubtotal_amount()));

        if (cartCompany.getUser_address() != null && !cartCompany.getUser_address().isEmpty() && UtilShaPre.getDefaultsString(AppConstants.ORDER_TYPE, getContext()).equals("delivery")){
            if ((cartCompany.getMax_radius_free() == 0 || cartCompany.getMax_radius_free() < cartCompany.getDistance())) {
                switch (cartCompany.getDelivery_type_value()) {
                    case 1:
                        cartCompany.setCartValorEntrega(cartCompany.getDelivery_fixed_fee());
                        tEntrega.setText(Util.formaterPrice(cartCompany.getCartValorEntrega()));
                        break;
                    case 2:
                        cartCompany.setCartValorEntrega(Util.roundHalf(cartCompany.getDelivery_fixed_fee() + (cartCompany.getDelivery_variable_fee() * cartCompany.getDistance())));
                        tEntrega.setText(Util.formaterPrice(cartCompany.getCartValorEntrega()));
                        break;
                    case 3:
                        cartCompany.setCartValorEntrega(Util.roundHalf(cartCompany.getDelivery_variable_fee() * cartCompany.getDistance()));
                        tEntrega.setText(Util.formaterPrice(cartCompany.getCartValorEntrega()));
                        break;
                    case 4:
                        if (cartCompany.getMin_purchase() > (cartCompany.getSubtotal_amount() - cartCompany.getCart_discount_value())) {
                            infoEntrega.setVisibility(View.VISIBLE);
                            infoEntrega.setText("Pedido minimo: " + Util.formaterPrice(cartCompany.getMin_purchase()));
                        } else {
                            cartCompany.setCartValorEntrega(0);
                        }
                        tEntrega.setText(Util.formaterPrice(cartCompany.getCartValorEntrega()));
                        break;
                }
            } else {
                if (cartCompany.getMin_purchase() > (cartCompany.getSubtotal_amount() - cartCompany.getCart_discount_value())) {
                    if (cartCompany.getDelivery_type_value() != 4) {
                        switch (cartCompany.getDelivery_type_value()) {
                            case 1:
                                cartCompany.setCartValorEntrega(cartCompany.getDelivery_fixed_fee());
                                break;
                            case 2:
                                cartCompany.setCartValorEntrega(Util.roundHalf(cartCompany.getDelivery_fixed_fee() + (cartCompany.getDelivery_variable_fee() * cartCompany.getDistance())));
                                break;
                            case 3:
                                cartCompany.setCartValorEntrega(Util.roundHalf(cartCompany.getDelivery_variable_fee() * cartCompany.getDistance()));
                                break;
                        }
                        infoEntrega.setVisibility(View.VISIBLE);
                        infoEntrega.setText("Entrega grátis acima de " + Util.formaterPrice(cartCompany.getMin_purchase()));
                    }
                } else {
                    cartCompany.setCartValorEntrega(0);
                }
                tEntrega.setText(Util.formaterPrice(cartCompany.getCartValorEntrega()));
            }
            entrega.setVisibility(View.VISIBLE);
            tEntrega.setVisibility(View.VISIBLE);
        }

        if (cartCompany.getCart_discount_value() > 0 && cartCompany.getCart_discount_value() < cartCompany.getSubtotal_amount()) {
            desconto.setVisibility(View.VISIBLE);
            descontotxt.setVisibility(View.VISIBLE);

            adicionarCupom.setText(R.string.cancelar);
            txtCupom.setText(getResources().getString(R.string.money_off, Util.formaterPrice(cartCompany.getCart_discount_value())));
            desconto.setText(Util.formaterPrice(cartCompany.getCart_discount_value()));

            if (UtilShaPre.getDefaultsString(AppConstants.ORDER_TYPE, getContext()).equals("delivery")) {
                if ((cartCompany.getSubtotal_amount() + cartCompany.getCartValorEntrega()) - cartCompany.getCart_discount_value() < 0) {
                    tTotal.setText(Util.formaterPrice(0));
                    cartCompany.setCart_total_value(0);
                } else {
                    double total = (cartCompany.getSubtotal_amount() + cartCompany.getCartValorEntrega()) - cartCompany.getCart_discount_value();
                    tTotal.setText(Util.formaterPrice(total));
                    cartCompany.setCart_total_value(total);
                }
            } else {
                if (cartCompany.getSubtotal_amount() - cartCompany.getCart_discount_value() < 0) {
                    tTotal.setText(Util.formaterPrice(0));
                    cartCompany.setCart_total_value(0);
                } else {
                    double total = (cartCompany.getSubtotal_amount() - cartCompany.getCart_discount_value());
                    tTotal.setText(Util.formaterPrice(total));
                    cartCompany.setCart_total_value(total);
                }
            }

        } else {
            if (cartCompany.getCart_discount_value() > cartCompany.getSubtotal_amount()) {
                Toast.makeText(getContext(), "Desconto invalido", Toast.LENGTH_SHORT).show();
            }
            cartCompany.setCart_discount_value(0);
            desconto.setVisibility(View.GONE);
            descontotxt.setVisibility(View.GONE);
            adicionarCupom.setText(R.string.btnAdicionar);
            txtCupom.setText(getResources().getString(R.string.add_discount));

            double total;
            if (UtilShaPre.getDefaultsString(AppConstants.ORDER_TYPE, getContext()).equals("delivery")) {
                total = cartCompany.getCartValorEntrega() + cartCompany.getSubtotal_amount();
            } else {
                total = cartCompany.getSubtotal_amount();
            }
            tTotal.setText(Util.formaterPrice(total));
            cartCompany.setCart_total_value(total);
        }
    }

    @Override
    public void onClick(CartProduct cart) {
        DialogFragment dialogFrag = FrDetalheProdutoManual.novaInstanciaEdit(cart.getProduct_id(), cart.getCompany_id(), cartCompany.getCategory_company_id(), cart.getCart_id());
        dialogFrag.show(FrCarrinhoManual.this.getChildFragmentManager(), "added");
    }

    @Override
    public void onClickExclui(CartProduct product) {
        cartProduct = product;
        progress.setVisibility(View.VISIBLE);
        loaderManager.restartLoader(2, null, FrCarrinhoManual.this);
    }

    @Override
    public void onDialogOKClick(View view, InfoDialogFragment info) {
        if (info.getTag() != null) {
            switch (info.getTag()) {
                case "confirm":
                    switch (view.getId()) {
                        case R.id.btnPositive:
                            if (cartCompany.getCart_payment_type().toLowerCase().equals("dinheiro") && !cartCompany.getCart_order_type().equals("pos")) {
                                DialogFragment dialogFrag = InfoDialogFragment.newInstance(getResources().getString(R.string.troco), getResources().getString(R.string.sim), getResources().getString(R.string.nao));
                                dialogFrag.show(FrCarrinhoManual.this.getChildFragmentManager(), "troco");
                            } else {
                                progress.setVisibility(View.VISIBLE);
                                btn.setClickable(false);
                                loaderManager.restartLoader(1, null, FrCarrinhoManual.this);
                                info.dismiss();
                                break;
                            }
                        case R.id.btnNegative:
                            info.dismiss();
                            break;
                    }
                    break;
                case "info":
                    switch (view.getId()) {
                        case R.id.btnPositive:
                            UtilShaPre.setDefaults("is_available_exception", true, getContext());
                            confirm();
                            info.dismiss();
                            break;
                        case R.id.btnNegative:
                            loaderManager.restartLoader(3, null, FrCarrinhoManual.this);
                            info.dismiss();
                            break;
                    }
                    break;
                case "troco":
                    switch (view.getId()) {
                        case R.id.btnPositive:
                            DialogFragment dialogFrag = TrocoDialogFragmentManual.newInstance(getResources().getString(R.string.troco_detail), getResources().getString(R.string.confirmar), "");
                            dialogFrag.show(FrCarrinhoManual.this.getChildFragmentManager(), "troco_detail");
                            info.dismiss();
                            break;
                        case R.id.btnNegative:
                            progress.setVisibility(View.VISIBLE);
                            btn.setClickable(false);
                            loaderManager.restartLoader(1, null, FrCarrinhoManual.this);
                            info.dismiss();
                            break;
                    }
                    break;
                case "pedido_minimo":
                    switch (view.getId()) {
                        case R.id.btnPositive:
                            UtilShaPre.setDefaults("min_purchase_exception", true, getContext());
                            confirm();
                            info.dismiss();
                            break;
                        case R.id.btnNegative:
                            loaderManager.restartLoader(3, null, FrCarrinhoManual.this);
                            info.dismiss();
                            break;
                    }
            }
        }
    }

    @Override
    public void onDialogOKClick(View view, String value, TrocoDialogFragmentManual info) {
        if (info.getTag() != null) {
            switch (info.getTag()) {
                case "troco_detail":
                    if (view.getId() == R.id.btnPositive) {
                        cartCompany.setCart_note((cartCompany.getCart_note() + "\nTROCO PARA " + value).trim());
                        progress.setVisibility(View.VISIBLE);
                        btn.setClickable(false);
                        loaderManager.restartLoader(1, null, FrCarrinhoManual.this);
                        info.dismiss();
                    }
                    break;
                case "desconto":
                    if (view.getId() == R.id.btnPositive) {
                        cartCompany.setCart_discount_value(Util.unmaskPrice(value));
                        mainGetFunction();
                        info.dismiss();
                    }
                    break;
            }
        }
    }

    @Override
    public void infosAddres(String address, double latitude, double longitude, DialogUserAddress dialogUserAddress) {
        if (latitude != 0) {
            cartCompany.setUser_address(address);
            cartCompany.setUser_latitude(latitude);
            cartCompany.setUser_longitude(longitude);
            loaderManager.restartLoader(0, null, FrCarrinhoManual.this);
            dialogUserAddress.dismiss();
        }
    }

    @Override
    public void onDialogOKClick(View view, String value, ManualSchedulingNameDialog infoDialogFragment) {
        switch (view.getId()) {
            case R.id.btnPositive:
                cartCompany.setUser_name(value);
                DialogFragment dialogFrag = InfoDialogFragment.newInstance("Confirmar pedido?", getResources().getString(R.string.confirmar), getResources().getString(R.string.revisar));
                dialogFrag.show(FrCarrinhoManual.this.getChildFragmentManager(), "confirm");
                infoDialogFragment.dismiss();
                break;
            case R.id.btnNegative:
                infoDialogFragment.dismiss();
                break;
        }
    }

    @Override
    public void added() {
        loaderManager.restartLoader(0, null, this);
    }

    private static class CartLoader extends AsyncTaskLoader<Object> {

        Context ctx;
        CartCompany cartCompany;
        Map map;

        CartLoader(Context context, CartCompany cartCompany, Map map) {
            super(context);
            ctx = context;
            this.cartCompany = cartCompany;
            this.map = map;
        }

        @Override
        protected void onStartLoading() {
            if (map != null) {
                deliverResult(map);
            }
            forceLoad();

        }

        @Override
        public Object loadInBackground() {
            Gson gson = new Gson();
            String sJson;
            if (cartCompany != null && UtilShaPre.getDefaultsString(AppConstants.ORDER_TYPE, getContext()).equals("delivery") && (cartCompany.getUser_address() != null && !cartCompany.getUser_address().isEmpty())) {
                sJson = ConnectApi.GET(ConnectApi.CART + "/"
                        + UtilShaPre.getDefaultsString(AppConstants.USER_ID, ctx) + ","
                        + UtilShaPre.getDefaultsString(AppConstants.COMPANY, ctx) + ","
                        + cartCompany.getUser_latitude() + "," + cartCompany.getUser_longitude() + "," + UtilShaPre.getDefaultsString(AppConstants.COUNTRY_CODE, getContext()), getContext());
            } else {
                sJson = ConnectApi.GET(ConnectApi.CART + "/"
                        + UtilShaPre.getDefaultsString(AppConstants.USER_ID, ctx) + ","
                        + UtilShaPre.getDefaultsString(AppConstants.COMPANY, ctx) + ","
                        + Util.returnStringLatLonCountr(getContext()), getContext());
            }
            JsonParser parser = new JsonParser();
            Coupon[] coupon;
            CartCompany cartCompany;
            List<Coupon> couponList;

            Map<String, Object> mapOrdPro;
            mapOrdPro = new HashMap<>();
            try {
                cartCompany = gson.fromJson(parser.parse(sJson).getAsJsonObject().getAsJsonObject(AppConstants.CART_COMPANY), CartCompany.class);

                try {
                    Boolean b = gson.fromJson(parser.parse(sJson).getAsJsonObject().getAsJsonPrimitive(AppConstants.CART_COMPANY_EMPTY), Boolean.class);
                    mapOrdPro.put(AppConstants.CART_COMPANY_EMPTY, b);
                } catch (Exception e1) {
                    FirebaseCrashlytics.getInstance().log(UtilShaPre.getDefaultsString(AppConstants.USER_ID, getContext()));
                    FirebaseCrashlytics.getInstance().recordException(e1);
                    mapOrdPro.put(AppConstants.CART_COMPANY_EMPTY, true);
                }

                if (this.cartCompany != null) {
                    cartCompany.setCart_payment_type(this.cartCompany.getCart_payment_type());
                    cartCompany.setCart_discount_value(this.cartCompany.getCart_discount_value());
                    cartCompany.setCart_note(this.cartCompany.getCart_note());
                    cartCompany.setCart_order_type(this.cartCompany.getCart_order_type());
                    cartCompany.setUser_address(this.cartCompany.getUser_address());
                    cartCompany.setUser_latitude(this.cartCompany.getUser_latitude());
                    cartCompany.setUser_longitude(this.cartCompany.getUser_longitude());
                    cartCompany.setUser_name(this.cartCompany.getUser_name());
                }

                try {
                    JsonArray cupons = parser.parse(sJson).getAsJsonObject().getAsJsonArray(AppConstants.COUPON);
                    coupon = gson.fromJson(cupons, Coupon[].class);
                    couponList = new ArrayList<>(Arrays.asList(coupon));
                } catch (Exception e) {
                    FirebaseCrashlytics.getInstance().log(UtilShaPre.getDefaultsString(AppConstants.USER_ID, getContext()));
                    FirebaseCrashlytics.getInstance().recordException(e);
                    couponList = null;
                }
            } catch (Exception e) {
                FirebaseCrashlytics.getInstance().log(UtilShaPre.getDefaultsString(AppConstants.USER_ID, getContext()));
                FirebaseCrashlytics.getInstance().recordException(e);
                return null;
            }
            mapOrdPro.put(AppConstants.CART_COMPANY, cartCompany);
            mapOrdPro.put(AppConstants.COUPON, couponList);
            return mapOrdPro;
        }
    }

    private static class Cart2Order extends AsyncTaskLoader<Object> {

        CartCompany cartCompany;

        private Cart2Order(Context context, CartCompany cartCompany) {
            super(context);
            this.cartCompany = cartCompany;
        }

        @Override
        protected void onStartLoading() {
            forceLoad();
        }

        @Override
        public Object loadInBackground() {
            Gson gson = new Gson();
            JsonParser parser = new JsonParser();
            String jsonString = ConnectApi.POST(cartCompany, ConnectApi.ORDERS_MANUAL, getContext());
            try {
                UtilShaPre.setDefaults(AppConstants.HAS_CART, "", getContext());
                return gson.fromJson(parser.parse(jsonString).getAsJsonObject().getAsJsonObject(AppConstants.ORDERS), Order.class);
            } catch (Exception e) {
                FirebaseCrashlytics.getInstance().log(UtilShaPre.getDefaultsString(AppConstants.USER_ID, getContext()));
                FirebaseCrashlytics.getInstance().recordException(e);
                try {
                    return gson.fromJson(parser.parse(jsonString).getAsJsonObject().getAsJsonObject(AppConstants.ERROR), ErrorCode.class);
                } catch (Exception e1) {
                    FirebaseCrashlytics.getInstance().log(UtilShaPre.getDefaultsString(AppConstants.USER_ID, getContext()));
                    FirebaseCrashlytics.getInstance().recordException(e1);
                    return null;
                }
            }
        }
    }

    private static class DeleteItem extends AsyncTaskLoader<Object> {

        CartProduct product;

        DeleteItem(Context context, CartProduct product) {
            super(context);
            this.product = product;
        }

        @Override
        protected void onStartLoading() {
            forceLoad();
        }

        @Override
        public Object loadInBackground() {
            try {
                Gson gson = new Gson();
                return gson.fromJson(ConnectApi.DELETE(product, ConnectApi.CART_ITEM, getContext()), CartProduct.class);
            } catch (Exception e) {
                FirebaseCrashlytics.getInstance().log(UtilShaPre.getDefaultsString(AppConstants.USER_ID, getContext()));
                FirebaseCrashlytics.getInstance().recordException(e);
                return null;
            }
        }
    }

    private static class LimpaCarrinho extends AsyncTaskLoader<Object> {

        LimpaCarrinho(Context context) {
            super(context);
        }

        @Override
        protected void onStartLoading() {
            forceLoad();
        }

        @Override
        public Object loadInBackground() {
            try {
                Cart cart = new Cart(UtilShaPre.getDefaultsString(AppConstants.USER_ID, getContext()));
                Gson gson = new Gson();
                return gson.fromJson(ConnectApi.DELETE(cart, ConnectApi.CART_LIMPA_TUDO, getContext()), Cart.class);
            } catch (Exception e) {
                FirebaseCrashlytics.getInstance().log(UtilShaPre.getDefaultsString(AppConstants.USER_ID, getContext()));
                FirebaseCrashlytics.getInstance().recordException(e);
                return null;
            }
        }
    }

    public interface FrtoActivity {
        void meioPagamentoManual(CartCompany cartCompany);

        void companyCartManual(CartCompany company);
    }
}