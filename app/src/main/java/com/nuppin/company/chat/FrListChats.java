package com.nuppin.company.chat;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
import com.nuppin.company.model.Chat;
import com.nuppin.company.model.Company;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class FrListChats extends Fragment implements
        LoaderManager.LoaderCallbacks<List<Chat>>,
        RvListChat.ChatOnClickListener {

    private static final String COMPANY = "COMPANY";
    public static boolean active = false;
    private LoaderManager loaderManager;
    private Company company;
    private CardView progress;
    private RvListChat adapter;
    private RecyclerView recyclerView;
    private LottieAnimationView dots;
    private LinearLayout linearEmpty;
    private ConstraintLayout constraint;
    private ConstraintLayout errorLayout;
    private FloatingActionButton fabError;
    private List<Chat> data;
    BroadcastReceiver broadCastNewMessage = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // here you can update your db with new messages and update the ui (chat message list)
            loaderManager.restartLoader(0, null, FrListChats.this);
        }
    };

    public FrListChats() {
    }

    @Override
    public void onDetach() {
        super.onDetach();
        loaderManager = null;
    }

    public static FrListChats newInstance(Company company) {
        FrListChats fragment = new FrListChats();
        Bundle args = new Bundle();
        args.putParcelable(COMPANY, company);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null && getArguments().containsKey(COMPANY)) {
            company = getArguments().getParcelable(COMPANY);
        }
        loaderManager = LoaderManager.getInstance(this);
        if (getActivity() != null) {
            getActivity().registerReceiver(this.broadCastNewMessage, new IntentFilter("bcNewMessage"));
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fr_list_chat, container, false);

        constraint = view.findViewById(R.id.container);
        dots = view.findViewById(R.id.dots);
        linearEmpty = view.findViewById(R.id.linearEmpty);
        errorLayout = view.findViewById(R.id.error_layout);
        fabError = view.findViewById(R.id.fabError);
        fabError.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dots.setVisibility(View.VISIBLE);
                errorLayout.setVisibility(View.GONE);
                loaderManager.restartLoader(0, null, FrListChats.this);
            }
        });

        Toolbar toolbar;
        toolbar = view.findViewById(R.id.toolbar_top);
        Util.setaToolbar(this, R.string.conversas, toolbar, getActivity(), false, 0);

        progress = view.findViewById(R.id.progress);
        recyclerView = view.findViewById(R.id.recyclerview_cashflow);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        adapter = new RvListChat(this);
        recyclerView.setAdapter(adapter);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        active = true;
        loaderManager.restartLoader(0, null, this);
        Util.cancelNotifyOnOff(getContext(), 9);
    }

    @Override
    public void onPause() {
        super.onPause();
        active = false;
        loaderManager.destroyLoader(0);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (getActivity() != null) {
            getActivity().unregisterReceiver(broadCastNewMessage);
        }
    }

    @NonNull
    @Override
    public Loader<List<Chat>> onCreateLoader(int id, Bundle args) {
        return new ChatsLoader(getActivity(), company.getCompany_id(), data);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<List<Chat>> loader, List<Chat> data) {
        dots.setVisibility(View.GONE);
        progress.setVisibility(View.GONE);

        if (data != null) {
            this.data = data;
            if (data.size() > 0) {
                adapter.setChats(data);
                constraint.setVisibility(View.VISIBLE);
                linearEmpty.setVisibility(View.GONE);
            } else {
                constraint.setVisibility(View.GONE);
                linearEmpty.setVisibility(View.VISIBLE);
            }
            errorLayout.setVisibility(View.GONE);
        } else {
            errorLayout.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<List<Chat>> loader) {
    }

    @Override
    public void onClickChat(String order_id, String userId, String userNome, String stoId) {
        if (getActivity() instanceof RvListChat.ChatOnClickListener) {
            RvListChat.ChatOnClickListener listener =
                    (RvListChat.ChatOnClickListener) getActivity();
            listener.onClickChat(order_id, userId, userNome, stoId);
        }
    }

    private static class ChatsLoader extends AsyncTaskLoader<List<Chat>> {

        Context ctx;
        String stoId;
        List<Chat> data;

        ChatsLoader(Context context, String stoId, List<Chat> data) {
            super(context);
            ctx = context;
            this.stoId = stoId;
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
        public List<Chat> loadInBackground() {
            Gson gson = new Gson();
            String stringJson = ConnectApi.GET(ConnectApi.CHAT + "/" + stoId, getContext());
            JsonParser parser = new JsonParser();
            try {
                JsonObject jObj = parser.parse(stringJson).getAsJsonObject();
                Chat[] chats = gson.fromJson(jObj.getAsJsonArray(AppConstants.CHAT), Chat[].class);
                return new ArrayList<>(Arrays.asList(chats));
            } catch (Exception e) {
                FirebaseCrashlytics.getInstance().log(UtilShaPre.getDefaultsString(AppConstants.USER_ID, getContext()));
                FirebaseCrashlytics.getInstance().recordException(e);
                return null;
            }
        }
    }

    public interface ToActivity {
        void nada(Company company);
    }
}
