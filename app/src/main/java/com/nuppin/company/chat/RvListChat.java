package com.nuppin.company.chat;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.nuppin.company.R;
import com.nuppin.company.model.Chat;

import java.util.List;

public class RvListChat extends RecyclerView.Adapter<RvListChat.RvOrdersAdapterViewHolder>{

    private List<Chat> chat;
    private ChatOnClickListener listener;

    RvListChat(ChatOnClickListener handler) {
        listener = handler;
    }

    public class RvOrdersAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        final TextView  msg, msgQtd, ord_id;
        Context context;

         RvOrdersAdapterViewHolder(View view) {
             super(view);
             context = view.getContext();
             ord_id = view.findViewById(R.id.order_id);
             msg = view.findViewById(R.id.msg);
             msgQtd = view.findViewById(R.id.qtdMsg);
             view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int adapterPosition = getAdapterPosition();
            listener.onClickChat(chat.get(adapterPosition).getOrder_id(),chat.get(adapterPosition).getUser_id(),chat.get(adapterPosition).getUser_name(),chat.get(adapterPosition).getCompany_id());
        }
    }

    @NonNull
    public RvOrdersAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.rv_list_chats;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);
        return new RvOrdersAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RvOrdersAdapterViewHolder holder, int position) {
        holder.ord_id.setText(chat.get(position).getOrder_id());
        holder.msg.setText(chat.get(position).getMessage());

        if (Integer.parseInt(chat.get(position).getQuantity_not_seen()) > 0) {
            holder.msgQtd.setText(chat.get(position).getQuantity_not_seen());
            holder.msgQtd.setVisibility(View.VISIBLE);
            holder.msg.setTypeface(holder.msg.getTypeface(), Typeface.BOLD);
        }else {
            holder.msg.setTypeface(holder.msg.getTypeface(), Typeface.NORMAL);
            holder.msgQtd.setVisibility(View.GONE);
        }
        holder.setIsRecyclable(false);
    }

    @Override
    public int getItemCount() {
        if (null == chat)
            return 0;
        return chat.size();
    }

    public void setChats(List<Chat> chat) {
        this.chat = chat;
        notifyDataSetChanged();
    }

    public interface ChatOnClickListener {
        void onClickChat(String order_id, String userId, String userNome, String stoId);
    }
}

