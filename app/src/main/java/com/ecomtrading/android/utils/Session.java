package com.ecomtrading.android.utils;

import android.content.Context;
import android.content.SharedPreferences;

import javax.inject.Inject;

import dagger.hilt.android.qualifiers.ActivityContext;

public class Session {
    Context context;
    SharedPreferences preferences;

    public Session(Context context) {
        this.context = context;
        preferences = context.getSharedPreferences("file",Context.MODE_PRIVATE);
    }

    public static String USER_TOKEN = "token";

    public void saveToken(String token){
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("user_token",token);
        editor.apply();
    }

    public void saveExpiryTime( String expiryTime){
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("expiry",expiryTime);
        editor.apply();
    }

    public String getExpiryTime(){
        return preferences.getString("expiry",null);
    }

    public String getUserToken(){
        return preferences.getString("user_token",null);
    }
}
