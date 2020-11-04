package com.nuppin.company.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Service implements Parcelable {

    private String service_id;
    private String company_id;
    private String name;
    private String description;
    private double price;
    private int duration;
    private int visibility;
    private int position;

    public Service(){
        service_id = "";
    }

    protected Service(Parcel in) {
        service_id = in.readString();
        company_id = in.readString();
        name = in.readString();
        description = in.readString();
        price = in.readDouble();
        duration = in.readInt();
    }

    public static final Creator<Service> CREATOR = new Creator<Service>() {
        @Override
        public Service createFromParcel(Parcel in) {
            return new Service(in);
        }

        @Override
        public Service[] newArray(int size) {
            return new Service[size];
        }
    };

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public void setCompany_id(String company_id) {
        this.company_id = company_id;
    }

    public int getVisibility() {
        return visibility;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPreco(double preco) {
        this.price = preco;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getService_id() {
        return service_id;
    }

    public String getCompany_id() {
        return company_id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public double getPreco() {
        return price;
    }

    public int getDuration() {
        return duration;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(service_id);
        parcel.writeString(company_id);
        parcel.writeString(name);
        parcel.writeString(description);
        parcel.writeDouble(price);
        parcel.writeInt(duration);
    }
}
