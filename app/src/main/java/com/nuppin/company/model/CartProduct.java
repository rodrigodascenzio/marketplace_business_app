package com.nuppin.company.model;

import android.os.Parcel;
import android.os.Parcelable;
import java.util.List;

public class CartProduct implements Parcelable {

    private String cart_id;
    private String company_id;
    private String name;
    private double total_price;
    private String note;
    private int quantity;
    private String product_id;
    private String user_id;
    private int is_available;
    private String source;
    private List<CartExtraItem> extra;
    private String size_name;
    private String size_id;

    private CartProduct(Parcel in) {
        company_id = in.readString();
        product_id = in.readString();
        name = in.readString();
        total_price = in.readDouble();
        quantity = in.readInt();
        note = in.readString();
    }

    public String getCart_id() {
        return cart_id;
    }

    public static final Creator<CartProduct> CREATOR = new Creator<CartProduct>() {
        @Override
        public CartProduct createFromParcel(Parcel in) {
            return new CartProduct(in);
        }

        @Override
        public CartProduct[] newArray(int size) {
            return new CartProduct[size];
        }
    };

    public int getIs_available() {
        return is_available;
    }

    public String getCompany_id() {
        return company_id;
    }

    public String getProduct_id() {
        return product_id;
    }

    public String getName() {
        return name;
    }

    public double getTotal_price() {
        return total_price;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getNote() {
        return note;
    }

    public String getSize_name() {
        return size_name;
    }

    public String getSize_id() {
        return size_id;
    }

    public List<CartExtraItem> getExtra() {
        return extra;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(company_id);
        dest.writeString(product_id);
        dest.writeString(name);
        dest.writeDouble(total_price);
        dest.writeInt(quantity);
        dest.writeString(note);
    }
}