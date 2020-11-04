package com.nuppin.company.tablet;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
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
import com.nuppin.company.Util.AppConstants;
import com.nuppin.company.Util.UtilShaPre;
import com.nuppin.company.connection.ConnectApi;
import com.nuppin.company.Loja.Orders.RvOrdersAdapter;
import com.nuppin.company.model.Order;
import com.nuppin.company.R;
import com.nuppin.company.Util.Util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FrOrdersTablet extends Fragment
        implements LoaderManager.LoaderCallbacks,
        RvOrdersAdapter.RvOrdersAdapterOnClickListener {

    public static boolean active = false;
    private LoaderManager loaderManager;
    private RvOrdersAdapter adapter;
    private RecyclerView mRecyclerView;
    private static final String COMPANY = "COMPANY";
    private static final String POSITION = "POSITION";
    private String storeId, status;
    private Order order;
    private int rating;
    private String position;
    private LottieAnimationView dots;
    private CardView progress;
    private BroadcastReceiver broadCastNewMessage = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // here you can update your db with new messages and update the ui (chat message list)
            loaderManager.restartLoader(0, null, FrOrdersTablet.this);
        }
    };

    public FrOrdersTablet() {
    }

    public static FrOrdersTablet newInstance(String storeId, String position) {
        FrOrdersTablet fragment = new FrOrdersTablet();
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
            getActivity().registerReceiver(this.broadCastNewMessage, new IntentFilter("newOrderFr"));
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
                titulo = R.string.em_andamento;
                break;
            case AppConstants.STATUS_DELIVERY:
                titulo = R.string.entregando;
                break;
            case AppConstants.STATUS_RELEASED:
                titulo = R.string.esperando_retirada;
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
        adapter = new RvOrdersAdapter(this);
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
            dots.playAnimation();
            return new StoresLoader(getActivity(), storeId, position);
        } else {
            return new AtualizaStatus(getActivity(), order, status, rating);
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
                adapter.setOrder((List) data);
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
                loaderManager.destroyLoader(loader.getId());
            }
        } else {
            Toast.makeText(getContext(), R.string.error, Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onLoaderReset(@NonNull Loader loader) {
    }

    @Override
    public void onClick(String order_id) {
    }

    @Override
    public void onClickBtn(Order order, String atualizarStatusPara) {
        this.order = order;
        status = atualizarStatusPara;
        progress.setVisibility(View.VISIBLE);
        loaderManager.restartLoader(1, null, this);
    }

    @Override
    public void onClickBtnRating(Order order, String atualizarStatusPara, int rating) {
        if (rating == 0) {
            Toast.makeText(getActivity(), R.string.warning_before_update_status_order, Toast.LENGTH_SHORT).show();
            return;
        }
        this.rating = rating;
        this.order = order;
        status = atualizarStatusPara;
        progress.setVisibility(View.VISIBLE);
        loaderManager.restartLoader(1, null, this);
    }

    @Override
    public void chat(Order order) {
        if (getActivity() instanceof ToActivity) {
            ToActivity listener = (ToActivity) getActivity();
            listener.chat(order);
        }
    }

    @Override
    public void enderecoClick(Order order) {
        try {
            Uri gmmIntentUri = Uri.parse("google.navigation:q=" + order.getLatitude() + "," + order.getLongitude());
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            mapIntent.setPackage("com.google.android.apps.maps");
            startActivity(mapIntent);
        } catch (Exception e) {
            Toast.makeText(getContext(), "Erro ao abrir no maps", Toast.LENGTH_SHORT).show();
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
                String sJson = ConnectApi.GET(ConnectApi.GET_ORDERS + "/" + storeId + "," + position, getContext());
                JsonParser parser = new JsonParser();
                JsonObject jObj = parser.parse(sJson).getAsJsonObject();
                JsonArray jArray = jObj.getAsJsonArray(AppConstants.ORDERS);
                Order[] o = gson.fromJson(jArray, Order[].class);
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

        Order order;
        String status;
        int rating;
        Context ctx;

        private AtualizaStatus(Context context, Order order, String status, int rating) {
            super(context);
            ctx = context;
            this.order = order;
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
                String sJson;
                if (rating != 0) {
                    sJson = ConnectApi.PATCH(order, ConnectApi.ORDER_UPDATE_STATUS + "/" + status + "/" + rating, getContext());
                } else {
                    sJson = ConnectApi.PATCH(order, ConnectApi.ORDER_UPDATE_STATUS + "/" + status, getContext());
                }
                JsonParser parser = new JsonParser();
                return gson.fromJson(parser.parse(sJson).getAsJsonPrimitive(), Boolean.class);
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

        void chat(Order order);
    }
}