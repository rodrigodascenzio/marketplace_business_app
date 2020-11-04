package com.nuppin.company.Loja.loja.perfil;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.AsyncTaskLoader;
import androidx.loader.content.Loader;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.gson.Gson;
import com.nuppin.company.Loja.dialogs.PreviewRoundPhotoDialogFragment;
import com.nuppin.company.Util.AppConstants;
import com.nuppin.company.Util.UtilShaPre;
import com.nuppin.company.connection.ConnectApi;
import com.nuppin.company.model.Company;
import com.nuppin.company.R;
import com.nuppin.company.Util.ImageCompress;
import com.nuppin.company.Util.RealPathUtil;
import com.nuppin.company.Util.Util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static android.app.Activity.RESULT_OK;

public class FrEditarLoja extends Fragment implements
        LoaderManager.LoaderCallbacks<Map<String, Object>>,
        View.OnClickListener,
        PreviewRoundPhotoDialogFragment.PreviewDialogListener {

    private static final String COMPANY = "COMPANY";
    private static final int FOTO_COMPRESS_OTHER_THREAD = 99;
    private Company company;
    private LoaderManager loaderManager;
    private EditText nome;
    private EditText desc;
    private SimpleDraweeView foto;
    private String mfotoPath;
    private static final int REQUEST_FOTO = 1;
    private Button btn;
    private CardView progress;
    private EditText eCidade, eBairro, eNumero, eRua, eEstado, eComplemento, site, insta, face;
    private byte[] imageCompressed;
    private Uri uri;
    private ConstraintLayout imageWrap;

    public FrEditarLoja() {
    }

    public static FrEditarLoja newInstance(Company company) {
        FrEditarLoja fragment = new FrEditarLoja();
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
        loaderManager = LoaderManager.getInstance(this);
    }

    TextWatcher lister = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            btn.setVisibility(View.VISIBLE);
        }
    };

    private void hasAltered(){
        nome.addTextChangedListener(lister);
        desc.addTextChangedListener(lister);
        eRua.addTextChangedListener(lister);
        eNumero.addTextChangedListener(lister);
        eCidade.addTextChangedListener(lister);
        eBairro.addTextChangedListener(lister);
        eEstado.addTextChangedListener(lister);
        eComplemento.addTextChangedListener(lister);
        site.addTextChangedListener(lister);
        insta.addTextChangedListener(lister);
        face.addTextChangedListener(lister);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.update_company, container, false);

        Toolbar toolbar = view.findViewById(R.id.toolbar_top);
        Util.setaToolbar(this, 0, toolbar, getActivity(), true, R.drawable.ic_clear_white_24dp);

        progress = view.findViewById(R.id.progress);
        imageWrap = view.findViewById(R.id.imageWrap);
        nome = view.findViewById(R.id.nome);
        desc = view.findViewById(R.id.desc);
        foto = view.findViewById(R.id.imageCadastro);
        imageWrap.setOnClickListener(this);
        btn = view.findViewById(R.id.botao);

        nome.setText(company.getName());
        desc.setText(company.getDescription());
        Util.hasPhoto(company, foto);

        eRua = view.findViewById(R.id.edtRua);
        eNumero = view.findViewById(R.id.edtNumero);
        eCidade = view.findViewById(R.id.edtCidade);
        eBairro = view.findViewById(R.id.edtBairro);
        eEstado = view.findViewById(R.id.edtEstado);
        eComplemento = view.findViewById(R.id.edtComplemento);

        site = view.findViewById(R.id.edtSite);
        insta = view.findViewById(R.id.edtInsta);
        face = view.findViewById(R.id.edtFace);

        eRua.setText(company.getStreet());
        eNumero.setText(company.getStreet_number());
        eCidade.setText(company.getCity());
        eBairro.setText(company.getDistrict());
        eEstado.setText(company.getState_code());
        eComplemento.setText(company.getComplement_address());
        site.setText(company.getSite());
        insta.setText(company.getInstagram());
        face.setText(company.getFacebook());


        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (nome.getText().toString().equals("")) {
                    Toast.makeText(getContext(), R.string.warning_before_update_store_name, Toast.LENGTH_SHORT).show();
                    return;
                }
                company.setName(Util.clearName(nome.getText().toString()));
                company.setDescription(desc.getText().toString());

                if (editAddress()) {
                    String address;
                    if (eComplemento.getText().toString().isEmpty()) {
                        address = Util.joinAddress(eRua.getText().toString(), eNumero.getText().toString(), eBairro.getText().toString(), eCidade.getText().toString(), eEstado.getText().toString(), requireContext());
                    } else {
                        address = Util.joinAddress(eRua.getText().toString(), eNumero.getText().toString(), eBairro.getText().toString(), eCidade.getText().toString(), eEstado.getText().toString(), eComplemento.getText().toString(), requireContext());
                    }
                    getLocationFromAddress(getContext(), address);
                }

                company.setSite(site.getText().toString().toLowerCase().replace("www.", "").replace("https://", "").replace("http://", "").replace(" ", ""));
                company.setInstagram(insta.getText().toString().toLowerCase().replace("www.", "").replace("instagram.com/", "").replace("instagram.com.br/", "").replace("https://", "").replace("http://", "").replace("/", "").replace("@", "").replace(" ", ""));
                company.setFacebook(face.getText().toString().toLowerCase().replace("www.", "").replace("facebook.com/", "").replace("facebook.com.br/", "").replace("fb.com/", "").replace("https://", "").replace("http://", "").replace("/", "").replace("@", "").replace(" ", ""));

                progress.setVisibility(View.VISIBLE);
                btn.setClickable(false);
                loaderManager.restartLoader(0, null, FrEditarLoja.this);
            }
        });

        final TextView txtEnd = view.findViewById(R.id.txtEnd);
        final ConstraintLayout contEnd = view.findViewById(R.id.contEnd);
        final LinearLayout contRedes = view.findViewById(R.id.contRedes);
        final TextView txtRedes = view.findViewById(R.id.txtRedes);

        txtEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (contEnd.getVisibility() == View.GONE) {
                    txtEnd.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.ic_keyboard_arrow_up_black_24dp), null);
                    contEnd.setVisibility(View.VISIBLE);
                } else {
                    txtEnd.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.ic_keyboard_arrow_down_black_24dp), null);
                    contEnd.setVisibility(View.GONE);
                }
            }
        });

        txtRedes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (contRedes.getVisibility() == View.GONE) {
                    txtRedes.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.ic_keyboard_arrow_up_black_24dp), null);
                    contRedes.setVisibility(View.VISIBLE);
                } else {
                    txtRedes.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.ic_keyboard_arrow_down_black_24dp), null);
                    contRedes.setVisibility(View.GONE);
                }
            }
        });

        hasAltered();

        return view;
    }


    private boolean editAddress() {
        boolean a = false;
        if (!company.getStreet().equals(eRua.getText().toString())) {
            a = true;
        }
        if (!company.getStreet_number().equals(eNumero.getText().toString())) {
            a = true;
        }
        if (!company.getDistrict().equals(eBairro.getText().toString())) {
            a = true;
        }
        if (!company.getCity().equals(eCidade.getText().toString())) {
            a = true;
        }
        if (!company.getState_code().equals(eEstado.getText().toString())) {
            a = true;
        }
        if (company.getComplement_address() != null) {
            if (!company.getComplement_address().equals(eComplemento.getText().toString())) {
                a = true;
            }
        } else {
            if (eComplemento.getText().toString().length() > 1) {
                a = true;
            }
        }
        return a;
    }

    //RETORNO DO ARQUIVO ESCOLHIDO NA GALERIA
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == REQUEST_FOTO) {
            //foto.setImageURI(data.getData());
            mfotoPath = RealPathUtil.getRealPath(getContext(), data.getData());
            DialogFragment dialogFrag = PreviewRoundPhotoDialogFragment.newInstance(data.getData(), getString(R.string.confirmar), getString(R.string.cancelar));
            dialogFrag.show(this.getChildFragmentManager(), "preview_dialog");
        }
    }

    //AO CLICAR NA IMAGEVIEW
    public void onClick(View view) {
        if (view.getId() == R.id.imageWrap) {
            if (ActivityCompat.checkSelfPermission(
                    Objects.requireNonNull(getContext()), Manifest.permission.READ_EXTERNAL_STORAGE) ==
                    PackageManager.PERMISSION_GRANTED) {
                selecionarFoto();
            } else {
                requestPermissions(
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
            }
        }
    }

    //SELECIONAR FOTO NA GALERIA
    private void selecionarFoto() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, REQUEST_FOTO);
    }

    //PEDINDO PERMISSÃO
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            selecionarFoto();
        }
    }

    @NonNull
    @Override
    public Loader<Map<String, Object>> onCreateLoader(int id, Bundle args) {
        if (id == 0) {
            return new EditaLoja(getActivity(), company, mfotoPath, imageCompressed);
        } else if (id == FOTO_COMPRESS_OTHER_THREAD) {
            return new CompressImageThread(getContext(), mfotoPath);
        } else {
            return new SendPhoto(getContext(), company, mfotoPath, imageCompressed);
        }
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Map<String, Object>> loader, Map<String, Object> data) {
        progress.setVisibility(View.GONE);
        if (data != null) {
            switch (loader.getId()) {
                case 0:
                    if (data.get(AppConstants.COMPANY) instanceof Company) {
                        Util.backFragmentFunction(this);
                    } else {
                        Toast.makeText(getContext(), R.string.upload_error, Toast.LENGTH_SHORT).show();
                    }
                    break;
                case FOTO_COMPRESS_OTHER_THREAD:
                    if (data.get("compress") != null && data.get("compress") instanceof byte[]) {
                        imageCompressed = (byte[]) data.get("compress");
                        progress.setVisibility(View.VISIBLE);
                        loaderManager.restartLoader(2, null, this);
                    } else {
                        Toast.makeText(getContext(), R.string.erro_compress_photo, Toast.LENGTH_SHORT).show();
                    }
                    break;
                case 2:
                    if (data.get("photo") instanceof String) {
                        foto.setImageURI(uri);
                        Toast.makeText(getContext(), R.string.upload_photo_success, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getContext(), R.string.error_photo, Toast.LENGTH_SHORT).show();
                    }
            }
        } else {
            Toast.makeText(getContext(), R.string.error, Toast.LENGTH_SHORT).show();
        }
        btn.setClickable(true);
        loaderManager.destroyLoader(loader.getId());
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

            Util.cleanRetryAddress(context);
            progress.setVisibility(View.GONE);
            Address location = address.get(0);

            UtilShaPre.setDefaults(AppConstants.COUNTRY_CODE, location.getCountryCode(), getContext());
            UtilShaPre.setDefaults(AppConstants.LATITUDE, String.valueOf(location.getLatitude()), getContext());
            UtilShaPre.setDefaults(AppConstants.LONGITUDE, String.valueOf(location.getLongitude()), getContext());


            Util.splitAddressLocation(endereco, company);
            company.setCountry_code(location.getCountryCode());
            company.setLatitude(location.getLatitude());
            company.setLongitude(location.getLongitude());
            company.setComplement_address(eComplemento.getText().toString());


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
                Toast.makeText(context, R.string.location_error, Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Map<String, Object>> loader) {}

    @Override
    public void onDialogOKClick(View view, Uri uri, PreviewRoundPhotoDialogFragment preview) {
        switch (view.getId()) {
            case R.id.btnPositive:
                preview.dismiss();
                this.uri = uri;
                loaderManager.restartLoader(FOTO_COMPRESS_OTHER_THREAD, null, this);
                break;
            case R.id.btnNegative:
                preview.dismiss();
                break;
        }
    }

    private static class EditaLoja extends AsyncTaskLoader<Map<String, Object>> {

        Company company;
        Context ctx;
        String fotoPath;
        byte[] imageocompressed;


        EditaLoja(Context context, Company company, String mFotoPath, byte[] imageCompressed) {
            super(context);
            ctx = context;
            this.company = company;
            this.fotoPath = mFotoPath;
            this.imageocompressed = imageCompressed;
        }

        @Override
        protected void onStartLoading() {
            forceLoad();
        }

        @Override
        public Map<String, Object> loadInBackground() {
            Gson gson = new Gson();
            Map<String, Object> mapOrdPro;
            mapOrdPro = new HashMap<>();
            try {
                Company s = gson.fromJson(ConnectApi.PATCH(company, ConnectApi.COMPANY, getContext()), Company.class);
                mapOrdPro.put(AppConstants.COMPANY, s);
            } catch (Exception e) {
                FirebaseCrashlytics.getInstance().log(UtilShaPre.getDefaultsString(AppConstants.USER_ID, getContext()));
                FirebaseCrashlytics.getInstance().recordException(e);
                return null;
            }

            return mapOrdPro;

        }
    }

    private static class CompressImageThread extends AsyncTaskLoader<Map<String, Object>> {

        Context ctx;
        String fotoPath;


        CompressImageThread(Context context, String mFotoPath) {
            super(context);
            ctx = context;
            this.fotoPath = mFotoPath;
        }

        @Override
        protected void onStartLoading() {
            forceLoad();
        }

        @Override
        public Map<String, Object> loadInBackground() {

            Map<String, Object> mapOrdPro;
            mapOrdPro = new HashMap<>();
            try {
                mapOrdPro.put("compress", ImageCompress.compressImage(fotoPath));
            } catch (Exception e) {
                FirebaseCrashlytics.getInstance().log(UtilShaPre.getDefaultsString(AppConstants.USER_ID, getContext()));
                FirebaseCrashlytics.getInstance().recordException(e);
                mapOrdPro.put("compress", null);
            }
            return mapOrdPro;
        }
    }

    private static class SendPhoto extends AsyncTaskLoader<Map<String, Object>> {

        Company company;
        Context ctx;
        String fotoPath;
        byte[] imageocompressed;


        SendPhoto(Context context, Company company, String mFotoPath, byte[] imageCompressed) {
            super(context);
            ctx = context;
            this.company = company;
            this.fotoPath = mFotoPath;
            this.imageocompressed = imageCompressed;
        }

        @Override
        protected void onStartLoading() {
            forceLoad();
        }

        @Override
        public Map<String, Object> loadInBackground() {
            Gson gson = new Gson();
            Map<String, Object> mapOrdPro;
            mapOrdPro = new HashMap<>();
            try {
                String ok = gson.fromJson(ConnectApi.enviarFoto(getContext(), fotoPath, company.getCompany_id(), AppConstants.COMPANY, imageocompressed), String.class);
                mapOrdPro.put("photo", ok);
            } catch (Exception e) {
                FirebaseCrashlytics.getInstance().log(UtilShaPre.getDefaultsString(AppConstants.USER_ID, getContext()));
                FirebaseCrashlytics.getInstance().recordException(e);
                mapOrdPro.put("photo", null);
            }

            return mapOrdPro;

        }
    }

}