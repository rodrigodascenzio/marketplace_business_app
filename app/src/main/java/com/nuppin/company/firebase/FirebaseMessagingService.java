package com.nuppin.company.firebase;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Intent;
import android.media.AudioAttributes;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.android.gms.common.api.Api;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.firebase.messaging.RemoteMessage;
import com.nuppin.company.Loja.Login.SplashScreen;
import com.nuppin.company.Util.AppConstants;
import com.nuppin.company.chat.FrListChats;
import com.nuppin.company.connection.ConnectApi;
import com.nuppin.company.Loja.activity.MainStoreActivity;
import com.nuppin.company.Loja.loja.FrStoreViewPager;
import com.nuppin.company.R;
import com.nuppin.company.Util.UtilShaPre;
import com.nuppin.company.chat.FrChat;
import com.nuppin.company.tablet.FrAgeTablet;
import com.nuppin.company.tablet.FrOrdersTablet;
import com.nuppin.company.tablet.MainStoreActivityTablet;

import java.io.IOException;
import java.util.Map;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        if (remoteMessage != null) {

            Map<String, String> data = remoteMessage.getData();
            String tag = data.get("tag");

            if (tag != null) {
                if (tag.equals(AppConstants.ORDERS) || tag.equals("cancel_order")) {
                    showNotification(data);
                    if (getResources().getBoolean(R.bool.isTablet10) || getResources().getBoolean(R.bool.isTablet7)) {
                        if (FrStoreViewPager.active) {
                            this.sendBroadcast(new Intent().setAction("newOrder"));
                            if (FrOrdersTablet.active) {
                                this.sendBroadcast(new Intent().setAction("newOrderFr"));
                            } else if (FrAgeTablet.active) {
                                this.sendBroadcast(new Intent().setAction("newAgeFr"));
                            }
                        }
                    } else {
                        if (FrStoreViewPager.active) {
                            this.sendBroadcast(new Intent().setAction("newOrder"));
                        }
                    }
                } else if (tag.equals(AppConstants.CHAT)) {
                    if (FrChat.active || FrListChats.active) {
                        this.sendBroadcast(new Intent().setAction("bcNewMessage"));
                    } else if (FrStoreViewPager.active) {
                        this.sendBroadcast(new Intent().setAction("newMessage"));
                    } else {
                        showNotification(data);
                    }
                } else if (tag.equals(AppConstants.INVOICE)) {
                    showNotification(data);
                }
            } else {
                String title = remoteMessage.getNotification().getTitle();
                String body = remoteMessage.getNotification().getBody();
                showNotification(title, body);
            }
        }
    }

    private void showNotification(Map<String, String> data) {
        String body = data.get("body");
        String title = data.get("title");
        String tag = data.get("tag");
        String sound = data.get("sound");

        Intent intent;
        if (getResources().getBoolean(R.bool.isTablet10) || getResources().getBoolean(R.bool.isTablet7)) {
            intent = new Intent(getApplicationContext(), MainStoreActivityTablet.class);
        } else {
            intent = new Intent(getApplicationContext(), MainStoreActivity.class);
        }

        if (data.get("extra") != null && tag.equals(AppConstants.CHAT)) {
            intent.putExtra(AppConstants.CHAT, data.get("extra"));
        }

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
        Uri soundOgg = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + getPackageName() + "/" + getOgg(sound));

        //Criar notificação
        NotificationCompat.Builder notificacao = new NotificationCompat.Builder(this, tag)
                .setContentTitle(title)
                .setContentText(body)
                .setSound(soundOgg)
                .setAutoCancel(true)
                .setGroup(tag)
                .setColor(getResources().getColor(R.color.colorPrimary))
                .setSmallIcon(R.drawable.ic_notification)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent);

        //Recupera notificationManager
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        //Check if notification channel exists and if not create one
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = notificationManager.getNotificationChannel(tag);
            if (notificationChannel == null) {
                AudioAttributes attributes = new AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_NOTIFICATION).build();
                int importance = NotificationManager.IMPORTANCE_HIGH; //Set the importance level
                notificationChannel = new NotificationChannel(tag, getChannel(tag), importance);
                notificationChannel.setSound(soundOgg, attributes);
                notificationManager.createNotificationChannel(notificationChannel);
            }
        }

        //Envia notificação
        switch (tag) {
            case AppConstants.CHAT:
                notificationManager.notify(9, notificacao.build());
                break;
            case AppConstants.INVOICE:
                notificationManager.notify(99, notificacao.build());
                break;
            case AppConstants.ORDERS:
            case "cancel_order":
                notificationManager.notify((int) System.currentTimeMillis(), notificacao.build());
                break;
        }
    }

    private void showNotification(String title, String message) {

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "fcm")
                .setAutoCancel(true)
                .setContentTitle(title)
                .setContentText(message)
                .setColor(getResources().getColor(R.color.colorPrimary))
                .setSmallIcon(R.drawable.ic_notification);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        //Check if notification channel exists and if not create one
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = notificationManager.getNotificationChannel("fcm");
            if (notificationChannel == null) {
                int importance = NotificationManager.IMPORTANCE_LOW; //Set the importance level
                notificationChannel = new NotificationChannel("fcm", "avisos", importance);
                notificationChannel.setSound(null, null);
                notificationChannel.setShowBadge(false);
                notificationManager.createNotificationChannel(notificationChannel);
            }
        }
        notificationManager.notify((int) System.currentTimeMillis(), builder.build());
    }


    private int getOgg(String sound) {
        if (sound != null) {
            switch (sound) {
                case AppConstants.ORDERS:
                    return R.raw.order;
                case "cancel_order":
                    return R.raw.cancel_order;
                case AppConstants.CHAT:
                    return R.raw.chat;
            }
        }
        return 0;
    }

    private String getChannel(String tag) {
        if (tag != null) {
            switch (tag) {
                case AppConstants.ORDERS:
                    return "pedidos";
                case "cancel_order":
                    return "cancelamentos";
                case AppConstants.CHAT:
                    return "chat";
            }
        }
        return "pedidos";
    }


    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the InstanceID token
     * is initially generated so this is where you would retrieve the token.
     */
    @Override
    public void onNewToken(String token) {
        UtilShaPre.setDefaults("userStoToken", token, getApplicationContext());

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        if (!UtilShaPre.getDefaultsString("auth_token", getApplicationContext()).equals("")) {
            sendRegistrationToServer(token);
        } else {
            sendTempTokenToServer(token);
        }
    }

    private void sendRegistrationToServer(String token) {
        ConnectApi.strongerRequest(ConnectApi.USER_COMPANY_TOKEN, token, getApplicationContext());
    }

    private void sendTempTokenToServer(String token) {
        ConnectApi.strongerRequest(ConnectApi.USER_COMPANY_TEMP_TOKEN, token, getApplicationContext());
    }
}
