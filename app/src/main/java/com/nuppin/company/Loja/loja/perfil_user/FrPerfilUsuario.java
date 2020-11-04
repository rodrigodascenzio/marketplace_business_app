package com.nuppin.company.Loja.loja.perfil_user;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.AsyncTaskLoader;
import androidx.loader.content.Loader;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.gson.Gson;
import com.google.gson.JsonParser;
import com.nuppin.company.Loja.dialogs.PreviewRoundPhotoDialogFragment;
import com.nuppin.company.Util.AppConstants;
import com.nuppin.company.connection.ConnectApi;
import com.nuppin.company.model.ErrorCode;
import com.nuppin.company.model.User;
import com.nuppin.company.R;
import com.nuppin.company.Util.CNP;
import com.nuppin.company.Util.ImageCompress;
import com.nuppin.company.Util.MaskEditUtil;
import com.nuppin.company.Util.RealPathUtil;
import com.nuppin.company.Util.Util;
import com.nuppin.company.Util.UtilShaPre;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static android.app.Activity.RESULT_OK;

public class FrPerfilUsuario extends Fragment implements
        LoaderManager.LoaderCallbacks<Map<String, Object>>,
        View.OnClickListener, PreviewRoundPhotoDialogFragment.PreviewDialogListener {

    private static final String USER = "USER";
    private static final int FOTO_COMPRESS_OTHER_THREAD = 99;
    private User user, userUpdate;
    private LoaderManager loaderManager;
    private EditText nome, cpf, celular, email;
    private TextInputLayout nomeWrap, cpfWrap, emailWrap;
    private SimpleDraweeView foto;
    private String mfotoPath;
    private static final int REQUEST_FOTO = 1;
    private Button btn;
    private TextView emailBtn, smsBtn;
    private CardView progress;
    private ScrollView scrollView;
    private byte[] imageCompressed;
    private Uri uri;


    public FrPerfilUsuario() {
    }

    public static FrPerfilUsuario newInstance(User user) {
        FrPerfilUsuario fragment = new FrPerfilUsuario();
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

    TextWatcher lister = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            btn.setVisibility(View.VISIBLE);
        }
    };

    private void hasAltered(){
        nome.addTextChangedListener(lister);
        cpf.addTextChangedListener(lister);
        if (!nome.getText().toString().equals(user.getNome()) && !nome.getText().toString().isEmpty()) {
            btn.setVisibility(View.VISIBLE);
        }
        if (!Util.clearNotNumber(cpf.getText().toString()).equals(user.getDocument_number()) && !cpf.getText().toString().isEmpty()) {
            btn.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.update_user_perfil, container, false);

        Toolbar toolbar = view.findViewById(R.id.toolbar_top);
        Util.setaToolbar(this, 0, toolbar, getActivity(), true, R.drawable.ic_clear_white_24dp);

        progress = view.findViewById(R.id.progress);
        scrollView = view.findViewById(R.id.scrollView);

        nome = view.findViewById(R.id.nome);
        cpf = view.findViewById(R.id.cpf);
        cpf.addTextChangedListener(MaskEditUtil.mask(cpf, MaskEditUtil.FORMAT_CPF));
        celular = view.findViewById(R.id.cel);
        email = view.findViewById(R.id.email);

        nomeWrap = view.findViewById(R.id.nomeWrap);
        emailWrap = view.findViewById(R.id.emailWrap);
        cpfWrap = view.findViewById(R.id.cpfWrap);

        foto = view.findViewById(R.id.imageCadastro);
        foto.setOnClickListener(this);
        btn = view.findViewById(R.id.botao);
        emailBtn = view.findViewById(R.id.emailBtn);

        nome.setText(user.getNome());
        if (user.getDocument_number() != null && !user.getDocument_number().equals("")) {
            cpf.setText(user.getDocument_number());
            cpf.setEnabled(false);
            cpf.setClickable(false);
        }

        email.setEnabled(false);
        email.setClickable(false);
        celular.setEnabled(false);
        celular.setClickable(false);

        Util.hasPhoto(user, foto);
        UtilShaPre.setDefaults("user_photo", user.getphoto(), getContext());

        smsBtn = view.findViewById(R.id.smsBtn);

        smsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getActivity() instanceof ToActivity) {
                    ToActivity listener = (ToActivity) getActivity();
                    listener.updateCelular(user);
                }
            }
        });

        emailBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getActivity() instanceof ToActivity) {
                    ToActivity listener = (ToActivity) getActivity();
                    listener.updateEmail(user);
                }
            }
        });

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!validar()) {
                    return;
                }
                btn.setClickable(false);
                userUpdate = new User();
                userUpdate.setUser_id(user.getId());
                userUpdate.setNome(nome.getText().toString());
                if (!Util.clearNotNumber(cpf.getText().toString()).equals(user.getDocument_number())) {
                    userUpdate.setDocument_number(Util.clearNotNumber(cpf.getText().toString()));
                }
                progress.setVisibility(View.VISIBLE);
                loaderManager.restartLoader(1, null, FrPerfilUsuario.this);
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        hasAltered();
    }

    @Override
    public void onStart() {
        super.onStart();
        email.setText(user.getEmail());
        celular.setText(user.getPhone_number());
    }

    private boolean validar() {
        boolean isGo = true;
        if (nome.getText().toString().isEmpty() || nome.getText().toString().length() < 3) {
            nomeWrap.setErrorEnabled(true);
            isGo = false;
            if (nome.getText().toString().isEmpty()) {
                nomeWrap.setError(getResources().getString(R.string.error_enabled_text));
            } else {
                nomeWrap.setError(getResources().getString(R.string.invalid_name));
            }
        } else {
            nomeWrap.setErrorEnabled(false);
        }
        if (!CNP.isValidCPF(cpf.getText().toString())) {
            cpfWrap.setErrorEnabled(true);
            cpfWrap.setError(getResources().getString(R.string.cpf_invalido));
            isGo = false;
        } else {
            cpfWrap.setErrorEnabled(false);
        }

        return isGo;
    }

    //RETORNO DO ARQUIVO ESCOLHIDO NA GALERIA
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == REQUEST_FOTO) {
            mfotoPath = RealPathUtil.getRealPath(getContext(), data.getData());
            DialogFragment dialogFrag = PreviewRoundPhotoDialogFragment.newInstance(data.getData(), getString(R.string.confirmar), getString(R.string.cancelar));
            dialogFrag.show(this.getChildFragmentManager(), "preview_dialog");
        }
    }

    //AO CLICAR NA IMAGEVIEW
    public void onClick(View view) {
        if (view.getId() == R.id.imageCadastro) {
            if (ActivityCompat.checkSelfPermission(
                    Objects.requireNonNull(getContext()), Manifest.permission.READ_EXTERNAL_STORAGE) ==
                    PackageManager.PERMISSION_GRANTED) {
                selecionarFoto();
            } else {
                requestPermissions(
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
            }
        }
    }

    //SELECIONAR FOTO NA GALERIA
    private void selecionarFoto() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, REQUEST_FOTO);
    }

    //PEDINDO PERMISSÃO
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            selecionarFoto();
        }
    }

    @Override
    public void onDialogOKClick(View view, Uri uri, PreviewRoundPhotoDialogFragment preview) {
        switch (view.getId()) {
            case R.id.btnPositive:
                preview.dismiss();
                this.uri = uri;
                loaderManager.restartLoader(FOTO_COMPRESS_OTHER_THREAD, null, this);
                break;
            case R.id.btnNegative:
                preview.dismiss();
                break;
        }
    }

    @NonNull
    @Override
    public Loader<Map<String, Object>> onCreateLoader(int id, Bundle args) {
        if (id == 1) {
            return new EditarUsuario(getActivity(), userUpdate, mfotoPath, imageCompressed);
        } else if (id == FOTO_COMPRESS_OTHER_THREAD) {
            return new CompressImageThread(getContext(), mfotoPath);
        } else {
            return new SendPhoto(getContext(), user.getId(), mfotoPath, imageCompressed);
        }
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Map<String, Object>> loader, Map<String, Object> data) {
        progress.setVisibility(View.GONE);
        btn.setClickable(true);

        if (data != null) {
            switch (loader.getId()) {
                case 1:
                    if (data.get(AppConstants.USERS) instanceof User) {
                        if (getActivity() != null) {
                            getActivity().onBackPressed();
                        }
                    } else if (data.get(AppConstants.ERROR) instanceof ErrorCode) {
                        ErrorCode e = (ErrorCode) data.get(AppConstants.ERROR);
                        if (e.getError_code().equals("001")) {
                            emailWrap.setErrorEnabled(true);
                            emailWrap.setError(getResources().getString(R.string.email_exist));
                        } else {
                            cpfWrap.setErrorEnabled(true);
                            cpfWrap.setError(getResources().getString(R.string.cpf_exist));
                        }
                    } else {
                        Toast.makeText(getContext(), R.string.upload_error, Toast.LENGTH_SHORT).show();
                    }
                    break;
                case FOTO_COMPRESS_OTHER_THREAD:
                    if (data.get("compress") instanceof byte[]) {
                        imageCompressed = (byte[]) data.get("compress");
                        progress.setVisibility(View.VISIBLE);
                        loaderManager.restartLoader(2, null, this);
                    } else {
                        Toast.makeText(getContext(), R.string.erro_compress_photo, Toast.LENGTH_SHORT).show();
                    }
                    break;
                case 2:
                    if (data.get("photo") instanceof String) {
                            foto.setImageURI(uri);
                            Toast.makeText(getContext(), R.string.upload_photo_success, Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getContext(), R.string.error_photo, Toast.LENGTH_SHORT).show();
                        }
                    break;
            }
            scrollView.setVisibility(View.VISIBLE);
        } else {
            Toast.makeText(getContext(), R.string.error, Toast.LENGTH_SHORT).show();
        }

        loaderManager.destroyLoader(loader.getId());
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Map<String, Object>> loader) {
    }

    private static class EditarUsuario extends AsyncTaskLoader<Map<String, Object>> {

        User user;
        Context ctx;
        String fotoPath;
        byte[] imagemCompressed;


        EditarUsuario(Context context, User user, String mFotoPath, byte[] imagemCompressed) {
            super(context);
            ctx = context;
            this.user = user;
            this.fotoPath = mFotoPath;
            this.imagemCompressed = imagemCompressed;
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
            String json = ConnectApi.PATCH(user, ConnectApi.USERS, getContext());
            try {
                User u = gson.fromJson(parser.parse(json).getAsJsonObject().getAsJsonObject(AppConstants.USERS), User.class);
                mapOrdPro.put(AppConstants.USERS, u);
            } catch (Exception e) {
                FirebaseCrashlytics.getInstance().log(UtilShaPre.getDefaultsString(AppConstants.USER_ID, getContext()));
                FirebaseCrashlytics.getInstance().recordException(e);
                try {
                    ErrorCode b = gson.fromJson(parser.parse(json).getAsJsonObject().getAsJsonObject(AppConstants.ERROR), ErrorCode.class);
                    mapOrdPro.put(AppConstants.ERROR, b);
                } catch (Exception e1) {
                    FirebaseCrashlytics.getInstance().log(UtilShaPre.getDefaultsString(AppConstants.USER_ID, getContext()));
                    FirebaseCrashlytics.getInstance().recordException(e1);
                    return null;
                }
            }
            return mapOrdPro;
        }
    }

    private static class CompressImageThread extends AsyncTaskLoader<Map<String, Object>> {

        Context ctx;
        String fotoPath;


        CompressImageThread(Context context, String mFotoPath) {
            super(context);
            ctx = context;
            this.fotoPath = mFotoPath;
        }

        @Override
        protected void onStartLoading() {
            forceLoad();
        }

        @Override
        public Map<String, Object> loadInBackground() {

            Map<String, Object> mapOrdPro;
            mapOrdPro = new HashMap<>();

            try {
                mapOrdPro.put("compress", ImageCompress.compressImage(fotoPath));
            } catch (Exception e) {
                FirebaseCrashlytics.getInstance().log(UtilShaPre.getDefaultsString(AppConstants.USER_ID, getContext()));
                FirebaseCrashlytics.getInstance().recordException(e);
                mapOrdPro.put("compress", null);
            }
            return mapOrdPro;
        }
    }

    private static class SendPhoto extends AsyncTaskLoader<Map<String, Object>> {

        Context ctx;
        String fotoPath, userId;
        byte[] imageocompressed;


        SendPhoto(Context context, String userId, String mFotoPath, byte[] imageCompressed) {
            super(context);
            ctx = context;
            this.userId = userId;
            this.fotoPath = mFotoPath;
            this.imageocompressed = imageCompressed;
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
                String ok = gson.fromJson(ConnectApi.enviarFoto(getContext(), fotoPath, userId, AppConstants.USERS, imageocompressed), String.class);
                mapOrdPro.put("photo", ok);
            } catch (Exception e) {
                FirebaseCrashlytics.getInstance().log(UtilShaPre.getDefaultsString(AppConstants.USER_ID, getContext()));
                FirebaseCrashlytics.getInstance().recordException(e);
                mapOrdPro.put("photo", null);
            }

            return mapOrdPro;

        }
    }

    public interface ToActivity {
        void updateEmail(User user);
        void updateCelular(User user);
    }
}