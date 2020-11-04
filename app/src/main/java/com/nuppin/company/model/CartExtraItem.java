package com.nuppin.company.model;

import com.google.gson.annotations.Expose;

public class CartExtraItem {

    @Expose
    private String cart_id = "";
    @Expose
    private String cart_extra_id = "";
    @Expose
    private String extra_id;
    @Expose
    private String user_id;
    @Expose
    private String product_id;
    @Expose
    private String name;
    @Expose
    private int quantity;
    @Expose
    private String collection_id;

    public CartExtraItem(CollectionExtra collectionExtras, Extra extra, String userId, String productId) {
        user_id = userId;
        product_id = productId;
        collection_id = collectionExtras.getCollection_id();
        extra_id = extra.getExtra_id();
        name = extra.getName();
        quantity = extra.getQuantity();
        cart_extra_id = "";
    }

    public String getCart_id() {
        return cart_id;
    }

    public String getExtra_id() {
        return extra_id;
    }

    public String getUser_id() {
        return user_id;
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

}
