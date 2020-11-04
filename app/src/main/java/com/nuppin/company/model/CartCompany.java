package com.nuppin.company.model;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.gson.annotations.Expose;
import java.util.List;

public class CartCompany implements Parcelable {

    @Expose
    private String company_id;
    @Expose
    private String user_address;
    @Expose
    private double user_latitude;
    @Expose
    private double user_longitude;
    @Expose
    private String name;
    @Expose
    private double min_purchase;
    @Expose
    private double delivery_fixed_fee;
    @Expose
    private String category_company_id;
    @Expose
    private String subcategory_company_id;
    @Expose
    private double subtotal_amount;
    @Expose
    private int is_available;
    @Expose
    private double distance;
    @Expose
    private int delivery_type_value;
    @Expose
    private String delivery_min_time;
    @Expose
    private String delivery_max_time;
    @Expose
    private double delivery_variable_fee;
    @Expose
    private int max_radius_free;
    @Expose
    private int max_radius;
    @Expose
    private String model_type;
    @Expose
    private int is_delivery;
    @Expose
    private int is_local;
    @Expose
    private int is_pos;
    @Expose
    private int is_online;
    @Expose
    private String photo;
    @Expose
    private String full_address;
    @Expose
    private double latitude;
    @Expose
    private double longitude;
    @Expose
    private String user_id;
    @Expose
    private double rating;
    @Expose
    private int num_rating;
    @Expose
    private String user_name;
    @Expose
    private List<CartProduct> product;

    @Expose
    private double cartValorEntrega;
    @Expose
    private double cart_total_value;
    @Expose
    private String cart_order_type;
    @Expose
    private String cart_payment_type;
    @Expose
    private String cart_payment_id;
    @Expose
    private String coupon_id;
    @Expose
    private double coupon_value;
    @Expose
    private double coupon_min_purchase;
    @Expose
    private double cart_discount_value;
    @Expose
    private String coupon_discount_type;
    @Expose
    private String cart_note;


    public double getCoupon_min_purchase() {
        return coupon_min_purchase;
    }

    public void setCoupon_min_purchase(double coupon_min_purchase) {
        this.coupon_min_purchase = coupon_min_purchase;
    }

    public int getIs_pos() {
        return is_pos;
    }

    public double getRating() {
        return rating;
    }

    public int getCart_sto_numRating() {
        return num_rating;
    }

    public String getUser_id() {
        return user_id;
    }

    public String getFull_address() {
        return full_address;
    }

    public String getCart_payment_id() {
        return cart_payment_id;
    }

    public void setCart_payment_id(String cart_payment_id) {
        this.cart_payment_id = cart_payment_id;
    }

    public double getCart_total_value() {
        return cart_total_value;
    }

    public void setCart_total_value(double cart_total_value) {
        this.cart_total_value = cart_total_value;
    }

    public String getphoto() {
        return photo;
    }

    public String getCart_note() {
        return cart_note;
    }

    public void setCart_note(String cart_note) {
        this.cart_note = cart_note;
    }

    public String getSubcategory_company_id() {
        return subcategory_company_id;
    }

    public int getDelivery_type_value() {
        return delivery_type_value;
    }

    public String getDelivery_min_time() {
        return delivery_min_time;
    }

    public String getDelivery_max_time() {
        return delivery_max_time;
    }

    public double getDelivery_variable_fee() {
        return delivery_variable_fee;
    }

    public int getMax_radius_free() {
        return max_radius_free;
    }

    public int getCart_sto_max_rio() {
        return max_radius;
    }

    public String getModel_type() {
        return model_type;
    }

    public int getIs_delivery() {
        return is_delivery;
    }

    public int getIs_local() {
        return is_local;
    }

    public double getDistance() {
        return distance;
    }

    public String getUser_address() {
        return user_address;
    }

    public String getCategory_company_id() {
        return category_company_id;
    }

    public int getIs_available() {
        return is_available;
    }



    public String getCompany_id() {
        return company_id;
    }

    public String getName() {
        return name;
    }

    public double getMin_purchase() {
        return min_purchase;
    }

    public double getCartValorEntrega() {
        return cartValorEntrega;
    }

    public String getCart_payment_type() {
        return cart_payment_type;
    }

    public int getIs_online() {
        return is_online;
    }

    public void setCartValorEntrega(double cartValorEntrega) {
        this.cartValorEntrega = cartValorEntrega;
    }

