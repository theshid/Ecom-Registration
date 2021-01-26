package com.ecomtrading.android;

import androidx.appcompat.widget.AppCompatEditText;
import androidx.hilt.lifecycle.ViewModelInject;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;

import com.ecomtrading.android.api.ApiService;
import com.ecomtrading.android.databinding.FragmentEditBinding;
import com.ecomtrading.android.db.MyDatabase;
import com.ecomtrading.android.entity.CommunityInformation;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;

public class FragmentEditViewModel extends ViewModel {
    private final SavedStateHandle savedStateHandle;
    ApiService apiService;
    MyDatabase database;

    @ViewModelInject
    public FragmentEditViewModel(ApiService apiService, MyDatabase database, SavedStateHandle savedStateHandle){
        this.apiService = apiService;
        this.database = database;
        this.savedStateHandle = savedStateHandle;
    }

    public CommunityInformation getCommunity(int id){
        return database.dao().getCommunity(id);
    }

    public void updateCommunity(int id){

    }

    public void deleteCommunity(int id){

    }
}
