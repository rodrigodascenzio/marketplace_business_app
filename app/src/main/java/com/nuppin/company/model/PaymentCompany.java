package com.nuppin.company.model;

import android.os.Parcel;
import android.os.Parcelable;

public class PaymentCompany implements Parcelable {

    private String payment_id;
    private String name;
    private String is_checked;
    private String company_id;

    public PaymentCompany() {

    }


    protected PaymentCompany(Parcel in) {
        payment_id = in.readString();
        name = in.readString();
        is_checked = in.readString();
        company_id = in.readString();
    }

    public String getCompany_id() {
        return company_id;
    }

    public void setCompany_id(String company_id) {
        this.company_id = company_id;
    }

    public String getIs_checked() {
        return is_checked;
    }

    public void setMeiopag_ischecked(String meiopag_checked) {
        this.is_checked = meiopag_checked;
    }

    public static final Creator<PaymentCompany> CREATOR = new Creator<PaymentCompany>() {
        @Override
        public PaymentCompany createFromParcel(Parcel in) {
            return new PaymentCompany(in);
        }

        @Override
        public PaymentCompany[] newArray(int size) {
            return new PaymentCompany[size];
        }
    };

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPayment_id() {
        return payment_id;
    }

    public void setPayment_id(String payment_id) {
        this.payment_id = payment_id;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(payment_id);
        parcel.writeString(name);
        parcel.writeString(is_checked);
        parcel.writeString(company_id);
    }
}
