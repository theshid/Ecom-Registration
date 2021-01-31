package com.ecomtrading.android;

import androidx.appcompat.widget.AppCompatEditText;
import androidx.hilt.Assisted;
import androidx.hilt.lifecycle.ViewModelInject;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;

import com.ecomtrading.android.api.ApiService;
import com.ecomtrading.android.databinding.FragmentEditBinding;
import com.ecomtrading.android.db.MyDatabase;
import com.ecomtrading.android.entity.CommunityInformation;
import com.ecomtrading.android.utils.AppExecutor;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;


public class FragmentEditViewModel extends ViewModel {
    ApiService apiService;
    MyDatabase database;
    AppExecutor appExecutor;
    CommunityInformation information;


    FragmentEditViewModel(ApiService apiService, MyDatabase database){
        this.apiService = apiService;
        this.database = database;
    }

    public CommunityInformation getCommunity(int id){

        AppExecutor.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                information = database.dao().getCommunity(id);
            }
        });
        return information;
    }

    public void updateCommunity(int id){

    }

    public void deleteCommunity(int id){

    }

    @Override
    protected void onCleared() {
        super.onCleared();
        
    }
}
