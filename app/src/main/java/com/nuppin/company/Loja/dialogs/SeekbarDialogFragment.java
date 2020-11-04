package com.nuppin.company.Loja.dialogs;

import android.app.Dialog;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.nuppin.company.R;
import com.nuppin.company.Util.AppConstants;
import com.nuppin.company.Util.Util;
import com.nuppin.company.Util.UtilShaPre;

public class SeekbarDialogFragment extends DialogFragment {
    private SeekbarDialogListener listener;

    private String stringPositive, stringNegative;
    private int value;

    public static SeekbarDialogFragment newInstance(String value, String btnPositive, String btnNegative) {
        SeekbarDialogFragment fragment = new SeekbarDialogFragment();
        Bundle args = new Bundle();
        args.putString("uri", value);
        args.putString("stringPositive", btnPositive);
        args.putString("stringNegative", btnNegative);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            if (getArguments().getString("uri") != null && !getArguments().getString("uri").equals("")) {
                value = Integer.parseInt(Util.clearNotNumber(getArguments().getString("uri")));
            } else {
                value = 0;
            }
            stringPositive = getArguments().getString("stringPositive");
            stringNegative = getArguments().getString("stringNegative");
        }
        try {
            listener = (SeekbarDialogListener) getParentFragment();
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
        View layout = inflater.inflate(R.layout.fr_seekbar_dialog, null);

        final Button btnPositive = layout.findViewById(R.id.btnPositive);
        final Button btnNegative = layout.findViewById(R.id.btnNegative);
        SeekBar seekBar = layout.findViewById(R.id.seekbar);
        final TextView txtValue = layout.findViewById(R.id.seekbarValue);
        seekBar.setProgress(value/5);
        txtValue.setText(String.valueOf(value));
        btnPositive.setText(stringPositive);
        btnNegative.setText(stringNegative);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                i = i * 5;
                value = i;
                txtValue.setText(getString(R.string.entrega_min_max_int, i));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        btnPositive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onDialogOKClick(btnPositive, value, SeekbarDialogFragment.this);
            }
        });
        btnNegative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onDialogOKClick(btnNegative, value, SeekbarDialogFragment.this);
            }
        });
        builder.setView(layout);
        return builder.create();
    }


    public interface SeekbarDialogListener {
        void onDialogOKClick(View view, int value, SeekbarDialogFragment infoDialogFragment);

    }
}