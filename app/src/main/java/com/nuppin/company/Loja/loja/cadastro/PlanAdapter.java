package com.nuppin.company.Loja.loja.cadastro;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.nuppin.company.R;
import com.nuppin.company.model.Plan;

import java.util.List;

public class PlanAdapter extends RecyclerView.Adapter<PlanAdapter.PlanAdapterViewHolder> {

    private List<Plan> plan;

    PlanAdapter() {}

    class PlanAdapterViewHolder extends RecyclerView.ViewHolder{

        TextView description;

        PlanAdapterViewHolder(View view) {
            super(view);
            description = view.findViewById(R.id.description);
        }
    }

    @NonNull
    @Override
    public PlanAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.rv_plan_benefit;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;
        View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);
        return new PlanAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlanAdapterViewHolder holder, int position) {
        holder.description.setText(plan.get(position).getDescription());
    }

    @Override
    public int getItemCount() {
        if (plan == null) {
            return 0;
        } else {
            return plan.size();
        }
    }

    void setBenefits(List<Plan> plan) {
        this.plan = plan;
        notifyDataSetChanged();
    }
}
