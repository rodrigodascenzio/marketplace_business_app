package com.nuppin.company.pos.servico.scheduling;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
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
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.gson.Gson;
import com.google.gson.JsonParser;
import com.nuppin.company.Loja.dialogs.ManualSchedulingNameDialog;
import com.nuppin.company.R;
import com.nuppin.company.Util.AppConstants;
import com.nuppin.company.Util.Util;
import com.nuppin.company.Util.UtilShaPre;
import com.nuppin.company.connection.ConnectApi;
import com.nuppin.company.model.Company;
import com.nuppin.company.model.ErrorCode;
import com.nuppin.company.model.Scheduling;
import com.nuppin.company.model.Service;
public class AgendamentosFr extends Fragment implements
        LoaderManager.LoaderCallbacks<Object>, ManualSchedulingNameDialog.CustomerName {

    private static final String HORARIOS = "HORARIOS";
    private static final String SERVICOS = "SERVICOS";
    private static final String STORE = "STORE";
    private LoaderManager loaderManager;
    private Scheduling scheduling;
    private Service service;
    private Company company;
    private CardView progress;
    private MaterialButton button;
    private TextView tSubtotal, tTotal, pagamentoEscolhido;
    private MaterialButton alterarMeioPagamento;

    public AgendamentosFr (){}

    public static AgendamentosFr newInstance(Scheduling horario, Service service, Company company) {
        AgendamentosFr fragment = new AgendamentosFr();
        Bundle args = new Bundle();
        args.putParcelable(HORARIOS, horario);
        args.putParcelable(SERVICOS, service);
        args.putParcelable(STORE, company);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null && getArguments().containsKey(HORARIOS)) {
            scheduling = getArguments().getParcelable(HORARIOS);
        }
        if (getArguments() != null && getArguments().containsKey(SERVICOS)) {
            service = getArguments().getParcelable(SERVICOS);
        }
        if (getArguments() != null && getArguments().containsKey(STORE)) {
            company = getArguments().getParcelable(STORE);
            if (company != null) {
                scheduling.setCompany_id(company.getCompany_id());
            }
        }
        loaderManager = LoaderManager.getInstance(this);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fr_scheduling_manual, container, false);

        Toolbar toolbar = view.findViewById(R.id.toolbar_top);
        Util.setaToolbar(this, R.string.scheduling_toolbar, toolbar, getActivity(), false, 0);

        progress = view.findViewById(R.id.progress);
        button = view.findViewById(R.id.btnAgendar);
        TextView dataAge = view.findViewById(R.id.dataAge);
        TextView profissional = view.findViewById(R.id.ageFuncionario);
        TextView servico = view.findViewById(R.id.servico);
        final TextView obs = view.findViewById(R.id.obs);
        TextView preco = view.findViewById(R.id.preco);
        TextView horaInicio = view.findViewById(R.id.ageHoraInicio);
        pagamentoEscolhido = view.findViewById(R.id.meioPagamentoEscolhido);
        tSubtotal = view.findViewById(R.id.resultSubtotal);
        tTotal = view.findViewById(R.id.resultTotal);
        alterarMeioPagamento = view.findViewById(R.id.alterar);

        alterarMeioPagamento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            if (getActivity() instanceof FrtoActivity) {
                FrtoActivity listener = (FrtoActivity) getActivity();
                listener.meioPagamentoManual(scheduling);
            }
            }
        });

        tSubtotal.setText(Util.formaterPrice(service.getPreco()));
        tTotal.setText(Util.formaterPrice(service.getPreco()));

        dataAge.setText(Util.timestampFormatDayMonthYear(scheduling.getStart_date()));
        horaInicio.setText(getResources().getString(R.string.time_with_dots, scheduling.getStart_time(), scheduling.getEnd_time()));
        profissional.setText(scheduling.getEmployee_name());
        servico.setText(service.getName());
        preco.setText(Util.formaterPrice(service.getPreco()));

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                scheduling.setScheduling_id("");
                scheduling.setStatus(AppConstants.STATUS_ACCEPTED);
                scheduling.setType("local");
                scheduling.setSource("nuppin_company");
                scheduling.setLatitude(company.getLatitude());
                scheduling.setLongitude(company.getLongitude());
                scheduling.setCompany_id(service.getCompany_id());
                scheduling.setService_id(service.getService_id());
                scheduling.setService_name(service.getName());
                if (!scheduling.getStart_time().contains(" ")) {
                    scheduling.setStart_time(scheduling.getStart_date() + " " + scheduling.getStart_time());
                    scheduling.setEnd_time(scheduling.getStart_date() + " " + scheduling.getEnd_time());
                }
                scheduling.setCompany_name(company.getName());
                scheduling.setSubtotal_amount(service.getPreco());
                scheduling.setService_duration(service.getDuration());
                scheduling.setAddress(company.getFull_address());
                scheduling.setNote(obs.getText().toString());
                scheduling.setTotal_amount(service.getPreco());
                scheduling.setUser_id(UtilShaPre.getDefaultsString(AppConstants.USER_ID, getActivity()));

                if (scheduling.getPayment_method() == null) {
                    Toast.makeText(getContext(), R.string.selecione_metodo_pagamento, Toast.LENGTH_SHORT).show();
                }else {
                    DialogFragment dialogFrag = ManualSchedulingNameDialog.newInstance(getResources().getString(R.string.confirmar), getResources().getString(R.string.revisar));
                    dialogFrag.show(AgendamentosFr.this.getChildFragmentManager(), "customer");
                }
            }
        });
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (scheduling.getPayment_method() != null && !scheduling.getPayment_method().equals("")) {
            pagamentoEscolhido.setText(scheduling.getPayment_method());
        }
    }

    @NonNull
    @Override
    public Loader<Object> onCreateLoader(int id, Bundle args) {
        return new AgendarLoader(getActivity(), scheduling);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Object> loader, Object data) {
        progress.setVisibility(View.GONE);
        if (data != null) {
            if (data instanceof Scheduling) {
                if (getActivity() != null) {
                    Util.backFragmentFunction(this);
                    Toast.makeText(getContext(), "Agendado com sucesso!", Toast.LENGTH_SHORT).show();
                }
            }else if (data instanceof ErrorCode) {
                Toast.makeText(getContext(), ((ErrorCode) data).getError_message(), Toast.LENGTH_SHORT).show();
            }
        }else {
            Toast.makeText(getActivity(), R.string.error, Toast.LENGTH_SHORT).show();
        }
        loaderManager.destroyLoader(loader.getId());
        button.setClickable(true);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Object> loader) {
    }

    @Override
    public void onDialogOKClick(View view, String value, ManualSchedulingNameDialog infoDialogFragment) {
        switch (view.getId()) {
            case R.id.btnPositive:
                button.setClickable(false);
                scheduling.setUser_name(value);
                loaderManager.restartLoader(0, null, AgendamentosFr.this);
                infoDialogFragment.dismiss();
                break;
            case R.id.btnNegative:
                infoDialogFragment.dismiss();
                break;
        }
    }

    private static class AgendarLoader extends AsyncTaskLoader<Object> {

        Context ctx;
        Scheduling scheduling;


        AgendarLoader(Context context, Scheduling scheduling) {
            super(context);
            ctx = context;
            this.scheduling= scheduling;
        }


        @Override
        protected void onStartLoading() {
            forceLoad();
        }


        @Override
        public Object loadInBackground() {
            Gson gson = new Gson();
            JsonParser parser = new JsonParser();

            String json = "";
            try {
                json = ConnectApi.POST(scheduling, ConnectApi.AGENDAR, getContext());
                return gson.fromJson(parser.parse(json).getAsJsonObject().getAsJsonObject(AppConstants.SCHEDULING), Scheduling.class);
            } catch (Exception e) {
                    FirebaseCrashlytics.getInstance().log(UtilShaPre.getDefaultsString(AppConstants.USER_ID, getContext()));
                    FirebaseCrashlytics.getInstance().recordException(e);
                try {
                    return gson.fromJson(parser.parse(json).getAsJsonObject().getAsJsonObject(AppConstants.ERROR), ErrorCode.class);
                } catch (Exception e1) {
                    FirebaseCrashlytics.getInstance().log(UtilShaPre.getDefaultsString(AppConstants.USER_ID, getContext()));
                    FirebaseCrashlytics.getInstance().recordException(e1);
                    return null;
                }            }
        }
    }

    public interface FrtoActivity {
        void meioPagamentoManual(Scheduling scheduling);
    }
}