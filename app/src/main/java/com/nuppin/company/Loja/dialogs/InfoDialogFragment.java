package com.nuppin.company.Loja.dialogs;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.nuppin.company.R;
import com.nuppin.company.Util.AppConstants;
import com.nuppin.company.Util.UtilShaPre;

public class InfoDialogFragment extends DialogFragment {
    private InfoDialogListener listener;

    private String stringMessage, stringPositive, stringNegative;

    public static InfoDialogFragment newInstance(String stringMessage, String btnPositive, String btnNegative) {
        InfoDialogFragment fragment = new InfoDialogFragment();
        Bundle args = new Bundle();
        args.putString("stringMessage", stringMessage);
        args.putString("stringPositive", btnPositive);
        args.putString("stringNegative", btnNegative);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            stringMessage = getArguments().getString("stringMessage");
            stringPositive = getArguments().getString("stringPositive");
            stringNegative = getArguments().getString("stringNegative");
        }
        try {
            listener = (InfoDialogListener) getParentFragment();
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
        View layout = inflater.inflate(R.layout.fr_dialog, null);

        final Button btnPositive = layout.findViewById(R.id.btnPositive);
        final Button btnNegative = layout.findViewById(R.id.btnNegative);
        TextView message = layout.findViewById(R.id.txtMessage);
        message.setText(stringMessage);
        btnPositive.setText(stringPositive);
        btnNegative.setText(stringNegative);
        //
        btnPositive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onDialogOKClick(btnPositive, InfoDialogFragment.this);
            }
        });
        btnNegative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onDialogOKClick(btnNegative, InfoDialogFragment.this);
            }
        });
        builder.setView(layout);
        return builder.create();
    }


    public interface InfoDialogListener {
        void onDialogOKClick(View view, InfoDialogFragment infoDialogFragment);

    }
}