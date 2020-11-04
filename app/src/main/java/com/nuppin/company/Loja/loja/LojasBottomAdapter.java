package com.nuppin.company.Loja.loja;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.nuppin.company.R;
import com.nuppin.company.Util.UtilShaPre;

public class LojasBottomAdapter extends RecyclerView.Adapter<LojasBottomAdapter.RvLojaAdapterViewHolder> {

    private String[] bottom;
    private LojasBottomOnClickListener listener;

    public LojasBottomAdapter(LojasBottomOnClickListener handler) {
        listener = handler;
    }

    public class RvLojaAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final TextView icon, nome;
        private CardView cardBottom;
        private Context ctx;

        private RvLojaAdapterViewHolder(View view) {
            super(view);
            ctx = view.getContext();
            nome = view.findViewById(R.id.nome);
            icon = view.findViewById(R.id.icon);
            cardBottom = view.findViewById(R.id.cardBottom);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            listener.onClick(bottom[adapterPosition]);
        }
    }

    @NonNull
    public RvLojaAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.bottom_company;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);
        return new RvLojaAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RvLojaAdapterViewHolder holder, int position) {
        holder.icon.setVisibility(View.VISIBLE);
        holder.nome.setVisibility(View.VISIBLE);
        holder.nome.setText(bottom[position]);
        holder.icon.setCompoundDrawablesWithIntrinsicBounds(setaIcon(bottom[position], holder.ctx),null,null,null);

        if (position == 0) {
            ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) holder.cardBottom.getLayoutParams();
            layoutParams.setMargins(holder.ctx.getResources().getDimensionPixelOffset(R.dimen.bottom_first_item_margin), 0, holder.ctx.getResources().getDimensionPixelOffset(R.dimen.bottom_first_item_right_margin), 0);
        }else if (position == bottom.length - 1) {
            ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) holder.cardBottom.getLayoutParams();
            layoutParams.setMargins(0, 0, holder.ctx.getResources().getDimensionPixelOffset(R.dimen.bottom_last_item_margin), 0);
        }else{
            ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) holder.cardBottom.getLayoutParams();
            layoutParams.setMargins(0, 0, holder.ctx.getResources().getDimensionPixelOffset(R.dimen.bottom_first_item_right_margin), 0);
        }
    }

    private Drawable setaIcon(String nomeIcon, Context ctx){
        switch (nomeIcon) {
            case "Produtos":
                return ctx.getResources().getDrawable(R.drawable.card_bulleted_outline);
            case "Serviços":
                return ctx.getResources().getDrawable(R.drawable.card_bulleted_outline);
            case "Faturas":
                return ctx.getResources().getDrawable(R.drawable.receipt);
            case "Relatório":
                return ctx.getResources().getDrawable(R.drawable.google_analytics);
            case "Cupom":
                return ctx.getResources().getDrawable(R.drawable.ticket_outline);
            case "Financeiro":
                return ctx.getResources().getDrawable(R.drawable.finance);
            case "Equipe":
                return ctx.getResources().getDrawable(R.drawable.account_group_outline);
            case "Área de estudos":
                return ctx.getResources().getDrawable(R.drawable.school_outline);
        }
        return ctx.getResources().getDrawable(R.drawable.information_outline);
    }

    @Override
    public int getItemCount() {
        if (null == bottom)
            return 0;
        return bottom.length;
    }

    public void setBottom(String[] bottom) {
        this.bottom = bottom;
        notifyDataSetChanged();
    }


    public interface LojasBottomOnClickListener {
        void onClick(String index);
    }

}

