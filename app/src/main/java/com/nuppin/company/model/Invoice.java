package com.nuppin.company.model;

public class Invoice {

    private String invoice_id;
    private String company_id;
    private String due_date;
    private String created_date;
    private String status;
    private String code_line;
    private String external_link;
    private double subtotal_amount;
    private double fee_amount;
    private double total_amount;
    private String category_company_id;

    public String getCategory_company_id() {
        return category_company_id;
    }

    public String getCompany_id() {
        return company_id;
    }

    public String getExternal_link() {
        return external_link;
    }

    public double getTotal_amount() {
        return total_amount;
    }

    public String getInvoice_id() {
        return invoice_id;
    }

    public String getDue_date() {
        return due_date;
    }

    public String getCreated_date() {
        return created_date;
    }

    public String getStatus() {
        return status;
    }

    public String getCode_line() {
        return code_line;
    }

    public double getSubtotal_amount() {
        return subtotal_amount;
    }

    public double getFee_amount() {
        return fee_amount;
    }
}
