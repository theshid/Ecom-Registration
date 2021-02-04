package com.ecomtrading.android.ui.register_activity;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.ecomtrading.android.api.ApiService;
import com.ecomtrading.android.db.MyDatabase;
import com.ecomtrading.android.ui.register_activity.RegisterViewModel;

public class RegisterViewModelFactory implements ViewModelProvider.Factory {
    ApiService apiService;
    MyDatabase database;
    Context context;


    public RegisterViewModelFactory(ApiService apiService, MyDatabase database,Context context){
        this.apiService = apiService;
        this.database = database;
        this.context = context;

    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(RegisterViewModel.class)){
            return (T) new RegisterViewModel(apiService,database,context);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
