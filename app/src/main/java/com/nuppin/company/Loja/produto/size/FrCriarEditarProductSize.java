package com.nuppin.company.Loja.produto.size;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.AsyncTaskLoader;
import androidx.loader.content.Loader;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.gson.Gson;
import com.google.gson.JsonParser;
import com.nuppin.company.Loja.dialogs.InfoDialogFragment;
import com.nuppin.company.R;
import com.nuppin.company.Util.AppConstants;
import com.nuppin.company.Util.MaskEditUtil;
import com.nuppin.company.Util.Util;
import com.nuppin.company.Util.UtilShaPre;
import com.nuppin.company.connection.ConnectApi;
import com.nuppin.company.model.ErrorCode;
import com.nuppin.company.model.Product;
import com.nuppin.company.model.Size;

import java.util.HashMap;
import java.util.Map;

public class FrCriarEditarProductSize extends Fragment
        implements LoaderManager.LoaderCallbacks<Map<String, Object>>,
        InfoDialogFragment.InfoDialogListener {

    private static final String EDITAR_PRODUTO = "EDITAR";
    private static final String NOVO_PRODUTO = "NOVO";
    private Size size;
    private LoaderManager loaderManager;
    private EditText sizeName, sizeValue, sizeStock;
    private TextInputLayout nomeWrap, estoqueWrap;
    private CardView progress;
    private Product product;
    private boolean editar = false;
    private MaterialButton btn, btnDeleta;


    public FrCriarEditarProductSize() {
    }

    public static FrCriarEditarProductSize newInstanceEdit(Size size) {
        FrCriarEditarProductSize fragment = new FrCriarEditarProductSize();
        Bundle args = new Bundle();
        args.putParcelable(EDITAR_PRODUTO, size);
        fragment.setArguments(args);
        return fragment;
    }

    public static FrCriarEditarProductSize newInstanceAdd(Product product) {
        FrCriarEditarProductSize fragment = new FrCriarEditarProductSize();
        Bundle args = new Bundle();
        args.putParcelable(NOVO_PRODUTO, product);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null && getArguments().containsKey(EDITAR_PRODUTO)) {
            editar = true;
            size = getArguments().getParcelable(EDITAR_PRODUTO);
        }
        if (getArguments() != null && getArguments().containsKey(NOVO_PRODUTO)) {
            product = getArguments().getParcelable(NOVO_PRODUTO);
        }
        loaderManager = LoaderManager.getInstance(this);

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.add_update_product_size, container, false);

        Toolbar toolbar = view.findViewById(R.id.toolbar_top);
        Util.setaToolbar(this, 0, toolbar, getActivity(), true, R.drawable.ic_clear_white_24dp);


        sizeStock = view.findViewById(R.id.estoque);
        estoqueWrap = view.findViewById(R.id.estoqueWrap);
        sizeValue = view.findViewById(R.id.preco);
        sizeValue.addTextChangedListener(MaskEditUtil.monetario(sizeValue));
        sizeName = view.findViewById(R.id.nome);
        nomeWrap = view.findViewById(R.id.nomeWrap);
        btn = view.findViewById(R.id.botao);
        progress = view.findViewById(R.id.progress);
        btnDeleta = view.findViewById(R.id.botaoDeleta);
        sizeValue = view.findViewById(R.id.preco);
        sizeValue.addTextChangedListener(MaskEditUtil.monetario(sizeValue));

        btnDeleta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment dialogFrag = InfoDialogFragment.newInstance("Confirmar exclusão?", getResources().getString(R.string.confirmar), getResources().getString(R.string.revisar));
                dialogFrag.show(FrCriarEditarProductSize.this.getChildFragmentManager(), "confirm");
            }
        });

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validar()) {
                    btnDeleta.setClickable(false);
                    btn.setClickable(false);
                    if (sizeValue.getText().toString().isEmpty())
                        size.setPrice(0);
                    else {
                        size.setPrice(Util.unmaskPrice(sizeValue.getText().toString()));
                    }
                    size.setName(sizeName.getText().toString());
                    size.setStock_quantity(Integer.parseInt(sizeStock.getText().toString()));
                    if (!editar) {
                        size.setCompany_id(product.getCompany_id());
                        loaderManager.restartLoader(0, null, FrCriarEditarProductSize.this);
                    } else {
                        loaderManager.restartLoader(1, null, FrCriarEditarProductSize.this);
                    }
                }
            }
        });

        if (editar) {
            sizeName.setEnabled(false);
            sizeName.setClickable(false);
            sizeName.setFocusable(false);
            btnDeleta.setVisibility(View.VISIBLE);
            sizeStock.setText(String.valueOf(size.getStock_quantity()));
            sizeValue.setText(Util.formatDecimaisDoubleToString(size.getPrice()));
            sizeName.setText(size.getName());
        } else {
            size = new Size();
            size.setProduct_id(product.getProduct_id());
        }
        return view;
    }

    private boolean validar() {
        boolean b = true;
        if (sizeName.getText().toString().isEmpty()) {
            nomeWrap.setErrorEnabled(true);
            nomeWrap.setError(getResources().getString(R.string.error_enabled_text));
            b = false;
        } else {
            nomeWrap.setErrorEnabled(false);
        }
        if (sizeStock.getText().toString().isEmpty()) {
            estoqueWrap.setErrorEnabled(true);
            estoqueWrap.setError(getResources().getString(R.string.error_enabled_text));
            b = false;
        } else {
            estoqueWrap.setErrorEnabled(false);
        }
        return b;
    }


    @Override
    public Loader<Map<String, Object>> onCreateLoader(int id, Bundle args) {
        progress.setVisibility(View.VISIBLE);
        if (id == 0) {
            return new AddExtra(getActivity(), size, editar);
        } else if (id == 1) {
            return new UpdateExtra(getActivity(), size);
        } else {
            return new DeleteExtra(getActivity(), size);
        }
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Map<String, Object>> loader, Map<String, Object> data) {
        progress.setVisibility(View.GONE);
        if (data != null) {

            if (data.get(AppConstants.SIZE) instanceof Size) {
                if (editar) {
                    Toast.makeText(getContext(), "Atualizado com sucesso!", Toast.LENGTH_SHORT).show();
                    Util.backFragmentFunction(this);
                } else {
                    DialogFragment dialogFrag = InfoDialogFragment.newInstance("Adicionar outro?", getResources().getString(R.string.confirmar), getResources().getString(R.string.cancelar));
                    dialogFrag.show(FrCriarEditarProductSize.this.getChildFragmentManager(), "other");
                    Toast.makeText(getContext(), "Adicionado com sucesso!", Toast.LENGTH_SHORT).show();
                }
            } else if (data.get("delete") instanceof Integer) {
                if ((Integer) data.get("delete") > 0) {
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
        if (info != null && info.getTag() != null) {
            switch (info.getTag()) {
                case "confirm":
                    switch (view.getId()) {
                        case R.id.btnPositive:
                            progress.setVisibility(View.VISIBLE);
                            btnDeleta.setClickable(false);
                            btn.setClickable(false);
                            loaderManager.restartLoader(2, null, FrCriarEditarProductSize.this);
                            info.dismiss();
                            break;
                        case R.id.btnNegative:
                            info.dismiss();
                            break;
                    }
                    break;
                case "other":
                    switch (view.getId()) {
                        case R.id.btnPositive:
                            sizeName.setText("");
                            sizeValue.setText("");
                            sizeStock.setText("");
                            info.dismiss();
                            break;
                        case R.id.btnNegative:
                            info.dismiss();
                            Util.backFragmentFunction(this);
                            break;
                    }
                    break;
            }
        }
    }

    private static class AddExtra extends AsyncTaskLoader<Map<String, Object>> {

        Size size;
        Context ctx;
        boolean edit;

        AddExtra(Context context, Size size, boolean edit) {
            super(context);
            ctx = context;
            this.size = size;
            this.edit = edit;
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

            String json = ConnectApi.POST(size, ConnectApi.PRODUCT_SIZE, ctx);
            JsonParser parser = new JsonParser();

            try {
                Size size = gson.fromJson(parser.parse(json).getAsJsonObject().getAsJsonObject(AppConstants.SIZE), Size.class);
                mapOrdPro.put(AppConstants.SIZE, size);
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

    private static class UpdateExtra extends AsyncTaskLoader<Map<String, Object>> {

        Size size;
        Context ctx;

        UpdateExtra(Context context, Size size) {
            super(context);
            ctx = context;
            this.size = size;
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

            String json = ConnectApi.PATCH(size, ConnectApi.PRODUCT_SIZE, ctx);
            JsonParser parser = new JsonParser();

            try {
                Size size = gson.fromJson(parser.parse(json).getAsJsonObject().getAsJsonObject(AppConstants.SIZE), Size.class);
                mapOrdPro.put(AppConstants.SIZE, size);
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


    private static class DeleteExtra extends AsyncTaskLoader<Map<String, Object>> {

        Activity ctx;
        Size size;


        DeleteExtra(Activity context, Size size) {
            super(context);
            ctx = context;
            this.size = size;
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
                mapOrdPro.put("delete", gson.fromJson(ConnectApi.DELETE(size, ConnectApi.PRODUCT_SIZE, ctx), Integer.class));
                return mapOrdPro;
            } catch (Exception e) {
                FirebaseCrashlytics.getInstance().log(UtilShaPre.getDefaultsString(AppConstants.USER_ID, getContext()));
                FirebaseCrashlytics.getInstance().recordException(e);
                return null;
            }
        }
    }
}