package com.nuppin.company.Loja.posts;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.AsyncTaskLoader;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.nuppin.company.Util.AppConstants;
import com.nuppin.company.Util.UtilShaPre;
import com.nuppin.company.connection.ConnectApi;
import com.nuppin.company.model.Material;
import com.nuppin.company.R;
import com.nuppin.company.Util.Util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ListaPosts extends Fragment implements
        LoaderManager.LoaderCallbacks<List<Material>>,
        RvPostsAdapter.RvPostsOnClickListener,
        RvPostsAdapter.RvPostLoad {

    private LoaderManager loaderManager;
    private RvPostsAdapter adapter;
    private RecyclerView mRecyclerView;
    private List<Material> data;
    private LottieAnimationView dots;
    private LinearLayout linearEmpty;
    private ConstraintLayout errorLayout;
    private FloatingActionButton fabError;
    private int limit = 10;
    private int offset = 0;
    private ProgressBar progressBottom;

    public ListaPosts() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loaderManager = LoaderManager.getInstance(this);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fr_material, container, false);

        dots = view.findViewById(R.id.dots);
        linearEmpty = view.findViewById(R.id.linearEmpty);
        errorLayout = view.findViewById(R.id.error_layout);
        fabError = view.findViewById(R.id.fabError);
        fabError.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dots.setVisibility(View.VISIBLE);
                errorLayout.setVisibility(View.GONE);
                mRecyclerView.setVisibility(View.GONE);
                loaderManager.restartLoader(0, null, ListaPosts.this);
            }
        });

        progressBottom = view.findViewById(R.id.progressBottom);
        mRecyclerView = view.findViewById(R.id.recyclerview_posts);
        Toolbar toolbar = view.findViewById(R.id.toolbar_top);
        Util.setaToolbar(this, R.string.blog_toolbar, toolbar, getActivity(), false, 0);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        adapter = new RvPostsAdapter(this, this);
        mRecyclerView.setAdapter(adapter);
        mRecyclerView.setHasFixedSize(true);
        loaderManager.restartLoader(0, null, this);
        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
        loaderManager.destroyLoader(0);
    }

    @NonNull
    @Override
    public Loader<List<Material>> onCreateLoader(int id, Bundle args) {
        if (id == 0) {
            return new PostsLoader(getActivity(), data, limit, offset);
        } else {
            return new PostsLoader(getActivity(), data, limit, offset, 1);
        }
    }

    @Override
    public void onLoadFinished(@NonNull Loader<List<Material>> loader, final List<Material> data) {
        dots.setVisibility(View.GONE);
        progressBottom.setVisibility(View.GONE);

        if (loader.getId() == 0) {
            this.data = data;
            if (data != null) {
                if (data.isEmpty()) {
                    mRecyclerView.setVisibility(View.GONE);
                    linearEmpty.setVisibility(View.VISIBLE);
                } else {
                    mRecyclerView.setVisibility(View.VISIBLE);
                    linearEmpty.setVisibility(View.GONE);
                    adapter.setPosts(data, offset);
                }
                errorLayout.setVisibility(View.GONE);
            } else {
                errorLayout.setVisibility(View.VISIBLE);
            }
        } else if (loader.getId() == 1) {
            if (data != null) {
                this.data.addAll(data);
                mRecyclerView.post(new Runnable() {
                    @Override
                    public void run() {
                        adapter.setPosts(data, offset);
                    }
                });
            } else {
                offset -= limit;
                Toast.makeText(getContext(), R.string.error, Toast.LENGTH_SHORT).show();
            }
        }
        loaderManager.destroyLoader(loader.getId());
    }

    @Override
    public void onLoaderReset(@NonNull Loader<List<Material>> loader) {

    }

    @Override
    public void onClick(Material material) {
        Activity activity = getActivity();
        if (activity instanceof RvPostsAdapter.RvPostsOnClickListener) {
            RvPostsAdapter.RvPostsOnClickListener listener = (RvPostsAdapter.RvPostsOnClickListener) activity;
            listener.onClick(material);
        }
    }

    @Override
    public void onLoad(Material material) {
        progressBottom.setVisibility(View.VISIBLE);
        offset += limit;
        loaderManager.restartLoader(1, null, this);
    }

    private static class PostsLoader extends AsyncTaskLoader<List<Material>> {

        List<Material> data;
        int limit, offset, noData;

        PostsLoader(Context context, List<Material> data, int limit, int offset) {
            super(context);
            this.data = data;
            this.limit = limit;
            this.offset = offset;
        }

        PostsLoader(Context context, List<Material> data, int limit, int offset, int noData) {
            super(context);
            this.data = data;
            this.limit = limit;
            this.offset = offset;
            this.noData = noData;
        }

        @Override
        protected void onStartLoading() {
            if (data != null && noData == 0) {
                deliverResult(data);
            }
            forceLoad();
        }


        @Override
        public List<Material> loadInBackground() {
            Gson gson = new Gson();
            try {
                String stringJson = ConnectApi.GET(ConnectApi.POSTS + "/" + limit + "," + offset, getContext());
                JsonParser parser = new JsonParser();
                JsonObject jObj = parser.parse(stringJson).getAsJsonObject();
                Material[] posts = gson.fromJson(jObj.getAsJsonArray(AppConstants.MATERIAL), Material[].class);
                return new ArrayList<>(Arrays.asList(posts));
            } catch (Exception e) {
                FirebaseCrashlytics.getInstance().log(UtilShaPre.getDefaultsString(AppConstants.USER_ID, getContext()));
                FirebaseCrashlytics.getInstance().recordException(e);
                return null;
            }
        }
    }
}
