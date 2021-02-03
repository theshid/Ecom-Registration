package com.ecomtrading.android.ui.edit_fragment;

import androidx.annotation.NonNull;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.ecomtrading.android.api.ApiService;
import com.ecomtrading.android.db.MyDatabase;

import javax.inject.Inject;

public class FragmentEditViewModelFactory implements ViewModelProvider.Factory {
    ApiService apiService;
    MyDatabase database;


    public FragmentEditViewModelFactory(ApiService apiService,
                                        MyDatabase database){
        this.apiService = apiService;
        this.database = database;

    }
    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return null;
    }
}
