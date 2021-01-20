package com.ecomtrading.android;

import com.google.gson.annotations.SerializedName;

public class AccessToken {

    @SerializedName("access_token")
    String token;
    @SerializedName("token_type")
    String token_type;
    @SerializedName("expires_in")
    String expires;

    public AccessToken(String token, String token_type, String expires) {
        this.token = token;
        this.token_type = token_type;
        this.expires = expires;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getToken_type() {
        return token_type;
    }

    public void setToken_type(String token_type) {
        this.token_type = token_type;
    }

    public String getExpires() {
        return expires;
    }

    public void setExpires(String expires) {
        this.expires = expires;
    }
}
