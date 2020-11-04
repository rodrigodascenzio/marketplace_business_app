package com.nuppin.company.Loja.loja.perfil_user;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.AsyncTaskLoader;
import androidx.loader.content.Loader;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.gson.Gson;
import com.google.gson.JsonParser;
import com.nuppin.company.R;
import com.nuppin.company.Util.AppConstants;
import com.nuppin.company.Util.Util;
import com.nuppin.company.Util.UtilShaPre;
import com.nuppin.company.connection.ConnectApi;
import com.nuppin.company.model.TempEmail;
import com.nuppin.company.model.User;

public class FrPerfilUsuarioUpdateEmail extends Fragment implements
        LoaderManager.LoaderCallbacks<Object> {

    private LoaderManager loaderManager;
    private EditText email, code;
    private TextView emailVerifyHint;
    private TextInputLayout emailWrap;
    private Button btn, verify;
    private CardView progress1, progress2;
    private ViewFlipper viewFlipper;
    private TempEmail tempEmail;
    private static final String USER = "USER";
    private User user;


    public FrPerfilUsuarioUpdateEmail() {
    }

    public static FrPerfilUsuarioUpdateEmail newInstance(User user) {
        FrPerfilUsuarioUpdateEmail fragment = new FrPerfilUsuarioUpdateEmail();
        Bundle args = new Bundle();
        args.putParcelable(USER, user);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null && getArguments().containsKey(USER)) {
            user = getArguments().getParcelable(USER);
        }
        loaderManager = LoaderManager.getInstance(this);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.update_email, container, false);

        Toolbar toolbar = view.findViewById(R.id.toolbar_top);
        Util.setaToolbar(this, 0, toolbar, getActivity(), true, R.drawable.ic_clear_white_24dp);

        viewFlipper = view.findViewById(R.id.viewFlipper);
        viewFlipper.setDisplayedChild(0);

        progress1 = view.findViewById(R.id.progress1);
        progress2 = view.findViewById(R.id.progress2);

        email = view.findViewById(R.id.edtEmail);
        emailWrap = view.findViewById(R.id.edtEmailT);
        code = view.findViewById(R.id.codigo);
        btn = view.findViewById(R.id.btnSendEmail);
        verify = view.findViewById(R.id.btnVerify);
        emailVerifyHint = view.findViewById(R.id.txtSubEmail);

        tempEmail = new TempEmail();

        TextView reenviar = view.findViewById(R.id.reenviar);

        reenviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewFlipper.setDisplayedChild(0);
            }
        });

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!validar()) {
                    return;
                }
                if (email.getText().toString().equals(user.getEmail())) {
                    Toast.makeText(getContext(), "Esse já é o seu email", Toast.LENGTH_SHORT).show();
                    return;
                }
                btn.setClickable(false);
                tempEmail.setEmail(email.getText().toString());
                progress1.setVisibility(View.VISIBLE);
                loaderManager.restartLoader(0, null, FrPerfilUsuarioUpdateEmail.this);
            }
        });

        verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!code.getText().toString().equals("")) {
                    tempEmail.setUser_id(UtilShaPre.getDefaultsString(AppConstants.USER_ID, getContext()));
                    tempEmail.setCode(code.getText().toString());
                    verify.setClickable(false);
                    progress2.setVisibility(View.VISIBLE);
                    loaderManager.restartLoader(1, null, FrPerfilUsuarioUpdateEmail.this);
                }
            }
        });

        return view;
    }

    private boolean validar() {
        boolean isGo = true;
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email.getText().toString()).matches()) {
            emailWrap.setErrorEnabled(true);
            emailWrap.setError(getResources().getString(R.string.invalid_email));
            isGo = false;
        } else {
            emailWrap.setErrorEnabled(false);
        }
        return isGo;
    }

    @NonNull
    @Override
    public Loader<Object> onCreateLoader(int id, Bundle args) {
        if (id == 0) {
            return new UserLoader(getContext(), tempEmail);
        } else {
            return new ConfirmCode(getContext(), tempEmail);
        }
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Object> loader, Object data) {
        progress1.setVisibility(View.GONE);
        progress2.setVisibility(View.GONE);
        if (data != null) {
            if (loader.getId() == 0) {
                if (data instanceof Boolean && ((Boolean) data)) {
                    viewFlipper.setDisplayedChild(1);
                    emailVerifyHint.setText(tempEmail.getEmail());
                } else {
                    Toast.makeText(getContext(), R.string.error_send_code, Toast.LENGTH_SHORT).show();
                }
            } else {
                if (data instanceof Integer) {
                    switch ((int) data) {
                        case 1:
                            UtilShaPre.setDefaults("user_email", tempEmail.getEmail(), getContext());
                            user.setEmail(tempEmail.getEmail());
                            Util.backFragmentFunction(this);
                            break;
                        case 2:
                            Toast.makeText(getContext(), R.string.wrong_code, Toast.LENGTH_SHORT).show();
                            break;
                        case 3:
                            Toast.makeText(getContext(), R.string.exp_code, Toast.LENGTH_SHORT).show();
                            break;
                        case 4:
                            Toast.makeText(getContext(), R.string.email_exist, Toast.LENGTH_SHORT).show();
                            break;
                    }
                }
            }
        } else {
            Toast.makeText(getContext(), R.string.error, Toast.LENGTH_SHORT).show();
        }
        btn.setClickable(true);
        verify.setClickable(true);
        loaderManager.destroyLoader(loader.getId());
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Object> loader) {

    }

    private static class UserLoader extends AsyncTaskLoader<Object> {
        TempEmail tempEmail;
        Context ctx;

        UserLoader(Context context, TempEmail tempEmail) {
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
            try {
                String json = ConnectApi.POST(tempEmail, ConnectApi.SEND_CODE_TO_EMAIL, getContext());
                JsonParser parser = new JsonParser();
                return gson.fromJson(parser.parse(json).getAsJsonPrimitive(), Boolean.class);
            } catch (Exception e) {
                FirebaseCrashlytics.getInstance().log(UtilShaPre.getDefaultsString(AppConstants.USER_ID, getContext()));
                FirebaseCrashlytics.getInstance().recordException(e);
                return null;
            }
        }
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
            String json = ConnectApi.PATCH(tempEmail, ConnectApi.VERIFY_CODE_TO_CHANGE_EMAIL, getContext());
            JsonParser parser = new JsonParser();
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