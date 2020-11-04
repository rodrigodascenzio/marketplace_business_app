package com.nuppin.company.Loja.produto.extra;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.nuppin.company.R;
import com.nuppin.company.Util.Util;
import com.nuppin.company.model.Extra;

import java.util.List;


public class RvExtraAdapter extends RecyclerView.Adapter<RvExtraAdapter.RvExtrasAdapterLojaViewHolder> {

    private List<Extra> extras;
    private RvExtraItemsOnClickListener listener;

    RvExtraAdapter(RvExtraItemsOnClickListener handler) {
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
            listener.onClick(extras.get(adapterPosition));
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


        holder.productName.setText(extras.get(position).getName());
        if (extras.get(position).getDescription() != null && !extras.get(position).getDescription().equals("")) {
            holder.description.setVisibility(View.VISIBLE);
            holder.description.setText(extras.get(position).getDescription());
        }
        holder.preco.setText(Util.formaterPrice(extras.get(position).getPrice()));
    }

    @Override
    public int getItemCount() {
        if (null == extras)
            return 0;
        return extras.size();
    }

    public void setExtras(List<Extra> extras) {
        this.extras = extras;
        notifyDataSetChanged();
    }

    public interface RvExtraItemsOnClickListener {
        void onClick(Extra extraItems);
    }
}