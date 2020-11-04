package com.nuppin.company.Loja.Fatura;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

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
import com.nuppin.company.Util.AppConstants;
import com.nuppin.company.Util.UtilShaPre;
import com.nuppin.company.connection.ConnectApi;
import com.nuppin.company.model.Invoice;
import com.nuppin.company.model.Company;
import com.nuppin.company.R;
import com.nuppin.company.Util.Util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class FaturasFr extends Fragment implements
        FaturasFrAdapter.FaturaOnClickListener,
        LoaderManager.LoaderCallbacks {
    private Company company;
    private static final String COMPANY = "COMPANY";
    private RecyclerView recyclerView;
    private LoaderManager loaderManager;
    private FaturasFrAdapter adapter;
    private LottieAnimationView dots;
    private LinearLayout linearEmpty;
    private ConstraintLayout errorLayout;
    private FloatingActionButton fabError;
    private Object data;

    public FaturasFr() {
    }

    public static FaturasFr newInstance(Company company) {
        FaturasFr fragment = new FaturasFr();
        Bundle args = new Bundle();
        args.putParcelable(COMPANY, company);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        assert getArguments() != null;
        if (getArguments().containsKey(COMPANY)) {
            company = getArguments().getParcelable(COMPANY);
        }

        loaderManager = LoaderManager.getInstance(this);

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fr_invoice, container, false);


        dots = view.findViewById(R.id.dots);
        linearEmpty = view.findViewById(R.id.linearEmpty);
        errorLayout = view.findViewById(R.id.error_layout);
        fabError = view.findViewById(R.id.fabError);
        fabError.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dots.setVisibility(View.VISIBLE);
                errorLayout.setVisibility(View.GONE);
                loaderManager.restartLoader(0, null, FaturasFr.this);
            }
        });


        //TextView textView = view.findViewById(R.id.text);
        Toolbar toolbar = view.findViewById(R.id.toolbar_top);
        Util.setaToolbar(this, R.string.faturas_toolbar, toolbar, getActivity(), false, 0);

        recyclerView = view.findViewById(R.id.invoices);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new FaturasFrAdapter(this);
        recyclerView.setAdapter(adapter);
        loaderManager.restartLoader(0, null, this);
        return view;
    }


    @Override
    public void onPause() {
        super.onPause();
        loaderManager.destroyLoader(0);
    }


    @Override
    public void onClickFatura(String faturaId) {
        if (getActivity() instanceof ToActivity) {
            ToActivity listener = (ToActivity) getActivity();
            listener.FaturaClick(faturaId);
        }
    }

    @NonNull
    @Override
    public Loader onCreateLoader(int id, @Nullable Bundle args) {
        return new GetFaturas(Objects.requireNonNull(getContext()), company.getCompany_id(), data);
    }

    @Override
    public void onLoadFinished(@NonNull Loader loader, Object data) {
        dots.setVisibility(View.GONE);
        if (data != null) {
            this.data = data;
            errorLayout.setVisibility(View.GONE);
            if (((List)data).size() > 0) {
                recyclerView.setVisibility(View.VISIBLE);
                linearEmpty.setVisibility(View.GONE);
                adapter.setInvoices((List) data);
            } else {
                recyclerView.setVisibility(View.GONE);
                linearEmpty.setVisibility(View.VISIBLE);
            }
        } else {
            errorLayout.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public void onLoaderReset(@NonNull Loader loader) {

    }

    private static class GetFaturas extends AsyncTaskLoader<Object> {

        String storeId;
        Context ctx;
        Object data;

        GetFaturas(@NonNull Context context, String storeId, Object data) {
            super(context);
            this.storeId = storeId;
            this.ctx = context;
            this.data = data;
        }

        @Override
        protected void onStartLoading() {
            super.onStartLoading();
            if (data != null) {
                deliverResult(data);
            }
            forceLoad();
        }

        @Nullable
        @Override
        public Object loadInBackground() {
            try {
                Gson gson = new Gson();
                Invoice[] invoices = gson.fromJson(ConnectApi.GET(ConnectApi.COMPANY_INVOICES + "/" + storeId, getContext()), Invoice[].class);
                return new ArrayList<>(Arrays.asList(invoices));
            } catch (Exception e) {
                FirebaseCrashlytics.getInstance().log(UtilShaPre.getDefaultsString(AppConstants.USER_ID, getContext()));
                FirebaseCrashlytics.getInstance().recordException(e);
                return null;
            }
        }
    }

    public interface ToActivity {
        void FaturaClick(String id);
    }
}