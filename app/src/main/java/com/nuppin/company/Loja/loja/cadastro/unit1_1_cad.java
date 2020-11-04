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
import com.nuppin.company.Util.CNP;
import com.nuppin.company.Util.MaskEditUtil;
import com.nuppin.company.Util.Util;

public class unit1_1_cad extends Fragment implements LoaderManager.LoaderCallbacks {
    private Company company;
    private static final String COMPANY = "COMPANY";
    private EditText cpfOrCnpj;
    private LoaderManager loaderManager;
    private String TAG;
    private CardView progress;

    public unit1_1_cad() {}

    public static unit1_1_cad newInstance(Company company) {
        unit1_1_cad fragment = new unit1_1_cad();
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
        final View view = inflater.inflate(R.layout.unit1_1_cad, container, false);

        Toolbar toolbar = view.findViewById(R.id.tb_main_top);
        Util.setaToolbar(this, 0, toolbar, getActivity(), false, 0);


        progress = view.findViewById(R.id.progress);

        cpfOrCnpj = view.findViewById(R.id.txtCpfOrCnpj);
        if (company.getDocument_type().equals("CPF")) {
            cpfOrCnpj.addTextChangedListener(MaskEditUtil.mask(cpfOrCnpj, MaskEditUtil.FORMAT_CPF));
            cpfOrCnpj.setHint("CPF");
            TAG = "CPF";
        } else {
            cpfOrCnpj.addTextChangedListener(MaskEditUtil.mask(cpfOrCnpj,MaskEditUtil.FORMAT_CNPJ));
            cpfOrCnpj.setHint("CNPJ");
            TAG = "CNPJ";
        }

        Button btnCad = view.findViewById(R.id.btnCad);
        btnCad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (company.getDocument_type()) {
                    case "CPF":
                        if (!CNP.isValidCPF(cpfOrCnpj.getText().toString())) {
                            Toast.makeText(getContext(), R.string.cpf_invalido, Toast.LENGTH_SHORT).show();
                            return;
                        }
                        break;
                    case "CNPJ":
                        if (!CNP.isValidCNPJ(cpfOrCnpj.getText().toString())) {
                            Toast.makeText(getContext(), R.string.cnpj_invalido, Toast.LENGTH_SHORT).show();
                            return;
                        }
                        break;
                }
                progress.setVisibility(View.VISIBLE);
                loaderManager.restartLoader(0, null, unit1_1_cad.this);
            }
        });
        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
        loaderManager.destroyLoader(0);
    }

    @NonNull
    @Override
    public Loader onCreateLoader(int id, @Nullable Bundle args) {
        return new LoaderSubCategorias(getContext(),Util.clearNotNumber(cpfOrCnpj.getText().toString()));
    }

    @Override
    public void onLoadFinished(@NonNull Loader loader, Object data) {
        progress.setVisibility(View.GONE);
        if (data != null) {
            if(data instanceof Boolean){
                if ((Boolean)data) {
                    if (TAG.equals("CPF")) {
                        Toast.makeText(getContext(), R.string.cpf_exist, Toast.LENGTH_SHORT).show();
                    } else if (TAG.equals("CNPJ")) {
                        Toast.makeText(getContext(), R.string.cnpj_exist, Toast.LENGTH_SHORT).show();
                    }
                }else {
                    company.setDocument_number(Util.clearNotNumber(cpfOrCnpj.getText().toString()));
                    if (getActivity() instanceof NovaEmpresa) {
                        NovaEmpresa listener = (NovaEmpresa) getActivity();
                        listener.cycleCad("2", company);
                    }
                }
            }
        }else {
            Toast.makeText(getContext(), R.string.error, Toast.LENGTH_SHORT).show();
        }
        loaderManager.destroyLoader(loader.getId());
    }

    @Override
    public void onLoaderReset(@NonNull Loader loader) {

    }


    private static class LoaderSubCategorias extends AsyncTaskLoader {

        Context ctx;
        Company company;

        LoaderSubCategorias(@NonNull Context context, String cpfCnpj) {
            super(context);
            company = new Company();
            company.setDocument_number(cpfCnpj);
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
            Gson gson = new Gson();
                try {
                    return gson.fromJson(ConnectApi.POST(company, ConnectApi.COMPANY_VERIFY_EXIST_CPFCNPJ, getContext()), Boolean.class);
                } catch (Exception e1) {
                    FirebaseCrashlytics.getInstance().log(UtilShaPre.getDefaultsString(AppConstants.USER_ID, getContext()));
                    FirebaseCrashlytics.getInstance().recordException(e1);
                    return null;
                }
        }
    }

    public interface NovaEmpresa {
        void cycleCad(String index, Company company);
    }
}
