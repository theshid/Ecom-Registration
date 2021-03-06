package com.ecomtrading.android.ui.list_activity;

import android.app.Application;
import android.content.Context;
import android.util.Log;

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
import com.ecomtrading.android.utils.Session;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ListViewModel extends ViewModel {
    private MutableLiveData<List<CommunityInformation>> communityList;
    LiveData<List<CommunityInformation>> infoFromDb;
    MyDatabase database;
    Context context;
    ApiService apiService;
    AppExecutor appExecutor;
    Session session;



    public ListViewModel(ApiService apiService, MyDatabase database,Session session) {

        this.apiService = apiService;
        this.database = database;
        this.session = session;
        infoFromDb = database.dao().getAll();
        //loadCommunityList();

    }

    public LiveData<List<CommunityInformation>> getData() {
        if (communityList == null) {
            communityList = new MutableLiveData<>();
            loadCommunityList();
        }
        return infoFromDb;
    }

    public void loadCommunityList() {
        //communityList.setValue(database.dao().getAll().getValue());

    }

}
