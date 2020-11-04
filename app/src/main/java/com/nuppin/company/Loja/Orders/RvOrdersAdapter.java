package com.nuppin.company.Loja.Orders;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.nuppin.company.Util.AppConstants;
import com.nuppin.company.model.Order;
import com.nuppin.company.R;
import com.nuppin.company.Util.Util;

import java.util.List;

public class RvOrdersAdapter extends RecyclerView.Adapter<RvOrdersAdapter.RvOrdersAdapterViewHolder>
        implements RvOrderProductsAdapter.RvOrderProductsAdapterListener{

    private RvOrdersAdapterOnClickListener listener;
    private List<Order> ordList;
    private boolean anima = false;
    private int position;

    public RvOrdersAdapter(RvOrdersAdapterOnClickListener handler) {
        listener = handler;
    }

    public class RvOrdersAdapterViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener{

        final TextView ordUser, ordUserRating, subTotal, total, entregat, entrega, ordEnd, txtLocal, distancia, rating, desconto, descontoTxt, meioPagamento;
        final MaterialButton aceitar;
        final ImageButton recusar;
        RecyclerView ordPro;
        RvOrderProductsAdapter adapter;
        Context context;
        RatingBar ratingUser;
        View v1;
        Context ctx;
        CardView cardView;
        TextView obs;
        MaterialButton chat;
        ConstraintLayout enderecoCard, localCard;
        CardView maps;

         RvOrdersAdapterViewHolder(View view) {
             super(view);
             v1 = view;

             obs = view.findViewById(R.id.obs);
             chat = view.findViewById(R.id.chat);
             cardView = view.findViewById(R.id.cardView);
             ctx = view.getContext();
             maps = view.findViewById(R.id.linearMaps);
             ratingUser = view.findViewById(R.id.ratingUser);
             ordEnd = view.findViewById(R.id.ordEnd);
             ordUser = view.findViewById(R.id.ordUser);
             ordUserRating = view.findViewById(R.id.ordUserRating);
             context = view.getContext();
             subTotal = view.findViewById(R.id.resultSubtotal);
             desconto = view.findViewById(R.id.resulDesconto);
             descontoTxt = view.findViewById(R.id.desconto);
             total = view.findViewById(R.id.resultTotal);
             ordPro = view.findViewById(R.id.ordProd);
             entrega = view.findViewById(R.id.resultEntrega);
             entregat = view.findViewById(R.id.entrega);
             aceitar = view.findViewById(R.id.btnAceitar);
             recusar = view.findViewById(R.id.btnRecusar);
             distancia = view.findViewById(R.id.distancia);
             meioPagamento = view.findViewById(R.id.meioPagamento);
             rating = view.findViewById(R.id.rating);
             enderecoCard = view.findViewById(R.id.endereco);
             localCard = view.findViewById(R.id.local);
             txtLocal = view.findViewById(R.id.txtLocal);
             aceitar.setOnClickListener(this);
             recusar.setOnClickListener(this);
             view.setOnClickListener(this);
             chat.setOnClickListener(this);
             //maps.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            position = getAdapterPosition();
            anima = true;
            switch (v.getId()) {
                case R.id.btnAceitar:
                    switch (ordList.get(position).getStatus()) {
                        case AppConstants.STATUS_PENDING:
                            listener.onClickBtn(ordList.get(position), AppConstants.STATUS_ACCEPTED);
                            break;
                        case AppConstants.STATUS_ACCEPTED:
                            if (ordList.get(position).getType().equals("delivery")) {
                                listener.onClickBtn(ordList.get(position), AppConstants.STATUS_DELIVERY);
                            } else {
                                listener.onClickBtn(ordList.get(position), AppConstants.STATUS_RELEASED);
                            }
                            break;
                        case AppConstants.STATUS_DELIVERY:
                            listener.onClickBtn(ordList.get(position), AppConstants.STATUS_CONCLUDED_NOT_RATED);
                            break;
                        case AppConstants.STATUS_RELEASED:
                            listener.onClickBtn(ordList.get(position), AppConstants.STATUS_CONCLUDED_NOT_RATED);
                            break;
                        case AppConstants.STATUS_CONCLUDED_NOT_RATED:
                            listener.onClickBtnRating(ordList.get(position), AppConstants.STATUS_CONCLUDED, ratingUser.getProgress());
                            if (ratingUser.getProgress() != 0) {
                                ratingUser.setProgress(0);
                            }
                            break;
                    }
                    break;
                case R.id.btnRecusar:
                    listener.onClickBtn(ordList.get(position),AppConstants.STATUS_CANCELED_REFUSED);
                    break;
                case R.id.chat:
                    listener.chat(ordList.get(position));
                    break;
                case R.id.linearMaps:
                    listener.enderecoClick(ordList.get(position));
                    break;
            }
        }
    }

    @NonNull
    public RvOrdersAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.rv_request;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);
        return new RvOrdersAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RvOrdersAdapterViewHolder holder, int position) {
        holder.chat.setVisibility(View.GONE);
        holder.obs.setVisibility(View.GONE);
        holder.recusar.setVisibility(View.GONE);
        holder.aceitar.setVisibility(View.GONE);
        holder.ratingUser.setVisibility(View.GONE);
        holder.desconto.setVisibility(View.GONE);
        holder.descontoTxt.setVisibility(View.GONE);
        holder.enderecoCard.setVisibility(View.GONE);
        holder.localCard.setVisibility(View.GONE);
        holder.entrega.setVisibility(View.GONE);
        holder.entregat.setVisibility(View.GONE);


        if (anima) {
            holder.itemView.startAnimation(AnimationUtils.loadAnimation(holder.context,R.anim.views_up));
            if (ordList.size() > 1) {
                if (position == (this.position + 1) || position == (this.position - 2)) {
                    anima = false;
                }
            } else {
                anima = false;
            }
        }

        if (ordList.get(position).getIs_chat_available() == 1) {
            holder.chat.setVisibility(View.VISIBLE);
        }

        if (ordList.get(position).getNote() != null && !ordList.get(position).getNote().equals("")) {
            holder.obs.setText(holder.obs.getContext().getResources().getString(R.string.observacao, ordList.get(position).getNote()));
            holder.obs.setVisibility(View.VISIBLE);
        }

        if (position == 0) {
            ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) holder.cardView.getLayoutParams();
            layoutParams.setMargins(holder.ctx.getResources().getDimensionPixelOffset(R.dimen.order_left), holder.ctx.getResources().getDimensionPixelOffset(R.dimen.order_first_last), holder.ctx.getResources().getDimensionPixelOffset(R.dimen.order_top), holder.ctx.getResources().getDimensionPixelOffset(R.dimen.order_bottom));
        }else if (position == ordList.size() - 1) {
            ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) holder.cardView.getLayoutParams();
            layoutParams.setMargins(holder.ctx.getResources().getDimensionPixelOffset(R.dimen.order_left), holder.ctx.getResources().getDimensionPixelOffset(R.dimen.order_right), holder.ctx.getResources().getDimensionPixelOffset(R.dimen.order_top), holder.ctx.getResources().getDimensionPixelOffset(R.dimen.order_first_last));
        }else{
            ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) holder.cardView.getLayoutParams();
            layoutParams.setMargins(holder.ctx.getResources().getDimensionPixelOffset(R.dimen.order_left), holder.ctx.getResources().getDimensionPixelOffset(R.dimen.order_right), holder.ctx.getResources().getDimensionPixelOffset(R.dimen.order_top), holder.ctx.getResources().getDimensionPixelOffset(R.dimen.order_bottom));
        }

        switch (ordList.get(position).getStatus()) {
            case AppConstants.STATUS_PENDING:
                holder.recusar.setVisibility(View.VISIBLE);
                holder.aceitar.setVisibility(View.VISIBLE);
                holder.aceitar.setText(R.string.aceitar);
                break;
            case AppConstants.STATUS_ACCEPTED:
                if (ordList.get(position).getType().equals("delivery")) {
                    holder.aceitar.setVisibility(View.VISIBLE);
                    holder.aceitar.setText(R.string.entregar);
                } else {
                    holder.aceitar.setVisibility(View.VISIBLE);
                    holder.aceitar.setText(R.string.liberar_retirada);
                }
                break;
            case AppConstants.STATUS_DELIVERY:
                holder.aceitar.setVisibility(View.VISIBLE);
                holder.aceitar.setText(R.string.confirmar_entrega);
                break;
            case AppConstants.STATUS_RELEASED:
                holder.aceitar.setVisibility(View.VISIBLE);
                holder.aceitar.setText(R.string.confirmar_retirada);
                break;
            case AppConstants.STATUS_CONCLUDED_NOT_RATED:
                holder.ratingUser.setVisibility(View.VISIBLE);
                holder.aceitar.setVisibility(View.VISIBLE);
                holder.aceitar.setText(R.string.avaliar);
                break;
        }

        holder.ordUser.setText(ordList.get(position).getUser_name());

        if (!ordList.get(position).getSource().equals("nuppin_company")) {
            if (ordList.get(position).getUser_num_rating() == 0) {
                holder.ordUserRating.setText(holder.ordUser.getContext().getResources().getString(R.string.user_no_rated));
                holder.ordUserRating.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            } else {
                holder.ordUserRating.setText(holder.ordUser.getContext().getResources().getString(R.string.order_user_nome_rating, Util.formaterRating(ordList.get(position).getUser_rating()), ordList.get(position).getUser_num_rating()));
                holder.ordUserRating.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_star_yellow_12dp, 0, 0, 0);
            }
        }else {
            holder.ordUserRating.setText(holder.ordUser.getContext().getResources().getString(R.string.manual));
            holder.ordUserRating.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        }

        if (ordList.get(position).getDiscount_amount() > 0) {
            holder.desconto.setVisibility(View.VISIBLE);
            holder.descontoTxt.setVisibility(View.VISIBLE);
        }

        holder.meioPagamento.setText(holder.meioPagamento.getContext().getResources().getString(R.string.ord_id, ordList.get(position).getOrder_id().toUpperCase(),ordList.get(position).getPayment_method()));

        if (ordList.get(position).getType().equals("pos")) {
            holder.txtLocal.setText(R.string.presencial);
            holder.localCard.setVisibility(View.VISIBLE);
        }else if (ordList.get(position).getType().equals("local")) {
            holder.txtLocal.setText(R.string.retirada);
            holder.localCard.setVisibility(View.VISIBLE);
        } else {
            holder.enderecoCard.setVisibility(View.VISIBLE);
            holder.entrega.setVisibility(View.VISIBLE);
            holder.entregat.setVisibility(View.VISIBLE);
            holder.distancia.setText(Util.formater(ordList.get(position).getDistance()));
            holder.ordEnd.setText(ordList.get(position).getAddress());
        }

        holder.subTotal.setText(Util.formaterPrice(ordList.get(position).getSubtotal_amount()));
        holder.entrega.setText(Util.formaterPrice(ordList.get(position).getDelivery_amount()));
        holder.desconto.setText(Util.formaterPrice(ordList.get(position).getDiscount_amount()));
        holder.total.setText(Util.formaterPrice(ordList.get(position).getTotal_amount()));

        holder.ordPro.setLayoutManager(new LinearLayoutManager(holder.context, RecyclerView.VERTICAL, false));
        holder.adapter = new RvOrderProductsAdapter(this);
        holder.ordPro.setAdapter(holder.adapter);
        holder.adapter.setOrder(ordList.get(position).getOrder_item());
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


    public interface RvOrdersAdapterOnClickListener {
        void onClick(String order_id);
        void onClickBtn(Order order, String atualizarParaStatus);
        void onClickBtnRating(Order order, String atualizarParaStatus, int rating);
        void chat(Order order);
        void enderecoClick(Order order);

    }

}

