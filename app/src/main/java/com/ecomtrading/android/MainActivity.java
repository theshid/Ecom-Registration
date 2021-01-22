package com.ecomtrading.android;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Toast;


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
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;
import io.nlopez.smartlocation.OnLocationUpdatedListener;
import io.nlopez.smartlocation.SmartLocation;
import okhttp3.ResponseBody;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.RuntimePermissions;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@RuntimePermissions
public class MainActivity extends AppCompatActivity {


    AppCompatEditText dateLicense;
    AppCompatButton btn_save, btn_location;
    int RESULT_LOAD_IMG = 007;
    byte[] dataImg;
    String dataImgInBase64;
    CommunityInformation information;
    MyDatabase db;
    String communityName, connedtedEcg;
    int district, accessibility_text, distanceToECG;
    Double latitude_text, longitude_text;
    Session session;
    CircleImageView circleImageView;
    Double lat, lgt;
    AppCompatEditText community_name, geoDistrict, accessibility, distance, connectedToEcg, latitude, longitude;
    private DatePickerDialog.OnDateSetListener date;
    private Calendar calendar;

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 999;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);


        setUI();
        prepareDatePicker();
        setBtnClickListeners();
        setEditTextClickListeners();
        getUserLocation();
        session = new Session(this);


    }

    @NeedsPermission(Manifest.permission_group.LOCATION)
    private void getUserLocation() {
        SmartLocation.with(this).location()
                .oneFix()
                .start(new OnLocationUpdatedListener() {
                    @Override
                    public void onLocationUpdated(Location location) {
                        lat = location.getLatitude();
                        lgt = location.getLongitude();
                    }
                });
    }

    private void setUI() {
        community_name = findViewById(R.id.community_name);
        circleImageView = findViewById(R.id.community_photo);
        geoDistrict = findViewById(R.id.geographical_district);
        connectedToEcg = findViewById(R.id.connected_ecg);
        btn_location = findViewById(R.id.location_button);
        dateLicense = findViewById(R.id.license_date);
        accessibility = findViewById(R.id.accessibility);
        distance = findViewById(R.id.distance_ecom);
        latitude = findViewById(R.id.latitude);
        longitude = findViewById(R.id.longitude);
        btn_save = findViewById(R.id.submit_button);

    }

    public void setEditTextClickListeners() {
        connectedToEcg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openConnectedToEcgDialog(v);
            }
        });

        accessibility.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openAccessibilityDialog(v);
            }
        });

        geoDistrict.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGeoDistricDialog(v);
            }
        });

        distance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDistanceDialog(v);
            }
        });

        dateLicense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDatePicker();
            }
        });
    }

    private void setBtnClickListeners() {
        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveToDb();

                AppExecutor executor = AppExecutor.getInstance();
                executor.diskIO().execute(new Runnable() {
                    @Override
                    public void run() {
                        db.dao().insertCommunityInfo(information);
                        Log.d("Main", "executor");

                    }
                });
            }
        });

        btn_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (lat == null || lgt == null){
                    getUserLocation();
                }
                latitude.setText(String.valueOf(lat));
                longitude.setText(String.valueOf(lgt));
            }
        });
    }

    public void openConnectedToEcgDialog(View view) {
        String[] ecgArray = getResources().getStringArray(R.array.connected_ecg);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.label_connected_ecg)
                .setItems(R.array.connected_ecg, (dialog, which) -> {
                    if (which == 0) {
                        // community.setConnectedecg("Y");
                        connectedToEcg.setText("Y");
                    } else {
                        //community.setConnectedecg("N");
                        connectedToEcg.setText("N");

                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void openGeoDistricDialog(View view) {
        String[] districtArray = getResources().getStringArray(R.array.geo_district);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.label_connected_ecg)
                .setItems(R.array.connected_ecg, (dialog, which) -> {
                    switch (which) {
                        case 0:
                            geoDistrict.setText(districtArray[0]);
                            break;
                        case 1:
                            geoDistrict.setText(districtArray[1]);
                            break;

                        case 2:
                            geoDistrict.setText(districtArray[2]);
                            break;

                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void openDistanceDialog(View view) {
        String[] distanceArray = getResources().getStringArray(R.array.ecom_distance);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.label_connected_ecg)
                .setItems(R.array.connected_ecg, (dialog, which) -> {
                    switch (which) {
                        case 0:
                            distance.setText(distanceArray[0]);
                            break;
                        case 1:
                            distance.setText(distanceArray[1]);
                            break;

                        case 2:
                            distance.setText(distanceArray[2]);
                            break;
                        case 3:
                            distance.setText(distanceArray[3]);
                            break;

                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void openAccessibilityDialog(View view) {
        String[] accessibilityArray = getResources().getStringArray(R.array.accessibility_options);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.label_connected_ecg)
                .setItems(R.array.connected_ecg, (dialog, which) -> {
                    switch (which) {
                        case 0:
                            accessibility.setText(accessibilityArray[0]);
                            break;
                        case 1:
                            accessibility.setText(accessibilityArray[1]);
                            break;

                        case 2:
                            accessibility.setText(accessibilityArray[2]);
                            break;

                        case 3:
                            accessibility.setText(accessibilityArray[3]);
                            break;

                        case 4:
                            accessibility.setText(accessibilityArray[4]);
                            break;


                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void prepareDatePicker() {
        calendar = Calendar.getInstance();
        date = (view, year, monthOfYear, dayOfMonth) -> {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, monthOfYear);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateDateUI();
        };
    }

    private void updateDateUI() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.UK);
        dateLicense.setText(sdf.format(calendar.getTime()));
    }

    private void openDatePicker() {
        new DatePickerDialog(this, date, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void saveToDb() {
        db = MyDatabase.getInstance(this);

        //information = new CommunityInformation();


        String createDate = formatString();
        String user = "murali";
        String updateBy = "";
        String updateDate = "";

        /*information = new CommunityInformation(communityName, district, accessibility_text,
                distanceToECG, connedtedEcg, date_licence, latitude_text, longitude_text, dataImgInBase64
        ,user,createDate,updateBy,updateDate);*/

        // ApiService service = ApiClient.getClient().create(ApiService.class);

       /* Call<ResponseBody> call = service.sendInformation(communityName, district, accessibility_text, distanceToECG, connedtedEcg, date_licence,
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
        });*/


    }

    private String formatString() {
        Date today = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss a", Locale.US);
        String date = format.format(today);
        return date;
    }

    private long setDateToLong(DatePicker date) {
        String year = String.valueOf(date.getYear());
        String month = String.valueOf(date.getMonth());
        String day = String.valueOf(date.getDayOfMonth());
        String jour = year + "-" + month + "-" + day;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        Date date_final;
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