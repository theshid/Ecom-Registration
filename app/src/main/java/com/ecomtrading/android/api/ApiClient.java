package com.ecomtrading.android.api;

import android.content.Context;

import com.ecomtrading.android.TokenAuthenticator;
import com.ecomtrading.android.TokenInterceptor;
import com.ecomtrading.android.utils.Session;

import java.util.concurrent.TimeUnit;

import javax.inject.Singleton;

import dagger.Provides;
import dagger.hilt.android.qualifiers.ApplicationContext;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    private static final String BASE_URL = "http://41.139.29.11/CropDoctorWebAPI/";
    public static Retrofit retrofit;
    public static OkHttpClient okHttpClient;



    private static void provideOkHttpClient() {
        if (okHttpClient == null) {
            okHttpClient = new OkHttpClient.Builder()
                    .authenticator(new TokenAuthenticator())
                    /*.addInterceptor(new TokenInterceptor())*/
                    .addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                    .connectTimeout(120, TimeUnit.SECONDS)
                    .readTimeout(120, TimeUnit.SECONDS)
                    .build();
        }

    }


    public static Retrofit getRetrofitInstance() {
        if (okHttpClient == null) {
            provideOkHttpClient();
        }
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(okHttpClient)
                    .build();
        }
        return retrofit;
    }
}
