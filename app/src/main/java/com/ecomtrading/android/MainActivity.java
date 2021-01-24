package com.ecomtrading.android;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.ActivityNotFoundException;
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
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Base64;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;


import com.ecomtrading.android.db.MyDatabase;
import com.ecomtrading.android.entity.CommunityInformation;
import com.ecomtrading.android.utils.PermissionUtils;
import com.ecomtrading.android.utils.Session;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.Checked;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import dagger.hilt.android.AndroidEntryPoint;
import de.hdodenhof.circleimageview.CircleImageView;
import io.nlopez.smartlocation.OnLocationUpdatedListener;
import io.nlopez.smartlocation.SmartLocation;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.RuntimePermissions;

import static com.ecomtrading.android.utils.ConversionUtils.bitmapToBase64;
import static com.ecomtrading.android.utils.PermissionUtils.checkCameraPermissions;
import static com.ecomtrading.android.utils.PermissionUtils.isLocationEnabled;
import static com.ecomtrading.android.utils.PermissionUtils.requestAccessFineLocationPermission;
import static com.ecomtrading.android.utils.PermissionUtils.showGPSNotEnabledDialog;

@AndroidEntryPoint
public class MainActivity extends AppCompatActivity implements Validator.ValidationListener {


    private RegisterViewModel viewModel;
    private static final int REQUEST_CAMERA = 87;
    private static final int REQUEST_GALLERY = 434;
    @NotEmpty
    AppCompatEditText dateLicense;
    AppCompatButton btn_save, btn_location;
    int RESULT_LOAD_IMG = 007;
    byte[] dataImg;
    String imgInString = "";
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
    Validator validator;
    TextView textView_photo;
    @Checked(message = "You need to upload an image")
    private CheckBox imgLoadedCheckBox;


