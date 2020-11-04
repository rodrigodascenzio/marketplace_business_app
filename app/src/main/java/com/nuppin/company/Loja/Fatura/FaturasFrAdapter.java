package com.nuppin.company.Loja.Fatura;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.nuppin.company.model.Invoice;
import com.nuppin.company.R;
import com.nuppin.company.Util.Util;

import java.util.List;

public class FaturasFrAdapter extends RecyclerView.Adapter<FaturasFrAdapter.FaturasAdapterViewHolder> {

    private List<Invoice> invoices;
    private FaturaOnClickListener listener;

    public FaturasFrAdapter() {
    }

    FaturasFrAdapter(FaturaOnClickListener handler) {
        listener = handler;
    }

    public class FaturasAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        final TextView mes, data, venc, valor, status;
        Context ctx;

        FaturasAdapterViewHolder(View view) {
            super(view);
            ctx = view.getContext();
            mes = view.findViewById(R.id.fat_data_cad);
            data = view.findViewById(R.id.fat_data);
            venc = view.findViewById(R.id.fat_vencimento);
            valor = view.findViewById(R.id.valor);
            status = view.findViewById(R.id.status);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            listener.onClickFatura(invoices.get(adapterPosition).getInvoice_id());
        }
    }

    @NonNull
    public FaturasAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.rv_invoice;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);
        return new FaturasAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FaturasAdapterViewHolder holder, int position) {
        holder.valor.setText(Util.formaterPrice(invoices.get(position).getTotal_amount()));
        holder.status.setText(Util.statusFatura(invoices.get(position).getStatus()));
        holder.mes.setText(Util.mesDaData(invoices.get(position).getCreated_date()));
        holder.data.setText(Util.timestampFormatDayMonthYear(invoices.get(position).getCreated_date()));
        holder.venc.setText(holder.ctx.getResources().getString(R.string.vencimento, Util.timestampFormatDayMonthYear(invoices.get(position).getDue_date())));
    }

    @Override
    public int getItemCount() {
        if (invoices == null)
            return 0;
        return invoices.size();
    }

    void setInvoices(List invoices) {
        this.invoices = invoices;
        notifyDataSetChanged();
    }


    public interface FaturaOnClickListener {
        void onClickFatura(String id);
    }

}