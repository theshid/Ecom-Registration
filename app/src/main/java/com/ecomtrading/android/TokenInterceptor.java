package com.ecomtrading.android;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.ecomtrading.android.api.ApiService;
import com.ecomtrading.android.utils.Session;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

import javax.inject.Inject;

import kotlin.contracts.ConditionalEffect;
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


    public TokenInterceptor(){
        context = EcomApplication.getContext();
        mPrefs = new Session(context);

    }

    @NotNull
    @Override
    public Response intercept(Chain chain) throws IOException {

        Request newRequest=chain.request().newBuilder()
                .header("Authorization", "Bearer "+mPrefs.getUserToken())
                .build();

        //get expire time from shared preferences
        long expireTime=1799;
        Calendar c = Calendar.getInstance();
        Date nowDate=c.getTime();
        c.setTimeInMillis(expireTime);
        Date expireDate=c.getTime();

        int result=nowDate.compareTo(expireDate);
        // when comparing dates -1 means date passed so we need to refresh token
        if(result==-1) {
            //refresh token here , and get new access token
            AccessToken tokenResponse = refreshToken();

            // Save refreshed token's expire time :
            int expiresIn=Integer.parseInt(tokenResponse.expires);
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.SECOND,expiresIn);
            mPrefs.saveExpiryTime(String.valueOf(calendar.getTimeInMillis()));

            String newaccessToken="Bearer "+ tokenResponse.token;
            newRequest=chain.request().newBuilder()
                    .header("Authorization", newaccessToken)
                    .build();
        }
        return chain.proceed(newRequest);
    }

    private AccessToken refreshToken() {

        Call<AccessToken> call = apiService.getToken("Bearer","murali",
                "welcome","password");

        call.enqueue(new Callback<AccessToken>() {
            @Override
            public void onResponse(Call<AccessToken> call, retrofit2.Response<AccessToken> response) {

                if (response.body() != null) {
                    Log.d("Interceptor","value of response"+ response.body().token);
                    mPrefs.saveToken(response.body().token);
                    mPrefs.saveExpiryTime(response.body().expires);
                    accessToken = response.body();
                }
                Log.d("Interceptor","value of response is null");

            }

            @Override
            public void onFailure(@NotNull Call<AccessToken> call, Throwable t) {
                Log.d("Interceptor","value of response"+"failed" +t.getMessage());
            }
        });
        return accessToken;
    }


}