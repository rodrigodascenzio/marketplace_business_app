package com.nuppin.company.Loja.Relatorio;

import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

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

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.nuppin.company.Util.AppConstants;
import com.nuppin.company.Util.UtilShaPre;
import com.nuppin.company.connection.ConnectApi;
import com.nuppin.company.model.Company;
import com.nuppin.company.model.Report;
import com.nuppin.company.R;
import com.nuppin.company.Util.DatePickerFragment;
import com.nuppin.company.Util.Util;

import java.util.Calendar;

public class FrRelatorioStore extends Fragment implements
        LoaderManager.LoaderCallbacks<Report>,
        DatePickerDialog.OnDateSetListener {

    private static final String COMPANY = "COMPANY";

    private LoaderManager loaderManager;
    private Company company;
    private final String DATA1 = "DATA1", DATA2 = "DATA2";
    private TextView faturamento, pedidos, receitas, despesas, vendas_conlcuidas, vendas_canceladas, ticket_medio, clientes_compra_unica, clientes_compra_recorrente, view_count, tm_view_count;
    private TextView data1, data2;
    private int dayofmonth, month, year;
    private int dayofmonth2, month2, year2;
    private String data1timestamp, data2timestamp;
    private String TAG;
    private CardView progress;
    private LottieAnimationView dots;
    private LinearLayout linearEmpty;
    private ScrollView scrollView;
    private ConstraintLayout errorLayout;
    private FloatingActionButton fabError;

    public FrRelatorioStore() {
    }


    public static FrRelatorioStore newInstance(Company company) {
        FrRelatorioStore fragment = new FrRelatorioStore();
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
        View view = inflater.inflate(R.layout.fr_company_data, container, false);
        dots = view.findViewById(R.id.dots);
        linearEmpty = view.findViewById(R.id.linearEmpty);
        scrollView = view.findViewById(R.id.scrollView);
        errorLayout = view.findViewById(R.id.error_layout);
        fabError = view.findViewById(R.id.fabError);
        fabError.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dots.setVisibility(View.VISIBLE);
                errorLayout.setVisibility(View.GONE);
                loaderManager.restartLoader(0, null, FrRelatorioStore.this);
            }
        });

        Toolbar toolbar = view.findViewById(R.id.toolbar_top);
        Util.setaToolbar(this, R.string.relatorio_toolbar, toolbar, getActivity(), false, 0);


        Calendar cal = Calendar.getInstance();
        if (dayofmonth == 0) {
            year = cal.get(Calendar.YEAR);
            month = cal.get(Calendar.MONTH) + 1;
            dayofmonth = 1;
        }
        if (dayofmonth2 == 0) {
            year2 = cal.get(Calendar.YEAR);
            month2 = cal.get(Calendar.MONTH) + 1;
            dayofmonth2 = cal.get(Calendar.DAY_OF_MONTH);
        }

        faturamento = view.findViewById(R.id.faturamento);
        receitas = view.findViewById(R.id.receitaValor);
        despesas = view.findViewById(R.id.despesasValor);
        pedidos = view.findViewById(R.id.vendasValor);
        vendas_conlcuidas = view.findViewById(R.id.concluido);
        vendas_canceladas = view.findViewById(R.id.cancelado);
        ticket_medio = view.findViewById(R.id.tm);
        clientes_compra_unica = view.findViewById(R.id.clientesUnicos);
        clientes_compra_recorrente = view.findViewById(R.id.clientesRecorrentes);
        view_count = view.findViewById(R.id.viewCount);
        tm_view_count = view.findViewById(R.id.tmViewCount);
        data1 = view.findViewById(R.id.data1);
        data2 = view.findViewById(R.id.data2);
        progress = view.findViewById(R.id.progress);

        data1.setText(Util.zeroToCalendar(dayofmonth, month, year));
        data2.setText(Util.zeroToCalendar(dayofmonth2, month2, year2));
        data1timestamp = Util.zeroToCalendarToMysql(dayofmonth, month, year);
        data2timestamp = Util.zeroToCalendarToMysql(dayofmonth2, month2, year2);

        data1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TAG = DATA1;
                DialogFragment timePicker = DatePickerFragment.newInstance(dayofmonth, month-1, year);
                timePicker.show(getChildFragmentManager(), AppConstants.CALENDAR);
            }
        });

        data2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TAG = DATA2;
                DialogFragment timePicker = DatePickerFragment.newInstance(dayofmonth2, month2-1, year2);
                timePicker.show(getChildFragmentManager(), AppConstants.CALENDAR);
            }
        });
        loaderManager.restartLoader(0, null, this);

        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
        loaderManager.destroyLoader(0);
    }

    @NonNull
    @Override
    public Loader<Report> onCreateLoader(int id, Bundle args) {
        return new RelatorioLoader(getActivity(), company, data1timestamp, data2timestamp);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Report> loader, Report data) {
        dots.setVisibility(View.GONE);
        progress.setVisibility(View.GONE);
        if (data != null) {
            if (data.getOrders_total_value() > 0 || data.getRevenue() > 0 || data.getExpenses() > 0 || data.getView_count() > 0) {
                view_count.setText(String.valueOf(data.getView_count()));
                tm_view_count.setText(String.valueOf(data.getTm_view_count()));
                faturamento.setText(Util.formaterPrice(data.getExpenses() + data.getRevenue() + data.getOrders_total_value()));
                pedidos.setText(Util.formaterPrice(data.getOrders_total_value()));
                receitas.setText(Util.formaterPrice(data.getRevenue()));
                despesas.setText(Util.formaterPrice(data.getExpenses()));
                vendas_conlcuidas.setText(String.valueOf(data.getConcluded_orders()));
                vendas_canceladas.setText(String.valueOf(data.getCanceled_orders()));
                ticket_medio.setText(Util.formaterPrice(data.getAverage_ticket()));
                clientes_compra_unica.setText(String.valueOf(data.getNew_customer()));
                clientes_compra_recorrente.setText(String.valueOf(data.getRecurring_customer()));
                scrollView.setVisibility(View.VISIBLE);
                linearEmpty.setVisibility(View.GONE);
            } else {
                scrollView.setVisibility(View.GONE);
                linearEmpty.setVisibility(View.VISIBLE);
            }
            errorLayout.setVisibility(View.GONE);
        } else {
            errorLayout.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Report> loader) {
    }

    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
        if (TAG.equals(DATA1)) {
            data1.setText(Util.zeroToCalendar(day, (month + 1), year));
            data1timestamp = Util.zeroToCalendarToMysql(day, (month + 1), year);
            this.dayofmonth = day; this.month = (month+1); this.year = year;
        } else {
            data2.setText(Util.zeroToCalendar(day, (month + 1), year));
            data2timestamp = Util.zeroToCalendarToMysql(day, (month + 1), year);
            this.dayofmonth2 = day; this.month2 = (month+1); this.year2 = year;
        }

        loaderManager.restartLoader(0, null, FrRelatorioStore.this);
        progress.setVisibility(View.VISIBLE);
        scrollView.setVisibility(View.GONE);
        linearEmpty.setVisibility(View.GONE);
    }

    private static class RelatorioLoader extends AsyncTaskLoader<Report> {

        Context ctx;
        Company company;
        String data1, data2;

        RelatorioLoader(Context context, Company company, String data1, String data2) {
            super(context);
            ctx = context;
            this.company = company;
            this.data1 = data1;
            this.data2 = data2;
        }


        @Override
        protected void onStartLoading() {
            forceLoad();
        }


        @Override
        public Report loadInBackground() {
            Gson gson = new Gson();
            try {
                String stringJson;
                if (company.getCategory_company_id().equals("3")) {
                    stringJson = ConnectApi.GET(ConnectApi.DATA_COMPANY_SCHEDULING + "/" + company.getCompany_id() + "," + data1 + "," + data2 + "," + Util.returnStringLatLonCountr(getContext()), getContext());
                } else {
                    stringJson = ConnectApi.GET(ConnectApi.DATA_COMPANY + "/" + company.getCompany_id() + "," + data1 + "," + data2 + "," + Util.returnStringLatLonCountr(getContext()), getContext());
                }
                JsonParser parser = new JsonParser();
                JsonObject jObj = parser.parse(stringJson).getAsJsonObject();
                return gson.fromJson(jObj.getAsJsonObject(AppConstants.COMPANY_DATA), Report.class);
            } catch (Exception e) {
                FirebaseCrashlytics.getInstance().log(UtilShaPre.getDefaultsString(AppConstants.USER_ID, getContext()));
                FirebaseCrashlytics.getInstance().recordException(e);
                return null;
            }
        }
    }
}
