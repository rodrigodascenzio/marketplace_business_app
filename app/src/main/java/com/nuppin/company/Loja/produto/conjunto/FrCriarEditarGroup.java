package com.nuppin.company.Loja.produto.conjunto;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
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
import com.nuppin.company.Util.Util;
import com.nuppin.company.Util.UtilShaPre;
import com.nuppin.company.connection.ConnectApi;
import com.nuppin.company.model.Company;
import com.nuppin.company.model.Collection;
import com.nuppin.company.model.ErrorCode;

import java.util.HashMap;
import java.util.Map;

public class FrCriarEditarGroup extends Fragment
        implements LoaderManager.LoaderCallbacks<Map<String, Object>>, InfoDialogFragment.InfoDialogListener {

    private static final String EDITAR_PRODUTO = "EDITAR";
    private static final String NOVO_PRODUTO = "NOVO";
    private Collection collection;
    private LoaderManager loaderManager;
    private EditText nomeConjuct, descConjuct, externalId, min, max;
    private TextInputLayout nomeWrap;
    private CardView progress;
    private boolean editar = false;
    private Company company;
    private TextView info;
    private MaterialButton btn, btnDeleta;
    private Switch isRequired;


    public FrCriarEditarGroup() {
    }

    public static FrCriarEditarGroup newInstance(Collection collection) {
        FrCriarEditarGroup fragment = new FrCriarEditarGroup();
        Bundle args = new Bundle();
        args.putParcelable(EDITAR_PRODUTO, collection);
        fragment.setArguments(args);
        return fragment;
    }

    public static FrCriarEditarGroup newInstance2(Company company) {
        FrCriarEditarGroup fragment = new FrCriarEditarGroup();
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
            collection = getArguments().getParcelable(EDITAR_PRODUTO);
        }
        if (getArguments() != null && getArguments().containsKey(NOVO_PRODUTO)) {
            company = getArguments().getParcelable(NOVO_PRODUTO);
        }
        loaderManager = LoaderManager.getInstance(this);

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.add_update_conjuct, container, false);

        Toolbar toolbar = view.findViewById(R.id.toolbar_top);
        Util.setaToolbar(this, 0, toolbar, getActivity(), true, R.drawable.ic_clear_white_24dp);

        info = view.findViewById(R.id.info);
        isRequired = view.findViewById(R.id.required);
        nomeConjuct = view.findViewById(R.id.nome);
        nomeWrap = view.findViewById(R.id.nomeWrap);
        descConjuct = view.findViewById(R.id.desc);
        btn = view.findViewById(R.id.botao);
        progress = view.findViewById(R.id.progress);
        btnDeleta = view.findViewById(R.id.botaoDeleta);
        externalId = view.findViewById(R.id.codigo);
        min = view.findViewById(R.id.minValue);
        max = view.findViewById(R.id.maxValue);

        isRequired.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    min.setVisibility(View.VISIBLE);
                    info.setVisibility(View.VISIBLE);
                } else {
                    min.setVisibility(View.GONE);
                    info.setVisibility(View.GONE);
                }
            }
        });

        btnDeleta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment dialogFrag = InfoDialogFragment.newInstance("Confirmar exclusão?", getResources().getString(R.string.confirmar), getResources().getString(R.string.revisar));
                dialogFrag.show(FrCriarEditarGroup.this.getChildFragmentManager(), "confirm");
            }
        });

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validar()) {
                    btnDeleta.setClickable(false);
                    btn.setClickable(false);
                    collection.setName(Util.clearName(nomeConjuct.getText().toString()));
                    collection.setDescription(descConjuct.getText().toString());
                    collection.setMax_quantity(Integer.parseInt(max.getText().toString()));

                    if (!isRequired.isChecked()) {
                        collection.setMin_quantity(0);
                    }else {
                        collection.setMin_quantity(Integer.parseInt(min.getText().toString()));
                    }

                    if (!editar) {
                        loaderManager.restartLoader(0, null, FrCriarEditarGroup.this);
                    } else {
                        loaderManager.restartLoader(1, null, FrCriarEditarGroup.this);
                    }
                }
            }
        });

        if (editar) {
            btnDeleta.setVisibility(View.VISIBLE);
            nomeConjuct.setText(collection.getName());
            descConjuct.setText(collection.getDescription());
            min.setText(String.valueOf(collection.getMin_quantity()));
            max.setText(String.valueOf(collection.getMax_quantity()));

            if (collection.getMin_quantity() > 0) {
                isRequired.setChecked(true);
            }else {
                isRequired.setChecked(false);
            }
        } else {
            collection = new Collection();
        }
        return view;
    }

    private boolean validar() {
        boolean b = true;
        if (nomeConjuct.getText().toString().isEmpty()) {
            nomeWrap.setErrorEnabled(true);
            nomeWrap.setError(getResources().getString(R.string.error_enabled_text));
            b = false;
            return b;
        } else {
            nomeWrap.setErrorEnabled(false);
        }
        if (max.getText().toString().isEmpty() || Integer.parseInt(max.getText().toString()) < 1) {
            Toast.makeText(getContext(), "É preciso colocar um numero maximo de itens", Toast.LENGTH_LONG).show();
            b = false;
            return b;
        }
        if (isRequired.isChecked() && (min.getText().toString().isEmpty() || Integer.parseInt(min.getText().toString()) < 1)){
            Toast.makeText(getContext(), "Grupo marcado como obrigatorio, minimo precisa ser maior que zero", Toast.LENGTH_LONG).show();
            b = false;
            return b;
        }
        if (isRequired.isChecked() && Integer.parseInt(min.getText().toString()) > Integer.parseInt(max.getText().toString())) {
            Toast.makeText(getContext(), "O numero minimo de opções não pode ser maior que o maximo", Toast.LENGTH_LONG).show();
            b = false;
            return b;
        }
        return b;
    }


    @Override
    public Loader<Map<String, Object>> onCreateLoader(int id, Bundle args) {
        progress.setVisibility(View.VISIBLE);
        if (id == 0) {
            return new AddConjuct(getActivity(), collection, editar, company.getCompany_id());
        } else if (id == 1) {
            return new UpdateConjuct(getActivity(), collection);
        } else {
            return new DeleteConjuct(getActivity(), collection);
        }
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Map<String, Object>> loader, Map<String, Object> data) {
        progress.setVisibility(View.GONE);
        if (data != null) {

            if (data.get(AppConstants.COLLECTION) instanceof Collection) {
                Util.backFragmentFunction(this);
            } else if (data.get("delete") instanceof Integer) {
                if ((Integer) data.get("delete") > 0) {
                    collection.setCollection_id(null);
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
        switch (view.getId()) {
            case R.id.btnPositive:
                progress.setVisibility(View.VISIBLE);
                btnDeleta.setClickable(false);
                btn.setClickable(false);
                loaderManager.restartLoader(2, null, FrCriarEditarGroup.this);
                info.dismiss();
                break;
            case R.id.btnNegative:
                info.dismiss();
                break;
        }
    }

    private static class AddConjuct extends AsyncTaskLoader<Map<String, Object>> {

        Collection collection;
        Context ctx;
        boolean edit;
        String idLoja;

        AddConjuct(Context context, Collection collection, boolean edit, String idLoja) {
            super(context);
            ctx = context;
            this.collection = collection;
            this.edit = edit;
            this.idLoja = idLoja;
        }

        @Override
        protected void onStartLoading() {
            forceLoad();
        }

        @Override
        public Map<String, Object> loadInBackground() {
            Gson gson = new Gson();
            collection.setCompany_id(idLoja);
            Map<String, Object> mapOrdPro;
            mapOrdPro = new HashMap<>();

            String json = ConnectApi.POST(collection, ConnectApi.COLLECTION, ctx);
            JsonParser parser = new JsonParser();

            try {
                Collection c = gson.fromJson(parser.parse(json).getAsJsonObject().getAsJsonObject(AppConstants.COLLECTION), Collection.class);
                mapOrdPro.put(AppConstants.COLLECTION, c);
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

    private static class UpdateConjuct extends AsyncTaskLoader<Map<String, Object>> {

        Collection collection;
        Context ctx;

        UpdateConjuct(Context context, Collection collection) {
            super(context);
            ctx = context;
            this.collection = collection;
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

            String json = ConnectApi.PATCH(collection, ConnectApi.COLLECTION, ctx);
            JsonParser parser = new JsonParser();

            try {
                Collection c = gson.fromJson(parser.parse(json).getAsJsonObject().getAsJsonObject(AppConstants.COLLECTION), Collection.class);
                mapOrdPro.put(AppConstants.COLLECTION, c);
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


    private static class DeleteConjuct extends AsyncTaskLoader<Map<String, Object>> {

        Activity ctx;
        Collection collection;


        DeleteConjuct(Activity context, Collection collection) {
            super(context);
            ctx = context;
            this.collection = collection;
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
                mapOrdPro.put("delete", gson.fromJson(ConnectApi.DELETE(collection, ConnectApi.COLLECTION, ctx), Integer.class));
                return mapOrdPro;
            } catch (Exception e) {
                FirebaseCrashlytics.getInstance().log(UtilShaPre.getDefaultsString(AppConstants.USER_ID, getContext()));
                FirebaseCrashlytics.getInstance().recordException(e);
                return null;
            }
        }
    }
}