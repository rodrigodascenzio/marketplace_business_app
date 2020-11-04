package com.nuppin.company.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Employee implements Parcelable {

    private String employee_id;
    private String company_id;
    private String user_id;
    private String start_time;
    private String end_time;
    private String title;
    private String user_name;
    private String role;

    protected Employee(Parcel in) {
        employee_id = in.readString();
        company_id = in.readString();
        user_id = in.readString();
        start_time = in.readString();
        end_time = in.readString();
        user_name = in.readString();
        title = in.readString();
    }

    public static final Creator<Employee> CREATOR = new Creator<Employee>() {
        @Override
        public Employee createFromParcel(Parcel in) {
            return new Employee(in);
        }

        @Override
        public Employee[] newArray(int size) {
            return new Employee[size];
        }
    };

    public Employee(String employee_id){
        this.employee_id = employee_id;
    }

    public Employee(String user_id, String company_id, String role) {
        this.employee_id = "";
        this.user_id = user_id;
        this.company_id = company_id;
        this.user_name = "";
        this.role = role;
    }

    public String getRole() {
        return role;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public void setEmployee_id(String employee_id) {
        this.employee_id = employee_id;
    }

    public void setCompany_id(String company_id) {
        this.company_id = company_id;
    }

    public void setStart_time(String start_time) {
        this.start_time = start_time;
    }

    public void setEnd_time(String end_time) {
        this.end_time = end_time;
    }

    public String getEmployee_id() {
        return employee_id;
    }

    public String getCompany_id() {
        return company_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public String getStart_time() {
        return start_time;
    }

    public String getEnd_time() {
        return end_time;
    }

    public String getUser_name() {
        return user_name;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(employee_id);
        parcel.writeString(company_id);
        parcel.writeString(user_id);
        parcel.writeString(start_time);
        parcel.writeString(end_time);
        parcel.writeString(user_name);
        parcel.writeString(title);
    }
}
