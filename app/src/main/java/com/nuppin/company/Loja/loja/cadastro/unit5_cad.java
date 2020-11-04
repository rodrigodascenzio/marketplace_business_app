package com.nuppin.company.Loja.loja.cadastro;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.AsyncTaskLoader;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.gson.Gson;
import com.nuppin.company.Util.AppConstants;
import com.nuppin.company.Util.UtilShaPre;
import com.nuppin.company.connection.ConnectApi;
import com.nuppin.company.model.Company;
import com.nuppin.company.model.Plan;
import com.nuppin.company.R;
import com.nuppin.company.Util.Util;

public class unit5_cad extends Fragment implements
        LoaderManager.LoaderCallbacks {
    private Company company;
    private static final String COMPANY = "COMPANY";
    private TextView mensalidade, taxa, name;
    private LoaderManager loaderManager;
    private LottieAnimationView dots;
    private ConstraintLayout errorLayout;
    private FloatingActionButton fabError;
    private Plan plan;
    private PlanAdapter adapter;
    private NestedScrollView scrollView;
    private ConstraintLayout cardPromo;
    private TextView promo;

    public unit5_cad() {
        // Required empty public constructor
    }

    public static unit5_cad newInstance(Company company) {
        unit5_cad fragment = new unit5_cad();
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
        View view = inflater.inflate(R.layout.unit5_cad, container, false);

        dots = view.findViewById(R.id.dots);
        errorLayout = view.findViewById(R.id.error_layout);
        fabError = view.findViewById(R.id.fabError);
        fabError.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dots.setVisibility(View.VISIBLE);
                errorLayout.setVisibility(View.GONE);
                loaderManager.restartLoader(0, null, unit5_cad.this);
            }
        });

        MaterialButton btn = view.findViewById(R.id.btn);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                company.setPlan_id(plan.getPlan_id());
                if (getActivity() instanceof NovaEmpresa) {
                    NovaEmpresa listener = (NovaEmpresa) getActivity();
                    listener.cycleCad("6", company);
                }
            }
        });

        mensalidade = view.findViewById(R.id.price);
        taxa = view.findViewById(R.id.taxa);
        scrollView = view.findViewById(R.id.scrollView);
        cardPromo = view.findViewById(R.id.cardPromo);
        promo = view.findViewById(R.id.promo);
        name = view.findViewById(R.id.txtInfo);

        RecyclerView recyclerView = view.findViewById(R.id.recycler_benefits);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new PlanAdapter();
        recyclerView.setAdapter(adapter);

        Toolbar toolbar = view.findViewById(R.id.tb_main_top);
        Util.setaToolbar(this, 0, toolbar, getActivity(), false, 0);

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
    public Loader onCreateLoader(int id, @Nullable Bundle args) {
        return new GetPlanos(requireContext(), company);
    }

    @Override
    public void onLoadFinished(@NonNull Loader loader, Object data) {
        dots.setVisibility(View.GONE);
        if (data != null) {
            plan = (Plan) data;
            name.setText(plan.getName());

            adapter.setBenefits(plan.getBenefit());

            if (plan.getTrial_period() > 0) {

                if (plan.getFee() > 0) {
                    if (company.getCategory_company_id().equals("3")) {
                        if (plan.getTrial_fee() == plan.getFee()) {
                            taxa.setText(getResources().getString(R.string.plano_taxa_servicos, plan.getFee()));
                        } else {
                            if (plan.getTrial_fee() == 0) {
                                taxa.setText(getResources().getString(R.string.no_fee_trial));
                            } else {
                                taxa.setText(getResources().getString(R.string.plano_taxa_servicos_trial, plan.getTrial_fee()));
                            }
                        }
                    } else {
                        if (plan.getTrial_fee() == plan.getFee()) {
                            taxa.setText(getResources().getString(R.string.plano_taxa, plan.getFee()));
                        } else {
                            taxa.setText(getResources().getString(R.string.plano_taxa_trial, plan.getTrial_fee()));
                        }
                    }
                } else {
                    taxa.setVisibility(View.GONE);
                }

                if (plan.getPrice() == 0) {
                    mensalidade.setVisibility(View.GONE);
                } else {
                    if (plan.getTrial_price() == plan.getPrice()) {
                        mensalidade.setText(getResources().getString(R.string.plano_price, Util.formaterPrice(plan.getPrice())));
                    } else if (plan.getTrial_price() > 0) {
                        mensalidade.setText(getResources().getString(R.string.plano_price_promo, Util.formaterPrice(plan.getTrial_price())));
                    } else {
                        mensalidade.setText(getResources().getString(R.string.plano_price_free));
                    }
                }

                cardPromo.setVisibility(View.VISIBLE);
                if (plan.getTrial_period() > 1) {
                    if (plan.getPrice() == 0 || plan.getTrial_price() == plan.getPrice()) {
                        promo.setText(getResources().getString(R.string.plan_trial_promo_fee, plan.getTrial_period(), plan.getFee()));
                    } else if (plan.getFee() == 0 || plan.getTrial_fee() == plan.getFee()) {
                        promo.setText(getResources().getString(R.string.plan_trial_promo_price, plan.getTrial_period(), Util.formaterPrice(plan.getPrice())));
                    } else {
                        promo.setText(getResources().getString(R.string.plan_trial_promo_and_fee, plan.getTrial_period(), Util.formaterPrice(plan.getPrice()), plan.getFee()));
                    }
                } else {
                    if (plan.getPrice() == 0 || plan.getTrial_price() == plan.getPrice()) {
                        promo.setText(getResources().getString(R.string.plan_trial_promo_fee_singular, plan.getFee()));
                    } else if (plan.getFee() == 0 || plan.getTrial_fee() == plan.getFee()) {
                        promo.setText(getResources().getString(R.string.plan_trial_promo_price_singular, Util.formaterPrice(plan.getPrice())));
                    } else {
                        promo.setText(getResources().getString(R.string.plan_trial_promo_singular_and_fee, Util.formaterPrice(plan.getPrice()), plan.getFee()));
                    }
                }

            } else {
                if (plan.getPrice() == 0) {
                    mensalidade.setVisibility(View.GONE);
                } else {
                    mensalidade.setText(getResources().getString(R.string.plano_price, Util.formaterPrice(plan.getPrice())));
                }
                if (plan.getFee() == 0) {
                    taxa.setVisibility(View.GONE);
                } else {
                    if (company.getCategory_company_id().equals("3")) {
                        taxa.setText(getResources().getString(R.string.plano_taxa_servicos, plan.getFee()));
                    } else {
                        taxa.setText(getResources().getString(R.string.plano_taxa, plan.getFee()));
                    }
                }
            }


            scrollView.setVisibility(View.VISIBLE);
            errorLayout.setVisibility(View.GONE);
        } else {
            errorLayout.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader loader) {

    }

    private static class GetPlanos extends AsyncTaskLoader {

        Context ctx;
        Company company;

        GetPlanos(@NonNull Context context, Company company) {
            super(context);
            this.ctx = context;
            this.company = company;
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
                return gson.fromJson(ConnectApi.GET(ConnectApi.COMPANY_PLAN + "/" + company.getCategory_company_id(), getContext()), Plan.class);
            } catch (Exception e) {
                FirebaseCrashlytics.getInstance().log(UtilShaPre.getDefaultsString(AppConstants.USER_ID, getContext()));
                FirebaseCrashlytics.getInstance().recordException(e);
                return null;
            }
        }
    }

    public interface NovaEmpresa {
        void cycleCad(String index, Company company);
    }
}
