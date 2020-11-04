package com.nuppin.company.Loja.produto;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.nuppin.company.R;
import com.nuppin.company.model.Collection;
import com.nuppin.company.model.Product;
import com.nuppin.company.model.ProductCollection;

import java.util.ArrayList;
import java.util.List;

public class RvProductConjuct extends RecyclerView.Adapter<RvProductConjuct.RvProductConjuctAdapterViewHolder> {

    private List<ProductCollection> collections;
    private ConjuctsOnClickListener listener;
    private ArrayList<ProductCollection> conjuctsSelected;
    private Product product;

    RvProductConjuct(ConjuctsOnClickListener handler) {
        listener = handler;
        conjuctsSelected = new ArrayList<>();
    }

    public class RvProductConjuctAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        final CheckBox conjuct;

        RvProductConjuctAdapterViewHolder(View view) {
            super(view);
            conjuct = view.findViewById(R.id.checkBoxDias);
            conjuct.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();

            ProductCollection diaSelected = collections.get(adapterPosition);
            diaSelected.setProduct_id(product.getProduct_id());
            diaSelected.setCompany_id(product.getCompany_id());

            if (conjuctsSelected.contains(diaSelected)) {
                conjuctsSelected.remove(diaSelected);
            } else {
                conjuctsSelected.add(diaSelected);
            }

            listener.onClickConjuct(conjuctsSelected);
        }
    }

    @NonNull
    public RvProductConjuctAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.rv_add_update_company_schedule;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);
        return new RvProductConjuctAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RvProductConjuctAdapterViewHolder holder, int position) {
        holder.conjuct.setText(collections.get(position).getName());
        if (conjuctsSelected.contains(collections.get(position))) {
            holder.conjuct.setChecked(true);
        } else {
            holder.conjuct.setChecked(false);
        }
    }


    @Override
    public int getItemCount() {
        if (collections == null)
            return 0;
        return collections.size();
    }

    void setConjucts(List conjucts, Product product) {
        this.collections = conjucts;
        this.product = product;
        notifyDataSetChanged();
    }

    public interface ConjuctsOnClickListener {
        void onClickConjuct(List<ProductCollection> collections);
    }

}