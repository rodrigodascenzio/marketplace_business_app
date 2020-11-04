package com.nuppin.company.Loja.dialogs;

import android.app.Dialog;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.nuppin.company.R;
import com.nuppin.company.Util.AppConstants;
import com.nuppin.company.Util.UtilShaPre;

public class PreviewBannerPhotoDialogFragment extends DialogFragment {
    private PreviewDialogListener listener;

    private String stringPositive, stringNegative;
    private Uri uri;

    public static PreviewBannerPhotoDialogFragment newInstance(Uri uri, String btnPositive, String btnNegative) {
        PreviewBannerPhotoDialogFragment fragment = new PreviewBannerPhotoDialogFragment();
        Bundle args = new Bundle();
        args.putParcelable("uri", uri);
        args.putString("stringPositive", btnPositive);
        args.putString("stringNegative", btnNegative);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            uri = getArguments().getParcelable("uri");
            stringPositive = getArguments().getString("stringPositive");
            stringNegative = getArguments().getString("stringNegative");
        }
        try {
            listener = (PreviewDialogListener) getParentFragment();
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
        View layout = inflater.inflate(R.layout.fr_dialog_banner_photo, null);

        final Button btnPositive = layout.findViewById(R.id.btnPositive);
        final Button btnNegative = layout.findViewById(R.id.btnNegative);
        SimpleDraweeView preview = layout.findViewById(R.id.imagePreview);
        preview.setImageURI(uri);
        btnPositive.setText(stringPositive);
        btnNegative.setText(stringNegative);

        btnPositive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onDialogOKClick(btnPositive, uri, PreviewBannerPhotoDialogFragment.this);
            }
        });
        btnNegative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onDialogOKClick(btnNegative, null, PreviewBannerPhotoDialogFragment.this);
            }
        });
        builder.setView(layout);
        return builder.create();
    }


    public interface PreviewDialogListener {
        void onDialogOKClick(View view, Uri uri, PreviewBannerPhotoDialogFragment infoDialogFragment);

    }
}