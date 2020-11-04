package com.nuppin.company.pos.produto.list;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.nuppin.company.R;
import com.nuppin.company.Util.Util;
import com.nuppin.company.model.Size;

import java.util.List;

public class ProducSizeAdapterManual extends RecyclerView.Adapter<ProducSizeAdapterManual.ConjuctExtrasAdapterViewHolder> {

    private List<Size> sizes;
    private ConjuctBodyExtrasAdapterListener listener;
    private View lastView;

    ProducSizeAdapterManual(ConjuctBodyExtrasAdapterListener listener) {
        this.listener = listener;
    }

    public class ConjuctExtrasAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView eiName, eiValue, radioIndisponivel;
        RadioButton radio;
        View view;
        ConstraintLayout constraint;

        ConjuctExtrasAdapterViewHolder(View view) {
            super(view);
            this.view = view;
            radioIndisponivel = view.findViewById(R.id.radioIndisponivel);
            constraint = view.findViewById(R.id.constraint);
            eiName = view.findViewById(R.id.nome);
            eiValue = view.findViewById(R.id.preco);
            radio = view.findViewById(R.id.radio);
            view.setOnClickListener(this);
            radio.setClickable(false);
        }

        @Override
        public void onClick(View view) {
            if (lastView != null) {
                RadioButton checked_rb = lastView.findViewById(R.id.radio);
                checked_rb.setChecked(false);
                radio.setChecked(true);
            } else {
                radio.setChecked(true);
            }
            lastView = view;

            for (Size radio : sizes) {
                radio.setIs_selected(0);
            }
            sizes.get(getAdapterPosition()).setIs_selected(1);
            listener.onClickProducSize(sizes);
        }
    }

    @NonNull
    @Override
    public ConjuctExtrasAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.manual_rv_product_size;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);
        return new ConjuctExtrasAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ConjuctExtrasAdapterViewHolder holder, int position) {

        holder.eiName.setText(sizes.get(position).getName());
        holder.eiValue.setText("+ " + Util.formaterPrice(sizes.get(position).getPrice()));

        if (sizes.get(position).getStock_quantity() == 0){
            holder.radio.setVisibility(View.GONE);
            holder.radioIndisponivel.setVisibility(View.VISIBLE);
            holder.view.setEnabled(true);
            holder.view.setClickable(false);
        }

        if (sizes.get(position).getIs_selected() == 1) {
            lastView = holder.view;
            holder.radio.setChecked(true);
        }

        holder.setIsRecyclable(false);
    }

    @Override
    public int getItemCount() {
        if (sizes == null) {
            return 0;
        } else {
            return sizes.size();
        }
    }

    void setSizes(List<Size> sizes) {
        this.sizes = sizes;
        notifyDataSetChanged();
    }

    public interface ConjuctBodyExtrasAdapterListener {
        void onClickProducSize(List<Size> sizes);
    }

}
