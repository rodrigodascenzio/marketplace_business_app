package com.nuppin.company.pos.servico;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.nuppin.company.R;
import com.nuppin.company.model.PaymentCompany;

import java.util.List;

public class RvMeiosPagamentoManual extends RecyclerView.Adapter<RvMeiosPagamentoManual.MeiosPagamentosAdapterViewHolder> {

    private List<PaymentCompany> companyPayments;
    private meiosPagamentoOnClickListener listener;

    RvMeiosPagamentoManual(meiosPagamentoOnClickListener handler) {
        listener = handler;
    }

    public class MeiosPagamentosAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        final TextView meiopag;

        MeiosPagamentosAdapterViewHolder(View view) {
            super(view);
            meiopag = view.findViewById(R.id.checkBoxMeiosPag);
            meiopag.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            listener.onClickMeioPagamento(companyPayments.get(adapterPosition).getName(), companyPayments.get(adapterPosition).getPayment_id());
        }
    }

    @NonNull
    public MeiosPagamentosAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.rv_company_payment;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);
        return new MeiosPagamentosAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MeiosPagamentosAdapterViewHolder holder, int position) {
            holder.meiopag.setText(companyPayments.get(position).getName());
    }

    @Override
    public int getItemCount() {
        if (companyPayments == null)
            return 0;
        return companyPayments.size();
    }

    void setMeiosPagamento(List meiosPagamentos) {
        this.companyPayments = meiosPagamentos;
        notifyDataSetChanged();
    }


    public interface meiosPagamentoOnClickListener {
        void onClickMeioPagamento(String nome, String id);
    }

}