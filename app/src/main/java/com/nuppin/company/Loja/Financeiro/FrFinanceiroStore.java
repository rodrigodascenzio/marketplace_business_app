package com.nuppin.company.Loja.Financeiro;

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
import com.nuppin.company.Util.AppConstants;
import com.nuppin.company.Util.UtilShaPre;
import com.nuppin.company.connection.ConnectApi;
import com.nuppin.company.model.Finance;
import com.nuppin.company.model.Company;
import com.nuppin.company.R;
import com.nuppin.company.Util.DatePickerFragment;
import com.nuppin.company.Util.Util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import me.toptas.fancyshowcase.FancyShowCaseView;
import me.toptas.fancyshowcase.listener.DismissListener;

public class FrFinanceiroStore extends Fragment implements
        LoaderManager.LoaderCallbacks<List<Finance>>,
        DatePickerDialog.OnDateSetListener,
        CashFlowAdapter.CashFlowAdapterVHOnClickListener {

    private static final String COMPANY = "COMPANY";

    private LoaderManager loaderManager;
    private Company company;
    private final String DATA1 = "DATA1", DATA2 = "DATA2";
    private TextView data1, data2;
    private int dayofmonth, month, year;
    private int dayofmonth2, month2, year2;
    private String data1timestamp, data2timestamp;
    private String TAG;
    private CardView progress;
    private CashFlowAdapter adapter;
    private RecyclerView recyclerView;
    private LottieAnimationView dots;
    private LinearLayout linearEmpty;
    private ConstraintLayout constraint;
    private ConstraintLayout errorLayout;
    private FloatingActionButton fabError, fab;
    private List<Finance> data;


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
                        .title(getString(R.string.unique_tutorial_finance_fab_string))
                        .showOnce(getString(R.string.unique_tutorial_finance_fab))
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


    public FrFinanceiroStore() {
    }

    @Override
    public void onDetach() {
        super.onDetach();
        loaderManager = null;
    }

    public static FrFinanceiroStore newInstance(Company company) {
        FrFinanceiroStore fragment = new FrFinanceiroStore();
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
        View view = inflater.inflate(R.layout.fr_cashflow, container, false);

        constraint = view.findViewById(R.id.container);
        dots = view.findViewById(R.id.dots);
        linearEmpty = view.findViewById(R.id.linearEmpty);
        errorLayout = view.findViewById(R.id.error_layout);
        fabError = view.findViewById(R.id.fabError);
        fabError.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dots.setVisibility(View.VISIBLE);
                errorLayout.setVisibility(View.GONE);
                loaderManager.restartLoader(0, null, FrFinanceiroStore.this);
            }
        });

        Toolbar toolbar;
        toolbar = view.findViewById(R.id.toolbar_top);
        Util.setaToolbar(this, R.string.financeiro_toolbar, toolbar, getActivity(), false, 0);


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

        fab = view.findViewById(R.id.fab);
        fab.hide();
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getActivity() instanceof ToActivity) {
                    ToActivity listener = (ToActivity) getActivity();
                    listener.CriarReceitaDespesa(company);
                }
            }
        });


        data1 = view.findViewById(R.id.data1);
        data2 = view.findViewById(R.id.data2);
        progress = view.findViewById(R.id.progress);
        recyclerView = view.findViewById(R.id.recyclerview_cashflow);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        adapter = new CashFlowAdapter(this);
        recyclerView.setAdapter(adapter);

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
    public Loader<List<Finance>> onCreateLoader(int id, Bundle args) {
        return new FinanceiroLoader(getActivity(), company.getCompany_id(), data1timestamp, data2timestamp, data);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<List<Finance>> loader, List<Finance> data) {
        dots.setVisibility(View.GONE);
        progress.setVisibility(View.GONE);

        if (data != null) {
            fab.show();
            this.data = data;
            if (data.size() > 0) {
                adapter.setCashFlow(data);
                constraint.setVisibility(View.VISIBLE);
                linearEmpty.setVisibility(View.GONE);
            } else {
                constraint.setVisibility(View.GONE);
                linearEmpty.setVisibility(View.VISIBLE);
            }
            errorLayout.setVisibility(View.GONE);
        } else {
            errorLayout.setVisibility(View.VISIBLE);
        }
        chainTutorial();
    }

    @Override
    public void onLoaderReset(@NonNull Loader<List<Finance>> loader) {
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
        loaderManager.restartLoader(0, null, FrFinanceiroStore.this);
        progress.setVisibility(View.VISIBLE);
        constraint.setVisibility(View.GONE);
        linearEmpty.setVisibility(View.GONE);
    }

    @Override
    public void onClickFromCashFlow(Finance finance) {
        if (getActivity() instanceof CashFlowAdapter.CashFlowAdapterVHOnClickListener) {
            CashFlowAdapter.CashFlowAdapterVHOnClickListener listener =
                    (CashFlowAdapter.CashFlowAdapterVHOnClickListener) getActivity();
            listener.onClickFromCashFlow(finance);
        }
    }

    private static class FinanceiroLoader extends AsyncTaskLoader<List<Finance>> {

        Context ctx;
        String stoId, data1, data2;
        List<Finance> data;

        FinanceiroLoader(Context context, String stoId, String data1, String data2, List<Finance> data) {
            super(context);
            ctx = context;
            this.stoId = stoId;
            this.data1 = data1;
            this.data2 = data2;
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
        public List<Finance> loadInBackground() {
            Gson gson = new Gson();
            String stringJson = ConnectApi.GET(ConnectApi.CASHFLOW_COMPANY + "/" + stoId + "," + data1 + "," + data2, getContext());
            JsonParser parser = new JsonParser();
            try {
                JsonObject jObj = parser.parse(stringJson).getAsJsonObject();
                Finance[] financeiro = gson.fromJson(jObj.getAsJsonArray(AppConstants.FINANCE), Finance[].class);
                return new ArrayList<>(Arrays.asList(financeiro));
            } catch (Exception e) {
                FirebaseCrashlytics.getInstance().log(UtilShaPre.getDefaultsString(AppConstants.USER_ID, getContext()));
                FirebaseCrashlytics.getInstance().recordException(e);
                return null;
            }
        }
    }

    public interface ToActivity {
        void CriarReceitaDespesa(Company company);
    }
}