package com.nuppin.company.model;

public class Report {

    private int view_count;
    private int tm_view_count;
    private int canceled_orders;
    private int concluded_orders;
    private double orders_total_value;
    private double average_ticket;
    private int recurring_customer;
    private int new_customer;
    private double expenses;
    private double revenue;

    public Report() {

    }

    public int getView_count() {
        return view_count;
    }

    public int getTm_view_count() {
        return tm_view_count;
    }

    public int getRecurring_customer() {
        return recurring_customer;
    }

    public int getNew_customer() {
        return new_customer;
    }

    public double getAverage_ticket() {
        return average_ticket;
    }

    public int getCanceled_orders() {
        return canceled_orders;
    }

    public int getConcluded_orders() {
        return concluded_orders;
    }

    public double getOrders_total_value() {
        return orders_total_value;
    }

    public double getExpenses() {
        return expenses;
    }

    public double getRevenue() {
        return revenue;
    }
}
