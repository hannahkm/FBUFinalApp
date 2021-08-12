package com.example.fbufinalapp;

import android.app.Application;

import com.example.fbufinalapp.models.Destination;
import com.example.fbufinalapp.models.Itinerary;
import com.parse.Parse;
import com.parse.ParseObject;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

public class ParseApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        Parse.setLogLevel(Parse.LOG_LEVEL_DEBUG);

        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        builder.networkInterceptors().add(httpLoggingInterceptor);

        // Register your parse models
        ParseObject.registerSubclass(Itinerary.class);
        ParseObject.registerSubclass(Destination.class);

        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId("QOiyHYg3hkWzAwm4czcfsXFxt8gu8oqttvnc1RCd")
                .clientKey("3tSA5h6wyASjAOxM1YmdETGornw4eZzL1TeumOF5")
                .server("https://parseapi.back4app.com")
                .build()
        );

    }
}
