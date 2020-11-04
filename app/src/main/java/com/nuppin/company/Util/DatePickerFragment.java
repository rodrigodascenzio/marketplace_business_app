package com.nuppin.company.Util;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

public class DatePickerFragment extends DialogFragment {

    int dia, mes, ano;

    public DatePickerFragment(){}

    public static DatePickerFragment newInstance(int dia, int mes, int ano) {
        DatePickerFragment fragment = new DatePickerFragment();
        Bundle args = new Bundle();
        args.putInt("dia", dia);
        args.putInt("mes", mes);
        args.putInt("ano", ano);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            dia = getArguments().getInt("dia");
            mes = getArguments().getInt("mes");
            ano = getArguments().getInt("ano");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new DatePickerDialog(getActivity(), (DatePickerDialog.OnDateSetListener) getParentFragment(),ano,mes,dia);
    }


}