package com.nuppin.company.pos.servico;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.nuppin.company.R;
import com.nuppin.company.Util.Util;
import com.nuppin.company.model.Company;
import com.nuppin.company.model.Service;

import java.util.List;

public class RvServicosAdapterManual extends RecyclerView.Adapter<RvServicosAdapterManual.RvServicosAdapterViewHolder> {

    private List<Service> service;
    private RvServicosOnClickListener listener;

    RvServicosAdapterManual(RvServicosOnClickListener handler) {
        listener = handler;
    }

    public class RvServicosAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        final TextView productName, preco, desc, duracao;
        final Button imagem;

        RvServicosAdapterViewHolder(View view) {
            super(view);
            productName = view.findViewById(R.id.nome);
            preco = view.findViewById(R.id.preco);
            imagem = view.findViewById(R.id.btn);
            desc = view.findViewById(R.id.desc);
            duracao = view.findViewById(R.id.duracao);
            imagem.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            listener.onClick(service.get(adapterPosition),null);
        }
    }

    @NonNull
    public RvServicosAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.rv_list_service_manual;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);
        return new RvServicosAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RvServicosAdapterViewHolder holder, int position) {
        holder.productName.setText(service.get(position).getName());
        holder.preco.setText(Util.formaterPrice(service.get(position).getPreco()));
        holder.desc.setText(service.get(position).getDescription());
        holder.duracao.setText(holder.duracao.getContext().getResources().getString(R.string.servico_duracao, service.get(position).getDuration()));
    }

    @Override
    public int getItemCount() {
        if (null == service)
            return 0;
        return service.size();
    }

    public void setServicos(List<Service> service) {
        this.service = service;
        notifyDataSetChanged();
    }

    public interface RvServicosOnClickListener {
        void onClick(Service service, Company company);
    }

}


