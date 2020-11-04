package com.nuppin.company.Loja.produto;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.nuppin.company.R;
import com.nuppin.company.Util.UtilShaPre;
import com.nuppin.company.model.Collection;
import com.nuppin.company.model.CollectionExtra;

import java.util.List;

import me.toptas.fancyshowcase.FancyShowCaseView;
import me.toptas.fancyshowcase.FocusShape;

public class ConjuctHeaderAdapter extends RecyclerView.Adapter<ConjuctHeaderAdapter.ConjuctHeaderAdapterViewHolder> {

    private List<CollectionExtra> collectionExtra;
    private ConjuctHeaderAdapterListener listener;

    ConjuctHeaderAdapter(ConjuctHeaderAdapterListener listener) {
        this.listener = listener;
    }

    public class ConjuctHeaderAdapterViewHolder extends RecyclerView.ViewHolder
            implements ConjuctExtrasAdapter.ConjuctBodyExtrasAdapterListener, View.OnClickListener {

        private TextView nome, options, description, required, invisible, delete, arrow;
        private Context ctx;
        private View view;
        private RecyclerView recyclerView;
        private ConstraintLayout card;
        private ConjuctExtrasAdapter adapter;

        ConjuctHeaderAdapterViewHolder(View view) {
            super(view);
            this.view = view;
            arrow = view.findViewById(R.id.arrow);
            delete = view.findViewById(R.id.delete);
            //edit = view.findViewById(R.id.edit);
            card = view.findViewById(R.id.card);
            recyclerView = view.findViewById(R.id.recycler);
            ctx = view.getContext();
            nome = view.findViewById(R.id.nome);
            options = view.findViewById(R.id.opcoes);
            required = view.findViewById(R.id.required);
            invisible = view.findViewById(R.id.invisible);
            description = view.findViewById(R.id.description);
            card.setOnClickListener(this);
            delete.setOnClickListener(this);
            //edit.setOnClickListener(this);
            invisible.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int adapterPosition = getAdapterPosition();

            switch (view.getId()) {
                case R.id.card:
                    if (recyclerView.getVisibility() == View.VISIBLE) {
                        recyclerView.setVisibility(View.GONE);
                        //edit.setVisibility(View.GONE);
                        delete.setVisibility(View.GONE);
                        arrow.setCompoundDrawablesWithIntrinsicBounds(arrow.getResources().getDrawable(R.drawable.ic_keyboard_arrow_down_black_24dp), null, null, null);
                    } else {
                        arrow.setCompoundDrawablesWithIntrinsicBounds(arrow.getResources().getDrawable(R.drawable.ic_keyboard_arrow_up_black_24dp), null, null, null);
                        recyclerView.setVisibility(View.VISIBLE);
                        //edit.setVisibility(View.VISIBLE);
                        delete.setVisibility(View.VISIBLE);
                    }
                    break;
                case R.id.delete:
                    Collection collection = new Collection();
                    collection.setCollection_id(collectionExtra.get(adapterPosition).getCollection_id());
                    collection.setProduct_id(collectionExtra.get(adapterPosition).getProduct_id());
                    collection.setCompany_id(collectionExtra.get(adapterPosition).getCompany_id());
                    listener.onHeaderClick(R.id.delete, collection);
                    break;
                case R.id.edit:
                    //Toast.makeText(ctx, "Editar", Toast.LENGTH_SHORT).show();
                    //listener.onHeaderClick(R.id.edit);
                    break;
                case R.id.invisible:
                    Toast.makeText(ctx, "Grupo com a quantidade minima de escolhas maior que a quantidade de itens do grupo. Adicione mais itens ao grupo para resolver o problema", Toast.LENGTH_LONG).show();
                    //listener.onHeaderClick(R.id.edit);
                    break;
            }
        }
    }

    @NonNull
    @Override
    public ConjuctHeaderAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.rv_conjuct_product;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);
        return new ConjuctHeaderAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ConjuctHeaderAdapterViewHolder holder, int position) {
        holder.required.setVisibility(View.GONE);
        holder.invisible.setVisibility(View.GONE);

        if (collectionExtra.get(position).getMin_quantity() > 0) {
            holder.required.setVisibility(View.VISIBLE);
        }

        if (collectionExtra.get(position).getHas_warning() == 1) {
            holder.invisible.setVisibility(View.VISIBLE);
        }

        if (collectionExtra.get(position).getMin_quantity() == collectionExtra.get(position).getMax_quantity()) {
            if (collectionExtra.get(position).getMax_quantity() > 1) {
                holder.options.setText(collectionExtra.get(position).getMax_quantity() + " escolhas");
            } else {
                holder.options.setText("1 escolha");
            }
        } else {
            if (collectionExtra.get(position).getMin_quantity() > 1) {
                if (collectionExtra.get(position).getMax_quantity() > 1) {
                    holder.options.setText("De " + collectionExtra.get(position).getMin_quantity() + " Até " + collectionExtra.get(position).getMax_quantity() + " escolhas");
                } else {
                    holder.options.setText("De " + collectionExtra.get(position).getMin_quantity() + " Até " + collectionExtra.get(position).getMax_quantity() + " escolha");
                }
            } else {
                if (collectionExtra.get(position).getMax_quantity() > 1) {
                    holder.options.setText("Até " + collectionExtra.get(position).getMax_quantity() + " escolhas");
                } else {
                    holder.options.setText("Até " + collectionExtra.get(position).getMax_quantity() + " escolha");
                }
            }
        }

        holder.nome.setText(collectionExtra.get(position).getName());
        holder.description.setText(collectionExtra.get(position).getDescription());
        LinearLayoutManager layoutManager = new LinearLayoutManager(holder.ctx, RecyclerView.VERTICAL, false);
        holder.recyclerView.setLayoutManager(layoutManager);
        holder.adapter = new ConjuctExtrasAdapter(holder);
        holder.recyclerView.setAdapter(holder.adapter);
        holder.adapter.setExtras(collectionExtra.get(position).getConjuct_extras(), collectionExtra.get(position));

        if (!UtilShaPre.getDefaultsBool(holder.ctx.getString(R.string.unique_tutorial_produto_collection_card_collection),holder.ctx)) {
            Toast.makeText(holder.ctx, holder.ctx.getString(R.string.unique_tutorial_produto_collection_card_collection_string), Toast.LENGTH_LONG).show();
            UtilShaPre.setDefaults(holder.ctx.getString(R.string.unique_tutorial_produto_collection_card_collection), true, holder.ctx);
        }
    }

    @Override
    public int getItemCount() {
        if (collectionExtra == null) {
            return 0;
        } else {
            return collectionExtra.size();
        }
    }

    void setConjuct(List<CollectionExtra> collectionExtra) {
        this.collectionExtra = collectionExtra;
        notifyDataSetChanged();
    }

    public interface ConjuctHeaderAdapterListener {
        void onHeaderClick(int index, Collection collection);
    }

}
