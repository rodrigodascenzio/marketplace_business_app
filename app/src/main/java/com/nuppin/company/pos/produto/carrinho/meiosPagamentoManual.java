package com.nuppin.company.pos.produto.carrinho;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.AsyncTaskLoader;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.gson.Gson;
import com.nuppin.company.R;
import com.nuppin.company.Util.AppConstants;
import com.nuppin.company.Util.Util;
import com.nuppin.company.Util.UtilShaPre;
import com.nuppin.company.connection.ConnectApi;
import com.nuppin.company.model.PaymentCompany;
import com.nuppin.company.model.Scheduling;
import com.nuppin.company.model.CartCompany;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class meiosPagamentoManual extends Fragment implements
        meiosPagamentosAdapterManual.meiosPagamentoOnClickListener,
        LoaderManager.LoaderCallbacks {
    private CartCompany company;
    private static final String STORE = "STORE";
    private static final String AGENDAMENTO = "AGENDAMENTO";
    private RecyclerView recyclerView;
    private meiosPagamentosAdapterManual adapter;
    private LoaderManager loaderManager;
    private LottieAnimationView dots;
    private ConstraintLayout errorLayout;
    private FloatingActionButton fabError;
    private Scheduling scheduling;
    private String TAG;

    public meiosPagamentoManual() {}

    public static meiosPagamentoManual newInstance(CartCompany company) {
        meiosPagamentoManual fragment = new meiosPagamentoManual();
        Bundle args = new Bundle();
        args.putParcelable(STORE, company);
        fragment.setArguments(args);
        return fragment;
    }

    public static meiosPagamentoManual newInstance(Scheduling scheduling) {
        meiosPagamentoManual fragment = new meiosPagamentoManual();
        Bundle args = new Bundle();
        args.putParcelable(AGENDAMENTO, scheduling);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null && getArguments().containsKey(STORE)) {
            company = getArguments().getParcelable(STORE);
            TAG = STORE;
        }
        if (getArguments() != null && getArguments().containsKey(AGENDAMENTO)) {
            scheduling = getArguments().getParcelable(AGENDAMENTO);
            TAG = AGENDAMENTO;
        }
        loaderManager = LoaderManager.getInstance(this);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.manual_fr_company_payment, container, false);

        dots = view.findViewById(R.id.dots);
        errorLayout = view.findViewById(R.id.error_layout);
        fabError = view.findViewById(R.id.fabError);
        fabError.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dots.setVisibility(View.VISIBLE);
                errorLayout.setVisibility(View.GONE);
                loaderManager.restartLoader(0, null, meiosPagamentoManual.this);
            }
        });

        Toolbar toolbar;
        toolbar = view.findViewById(R.id.toolbar_top);
        Util.setaToolbar(this, 0, toolbar, getActivity(), true, R.drawable.ic_clear_white_24dp);

        recyclerView = view.findViewById(R.id.meios_pagamentos);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new meiosPagamentosAdapterManual(this);
        recyclerView.setAdapter(adapter);
        loaderManager.restartLoader(0, null, this);
        return view;
    }

    @Override
    public void onClickMeioPagamento(String nome, String id) {
        if (nome != null) {
            if (!nome.equals("")){
                if (TAG.equals(STORE)) {
                    company.setCart_payment_type(nome);
                    company.setCart_payment_id(id);
                } else {
                    scheduling.setPayment_method(nome);
                }
            }
            Util.backFragmentFunction(this);
        }
    }

    @NonNull
    @Override
    public Loader onCreateLoader(int id, @Nullable Bundle args) {
        if (TAG.equals(STORE)) {
            return new LoaderMeiosPagamento(requireContext(),company.getCompany_id());
        }else{
            return new LoaderMeiosPagamento(requireContext(),scheduling.getCompany_id());}
    }

    @Override
    public void onLoadFinished(@NonNull Loader loader, Object data) {
        dots.setVisibility(View.GONE);
        if (data != null) {
            if (loader.getId() == 0) {
                adapter.setMeiosPagamento((List)data);
            }
            errorLayout.setVisibility(View.GONE);
        }else {
            errorLayout.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader loader) {

    }

    private static class LoaderMeiosPagamento extends AsyncTaskLoader {

        String companyId;
        Context ctx;

        LoaderMeiosPagamento(@NonNull Context context, String companyId) {
            super(context);
            this.companyId = companyId;
            this.ctx = context;
        }

        @Override
        protected void onStartLoading() {
            super.onStartLoading();
            forceLoad();
        }

        @Nullable
        @Override
        public Object loadInBackground() {
            Gson gson = new Gson();
            try {
                PaymentCompany[] companyPayments = gson.fromJson(ConnectApi.GET(ConnectApi.STORES_MEIOS_PAGAMENTO + "/" + companyId, getContext()), PaymentCompany[].class);
                    return new ArrayList<>(Arrays.asList(companyPayments));
            } catch (Exception e) {
                    FirebaseCrashlytics.getInstance().log(UtilShaPre.getDefaultsString(AppConstants.USER_ID, getContext()));
                    FirebaseCrashlytics.getInstance().recordException(e);
                return null;
            }
        }
    }

}
