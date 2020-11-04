package com.nuppin.company.model;

public class OrderItemExtra {

    private String order_item_extra_id;
    private String product_id;
    private String name;
    private int quantity;
    private String collection_id;
    private double total_amount;

    public OrderItemExtra() {
    }

    public String getOrder_item_extra_id() {
        return order_item_extra_id;
    }


    public String getProduct_id() {
        return product_id;
    }

    public String getName() {
        return name;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getCollection_id() {
        return collection_id;
    }

    public double getTotal_amount() {
        return total_amount;
    }
}
