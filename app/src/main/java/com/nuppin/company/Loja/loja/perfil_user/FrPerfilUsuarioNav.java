package com.nuppin.company.Loja.loja.perfil_user;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.AsyncTaskLoader;
import androidx.loader.content.Loader;

import com.airbnb.lottie.LottieAnimationView;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.nuppin.company.Loja.Config.ConfigLoja;
import com.nuppin.company.Loja.dialogs.InfoDialogFragment;
import com.nuppin.company.Util.AppConstants;
import com.nuppin.company.connection.ConnectApi;
import com.nuppin.company.model.User;
import com.nuppin.company.R;
import com.nuppin.company.Util.Util;
import com.nuppin.company.Util.UtilShaPre;

public class FrPerfilUsuarioNav extends Fragment implements
        LoaderManager.LoaderCallbacks<Object>,
        InfoDialogFragment.InfoDialogListener {

    private static final String USER_ID = "USER";
    private User user;
    private String userId;
    private LoaderManager loaderManager;
    private TextView nome, cpf, celular, email;
    private SimpleDraweeView foto;
    private Button btn;
    private CardView progress;
    private LottieAnimationView dots;
    private ScrollView scrollView;
    private Object data;
    private MaterialButton feedback, logout;

    public FrPerfilUsuarioNav() {
    }

    public static FrPerfilUsuarioNav newInstance(String id) {
        FrPerfilUsuarioNav fragment = new FrPerfilUsuarioNav();
        Bundle args = new Bundle();
        args.putString(USER_ID, id);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null && getArguments().containsKey(USER_ID)) {
            userId = getArguments().getString(USER_ID);
        }
        loaderManager = LoaderManager.getInstance(this);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fr_user_perfil, container, false);

        Toolbar toolbar = view.findViewById(R.id.toolbar_top);
        Util.setaToolbar(this, R.string.perfil_toolbar, toolbar, getActivity(), false, 0);

        dots = view.findViewById(R.id.dots);
        progress = view.findViewById(R.id.progress);
        scrollView = view.findViewById(R.id.scrollView);
        logout = view.findViewById(R.id.logout);
        nome = view.findViewById(R.id.nome);
        cpf = view.findViewById(R.id.cpf);
        celular = view.findViewById(R.id.cel);
        email = view.findViewById(R.id.email);
        foto = view.findViewById(R.id.imageCadastro);
        btn = view.findViewById(R.id.btnEditar);
        feedback = view.findViewById(R.id.feedback);

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment dialogFrag = InfoDialogFragment.newInstance("Deseja mesmo sair?", getString(R.string.confirmar), getString(R.string.revisar));
                dialogFrag.show(FrPerfilUsuarioNav.this.getChildFragmentManager(), "logout");

            }
        });

        feedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getActivity() instanceof ToActivity) {
                    ToActivity listener = (ToActivity) getActivity();
                    listener.feedback();
                }
            }
        });

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getActivity() instanceof ToActivity) {
                    ToActivity listener = (ToActivity) getActivity();
                    listener.editarUsuario(user);
                }
            }
        });

        ImageView chat = view.findViewById(R.id.whatsIcon);
        chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    String url = "https://wa.me/"+"5519989831145";
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(url));
                    startActivity(i);
                } catch (Exception e) {
                    FirebaseCrashlytics.getInstance().log(UtilShaPre.getDefaultsString(AppConstants.USER_ID, getContext()));
                    FirebaseCrashlytics.getInstance().recordException(e);
                    Toast.makeText(getContext(), R.string.whatsapp_not_found, Toast.LENGTH_SHORT).show();
                }
            }
        });
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
    public Loader<Object> onCreateLoader(int id, Bundle args) {
        if (id == 0) {
            return new UserLoader(getContext(), userId, data);
        }else {
            return new UserLogout(getContext(), user);
        }
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Object> loader, Object data) {
        progress.setVisibility(View.GONE);
        dots.setVisibility(View.GONE);

        if (data != null) {
            if (loader.getId() == 0) {
                if (data instanceof User) {
                    this.data = data;
                    user = (User) data;
                    nome.setText(user.getNome());
                    email.setText(user.getEmail());
                    cpf.setText(user.getDocument_number());
                    celular.setText(user.getPhone_number());
                    Util.hasPhoto(user, foto);
                    UtilShaPre.setDefaults("user_photo", user.getphoto(), getContext());
                } else {
                    Toast.makeText(getContext(), R.string.error, Toast.LENGTH_SHORT).show();
                }
            } else if (loader.getId() == 1) {
                if (data instanceof Boolean && (Boolean) data) {
                    ConnectApi.loggout(getContext());
                }
                loaderManager.destroyLoader(loader.getId());
            }
            scrollView.setVisibility(View.VISIBLE);
        } else {
            Toast.makeText(getContext(), R.string.error, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Object> loader) {

    }

    @Override
    public void onDialogOKClick(View view, InfoDialogFragment infoDialogFragment) {
        switch (view.getId()) {
            case R.id.btnPositive:
                loaderManager.restartLoader(1, null, this);
                break;
            case R.id.btnNegative:
                infoDialogFragment.dismiss();
                break;
        }
    }


    private static class UserLoader extends AsyncTaskLoader<Object> {
        Context ctx;
        String userId;
        Object data;

        UserLoader(Context context, String userId, Object data) {
            super(context);
            ctx = context;
            this.userId = userId;
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
        public Object loadInBackground() {
            Gson gson = new Gson();

            try {
                String stringJson = ConnectApi.GET(ConnectApi.USERS + "/" + userId, getContext());
                JsonParser parser = new JsonParser();
                JsonObject jObj = parser.parse(stringJson).getAsJsonObject();
                return gson.fromJson(jObj, User.class);
            } catch (Exception e) {
                FirebaseCrashlytics.getInstance().log(UtilShaPre.getDefaultsString(AppConstants.USER_ID, getContext()));
                FirebaseCrashlytics.getInstance().recordException(e);
                return null;
            }
        }
    }

    private static class UserLogout extends AsyncTaskLoader<Object> {
        Context ctx;
        User user;

        UserLogout(Context context, User user) {
            super(context);
            ctx = context;
            this.user = user;
        }


        @Override
        protected void onStartLoading() {
            forceLoad();
        }


        @Override
        public Boolean loadInBackground() {
            Gson gson = new Gson();
            JsonParser parser = new JsonParser();

            try {
                user.setRefresh_token(UtilShaPre.getDefaultsString("refresh_token",getContext()));
                String stringJson = ConnectApi.DELETE(user,ConnectApi.USERS_LOGOUT, getContext());
                return gson.fromJson(parser.parse(stringJson).getAsJsonPrimitive(), Boolean.class);
            } catch (Exception e) {
                FirebaseCrashlytics.getInstance().log(UtilShaPre.getDefaultsString(AppConstants.USER_ID, getContext()));
                FirebaseCrashlytics.getInstance().recordException(e);
                return null;
            }
        }
    }

    public interface ToActivity {
        void editarUsuario(User user);
        void feedback();
    }
}