package com.nuppin.company.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;

public class Scheduling implements Parcelable {

    @Expose
    private String scheduling_id = "";
    @Expose
    private String company_id;
    @Expose
    private String user_id;
    @Expose
    private String service_id;
    @Expose
    private String employee_id;
    @Expose
    private String start_time;
    @Expose
    private String end_time;
    @Expose
    private String status;
    @Expose
    private String service_name;
    @Expose
    private String user_name;
    @Expose
    private String employee_name;
    private int rating;
    @Expose
    private double subtotal_amount;
    @Expose
    private int service_duration;
    @Expose
    private String note;
    @Expose
    private String source;
    @Expose
    private double latitude;
    @Expose
    private double longitude;
    @Expose
    private String address;
    @Expose
    private String type;
    @Expose
    private String company_name;
    private String created_date;
    private String start_date;
    private double discount_amount;
    @Expose
    private double total_amount;
    @Expose
    private String payment_method;
    private boolean expired;
    private String rating_note;
    private double user_rating;
    private int user_num_rating;
    private int is_chat_available;

    public double getUser_rating() {
        return user_rating;
    }

    public int getUser_num_rating() {
        return user_num_rating;
    }

    public String getRating_note() {
        return rating_note;
    }

    public void setRating_note(String rating_note) {
        this.rating_note = rating_note;
    }

    public int getIs_chat_available() {
        return is_chat_available;
    }

    public String getCompany_name() {
        return company_name;
    }

    public boolean getExpired() {
        return expired;
    }

    public double getDiscount_amount() {
        return discount_amount;
    }

    public double getTotal_amount() {
        return total_amount;
    }

    public String getPayment_method() {
        return payment_method;
    }

    public String getScheduling_id() {
        return scheduling_id;
    }

    public String getCompany_id() {
        return company_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public String getService_id() {
        return service_id;
    }

    public String getEmployee_id() {
        return employee_id;
    }

    public String getStart_time() {
        return start_time;
    }

    public String getEnd_time() {
        return end_time;
    }

    public String getStatus() {
        return status;
    }

    public String getService_name() {
        return service_name;
    }

    public String getUser_name() {
        return user_name;
    }

    public String getEmployee_name() {
        return employee_name;
    }

    public int getRating() {
        return rating;
    }

    public double getSubtotal_amount() {
        return subtotal_amount;
    }

    public int getService_duration() {
        return service_duration;
    }

    public String getNote() {
        return note;
    }

    public String getCreated_date() {
        return created_date;
    }

    public String getStart_date() {
        return start_date;
    }

    public String getSource() {
        return source;
    }

    public void setCompany_id(String company_id) {
        this.company_id = company_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public void setService_id(String service_id) {
        this.service_id = service_id;
    }

    public void setEmployee_id(String employee_id) {
        this.employee_id = employee_id;
    }

    public void setStart_time(String start_time) {
        this.start_time = start_time;
    }

    public void setEnd_time(String end_time) {
        this.end_time = end_time;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setService_name(String service_name) {
        this.service_name = service_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public void setEmployee_name(String employee_name) {
        this.employee_name = employee_name;
    }

    public void setSubtotal_amount(double subtotal_amount) {
        this.subtotal_amount = subtotal_amount;
    }

    public void setService_duration(int service_duration) {
        this.service_duration = service_duration;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public void setStart_date(String start_date) {
        this.start_date = start_date;
    }

    public void setTotal_amount(double total_amount) {
        this.total_amount = total_amount;
    }

    public void setPayment_method(String payment_method) {
        this.payment_method = payment_method;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setCompany_name(String company_name) {
        this.company_name = company_name;
    }

    public void setType(String type) {
        this.type = type;
    }


    public void setScheduling_id(String scheduling_id) {
        this.scheduling_id = scheduling_id;
    }

    protected Scheduling(Parcel in) {
        scheduling_id = in.readString();
        company_id = in.readString();
        user_id = in.readString();
        service_id = in.readString();
        employee_id = in.readString();
        start_time = in.readString();
        end_time = in.readString();
        status = in.readString();
        service_name = in.readString();
        user_name = in.readString();
        employee_name = in.readString();
        rating = in.readInt();
        service_duration = in.readInt();
        note = in.readString();
        subtotal_amount = in.readDouble();
    }









    public static final Creator<Scheduling> CREATOR = new Creator<Scheduling>() {
        @Override
        public Scheduling createFromParcel(Parcel in) {
            return new Scheduling(in);
        }

        @Override
        public Scheduling[] newArray(int size) {
            return new Scheduling[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(scheduling_id);
        parcel.writeString(company_id);
        parcel.writeString(user_id);
        parcel.writeString(service_id);
        parcel.writeString(employee_id);
        parcel.writeString(start_time);
        parcel.writeString(end_time);
        parcel.writeString(status);
        parcel.writeString(service_name);
        parcel.writeString(user_name);
        parcel.writeString(employee_name);
        parcel.writeInt(rating);
        parcel.writeInt(service_duration);
        parcel.writeString(note);
        parcel.writeDouble(subtotal_amount);
    }
}
