package com.ecomtrading.android;

import androidx.annotation.NonNull;
import androidx.hilt.Assisted;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.ecomtrading.android.api.ApiService;
import com.ecomtrading.android.db.MyDatabase;

import javax.inject.Inject;

public class RegisterViewModelFactory implements ViewModelProvider.Factory {
    ApiService apiService;
    MyDatabase database;


    public RegisterViewModelFactory(ApiService apiService, MyDatabase database){
        this.apiService = apiService;
        this.database = database;

    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(RegisterViewModel.class)){
            return (T) new RegisterViewModel(apiService,database);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
