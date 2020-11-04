package com.nuppin.company.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;

import java.util.ArrayList;
import java.util.List;

public class Cart implements Parcelable {

    @Expose
    private String cart_id = "";
    @Expose
    private int quantity = 1;
    @Expose
    private String user_id;
    @Expose
    private String product_id;
    @Expose
    private String company_id;
    @Expose
    private String note;
    @Expose
    private String size_id;
    @Expose
    private String size_name;
    @Expose
    private List<CartExtraItem> extra;
    private String name;
    private double total_price;
    private int has_cart;


    public List<CartExtraItem> getExtra() {
        return extra;
    }

    public void setCart_extras(List<CollectionExtra> cart_extras, String userId, String productId) {
        List<CartExtraItem> extras = new ArrayList<>();
        for (CollectionExtra collectionExtra : cart_extras) {
            if (collectionExtra.getQuantity_selected() > 0) {
                for (Extra extra : collectionExtra.getConjuct_extras()) {
                    if (extra.getQuantity() > 0) {
                        extras.add(new CartExtraItem(collectionExtra, extra, userId, productId));
                    }
                }
            }
        }
        this.extra = extras;
    }

    public void setCart_id(String cart_id) {
        this.cart_id = cart_id;
    }

    public String getCartStoId() {
        return company_id;
    }

    public int getTemcarrinho() {
        return has_cart;
    }

    public Cart(String user_id) {
        this.user_id = user_id;
    }

    public Cart(String user_id, String product_id, String company_id) {
        this.user_id = user_id;
        this.product_id = product_id;
        this.company_id = company_id;
    }

    protected Cart(Parcel in) {
        quantity = in.readInt();
        product_id = in.readString();
        name = in.readString();
        total_price = in.readInt();
        user_id = in.readString();
        note = in.readString();
        cart_id = in.readString();
    }

    public static final Creator<Cart> CREATOR = new Creator<Cart>() {
        @Override
        public Cart createFromParcel(Parcel in) {
            return new Cart(in);
        }

        @Override
        public Cart[] newArray(int size) {
            return new Cart[size];
        }
    };

    public int getCartQuantidade() {
        return quantity;
    }

    public void setCartQuantidade(int cartQuantidade) {
        this.quantity = cartQuantidade;
    }

    public double getCartProValor() {
        return total_price;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public void setSize_id(String size_id) {
        this.size_id = size_id;
    }

    public void setSize_name(String size_name) {
        this.size_name = size_name;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(quantity);
        dest.writeString(product_id);
        dest.writeString(name);
        dest.writeDouble(total_price);
        dest.writeString(user_id);
        dest.writeString(note);
        dest.writeString(cart_id);
    }
}