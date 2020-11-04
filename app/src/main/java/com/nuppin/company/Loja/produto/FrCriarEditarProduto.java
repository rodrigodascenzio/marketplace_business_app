package com.nuppin.company.Loja.produto;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.AsyncTaskLoader;
import androidx.loader.content.Loader;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.gson.Gson;
import com.google.gson.JsonParser;
import com.nuppin.company.Loja.dialogs.InfoDialogFragment;
import com.nuppin.company.Util.AppConstants;
import com.nuppin.company.Util.UtilShaPre;
import com.nuppin.company.connection.ConnectApi;
import com.nuppin.company.model.ErrorCode;
import com.nuppin.company.model.Product;
import com.nuppin.company.model.Company;
import com.nuppin.company.R;
import com.nuppin.company.Util.ImageCompress;
import com.nuppin.company.Util.MaskEditUtil;
import com.nuppin.company.Util.RealPathUtil;
import com.nuppin.company.Util.Util;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static android.app.Activity.RESULT_OK;

public class FrCriarEditarProduto extends Fragment
        implements LoaderManager.LoaderCallbacks<Map<String, Object>>, View.OnClickListener, InfoDialogFragment.InfoDialogListener {

    private static final String EDITAR_PRODUTO = "EDITAR";
    private static final String NOVO_PRODUTO = "NOVO";
    private static final int FOTO_COMPRESS_OTHER_THREAD = 99;
    private Product product;
    private LoaderManager loaderManager;
    private EditText nomeProduto, descProduto, preco, qtdEstoque, externalId;
    private TextInputLayout estoqueWrap, nomeWrap, descWrap, precoWrap;
    private SwitchCompat isEstoque, isMultiStock;
    private SimpleDraweeView imageCadastro;
    private String mfotoPath;
    private CardView progress;
    private boolean isFotoPath;
    private static final int REQUEST_FOTO = 1;
    private boolean editar = false;
    private Company company;
    private MaterialButton btn, btnDeleta;
    private byte[] imageCompressed;
    private Uri uri;


    public FrCriarEditarProduto() {
    }

    public static FrCriarEditarProduto newInstance(Product product) {
        FrCriarEditarProduto fragment = new FrCriarEditarProduto();
        Bundle args = new Bundle();
        args.putParcelable(EDITAR_PRODUTO, product);
        fragment.setArguments(args);
        return fragment;
    }

    public static FrCriarEditarProduto newInstance2(Company company) {
        FrCriarEditarProduto fragment = new FrCriarEditarProduto();
        Bundle args = new Bundle();
        args.putParcelable(NOVO_PRODUTO, company);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null && getArguments().containsKey(EDITAR_PRODUTO)) {
            editar = true;
            product = getArguments().getParcelable(EDITAR_PRODUTO);
        }
        if (getArguments() != null && getArguments().containsKey(NOVO_PRODUTO)) {
            company = getArguments().getParcelable(NOVO_PRODUTO);
        }
        loaderManager = LoaderManager.getInstance(this);

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.add_update_product, container, false);

        Toolbar toolbar = view.findViewById(R.id.toolbar_top);
        Util.setaToolbar(this, 0, toolbar, getActivity(), true, R.drawable.ic_clear_white_24dp);


        isEstoque = view.findViewById(R.id.isEstoque);
        qtdEstoque = view.findViewById(R.id.estoque);
        estoqueWrap = view.findViewById(R.id.estoqueWrap);
        nomeProduto = view.findViewById(R.id.nome);
        nomeWrap = view.findViewById(R.id.nomeWrap);
        descProduto = view.findViewById(R.id.desc);
        descWrap = view.findViewById(R.id.descWrap);
        preco = view.findViewById(R.id.preco);
        precoWrap = view.findViewById(R.id.precoWrap);
        preco.addTextChangedListener(MaskEditUtil.monetario(preco));
        imageCadastro = view.findViewById(R.id.imageCadastro);
        imageCadastro.setOnClickListener(this);
        btn = view.findViewById(R.id.botao);
        progress = view.findViewById(R.id.progress);
        btnDeleta = view.findViewById(R.id.botaoDeleta);
        externalId = view.findViewById(R.id.codigo);
        isMultiStock = view.findViewById(R.id.multiEstoque);

        btnDeleta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment dialogFrag = InfoDialogFragment.newInstance("Confirmar exclusão?", getResources().getString(R.string.confirmar), getResources().getString(R.string.revisar));
                dialogFrag.show(FrCriarEditarProduto.this.getChildFragmentManager(), "confirm");
            }
        });

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validar()) {
                    btnDeleta.setClickable(false);
                    btn.setClickable(false);
                    product.setExternal_code(Util.clearExternalId(externalId.getText().toString()));
                    product.setName(Util.clearName(nomeProduto.getText().toString()));
                    product.setDescription(descProduto.getText().toString());
                    product.setPrice(Util.unmaskPrice(preco.getText().toString()));
                    if (isEstoque.isChecked()) {
                        if (isMultiStock.isChecked()) {
                            product.setIs_multi_stock(1);
                            product.setIs_stock(0);
                        } else {
                            product.setIs_stock(1);
                            product.setIs_multi_stock(0);
                            product.setStock_quantity(Integer.parseInt(qtdEstoque.getText().toString()));
                        }
                    } else {
                        product.setIs_stock(0);
                        product.setIs_multi_stock(0);
                    }
                    if (!editar) {
                        loaderManager.restartLoader(0, null, FrCriarEditarProduto.this);
                    } else {
                        loaderManager.restartLoader(1, null, FrCriarEditarProduto.this);
                    }
                }
            }
        });

        isEstoque.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    estoqueWrap.setVisibility(View.VISIBLE);
                    isMultiStock.setVisibility(View.VISIBLE);
                } else {
                    estoqueWrap.setVisibility(View.GONE);
                    isMultiStock.setVisibility(View.GONE);
                }
            }
        });
        isMultiStock.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    estoqueWrap.setVisibility(View.GONE);
                } else {
                    estoqueWrap.setVisibility(View.VISIBLE);
                }
            }
        });

        if (editar) {
            btnDeleta.setVisibility(View.VISIBLE);
            externalId.setText(product.getExternal_code());
            nomeProduto.setText(product.getName());
            descProduto.setText(product.getDescription());
            preco.setText(Util.formatDecimaisDoubleToString(product.getPrice()));
            qtdEstoque.setText(String.valueOf(product.getStock_quantity()));
            Util.hasPhoto(product, imageCadastro);
            if (product.getIs_stock() == 1) {
                isEstoque.setChecked(true);
                estoqueWrap.setVisibility(View.VISIBLE);
            }
            if (product.getIs_multi_stock() == 1) {
                isMultiStock.setChecked(true);
                isEstoque.setChecked(true);
                estoqueWrap.setVisibility(View.GONE);
            }
        } else {
            product = new Product();
        }
        return view;
    }

    private boolean validar() {
        boolean b = true;
        if (nomeProduto.getText().toString().isEmpty()) {
            nomeWrap.setErrorEnabled(true);
            nomeWrap.setError(getResources().getString(R.string.error_enabled_text));
            b = false;
        } else {
            nomeWrap.setErrorEnabled(false);
        }
        if (preco.getText().toString().isEmpty() || preco.getText().toString().equals("0,00")) {
            precoWrap.setErrorEnabled(true);
            precoWrap.setError(getResources().getString(R.string.error_enabled_text));
            b = false;
        } else {
            precoWrap.setErrorEnabled(false);
        }
        if (isEstoque.isChecked() && !isMultiStock.isChecked()) {
            if (qtdEstoque.getText().toString().isEmpty()) {
                estoqueWrap.setErrorEnabled(true);
                estoqueWrap.setError(getResources().getString(R.string.error_enabled_text));
                b = false;
            } else {
                estoqueWrap.setErrorEnabled(false);
            }
        }
        return b;
    }

    //RETORNO DO ARQUIVO ESCOLHIDO NA GALERIA
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == REQUEST_FOTO) {
            btn.setClickable(false);
            uri = data.getData();
            mfotoPath = RealPathUtil.getRealPath(getContext(), data.getData());
            isFotoPath = true;
            loaderManager.restartLoader(FOTO_COMPRESS_OTHER_THREAD, null, this);
        }
    }

    //AO CLICAR NA IMAGEVIEW
    public void onClick(View view) {
        if (view.getId() == R.id.imageCadastro) {
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

    @Override
    public Loader<Map<String, Object>> onCreateLoader(int id, Bundle args) {
        progress.setVisibility(View.VISIBLE);
        if (id == 0) {
            return new LoaderPP(getActivity(), product, editar, company.getCompany_id(), mfotoPath, imageCompressed);
        } else if (id == 1) {
            return new EditaProduto(getActivity(), product, mfotoPath, isFotoPath, imageCompressed);
        } else if (id == 2) {
            return new DeletaProduto(getActivity(), product);
        } else {
            return new CompressImageThread(getContext(), mfotoPath);
        }
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Map<String, Object>> loader, Map<String, Object> data) {
        progress.setVisibility(View.GONE);
        if (data != null) {
            if (loader.getId() != FOTO_COMPRESS_OTHER_THREAD) {
                if (data.get(AppConstants.PRODUCT) instanceof Product) {
                    if (!(data.get("photo") instanceof String)) {
                        Toast.makeText(getContext(), R.string.error_photo, Toast.LENGTH_SHORT).show();
                    }
                    Util.backFragmentFunction(this);
                } else if (data.get("delete") instanceof Integer) {
                    if ((Integer) data.get("delete") > 0) {
                        product.setProduct_id(null);
                        Util.backFragmentFunction(this);
                    } else {
                        Toast.makeText(getContext(), R.string.error, Toast.LENGTH_SHORT).show();
                    }
                } else if (data.get(AppConstants.ERROR) instanceof ErrorCode) {
                    Toast.makeText(getContext(), ((ErrorCode) data.get(AppConstants.ERROR)).getError_message(), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), R.string.upload_error, Toast.LENGTH_SHORT).show();
                }
            } else {
                if (data.get("compress") != null && data.get("compress") instanceof byte[]) {
                    imageCompressed = (byte[]) data.get("compress");
                    imageCadastro.setImageURI(uri);
                } else {
                    Toast.makeText(getContext(), R.string.erro_compress_photo, Toast.LENGTH_SHORT).show();
                }
            }
        } else {
            Toast.makeText(getContext(), R.string.error, Toast.LENGTH_SHORT).show();
        }
        btn.setClickable(true);
        btnDeleta.setClickable(true);
        loaderManager.destroyLoader(loader.getId());
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Map<String, Object>> loader) {

    }

    @Override
    public void onDialogOKClick(View view, InfoDialogFragment info) {
        switch (view.getId()) {
            case R.id.btnPositive:
                progress.setVisibility(View.VISIBLE);
                btnDeleta.setClickable(false);
                btn.setClickable(false);
                loaderManager.restartLoader(2, null, FrCriarEditarProduto.this);
                info.dismiss();
                break;
            case R.id.btnNegative:
                info.dismiss();
                break;
        }
    }

    private static class LoaderPP extends AsyncTaskLoader<Map<String, Object>> {

        Product product;
        Context ctx;
        boolean edit;
        String idLoja;
        String fotoPath;
        byte[] imageCompressed;


        LoaderPP(Context context, Product product, boolean edit, String idLoja, String mFotoPath, byte[] imageCompressed) {
            super(context);
            ctx = context;
            this.product = product;
            this.edit = edit;
            this.fotoPath = mFotoPath;
            this.idLoja = idLoja;
            this.imageCompressed = imageCompressed;
        }

        @Override
        protected void onStartLoading() {
            forceLoad();
        }

        @Override
        public Map<String, Object> loadInBackground() {
            Gson gson = new Gson();
            product.setCompany_id(idLoja);
            Map<String, Object> mapOrdPro;
            mapOrdPro = new HashMap<>();

            String json = ConnectApi.POST(product, ConnectApi.PRODUCT, ctx);
            JsonParser parser = new JsonParser();

            try {
                Product p = gson.fromJson(parser.parse(json).getAsJsonObject().getAsJsonObject(AppConstants.PRODUCT), Product.class);
                mapOrdPro.put(AppConstants.PRODUCT, p);
                try {
                    if (fotoPath != null) {
                        String ok = gson.fromJson(ConnectApi.enviarFoto(getContext(), fotoPath, p.getProduct_id(), AppConstants.PRODUCT, imageCompressed), String.class);
                        mapOrdPro.put("photo", ok);
                    } else {
                        mapOrdPro.put("photo", "empty");
                    }
                } catch (Exception e) {
                    FirebaseCrashlytics.getInstance().log(UtilShaPre.getDefaultsString(AppConstants.USER_ID, getContext()));
                FirebaseCrashlytics.getInstance().recordException(e);
                    mapOrdPro.put("photo", null);
                }
            } catch (Exception e) {
                FirebaseCrashlytics.getInstance().log(UtilShaPre.getDefaultsString(AppConstants.USER_ID, getContext()));
                FirebaseCrashlytics.getInstance().recordException(e);
                try {
                    ErrorCode error = gson.fromJson(parser.parse(json).getAsJsonObject().getAsJsonObject(AppConstants.ERROR), ErrorCode.class);
                    mapOrdPro.put(AppConstants.ERROR, error);
                } catch (Exception e1) {
                    FirebaseCrashlytics.getInstance().log(UtilShaPre.getDefaultsString(AppConstants.USER_ID, getContext()));
                    FirebaseCrashlytics.getInstance().recordException(e1);
                    return null;
                }
            }

            return mapOrdPro;
        }
    }

    private static class EditaProduto extends AsyncTaskLoader<Map<String, Object>> {

        Product product;
        Context ctx;
        String fotoPath;
        boolean isFotoPath;
        byte[] imageCompressed;

        EditaProduto(Context context, Product product, String mFotoPath, boolean isFotoPath, byte[] imageCompressed) {
            super(context);
            ctx = context;
            this.product = product;
            this.fotoPath = mFotoPath;
            this.isFotoPath = isFotoPath;
            this.imageCompressed = imageCompressed;
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

            String json = ConnectApi.PATCH(product, ConnectApi.PRODUCT + "/" + product.getProduct_id(), ctx);
            JsonParser parser = new JsonParser();


            try {
                Product p = gson.fromJson(parser.parse(json).getAsJsonObject().getAsJsonObject(AppConstants.PRODUCT), Product.class);
                mapOrdPro.put(AppConstants.PRODUCT, p);
                try {
                    if (isFotoPath) {
                        String ok = gson.fromJson(ConnectApi.enviarFoto(getContext(), fotoPath, product.getProduct_id(), AppConstants.PRODUCT, imageCompressed), String.class);
                        mapOrdPro.put("photo", ok);
                    } else {
                        mapOrdPro.put("photo", "empty");
                    }
                } catch (Exception e) {
                    FirebaseCrashlytics.getInstance().log(UtilShaPre.getDefaultsString(AppConstants.USER_ID, getContext()));
                FirebaseCrashlytics.getInstance().recordException(e);
                    mapOrdPro.put("photo", null);
                }
            } catch (Exception e) {
                FirebaseCrashlytics.getInstance().log(UtilShaPre.getDefaultsString(AppConstants.USER_ID, getContext()));
                FirebaseCrashlytics.getInstance().recordException(e);
                try {
                    ErrorCode error = gson.fromJson(parser.parse(json).getAsJsonObject().getAsJsonObject(AppConstants.ERROR), ErrorCode.class);
                    mapOrdPro.put(AppConstants.ERROR, error);
                } catch (Exception e1) {
                    FirebaseCrashlytics.getInstance().log(UtilShaPre.getDefaultsString(AppConstants.USER_ID, getContext()));
                    FirebaseCrashlytics.getInstance().recordException(e1);
                    return null;
                }
            }
            return mapOrdPro;
        }
    }


    private static class DeletaProduto extends AsyncTaskLoader<Map<String, Object>> {

        Activity ctx;
        Product product;


        DeletaProduto(Activity context, Product product) {
            super(context);
            ctx = context;
            this.product = product;
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
                Gson gson = new Gson();
                mapOrdPro.put("delete", gson.fromJson(ConnectApi.DELETE(product, ConnectApi.PRODUCT, ctx), Integer.class));
                return mapOrdPro;
            } catch (Exception e) {
                FirebaseCrashlytics.getInstance().log(UtilShaPre.getDefaultsString(AppConstants.USER_ID, getContext()));
                FirebaseCrashlytics.getInstance().recordException(e);
                return null;
            }
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

}