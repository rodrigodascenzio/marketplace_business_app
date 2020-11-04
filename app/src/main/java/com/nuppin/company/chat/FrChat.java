package com.nuppin.company.chat;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
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
import com.nuppin.company.Util.AppConstants;
import com.nuppin.company.Util.UtilShaPre;
import com.nuppin.company.connection.ConnectApi;
import com.nuppin.company.model.Chat;
import com.nuppin.company.R;
import com.nuppin.company.Util.Util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FrChat extends Fragment implements
        LoaderManager.LoaderCallbacks<Map<String, Object>> {

    private static final String USERID = "userid";
    private static final String ORDORAGEID = "ordorageid";
    private static final String STOID = "stoid";
    private static final String USERNAME = "username";
    public static boolean active = false;
    private LoaderManager loaderManager;
    private ChatAdapter adapter;
    private Chat chat;
    private FloatingActionButton send;
    private EditText edtChat;
    private RecyclerView mRecyclerView;
    private String backEdt;
    private LinearLayout linearEmpty;
    private List data;
    private String userId, ordOrAgeId, stoId, userName;
    BroadcastReceiver broadCastNewMessage = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // here you can update your db with new messages and update the ui (chat message list)
            loaderManager.restartLoader(0, null, FrChat.this);
        }
    };
    private LottieAnimationView dots;

    public FrChat() {
    }

    public static FrChat newInstance(String userId, String ordOrAgeId, String stoId, String userName) {
        FrChat fragment = new FrChat();
        Bundle args = new Bundle();
        args.putString(USERID, userId);
        args.putString(ORDORAGEID, ordOrAgeId);
        args.putString(STOID, stoId);
        args.putString(USERNAME, userName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null && getArguments().containsKey(USERID)) {
            userId = getArguments().getString(USERID);
        }
        if (getArguments() != null && getArguments().containsKey(USERNAME)) {
            userName = getArguments().getString(USERNAME);
        }
        if (getArguments() != null && getArguments().containsKey(STOID)) {
            stoId = getArguments().getString(STOID);
        }
        if (getArguments() != null && getArguments().containsKey(ORDORAGEID)) {
            ordOrAgeId = getArguments().getString(ORDORAGEID);
        }

        loaderManager = LoaderManager.getInstance(this);
        if (getActivity() != null) {
            getActivity().registerReceiver(this.broadCastNewMessage, new IntentFilter("bcNewMessage"));
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.chat_room, container, false);

        Toolbar toolbar = view.findViewById(R.id.toolbar_top);
        Util.setaToolbar(this, userName, toolbar, getActivity(), false, 0);


        dots = view.findViewById(R.id.dots);
        linearEmpty = view.findViewById(R.id.linearEmpty);
        mRecyclerView = view.findViewById(R.id.recyclerview_chat);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);
        adapter = new ChatAdapter(getContext());
        mRecyclerView.setAdapter(adapter);


        edtChat = view.findViewById(R.id.edtChat);
        send = view.findViewById(R.id.send);

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!edtChat.getText().toString().equals("")) {
                    backEdt = edtChat.getText().toString();
                    edtChat.setText("");
                    chat = new Chat();
                    chat.setOrder_id(ordOrAgeId);
                    chat.setChat_from(stoId);
                    chat.setChat_to(userId);
                    chat.setUser_id(userId);
                    chat.setCompany_id(stoId);
                    chat.setMessage(backEdt);
                    chat.setChat_id("");
                    send.setClickable(false);
                    data.add(chat);
                    adapter.setChat(data, stoId);
                    mRecyclerView.scrollToPosition(adapter.getItemCount() - 1);
                    linearEmpty.setVisibility(View.GONE);
                    mRecyclerView.setVisibility(View.VISIBLE);
                    loaderManager.restartLoader(1, null, FrChat.this);
                }
            }
        });

        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (getActivity() != null) {
            getActivity().unregisterReceiver(broadCastNewMessage);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        active = true;
        loaderManager.restartLoader(0, null, this);
    }

    @Override
    public void onPause() {
        super.onPause();
        active = false;
        loaderManager.destroyLoader(0);
    }

    @NonNull
    @Override
    public Loader<Map<String, Object>> onCreateLoader(int id, Bundle args) {
        if (id == 0) {
            return new ChatLoader(getContext(), ordOrAgeId, stoId);
        } else {
            return new SendMessage(getActivity(), chat);
        }
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Map<String, Object>> loader, Map<String, Object> data) {
        dots.setVisibility(View.GONE);
        if (loader.getId() == 0) {
            if (data != null) {
                this.data = (List) data.get(AppConstants.CHAT);
                if (((List) data.get(AppConstants.CHAT)).size() > 0) {
                    adapter.setChat((List) data.get(AppConstants.CHAT), stoId);
                    mRecyclerView.scrollToPosition(adapter.getItemCount() - 1);
                    mRecyclerView.setVisibility(View.VISIBLE);
                    linearEmpty.setVisibility(View.GONE);
                } else {
                    linearEmpty.setVisibility(View.VISIBLE);
                    mRecyclerView.setVisibility(View.GONE);
                }
            } else {
                Toast.makeText(getContext(), R.string.error, Toast.LENGTH_SHORT).show();
            }
        }
        if (loader.getId() == 1) {
            if (data != null) {
                loaderManager.destroyLoader(1);
                loaderManager.restartLoader(0, null, this);
            } else {
                Toast.makeText(getContext(), R.string.send_message_fail, Toast.LENGTH_SHORT).show();
                edtChat.setText(backEdt);
                this.data.remove(chat);
                adapter.setChat(this.data, stoId);
                mRecyclerView.scrollToPosition(adapter.getItemCount() - 1);
            }
        }
        send.setClickable(true);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Map<String, Object>> loader) {

    }

    private static class ChatLoader extends AsyncTaskLoader<Map<String, Object>> {

        Context ctx;
        String ordAgeId, stoId;

        ChatLoader(Context context, String ordAgeId, String stoId) {
            super(context);
            ctx = context;
            this.ordAgeId = ordAgeId;
            this.stoId = stoId;
        }


        @Override
        protected void onStartLoading() {
            forceLoad();
        }


        @Override
        public Map<String, Object> loadInBackground() {
            Gson gson = new Gson();
            Map<String, Object> mapOrdPro;
            mapOrdPro = new HashMap<>();

            try {
                String stringJson = ConnectApi.GET(ConnectApi.ORDER_CHAT + "/" + ordAgeId + "," + stoId, getContext());
                JsonParser parser = new JsonParser();
                JsonObject jObj = parser.parse(stringJson).getAsJsonObject();
                Chat[] chat = gson.fromJson(jObj.getAsJsonArray(AppConstants.CHAT), Chat[].class);
                List messages = new ArrayList<>(Arrays.asList(chat));
                mapOrdPro.put(AppConstants.CHAT, messages);
                return mapOrdPro;
            } catch (Exception e) {
                FirebaseCrashlytics.getInstance().log(UtilShaPre.getDefaultsString(AppConstants.USER_ID, getContext()));
                FirebaseCrashlytics.getInstance().recordException(e);
                return null;
            }
        }
    }

    private static class SendMessage extends AsyncTaskLoader<Map<String, Object>> {

        Context ctx;
        Chat chat;


        SendMessage(Context context, Chat chat) {
            super(context);
            ctx = context;
            this.chat = chat;
        }

        @Override
        protected void onStartLoading() {
            forceLoad();
        }

        @Override
        public Map<String, Object> loadInBackground() {
            Gson gson = new Gson();
            Map<String, Object> mapOrdPro;
            mapOrdPro = new HashMap<>();
            JsonParser parser = new JsonParser();
            try {
                String json = ConnectApi.POST(chat, ConnectApi.ORDER_CHAT + "/user", getContext());
                Chat chat = gson.fromJson(parser.parse(json).getAsJsonObject().getAsJsonObject(), Chat.class);
                mapOrdPro.put(AppConstants.CHAT, chat);
               /* try {
                    if (isFotoPath) {
                        String ok = gson.fromJson(ConnectApi.enviarFoto(getContext(),fotoPath, user.getCompany_id(), AppConstants.USERS),String.class);
                        mapOrdPro.put("photo", ok);
                    }
                }catch (Exception e){
                    mapOrdPro.put("photo", "null");
                }*/
            } catch (Exception e) {
                FirebaseCrashlytics.getInstance().log(UtilShaPre.getDefaultsString(AppConstants.USER_ID, getContext()));
                FirebaseCrashlytics.getInstance().recordException(e);
                try {
                    //ErrorCode b = gson.fromJson(parser.parse(json).getAsJsonObject().getAsJsonObject(AppConstants.ERROR), ErrorCode.class);
                    //mapOrdPro.put(AppConstants.ERROR, b);
                    return null;
                } catch (Exception e1) {
                    FirebaseCrashlytics.getInstance().log(UtilShaPre.getDefaultsString(AppConstants.USER_ID, getContext()));
                    FirebaseCrashlytics.getInstance().recordException(e1);
                    return null;
                }
            }
            return mapOrdPro;
        }
    }
}