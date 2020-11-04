package com.nuppin.company.chat;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.nuppin.company.model.Chat;
import com.nuppin.company.R;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatAdapterViewHolder> {

    private List<Chat> chat;
    private String stoId;
    private Context ctx;
    private static final int TIPO_REMETENTE     = 0;
    private static final int TIPO_DESTINATARIO  = 1;

    ChatAdapter(Context context) {
        ctx = context;
    }

    class ChatAdapterViewHolder extends RecyclerView.ViewHolder {

        TextView msg;
        TextView seen;

        ChatAdapterViewHolder(View view) {
            super(view);
            msg = view.findViewById(R.id.textMensagemTexto);
            seen = view.findViewById(R.id.seen);
        }
    }

    @NonNull
    public ChatAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view;

        if ( viewType == TIPO_REMETENTE ){
             view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.chat_user1_item, viewGroup, false);
        }else {
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.chat_user2_item, viewGroup, false);
        }

        return new ChatAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatAdapterViewHolder holder, int position) {
        holder.msg.setText(chat.get(position).getMessage());

        if (holder.getItemViewType() == TIPO_REMETENTE) {
            holder.seen.setVisibility(View.GONE);
            if (position == chat.size() - 1 && chat.get(position).getChat_from().equals(stoId) && chat.get(position).getSeen_date() != null) {
                holder.seen.setVisibility(View.VISIBLE);
            }
        }

        holder.setIsRecyclable(false);
    }

    @Override
    public int getItemViewType(int position) {
        if (chat.get(position).getChat_from().equals(stoId)) {
            return TIPO_REMETENTE;
        }else {
            return TIPO_DESTINATARIO;
        }
    }

    @Override
    public int getItemCount() {
        if (chat == null)
            return 0;
        return chat.size();
    }

    void setChat(List chat, String stoId) {
        this.chat = chat;
        this.stoId = stoId;
        notifyDataSetChanged();
    }

}