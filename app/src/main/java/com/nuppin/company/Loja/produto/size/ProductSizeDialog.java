package com.nuppin.company.Loja.produto.size;

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
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.gson.Gson;
import com.nuppin.company.R;
import com.nuppin.company.Util.AppConstants;
import com.nuppin.company.Util.UtilShaPre;
import com.nuppin.company.connection.ConnectApi;
import com.nuppin.company.model.Product;
import com.nuppin.company.model.Size;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class ProductSizeDialog extends DialogFragment implements
        RvProductSizeDialog.ConjuctsOnClickListener,
        LoaderManager.LoaderCallbacks<Object> {
    private SeekbarDialogListener listener;

    private String stringPositive, stringNegative;
    private RecyclerView recyclerView;
    private RvProductSizeDialog adapter;
    private LoaderManager loaderManager;
    private Button btnPositive, btnNegative;
    private Product product;
    private CardView progress;
    private LottieAnimationView dots;

    public static ProductSizeDialog newInstance(Product product, String btnPositive, String btnNegative) {
        ProductSizeDialog fragment = new ProductSizeDialog();
        Bundle args = new Bundle();
        args.putParcelable("uri", product);
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
            product = getArguments().getParcelable("uri");
            stringPositive = getArguments().getString("stringPositive");
            stringNegative = getArguments().getString("stringNegative");
        }
        try {
            listener = (SeekbarDialogListener) getParentFragment();
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

        //LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false);
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 3);
        recyclerView = layout.findViewById(R.id.recycler);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new RvProductSizeDialog(this);
        recyclerView.setAdapter(adapter);

        btnPositive = layout.findViewById(R.id.btnPositive);
        btnNegative = layout.findViewById(R.id.btnNegative);
        btnPositive.setText(stringPositive);
        btnNegative.setText(stringNegative);

        TextView txt = layout.findViewById(R.id.txt);
        txt.setText("Selecione os tamanhos para esse produto");

        btnPositive.setVisibility(View.GONE);
        btnNegative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onDialogOKClick(btnNegative, "OK", null, ProductSizeDialog.this);
            }
        });
        loaderManager.restartLoader(0, null, ProductSizeDialog.this);

        builder.setView(layout);
        return builder.create();
    }

    @NonNull
    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        return new GetConjucts(Objects.requireNonNull(getActivity()), product);
    }

    @Override
    public void onLoadFinished(@NonNull Loader loader, Object data) {
        dots.setVisibility(View.GONE);
        progress.setVisibility(View.GONE);
        if (data != null) {
            if (data instanceof List && ((List) data).size() > 0) {
                recyclerView.setVisibility(View.VISIBLE);
                adapter.setConjucts((List) data, product);
            }else {
                Toast.makeText(getContext(), "Nenhum tamanho cadastrado ou disponivel", Toast.LENGTH_SHORT).show();
                listener.onDialogOKClick(btnNegative, AppConstants.COLLECTION, null,this);
            }
        } else {
            Toast.makeText(getContext(), R.string.error, Toast.LENGTH_SHORT).show();
            listener.onDialogOKClick(btnNegative, AppConstants.ERROR, null,this);
        }
        if (loader.getId() != 0) {
            loaderManager.destroyLoader(loader.getId());
        }
        btnPositive.setClickable(true);
        btnNegative.setClickable(true);
    }

    @Override
    public void onLoaderReset(@NonNull Loader loader) {
    }

    @Override
    public void onClickConjuct(Size sizes) {
        listener.onDialogOKClick(btnPositive, "OK", sizes, ProductSizeDialog.this);
    }

    private static class GetConjucts extends AsyncTaskLoader<Object> {

        Product product;
        Context ctx;

        GetConjucts(@NonNull Context context, Product product) {
            super(context);
            this.product = product;
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
                Size[] size = gson.fromJson(ConnectApi.GET(ConnectApi.SIZE_NOT_PRODUCT + "/" + product.getCompany_id() + "," + product.getProduct_id(), getContext()), Size[].class);
                return new ArrayList<>(Arrays.asList(size));
            } catch (Exception e) {
                FirebaseCrashlytics.getInstance().log(UtilShaPre.getDefaultsString(AppConstants.USER_ID, getContext()));
                FirebaseCrashlytics.getInstance().recordException(e);
                return null;
            }
        }
    }

    public interface SeekbarDialogListener {
        void onDialogOKClick(View view, String type, Size size, ProductSizeDialog infoDialogFragment);
    }

}