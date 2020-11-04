package com.nuppin.company.Loja.loja;

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
import androidx.fragment.app.Fragment;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.AsyncTaskLoader;
import androidx.loader.content.Loader;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.gson.Gson;
import com.nuppin.company.R;
import com.nuppin.company.Util.AppConstants;
import com.nuppin.company.Util.Util;
import com.nuppin.company.Util.UtilShaPre;
import com.nuppin.company.connection.ConnectApi;
import com.nuppin.company.model.Feedback;

public class FrFeedback extends Fragment implements LoaderManager.LoaderCallbacks<Object> {

    private static final String COMPANY = "COMPANY";
    private LoaderManager loaderManager;
    private EditText feedbackEdit;
    private TextInputLayout feedbackWrap;
    private CardView progress;
    private MaterialButton btn;
    private Feedback feedback;


    public FrFeedback() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loaderManager = LoaderManager.getInstance(this);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fr_feedback, container, false);

        Toolbar toolbar = view.findViewById(R.id.toolbar_top);
        Util.setaToolbar(this, 0, toolbar, getActivity(), true, R.drawable.ic_clear_white_24dp);


        feedbackEdit = view.findViewById(R.id.feedback);
        feedbackWrap = view.findViewById(R.id.feedbackWrap);
        btn = view.findViewById(R.id.botao);
        progress = view.findViewById(R.id.progress);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validar()) {
                    btn.setClickable(false);
                    feedback = new Feedback();
                    feedback.setUser_id(UtilShaPre.getDefaultsString(AppConstants.USER_ID, getContext()));
                    feedback.setMessage(feedbackEdit.getText().toString());
                    feedback.setNps(0);
                    progress.setVisibility(View.VISIBLE);
                    loaderManager.restartLoader(1, null, FrFeedback.this);
                }
            }
        });

        return view;
    }

    private boolean validar() {
        boolean b = true;
        if (feedbackEdit.getText().toString().isEmpty()) {
            feedbackWrap.setErrorEnabled(true);
            feedbackWrap.setError(getResources().getString(R.string.error_enabled_text));
            b = false;
        } else {
            feedbackWrap.setErrorEnabled(false);
        }
        return b;
    }

    @NonNull
    @Override
    public Loader<Object> onCreateLoader(int id, Bundle args) {
        return new LoaderPP(getActivity(), feedback);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Object> loader, Object data) {
        progress.setVisibility(View.GONE);
        if (data != null) {
            if (data instanceof Boolean && (boolean) data) {
                Toast.makeText(getContext(), "Enviado com sucesso. Agradecemos!", Toast.LENGTH_SHORT).show();
                Util.backFragmentFunction(this);
            } else {
                Toast.makeText(getContext(), R.string.error, Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getContext(), R.string.error, Toast.LENGTH_SHORT).show();
        }
        btn.setClickable(true);
        loaderManager.destroyLoader(loader.getId());
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Object> loader) {

    }

    private static class LoaderPP extends AsyncTaskLoader<Object> {

        Feedback feedback;
        Context ctx;

        LoaderPP(Context context, Feedback feedback) {
            super(context);
            ctx = context;
            this.feedback = feedback;
        }

        @Override
        protected void onStartLoading() {
            forceLoad();
        }

        @Override
        public Object loadInBackground() {
            Gson gson = new Gson();
            try {
                return gson.fromJson(ConnectApi.POST(feedback, ConnectApi.FEEDBACK, getContext()), Boolean.class);
            } catch (Exception e) {
                FirebaseCrashlytics.getInstance().log(UtilShaPre.getDefaultsString(AppConstants.USER_ID, getContext()));
                FirebaseCrashlytics.getInstance().recordException(e);
                return null;
            }

        }
    }
}