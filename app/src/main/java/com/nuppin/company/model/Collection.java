package com.nuppin.company.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;

public class Collection implements Parcelable {

    @Expose
    private String collection_id = "";
    @Expose
    private String company_id;
    @Expose
    private String name;
    @Expose
    private String external_code;
    @Expose
    private String description;
    @Expose
    private int min_quantity;
    @Expose
    private int max_quantity;
    @Expose
    private int is_free;
    private String product_id;
    private int has_warning;
    private int is_empty;

    public Collection(){

    }

    protected Collection(Parcel in) {
        collection_id = in.readString();
        company_id = in.readString();
        name = in.readString();
        product_id = in.readString();
        external_code = in.readString();
        description = in.readString();
        max_quantity = in.readInt();
        min_quantity = in.readInt();
        is_free = in.readInt();
        has_warning = in.readInt();
    }

    public String getProduct_id() {
        return product_id;
    }

    public void setProduct_id(String product_id) {
        this.product_id = product_id;
    }

    public static final Creator<Collection> CREATOR = new Creator<Collection>() {
        @Override
        public Collection createFromParcel(Parcel in) {
            return new Collection(in);
        }

        @Override
        public Collection[] newArray(int size) {
            return new Collection[size];
        }
    };

    public int getMin_quantity() {
        return min_quantity;
    }

    public void setMin_quantity(int min_quantity) {
        this.min_quantity = min_quantity;
    }

    public int getMax_quantity() {
        return max_quantity;
    }

    public void setMax_quantity(int max_quantity) {
        this.max_quantity = max_quantity;
    }

    public int getIs_free() {
        return is_free;
    }

    public void setIs_free(int is_free) {
        this.is_free = is_free;
    }

    public String getExternal_code() {
        return external_code;
    }

    public void setExternal_code(String external_code) {
        this.external_code = external_code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCollection_id() {
        return collection_id;
    }

    public void setCollection_id(String collection_id) {
        this.collection_id = collection_id;
    }

    public String getCompany_id() {
        return company_id;
    }

    public void setCompany_id(String company_id) {
        this.company_id = company_id;
    }

    public int getHas_warning() {
        return has_warning;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getIs_empty() {
        return is_empty;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(collection_id);
        parcel.writeString(company_id);
        parcel.writeString(name);
        parcel.writeString(external_code);
        parcel.writeString(description);
        parcel.writeString(product_id);
        parcel.writeInt(max_quantity);
        parcel.writeInt(min_quantity);
        parcel.writeInt(is_free);
        parcel.writeInt(has_warning);
    }
}
