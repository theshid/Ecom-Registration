package com.ecomtrading.android;

import android.content.Context;

import com.ecomtrading.android.utils.Session;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class TokenInterceptor implements Interceptor {


    @Override
    public Response intercept(Chain chain) throws IOException {

        //rewrite the request to add bearer token
        Request newRequest = chain.request().newBuilder()
                .header("Authorization", "Bearer " + ListActivity.token)
                .build();

        return chain.proceed(newRequest);
    }
}