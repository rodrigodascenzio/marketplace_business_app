package com.nuppin.company.Loja.Login;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.AsyncTaskLoader;
import androidx.loader.content.Loader;

import com.airbnb.lottie.LottieAnimationView;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.gson.Gson;
import com.google.gson.JsonParser;
import com.nuppin.company.Util.AppConstants;
import com.nuppin.company.Util.UtilShaPre;
import com.nuppin.company.connection.ConnectApi;
import com.nuppin.company.R;
import com.nuppin.company.Util.Util;

public class TermosDeUso extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Object> {

    private LoaderManager loaderManager;
    private TextView termos;
    private LottieAnimationView dots;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loaderManager = LoaderManager.getInstance(this);
        setContentView(R.layout.activity_term);
        Toolbar toolbar = findViewById(R.id.toolbar_top);
        Util.setaToolbarActivity(0, toolbar, this, true, R.drawable.ic_clear_white_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        termos = findViewById(R.id.termos);
        dots = findViewById(R.id.dots);
        loaderManager.restartLoader(0, null, this);
    }

    @Override
    public void onPause() {
        super.onPause();
        loaderManager.destroyLoader(0);
    }

    @NonNull
    @Override
    public Loader<Object> onCreateLoader(int id, Bundle args) {
        return new TermosLoader(this);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Object> loader, Object data) {
        dots.setVisibility(View.GONE);
        if (data != null) {
            termos.setText(String.valueOf(data));
        } else {
            Toast.makeText(this, R.string.error, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Object> loader) {
    }


    private static class TermosLoader extends AsyncTaskLoader<Object> {

        Context ctx;

        TermosLoader(Context context) {
            super(context);
            ctx = context;
        }


        @Override
        protected void onStartLoading() {
            forceLoad();
        }


        @Override
        public Object loadInBackground() {
            Gson gson = new Gson();
            try {
                JsonParser parser = new JsonParser();
                String json = ConnectApi.GET(ConnectApi.LEGAL+"/user_agreement", getContext());
                return gson.fromJson(parser.parse(json).getAsJsonObject().getAsJsonPrimitive("content"), String.class);
            } catch (Exception e) {
                FirebaseCrashlytics.getInstance().log(UtilShaPre.getDefaultsString(AppConstants.USER_ID, getContext()));
                FirebaseCrashlytics.getInstance().recordException(e);
                return null;
            }
        }
    }
}
