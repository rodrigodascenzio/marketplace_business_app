package com.nuppin.company.Loja.produto.size;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.nuppin.company.R;
import com.nuppin.company.model.Product;
import com.nuppin.company.model.Size;

import java.util.List;

public class RvProductSizeDialog extends RecyclerView.Adapter<RvProductSizeDialog.RvProductConjuctAdapterViewHolder> {

    private List<Size> size;
    private ConjuctsOnClickListener listener;
    private Product product;

    RvProductSizeDialog(ConjuctsOnClickListener handler) {
        listener = handler;
    }

    public class RvProductConjuctAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView size;

        RvProductConjuctAdapterViewHolder(View view) {
            super(view);
            size = view.findViewById(R.id.size);
            size.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();

            Size diaSelected = RvProductSizeDialog.this.size.get(adapterPosition);
            diaSelected.setProduct_id(product.getProduct_id());
            listener.onClickConjuct(diaSelected);

        }
    }

    @NonNull
    public RvProductConjuctAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.rv_product_size_dialog;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);
        return new RvProductConjuctAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RvProductConjuctAdapterViewHolder holder, int position) {
        holder.size.setText(size.get(position).getName());
    }


    @Override
    public int getItemCount() {
        if (size == null)
            return 0;
        return size.size();
    }

    void setConjucts(List productSize, Product product) {
        this.size = productSize;
        this.product = product;
        notifyDataSetChanged();
    }

    public interface ConjuctsOnClickListener {
        void onClickConjuct(Size sizes);
    }

}