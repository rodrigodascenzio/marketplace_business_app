package com.nuppin.company.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;

public class Company implements Parcelable {

    @Expose
    private String name;
    @Expose
    private String company_id = "";
    @Expose
    private int max_radius_free;
    @Expose
    private String street;
    @Expose
    private String district;
    @Expose
    private String street_number;
    @Expose
    private String city;
    @Expose
    private String country_code;
    @Expose
    private String category_company_id;
    @Expose
    private String subcategory_company_id;
    @Expose
    private String user_id;
    @Expose
    private double latitude, longitude;
    @Expose
    private double rating;
    @Expose
    private int num_rating;
    @Expose
    private String document_type;
    @Expose
    private String document_number;
    @Expose
    private String model_type;
    @Expose
    private int delivery_type_value;
    @Expose
    private int is_delivery;
    @Expose
    private int is_local;
    @Expose
    private int is_pos;
    @Expose
    private String delivery_max_time;
    @Expose
    private String delivery_min_time;
    @Expose
    private double delivery_fixed_fee;
    @Expose
    private double delivery_variable_fee;
    @Expose
    private int max_radius;
    @Expose
    private String description;
    @Expose
    private String full_address;
    @Expose
    private String state_code;
    @Expose
    private String instagram;
    @Expose
    private String facebook;
    @Expose
    private String site;
    @Expose
    private int visibility;
    @Expose
    private double min_purchase;
    @Expose
    private String complement_address;

    //not exposed - not to be sent
    private String status;
    private int is_online;
    private String photo;
    private String validation;
    private String banner_photo;
    private String category_name;
    private String subcategory_name;
    private String plan_id;

    public String getBanner_photo() {
        return banner_photo;
    }

    public double getMin_purchase() {
        return min_purchase;
    }

    public void setMin_purchase(double min_purchase) {
        this.min_purchase = min_purchase;
    }

    public String getPlan_id() {
        return plan_id;
    }

    public String getValidation() {
        return validation;
    }

    public int getVisibility() {
        return visibility;
    }

    public String getState_code() {
        return state_code;
    }

    public void setState_code(String state_code) {
        this.state_code = state_code;
    }

    public String getInstagram() {
        return instagram;
    }

    public void setInstagram(String instagram) {
        this.instagram = instagram;
    }

    public String getFacebook() {
        return facebook;
    }

