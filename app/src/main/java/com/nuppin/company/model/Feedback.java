package com.nuppin.company.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Feedback implements Parcelable {
    private String user_id;
    private String message;
    private int nps;


    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getNps() {
        return nps;
    }

    public void setNps(int nps) {
        this.nps = nps;
    }

    public Feedback(){}

    private Feedback(Parcel in) {
        user_id = in.readString();
        message = in.readString();
        nps = in.readInt();
    }

    public static final Creator<Feedback> CREATOR = new Creator<Feedback>() {
        @Override
        public Feedback createFromParcel(Parcel in) {
            return new Feedback(in);
        }

        @Override
        public Feedback[] newArray(int size) {
            return new Feedback[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(user_id);
        parcel.writeString(message);
        parcel.writeInt(nps);
    }
}
