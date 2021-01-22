package com.ecomtrading.android;

import android.util.Log;

import androidx.lifecycle.ViewModel;

import com.ecomtrading.android.api.ApiService;
import com.ecomtrading.android.db.MyDatabase;
import com.ecomtrading.android.entity.CommunityInformation;

import javax.inject.Inject;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterViewModel extends ViewModel{
ApiService apiService;
MyDatabase database;

    @Inject
    public RegisterViewModel(ApiService apiService, MyDatabase database){
        this.apiService = apiService;
        this.database = database;
    }

    public void saveCommunityToDb(CommunityInformation information){
        Call<ResponseBody> call = apiService.sendInformation(information.getCommunity_name(),
                information.getGeographical_district(), information.getAccessibility(),
                information.getDistance(), information.getConnected_to_ecg(), information.getDate_licence(),
                information.getLatitude(), information.getLongitude(), information.getImage(),
                information.getCreatedBy(),information.getCreatedDate(),information.getUpdateBy(),
                information.getUpdateDate());
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.d("Main","Success");
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d("Main","failed to post");
            }
        });
    }
}
