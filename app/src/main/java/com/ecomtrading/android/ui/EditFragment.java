package com.ecomtrading.android.ui;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.PopupMenu;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.ecomtrading.android.FragmentEditViewModel;
import com.ecomtrading.android.MainActivity;
import com.ecomtrading.android.R;
import com.ecomtrading.android.databinding.FragmentEditBinding;
import com.ecomtrading.android.db.MyDatabase;
import com.ecomtrading.android.entity.CommunityInformation;
import com.ecomtrading.android.utils.ConversionUtils;
import com.ecomtrading.android.utils.PermissionUtils;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.Checked;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;
import static com.ecomtrading.android.utils.ConversionUtils.bitmapToBase64;
import static com.ecomtrading.android.utils.PermissionUtils.isLocationEnabled;
import static com.ecomtrading.android.utils.PermissionUtils.requestAccessFineLocationPermission;
import static com.ecomtrading.android.utils.PermissionUtils.showGPSNotEnabledDialog;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link EditFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
@AndroidEntryPoint
public class EditFragment extends DialogFragment implements Validator.ValidationListener{

    private static final String ARG_PARAM1 = "param1";
    private String localID = "";
    private String imageString = "";
    private DatePickerDialog.OnDateSetListener date;
    private Calendar calendar;
    private FragmentEditBinding fragmentEditBinding;
    @NotEmpty
    AppCompatEditText community_name, geoDistrict, accessibility, distance, connectedToEcg, latitude, longitude;
    CircleImageView circleImageView;
    private FusedLocationProviderClient fusedLocationClient;
    private static final int REQUEST_CAMERA = 87;
    private static final int REQUEST_GALLERY = 434;
    @NotEmpty
    AppCompatEditText dateLicense;
    Double lat, lgt;
    AppCompatButton btn_save, btn_location;
    @Checked(message = "You need to upload an image")
    private CheckBox imgLoadedCheckBox;
    TextView textView_photo;
    String imgInString;
    @Inject
    MyDatabase database;
    private FragmentEditViewModel editViewModel;
    CommunityInformation information;
    Validator validator;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 999;


    public EditFragment() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static EditFragment newInstance(String param1) {
        EditFragment fragment = new EditFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            localID = getArguments().getString(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        editViewModel = new ViewModelProvider(this).get(FragmentEditViewModel.class);
        validator = new Validator(this);
        validator.setValidationListener(this);
        information = editViewModel.getCommunity(Integer.parseInt(localID));
        fragmentEditBinding = FragmentEditBinding.inflate(inflater,container,false);
        View view = fragmentEditBinding.getRoot();
        setUi(view);
        hideSoftKeyboard();
        prepareDatePicker();
        setBtnClickListeners();
        setEditTextClickListeners();

        return view;
    }



    private void setUi(View view) {
        community_name = view.findViewById(R.id.community_name);
        circleImageView = view.findViewById(R.id.community_photo);
        geoDistrict = view.findViewById(R.id.geographical_district);
        connectedToEcg = view.findViewById(R.id.connected_ecg);
        btn_location = view.findViewById(R.id.location_button);
        dateLicense = view.findViewById(R.id.license_date);
        accessibility = view.findViewById(R.id.accessibility);
        distance = view.findViewById(R.id.distance_ecom);
        latitude = view.findViewById(R.id.latitude);
        longitude = view.findViewById(R.id.longitude);
        btn_save = view.findViewById(R.id.submit_button);
        textView_photo = view.findViewById(R.id.add_photo);
        imgLoadedCheckBox = view.findViewById(R.id.checkBox);

        community_name.setText(information.getCommunity_name());
        imageString = information.getImage();
        if (!imageString.isEmpty()){
            imgLoadedCheckBox.setChecked(true);
            imgLoadedCheckBox.setText("Image Loaded");
        }
        circleImageView.setImageBitmap(ConversionUtils.base64ToBitmap(information.getImage()));
        geoDistrict.setText(information.getGeographical_district());
        connectedToEcg.setText(information.getConnected_to_ecg());
        distance.setText(information.getDistance());
        latitude.setText(String.valueOf(information.getLatitude()));
        longitude.setText(String.valueOf(information.getLongitude()));
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

    @SuppressLint("MissingPermission")
    public void getUserLocation() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(getContext());

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
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

    private void hideSoftKeyboard() {
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    private void intentGalleryPhotoPicker() {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, REQUEST_GALLERY);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == REQUEST_GALLERY) {
            try {
                final Uri imageUri = data.getData();
                final InputStream imageStream = getActivity().getContentResolver().openInputStream(imageUri);
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
            Toast.makeText(getContext(), "You haven't picked Image", Toast.LENGTH_LONG).show();
        }
    }



    public void openConnectedToEcgDialog(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
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
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
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

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
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
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
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
        new DatePickerDialog(getContext(), date, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)).show();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        fragmentEditBinding = null;
    }

    @Override
    public void onValidationSucceeded() {

    }

    @Override
    public void onValidationFailed(List<ValidationError> errors) {

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
            Toast.makeText(getContext(), "Camera Unavailable", Toast.LENGTH_SHORT).show();
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