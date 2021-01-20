package com.ecomtrading.android;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Toast;

import com.ecomtrading.android.api.ApiClient;
import com.ecomtrading.android.api.ApiService;
import com.ecomtrading.android.db.MyDatabase;
import com.ecomtrading.android.entity.CommunityInformation;
import com.ecomtrading.android.utils.AppExecutor;
import com.ecomtrading.android.utils.Session;
import com.google.android.material.textfield.TextInputEditText;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    TextInputEditText community_name, geographical_distract, accessibility, distance, connectedToEcg;
    TextInputEditText  latitude, longitude, image;
    DatePicker dateLicense;
    Button btn_save, btn_upload;
    int RESULT_LOAD_IMG = 007;
    byte[] dataImg;
    String dataImgInBase64;
    CommunityInformation information;
    MyDatabase db;
    String communityName, connedtedEcg;
    int district, accessibility_text, distanceToECG;
    Long date_licence;
    Double latitude_text, longitude_text;
    Session session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setUI();
        session = new Session(this);
        btn_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intentPicker();
            }
        });

        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveToDb();

                AppExecutor executor = AppExecutor.getInstance();
                executor.diskIO().execute(new Runnable() {
                    @Override
                    public void run() {
                        db.dao().insertCommunityInfo(information);
                        Log.d("Main","executor");

                    }
                });
            }
        });


    }

    private void saveToDb() {
        db = MyDatabase.getInstance(this);

        //information = new CommunityInformation();
        communityName = community_name.getText().toString();
        district = Integer.parseInt(geographical_distract.getText().toString());
        accessibility_text = Integer.parseInt(accessibility.getText().toString());
        distanceToECG = Integer.parseInt(distance.getText().toString());
        connedtedEcg = connectedToEcg.getText().toString();
        date_licence = setDateToLong(dateLicense);
        latitude_text = Double.parseDouble(latitude.getText().toString());
        longitude_text = Double.parseDouble(longitude.getText().toString());

        String createDate = formatString();
        String user = "murali";
        String updateBy = "";
        String updateDate = "";

        information = new CommunityInformation(communityName, district, accessibility_text,
                distanceToECG, connedtedEcg, date_licence, latitude_text, longitude_text, dataImgInBase64
        ,user,createDate,updateBy,updateDate);

        ApiService service = ApiClient.getClient().create(ApiService.class);

        Call<ResponseBody> call = service.sendInformation(communityName, district, accessibility_text, distanceToECG, connedtedEcg, date_licence,
                latitude_text, longitude_text, dataImgInBase64,user,createDate,updateBy,updateDate);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
            Log.d("Main","Success");
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d("Main","failed to post");
            }
        });



    }
    private String formatString(){
        Date today = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss a", Locale.US);
        String date = format.format(today);
        return date;
    }

    private long setDateToLong(DatePicker date){
        String year = String.valueOf(date.getYear());
        String month = String.valueOf(date.getMonth());
        String day = String.valueOf(date.getDayOfMonth());
        String jour = year+"-"+month+"-"+day;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd",Locale.US);
        Date date_final ;
        try {
            date_final = sdf.parse(jour);
            long startDate = date_final.getTime();
            return startDate;
        } catch (ParseException e) {
            e.printStackTrace();
        }

        //long startDate = date_final.getTime();
        return 0;
    }

    private void setUI() {
        community_name = findViewById(R.id.edit_comm_name);
        geographical_distract = findViewById(R.id.edit_district);
        connectedToEcg = findViewById(R.id.edit_connect_ecg);
        dateLicense = findViewById(R.id.edit_date_licence);
        accessibility = findViewById(R.id.edit_accessibility);
        distance = findViewById(R.id.edit_distance_ecom);
        latitude = findViewById(R.id.edit_latitude);
        longitude = findViewById(R.id.edit_longitude);
        btn_save = findViewById(R.id.btn_save);
        btn_upload = findViewById(R.id.btn_upload);
        image = findViewById(R.id.edit_image);
    }

    private void intentPicker() {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, RESULT_LOAD_IMG);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (resultCode == RESULT_OK) {
            try {
                final Uri imageUri = data.getData();
                final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                dataImg = convertImgToBit(selectedImage);
                dataImgInBase64 = encodeToBase64(dataImg);
                image.setText("image has been uploaded");

                //image_view.setImageBitmap(selectedImage);
            } catch (FileNotFoundException e) {
                e.printStackTrace();

            }

        } else {
            Toast.makeText(MainActivity.this, "You haven't picked Image", Toast.LENGTH_LONG).show();
        }

    }

    private byte[] convertImgToBit(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        return byteArray;
    }

    private String encodeToBase64(byte[] byteArray) {
        String encoded = Base64.encodeToString(byteArray, Base64.DEFAULT);
        return encoded;
    }
}