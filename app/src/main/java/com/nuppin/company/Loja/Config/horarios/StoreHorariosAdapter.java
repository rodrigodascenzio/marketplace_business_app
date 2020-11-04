package com.nuppin.company.Loja.Config.horarios;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.nuppin.company.model.Company;
import com.nuppin.company.model.OpeningHours;
import com.nuppin.company.R;
import com.nuppin.company.Util.Util;

import java.util.List;

public class StoreHorariosAdapter extends RecyclerView.Adapter<StoreHorariosAdapter.StoreHorariosAdapterViewHolder> {

    private List<OpeningHours> horarios;
    private diaSemanaOnClickListener listener;

    StoreHorariosAdapter(diaSemanaOnClickListener handler) {
        listener = handler;
    }

    public class StoreHorariosAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        final TextView dia, horario;

        StoreHorariosAdapterViewHolder(View view) {
            super(view);
            dia = view.findViewById(R.id.diaSemana);
            horario = view.findViewById(R.id.horario);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            listener.onClickDiaSemana(horarios.get(adapterPosition), null);
        }
    }

    @NonNull
    public StoreHorariosAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.rv_company_schedule;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);
        return new StoreHorariosAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StoreHorariosAdapterViewHolder holder, int position) {
        holder.dia.setText(Util.nomeDiaSemana(horarios.get(position).getWeekday_id()));
        holder.horario.setText(holder.horario.getContext().getResources().getString(R.string.employee_time_with_dot, horarios.get(position).getStart_time(), horarios.get(position).getEnd_time()));
    }



    @Override
    public int getItemCount() {
        if (horarios == null)
            return 0;
        return horarios.size();
    }

    void setHorarios(List horarios) {
        this.horarios = horarios;
        notifyDataSetChanged();
    }


    public interface diaSemanaOnClickListener {
        void onClickDiaSemana(OpeningHours openingHours, Company company);
    }

}