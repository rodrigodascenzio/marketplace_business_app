package com.nuppin.company.Loja.Fatura;

import android.content.Context;
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
import com.nuppin.company.R;
import com.nuppin.company.Util.AppConstants;
import com.nuppin.company.Util.Util;
import com.nuppin.company.Util.UtilShaPre;
import com.nuppin.company.connection.ConnectApi;
import com.nuppin.company.model.Order;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FrOrdersHistoricoFatura extends Fragment
        implements LoaderManager.LoaderCallbacks<Object> {

    private LoaderManager loaderManager;
    private RvOrdersHistoricoFaturaAdapter adapter;
    private RecyclerView mRecyclerView;
    private static final String COMPANY = "COMPANY";
    private static final String DATA_CAD = "DATACAD";
    private String storeId;
    private String dataCad;
    private LottieAnimationView dots;
    private CardView progress;

    public FrOrdersHistoricoFatura() {
    }

    public static FrOrdersHistoricoFatura newInstance(String storeId, String dataCad) {
        FrOrdersHistoricoFatura fragment = new FrOrdersHistoricoFatura();
        Bundle args = new Bundle();
        args.putString(COMPANY, storeId);
        args.putString(DATA_CAD, dataCad);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        assert getArguments() != null;
        if (getArguments().containsKey(COMPANY)) {
            storeId = getArguments().getString(COMPANY);
            if (getArguments().containsKey(DATA_CAD)) {
                dataCad = getArguments().getString(DATA_CAD);
            }
        }
        loaderManager = LoaderManager.getInstance(this);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fr_invoice_request, container, false);

        Toolbar toolbar = view.findViewById(R.id.toolbar_top);
        Util.setaToolbar(this, R.string.historico, toolbar, getActivity(), false, 0);

        dots = view.findViewById(R.id.dots);
        progress = view.findViewById(R.id.progress);
        mRecyclerView = view.findViewById(R.id.recyclerview_order);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);
        adapter = new RvOrdersHistoricoFaturaAdapter();
        mRecyclerView.setAdapter(adapter);
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
    public Loader onCreateLoader(int id, Bundle args) {
        return new StoresLoader(getActivity(), storeId, dataCad);
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
            }
        } else {
            Toast.makeText(getContext(), R.string.error, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader loader) {
    }

    private static class StoresLoader extends AsyncTaskLoader<Object> {

        Context ctx;
        String storeId;
        String dataCad;

        StoresLoader(Context context, String storeId, String dataCad) {
            super(context);
            ctx = context;
            this.storeId = storeId;
            this.dataCad = dataCad;
        }

        @Override
        protected void onStartLoading() {
            forceLoad();
        }

        @Override
        public Object loadInBackground() {
            Gson gson = new Gson();
            try {
                String sJson = ConnectApi.GET(ConnectApi.COMPANY_INVOICE_DETAIL_ORDER + "/" + storeId + "," + dataCad, getContext());
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

    public interface ToActivity {
        void detalhe(Order order);
    }
}