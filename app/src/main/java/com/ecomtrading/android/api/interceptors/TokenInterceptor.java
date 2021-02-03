package com.ecomtrading.android.api.interceptors;

import android.content.Context;
import android.util.Log;

import com.ecomtrading.android.EcomApplication;
import com.ecomtrading.android.api.ApiClient;
import com.ecomtrading.android.api.ApiService;
import com.ecomtrading.android.utils.Session;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Call;
import retrofit2.Callback;

public class TokenInterceptor implements Interceptor {
    Session mPrefs;
    ApiService apiService;
    AccessToken accessToken;
    Context context;


    public TokenInterceptor() {
        context = EcomApplication.getContext();
        mPrefs = new Session(context);


    }

    @NotNull
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request newRequest;

        if (mPrefs.getUserToken() != null ) {

                newRequest = chain.request().newBuilder()
                        .header("Authorization", "Bearer " + mPrefs.getUserToken())
                        .build();
            return  chain.proceed(newRequest);

        } else{
            return null;
        }

    }

    private int checkIfTokenExpire(String tokenExpiryTime) {
        //get expire time from shared preferences
        long expireTime = Long.parseLong(tokenExpiryTime);
        Calendar c = Calendar.getInstance();
        Date nowDate = c.getTime();
        c.setTimeInMillis(expireTime);
        Date expireDate = c.getTime();
        Log.d("Interceptor", "value of expiry time:" + expireDate);
        int result = nowDate.compareTo(expireDate);
        return result;
    }

    private Request builtNewRequest(Chain chain) {
        //refresh token here , and get new access token
        AccessToken tokenResponse = refreshToken();

        // Save refreshed token's expire time :
        int expiresIn = Integer.parseInt(tokenResponse.expires);
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.SECOND, expiresIn);
        mPrefs.saveExpiryTime(String.valueOf(calendar.getTimeInMillis()));
        Log.d("Interceptor", "value of expiry time converted in calendar:" + calendar.getTimeInMillis());

        String newaccessToken = "Bearer " + tokenResponse.token;
        return chain.request().newBuilder()
                .header("Authorization", newaccessToken)
                .build();
    }

    private AccessToken refreshToken() {
        apiService = ApiClient.getRetrofitInstance().create(ApiService.class);
        Call<AccessToken> call = apiService.getToken("Bearer", "murali",
                "welcome", "password");

        /*try {
            retrofit2.Response<AccessToken> response = call.execute();
             accessToken = response.body();
           // Log.d("Interceptor", "value of response" + accessToken.getToken());
        } catch (IOException e) {
            e.printStackTrace();
        }*/
        call.enqueue(new Callback<AccessToken>() {
            @Override
            public void onResponse(Call<AccessToken> call, retrofit2.Response<AccessToken> response) {

                if (response.body() != null) {

                    // mPrefs.saveExpiryTime(response.body().expires);
                    accessToken = response.body();
                    Log.d("Interceptor", "value of response" + accessToken.getToken());

                    mPrefs.saveToken(accessToken.getToken());
                    int expiresIn = Integer.parseInt(accessToken.getExpires());
                    Calendar calendar = Calendar.getInstance();
                    calendar.add(Calendar.SECOND, expiresIn);
                    mPrefs.saveExpiryTime(String.valueOf(calendar.getTimeInMillis()));
                    Log.d("Interceptor", "value of expiry time converted in calendar:" + calendar.getTimeInMillis());
                }
                Log.d("Interceptor", "value of response is null");

            }

            @Override
            public void onFailure(@NotNull Call<AccessToken> call, Throwable t) {
                Log.d("Interceptor", "value of response" + "failederror" + t.getMessage());
            }
        });
        return accessToken;
    }


}