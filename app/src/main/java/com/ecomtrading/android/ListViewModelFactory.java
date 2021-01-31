package com.ecomtrading.android;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.hilt.Assisted;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.ecomtrading.android.api.ApiService;
import com.ecomtrading.android.db.MyDatabase;
import com.ecomtrading.android.utils.Session;

import javax.inject.Inject;

public class ListViewModelFactory implements ViewModelProvider.Factory {
    MyDatabase database;
    ApiService apiService;
    Session session;



    public ListViewModelFactory(ApiService apiService, MyDatabase database,Session session){
        this.apiService = apiService;
        this.database = database;
        this.session = session;

    }
    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(ListViewModel.class)){
            return (T) new ListViewModel(apiService,database,session);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
