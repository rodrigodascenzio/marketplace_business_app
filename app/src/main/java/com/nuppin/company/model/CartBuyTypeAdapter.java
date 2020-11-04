package com.nuppin.company.model;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.nuppin.company.Util.AppConstants;
import com.nuppin.company.Util.UtilShaPre;

import java.io.IOException;

public class CartBuyTypeAdapter extends TypeAdapter<CartCompany> {

    Context ctx;

    public CartBuyTypeAdapter(Context context) {
        ctx = context;
    }

    @Override
    public void write(JsonWriter out, CartCompany value) throws IOException {
        out.beginObject();
        out.name("order_id").value("");
        out.name("user_name").value(value.getUser_name());
        out.name("company_id").value(value.getCompany_id());
        if (value.getCart_order_type().equals("delivery")) {
            out.name("address").value(value.getUser_address());
            out.name("latitude").value(value.getUser_latitude());
            out.name("longitude").value(value.getUser_longitude());
        } else {
            out.name("address").value(value.getFull_address());
            out.name("latitude").value(UtilShaPre.getDefaultsString(AppConstants.LATITUDE, ctx));
            out.name("longitude").value(UtilShaPre.getDefaultsString(AppConstants.LONGITUDE, ctx));
        }
        out.name("user_id").value(UtilShaPre.getDefaultsString(AppConstants.USER_ID, ctx));
        out.name("discount_amount").value(value.getCart_discount_value());
        out.name("note").value(value.getCart_note());
        out.name("type").value(value.getCart_order_type());
        out.name("payment_method").value(value.getCart_payment_type());
        out.endObject();
    }

    @Override
    public CartCompany read(JsonReader in) throws IOException {

        return null;
    }
}