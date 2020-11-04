package com.nuppin.company.Loja.loja.cadastro;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.nuppin.company.model.Company;
import com.nuppin.company.R;
import com.nuppin.company.Util.Util;

public class unit2_cad extends Fragment implements View.OnClickListener {
    private Company company;
    private static final String COMPANY = "COMPANY";
    private TextView alimentacao, produtos, servicos;


    public unit2_cad() {
        // Required empty public constructor
    }

    public static unit2_cad newInstance(Company company) {
        unit2_cad fragment = new unit2_cad();
        Bundle args = new Bundle();
        args.putParcelable(COMPANY, company);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        assert getArguments() != null;
        if (getArguments().containsKey(COMPANY)) {
            company = getArguments().getParcelable(COMPANY);
        }

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.unit2_cad, container, false);

        Toolbar toolbar = view.findViewById(R.id.tb_main_top);
        Util.setaToolbar(this, 0, toolbar, getActivity(), false, 0);


        alimentacao = view.findViewById(R.id.alimentos);
        produtos = view.findViewById(R.id.produtos);
        servicos = view.findViewById(R.id.service);
        alimentacao.setOnClickListener(this);
        produtos.setOnClickListener(this);
        servicos.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.alimentos:
                company.setCategory_company_id("1");
                listerMethod("2_1", company);
                break;
            case R.id.produtos:
                company.setCategory_company_id("2");
                listerMethod("2_1", company);
                break;
            case R.id.service:
                company.setCategory_company_id("3");
                company.setModel_type("fixed");
                listerMethod("3", company);
                break;
        }
    }

    private void listerMethod(String index, Company company) {
        if (getActivity() instanceof NovaEmpresa) {
            NovaEmpresa listener = (NovaEmpresa) getActivity();
            listener.cycleCad(index, company);
        }
    }

    public interface NovaEmpresa {
        void cycleCad(String index, Company company);
    }
}
