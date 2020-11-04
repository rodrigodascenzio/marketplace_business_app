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

public class AcompanharAgendamentoAdapter extends RecyclerView.Adapter<AcompanharAgendamentoAdapter.AcompanharAgendamentoAdapterViewHolder> {

    private List<Status> status;
    private AcompanharAgendamentoOnClickListener listener;

    public AcompanharAgendamentoAdapter(AcompanharAgendamentoOnClickListener handler) {
        listener = handler;
    }

    public class AcompanharAgendamentoAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        final TextView qtd, statusNome;

        AcompanharAgendamentoAdapterViewHolder(View view) {
            super(view);
            qtd = view.findViewById(R.id.qtdStatus);
            statusNome = view.findViewById(R.id.status);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            listener.onClickStatusAgendamento(status.get(adapterPosition).getCode(), status.get(adapterPosition).getCompany_id());
        }
    }

    @NonNull
    public AcompanharAgendamentoAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.rv_status_scheduling;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);
        return new AcompanharAgendamentoAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AcompanharAgendamentoAdapterViewHolder holder, int position) {
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
                if (status.get(position).getQuantity().equals("1")) {
                    holder.statusNome.setText(R.string.agendado);
                }else {
                    holder.statusNome.setText(R.string.agendados);
                }
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


    public interface AcompanharAgendamentoOnClickListener {
        void onClickStatusAgendamento(String index, String stoId);
    }

}