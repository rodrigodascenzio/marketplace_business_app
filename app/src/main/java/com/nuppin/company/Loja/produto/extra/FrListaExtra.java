package com.nuppin.company.Loja.produto.extra;

import android.app.Activity;
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
import com.nuppin.company.R;
import com.nuppin.company.Util.AppConstants;
import com.nuppin.company.Util.Util;
import com.nuppin.company.Util.UtilShaPre;
import com.nuppin.company.connection.ConnectApi;
import com.nuppin.company.model.Company;
import com.nuppin.company.model.Extra;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import me.toptas.fancyshowcase.FancyShowCaseView;
import me.toptas.fancyshowcase.listener.DismissListener;

public class FrListaExtra extends Fragment implements
        LoaderManager.LoaderCallbacks<List<Extra>>,
        RvExtraAdapter.RvExtraItemsOnClickListener{

    private RvExtraAdapter adapter;
    private RecyclerView mRecyclerView;
    private Company company;
    private LoaderManager loaderManager;
    private static final String EXTRA_COMPANY = "COMPANY";
    private List<Extra> data;
    private LottieAnimationView dots;
    private LinearLayout linearEmpty;
    private ConstraintLayout errorLayout;
    private FloatingActionButton fabError, fab;
    private FancyShowCaseView fancyShowCaseView1;

    private void chainCreatedTutorial() {
        try {
            if (fancyShowCaseView1 == null) {
                fancyShowCaseView1 = new FancyShowCaseView.Builder(requireActivity())
                        .focusOn(fab)
                        .delay(500)
                        .title(getString(R.string.unique_tutorial_extra_fab_string))
                        .showOnce(getString(R.string.unique_tutorial_extra_fab))
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

    public static FrListaExtra novaInstancia(Company company) {
        Bundle parametros = new Bundle();
        parametros.putParcelable(EXTRA_COMPANY, company);
        FrListaExtra fragment = new FrListaExtra();
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
        View view = inflater.inflate(R.layout.fr_list_extra, container, false);

        dots = view.findViewById(R.id.dots);
        linearEmpty = view.findViewById(R.id.linearEmpty);
        errorLayout = view.findViewById(R.id.error_layout);
        fabError = view.findViewById(R.id.fabError);
        fabError.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dots.setVisibility(View.VISIBLE);
                errorLayout.setVisibility(View.GONE);
                loaderManager.restartLoader(0, null, FrListaExtra.this);
            }
        });

        Toolbar toolbar = view.findViewById(R.id.toolbar_top);
        Util.setaToolbar(this, R.string.itens, toolbar, getActivity(), false, 0);

        fab = view.findViewById(R.id.fab);
        fab.hide();
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getActivity() instanceof FrListaExtra.ToActivity) {
                    FrListaExtra.ToActivity listener = (FrListaExtra.ToActivity) getActivity();
                    listener.ListaExtraItemBottom(company);
                }
            }
        });

        mRecyclerView = view.findViewById(R.id.recyclerview_products);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        adapter = new RvExtraAdapter(this);
        mRecyclerView.setAdapter(adapter);

        loaderManager.restartLoader(0, null, this);

        chainCreatedTutorial();

        return view;
    }

    @NonNull
    @Override
    public Loader<List<Extra>> onCreateLoader(int id, Bundle args) {
        return new ExtrasLoader(getActivity(), company.getCompany_id(), data);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<List<Extra>> loader, List<Extra> data) {
        dots.setVisibility(View.GONE);
        if (data != null) {
            fab.show();
            this.data = data;
            if (data.size() > 0) {
                mRecyclerView.setVisibility(View.VISIBLE);
                linearEmpty.setVisibility(View.GONE);
                adapter.setExtras(data);
            } else {
                mRecyclerView.setVisibility(View.GONE);
                linearEmpty.setVisibility(View.VISIBLE);
            }
            errorLayout.setVisibility(View.GONE);
        } else {
            errorLayout.setVisibility(View.VISIBLE);
        }
        chainTutorial();
    }

    @Override
    public void onLoaderReset(@NonNull Loader<List<Extra>> loader) {

    }


    @Override
    public void onClick(Extra extra) {
        Activity activity = getActivity();
        if (activity instanceof RvExtraAdapter.RvExtraItemsOnClickListener) {
            RvExtraAdapter.RvExtraItemsOnClickListener listener =
                    (RvExtraAdapter.RvExtraItemsOnClickListener) activity;
            listener.onClick(extra);
        }
    }

    private static class ExtrasLoader extends AsyncTaskLoader<List<Extra>> {

        String storeId;
        Context ctx;
        List<Extra> data;

        ExtrasLoader(Context context, String storeId, List<Extra> data) {
            super(context);
            ctx = context;
            this.storeId = storeId;
            this.data = data;
        }

        @Override
        protected void onStartLoading() {
            if (data != null && !data.isEmpty()) {
                deliverResult(data);
            }
            forceLoad();
        }


        @Override
        public List<Extra> loadInBackground() {
            Gson gson = new Gson();
            try {
                Extra[] extras = gson.fromJson(ConnectApi.GET(ConnectApi.EXTRA_COMPANY + "/" + storeId, getContext()), Extra[].class);
                return new ArrayList<>(Arrays.asList(extras));
            } catch (Exception e) {
                FirebaseCrashlytics.getInstance().log(UtilShaPre.getDefaultsString(AppConstants.USER_ID, getContext()));
                FirebaseCrashlytics.getInstance().recordException(e);
                return null;
            }
        }
    }

    public interface ToActivity {
        void ListaExtraItemBottom(Company company);
    }
}
