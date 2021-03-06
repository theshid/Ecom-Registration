package com.ecomtrading.android.ui.list_activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;


import com.ecomtrading.android.ui.adapter.EcomAdapter;
import com.ecomtrading.android.ui.register_activity.MainActivity;
import com.ecomtrading.android.R;
import com.ecomtrading.android.api.ApiClient;
import com.ecomtrading.android.api.ApiService;
import com.ecomtrading.android.db.MyDatabase;
import com.ecomtrading.android.entity.CommunityInformation;
import com.ecomtrading.android.utils.Session;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import net.steamcrafted.loadtoast.LoadToast;

import java.util.ArrayList;
import java.util.List;


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
        adapter.updateList(listViewModel.getData().getValue());
        // hideEmptyCommunityList();
    }
}