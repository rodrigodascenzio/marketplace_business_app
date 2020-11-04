package com.nuppin.company.Loja.Config.pagamentos;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.nuppin.company.model.PaymentCompany;
import com.nuppin.company.R;

import java.util.List;

public class meiosPagamentosAdapter extends RecyclerView.Adapter<meiosPagamentosAdapter.MeiosPagamentosAdapterViewHolder> {

    private List<PaymentCompany> paymentCompanies;
    private meiosPagamentoOnClickListener listener;

    public meiosPagamentosAdapter(meiosPagamentoOnClickListener handler) {
        listener = handler;
    }

    public class MeiosPagamentosAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        final CheckBox meiopag;

        MeiosPagamentosAdapterViewHolder(View view) {
            super(view);
            meiopag = view.findViewById(R.id.checkBoxMeiosPag);
            meiopag.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            listener.onClickMeioPagamento(meiopag.isChecked(), paymentCompanies.get(adapterPosition).getPayment_id());
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
        holder.meiopag.setText(paymentCompanies.get(position).getName());
        if (paymentCompanies.get(position).getIs_checked().equals("1")) {
            holder.meiopag.setChecked(true);
        }
        holder.setIsRecyclable(false);
    }

    @Override
    public int getItemCount() {
        if (paymentCompanies == null)
            return 0;
        return paymentCompanies.size();
    }

    void setMeiosPagamento(List meiosPagamentos) {
        this.paymentCompanies = meiosPagamentos;
        notifyDataSetChanged();
    }


    public interface meiosPagamentoOnClickListener {
        void onClickMeioPagamento(Boolean isChecked, String id);
    }

}