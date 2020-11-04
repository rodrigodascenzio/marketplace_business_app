package com.nuppin.company.firebase;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;

import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.nuppin.company.Util.AppConstants;
import com.nuppin.company.connection.ConnectApi;
import com.nuppin.company.Util.UtilShaPre;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class updateUserToken extends Service {

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                sendRegistrationToServer();
            }
        });
        stopSelf();
        return START_NOT_STICKY;
    }

    private void sendRegistrationToServer() {
        try {
           ConnectApi.strongerRequest(ConnectApi.USER_COMPANY_TOKEN, UtilShaPre.getDefaultsString("userStoToken", getApplicationContext()),getApplicationContext());
        } catch (Exception e) {
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
