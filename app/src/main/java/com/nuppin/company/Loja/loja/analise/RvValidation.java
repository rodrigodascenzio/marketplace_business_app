package com.nuppin.company.Loja.loja.analise;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.nuppin.company.R;
import com.nuppin.company.model.ValidationMessage;

import java.util.List;

public class RvValidation extends RecyclerView.Adapter<RvValidation.RvValidationAdapterViewHolder> {

    private List<ValidationMessage> validation;

    public RvValidation() {
    }


    public class RvValidationAdapterViewHolder extends RecyclerView.ViewHolder {

        final TextView message;
        TextView status;
        Context ctx;

        RvValidationAdapterViewHolder(View view) {
            super(view);
            ctx = view.getContext();
            message = view.findViewById(R.id.message);
            status = view.findViewById(R.id.action);
        }
    }

    @NonNull
    public RvValidationAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.rv_validation;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);
        return new RvValidationAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RvValidationAdapterViewHolder holder, int position) {
        holder.message.setText(validation.get(position).getMessage());
        if (validation.get(position).getStatus().equals("like")) {
            holder.status.setCompoundDrawablesWithIntrinsicBounds(null, null, holder.ctx.getResources().getDrawable(R.drawable.ic_thumb_up_24), null);
        }else if(validation.get(position).getStatus().equals("dislike")){
            holder.status.setCompoundDrawablesWithIntrinsicBounds(null, null, holder.ctx.getResources().getDrawable(R.drawable.ic_thumb_down_24), null);
        }
    }

    @Override
    public int getItemCount() {
        if (null == validation)
            return 0;
        return validation.size();
    }

    public void setValidation(List<ValidationMessage> validation) {
        this.validation = validation;
        notifyDataSetChanged();
    }
}


