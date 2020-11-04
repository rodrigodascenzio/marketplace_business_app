package com.nuppin.company.Loja.loja.Employee;

import android.Manifest;
import android.app.Activity;
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
import com.nuppin.company.Loja.Equipe.FuncionarioDetalhe;
import com.nuppin.company.Loja.dialogs.InfoDialogFragment;
import com.nuppin.company.Loja.dialogs.PreviewBannerPhotoDialogFragment;
import com.nuppin.company.Loja.loja.perfil.MainFr;
import com.nuppin.company.R;
import com.nuppin.company.Util.AppConstants;
import com.nuppin.company.Util.ImageCompress;
import com.nuppin.company.Util.RealPathUtil;
import com.nuppin.company.Util.Util;
import com.nuppin.company.Util.UtilShaPre;
import com.nuppin.company.connection.ConnectApi;
import com.nuppin.company.model.Company;
import com.nuppin.company.model.Employee;
import com.nuppin.company.model.Plan;
import com.nuppin.company.model.User;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static android.app.Activity.RESULT_OK;

public class MainFrEmployee extends Fragment
        implements LoaderManager.LoaderCallbacks<Object>,
        InfoDialogFragment.InfoDialogListener {

    private static final String COMPANY = "company";
    private TextView descricao, nome, categoria, config, feedback, support, txtEnd, endereco, site, insta, face;
    private SimpleDraweeView foto, fotoPerfil, banner;
    private Company company;
    private ConstraintLayout contEnd;
    private MaterialButton share;
    private CardView cardEmployee;
    private LoaderManager loaderManager;
    private Employee employee;

    public MainFrEmployee() {
    }

    public static MainFrEmployee newInstance(Company company) {
        MainFrEmployee fragment = new MainFrEmployee();
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
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable final ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fr_main_employee, container, false);

        share = view.findViewById(R.id.share);
        banner = view.findViewById(R.id.imageBanner);
        cardEmployee = view.findViewById(R.id.cardEmployee);

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
                        UtilShaPre.setDefaults("mode", "company", getContext());
                        listener.CompanyMode();
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
                DialogFragment dialogFrag = InfoDialogFragment.newInstance("Certeza que deseja deixar de fazer parte da equipe?", getString(R.string.confirmar), getString(R.string.revisar));
                dialogFrag.show(MainFrEmployee.this.getChildFragmentManager(), "info");
            }
        });

        if (company != null) {

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

        }
        loaderManager.restartLoader(0, null, this);
        return view;
    }

    @Override
    public void onDialogOKClick(View view, InfoDialogFragment info) {
        switch (view.getId()) {
            case R.id.btnPositive:
                loaderManager.restartLoader(1, null, MainFrEmployee.this);
                config.setClickable(false);
                info.dismiss();
                break;
            case R.id.btnNegative:
                info.dismiss();
                break;
        }
    }

    @NonNull
    @Override
    public Loader<Object> onCreateLoader(int id, @Nullable Bundle args) {
        if (id == 0) {
            return new EmployeeLoader(getActivity());
        }else {
            return new RemoveFuncionario(getActivity(), employee);
        }
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Object> loader, Object data) {
        if (data != null) {
            if (loader.getId() == 0) {
                if (data instanceof Employee) {
                    employee = (Employee) data;
                }
            } else {
                if (data instanceof Integer && (Integer)data > 0) {
                    if (getActivity() instanceof ToActivity) {
                        ToActivity listener = (ToActivity) getActivity();
                        UtilShaPre.setDefaults("mode", "company", getContext());
                        Toast.makeText(getContext(), "Saiu da equipe", Toast.LENGTH_SHORT).show();
                        loaderManager.destroyLoader(loader.getId());
                        listener.CompanyMode();
                    }
                } else {
                    Toast.makeText(getContext(), R.string.error, Toast.LENGTH_SHORT).show();
                }
            }
        } else {
            Toast.makeText(getContext(), R.string.error, Toast.LENGTH_SHORT).show();
        }
        config.setClickable(true);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Object> loader) {

    }

    private static class EmployeeLoader extends AsyncTaskLoader<Object> {

        Context ctx;
        EmployeeLoader(Context context) {
            super(context);
            ctx = context;
        }


        @Override
        protected void onStartLoading() {
            forceLoad();
        }


        @Override
        public Employee loadInBackground() {
            Gson gson = new Gson();
            Employee employee;
            JsonParser parser = new JsonParser();

            try {
                String json = ConnectApi.GET(ConnectApi.COMPANY_MAIN + "/" + UtilShaPre.getDefaultsString(AppConstants.USER_ID, ctx), getContext());
                return gson.fromJson(parser.parse(json).getAsJsonObject().getAsJsonObject(AppConstants.EMPLOYEE), Employee.class);
            } catch (Exception e) {
                FirebaseCrashlytics.getInstance().log(UtilShaPre.getDefaultsString(AppConstants.USER_ID, getContext()));
                FirebaseCrashlytics.getInstance().recordException(e);
                return null;
            }
        }
    }

    private static class RemoveFuncionario extends AsyncTaskLoader<Object> {

        Activity ctx;
        Employee employee;


        RemoveFuncionario(Activity context, Employee employee) {
            super(context);
            ctx = context;
            this.employee = employee;
        }


        @Override
        protected void onStartLoading() {
            forceLoad();
        }


        @Override
        public Integer loadInBackground() {
            try {
                Gson gson = new Gson();
                return gson.fromJson(ConnectApi.DELETE(employee, ConnectApi.EMPLOYEE, ctx), Integer.class);
            } catch (Exception e) {
                FirebaseCrashlytics.getInstance().log(UtilShaPre.getDefaultsString(AppConstants.USER_ID, getContext()));
                FirebaseCrashlytics.getInstance().recordException(e);
                return null;
            }
        }
    }

    public interface ToActivity {
        void MainFrBottom(int index, Company company);
        void CompanyMode();
    }
}