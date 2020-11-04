package com.nuppin.company.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;

public class Coupon implements Parcelable {

    @Expose
    private String coupon_id;
    @Expose
    private String company_id;
    @Expose
    private double value;
    @Expose
    private String target;
    @Expose
    private String discount_type;
    @Expose
    private String created_date;
    @Expose
    private String due_date;
    @Expose
    private double min_purchase;
    @Expose
    private int quantity;

    //not expose
    private int quantity_used;
    private int expires_day;
    private int expires_hour;
    private int expires_minute;

    public int getQuantity_used() {
        return quantity_used;
    }

    public int getExpires_day() {
        return expires_day;
    }

    public int getExpires_hour() {
        return expires_hour;
    }

    public int getExpires_minute() {
        return expires_minute;
    }

    private Coupon(Parcel in) {
        coupon_id = in.readString();
        company_id = in.readString();
        value = in.readDouble();
        target = in.readString();
        discount_type = in.readString();
        created_date = in.readString();
        due_date = in.readString();
        min_purchase = in.readDouble();
        quantity = in.readInt();
    }

    public Coupon() {

    }

    public static final Creator<Coupon> CREATOR = new Creator<Coupon>() {
        @Override
        public Coupon createFromParcel(Parcel in) {
            return new Coupon(in);
        }

        @Override
        public Coupon[] newArray(int size) {
            return new Coupon[size];
        }
    };

    public void setCoupon_id(String coupon_id) {
        this.coupon_id = coupon_id;
    }

    public String getCompany_id() {
        return company_id;
    }

    public void setCompany_id(String company_id) {
        this.company_id = company_id;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public String getCreated_date() {
        return created_date;
    }

    public void setDue_date(String due_date) {
        this.due_date = due_date;
    }

    public void setMin_purchase(double min_purchase) {
        this.min_purchase = min_purchase;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getDiscount_type() {
        return discount_type;
    }

    public void setDiscount_type(String discount_type) {
        this.discount_type = discount_type;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(coupon_id);
        parcel.writeString(company_id);
        parcel.writeDouble(value);
        parcel.writeString(target);
        parcel.writeString(discount_type);
        parcel.writeString(created_date);
        parcel.writeString(due_date);
        parcel.writeDouble(min_purchase);
        parcel.writeInt(quantity);
    }
}
