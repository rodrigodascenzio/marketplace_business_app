package com.nuppin.company.Loja.Cupom;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.AsyncTaskLoader;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.gson.Gson;
import com.nuppin.company.Util.AppConstants;
import com.nuppin.company.Util.UtilShaPre;
import com.nuppin.company.connection.ConnectApi;
import com.nuppin.company.model.Coupon;
import com.nuppin.company.model.Company;
import com.nuppin.company.R;
import com.nuppin.company.Util.Util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import me.toptas.fancyshowcase.FancyShowCaseView;
import me.toptas.fancyshowcase.listener.DismissListener;

public class ListaCupom extends Fragment implements
        LoaderManager.LoaderCallbacks {
    private Company company;
    private static final String COMPANY = "COMPANY";
    private RecyclerView recyclerView;
    private listaCuponsAdapter adapter;
    private LoaderManager loaderManager;
    private LottieAnimationView dots;
    private LinearLayout linearEmpty;
    private ConstraintLayout errorLayout;
    private FloatingActionButton fabError, fab;
    private Object data;


    private FancyShowCaseView fancyShowCaseView1;
    private DismissListener listenerTutorial = new DismissListener() {
        @Override
        public void onDismiss(String s) {
            chainTutorial();
        }

        @Override
        public void onSkipped(String s) {

        }
    };

    private void chainCreatedTutorial() {
        try {
            if (fancyShowCaseView1 == null) {
                fancyShowCaseView1 = new FancyShowCaseView.Builder(requireActivity())
                        .focusOn(fab)
                        .delay(500)
                        .title(getString(R.string.unique_tutorial_coupon_fab_string))
                        .showOnce(getString(R.string.unique_tutorial_coupon_fab))
                        .backgroundColor(getResources().getColor(R.color.primary_light))
                        .enableAutoTextPosition()
                        .closeOnTouch(true)
                        .dismissListener(listenerTutorial)
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



    public ListaCupom() {
        // Required empty public constructor
    }

    public static ListaCupom newInstance(Company company) {
        ListaCupom fragment = new ListaCupom();
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
        View view = inflater.inflate(R.layout.fr_coupon, container, false);

        dots = view.findViewById(R.id.dots);
        linearEmpty = view.findViewById(R.id.linearEmpty);
        errorLayout = view.findViewById(R.id.error_layout);
        fabError = view.findViewById(R.id.fabError);
        fabError.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dots.setVisibility(View.VISIBLE);
                errorLayout.setVisibility(View.GONE);
                loaderManager.restartLoader(0, null, ListaCupom.this);
            }
        });

        Toolbar toolbar = view.findViewById(R.id.toolbar_top);
        Util.setaToolbar(this, R.string.cupom_toolbar, toolbar, getActivity(), false, 0);

        fab = view.findViewById(R.id.floatbutton);
        fab.hide();
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (company.getMax_radius() < 1) {
                    Toast.makeText(getContext(), R.string.warning_settings_before_cupom, Toast.LENGTH_SHORT).show();
                    return;
                }
                if (getActivity() instanceof ToActivity) {
                    ToActivity listener = (ToActivity) getActivity();
                    listener.AddCupom(company);
                }
            }
        });

        recyclerView = view.findViewById(R.id.listaCupons);

        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 2);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new listaCuponsAdapter();
        recyclerView.setAdapter(adapter);
        loaderManager.restartLoader(0, null, this);

        chainCreatedTutorial();

        return view;
    }

    @NonNull
    @Override
    public Loader onCreateLoader(int id, @Nullable Bundle args) {
        return new LoaderCupom(Objects.requireNonNull(getContext()), company.getCompany_id(), data);
    }

    @Override
    public void onLoadFinished(@NonNull Loader loader, Object data) {
        dots.setVisibility(View.GONE);
        if (data != null) {
            fab.show();
            this.data = data;
            if (((List) data).size() > 0) {
                adapter.setCupons((List) data);
                recyclerView.setVisibility(View.VISIBLE);
                linearEmpty.setVisibility(View.GONE);
            } else {
                recyclerView.setVisibility(View.GONE);
                linearEmpty.setVisibility(View.VISIBLE);
            }
            errorLayout.setVisibility(View.GONE);
        } else {
            errorLayout.setVisibility(View.VISIBLE);
        }
        chainTutorial();
    }

    @Override
    public void onLoaderReset(@NonNull Loader loader) {

    }

    @Override
    public void onPause() {
        super.onPause();
        loaderManager.destroyLoader(0);
    }

    private static class LoaderCupom extends AsyncTaskLoader<Object> {

        String storeId;
        Context ctx;
        Object data;

        LoaderCupom(@NonNull Context context, String storeId, Object data) {
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
            Gson gson = new Gson();
            try {
                Coupon[] cupons = gson.fromJson(ConnectApi.GET(ConnectApi.COMPANY_COUPON + "/" + storeId, getContext()), Coupon[].class);
                return new ArrayList<>(Arrays.asList(cupons));
            } catch (Exception e) {
                FirebaseCrashlytics.getInstance().log(UtilShaPre.getDefaultsString(AppConstants.USER_ID, getContext()));
                FirebaseCrashlytics.getInstance().recordException(e);
                return null;
            }
        }
    }

    public interface ToActivity {
        void AddCupom(Company company);
    }
}
