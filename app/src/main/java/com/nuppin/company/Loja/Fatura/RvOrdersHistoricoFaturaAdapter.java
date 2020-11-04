package com.nuppin.company.Loja.Fatura;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.nuppin.company.R;
import com.nuppin.company.Util.Util;
import com.nuppin.company.model.Order;

import java.util.List;

public class RvOrdersHistoricoFaturaAdapter extends RecyclerView.Adapter<RvOrdersHistoricoFaturaAdapter.RvOrdersAdapterViewHolder>{

    private List<Order> ordList;
    private boolean anima = false;

    public class RvOrdersAdapterViewHolder extends RecyclerView.ViewHolder{

        final TextView  total, data, comissao, nome, meioPagamento;
        Context context;

         RvOrdersAdapterViewHolder(View view) {
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
    public RvOrdersAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.rv_invoice_request;
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

        holder.nome.setText(ordList.get(position).getUser_name());
        holder.meioPagamento.setText(ordList.get(position).getPayment_method());
        holder.comissao.setText(holder.context.getResources().getString(R.string.taxa, Util.formaterPrice(ordList.get(position).getInvoice_fee())));
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

