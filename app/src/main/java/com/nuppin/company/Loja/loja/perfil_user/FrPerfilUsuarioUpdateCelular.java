package com.nuppin.company.Loja.loja.perfil_user;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import com.google.android.material.button.MaterialButton;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.gson.Gson;
import com.google.gson.JsonParser;
import com.nuppin.company.R;
import com.nuppin.company.Util.AppConstants;
import com.nuppin.company.Util.Util;
import com.nuppin.company.Util.UtilShaPre;
import com.nuppin.company.connection.ConnectApi;
import com.nuppin.company.model.TempSms;
import com.nuppin.company.model.User;

public class FrPerfilUsuarioUpdateCelular extends Fragment implements
        LoaderManager.LoaderCallbacks<Object> {
    private static final String USER = "user";
    private EditText numero, codigo, ccode;
    private CardView progress1, progress2;
    private MaterialButton btn, btnVerify;
    private LoaderManager loaderManager;
    private ViewFlipper viewFlipper;
    private TempSms tempSms;
    private User user;
    private TextView celularVerifyHint;

    public FrPerfilUsuarioUpdateCelular() {
    }

    public static FrPerfilUsuarioUpdateCelular newInstance(User user) {
        FrPerfilUsuarioUpdateCelular fragment = new FrPerfilUsuarioUpdateCelular();
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
        View view = inflater.inflate(R.layout.update_cellphone, container, false);
        tempSms = new TempSms();

        Toolbar toolbar = view.findViewById(R.id.toolbar_top);
        Util.setaToolbar(this, 0, toolbar, getActivity(), true, R.drawable.ic_clear_white_24dp);

        viewFlipper = view.findViewById(R.id.viewFlipper);

        viewFlipper.setDisplayedChild(0);


        progress1 = view.findViewById(R.id.progress1);
        progress2 = view.findViewById(R.id.progress2);
        celularVerifyHint = view.findViewById(R.id.txtSubCelular);
        numero = view.findViewById(R.id.edtCelular);
        codigo = view.findViewById(R.id.codigo);
        ccode = view.findViewById(R.id.ccode);
        btn = view.findViewById(R.id.btnSendSMS);
        btnVerify = view.findViewById(R.id.btnVerify);

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
                //btn.setClickable(false);
                //progress.setVisibility(View.VISIBLE);
                if (!Util.validarTelefone(numero.getText().toString())) {
                    Toast.makeText(getContext(), R.string.invalid_numero, Toast.LENGTH_SHORT).show();
                    return;
                }
                if (user.getPhone_number() != null && user.getPhone_number().equals(ccode.getText().toString() + numero.getText().toString())) {
                    Toast.makeText(getContext(), "Esse já é o seu numero", Toast.LENGTH_SHORT).show();
                    return;
                }
                btn.setClickable(false);
                progress1.setVisibility(View.VISIBLE);
                tempSms.setCelular(ccode.getText().toString() + numero.getText().toString());
                loaderManager.restartLoader(0, null, FrPerfilUsuarioUpdateCelular.this);
            }
        });

        btnVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (codigo.getText().toString().equals("")) {
                    Toast.makeText(getContext(), R.string.invalid_code, Toast.LENGTH_SHORT).show();
                    return;
                }
                btnVerify.setClickable(false);
                tempSms.setUser_id(UtilShaPre.getDefaultsString(AppConstants.USER_ID, getContext()));
                tempSms.setCode(codigo.getText().toString());
                progress2.setVisibility(View.VISIBLE);
                loaderManager.restartLoader(1, null, FrPerfilUsuarioUpdateCelular.this);
            }
        });

        return view;
    }

    @NonNull
    @Override
    public Loader<Object> onCreateLoader(int id, Bundle args) {
        if (id == 0) {
            return new SendSms(getContext(), tempSms);
        } else {
            return new VerifyCode(getContext(), tempSms);
        }
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Object> loader, Object data) {
        progress2.setVisibility(View.GONE);
        progress1.setVisibility(View.GONE);
        if (data != null) {
            if (loader.getId() == 0) {
                if (data instanceof Boolean && ((Boolean) data)) {
                    Toast.makeText(getContext(), R.string.code_sent, Toast.LENGTH_SHORT).show();
                    viewFlipper.setDisplayedChild(1);
                    celularVerifyHint.setText(tempSms.getCelular());
                } else {
                    Toast.makeText(getContext(), R.string.error_send_code, Toast.LENGTH_SHORT).show();
                }
            } else if (loader.getId() == 1) {
                if (data instanceof Integer) {
                    switch ((int) data) {
                        case 1:
                            user.setPhone_number(tempSms.getCelular());
                            Util.backFragmentFunction(this);
                            break;
                        case 2:
                            Toast.makeText(getContext(), R.string.wrong_code, Toast.LENGTH_SHORT).show();
                            break;
                        case 3:
                            Toast.makeText(getContext(), R.string.exp_code, Toast.LENGTH_SHORT).show();
                            break;
                        case 4:
                            Toast.makeText(getContext(), R.string.phone_number_exist, Toast.LENGTH_SHORT).show();
                            break;
                    }
                }
            }
        } else {
            Toast.makeText(getContext(), R.string.error, Toast.LENGTH_SHORT).show();
        }
        btn.setClickable(true);
        btnVerify.setClickable(true);
        loaderManager.destroyLoader(loader.getId());
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Object> loader) {

    }

    private static class VerifyCode extends AsyncTaskLoader<Object> {
        TempSms tempSms;
        Context ctx;

        VerifyCode(Context context, TempSms tempSms) {
            super(context);
            this.tempSms = tempSms;
            ctx = context;
        }

        @Override
        protected void onStartLoading() {
            forceLoad();
        }


        @Override
        public Object loadInBackground() {
            Gson gson = new Gson();
            String json = ConnectApi.PATCH(tempSms, ConnectApi.VERIFY_CODE_TO_CHANGE_CELLPHONE, getContext());
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

    private static class SendSms extends AsyncTaskLoader<Object> {
        TempSms tempSms;
        Context ctx;

        SendSms(Context context, TempSms tempSms) {
            super(context);
            this.tempSms = tempSms;
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
                String json = ConnectApi.POST(tempSms, ConnectApi.SEND_CODE_TO_SMS, getContext());
                JsonParser parser = new JsonParser();
                return gson.fromJson(parser.parse(json).getAsJsonPrimitive(), Boolean.class);
            } catch (Exception e) {
                FirebaseCrashlytics.getInstance().log(UtilShaPre.getDefaultsString(AppConstants.USER_ID, getContext()));
                FirebaseCrashlytics.getInstance().recordException(e);
                return null;
            }
        }
    }
}
