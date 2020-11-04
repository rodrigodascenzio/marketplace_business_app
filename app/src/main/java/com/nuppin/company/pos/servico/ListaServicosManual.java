package com.nuppin.company.pos.servico;

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
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.nuppin.company.R;
import com.nuppin.company.Util.AppConstants;
import com.nuppin.company.Util.Util;
import com.nuppin.company.Util.UtilShaPre;
import com.nuppin.company.connection.ConnectApi;
import com.nuppin.company.model.Company;
import com.nuppin.company.model.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ListaServicosManual extends Fragment implements
        LoaderManager.LoaderCallbacks<List<Service>>,
        RvServicosAdapterManual.RvServicosOnClickListener{

    private LoaderManager loaderManager;
    private RvServicosAdapterManual serviceAdapter;
    private RecyclerView mRecyclerView;
    private Company company;
    private Toolbar toolbar;
    private static final String EXTRA_STORE = "company";
    private LottieAnimationView dots;
    private LinearLayout linearEmpty;
    private ConstraintLayout errorLayout;
    private FloatingActionButton fabError;
    private List<Service> data;

    public ListaServicosManual() {}


    public static ListaServicosManual novaInstancia2(Company company) {
        Bundle parametros = new Bundle();
        parametros.putParcelable(EXTRA_STORE, company);
        ListaServicosManual fragment = new ListaServicosManual();
        fragment.setArguments(parametros);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null && getArguments().containsKey(EXTRA_STORE)) {
            company = getArguments().getParcelable(EXTRA_STORE);
        }
        loaderManager = LoaderManager.getInstance(this);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fr_list_service_manual, container, false);

        dots = view.findViewById(R.id.dots);
        linearEmpty = view.findViewById(R.id.linearEmpty);
        errorLayout = view.findViewById(R.id.error_layout);
        fabError = view.findViewById(R.id.fabError);
        fabError.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dots.setVisibility(View.VISIBLE);
                errorLayout.setVisibility(View.GONE);
                loaderManager.restartLoader(0, null, ListaServicosManual.this);
            }
        });


        mRecyclerView =  view.findViewById(R.id.recyclerview_service);
        toolbar = view.findViewById(R.id.toolbar_top);
        Util.setaToolbar(this, R.string.agendamento_manual, toolbar, getActivity(), false, 0);

        if (getResources().getBoolean(R.bool.isTablet7) || (getResources().getBoolean(R.bool.isTablet10))) {
            mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        } else {
            mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false));
        }
        serviceAdapter = new RvServicosAdapterManual(this);
        mRecyclerView.setAdapter(serviceAdapter);
        mRecyclerView.setHasFixedSize(true);
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
    public Loader<List<Service>> onCreateLoader(int id, Bundle args) {
        return new ServicesLoader(getActivity(), company.getCompany_id(), data);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<List<Service>> loader, List<Service> data) {
        dots.setVisibility(View.GONE);
        if (data != null) {
            if (data.size() > 0) {
                this.data = data;
                serviceAdapter.setServicos(data);
                linearEmpty.setVisibility(View.GONE);
                mRecyclerView.setVisibility(View.VISIBLE);
            }else {
                linearEmpty.setVisibility(View.VISIBLE);
                mRecyclerView.setVisibility(View.GONE);
            }
            errorLayout.setVisibility(View.GONE);
        }else {
            errorLayout.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<List<Service>> loader) {

    }

    @Override
    public void onClick(Service service, Company companyNull) {
        Activity activity = getActivity();
        if (activity instanceof RvServicosAdapterManual.RvServicosOnClickListener) {
            RvServicosAdapterManual.RvServicosOnClickListener listener = (RvServicosAdapterManual.RvServicosOnClickListener) activity;
            listener.onClick(service, company);
        }
    }

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
            String stringJson = ConnectApi.GET(ConnectApi.SERVICES_STORE + "/" + idStore, getContext());
            JsonParser parser = new JsonParser();
            try {
                JsonObject jObj = parser.parse(stringJson).getAsJsonObject();
                Service[] service = gson.fromJson(jObj.getAsJsonArray("service"), Service[].class);
                return new ArrayList<>(Arrays.asList(service));
            } catch (Exception e) {
                    FirebaseCrashlytics.getInstance().log(UtilShaPre.getDefaultsString(AppConstants.USER_ID, getContext()));
                    FirebaseCrashlytics.getInstance().recordException(e);
                return null;
            }
        }
    }
}
