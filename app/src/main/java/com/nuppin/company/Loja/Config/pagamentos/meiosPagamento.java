package com.nuppin.company.Loja.Config.pagamentos;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

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
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.nuppin.company.Util.AppConstants;
import com.nuppin.company.Util.UtilShaPre;
import com.nuppin.company.connection.ConnectApi;
import com.nuppin.company.model.ErrorCode;
import com.nuppin.company.model.PaymentCompany;
import com.nuppin.company.model.Company;
import com.nuppin.company.R;
import com.nuppin.company.Util.Util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class meiosPagamento extends Fragment implements
        meiosPagamentosAdapter.meiosPagamentoOnClickListener,
        LoaderManager.LoaderCallbacks<Object> {
    private Company company;
    private static final String COMPANY = "COMPANY";
    private RecyclerView recyclerView;
    private meiosPagamentosAdapter adapter;
    private LoaderManager loaderManager;
    private PaymentCompany paymentCompany;
    private boolean checked;
    private FrameLayout progress;
    private LottieAnimationView dots;
    private ConstraintLayout errorLayout;
    private FloatingActionButton fabError;


    public meiosPagamento() {
        // Required empty public constructor
    }

    public static meiosPagamento newInstance(Company company) {
        meiosPagamento fragment = new meiosPagamento();
        Bundle args = new Bundle();
        args.putParcelable(COMPANY, company);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null && getArguments().containsKey(COMPANY)) {
            company = getArguments().getParcelable(COMPANY);
        }

        loaderManager = LoaderManager.getInstance(this);

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fr_company_payment, container, false);

        progress = view.findViewById(R.id.framProgress);
        dots = view.findViewById(R.id.dots);
        errorLayout = view.findViewById(R.id.error_layout);
        fabError = view.findViewById(R.id.fabError);
        fabError.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dots.setVisibility(View.VISIBLE);
                errorLayout.setVisibility(View.GONE);
                loaderManager.restartLoader(0, null, meiosPagamento.this);
            }
        });

        //TextView = view.findViewById(R.id.text);
        Toolbar toolbar = view.findViewById(R.id.toolbar_top);
        Util.setaToolbar(this, R.string.payment_methods, toolbar, getActivity(), false, 0);

        recyclerView = view.findViewById(R.id.meios_pagamentos);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new meiosPagamentosAdapter(this);
        recyclerView.setAdapter(adapter);
        loaderManager.restartLoader(0, null, this);
        return view;
    }

    @Override
    public void onClickMeioPagamento(Boolean checked, String meiopagId) {
        progress.setVisibility(View.VISIBLE);
        paymentCompany = new PaymentCompany();
        paymentCompany.setPayment_id(meiopagId);
        paymentCompany.setCompany_id(company.getCompany_id());
        this.checked = checked;
        loaderManager.restartLoader(1, null, meiosPagamento.this);
    }

    @NonNull
    @Override
    public Loader<Object> onCreateLoader(int id, @Nullable Bundle args) {
        if (id == 0) {
            return new LoaderMeiosPagamento(requireContext(), company.getCompany_id());
        } else {
            return new SetMeioPagamento(getContext(), paymentCompany, checked);
        }
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Object> loader, Object data) {
        dots.setVisibility(View.GONE);
        progress.setVisibility(View.GONE);
        if (data != null) {
            if (loader.getId() == 0) {
                adapter.setMeiosPagamento((List) data);
            } else {
                if (data instanceof ErrorCode) {
                    Toast.makeText(getContext(), ((ErrorCode) data).getError_message(), Toast.LENGTH_SHORT).show();
                }
                loaderManager.destroyLoader(loader.getId());
                loaderManager.restartLoader(0, null, this);
            }
            errorLayout.setVisibility(View.GONE);
        } else {
            errorLayout.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Object> loader) {

    }

    private static class LoaderMeiosPagamento extends AsyncTaskLoader<Object> {

        String storeId;
        Context ctx;

        LoaderMeiosPagamento(@NonNull Context context, String storeId) {
            super(context);
            this.storeId = storeId;
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
                PaymentCompany[] paymentCompanies = gson.fromJson(ConnectApi.GET(ConnectApi.COMPANY_PAYMENT + "/" + storeId, getContext()), PaymentCompany[].class);
                return new ArrayList<>(Arrays.asList(paymentCompanies));
            } catch (Exception e) {
                FirebaseCrashlytics.getInstance().log(UtilShaPre.getDefaultsString(AppConstants.USER_ID, getContext()));
                FirebaseCrashlytics.getInstance().recordException(e);
                return null;
            }
        }
    }

    private static class SetMeioPagamento extends AsyncTaskLoader<Object> {

        Context ctx;
        PaymentCompany paymentCompany;
        boolean checked;

        SetMeioPagamento(Context context, PaymentCompany paymentCompany, boolean checked) {
            super(context);
            ctx = context;
            this.paymentCompany = paymentCompany;
            this.checked = checked;
        }


        @Override
        protected void onStartLoading() {
            forceLoad();
        }


        @Override
        public Object loadInBackground() {
            Gson gson = new Gson();
            try {
                if (checked) {
                    return gson.fromJson(ConnectApi.POST(paymentCompany, ConnectApi.COMPANY_PAYMENT, getContext()), PaymentCompany.class);
                } else {
                    String stringJson = ConnectApi.DELETE(paymentCompany, ConnectApi.COMPANY_PAYMENT, getContext());
                    try {
                        return gson.fromJson(stringJson, Integer.class);
                    } catch (Exception e) {
                        FirebaseCrashlytics.getInstance().log(UtilShaPre.getDefaultsString(AppConstants.USER_ID, getContext()));
                FirebaseCrashlytics.getInstance().recordException(e);
                        try {
                            JsonParser parser = new JsonParser();
                            JsonObject jObj = parser.parse(stringJson).getAsJsonObject();
                            return gson.fromJson(jObj.getAsJsonObject(AppConstants.PAYMENT_ERROR), ErrorCode.class);
                        } catch (Exception e1) {
                    FirebaseCrashlytics.getInstance().log(UtilShaPre.getDefaultsString(AppConstants.USER_ID, getContext()));
                    FirebaseCrashlytics.getInstance().recordException(e1);
                            return null;
                        }
                    }
                }
            } catch (Exception e) {
                FirebaseCrashlytics.getInstance().log(UtilShaPre.getDefaultsString(AppConstants.USER_ID, getContext()));
                FirebaseCrashlytics.getInstance().recordException(e);
                return null;
            }
        }
    }
}