package com.nuppin.company.Loja.produto.extra;

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
import com.nuppin.company.model.Company;
import com.nuppin.company.model.ErrorCode;
import com.nuppin.company.model.Extra;

import java.util.HashMap;
import java.util.Map;

public class FrCriarEditarExtra extends Fragment
        implements LoaderManager.LoaderCallbacks<Map<String, Object>>, InfoDialogFragment.InfoDialogListener {

    private static final String EDITAR_EXTRA = "EDITAR";
    private static final String NOVO_EXTRA = "NOVO";
    private Extra extra;
    private LoaderManager loaderManager;
    private EditText name, description, value;
    private TextInputLayout nameWrap;
    private CardView progress;
    private boolean editar = false;
    private Company company;
    private MaterialButton btn, btnDeleta;


    public FrCriarEditarExtra() {
    }

    public static FrCriarEditarExtra newInstance(Extra extra) {
        FrCriarEditarExtra fragment = new FrCriarEditarExtra();
        Bundle args = new Bundle();
        args.putParcelable(EDITAR_EXTRA, extra);
        fragment.setArguments(args);
        return fragment;
    }

    public static FrCriarEditarExtra newInstance2(Company company) {
        FrCriarEditarExtra fragment = new FrCriarEditarExtra();
        Bundle args = new Bundle();
        args.putParcelable(NOVO_EXTRA, company);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null && getArguments().containsKey(EDITAR_EXTRA)) {
            editar = true;
            extra = getArguments().getParcelable(EDITAR_EXTRA);
        }
        if (getArguments() != null && getArguments().containsKey(NOVO_EXTRA)) {
            company = getArguments().getParcelable(NOVO_EXTRA);
        }
        loaderManager = LoaderManager.getInstance(this);

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.add_update_extra, container, false);

        Toolbar toolbar = view.findViewById(R.id.toolbar_top);
        Util.setaToolbar(this, 0, toolbar, getActivity(), true, R.drawable.ic_clear_white_24dp);


        name = view.findViewById(R.id.nome);
        nameWrap = view.findViewById(R.id.nomeWrap);
        description = view.findViewById(R.id.desc);
        btn = view.findViewById(R.id.botao);
        progress = view.findViewById(R.id.progress);
        btnDeleta = view.findViewById(R.id.botaoDeleta);
        value = view.findViewById(R.id.preco);
        value.addTextChangedListener(MaskEditUtil.monetario(value));

        btnDeleta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment dialogFrag = InfoDialogFragment.newInstance("Confirmar exclusão?", getResources().getString(R.string.confirmar), getResources().getString(R.string.revisar));
                dialogFrag.show(FrCriarEditarExtra.this.getChildFragmentManager(), "confirm");
            }
        });

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validar()) {
                    btnDeleta.setClickable(false);
                    btn.setClickable(false);
                    extra.setName(Util.clearName(name.getText().toString()));
                    extra.setDescription(description.getText().toString());
                    if (value.getText().toString().isEmpty())
                        extra.setPrice(0);
                    else {
                        extra.setPrice(Util.unmaskPrice(value.getText().toString()));
                    }
                    if (!editar) {
                        loaderManager.restartLoader(0, null, FrCriarEditarExtra.this);
                    } else {
                        loaderManager.restartLoader(1, null, FrCriarEditarExtra.this);
                    }
                }
            }
        });

        if (editar) {
            btnDeleta.setVisibility(View.VISIBLE);
            name.setText(extra.getName());
            description.setText(extra.getDescription());
            value.setText(Util.formaterPrice(extra.getPrice()));
        } else {
            extra = new Extra();
        }
        return view;
    }

    private boolean validar() {
        boolean b = true;
        if (name.getText().toString().isEmpty()) {
            nameWrap.setErrorEnabled(true);
            nameWrap.setError(getResources().getString(R.string.error_enabled_text));
            b = false;
        } else {
            nameWrap.setErrorEnabled(false);
        }
        return b;
    }


    @Override
    public Loader<Map<String, Object>> onCreateLoader(int id, Bundle args) {
        progress.setVisibility(View.VISIBLE);
        if (id == 0) {
            return new AddExtra(getActivity(), extra, editar, company.getCompany_id());
        } else if (id == 1) {
            return new UpdateExtra(getActivity(), extra);
        } else {
            return new DeleteExtra(getActivity(), extra);
        }
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Map<String, Object>> loader, Map<String, Object> data) {
        progress.setVisibility(View.GONE);
        if (data != null) {
            if (data.get(AppConstants.EXTRA) instanceof Extra) {
                Util.backFragmentFunction(this);
            } else if (data.get("delete") instanceof Integer) {
                if ((Integer) data.get("delete") > 0) {
                    extra.setExtra_id(null);
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
                loaderManager.restartLoader(2, null, FrCriarEditarExtra.this);
                info.dismiss();
                break;
            case R.id.btnNegative:
                info.dismiss();
                break;
        }
    }

    private static class AddExtra extends AsyncTaskLoader<Map<String, Object>> {

        Extra extra;
        Context ctx;
        boolean edit;
        String idLoja;

        AddExtra(Context context, Extra extra, boolean edit, String idLoja) {
            super(context);
            ctx = context;
            this.extra = extra;
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
            extra.setCompany_id(idLoja);
            Map<String, Object> mapOrdPro;
            mapOrdPro = new HashMap<>();

            String json = ConnectApi.POST(extra, ConnectApi.EXTRA_ITEMS, ctx);
            JsonParser parser = new JsonParser();

            try {
                Extra ei = gson.fromJson(parser.parse(json).getAsJsonObject().getAsJsonObject(AppConstants.EXTRA), Extra.class);
                mapOrdPro.put(AppConstants.EXTRA, ei);
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

        Extra extra;
        Context ctx;

        UpdateExtra(Context context, Extra extra) {
            super(context);
            ctx = context;
            this.extra = extra;
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

            String json = ConnectApi.PATCH(extra, ConnectApi.EXTRA_ITEMS, ctx);
            JsonParser parser = new JsonParser();

            try {
                Extra ei = gson.fromJson(parser.parse(json).getAsJsonObject().getAsJsonObject(AppConstants.EXTRA), Extra.class);
                mapOrdPro.put(AppConstants.EXTRA, ei);
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
        Extra extra;


        DeleteExtra(Activity context, Extra extra) {
            super(context);
            ctx = context;
            this.extra = extra;
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
                mapOrdPro.put("delete", gson.fromJson(ConnectApi.DELETE(extra, ConnectApi.EXTRA_ITEMS, ctx), Integer.class));
                return mapOrdPro;
            } catch (Exception e) {
                FirebaseCrashlytics.getInstance().log(UtilShaPre.getDefaultsString(AppConstants.USER_ID, getContext()));
                FirebaseCrashlytics.getInstance().recordException(e);
                return null;
            }
        }
    }
}