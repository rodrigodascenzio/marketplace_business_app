package com.nuppin.company.Loja.loja.perfil;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ShareCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.AsyncTaskLoader;
import androidx.loader.content.Loader;

import com.airbnb.lottie.LottieAnimationView;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.gson.Gson;
import com.google.gson.JsonParser;
import com.nuppin.company.Loja.dialogs.InfoDialogFragment;
import com.nuppin.company.Loja.dialogs.PreviewBannerPhotoDialogFragment;
import com.nuppin.company.Util.AppConstants;
import com.nuppin.company.Util.ImageCompress;
import com.nuppin.company.Util.RealPathUtil;
import com.nuppin.company.connection.ConnectApi;
import com.nuppin.company.model.Company;
import com.nuppin.company.model.Employee;
import com.nuppin.company.model.Plan;
import com.nuppin.company.R;
import com.nuppin.company.Util.Util;
import com.nuppin.company.Util.UtilShaPre;
import com.nuppin.company.model.User;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import me.toptas.fancyshowcase.FancyShowCaseView;
import me.toptas.fancyshowcase.listener.DismissListener;

import static android.app.Activity.RESULT_OK;

public class MainFr extends Fragment
        implements LoaderManager.LoaderCallbacks<Map<String, Object>>,
        InfoDialogFragment.InfoDialogListener,
        PreviewBannerPhotoDialogFragment.PreviewDialogListener {

    private LoaderManager loaderManager;
    private TextView descricao, nome, categoria, config, feedback, support, txtEnd, endereco, site, insta, face;
    private SimpleDraweeView foto, fotoPerfil, banner;
    private TextView btnEditar, taxa, price, nomePlano, txtMode;
    private Company company;
    private Employee employee;
    private LottieAnimationView dots;
    private ScrollView scrollViewEmpty;
    private ScrollView scrollView;
    private ConstraintLayout errorLayout;
    private FloatingActionButton fabError, fabBanner;
    private ConstraintLayout contEnd;
    private Map<String, Object> data;
    private MaterialButton share;
    private static final int REQUEST_FOTO = 1;
    private static final int FOTO_COMPRESS_OTHER_THREAD = 99;
    private String mfotoPath;
    private byte[] imageCompressed;
    private CardView progress, cardEmployee;


    private FancyShowCaseView fancyShowCaseView1;
    private DismissListener listenerTutorial = new DismissListener() {
        @Override
        public void onDismiss(String s) {
            chainTutorial();
        }

        @Override
        public void onSkipped(String s) {

        }
    };

    private void chainCreatedTutorial() {
        try {
            if (fancyShowCaseView1 == null) {
                fancyShowCaseView1 = new FancyShowCaseView.Builder(requireActivity())
                        .focusOn(fotoPerfil)
                        .delay(500)
                        .title(getString(R.string.unique_tutorial_company_detalhe_perfil_usuario_string))
                        .showOnce(getString(R.string.unique_tutorial_company_detalhe_perfil_usuario))
                        .backgroundColor(getResources().getColor(R.color.primary_light))
                        .enableAutoTextPosition()
                        .closeOnTouch(true)
                        .dismissListener(listenerTutorial)
                        .build();
            }
        } catch (Exception e) {
            return;
        }
    }

    private void chainTutorial() {
        try {
            if (!fancyShowCaseView1.isShownBefore()) {
                fancyShowCaseView1.show();
                return;
            }
        } catch (Exception e) {
            return;
        }
    }

    public MainFr() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loaderManager = LoaderManager.getInstance(this);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable final ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fr_main, container, false);

        progress = view.findViewById(R.id.progress);
        dots = view.findViewById(R.id.dots);
        scrollViewEmpty = view.findViewById(R.id.scrollViewEmpty);
        errorLayout = view.findViewById(R.id.error_layout);
        fabError = view.findViewById(R.id.fabError);
        fabError.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dots.setVisibility(View.VISIBLE);
                errorLayout.setVisibility(View.GONE);
                loaderManager.restartLoader(0, null, MainFr.this);
            }
        });

        scrollView = view.findViewById(R.id.scrollView);
        taxa = view.findViewById(R.id.taxa);
        price = view.findViewById(R.id.price);
        nomePlano = view.findViewById(R.id.txtInfo);
        share = view.findViewById(R.id.share);
        fabBanner = view.findViewById(R.id.fab);
        banner = view.findViewById(R.id.imageBanner);
        cardEmployee = view.findViewById(R.id.cardEmployee);
        txtMode = view.findViewById(R.id.txtMode);

        fabBanner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ActivityCompat.checkSelfPermission(
                        Objects.requireNonNull(getContext()), Manifest.permission.READ_EXTERNAL_STORAGE) ==
                        PackageManager.PERMISSION_GRANTED) {
                    selecionarFoto();
                } else {
                    requestPermissions(
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
                }
            }
        });

        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShareCompat.IntentBuilder.from(requireActivity())
                        .setType("text/plain")
                        .setChooserTitle("Compartilhar no..")
                        .setText(getString(R.string.share_nuppin))
                        .startChooser();
            }
        });

        Toolbar toolbar = view.findViewById(R.id.toolbar_top);
        Util.setaToolbar(this, R.string.perfil_toolbar, toolbar, getActivity(), false, 0);

        descricao = view.findViewById(R.id.descricao);
        nome = view.findViewById(R.id.nome);
        categoria = view.findViewById(R.id.categoria);
        btnEditar = view.findViewById(R.id.btnEditar);
        foto = view.findViewById(R.id.foto);
        config = view.findViewById(R.id.config);
        feedback = view.findViewById(R.id.feedback);
        support = view.findViewById(R.id.support);
        final TextView txtDesc = view.findViewById(R.id.txtDescricao);
        txtEnd = view.findViewById(R.id.txtEndereco);
        final TextView txtRedes = view.findViewById(R.id.txtRedes);
        final ConstraintLayout contDesc = view.findViewById(R.id.containerDescricao);
        contEnd = view.findViewById(R.id.containerEndereco);
        final LinearLayout contRedes = view.findViewById(R.id.containerRedes);
        endereco = view.findViewById(R.id.endereco);
        insta = view.findViewById(R.id.instagram);
        site = view.findViewById(R.id.site);
        face = view.findViewById(R.id.facebook);
        fotoPerfil = view.findViewById(R.id.fotoPerfil);
        Util.hasPhoto(UtilShaPre.getDefaultsString("user_photo", getContext()), fotoPerfil);


        txtDesc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (contDesc.getVisibility() == View.GONE) {
                    txtDesc.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.ic_keyboard_arrow_up_black_24dp), null);
                    contDesc.setVisibility(View.VISIBLE);
                } else {
                    txtDesc.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.ic_keyboard_arrow_down_black_24dp), null);
                    contDesc.setVisibility(View.GONE);
                }
            }
        });

        txtEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (contEnd.getVisibility() == View.GONE) {
                    txtEnd.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.ic_keyboard_arrow_up_black_24dp), null);
                    contEnd.setVisibility(View.VISIBLE);
                } else {
                    txtEnd.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.ic_keyboard_arrow_down_black_24dp), null);
                    contEnd.setVisibility(View.GONE);
                }
            }
        });

        txtRedes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (contRedes.getVisibility() == View.GONE) {
                    txtRedes.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.ic_keyboard_arrow_up_black_24dp), null);
                    contRedes.setVisibility(View.VISIBLE);
                } else {
                    txtRedes.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.ic_keyboard_arrow_down_black_24dp), null);
                    contRedes.setVisibility(View.GONE);
                }
            }
        });

        cardEmployee.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getActivity() instanceof ToActivity) {
                    ToActivity listener = (ToActivity) getActivity();
                        UtilShaPre.setDefaults("mode", "employee", getContext());
                        listener.EmployeeMode();
                }
            }
        });

        fotoPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getActivity() instanceof ToActivity) {
                    ToActivity listener = (ToActivity) getActivity();
                    listener.MainFrBottom(fotoPerfil.getId(), null);
                }
            }
        });

        btnEditar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getActivity() instanceof ToActivity) {
                    ToActivity listener = (ToActivity) getActivity();
                    listener.MainFrBottom(btnEditar.getId(), company);
                }
            }
        });

        feedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getActivity() instanceof ToActivity) {
                    ToActivity listener = (ToActivity) getActivity();
                    listener.MainFrBottom(feedback.getId(), company);
                }
            }
        });

        support.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    String url = "https://wa.me/" + "5519989831145";
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

        config.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getActivity() instanceof ToActivity) {
                    ToActivity listener = (ToActivity) getActivity();
                    listener.MainFrBottom(config.getId(), company);
                }
            }
        });

        final MaterialButton btn = view.findViewById(R.id.btnCriar);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!UtilShaPre.getDefaultsString("user_email", getContext()).equals("")) {
                    if (getActivity() instanceof ToActivity) {
                        ToActivity listener = (ToActivity) getActivity();
                        listener.MainFrBottom(btn.getId(), null);
                    }
                } else {
                    DialogFragment dialogFrag = InfoDialogFragment.newInstance("É necessario um email para começar", "Cadastrar email", "Agora não");
                    dialogFrag.show(MainFr.this.getChildFragmentManager(), "email_dialog");
                }
            }
        });

        loaderManager.restartLoader(0, null, this);

        chainCreatedTutorial();

        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
        loaderManager.destroyLoader(0);
    }

    @NonNull
    @Override
    public Loader<Map<String, Object>> onCreateLoader(int id, Bundle args) {
        if (id == 0) {
            return new StoresLoader(getActivity(), data);
        } else if (id == FOTO_COMPRESS_OTHER_THREAD) {
            return new CompressImageThread(getContext(), mfotoPath);
        } else {
            return new SendPhoto(getContext(), company, mfotoPath, imageCompressed);
        }
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Map<String, Object>> loader, Map<String, Object> data) {
        dots.setVisibility(View.GONE);
        progress.setVisibility(View.GONE);

        if (data != null) {
            if (loader.getId() == 0) {
                this.data = data;
                if (data.get(AppConstants.EMPLOYEE) != null && data.get(AppConstants.EMPLOYEE) instanceof Employee) {
                    employee = (Employee) data.get(AppConstants.EMPLOYEE);
                    cardEmployee.setVisibility(View.VISIBLE);
                }

                if (data.get(AppConstants.COMPANY) != null && data.get(AppConstants.COMPANY) instanceof Company) {
                    company = (Company) data.get(AppConstants.COMPANY);

                    nome.setText(company.getName());
                    categoria.setText(company.getCategory_name() + " • " + company.getSubcategory_name());
                    if (company.getDescription() != null && !company.getDescription().equals("")) {
                        descricao.setText(company.getDescription());
                    }
                    endereco.setText(company.getFull_address());
                    site.setText(company.getSite());
                    face.setText(company.getFacebook());
                    insta.setText(company.getInstagram());

                    if (site.getText().toString().isEmpty()) {
                        site.setText(getString(R.string.nao_cadastrado));
                    }
                    if (insta.getText().toString().isEmpty()) {
                        insta.setText(getString(R.string.nao_cadastrado));
                    }
                    if (face.getText().toString().isEmpty()) {
                        face.setText(getString(R.string.nao_cadastrado));
                    }

                    Util.hasPhoto(company, foto);
                    Util.hasPhoto(company, banner);
                    scrollView.setVisibility(View.VISIBLE);
                    scrollViewEmpty.setVisibility(View.GONE);

                    if (data.get(AppConstants.PLAN) != null && data.get(AppConstants.PLAN) instanceof Plan) {
                        Plan plan = (Plan) data.get(AppConstants.PLAN);
                        nomePlano.setText(plan.getName());
                    }
                } else {
                    scrollView.setVisibility(View.GONE);
                    scrollViewEmpty.setVisibility(View.VISIBLE);
                }
                errorLayout.setVisibility(View.GONE);
            } else if (loader.getId() == FOTO_COMPRESS_OTHER_THREAD) {
                if (data.get("compress") != null && data.get("compress") instanceof byte[]) {
                    imageCompressed = (byte[]) data.get("compress");
                    progress.setVisibility(View.VISIBLE);
                    loaderManager.restartLoader(2, null, this);
                } else {
                    Toast.makeText(getContext(), R.string.erro_compress_photo, Toast.LENGTH_SHORT).show();
                }
            } else if (loader.getId() == 2) {
                if (data.get("photo") instanceof String) {
                    loaderManager.restartLoader(0, null, this);
                    Toast.makeText(getContext(), R.string.upload_photo_success, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), R.string.error_photo, Toast.LENGTH_SHORT).show();
                }
            }
        } else {
            errorLayout.setVisibility(View.VISIBLE);
            scrollView.setVisibility(View.GONE);
        }

        if (loader.getId() != 0) {
            loaderManager.destroyLoader(loader.getId());
        }
        chainTutorial();
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Map<String, Object>> loader) {
    }

    @Override
    public void onDialogOKClick(View view, InfoDialogFragment info) {
        switch (view.getId()) {
            case R.id.btnPositive:
                info.dismiss();
                User user = new User();
                user.setUser_id(UtilShaPre.getDefaultsString(AppConstants.USER_ID, getContext()));
                user.setEmail(UtilShaPre.getDefaultsString("user_email", getContext()));
                user.setNome(UtilShaPre.getDefaultsString("user_nome", getContext()));
                if (getActivity() instanceof ToActivity) {
                    ToActivity listener = (ToActivity) getActivity();
                    listener.MainFrBottom(user);
                }
                break;
            case R.id.btnNegative:
                info.dismiss();
                break;
        }
    }

    @Override
    public void onDialogOKClick(View view, Uri uri, PreviewBannerPhotoDialogFragment preview) {
        switch (view.getId()) {
            case R.id.btnPositive:
                preview.dismiss();
                loaderManager.restartLoader(FOTO_COMPRESS_OTHER_THREAD, null, this);
                break;
            case R.id.btnNegative:
                preview.dismiss();
                break;
        }
    }

    private static class StoresLoader extends AsyncTaskLoader<Map<String, Object>> {

        Context ctx;
        Map<String, Object> data;

        StoresLoader(Context context, Map<String, Object> data) {
            super(context);
            ctx = context;
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
        public Map<String, Object> loadInBackground() {
            Gson gson = new Gson();
            Company company;
            Employee employee;
            Plan plan;
            Map<String, Object> mapOrdPro;
            mapOrdPro = new HashMap<>();
            JsonParser parser = new JsonParser();
            String json = ConnectApi.GET(ConnectApi.COMPANY_MAIN + "/" + UtilShaPre.getDefaultsString(AppConstants.USER_ID, ctx), getContext());

            try {
                company = gson.fromJson(parser.parse(json).getAsJsonObject().getAsJsonObject(AppConstants.COMPANY), Company.class);
                mapOrdPro.put(AppConstants.COMPANY, company);
                try {
                    plan = gson.fromJson(parser.parse(json).getAsJsonObject().getAsJsonObject(AppConstants.PLAN), Plan.class);
                    mapOrdPro.put(AppConstants.PLAN, plan);
                } catch (Exception e) {
                    FirebaseCrashlytics.getInstance().log(UtilShaPre.getDefaultsString(AppConstants.USER_ID, getContext()));
                    FirebaseCrashlytics.getInstance().recordException(e);
                    //nothing
                }
                try {
                    employee = gson.fromJson(parser.parse(json).getAsJsonObject().getAsJsonObject(AppConstants.EMPLOYEE), Employee.class);
                    mapOrdPro.put(AppConstants.EMPLOYEE, employee);
                } catch (Exception e1) {
                    FirebaseCrashlytics.getInstance().log(UtilShaPre.getDefaultsString(AppConstants.USER_ID, getContext()));
                    FirebaseCrashlytics.getInstance().recordException(e1);
                    //nothing
                }
            } catch (Exception e) {
                FirebaseCrashlytics.getInstance().log(UtilShaPre.getDefaultsString(AppConstants.USER_ID, getContext()));
                FirebaseCrashlytics.getInstance().recordException(e);
                try {
                    gson.fromJson(parser.parse(json).getAsJsonObject().getAsJsonPrimitive(AppConstants.COMPANY), Boolean.class);
                    mapOrdPro.put(AppConstants.COMPANY, null);
                    try {
                        employee = gson.fromJson(parser.parse(json).getAsJsonObject().getAsJsonObject(AppConstants.EMPLOYEE), Employee.class);
                        mapOrdPro.put(AppConstants.EMPLOYEE, employee);
                    } catch (Exception e1) {
                        FirebaseCrashlytics.getInstance().log(UtilShaPre.getDefaultsString(AppConstants.USER_ID, getContext()));
                        FirebaseCrashlytics.getInstance().recordException(e1);
                        //nothing
                    }
                } catch (Exception e2) {
                    FirebaseCrashlytics.getInstance().log(UtilShaPre.getDefaultsString(AppConstants.USER_ID, getContext()));
                    FirebaseCrashlytics.getInstance().recordException(e2);
                    return null;
                }
            }

            return mapOrdPro;
        }
    }


    //RETORNO DO ARQUIVO ESCOLHIDO NA GALERIA
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == REQUEST_FOTO) {
            mfotoPath = RealPathUtil.getRealPath(getContext(), data.getData());
            DialogFragment dialogFrag = PreviewBannerPhotoDialogFragment.newInstance(data.getData(), getString(R.string.confirmar), getString(R.string.cancelar));
            dialogFrag.show(this.getChildFragmentManager(), "preview_dialog");
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


    private static class SendPhoto extends AsyncTaskLoader<Map<String, Object>> {

        Company company;
        Context ctx;
        String fotoPath;
        byte[] imageocompressed;


        SendPhoto(Context context, Company company, String mFotoPath, byte[] imageCompressed) {
            super(context);
            ctx = context;
            this.company = company;
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
                String ok = gson.fromJson(ConnectApi.enviarFoto(getContext(), fotoPath, company.getCompany_id(), "company_banner", imageocompressed), String.class);
                mapOrdPro.put("photo", ok);
            } catch (Exception e) {
                FirebaseCrashlytics.getInstance().log(UtilShaPre.getDefaultsString(AppConstants.USER_ID, getContext()));
                FirebaseCrashlytics.getInstance().recordException(e);
                mapOrdPro.put("photo", null);
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

    public interface ToActivity {
        void MainFrBottom(int index, Company company);
        void EmployeeMode();
        void MainFrBottom(User user);
    }
}