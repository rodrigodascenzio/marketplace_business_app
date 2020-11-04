package com.nuppin.company.Loja.loja;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.nuppin.company.R;
import com.nuppin.company.Util.Util;
import com.nuppin.company.model.Scheduling;

import java.util.List;

public class RvAvaliacoesAge extends RecyclerView.Adapter<RvAvaliacoesAge.RvOrdersAdapterViewHolder> {

    private List<Scheduling> ordList;

    public class RvOrdersAdapterViewHolder extends RecyclerView.ViewHolder{

        final TextView  age_rating, age_rating_obs, age_id, age_data;
        Context context;

         RvOrdersAdapterViewHolder(View view) {
             super(view);
             context = view.getContext();
             age_data = view.findViewById(R.id.ord_data);
             age_id = view.findViewById(R.id.ord_id);
             age_rating = view.findViewById(R.id.ord_rating);
             age_rating_obs = view.findViewById(R.id.ord_rating_obs);
        }
    }

    @NonNull
    public RvOrdersAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.rv_rating;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);
        return new RvOrdersAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RvOrdersAdapterViewHolder holder, int position) {
        holder.age_data.setText(Util.timestampFormatDayMonth(ordList.get(position).getStart_time()));
        holder.age_id.setText(ordList.get(position).getScheduling_id());
        holder.age_rating.setText(Util.formaterRating(ordList.get(position).getRating()));
        if (ordList.get(position).getRating_note() != null && !ordList.get(position).getRating_note().equals("")) {
            holder.age_rating_obs.setText(ordList.get(position).getRating_note());
        }else {
            holder.age_rating_obs.setText(R.string.not_comment);
        }
    }

    @Override
    public int getItemCount() {
        if (null == ordList)
            return 0;
        return ordList.size();
    }

    public void setAgendamentos(List<Scheduling> agendamentos) {
        ordList = agendamentos;
        notifyDataSetChanged();
    }
}

