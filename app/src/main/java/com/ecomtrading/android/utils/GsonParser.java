package com.ecomtrading.android.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class GsonParser {

    private static Gson gson;

    public static Gson getGsonParser() {
        if(gson ==null) {
            GsonBuilder builder = new GsonBuilder();
            gson = builder.create();
        }
        return gson;
    }
}
