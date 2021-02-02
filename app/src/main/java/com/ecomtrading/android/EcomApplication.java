package com.ecomtrading.android;

import android.app.Application;
import android.content.Context;

import com.facebook.stetho.Stetho;

import dagger.hilt.android.HiltAndroidApp;


public class EcomApplication extends Application {
    private static EcomApplication instance;

    public static EcomApplication getInstance() {
        return instance;
    }

    public static Context getContext(){
        return instance;
    }

    @Override
    public void onCreate() {
        instance = this;
        super.onCreate();
        Stetho.initializeWithDefaults(this);
    }
}
