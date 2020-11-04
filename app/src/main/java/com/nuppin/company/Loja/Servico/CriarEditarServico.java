package com.nuppin.company.Loja.Servico;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.SeekBar;
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

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.gson.Gson;
import com.nuppin.company.Loja.dialogs.InfoDialogFragment;
import com.nuppin.company.Util.AppConstants;
import com.nuppin.company.Util.UtilShaPre;
import com.nuppin.company.connection.ConnectApi;
import com.nuppin.company.model.Company;
import com.nuppin.company.model.Service;
import com.nuppin.company.R;
import com.nuppin.company.Util.MaskEditUtil;
import com.nuppin.company.Util.Util;


public class CriarEditarServico extends Fragment
        implements LoaderManager.LoaderCallbacks, InfoDialogFragment.InfoDialogListener {

    private static final String EDITAR_SERVICE = "EDITAR_SERVICE";
    private static final String NOVO_SERVICE = "NOVO_SERVICE";
    private Service service;
    private LoaderManager loaderManager;
    private TextView nomeServico;
    private TextView descServico;
    private TextView preco, txtValueDuracao;
    private SeekBar duracao;
    private TextInputLayout nomeWrap, precoWrap;
    private boolean editar = false;
    private Company company;
    private CardView progress;
    private MaterialButton btn, btnDeleta;

    public CriarEditarServico() {
    }

    public static CriarEditarServico newInstance(Service service) {
        CriarEditarServico fragment = new CriarEditarServico();
        Bundle args = new Bundle();
        args.putParcelable(EDITAR_SERVICE, service);
        fragment.setArguments(args);
        return fragment;
    }

    public static CriarEditarServico newInstance(Company company) {
        CriarEditarServico fragment = new CriarEditarServico();
        Bundle args = new Bundle();
        args.putParcelable(NOVO_SERVICE, company);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null && getArguments().containsKey(EDITAR_SERVICE)) {
            editar = true;
            service = getArguments().getParcelable(EDITAR_SERVICE);
        }
        if (getArguments() != null && getArguments().containsKey(NOVO_SERVICE)) {
            company = getArguments().getParcelable(NOVO_SERVICE);
        }

        loaderManager = LoaderManager.getInstance(this);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.add_update_service, container, false);

        Toolbar toolbar = view.findViewById(R.id.toolbar_top);
        Util.setaToolbar(this, 0, toolbar, getActivity(), true, R.drawable.ic_clear_white_24dp);


        progress = view.findViewById(R.id.progress);
        nomeServico = view.findViewById(R.id.nome);
        descServico = view.findViewById(R.id.desc);
        preco = view.findViewById(R.id.preco);
        duracao = view.findViewById(R.id.duracao);
        nomeWrap = view.findViewById(R.id.nomeWrap);
        precoWrap = view.findViewById(R.id.precoWrap);
        txtValueDuracao = view.findViewById(R.id.txtValueDuracao);
        preco.addTextChangedListener(MaskEditUtil.monetario((EditText) preco));
        btn = view.findViewById(R.id.botao);
        btnDeleta = view.findViewById(R.id.botaoDeleta);
        duracao.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                int step = i * 10;
                txtValueDuracao.setText(getResources().getString(R.string.criarEditarServico_duracao_min_value, step));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        btnDeleta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment dialogFrag = InfoDialogFragment.newInstance("Confirmar exclusão?", getResources().getString(R.string.confirmar), getResources().getString(R.string.revisar));
                dialogFrag.show(CriarEditarServico.this.getChildFragmentManager(), "confirm");
            }
        });

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validar()) {
                    service.setName(Util.clearName(nomeServico.getText().toString()));
                    service.setDescription(descServico.getText().toString());
                    service.setPreco(Util.unmaskPrice(preco.getText().toString()));
                    service.setDuration(duracao.getProgress() * 10);

                    progress.setVisibility(View.VISIBLE);
                    btn.setClickable(false);
                    btnDeleta.setClickable(false);
                    if (!editar) {
                        loaderManager.restartLoader(0, null, CriarEditarServico.this);
                    } else {
                        loaderManager.restartLoader(1, null, CriarEditarServico.this);
                    }
                }
            }
        });

        if (editar) {
            btnDeleta.setVisibility(View.VISIBLE);
            nomeServico.setText(service.getName());
            descServico.setText(service.getDescription());
            preco.setText(Util.formatDecimaisDoubleToString(service.getPreco()));
            duracao.setProgress(service.getDuration() / 10);
        } else {
            service = new Service();
        }
        return view;
    }

    private boolean validar() {
        if (nomeServico.getText().toString().isEmpty()) {
            nomeWrap.setErrorEnabled(true);
            nomeWrap.setError(getResources().getString(R.string.error_enabled_text));
            return false;
        } else {
            nomeWrap.setErrorEnabled(false);
        }
        if (preco.getText().toString().isEmpty() || preco.getText().toString().equals("0,00")) {
            precoWrap.setErrorEnabled(true);
            precoWrap.setError(getResources().getString(R.string.error_enabled_text));
            return false;
        } else {
            precoWrap.setErrorEnabled(false);
        }
        if (duracao.getProgress() < 1) {
            Toast.makeText(getContext(), R.string.criarEditarServico_duracao_warning, Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    @NonNull
    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        if (id == 0) {
            return new LoaderPP(getActivity(), service, editar, company);
        } else if (id == 1) {
            return new EditaServico(getActivity(), service);
        } else {
            return new DeletaServico(getActivity(), service);
        }
    }

    @Override
    public void onLoadFinished(@NonNull Loader loader, Object data) {
        progress.setVisibility(View.GONE);
        if (data != null) {
            if (data instanceof Service) {
                Util.backFragmentFunction(this);
            } else {
                if (data instanceof Integer && (Integer) data > 0) {
                    service.setCompany_id(null);
                    Util.backFragmentFunction(this);
                } else {
                    Toast.makeText(getContext(), R.string.error, Toast.LENGTH_SHORT).show();
                }
            }
        } else {
            Toast.makeText(getContext(), R.string.error, Toast.LENGTH_SHORT).show();
        }
        loaderManager.destroyLoader(loader.getId());
        btn.setClickable(true);
        btnDeleta.setClickable(true);
    }

    @Override
    public void onLoaderReset(@NonNull Loader loader) {

    }

    @Override
    public void onDialogOKClick(View view, InfoDialogFragment info) {
        switch (view.getId()) {
            case R.id.btnPositive:
                progress.setVisibility(View.VISIBLE);
                btnDeleta.setClickable(false);
                btn.setClickable(false);
                loaderManager.restartLoader(2, null, CriarEditarServico.this);
                info.dismiss();
                break;
            case R.id.btnNegative:
                info.dismiss();
                break;
        }
    }

    private static class LoaderPP extends AsyncTaskLoader<Object> {

        Service service;
        Context ctx;
        boolean edit;
        Company company;

        LoaderPP(Context context, Service service, boolean edit, Company company) {
            super(context);
            ctx = context;
            this.service = service;
            this.edit = edit;
            this.company = company;
        }

        @Override
        protected void onStartLoading() {
            forceLoad();
        }

        @Override
        public Service loadInBackground() {
            Gson gson = new Gson();
            try {
                service.setCompany_id(company.getCompany_id());
                return gson.fromJson(ConnectApi.POST(service, ConnectApi.SERVICE, getContext()), Service.class);
            } catch (Exception e) {
                FirebaseCrashlytics.getInstance().log(UtilShaPre.getDefaultsString(AppConstants.USER_ID, getContext()));
                FirebaseCrashlytics.getInstance().recordException(e);
                return null;
            }
        }
    }

    private static class EditaServico extends AsyncTaskLoader<Object> {

        Service service;
        Context ctx;

        EditaServico(Context context, Service service) {
            super(context);
            ctx = context;
            this.service = service;
        }

        @Override
        protected void onStartLoading() {
            forceLoad();
        }

        @Override
        public Service loadInBackground() {
            Gson gson = new Gson();
            try {
                return gson.fromJson(ConnectApi.PATCH(service, ConnectApi.SERVICE, getContext()), Service.class);
            } catch (Exception e) {
                FirebaseCrashlytics.getInstance().log(UtilShaPre.getDefaultsString(AppConstants.USER_ID, getContext()));
                FirebaseCrashlytics.getInstance().recordException(e);
                return null;
            }
        }
    }

    private static class DeletaServico extends AsyncTaskLoader<Object> {

        Service service;
        Context ctx;

        DeletaServico(Context context, Service service) {
            super(context);
            ctx = context;
            this.service = service;
        }

        @Override
        protected void onStartLoading() {
            forceLoad();
        }

        @Override
        public Integer loadInBackground() {
            Gson gson = new Gson();
            try {
                return gson.fromJson(ConnectApi.DELETE(service, ConnectApi.SERVICE, getContext()), Integer.class);
            } catch (Exception e) {
                FirebaseCrashlytics.getInstance().log(UtilShaPre.getDefaultsString(AppConstants.USER_ID, getContext()));
                FirebaseCrashlytics.getInstance().recordException(e);
                return null;
            }
        }
    }
}