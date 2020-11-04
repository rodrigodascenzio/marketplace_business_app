package com.nuppin.company.Loja.Config;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.AsyncTaskLoader;
import androidx.loader.content.Loader;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.gson.Gson;
import com.google.gson.JsonParser;
import com.nuppin.company.Loja.dialogs.InfoDialogFragment;
import com.nuppin.company.Util.AppConstants;
import com.nuppin.company.Util.UtilShaPre;
import com.nuppin.company.connection.ConnectApi;
import com.nuppin.company.model.Company;
import com.nuppin.company.model.PaymentCompany;
import com.nuppin.company.R;
import com.nuppin.company.Util.Util;
import com.nuppin.company.model.ErrorCode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import me.toptas.fancyshowcase.FancyShowCaseView;
import me.toptas.fancyshowcase.FocusShape;
import me.toptas.fancyshowcase.listener.DismissListener;

public class ConfigServico extends Fragment implements
        LoaderManager.LoaderCallbacks,
        View.OnClickListener,
        CompoundButton.OnCheckedChangeListener,
        InfoDialogFragment.InfoDialogListener {

    private static final String COMPANY = "COMPANY";
    private LoaderManager loaderManager;
    private SeekBar raioClientes;
    private CheckBox agendamentos, pdv;
    private TextView seekRaio, seekRaioTxt;
    private MaterialButton btn;
    private Company company;
    private Company companyWithNewConfigs;
    private Company companyVisibility;
    private List<PaymentCompany> paymentCompanies;
    private LottieAnimationView dots;
    private ConstraintLayout errorLayout;
    private FloatingActionButton fabError;
    private CardView progress;
    private ScrollView scrollView;
    private Object data;
    private Switch visibilitySwitch;
    private ConstraintLayout constraintVisibility, constraintAnalyze;


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
                        .focusOn(constraintVisibility)
                        .delay(500)
                        .title(getString(R.string.unique_tutorial_company_config_visibility_string))
                        .focusShape(FocusShape.ROUNDED_RECTANGLE)
                        .showOnce(getString(R.string.unique_tutorial_company_config_visibility))
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


    public ConfigServico() {
        // Required empty public constructor
    }

    public static ConfigServico newInstance(Company company) {
        ConfigServico fragment = new ConfigServico();
        Bundle args = new Bundle();
        args.putParcelable(COMPANY, company);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        assert getArguments() != null;
        if (getArguments().containsKey(COMPANY)) {
            company = getArguments().getParcelable(COMPANY);
        }

        loaderManager = LoaderManager.getInstance(this);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.company_scheduling_config, container, false);


        dots = view.findViewById(R.id.dots);
        errorLayout = view.findViewById(R.id.error_layout);
        fabError = view.findViewById(R.id.fabError);
        fabError.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dots.setVisibility(View.VISIBLE);
                errorLayout.setVisibility(View.GONE);
                loaderManager.restartLoader(0, null, ConfigServico.this);
            }
        });
        progress = view.findViewById(R.id.progress);
        scrollView = view.findViewById(R.id.scrollView);

        Toolbar toolbar = view.findViewById(R.id.toolbar_top);
        Util.setaToolbar(this, R.string.config_toolbar, toolbar, getActivity(), false, 0);

        agendamentos = view.findViewById(R.id.agendamentos);
        pdv = view.findViewById(R.id.pdv);
        raioClientes = view.findViewById(R.id.distanciaMax);
        seekRaio = view.findViewById(R.id.valorMaxDistancia);
        CardView cardMeiospag = view.findViewById(R.id.cardMeioPag);
        CardView cardHorarios = view.findViewById(R.id.cardHorarios);
        visibilitySwitch = view.findViewById(R.id.visibilitySwitch);
        seekRaioTxt = view.findViewById(R.id.txtMaxDistancia);
        constraintVisibility = view.findViewById(R.id.companyVisibility);
        constraintAnalyze = view.findViewById(R.id.companyAnalyze);

        if (company.getVisibility() == 1) {
            visibilitySwitch.setChecked(true);
        } else {
            if (company.getValidation() != null && company.getValidation().equals("pending")) {
                constraintAnalyze.setVisibility(View.VISIBLE);
                constraintVisibility.setVisibility(View.GONE);
            }
            visibilitySwitch.setChecked(false);
        }

        visibilitySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (visibilitySwitch.isPressed()) {
                    if (companyVisibility == null) {
                        companyVisibility = new Company();
                    }
                    companyVisibility.setCompany_id(company.getCompany_id());
                    companyVisibility.setCategory_company_id(company.getCategory_company_id());
                    String message;
                    String btnPositive;
                    String btnNegative;
                    if (b) {
                        companyVisibility.setVisibility(1);
                        message = "Tudo pronto para ficar online?";
                        btnPositive = "Mais que pronto";
                        btnNegative = "Ainda não";
                    } else {
                        companyVisibility.setVisibility(0);
                        message = "Deseja mesmo ocultar sua empresa?";
                        btnPositive = "Desejo";
                        btnNegative = "Vou Revisar";
                    }
                    DialogFragment dialogFrag = InfoDialogFragment.newInstance(message, btnPositive, btnNegative);
                    dialogFrag.show(ConfigServico.this.getChildFragmentManager(), "dialog_fragment");
                    dialogFrag.setCancelable(false);

                }
            }
        });

        setaConfig();

        agendamentos.setOnCheckedChangeListener(this);
        pdv.setOnCheckedChangeListener(this);
        cardMeiospag.setOnClickListener(this);
        cardHorarios.setOnClickListener(this);
        raioClientes.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                seekRaio.setText(getResources().getString(R.string.raio_km, i));
                btn.setVisibility(View.VISIBLE);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        btn = view.findViewById(R.id.btnSalvar);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (valida()) {
                    companyWithNewConfigs = defaultValues();
                    if (agendamentos.isChecked()) {
                        companyWithNewConfigs.setIs_local(1);
                        companyWithNewConfigs.setMax_radius(raioClientes.getProgress());
                    }
                    if (pdv.isChecked()) {
                        companyWithNewConfigs.setIs_pos(1);
                    }
                    progress.setVisibility(View.VISIBLE);
                    btn.setClickable(false);
                    loaderManager.restartLoader(1, null, ConfigServico.this);
                }
            }
        });

        loaderManager.restartLoader(0, null, ConfigServico.this);
        chainCreatedTutorial();
        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
        loaderManager.destroyLoader(0);
    }

    private Company defaultValues() {
        company.setIs_local(0);
        company.setIs_pos(0);
        company.setMax_radius(0);
        return company;
    }

    private boolean valida() {
        if (agendamentos.isChecked()) {
            if (raioClientes.getProgress() < 1) {
                Toast.makeText(getActivity(), R.string.warning_raio_min, Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        return true;
    }

    private void setaConfig() {

        if (company.getMax_radius() > 0) {
            raioClientes.setProgress(company.getMax_radius());
            seekRaio.setText(getResources().getString(R.string.raio_km, company.getMax_radius()));
            seekRaio.setVisibility(View.VISIBLE);
            raioClientes.setVisibility(View.VISIBLE);
            seekRaioTxt.setVisibility(View.VISIBLE);
        }
        if (company.getIs_pos() == 1) {
            pdv.setChecked(true);
        }
        if (company.getIs_local() == 1) {
            agendamentos.setChecked(true);
        }

    }

    @Override
    public void onDialogOKClick(View view, InfoDialogFragment info) {
        switch (view.getId()) {
            case R.id.btnPositive:
                info.dismiss();
                visibilitySwitch.setClickable(false);
                loaderManager.restartLoader(2, null, ConfigServico.this);
                break;
            case R.id.btnNegative:
                if (company.getVisibility() > 0) {
                    visibilitySwitch.setChecked(true);
                } else {
                    visibilitySwitch.setChecked(false);
                }
                info.dismiss();
                break;
        }
    }

    @NonNull
    @Override
    public Loader onCreateLoader(int id, @Nullable Bundle args) {
        if (id == 0) {
            return new LoaderMeiosPagamento(Objects.requireNonNull(getContext()), company.getCompany_id(), data);
        } else if (id == 1) {
            return new EditaStore(getContext(), companyWithNewConfigs);
        } else {
            return new Visibility(requireContext(), companyVisibility);
        }
    }

    @Override
    public void onLoadFinished(@NonNull Loader loader, Object data) {
        dots.setVisibility(View.GONE);
        progress.setVisibility(View.GONE);
        if (loader.getId() != 2) {
            if (data != null) {
                this.data = data;
                if (loader.getId() == 0) {
                    if (data instanceof List) {
                        scrollView.setVisibility(View.VISIBLE);
                        paymentCompanies = (List) data;
                    }
                } else {
                    loaderManager.destroyLoader(loader.getId());
                    Util.backFragmentFunction(this);
                }
                errorLayout.setVisibility(View.GONE);
            } else {
                scrollView.setVisibility(View.VISIBLE);
                errorLayout.setVisibility(View.VISIBLE);
            }
        } else {
            if (data != null) {
                if (data instanceof Integer) {
                    switch ((int) data){
                        case 0:
                            Toast.makeText(getContext(), "Houve um erro", Toast.LENGTH_SHORT).show();
                            if (company.getVisibility() > 0) {
                                visibilitySwitch.setChecked(true);
                            } else {
                                visibilitySwitch.setChecked(false);
                            }
                            break;
                        case 1:
                            Toast.makeText(getContext(), R.string.concluido, Toast.LENGTH_SHORT).show();
                            company.setVisibility(companyVisibility.getVisibility());
                            if (company.getVisibility() > 0) {
                                visibilitySwitch.setChecked(true);
                            } else {
                                visibilitySwitch.setChecked(false);
                            }
                            break;
                        case 2:
                            company.setValidation("pending");
                            Toast.makeText(getContext(), R.string.concluido, Toast.LENGTH_SHORT).show();
                            constraintAnalyze.setVisibility(View.VISIBLE);
                            constraintVisibility.setVisibility(View.GONE);
                            break;
                    }
                } else if (data instanceof ErrorCode) {
                    Toast.makeText(getContext(), ((ErrorCode) data).getError_message(), Toast.LENGTH_SHORT).show();
                    if (company.getVisibility() > 0) {
                        visibilitySwitch.setChecked(true);
                    } else {
                        visibilitySwitch.setChecked(false);
                    }
                }
            } else {
                Toast.makeText(getContext(), R.string.error, Toast.LENGTH_SHORT).show();
                if (company.getVisibility() > 0) {
                    visibilitySwitch.setChecked(true);
                } else {
                    visibilitySwitch.setChecked(false);
                }
            }
        }
        if (loader.getId() != 0) {
            loaderManager.destroyLoader(loader.getId());
        }
        btn.setClickable(true);
        visibilitySwitch.setClickable(true);
        chainTutorial();
    }

    @Override
    public void onLoaderReset(@NonNull Loader loader) {

    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        btn.setVisibility(View.VISIBLE);
        switch (compoundButton.getId()) {
            case R.id.agendamentos:
                if (b) {
                    if (paymentCompanies.size() > 0) {
                        seekRaio.setVisibility(View.VISIBLE);
                        raioClientes.setVisibility(View.VISIBLE);
                        seekRaioTxt.setVisibility(View.VISIBLE);
                    } else {
                        btn.setVisibility(View.GONE);
                        agendamentos.setChecked(false);
                        Toast.makeText(getActivity(), R.string.warning_check_delivery_before_set_payment_method, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    seekRaio.setVisibility(View.GONE);
                    raioClientes.setVisibility(View.GONE);
                    seekRaioTxt.setVisibility(View.GONE);
                }
                break;
            case R.id.pdv:
                break;
        }
    }

    @Override
    public void onClick(View view) {
        if (getActivity() instanceof ConfigDetails) {
            ConfigDetails listener = (ConfigDetails) getActivity();
            switch (view.getId()) {
                case R.id.cardHorarios:
                    listener.clickDetails(R.id.cardHorarios, company);
                    break;
                case R.id.cardMeioPag:
                    listener.clickDetails(R.id.cardMeioPag, company);
                    break;
            }
        }
    }

    private static class EditaStore extends AsyncTaskLoader {

        Company company;
        Context ctx;

        private EditaStore(Context context, Company company) {
            super(context);
            ctx = context;
            this.company = company;
        }

        @Override
        protected void onStartLoading() {
            forceLoad();
        }

        @Override
        public Company loadInBackground() {
            Gson gson = new Gson();
            return gson.fromJson(ConnectApi.PATCH(company, ConnectApi.COMPANY, getContext()), Company.class);
        }
    }


    private static class LoaderMeiosPagamento extends AsyncTaskLoader<Object> {

        String storeId;
        Context ctx;
        Object data;

        LoaderMeiosPagamento(@NonNull Context context, String storeId, Object data) {
            super(context);
            this.storeId = storeId;
            this.ctx = context;
            this.data = data;
        }

        @Override
        protected void onStartLoading() {
            super.onStartLoading();
            if (data != null) {
                deliverResult(data);
            }
            forceLoad();
        }

        @Nullable
        @Override
        public Object loadInBackground() {
            Gson gson = new Gson();
            try {
                PaymentCompany[] paymentCompanies = gson.fromJson(ConnectApi.GET(ConnectApi.COMPANY_PAYMENT_CHECK + "/" + storeId, getContext()), PaymentCompany[].class);
                return new ArrayList<>(Arrays.asList(paymentCompanies));
            } catch (Exception e) {
                FirebaseCrashlytics.getInstance().log(UtilShaPre.getDefaultsString(AppConstants.USER_ID, getContext()));
                FirebaseCrashlytics.getInstance().recordException(e);
                return null;
            }
        }
    }

    private static class Visibility extends AsyncTaskLoader<Object> {

        Company company;

        Visibility(@NonNull Context context, Company company) {
            super(context);
            this.company = company;
        }

        @Override
        protected void onStartLoading() {
            super.onStartLoading();
            forceLoad();
        }

        @Nullable
        @Override
        public Object loadInBackground() {
            Gson gson = new Gson();
            String json = ConnectApi.PATCH(company, ConnectApi.COMPANY_VISIBILITY, getContext());
            JsonParser parser = new JsonParser();
            try {
                return gson.fromJson(parser.parse(json).getAsJsonPrimitive(), Integer.class);
            } catch (Exception e) {
                FirebaseCrashlytics.getInstance().log(UtilShaPre.getDefaultsString(AppConstants.USER_ID, getContext()));
                FirebaseCrashlytics.getInstance().recordException(e);
                try {
                    return gson.fromJson(parser.parse(json).getAsJsonObject().getAsJsonObject(AppConstants.ERROR), ErrorCode.class);
                } catch (Exception e1) {
                    FirebaseCrashlytics.getInstance().log(UtilShaPre.getDefaultsString(AppConstants.USER_ID, getContext()));
                    FirebaseCrashlytics.getInstance().recordException(e1);
                    return null;
                }
            }
        }
    }


    public interface ConfigDetails {
        void clickDetails(int index, Company company);
    }
}