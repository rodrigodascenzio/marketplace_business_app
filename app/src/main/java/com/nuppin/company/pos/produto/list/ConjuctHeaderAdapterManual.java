package com.nuppin.company.pos.produto.list;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.nuppin.company.R;
import com.nuppin.company.model.CollectionExtra;

import java.util.List;

public class ConjuctHeaderAdapterManual extends RecyclerView.Adapter<ConjuctHeaderAdapterManual.ConjuctHeaderAdapterViewHolder> {

    private List<CollectionExtra> collectionExtra;
    ConjuctHeaderAdapterListener listener;

    ConjuctHeaderAdapterManual(ConjuctHeaderAdapterListener listener) {
        this.listener = listener;
    }

    public class ConjuctHeaderAdapterViewHolder extends RecyclerView.ViewHolder
            implements ConjuctExtrasAdapterManual.ConjuctBodyExtrasAdapterListener,
            View.OnClickListener, ConjuctExtrasRadioAdapterManual.ConjuctBodyExtrasAdapterListener {

        TextView nome, options, description, required, arrow;
        Context ctx;
        View view;
        RecyclerView recyclerView;
        ConstraintLayout card;
        ConjuctExtrasAdapterManual adapter;
        ConjuctExtrasRadioAdapterManual adapterRadio;

        ConjuctHeaderAdapterViewHolder(View view) {
            super(view);
            this.view = view;
            arrow = view.findViewById(R.id.arrow);
            card = view.findViewById(R.id.card);
            recyclerView = view.findViewById(R.id.recycler);
            ctx = view.getContext();
            nome = view.findViewById(R.id.nome);
            options = view.findViewById(R.id.opcoes);
            required = view.findViewById(R.id.required);
            description = view.findViewById(R.id.description);
            card.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int adapterPosition = getAdapterPosition();

            if (recyclerView.getVisibility() == View.VISIBLE) {
                recyclerView.setVisibility(View.GONE);
                arrow.setCompoundDrawablesWithIntrinsicBounds(arrow.getResources().getDrawable(R.drawable.ic_keyboard_arrow_down_black_24dp), null, null, null);
            } else {
                arrow.setCompoundDrawablesWithIntrinsicBounds(arrow.getResources().getDrawable(R.drawable.ic_keyboard_arrow_up_black_24dp), null, null, null);
                recyclerView.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onClickCheck(CollectionExtra collectionExtras) {
            listener.onHeaderClick(collectionExtra);
        }
    }

    @NonNull
    @Override
    public ConjuctHeaderAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.manual_rv_conjuct_product;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);
        return new ConjuctHeaderAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ConjuctHeaderAdapterViewHolder holder, int position) {
        holder.required.setVisibility(View.GONE);

        if (collectionExtra.get(position).getMin_quantity() > 0) {
            holder.required.setVisibility(View.VISIBLE);
        }

        if (collectionExtra.get(position).getMin_quantity() == collectionExtra.get(position).getMax_quantity()) {
            if (collectionExtra.get(position).getMax_quantity() > 1) {
                holder.options.setText(collectionExtra.get(position).getMax_quantity() + " escolhas");
            } else {
                holder.options.setText("1 escolha");
            }
        } else {
            if (collectionExtra.get(position).getMin_quantity() > 1) {
                if (collectionExtra.get(position).getMax_quantity() > 1) {
                    holder.options.setText("De " + collectionExtra.get(position).getMin_quantity() + " Até " + collectionExtra.get(position).getMax_quantity() + " escolhas");
                } else {
                    holder.options.setText("De " + collectionExtra.get(position).getMin_quantity() + " Até " + collectionExtra.get(position).getMax_quantity() + " escolha");
                }
            } else {
                if (collectionExtra.get(position).getMax_quantity() > 1) {
                    holder.options.setText("Até " + collectionExtra.get(position).getMax_quantity() + " escolhas");
                } else {
                    holder.options.setText("Até " + collectionExtra.get(position).getMax_quantity() + " escolha");
                }
            }
        }

        holder.nome.setText(collectionExtra.get(position).getName());
        holder.description.setText(collectionExtra.get(position).getDescription());
        LinearLayoutManager layoutManager = new LinearLayoutManager(holder.ctx, RecyclerView.VERTICAL, false);
        holder.recyclerView.setLayoutManager(layoutManager);
        if (collectionExtra.get(position).getMin_quantity() == 1 && collectionExtra.get(position).getMax_quantity() == 1) {
            holder.adapterRadio = new ConjuctExtrasRadioAdapterManual(holder);
            holder.recyclerView.setAdapter(holder.adapterRadio);
            holder.adapterRadio.setExtras(collectionExtra.get(position).getConjuct_extras(), collectionExtra.get(position));
        } else {
            holder.adapter = new ConjuctExtrasAdapterManual(holder);
            holder.recyclerView.setAdapter(holder.adapter);
            holder.adapter.setExtras(collectionExtra.get(position).getConjuct_extras(), collectionExtra.get(position));
        }
    }

    @Override
    public int getItemCount() {
        if (collectionExtra == null) {
            return 0;
        } else {
            return collectionExtra.size();
        }
    }

    void setConjuct(List<CollectionExtra> collectionExtra) {
        this.collectionExtra = collectionExtra;
        notifyDataSetChanged();
    }

    public interface ConjuctHeaderAdapterListener {
        void onHeaderClick(List<CollectionExtra> conjuct);
    }

}
