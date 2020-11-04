package com.nuppin.company.Loja.loja.cadastro;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.nuppin.company.model.Company;
import com.nuppin.company.R;
import com.nuppin.company.Util.Util;

public class unit2_1_cad extends Fragment implements View.OnClickListener {
    private Company company;
    private static final String COMPANY = "COMPANY";


    public unit2_1_cad() {
        // Required empty public constructor
    }

    public static unit2_1_cad newInstance(Company company) {
        unit2_1_cad fragment = new unit2_1_cad();
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

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.unit2_1_cad, container, false);

        Toolbar toolbar = view.findViewById(R.id.tb_main_top);
        Util.setaToolbar(this, 0, toolbar, getActivity(), false, 0);


        LinearLayout fixo = view.findViewById(R.id.fixo);
        LinearLayout movel = view.findViewById(R.id.movel);
        fixo.setOnClickListener(this);
        movel.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fixo:
                company.setModel_type("fixed");
                listerMethod(company);
                break;
            case R.id.movel:
                company.setModel_type("mobile");
                listerMethod(company);
                break;
        }
    }

    private void listerMethod(Company company) {
        if (getActivity() instanceof NovaEmpresa) {
            NovaEmpresa listener = (NovaEmpresa) getActivity();
            listener.cycleCad("3", company);
        }
    }

    public interface NovaEmpresa {
        void cycleCad(String index, Company company);
    }
}