    private static final int LOCATION_PERMISSION_REQUEST_CODE = 999;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);


        setUI();
        viewModel = new ViewModelProvider(this).get(RegisterViewModel.class);
        hideSoftKeyboard();
        checkCameraPermissions(this);
        validator = new Validator(this);
        validator.setValidationListener(this);
        prepareDatePicker();
        setBtnClickListeners();
        setEditTextClickListeners();
        checkIfPermissionIsActive();

        session = new Session(this);


    }

    @SuppressLint("MissingPermission")
    public void getUserLocation() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
       /* SmartLocation.with(this).location()
                .oneFix()
                .start(new OnLocationUpdatedListener() {
                    @Override
                    public void onLocationUpdated(Location location) {
                        lat = location.getLatitude();
                        lgt = location.getLongitude();
                    }
                });*/


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
        textView_photo = findViewById(R.id.add_photo);
        imgLoadedCheckBox = findViewById(R.id.checkBox);

    }

    private void hideSoftKeyboard() {
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
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
                validator.validate();
            }
        });

        btn_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                latitude.setText(String.valueOf(lat));
                longitude.setText(String.valueOf(lgt));


            }
        });

        textView_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupMenu(v);
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
        String[] districtArray = getResources().getStringArray(R.array.accessibility_numbers);
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
        String[] distanceArray = getResources().getStringArray(R.array.accessibility_numbers);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.label_connected_ecg)
                .setItems(R.array.ecom_distance, (dialog, which) -> {
                    switch (which) {
                        case 0:
                            distance.setText(distanceArray[5]);
                            break;
                        case 1:
                            distance.setText(distanceArray[6]);
                            break;

                        case 2:
                            distance.setText(distanceArray[7]);
                            break;
                        case 3:
                            distance.setText(distanceArray[8]);
                            break;

                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void openAccessibilityDialog(View view) {
        String[] accessibilityArray = getResources().getStringArray(R.array.accessibility_options);
        String[] accessibilityArrayNumbers = getResources().getStringArray(R.array.accessibility_numbers);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.label_connected_ecg)
                .setItems(R.array.accessibility_options, (dialog, which) -> {
                    switch (which) {
                        case 0:
                            accessibility.setText(accessibilityArrayNumbers[0]);
                            break;
                        case 1:
                            accessibility.setText(accessibilityArrayNumbers[1]);
                            break;

                        case 2:
                            accessibility.setText(accessibilityArrayNumbers[2]);
                            break;

                        case 3:
                            accessibility.setText(accessibilityArrayNumbers[3]);
                            break;

                        case 4:
                            accessibility.setText(accessibilityArrayNumbers[4]);
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

    private String getCurrentTimeStamp() {
        SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.UK);//dd/MM/yyyy
        Date now = new Date();
        String strDate = sdfDate.format(now);
        return strDate;
    }

    private void updateDateUI() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.UK);
        dateLicense.setText(sdf.format(calendar.getTime()));
    }

    private void openDatePicker() {
        new DatePickerDialog(this, date, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void sendDataToViewModel(CommunityInformation information) {
        viewModel.saveCommunityToDb(information);

    }

    private String formatString() {
        Date today = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss a", Locale.US);
        String date = format.format(today);
        return date;
    }


    private void intentGalleryPhotoPicker() {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, REQUEST_GALLERY);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (resultCode == RESULT_OK && requestCode == REQUEST_GALLERY) {
            try {
                final Uri imageUri = data.getData();
                final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                circleImageView.setImageBitmap(selectedImage);
                imgInString = bitmapToBase64(selectedImage);
                imgLoadedCheckBox.setChecked(true);
                imgLoadedCheckBox.setText(R.string.image_loaded);


                //image_view.setImageBitmap(selectedImage);
            } catch (FileNotFoundException e) {
                e.printStackTrace();

            }

        } else if (requestCode == REQUEST_CAMERA && resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CAMERA && resultCode == RESULT_OK) {
                Bundle extras = data.getExtras();
                Bitmap imageBitmap = (Bitmap) extras.get("data");
                circleImageView.setImageBitmap(imageBitmap);
                imgInString = bitmapToBase64(imageBitmap);
                imgLoadedCheckBox.setChecked(true);
                imgLoadedCheckBox.setText("Image has been loaded");
            }

        } else {
            Toast.makeText(MainActivity.this, "You haven't picked Image", Toast.LENGTH_LONG).show();
        }

    }


    private void checkIfPermissionIsActive() {
        if (PermissionUtils.isAccessFineLocationGranted(this)) {
            if (isLocationEnabled(this)) {
                getUserLocation();
            } else {
                showGPSNotEnabledDialog(this);
            }
        } else {
            requestAccessFineLocationPermission(this, LOCATION_PERMISSION_REQUEST_CODE);
        }
    }


    @Override
    public void onValidationSucceeded() {
        String communityName = community_name.getText().toString();
        int geo_district = Integer.parseInt(geoDistrict.getText().toString());
        int accessibility_str = Integer.parseInt(accessibility.getText().toString());
        int distance_ecom = Integer.parseInt(distance.getText().toString());
        String connect_ecg = connectedToEcg.getText().toString();
        String date_license = dateLicense.getText().toString();
        double lat = Double.parseDouble(latitude.getText().toString());
        double longitu = Double.parseDouble(longitude.getText().toString());
        String createdBy = "murali";
        String createdDate = getCurrentTimeStamp();
        String updateBy = "";
        String updateDate = "";

        CommunityInformation communityInformation = new CommunityInformation(communityName, geo_district,
                accessibility_str, distance_ecom, connect_ecg, date_license, lat, longitu, imgInString, createdBy, createdDate,
                updateBy, updateDate, false);

        sendDataToViewModel(communityInformation);

    }

    @Override
    public void onValidationFailed(List<ValidationError> errors) {
        for (ValidationError error : errors) {
            View view = error.getView();
            String message = error.getCollatedErrorMessage(this);

            // Display error messages ;)
            if (view instanceof AppCompatEditText) {
                ((AppCompatEditText) view).setError(message);
            } else {
                Toast.makeText(this, message, Toast.LENGTH_LONG).show();
            }
        }
    }

    private void showPopupMenu(View view) {
        // inflate menu

        PopupMenu popup = new PopupMenu(view.getContext(), view);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.menu_popup, popup.getMenu());
        popup.setOnMenuItemClickListener(new PopupClickListener());
        popup.show();
    }

    private void dispatchTakePictureIntent() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        try {
            startActivityForResult(intent, REQUEST_CAMERA);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(getApplicationContext(), "Camera Unavailable", Toast.LENGTH_SHORT).show();
        }
    }

    class PopupClickListener implements PopupMenu.OnMenuItemClickListener {

        public PopupClickListener() {
        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            switch (item.getItemId()) {
                case R.id.action_camera:
                    dispatchTakePictureIntent();
                    return true;
                case R.id.action_gallery:
                    intentGalleryPhotoPicker();
                    return true;
                default:
            }
            return false;
        }
    }
}
