package com.nuppin.company.pos.produto.list;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.nuppin.company.R;
import com.nuppin.company.Util.Util;
import com.nuppin.company.model.CollectionExtra;
import com.nuppin.company.model.Extra;

import java.util.List;

public class ConjuctExtrasRadioAdapterManual extends RecyclerView.Adapter<ConjuctExtrasRadioAdapterManual.ConjuctExtrasRadioAdapterViewHolder> {

    private List<Extra> extras;
    private CollectionExtra collectionExtra;
    private ConjuctBodyExtrasAdapterListener listener;
    private View lastView;

    ConjuctExtrasRadioAdapterManual(ConjuctBodyExtrasAdapterListener listener) {
        this.listener = listener;
    }

    public class ConjuctExtrasRadioAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView eiName, eiValue, eiDrescription;
        Context ctx;
        View view;
        ConstraintLayout constraint;
        RadioButton radio;

        ConjuctExtrasRadioAdapterViewHolder(View view) {
            super(view);
            this.view = view;
            ctx = view.getContext();
            eiName = view.findViewById(R.id.nome);
            eiValue = view.findViewById(R.id.preco);
            eiDrescription = view.findViewById(R.id.description);
            constraint = view.findViewById(R.id.constraint);
            radio = view.findViewById(R.id.radio);
            constraint.setOnClickListener(this);
            radio.setClickable(false);
        }

        @Override
        public void onClick(View view) {
            if (lastView != null) {
                RadioButton checked_rb = lastView.findViewById(R.id.radio);
                checked_rb.setChecked(false);
                radio.setChecked(true);
            } else {
                radio.setChecked(true);
            }
            lastView = view;
            for (Extra radio : extras) {
                radio.setQuantity(0);
            }
            collectionExtra.setQuantity_selected(1);
            extras.get(getAdapterPosition()).setQuantity(1);
            collectionExtra.setTotal_price(extras.get(getAdapterPosition()).getPrice());
            listener.onClickCheck(collectionExtra);
        }
    }

    @NonNull
    @Override
    public ConjuctExtrasRadioAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.manual_rv_conjuct_extra_radio;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);
        return new ConjuctExtrasRadioAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ConjuctExtrasRadioAdapterViewHolder holder, int position) {
        holder.eiDrescription.setVisibility(View.GONE);
        holder.eiName.setText(extras.get(position).getName());
        holder.eiValue.setText(Util.formaterPrice(extras.get(position).getPrice()));

        if (extras.get(position).getDescription() != null && !extras.get(position).getDescription().equals("")) {
            holder.eiDrescription.setVisibility(View.VISIBLE);
            holder.eiDrescription.setText(extras.get(position).getDescription());
        }

        if (extras.get(position).getQuantity() > 0) {
            lastView = holder.view;
            holder.radio.setChecked(true);
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
        void onClickCheck(CollectionExtra collectionExtra);
    }

}
