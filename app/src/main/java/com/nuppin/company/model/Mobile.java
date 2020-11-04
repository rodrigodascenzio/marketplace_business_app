package com.nuppin.company.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Mobile implements Parcelable {
    private String mobile_id;
    private String company_id;
    private String start_date;
    private String end_date;
    private double latitude;
    private double longitude;

    private Mobile(Parcel in) {
        mobile_id = in.readString();
        company_id = in.readString();
        start_date = in.readString();
        end_date = in.readString();
        latitude = in.readDouble();
        longitude = in.readDouble();
    }

    public Mobile(){
        mobile_id = "";
    }

    public static final Creator<Mobile> CREATOR = new Creator<Mobile>() {
        @Override
        public Mobile createFromParcel(Parcel in) {
            return new Mobile(in);
        }

        @Override
        public Mobile[] newArray(int size) {
            return new Mobile[size];
        }
    };

    public String getMobile_id() {
        return mobile_id;
    }

    public void setMobile_id(String mobile_id) {
        this.mobile_id = mobile_id;
    }

    public String getCompany_id() {
        return company_id;
    }

    public void setCompany_id(String company_id) {
        this.company_id = company_id;
    }

    public String getStart_date() {
        return start_date;
    }

    public void setStart_date(String start_date) {
        this.start_date = start_date;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getEnd_date() {
        return end_date;
    }

    public void setEnd_date(String end_date) {
        this.end_date = end_date;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(mobile_id);
        parcel.writeString(company_id);
        parcel.writeString(start_date);
        parcel.writeString(end_date);
        parcel.writeDouble(latitude);
        parcel.writeDouble(longitude);
    }
}
