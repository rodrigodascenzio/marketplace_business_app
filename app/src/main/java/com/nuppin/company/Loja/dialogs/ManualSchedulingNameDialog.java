package com.nuppin.company.Loja.dialogs;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.nuppin.company.R;
import com.nuppin.company.Util.AppConstants;
import com.nuppin.company.Util.UtilShaPre;

public class ManualSchedulingNameDialog extends DialogFragment {
    private CustomerName listener;

    private String stringPositive, stringNegative;

    public static ManualSchedulingNameDialog newInstance(String btnPositive, String btnNegative) {
        ManualSchedulingNameDialog fragment = new ManualSchedulingNameDialog();
        Bundle args = new Bundle();
        args.putString("stringPositive", btnPositive);
        args.putString("stringNegative", btnNegative);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            stringPositive = getArguments().getString("stringPositive");
            stringNegative = getArguments().getString("stringNegative");
        }
        try {
            listener = (CustomerName) getParentFragment();
        } catch (ClassCastException e) {
            FirebaseCrashlytics.getInstance().log(UtilShaPre.getDefaultsString(AppConstants.USER_ID, getContext()));
                FirebaseCrashlytics.getInstance().recordException(e);
            throw new ClassCastException("must implement InfoListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View layout = inflater.inflate(R.layout.fr_scheduling_name_manual_dialog, null);

        final Button btnPositive = layout.findViewById(R.id.btnPositive);
        final Button btnNegative = layout.findViewById(R.id.btnNegative);
        final EditText nome = layout.findViewById(R.id.nome);
        btnPositive.setText(stringPositive);
        btnNegative.setText(stringNegative);

        btnPositive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (nome.getText().toString().equals("")) {
                    Toast.makeText(getContext(), "Nome invalido", Toast.LENGTH_SHORT).show();
                    return;
                }
                listener.onDialogOKClick(btnPositive, nome.getText().toString(), ManualSchedulingNameDialog.this);
            }
        });
        btnNegative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onDialogOKClick(btnNegative, "", ManualSchedulingNameDialog.this);
            }
        });
        builder.setView(layout);
        return builder.create();
    }


    public interface CustomerName {
        void onDialogOKClick(View view, String value, ManualSchedulingNameDialog infoDialogFragment);

    }
}