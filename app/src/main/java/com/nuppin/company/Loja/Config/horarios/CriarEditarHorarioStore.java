package com.nuppin.company.Loja.Config.horarios;

import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.TimePicker;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.gson.Gson;
import com.nuppin.company.Util.AppConstants;
import com.nuppin.company.Util.UtilShaPre;
import com.nuppin.company.connection.ConnectApi;
import com.nuppin.company.model.Company;
import com.nuppin.company.model.OpeningHours;
import com.nuppin.company.R;
import com.nuppin.company.Util.TimerPickerFragment;
import com.nuppin.company.Util.Util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class CriarEditarHorarioStore extends Fragment implements
        LoaderManager.LoaderCallbacks,
        CriarEditarHorarioStoreRv.diaSemanaOnClickListener,
        TimePickerDialog.OnTimeSetListener {

    private static final String EDITAR_HORARIO = "EDITAR_HORARIO";
    private static final String NOVO_HORARIO = "NOVO_HORARIO";
    private static final String COMPANY = "COMPANY";
    private static final String INICIO = "INICIO";
    private static final String TERMINO = "TERMINO";
    private OpeningHours horario;
    private LoaderManager loaderManager;
    private TextView inicio;
    private TextView termino;
    private boolean editar = false;
    private Company company;
    private CriarEditarHorarioStoreRv adapter;
    private List<OpeningHours> diasSelected;
    private String TAG;
    private int inicioInt, terminoInt;
    private LottieAnimationView dots;
    private ConstraintLayout errorLayout;
    private FloatingActionButton fabError;
    private RecyclerView mRecyclerView;
    private CardView progress;
    private MaterialButton btn, btnDeleta;

    public CriarEditarHorarioStore() {
    }

    public static CriarEditarHorarioStore newInstance(OpeningHours horario, Company company) {
        CriarEditarHorarioStore fragment = new CriarEditarHorarioStore();
        Bundle args = new Bundle();
        args.putParcelable(EDITAR_HORARIO, horario);
        args.putParcelable(COMPANY, company);
        fragment.setArguments(args);
        return fragment;
    }

    public static CriarEditarHorarioStore newInstance(Company company) {
        CriarEditarHorarioStore fragment = new CriarEditarHorarioStore();
        Bundle args = new Bundle();
        args.putParcelable(NOVO_HORARIO, company);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null && getArguments().containsKey(EDITAR_HORARIO)) {
            editar = true;
            horario = getArguments().getParcelable(EDITAR_HORARIO);
            company = getArguments().getParcelable(COMPANY);
        }
        if (getArguments() != null && getArguments().containsKey(NOVO_HORARIO)) {
            company = getArguments().getParcelable(NOVO_HORARIO);
        }

        loaderManager = LoaderManager.getInstance(this);
        diasSelected = new ArrayList<>();
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.add_update_company_schedule, container, false);

        Toolbar toolbar = view.findViewById(R.id.toolbar_top);
        Util.setaToolbar(this, 0, toolbar, getActivity(), true, R.drawable.ic_clear_white_24dp);

        dots = view.findViewById(R.id.dots);
        errorLayout = view.findViewById(R.id.error_layout);
        fabError = view.findViewById(R.id.fabError);
        fabError.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dots.setVisibility(View.VISIBLE);
                errorLayout.setVisibility(View.GONE);
                loaderManager.restartLoader(0, null, CriarEditarHorarioStore.this);
            }
        });
        progress = view.findViewById(R.id.progress);

        inicio = view.findViewById(R.id.inicio);
        inicio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment timePicker = new TimerPickerFragment();
                TAG = INICIO;
                timePicker.show(getChildFragmentManager(), TAG);
            }
        });
        termino = view.findViewById(R.id.termino);
        termino.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment timePicker = new TimerPickerFragment();
                TAG = TERMINO;
                timePicker.show(getChildFragmentManager(), TAG);
            }
        });


        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false);
        mRecyclerView = view.findViewById(R.id.dias);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        adapter = new CriarEditarHorarioStoreRv(this);
        mRecyclerView.setAdapter(adapter);


        btn = view.findViewById(R.id.botao);
        btnDeleta = view.findViewById(R.id.botaoDeleta);


        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (inicio.getText().toString().equals("") || termino.getText().toString().equals("")) {
                    Toast.makeText(getContext(), R.string.warning_about_time, Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!editar) {
                    if (diasSelected.size() < 1) {
                        Toast.makeText(getContext(), R.string.warning_about_days, Toast.LENGTH_SHORT).show();
                        adapter.notifyDataSetChanged();
                        return;
                    }

                    if (termino.getText().toString().equals(inicio.getText().toString())) {
                        Toast.makeText(getContext(), R.string.categoria_cadastro_horario_igual_warning, Toast.LENGTH_SHORT).show();
                        return;
                    }

                    for (int i = 0; i < diasSelected.size(); i++) {
                        diasSelected.get(i).setStart_time(inicio.getText().toString());
                        diasSelected.get(i).setEnd_time(termino.getText().toString());
                        diasSelected.get(i).setCompany_id(company.getCompany_id());
                        if (setaDiaTermino(diasSelected.get(i)).getWeekday_end_id() == null) {
                            Toast.makeText(getContext(), R.string.error, Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                    btn.setClickable(false);
                    progress.setVisibility(View.VISIBLE);
                    loaderManager.initLoader(1, null, CriarEditarHorarioStore.this);
                } else {
                    horario.setStart_time(inicio.getText().toString());
                    horario.setEnd_time(termino.getText().toString());
                    if (horario.getWeekday_end_id() == null) {
                        Toast.makeText(getContext(), R.string.error, Toast.LENGTH_SHORT).show();
                        return;
                    }
                    btn.setClickable(false);
                    progress.setVisibility(View.VISIBLE);
                    loaderManager.initLoader(2, null, CriarEditarHorarioStore.this);
                }
            }
        });

        btnDeleta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progress.setVisibility(View.VISIBLE);
                btnDeleta.setClickable(false);
                loaderManager.restartLoader(3, null, CriarEditarHorarioStore.this);
            }
        });

        if (editar) {
            btn.setText(R.string.btn_update);
            CardView cardView = view.findViewById(R.id.cardCheck);
            cardView.setVisibility(View.GONE);
            inicio.setText(horario.getStart_time());
            termino.setText(horario.getEnd_time());
            inicioInt = Integer.parseInt(Util.clearNotNumber(horario.getStart_time()));
            terminoInt = Integer.parseInt(Util.clearNotNumber(horario.getEnd_time()));
            btnDeleta.setVisibility(View.VISIBLE);
        } else {
            btn.setText(R.string.btn_save);
            loaderManager.restartLoader(0, null, this);
        }
        return view;
    }

    @NonNull
    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        if (id == 1) {
            return new SalvaHorario(getActivity(), diasSelected, editar, company);
        } else if (id == 2) {
            return new EditaHorario(getActivity(), horario);
        } else if (id == 3) {
            return new DeletaHorario(getActivity(), horario);
        } else {
            return new LoaderHorariosStore(Objects.requireNonNull(getActivity()), company.getCompany_id());
        }
    }

    @Override
    public void onLoadFinished(@NonNull Loader loader, Object data) {
        dots.setVisibility(View.GONE);
        progress.setVisibility(View.GONE);
        if (data != null) {
            if (loader.getId() == 1) {
                Util.backFragmentFunction(this);
            } else if (loader.getId() == 2) {
                Util.backFragmentFunction(this);
            } else if (loader.getId() == 3) {
                Util.backFragmentFunction(this);
            } else {
                mRecyclerView.setVisibility(View.VISIBLE);
                adapter.setDiasSemana((List) data);
            }
            errorLayout.setVisibility(View.GONE);
        } else {
            mRecyclerView.setVisibility(View.GONE);
            errorLayout.setVisibility(View.VISIBLE);
        }
        if (loader.getId() != 0) {
            loaderManager.destroyLoader(loader.getId());
        }
        btn.setClickable(true);
        btnDeleta.setClickable(true);
        loaderManager.destroyLoader(loader.getId());
    }

    @Override
    public void onLoaderReset(@NonNull Loader loader) {

    }

    @Override
    public void onClickDiaSemana(List<OpeningHours> diasSelected) {
        this.diasSelected = diasSelected;
    }

    @Override
    public void onTimeSet(TimePicker timePicker, int hour, int minute) {
        if (company.getCategory_company_id().equals("3")) {
            if (TAG.equals(INICIO)) {
                if (!termino.getText().toString().equals("")) {
                    if ((Integer.parseInt(Util.clearNotNumber(termino.getText().toString())) - Integer.parseInt(Util.clearNotNumber(Util.zeroToTime(minute, hour)))) < 0) {
                        Toast.makeText(getContext(), R.string.categoria_cadastro_horario_warning, Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
            } else {
                if (!inicio.getText().toString().equals("")) {
                    if ((Integer.parseInt(Util.clearNotNumber(Util.zeroToTime(minute, hour)))) - Integer.parseInt(Util.clearNotNumber(inicio.getText().toString())) < 0) {
                        Toast.makeText(getContext(), R.string.categoria_cadastro_horario_warning, Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
            }
        }

        if (TAG.equals(INICIO)) {
            inicio.setText(Util.zeroToTime(minute, hour));
            inicioInt = Integer.parseInt(Util.clearNotNumber(inicio.getText().toString()));
            adapter.setInicio(inicio.getText().toString(), inicioInt);
            if (editar) {
                setaDiaTermino(horario);
            }
        } else {
            termino.setText(Util.zeroToTime(minute, hour));
            terminoInt = Integer.parseInt(Util.clearNotNumber(termino.getText().toString()));
            adapter.setTermino(termino.getText().toString(), terminoInt);
            if (editar) {
                setaDiaTermino(horario);
            }
        }

    }

    private OpeningHours setaDiaTermino(OpeningHours dia) {
        if (terminoInt < inicioInt) {
            if (dia.getWeekday_id().equals("7")) {
                dia.setWeekday_end_id(String.valueOf(1));
            } else {
                dia.setWeekday_end_id(String.valueOf(Integer.parseInt(dia.getWeekday_id()) + 1));
            }
        } else {
            dia.setWeekday_end_id(dia.getWeekday_id());
        }
        return dia;
    }

    private static class LoaderHorariosStore extends AsyncTaskLoader {

        String storeId;
        Context ctx;

        LoaderHorariosStore(@NonNull Context context, String storeId) {
            super(context);
            this.storeId = storeId;
            this.ctx = context;
        }

        @Override
        protected void onStartLoading() {
            super.onStartLoading();
            forceLoad();
        }

        @Nullable
        @Override
        public Object loadInBackground() {
            try {
                Gson gson = new Gson();
                OpeningHours[] horarios = gson.fromJson(ConnectApi.GET(ConnectApi.COMPANY_UNDEFINED_SCHEDULE + "/" + storeId, getContext()), OpeningHours[].class);
                return new ArrayList<>(Arrays.asList(horarios));
            } catch (Exception e) {
                FirebaseCrashlytics.getInstance().log(UtilShaPre.getDefaultsString(AppConstants.USER_ID, getContext()));
                FirebaseCrashlytics.getInstance().recordException(e);
                return null;
            }
        }
    }

    private static class SalvaHorario extends AsyncTaskLoader {

        Context ctx;
        boolean edit;
        Company company;
        List<OpeningHours> horario;

        SalvaHorario(Context context, List<OpeningHours> horario, boolean edit, Company company) {
            super(context);
            ctx = context;
            this.horario = horario;
            this.edit = edit;
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
                return gson.fromJson(ConnectApi.POST(horario, ConnectApi.COMPANY_SCHEDULE, getContext()), OpeningHours[].class);
            } catch (Exception e) {
                FirebaseCrashlytics.getInstance().log(UtilShaPre.getDefaultsString(AppConstants.USER_ID, getContext()));
                FirebaseCrashlytics.getInstance().recordException(e);
                return null;
            }
        }
    }

    private static class EditaHorario extends AsyncTaskLoader {

        OpeningHours horario;
        Context ctx;

        EditaHorario(Context context, OpeningHours horario) {
            super(context);
            ctx = context;
            this.horario = horario;
        }

        @Override
        protected void onStartLoading() {
            forceLoad();
        }

        @Override
        public Object loadInBackground() {
            try {
                Gson gson = new Gson();
                return gson.fromJson(ConnectApi.PATCH(horario, ConnectApi.COMPANY_SCHEDULE, getContext()), Integer.class);
            } catch (Exception e) {
                FirebaseCrashlytics.getInstance().log(UtilShaPre.getDefaultsString(AppConstants.USER_ID, getContext()));
                FirebaseCrashlytics.getInstance().recordException(e);
                return true;
            }
        }
    }

    private static class DeletaHorario extends AsyncTaskLoader {

        OpeningHours horario;
        Context ctx;

        DeletaHorario(Context context, OpeningHours horario) {
            super(context);
            ctx = context;
            this.horario = horario;
        }

        @Override
        protected void onStartLoading() {
            forceLoad();
        }

        @Override
        public Object loadInBackground() {
            try {
                Gson gson = new Gson();
                return gson.fromJson(ConnectApi.DELETE(horario, ConnectApi.COMPANY_SCHEDULE, getContext()), Integer.class);
            } catch (Exception e) {
                FirebaseCrashlytics.getInstance().log(UtilShaPre.getDefaultsString(AppConstants.USER_ID, getContext()));
                FirebaseCrashlytics.getInstance().recordException(e);
                return true;
            }
        }
    }
}