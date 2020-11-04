package com.nuppin.company.Loja.produto.conjunto;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.AsyncTaskLoader;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.gson.Gson;
import com.google.gson.JsonParser;
import com.nuppin.company.Loja.dialogs.InfoDialogFragment;
import com.nuppin.company.R;
import com.nuppin.company.Util.AppConstants;
import com.nuppin.company.Util.Util;
import com.nuppin.company.Util.UtilShaPre;
import com.nuppin.company.connection.ConnectApi;
import com.nuppin.company.model.Collection;
import com.nuppin.company.model.Extra;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FrDetalheGroup extends Fragment implements
        LoaderManager.LoaderCallbacks<Map<String, Object>>,
        RvDetailGroup.ConjuctExtraOnClickListener,
        InfoDialogFragment.InfoDialogListener,
        FrGroupExtrasDialog.DialogListener {

    private LoaderManager loaderManager;
    private static final String EXTRA_PRODUCT = "conjuct";
    private Collection collection;
    private FrameLayout progress;
    private LottieAnimationView dots;
    private ConstraintLayout errorLayout;
    private FloatingActionButton fabError;
    private RvDetailGroup adapter;
    private RecyclerView recyclerView;
    private MaterialButton teste;
    private Extra extra;


    public static FrDetalheGroup novaIntancia(Collection collection) {
        Bundle parametros = new Bundle();
        parametros.putParcelable(EXTRA_PRODUCT, collection);
        FrDetalheGroup fragment = new FrDetalheGroup();
        fragment.setArguments(parametros);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null && getArguments().containsKey(EXTRA_PRODUCT)) {
            collection = getArguments().getParcelable(EXTRA_PRODUCT);
            loaderManager = LoaderManager.getInstance(this);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fr_conjuct_detail, container, false);

        Toolbar toolbar = v.findViewById(R.id.toolbar_top);
        Util.setaToolbar(this, R.string.detalhe_toolbar, toolbar, getActivity(), false, 0);

        if (collection.getCollection_id() == null) {
            Toast.makeText(getContext(), "Grupo deletado", Toast.LENGTH_SHORT).show();
            Util.backFragmentFunction(this);
        }

        progress = v.findViewById(R.id.framProgress);
        dots = v.findViewById(R.id.dots);
        errorLayout = v.findViewById(R.id.error_layout);
        fabError = v.findViewById(R.id.fabError);
        fabError.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dots.setVisibility(View.VISIBLE);
                errorLayout.setVisibility(View.GONE);
                loaderManager.restartLoader(0, null, FrDetalheGroup.this);
            }
        });

        teste = v.findViewById(R.id.teste);
        teste.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment dialogFrag = FrGroupExtrasDialog.newInstance(collection, getResources().getString(R.string.confirmar), getResources().getString(R.string.revisar));
                dialogFrag.show(FrDetalheGroup.this.getChildFragmentManager(), "confirm");
            }
        });

        TextView nome = v.findViewById(R.id.nome);
        TextView descricao = v.findViewById(R.id.desc);
        recyclerView = v.findViewById(R.id.conjuct_recycler);
        nome.setText(collection.getName());

        if (collection.getDescription() != null && collection.getDescription().trim().equals("")) {
            descricao.setText(getString(R.string.sem_descricao));
        } else {
            descricao.setText(collection.getDescription());
        }

        FloatingActionButton fab = v.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getActivity() instanceof ToActivity) {
                    ToActivity listener = (ToActivity) getActivity();
                    listener.DetalheConjuctBottom(R.id.fab, collection);
                }
            }
        });

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new RvDetailGroup(this);
        recyclerView.setAdapter(adapter);
        loaderManager.restartLoader(0, null, this);
        return v;
    }

    @Override
    public void onPause() {
        super.onPause();
        loaderManager.destroyLoader(0);
    }

    @NonNull
    @Override
    public Loader<Map<String, Object>> onCreateLoader(int id, Bundle args) {
        if (id == 0) {
            return new LoadConjuct(getActivity(), collection);
        } else {
            return new DeleteExtra(getActivity(), extra);
        }
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Map<String, Object>> loader, Map<String, Object> data) {
        dots.setVisibility(View.GONE);
        if (data != null) {
            if (data.get(AppConstants.COLLECTION) instanceof Collection) {
                progress.setVisibility(View.GONE);
                collection = (Collection) data.get(AppConstants.COLLECTION);
            }
            if (data.get(AppConstants.EXTRA) instanceof List) {
                adapter.setExtraItem((List) data.get(AppConstants.EXTRA), collection);

            } else if (data.get("delete") instanceof Integer) {
                if ((Integer) data.get("delete") > 0) {
                    loaderManager.restartLoader(0, null, this);
                } else {
                    Toast.makeText(getContext(), R.string.error, Toast.LENGTH_SHORT).show();
                }
            }
            errorLayout.setVisibility(View.GONE);
        } else {
            progress.setVisibility(View.GONE);
            errorLayout.setVisibility(View.VISIBLE);
        }

        if (loader.getId() != 0) {
            loaderManager.destroyLoader(loader.getId());
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Map<String, Object>> loader) {
    }

    @Override
    public void onDialogOKClick(View view, String type, FrGroupExtrasDialog infoDialogFragment) {
        if (!type.equals(AppConstants.EXTRA)) {
            if (view.getId() == R.id.btnPositive) {
                loaderManager.restartLoader(0, null, this);
                infoDialogFragment.dismiss();
            } else {
                infoDialogFragment.dismiss();
            }
        }else {
            infoDialogFragment.dismiss();
            DialogFragment dialogFrag = InfoDialogFragment.newInstance("Deseja criar um novo item?", getResources().getString(R.string.confirmar), getResources().getString(R.string.revisar));
            dialogFrag.show(FrDetalheGroup.this.getChildFragmentManager(), "item");
        }
    }

    @Override
    public void onDialogOKClick(View view, InfoDialogFragment infoDialogFragment) {
        if (infoDialogFragment != null && infoDialogFragment.getTag() != null) {
            switch (infoDialogFragment.getTag()) {
                case "item":
                    if (view.getId() == R.id.btnPositive) {
                        if (getActivity() instanceof ToActivity) {
                            ToActivity listener = (ToActivity) getActivity();
                            listener.DetalheConjuctBottom(R.id.btnPositive, collection);
                        }
                    }
                    infoDialogFragment.dismiss();
                    break;
                case "excluir":
                    if (view.getId() == R.id.btnPositive) {
                        progress.setVisibility(View.VISIBLE);
                        loaderManager.restartLoader(1, null, this);
                    }
                    infoDialogFragment.dismiss();
            }
        }
    }

    @Override
    public void conjuctExtra(Extra extra) {
        DialogFragment dialogFrag = InfoDialogFragment.newInstance("Deseja mesmo excluir esse item?", getString(R.string.confirmar), getString(R.string.revisar));
        dialogFrag.show(FrDetalheGroup.this.getChildFragmentManager(), "excluir");
        this.extra = extra;
    }

    private static class LoadConjuct extends AsyncTaskLoader<Map<String, Object>> {

        Context ctx;
        Collection collection;

        LoadConjuct(Context context, Collection collection) {
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

            try {
                String json = ConnectApi.GET(ConnectApi.COLLECTION_DETAIL + "/" + collection.getCollection_id(), getContext());
                JsonParser parser = new JsonParser();
                Collection c = gson.fromJson(parser.parse(json).getAsJsonObject().getAsJsonObject(AppConstants.COLLECTION), Collection.class);
                mapOrdPro.put(AppConstants.COLLECTION, c);
                try {
                    Extra[] extras = gson.fromJson(parser.parse(json).getAsJsonObject().getAsJsonArray(AppConstants.EXTRA), Extra[].class);
                    mapOrdPro.put(AppConstants.EXTRA, new ArrayList<>(Arrays.asList(extras)));
                } catch (Exception e) {
                    FirebaseCrashlytics.getInstance().log(UtilShaPre.getDefaultsString(AppConstants.USER_ID, getContext()));
                    FirebaseCrashlytics.getInstance().recordException(e);
                }
            } catch (Exception e) {
                FirebaseCrashlytics.getInstance().log(UtilShaPre.getDefaultsString(AppConstants.USER_ID, getContext()));
                FirebaseCrashlytics.getInstance().recordException(e);
                return null;
            }

            return mapOrdPro;
        }
    }

    private static class DeleteExtra extends AsyncTaskLoader<Map<String, Object>> {

        Activity ctx;
        Extra conjuct;

        DeleteExtra(Activity context, Extra conjuct) {
            super(context);
            ctx = context;
            this.conjuct = conjuct;
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
                mapOrdPro.put("delete", gson.fromJson(ConnectApi.DELETE(conjuct, ConnectApi.COLLECTION_EXTRA, ctx), Integer.class));
                return mapOrdPro;
            } catch (Exception e) {
                FirebaseCrashlytics.getInstance().log(UtilShaPre.getDefaultsString(AppConstants.USER_ID, getContext()));
                FirebaseCrashlytics.getInstance().recordException(e);
                return null;
            }
        }
    }


    public interface ToActivity {
        void DetalheConjuctBottom(int index, Collection collection);
    }
}
