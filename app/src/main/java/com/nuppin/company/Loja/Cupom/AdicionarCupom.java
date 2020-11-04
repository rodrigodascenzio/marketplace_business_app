package com.nuppin.company.Loja.Cupom;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.AsyncTaskLoader;
import androidx.loader.content.Loader;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.gson.Gson;
import com.google.gson.JsonParser;
import com.nuppin.company.Loja.dialogs.InfoDialogFragment;
import com.nuppin.company.Util.AppConstants;
import com.nuppin.company.Util.UtilShaPre;
import com.nuppin.company.connection.ConnectApi;
import com.nuppin.company.model.Company;
import com.nuppin.company.model.Coupon;
import com.nuppin.company.R;
import com.nuppin.company.Util.MaskEditUtil;
import com.nuppin.company.Util.Util;
import com.nuppin.company.model.ErrorCode;

public class AdicionarCupom extends Fragment implements
        LoaderManager.LoaderCallbacks, InfoDialogFragment.InfoDialogListener {

    private static final String COMPANY = "COMPANY";
    private Company company;
    private LoaderManager loaderManager;
    private int validade;
    private CardView progress;
    private Coupon coupon;
    private MaterialButton btnAdicionar;
    private EditText valor, qtd, minCompraValor, percent;
    private TextInputLayout minCompraValorWrap, qtdWrap, valorWrap, percentWrap;
    private RadioGroup radioGroup, radioGroup2;
    private Switch minCompra;
    private Spinner dias;

    public AdicionarCupom() {
    }

    public static AdicionarCupom newInstance(Company company) {
        AdicionarCupom fragment = new AdicionarCupom();
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
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.add_cupom, container, false);

        valor = view.findViewById(R.id.valor);
        qtd = view.findViewById(R.id.qtd);
        minCompraValor = view.findViewById(R.id.minCompraValor);
        minCompraValorWrap = view.findViewById(R.id.minCompraValorWrap);
        qtdWrap = view.findViewById(R.id.qtdWrap);
        valorWrap = view.findViewById(R.id.valorWrap);
        radioGroup = view.findViewById(R.id.radioGroup);
        radioGroup2 = view.findViewById(R.id.radioGroup2);
        btnAdicionar = view.findViewById(R.id.btnAdicionar);
        minCompra = view.findViewById(R.id.minCompra);
        dias = view.findViewById(R.id.spinner);
        progress = view.findViewById(R.id.progress);
        percent = view.findViewById(R.id.percent);
        percentWrap = view.findViewById(R.id.percentWrap);

        minCompraValor.addTextChangedListener(MaskEditUtil.monetario(minCompraValor));
        valor.addTextChangedListener(MaskEditUtil.monetario(valor));

        minCompra.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    minCompraValorWrap.setVisibility(View.VISIBLE);
                } else {
                    minCompraValorWrap.setVisibility(View.GONE);
                }
            }
        });

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if (i == R.id.valorFixo) {
                    valorWrap.setVisibility(View.VISIBLE);
                    percentWrap.setVisibility(View.GONE);
                    percent.setText("");
                    valor.requestFocus();
                } else {
                    valorWrap.setVisibility(View.GONE);
                    percentWrap.setVisibility(View.VISIBLE);
                    valor.setText("0");
                    percent.requestFocus();

                }
            }
        });

        btnAdicionar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (radioGroup.getCheckedRadioButtonId() == R.id.porcentagem && Util.unmaskPrice(percent.getText().toString()) >= 50) {
                    DialogFragment dialogFrag = InfoDialogFragment.newInstance(getResources().getString(R.string.aviso_porcentagem), getResources().getString(R.string.correto), getResources().getString(R.string.revisar));
                    dialogFrag.show(AdicionarCupom.this.getChildFragmentManager(), "aviso_porcentagem");
                    return;
                }

                DialogFragment dialogFrag = InfoDialogFragment.newInstance(getResources().getString(R.string.adicionar_cupom_aviso), getResources().getString(R.string.criar_cupom), getResources().getString(R.string.revisar));
                dialogFrag.show(AdicionarCupom.this.getChildFragmentManager(), "aviso_criar");
            }
        });

        Toolbar toolbar = view.findViewById(R.id.toolbar_top);
        Util.setaToolbar(this, 0, toolbar, getActivity(), false, 0);

        return view;
    }

    @NonNull
    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        progress.setVisibility(View.VISIBLE);
        return new AddFuncionariosLoader(getContext(), coupon, company);
    }


    private void adicionarCupom() {
        coupon = new Coupon();
        validade = Integer.parseInt(dias.getSelectedItem().toString());
        switch (radioGroup.getCheckedRadioButtonId()) {
            case R.id.valorFixo:
                coupon.setDiscount_type("1");
                break;
            case R.id.porcentagem:
                coupon.setDiscount_type("2");
                break;
            default:
                Toast.makeText(getContext(), R.string.AdicionarCupom_warning_tipo_desconto, Toast.LENGTH_SHORT).show();
                return;
        }
        switch (radioGroup2.getCheckedRadioButtonId()) {
            case R.id.todos:
                coupon.setTarget("1");
                break;
            case R.id.novos:
                coupon.setTarget("2");
                break;
            case R.id.clientes:
                coupon.setTarget("3");
                break;
            default:
                Toast.makeText(getContext(), R.string.AdicionarCupom_warning_target, Toast.LENGTH_SHORT).show();
                return;
        }

        if (radioGroup.getCheckedRadioButtonId() == R.id.valorFixo && (valor.getText().toString().equals("") || valor.getText() == null || Util.unmaskPrice(valor.getText().toString()) < 1)) {
            valorWrap.setErrorEnabled(true);
            valorWrap.setError(getResources().getString(R.string.invalid_value));
            return;
        } else {
            valorWrap.setErrorEnabled(false);
        }

        if (radioGroup.getCheckedRadioButtonId() == R.id.porcentagem && (Util.unmaskPrice(percent.getText().toString()) >= 100 || percent.getText().toString().equals("") || percent.getText() == null || Util.unmaskPrice(percent.getText().toString()) < 1)) {
            percentWrap.setErrorEnabled(true);
            percentWrap.setError(getResources().getString(R.string.invalid_value));
            return;
        } else {
            percentWrap.setErrorEnabled(false);
        }

        if (qtd.getText().toString().equals("") || qtd.getText() == null || Integer.parseInt(qtd.getText().toString()) < 1) {
            qtdWrap.setErrorEnabled(true);
            qtdWrap.setError(getResources().getString(R.string.invalid_quantity));
            return;
        } else {
            qtdWrap.setErrorEnabled(false);
        }
        if (minCompra.isChecked() && (minCompraValor.getText().toString().equals("") || minCompraValor.getText() == null)) {
            minCompraValorWrap.setErrorEnabled(true);
            minCompraValorWrap.setError(getResources().getString(R.string.error_enabled_text));
            return;
        } else {
            minCompraValorWrap.setErrorEnabled(false);
        }

        if (minCompra.isChecked()) {
            coupon.setMin_purchase(Util.unmaskPrice(minCompraValor.getText().toString()));
        }

        if (radioGroup.getCheckedRadioButtonId() == R.id.valorFixo) {
            coupon.setValue(Util.unmaskPrice(valor.getText().toString()));
        } else {
            coupon.setValue(Util.unmaskPrice(percent.getText().toString()));
        }
        coupon.setQuantity(Integer.parseInt(qtd.getText().toString()));
        coupon.setCompany_id(company.getCompany_id());
        coupon.setCoupon_id("");
        coupon.setDue_date(String.valueOf(validade));

        btnAdicionar.setClickable(false);
        loaderManager.restartLoader(0, null, AdicionarCupom.this);
    }


    @Override
    public void onLoadFinished(@NonNull Loader loader, Object data) {
        progress.setVisibility(View.GONE);
        if (data != null) {
            if (data instanceof ErrorCode) {
                Toast.makeText(getContext(), ((ErrorCode) data).getError_message(), Toast.LENGTH_SHORT).show();
            } else {
                Util.backFragmentFunction(this);
            }
        } else {
            Toast.makeText(getContext(), R.string.error, Toast.LENGTH_SHORT).show();
        }
        btnAdicionar.setClickable(true);
        loaderManager.destroyLoader(loader.getId());
    }

    @Override
    public void onLoaderReset(@NonNull Loader loader) {

    }

    @Override
    public void onDialogOKClick(View view, InfoDialogFragment info) {

        if (info.getTag() != null) {
            switch (info.getTag()) {
                case "aviso_porcentagem":
                    switch (view.getId()) {
                        case R.id.btnPositive:
                            info.dismiss();
                            DialogFragment dialogFrag = InfoDialogFragment.newInstance(getResources().getString(R.string.adicionar_cupom_aviso), getResources().getString(R.string.criar_cupom), getResources().getString(R.string.revisar));
                            dialogFrag.show(AdicionarCupom.this.getChildFragmentManager(), "aviso_criar");
                            break;
                        case R.id.btnNegative:
                            info.dismiss();
                            break;
                    }
                    break;
                case "aviso_criar":
                    switch (view.getId()) {
                        case R.id.btnPositive:
                            info.dismiss();
                            adicionarCupom();
                            break;
                        case R.id.btnNegative:
                            info.dismiss();
                            break;
                    }
                    break;
            }
        }

    }


    private static class AddFuncionariosLoader extends AsyncTaskLoader<Object> {

        Context ctx;
        Coupon coupon;
        Company company;

        AddFuncionariosLoader(Context context, Coupon coupon, Company company) {
            super(context);
            ctx = context;
            this.coupon = coupon;
            this.company = company;
        }


        @Override
        protected void onStartLoading() {
            forceLoad();
        }


        @Override
        public Object loadInBackground() {
            Gson gson = new Gson();
            String stringJson = ConnectApi.POST(coupon, ConnectApi.COMPANY_CUPOM_LAT_LON + "/" +company.getCategory_company_id() + "," + company.getMax_radius() + "," + company.getLatitude() + "," + company.getLongitude() +","+company.getModel_type(), getContext());
            JsonParser parser = new JsonParser();
            try {
                return gson.fromJson(parser.parse(stringJson).getAsJsonPrimitive(), Integer.class);
            } catch (Exception e) {
                FirebaseCrashlytics.getInstance().log(UtilShaPre.getDefaultsString(AppConstants.USER_ID, getContext()));
                FirebaseCrashlytics.getInstance().recordException(e);
                try {
                    return gson.fromJson(parser.parse(stringJson).getAsJsonObject().getAsJsonObject(AppConstants.ERROR), ErrorCode.class);
                } catch (Exception e1) {
                    FirebaseCrashlytics.getInstance().log(UtilShaPre.getDefaultsString(AppConstants.USER_ID, getContext()));
                    FirebaseCrashlytics.getInstance().recordException(e1);
                    return null;
                }
            }
        }
    }


}