    public void setCart_payment_type(String cart_payment_type) {
        this.cart_payment_type = cart_payment_type;
    }

    public double getSubtotal_amount() {
        return subtotal_amount;
    }

    public List<CartProduct> getProduct() {
        return product;
    }

    public String getCupom_id() {
        return coupon_id;
    }

    public void setCupom_id(String coupon_id) {
        this.coupon_id = coupon_id;
    }

    public double getCupom_valor() {
        return coupon_value;
    }

    public void setCupom_valor(double coupon_valor) {
        this.coupon_value = coupon_valor;
    }

    public String getCupom_tipo_desconto() {
        return coupon_discount_type;
    }

    public double getCart_discount_value() {
        return cart_discount_value;
    }

    public void setCart_discount_value(double cart_discount_value) {
        this.cart_discount_value = cart_discount_value;
    }


    public void setUser_address(String user_address) {
        this.user_address = user_address;
    }

    public void setUser_latitude(double user_latitude) {
        this.user_latitude = user_latitude;
    }

    public void setUser_longitude(double user_longitude) {
        this.user_longitude = user_longitude;
    }

    public void setCupom_tipo_desconto(String coupon_tipo_desconto) {
        this.coupon_discount_type = coupon_tipo_desconto;
    }

    public String getCart_order_type() {
        return cart_order_type;
    }

    public void setCart_order_type(String cart_order_type) {
        this.cart_order_type = cart_order_type;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public double getDelivery_fixed_fee() {
        return delivery_fixed_fee;
    }

    public void setProduct(List<CartProduct> product) {
        this.product = product;
    }


    public double getUser_latitude() {
        return user_latitude;
    }

    public double getUser_longitude() {
        return user_longitude;
    }

    protected CartCompany(Parcel in) {
        company_id = in.readString();
        name = in.readString();
        min_purchase = in.readDouble();
        cartValorEntrega = in.readDouble();
        subtotal_amount = in.readDouble();
        is_available = in.readInt();
        product = in.createTypedArrayList(CartProduct.CREATOR);
        category_company_id = in.readString();
        subcategory_company_id = in.readString();
        user_address = in.readString();
        distance = in.readDouble();
        delivery_type_value = in.readInt();
        delivery_min_time = in.readString();
        delivery_max_time = in.readString();
        delivery_variable_fee = in.readDouble();
        max_radius_free = in.readInt();
        max_radius = in.readInt();
        model_type = in.readString();
        is_delivery = in.readInt();
        is_local = in.readInt();
        cart_payment_type = in.readString();
        cart_payment_id = in.readString();
        coupon_id = in.readString();
        coupon_discount_type = in.readString();
        coupon_value = in.readDouble();
        cart_discount_value = in.readDouble();
        delivery_fixed_fee = in.readDouble();
        cart_note = in.readString();
        is_online = in.readInt();
        cart_order_type = in.readString();
    }

    public static final Creator<CartCompany> CREATOR = new Creator<CartCompany>() {
        @Override
        public CartCompany createFromParcel(Parcel in) {
            return new CartCompany(in);
        }

        @Override
        public CartCompany[] newArray(int size) {
            return new CartCompany[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(company_id);
        parcel.writeString(name);
        parcel.writeDouble(min_purchase);
        parcel.writeDouble(cartValorEntrega);
        parcel.writeDouble(subtotal_amount);
        parcel.writeInt(is_available);
        parcel.writeTypedList(product);
        parcel.writeString(category_company_id);
        parcel.writeString(subcategory_company_id);
        parcel.writeString(user_address);
        parcel.writeDouble(distance);
        parcel.writeInt(delivery_type_value);
        parcel.writeString(delivery_min_time);
        parcel.writeString(delivery_max_time);
        parcel.writeDouble(delivery_variable_fee);
        parcel.writeInt(max_radius_free);
        parcel.writeInt(max_radius);
        parcel.writeString(model_type);
        parcel.writeInt(is_delivery);
        parcel.writeInt(is_local);
        parcel.writeString(cart_payment_type);
        parcel.writeString(cart_payment_id);
        parcel.writeString(coupon_id);
        parcel.writeString(coupon_discount_type);
        parcel.writeDouble(coupon_value);
        parcel.writeDouble(cart_discount_value);
        parcel.writeDouble(delivery_fixed_fee);
        parcel.writeString(cart_note);
        parcel.writeInt(is_online);
        parcel.writeString(cart_order_type);
    }
}