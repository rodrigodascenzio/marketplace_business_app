package com.nuppin.company.model;

import java.util.List;

public class Plan {
    private String plan_id;
    private double price;
    private String name;
    private String description;
    private int fee;
    private int trial_fee;
    private int trial_period;
    private double trial_price;
    private boolean is_trial;

    private List<Plan> benefit;

    public List<Plan> getBenefit() {
        return benefit;
    }

    public int getTrial_fee() {
        return trial_fee;
    }

    public boolean getIs_trial() {
        return is_trial;
    }

    public int getTrial_period() {
        return trial_period;
    }

    public double getTrial_price() {
        return trial_price;
    }

    public String getPlan_id() {
        return plan_id;
    }

    public double getPrice() {
        return price;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getFee() {
        return fee;
    }

}
