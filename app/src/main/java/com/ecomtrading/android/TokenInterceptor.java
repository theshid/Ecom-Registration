package com.ecomtrading.android;

import android.content.Context;

import com.ecomtrading.android.utils.Session;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

import javax.inject.Inject;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class TokenInterceptor implements Interceptor {
    Session mPrefs;

    @Inject
    public TokenInterceptor(Session mPrefs){
        this.mPrefs = mPrefs;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {

        Request newRequest=chain.request();

        //get expire time from shared preferences
        long expireTime=Long.parseLong(mPrefs.getExpiryTime());
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
            mPrefs.saveExpiryTime("expiretime",String.valueOf(calendar.getTimeInMillis()));

            String newaccessToken="new access token";
            newRequest=chain.request().newBuilder()
                    .header("Authorization", newaccessToken)
                    .build();
        }
        return chain.proceed(newRequest);
    }

    private AccessToken refreshToken() {
    }


}