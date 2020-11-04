package com.nuppin.company.Loja.posts;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.AsyncTaskLoader;
import androidx.loader.content.Loader;

import com.airbnb.lottie.LottieAnimationView;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.gson.Gson;
import com.google.gson.JsonParser;
import com.nuppin.company.Util.AppConstants;
import com.nuppin.company.Util.UtilShaPre;
import com.nuppin.company.connection.ConnectApi;
import com.nuppin.company.model.Material;
import com.nuppin.company.R;
import com.nuppin.company.Util.Util;

public class FrDetalhePost extends Fragment implements LoaderManager.LoaderCallbacks<Object> {

    private LoaderManager loaderManager;
    private static final String POST = "POST";
    private String postId;
    private SimpleDraweeView thumb;
    private TextView title, body;
    private LottieAnimationView dots;
    private NestedScrollView nested;
    private WebView view;
    private ConstraintLayout errorLayout;
    private FloatingActionButton fabError;

    public static FrDetalhePost novaIntancia(String posts) {
        Bundle parametros = new Bundle();
        parametros.putString(POST, posts);
        FrDetalhePost fragment = new FrDetalhePost();
        fragment.setArguments(parametros);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null && getArguments().containsKey(POST)) {
            postId = getArguments().getString(POST);
            loaderManager = LoaderManager.getInstance(this);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fr_material_body, container, false);

        Toolbar toolbar = v.findViewById(R.id.tb_main_top);
        Util.setaToolbar(this, 0, toolbar, getActivity(), true, R.drawable.ic_clear_white_24dp);


        errorLayout = v.findViewById(R.id.error_layout);
        fabError = v.findViewById(R.id.fabError);
        fabError.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dots.setVisibility(View.VISIBLE);
                errorLayout.setVisibility(View.GONE);
                loaderManager.restartLoader(0, null, FrDetalhePost.this);
            }
        });


        title = v.findViewById(R.id.title);
        body = v.findViewById(R.id.body);
        thumb = v.findViewById(R.id.thumb);

        view = new WebView(getContext());
        view.setVerticalScrollBarEnabled(false);
        ((ConstraintLayout) v.findViewById(R.id.wrapConst)).addView(view);

        nested = v.findViewById(R.id.nested);
        dots = v.findViewById(R.id.dots);

        view.setWebViewClient(new WebViewClient() {

            public void onPageFinished(WebView view, String url) {
                dots.setVisibility(View.GONE);
                thumb.setVisibility(View.VISIBLE);
                nested.setVisibility(View.VISIBLE);
            }
        });
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
    public Loader<Object> onCreateLoader(int id, Bundle args) {
        return new PostLoader(getActivity(), postId);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Object> loader, Object data) {
        if (data != null) {
            if (data instanceof Material) {
                Util.hasPhoto(data, thumb);
                title.setText(((Material) data).getTitle());
                view.loadData(((Material) data).getBody(), "text/html; charset=utf-8", "utf-8");
            }
            errorLayout.setVisibility(View.GONE);
        } else {
            dots.setVisibility(View.GONE);
            errorLayout.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Object> loader) {
    }


    private static class PostLoader extends AsyncTaskLoader<Object> {

        Context ctx;
        String postId;

        PostLoader(Context context, String postId) {
            super(context);
            ctx = context;
            this.postId = postId;
        }


        @Override
        protected void onStartLoading() {
            forceLoad();
        }


        @Override
        public Object loadInBackground() {
            Gson gson = new Gson();
            try {
                String json = ConnectApi.GET(ConnectApi.POST_ITEM + "/" + postId, getContext());
                JsonParser parser = new JsonParser();
                return gson.fromJson(parser.parse(json).getAsJsonObject(), Material.class);
            } catch (Exception e) {
                FirebaseCrashlytics.getInstance().log(UtilShaPre.getDefaultsString(AppConstants.USER_ID, getContext()));
                FirebaseCrashlytics.getInstance().recordException(e);
                return null;
            }
        }
    }
}
