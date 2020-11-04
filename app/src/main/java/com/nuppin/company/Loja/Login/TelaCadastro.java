package com.nuppin.company.Loja.Login;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.AsyncTaskLoader;
import androidx.loader.content.Loader;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.gson.Gson;
import com.google.gson.JsonParser;
import com.nuppin.company.Util.AppConstants;
import com.nuppin.company.Util.Util;
import com.nuppin.company.connection.ConnectApi;
import com.nuppin.company.firebase.updateUserToken;
import com.nuppin.company.Loja.activity.MainStoreActivity;
import com.nuppin.company.model.User;
import com.nuppin.company.R;
import com.nuppin.company.Util.UtilShaPre;
import com.nuppin.company.tablet.MainStoreActivityTablet;

public class TelaCadastro extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Object> {

    private EditText nome;
    private TextInputLayout tNome;
    private User user;
    private LoaderManager loaderManager;
    private CardView progress;
    private MaterialButton btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        user = new User();

        if (getIntent().getStringExtra("CELLPHONE") != null) {
            user.setPhone_number(getIntent().getStringExtra("CELLPHONE"));
        }

        if (getIntent().getStringExtra("EMAIL") != null) {
            user.setEmail(getIntent().getStringExtra("EMAIL"));
        }

        TextView termos = findViewById(R.id.termos);
        termos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent it = new Intent(TelaCadastro.this, TermosDeUso.class);
                startActivity(it);
            }
        });

        nome = findViewById(R.id.editNome);
        tNome = findViewById(R.id.editNomeT);

        progress = findViewById(R.id.progress);

        btn = findViewById(R.id.criarConta);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validar()) {
                    btn.setClickable(false);
                    progress.setVisibility(View.VISIBLE);
                    criarPerfil();
                }
            }
        });
    }

    public boolean validar() {
        boolean isGo = true;
        if (nome.getText().toString().isEmpty() || nome.getText().toString().length() < 3) {
            tNome.setErrorEnabled(true);
            isGo = false;
            if (nome.getText().toString().isEmpty()) {
                tNome.setError(getResources().getString(R.string.error_enabled_text));
            } else {
                tNome.setError(getResources().getString(R.string.invalid_name));
            }
        } else {
            tNome.setErrorEnabled(false);
        }


        return isGo;
    }

    private void criarPerfil() {
        user.setNome(Util.clearName(nome.getText().toString()));
        loaderManager = LoaderManager.getInstance(this);
        loaderManager.restartLoader(0, null, this);
    }


    @NonNull
    @Override
    public Loader<Object> onCreateLoader(int id, Bundle args) {
        return new UserLoader(this, user);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Object> loader, Object data) {
        progress.setVisibility(View.GONE);
        if (data != null) {
            UtilShaPre.setDefaults(AppConstants.USER_ID, ((User) data).getId(), this);
            UtilShaPre.setDefaults("user_nome", ((User) data).getNome(), this);
            UtilShaPre.setDefaults("user_email", ((User) data).getEmail(), this);
            UtilShaPre.setDefaults("refresh_token", ((User) data).getRefresh_token(), this);
            UtilShaPre.setDefaults("user_logged", true, this);

            startService(new Intent(this, updateUserToken.class));

            Intent it;
            if (getResources().getBoolean(R.bool.isTablet10) || getResources().getBoolean(R.bool.isTablet7)) {
                it = new Intent(TelaCadastro.this, MainStoreActivityTablet.class);
                it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(it);
            } else {
                it = new Intent(TelaCadastro.this, MainStoreActivity.class);
                it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(it);
            }
        } else {
            Toast.makeText(this, R.string.error, Toast.LENGTH_SHORT).show();
        }
        btn.setClickable(true);
        loaderManager.destroyLoader(loader.getId());
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Object> loader) {

    }

    private static class UserLoader extends AsyncTaskLoader<Object> {
        User user;
        Context ctx;

        UserLoader(Context context, User user__) {
            super(context);
            user = user__;
            ctx = context;
        }

        @Override
        protected void onStartLoading() {
            forceLoad();
        }


        @Override
        public Object loadInBackground() {
            Gson gson = new Gson();
            String json = ConnectApi.BEARER(user, ConnectApi.SIGNUP, getContext());
            JsonParser parser = new JsonParser();
            try {
                return gson.fromJson(parser.parse(json).getAsJsonObject(), User.class);
            } catch (Exception e) {
                FirebaseCrashlytics.getInstance().log(UtilShaPre.getDefaultsString(AppConstants.USER_ID, getContext()));
                FirebaseCrashlytics.getInstance().recordException(e);
                return null;
            }
        }
    }
}
