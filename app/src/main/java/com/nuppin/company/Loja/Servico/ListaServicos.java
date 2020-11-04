package com.nuppin.company.Loja.Servico;

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
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.AsyncTaskLoader;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.ItemTouchHelper;
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
import com.nuppin.company.model.Company;
import com.nuppin.company.model.Service;
import com.nuppin.company.R;
import com.nuppin.company.Util.Util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import me.toptas.fancyshowcase.FancyShowCaseView;

public class ListaServicos extends Fragment implements
        LoaderManager.LoaderCallbacks<List<Service>>,
        RvServicosAdapter.RvServicosOnClickListener {

    private LoaderManager loaderManager;
    private RvServicosAdapter servicosAdapter;
    private RecyclerView mRecyclerView;
    private Company company;
    private static final String EXTRA_COMPANY = "company";
    private List<Service> data;
    private LottieAnimationView dots;
    private LinearLayout linearEmpty;
    private ConstraintLayout errorLayout;
    private FloatingActionButton fabError, fab;
    private CardView progress;

    private FancyShowCaseView fancyShowCaseView1;

    private void chainCreatedTutorial() {
        try {
            if (fancyShowCaseView1 == null) {
                fancyShowCaseView1 = new FancyShowCaseView.Builder(requireActivity())
                        .focusOn(fab)
                        .delay(500)
                        .title(getString(R.string.unique_tutorial_lista_servicos_fab_string))
                        .showOnce(getString(R.string.unique_tutorial_lista_servicos_fab))
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


    public ListaServicos() {
    }


    public static ListaServicos newInstance(Company company) {
        Bundle parametros = new Bundle();
        parametros.putParcelable(EXTRA_COMPANY, company);
        ListaServicos fragment = new ListaServicos();
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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fr_list_service, container, false);

        progress = view.findViewById(R.id.progress);
        dots = view.findViewById(R.id.dots);
        linearEmpty = view.findViewById(R.id.linearEmpty);
        errorLayout = view.findViewById(R.id.error_layout);
        fabError = view.findViewById(R.id.fabError);
        fabError.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dots.setVisibility(View.VISIBLE);
                errorLayout.setVisibility(View.GONE);
                loaderManager.restartLoader(0, null, ListaServicos.this);
            }
        });

        mRecyclerView = view.findViewById(R.id.recyclerview_products);
        Toolbar toolbar = view.findViewById(R.id.toolbar_top);
        Util.setaToolbar(this, R.string.servicos_toolbar, toolbar, getActivity(), false, 0);

        fab = view.findViewById(R.id.fab);
        fab.hide();
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getActivity() instanceof ListaServicos.ToActivity) {
                    ListaServicos.ToActivity listener = (ListaServicos.ToActivity) getActivity();
                    listener.ServicosBottom(company);
                }
            }
        });

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        servicosAdapter = new RvServicosAdapter(this, requireActivity());
        mRecyclerView.setAdapter(servicosAdapter);
        mRecyclerView.setHasFixedSize(true);
        loaderManager.restartLoader(0, null, this);

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(mRecyclerView);

        chainCreatedTutorial();

        return view;
    }

    @NonNull
    @Override
    public Loader<List<Service>> onCreateLoader(int id, Bundle args) {
        if (id == 0) {
            return new ServicesLoader(getActivity(), company.getCompany_id(), data);
        } else {
            return new UpdateServicePosition(getActivity(), data);
        }

    }

    @Override
    public void onPause() {
        super.onPause();
        loaderManager.destroyLoader(0);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<List<Service>> loader, List<Service> data) {
        dots.setVisibility(View.GONE);
        progress.setVisibility(View.GONE);
        if (data != null) {
            fab.show();
            this.data = data;
            if (loader.getId() == 0) {
                if (data.isEmpty()) {
                    mRecyclerView.setVisibility(View.GONE);
                    linearEmpty.setVisibility(View.VISIBLE);
                } else {
                    mRecyclerView.setVisibility(View.VISIBLE);
                    linearEmpty.setVisibility(View.GONE);
                    servicosAdapter.setServicos(data);
                }
            }
            errorLayout.setVisibility(View.GONE);
        } else {
            errorLayout.setVisibility(View.VISIBLE);
        }

        if (loader.getId() != 0) {
            if (loader.getId() == 1) {
                loaderManager.restartLoader(0, null, this);
            }
            loaderManager.destroyLoader(loader.getId());
        }
        chainTutorial();
    }

    @Override
    public void onLoaderReset(@NonNull Loader<List<Service>> loader) {

    }

    @Override
    public void onClick(Service service) {
        Activity activity = getActivity();
        if (activity instanceof RvServicosAdapter.RvServicosOnClickListener) {
            RvServicosAdapter.RvServicosOnClickListener listener = (RvServicosAdapter.RvServicosOnClickListener) activity;
            listener.onClick(service);
        }
    }


    ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.START | ItemTouchHelper.END, 0) {
        private boolean mOrderChanged;

        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {

            int fromPosition = viewHolder.getAdapterPosition();
            int toPosition = target.getAdapterPosition();
            Collections.swap(data, fromPosition, toPosition);
            if (recyclerView.getAdapter() != null) {
                recyclerView.getAdapter().notifyItemMoved(fromPosition, toPosition);
            }
            mOrderChanged = true;
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

        }

        @Override
        public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
            super.onSelectedChanged(viewHolder, actionState);

            if (actionState == ItemTouchHelper.ACTION_STATE_IDLE && mOrderChanged) {
                progress.setVisibility(View.VISIBLE);
                for (int i = 0; i < data.size(); i++) {
                    data.get(i).setPosition(i);
                }
                loaderManager.restartLoader(1, null, ListaServicos.this);
                mOrderChanged = false;
            }
        }

    };


    private static class ServicesLoader extends AsyncTaskLoader<List<Service>> {

        String idStore;
        List<Service> data;

        ServicesLoader(Context context, String idStore, List<Service> data) {
            super(context);
            this.idStore = idStore;
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
        public List<Service> loadInBackground() {
            Gson gson = new Gson();
            try {
                String stringJson = ConnectApi.GET(ConnectApi.SERVICE_COMPANY + "/" + idStore, getContext());
                JsonParser parser = new JsonParser();
                JsonObject jObj = parser.parse(stringJson).getAsJsonObject();
                Service[] servicos = gson.fromJson(jObj.getAsJsonArray(AppConstants.SERVICE), Service[].class);
                return new ArrayList<>(Arrays.asList(servicos));
            } catch (Exception e) {
                FirebaseCrashlytics.getInstance().log(UtilShaPre.getDefaultsString(AppConstants.USER_ID, getContext()));
                FirebaseCrashlytics.getInstance().recordException(e);
                return null;
            }
        }
    }

    private static class UpdateServicePosition extends AsyncTaskLoader<List<Service>> {

        Context ctx;
        List<Service> service;

        UpdateServicePosition(Context context, List<Service> service) {
            super(context);
            ctx = context;
            this.service = service;
        }

        @Override
        protected void onStartLoading() {
            forceLoad();
        }

        @Override
        public List<Service> loadInBackground() {
            try {
                Gson gson = new Gson();
                return new ArrayList<>(Arrays.asList(gson.fromJson(ConnectApi.PATCH(service, ConnectApi.SERVICE_POSITION, getContext()), Service[].class)));
            } catch (Exception e) {
                FirebaseCrashlytics.getInstance().log(UtilShaPre.getDefaultsString(AppConstants.USER_ID, getContext()));
                FirebaseCrashlytics.getInstance().recordException(e);
                return null;
            }
        }
    }

    public interface ToActivity {
        void ServicosBottom(Company company);
    }
}
