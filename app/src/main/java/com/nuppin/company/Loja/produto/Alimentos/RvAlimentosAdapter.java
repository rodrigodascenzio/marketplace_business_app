package com.nuppin.company.Loja.produto.Alimentos;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.nuppin.company.Util.UtilShaPre;
import com.nuppin.company.model.Product;
import com.nuppin.company.R;
import com.nuppin.company.Util.Util;

import java.util.List;

import me.toptas.fancyshowcase.FancyShowCaseView;
import me.toptas.fancyshowcase.FocusShape;

public class RvAlimentosAdapter extends RecyclerView.Adapter<RvAlimentosAdapter.RvProdutosAdapterViewHolder> {

    private List<Product> products;
    private RvProdutosOnClickListener listener;
    private FragmentActivity fragmentActivity;

    public RvAlimentosAdapter(RvProdutosOnClickListener handler, FragmentActivity fragmentActivity) {
        listener = handler;
        this.fragmentActivity = fragmentActivity;
    }


    public class RvProdutosAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        final TextView productName, preco, desc, estoqueNum;
        final SimpleDraweeView imagem;
        LinearLayout estoqueCard;
        Context ctx;
        private CardView cardProd;


        private FancyShowCaseView fancyShowCaseView1;

        private void chainCreatedTutorial() {
            if (fancyShowCaseView1 == null) {
                fancyShowCaseView1 = new FancyShowCaseView.Builder(fragmentActivity)
                        .focusOn(cardProd)
                        .title(ctx.getString(R.string.unique_tutorial_produto_card_produto_string))
                        .focusShape(FocusShape.ROUNDED_RECTANGLE)
                        .showOnce(ctx.getString(R.string.unique_tutorial_produto_card_produto))
                        .backgroundColor(ctx.getResources().getColor(R.color.primary_light))
                        .enableAutoTextPosition()
                        .closeOnTouch(true)
                        .build();
            }
        }

        private void chainTutorial() {
            if (!fancyShowCaseView1.isShownBefore()) {
                fancyShowCaseView1.show();
                return;
            }
        }

        RvProdutosAdapterViewHolder(View view) {
            super(view);
            ctx = view.getContext();
            cardProd = view.findViewById(R.id.cardProduto);
            productName = view.findViewById(R.id.nome);
            preco = view.findViewById(R.id.preco);
            imagem = view.findViewById(R.id.imagem);
            desc = view.findViewById(R.id.desc);
            estoqueCard = view.findViewById(R.id.estoqueCard);
            estoqueNum = view.findViewById(R.id.estoqueNum);
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
        int layoutIdForListItem = R.layout.rv_list_product_food;
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(layoutIdForListItem, viewGroup, false);
        return new RvProdutosAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RvProdutosAdapterViewHolder holder, int position) {
        holder.productName.setText(products.get(position).getName());
        holder.preco.setText(Util.formaterPrice(products.get(position).getPrice()));
        holder.desc.setText(products.get(position).getDescription());
        Util.hasPhoto(products.get(position), holder.imagem);

        if (products.get(position).getIs_stock() == 1) {
            holder.estoqueCard.setVisibility(View.VISIBLE);
            if (products.get(position).getStock_quantity() > 0) {
                holder.estoqueCard.setBackground(holder.ctx.getResources().getDrawable(R.drawable.corner_card_estoque));
                holder.estoqueNum.setText(String.valueOf(products.get(position).getStock_quantity()));
            } else {
                holder.estoqueCard.setBackground(holder.ctx.getResources().getDrawable(R.drawable.corner_card_estoque_empty));
                holder.estoqueNum.setText(String.valueOf(products.get(position).getStock_quantity()));
            }
        } else if (products.get(position).getIs_multi_stock() == 1) {
            holder.estoqueCard.setVisibility(View.VISIBLE);
            if (products.get(position).getMulti_stock_quantity() > 0) {
                holder.estoqueCard.setBackground(holder.ctx.getResources().getDrawable(R.drawable.corner_card_estoque));
                holder.estoqueNum.setText(String.valueOf(products.get(position).getMulti_stock_quantity()));
            } else {
                holder.estoqueCard.setBackground(holder.ctx.getResources().getDrawable(R.drawable.corner_card_estoque_empty));
                holder.estoqueNum.setText(String.valueOf(products.get(position).getMulti_stock_quantity()));
            }
        } else {
            holder.estoqueCard.setVisibility(View.GONE);
        }

        if (position == products.size() - 1) {
            if (products.size() == 1) {
                holder.chainCreatedTutorial();
                holder.chainTutorial();
            } else if (products.size() > 0) {
                UtilShaPre.writeShown(fragmentActivity, holder.ctx.getString(R.string.unique_tutorial_produto_card_produto));
            }
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


