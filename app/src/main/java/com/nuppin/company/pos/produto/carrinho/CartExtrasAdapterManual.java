package com.nuppin.company.pos.produto.carrinho;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.nuppin.company.R;
import com.nuppin.company.model.CartExtraItem;

import java.util.List;

public class CartExtrasAdapterManual extends RecyclerView.Adapter<CartExtrasAdapterManual.ConjuctExtrasAdapterViewHolder> {

    private List<CartExtraItem> extraItems;
    private RvCartExtraOnClickListener listener;
    private View v;

    CartExtrasAdapterManual(RvCartExtraOnClickListener handler, View v) {
        this.listener = handler;
        this.v = v;
    }

    class ConjuctExtrasAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView eiName, eiQuantity;
        Context ctx;
        View view;

        ConjuctExtrasAdapterViewHolder(View view) {
            super(view);
            this.view = view;
            ctx = view.getContext();
            eiQuantity = view.findViewById(R.id.quantidade);
            eiName = view.findViewById(R.id.nome);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            listener.onClickExtra(v);
        }
    }

    @NonNull
    @Override
    public ConjuctExtrasAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.manual_rv_cart_extras;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);
        return new ConjuctExtrasAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ConjuctExtrasAdapterViewHolder holder, int position) {
        holder.eiName.setText(extraItems.get(position).getName());
        holder.eiQuantity.setText(String.valueOf(extraItems.get(position).getQuantity()));
    }

    @Override
    public int getItemCount() {
        if (extraItems == null) {
            return 0;
        } else {
            return extraItems.size();
        }
    }

    void setExtras(List<CartExtraItem> extraItems) {
        this.extraItems = extraItems;
        notifyDataSetChanged();
    }

    public interface RvCartExtraOnClickListener {
        void onClickExtra(View v);
    }

}
