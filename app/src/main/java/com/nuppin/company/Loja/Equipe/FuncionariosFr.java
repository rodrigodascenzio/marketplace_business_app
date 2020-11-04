package com.nuppin.company.Loja.Equipe;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

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
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.nuppin.company.Loja.dialogs.InfoDialogFragment;
import com.nuppin.company.Util.AppConstants;
import com.nuppin.company.Util.UtilShaPre;
import com.nuppin.company.connection.ConnectApi;
import com.nuppin.company.model.Employee;
import com.nuppin.company.model.ServiceEmployee;
import com.nuppin.company.model.Service;
import com.nuppin.company.model.Company;
import com.nuppin.company.R;
import com.nuppin.company.Util.Util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import me.toptas.fancyshowcase.FancyShowCaseView;
import me.toptas.fancyshowcase.listener.DismissListener;

public class FuncionariosFr extends Fragment implements
        LoaderManager.LoaderCallbacks<List<Employee>>,
        FuncionariosAdapter.FuncionariosAdapterListener,
        FuncionariosNotServicoAdapter.FuncionariosServicoAdapterListener,
        InfoDialogFragment.InfoDialogListener {

    private static final String COMPANY = "COMPANY";
    private static final String SERVICE = "SERVICE";
    private RecyclerView mRecyclerView;
    private FuncionariosAdapter adapter;
    private FuncionariosNotServicoAdapter funcServAdapter;
    private LoaderManager loaderManager;
    private Company company;
    private Service servico;
    private ServiceEmployee serviceEmployee;
    private Employee employeeNotServiceToDetail;
    private String key;
    private LottieAnimationView dots;
    private LinearLayout linearEmpty, linearEmptyService;
    private ConstraintLayout errorLayout;
    private FloatingActionButton fabError, fab;
    private List<Employee> data;
    private CardView progress;


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
                        .focusOn(fab)
                        .delay(500)
                        .title(getString(R.string.unique_tutorial_employee_fab_string))
                        .showOnce(getString(R.string.unique_tutorial_employee_fab))
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


    public FuncionariosFr() {
    }

    public static FuncionariosFr newInstance(Company company) {
        FuncionariosFr fragment = new FuncionariosFr();
        Bundle args = new Bundle();
        args.putParcelable(COMPANY, company);
        fragment.setArguments(args);
        return fragment;
    }

    public static FuncionariosFr newInstance(Service servico) {
        FuncionariosFr fragment = new FuncionariosFr();
        Bundle args = new Bundle();
        args.putParcelable(SERVICE, servico);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null && getArguments().containsKey(COMPANY)) {
            company = getArguments().getParcelable(COMPANY);
            key = COMPANY;
        }
        if (getArguments() != null && getArguments().containsKey(SERVICE)) {
            servico = getArguments().getParcelable(SERVICE);
            key = SERVICE;
        }
        loaderManager = LoaderManager.getInstance(this);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fr_employee, container, false);

        dots = view.findViewById(R.id.dots);
        linearEmpty = view.findViewById(R.id.linearEmpty);
        linearEmptyService = view.findViewById(R.id.linearEmptyService);
        errorLayout = view.findViewById(R.id.error_layout);
        fabError = view.findViewById(R.id.fabError);
        fabError.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dots.setVisibility(View.VISIBLE);
                errorLayout.setVisibility(View.GONE);
                if (key.equals(COMPANY)) {
                    loaderManager.restartLoader(99, null, FuncionariosFr.this);

                } else {
                    dots.setVisibility(View.VISIBLE);
                    loaderManager.restartLoader(88, null, FuncionariosFr.this);
                }
            }
        });
        progress = view.findViewById(R.id.progress);

        Toolbar toolbar = view.findViewById(R.id.toolbar_top);
        if (key.equals(COMPANY)) {
            Util.setaToolbar(this, R.string.equipe_toolbar, toolbar, getActivity(), false, 0);
        } else {
            Util.setaToolbar(this, 0, toolbar, getActivity(), true, R.drawable.ic_clear_white_24dp);
        }

        mRecyclerView = view.findViewById(R.id.recyclerview_funcionarios);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);

        fab = view.findViewById(R.id.fab);
        fab.hide();
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getActivity() instanceof ToActivity) {
                    ToActivity listener = (ToActivity) getActivity();
                    if (key.equals(COMPANY)) {
                        listener.FrFuncionariosBottom(company.getCompany_id());
                    } else {
                        listener.FrFuncionariosBottom(servico.getCompany_id());
                    }
                }
            }
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (key.equals(COMPANY)) {
            adapter = new FuncionariosAdapter(this);
            mRecyclerView.setAdapter(adapter);
            loaderManager.restartLoader(99, null, this);
            chainCreatedTutorial();
        } else {
            funcServAdapter = new FuncionariosNotServicoAdapter(this);
            mRecyclerView.setAdapter(funcServAdapter);
            fab.hide();
            loaderManager.restartLoader(88, null, this);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (key.equals(COMPANY)) {
            loaderManager.destroyLoader(99);
        } else {
            loaderManager.destroyLoader(88);
        }
    }

    @NonNull
    @Override
    public Loader<List<Employee>> onCreateLoader(int id, Bundle args) {
        if (id == 99) {
            return new FuncionariosLoader(getActivity(), company, data);
        } else if (id == 88) {
            return new FuncionariosNotServicoLoader(getActivity(), servico, data);
        } else {
            return new AddFuncionariosServicoLoader(getActivity(), serviceEmployee);
        }
    }

    @Override
    public void onLoadFinished(@NonNull Loader<List<Employee>> loader, List<Employee> data) {
        dots.setVisibility(View.GONE);
        if (data != null) {
            if (loader.getId() == 99) {
                this.data = data;
                fab.show();
                if (data.size() > 0) {
                    adapter.setEmployees(data);
                    mRecyclerView.setVisibility(View.VISIBLE);
                    linearEmpty.setVisibility(View.GONE);
                } else {
                    mRecyclerView.setVisibility(View.GONE);
                    linearEmpty.setVisibility(View.VISIBLE);
                }
            chainTutorial();
            } else if (loader.getId() == 88) {
                this.data = data;
                if (data.size() > 0) {
                    funcServAdapter.setEmployees(data);
                    mRecyclerView.setVisibility(View.VISIBLE);
                    linearEmptyService.setVisibility(View.GONE);
                } else {
                    fab.show();
                    mRecyclerView.setVisibility(View.GONE);
                    linearEmptyService.setVisibility(View.VISIBLE);
                }
            } else {
                progress.setVisibility(View.GONE);
                loaderManager.destroyLoader(loader.getId());
                if (this.data.size() > 1) {
                    this.data = null;
                    dots.setVisibility(View.VISIBLE);
                    mRecyclerView.setVisibility(View.GONE);
                    loaderManager.restartLoader(88, null, this);
                } else {
                    Util.backFragmentFunction(this);
                }
            }
            errorLayout.setVisibility(View.GONE);
        } else {
            errorLayout.setVisibility(View.VISIBLE);
        }
        if (loader.getId() != 99 && loader.getId() != 88) {
            loaderManager.destroyLoader(loader.getId());
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<List<Employee>> loader) {

    }

    @Override
    public void onClickFuncionario(Employee employee) {
        if (getActivity() instanceof ToActivity) {
            ToActivity listener = (ToActivity) getActivity();
            listener.FrFuncionarioDetalhe(employee);
        }
    }

    @Override
    public void incluirFuncionarioServico(Employee employee) {
        if (employee.getStart_time() == null) {
            employeeNotServiceToDetail = employee;
            DialogFragment dialogFrag = InfoDialogFragment.newInstance(getString(R.string.warning_before_add_employee_to_service), "Fazer agora", "Depois");
            dialogFrag.show(FuncionariosFr.this.getChildFragmentManager(), "dialog_fragment");
            return;
        }
        progress.setVisibility(View.VISIBLE);
        serviceEmployee = new ServiceEmployee();
        serviceEmployee.setEmployee_id(employee.getEmployee_id());
        serviceEmployee.setService_id(servico.getService_id());
        serviceEmployee.setCompany_id(servico.getCompany_id());
        loaderManager.restartLoader(1, null, this);
    }

    @Override
    public void onDialogOKClick(View view, InfoDialogFragment infoDialogFragment) {
        switch (view.getId()) {
            case R.id.btnPositive:
                if (getActivity() instanceof ToActivity) {
                    ToActivity listener = (ToActivity) getActivity();
                    listener.FrFuncionarioDetalhe(employeeNotServiceToDetail);
                }
                infoDialogFragment.dismiss();
                break;
            case R.id.btnNegative:
                infoDialogFragment.dismiss();
                break;
        }
    }

    private static class FuncionariosLoader extends AsyncTaskLoader<List<Employee>> {

        Context ctx;
        Company company;
        List<Employee> data;

        FuncionariosLoader(Context context, Company company, List<Employee> data) {
            super(context);
            ctx = context;
            this.company = company;
            this.data = data;
        }


        @Override
        protected void onStartLoading() {
            if (data != null) {
                deliverResult(data);
            }
            forceLoad();
        }


        @Override
        public List<Employee> loadInBackground() {
            try {
                Gson gson = new Gson();
                String stringJson = ConnectApi.GET(ConnectApi.EMPLOYEE_COMPANY + "/" + company.getCompany_id(), getContext());
                JsonParser parser = new JsonParser();
                JsonObject jObj = parser.parse(stringJson).getAsJsonObject();
                Employee[] employees = gson.fromJson(jObj.getAsJsonArray(AppConstants.EMPLOYEE), Employee[].class);
                return new ArrayList<>(Arrays.asList(employees));
            } catch (Exception e) {
                FirebaseCrashlytics.getInstance().log(UtilShaPre.getDefaultsString(AppConstants.USER_ID, getContext()));
                FirebaseCrashlytics.getInstance().recordException(e);
                return null;
            }
        }
    }

    private static class FuncionariosNotServicoLoader extends AsyncTaskLoader<List<Employee>> {

        Context ctx;
        Service servico;
        List<Employee> data;

        FuncionariosNotServicoLoader(Context context, Service servico, List<Employee> data) {
            super(context);
            ctx = context;
            this.servico = servico;
            this.data = data;
        }


        @Override
        protected void onStartLoading() {
            if (data != null) {
                deliverResult(data);
            }
            forceLoad();
        }


        @Override
        public List<Employee> loadInBackground() {
            try {
                Gson gson = new Gson();
                String stringJson = ConnectApi.GET(ConnectApi.EMPLOYEE_NOT_SERVICE + "/" + servico.getService_id() + "," + servico.getCompany_id(), getContext());
                JsonParser parser = new JsonParser();
                JsonObject jObj = parser.parse(stringJson).getAsJsonObject();
                Employee[] employees = gson.fromJson(jObj.getAsJsonArray(AppConstants.EMPLOYEE), Employee[].class);
                return new ArrayList<>(Arrays.asList(employees));
            } catch (Exception e) {
                FirebaseCrashlytics.getInstance().log(UtilShaPre.getDefaultsString(AppConstants.USER_ID, getContext()));
                FirebaseCrashlytics.getInstance().recordException(e);
                return null;
            }
        }
    }

    private static class AddFuncionariosServicoLoader extends AsyncTaskLoader<List<Employee>> {

        Context ctx;
        ServiceEmployee serviceEmployee;

        AddFuncionariosServicoLoader(Context context, ServiceEmployee serviceEmployee) {
            super(context);
            ctx = context;
            this.serviceEmployee = serviceEmployee;
        }


        @Override
        protected void onStartLoading() {
            forceLoad();
        }


        @Override
        public List<Employee> loadInBackground() {
            try {
                Gson gson = new Gson();
                ServiceEmployee func = gson.fromJson(ConnectApi.POST(serviceEmployee, ConnectApi.EMPLOYEE_SERVICE, ctx), ServiceEmployee.class);
                List<Employee> ok = new ArrayList<>();
                ok.add(new Employee(func.getEmployee_id()));
                return ok;
            } catch (Exception e) {
                FirebaseCrashlytics.getInstance().log(UtilShaPre.getDefaultsString(AppConstants.USER_ID, getContext()));
                FirebaseCrashlytics.getInstance().recordException(e);
                return null;
            }
        }
    }

    public interface ToActivity {
        void FrFuncionariosBottom(String companyId);

        void FrFuncionarioDetalhe(Employee employee);
    }
}