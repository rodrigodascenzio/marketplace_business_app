package com.nuppin.company.Loja.loja;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.AsyncTaskLoader;
import androidx.loader.content.Loader;

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
import com.nuppin.company.model.Plan;

import java.util.Objects;

public class FrPlanos extends Fragment implements
        LoaderManager.LoaderCallbacks {
    private Company company;
    private static final String COMPANY = "COMPANY";
    private TextView mensalidade, taxa, name;
    private LoaderManager loaderManager;
    private LottieAnimationView dots;
    private ConstraintLayout errorLayout;
    private FloatingActionButton fabError;
    private Plan plan;
    private ScrollView scrollView;

    public FrPlanos() {
        // Required empty public constructor
    }

    public static FrPlanos newInstance(Company company) {
        FrPlanos fragment = new FrPlanos();
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


    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.unit5_cad, container, false);

        dots = view.findViewById(R.id.dots);
        errorLayout = view.findViewById(R.id.error_layout);
        fabError = view.findViewById(R.id.fabError);
        fabError.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dots.setVisibility(View.VISIBLE);
                errorLayout.setVisibility(View.GONE);
                loaderManager.restartLoader(0, null, FrPlanos.this);
            }
        });

        mensalidade = view.findViewById(R.id.price);
        taxa = view.findViewById(R.id.taxa);
        scrollView = view.findViewById(R.id.scrollView);
        name = view.findViewById(R.id.txtInfo);

        loaderManager = LoaderManager.getInstance(this);

        Toolbar toolbar = view.findViewById(R.id.tb_main_top);
        Util.setaToolbar(this, 0, toolbar, getActivity(), false, 0);

        loaderManager.restartLoader(0, null, this);
        return view;
    }

    @NonNull
    @Override
    public Loader onCreateLoader(int id, @Nullable Bundle args) {
        return new GetPlanos(Objects.requireNonNull(getContext()));
    }

    @Override
    public void onLoadFinished(@NonNull Loader loader, Object data) {
        dots.setVisibility(View.GONE);
        if (data != null) {
            if (loader.getId() == 0) {
                plan = (Plan) data;
                name.setText(plan.getName());
                mensalidade.setText(getResources().getString(R.string.plano_price, Util.formaterPrice(plan.getPrice())));
                if (company.getCategory_company_id().equals("3")) {
                    taxa.setText(getResources().getString(R.string.plano_taxa_servicos, plan.getFee() * 100));
                } else {
                    taxa.setText(getResources().getString(R.string.plano_taxa, plan.getFee() * 100));
                }
                scrollView.setVisibility(View.VISIBLE);
                errorLayout.setVisibility(View.GONE);
            }
        } else {
            errorLayout.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader loader) {

    }

    private static class GetPlanos extends AsyncTaskLoader {

        Context ctx;

        GetPlanos(@NonNull Context context) {
            super(context);
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
                return gson.fromJson(ConnectApi.GET(ConnectApi.COMPANY_PLAN, getContext()), Plan.class);
            } catch (Exception e) {
                FirebaseCrashlytics.getInstance().log(UtilShaPre.getDefaultsString(AppConstants.USER_ID, getContext()));
                FirebaseCrashlytics.getInstance().recordException(e);
                return null;
            }
        }
    }
}
