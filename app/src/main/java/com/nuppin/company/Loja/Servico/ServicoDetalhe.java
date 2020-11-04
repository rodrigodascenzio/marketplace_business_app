package com.nuppin.company.Loja.Servico;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
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
import com.nuppin.company.Util.AppConstants;
import com.nuppin.company.Util.UtilShaPre;
import com.nuppin.company.connection.ConnectApi;
import com.nuppin.company.Loja.Equipe.FuncionariosServicoAdapter;
import com.nuppin.company.model.Employee;
import com.nuppin.company.model.ServiceEmployee;
import com.nuppin.company.model.Service;
import com.nuppin.company.R;
import com.nuppin.company.Util.Util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import me.toptas.fancyshowcase.FancyShowCaseView;
import me.toptas.fancyshowcase.listener.DismissListener;

public class ServicoDetalhe extends Fragment implements
        LoaderManager.LoaderCallbacks<Object>,
        FuncionariosServicoAdapter.FuncionariosServicoAdapterListener {

    private static final String SERVICE = "SERVICE";
    private RecyclerView mRecyclerView;
    private FuncionariosServicoAdapter adapter;
    private LoaderManager loaderManager;
    private Service service;
    private ServiceEmployee serviceEmployee;
    private LottieAnimationView dots;
    private LinearLayout linearEmpty;
    private ConstraintLayout errorLayout;
    private FloatingActionButton fabError, fab, fabEdit;
    private Object data;
    private CardView progress;

    private FancyShowCaseView fancyShowCaseView1;
    private FancyShowCaseView fancyShowCaseView2;
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
                        .focusOn(fabEdit)
                        .delay(500)
                        .title(getString(R.string.unique_tutorial_detalhe_servico_fab_edit_string))
                        .showOnce(getString(R.string.unique_tutorial_detalhe_servico_fab_edit))
                        .backgroundColor(getResources().getColor(R.color.primary_light))
                        .enableAutoTextPosition()
                        .closeOnTouch(true)
                        .dismissListener(listenerTutorial)
                        .build();
            }

            if (fancyShowCaseView2 == null) {
                fancyShowCaseView2 = new FancyShowCaseView.Builder(requireActivity())
                        .focusOn(fab)
                        .title(getString(R.string.unique_tutorial_detalhe_servico_fab_string))
                        .dismissListener(listenerTutorial)
                        .backgroundColor(getResources().getColor(R.color.primary_light))
                        .enableAutoTextPosition()
                        .showOnce(getString(R.string.unique_tutorial_detalhe_servico_fab))
                        .closeOnTouch(true)
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
            if (!fancyShowCaseView2.isShownBefore()) {
                fancyShowCaseView2.show();
                return;
            }
        } catch (Exception e) {
            return;
        }
    }


    public ServicoDetalhe() {
    }

    public static ServicoDetalhe newInstance(Service service) {
        ServicoDetalhe fragment = new ServicoDetalhe();
        Bundle args = new Bundle();
        args.putParcelable(SERVICE, service);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null && getArguments().containsKey(SERVICE)) {
            service = getArguments().getParcelable(SERVICE);
        }
        loaderManager = LoaderManager.getInstance(this);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fr_service_detail, container, false);

        if (service.getCompany_id() == null) {
            Util.backFragmentFunction(this);
            Toast.makeText(getContext(), "Serviço deletado", Toast.LENGTH_SHORT).show();
        }


        dots = view.findViewById(R.id.dots);
        linearEmpty = view.findViewById(R.id.linearEmpty);
        errorLayout = view.findViewById(R.id.error_layout);
        fabError = view.findViewById(R.id.fabError);
        fabError.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dots.setVisibility(View.VISIBLE);
                errorLayout.setVisibility(View.GONE);
                loaderManager.restartLoader(0, null, ServicoDetalhe.this);
            }
        });

        progress = view.findViewById(R.id.progress);


        Toolbar toolbar = view.findViewById(R.id.toolbar_top);
        Util.setaToolbar(this, R.string.detalhe_toolbar, toolbar, getActivity(), false, 0);

        TextView productName, preco, desc, duracao, txt;
        productName = view.findViewById(R.id.nome);
        preco = view.findViewById(R.id.preco);
        desc = view.findViewById(R.id.desc);
        duracao = view.findViewById(R.id.duracao);
        txt = view.findViewById(R.id.txt);

        productName.setText(service.getName());
        preco.setText(Util.formaterPrice(service.getPreco()));
        desc.setText(service.getDescription());
        duracao.setText(getResources().getString(R.string.servico_duracao, service.getDuration()));
        txt.setText(getResources().getString(R.string.servico_in_service_equipe, service.getName()));

        mRecyclerView = view.findViewById(R.id.recyclerview_funcionarios);

        fabEdit = view.findViewById(R.id.fab);
        fabEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getActivity() instanceof ServicoDetalhe.ToActivity) {
                    ServicoDetalhe.ToActivity listener = (ServicoDetalhe.ToActivity) getActivity();
                    listener.ServicosDetalheBottom(0, service);
                }
            }
        });

        fab = view.findViewById(R.id.fab2);
        fab.hide();
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getActivity() instanceof ServicoDetalhe.ToActivity) {
                    ServicoDetalhe.ToActivity listener = (ServicoDetalhe.ToActivity) getActivity();
                    listener.ServicosDetalheBottom(1, service);
                }
            }
        });

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);
        adapter = new FuncionariosServicoAdapter(this);
        mRecyclerView.setAdapter(adapter);
        loaderManager.restartLoader(0, null, this);

        chainCreatedTutorial();

        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
        loaderManager.destroyLoader(0);
    }

    @NonNull
    @Override
    public Loader<Object> onCreateLoader(int id, Bundle args) {
        if (id == 0) {
            return new ServicoDetalheLoader(getActivity(), service, data);
        } else {
            return new DeleteFuncionariosServicoLoader(getActivity(), serviceEmployee);
        }
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Object> loader, Object data) {
        dots.setVisibility(View.GONE);
        switch (loader.getId()) {
            case 0:
                fab.show();
                if (data instanceof List) {
                    this.data = data;
                    if (((List) data).size() > 0) {
                        adapter.setEmployees((List) data);
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
                break;
            case 1:
                progress.setVisibility(View.GONE);
                loaderManager.destroyLoader(loader.getId());
                if (data instanceof Integer) {
                    if (((Integer) data) > 0) {
                        this.data = null;
                        dots.setVisibility(View.VISIBLE);
                        mRecyclerView.setVisibility(View.GONE);
                        Toast.makeText(getActivity(), R.string.desvinculado_sucesso, Toast.LENGTH_SHORT).show();
                        loaderManager.restartLoader(0, null, this);
                    } else {
                        Toast.makeText(getContext(), R.string.error, Toast.LENGTH_SHORT).show();
                    }
                    errorLayout.setVisibility(View.GONE);
                } else {
                    errorLayout.setVisibility(View.VISIBLE);
                }
                break;
        }
        chainTutorial();
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Object> loader) {

    }

    @Override
    public void desvincularFuncionarioServico(Employee employee) {
        progress.setVisibility(View.VISIBLE);
        serviceEmployee = new ServiceEmployee();
        serviceEmployee.setEmployee_id(employee.getEmployee_id());
        serviceEmployee.setService_id(service.getService_id());
        serviceEmployee.setCompany_id(service.getCompany_id());
        loaderManager.restartLoader(1, null, this);
    }

    private static class ServicoDetalheLoader extends AsyncTaskLoader<Object> {

        Context ctx;
        Service service;
        Object data;


        ServicoDetalheLoader(Context context, Service service, Object data) {
            super(context);
            ctx = context;
            this.service = service;
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
        public Object loadInBackground() {
            try {
                Gson gson = new Gson();
                String stringJson = ConnectApi.GET(ConnectApi.EMPLOYEE_SERVICE + "/" + service.getService_id()+","+service.getCompany_id(), getContext());
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

    private static class DeleteFuncionariosServicoLoader extends AsyncTaskLoader<Object> {

        Context ctx;
        ServiceEmployee serviceEmployee;

        DeleteFuncionariosServicoLoader(Context context, ServiceEmployee serviceEmployee) {
            super(context);
            ctx = context;
            this.serviceEmployee = serviceEmployee;
        }


        @Override
        protected void onStartLoading() {
            forceLoad();
        }


        @Override
        public Object loadInBackground() {
            try {
                Gson gson = new Gson();
                return gson.fromJson(ConnectApi.DELETE(serviceEmployee, ConnectApi.EMPLOYEE_SERVICE, getContext()), Integer.class);
            } catch (Exception e) {
                FirebaseCrashlytics.getInstance().log(UtilShaPre.getDefaultsString(AppConstants.USER_ID, getContext()));
                FirebaseCrashlytics.getInstance().recordException(e);
                return null;
            }
        }
    }

    public interface ToActivity {
        void ServicosDetalheBottom(int index, Service servico);
    }
}