package com.ecomtrading.android;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.ecomtrading.android.db.MyDatabase;
import com.ecomtrading.android.entity.CommunityInformation;
import com.ecomtrading.android.utils.AppExecutor;

import java.util.ArrayList;
import java.util.List;

public class ListViewModel extends AndroidViewModel {

    LiveData<List<CommunityInformation>> infoFromDb;
    MyDatabase myDatabase;
    Context context;
    AppExecutor appExecutor;


    public ListViewModel(@NonNull Application application) {
        super(application);
        myDatabase = MyDatabase.getInstance(context);
        appExecutor = AppExecutor.getInstance();
        appExecutor.diskIO().execute(new Runnable() {
            @Override
            public void run() {
                infoFromDb = myDatabase.dao().getAll();
            }
        });
    }

    public LiveData<List<CommunityInformation>> getData() {
        if (infoFromDb == null) {
            infoFromDb = myDatabase.dao().getAll();
        }
        return infoFromDb;
    }
}
