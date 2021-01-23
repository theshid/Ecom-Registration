package com.ecomtrading.android.utils;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.provider.Settings;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.ecomtrading.android.R;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.util.List;

public class PermissionUtils {

    /**
     * Function to request permission from the user
     */
    public static void requestAccessFineLocationPermission(AppCompatActivity activity, int requestId) {
        ActivityCompat.requestPermissions(
                activity,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                requestId
        );
    }

    /**
     * Function to check if the location permissions are granted or not
     */
    public static Boolean isAccessFineLocationGranted(Context context) {
        return ContextCompat
                .checkSelfPermission(
                        context,
                        Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * Function to check if location of the device is enabled or not
     */
    public static Boolean isLocationEnabled(Context context) {
        LocationManager locationManager =
                (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    /**
     * Function to show the "enable GPS" Dialog box
     */
    public static void showGPSNotEnabledDialog(Context context) {
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

    public static void checkCameraPermissions(Activity activity) {
        Dexter.withActivity(activity)
                .withPermissions(
                        Manifest.permission.CAMERA

                ).withListener(new MultiplePermissionsListener() {
            @Override
            public void onPermissionsChecked(MultiplePermissionsReport report) {/* ... */}

            @Override
            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {/* ... */}
        }).check();
    }
}
