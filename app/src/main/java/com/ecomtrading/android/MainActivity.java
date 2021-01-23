package com.ecomtrading.android;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.Toast;


import com.ecomtrading.android.db.MyDatabase;
import com.ecomtrading.android.entity.CommunityInformation;
import com.ecomtrading.android.utils.AppExecutor;
import com.ecomtrading.android.utils.Session;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;

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
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.RuntimePermissions;

@RuntimePermissions
public class MainActivity extends AppCompatActivity {


    @NotEmpty
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
    @NotEmpty
    AppCompatEditText community_name, geoDistrict, accessibility, distance, connectedToEcg, latitude, longitude;
    private DatePickerDialog.OnDateSetListener date;
    private Calendar calendar;
    private FusedLocationProviderClient fusedLocationClient;


    private static final int LOCATION_PERMISSION_REQUEST_CODE = 999;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);



        setUI();
        prepareDatePicker();
        setBtnClickListeners();
        setEditTextClickListeners();
        checkIfPermissionIsActive();

        session = new Session(this);


    }

    @SuppressLint("MissingPermission")
    @NeedsPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
    public void getUserLocation() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        SmartLocation.with(this).location()
                .oneFix()
                .start(new OnLocationUpdatedListener() {
                    @Override
                    public void onLocationUpdated(Location location) {
                        lat = location.getLatitude();
                        lgt = location.getLongitude();
                    }
                });


        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            lat = location.getLatitude();
                            lgt = location.getLongitude();
                        }
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
                openGeoDistrictDialog(v);
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
                sendDataToViewModel();

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
                latitude.setText(String.valueOf(lat));
                longitude.setText(String.valueOf(lgt));


            }
        });
    }

    public void openConnectedToEcgDialog(View view) {
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

    public void openGeoDistrictDialog(View view) {
        String[] districtArray = getResources().getStringArray(R.array.geo_district);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.label_connected_ecg)
                .setItems(R.array.geo_district, (dialog, which) -> {
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
                .setItems(R.array.ecom_distance, (dialog, which) -> {
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
                .setItems(R.array.accessibility_options, (dialog, which) -> {
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

    private void sendDataToViewModel() {


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

    private void checkIfPermissionIsActive() {
        if (isAccessFineLocationGranted(this)) {
            if (isLocationEnabled(this)) {
                getUserLocation();
            } else {
                showGPSNotEnabledDialog(this);
            }
        } else {
            requestAccessFineLocationPermission(this, LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    /**
     * Function to request permission from the user
     */
    public void requestAccessFineLocationPermission(AppCompatActivity activity, int requestId) {
        ActivityCompat.requestPermissions(
                activity,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                requestId
        );
    }

    /**
     * Function to check if the location permissions are granted or not
     */
    public Boolean isAccessFineLocationGranted(Context context) {
        return ContextCompat
                .checkSelfPermission(
                        context,
                        Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * Function to check if location of the device is enabled or not
     */
    public Boolean isLocationEnabled(Context context) {
        LocationManager locationManager =
                (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    /**
     * Function to show the "enable GPS" Dialog box
     */
    public void showGPSNotEnabledDialog(Context context) {
        new AlertDialog.Builder(context)
                .setTitle(context.getString(R.string.enable_gps))
                .setMessage(context.getString(R.string.required_for_this_app))
                .setCancelable(false)
                .setPositiveButton(context.getString(R.string.enable_now), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        context.startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .show();


    }

}
