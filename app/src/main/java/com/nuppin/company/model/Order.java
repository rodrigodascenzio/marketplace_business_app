package com.nuppin.company.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;

import java.util.List;

public class Order implements Parcelable {

    @Expose
    private String order_id = "";
    @Expose
    private String company_id;
    @Expose
    private String company_name;
    @Expose
    private String address;
    @Expose
    private String latitude;
    @Expose
    private String longitude;
    @Expose
    private String user_name;
    @Expose
    private String user_id;
    @Expose
    private double delivery_amount;
    @Expose
    private String note;
    @Expose
    private String status;
    @Expose
    private String payment_method;
    @Expose
    private double total_amount;
    @Expose
    private double distance;
    @Expose
    private double user_rating;
    @Expose
    private int user_num_rating;
    @Expose
    private double subtotal_amount;
    @Expose
    private double discount_amount;
    @Expose
    private String source;

    private int is_chat_available;
    private String type;
    private String created_date;
    private int rating;
    private String rating_note;
    private List<OrderItem> order_item;

    //variavel that is receive from api when ConnectApi.COMPANY_INVOICE_DETAIL_REQUEST is called
    private double invoice_fee;

    public Order() {
    }

    protected Order(Parcel in) {
        order_id = in.readString();
        company_id = in.readString();
        company_name = in.readString();
        user_name = in.readString();
        user_id = in.readString();
        delivery_amount = in.readDouble();
        note = in.readString();
        status = in.readString();
        payment_method = in.readString();
        total_amount = in.readDouble();
        address = in.readString();
        distance = in.readDouble();
        user_rating = in.readDouble();
        user_num_rating = in.readInt();
    }

    public static final Creator<Order> CREATOR = new Creator<Order>() {
        @Override
        public Order createFromParcel(Parcel in) {
            return new Order(in);
        }

        @Override
        public Order[] newArray(int size) {
            return new Order[size];
        }
    };

    public String getSource() {
        return source;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public int getIs_chat_available() {
        return is_chat_available;
    }

    public int getRating() {
        return rating;
    }

    public String getRating_note() {
        return rating_note;
    }

    public String getCreated_date() {
        return created_date;
    }

    public List<OrderItem> getOrder_item() {
        return order_item;
    }

    public String getType() {
        return type;
    }

    public double getSubtotal_amount() {
        return subtotal_amount;
    }

    public double getDiscount_amount() {
        return discount_amount;
    }

    public void setUser_rating(double user_rating) {
        this.user_rating = user_rating;
    }

    public String getCompany_name() {
        return company_name;
    }

    public String getAddress() {
        return address;
    }

    public double getDistance() {
        return distance;
    }

    public double getUser_rating() {
        return user_rating;
    }

    public int getUser_num_rating() {
        return user_num_rating;
    }

    public void setCompany_name(String company_name) {
        this.company_name = company_name;
    }

    public String getOrder_id() {
        return order_id;
    }

    public void setOrder_id(String order_id) {
        this.order_id = order_id;
    }

    public String getCompany_id() {
        return company_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public double getDelivery_amount() {
        return delivery_amount;
    }

    public String getNote() {
        return note;
    }

    public String getStatus() {
        return status;
    }

    public String getPayment_method() {
        return payment_method;
    }

    public double getTotal_amount() {
        return total_amount;
    }

    public String getUser_name() {
        return user_name;
    }

    public double getInvoice_fee() {
        return invoice_fee;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(order_id);
        parcel.writeString(company_id);
        parcel.writeString(company_name);
        parcel.writeString(user_name);
        parcel.writeString(user_id);
        parcel.writeDouble(delivery_amount);
        parcel.writeString(note);
        parcel.writeString(status);
        parcel.writeString(payment_method);
        parcel.writeDouble(total_amount);
        parcel.writeString(address);
        parcel.writeDouble(distance);
        parcel.writeDouble(user_rating);
        parcel.writeInt(user_num_rating);
    }
}