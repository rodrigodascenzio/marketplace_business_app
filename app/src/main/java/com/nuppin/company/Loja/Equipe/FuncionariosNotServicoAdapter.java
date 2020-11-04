package com.nuppin.company.Loja.Equipe;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.nuppin.company.Util.AppConstants;
import com.nuppin.company.Util.UtilShaPre;
import com.nuppin.company.model.Employee;
import com.nuppin.company.R;

import java.util.List;

public class FuncionariosNotServicoAdapter extends RecyclerView.Adapter<FuncionariosNotServicoAdapter.FuncionariosNotServicoAdapterViewHolder> {

    private List<Employee> employees;
    private FuncionariosServicoAdapterListener listener;

    public FuncionariosNotServicoAdapter(){}

    FuncionariosNotServicoAdapter(FuncionariosServicoAdapterListener listener) {
        this.listener = listener;
    }

    public class FuncionariosNotServicoAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView func, txtHorario, btnTxt, cargo, voce;

        FuncionariosNotServicoAdapterViewHolder(View view){
            super(view);
            voce = view.findViewById(R.id.user);
            cargo = view.findViewById(R.id.txtCargo);
            func = view.findViewById(R.id.employee);
            txtHorario = view.findViewById(R.id.txtHorario);
            btnTxt = view.findViewById(R.id.btnTxt);
            btnTxt.setOnClickListener(this);
        }


        @Override
        public void onClick(View view) {
            listener.incluirFuncionarioServico(employees.get(getAdapterPosition()));
        }
    }

    @NonNull
    @Override
    public FuncionariosNotServicoAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.rv_employee;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);
        return new FuncionariosNotServicoAdapterViewHolder(view);    }

    @Override
    public void onBindViewHolder(@NonNull FuncionariosNotServicoAdapterViewHolder holder, int position) {
        holder.btnTxt.setVisibility(View.VISIBLE);
        holder.voce.setVisibility(View.GONE);

        holder.btnTxt.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_add_black_24dp,0,0,0);
        holder.func.setText(employees.get(position).getUser_name());
        if (employees.get(position).getTitle() != null && !employees.get(position).getTitle().equals("")) {
            holder.cargo.setText(employees.get(position).getTitle());
        }else {
            holder.cargo.setText(holder.cargo.getContext().getResources().getString(R.string.default_cargo));
        }
        if (employees.get(position).getStart_time() != null) {
            holder.txtHorario.setText(holder.txtHorario.getContext().getResources().getString(R.string.employee_time_with_dot, employees.get(position).getStart_time(), employees.get(position).getEnd_time()));
        }else {
            holder.txtHorario.setText(holder.txtHorario.getContext().getResources().getString(R.string.default_time));
        }
        if (employees.get(position).getUser_id().equals(UtilShaPre.getDefaultsString(AppConstants.USER_ID, holder.voce.getContext()))) {
            holder.voce.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        if (employees == null) {
            return 0;
        } else {
            return employees.size();
        }
    }

    void setEmployees(List<Employee> employees){
        this.employees = employees;
        notifyDataSetChanged();
    }

    public interface FuncionariosServicoAdapterListener {
        void incluirFuncionarioServico(Employee employee);
    }
}
