package com.nuppin.company.tablet;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.AsyncTaskLoader;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.nuppin.company.Loja.agendamentos.RvAgeAdapter;
import com.nuppin.company.R;
import com.nuppin.company.Util.AppConstants;
import com.nuppin.company.Util.Util;
import com.nuppin.company.Util.UtilShaPre;
import com.nuppin.company.connection.ConnectApi;
import com.nuppin.company.model.Scheduling;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FrAgeTablet extends Fragment
        implements LoaderManager.LoaderCallbacks,
        RvAgeAdapter.RvAgeAdapterOnClickListener {

    public static boolean active = false;
    private LoaderManager loaderManager;
    private RecyclerView mRecyclerView;
    private RvAgeAdapter adapter;
    private static final String COMPANY = "COMPANY";
    private static final String POSITION = "POSITION";
    private String storeId, status;
    private Scheduling scheduling;
    private int rating;
    private String position;
    private LottieAnimationView dots;
    private CardView progress;
    private BroadcastReceiver broadCastNewMessage = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // here you can update your db with new messages and update the ui (chat message list)
            loaderManager.restartLoader(0, null, FrAgeTablet.this);
        }
    };

    public FrAgeTablet() {
    }

    public static FrAgeTablet newInstance(String storeId, String position) {
        FrAgeTablet fragment = new FrAgeTablet();
        Bundle args = new Bundle();
        args.putString(COMPANY, storeId);
        args.putString(POSITION, position);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        assert getArguments() != null;
        if (getArguments().containsKey(COMPANY)) {
            storeId = getArguments().getString(COMPANY);
        }
        if (getArguments().containsKey(POSITION)) {
            position = getArguments().getString(POSITION);
        }
        if (getActivity() != null) {
            getActivity().registerReceiver(this.broadCastNewMessage, new IntentFilter("newAgeFr"));
        }
        loaderManager = LoaderManager.getInstance(this);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fr_request, container, false);

        int titulo = 0;

        switch (position) {
            case AppConstants.STATUS_PENDING:
                titulo = R.string.novos;
                break;
            case AppConstants.STATUS_ACCEPTED:
                titulo = R.string.agendados;
                break;
            case AppConstants.STATUS_CONCLUDED_NOT_RATED:
                titulo = R.string.concluidos;
                break;
        }
        Toolbar toolbar = view.findViewById(R.id.toolbar_top);
        Util.setaToolbar(this, titulo, toolbar, getActivity(), false, 0);

        dots = view.findViewById(R.id.dots);
        progress = view.findViewById(R.id.progress);
        mRecyclerView = view.findViewById(R.id.recyclerview_order);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);
        adapter = new RvAgeAdapter(this);
        mRecyclerView.setAdapter(adapter);
        loaderManager.restartLoader(0, null, this);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        active = true;
    }

    @Override
    public void onPause() {
        super.onPause();
        active = false;
        loaderManager.destroyLoader(0);
    }

    @NonNull
    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        if (id == 0) {
            return new StoresLoader(getActivity(), storeId, position);
        } else {
            return new AtualizaStatus(getActivity(), scheduling, status, rating);
        }
    }

    @Override
    public void onLoadFinished(@NonNull Loader loader, Object data) {
        dots.setVisibility(View.GONE);
        progress.setVisibility(View.GONE);
        if (data != null) {
            if (loader.getId() == 0) {
                if (((List) data).isEmpty()) {
                    loaderManager.destroyLoader(loader.getId());
                    Util.backFragmentFunction(this);
                    return;
                }
                adapter.setAge((List) data);
            } else {
                if ((Boolean) data) {
                    if (getActivity() instanceof ToActivity) {
                        ToActivity listener = (ToActivity) getActivity();
                        listener.RefreshNotify();
                    }
                    loaderManager.restartLoader(0, null, this);
                } else {
                    Toast.makeText(getContext(), R.string.error, Toast.LENGTH_SHORT).show();
                }
            }
        } else {
            Toast.makeText(getContext(), R.string.error, Toast.LENGTH_SHORT).show();
        }
        loaderManager.destroyLoader(loader.getId());
    }

    @Override
    public void onLoaderReset(@NonNull Loader loader) {
    }

    @Override
    public void onClick(String order_id) {
    }

    @Override
    public void onClickBtn(Scheduling age, String atualizarParaStatus) {
        this.scheduling = age;
        status = atualizarParaStatus;
        progress.setVisibility(View.VISIBLE);
        loaderManager.restartLoader(1, null, this);
    }

    @Override
    public void onClickBtnRating(Scheduling age, String atualizarParaStatus, int rating) {
        if (rating == 0) {
            Toast.makeText(getActivity(), R.string.warning_before_update_status_order, Toast.LENGTH_SHORT).show();
            return;
        }
        this.rating = rating;
        this.scheduling = age;
        status = atualizarParaStatus;
        progress.setVisibility(View.VISIBLE);
        loaderManager.restartLoader(1, null, this);
    }

    @Override
    public void chat(Scheduling agendamento) {
        if (getActivity() instanceof ToActivity) {
            ToActivity listener = (ToActivity) getActivity();
            listener.chat(agendamento);
        }
    }

    private static class StoresLoader extends AsyncTaskLoader {

        Context ctx;
        String storeId;
        String position;

        StoresLoader(Context context, String storeId, String position) {
            super(context);
            ctx = context;
            this.storeId = storeId;
            this.position = position;
        }

        @Override
        protected void onStartLoading() {
            forceLoad();
        }

        @Override
        public Object loadInBackground() {
            Gson gson = new Gson();
            try {
                String sJson = ConnectApi.GET(ConnectApi.SCHEDULING + "/" + storeId + "," + position + "," + Util.returnStringLatLonCountr(getContext()), getContext());
                JsonParser parser = new JsonParser();
                JsonObject jObj = parser.parse(sJson).getAsJsonObject();
                JsonArray jArray = jObj.getAsJsonArray(AppConstants.SCHEDULING);
                Scheduling[] o = gson.fromJson(jArray, Scheduling[].class);
                return new ArrayList(Arrays.asList(o));
            } catch (Exception e) {
                FirebaseCrashlytics.getInstance().log(UtilShaPre.getDefaultsString(AppConstants.USER_ID, getContext()));
                FirebaseCrashlytics.getInstance().recordException(e);
                e.printStackTrace();
                return null;
            }
        }
    }


    private static class AtualizaStatus extends AsyncTaskLoader {

        Scheduling agendamento;
        String status;
        int rating;
        Context ctx;

        private AtualizaStatus(Context context, Scheduling agendamento, String status, int rating) {
            super(context);
            ctx = context;
            this.agendamento = agendamento;
            this.status = status;
            this.rating = rating;
        }

        @Override
        protected void onStartLoading() {
            forceLoad();
        }


        @Override
        public Boolean loadInBackground() {
            try {
                Gson gson = new Gson();
                String json;
                if (rating != 0) {
                    json = ConnectApi.PATCH(agendamento, ConnectApi.UPDATE_STATUS_SCHEDULING + "/" + status + "/" + rating, getContext());
                } else {
                    json = ConnectApi.PATCH(agendamento, ConnectApi.UPDATE_STATUS_SCHEDULING + "/" + status + "/" + rating, getContext());
                }
                JsonParser parser = new JsonParser();
                return gson.fromJson(parser.parse(json).getAsJsonPrimitive(), Boolean.class);
            } catch (Exception e) {
                FirebaseCrashlytics.getInstance().log(UtilShaPre.getDefaultsString(AppConstants.USER_ID, getContext()));
                FirebaseCrashlytics.getInstance().recordException(e);
                return null;
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (getActivity() != null) {
            getActivity().unregisterReceiver(broadCastNewMessage);
        }
    }

    public interface ToActivity {
        void RefreshNotify();

        void chat(Scheduling agendamento);
    }
}