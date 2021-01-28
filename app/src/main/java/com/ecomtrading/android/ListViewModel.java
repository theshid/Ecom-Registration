package com.ecomtrading.android;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.hilt.Assisted;
import androidx.hilt.lifecycle.ViewModelInject;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;

import com.ecomtrading.android.api.ApiService;
import com.ecomtrading.android.db.MyDatabase;
import com.ecomtrading.android.entity.CommunityInformation;
import com.ecomtrading.android.utils.AppExecutor;

import java.util.ArrayList;
import java.util.List;

public class ListViewModel extends ViewModel {
    private MutableLiveData<List<CommunityInformation>> communityList;
    LiveData<List<CommunityInformation>> infoFromDb;
    MyDatabase database;
    Context context;
    ApiService apiService;
    AppExecutor appExecutor;
    private final SavedStateHandle savedStateHandle;

    @ViewModelInject
    public ListViewModel(ApiService apiService, MyDatabase database,
                         @Assisted SavedStateHandle savedStateHandle) {

        this.apiService = apiService;
        this.database = database;
        this.savedStateHandle = savedStateHandle;

    }

    public LiveData<List<CommunityInformation>> getData() {
        if (communityList == null) {
            communityList = new MutableLiveData<>();
            loadCommunityList();
        }
        return communityList;
    }

    public void loadCommunityList() {
        communityList.setValue(database.dao().getAll().getValue());
    }
}
