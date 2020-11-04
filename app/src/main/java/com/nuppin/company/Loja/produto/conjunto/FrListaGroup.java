package com.nuppin.company.Loja.produto.conjunto;

import android.app.Activity;
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
import com.nuppin.company.model.Collection;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.toptas.fancyshowcase.FancyShowCaseView;
import me.toptas.fancyshowcase.FocusShape;
import me.toptas.fancyshowcase.listener.DismissListener;

public class FrListaGroup extends Fragment implements
        LoaderManager.LoaderCallbacks<List<Collection>>,
        RvListGroup.RvConjuctOnClickListener{

    private RvListGroup adapter;
    private RecyclerView mRecyclerView;
    private Company company;
    private LoaderManager loaderManager;
    private static final String EXTRA_COMPANY = "COMPANY";
    private List<Collection> data;
    private LottieAnimationView dots;
    private LinearLayout linearEmpty;
    private ConstraintLayout errorLayout;
    private FloatingActionButton fabError, fab;
    private TextView extraIcon;

    private FancyShowCaseView fancyShowCaseView1;
    private FancyShowCaseView fancyShowCaseView2;
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
                        .focusOn(extraIcon)
                        .delay(500)
                        .title(getString(R.string.unique_tutorial_group_extraIcon_string))
                        .showOnce(getString(R.string.unique_tutorial_group_extraIcon))
                        .backgroundColor(getResources().getColor(R.color.primary_light))
                        .enableAutoTextPosition()
                        .closeOnTouch(true)
                        .dismissListener(listenerTutorial)
                        .build();
            }

            if (fancyShowCaseView2 == null) {
                fancyShowCaseView2 = new FancyShowCaseView.Builder(requireActivity())
                        .focusOn(fab)
                        .title(getString(R.string.unique_tutorial_group_fab_string))
                        .dismissListener(listenerTutorial)
                        .backgroundColor(getResources().getColor(R.color.primary_light))
                        .enableAutoTextPosition()
                        .showOnce(getString(R.string.unique_tutorial_group_fab))
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
            if (!fancyShowCaseView2.isShownBefore()) {
                fancyShowCaseView2.show();
                return;
            }
        } catch (Exception e) {
            return;
        }
    }

    public static FrListaGroup novaInstancia(Company company) {
        Bundle parametros = new Bundle();
        parametros.putParcelable(EXTRA_COMPANY, company);
        FrListaGroup fragment = new FrListaGroup();
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
        View view = inflater.inflate(R.layout.fr_list_conjuct, container, false);

        dots = view.findViewById(R.id.dots);
        linearEmpty = view.findViewById(R.id.linearEmpty);
        errorLayout = view.findViewById(R.id.error_layout);
        fabError = view.findViewById(R.id.fabError);
        fabError.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dots.setVisibility(View.VISIBLE);
                errorLayout.setVisibility(View.GONE);
                loaderManager.restartLoader(0, null, FrListaGroup.this);
            }
        });

        Toolbar toolbar = view.findViewById(R.id.toolbar_top);
        Util.setaToolbar(this, R.string.groups, toolbar, getActivity(), false, 0);

        fab = view.findViewById(R.id.fab);
        fab.hide();
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getActivity() instanceof FrListaGroup.ToActivity) {
                    FrListaGroup.ToActivity listener = (FrListaGroup.ToActivity) getActivity();
                    listener.ListaConjuctBottom(R.id.fab, company);
                }
            }
        });

        extraIcon = view.findViewById(R.id.fab2);
        extraIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getActivity() instanceof FrListaGroup.ToActivity) {
                    FrListaGroup.ToActivity listener = (FrListaGroup.ToActivity) getActivity();
                    listener.ListaConjuctBottom(R.id.fab2, company);
                }
            }
        });

        mRecyclerView = view.findViewById(R.id.recyclerview_products);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        adapter = new RvListGroup(this);
        mRecyclerView.setAdapter(adapter);

        loaderManager.restartLoader(0, null, this);

        chainCreatedTutorial();

        return view;
    }

    @NonNull
    @Override
    public Loader<List<Collection>> onCreateLoader(int id, Bundle args) {
        return new ProductsLoader(getActivity(), company.getCompany_id(), data);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<List<Collection>> loader, List<Collection> data) {
        dots.setVisibility(View.GONE);
        if (data != null) {
            fab.show();
            this.data = data;
            if (data.size() > 0) {
                mRecyclerView.setVisibility(View.VISIBLE);
                linearEmpty.setVisibility(View.GONE);
                adapter.setCollection(data);
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
    public void onLoaderReset(@NonNull Loader<List<Collection>> loader) {

    }


    @Override
    public void onClick(Collection collection) {
        Activity activity = getActivity();
        if (activity instanceof RvListGroup.RvConjuctOnClickListener) {
            RvListGroup.RvConjuctOnClickListener listener =
                    (RvListGroup.RvConjuctOnClickListener) activity;
            listener.onClick(collection);
        }
    }

    private static class ProductsLoader extends AsyncTaskLoader<List<Collection>> {

        String storeId;
        Context ctx;
        List<Collection> data;

        ProductsLoader(Context context, String storeId, List<Collection> data) {
            super(context);
            ctx = context;
            this.storeId = storeId;
            this.data = data;
        }

        @Override
        protected void onStartLoading() {
            if (data != null && !data.isEmpty()) {
                deliverResult(data);
                forceLoad();
            } else {
                forceLoad();
            }
        }


        @Override
        public List<Collection> loadInBackground() {
            Gson gson = new Gson();
            try {
                Collection[] collections = gson.fromJson(ConnectApi.GET(ConnectApi.COLLECTION_COMPANY + "/" + storeId, getContext()), Collection[].class);
                return new ArrayList<>(Arrays.asList(collections));
            } catch (Exception e) {
                FirebaseCrashlytics.getInstance().log(UtilShaPre.getDefaultsString(AppConstants.USER_ID, getContext()));
                FirebaseCrashlytics.getInstance().recordException(e);
                return null;
            }
        }
    }

    private static class DeletaProduto extends AsyncTaskLoader<Map<String, Object>> {

        Activity ctx;
        Collection collection;

        DeletaProduto(Activity context, Collection collection) {
            super(context);
            ctx = context;
            this.collection = collection;
        }


        @Override
        protected void onStartLoading() {
            forceLoad();
        }


        @Override
        public Map<String, Object> loadInBackground() {
            Map<String, Object> mapOrdPro;
            mapOrdPro = new HashMap<>();
            try {
                Gson gson = new Gson();
                mapOrdPro.put("delete", gson.fromJson(ConnectApi.DELETE(collection, ConnectApi.COLLECTION, ctx), Integer.class));
                return mapOrdPro;
            } catch (Exception e) {
                FirebaseCrashlytics.getInstance().log(UtilShaPre.getDefaultsString(AppConstants.USER_ID, getContext()));
                FirebaseCrashlytics.getInstance().recordException(e);
                return null;
            }
        }
    }

    public interface ToActivity {
        void ListaConjuctBottom(int index, Company company);
    }
}
