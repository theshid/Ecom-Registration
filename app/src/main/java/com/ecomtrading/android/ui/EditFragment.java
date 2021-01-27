package com.ecomtrading.android.ui;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.location.Location;
import android.os.Bundle;

import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.TextView;

import com.ecomtrading.android.FragmentEditViewModel;
import com.ecomtrading.android.R;
import com.ecomtrading.android.databinding.FragmentEditBinding;
import com.ecomtrading.android.db.MyDatabase;
import com.ecomtrading.android.entity.CommunityInformation;
import com.ecomtrading.android.utils.ConversionUtils;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.Checked;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;

import java.util.Calendar;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link EditFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
@AndroidEntryPoint
public class EditFragment extends Fragment implements Validator.ValidationListener{

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
    @Inject
    MyDatabase database;
    private FragmentEditViewModel editViewModel;
    CommunityInformation information;


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
        information = editViewModel.getCommunity(Integer.parseInt(localID));
        fragmentEditBinding = FragmentEditBinding.inflate(inflater,container,false);
        View view = fragmentEditBinding.getRoot();
        setUi(view);

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
        circleImageView.setImageBitmap(ConversionUtils.base64ToBitmap(information.getImage()));
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
}