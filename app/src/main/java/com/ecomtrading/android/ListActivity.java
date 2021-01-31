package com.ecomtrading.android;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;


import com.ecomtrading.android.api.ApiClient;
import com.ecomtrading.android.api.ApiService;
import com.ecomtrading.android.databinding.ActivityListBinding;
import com.ecomtrading.android.db.MyDatabase;
import com.ecomtrading.android.entity.CommunityInformation;
import com.ecomtrading.android.utils.Session;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;


public class ListActivity extends AppCompatActivity {


    EcomAdapter adapter;
    List<CommunityInformation> informationList = new ArrayList<>();
    Session session;
    MyDatabase database;
    ApiService apiService;
    ListViewModel listViewModel;
    RecyclerView recyclerView;
    FloatingActionButton fab;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_list);
        apiService = ApiClient.getRetrofitInstance().create(ApiService.class);
        database = MyDatabase.getInstance(this);
        session = new Session(this);
        fab = findViewById(R.id.fab);
        recyclerView = findViewById(R.id.recycler_view);
        setupRecyclerViewAndAdapter();
        setViewModel();

        checkIfTokenAvailable();

        setClickListeners();
        //refreshList();


    }

    private void setViewModel() {
        listViewModel = new ViewModelProvider(this,
                new ListViewModelFactory(apiService, database, session)).get(ListViewModel.class);
        listViewModel.getData().observe(this, new Observer<List<CommunityInformation>>() {
            @Override
            public void onChanged(List<CommunityInformation> communityInformations) {
                informationList = communityInformations;
                refreshList();
                Log.d("ListActivity", String.valueOf(informationList.size()));

            }
        });
    }

    private void checkIfTokenAvailable() {
        if (session.getUserToken() == null) {
            listViewModel.getToken();
        }

    }

    private void setClickListeners() {
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ListActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

    private void setupRecyclerViewAndAdapter() {

        adapter = new EcomAdapter(ListActivity.this, informationList, ListActivity.this, getSupportFragmentManager());
        recyclerView.setLayoutManager(new LinearLayoutManager(ListActivity.this));
        recyclerView.setAdapter(adapter);

    }

    public void refreshList() {
        adapter.updateList((ArrayList<CommunityInformation>) listViewModel.getData().getValue());
        // hideEmptyCommunityList();
    }
}