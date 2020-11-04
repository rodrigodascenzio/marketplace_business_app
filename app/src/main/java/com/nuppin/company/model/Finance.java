package com.nuppin.company.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Finance implements Parcelable {

    private String finance_id;
    private String title;
    private String description;
    private double amount;
    private String reference_date;
    private String company_id;
    private String photo;

    public Finance(){
        finance_id = "";
    }

    protected Finance(Parcel in) {
        finance_id = in.readString();
        title = in.readString();
        description = in.readString();
        amount = in.readDouble();
        reference_date = in.readString();
        company_id = in.readString();
        photo = in.readString();
    }

    public static final Creator<Finance> CREATOR = new Creator<Finance>() {
        @Override
        public Finance createFromParcel(Parcel in) {
            return new Finance(in);
        }

        @Override
        public Finance[] newArray(int size) {
            return new Finance[size];
        }
    };

    public String getFinance_id() {
        return finance_id;
    }

    public void setFinance_id(String finance_id) {
        this.finance_id = finance_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getReference_date() {
        return reference_date;
    }

    public void setReference_date(String reference_date) {
        this.reference_date = reference_date;
    }

    public String getCompany_id() {
        return company_id;
    }

    public void setCompany_id(String company_id) {
        this.company_id = company_id;
    }

    public String getphoto() {
        return photo;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(finance_id);
        parcel.writeString(title);
        parcel.writeString(description);
        parcel.writeDouble(amount);
        parcel.writeString(reference_date);
        parcel.writeString(company_id);
        parcel.writeString(photo);
    }
}
