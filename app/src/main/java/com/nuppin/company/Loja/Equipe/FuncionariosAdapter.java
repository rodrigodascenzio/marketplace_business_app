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


public class FuncionariosAdapter extends RecyclerView.Adapter<FuncionariosAdapter.FuncionariosAdapterViewHolder> {

    private List<Employee> employees;
    private FuncionariosAdapterListener listener;

    public FuncionariosAdapter() {
    }

    FuncionariosAdapter(FuncionariosAdapterListener listener) {
        this.listener = listener;
    }

    public class FuncionariosAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView func, horario, cargo, icon, voce;

        FuncionariosAdapterViewHolder(View view) {
            super(view);
            voce = view.findViewById(R.id.user);
            icon = view.findViewById(R.id.icon);
            cargo = view.findViewById(R.id.txtCargo);
            func = view.findViewById(R.id.employee);
            horario = view.findViewById(R.id.txtHorario);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            listener.onClickFuncionario(employees.get(getAdapterPosition()));
        }
    }

    @NonNull
    @Override
    public FuncionariosAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.rv_employee;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);
        return new FuncionariosAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FuncionariosAdapterViewHolder holder, int position) {
        holder.icon.setVisibility(View.GONE);
        holder.voce.setVisibility(View.GONE);

        holder.func.setText(employees.get(position).getUser_name());
        if (employees.get(position).getTitle() != null && !employees.get(position).getTitle().equals("")) {
            holder.cargo.setText(employees.get(position).getTitle());
        } else {
            holder.cargo.setText(holder.cargo.getContext().getResources().getString(R.string.default_cargo));
        }
        if (employees.get(position).getStart_time() != null) {
            holder.horario.setText(holder.horario.getContext().getResources().getString(R.string.employee_time_with_dot, employees.get(position).getStart_time(), employees.get(position).getEnd_time()));
        } else {
            holder.horario.setText(holder.horario.getContext().getResources().getString(R.string.default_time));
        }

        if (employees.get(position).getRole().equals("owner")) {
            holder.icon.setCompoundDrawablesWithIntrinsicBounds(R.drawable.crown,0,0,0);
            holder.icon.setVisibility(View.VISIBLE);
        } else if (employees.get(position).getRole().equals("admin")) {
            holder.icon.setCompoundDrawablesWithIntrinsicBounds(R.drawable.crown_outline,0,0,0);
            holder.icon.setVisibility(View.VISIBLE);
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

    void setEmployees(List<Employee> employees) {
        this.employees = employees;
        notifyDataSetChanged();
    }

    public interface FuncionariosAdapterListener {
        void onClickFuncionario(Employee employee);
    }
}