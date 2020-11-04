package com.nuppin.company.pos.produto.dialog;

import android.app.Dialog;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.nuppin.company.R;
import com.nuppin.company.Util.AppConstants;
import com.nuppin.company.Util.Util;
import com.nuppin.company.Util.UtilShaPre;
import com.nuppin.company.model.Company;

import java.util.List;

public class DialogUserAddress extends DialogFragment {
    private CardView progress;
    private TextInputLayout cidadeWrap, bairroWrap, ruaWrap, numeroWrap, estadoWrap;
    private EditText eCidade, eBairro, eNumero, eRua, eEstado, eComplemento;
    private Button btn;
    private UsuarioAddresLatLon listener;
    private static String ENDERECO = "ENDERECO";
    private String addressEdit;

    public DialogUserAddress() {
        // Required empty public constructor
    }

    public static DialogUserAddress instance(String address) {
        DialogUserAddress fragment = new DialogUserAddress();
        Bundle args = new Bundle();
        args.putString(ENDERECO, address);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null && getArguments().containsKey(ENDERECO)) {
            addressEdit = getArguments().getString(ENDERECO);
        }

        try {
            listener = (UsuarioAddresLatLon) getParentFragment();
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
        View view = inflater.inflate(R.layout.manual_pos_address, null);

        eEstado = view.findViewById(R.id.edtEstado);
        eCidade = view.findViewById(R.id.edtCidade);
        eBairro = view.findViewById(R.id.edtBairro);
        eNumero = view.findViewById(R.id.edtNumero);
        estadoWrap = view.findViewById(R.id.estadoWrap);
        cidadeWrap = view.findViewById(R.id.cidadeWrap);
        bairroWrap = view.findViewById(R.id.bairroWrap);
        numeroWrap = view.findViewById(R.id.numeroWrap);
        ruaWrap = view.findViewById(R.id.ruaWrap);
        eRua = view.findViewById(R.id.edtRua);
        btn = view.findViewById(R.id.btnCadastrar);
        progress = view.findViewById(R.id.progress);
        eComplemento = view.findViewById(R.id.edtComplemento);

        if (addressEdit != null && !addressEdit.isEmpty()) {
            Company companyAddressEdit = new Company();
            Util.splitAddressLocation(addressEdit, companyAddressEdit);

            try {
                eNumero.setText(companyAddressEdit.getStreet_number());
                eEstado.setText(companyAddressEdit.getState_code());
                eCidade.setText(companyAddressEdit.getCity());
                eBairro.setText(companyAddressEdit.getDistrict());
                eRua.setText(companyAddressEdit.getStreet());
                eComplemento.setText(companyAddressEdit.getComplement_address());
            } catch (Exception e) {

            }
        }

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validar()) {
                    progress.setVisibility(View.VISIBLE);
                    String stringAddress;
                    if (eComplemento.getText().toString().isEmpty()) {
                        stringAddress = Util.joinAddress(eRua.getText().toString(), eNumero.getText().toString(), eBairro.getText().toString(), eCidade.getText().toString(), eEstado.getText().toString(), requireContext());
                    } else {
                        stringAddress = Util.joinAddress(eRua.getText().toString(), eNumero.getText().toString(), eBairro.getText().toString(), eCidade.getText().toString(), eEstado.getText().toString(), eComplemento.getText().toString(), requireContext());
                    }
                    btn.setClickable(false);
                    getLocationFromAddress(getContext(), stringAddress);
                }
            }
        });

        builder.setView(view);
        return builder.create();
    }

    private boolean validar() {
        boolean b = true;
        if (eRua.getText().toString().trim().isEmpty()) {
            ruaWrap.setErrorEnabled(true);
            ruaWrap.setError(getResources().getString(R.string.error_enabled_text));
            b = false;
        } else {
            ruaWrap.setErrorEnabled(false);
        }
        if (eNumero.getText().toString().trim().isEmpty()) {
            numeroWrap.setErrorEnabled(true);
            numeroWrap.setError(getResources().getString(R.string.error_enabled_text));
            b = false;
        } else {
            numeroWrap.setErrorEnabled(false);
        }
        if (eBairro.getText().toString().trim().isEmpty()) {
            bairroWrap.setErrorEnabled(true);
            bairroWrap.setError(getResources().getString(R.string.error_enabled_text));
            b = false;
        } else {
            bairroWrap.setErrorEnabled(false);
        }
        if (eCidade.getText().toString().trim().isEmpty()) {
            cidadeWrap.setErrorEnabled(true);
            cidadeWrap.setError(getResources().getString(R.string.error_enabled_text));
            b = false;
        } else {
            cidadeWrap.setErrorEnabled(false);
        }
        if (eEstado.getText().toString().trim().isEmpty()) {
            estadoWrap.setErrorEnabled(true);
            estadoWrap.setError(getResources().getString(R.string.error_enabled_text));
            b = false;
        } else {
            estadoWrap.setErrorEnabled(false);
        }
        return b;
    }

    private void getLocationFromAddress(Context context, String endereco) {

        Geocoder coder = new Geocoder(context);
        List<Address> address;

        try {
            address = coder.getFromLocationName(endereco, 1);
            if (address == null) {
                if (Util.retryAddressError(context)) {
                    getLocationFromAddress(context, endereco);
                } else {
                    btn.setClickable(true);
                    progress.setVisibility(View.GONE);
                    Toast.makeText(context, R.string.endereco_nao_encontrado, Toast.LENGTH_SHORT).show();
                    return;
                }
            }

            Address location = address.get(0);

            UtilShaPre.setDefaults(AppConstants.COUNTRY_CODE, location.getCountryCode(), getContext());
            UtilShaPre.setDefaults(AppConstants.LATITUDE, String.valueOf(location.getLatitude()), getContext());
            UtilShaPre.setDefaults(AppConstants.LONGITUDE, String.valueOf(location.getLongitude()), getContext());


            Company company = new Company();
            Util.splitAddressLocation(endereco, company);
            company.setCountry_code(location.getCountryCode());
            company.setLatitude(location.getLatitude());
            company.setLongitude(location.getLongitude());
            company.setComplement_address(eComplemento.getText().toString());

            Util.cleanRetryAddress(context);
            progress.setVisibility(View.GONE);

            listener.infosAddres(endereco, company.getLatitude(), company.getLongitude(), DialogUserAddress.this);

        } catch (IndexOutOfBoundsException e) {
            FirebaseCrashlytics.getInstance().log(UtilShaPre.getDefaultsString(AppConstants.USER_ID, getContext()));
            FirebaseCrashlytics.getInstance().recordException(e);
            if (Util.retryAddressError(context)) {
                getLocationFromAddress(context, endereco);
            } else {
                btn.setClickable(true);
                progress.setVisibility(View.GONE);
                Toast.makeText(context, R.string.endereco_nao_encontrado, Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            FirebaseCrashlytics.getInstance().log(UtilShaPre.getDefaultsString(AppConstants.USER_ID, getContext()));
            FirebaseCrashlytics.getInstance().recordException(e);
            if (Util.retryAddressError(context)) {
                getLocationFromAddress(context, endereco);
            } else {
                btn.setClickable(true);
                progress.setVisibility(View.GONE);
                Toast.makeText(context, R.string.erro_layout_sub, Toast.LENGTH_SHORT).show();
            }
        }
    }

    public interface UsuarioAddresLatLon {
        void infosAddres(String address, double latitude, double longitude, DialogUserAddress dialogUserAddress);
    }
}
