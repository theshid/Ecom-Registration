package com.ecomtrading.android.ui.register_activity;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.hilt.Assisted;
import androidx.hilt.lifecycle.ViewModelInject;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;

import com.ecomtrading.android.EcomApplication;
import com.ecomtrading.android.api.ApiService;
import com.ecomtrading.android.db.MyDatabase;
import com.ecomtrading.android.entity.CommunityInformation;
import com.ecomtrading.android.utils.AppExecutor;

import net.steamcrafted.loadtoast.LoadToast;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class RegisterViewModel extends ViewModel {
    ApiService apiService;
    MyDatabase database;
    Context context;
    Boolean status_check = false;



    RegisterViewModel(ApiService apiService, MyDatabase database, Context context) {
        this.apiService = apiService;
        this.database = database;
        this.context = context;

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
                if (response.code() == 200) {
                    information.setSent_server(true);
                    status_check = true;
                    displayToast(status_check);
                    executor.diskIO().execute(new Runnable() {
                        @Override
                        public void run() {

                            database.dao().insertCommunityInfo(information);
                            Log.d("Main", "executor");

                        }
                    });
                } else {
                    information.setSent_server(false);
                    status_check = false;
                    displayToast(status_check);
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
                status_check = false;
                displayToast(status_check);
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

    private void displayToast(Boolean status) {
        if (status) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(context, "Data successfully sent to server!", Toast.LENGTH_LONG).show();
                }
            });

        } else {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(context, "Data not sent to server! Check Internet", Toast.LENGTH_LONG).show();
                }
            });

        }
    }
}
