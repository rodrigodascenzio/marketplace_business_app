package com.nuppin.company.Loja.posts;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.nuppin.company.model.Material;
import com.nuppin.company.R;
import com.nuppin.company.Util.Util;

import java.util.ArrayList;
import java.util.List;

public class RvPostsAdapter extends RecyclerView.Adapter<RvPostsAdapter.RvPostsAdapterViewHolder> {

    private List<Material> posts;
    private RvPostsOnClickListener listener;
    private RvPostLoad listener2;
    int offset;

    RvPostsAdapter(RvPostsOnClickListener handler, RvPostLoad handler2) {
        listener = handler;
        listener2 = handler2;
    }

    public class RvPostsAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final TextView title, categoria;
        private SimpleDraweeView thumb;

        RvPostsAdapterViewHolder(View view) {
            super(view);
            title = view.findViewById(R.id.title);
            thumb = view.findViewById(R.id.thumb);
            categoria = view.findViewById(R.id.subCategoria);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            listener.onClick(posts.get(adapterPosition));
        }
    }

    @NonNull
    public RvPostsAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.rv_list_material;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);
        return new RvPostsAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RvPostsAdapterViewHolder holder, int position) {
        holder.title.setText(posts.get(position).getTitle());
        Util.hasPhoto(posts.get(position),holder.thumb);
        holder.categoria.setText(holder.categoria.getContext().getResources().getString(R.string.categoria_subcategoria, posts.get(position).getCategory_name(), posts.get(position).getSubcategory_name()));

        if( position == getItemCount() - 1 && getItemCount() >=(offset+10)){ // && getItemCount() >= maxLimitLoad
            listener2.onLoad(posts.get(position));
        }
    }

    @Override
    public int getItemCount() {
        if (null == posts)
            return 0;
        return posts.size();
    }

    void setPosts(List<Material> posts, int offset) {
        this.offset = offset;

        if (this.posts == null) {
            this.posts = new ArrayList<>();
        }
        this.posts.addAll(posts);
        notifyDataSetChanged();
    }

    public interface RvPostsOnClickListener {
        void onClick(Material material);
    }
    public interface RvPostLoad {
        void onLoad(Material material);
    }

}


