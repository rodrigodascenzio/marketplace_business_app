package com.nuppin.company.Loja.loja;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.nuppin.company.R;
import com.nuppin.company.Util.Util;
import com.nuppin.company.model.Order;

import java.util.List;

public class RvAvaliacoes extends RecyclerView.Adapter<RvAvaliacoes.RvOrdersAdapterViewHolder> {

    private List<Order> ordList;
    private boolean anima = false;

    public class RvOrdersAdapterViewHolder extends RecyclerView.ViewHolder{

        final TextView  ord_rating, ord_rating_obs, ord_id, ord_data;
        Context context;

         RvOrdersAdapterViewHolder(View view) {
             super(view);
             context = view.getContext();
             ord_data = view.findViewById(R.id.ord_data);
             ord_id = view.findViewById(R.id.ord_id);
             ord_rating = view.findViewById(R.id.ord_rating);
             ord_rating_obs = view.findViewById(R.id.ord_rating_obs);
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
        if (anima) {
            holder.itemView.startAnimation(AnimationUtils.loadAnimation(holder.context,R.anim.views_up));
            if (ordList.size() > 1) {

            } else {
                anima = false;
            }
        }

        holder.ord_data.setText(Util.timestampFormatDayMonth(ordList.get(position).getCreated_date()));
        holder.ord_id.setText(ordList.get(position).getOrder_id());
        holder.ord_rating.setText(Util.formaterRating(ordList.get(position).getRating()));
        if (ordList.get(position).getRating_note() != null && !ordList.get(position).getRating_note().equals("")) {
            holder.ord_rating_obs.setText(ordList.get(position).getRating_note());
        }else {
            holder.ord_rating_obs.setText(R.string.not_comment);
        }
    }

    @Override
    public int getItemCount() {
        if (null == ordList)
            return 0;
        return ordList.size();
    }

    public void setOrder(List<Order> order) {
        ordList = order;
        notifyDataSetChanged();
    }
}

