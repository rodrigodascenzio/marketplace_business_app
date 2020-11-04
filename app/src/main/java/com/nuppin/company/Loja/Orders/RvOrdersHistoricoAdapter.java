package com.nuppin.company.Loja.Orders;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.nuppin.company.R;
import com.nuppin.company.Util.AppConstants;
import com.nuppin.company.Util.Util;
import com.nuppin.company.model.Order;

import java.util.List;

public class RvOrdersHistoricoAdapter extends RecyclerView.Adapter<RvOrdersHistoricoAdapter.RvOrdersAdapterViewHolder>{

    private List<Order> ordList;
    private boolean anima = false;

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
        if (anima) {
            holder.itemView.startAnimation(AnimationUtils.loadAnimation(holder.context,R.anim.views_up));
            if (ordList.size() > 1) {

            } else {
                anima = false;
            }
        }

        switch (ordList.get(position).getStatus()) {
            case AppConstants.STATUS_PENDING:
                holder.order_id.setText(holder.context.getResources().getString(R.string.ord_id_historico, ordList.get(position).getOrder_id(), holder.context.getResources().getString(R.string.novo)));
                break;
            case AppConstants.STATUS_ACCEPTED:
                holder.order_id.setText(holder.context.getResources().getString(R.string.ord_id_historico, ordList.get(position).getOrder_id(), holder.context.getResources().getString(R.string.em_andamento)));
                break;
            case AppConstants.STATUS_DELIVERY:
                holder.order_id.setText(holder.context.getResources().getString(R.string.ord_id_historico, ordList.get(position).getOrder_id(), holder.context.getResources().getString(R.string.entregando)));
                break;
            case AppConstants.STATUS_RELEASED:
                holder.order_id.setText(holder.context.getResources().getString(R.string.ord_id_historico, ordList.get(position).getOrder_id(), holder.context.getResources().getString(R.string.esperando_retirada_historico)));
                break;
            case AppConstants.STATUS_CONCLUDED_NOT_RATED:
            case AppConstants.STATUS_CONCLUDED:
                holder.order_id.setText(holder.context.getResources().getString(R.string.ord_id_historico, ordList.get(position).getOrder_id(), holder.context.getResources().getString(R.string.concluido)));
                break;
            case AppConstants.STATUS_CANCELED_BY_ROBOT:
            case AppConstants.STATUS_CANCELED_REFUSED:
            case AppConstants.STATUS_CANCELED_BY_USER:
                holder.order_id.setText(holder.context.getResources().getString(R.string.ord_id_historico, ordList.get(position).getOrder_id(), holder.context.getResources().getString(R.string.cancelado)));
                break;
        }
        holder.nome.setText(ordList.get(position).getUser_name());
        holder.meioPagamento.setText(ordList.get(position).getPayment_method());
        holder.data.setText(Util.timestampFormatDayMonth(ordList.get(position).getCreated_date()));
        holder.total.setText(Util.formaterPrice(ordList.get(position).getTotal_amount()));
    }

    @Override
    public int getItemCount() {
        if (null == ordList)
            return 0;
        return ordList.size();
    }

    public void setOrder(List<Order> order) {
        ordList = order;
        notifyDataSetChanged();
    }
}

