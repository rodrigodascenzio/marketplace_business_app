package com.nuppin.company.service;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;

import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.nuppin.company.Util.AppConstants;
import com.nuppin.company.connection.ConnectApi;
import com.nuppin.company.Util.UtilShaPre;
import com.nuppin.company.model.Mobile;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class UpdateLocationToCloudService extends Service {

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        final Mobile sms;
        if (intent.getParcelableExtra(AppConstants.MOBILE) != null) {
            sms = intent.getParcelableExtra(AppConstants.MOBILE);
            if (sms != null) {
                AsyncTask.execute(new Runnable() {
                    @Override
                    public void run() {
                        sendRegistrationToServer(sms);
                    }
                });
                stopSelf();
            } else {
                stopSelf();
            }
        } else {
            stopSelf();
        }

        return START_NOT_STICKY;
    }

    private void sendRegistrationToServer(Mobile sms) {

        OkHttpClient client = new OkHttpClient();
        RequestBody body = new FormBody.Builder()
                .add("latitude", String.valueOf(sms.getLatitude()))
                .add("longitude", String.valueOf(sms.getLongitude()))
                .add("company_id", sms.getCompany_id())
                .add("mobile_id", "")
                .build();

        Request request = new Request.Builder()
                .url(ConnectApi.MOBILE_UPDATE)
                .addHeader("Authorization", "Bearer " + UtilShaPre.getDefaultsString("auth_token", getApplicationContext()))
                .patch(body)
                .build();
        try {
            client.newCall(request).execute();
        } catch (IOException e) {
            FirebaseCrashlytics.getInstance().log(UtilShaPre.getDefaultsString(AppConstants.USER_ID, getApplicationContext()));
            FirebaseCrashlytics.getInstance().recordException(e);
            e.printStackTrace();
            stopSelf();
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
