package com.nuppin.company.Loja.agendamentos;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.nuppin.company.Util.AppConstants;
import com.nuppin.company.model.Scheduling;
import com.nuppin.company.R;
import com.nuppin.company.Util.Util;

import java.util.List;

public class RvAgeAdapter extends RecyclerView.Adapter<RvAgeAdapter.RvAgeAdapterViewHolder>{

    private RvAgeAdapterOnClickListener listener;
    private List<Scheduling> ageList;
    private boolean anima = false;
    private int position;

    public RvAgeAdapter(RvAgeAdapterOnClickListener handler) {
        listener = handler;
    }

    public class RvAgeAdapterViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener{

        Context context;
        RatingBar ratingUser;
        MaterialButton aceitar,chat;
        ImageButton recusar;
        TextView subTotal, total, rating, desconto, descontoTxt, meioPagamento;
        private TextView dataAge, horaInicio, profissional, preco, servico, obs, nomeUser,ageUserRating;

        RvAgeAdapterViewHolder(View view) {
             super(view);
            chat = view.findViewById(R.id.chat);
            ratingUser = view.findViewById(R.id.ratingUser);
            context = view.getContext();
            nomeUser = view.findViewById(R.id.ageUser);
            ageUserRating = view.findViewById(R.id.ageUserRating);
            dataAge = view.findViewById(R.id.dataAge);
            profissional = view.findViewById(R.id.ageFuncionario);
            servico = view.findViewById(R.id.servico);
            preco = view.findViewById(R.id.preco);
            obs = view.findViewById(R.id.obs);
            horaInicio = view.findViewById(R.id.ageHoraInicio);
            meioPagamento = view.findViewById(R.id.meioPagamento);
            subTotal = view.findViewById(R.id.resultSubtotal);
            desconto = view.findViewById(R.id.resulDesconto);
            descontoTxt = view.findViewById(R.id.desconto);
            total = view.findViewById(R.id.resultTotal);
            rating = view.findViewById(R.id.rating);
            aceitar = view.findViewById(R.id.btnAceitar);
            recusar = view.findViewById(R.id.btnRecusar);
            aceitar.setOnClickListener(this);
            recusar.setOnClickListener(this);
            chat.setOnClickListener(this);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            position = getAdapterPosition();
            anima = true;
            switch (v.getId()) {
                case R.id.btnAceitar:
                    switch (ageList.get(position).getStatus()) {
                        case AppConstants.STATUS_PENDING:
                            listener.onClickBtn(ageList.get(position), AppConstants.STATUS_ACCEPTED);
                            break;
                        case AppConstants.STATUS_ACCEPTED:
                            listener.onClickBtn(ageList.get(position), AppConstants.STATUS_CONCLUDED_NOT_RATED);
                            break;
                        case AppConstants.STATUS_CONCLUDED_NOT_RATED:
                            listener.onClickBtnRating(ageList.get(position), AppConstants.STATUS_CONCLUDED, ratingUser.getProgress());
                            if (ratingUser.getProgress() != 0) {
                                ratingUser.setProgress(0);
                            }
                            break;
                    }
                    break;
                case R.id.btnRecusar:
                    switch (ageList.get(position).getStatus()) {
                        case AppConstants.STATUS_PENDING:
                            listener.onClickBtn(ageList.get(position),AppConstants.STATUS_CANCELED_REFUSED);
                            break;
                        case AppConstants.STATUS_ACCEPTED:
                            listener.onClickBtn(ageList.get(position),AppConstants.STATUS_NOSHOW);
                            break;
                    }
                    break;
                case R.id.chat:
                    listener.chat(ageList.get(position));
                    break;
            }
        }
    }

