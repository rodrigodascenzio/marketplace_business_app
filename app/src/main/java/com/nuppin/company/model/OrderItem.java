package com.nuppin.company.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;

import java.util.List;

public class OrderItem implements Parcelable {

    @Expose
    private String order_item_id;
    @Expose
    private String name;
    @Expose
    private int quantity;
    @Expose
    private double total_amount;
    @Expose
    private String note;
    private List<OrderItemExtra> extra;
    private String size_name;

    private OrderItem(Parcel in) {
        order_item_id = in.readString();
        name = in.readString();
        quantity = in.readInt();
        total_amount = in.readDouble();
    }

    public static final Creator<OrderItem> CREATOR = new Creator<OrderItem>() {
        @Override
        public OrderItem createFromParcel(Parcel in) {
            return new OrderItem(in);
        }

        @Override
        public OrderItem[] newArray(int size) {
            return new OrderItem[size];
        }
    };

    public List<OrderItemExtra> getExtra() {
        return extra;
    }

    public String getNote() {
        return note;
    }

    public String getName() {
        return name;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getTotal_amount() {
        return total_amount;
    }


    public String getSize_name() {
        return size_name;
    }

    public OrderItem() {

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(order_item_id);
        dest.writeString(name);
        dest.writeInt(quantity);
        dest.writeDouble(total_amount);
    }
}