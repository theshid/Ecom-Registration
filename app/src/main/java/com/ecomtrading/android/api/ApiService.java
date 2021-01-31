package com.ecomtrading.android.api;

import android.widget.SimpleAdapter;

import com.ecomtrading.android.AccessToken;
import com.ecomtrading.android.entity.CommunityInformation;

import java.text.SimpleDateFormat;
import java.util.List;

import dagger.hilt.android.scopes.ViewModelScoped;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;


public interface ApiService {

    @GET("api/CommunityRegistration/Masters?sMasterType={masterType}")
    Call<CommunityInformation> getData(@Path("masterType")String masterType);


    /*@POST("api/InstructionSheet/SaveCommunityData")
    @FormUrlEncoded
    Call<ResponseBody> sendInformation(
            @Body List<CommunityInformation> infoList
    );*/

    @POST("api/InstructionSheet/SaveCommunityData")
    @FormUrlEncoded
    Call<ResponseBody> sendInformation(
            @Field("communityName") String name,
            @Field("geographicalDistrict") int district,
            @Field("accessibility") int accessibility,
            @Field("distanceToECOM") int distance,
            @Field("connectedToEcg") String connected,
            @Field("licenseDate") String date_license,
            @Field("latitude") Double latitude,
            @Field("longitude") Double longitude,
            @Field("photo") String photo,
            @Field("createdBy") String createdBy,
            @Field("createdDate") String createdDate,
            @Field("updateBy") String updateBy,
            @Field("updateDate") String updateDate
    );


    @POST("api/token")
    @FormUrlEncoded
    Call<AccessToken> getToken(
            @Header("authorization") String auth,
            @Field("username") String userName,
            @Field("password") String password,
            @Field("grant_type") String pass
    );



}
