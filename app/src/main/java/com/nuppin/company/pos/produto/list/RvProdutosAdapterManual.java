package com.nuppin.company.pos.produto.list;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.nuppin.company.R;
import com.nuppin.company.Util.Util;
import com.nuppin.company.model.Product;

import java.util.List;

public class RvProdutosAdapterManual extends RecyclerView.Adapter<RvProdutosAdapterManual.RvProdutosAdapterViewHolder> {

    private List<Product> products;
    private RvProdutosOnClickListener listener;

    RvProdutosAdapterManual(RvProdutosOnClickListener handler) {
        listener = handler;
    }


    public class RvProdutosAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        final TextView productName, preco, descricao;
        final SimpleDraweeView imagem;
        CardView cardProduto;

        RvProdutosAdapterViewHolder(View view) {
            super(view);
            cardProduto = view.findViewById(R.id.cardProduto);
            productName = view.findViewById(R.id.nome);
            preco = view.findViewById(R.id.preco);
            imagem = view.findViewById(R.id.imagem);
            descricao = view.findViewById(R.id.descricao);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            listener.onClick(products.get(adapterPosition));
        }
    }

    @NonNull
    public RvProdutosAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.manual_rv_list_product;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);
        return new RvProdutosAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RvProdutosAdapterViewHolder holder, int position) {
        holder.productName.setText(products.get(position).getName());
        Util.hasPhoto(products.get(position), holder.imagem);
        holder.descricao.setText(products.get(position).getDescription());

        if (products.get(position).getIs_stock() == 1 && products.get(position).getStock_quantity() < 1) {
            holder.preco.setText(R.string.produto_indisponivel);
            holder.preco.setTextColor(holder.preco.getContext().getResources().getColor(R.color.orange_nuppin));
            holder.cardProduto.setClickable(false);
        } else if (products.get(position).getIs_multi_stock() == 1 && products.get(position).getMulti_stock_quantity() < 1) {
            holder.preco.setText(R.string.produto_indisponivel);
            holder.preco.setTextColor(holder.preco.getContext().getResources().getColor(R.color.orange_nuppin));
            holder.cardProduto.setClickable(false);
        } else {
            holder.preco.setText(Util.formaterPrice(products.get(position).getPrice()));
            holder.preco.setTextColor(holder.preco.getContext().getResources().getColor(R.color.green_dark));
        }
    }

    @Override
    public int getItemCount() {
        if (null == products)
            return 0;
        return products.size();
    }

    public void setProducts(List<Product> products) {
        this.products = products;
        notifyDataSetChanged();
    }

    public interface RvProdutosOnClickListener {
        void onClick(Product product);
    }

}