    public void setFacebook(String facebook) {
        this.facebook = facebook;
    }

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }

    public String getFull_address() {
        return full_address;
    }

    public void setFull_address(String full_address) {
        this.full_address = full_address;
    }

    public String getCategory_name() {
        return category_name;
    }

    public String getSubcategory_name() {
        return subcategory_name;
    }

    public void setCompany_id(String company_id) {
        this.company_id = company_id;
    }

    public String getCountry_code() {
        return country_code;
    }

    public void setCountry_code(String sto_country_cod) {
        this.country_code = sto_country_cod;
    }

    public String getphoto() {
        return photo;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setPlan_id(String plan_id) {
        this.plan_id = plan_id;
    }

    public int getIs_online() {
        return is_online;
    }

    public String getModel_type() {
        return model_type;
    }

    public void setModel_type(String model_type) {
        this.model_type = model_type;
    }

    public String getDocument_type() {
        return document_type;
    }

    public void setDocument_type(String document_type) {
        this.document_type = document_type;
    }

    public void setDocument_number(String document_number) {
        this.document_number = document_number;
    }

    public Company() {
    }


    public Company(String userId) {
        company_id = "";
        this.user_id = userId;
    }


    protected Company(Parcel in) {
        name = in.readString();
        company_id = in.readString();
        category_company_id = in.readString();
        subcategory_company_id = in.readString();
        city = in.readString();
        district = in.readString();
        street_number = in.readString();
        street = in.readString();
        latitude = in.readDouble();
        longitude = in.readDouble();
        rating = in.readDouble();
        num_rating = in.readInt();
        document_type = in.readString();
        document_number = in.readString();
        model_type = in.readString();
        delivery_type_value = in.readInt();
        is_delivery = in.readInt();
        is_local = in.readInt();
        is_pos = in.readInt();
        delivery_max_time = in.readString();
        delivery_min_time = in.readString();
        is_online = in.readInt();
        max_radius_free = in.readInt();
        delivery_fixed_fee = in.readDouble();
        delivery_variable_fee = in.readDouble();
        max_radius = in.readInt();
        plan_id = in.readString();
        status = in.readString();
        description = in.readString();
        category_name = in.readString();
        subcategory_name = in.readString();
    }

    public static final Creator<Company> CREATOR = new Creator<Company>() {
        @Override
        public Company createFromParcel(Parcel in) {
            return new Company(in);
        }

        @Override
        public Company[] newArray(int size) {
            return new Company[size];
        }
    };

    public String getCompany_id() {
        return company_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getStreet_number() {
        return street_number;
    }

    public void setStreet_number(String street_number) {
        this.street_number = street_number;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public double getRating() {
        return rating;
    }

    public int getNum_rating() {
        return num_rating;
    }

    public String getCategory_company_id() {
        return category_company_id;
    }

    public void setCategory_company_id(String category_company_id) {
        this.category_company_id = category_company_id;
    }

    public void setSubcategory_company_id(String subcategory_company_id) {
        this.subcategory_company_id = subcategory_company_id;
    }

    public int getDelivery_type_value() {
        return delivery_type_value;
    }

    public void setDelivery_type_value(int delivery_type_value) {
        this.delivery_type_value = delivery_type_value;
    }

    public int getIs_delivery() {
        return is_delivery;
    }

    public void setIs_delivery(int is_delivery) {
        this.is_delivery = is_delivery;
    }

    public int getIs_local() {
        return is_local;
    }

    public void setIs_local(int is_local) {
        this.is_local = is_local;
    }

    public int getIs_pos() {
        return is_pos;
    }

    public void setIs_pos(int is_pos) {
        this.is_pos = is_pos;
    }

    public String getDelivery_max_time() {
        return delivery_max_time;
    }

    public void setDelivery_max_time(String delivery_max_time) {
        this.delivery_max_time = delivery_max_time;
    }

    public String getDelivery_min_time() {
        return delivery_min_time;
    }

    public void setDelivery_min_time(String delivery_min_time) {
        this.delivery_min_time = delivery_min_time;
    }

    public void setValidation(String validation) {
        this.validation = validation;
    }

    public int getMax_radius_free() {
        return max_radius_free;
    }

    public void setMax_radius_free(int max_radius_free) {
        this.max_radius_free = max_radius_free;
    }

    public double getDelivery_variable_fee() {
        return delivery_variable_fee;
    }

    public void setDelivery_variable_fee(double delivery_variable_fee) {
        this.delivery_variable_fee = delivery_variable_fee;
    }

    public double getDelivery_fixed_fee() {
        return delivery_fixed_fee;
    }

    public void setDelivery_fixed_fee(double delivery_fixed_fee) {
        this.delivery_fixed_fee = delivery_fixed_fee;
    }

    public int getMax_radius() {
        return max_radius;
    }

    public String getComplement_address() {
        return complement_address;
    }

    public void setComplement_address(String complement_address) {
        this.complement_address = complement_address;
    }

    public void setMax_radius(int max_radius) {
        this.max_radius = max_radius;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(company_id);
        dest.writeString(category_company_id);
        dest.writeString(subcategory_company_id);
        dest.writeString(city);
        dest.writeString(street);
        dest.writeString(district);
        dest.writeString(street_number);
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);
        dest.writeInt(num_rating);
        dest.writeDouble(rating);
        dest.writeInt(is_delivery);
        dest.writeInt(is_local);
        dest.writeInt(is_pos);
        dest.writeString(delivery_max_time);
        dest.writeString(delivery_min_time);
        dest.writeString(document_type);
        dest.writeString(document_number);
        dest.writeInt(is_online);
        dest.writeString(plan_id);
        dest.writeString(status);
        dest.writeInt(max_radius_free);
        dest.writeDouble(delivery_fixed_fee);
        dest.writeDouble(delivery_variable_fee);
        dest.writeInt(max_radius);
        dest.writeString(description);
        dest.writeString(category_name);
        dest.writeString(subcategory_name);
    }

    public void setVisibility(int visibility) {
        this.visibility = visibility;
    }
}
