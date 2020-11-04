package com.nuppin.company.Loja.produto.size;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.nuppin.company.R;
import com.nuppin.company.model.Size;

import java.util.List;


public class RvSizeAdapter extends RecyclerView.Adapter<RvSizeAdapter.RvExtrasAdapterLojaViewHolder> {

    private List<Size> Sizes;
    private RvExtraItemsOnClickListener listener;

    RvSizeAdapter(RvExtraItemsOnClickListener handler) {
        listener = handler;
    }


    public class RvExtrasAdapterLojaViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        final TextView productName, preco, description;
        Context ctx;

        RvExtrasAdapterLojaViewHolder(View view) {
            super(view);
            ctx = view.getContext();
            productName = view.findViewById(R.id.nome);
            description = view.findViewById(R.id.description);
            preco = view.findViewById(R.id.preco);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            listener.onClick(Sizes.get(adapterPosition));
        }
    }

    @NonNull
    public RvExtrasAdapterLojaViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.rv_list_extra;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);
        return new RvExtrasAdapterLojaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RvExtrasAdapterLojaViewHolder holder, int position) {
        holder.description.setVisibility(View.GONE);
        holder.productName.setText(Sizes.get(position).getName());
    }

    @Override
    public int getItemCount() {
        if (null == Sizes)
            return 0;
        return Sizes.size();
    }

    public void setExtras(List<Size> Sizes) {
        this.Sizes = Sizes;
        notifyDataSetChanged();
    }

    public interface RvExtraItemsOnClickListener {
        void onClick(Size size);
    }
}