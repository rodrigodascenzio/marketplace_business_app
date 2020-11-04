package com.nuppin.company.Loja.produto;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.nuppin.company.R;
import com.nuppin.company.Util.Util;
import com.nuppin.company.model.CollectionExtra;
import com.nuppin.company.model.Extra;

import java.util.List;


public class ConjuctExtrasAdapter extends RecyclerView.Adapter<ConjuctExtrasAdapter.ConjuctExtrasAdapterViewHolder> {

    private List<Extra> extras;
    private CollectionExtra collectionExtra;
    private ConjuctBodyExtrasAdapterListener listener;

    ConjuctExtrasAdapter(ConjuctBodyExtrasAdapterListener listener) {
        this.listener = listener;
    }

    public class ConjuctExtrasAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView eiName, eiValue, eiDrescription;
        Context ctx;
        View view;

        ConjuctExtrasAdapterViewHolder(View view) {
            super(view);
            this.view = view;
            ctx = view.getContext();
            eiName = view.findViewById(R.id.nome);
            eiValue = view.findViewById(R.id.preco);
            eiDrescription = view.findViewById(R.id.description);
        }

        @Override
        public void onClick(View view) {
            //listener.onClickHorario(horarios.get(getAdapterPosition()));
        }
    }

    @NonNull
    @Override
    public ConjuctExtrasAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.rv_conjuct_extra;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);
        return new ConjuctExtrasAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ConjuctExtrasAdapterViewHolder holder, int position) {
        holder.eiDrescription.setVisibility(View.GONE);

        holder.eiName.setText(extras.get(position).getName());
        holder.eiValue.setText(Util.formaterPrice(extras.get(position).getPrice()));
        if (extras.get(position).getDescription() != null && !extras.get(position).getDescription().equals("")) {
            holder.eiDrescription.setVisibility(View.VISIBLE);
            holder.eiDrescription.setText(extras.get(position).getDescription());
        }
    }

    @Override
    public int getItemCount() {
        if (extras == null) {
            return 0;
        } else {
            return extras.size();
        }
    }

    void setExtras(List<Extra> extras, CollectionExtra collectionExtra) {
        this.extras = extras;
        this.collectionExtra = collectionExtra;
        notifyDataSetChanged();
    }

    public interface ConjuctBodyExtrasAdapterListener {
        //void onClickHorario(Scheduling horario);
    }

}
