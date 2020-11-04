package com.nuppin.company.Loja.Login;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.nuppin.company.Loja.activity.MainStoreActivity;
import com.nuppin.company.R;
import com.nuppin.company.Util.UtilShaPre;
import com.nuppin.company.tablet.MainStoreActivityTablet;

public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);

        Handler handle = new Handler();
        handle.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent it;
                if (UtilShaPre.getDefaultsBool("user_logged", SplashScreen.this)) {
                    if (getResources().getBoolean(R.bool.isTablet10) || getResources().getBoolean(R.bool.isTablet7)) {
                        it = new Intent(SplashScreen.this, MainStoreActivityTablet.class);
                        it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(it);
                    } else {
                        it = new Intent(SplashScreen.this, MainStoreActivity.class);
                        it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(it);
                    }

                } else {
                    it = new Intent(SplashScreen.this, TelaIntro.class);
                    it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(it);
                }
            }
        }, 2000);
    }
}