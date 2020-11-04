package com.nuppin.company.Loja.Equipe;

import android.app.Activity;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
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

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.gson.Gson;
import com.nuppin.company.Loja.dialogs.InfoDialogFragment;
import com.nuppin.company.Util.AppConstants;
import com.nuppin.company.Util.UtilShaPre;
import com.nuppin.company.connection.ConnectApi;
import com.nuppin.company.model.Employee;
import com.nuppin.company.R;
import com.nuppin.company.Util.TimerPickerFragment;
import com.nuppin.company.Util.Util;

public class FuncionarioDetalhe extends Fragment implements
        TimePickerDialog.OnTimeSetListener, LoaderManager.LoaderCallbacks<Integer>, InfoDialogFragment.InfoDialogListener {


    private static final String FUNCIONARIO = "FUNCIONARIO";
    private static final String INICIO = "INICIO";
    private static final String TERMINO = "TERMINO";
    private Employee employee;
    private String TAG;
    private TextView inicio, termino;
    private EditText cargo, nome;
    private LoaderManager loaderManager;
    private CardView progress;
    private MaterialButton btnSalvar, btnRemove;

    public static FuncionarioDetalhe newInstance(Employee employee) {
        Bundle parametros = new Bundle();
        parametros.putParcelable(FUNCIONARIO, employee);
        FuncionarioDetalhe fragment = new FuncionarioDetalhe();
        fragment.setArguments(parametros);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null && getArguments().containsKey(FUNCIONARIO)) {
            employee = getArguments().getParcelable(FUNCIONARIO);
        }
        loaderManager = LoaderManager.getInstance(this);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fr_employee_detail, container, false);

        Toolbar toolbar = v.findViewById(R.id.toolbar_top);
        Util.setaToolbar(this, R.string.detalhe_toolbar, toolbar, getActivity(), false, 0);

        btnSalvar = v.findViewById(R.id.btnSalvar);
        progress = v.findViewById(R.id.progress);
        nome = v.findViewById(R.id.nome);
        cargo = v.findViewById(R.id.cargo);
        btnRemove = v.findViewById(R.id.btnRemover);

        final TextView nome = v.findViewById(R.id.nome);
        inicio = v.findViewById(R.id.inicio);
        inicio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment timePicker = new TimerPickerFragment();
                TAG = INICIO;
                timePicker.show(getChildFragmentManager(), TAG);
            }
        });
        termino = v.findViewById(R.id.termino);
        termino.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment timePicker = new TimerPickerFragment();
                TAG = TERMINO;
                timePicker.show(getChildFragmentManager(), TAG);
            }
        });

        SimpleDraweeView image = v.findViewById(R.id.imagem);
        Util.hasPhoto(employee.getUser_id(), image);
        nome.setText(employee.getUser_name());
        inicio.setText(employee.getStart_time());
        termino.setText(employee.getEnd_time());
        cargo.setText(employee.getTitle());

        if (employee.getRole().equals("owner")) {
            btnRemove.setVisibility(View.GONE);
        }

        btnRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment dialogFrag = InfoDialogFragment.newInstance("Remover da equipe?", getResources().getString(R.string.confirmar), getResources().getString(R.string.revisar));
                dialogFrag.show(FuncionarioDetalhe.this.getChildFragmentManager(), "confirm");
            }
        });

        btnSalvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (nome.getText().toString().equals("") || nome.getText().toString().length() < 3) {
                    Toast.makeText(getContext(), R.string.invalid_name, Toast.LENGTH_SHORT).show();
                    return;
                }
                if (inicio.getText().toString().equals("") || inicio.getText().toString().equals("")) {
                    Toast.makeText(getContext(), R.string.set_time_of_employee_before_update_them, Toast.LENGTH_SHORT).show();
                    return;
                }
                employee.setTitle(cargo.getText().toString());
                employee.setUser_name(nome.getText().toString());
                btnSalvar.setClickable(false);
                btnRemove.setClickable(false);
                progress.setVisibility(View.VISIBLE);
                loaderManager.restartLoader(0, null, FuncionarioDetalhe.this);
            }
        });


        return v;
    }

    @Override
    public void onTimeSet(TimePicker timePicker, int hour, int minute) {
        if (TAG.equals(INICIO)) {
            employee.setStart_time(getResources().getString(R.string.set_time, hour, minute));
            inicio.setText(Util.zeroToTime(minute, hour));
        } else {
            employee.setEnd_time(getResources().getString(R.string.set_time, hour, minute));
            termino.setText(Util.zeroToTime(minute, hour));
        }

    }

    @NonNull
    @Override
    public Loader<Integer> onCreateLoader(int id, @Nullable Bundle args) {
        if (id == 0) {
            return new EditFuncionarioLoader(getActivity(), employee);
        } else {
            return new RemoveFuncionario(getActivity(), employee);
        }
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Integer> loader, Integer data) {
        progress.setVisibility(View.GONE);
        if (data != null) {
            if (loader.getId() == 0) {
                Util.backFragmentFunction(this);
            } else {
                if (data > 0) {
                    Util.backFragmentFunction(this);
                } else {
                    Toast.makeText(getContext(), R.string.error, Toast.LENGTH_SHORT).show();
                }
            }
        } else {
            Toast.makeText(getContext(), R.string.error, Toast.LENGTH_SHORT).show();
        }
        btnSalvar.setClickable(true);
        btnRemove.setClickable(true);
        loaderManager.destroyLoader(loader.getId());
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Integer> loader) {

    }

    @Override
    public void onDialogOKClick(View view, InfoDialogFragment info) {
        switch (view.getId()) {
            case R.id.btnPositive:
                progress.setVisibility(View.VISIBLE);
                btnRemove.setClickable(false);
                btnSalvar.setClickable(false);
                loaderManager.restartLoader(1, null, FuncionarioDetalhe.this);
                info.dismiss();
                break;
            case R.id.btnNegative:
                info.dismiss();
                break;
        }
    }

    private static class EditFuncionarioLoader extends AsyncTaskLoader<Integer> {

        Activity ctx;
        Employee employee;


        EditFuncionarioLoader(Activity context, Employee employee) {
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
                return gson.fromJson(ConnectApi.PATCH(employee, ConnectApi.EMPLOYEE, ctx), Integer.class);
            } catch (Exception e) {
                FirebaseCrashlytics.getInstance().log(UtilShaPre.getDefaultsString(AppConstants.USER_ID, getContext()));
                FirebaseCrashlytics.getInstance().recordException(e);
                return null;
            }
        }
    }

    private static class RemoveFuncionario extends AsyncTaskLoader<Integer> {

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
}
