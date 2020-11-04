package com.nuppin.company.Loja.loja.cadastro;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.nuppin.company.model.Plan;
import com.nuppin.company.R;
import com.nuppin.company.Util.Util;

import java.util.List;

public class PlanoAdapter extends RecyclerView.Adapter<PlanoAdapter.PlanosAdapterViewHolder> {

    private List<Plan> plans;
    private PlanoAdapterOnClickListener listener;

    PlanoAdapter(PlanoAdapterOnClickListener handler) {
        listener = handler;
    }

    public class PlanosAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public final TextView nome, desc, valor;

        PlanosAdapterViewHolder(View view) {
            super(view);
            nome = view.findViewById(R.id.nome);
            desc = view.findViewById(R.id.desc);
            valor = view.findViewById(R.id.valor);

            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            listener.onClickFromPlanos(plans.get(adapterPosition).getPlan_id());
        }
    }

    @NonNull
    public PlanosAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.rv_plan;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);
        return new PlanosAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlanosAdapterViewHolder holder, int position) {
        holder.nome.setText(plans.get(position).getName());
        holder.desc.setText(plans.get(position).getDescription());
        holder.valor.setText(holder.valor.getContext().getResources().getString(R.string.plano_mes, Util.formaterPrice(plans.get(position).getPrice())));
    }

    @Override
    public int getItemCount() {
        if (plans == null)
            return 0;
        return plans.size();
    }

    void setPlans(List plans) {
        this.plans = plans;
        notifyDataSetChanged();
    }


    public interface PlanoAdapterOnClickListener {
        void onClickFromPlanos(String planoId);
    }

}