package com.ecomtrading.android.modules;

import android.content.Context;

import com.ecomtrading.android.api.interceptors.TokenAuthenticator;
import com.ecomtrading.android.api.interceptors.TokenInterceptor;
import com.ecomtrading.android.api.ApiService;
import com.ecomtrading.android.utils.Session;

import java.util.concurrent.TimeUnit;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.qualifiers.ApplicationContext;
import dagger.hilt.components.SingletonComponent;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

@Module
@InstallIn({SingletonComponent.class})
public class NetworkModule {
    private static final String BASE_URL ="http://41.139.29.11/CropDoctorWebAPI/";

    @Provides
    @Singleton
     public static OkHttpClient provideOkHttpClient(@ApplicationContext Context context, Session session,ApiService apiService){
        return new OkHttpClient.Builder()
                .addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                .authenticator(new TokenAuthenticator())
                .addInterceptor(new TokenInterceptor())
                .connectTimeout(120, TimeUnit.SECONDS)
                .readTimeout(120,TimeUnit.SECONDS)
                .build();
    }

    @Provides
    @Singleton
    public static Retrofit provideRetrofit(OkHttpClient okHttpClient){
        return new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build();
    }

    @Provides
    @Singleton
    public static ApiService provideApiServices( Retrofit retrofit){
        return retrofit.create(ApiService.class);
    }

}
