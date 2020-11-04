package com.nuppin.company.pos.produto.carrinho;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.nuppin.company.R;
import com.nuppin.company.Util.Util;
import com.nuppin.company.model.CartProduct;

import java.util.List;

public class RvCarrinhoAdapterManual extends RecyclerView.Adapter<RvCarrinhoAdapterManual.RvCartAdapterViewHolder> {

    private List<CartProduct> cart;
    private RvCartOnClickListener listener;
    private Context ctx;
    private View view;

    RvCarrinhoAdapterManual(RvCartOnClickListener handler, Context ctx) {
        listener = handler;
        this.ctx = ctx;
    }

    public class RvCartAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, CartExtrasAdapterManual.RvCartExtraOnClickListener {

        final TextView cartNome, prodObs;
        final TextView cartQuantidade, stockWarning, tamanho;
        final TextView preco;
        private RecyclerView recyclerView;
        private CartExtrasAdapterManual adapter;
        private View v;
        final TextView edit, exclui;

        RvCartAdapterViewHolder(View view) {
            super(view);
            v = view;
            tamanho = view.findViewById(R.id.tamanho);
            recyclerView = view.findViewById(R.id.recycler);
            cartNome = view.findViewById(R.id.nome);
            prodObs = view.findViewById(R.id.obs);
            cartQuantidade = view.findViewById(R.id.quantidade);
            stockWarning = view.findViewById(R.id.stockWarning);
            preco = view.findViewById(R.id.preco);
            view.setOnClickListener(this);
            exclui = view.findViewById(R.id.cartExcluir);
            edit = view.findViewById(R.id.cartEditar);
        }

        @Override
        public void onClick(View v) {
            showHide(v, getAdapterPosition());
        }

        @Override
        public void onClickExtra(View view) {
            showHide(view,getAdapterPosition());
        }
    }

    private void showHide(View v, final int adapterPosition) {
        v.findViewById(R.id.cartExcluir).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onClickExclui(cart.get(adapterPosition));
            }
        });
        v.findViewById(R.id.cartEditar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onClick(cart.get(adapterPosition));
            }
        });
        if (view != null) {

            if (view == v) {
                if (v.findViewById(R.id.cartExcluir).getVisibility() == View.VISIBLE) {
                    v.findViewById(R.id.cartExcluir).setVisibility(View.GONE);
                    v.findViewById(R.id.cartEditar).setVisibility(View.GONE);
                    return;
                } else {
                    v.findViewById(R.id.cartExcluir).setVisibility(View.VISIBLE);
                    v.findViewById(R.id.cartEditar).setVisibility(View.VISIBLE);
                    return;
                }
            }
            view.findViewById(R.id.cartExcluir).setVisibility(View.GONE);
            view.findViewById(R.id.cartEditar).setVisibility(View.GONE);
        }
        v.findViewById(R.id.cartExcluir).setVisibility(View.VISIBLE);
        v.findViewById(R.id.cartEditar).setVisibility(View.VISIBLE);

        view = v;
    }

    @NonNull
    public RvCartAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.manual_rv_cart;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;
        View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);
        return new RvCartAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RvCartAdapterViewHolder holder, int position) {
        holder.prodObs.setVisibility(View.GONE);
        holder.edit.setVisibility(View.GONE);
        holder.exclui.setVisibility(View.GONE);
        holder.stockWarning.setVisibility(View.GONE);
        holder.recyclerView.setVisibility(View.GONE);
        holder.tamanho.setVisibility(View.GONE);

        if (cart.get(position).getExtra() != null && cart.get(position).getExtra().size() > 0) {
            holder.recyclerView.setVisibility(View.VISIBLE);
        }

        if (cart.get(position).getSize_name() != null && !cart.get(position).getSize_name().isEmpty()) {
            holder.tamanho.setVisibility(View.VISIBLE);
            holder.tamanho.setText("Tamanho: "+cart.get(position).getSize_name());
        }

        if (cart.get(position).getIs_available() == 0) {
            holder.stockWarning.setVisibility(View.VISIBLE);
        }

        holder.cartNome.setText(cart.get(position).getName());
        if (cart.get(position).getNote() != null && !cart.get(position).getNote().equals("")) {
            holder.prodObs.setVisibility(View.VISIBLE);
            holder.prodObs.setText(ctx.getResources().getString(R.string.obs_prod, cart.get(position).getNote()));
        }
        holder.cartQuantidade.setText(String.valueOf(cart.get(position).getQuantity()));
        holder.preco.setText(Util.formaterPrice(cart.get(position).getTotal_price()));

        LinearLayoutManager layoutManager = new LinearLayoutManager(ctx, RecyclerView.VERTICAL, false);
        holder.recyclerView.setLayoutManager(layoutManager);
        holder.adapter = new CartExtrasAdapterManual(holder, holder.v);
        holder.recyclerView.setAdapter(holder.adapter);
        holder.adapter.setExtras(cart.get(position).getExtra());
    }

    @Override
    public int getItemCount() {
        if (null == cart)
            return 0;
        return cart.size();
    }

    void setCart(List cart) {
        this.cart = cart;
        notifyDataSetChanged();
    }


    public interface RvCartOnClickListener {
        void onClick(CartProduct cart);
        void onClickExclui(CartProduct cart);
    }
}

