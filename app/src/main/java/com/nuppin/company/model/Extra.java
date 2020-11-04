package com.nuppin.company.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;

public class Extra implements Parcelable {

    @Expose
    private String extra_id = "";
    @Expose
    private String name;
    @Expose
    private double price;
    @Expose
    private String company_id;
    @Expose
    private String description;

    private String collection_id;

    //for pos
    private int quantity;

    public Extra(){}

    protected Extra(Parcel in) {
        extra_id = in.readString();
        name = in.readString();
        price = in.readDouble();
        collection_id = in.readString();
        company_id = in.readString();
        description = in.readString();
    }

    public static final Creator<Extra> CREATOR = new Creator<Extra>() {
        @Override
        public Extra createFromParcel(Parcel in) {
            return new Extra(in);
        }

        @Override
        public Extra[] newArray(int size) {
            return new Extra[size];
        }
    };

    public String getExtra_id() {
        return extra_id;
    }

    public String getCompany_id() {
        return company_id;
    }

    public void setCompany_id(String company_id) {
        this.company_id = company_id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setExtra_id(String extra_id) {
        this.extra_id = extra_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double ei_double) {
        this.price = ei_double;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(extra_id);
        parcel.writeString(name);
        parcel.writeDouble(price);
        parcel.writeString(collection_id);
        parcel.writeString(company_id);
        parcel.writeString(description);
    }
}
