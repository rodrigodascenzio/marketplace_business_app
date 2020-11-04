package com.nuppin.company.Loja.Config.horarios;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.nuppin.company.model.OpeningHours;
import com.nuppin.company.R;
import com.nuppin.company.Util.Util;

import java.util.ArrayList;
import java.util.List;

public class CriarEditarHorarioStoreRv extends RecyclerView.Adapter<CriarEditarHorarioStoreRv.CriarEditarHorarioStoreRvAdapterViewHolder> {

    private List<OpeningHours> dias;
    private diaSemanaOnClickListener listener;
    private ArrayList<OpeningHours> diasSelected;

    CriarEditarHorarioStoreRv(diaSemanaOnClickListener handler) {
        listener = handler;
        diasSelected = new ArrayList<>();
    }

    public class CriarEditarHorarioStoreRvAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        final CheckBox dia;

        CriarEditarHorarioStoreRvAdapterViewHolder(View view) {
            super(view);
            dia = view.findViewById(R.id.checkBoxDias);
            dia.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();

            OpeningHours diaSelected = dias.get(adapterPosition);

            if (diasSelected.contains(diaSelected)) {
                diasSelected.remove(diaSelected);
            } else {
                diasSelected.add(diaSelected);
            }

            listener.onClickDiaSemana(diasSelected);
        }
    }

    @NonNull
    public CriarEditarHorarioStoreRvAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.rv_add_update_company_schedule;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);
        return new CriarEditarHorarioStoreRvAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CriarEditarHorarioStoreRvAdapterViewHolder holder, int position) {
        holder.dia.setText(Util.nomeDiaSemana(dias.get(position).getWeekday_id()));
        if (diasSelected.contains(dias.get(position))) {
            holder.dia.setChecked(true);
        } else {
            holder.dia.setChecked(false);
        }
    }



    @Override
    public int getItemCount() {
        if (dias == null)
            return 0;
        return dias.size();
    }

    void setDiasSemana(List diasSemana) {
        this.dias = diasSemana;
        notifyDataSetChanged();
    }

    void setInicio(String inicio, int inicioInt) {
        notifyDataSetChanged();
    }

    void setTermino(String termino, int terminoInt) {
        notifyDataSetChanged();
    }

    public interface diaSemanaOnClickListener {
        void onClickDiaSemana(List<OpeningHours> openingHours);
    }

}