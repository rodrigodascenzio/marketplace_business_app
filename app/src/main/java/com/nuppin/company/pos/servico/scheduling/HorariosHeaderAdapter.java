package com.nuppin.company.pos.servico.scheduling;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.nuppin.company.R;
import com.nuppin.company.Util.Util;
import com.nuppin.company.model.Company;
import com.nuppin.company.model.Scheduling;
import com.nuppin.company.model.Service;
import com.nuppin.company.model.ServiceEmployee;

import java.util.List;

public class HorariosHeaderAdapter extends RecyclerView.Adapter<HorariosHeaderAdapter.AgendamentosAdapterViewHolder> {

    private List<ServiceEmployee> horarios;
    HorariosAdapterListener listener;
    private Service service;

    HorariosHeaderAdapter(HorariosAdapterListener listener){
        this.listener = listener;
    }

    public class AgendamentosAdapterViewHolder extends RecyclerView.ViewHolder
            implements HorariosBodyAdapter.Horarios2AdapterListener {

        TextView nome;
        Context ctx;
        View view;
        RecyclerView recyclerView;
        HorariosBodyAdapter adapter;
        SimpleDraweeView foto;
        TextView linearEmpty;

        AgendamentosAdapterViewHolder(View view){
            super(view);
            this.view = view;
            recyclerView = view.findViewById(R.id.recycler);
            ctx = view.getContext();
            nome = view.findViewById(R.id.nome);
            foto = view.findViewById(R.id.foto);
            linearEmpty = view.findViewById(R.id.linearEmpty);
        }

        @Override
        public void onClickHorario(Scheduling horario) {
            listener.onClickHorario(horario, service,null);
        }
    }

    @NonNull
    @Override
    public AgendamentosAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.rv_scheduling_header_manual;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);
        return new AgendamentosAdapterViewHolder(view);    }

    @Override
    public void onBindViewHolder(@NonNull AgendamentosAdapterViewHolder holder, int position) {
        holder.nome.setText(horarios.get(position).getUser_name());
        Util.hasPhoto("", holder.foto);
        LinearLayoutManager layoutManager = new LinearLayoutManager(holder.ctx, RecyclerView.HORIZONTAL,false                                                                                   );
        holder.recyclerView.setLayoutManager(layoutManager);
        holder.adapter = new HorariosBodyAdapter(holder);
        holder.recyclerView.setAdapter(holder.adapter);
        if (horarios.get(position).getScheduling().size() > 0) {
            holder.adapter.setHorarios(horarios.get(position).getScheduling());
            holder.recyclerView.setVisibility(View.VISIBLE);
            holder.linearEmpty.setVisibility(View.GONE);
        }else {
            holder.recyclerView.setVisibility(View.GONE);
            holder.linearEmpty.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        if (horarios == null) {
            return 0;
        } else {
            return horarios.size();
        }
    }

    void setHorarios(List<ServiceEmployee> horarios, Service service){
        this.horarios = horarios;
        this.service = service;
        notifyDataSetChanged();
    }

    public interface HorariosAdapterListener {
        void onClickHorario(Scheduling horario, Service service, Company company);
    }

}
