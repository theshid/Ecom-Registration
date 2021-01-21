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
    EcomAdapter ecomAdapter;
    AppExecutor executor;
    List<CommunityInformation> informationList;
    MyDatabase db;
    Button btn_save_server, btn_token;
    ApiService apiService;
    Session session;
    ListViewModel listViewModel;
    public static String token;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        db = MyDatabase.getInstance(this);
        session = new Session(this);
        if (session.getUserToken() != null){
            token = session.getUserToken();
        }
        informationList = new ArrayList<>();
listViewModel= new ListViewModel(getApplication());

        Log.d("List","size of list:"+informationList.size());


        btn_save_server = findViewById(R.id.btn_save_server);
        btn_token = findViewById(R.id.btn_token);
        ecomAdapter = new EcomAdapter(this,informationList);
        recyclerView = findViewById(R.id.recycler_view);




        btn_save_server.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             Intent intent = new Intent(ListActivity.this,MainActivity.class);
             startActivity(intent);
            }
        });

       /* btn_token.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                apiService = ApiClient.getClient().create(ApiService.class);
                Call<AccessToken> call = apiService.sendIdentification("Bearer","murali",
                        "welcome","password");

                call.enqueue(new Callback<AccessToken>() {
                    @Override
                    public void onResponse(Call<AccessToken> call, Response<AccessToken> response) {

                            Log.d("List","value of response"+ response.body().token);
                        Toast.makeText(ListActivity.this,"Token received",Toast.LENGTH_LONG).show();
                        session.saveToken(response.body().token);
                    }

                    @Override
                    public void onFailure(Call<AccessToken> call, Throwable t) {
                        Log.d("List","value of response"+"failed" +t.getMessage());
                    }
                });

            }
        });*/

        listViewModel.getData().observe(this, new Observer<List<CommunityInformation>>() {
            @Override
            public void onChanged(List<CommunityInformation> communityInformations) {
                if (communityInformations != null){
                    informationList = communityInformations;
                }

                recyclerView.setAdapter(ecomAdapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

            }
        });



    }
}