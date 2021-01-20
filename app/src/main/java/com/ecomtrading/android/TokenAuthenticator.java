package com.ecomtrading.android;

import android.content.Context;

import com.ecomtrading.android.api.ApiService;
import com.ecomtrading.android.utils.Session;
import com.google.gson.JsonObject;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;

import javax.inject.Inject;

import okhttp3.Authenticator;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Route;

public class TokenAuthenticator implements Authenticator {
    Context context;
    Session session;
    ApiService apiService;
    private static final String HEADER_AUTHORIZATION = "Authorization";
    private static final int REFRESH_TOKEN_FAIL = 403;
    @Inject
    public TokenAuthenticator(Context context,Session session,ApiService apiService) {
         this.context= context;
         this.session = session;
         this.apiService = apiService;
    }


    @Nullable
    @Override
    public Request authenticate(@Nullable Route route, @NotNull Response response) throws IOException {
        session = new Session(context);
        // We need to have a token in order to refresh it.
        String token = session.getUserToken();
        if (token == null)
            return null;

        synchronized (this) {
            String newToken = session.getUserToken();
            if (newToken == null)
                return null;

            // Check if the request made was previously made as an authenticated request.
            if (response.request().header(HEADER_AUTHORIZATION) != null) {

                // If the token has changed since the request was made, use the new token.
                if (!newToken.equals(token)) {
                    return response.request()
                            .newBuilder()
                            .removeHeader(HEADER_AUTHORIZATION)
                            .addHeader(HEADER_AUTHORIZATION, "Bearer " + newToken)
                            .build();
                }

                JsonObject refreshObject = new JsonObject();
                refreshObject.addProperty("refreshToken", session.getUserToken());

                retrofit2.Response<AccessToken> tokenResponse =  apiService.sendIdentification("Bearer","murali",
                        "welcome","password").execute();

                if (tokenResponse.isSuccessful()) {

                    AccessToken userToken = tokenResponse.body();
                    if (userToken == null)
                        return null;

                    session.saveToken(userToken.getToken());
                    //preferencesHelper.saveRefreshToken(userToken.getRefreshToken());


                    // Retry the request with the new token.
                    return response.request()
                            .newBuilder()
                            .removeHeader(HEADER_AUTHORIZATION)
                            .addHeader(HEADER_AUTHORIZATION, "Bearer " + userToken.getToken())
                            .build();
                } else {
                    if (tokenResponse.code() == REFRESH_TOKEN_FAIL) {
                        logoutUser();
                    }
                }
            }
        }
        return null;
    }

    private void logoutUser() {
    }


}
