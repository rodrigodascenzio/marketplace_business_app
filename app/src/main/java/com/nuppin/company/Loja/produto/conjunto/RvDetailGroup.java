package com.nuppin.company.Loja.produto.conjunto;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.nuppin.company.R;
import com.nuppin.company.Util.Util;
import com.nuppin.company.model.Collection;
import com.nuppin.company.model.Extra;

import java.util.List;

public class RvDetailGroup extends RecyclerView.Adapter<RvDetailGroup.ConjuctExtraAdapterViewHolder> {

    private List<Extra> extra;
    private Collection collection;
    private ConjuctExtraOnClickListener listener;

    RvDetailGroup(ConjuctExtraOnClickListener handler) {
        listener = handler;
    }

    public class ConjuctExtraAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView description, nome, preco, delete;
        ConstraintLayout constraint;

        ConjuctExtraAdapterViewHolder(View view) {
            super(view);
            delete = view.findViewById(R.id.delete);
            constraint = view.findViewById(R.id.constraint);
            nome = view.findViewById(R.id.nome);
            description = view.findViewById(R.id.description);
            preco = view.findViewById(R.id.preco);
            delete.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            listener.conjuctExtra(extra.get(adapterPosition));
        }
    }

    @NonNull
    public ConjuctExtraAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.rv_conjuct_extra;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);
        return new ConjuctExtraAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ConjuctExtraAdapterViewHolder holder, int position) {
        holder.description.setVisibility(View.GONE);
        holder.delete.setVisibility(View.VISIBLE);

        holder.nome.setText(extra.get(position).getName());

        if (collection.getIs_free() == 1) {
            holder.preco.setText(Util.formaterPrice(0));
        } else {
            holder.preco.setText(Util.formaterPrice(extra.get(position).getPrice()));
        }
        if (extra.get(position).getDescription() != null && !extra.get(position).getDescription().equals("")) {
            holder.description.setVisibility(View.VISIBLE);
            holder.description.setText(extra.get(position).getDescription());
        }
        holder.setIsRecyclable(false);
    }

    @Override
    public int getItemCount() {
        if (extra == null)
            return 0;
        return extra.size();
    }

    void setExtraItem(List extraItem, Collection collection) {
        this.extra = extraItem;
        this.collection = collection;
        notifyDataSetChanged();
    }


    public interface ConjuctExtraOnClickListener {
        void conjuctExtra(Extra extra);
    }

}