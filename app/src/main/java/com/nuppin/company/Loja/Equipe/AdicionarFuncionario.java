package com.nuppin.company.Loja.Equipe;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.AsyncTaskLoader;
import androidx.loader.content.Loader;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.nuppin.company.Util.AppConstants;
import com.nuppin.company.Util.UtilShaPre;
import com.nuppin.company.connection.ConnectApi;
import com.nuppin.company.model.Company;
import com.nuppin.company.model.Employee;
import com.nuppin.company.model.ErrorCode;
import com.nuppin.company.R;
import com.nuppin.company.Util.CNP;
import com.nuppin.company.Util.MaskEditUtil;
import com.nuppin.company.Util.Util;

public class AdicionarFuncionario extends Fragment implements
        LoaderManager.LoaderCallbacks {

    private static final String COMPANY = "COMPANY";
    private String companyId;
    private LoaderManager loaderManager;
    private Employee employee;
    private CardView progress;
    private MaterialButton btnAdicionar;
    private RadioGroup radioGroup;
    private String role = "";

    public AdicionarFuncionario() {
    }

    public static AdicionarFuncionario newInstance(String company) {
        AdicionarFuncionario fragment = new AdicionarFuncionario();
        Bundle args = new Bundle();
        args.putString(COMPANY, company);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        assert getArguments() != null;
        if (getArguments().containsKey(COMPANY)) {
            companyId = getArguments().getString(COMPANY);
        }
        loaderManager = LoaderManager.getInstance(this);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.add_employee, container, false);

        final EditText userId = view.findViewById(R.id.textId);
        userId.addTextChangedListener(MaskEditUtil.mask(userId, MaskEditUtil.FORMAT_CPF));
        btnAdicionar = view.findViewById(R.id.btnAdicionar);
        progress = view.findViewById(R.id.progress);
        radioGroup = view.findViewById(R.id.nivelUser);

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if (i == R.id.admin) {
                    role = "admin";
                } else {
                    role = "employee";
                }
            }
        });

        btnAdicionar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (role.isEmpty()) {
                    Toast.makeText(getContext(), "Selecione o nivel de acesso do usuário", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (userId.getText().toString().isEmpty()) {
                    return;
                }
                if (!CNP.isValidCPF(userId.getText().toString())) {
                    Toast.makeText(getContext(), R.string.cpf_invalido, Toast.LENGTH_SHORT).show();
                    return;
                }
                employee = new Employee(Util.clearNotNumber(userId.getText().toString()), companyId, role);
                btnAdicionar.setClickable(false);
                loaderManager.restartLoader(0, null, AdicionarFuncionario.this);
            }
        });

        Toolbar toolbar = view.findViewById(R.id.toolbar_top);
        Util.setaToolbar(this, 0, toolbar, getActivity(), true, R.drawable.ic_clear_white_24dp);

        return view;
    }

    @NonNull
    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        progress.setVisibility(View.VISIBLE);
        return new AddFuncionariosLoader(getActivity(), employee);
    }

    @Override
    public void onLoadFinished(@NonNull Loader loader, Object data) {
        progress.setVisibility(View.GONE);
        loaderManager.destroyLoader(loader.getId());
        if (data != null) {
            if (data instanceof Employee) {
                Util.backFragmentFunction(this);
            } else if (data instanceof ErrorCode) {
                Toast.makeText(getContext(), ((ErrorCode) data).getError_message(), Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getContext(), R.string.error, Toast.LENGTH_SHORT).show();
        }
        btnAdicionar.setClickable(true);
    }

    @Override
    public void onLoaderReset(@NonNull Loader loader) {

    }

    private static class AddFuncionariosLoader extends AsyncTaskLoader<Object> {

        Activity ctx;
        Employee employee;

        AddFuncionariosLoader(Activity context, Employee employee) {
            super(context);
            ctx = context;
            this.employee = employee;
        }


        @Override
        protected void onStartLoading() {
            forceLoad();

        }


        @Override
        public Object loadInBackground() {
            Gson gson = new Gson();
            String json = ConnectApi.POST(employee, ConnectApi.EMPLOYEE_COMPANY, ctx);
            JsonParser parser = new JsonParser();
            try {
                return gson.fromJson(parser.parse(json).getAsJsonObject().getAsJsonObject(AppConstants.EMPLOYEE), Employee.class);
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


}