package com.test.feulmgmt.utils;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;

import com.test.feulmgmt.FuelApplication;

public class PermissionUtilities {

    public static void requestPermission(Activity activity) {
        if (!checkPermissions())
            ActivityCompat.requestPermissions(activity, requestStringPermissions(), 100);
    }

    private static boolean checkPermissions() {
        boolean check = ActivityCompat.checkSelfPermission(FuelApplication.getContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(FuelApplication.getContext(),
                        Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(FuelApplication.getContext(),
                Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
        return check;
    }

    public static String[] requestStringPermissions() {
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.CAMERA};
        return permissions;
    }
}
