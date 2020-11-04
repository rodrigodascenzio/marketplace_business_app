package com.nuppin.company.Loja.Financeiro;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.gson.Gson;
import com.nuppin.company.Util.AppConstants;
import com.nuppin.company.Util.UtilShaPre;
import com.nuppin.company.connection.ConnectApi;
import com.nuppin.company.model.Finance;
import com.nuppin.company.model.Company;
import com.nuppin.company.R;
import com.nuppin.company.Util.DatePickerFragment;
import com.nuppin.company.Util.ImageCompress;
import com.nuppin.company.Util.MaskEditUtil;
import com.nuppin.company.Util.RealPathUtil;
import com.nuppin.company.Util.Util;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static android.app.Activity.RESULT_OK;

public class FrCriarEditarCashFlow extends Fragment implements
        LoaderManager.LoaderCallbacks<Map<String, Object>>,
        View.OnClickListener,
        DatePickerDialog.OnDateSetListener {

    private static final String EDITAR_MOVIMENTACAO = "EDITAR";
    private static final String NOVA_MOVIMENTACAO = "NOVO";
    private static final int FOTO_COMPRESS_OTHER_THREAD = 99;
    private Finance finance;
    private LoaderManager loaderManager;
    private EditText titulo, descricao, valor;
    private TextView data;
    private int dayofmonth, month, year;
    private TextInputLayout tituloWrap, descWrap, valorWrap, dataWrap;
    private SimpleDraweeView imageCadastro;
    private String mfotoPath;
    private CardView progress;
    private boolean isFotoPath;
    private static final int REQUEST_FOTO = 1;
    private boolean editar = false;
    private Company company;
    private MaterialButton btn, btnDeleta;
    private RadioGroup radioGroup;
    private RadioButton despesa, receita;
    private byte[] imageCompressed;

    public FrCriarEditarCashFlow() {
    }

    public static FrCriarEditarCashFlow newInstance(Finance finance) {
        FrCriarEditarCashFlow fragment = new FrCriarEditarCashFlow();
        Bundle args = new Bundle();
        args.putParcelable(EDITAR_MOVIMENTACAO, finance);
        fragment.setArguments(args);
        return fragment;
    }

    public static FrCriarEditarCashFlow newInstance2(Company company) {
        FrCriarEditarCashFlow fragment = new FrCriarEditarCashFlow();
        Bundle args = new Bundle();
        args.putParcelable(NOVA_MOVIMENTACAO, company);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null && getArguments().containsKey(EDITAR_MOVIMENTACAO)) {
            editar = true;
            finance = getArguments().getParcelable(EDITAR_MOVIMENTACAO);
        }
        if (getArguments() != null && getArguments().containsKey(NOVA_MOVIMENTACAO)) {
            company = getArguments().getParcelable(NOVA_MOVIMENTACAO);
        }
        loaderManager = LoaderManager.getInstance(this);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.add_update_cashflow, container, false);
        titulo = view.findViewById(R.id.nome);
        tituloWrap = view.findViewById(R.id.nomeWrap);
        descricao = view.findViewById(R.id.desc);
        descWrap = view.findViewById(R.id.descWrap);
        valor = view.findViewById(R.id.preco);
        valorWrap = view.findViewById(R.id.precoWrap);
        data = view.findViewById(R.id.data);
        dataWrap = view.findViewById(R.id.dataWrap);
        valor.addTextChangedListener(MaskEditUtil.monetario(valor));
        imageCadastro = view.findViewById(R.id.imageCadastro);
        imageCadastro.setOnClickListener(this);
        btn = view.findViewById(R.id.botao);
        progress = view.findViewById(R.id.progressCard);
        radioGroup = view.findViewById(R.id.radioGroup);
        despesa = view.findViewById(R.id.despesa);
        receita = view.findViewById(R.id.receita);
        Toolbar toolbar = view.findViewById(R.id.toolbar_top);
        btnDeleta = view.findViewById(R.id.botaoDeleta);

        btnDeleta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progress.setVisibility(View.VISIBLE);
                btnDeleta.setClickable(false);
                btn.setClickable(false);
                loaderManager.restartLoader(2, null, FrCriarEditarCashFlow.this);
            }
        });

        final TextView anexo = view.findViewById(R.id.txtAnexo);
        anexo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (imageCadastro.getVisibility() == View.GONE) {
                    anexo.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.ic_keyboard_arrow_up_black_24dp), null);
                    imageCadastro.setVisibility(View.VISIBLE);
                } else {
                    anexo.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.ic_keyboard_arrow_down_black_24dp), null);
                    imageCadastro.setVisibility(View.GONE);
                }
            }
        });

        Util.setaToolbar(this, 0, toolbar, getActivity(), true, R.drawable.ic_clear_white_24dp);

        Calendar cal = Calendar.getInstance();
        if (dayofmonth == 0) {
            month = cal.get(Calendar.MONTH)+1;
            dayofmonth = cal.get(Calendar.DAY_OF_MONTH);
            year = cal.get(Calendar.YEAR);
        }

        data.setText(Util.zeroToCalendar(dayofmonth, month, year));

        data.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment timePicker = DatePickerFragment.newInstance(dayofmonth, month-1, year);
                timePicker.show(getChildFragmentManager(), "calendar");
            }
        });

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validar()) {
                    switch (radioGroup.getCheckedRadioButtonId()) {
                        case R.id.despesa:
                            finance.setAmount(Util.unmaskPriceToNegative(valor.getText().toString()));
                            break;
                        case R.id.receita:
                            finance.setAmount(Util.unmaskPrice(valor.getText().toString()));
                            break;
                        default:
                            Toast.makeText(getContext(), R.string.warning_financeiro_cadastro, Toast.LENGTH_SHORT).show();
                            return;
                    }
                    btn.setClickable(false);
                    btnDeleta.setClickable(false);
                    finance.setTitle(Util.clearName(titulo.getText().toString()));
                    finance.setDescription(descricao.getText().toString());
                    finance.setReference_date(Util.zeroToCalendarToMysql(dayofmonth, month, year));
                    if (!editar) {
                        loaderManager.restartLoader(0, null, FrCriarEditarCashFlow.this);
                    } else {
                        loaderManager.restartLoader(1, null, FrCriarEditarCashFlow.this);
                    }
                }
            }
        });


        if (editar) {
            btnDeleta.setVisibility(View.VISIBLE);
            titulo.setText(finance.getTitle());
            descricao.setText(finance.getDescription());
            valor.setText(Util.formatDecimaisDoubleToString(finance.getAmount()));
            Util.hasPhoto(finance, imageCadastro);
            if (finance.getAmount() < 0) {
                despesa.setChecked(true);
            } else {
                receita.setChecked(true);
            }
            data.setText(Util.timestampFormatDayMonthYear(finance.getReference_date()));
        } else {
            finance = new Finance();
        }
        return view;
    }

    private boolean validar() {
        boolean b = true;
        if (titulo.getText().toString().isEmpty()) {
            tituloWrap.setErrorEnabled(true);
            tituloWrap.setError(getResources().getString(R.string.error_enabled_text));
            b = false;
        } else {
            tituloWrap.setErrorEnabled(false);
        }
        if (valor.getText().toString().isEmpty() || valor.getText().toString().equals("0,00")) {
            valorWrap.setErrorEnabled(true);
            valorWrap.setError(getResources().getString(R.string.error_enabled_text));
            b = false;
        } else {
            valorWrap.setErrorEnabled(false);
        }
        if (data.getText().toString().isEmpty()) {
            Toast.makeText(getContext(), R.string.warning_select_date_financeiro, Toast.LENGTH_SHORT).show();
            b = false;
        }
        return b;
    }

    //RETORNO DO ARQUIVO ESCOLHIDO NA GALERIA
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == REQUEST_FOTO) {
            btn.setClickable(false);
            imageCadastro.setImageURI(data.getData());
            mfotoPath = RealPathUtil.getRealPath(getContext(), data.getData());
            isFotoPath = true;
            loaderManager.restartLoader(FOTO_COMPRESS_OTHER_THREAD, null, this);
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

    @NonNull
    @Override
    public Loader<Map<String, Object>> onCreateLoader(int id, Bundle args) {
        progress.setVisibility(View.VISIBLE);
        if (id == 0) {
            return new CriaCashFlow(getActivity(), finance, editar, company.getCompany_id(), mfotoPath, imageCompressed);
        } else if (id == 1) {
            return new EditaCashFlow(getActivity(), finance, mfotoPath, isFotoPath, imageCompressed);
        } else if (id == 2) {
            return new DeletaCashFlow(getActivity(), finance);
        } else {
            return new CompressImageThread(getContext(), mfotoPath);
        }
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Map<String, Object>> loader, Map<String, Object> data) {
        progress.setVisibility(View.GONE);
        loaderManager.destroyLoader(loader.getId());
        if (data != null) {
            if (loader.getId() != FOTO_COMPRESS_OTHER_THREAD) {
                if (data.get(AppConstants.FINANCE) instanceof Finance) {
                    if (!(data.get("photo") instanceof String)) {
                        Toast.makeText(getContext(), R.string.error_photo, Toast.LENGTH_SHORT).show();
                    }
                    Util.backFragmentFunction(this);
                } else if (data.get(AppConstants.FINANCE) instanceof Integer) {
                    Util.backFragmentFunction(this);
                } else if (data.get("delete") instanceof Integer) {
                    if ((Integer) data.get("delete") > 0) {
                        finance.setTitle(null);
                        Util.backFragmentFunction(this);
                    } else {
                        Toast.makeText(getContext(), R.string.error, Toast.LENGTH_SHORT).show();
                    }
                }
            } else {
                if (data.get("compress") != null && data.get("compress") instanceof byte[]) {
                    imageCompressed = (byte[]) data.get("compress");
                } else {
                    Toast.makeText(getContext(), R.string.erro_compress_photo, Toast.LENGTH_SHORT).show();
                }
            }
        } else {
            Toast.makeText(getContext(), R.string.error, Toast.LENGTH_SHORT).show();
        }
        loaderManager.destroyLoader(loader.getId());
        btnDeleta.setClickable(true);
        btn.setClickable(true);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Map<String, Object>> loader) {

    }

    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
        this.dayofmonth = day; this.month = month+1; this.year = year;
        data.setText(Util.zeroToCalendar(day, (month + 1), year));
        finance.setReference_date(Util.zeroToCalendarToMysql(day, (month + 1), year));
    }

    private static class CriaCashFlow extends AsyncTaskLoader<Map<String, Object>> {

        Finance finance;
        Context ctx;
        boolean edit;
        String idLoja;
        String fotoPath;
        byte[] imageCompressed;


        CriaCashFlow(Context context, Finance cashflow, boolean edit, String idLoja, String mFotoPath, byte[] imageCompressed) {
            super(context);
            ctx = context;
            this.finance = cashflow;
            this.edit = edit;
            this.fotoPath = mFotoPath;
            this.idLoja = idLoja;
            this.imageCompressed = imageCompressed;
        }

        @Override
        protected void onStartLoading() {
            forceLoad();
        }

        @Override
        public Map<String, Object> loadInBackground() {
            Gson gson = new Gson();
            finance.setCompany_id(idLoja);
            Map<String, Object> mapOrdPro;
            mapOrdPro = new HashMap<>();
            try {
                Finance c = gson.fromJson(ConnectApi.POST(finance, ConnectApi.CASH_FLOW, getContext()), Finance.class);
                mapOrdPro.put(AppConstants.FINANCE, c);
                try {
                    if (fotoPath != null) {
                        String ok = gson.fromJson(ConnectApi.enviarFoto(getContext(), fotoPath, c.getFinance_id(), AppConstants.FINANCE, imageCompressed), String.class);
                        mapOrdPro.put("photo", ok);
                    } else {
                        mapOrdPro.put("photo", "empty");
                    }
                } catch (Exception e) {
                    FirebaseCrashlytics.getInstance().log(UtilShaPre.getDefaultsString(AppConstants.USER_ID, getContext()));
                FirebaseCrashlytics.getInstance().recordException(e);
                    mapOrdPro.put("photo", null);
                }
            } catch (Exception e) {
                FirebaseCrashlytics.getInstance().log(UtilShaPre.getDefaultsString(AppConstants.USER_ID, getContext()));
                FirebaseCrashlytics.getInstance().recordException(e);
                return null;
            }

            return mapOrdPro;
        }
    }

    private static class EditaCashFlow extends AsyncTaskLoader<Map<String, Object>> {

        Finance finance;
        Context ctx;
        String fotoPath;
        boolean isFotoPath;
        byte[] imageCompressed;

        EditaCashFlow(Context context, Finance finance, String mFotoPath, boolean isFotoPath, byte[] imageCompressed) {
            super(context);
            ctx = context;
            this.finance = finance;
            this.fotoPath = mFotoPath;
            this.isFotoPath = isFotoPath;
            this.imageCompressed = imageCompressed;
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
                Integer affectedRows = gson.fromJson(ConnectApi.PATCH(finance, ConnectApi.CASH_FLOW, getContext()), Integer.class);
                mapOrdPro.put(AppConstants.FINANCE, affectedRows);
                try {
                    if (isFotoPath) {
                        String ok = gson.fromJson(ConnectApi.enviarFoto(getContext(), fotoPath, finance.getFinance_id(), AppConstants.FINANCE, imageCompressed), String.class);
                        mapOrdPro.put("photo", ok);
                    } else {
                        mapOrdPro.put("photo", "empty");
                    }
                } catch (Exception e) {
                    FirebaseCrashlytics.getInstance().log(UtilShaPre.getDefaultsString(AppConstants.USER_ID, getContext()));
                FirebaseCrashlytics.getInstance().recordException(e);
                    mapOrdPro.put("photo", null);
                }
            } catch (Exception e) {
                FirebaseCrashlytics.getInstance().log(UtilShaPre.getDefaultsString(AppConstants.USER_ID, getContext()));
                FirebaseCrashlytics.getInstance().recordException(e);
                return null;
            }

            return mapOrdPro;
        }
    }

    private static class DeletaCashFlow extends AsyncTaskLoader<Map<String, Object>> {

        Finance finance;
        Context ctx;

        DeletaCashFlow(Context context, Finance finance) {
            super(context);
            ctx = context;
            this.finance = finance;
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
                Gson gson = new Gson();
                mapOrdPro.put("delete", gson.fromJson(ConnectApi.DELETE(finance, ConnectApi.CASH_FLOW, getContext()), Integer.class));
                return mapOrdPro;
            } catch (Exception e) {
                FirebaseCrashlytics.getInstance().log(UtilShaPre.getDefaultsString(AppConstants.USER_ID, getContext()));
                FirebaseCrashlytics.getInstance().recordException(e);
                return null;
            }
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
}