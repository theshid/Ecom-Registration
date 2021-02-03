package com.ecomtrading.android.api.interceptors;

import android.content.Context;
import android.util.Log;

import com.ecomtrading.android.EcomApplication;
import com.ecomtrading.android.api.ApiClient;
import com.ecomtrading.android.api.ApiService;
import com.ecomtrading.android.utils.Session;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;

import okhttp3.Authenticator;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Route;
import retrofit2.Call;
import retrofit2.Callback;

public class TokenAuthenticator implements Authenticator {
    private static final String AUTHORIZATION = "Authorization";
    Context context;
    Session session;
    ApiService apiService;
    private static final String HEADER_AUTHORIZATION = "Authorization";
    private static final int REFRESH_TOKEN_FAIL = 401;
    AccessToken accessToken;

    public TokenAuthenticator() {


    }


    @Nullable
    @Override
    public Request authenticate(@Nullable Route route, @NotNull Response response) throws IOException {
       AccessToken newToken = refreshToken();
       return  response.request().newBuilder()
               .header(AUTHORIZATION, "Bearer " +session.getUserToken())
               .build();

    }

    private AccessToken refreshToken() {
        context = EcomApplication.getContext();
        session = new Session(context);
        apiService = ApiClient.getRetrofitInstance().create(ApiService.class);
        Call<AccessToken> call = apiService.getToken("Bearer", "murali",
                "welcome", "password");

        call.enqueue(new Callback<AccessToken>() {
            @Override
            public void onResponse(Call<AccessToken> call, retrofit2.Response<AccessToken> response) {

                if (response.body() != null) {

                    // mPrefs.saveExpiryTime(response.body().expires);
                    accessToken = response.body();
                    Log.d("Auth", "value of response" + accessToken.getToken());

                    session.saveToken(accessToken.getToken());
                    session.saveExpiryTime(accessToken.getExpires());
                    Log.d("Auth", "value of expiry time converted in calendar:" + accessToken.getExpires());
                }


            }

            @Override
            public void onFailure(@NotNull Call<AccessToken> call, Throwable t) {
                Log.d("Auth", "value of response" + "failederror" + t.getMessage());
            }
        });
        return accessToken;
    }


}
