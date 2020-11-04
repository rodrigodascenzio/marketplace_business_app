package com.nuppin.company.Loja.loja.cadastro;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.nuppin.company.model.Subcategory;
import com.nuppin.company.R;

import java.util.List;

public class CategoriaAdapter extends RecyclerView.Adapter<CategoriaAdapter.CategoriaAdapterViewHolder> {

    private List<Subcategory> stoCategorias;
    private CategoriaAdapterViewHolderOnClickListener listener;

    CategoriaAdapter(CategoriaAdapterViewHolderOnClickListener handler) {
        listener = handler;
    }

    public class CategoriaAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        final TextView categoria;

        CategoriaAdapterViewHolder(View view) {
            super(view);
            categoria = view.findViewById(R.id.categoria_loja);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            listener.onClickFromCategorias(stoCategorias.get(adapterPosition).getSubcategory_company_id());
        }
    }

    @NonNull
    public CategoriaAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.company_category;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);
        return new CategoriaAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoriaAdapterViewHolder holder, int position) {
        holder.categoria.setText(stoCategorias.get(position).getName());
    }

    @Override
    public int getItemCount() {
        if (stoCategorias == null)
            return 0;
        return stoCategorias.size();
    }

    void setSCategorias(List mstoCategorias) {
        stoCategorias = mstoCategorias;
        notifyDataSetChanged();
    }


    public interface CategoriaAdapterViewHolderOnClickListener {
        void onClickFromCategorias(String categorias);
    }

}