package com.nuppin.company.pos.produto.list;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.nuppin.company.R;
import com.nuppin.company.Util.Util;
import com.nuppin.company.model.CollectionExtra;
import com.nuppin.company.model.Extra;

import java.util.List;

public class ConjuctExtrasAdapterManual extends RecyclerView.Adapter<ConjuctExtrasAdapterManual.ConjuctExtrasAdapterViewHolder> {

    private List<Extra> extras;
    private CollectionExtra collectionExtra;
    private ConjuctBodyExtrasAdapterListener listener;

    ConjuctExtrasAdapterManual(ConjuctBodyExtrasAdapterListener listener) {
        this.listener = listener;
    }

    public class ConjuctExtrasAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView eiName, eiValue, eiDrescription, btnPlus, btnMinus, value;
        Context ctx;
        View view;

        ConjuctExtrasAdapterViewHolder(View view) {
            super(view);
            this.view = view;
            ctx = view.getContext();
            btnPlus = view.findViewById(R.id.qtdMais);
            btnMinus = view.findViewById(R.id.qtdMenos);
            value = view.findViewById(R.id.txtQtd);
            eiName = view.findViewById(R.id.nome);
            eiValue = view.findViewById(R.id.preco);
            eiDrescription = view.findViewById(R.id.description);

            btnPlus.setOnClickListener(this);
            btnMinus.setOnClickListener(this);
            value.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.qtdMais:
                    if (collectionExtra.getQuantity_selected() < collectionExtra.getMax_quantity()) {
                        value.setText(String.valueOf(Integer.parseInt(value.getText().toString()) + 1));
                        collectionExtra.setQuantity_selected(collectionExtra.getQuantity_selected() + 1);
                        extras.get(getAdapterPosition()).setQuantity(extras.get(getAdapterPosition()).getQuantity() + 1);
                        collectionExtra.setTotal_price(collectionExtra.getTotal_price() + extras.get(getAdapterPosition()).getPrice());
                        if (btnMinus.getVisibility() == View.GONE) {
                            btnMinus.setVisibility(View.VISIBLE);
                            value.setVisibility(View.VISIBLE);
                        }
                    }
                    break;
                case R.id.qtdMenos:
                    if (Integer.parseInt(value.getText().toString()) > 0) {
                        if (Integer.parseInt(value.getText().toString()) == 1) {
                            value.setText("0");
                            btnMinus.setVisibility(View.GONE);
                            value.setVisibility(View.GONE);
                        } else {
                            value.setText(String.valueOf(Integer.parseInt(value.getText().toString()) - 1));
                        }
                        collectionExtra.setQuantity_selected(collectionExtra.getQuantity_selected() - 1);
                        extras.get(getAdapterPosition()).setQuantity(extras.get(getAdapterPosition()).getQuantity() - 1);
                        collectionExtra.setTotal_price(collectionExtra.getTotal_price() - extras.get(getAdapterPosition()).getPrice());
                    }
                    break;
            }
            listener.onClickCheck(collectionExtra);
        }
    }

    @NonNull
    @Override
    public ConjuctExtrasAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.manual_rv_conjuct_extra;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);
        return new ConjuctExtrasAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ConjuctExtrasAdapterViewHolder holder, int position) {
        holder.eiDrescription.setVisibility(View.GONE);
        holder.btnMinus.setVisibility(View.GONE);
        holder.value.setVisibility(View.GONE);

        if (extras.get(position).getQuantity() > 0) {
            holder.btnMinus.setVisibility(View.VISIBLE);
            holder.value.setVisibility(View.VISIBLE);
            holder.value.setText(String.valueOf(extras.get(position).getQuantity()));
        } else {
            holder.value.setText("0");
            holder.btnMinus.setVisibility(View.GONE);
            holder.value.setVisibility(View.GONE);
        }

        holder.eiName.setText(extras.get(position).getName());
        holder.eiValue.setText(Util.formaterPrice(extras.get(position).getPrice()));
        if (extras.get(position).getDescription() != null && !extras.get(position).getDescription().equals("")) {
            holder.eiDrescription.setVisibility(View.VISIBLE);
            holder.eiDrescription.setText(extras.get(position).getDescription());
        }
    }

    @Override
    public int getItemCount() {
        if (extras == null) {
            return 0;
        } else {
            return extras.size();
        }
    }

    void setExtras(List<Extra> extras, CollectionExtra collectionExtra) {
        this.extras = extras;
        this.collectionExtra = collectionExtra;
        notifyDataSetChanged();
    }

    public interface ConjuctBodyExtrasAdapterListener {
        void onClickCheck(CollectionExtra extraItem);
    }

}
