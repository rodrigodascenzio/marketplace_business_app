package com.nuppin.company.Loja.Cupom;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.nuppin.company.model.Coupon;
import com.nuppin.company.R;
import com.nuppin.company.Util.Util;

import java.util.List;

public class listaCuponsAdapter extends RecyclerView.Adapter<listaCuponsAdapter.ListaCuponsAdapterViewHoler> {

    private List<Coupon> cupons;
   // private diaSemanaOnClickListener listener;

    listaCuponsAdapter() {
    }

    //public StoreHorariosAdapter(diaSemanaOnClickListener handler) {
      //  listener = handler;
    //}

    public class ListaCuponsAdapterViewHoler extends RecyclerView.ViewHolder implements View.OnClickListener {

        final TextView tipoCliente, desconto, qtd, expira;

        ListaCuponsAdapterViewHoler(View view) {
            super(view);
            tipoCliente = view.findViewById(R.id.tipoCliente);
            desconto = view.findViewById(R.id.desconto);
            qtd = view.findViewById(R.id.qtd);
            expira = view.findViewById(R.id.data);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {}
    }

    @NonNull
    public ListaCuponsAdapterViewHoler onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        return new ListaCuponsAdapterViewHoler(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.rv_list_coupon, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ListaCuponsAdapterViewHoler holder, int position) {
        if (cupons.get(position).getExpires_day() < 1 && cupons.get(position).getExpires_hour() < 1 && cupons.get(position).getExpires_minute() < 1) {
            holder.expira.setText(holder.expira.getResources().getString(R.string.cupom_expirado));
        } else {
            if (cupons.get(position).getExpires_day() < 1) {
                if (cupons.get(position).getExpires_hour() < 1) {
                    holder.expira.setText("Expira em " + cupons.get(position).getExpires_minute() + " minutos");
                } else if(cupons.get(position).getExpires_hour() < 2) {
                    holder.expira.setText("Expira em " + cupons.get(position).getExpires_hour() + " hora e " + cupons.get(position).getExpires_minute() + " minutos");
                }else {
                    holder.expira.setText("Expira em " + cupons.get(position).getExpires_hour() + " horas e " + cupons.get(position).getExpires_minute() + " minutos");
                }
            } else if (cupons.get(position).getExpires_day() == 1) {
                holder.expira.setText("Expira amanhã");
            } else {
                holder.expira.setText("Expira em " + cupons.get(position).getExpires_day() + " dias");
            }
        }

        holder.tipoCliente.setText(tipoCampanha(cupons.get(position).getTarget()));

        if (cupons.get(position).getDiscount_type().equals("1")) {
            holder.desconto.setCompoundDrawablesWithIntrinsicBounds(holder.desconto.getResources().getDrawable(R.drawable.cash_usd_outline),null,null,null);
            holder.desconto.setText(Util.formaterPriceCupom(cupons.get(position).getValue()));
        } else {
            holder.desconto.setCompoundDrawablesWithIntrinsicBounds(holder.desconto.getResources().getDrawable(R.drawable.discount_percent),null,null,null);
            holder.desconto.setText(holder.expira.getContext().getResources().getString(R.string.cupom_porcento, String.valueOf((int) cupons.get(position).getValue())));
        }

        if (cupons.get(position).getQuantity_used() < 10){
            holder.qtd.setText("0" + cupons.get(position).getQuantity_used());
        }else {
            holder.qtd.setText(String.valueOf(cupons.get(position).getQuantity_used()));
        }
    }

    private String tipoCampanha(String tipo) {
        switch (tipo) {
            case "1":
                return "Todos";
            case "2":
                return "Novos";
            case "3":
                return "Clientes";
                default:
                    return "";
        }
    }

    @Override
    public int getItemCount() {
        if (cupons == null)
            return 0;
        return cupons.size();
    }

    void setCupons(List cupons) {
        this.cupons = cupons;
        notifyDataSetChanged();
    }


    //public interface diaSemanaOnClickListener {
      //  void onClickDiaSemana(Boolean isChecked, String id);
    //}

}