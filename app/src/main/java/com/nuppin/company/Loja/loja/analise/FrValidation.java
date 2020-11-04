package com.nuppin.company.Loja.loja.analise;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

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
import com.nuppin.company.model.Company;
import com.nuppin.company.model.Validation;

public class FrValidation extends Fragment implements
        LoaderManager.LoaderCallbacks<Validation>{

    private RecyclerView mRecyclerView;
    private Company company;
    private LoaderManager loaderManager;
    private static final String EXTRA_COMPANY = "COMPANY";
    private Validation data;
    private LottieAnimationView dots;
    private LinearLayout linearEmpty;
    private ConstraintLayout errorLayout;
    private FloatingActionButton fabError;
    private RvValidation adapter;
    private TextView txtInfo, txtInfoSub;

    public static FrValidation novaInstancia(Company company) {
        Bundle parametros = new Bundle();
        parametros.putParcelable(EXTRA_COMPANY, company);
        FrValidation fragment = new FrValidation();
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
        View view = inflater.inflate(R.layout.fr_validation, container, false);

        dots = view.findViewById(R.id.dots);
        linearEmpty = view.findViewById(R.id.linearEmpty);
        errorLayout = view.findViewById(R.id.error_layout);
        fabError = view.findViewById(R.id.fabError);
        fabError.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dots.setVisibility(View.VISIBLE);
                errorLayout.setVisibility(View.GONE);
                loaderManager.restartLoader(0, null, FrValidation.this);
            }
        });

        Toolbar toolbar = view.findViewById(R.id.toolbar_top);
        Util.setaToolbar(this, R.string.validacao_toolbar, toolbar, getActivity(), false, 0);

        mRecyclerView = view.findViewById(R.id.recyclerview_validation);
        txtInfo = view.findViewById(R.id.txtInfo);
        txtInfoSub = view.findViewById(R.id.txtInfoSub);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        adapter = new RvValidation();
        mRecyclerView.setAdapter(adapter);

        loaderManager.restartLoader(0, null, this);
        return view;
    }

    @NonNull
    @Override
    public Loader<Validation> onCreateLoader(int id, Bundle args) {
        return new ValidationLoader(getActivity(), company.getCompany_id(), data);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Validation> loader, Validation data) {
        dots.setVisibility(View.GONE);

        if (data != null) {
            this.data = data;
            if (data.getMessages().size() > 0 && data.getStatus().equals("denied")) {
                txtInfo.setVisibility(View.VISIBLE);
                txtInfoSub.setVisibility(View.VISIBLE);
                mRecyclerView.setVisibility(View.VISIBLE);
                linearEmpty.setVisibility(View.GONE);
                adapter.setValidation(data.getMessages());
            } else {
                txtInfo.setVisibility(View.GONE);
                txtInfoSub.setVisibility(View.GONE);
                mRecyclerView.setVisibility(View.GONE);
                linearEmpty.setVisibility(View.VISIBLE);
            }
            errorLayout.setVisibility(View.GONE);
        } else {
            errorLayout.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Validation> loader) {

    }

    private static class ValidationLoader extends AsyncTaskLoader<Validation> {

        String storeId;
        Context ctx;
        Validation data;

        ValidationLoader(Context context, String storeId, Validation data) {
            super(context);
            ctx = context;
            this.storeId = storeId;
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
        public Validation loadInBackground() {
            Gson gson = new Gson();
            try {
                return gson.fromJson(ConnectApi.GET(ConnectApi.VALIDATION + "/" + storeId, getContext()), Validation.class);
            } catch (Exception e) {
                FirebaseCrashlytics.getInstance().log(UtilShaPre.getDefaultsString(AppConstants.USER_ID, getContext()));
                FirebaseCrashlytics.getInstance().recordException(e);
                return null;
            }
        }
    }

}
