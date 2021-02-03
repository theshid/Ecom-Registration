package com.ecomtrading.android.ui.list_activity;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.ecomtrading.android.api.ApiService;
import com.ecomtrading.android.db.MyDatabase;
import com.ecomtrading.android.ui.list_activity.ListViewModel;
import com.ecomtrading.android.utils.Session;

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
