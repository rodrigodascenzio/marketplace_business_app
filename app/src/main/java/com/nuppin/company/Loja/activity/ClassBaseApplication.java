package com.nuppin.company.Loja.activity;

import android.app.Application;

import com.facebook.cache.disk.DiskCacheConfig;
import com.facebook.common.util.ByteConstants;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.backends.okhttp3.OkHttpImagePipelineConfigFactory;
import com.facebook.imagepipeline.core.ImagePipelineConfig;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

public class ClassBaseApplication extends Application {
        @Override
        public void onCreate() {
            super.onCreate();
            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .connectTimeout(120, TimeUnit.SECONDS)
                    .writeTimeout(120, TimeUnit.SECONDS)
                    .readTimeout(120, TimeUnit.SECONDS)
                   /* .addNetworkInterceptor(new Interceptor() {
                        @Override
                        public Response intercept(Chain chain) throws IOException {

                            Order.Builder builder = chain.request().newBuilder();
                            String token = UtilShaPre.getDefaultsString("auth_token", getApplicationContext());

                            if (!TextUtils.isEmpty(token)) {
                                builder.addHeader("Authorization", "Bearer " + token);
                            }
                            return chain.proceed(builder.build());
                        }
                    })*/
                    .build();

            ImagePipelineConfig config = OkHttpImagePipelineConfigFactory
                    .newBuilder(getApplicationContext(), okHttpClient)
                    .setMainDiskCacheConfig(DiskCacheConfig.newBuilder(this)
                            .setMaxCacheSize(100L * ByteConstants.MB)
                            .setMaxCacheSizeOnLowDiskSpace(10L * ByteConstants.MB)
                            .setMaxCacheSizeOnVeryLowDiskSpace(5L * ByteConstants.MB)
                            .build())
                    .build();
            Fresco.initialize(getApplicationContext(), config);
        }
}