package com.ecomtrading.android;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;


import com.ecomtrading.android.api.ApiService;
import com.ecomtrading.android.databinding.ActivityListBinding;
import com.ecomtrading.android.db.MyDatabase;
import com.ecomtrading.android.entity.CommunityInformation;
import com.ecomtrading.android.utils.AppExecutor;
import com.ecomtrading.android.utils.Session;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ListActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    EcomAdapter adapter;
    ArrayList<CommunityInformation> informationList = new ArrayList<>();
    private ActivityListBinding binding;
    Session session;
    ListViewModel listViewModel;
    public static String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityListBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        listViewModel = new ViewModelProvider(this).get(ListViewModel.class);
        setupRecyclerViewAndAdapter();


    }

    private void setupRecyclerViewAndAdapter() {
        adapter = new EcomAdapter(this, informationList, this, getSupportFragmentManager());
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerView.setAdapter(adapter);
    }

    public void refreshList() {
        adapter.updateList((ArrayList<CommunityInformation>) listViewModel.getData().getValue());
       // hideEmptyCommunityList();
    }
}