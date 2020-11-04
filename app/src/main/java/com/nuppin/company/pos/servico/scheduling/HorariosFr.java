package com.nuppin.company.pos.servico.scheduling;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.AsyncTaskLoader;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.nuppin.company.R;
import com.nuppin.company.Util.AppConstants;
import com.nuppin.company.Util.DatePickerFragment;
import com.nuppin.company.Util.Util;
import com.nuppin.company.Util.UtilShaPre;
import com.nuppin.company.connection.ConnectApi;
import com.nuppin.company.model.Company;
import com.nuppin.company.model.Scheduling;
import com.nuppin.company.model.Service;
import com.nuppin.company.model.ServiceEmployee;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class HorariosFr extends Fragment
        implements LoaderManager.LoaderCallbacks<List<ServiceEmployee>>,
        HorariosHeaderAdapter.HorariosAdapterListener,
        DatePickerDialog.OnDateSetListener {

    private static final String SERVICOS = "SERVICOS";
    private static final String STORE = "STORE";
    private RecyclerView mRecyclerView;
    private HorariosHeaderAdapter adapter;
    private LoaderManager loaderManager;
    private Service service;
    private Company company;
    private TextView data;
    private int dayofmonth, month, year;
    private String datatimestamp;
    private LottieAnimationView dots;
    private LinearLayout linearEmpty;
    private ConstraintLayout errorLayout;
    private FloatingActionButton fabError;
    private MaterialButton changeDate;
    private TextView descricao, preco, nome;


    public HorariosFr() {
    }

    public static HorariosFr newInstance(Service service, Company company) {
        HorariosFr fragment = new HorariosFr();
        Bundle args = new Bundle();
        args.putParcelable(SERVICOS, service);
        args.putParcelable(STORE, company);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null && getArguments().containsKey(SERVICOS)) {
            service = getArguments().getParcelable(SERVICOS);
        }
        if (getArguments() != null && getArguments().containsKey(STORE)) {
            company = getArguments().getParcelable(STORE);
        }
        loaderManager = LoaderManager.getInstance(this);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fr_employee_manual, container, false);

        Toolbar toolbar = view.findViewById(R.id.toolbar_top);
        Util.setaToolbar(this, R.string.horarios, toolbar, getActivity(), false, 0);

        mRecyclerView = view.findViewById(R.id.recyclerview_scheduling);
        changeDate = view.findViewById(R.id.changeDate);

        dots = view.findViewById(R.id.dots);
        linearEmpty = view.findViewById(R.id.linearEmpty);
        errorLayout = view.findViewById(R.id.error_layout);
        fabError = view.findViewById(R.id.fabError);
        fabError.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dots.setVisibility(View.VISIBLE);
                errorLayout.setVisibility(View.GONE);
                loaderManager.restartLoader(0, null, HorariosFr.this);
            }
        });

        Calendar cal = Calendar.getInstance();
        if (dayofmonth == 0) {
            month = cal.get(Calendar.MONTH)+1;
            dayofmonth = cal.get(Calendar.DAY_OF_MONTH);
            year = cal.get(Calendar.YEAR);
        }

        data = view.findViewById(R.id.data);
        descricao = view.findViewById(R.id.desc);
        preco = view.findViewById(R.id.preco);
        nome = view.findViewById(R.id.nome);
        preco.setText(Util.formaterPrice(service.getPreco()));
        nome.setText(service.getName());
        if (service.getDescription() != null && service.getDescription().trim().equals("")) {
            descricao.setText(getString(R.string.sem_descricao));
        } else {
            descricao.setText(service.getDescription());
        }

        data.setText(Util.zeroToCalendar(dayofmonth, month, year));
        datatimestamp = Util.zeroToCalendarToMysql(dayofmonth, month, year);

        changeDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment timePicker = DatePickerFragment.newInstance(dayofmonth, month-1, year);
                timePicker.show(getChildFragmentManager(), "calendar");
            }
        });

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);
        adapter = new HorariosHeaderAdapter(this);
        mRecyclerView.setAdapter(adapter);
        loaderManager.restartLoader(0, null, this);
        return view;
    }

    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
        this.dayofmonth = day; this.month = month + 1; this.year = year;
        mRecyclerView.setVisibility(View.GONE);
        dots.setVisibility(View.VISIBLE);
        data.setText(Util.zeroToCalendar(day, (month + 1), year));
        datatimestamp = Util.zeroToCalendarToMysql(day, (month + 1), year);
        loaderManager.restartLoader(0, null, this);
    }

    @NonNull
    @Override
    public Loader<List<ServiceEmployee>> onCreateLoader(int id, Bundle args) {
        return new AgendamentosLoader(
                getActivity(),
                service.getService_id(),
                service.getCompany_id(), datatimestamp);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<List<ServiceEmployee>> loader, List<ServiceEmployee> data) {
        dots.setVisibility(View.GONE);
        if (data != null) {
            if (data.size() > 0) {
                adapter.setHorarios(data, service);
                mRecyclerView.setVisibility(View.VISIBLE);
                linearEmpty.setVisibility(View.GONE);
            } else {
                mRecyclerView.setVisibility(View.GONE);
                linearEmpty.setVisibility(View.VISIBLE);
            }
            errorLayout.setVisibility(View.GONE);
        } else {
            errorLayout.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<List<ServiceEmployee>> loader) {

    }

    @Override
    public void onClickHorario(Scheduling horario, Service service, Company companyNull) {
        Activity activity = getActivity();
        if (activity instanceof HorariosHeaderAdapter.HorariosAdapterListener) {
            HorariosHeaderAdapter.HorariosAdapterListener listener = (HorariosHeaderAdapter.HorariosAdapterListener) activity;
            listener.onClickHorario(horario, service, company);
        }
    }

    private static class AgendamentosLoader extends AsyncTaskLoader<List<ServiceEmployee>> {

        Context ctx;
        String servId;
        String stoId;
        String data;

        AgendamentosLoader(Context context, String servId, String stoId, String data) {
            super(context);
            ctx = context;
            this.servId = servId;
            this.stoId = stoId;
            this.data = data;
        }


        @Override
        protected void onStartLoading() {
            forceLoad();
        }


        @Override
        public List<ServiceEmployee> loadInBackground() {
            Gson gson = new Gson();
            String stringJson = ConnectApi.GET(
                    ConnectApi.HORARIOS + "/" +
                            servId + "," +
                            data + "," +
                            Util.returnStringLatLonCountr(getContext()), getContext());
            try {
                JsonParser parser = new JsonParser();
                JsonObject jObj = parser.parse(stringJson).getAsJsonObject();
                ServiceEmployee[] horarios = gson.fromJson(jObj.getAsJsonArray(AppConstants.EMPLOYEE), ServiceEmployee[].class);
                return new ArrayList<>(Arrays.asList(horarios));
            } catch (Exception e) {
                FirebaseCrashlytics.getInstance().log(UtilShaPre.getDefaultsString(AppConstants.USER_ID, getContext()));
                FirebaseCrashlytics.getInstance().recordException(e);
                return null;
            }
        }
    }
}