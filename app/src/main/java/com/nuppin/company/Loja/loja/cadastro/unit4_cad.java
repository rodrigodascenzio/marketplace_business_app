package com.nuppin.company.Loja.loja.cadastro;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.nuppin.company.model.Company;
import com.nuppin.company.R;
import com.nuppin.company.Util.AppConstants;
import com.nuppin.company.Util.GpsUtils;
import com.nuppin.company.Util.Util;
import com.nuppin.company.Util.UtilShaPre;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

public class unit4_cad extends Fragment {
    private Company company;
    private static final String COMPANY = "COMPANY";
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    private boolean isGPS = false;
    private CardView progress;
    private TextInputLayout cidadeWrap, bairroWrap, ruaWrap, numeroWrap, estadoWrap;
    private TextView txtSub;
    private EditText eCidade, eBairro, eNumero, eRua, eEstado, eComplemento;
    private Button btn, addresBtn, locationBtn;
    private ConstraintLayout um, dois;

    public unit4_cad() {
        // Required empty public constructor
    }

    public static unit4_cad newInstance(Company company) {
        unit4_cad fragment = new unit4_cad();
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

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(Objects.requireNonNull(getActivity()));
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.unit4_cad, container, false);

        um = view.findViewById(R.id.location);
        dois = view.findViewById(R.id.manualEndereco);
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
        addresBtn = view.findViewById(R.id.addressBtn);
        txtSub = view.findViewById(R.id.txtSub);
        progress = view.findViewById(R.id.progress);
        eComplemento = view.findViewById(R.id.edtComplemento);

        addresBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                um.setVisibility(View.GONE);
                dois.setVisibility(View.VISIBLE);
                eEstado.setText("");
                eCidade.setText("");
                eBairro.setText("");
                eNumero.setText("");
                eRua.setText("");
                eComplemento.setText("");
            }
        });

        locationBtn = view.findViewById(R.id.locationBtn);

        locationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                locationBtn.setClickable(false);
                new GpsUtils(unit4_cad.this, requireContext(), AppConstants.GPS_REQUEST).turnGPSOn(new GpsUtils.onGpsListener() {
                    @Override
                    public void gpsStatus(boolean isGPSEnable) {
                        progress.setVisibility(View.VISIBLE);
                        // turn on GPS
                        isGPS = isGPSEnable;
                        getLocation();
                    }
                });
                if (!isGPS) {
                    Toast.makeText(getContext(), R.string.turn_on_gps, Toast.LENGTH_SHORT).show();
                }
            }
        });

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

        Toolbar toolbar = view.findViewById(R.id.tb_main_top);
        Util.setaToolbar(this, 0, toolbar, getActivity(), false, 0);

        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(10 * 1000); // 10 seconds
        locationRequest.setFastestInterval(5 * 1000); // 5 seconds

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    progress.setVisibility(View.GONE);
                    locationBtn.setClickable(true);
                    Toast.makeText(getContext(), R.string.location_error, Toast.LENGTH_SHORT).show();
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    if (location != null) {
                        getAddressFromLocation(getContext(), location.getLatitude(), location.getLongitude());
                        if (mFusedLocationClient != null) {
                            mFusedLocationClient.removeLocationUpdates(locationCallback);
                        }
                    } else {
                        progress.setVisibility(View.GONE);
                        locationBtn.setClickable(true);
                        Toast.makeText(getContext(), R.string.location_error, Toast.LENGTH_SHORT).show();
                    }
                }
            }
        };
        return view;
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
                }else{
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

            Util.splitAddressLocation(endereco, company);
            company.setCountry_code(location.getCountryCode());
            company.setLatitude(location.getLatitude());
            company.setLongitude(location.getLongitude());
            company.setComplement_address(eComplemento.getText().toString());

            Util.cleanRetryAddress(context);
            progress.setVisibility(View.GONE);
            if (getActivity() instanceof NovaEmpresa) {
                NovaEmpresa listener = (NovaEmpresa) getActivity();
                listener.cycleCad("5", company);
            }

        } catch (IndexOutOfBoundsException e) {
            FirebaseCrashlytics.getInstance().log(UtilShaPre.getDefaultsString(AppConstants.USER_ID, getContext()));
            FirebaseCrashlytics.getInstance().recordException(e);
            if (Util.retryAddressError(context)) {
                getLocationFromAddress(context, endereco);
            }else{
                btn.setClickable(true);
                progress.setVisibility(View.GONE);
                Toast.makeText(context, R.string.endereco_nao_encontrado, Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            FirebaseCrashlytics.getInstance().log(UtilShaPre.getDefaultsString(AppConstants.USER_ID, getContext()));
            FirebaseCrashlytics.getInstance().recordException(e);
            if (Util.retryAddressError(context)) {
                getLocationFromAddress(context, endereco);
            }else{
                btn.setClickable(true);
                progress.setVisibility(View.GONE);
                Toast.makeText(context, R.string.erro_layout_sub, Toast.LENGTH_SHORT).show();
            }
        }
    }


    private void getAddressFromLocation(Context context, double lat, double lon) {

        Geocoder coder = new Geocoder(context);
        List<Address> address;

        try {
            address = coder.getFromLocation(lat, lon, 1);
            if (address == null) {
                if (Util.retryAddressError(context)) {
                    getAddressFromLocation(context, lat, lon);
                } else {
                    locationBtn.setClickable(true);
                    progress.setVisibility(View.GONE);
                    Toast.makeText(context, R.string.endereco_nao_encontrado, Toast.LENGTH_SHORT).show();
                    return;
                }
            }

            Address location = address.get(0);

            UtilShaPre.setDefaults(AppConstants.COUNTRY_CODE, location.getCountryCode(), getContext());
            UtilShaPre.setDefaults(AppConstants.LATITUDE, String.valueOf(lat), getContext());
            UtilShaPre.setDefaults(AppConstants.LONGITUDE, String.valueOf(lon), getContext());


            try {
                company.setCountry_code(location.getCountryCode());
                company.setLatitude(lat);
                company.setLongitude(lon);
                Util.splitAddressLocationWithoutNumber(location.getAddressLine(0), company);
                eEstado.setText(company.getState_code());

            } catch (Exception e) {
                FirebaseCrashlytics.getInstance().log(UtilShaPre.getDefaultsString(AppConstants.USER_ID, getContext()));
                FirebaseCrashlytics.getInstance().recordException(e);
                company.setCountry_code(location.getCountryCode());
                company.setCity(location.getSubAdminArea());
                company.setDistrict(location.getSubLocality());
                company.setStreet(location.getThoroughfare());
                company.setLatitude(lat);
                company.setLongitude(lon);
            }

            um.setVisibility(View.GONE);
            dois.setVisibility(View.VISIBLE);

            eCidade.setText(company.getCity());
            eBairro.setText(company.getDistrict());
            eRua.setText(company.getStreet());
            eNumero.requestFocus();
            progress.setVisibility(View.GONE);
            locationBtn.setClickable(true);
            Util.cleanRetryAddress(context);

        } catch (Exception ex) {
            FirebaseCrashlytics.getInstance().log(UtilShaPre.getDefaultsString(AppConstants.USER_ID, getContext()));
            FirebaseCrashlytics.getInstance().recordException(ex);
            if (Util.retryAddressError(context)) {
                getAddressFromLocation(context, lat, lon);
            }else{
                progress.setVisibility(View.GONE);
                locationBtn.setClickable(true);
                Toast.makeText(context, R.string.erro_layout_sub, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(Objects.requireNonNull(getActivity()), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    AppConstants.LOCATION_REQUEST);
        } else {
            mFusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
        }
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // If request is cancelled, the result arrays are empty.
        if (requestCode == 1000) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                mFusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
            } else {
                locationBtn.setClickable(true);
                progress.setVisibility(View.GONE);
                Toast.makeText(getActivity(), R.string.permission_denied, Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == AppConstants.GPS_REQUEST) {
                progress.setVisibility(View.VISIBLE);
                // turn on GPS
                isGPS = true;
                getLocation();
            }
        } else {
            if (requestCode == AppConstants.GPS_REQUEST) {
                progress.setVisibility(View.GONE);
                locationBtn.setClickable(true);
            }
        }
    }

    public interface NovaEmpresa {
        void cycleCad(String index, Company company);
    }
}
