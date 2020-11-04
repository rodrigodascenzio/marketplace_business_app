package com.nuppin.company.Loja.Orders;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.nuppin.company.model.OrderItem;
import com.nuppin.company.R;
import com.nuppin.company.Util.Util;

import java.util.List;

public class RvOrderProductsAdapter extends RecyclerView.Adapter<RvOrderProductsAdapter.RvOrderProductsAdapterViewHolder> {

    private RvOrderProductsAdapterListener listener;
    private List<OrderItem> orderItems;

    public RvOrderProductsAdapter(List<OrderItem> orderItems) {
        this.orderItems = orderItems;
    }

    RvOrderProductsAdapter(RvOrderProductsAdapterListener handler) {
        listener = handler;
    }


    public class RvOrderProductsAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        final TextView quantidade, nome, preco, prodObs, tamanho;
        private RecyclerView recyclerView;
        private RvOrderProductsExtrasAdapter adapter;
        private Context ctx;

        RvOrderProductsAdapterViewHolder(View view) {
            super(view);
            ctx = view.getContext();
            recyclerView = view.findViewById(R.id.recycler);
            quantidade = view.findViewById(R.id.quantidade);
            nome = view.findViewById(R.id.nome);
            preco = view.findViewById(R.id.preco);
            prodObs = view.findViewById(R.id.obs);
            tamanho = view.findViewById(R.id.tamanho);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            //int adapterPosition = getAdapterPosition();
            // listener.onClick(ordList.get(adapterPosition), mapOrdPro.get(String.valueOf(ordList.get(adapterPosition).getOrder_id())));
        }
    }

    @NonNull
    public RvOrderProductsAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.request_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);
        return new RvOrderProductsAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RvOrderProductsAdapterViewHolder holder, int position) {
        holder.prodObs.setVisibility(View.GONE);
        holder.recyclerView.setVisibility(View.GONE);
        holder.tamanho.setVisibility(View.GONE);

        if (orderItems.get(position).getExtra() != null && orderItems.get(position).getExtra().size() > 0) {
            holder.recyclerView.setVisibility(View.VISIBLE);
        }

        if (orderItems.get(position).getSize_name() != null && !orderItems.get(position).getSize_name().isEmpty()) {
            holder.tamanho.setVisibility(View.VISIBLE);
            holder.tamanho.setText("Tamanho: "+orderItems.get(position).getSize_name());
        }

        holder.nome.setText(String.valueOf(orderItems.get(position).getName()));
        holder.preco.setText(Util.formaterPrice(orderItems.get(position).getTotal_amount()));
        holder.quantidade.setText(String.valueOf(orderItems.get(position).getQuantity()));
        if (orderItems.get(position).getNote() != null && !orderItems.get(position).getNote().equals("")) {
            holder.prodObs.setVisibility(View.VISIBLE);
            holder.prodObs.setText(holder.prodObs.getContext().getResources().getString(R.string.obs_prod, orderItems.get(position).getNote()));
        }

        LinearLayoutManager layoutManager = new LinearLayoutManager(holder.ctx, RecyclerView.VERTICAL, false);
        holder.recyclerView.setLayoutManager(layoutManager);
        holder.adapter = new RvOrderProductsExtrasAdapter();
        holder.recyclerView.setAdapter(holder.adapter);
        holder.adapter.setExtras(orderItems.get(position).getExtra());
    }

    @Override
    public int getItemCount() {
        if (null == orderItems)
            return 0;
        return orderItems.size();
    }

    void setOrder(List<OrderItem> map) {
        orderItems = map;
        notifyDataSetChanged();
    }

    interface RvOrderProductsAdapterListener {
        //void onClick(Order order, List<OrderItem> orderItems);
    }

}


