package com.nuppin.company.Loja.Servico;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.nuppin.company.Util.UtilShaPre;
import com.nuppin.company.model.Service;
import com.nuppin.company.R;
import com.nuppin.company.Util.Util;

import java.util.List;

import me.toptas.fancyshowcase.FancyShowCaseView;
import me.toptas.fancyshowcase.FocusShape;

public class RvServicosAdapter extends RecyclerView.Adapter<RvServicosAdapter.RvServicosAdapterViewHolder> {

    private List<Service> servicos;
    private RvServicosOnClickListener listener;
    private FragmentActivity fragmentActivity;

    RvServicosAdapter(RvServicosOnClickListener handler, FragmentActivity fragmentActivity) {
        listener = handler;
        this.fragmentActivity = fragmentActivity;
    }


    public class RvServicosAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final TextView productName, preco, desc, duracao, btn;
        private CardView cardService;
        private Context ctx;


        private FancyShowCaseView fancyShowCaseView1;

        private void chainCreatedTutorial() {
            if (fancyShowCaseView1 == null) {
                fancyShowCaseView1 = new FancyShowCaseView.Builder(fragmentActivity)
                        .focusOn(cardService)
                        .title(ctx.getString(R.string.unique_tutorial_service_card_service_string))
                        .focusShape(FocusShape.ROUNDED_RECTANGLE)
                        .showOnce(ctx.getString(R.string.unique_tutorial_service_card_service))
                        .backgroundColor(ctx.getResources().getColor(R.color.primary_light))
                        .enableAutoTextPosition()
                        .closeOnTouch(true)
                        .build();
            }
        }

        private void chainTutorial() {
            if (!fancyShowCaseView1.isShownBefore()) {
                fancyShowCaseView1.show();
                return;
            }
        }


        RvServicosAdapterViewHolder(View view) {
            super(view);
            ctx = view.getContext();
            cardService = view.findViewById(R.id.cardService);
            productName = view.findViewById(R.id.nome);
            preco = view.findViewById(R.id.preco);
            desc = view.findViewById(R.id.desc);
            duracao = view.findViewById(R.id.duracao);
            btn = view.findViewById(R.id.btn);
            btn.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            listener.onClick(servicos.get(adapterPosition));
        }
    }

    @NonNull
    public RvServicosAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.rv_list_service;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);
        return new RvServicosAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RvServicosAdapterViewHolder holder, int position) {
        holder.productName.setText(servicos.get(position).getName());
        holder.preco.setText(Util.formaterPrice(servicos.get(position).getPreco()));
        holder.desc.setText(servicos.get(position).getDescription());
        holder.duracao.setText(holder.duracao.getContext().getResources().getString(R.string.servico_duracao, servicos.get(position).getDuration()));

        if (position == servicos.size() - 1) {
            if (servicos.size() == 1) {
                holder.chainCreatedTutorial();
                holder.chainTutorial();
            } else if (servicos.size() > 0) {
                UtilShaPre.writeShown(fragmentActivity, holder.ctx.getString(R.string.unique_tutorial_service_card_service));
            }
        }
    }

    @Override
    public int getItemCount() {
        if (null == servicos)
            return 0;
        return servicos.size();
    }

    public void setServicos(List<Service> servicos) {
        this.servicos = servicos;
        notifyDataSetChanged();
    }

    public interface RvServicosOnClickListener {
        void onClick(Service service);
    }

}


