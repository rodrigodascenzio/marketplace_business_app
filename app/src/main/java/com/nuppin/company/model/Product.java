package com.nuppin.company.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;

public class Product implements Parcelable {

    @Expose
    private String product_id = "";
    @Expose
    private String company_id;
    @Expose
    private String name;
    @Expose
    private String description;
    @Expose
    private double price;
    @Expose
    private int stock_quantity;
    @Expose
    private int is_stock;
    @Expose
    private int is_multi_stock;
    @Expose
    private int multi_stock_quantity;
    @Expose
    private String external_code;
    private String photo;
    private int visibility;
    private int position;

    //when has this product in cart
    private int cart_quantity;
    private String cart_note;

    public Product() {
    }

    protected Product(Parcel in) {
        product_id = in.readString();
        company_id = in.readString();
        name = in.readString();
        description = in.readString();
        price = in.readDouble();
        stock_quantity = in.readInt();
        is_stock = in.readInt();
        is_multi_stock = in.readInt();
        multi_stock_quantity = in.readInt();
    }

    public static final Creator<Product> CREATOR = new Creator<Product>() {
        @Override
        public Product createFromParcel(Parcel in) {
            return new Product(in);
        }

        @Override
        public Product[] newArray(int size) {
            return new Product[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(product_id);
        dest.writeString(company_id);
        dest.writeString(name);
        dest.writeString(description);
        dest.writeDouble(price);
        dest.writeInt(stock_quantity);
        dest.writeInt(is_stock);
        dest.writeInt(is_multi_stock);
        dest.writeInt(multi_stock_quantity);
    }

    public int getCart_quantity() {
        return cart_quantity;
    }

    public String getCart_note() {
        return cart_note;
    }

    public void setIs_multi_stock(int is_multi_stock) {
        this.is_multi_stock = is_multi_stock;
    }

    public int getIs_multi_stock() {
        return is_multi_stock;
    }

    public String getExternal_code() {
        return external_code;
    }

    public void setExternal_code(String external_code) {
        this.external_code = external_code;
    }

    public int getVisibility() {
        return visibility;
    }

    public String getProduct_id() {
        return product_id;
    }

    public void setProduct_id(String product_id) {
        this.product_id = product_id;
    }

    public String getCompany_id() {
        return company_id;
    }

    public void setCompany_id(String company_id) {
        this.company_id = company_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getStock_quantity() {
        return stock_quantity;
    }

    public void setStock_quantity(int stock_quantity) {
        this.stock_quantity = stock_quantity;
    }

    public int getIs_stock() {
        return is_stock;
    }

    public void setIs_stock(int is_stock) {
        this.is_stock = is_stock;
    }

    public String getphoto() {
        return photo;
    }

    public void setphoto(String photo) {
        this.photo = photo;
    }

    public int getMulti_stock_quantity() {
        return multi_stock_quantity;
    }

    public void setMulti_stock_quantity(int multi_stock_quantity) {
        this.multi_stock_quantity = multi_stock_quantity;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }
}