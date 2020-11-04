package com.nuppin.company.Loja.Financeiro;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.nuppin.company.model.Finance;
import com.nuppin.company.R;
import com.nuppin.company.Util.Util;

import java.util.List;

public class CashFlowAdapter extends RecyclerView.Adapter<CashFlowAdapter.CashFlowAdapterViewHolder> {

    private List<Finance> mFinance;
    private CashFlowAdapterVHOnClickListener listener;

    public CashFlowAdapter() {
    }

    CashFlowAdapter(CashFlowAdapterVHOnClickListener handler) {
        listener = handler;
    }

    public class CashFlowAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView titulo, icon, descricao, valor, data_cad;

        CashFlowAdapterViewHolder(View view) {
            super(view);
            view.setOnClickListener(this);
            titulo = view.findViewById(R.id.titulo);
            icon = view.findViewById(R.id.icon);
            descricao = view.findViewById(R.id.descricao);
            valor = view.findViewById(R.id.valor);
            data_cad = view.findViewById(R.id.data);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            listener.onClickFromCashFlow(mFinance.get(adapterPosition));
        }
    }

    @NonNull
    public CashFlowAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.rv_cashflow;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);
        return new CashFlowAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CashFlowAdapterViewHolder holder, int position) {
        holder.titulo.setText(mFinance.get(position).getTitle());
        holder.descricao.setText(mFinance.get(position).getDescription());
        holder.valor.setText(Util.formaterPrice(mFinance.get(position).getAmount()));
        holder.data_cad.setText(Util.timestampFormatDayMonth(mFinance.get(position).getReference_date()));

        if (mFinance.get(position).getAmount() < 0) {
            holder.icon.setCompoundDrawablesWithIntrinsicBounds(holder.icon.getContext().getResources().getDrawable(R.drawable.ic_arrow_downward_black_24dp),null,null,null);
        }else {
            holder.icon.setCompoundDrawablesWithIntrinsicBounds(holder.icon.getContext().getResources().getDrawable(R.drawable.ic_arrow_upward_black_24dp),null,null,null);
        }
    }

    @Override
    public int getItemCount() {
        if (mFinance == null)
            return 0;
        return mFinance.size();
    }

    void setCashFlow(List<Finance> mFinance) {
        this.mFinance = mFinance;
        notifyDataSetChanged();
    }


    public interface CashFlowAdapterVHOnClickListener {
        void onClickFromCashFlow(Finance finance);
    }

}