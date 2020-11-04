package com.nuppin.company.pos.produto.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.nuppin.company.R;
import com.nuppin.company.Util.AppConstants;
import com.nuppin.company.Util.MaskEditUtil;
import com.nuppin.company.Util.UtilShaPre;

public class TrocoDialogFragmentManual extends DialogFragment {
    private TrocoDialogListener listener;

    String stringMessage, stringPositive, stringNegative;

    public static TrocoDialogFragmentManual newInstance(String stringMessage, String btnPositive, String btnNegative) {
        TrocoDialogFragmentManual fragment = new TrocoDialogFragmentManual();
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
            listener = (TrocoDialogListener) getParentFragment();
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
        View layout = inflater.inflate(R.layout.manual_fr_dialog_troco, null);

        final Button btnPositive = layout.findViewById(R.id.btnPositive);
        final Button btnNegative = layout.findViewById(R.id.btnNegative);
        TextView message = layout.findViewById(R.id.about_fragment);
        final EditText editText = layout.findViewById(R.id.edtTroco);
        final TextInputLayout editWrap = layout.findViewById(R.id.trocoWrap);
        editText.addTextChangedListener(MaskEditUtil.monetario(editText));

        message.setText(stringMessage);
        btnPositive.setText(stringPositive);
        btnNegative.setText(stringNegative);
        //
        btnPositive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!editText.getText().toString().isEmpty() && !editText.getText().toString().equals("0,00")) {
                    listener.onDialogOKClick(btnPositive, "R$"+editText.getText().toString(), TrocoDialogFragmentManual.this);
                }else {
                    editWrap.setErrorEnabled(true);
                    editText.setError("Valor incorreto");
                }
            }
        });
        builder.setView(layout);
        return builder.create();
    }


    public interface TrocoDialogListener {
        void onDialogOKClick(View view, String value, TrocoDialogFragmentManual infoDialogFragment);

    }
}