package com.nuppin.company.Loja.loja.cadastro;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.AsyncTaskLoader;
import androidx.loader.content.Loader;

import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.gson.Gson;
import com.nuppin.company.Util.AppConstants;
import com.nuppin.company.Util.UtilShaPre;
import com.nuppin.company.connection.ConnectApi;
import com.nuppin.company.model.Company;
import com.nuppin.company.R;
import com.nuppin.company.Util.Util;

public class unit6_cad extends Fragment implements LoaderManager.LoaderCallbacks {
    private Company company;
    private static final String COMPANY = "COMPANY";
    private LoaderManager loaderManager;
    private CardView progress;
    private Button btnCad;


    public unit6_cad() {
        // Required empty public constructor
    }

    public static unit6_cad newInstance(Company company) {
        unit6_cad fragment = new unit6_cad();
        Bundle args = new Bundle();
        args.putParcelable(COMPANY, company);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null && getArguments().containsKey(COMPANY)) {
            company = getArguments().getParcelable(COMPANY);
        }
        loaderManager = LoaderManager.getInstance(this);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.unit6_cad, container, false);

        progress = view.findViewById(R.id.progress);

        Toolbar toolbar = view.findViewById(R.id.tb_main_top);
        Util.setaToolbar(this, 0, toolbar, getActivity(), false, 0);


        final EditText nomeEmpresa = view.findViewById(R.id.nomeEmpresa);
        btnCad = view.findViewById(R.id.btnCad);
        btnCad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (nomeEmpresa.getText().toString().equals("") || nomeEmpresa.getText().toString().length() < 3) {
                    Toast.makeText(getContext(), R.string.invalid_name, Toast.LENGTH_SHORT).show();
                    return;
                }
                progress.setVisibility(View.VISIBLE);
                btnCad.setClickable(false);
                company.setName(Util.clearName(nomeEmpresa.getText().toString()));
                loaderManager.restartLoader(0, null, unit6_cad.this);
            }
        });
        return view;
    }

    @NonNull
    @Override
    public Loader onCreateLoader(int id, @Nullable Bundle args) {
        return new EmpresaToDB(getContext(), company);
    }

    @Override
    public void onLoadFinished(@NonNull Loader loader, Object data) {
        progress.setVisibility(View.GONE);
        if (data != null) {
            company = (Company) data;
            if (getActivity() instanceof NovaEmpresa) {
                NovaEmpresa listener = (NovaEmpresa) getActivity();
                listener.cycleCad("listLojas", company);
            }
        } else {
            Toast.makeText(getContext(), R.string.error, Toast.LENGTH_SHORT).show();
        }
        loaderManager.destroyLoader(loader.getId());
        btnCad.setClickable(true);
    }

    @Override
    public void onLoaderReset(@NonNull Loader loader) {

    }


    private static class EmpresaToDB extends AsyncTaskLoader {

        Context ctx;
        Company company;

        EmpresaToDB(Context context, Company company) {
            super(context);
            ctx = context;
            this.company = company;
        }


        @Override
        protected void onStartLoading() {
            forceLoad();
        }


        @Override
        public Company loadInBackground() {
            Gson gson = new Gson();
            try {
                return gson.fromJson(ConnectApi.BEARER(company, ConnectApi.COMPANY_NEW + "/" + company.getPlan_id(), getContext()), Company.class);
            } catch (Exception e) {
                FirebaseCrashlytics.getInstance().log(UtilShaPre.getDefaultsString(AppConstants.USER_ID, getContext()));
                FirebaseCrashlytics.getInstance().recordException(e);
                return null;
            }
        }
    }

    public interface NovaEmpresa {
        void cycleCad(String index, Company company);
    }

}
