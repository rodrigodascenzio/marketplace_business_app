package com.nuppin.company.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Subcategory implements Parcelable {

    String name;
    String subcategory_company_id;


    protected Subcategory(Parcel in) {
        name = in.readString();
        subcategory_company_id = in.readString();
    }

    public static final Creator<Subcategory> CREATOR = new Creator<Subcategory>() {
        @Override
        public Subcategory createFromParcel(Parcel in) {
            return new Subcategory(in);
        }

        @Override
        public Subcategory[] newArray(int size) {
            return new Subcategory[size];
        }
    };

    public String getSubcategory_company_id() {
        return subcategory_company_id;
    }

    public void setSubcategory_company_id(String subcategory_company_id) {
        this.subcategory_company_id = subcategory_company_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeString(subcategory_company_id);
    }
}
