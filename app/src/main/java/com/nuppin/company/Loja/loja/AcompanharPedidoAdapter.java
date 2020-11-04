package com.nuppin.company.Loja.loja;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.nuppin.company.Util.AppConstants;
import com.nuppin.company.model.Status;
import com.nuppin.company.R;

import java.util.List;

public class AcompanharPedidoAdapter extends RecyclerView.Adapter<AcompanharPedidoAdapter.AcompanharPedidoAdapterViewHolder> {

    private List<Status> status;
    private AcompanharPedidoOnClickListener listener;

    public AcompanharPedidoAdapter(AcompanharPedidoOnClickListener handler) {
        listener = handler;
    }

    public class AcompanharPedidoAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        final TextView qtd, statusNome;

        AcompanharPedidoAdapterViewHolder(View view) {
            super(view);
            qtd = view.findViewById(R.id.qtdStatus);
            statusNome = view.findViewById(R.id.status);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            listener.onClickStatus(status.get(adapterPosition).getCode(), status.get(adapterPosition).getCompany_id());
        }
    }

    @NonNull
    public AcompanharPedidoAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.rv_status;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);
        return new AcompanharPedidoAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AcompanharPedidoAdapterViewHolder holder, int position) {
        holder.qtd.setText(status.get(position).getQuantity());
        switch (status.get(position).getCode()) {
            case AppConstants.STATUS_PENDING:
                if (status.get(position).getQuantity().equals("1")) {
                    holder.statusNome.setText(R.string.novo);
                }else {
                    holder.statusNome.setText(R.string.novos);
                }
                break;
            case AppConstants.STATUS_ACCEPTED:
                holder.statusNome.setText(R.string.em_andamento);
                break;
            case AppConstants.STATUS_DELIVERY:
                holder.statusNome.setText(R.string.entregando);
                break;
            case AppConstants.STATUS_RELEASED:
                holder.statusNome.setText(R.string.esperando_retirada);
                break;
            case AppConstants.STATUS_CONCLUDED_NOT_RATED:
                if (status.get(position).getQuantity().equals("1")) {
                    holder.statusNome.setText(R.string.concluido);
                }else {
                    holder.statusNome.setText(R.string.concluidos);
                }
                break;
        }
    }

    @Override
    public int getItemCount() {
        if (status == null)
            return 0;
        return status.size();
    }

    public void setStatusAcompanhar(List status) {
        this.status = status;
        notifyDataSetChanged();
    }


    public interface AcompanharPedidoOnClickListener {
        void onClickStatus(String index, String stoId);
    }

}