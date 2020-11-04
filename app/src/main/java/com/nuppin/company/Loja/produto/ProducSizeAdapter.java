package com.nuppin.company.Loja.produto;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.nuppin.company.R;
import com.nuppin.company.Util.Util;
import com.nuppin.company.model.Size;

import java.util.List;


public class ProducSizeAdapter extends RecyclerView.Adapter<ProducSizeAdapter.ConjuctExtrasAdapterViewHolder> {

    private List<Size> sizes;
    private ConjuctBodyExtrasAdapterListener listener;

    ProducSizeAdapter(ConjuctBodyExtrasAdapterListener listener) {
        this.listener = listener;
    }

    public class ConjuctExtrasAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView stock, name, value;

        ConjuctExtrasAdapterViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.nome);
            value = view.findViewById(R.id.preco);
            stock = view.findViewById(R.id.stock);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            listener.onClickProducSize(sizes.get(getAdapterPosition()));
        }
    }

    @NonNull
    @Override
    public ConjuctExtrasAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.rv_product_size;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);
        return new ConjuctExtrasAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ConjuctExtrasAdapterViewHolder holder, int position) {
        holder.name.setText("Tamanho: "+ sizes.get(position).getName());
        holder.stock.setText(String.valueOf(sizes.get(position).getStock_quantity()));
        holder.value.setText(" + "+Util.formaterPrice(sizes.get(position).getPrice()));
    }

    @Override
    public int getItemCount() {
        if (sizes == null) {
            return 0;
        } else {
            return sizes.size();
        }
    }

    void setSizes(List<Size> sizes) {
        this.sizes = sizes;
        notifyDataSetChanged();
    }

    public interface ConjuctBodyExtrasAdapterListener {
        void onClickProducSize(Size size);
    }

}
