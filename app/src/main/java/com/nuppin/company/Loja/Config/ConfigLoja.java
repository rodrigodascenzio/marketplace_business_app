package com.nuppin.company.Loja.Config;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
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
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.gson.Gson;
import com.google.gson.JsonParser;
import com.nuppin.company.Loja.dialogs.InfoDialogFragment;
import com.nuppin.company.Loja.dialogs.SeekbarDialogFragment;
import com.nuppin.company.Util.AppConstants;
import com.nuppin.company.Util.UtilShaPre;
import com.nuppin.company.connection.ConnectApi;
import com.nuppin.company.model.Company;
import com.nuppin.company.model.PaymentCompany;
import com.nuppin.company.R;
import com.nuppin.company.Util.MaskEditUtil;
import com.nuppin.company.Util.Util;
import com.nuppin.company.model.ErrorCode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import me.toptas.fancyshowcase.FancyShowCaseView;
import me.toptas.fancyshowcase.FocusShape;
import me.toptas.fancyshowcase.listener.DismissListener;

public class ConfigLoja extends Fragment implements
        LoaderManager.LoaderCallbacks,
        View.OnClickListener,
        CompoundButton.OnCheckedChangeListener,
        RadioGroup.OnCheckedChangeListener,
        InfoDialogFragment.InfoDialogListener,
        SeekbarDialogFragment.SeekbarDialogListener {

    private static final String COMPANY = "COMPANY";
    private static final String TEMPO_MAX = "TEMPO_MAX";
    private LoaderManager loaderManager;
    private EditText fixo, variavel, minPurchase;
    private SwitchCompat opcaoGratis, switchMinPurchase;
    private SeekBar distanciaMaxGratis, raioEntrega;
    private CardView detalhesEntrega, cardRaio;
    private RadioGroup formaEntrega;
    private CheckBox entrega, retirada, pdv;
    private TextView tempoMaximo, valorSeekGratis, valorSeekRaio, infoMaxKmValue, infoMaxFee;
    private TextInputLayout wrapPrecoFixo, wrapPrecoVariavel, minPurchaseWrap;
    private String tempoMaximoTxt;
    private MaterialButton btn;
    private Company company;
    private Company companyWithNewConfigs;
    private Company companyVisibility;
    private List<PaymentCompany> paymentCompanies;
    private LottieAnimationView dots;
    private ConstraintLayout errorLayout, constraintMaxFeeInfo, constInfoMaxKmValue;
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
                        .focusShape(FocusShape.ROUNDED_RECTANGLE)
                        .title(getString(R.string.unique_tutorial_company_config_visibility_string))
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


    public ConfigLoja() {
        // Required empty public constructor
    }

    public static ConfigLoja newInstance(Company company) {
        ConfigLoja fragment = new ConfigLoja();
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
        View view = inflater.inflate(R.layout.company_config, container, false);

        dots = view.findViewById(R.id.dots);
        errorLayout = view.findViewById(R.id.error_layout);
        fabError = view.findViewById(R.id.fabError);
        fabError.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dots.setVisibility(View.VISIBLE);
                errorLayout.setVisibility(View.GONE);
                loaderManager.restartLoader(0, null, ConfigLoja.this);
            }
        });
        progress = view.findViewById(R.id.progress);
        scrollView = view.findViewById(R.id.scrollView);
        cardRaio = view.findViewById(R.id.cardRaio);

        Toolbar toolbar = view.findViewById(R.id.toolbar_top);
        Util.setaToolbar(this, R.string.config_toolbar, toolbar, getActivity(), false, 0);

        constInfoMaxKmValue = view.findViewById(R.id.constInfoMaxKmValue);
        infoMaxFee = view.findViewById(R.id.infoMaxfee);
        constraintMaxFeeInfo = view.findViewById(R.id.constMaxFeeInfo);
        infoMaxKmValue = view.findViewById(R.id.infoMaxKmValue);
        wrapPrecoFixo = view.findViewById(R.id.wrapValorPrecoFixo);
        wrapPrecoVariavel = view.findViewById(R.id.wrapValorPrecoVariavel);
        fixo = view.findViewById(R.id.valorPrecoFixo);
        variavel = view.findViewById(R.id.valorPrecoVariavel);
        fixo.addTextChangedListener(MaskEditUtil.monetario(fixo));
        variavel.addTextChangedListener(MaskEditUtil.monetario(variavel));
        opcaoGratis = view.findViewById(R.id.opcaoCurtasDistancias);
        distanciaMaxGratis = view.findViewById(R.id.distanciaMaxGratis);
        detalhesEntrega = view.findViewById(R.id.cardEntrega);
        entrega = view.findViewById(R.id.opcaoEntrega);
        retirada = view.findViewById(R.id.opcaoRetirada);
        formaEntrega = view.findViewById(R.id.formaEntrega);
        pdv = view.findViewById(R.id.opcaoPdv);
        raioEntrega = view.findViewById(R.id.distanciaMax);
        valorSeekRaio = view.findViewById(R.id.valorMaxDistancia);
        valorSeekGratis = view.findViewById(R.id.valorMaxDistanciaGratis);
        tempoMaximo = view.findViewById(R.id.tempoMaximo);
        visibilitySwitch = view.findViewById(R.id.visibilitySwitch);
        switchMinPurchase = view.findViewById(R.id.switchMinPurchase);
        minPurchaseWrap = view.findViewById(R.id.minPurchaseWrap);
        minPurchase = view.findViewById(R.id.minPurchase);
        minPurchase.addTextChangedListener(MaskEditUtil.monetario(minPurchase));
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
                    dialogFrag.show(ConfigLoja.this.getChildFragmentManager(), "dialog_fragment");
                    dialogFrag.setCancelable(false);

                }
            }
        });

        tempoMaximo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment dialogFrag = SeekbarDialogFragment.newInstance(tempoMaximo.getText().toString(), getString(R.string.confirmar), getString(R.string.cancelar));
                dialogFrag.show(ConfigLoja.this.getChildFragmentManager(), TEMPO_MAX);
            }
        });

        CardView cardMeiospag = view.findViewById(R.id.cardMeioPag);
        CardView cardHorarios = view.findViewById(R.id.cardHorarios);

        setaConfig();

        fixo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                defineMaxRadius();
            }
        });

        variavel.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                defineMaxRadius();
            }
        });

        fixo.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                btn.setVisibility(View.VISIBLE);
            }
        });
        variavel.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                defineMaxRadius();
                btn.setVisibility(View.VISIBLE);
            }
        });
        minPurchase.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                btn.setVisibility(View.VISIBLE);
            }
        });
        pdv.setOnCheckedChangeListener(this);
        retirada.setOnCheckedChangeListener(this);
        entrega.setOnCheckedChangeListener(this);
        formaEntrega.setOnCheckedChangeListener(this);
        opcaoGratis.setOnCheckedChangeListener(this);
        switchMinPurchase.setOnCheckedChangeListener(this);
        cardMeiospag.setOnClickListener(this);
        cardHorarios.setOnClickListener(this);
        raioEntrega.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                btn.setVisibility(View.VISIBLE);
                defineMaxRadius();
                valorSeekRaio.setText(getResources().getString(R.string.raio_km, i));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        distanciaMaxGratis.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                valorSeekGratis.setText(getResources().getString(R.string.raio_km, i));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        if (company.getModel_type().equals("mobile")) {
            cardHorarios.setVisibility(View.GONE);
            retirada.setVisibility(View.GONE);
        }

        btn = view.findViewById(R.id.btnSalvar);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (valida()) {
                    progress.setVisibility(View.VISIBLE);
                    companyWithNewConfigs = defaultValues();
                    btn.setClickable(false);

                    if (pdv.isChecked()) {
                        companyWithNewConfigs.setIs_pos(1);
                    }
                    if (retirada.isChecked()) {
                        companyWithNewConfigs.setIs_local(1);
                        companyWithNewConfigs.setMax_radius(raioEntrega.getProgress());
                    }
                    if (entrega.isChecked()) {
                        companyWithNewConfigs.setIs_delivery(1);
                        companyWithNewConfigs.setDelivery_max_time(tempoMaximoTxt);
                        companyWithNewConfigs.setMax_radius(raioEntrega.getProgress());

                        switch (formaEntrega.getCheckedRadioButtonId()) {
                            case R.id.precoFixo:
                                companyWithNewConfigs.setDelivery_type_value(1);
                                companyWithNewConfigs.setDelivery_fixed_fee(Util.unmaskPrice(fixo.getText().toString()));
                                if (opcaoGratis.isChecked()) {
                                    companyWithNewConfigs.setMax_radius_free(distanciaMaxGratis.getProgress());
                                    if (switchMinPurchase.isChecked()) {
                                        companyWithNewConfigs.setMin_purchase(Util.unmaskPrice(minPurchase.getText().toString()));
                                    }
                                }
                                break;
                            case R.id.fixoMaisDistancia:
                                companyWithNewConfigs.setDelivery_type_value(2);
                                companyWithNewConfigs.setDelivery_fixed_fee(Util.unmaskPrice(fixo.getText().toString()));
                                companyWithNewConfigs.setDelivery_variable_fee(Util.unmaskPrice(variavel.getText().toString()));
                                if (opcaoGratis.isChecked()) {
                                    companyWithNewConfigs.setMax_radius_free(distanciaMaxGratis.getProgress());
                                    if (switchMinPurchase.isChecked()) {
                                        companyWithNewConfigs.setMin_purchase(Util.unmaskPrice(minPurchase.getText().toString()));
                                    }
                                }
                                break;
                            case R.id.apenasDistancia:
                                companyWithNewConfigs.setDelivery_type_value(3);
                                companyWithNewConfigs.setDelivery_variable_fee(Util.unmaskPrice(variavel.getText().toString()));
                                if (opcaoGratis.isChecked()) {
                                    companyWithNewConfigs.setMax_radius_free(distanciaMaxGratis.getProgress());
                                    if (switchMinPurchase.isChecked()) {
                                        companyWithNewConfigs.setMin_purchase(Util.unmaskPrice(minPurchase.getText().toString()));
                                    }
                                }
                                break;
                            case R.id.gratis:
                                if (switchMinPurchase.isChecked()) {
                                    companyWithNewConfigs.setMin_purchase(Util.unmaskPrice(minPurchase.getText().toString()));
                                }
                                companyWithNewConfigs.setDelivery_type_value(4);
                                break;
                        }
                    }
                    loaderManager.restartLoader(1, null, ConfigLoja.this);
                }
            }
        });
        loaderManager.restartLoader(0, null, ConfigLoja.this);

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
        company.setIs_delivery(0);
        company.setMax_radius(0);
        company.setDelivery_max_time("0");
        company.setDelivery_min_time("0");
        company.setMax_radius_free(0);
        company.setDelivery_fixed_fee(0);
        company.setDelivery_variable_fee(0);
        company.setDelivery_type_value(0);
        company.setMin_purchase(0);
        return company;
    }


    private boolean valida() {
        if (retirada.isChecked()) {
            if (raioEntrega.getProgress() < 1) {
                Toast.makeText(getActivity(), R.string.warning_raio_min, Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        if (entrega.isChecked()) {
            if (raioEntrega.getProgress() < 1) {
                Toast.makeText(getActivity(), R.string.warning_raio_min, Toast.LENGTH_SHORT).show();
                return false;
            }
            if (tempoMaximo.getText().toString().equals("")) {
                Toast.makeText(getActivity(), R.string.warning_time, Toast.LENGTH_SHORT).show();
                return false;
            }
            if (opcaoGratis.isChecked()) {
                if (distanciaMaxGratis.getProgress() < 1) {
                    Toast.makeText(getActivity(), R.string.warning_raio_min_free, Toast.LENGTH_SHORT).show();
                    return false;
                }
            }

            if (switchMinPurchase.isChecked()) {
                if (minPurchase.getText().toString().equals("") || minPurchase.getText().toString().equals("0,00")) {
                    Toast.makeText(getActivity(), "Defina o valor minimo do pedido", Toast.LENGTH_SHORT).show();
                    return false;
                }
            }

            switch (formaEntrega.getCheckedRadioButtonId()) {
                case R.id.precoFixo:
                    if (fixo.getText().toString().equals("") || fixo.getText().toString().equals("0,00")) {
                        Toast.makeText(getActivity(), R.string.warning_value_of_delivery, Toast.LENGTH_SHORT).show();
                        return false;
                    }
                    break;
                case R.id.fixoMaisDistancia:
                    if (fixo.getText().toString().equals("") || fixo.getText().toString().equals("0,00")) {
                        Toast.makeText(getActivity(), R.string.warning_value_of_delivery, Toast.LENGTH_SHORT).show();
                        return false;
                    } else if (variavel.getText().toString().equals("") || variavel.getText().toString().equals("0,00")) {
                        Toast.makeText(getActivity(), R.string.warning_value_of_delivery, Toast.LENGTH_SHORT).show();
                        return false;
                    }
                    break;
                case R.id.apenasDistancia:
                    if (variavel.getText().toString().equals("") || variavel.getText().toString().equals("0,00")) {
                        Toast.makeText(getActivity(), R.string.warning_value_of_delivery, Toast.LENGTH_SHORT).show();
                        return false;
                    }
                    break;
                case R.id.gratis:
                    break;
                default:
                    Toast.makeText(getActivity(), R.string.warning_no_delivery_type_selected, Toast.LENGTH_SHORT).show();
                    return false;
            }
        }
        return true;
    }

    private void setaConfig() {

        if (company.getIs_delivery() == 1) {
            entrega.setChecked(true);
            detalhesEntrega.setVisibility(View.VISIBLE);
            opcaoGratis.setVisibility(View.VISIBLE);
            if (company.getMax_radius_free() > 0) {
                opcaoGratis.setChecked(true);
                distanciaMaxGratis.setVisibility(View.VISIBLE);
                distanciaMaxGratis.setProgress(company.getMax_radius_free());
                valorSeekGratis.setVisibility(View.VISIBLE);
                valorSeekGratis.setText(getResources().getString(R.string.raio_km, company.getMax_radius_free()));
                switchMinPurchase.setVisibility(View.VISIBLE);
            }

            if (company.getMin_purchase() > 0) {
                switchMinPurchase.setChecked(true);
                switchMinPurchase.setVisibility(View.VISIBLE);
                minPurchaseWrap.setVisibility(View.VISIBLE);
                minPurchase.setText(Util.formatDecimaisDoubleToString(company.getMin_purchase()));
            }

            if (company.getMax_radius() > 0) {
                raioEntrega.setProgress(company.getMax_radius());
                valorSeekRaio.setText(getResources().getString(R.string.raio_km, company.getMax_radius()));
                cardRaio.setVisibility(View.VISIBLE);
            }

            tempoMaximo.setText(getResources().getString(R.string.entrega_min_max, company.getDelivery_max_time()));

            switch (company.getDelivery_type_value()) {
                case 1:
                    formaEntrega.check(R.id.precoFixo);
                    fixo.setText(Util.formatDecimaisDoubleToString(company.getDelivery_fixed_fee()));
                    wrapPrecoFixo.setVisibility(View.VISIBLE);
                    break;
                case 2:
                    formaEntrega.check(R.id.fixoMaisDistancia);
                    fixo.setText(Util.formatDecimaisDoubleToString(company.getDelivery_fixed_fee()));
                    variavel.setText(Util.formatDecimaisDoubleToString(company.getDelivery_variable_fee()));
                    wrapPrecoFixo.setVisibility(View.VISIBLE);
                    wrapPrecoVariavel.setVisibility(View.VISIBLE);
                    break;
                case 3:
                    formaEntrega.check(R.id.apenasDistancia);
                    variavel.setText(Util.formatDecimaisDoubleToString(company.getDelivery_variable_fee()));
                    wrapPrecoVariavel.setVisibility(View.VISIBLE);
                    break;
                case 4:
                    formaEntrega.check(R.id.gratis);
                    opcaoGratis.setVisibility(View.GONE);
                    switchMinPurchase.setVisibility(View.VISIBLE);
                    break;
            }
        }


        if (company.getIs_local() == 1 && company.getModel_type().equals("fixed")) {
            retirada.setChecked(true);
            if (company.getMax_radius() > 0) {
                raioEntrega.setProgress(company.getMax_radius());
                valorSeekRaio.setText(getResources().getString(R.string.raio_km, company.getMax_radius()));
                cardRaio.setVisibility(View.VISIBLE);
            }
        }
        if (company.getIs_pos() == 1) {
            pdv.setChecked(true);
        }
    }

    @NonNull
    @Override
    public Loader onCreateLoader(int id, @Nullable Bundle args) {
        if (id == 0) {
            return new LoaderMeiosPagamento(requireContext(), company.getCompany_id(), data);
        } else if (id == 1) {
            return new EditaStore(requireContext(), companyWithNewConfigs);
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

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        btn.setVisibility(View.VISIBLE);
        switch (compoundButton.getId()) {
            case R.id.opcaoEntrega:
                if (b) {
                    if (paymentCompanies.size() > 0) {
                        detalhesEntrega.setVisibility(View.VISIBLE);
                        cardRaio.setVisibility(View.VISIBLE);
                    } else {
                        btn.setVisibility(View.GONE);
                        entrega.setChecked(false);
                        Toast.makeText(getActivity(), R.string.warning_check_delivery_before_set_payment_method, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    detalhesEntrega.setVisibility(View.GONE);
                    if (!retirada.isChecked()) {
                        cardRaio.setVisibility(View.GONE);
                    }
                }
                break;
            case R.id.opcaoRetirada:
                if (b) {
                    if (paymentCompanies.size() > 0) {
                        cardRaio.setVisibility(View.VISIBLE);
                    } else {
                        retirada.setChecked(false);
                        btn.setVisibility(View.GONE);
                        Toast.makeText(getActivity(), R.string.warning_check_retirada_before_set_payment_method, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    if (!entrega.isChecked()) {
                        cardRaio.setVisibility(View.GONE);
                    }
                }
                break;
            case R.id.opcaoPdv:
                break;
            case R.id.switchMinPurchase:
                if (b) {
                    minPurchaseWrap.setVisibility(View.VISIBLE);
                } else {
                    minPurchaseWrap.setVisibility(View.GONE);
                    minPurchase.setText("");
                }
                break;
            case R.id.opcaoCurtasDistancias:
                if (b) {
                    distanciaMaxGratis.setVisibility(View.VISIBLE);
                    valorSeekGratis.setVisibility(View.VISIBLE);
                    switchMinPurchase.setVisibility(View.VISIBLE);
                } else {
                    switchMinPurchase.setVisibility(View.GONE);
                    switchMinPurchase.setChecked(false);
                    distanciaMaxGratis.setVisibility(View.GONE);
                    valorSeekGratis.setVisibility(View.GONE);
                    valorSeekGratis.setText("0");
                    distanciaMaxGratis.setProgress(0);
                }
                break;
        }
        defineMaxRadius();
    }

    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int i) {
        btn.setVisibility(View.VISIBLE);
        wrapPrecoFixo.setVisibility(View.GONE);
        wrapPrecoVariavel.setVisibility(View.GONE);
        opcaoGratis.setVisibility(View.GONE);
        variavel.setText("");
        fixo.setText("");

        if (i != R.id.gratis && !opcaoGratis.isChecked()) {
            switchMinPurchase.setChecked(false);
            switchMinPurchase.setVisibility(View.GONE);
        }

        switch (i) {
            case R.id.precoFixo:
                wrapPrecoFixo.setVisibility(View.VISIBLE);
                opcaoGratis.setVisibility(View.VISIBLE);
                fixo.requestFocus();
                break;
            case R.id.fixoMaisDistancia:
                wrapPrecoFixo.setVisibility(View.VISIBLE);
                wrapPrecoVariavel.setVisibility(View.VISIBLE);
                opcaoGratis.setVisibility(View.VISIBLE);
                fixo.requestFocus();
                break;
            case R.id.apenasDistancia:
                wrapPrecoVariavel.setVisibility(View.VISIBLE);
                opcaoGratis.setVisibility(View.VISIBLE);
                variavel.requestFocus();
                break;
            case R.id.gratis:
                opcaoGratis.setChecked(false);
                switchMinPurchase.setVisibility(View.VISIBLE);
                break;
        }
    }

    @Override
    public void onDialogOKClick(View view, InfoDialogFragment info) {
        switch (view.getId()) {
            case R.id.btnPositive:
                info.dismiss();
                visibilitySwitch.setClickable(false);
                loaderManager.restartLoader(2, null, ConfigLoja.this);
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

    @Override
    public void onDialogOKClick(View view, int value, SeekbarDialogFragment info) {
        btn.setVisibility(View.VISIBLE);
        switch (view.getId()) {
            case R.id.btnPositive:
                tempoMaximo.setText(getResources().getString(R.string.entrega_min_max_int, value));
                tempoMaximoTxt = String.valueOf(value);
                info.dismiss();
                break;
            case R.id.btnNegative:
                info.dismiss();
                break;
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
        public Object loadInBackground() {
            try {
                Gson gson = new Gson();
                return gson.fromJson(ConnectApi.PATCH(company, ConnectApi.COMPANY, getContext()), Company.class);
            } catch (Exception e) {
                FirebaseCrashlytics.getInstance().log(UtilShaPre.getDefaultsString(AppConstants.USER_ID, getContext()));
                FirebaseCrashlytics.getInstance().recordException(e);
                return null;
            }
        }
    }

    private void defineMaxRadius() {
        double maxFee = 25.00;
        boolean isPay = false;
        boolean isOk = false;
        if (entrega.isChecked()) {

            switch (formaEntrega.getCheckedRadioButtonId()) {
                case R.id.precoFixo:
                    if (!fixo.getText().toString().isEmpty()) {
                        double fixoFee = Util.unmaskPrice(fixo.getText().toString());
                        if (fixoFee > 25) {
                            fixo.setText("25,00");
                            infoMaxFee.setText(Util.formaterPrice(25.00));
                        } else {
                            infoMaxFee.setText(Util.formaterPrice(fixoFee));
                        }

                        raioEntrega.setMax(50);
                        isOk = true;
                        isPay = true;
                    } else {
                        raioEntrega.setMax(0);
                    }
                    break;
                case R.id.fixoMaisDistancia:
                    if (!fixo.getText().toString().isEmpty() && !variavel.getText().toString().isEmpty()) {
                        double fixoFee = Util.unmaskPrice(fixo.getText().toString());
                        if (fixoFee > 25) {
                            fixo.setText("25,00");
                            fixoFee = 25.00;
                        }
                        double newfee = maxFee - Util.unmaskPrice(fixo.getText().toString());
                        int maxRadius = (int) (newfee / Util.unmaskPrice(variavel.getText().toString()));
                        if (opcaoGratis.isChecked() && distanciaMaxGratis.getProgress() > maxRadius) {
                            raioEntrega.setMax(distanciaMaxGratis.getProgress());
                        } else {
                            raioEntrega.setMax(Math.min(maxRadius, 50));
                        }
                        if (raioEntrega.getProgress() > 0) {
                            infoMaxFee.setText(Util.formaterPrice(Util.roundHalf(fixoFee + (Util.unmaskPrice(variavel.getText().toString())) * raioEntrega.getProgress())));
                            isPay = true;
                        }
                        isOk = true;
                    } else {
                        raioEntrega.setMax(0);
                    }
                    break;
                case R.id.apenasDistancia:
                    if (!variavel.getText().toString().isEmpty()) {
                        int maxRadius2 = (int) ((maxFee / Util.unmaskPrice(variavel.getText().toString())));
                        if (opcaoGratis.isChecked() && distanciaMaxGratis.getProgress() > maxRadius2) {
                            raioEntrega.setMax(distanciaMaxGratis.getProgress());
                        } else {
                            raioEntrega.setMax(Math.min(maxRadius2, 50));
                        }
                        if (raioEntrega.getProgress() > 0) {
                            infoMaxFee.setText(Util.formaterPrice(Util.roundHalf(Util.unmaskPrice(variavel.getText().toString()) * raioEntrega.getProgress())));
                            isPay = true;
                        }
                        isOk = true;
                    } else {
                        raioEntrega.setMax(0);
                    }
                    break;
                case R.id.gratis:
                    raioEntrega.setMax(50);
                    isPay = false;
                    isOk = true;
                    break;
            }
        } else if (retirada.isChecked()) {
            raioEntrega.setMax(50);
            isPay = false;
            isOk = true;
        }

        raioEntrega.setProgress(raioEntrega.getProgress());

        constInfoMaxKmValue.setVisibility(View.VISIBLE);
        if (isOk) {
            infoMaxKmValue.setText(String.valueOf(raioEntrega.getMax()) + " km");
        } else {
            infoMaxKmValue.setText("0 km");
        }
        if (isPay) {
            constraintMaxFeeInfo.setVisibility(View.VISIBLE);
        } else {
            constraintMaxFeeInfo.setVisibility(View.GONE);
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