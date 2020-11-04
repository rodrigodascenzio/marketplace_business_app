package com.nuppin.company.Loja.Fatura;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.nuppin.company.R;
import com.nuppin.company.Util.Util;
import com.nuppin.company.model.Scheduling;

import java.util.List;

public class RvAgendamentosHistoricoFaturaAdapter extends RecyclerView.Adapter<RvAgendamentosHistoricoFaturaAdapter.RvAgendamentoAdapterViewHolder>{

    private List<Scheduling> ordList;

    public class RvAgendamentoAdapterViewHolder extends RecyclerView.ViewHolder{

        final TextView  total, data, comissao, nome, meioPagamento;
        Context context;

         RvAgendamentoAdapterViewHolder(View view) {
             super(view);
             context = view.getContext();
             comissao = view.findViewById(R.id.comissao);
             total = view.findViewById(R.id.valor);
             data = view.findViewById(R.id.data);
             nome = view.findViewById(R.id.nome);
             meioPagamento = view.findViewById(R.id.meioPagamento);
        }
    }

    @NonNull
    public RvAgendamentoAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.rv_invoice_request;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);
        return new RvAgendamentoAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RvAgendamentoAdapterViewHolder holder, int position) {
        holder.nome.setText(ordList.get(position).getUser_name());
        holder.meioPagamento.setText(ordList.get(position).getPayment_method());
        holder.comissao.setText(holder.context.getResources().getString(R.string.taxa, Util.formaterPrice(ordList.get(position).getTotal_amount() * 0.0499)));
        holder.data.setText(Util.timestampFormatDayMonth(ordList.get(position).getStart_time()));
        holder.total.setText(Util.formaterPrice(ordList.get(position).getTotal_amount()));
    }

    @Override
    public int getItemCount() {
        if (null == ordList)
            return 0;
        return ordList.size();
    }

    public void setAgendamentos(List<Scheduling> agendamentos) {
        ordList = agendamentos;
        notifyDataSetChanged();
    }
}

