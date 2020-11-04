package com.nuppin.company.Loja.Financeiro;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.nuppin.company.model.Finance;
import com.nuppin.company.R;
import com.nuppin.company.Util.Util;

public class FrDetalheCashFlowLoja extends Fragment{

    private static final String EXTRA_CASH_FLOW = "CASH_FLOW";
    private Finance finance;

    public static FrDetalheCashFlowLoja newInstance(Finance finance) {
        Bundle parametros = new Bundle();
        parametros.putParcelable(EXTRA_CASH_FLOW, finance);
        FrDetalheCashFlowLoja fragment = new FrDetalheCashFlowLoja();
        fragment.setArguments(parametros);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null && getArguments().containsKey(EXTRA_CASH_FLOW)) {
            finance = getArguments().getParcelable(EXTRA_CASH_FLOW);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fr_cashflow_detail, container, false);

        //item deletado
        if (finance.getTitle() == null) {
            Toast.makeText(getContext(), "Registro excluido", Toast.LENGTH_SHORT).show();
            Util.backFragmentFunction(this);
        }

        Toolbar toolbar = v.findViewById(R.id.toolbar_top);
        Util.setaToolbar(this,R.string.detalhe_toolbar, toolbar,getActivity(),false,0);

        TextView titulo = v.findViewById(R.id.titulo);
        TextView descricao =  v.findViewById(R.id.description);
        TextView valor = v.findViewById(R.id.valor);
        final SimpleDraweeView image = v.findViewById(R.id.imagem);
        TextView despesaReceita = v.findViewById(R.id.despesaOuReceita);
        final TextView txtAnexo = v.findViewById(R.id.txtAnexo);

        txtAnexo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (image.getVisibility() == View.GONE) {
                    txtAnexo.setCompoundDrawablesWithIntrinsicBounds(null,null,getResources().getDrawable(R.drawable.ic_keyboard_arrow_up_black_24dp),null);
                    image.setVisibility(View.VISIBLE);
                }else {
                    txtAnexo.setCompoundDrawablesWithIntrinsicBounds(null,null,getResources().getDrawable(R.drawable.ic_keyboard_arrow_down_black_24dp),null);
                    image.setVisibility(View.GONE);
                }
            }
        });


        Util.hasPhoto(finance,image);
        titulo.setText(finance.getTitle());
        valor.setText(Util.formaterPrice(finance.getAmount()));

        if (finance.getDescription() == null || finance.getDescription().equals("")) {
            descricao.setText(R.string.sem_descricao);
        }else{
            descricao.setText(finance.getDescription());
        }

        if (finance.getAmount() < 0) {
            despesaReceita.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.ic_arrow_downward_black_24dp),null,null,null);
            valor.setTextColor(getResources().getColor(android.R.color.holo_red_light));
            despesaReceita.setText(R.string.despesa);
        }else {
            despesaReceita.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.ic_arrow_upward_black_24dp),null,null,null);
            valor.setTextColor(getResources().getColor(android.R.color.holo_green_light));
            despesaReceita.setText(R.string.receita);
        }

        FloatingActionButton fab = v.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getActivity() instanceof ToActivity) {
                    ToActivity listener = (ToActivity) getActivity();
                    listener.FabCashDetalhe(finance);
                }
            }
        });

        return v;
    }

    public interface ToActivity {
        void FabCashDetalhe(Finance finance);
    }
}
