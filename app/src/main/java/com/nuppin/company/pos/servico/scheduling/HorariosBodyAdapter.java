package com.nuppin.company.pos.servico.scheduling;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.nuppin.company.R;
import com.nuppin.company.model.Scheduling;

import java.util.List;

public class HorariosBodyAdapter extends RecyclerView.Adapter<HorariosBodyAdapter.AgendamentosAdapterViewHolder> {

    private List<Scheduling> horarios;
    private Horarios2AdapterListener listener;

    HorariosBodyAdapter(Horarios2AdapterListener listener){
        this.listener = listener;
    }

    public class AgendamentosAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView horario;
        Context ctx;
        View view;

        AgendamentosAdapterViewHolder(View view){
            super(view);
            this.view = view;
            ctx = view.getContext();
            horario = view.findViewById(R.id.horario);
            horario.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            listener.onClickHorario(horarios.get(getAdapterPosition()));
        }
    }

    @NonNull
    @Override
    public AgendamentosAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.rv_scheduling_manual;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);
        return new AgendamentosAdapterViewHolder(view);    }

    @Override
    public void onBindViewHolder(@NonNull AgendamentosAdapterViewHolder holder, int position) {
        holder.horario.setText(horarios.get(position).getStart_time());
    }

    @Override
    public int getItemCount() {
        if (horarios == null) {
            return 0;
        } else {
            return horarios.size();
        }
    }

    void setHorarios(List<Scheduling> horarios){
        this.horarios = horarios;
        notifyDataSetChanged();
    }

    public interface Horarios2AdapterListener {
        void onClickHorario(Scheduling horario);
    }

}