    @NonNull
    public RvAgeAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.fr_scheduling_detail;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);
        return new RvAgeAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RvAgeAdapterViewHolder holder, int position) {
        holder.recusar.setVisibility(View.GONE);
        holder.aceitar.setVisibility(View.GONE);
        holder.ratingUser.setVisibility(View.GONE);
        holder.desconto.setVisibility(View.GONE);
        holder.descontoTxt.setVisibility(View.GONE);
        holder.obs.setVisibility(View.GONE);
        holder.chat.setVisibility(View.GONE);

        if (anima) {
            holder.itemView.startAnimation(AnimationUtils.loadAnimation(holder.context,R.anim.views_up));
            if (ageList.size() > 1) {
                if (position == (this.position + 1) || position == (this.position - 2)) {
                    anima = false;
                }
            } else {
                anima = false;
            }
        }

        if (ageList.get(position).getIs_chat_available() == 1) {
            holder.chat.setVisibility(View.VISIBLE);
        }

        switch (ageList.get(position).getStatus()) {
            case AppConstants.STATUS_PENDING:
                holder.recusar.setVisibility(View.VISIBLE);
                holder.aceitar.setVisibility(View.VISIBLE);
                holder.aceitar.setText(R.string.aceitar);
                break;
            case AppConstants.STATUS_ACCEPTED:
                if (ageList.get(position).getExpired()) {
                    holder.recusar.setVisibility(View.VISIBLE);
                    holder.recusar.setImageResource(R.drawable.ic_thumb_down_black_24dp);
                    holder.aceitar.setVisibility(View.VISIBLE);
                    holder.aceitar.setText(R.string.concluido);
                }
                break;
            case AppConstants.STATUS_CONCLUDED_NOT_RATED:
                holder.ratingUser.setVisibility(View.VISIBLE);
                holder.aceitar.setVisibility(View.VISIBLE);
                holder.aceitar.setText(R.string.avaliar);
                break;
        }

        holder.nomeUser.setText(ageList.get(position).getUser_name());

        if (!ageList.get(position).getSource().equals("nuppin_company")) {
            if (ageList.get(position).getUser_num_rating() == 0) {
                holder.ageUserRating.setText(holder.ageUserRating.getContext().getResources().getString(R.string.user_no_rated));
                holder.ageUserRating.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            } else {
                holder.ageUserRating.setText(holder.ageUserRating.getContext().getResources().getString(R.string.order_user_nome_rating, Util.formaterRating(ageList.get(position).getUser_rating()), ageList.get(position).getUser_num_rating()));
                holder.ageUserRating.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_star_yellow_12dp, 0, 0, 0);
            }
        }else {
            holder.ageUserRating.setText(holder.ageUserRating.getContext().getResources().getString(R.string.manual));
            holder.ageUserRating.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        }

        holder.meioPagamento.setText(holder.meioPagamento.getContext().getResources().getString(R.string.ord_id, ageList.get(position).getScheduling_id().toUpperCase(),ageList.get(position).getPayment_method()));


        holder.preco.setText(Util.formaterPrice(ageList.get(position).getSubtotal_amount()));
        holder.profissional.setText(ageList.get(position).getEmployee_name());
        holder.servico.setText(ageList.get(position).getService_name());
        holder.horaInicio.setText(holder.horaInicio.getContext().getResources().getString(R.string.employee_time_with_dot, ageList.get(position).getStart_time(), ageList.get(position).getEnd_time()));
        holder.dataAge.setText(Util.timestampFormatDayMonthYear(ageList.get(position).getStart_date()));

        if (ageList.get(position).getNote() != null && !ageList.get(position).getNote().equals("")) {
            holder.obs.setVisibility(View.VISIBLE);
            holder.obs.setText(holder.obs.getContext().getResources().getString(R.string.observacao, ageList.get(position).getNote()));
        }

        if (ageList.get(position).getDiscount_amount() > 0) {
            holder.desconto.setVisibility(View.VISIBLE);
            holder.descontoTxt.setVisibility(View.VISIBLE);
        }

        holder.subTotal.setText(Util.formaterPrice(ageList.get(position).getSubtotal_amount()));
        holder.desconto.setText(Util.formaterPrice(ageList.get(position).getDiscount_amount()));
        holder.total.setText(Util.formaterPrice(ageList.get(position).getTotal_amount()));
    }

    @Override
    public int getItemCount() {
        if (null == ageList)
            return 0;
        return ageList.size();
    }

    public void setAge(List<Scheduling> age) {
        ageList = age;
        notifyDataSetChanged();
    }


    public interface RvAgeAdapterOnClickListener {
        void onClick(String ageId);
        void onClickBtn(Scheduling age, String atualizarParaStatus);
        void onClickBtnRating(Scheduling age, String atualizarParaStatus, int rating);
        void chat(Scheduling agendamento);
    }

}

