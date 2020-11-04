package com.nuppin.company.Loja.produto.conjunto;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.DialogFragment;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.AsyncTaskLoader;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.gson.Gson;
import com.nuppin.company.R;
import com.nuppin.company.Util.AppConstants;
import com.nuppin.company.Util.UtilShaPre;
import com.nuppin.company.connection.ConnectApi;
import com.nuppin.company.model.Collection;
import com.nuppin.company.model.CollectionExtra;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class FrGroupExtrasDialog extends DialogFragment implements
        RvGroupExtras.extrasOnClickListener,
        LoaderManager.LoaderCallbacks<Object> {

    private DialogListener listener;
    private String stringPositive, stringNegative;
    private RecyclerView recyclerView;
    private RvGroupExtras RvGroupExtras;
    private LoaderManager loaderManager;
    private Button btnPositive, btnNegative;
    private List<CollectionExtra> extraList;
    private Collection collection;
    private CardView progress;
    private LottieAnimationView dots;

    public static FrGroupExtrasDialog newInstance(Collection collection, String btnPositive, String btnNegative) {
        FrGroupExtrasDialog fragment = new FrGroupExtrasDialog();
        Bundle args = new Bundle();
        args.putParcelable("uri", collection);
        args.putString("stringPositive", btnPositive);
        args.putString("stringNegative", btnNegative);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loaderManager = LoaderManager.getInstance(this);
        if (getArguments() != null) {
            collection = getArguments().getParcelable("uri");
            stringPositive = getArguments().getString("stringPositive");
            stringNegative = getArguments().getString("stringNegative");
        }
        try {
            listener = (DialogListener) getParentFragment();
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
        View layout = inflater.inflate(R.layout.teste_dialog, null);

        progress = layout.findViewById(R.id.progress);
        dots = layout.findViewById(R.id.dots);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false);
        recyclerView = layout.findViewById(R.id.recycler);
        recyclerView.setLayoutManager(linearLayoutManager);
        RvGroupExtras = new RvGroupExtras(this);
        recyclerView.setAdapter(RvGroupExtras);

        btnPositive = layout.findViewById(R.id.btnPositive);
        btnNegative = layout.findViewById(R.id.btnNegative);
        btnPositive.setText(stringPositive);
        btnNegative.setText(stringNegative);

        TextView txt = layout.findViewById(R.id.txt);
        txt.setText("Selecione os itens para esse grupo");

        btnPositive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //progress.setVisibility(View.VISIBLE);
                if (extraList == null  || extraList.size() < 1) {
                    Toast.makeText(getContext(), "É necessario escolher algum", Toast.LENGTH_SHORT).show();
                    return;
                }
                progress.setVisibility(View.VISIBLE);
                btnPositive.setClickable(false);
                loaderManager.restartLoader(1, null, FrGroupExtrasDialog.this);
            }
        });
        btnNegative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onDialogOKClick(btnNegative, "OK", FrGroupExtrasDialog.this);
            }
        });
        loaderManager.restartLoader(0, null, FrGroupExtrasDialog.this);

        builder.setView(layout);
        return builder.create();
    }

    @NonNull
    @Override
    public Loader<Object> onCreateLoader(int id, Bundle args) {
        if (id == 1) {
            return new AddConjuctExtra(getActivity(), extraList);
        } else {
            return new LoadExtra(Objects.requireNonNull(getActivity()), collection);
        }
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Object> loader, Object data) {
        dots.setVisibility(View.GONE);
        progress.setVisibility(View.GONE);
        if (data != null) {
            if (loader.getId() == 1) {
                listener.onDialogOKClick(btnPositive, "OK", FrGroupExtrasDialog.this);
            } else {
                if (data instanceof List && ((List) data).size() > 0) {
                    recyclerView.setVisibility(View.VISIBLE);
                    RvGroupExtras.setExtras((List) data, collection);
                }else {
                    Toast.makeText(getContext(), "Nenhum item cadastrado ou disponivel", Toast.LENGTH_SHORT).show();
                    listener.onDialogOKClick(btnNegative,AppConstants.EXTRA,this);
                }
            }
        } else {
            Toast.makeText(getContext(), R.string.error, Toast.LENGTH_SHORT).show();
            listener.onDialogOKClick(btnNegative,AppConstants.ERROR,this);
        }
        if (loader.getId() != 0) {
            loaderManager.destroyLoader(loader.getId());
        }
        btnPositive.setClickable(true);
        btnNegative.setClickable(true);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Object> loader) {

    }

    @Override
    public void onClickExtra(List<CollectionExtra> conjuctList) {
        this.extraList = conjuctList;
    }


    private static class LoadExtra extends AsyncTaskLoader<Object> {

        Collection collection;
        Context ctx;

        LoadExtra(@NonNull Context context, Collection collection) {
            super(context);
            this.collection = collection;
            this.ctx = context;
        }

        @Override
        protected void onStartLoading() {
            super.onStartLoading();
            forceLoad();
        }

        @Nullable
        @Override
        public Object loadInBackground() {
            try {
                Gson gson = new Gson();
                CollectionExtra[] horarios = gson.fromJson(ConnectApi.GET(ConnectApi.EXTRA_NOT_CONJUCT + "/" + collection.getCompany_id()+","+ collection.getCollection_id(), getContext()), CollectionExtra[].class);
                return new ArrayList<>(Arrays.asList(horarios));
            } catch (Exception e) {
                FirebaseCrashlytics.getInstance().log(UtilShaPre.getDefaultsString(AppConstants.USER_ID, getContext()));
                FirebaseCrashlytics.getInstance().recordException(e);
                return null;
            }
        }
    }

    private static class AddConjuctExtra extends AsyncTaskLoader<Object> {

        Context ctx;
        List<CollectionExtra> horario;

        AddConjuctExtra(Context context, List<CollectionExtra> horario) {
            super(context);
            ctx = context;
            this.horario = horario;
        }

        @Override
        protected void onStartLoading() {
            forceLoad();
        }

        @Override
        public Object loadInBackground() {
            try {
                Gson gson = new Gson();
                return gson.fromJson(ConnectApi.POST3(horario, ConnectApi.COLLECTION_EXTRA, getContext()), CollectionExtra[].class);
            } catch (Exception e) {
                FirebaseCrashlytics.getInstance().log(UtilShaPre.getDefaultsString(AppConstants.USER_ID, getContext()));
                FirebaseCrashlytics.getInstance().recordException(e);
                return null;
            }
        }
    }

    public interface DialogListener {
        void onDialogOKClick(View view, String type, FrGroupExtrasDialog infoDialogFragment);
    }

}