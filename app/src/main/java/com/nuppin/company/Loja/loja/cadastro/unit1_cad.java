package com.nuppin.company.Loja.loja.cadastro;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.nuppin.company.model.Company;
import com.nuppin.company.R;
import com.nuppin.company.Util.Util;

public class unit1_cad extends Fragment {
    private Company company;
    private String userId;
    private static final String USER = "USER";
    private RadioGroup radioGroup;
    private RadioButton radioButton;


    public unit1_cad() {
        // Required empty public constructor
    }

    public static unit1_cad newInstance(String userId) {
        unit1_cad fragment = new unit1_cad();
        Bundle args = new Bundle();
        args.putString(USER, userId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        assert getArguments() != null;
        if (getArguments().containsKey(USER)) {
            userId = getArguments().getString(USER);
        }
        company = new Company(userId);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.unit1_cad, container, false);

        Toolbar toolbar = view.findViewById(R.id.tb_main_top);
        Util.setaToolbar(this, 0, toolbar, getActivity(), false, 0);

        radioGroup = view.findViewById(R.id.radioGroup);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                radioButton = view.findViewById(i);
                switch (radioButton.getId()) {
                    case R.id.cpf:
                        company.setDocument_type("CPF");
                        break;
                    case R.id.cnpj:
                        company.setDocument_type("CNPJ");
                        break;
                }
                if (getActivity() instanceof NovaEmpresa) {
                    NovaEmpresa listener = (NovaEmpresa) getActivity();
                    listener.cycleCad("1_1", company);
                }
                radioButton.setChecked(false);
            }
        });
        return view;
    }

    public interface NovaEmpresa {
        void cycleCad(String index, Company company);
    }
}
