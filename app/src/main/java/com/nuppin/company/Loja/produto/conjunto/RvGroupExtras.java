package com.nuppin.company.Loja.produto.conjunto;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.nuppin.company.R;
import com.nuppin.company.model.Collection;
import com.nuppin.company.model.CollectionExtra;
import com.nuppin.company.model.Extra;

import java.util.ArrayList;
import java.util.List;

public class RvGroupExtras extends RecyclerView.Adapter<RvGroupExtras.RvGroupExtrasAdapterViewHolder> {

    private List<CollectionExtra> extra;
    private extrasOnClickListener listener;
    private ArrayList<CollectionExtra> selectedExtras;
    private Collection collection;

    RvGroupExtras(extrasOnClickListener handler) {
        listener = handler;
        selectedExtras = new ArrayList<>();
    }

    public class RvGroupExtrasAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        final CheckBox extra;
        TextView nome, desc, preco;
        ConstraintLayout constraintLayout;

        RvGroupExtrasAdapterViewHolder(View view) {
            super(view);
            constraintLayout = view.findViewById(R.id.constraint);
            extra = view.findViewById(R.id.extra);
            nome = view.findViewById(R.id.nome);
            desc = view.findViewById(R.id.description);
            preco = view.findViewById(R.id.preco);
            constraintLayout.setOnClickListener(this);
            extra.setClickable(false);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();

            CollectionExtra extraSelected = RvGroupExtras.this.extra.get(adapterPosition);
            extraSelected.setCollection_id(collection.getCollection_id());
            extraSelected.setCompany_id(collection.getCompany_id());

            if (selectedExtras.contains(extraSelected)) {
                selectedExtras.remove(extraSelected);
                extra.setChecked(false);
            } else {
                selectedExtras.add(extraSelected);
                extra.setChecked(true);
            }

            listener.onClickExtra(selectedExtras);
        }
    }

    @NonNull
    public RvGroupExtrasAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.rv_conjuct_extra_set;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);
        return new RvGroupExtrasAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RvGroupExtrasAdapterViewHolder holder, int position) {
        holder.extra.setText(extra.get(position).getName());
        if (selectedExtras.contains(extra.get(position))) {
            holder.extra.setChecked(true);
        }else {
            holder.extra.setChecked(false);
        }

        holder.setIsRecyclable(false);
    }


    @Override
    public int getItemCount() {
        if (extra == null)
            return 0;
        return extra.size();
    }

    void setExtras(List extraItem, Collection collection) {
        this.extra = extraItem;
        this.collection = collection;
        notifyDataSetChanged();
    }

    public interface extrasOnClickListener {
        void onClickExtra(List<CollectionExtra> extra);
    }

}