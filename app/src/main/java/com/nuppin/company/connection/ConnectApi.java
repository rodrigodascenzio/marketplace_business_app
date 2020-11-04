package com.nuppin.company.connection;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.nuppin.company.BuildConfig;
import com.nuppin.company.Loja.Login.SplashScreen;
import com.nuppin.company.R;
import com.nuppin.company.Util.AppConstants;
import com.nuppin.company.model.CollectionExtra;
import com.nuppin.company.model.Company;
import com.nuppin.company.model.Collection;
import com.nuppin.company.model.Employee;
import com.nuppin.company.model.Extra;
import com.nuppin.company.model.Feedback;
import com.nuppin.company.model.Invoice;
import com.nuppin.company.model.Order;
import com.nuppin.company.model.Product;
import com.nuppin.company.model.ProductCollection;
import com.nuppin.company.model.Size;
import com.nuppin.company.model.Scheduling;
import com.nuppin.company.model.Finance;
import com.nuppin.company.model.Chat;
import com.nuppin.company.model.Coupon;
import com.nuppin.company.model.TempEmail;
import com.nuppin.company.model.ServiceEmployee;
import com.nuppin.company.model.OpeningHours;
import com.nuppin.company.model.PaymentCompany;
import com.nuppin.company.model.Service;
import com.nuppin.company.model.TempSms;
import com.nuppin.company.model.Mobile;
import com.nuppin.company.model.User;
import com.nuppin.company.Util.UtilShaPre;
import com.nuppin.company.model.Cart;
import com.nuppin.company.model.CartBuyTypeAdapter;
import com.nuppin.company.model.CartCompany;
import com.nuppin.company.model.CartProduct;

