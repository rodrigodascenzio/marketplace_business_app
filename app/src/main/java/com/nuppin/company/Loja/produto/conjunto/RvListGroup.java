package com.nuppin.company.Loja.produto.conjunto;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.nuppin.company.R;
import com.nuppin.company.model.Collection;

import java.util.List;


public class RvListGroup extends RecyclerView.Adapter<RvListGroup.RvConjuctAdapterLojaViewHolder> {

    private List<Collection> collection;
    private RvConjuctOnClickListener listener;

    RvListGroup(RvConjuctOnClickListener handler) {
        listener = handler;
    }


    public class RvConjuctAdapterLojaViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        final TextView productName, preco, required, options, descricao, invisible;
        Context ctx;

        RvConjuctAdapterLojaViewHolder(View view) {
            super(view);
            ctx = view.getContext();
            invisible = view.findViewById(R.id.invisible);
            productName = view.findViewById(R.id.nome);
            descricao = view.findViewById(R.id.descricao);
            preco = view.findViewById(R.id.preco);
            required = view.findViewById(R.id.required);
            options = view.findViewById(R.id.opcoes);
            view.setOnClickListener(this);
            invisible.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (collection.get(getAdapterPosition()).getIs_empty() == 0) {
                        Toast.makeText(ctx, "Grupo com a quantidade minima de escolhas maior que a quantidade de itens do grupo. Adicione mais itens ao grupo para resolver o problema", Toast.LENGTH_LONG).show();
                    }else {
                        Toast.makeText(ctx, "Adicione itens ao grupo", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            listener.onClick(collection.get(adapterPosition));
        }
    }

    @NonNull
    public RvConjuctAdapterLojaViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.rv_list_conjuct;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);
        return new RvConjuctAdapterLojaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RvConjuctAdapterLojaViewHolder holder, int position) {
        holder.required.setVisibility(View.GONE);
        holder.invisible.setVisibility(View.GONE);

        if (collection.get(position).getMin_quantity() > 0) {
            holder.required.setVisibility(View.VISIBLE);
        }

        if (collection.get(position).getHas_warning() == 1 || collection.get(position).getIs_empty() == 1) {
            holder.required.setVisibility(View.GONE);
            holder.invisible.setVisibility(View.VISIBLE);
        }

        if (collection.get(position).getIs_empty() == 1) {
            holder.required.setVisibility(View.GONE);
            holder.invisible.setVisibility(View.VISIBLE);
            holder.invisible.setText("Vazio");
        }

        holder.productName.setText(collection.get(position).getName());
        holder.descricao.setText(collection.get(position).getDescription());

        if (collection.get(position).getMin_quantity() == collection.get(position).getMax_quantity()) {
            if (collection.get(position).getMax_quantity() > 1) {
                holder.options.setText(collection.get(position).getMax_quantity()+" escolhas");
            }else {
                holder.options.setText("1 escolha");
            }
        }else {
            if (collection.get(position).getMin_quantity() > 1) {
                if (collection.get(position).getMax_quantity() > 1) {
                    holder.options.setText("De " + collection.get(position).getMin_quantity() + " Até " + collection.get(position).getMax_quantity() + " escolhas");
                } else {
                    holder.options.setText("De " + collection.get(position).getMin_quantity() + " Até " + collection.get(position).getMax_quantity() + " escolha");
                }
            } else {
                if (collection.get(position).getMax_quantity() > 1) {
                    holder.options.setText("Até " + collection.get(position).getMax_quantity() + " escolhas");
                } else {
                    holder.options.setText("Até " + collection.get(position).getMax_quantity() + " escolha");
                }
            }
        }
    }

    @Override
    public int getItemCount() {
        if (null == collection)
            return 0;
        return collection.size();
    }

    public void setCollection(List<Collection> collection) {
        this.collection = collection;
        notifyDataSetChanged();
    }

    public interface RvConjuctOnClickListener {
        void onClick(Collection collection);
    }
}