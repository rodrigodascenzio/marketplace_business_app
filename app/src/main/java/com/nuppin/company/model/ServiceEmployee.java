package com.nuppin.company.model;

import com.google.gson.annotations.Expose;

import java.util.List;

public class ServiceEmployee {
    @Expose
    private String employee_id;
    @Expose
    private String service_id;
    @Expose
    private String company_id;

    //usado no agendamento manual
    private String user_name;
    private List<Scheduling> scheduling;


    public String getCompany_id() {
        return company_id;
    }

    public void setCompany_id(String company_id) {
        this.company_id = company_id;
    }

    public List<Scheduling> getScheduling() {
        return scheduling;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setEmployee_id(String employee_id) {
        this.employee_id = employee_id;
    }

    public void setService_id(String service_id) {
        this.service_id = service_id;
    }

    public String getEmployee_id() {
        return employee_id;
    }

    public ServiceEmployee(){

    }
}