import java.util.List;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ConnectApi {

    //Servidor AWS:
    public static final String API_VERSION = "v4";
    public static final String SERVIDOR = "https://api.nuppin.com/"+API_VERSION;

    public static final String SERVICE = ConnectApi.SERVIDOR + "/service";
    public static final String SERVICE_COMPANY = ConnectApi.SERVIDOR + "/service/company";
    public static final String UPDATE_STATUS_SCHEDULING = ConnectApi.SERVIDOR + "/scheduling/company/status";
    public static final String SCHEDULING_HISTORICO = ConnectApi.SERVIDOR + "/scheduling/history";
    public static final String SCHEDULING_AVALIACOES = ConnectApi.SERVIDOR + "/scheduling/rating";
    public static final String SERVICE_POSITION = ConnectApi.SERVIDOR + "/service/position";

    public static final String COMPANY_NEW = ConnectApi.SERVIDOR + "/company/new";
    public static final String COMPANY = ConnectApi.SERVIDOR + "/company";
    public static final String CHAT = SERVIDOR + "/company/chat";
    public static final String MOBILE = ConnectApi.SERVIDOR + "/company/mobile";
    public static final String MOBILE_UPDATE = ConnectApi.SERVIDOR + "/company/mobile/update";
    public static final String COMPANY_MAIN = ConnectApi.SERVIDOR + "/company/user/main";
    public static final String COMPANY_USER = ConnectApi.SERVIDOR + "/company/user";
    public static final String COMPANY_EMPLOYEE = ConnectApi.SERVIDOR + "/company/employee";
    public static final String COMPANY_PLAN = ConnectApi.SERVIDOR + "/company/plan";
    public static final String COMPANY_PAYMENT = ConnectApi.SERVIDOR + "/company/payment";
    public static final String COMPANY_PAYMENT_CHECK = ConnectApi.SERVIDOR + "/company/payment/checked";
    public static final String COMPANY_INVOICES = ConnectApi.SERVIDOR + "/company/invoice";
    public static final String COMPANY_INVOICE_DETAIL = ConnectApi.SERVIDOR + "/company/invoice/detail";
    public static final String COMPANY_UPDATE_BOLETO = ConnectApi.SERVIDOR + "/company/boleto/update";
    public static final String COMPANY_INVOICE_DETAIL_ORDER = ConnectApi.SERVIDOR + "/company/invoice/detail/order";
    public static final String COMPANY_INVOICE_DETAIL_SCHEDULING = ConnectApi.SERVIDOR + "/company/invoice/detail/scheduling";
    public static final String COMPANY_SCHEDULE = ConnectApi.SERVIDOR + "/company/schedule";
    public static final String COMPANY_COUPON = ConnectApi.SERVIDOR + "/company/coupon";
    public static final String COMPANY_CUPOM_LAT_LON = ConnectApi.SERVIDOR + "/company/coupon/near";
    public static final String COMPANY_UNDEFINED_SCHEDULE = ConnectApi.SERVIDOR + "/company/schedule/undefined";
    public static final String COMPANY_SUBCATEGORY = ConnectApi.SERVIDOR + "/company/register/subcategory";
    public static final String DATA_COMPANY = ConnectApi.SERVIDOR + "/company/data";
    public static final String DATA_COMPANY_SCHEDULING = ConnectApi.SERVIDOR + "/company/data/scheduling";
    public static final String CASHFLOW_COMPANY = ConnectApi.SERVIDOR + "/company/cashflow";
    public static final String SCHEDULING = ConnectApi.SERVIDOR + "/company/scheduling";
    public static final String CASH_FLOW = ConnectApi.SERVIDOR + "/company/cashflow";
    public static final String COMPANY_VERIFY_EXIST_CPFCNPJ = ConnectApi.SERVIDOR + "/company/checkcpfcnpj";
    public static final String COMPANY_VISIBILITY = ConnectApi.SERVIDOR + "/company/visibility";
    public static final String VALIDATION = ConnectApi.SERVIDOR + "/company/validation";

    public static final String USERS = ConnectApi.SERVIDOR + "/users";
    public static final String USERS_LOGOUT = ConnectApi.SERVIDOR + "/users/logout";
    public static final String USERS_REFRESH_TOKEN = ConnectApi.SERVIDOR + "/users/refreshtoken/newaccesstoken";
    public static final String USER_COMPANY_TOKEN = ConnectApi.SERVIDOR + "/users/company/registerTokenNotification";
    public static final String USER_COMPANY_TEMP_TOKEN = ConnectApi.SERVIDOR + "/company/tempNotification";
    public static final String VERIFY_CODE_TO_CHANGE_EMAIL = ConnectApi.SERVIDOR + "/users/changeemail/verifycode";
    public static final String VERIFY_CODE_TO_CHANGE_CELLPHONE = ConnectApi.SERVIDOR + "/users/changephonenumber/verifycode";

    public static final String PRODUCT = ConnectApi.SERVIDOR + "/product";
    public static final String PRODUCT_DETAIL = SERVIDOR + "/product/detail";
    public static final String PRODUCT_COMPANY = ConnectApi.SERVIDOR + "/product/company";
    public static final String PRODUCT_COMPANY_POS = ConnectApi.SERVIDOR + "/product/companypos";
    public static final String PRODUCT_ITEM = ConnectApi.SERVIDOR + "/product/item";
    public static final String PRODUCT_POSITION = ConnectApi.SERVIDOR + "/product/position";

    public static final String CART = SERVIDOR + "/cart/company";
    public static final String CART_ITEM = SERVIDOR + "/cart/item";
    public static final String CART_LIMPA_TUDO = SERVIDOR + "/cart/clear";

    public static final String CART_ATUALIZA_ITEM = SERVIDOR + "/cart/item";

    public static final String SIZE_NOT_PRODUCT = ConnectApi.SERVIDOR + "/size/product/not";
    public static final String PRODUCT_SIZE = ConnectApi.SERVIDOR + "/size/product";

    public static final String COLLECTION = ConnectApi.SERVIDOR + "/collection";
    public static final String COLLECTION_COMPANY = ConnectApi.SERVIDOR + "/collection/company";
    public static final String COLLECTION_DETAIL = ConnectApi.SERVIDOR + "/collection/detail";
    public static final String COLLECTION_EXTRA = ConnectApi.SERVIDOR + "/collection/extra";
    public static final String COLLECTION_NOT_PRODUCT = ConnectApi.SERVIDOR + "/collection/product/not";
    public static final String COLLECTION_PRODUCT = ConnectApi.SERVIDOR + "/collection/product";
    public static final String COLLECTION_POSITION = ConnectApi.SERVIDOR + "/collection/product/position";

    public static final String EXTRA_NOT_CONJUCT = ConnectApi.SERVIDOR + "/extra/collection/not";
    public static final String EXTRA_ITEMS = ConnectApi.SERVIDOR + "/extra";
    public static final String EXTRA_COMPANY = ConnectApi.SERVIDOR + "/extra/company";

    public static final String EMPLOYEE = ConnectApi.SERVIDOR + "/employee";
    public static final String EMPLOYEE_COMPANY = ConnectApi.SERVIDOR + "/employee/company";
    public static final String EMPLOYEE_SERVICE = ConnectApi.SERVIDOR + "/employee/service";
    public static final String EMPLOYEE_NOT_SERVICE = ConnectApi.SERVIDOR + "/employee/service/unselected";

    public static final String ORDERS_MANUAL = SERVIDOR + "/orders/company";
    public static final String GET_ORDERS = ConnectApi.SERVIDOR + "/orders/company";
    public static final String ORDER_CHAT = SERVIDOR + "/orders/chat";
    public static final String ORDER_HISTORICO = ConnectApi.SERVIDOR + "/orders/history";
    public static final String ORDER_AVALIACOES = ConnectApi.SERVIDOR + "/orders/rating";
    public static final String ORDER_UPDATE_STATUS = ConnectApi.SERVIDOR + "/orders/company/status";

    public static final String SIGNUP = ConnectApi.SERVIDOR + "/signup";
    public static final String SEND_CODE_TO_EMAIL = ConnectApi.SERVIDOR + "/sendemail";
    public static final String SEND_CODE_TO_SMS = ConnectApi.SERVIDOR + "/sendsms";
    public static final String VERIFY_CODE_FROM_EMAIL = ConnectApi.SERVIDOR + "/verifycodeemail";
    public static final String VERIFY_CODE_FROM_CELLPHONE = ConnectApi.SERVIDOR + "/verifycodephonenumber";
    public static final String LEGAL = ConnectApi.SERVIDOR + "/legal";

    public static final String POSTS = ConnectApi.SERVIDOR + "/nukno/posts";
    public static final String POST_ITEM = ConnectApi.SERVIDOR + "/nukno/material/body";
    public static final String FEEDBACK = ConnectApi.SERVIDOR + "/users/feedback";
    public static final String SERVICES_STORE = SERVIDOR + "/service/company";
    public static final String HORARIOS = SERVIDOR + "/service/scheduling";
    public static final String AGENDAR = SERVIDOR + "/scheduling";
    public static final String STORES_MEIOS_PAGAMENTO = SERVIDOR + "/company/payment/checked";

    public static String BEARER(User user, String route, Context ctx) {
        try {
            GsonBuilder gsonBuilder = new GsonBuilder();
            gsonBuilder.excludeFieldsWithoutExposeAnnotation();
            Gson gson = gsonBuilder.create();
            String json = gson.toJson(user);
            return SENDJSON("POST",json, route, ctx);
        } catch (Exception e) {
            FirebaseCrashlytics.getInstance().log(UtilShaPre.getDefaultsString(AppConstants.USER_ID, ctx));
            FirebaseCrashlytics.getInstance().recordException(e);
            e.printStackTrace();
            return null;
        }
    }

    public static String PATCH(User user, String route, Context ctx) {
        try {
            GsonBuilder gsonBuilder = new GsonBuilder();
            gsonBuilder.excludeFieldsWithoutExposeAnnotation();
            Gson gson = gsonBuilder.create();
            String json = gson.toJson(user);
            return SENDJSON("PATCH", json, route, ctx);
        } catch (Exception e) {
            FirebaseCrashlytics.getInstance().log(UtilShaPre.getDefaultsString(AppConstants.USER_ID, ctx));
            FirebaseCrashlytics.getInstance().recordException(e);
            e.printStackTrace();
            return null;
        }
    }

    public static String POST(PaymentCompany paymentCompany, String route, Context ctx) {
        try {
            Gson gson = new Gson();
            String json = gson.toJson(paymentCompany);
            return SENDJSON("POST", json, route, ctx);
        } catch (Exception e) {
            FirebaseCrashlytics.getInstance().log(UtilShaPre.getDefaultsString(AppConstants.USER_ID, ctx));
            FirebaseCrashlytics.getInstance().recordException(e);
            e.printStackTrace();
            return null;
        }
    }


    public static String POST(Scheduling scheduling, String route, Context ctx) {
        try {
            GsonBuilder gsonBuilder = new GsonBuilder();
            gsonBuilder.excludeFieldsWithoutExposeAnnotation();
            Gson gson = gsonBuilder.create();
            String json = gson.toJson(scheduling);
            return SENDJSON("POST", json, route, ctx);
        } catch (Exception e) {
            FirebaseCrashlytics.getInstance().log(UtilShaPre.getDefaultsString(AppConstants.USER_ID, ctx));
            FirebaseCrashlytics.getInstance().recordException(e);
            e.printStackTrace();
            return null;
        }
    }

    public static String POST(Extra extra, String route, Context ctx) {
        try {
            String json;
            GsonBuilder gsonBuilder = new GsonBuilder();
            gsonBuilder.excludeFieldsWithoutExposeAnnotation();
            Gson gson = gsonBuilder.create();
            json = gson.toJson(extra);
            return SENDJSON("POST", json, route, ctx);
        } catch (Exception e) {
            FirebaseCrashlytics.getInstance().log(UtilShaPre.getDefaultsString(AppConstants.USER_ID, ctx));
            FirebaseCrashlytics.getInstance().recordException(e);
            e.printStackTrace();
            return null;
        }
    }

    public static String POST(CollectionExtra extra, String route, Context ctx) {
        try {
            String json;
            GsonBuilder gsonBuilder = new GsonBuilder();
            gsonBuilder.excludeFieldsWithoutExposeAnnotation();
            Gson gson = gsonBuilder.create();
            json = gson.toJson(extra);
            return SENDJSON("POST", json, route, ctx);
        } catch (Exception e) {
            FirebaseCrashlytics.getInstance().log(UtilShaPre.getDefaultsString(AppConstants.USER_ID, ctx));
            FirebaseCrashlytics.getInstance().recordException(e);
            e.printStackTrace();
            return null;
        }
    }

    public static String PATCH(Extra extra, String route, Context ctx) {
        try {
            String json;
            GsonBuilder gsonBuilder = new GsonBuilder();
            gsonBuilder.excludeFieldsWithoutExposeAnnotation();
            Gson gson = gsonBuilder.create();
            json = gson.toJson(extra);

            return SENDJSON("PATCH", json, route, ctx);
        } catch (Exception e) {
            FirebaseCrashlytics.getInstance().log(UtilShaPre.getDefaultsString(AppConstants.USER_ID, ctx));
            FirebaseCrashlytics.getInstance().recordException(e);
            e.printStackTrace();
            return null;
        }
    }

    public static String DELETE(Extra extra, String route, Context ctx) {
        try {
            Gson gson = new Gson();
            String json = gson.toJson(extra);
            return SENDJSON("DELETE", json, route, ctx);
        } catch (Exception e) {
            FirebaseCrashlytics.getInstance().log(UtilShaPre.getDefaultsString(AppConstants.USER_ID, ctx));
            FirebaseCrashlytics.getInstance().recordException(e);
            e.printStackTrace();
            return null;
        }
    }



    public static String DELETE(Cart cart, String route, Context ctx) {
        try {
            Gson gson = new GsonBuilder()
                    .excludeFieldsWithoutExposeAnnotation()
                    .create();
            String json = gson.toJson(cart);
            return SENDJSON("DELETE", json, route, ctx);
        } catch (Exception e) {
            FirebaseCrashlytics.getInstance().log(UtilShaPre.getDefaultsString(AppConstants.USER_ID, ctx));
            FirebaseCrashlytics.getInstance().recordException(e);
            e.printStackTrace();
        }
        return null;
    }

    public static String DELETE(CartProduct cart, String route, Context ctx) {
        try {
            Gson gson = new Gson();
            String json = gson.toJson(cart);
            return SENDJSON("DELETE", json, route, ctx);
        } catch (Exception e) {
            FirebaseCrashlytics.getInstance().log(UtilShaPre.getDefaultsString(AppConstants.USER_ID, ctx));
            FirebaseCrashlytics.getInstance().recordException(e);
            e.printStackTrace();
        }
        return null;
    }

    public static String POST(CartCompany cartCompany, String route, Context ctx) {
        try {
            GsonBuilder gsonBuilder = new GsonBuilder();
            gsonBuilder.registerTypeAdapter(CartCompany.class, new CartBuyTypeAdapter(ctx));
            gsonBuilder.excludeFieldsWithoutExposeAnnotation();
            Gson gson = gsonBuilder.create();
            String json = gson.toJson(cartCompany);
            return SENDJSON("POST", json, route, ctx);
        } catch (Exception e) {
            FirebaseCrashlytics.getInstance().log(UtilShaPre.getDefaultsString(AppConstants.USER_ID, ctx));
            FirebaseCrashlytics.getInstance().recordException(e);
            e.printStackTrace();
        }
        return null;
    }

    public static String POST(Cart cart, String route, Context ctx) {
        try {
            Gson gson = new GsonBuilder()
                    .excludeFieldsWithoutExposeAnnotation()
                    .create();
            String json = gson.toJson(cart);
            return SENDJSON("POST", json, route, ctx);
        } catch (Exception e) {
            FirebaseCrashlytics.getInstance().log(UtilShaPre.getDefaultsString(AppConstants.USER_ID, ctx));
            FirebaseCrashlytics.getInstance().recordException(e);
            e.printStackTrace();
        }
        return null;
    }

    public static String PATCH(Cart cart, String route, Context ctx) {
        try {
            Gson gson = new GsonBuilder()
                    .excludeFieldsWithoutExposeAnnotation()
                    .create();
            String json = gson.toJson(cart);
            return SENDJSON("PATCH", json, route, ctx);
        } catch (Exception e) {
            FirebaseCrashlytics.getInstance().log(UtilShaPre.getDefaultsString(AppConstants.USER_ID, ctx));
            FirebaseCrashlytics.getInstance().recordException(e);
            e.printStackTrace();
        }
        return null;
    }




    public static String DELETE(User user, String route, Context ctx) {
        try {
            Gson gson = new Gson();
            String json = gson.toJson(user);
            return SENDJSON("DELETE", json, route, ctx);
        } catch (Exception e) {
            FirebaseCrashlytics.getInstance().log(UtilShaPre.getDefaultsString(AppConstants.USER_ID, ctx));
            FirebaseCrashlytics.getInstance().recordException(e);
            e.printStackTrace();
            return null;
        }
    }

    public static String POST(Size size, String route, Context ctx) {
        try {
            Gson gson = new GsonBuilder()
                    .excludeFieldsWithoutExposeAnnotation()
                    .create();
            String json = gson.toJson(size);
            return SENDJSON("POST", json, route, ctx);
        } catch (Exception e) {
            FirebaseCrashlytics.getInstance().log(UtilShaPre.getDefaultsString(AppConstants.USER_ID, ctx));
            FirebaseCrashlytics.getInstance().recordException(e);
            e.printStackTrace();
            return null;
        }
    }

    public static String PATCH(Size size, String route, Context ctx) {
        try {
            Gson gson = new GsonBuilder()
                    .excludeFieldsWithoutExposeAnnotation()
                    .create();
            String json = gson.toJson(size);
            return SENDJSON("PATCH", json, route, ctx);
        } catch (Exception e) {
            FirebaseCrashlytics.getInstance().log(UtilShaPre.getDefaultsString(AppConstants.USER_ID, ctx));
            FirebaseCrashlytics.getInstance().recordException(e);
            e.printStackTrace();
            return null;
        }
    }

    public static String DELETE(Size size, String route, Context ctx) {
        try {
            Gson gson = new GsonBuilder()
                    .excludeFieldsWithoutExposeAnnotation()
                    .create();
            String json = gson.toJson(size);
            return SENDJSON("DELETE", json, route, ctx);
        } catch (Exception e) {
            FirebaseCrashlytics.getInstance().log(UtilShaPre.getDefaultsString(AppConstants.USER_ID, ctx));
            FirebaseCrashlytics.getInstance().recordException(e);
            e.printStackTrace();
            return null;
        }
    }


    public static String POST(Feedback feedback, String route, Context ctx) {
        try {
            Gson gson = new Gson();
            String json = gson.toJson(feedback);
            return SENDJSON("POST", json, route, ctx);
        } catch (Exception e) {
            FirebaseCrashlytics.getInstance().log(UtilShaPre.getDefaultsString(AppConstants.USER_ID, ctx));
            FirebaseCrashlytics.getInstance().recordException(e);
            e.printStackTrace();
            return null;
        }
    }

    public static String POST(Invoice invoice, String route, Context ctx) {
        try {
            Gson gson = new Gson();
            String json = gson.toJson(invoice);
            return SENDJSON("POST", json, route, ctx);
        } catch (Exception e) {
            FirebaseCrashlytics.getInstance().log(UtilShaPre.getDefaultsString(AppConstants.USER_ID, ctx));
            FirebaseCrashlytics.getInstance().recordException(e);
            e.printStackTrace();
            return null;
        }
    }

    public static String BEARER(TempEmail tempEmail, String route, Context ctx) {
        try {
            Gson gson = new Gson();
            String json = gson.toJson(tempEmail);
            return SENDJSON("POST",json, route, ctx);
        } catch (Exception e) {
            FirebaseCrashlytics.getInstance().log(UtilShaPre.getDefaultsString(AppConstants.USER_ID, ctx));
            FirebaseCrashlytics.getInstance().recordException(e);
            e.printStackTrace();
            return null;
        }
    }

    public static String BEARER(TempSms tempSms, String route, Context ctx) {
        try {
            Gson gson = new Gson();
            String json = gson.toJson(tempSms);
            return SENDJSON("POST",json, route, ctx);
        } catch (Exception e) {
            FirebaseCrashlytics.getInstance().log(UtilShaPre.getDefaultsString(AppConstants.USER_ID, ctx));
            FirebaseCrashlytics.getInstance().recordException(e);
            e.printStackTrace();
            return null;
        }
    }

    public static String POST(TempEmail tempEmail, String route, Context ctx) {
        try {
            Gson gson = new Gson();
            String json = gson.toJson(tempEmail);
            return SENDJSON("POST", json, route, ctx);
        } catch (Exception e) {
            FirebaseCrashlytics.getInstance().log(UtilShaPre.getDefaultsString(AppConstants.USER_ID, ctx));
            FirebaseCrashlytics.getInstance().recordException(e);
            e.printStackTrace();
            return null;
        }
    }

    public static String POST(TempSms tempSms, String route, Context ctx) {
        try {
            Gson gson = new Gson();
            String json = gson.toJson(tempSms);
            return SENDJSON("POST", json, route, ctx);
        } catch (Exception e) {
            FirebaseCrashlytics.getInstance().log(UtilShaPre.getDefaultsString(AppConstants.USER_ID, ctx));
            FirebaseCrashlytics.getInstance().recordException(e);
            e.printStackTrace();
            return null;
        }
    }

    public static String PATCH(TempSms tempSms, String route, Context ctx) {
        try {
            Gson gson = new Gson();
            String json = gson.toJson(tempSms);
            return SENDJSON("PATCH", json, route, ctx);
        } catch (Exception e) {
            FirebaseCrashlytics.getInstance().log(UtilShaPre.getDefaultsString(AppConstants.USER_ID, ctx));
            FirebaseCrashlytics.getInstance().recordException(e);
            e.printStackTrace();
            return null;
        }
    }

    public static String PATCH(TempEmail tempEmail, String route, Context ctx) {
        try {
            Gson gson = new Gson();
            String json = gson.toJson(tempEmail);
            return SENDJSON("PATCH", json, route, ctx);
        } catch (Exception e) {
            FirebaseCrashlytics.getInstance().log(UtilShaPre.getDefaultsString(AppConstants.USER_ID, ctx));
            FirebaseCrashlytics.getInstance().recordException(e);
            e.printStackTrace();
            return null;
        }
    }


    public static String POST(Chat chat, String route, Context ctx) {
        try {
            Gson gson = new Gson();
            String json = gson.toJson(chat);
            return SENDJSON("POST", json, route, ctx);
        } catch (Exception e) {
            FirebaseCrashlytics.getInstance().log(UtilShaPre.getDefaultsString(AppConstants.USER_ID, ctx));
            FirebaseCrashlytics.getInstance().recordException(e);
            e.printStackTrace();
            return null;
        }
    }

    public static String POST(Mobile sms, String route, Context ctx) {
        try {
            Gson gson = new Gson();
            String json = gson.toJson(sms);
            return SENDJSON("POST", json, route, ctx);
        } catch (Exception e) {
            FirebaseCrashlytics.getInstance().log(UtilShaPre.getDefaultsString(AppConstants.USER_ID, ctx));
            FirebaseCrashlytics.getInstance().recordException(e);
            e.printStackTrace();
        }
        return null;
    }

    public static String PATCH(Mobile sms, String route, Context ctx) {
        try {
            Gson gson = new Gson();
            String json = gson.toJson(sms);
            return SENDJSON("PATCH", json, route, ctx);
        } catch (Exception e) {
            FirebaseCrashlytics.getInstance().log(UtilShaPre.getDefaultsString(AppConstants.USER_ID, ctx));
            FirebaseCrashlytics.getInstance().recordException(e);
            e.printStackTrace();
        }
        return null;
    }

    public static String POST(Coupon coupon, String route, Context ctx) {
        try {
            GsonBuilder gsonBuilder = new GsonBuilder();
            gsonBuilder.excludeFieldsWithoutExposeAnnotation();
            Gson gson = gsonBuilder.create();
            String json = gson.toJson(coupon);
            return SENDJSON("POST", json, route, ctx);
        } catch (Exception e) {
            FirebaseCrashlytics.getInstance().log(UtilShaPre.getDefaultsString(AppConstants.USER_ID, ctx));
            FirebaseCrashlytics.getInstance().recordException(e);
            e.printStackTrace();
        }
        return null;
    }

    public static String POST(List<OpeningHours> openingHours, String route, Context ctx) {
        try {
            Gson gson = new Gson();
            String json = gson.toJson(openingHours);
            return SENDJSON("POST", json, route, ctx);
        } catch (Exception e) {
            FirebaseCrashlytics.getInstance().log(UtilShaPre.getDefaultsString(AppConstants.USER_ID, ctx));
            FirebaseCrashlytics.getInstance().recordException(e);
            e.printStackTrace();
        }
        return null;
    }

    public static String POST2(List collection, String route, Context ctx) {
        try {
            GsonBuilder gsonBuilder = new GsonBuilder();
            gsonBuilder.excludeFieldsWithoutExposeAnnotation();
            Gson gson = gsonBuilder.create();
            String json = gson.toJson(collection);
            return SENDJSON("POST", json, route, ctx);
        } catch (Exception e) {
            FirebaseCrashlytics.getInstance().log(UtilShaPre.getDefaultsString(AppConstants.USER_ID, ctx));
            FirebaseCrashlytics.getInstance().recordException(e);
            e.printStackTrace();
        }
        return null;
    }


    public static String POST3(List extra, String route, Context ctx) {
        try {
            GsonBuilder gsonBuilder = new GsonBuilder();
            gsonBuilder.excludeFieldsWithoutExposeAnnotation();
            Gson gson = gsonBuilder.create();
            String json = gson.toJson(extra);
            return SENDJSON("POST", json, route, ctx);
        } catch (Exception e) {
            FirebaseCrashlytics.getInstance().log(UtilShaPre.getDefaultsString(AppConstants.USER_ID, ctx));
            FirebaseCrashlytics.getInstance().recordException(e);
            e.printStackTrace();
        }
        return null;
    }

    public static String PATCH(List product, String route, Context ctx) {
        try {
            Gson gson = new Gson();
            String json = gson.toJson(product);
            return SENDJSON("PATCH", json, route, ctx);
        } catch (Exception e) {
            FirebaseCrashlytics.getInstance().log(UtilShaPre.getDefaultsString(AppConstants.USER_ID, ctx));
            FirebaseCrashlytics.getInstance().recordException(e);
            e.printStackTrace();
        }
        return null;
    }


    public static String DELETE(PaymentCompany paymentCompany, String route, Context ctx) {
        try {
            Gson gson = new Gson();
            String json = gson.toJson(paymentCompany);
            return SENDJSON("DELETE", json, route, ctx);
        } catch (Exception e) {
            FirebaseCrashlytics.getInstance().log(UtilShaPre.getDefaultsString(AppConstants.USER_ID, ctx));
            FirebaseCrashlytics.getInstance().recordException(e);
            e.printStackTrace();
        }
        return null;
    }

    public static String POST(Service service, String route, Context ctx) {
        try {
            Gson gson = new Gson();
            String json = gson.toJson(service);
            return SENDJSON("POST", json, route, ctx);
        } catch (Exception e) {
            FirebaseCrashlytics.getInstance().log(UtilShaPre.getDefaultsString(AppConstants.USER_ID, ctx));
            FirebaseCrashlytics.getInstance().recordException(e);
            e.printStackTrace();
        }
        return null;
    }

    public static String POST(ServiceEmployee serviceEmployee, String route, Context ctx) {
        try {
            Gson gson = new Gson();
            String json = gson.toJson(serviceEmployee);
            return SENDJSON("POST", json, route, ctx);
        } catch (Exception e) {
            FirebaseCrashlytics.getInstance().log(UtilShaPre.getDefaultsString(AppConstants.USER_ID, ctx));
            FirebaseCrashlytics.getInstance().recordException(e);
            e.printStackTrace();
        }
        return null;
    }

    public static String POST(Employee employee, String route, Activity ctx) {
        try {
            Gson gson = new Gson();
            String json = gson.toJson(employee);
            return SENDJSON("POST", json, route, ctx);
        } catch (Exception e) {
            FirebaseCrashlytics.getInstance().log(UtilShaPre.getDefaultsString(AppConstants.USER_ID, ctx));
            FirebaseCrashlytics.getInstance().recordException(e);
            e.printStackTrace();
        }
        return null;
    }

    public static String PATCH(Employee employee, String route, Activity ctx) {
        try {
            Gson gson = new Gson();
            String json = gson.toJson(employee);
            return SENDJSON("PATCH", json, route, ctx);
        } catch (Exception e) {
            FirebaseCrashlytics.getInstance().log(UtilShaPre.getDefaultsString(AppConstants.USER_ID, ctx));
            FirebaseCrashlytics.getInstance().recordException(e);
            e.printStackTrace();
        }
        return null;
    }

    public static String DELETE(Employee employee, String route, Activity ctx) {
        try {
            Gson gson = new Gson();
            String json = gson.toJson(employee);
            return SENDJSON("DELETE", json, route, ctx);
        } catch (Exception e) {
            FirebaseCrashlytics.getInstance().log(UtilShaPre.getDefaultsString(AppConstants.USER_ID, ctx));
            FirebaseCrashlytics.getInstance().recordException(e);
            e.printStackTrace();
        }
        return null;
    }

    public static String POST(Finance funcionario, String route, Context ctx) {
        try {
            Gson gson = new Gson();
            String json = gson.toJson(funcionario);
            return SENDJSON("POST", json, route, ctx);
        } catch (Exception e) {
            FirebaseCrashlytics.getInstance().log(UtilShaPre.getDefaultsString(AppConstants.USER_ID, ctx));
            FirebaseCrashlytics.getInstance().recordException(e);
            e.printStackTrace();
        }
        return null;
    }

    public static String PATCH(Finance finance, String route, Context ctx) {
        try {
            Gson gson = new Gson();
            String json = gson.toJson(finance);
            return SENDJSON("PATCH", json, route, ctx);
        } catch (Exception e) {
            FirebaseCrashlytics.getInstance().log(UtilShaPre.getDefaultsString(AppConstants.USER_ID, ctx));
            FirebaseCrashlytics.getInstance().recordException(e);
            e.printStackTrace();
        }
        return null;
    }

    public static String DELETE(Finance finance, String route, Context ctx) {
        try {
            Gson gson = new Gson();
            String json = gson.toJson(finance);
            return SENDJSON("DELETE", json, route, ctx);
        } catch (Exception e) {
            FirebaseCrashlytics.getInstance().log(UtilShaPre.getDefaultsString(AppConstants.USER_ID, ctx));
            FirebaseCrashlytics.getInstance().recordException(e);
            e.printStackTrace();
        }
        return null;
    }

    public static String PATCH(OpeningHours openingHours, String route, Context ctx) {
        try {
            Gson gson = new Gson();
            String json = gson.toJson(openingHours);
            return SENDJSON("PATCH", json, route, ctx);
        } catch (Exception e) {
            FirebaseCrashlytics.getInstance().log(UtilShaPre.getDefaultsString(AppConstants.USER_ID, ctx));
            FirebaseCrashlytics.getInstance().recordException(e);
            e.printStackTrace();
        }
        return null;
    }

    public static String DELETE(OpeningHours openingHours, String route, Context ctx) {
        try {
            Gson gson = new Gson();
            String json = gson.toJson(openingHours);
            return SENDJSON("DELETE", json, route, ctx);
        } catch (Exception e) {
            FirebaseCrashlytics.getInstance().log(UtilShaPre.getDefaultsString(AppConstants.USER_ID, ctx));
            FirebaseCrashlytics.getInstance().recordException(e);
            e.printStackTrace();
        }
        return null;
    }

    public static String PATCH(Service service, String route, Context ctx) {
        try {
            Gson gson = new Gson();
            String json = gson.toJson(service);
            return SENDJSON("PATCH", json, route, ctx);
        } catch (Exception e) {
            FirebaseCrashlytics.getInstance().log(UtilShaPre.getDefaultsString(AppConstants.USER_ID, ctx));
            FirebaseCrashlytics.getInstance().recordException(e);
            e.printStackTrace();
        }
        return null;
    }

    public static String DELETE(Service service, String route, Context ctx) {
        try {
            Gson gson = new Gson();
            String json = gson.toJson(service);
            return SENDJSON("DELETE", json, route, ctx);
        } catch (Exception e) {
            FirebaseCrashlytics.getInstance().log(UtilShaPre.getDefaultsString(AppConstants.USER_ID, ctx));
            FirebaseCrashlytics.getInstance().recordException(e);
            e.printStackTrace();
        }
        return null;
    }

    public static String DELETE(ServiceEmployee serviceEmployee, String route, Context ctx) {
        try {
            Gson gson = new Gson();
            String json = gson.toJson(serviceEmployee);
            return SENDJSON("DELETE", json, route, ctx);
        } catch (Exception e) {
            FirebaseCrashlytics.getInstance().log(UtilShaPre.getDefaultsString(AppConstants.USER_ID, ctx));
            FirebaseCrashlytics.getInstance().recordException(e);
            e.printStackTrace();
        }
        return null;
    }

    public static String POST(Product product, String route, Context ctx) {
        try {
            GsonBuilder gsonBuilder = new GsonBuilder();
            gsonBuilder.excludeFieldsWithoutExposeAnnotation();
            Gson gson = gsonBuilder.create();
            String json = gson.toJson(product);
            return SENDJSON("POST", json, route, ctx);
        } catch (Exception e) {
            FirebaseCrashlytics.getInstance().log(UtilShaPre.getDefaultsString(AppConstants.USER_ID, ctx));
            FirebaseCrashlytics.getInstance().recordException(e);
            e.printStackTrace();
        }
        return null;
    }

    public static String POST(Collection collection, String route, Context ctx) {
        try {
            GsonBuilder gsonBuilder = new GsonBuilder();
            gsonBuilder.excludeFieldsWithoutExposeAnnotation();
            Gson gson = gsonBuilder.create();
            String json = gson.toJson(collection);
            return SENDJSON("POST", json, route, ctx);
        } catch (Exception e) {
            FirebaseCrashlytics.getInstance().log(UtilShaPre.getDefaultsString(AppConstants.USER_ID, ctx));
            FirebaseCrashlytics.getInstance().recordException(e);
            e.printStackTrace();
        }
        return null;
    }

    public static String PATCH(Collection collection, String route, Context ctx) {
        try {
            GsonBuilder gsonBuilder = new GsonBuilder();
            gsonBuilder.excludeFieldsWithoutExposeAnnotation();
            Gson gson = gsonBuilder.create();
            String json = gson.toJson(collection);
            return SENDJSON("PATCH", json, route, ctx);
        } catch (Exception e) {
            FirebaseCrashlytics.getInstance().log(UtilShaPre.getDefaultsString(AppConstants.USER_ID, ctx));
            FirebaseCrashlytics.getInstance().recordException(e);
            e.printStackTrace();
        }
        return null;
    }

    public static String DELETE(Collection collection, String route, Context ctx) {
        try {
            Gson gson = new Gson();
            String json = gson.toJson(collection);
            return SENDJSON("DELETE", json, route, ctx);
        } catch (Exception e) {
            FirebaseCrashlytics.getInstance().log(UtilShaPre.getDefaultsString(AppConstants.USER_ID, ctx));
            FirebaseCrashlytics.getInstance().recordException(e);
            e.printStackTrace();
        }
        return null;
    }

    public static String DELETE(ProductCollection collection, String route, Context ctx) {
        try {
            Gson gson = new Gson();
            String json = gson.toJson(collection);
            return SENDJSON("DELETE", json, route, ctx);
        } catch (Exception e) {
            FirebaseCrashlytics.getInstance().log(UtilShaPre.getDefaultsString(AppConstants.USER_ID, ctx));
            FirebaseCrashlytics.getInstance().recordException(e);
            e.printStackTrace();
        }
        return null;
    }


    public static String POST(Company company, String route, Context ctx) {
        try {
            GsonBuilder gsonBuilder = new GsonBuilder();
            gsonBuilder.excludeFieldsWithoutExposeAnnotation();
            Gson gson = gsonBuilder.create();
            String json = gson.toJson(company);
            return SENDJSON("POST", json, route, ctx);
        } catch (Exception e) {
            FirebaseCrashlytics.getInstance().log(UtilShaPre.getDefaultsString(AppConstants.USER_ID, ctx));
            FirebaseCrashlytics.getInstance().recordException(e);
            e.printStackTrace();
        }
        return null;
    }

    public static String BEARER(Company company, String route, Context ctx) {
        try {
            GsonBuilder gsonBuilder = new GsonBuilder();
            gsonBuilder.excludeFieldsWithoutExposeAnnotation();
            Gson gson = gsonBuilder.create();
            String json = gson.toJson(company);
            return SENDJSON("POST",json, route, ctx);
        } catch (Exception e) {
            FirebaseCrashlytics.getInstance().log(UtilShaPre.getDefaultsString(AppConstants.USER_ID, ctx));
            FirebaseCrashlytics.getInstance().recordException(e);
            e.printStackTrace();
        }
        return null;
    }

    public static String PATCH(Product product, String route, Context ctx) {
        try {
            GsonBuilder gsonBuilder = new GsonBuilder();
            gsonBuilder.excludeFieldsWithoutExposeAnnotation();
            Gson gson = gsonBuilder.create();
            String json = gson.toJson(product);
            return SENDJSON("PATCH", json, route, ctx);
        } catch (Exception e) {
            FirebaseCrashlytics.getInstance().log(UtilShaPre.getDefaultsString(AppConstants.USER_ID, ctx));
            FirebaseCrashlytics.getInstance().recordException(e);
            e.printStackTrace();
        }
        return null;
    }

    public static String DELETE(Product product, String route, Context ctx) {
        try {
            GsonBuilder gsonBuilder = new GsonBuilder();
            gsonBuilder.excludeFieldsWithoutExposeAnnotation();
            Gson gson = gsonBuilder.create();
            String json = gson.toJson(product);
            return SENDJSON("DELETE", json, route, ctx);
        } catch (Exception e) {
            FirebaseCrashlytics.getInstance().log(UtilShaPre.getDefaultsString(AppConstants.USER_ID, ctx));
            FirebaseCrashlytics.getInstance().recordException(e);
            e.printStackTrace();
        }
        return null;
    }

    public static String PATCH(Company company, String route, Context ctx) {
        try {
            GsonBuilder gsonBuilder = new GsonBuilder();
            gsonBuilder.excludeFieldsWithoutExposeAnnotation();
            Gson gson = gsonBuilder.create();
            String json = gson.toJson(company);
            return SENDJSON("PATCH", json, route, ctx);
        } catch (Exception e) {
            FirebaseCrashlytics.getInstance().log(UtilShaPre.getDefaultsString(AppConstants.USER_ID, ctx));
            FirebaseCrashlytics.getInstance().recordException(e);
            e.printStackTrace();
        }
        return null;
    }

    public static String PATCH(Order order, String route, Context ctx) {
        try {
            Gson gson = new Gson();
            String json = gson.toJson(order);
            return SENDJSON("PATCH", json, route, ctx);
        } catch (Exception e) {
            FirebaseCrashlytics.getInstance().log(UtilShaPre.getDefaultsString(AppConstants.USER_ID, ctx));
            FirebaseCrashlytics.getInstance().recordException(e);
            e.printStackTrace();
        }
        return null;
    }

    public static String PATCH(Scheduling agendamento, String route, Context ctx) {
        try {
            GsonBuilder gsonBuilder = new GsonBuilder();
            gsonBuilder.excludeFieldsWithoutExposeAnnotation();
            Gson gson = gsonBuilder.create();
            String json = gson.toJson(agendamento);
            return SENDJSON("PATCH", json, route, ctx);
        } catch (Exception e) {
            FirebaseCrashlytics.getInstance().log(UtilShaPre.getDefaultsString(AppConstants.USER_ID, ctx));
            FirebaseCrashlytics.getInstance().recordException(e);
            e.printStackTrace();
        }
        return null;
    }

    public static int REQUESTNEWTOKEN(String route, Context ctx) {
        OkHttpClient client = new OkHttpClient();
        final String BEARER = UtilShaPre.getDefaultsString("refresh_token", ctx);

        RequestBody body = new FormBody.Builder()
                .add("api_version", API_VERSION)
                .add("aplication_version", BuildConfig.VERSION_NAME)
                .add("device_type", !ctx.getResources().getBoolean(R.bool.isTablet10) || !ctx.getResources().getBoolean(R.bool.isTablet7) ? "Smartphone" : "Tablet")
                .add("source", "nuppin_company")
                .add(AppConstants.USER_ID, String.valueOf(UtilShaPre.getDefaultsString(AppConstants.USER_ID, ctx)))
                .build();

        okhttp3.Request request = new okhttp3.Request.Builder()
                .url(route)
                .post(body)
                .header("Authorization", "Bearer " + BEARER)
                .build();

        try (Response response = client.newCall(request).execute()) {
            String token = response.header("token");
            if (token != null && !token.isEmpty()) {
                UtilShaPre.setDefaults("auth_token", token, ctx);
            }

            String s = response.body().string();
            int code = response.code();

            return code;

        } catch (Exception e) {
            FirebaseCrashlytics.getInstance().log(UtilShaPre.getDefaultsString(AppConstants.USER_ID, ctx));
            FirebaseCrashlytics.getInstance().recordException(e);
            return 500;
        }
    }

    private static String SENDJSON(String metodoHttp, String json, String route, Context ctx) {
        final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        OkHttpClient client = new OkHttpClient();
        okhttp3.Request request;
        final String BEARER = UtilShaPre.getDefaultsString("auth_token", ctx);

        Log.d("post_api", json);
        Log.d("post_api_route", route);

        if (metodoHttp.equals("POST")) {
            RequestBody body = RequestBody.create(JSON, json);
            request = new okhttp3.Request.Builder()
                    .url(route)
                    .header("Authorization", "Bearer " + BEARER)
                    .post(body)
                    .build();
        } else if (metodoHttp.equals("PATCH")) {
            RequestBody body = RequestBody.create(JSON, json);
            request = new okhttp3.Request.Builder()
                    .url(route)
                    .header("Authorization", "Bearer " + BEARER)
                    .patch(body)
                    .build();
        } else {
            RequestBody body = RequestBody.create(JSON, json);
            request = new okhttp3.Request.Builder()
                    .url(route)
                    .header("Authorization", "Bearer " + BEARER)
                    .delete(body)
                    .build();
        }

        try (Response response = client.newCall(request).execute()) {

            String token = response.header("token");
            if (token != null && !token.isEmpty()) {
                UtilShaPre.setDefaults("auth_token", token, ctx);
            }

            String s = response.body().string();
            int code = response.code();
            Log.d("post_api_post_res", s);

            if (code == 401) {
                if (UtilShaPre.getDefaultsInt("counter", ctx) < 3) {
                    if (REQUESTNEWTOKEN(USERS_REFRESH_TOKEN, ctx) != 500) {
                        UtilShaPre.setDefaults("counter", UtilShaPre.getDefaultsInt("counter", ctx) + 1, ctx);
                        return SENDJSON(metodoHttp, json, route, ctx);
                    } else {
                        return "false";
                    }
                } else {
                    UtilShaPre.setDefaults("counter", 0, ctx);
                    loggout(ctx);
                    return s;
                }
            } else if (code == 403) {
                if (UtilShaPre.getDefaultsInt("counter", ctx) < 3) {
                    UtilShaPre.setDefaults("counter", UtilShaPre.getDefaultsInt("counter", ctx) + 1, ctx);
                    REQUESTNEWTOKEN(USERS_REFRESH_TOKEN, ctx);
                    return SENDJSON(metodoHttp, json, route, ctx);
                } else {
                    UtilShaPre.setDefaults("counter", 0, ctx);
                    return s;
                }
            } else if (code == 500) {
                UtilShaPre.setDefaults("counter", UtilShaPre.getDefaultsInt("counter", ctx) + 1, ctx);
                if (UtilShaPre.getDefaultsInt("counter", ctx) < 3) {
                    return SENDJSON(metodoHttp, json, route, ctx);
                } else {
                    UtilShaPre.setDefaults("counter", 0, ctx);
                    return s;
                }
            } else {
                UtilShaPre.setDefaults("counter", 0, ctx);
                return s;
            }
        } catch (Exception e) {
            FirebaseCrashlytics.getInstance().log(UtilShaPre.getDefaultsString(AppConstants.USER_ID, ctx));
            FirebaseCrashlytics.getInstance().recordException(e);
            return null;
        }
    }

    public static String GET(String route, Context ctx) {

        final OkHttpClient client = new OkHttpClient();

        Log.d("get_api_route", route);
        Log.d("api_token", UtilShaPre.getDefaultsString("auth_token", ctx));

        okhttp3.Request request = new okhttp3.Request.Builder()
                .url(route)
                .header("Authorization", "Bearer " + UtilShaPre.getDefaultsString("auth_token", ctx))
                .build();

        try {
            try (Response response = client.newCall(request).execute()) {
                String s = response.body().string();
                int code = response.code();
                Log.d("post_api_get", s);

                if (code == 401) {
                    if (UtilShaPre.getDefaultsInt("counter", ctx) < 3) {
                        if (REQUESTNEWTOKEN(USERS_REFRESH_TOKEN, ctx) != 500) {
                            UtilShaPre.setDefaults("counter", UtilShaPre.getDefaultsInt("counter", ctx) + 1, ctx);
                            return GET(route, ctx);
                        } else {
                            return "false";
                        }
                    } else {
                        UtilShaPre.setDefaults("counter", 0, ctx);
                        loggout(ctx);
                        return s;
                    }
                } else if (code == 403) {
                    if (UtilShaPre.getDefaultsInt("counter", ctx) < 3) {
                        UtilShaPre.setDefaults("counter", UtilShaPre.getDefaultsInt("counter", ctx) + 1, ctx);
                        REQUESTNEWTOKEN(USERS_REFRESH_TOKEN, ctx);
                        return GET(route, ctx);
                    } else {
                        UtilShaPre.setDefaults("counter", 0, ctx);
                        return s;
                    }
                } else if (code == 500) {
                    UtilShaPre.setDefaults("counter", UtilShaPre.getDefaultsInt("counter", ctx) + 1, ctx);
                    if (UtilShaPre.getDefaultsInt("counter", ctx) < 3) {
                        return GET(route, ctx);
                    } else {
                        UtilShaPre.setDefaults("counter", 0, ctx);
                        return s;
                    }
                } else {
                    UtilShaPre.setDefaults("counter", 0, ctx);
                    return s;
                }
            }
        } catch (
                Exception e) {
            FirebaseCrashlytics.getInstance().log(UtilShaPre.getDefaultsString(AppConstants.USER_ID, ctx));
            FirebaseCrashlytics.getInstance().recordException(e);
            return null;
        }

    }

    public static void loggout(Context ctx) {
        UtilShaPre.setDefaults("auth_token", "", ctx);
        UtilShaPre.setDefaults("refresh_token", "", ctx);
        UtilShaPre.setDefaults("user_logged", false, ctx);
        Intent intent = new Intent(ctx, SplashScreen.class);
        ctx.startActivity(intent);
    }

    //=========================== UPLOAD IMAGE =======================================

    public static String enviarFoto(Context ctx, String path, String id, String route, byte[] imageCompressed) {
        OkHttpClient client = new OkHttpClient();
        final String urlDoServidor = SERVIDOR + "/upload/" + route + "," + id;

        final MediaType MEDIA_TYPE_ORDERS_ = MediaType.parse("image/jpeg");
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("image", "image.jpeg",
                        RequestBody.create(MEDIA_TYPE_ORDERS_, imageCompressed))
                .build();

        okhttp3.Request request = new okhttp3.Request.Builder()
                .header("Authorization", "Bearer " + UtilShaPre.getDefaultsString("auth_token", ctx))
                .url(urlDoServidor)
                .post(requestBody)
                .build();

        //Check the response
        try {
            try (Response response = client.newCall(request).execute()) {
                String s = response.body().string();
                int code = response.code();

                if (code == 401) {
                    if (UtilShaPre.getDefaultsInt("counter", ctx) < 3) {
                        if (REQUESTNEWTOKEN(USERS_REFRESH_TOKEN, ctx) != 500) {
                            UtilShaPre.setDefaults("counter", UtilShaPre.getDefaultsInt("counter", ctx) + 1, ctx);
                            return enviarFoto(ctx, path, id, route, imageCompressed);
                        } else {
                            return "false";
                        }
                    } else {
                        UtilShaPre.setDefaults("counter", 0, ctx);
                        loggout(ctx);
                        return s;
                    }
                } else if (code == 403) {
                    if (UtilShaPre.getDefaultsInt("counter", ctx) < 3) {
                        UtilShaPre.setDefaults("counter", UtilShaPre.getDefaultsInt("counter", ctx) + 1, ctx);
                        REQUESTNEWTOKEN(USERS_REFRESH_TOKEN, ctx);
                        return enviarFoto(ctx, path, id, route, imageCompressed);
                    } else {
                        UtilShaPre.setDefaults("counter", 0, ctx);
                        return s;
                    }
                } else if (code == 500) {
                    UtilShaPre.setDefaults("counter", UtilShaPre.getDefaultsInt("counter", ctx) + 1, ctx);
                    if (UtilShaPre.getDefaultsInt("counter", ctx) < 3) {
                        return enviarFoto(ctx, path, id, route, imageCompressed);
                    } else {
                        UtilShaPre.setDefaults("counter", 0, ctx);
                        return s;
                    }
                } else {
                    UtilShaPre.setDefaults("counter", 0, ctx);
                    return s;
                }
            }
        } catch (
                Exception e) {
            FirebaseCrashlytics.getInstance().log(UtilShaPre.getDefaultsString(AppConstants.USER_ID, ctx));
            FirebaseCrashlytics.getInstance().recordException(e);
            return null;
        }
    }


    public static void strongerRequest(String route, String token, Context ctx) {
        OkHttpClient client = new OkHttpClient();
        RequestBody body = new FormBody.Builder()
                .add("token", token)
                .add(AppConstants.USER_ID, String.valueOf(UtilShaPre.getDefaultsString(AppConstants.USER_ID, ctx)))
                .build();

        Request request = new Request.Builder()
                .url(route)
                .addHeader("Authorization", "Bearer " + UtilShaPre.getDefaultsString("auth_token", ctx))
                .post(body)
                .build();

        try (Response response = client.newCall(request).execute()) {
            String s = response.body().string();

            int code = response.code();

            if (code == 401) {
                if (UtilShaPre.getDefaultsInt("counter", ctx) < 3) {
                    if (REQUESTNEWTOKEN(USERS_REFRESH_TOKEN, ctx) != 500) {
                        UtilShaPre.setDefaults("counter", UtilShaPre.getDefaultsInt("counter", ctx) + 1, ctx);
                        strongerRequest(route, token, ctx);
                    }
                } else {
                    UtilShaPre.setDefaults("counter", 0, ctx);
                    ConnectApi.loggout(ctx);
                }
            } else if (code == 403) {
                if (UtilShaPre.getDefaultsInt("counter", ctx) < 3) {
                    UtilShaPre.setDefaults("counter", UtilShaPre.getDefaultsInt("counter", ctx) + 1, ctx);
                    ConnectApi.REQUESTNEWTOKEN(ConnectApi.USERS_REFRESH_TOKEN, ctx);
                    strongerRequest(route, token, ctx);
                } else {
                    UtilShaPre.setDefaults("counter", 0, ctx);
                }
            } else if (code == 500) {
                UtilShaPre.setDefaults("counter", UtilShaPre.getDefaultsInt("counter", ctx) + 1, ctx);
                if (UtilShaPre.getDefaultsInt("counter", ctx) < 3) {
                    ConnectApi.REQUESTNEWTOKEN(ConnectApi.USERS_REFRESH_TOKEN, ctx);
                    strongerRequest(route, token, ctx);
                } else {
                    UtilShaPre.setDefaults("counter", 0, ctx);
                }
            } else {
                UtilShaPre.setDefaults("counter", 0, ctx);
            }
        } catch (Exception e) {
            FirebaseCrashlytics.getInstance().log(UtilShaPre.getDefaultsString(AppConstants.USER_ID, ctx));
            FirebaseCrashlytics.getInstance().recordException(e);
            e.printStackTrace();
        }
    }
}
