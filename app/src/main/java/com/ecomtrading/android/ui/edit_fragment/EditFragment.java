package com.ecomtrading.android.ui.edit_fragment;

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
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.PopupMenu;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.ecomtrading.android.R;
import com.ecomtrading.android.api.ApiClient;
import com.ecomtrading.android.api.ApiService;
import com.ecomtrading.android.databinding.FragmentEditBinding;
import com.ecomtrading.android.db.MyDatabase;
import com.ecomtrading.android.entity.CommunityInformation;
import com.ecomtrading.android.utils.AppExecutor;
import com.ecomtrading.android.utils.ConversionUtils;
import com.ecomtrading.android.utils.GsonParser;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.Checked;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;

import net.steamcrafted.loadtoast.LoadToast;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_OK;
import static com.ecomtrading.android.utils.ConversionUtils.bitmapToBase64;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link EditFragment#newInstance} factory method to
 * create an instance of this fragment.
 */

public class EditFragment extends DialogFragment implements Validator.ValidationListener {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String ARG_PARAM3 = "param3";
    private int localID;
    private String communityInfoInString;
    private String imageString = "";
    private DatePickerDialog.OnDateSetListener date;
    private Calendar calendar;
    private FragmentEditBinding fragmentEditBinding;
    @NotEmpty
    AppCompatEditText community_name, geoDistrict, accessibility, distance, connectedToEcg, latitude, longitude;
    AppCompatImageView circleImageView;
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
    MyDatabase database;
    ApiService apiService;
    private FragmentEditViewModel editViewModel;
    CommunityInformation information;
    Validator validator;
    CommunityInformation communityInformation;
    Boolean status_check = false;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 999;


    public EditFragment() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static EditFragment newInstance(int param1, CommunityInformation information) {
        EditFragment fragment = new EditFragment();
        Bundle args = new Bundle();
        String communityInfo = GsonParser.getGsonParser().toJson(information);
        args.putString(ARG_PARAM2, communityInfo);
        args.putInt(ARG_PARAM1, param1);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            localID = getArguments().getInt(ARG_PARAM1);
            communityInfoInString = getArguments().getString(ARG_PARAM2);
            communityInformation = GsonParser.getGsonParser().fromJson(communityInfoInString, CommunityInformation.class);

            Log.d("Fragment", String.valueOf(localID));
        }
        setStyle(DialogFragment.STYLE_NORMAL, R.style.yourStyle);
        apiService = ApiClient.getRetrofitInstance().create(ApiService.class);
        database = MyDatabase.getInstance(getActivity());
        getUserLocation();
        AppExecutor.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                information = database.dao().getCommunity(localID);
            }
        });


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment


        fragmentEditBinding = FragmentEditBinding.inflate(inflater, container, false);
        View view = fragmentEditBinding.getRoot();
        validator = new Validator(this);
        validator.setValidationListener(this);
        Log.d("Fragment", String.valueOf(localID));


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
        textView_photo = view.findViewById(R.id.fragment_text_photo);
        imgLoadedCheckBox = view.findViewById(R.id.fragment_check);

        community_name.setText(information.getCommunity_name());
        imageString = information.getImage();
        if (!imageString.isEmpty()) {
            imgLoadedCheckBox.setChecked(true);
            imgLoadedCheckBox.setText("Image Loaded");
        }
        accessibility.setText(String.valueOf(information.getAccessibility()));
        dateLicense.setText(String.valueOf(information.getDate_licence()));
        circleImageView.setImageBitmap(ConversionUtils.base64ToBitmap(information.getImage()));
        geoDistrict.setText(String.valueOf(information.getGeographical_district()));
        connectedToEcg.setText(information.getConnected_to_ecg());
        distance.setText(String.valueOf(information.getDistance()));
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
        String communityName = community_name.getText().toString();
        int geo_district = Integer.parseInt(geoDistrict.getText().toString());
        int accessibility_str = Integer.parseInt(accessibility.getText().toString());
        int distance_ecom = Integer.parseInt(distance.getText().toString());
        String connect_ecg = connectedToEcg.getText().toString();
        String date_license = dateLicense.getText().toString();
        double lat = Double.parseDouble(latitude.getText().toString());
        double longitu = Double.parseDouble(longitude.getText().toString());
        String updateBy = "murali";
        String updateDate = getCurrentTimeStamp();


        updateCommunity(localID, communityName, geo_district, accessibility_str, distance_ecom, connect_ecg,
                date_license, lat, longitu, imageString, updateBy, updateDate);


    }

    private void updateCommunity(int id, String name, int geographical_district,
                                 int accessibility, int distance, String connected_to_ecg,
                                 String date_licence, Double latitude, Double longitude, String image,
                                 String updateBy, String updateDate) {
        AppExecutor executor = AppExecutor.getInstance();
        Call<ResponseBody> call = apiService.sendInformation(name, geographical_district, accessibility,
                distance, connected_to_ecg, date_licence, latitude, longitude, image, communityInformation.getCreatedBy(),
                communityInformation.getCreatedDate(), updateBy, updateDate);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                communityInformation.setSent_server(response.code() == 200);
                Log.d("Response Code",String.valueOf(response.code()));
                if (response.code() == 200){
                    status_check = true;
                    displayToast(status_check);
                } else {
                    status_check = false;
                    displayToast(status_check);
                }


                executor.diskIO().execute(new Runnable() {
                    @Override
                    public void run() {
                        database.dao().updateInformation(name, id, geographical_district, accessibility,
                                distance, connected_to_ecg, date_licence, latitude, longitude, image, updateBy,
                                updateDate, communityInformation.getSent_server());
                        Log.d("Fragment", "executor");

                    }
                });
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d("Fragment", "failed to post");

                executor.diskIO().execute(new Runnable() {
                    @Override
                    public void run() {
                        status_check = false;
                        displayToast(status_check);
                        communityInformation.setSent_server(false);
                        database.dao().updateInformation(name, id, geographical_district, accessibility,
                                distance, connected_to_ecg, date_licence, latitude, longitude, image, updateBy,
                                updateDate, communityInformation.getSent_server());
                        Log.d("Fragment", "executor");

                    }
                });
            }
        });

    }

    private void displayToast(Boolean status) {
        if (status) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getContext(), "Data successfully sent to server!", Toast.LENGTH_LONG).show();
                    dismissFragment();
                }
            });

        } else {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getContext(), "Data not sent to server! Check Internet", Toast.LENGTH_LONG).show();
                    dismissFragment();
                }
            });

        }
    }

    private void dismissFragment() {
        dismiss();
    }

    @Override
    public void onValidationFailed(List<ValidationError> errors) {
        for (ValidationError error : errors) {
            View view = error.getView();
            String message = error.getCollatedErrorMessage(getContext());

            // Display error messages ;)
            if (view instanceof AppCompatEditText) {
                ((AppCompatEditText) view).setError(message);
            } else {
                Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
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