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
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.gson.Gson;
import com.google.gson.JsonParser;
import com.nuppin.company.Util.AppConstants;
import com.nuppin.company.connection.ConnectApi;
import com.nuppin.company.firebase.updateUserToken;
import com.nuppin.company.Loja.activity.MainStoreActivity;
import com.nuppin.company.model.TempEmail;
import com.nuppin.company.model.User;
import com.nuppin.company.R;
import com.nuppin.company.Util.UtilShaPre;
import com.nuppin.company.tablet.MainStoreActivityTablet;

public class TelaCadastroEmailVerifyCode extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Object> {

    private EditText code;
    private TempEmail tempEmail;
    private LoaderManager loaderManager;
    private CardView progress;
    private MaterialButton btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_code_email);

        loaderManager = LoaderManager.getInstance(this);

        tempEmail = new TempEmail();

        if (getIntent().getStringExtra("EMAIL") != null) {
            tempEmail.setEmail(getIntent().getStringExtra("EMAIL"));
        }

        TextView reenviar = findViewById(R.id.reenviar);
        reenviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        code = findViewById(R.id.codigo);
        progress = findViewById(R.id.progress);
        btn = findViewById(R.id.criarConta);

        TextView email = findViewById(R.id.txtSubEmail);
        email.setText(getResources().getString(R.string.email_caption, tempEmail.getEmail()));


        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!code.getText().toString().equals("")) {
                    tempEmail.setCode(code.getText().toString());
                    btn.setClickable(false);
                    progress.setVisibility(View.VISIBLE);
                    loaderManager.restartLoader(0, null, TelaCadastroEmailVerifyCode.this);
                }
            }
        });
    }

    @NonNull
    @Override
    public Loader<Object> onCreateLoader(int id, Bundle args) {
        return new ConfirmCode(this, tempEmail);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Object> loader, Object data) {
        progress.setVisibility(View.GONE);
        if (data != null) {
            if (data instanceof User) {
                UtilShaPre.setDefaults(AppConstants.USER_ID, ((User) data).getId(), this);
                UtilShaPre.setDefaults("user_nome", ((User) data).getNome(), this);
                UtilShaPre.setDefaults("user_email", ((User) data).getEmail(), this);
                UtilShaPre.setDefaults("refresh_token", ((User) data).getRefresh_token(), this);
                UtilShaPre.setDefaults("user_logged", true, this);

                startService(new Intent(this, updateUserToken.class));

                Intent it;
                if (getResources().getBoolean(R.bool.isTablet10) || getResources().getBoolean(R.bool.isTablet7)) {
                    it = new Intent(TelaCadastroEmailVerifyCode.this, MainStoreActivityTablet.class);
                    it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(it);
                } else {
                    it = new Intent(TelaCadastroEmailVerifyCode.this, MainStoreActivity.class);
                    it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(it);
                }
            }
            if (data instanceof Integer) {
                switch ((int) data) {
                    case 1:
                        Intent it = new Intent(this, TelaCadastro.class);
                        it.putExtra("EMAIL", tempEmail.getEmail());
                        startActivity(it);
                        break;
                    case 2:
                        Toast.makeText(this, R.string.wrong_code, Toast.LENGTH_SHORT).show();
                        break;
                    case 3:
                        Toast.makeText(this, R.string.exp_code, Toast.LENGTH_SHORT).show();
                        break;
                    case 4:
                        Toast.makeText(this, R.string.email_exist, Toast.LENGTH_SHORT).show();
                        break;
                }
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

    private static class ConfirmCode extends AsyncTaskLoader<Object> {
        TempEmail tempEmail;
        Context ctx;

        ConfirmCode(Context context, TempEmail tempEmail) {
            super(context);
            this.tempEmail = tempEmail;
            ctx = context;
        }

        @Override
        protected void onStartLoading() {
            forceLoad();
        }


        @Override
        public Object loadInBackground() {
            Gson gson = new Gson();
            String json = ConnectApi.BEARER(tempEmail, ConnectApi.VERIFY_CODE_FROM_EMAIL, getContext());
            JsonParser parser = new JsonParser();
            try {
                return gson.fromJson(parser.parse(json).getAsJsonObject(), User.class);
            } catch (Exception e) {
                FirebaseCrashlytics.getInstance().log(UtilShaPre.getDefaultsString(AppConstants.USER_ID, getContext()));
                FirebaseCrashlytics.getInstance().recordException(e);
                try {
                    return gson.fromJson(parser.parse(json).getAsJsonPrimitive(), Integer.class);
                } catch (Exception e1) {
                    FirebaseCrashlytics.getInstance().log(UtilShaPre.getDefaultsString(AppConstants.USER_ID, getContext()));
                    FirebaseCrashlytics.getInstance().recordException(e1);
                    return null;
                }
            }
        }
    }
}
