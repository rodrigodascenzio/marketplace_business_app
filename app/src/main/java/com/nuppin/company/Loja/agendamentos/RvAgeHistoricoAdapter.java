package com.nuppin.company.Loja.agendamentos;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.nuppin.company.R;
import com.nuppin.company.Util.AppConstants;
import com.nuppin.company.Util.Util;
import com.nuppin.company.model.Scheduling;

import java.util.List;

public class RvAgeHistoricoAdapter extends RecyclerView.Adapter<RvAgeHistoricoAdapter.RvOrdersAdapterViewHolder>{

    private List<Scheduling> ordList;

    public class RvOrdersAdapterViewHolder extends RecyclerView.ViewHolder{

        final TextView  total, data, nome, meioPagamento, order_id;
        Context context;

         RvOrdersAdapterViewHolder(View view) {
             super(view);
             context = view.getContext();
             order_id = view.findViewById(R.id.order_id);
             total = view.findViewById(R.id.valor);
             data = view.findViewById(R.id.data);
             nome = view.findViewById(R.id.nome);
             meioPagamento = view.findViewById(R.id.meioPagamento);
        }
    }

    @NonNull
    public RvOrdersAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.rv_request_history;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);
        return new RvOrdersAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RvOrdersAdapterViewHolder holder, int position) {
        switch (ordList.get(position).getStatus()) {
            case AppConstants.STATUS_PENDING:
                holder.order_id.setText(holder.context.getResources().getString(R.string.ord_id_historico, ordList.get(position).getScheduling_id(), holder.context.getResources().getString(R.string.novo)));
                break;
            case AppConstants.STATUS_ACCEPTED:
                holder.order_id.setText(holder.context.getResources().getString(R.string.ord_id_historico, ordList.get(position).getScheduling_id(), holder.context.getResources().getString(R.string.agendado)));
                break;
            case AppConstants.STATUS_CONCLUDED_NOT_RATED:
            case AppConstants.STATUS_CONCLUDED:
                holder.order_id.setText(holder.context.getResources().getString(R.string.ord_id_historico, ordList.get(position).getScheduling_id(), holder.context.getResources().getString(R.string.concluido)));
                break;
            case AppConstants.STATUS_CANCELED_BY_ROBOT:
            case AppConstants.STATUS_CANCELED_REFUSED:
            case AppConstants.STATUS_CANCELED_BY_USER:
            case AppConstants.STATUS_CANCELED_BY_COMPANY:
                holder.order_id.setText(holder.context.getResources().getString(R.string.ord_id_historico, ordList.get(position).getScheduling_id(), holder.context.getResources().getString(R.string.cancelado)));
                break;
            case AppConstants.STATUS_NOSHOW:
                holder.order_id.setText(holder.context.getResources().getString(R.string.ord_id_historico, ordList.get(position).getScheduling_id(), holder.context.getResources().getString(R.string.nao_compareceu)));
                break;
        }
        holder.nome.setText(ordList.get(position).getUser_name());
        holder.meioPagamento.setText(ordList.get(position).getPayment_method());
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

