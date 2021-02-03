package com.ecomtrading.android.ui.register_activity;

import android.content.Context;
import android.util.Log;

import androidx.hilt.Assisted;
import androidx.hilt.lifecycle.ViewModelInject;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;

import com.ecomtrading.android.api.ApiService;
import com.ecomtrading.android.db.MyDatabase;
import com.ecomtrading.android.entity.CommunityInformation;
import com.ecomtrading.android.utils.AppExecutor;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class RegisterViewModel extends ViewModel {
    ApiService apiService;
    MyDatabase database;




    RegisterViewModel(ApiService apiService, MyDatabase database) {
        this.apiService = apiService;
        this.database = database;
    }

    public void saveCommunityToDb(CommunityInformation information) {
        Call<ResponseBody> call = apiService.sendInformation(information.getCommunity_name(),
                information.getGeographical_district(), information.getAccessibility(),
                information.getDistance(), information.getConnected_to_ecg(), information.getDate_licence(),
                information.getLatitude(), information.getLongitude(), information.getImage(),
                information.getCreatedBy(), information.getCreatedDate(), information.getUpdateBy(),
                information.getUpdateDate());
        AppExecutor executor = AppExecutor.getInstance();

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.code() == 200){
                    information.setSent_server(true);
                    executor.diskIO().execute(new Runnable() {
                        @Override
                        public void run() {

                            database.dao().insertCommunityInfo(information);
                            Log.d("Main", "executor");

                        }
                    });
                }else{
                    information.setSent_server(false);
                    executor.diskIO().execute(new Runnable() {
                        @Override
                        public void run() {

                            database.dao().insertCommunityInfo(information);
                            Log.d("Main", "executor");

                        }
                    });
                }
                Log.d("Main", "Success");

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d("Main", "failed to post");
                executor.diskIO().execute(new Runnable() {
                    @Override
                    public void run() {

                        database.dao().insertCommunityInfo(information);
                        Log.d("Main", "executor");

                    }
                });

            }
        });

    }
}
