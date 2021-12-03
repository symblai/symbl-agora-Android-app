package com.example.myapplication.services;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.Arrays;

public class PermissionService {
    private static final String[] REQUIRED_PERMISSIONS = {
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.CAMERA
    };

    private static final String TAG = "Permission Service : ";

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void requestPermissionsIfNotGranted(Activity activity, int permissionRequestCode) {
        String[] permissionsYetToBeGranted = Arrays.stream(REQUIRED_PERMISSIONS)
                .filter(permission -> !isPermissionGranted(permission, activity))
                .toArray(String[]::new);

        Log.d(TAG, "Requesting following permissions : " + Arrays.toString(REQUIRED_PERMISSIONS));
        if (permissionsYetToBeGranted.length > 0) {
            ActivityCompat.requestPermissions(activity, permissionsYetToBeGranted, permissionRequestCode);
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    public boolean requiredPermissionGranted(Context context) {
        return Arrays.stream(REQUIRED_PERMISSIONS).allMatch(permission -> isPermissionGranted(permission, context));
    }

    private boolean isPermissionGranted(String permission, Context context) {
        return ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED;
    }

}
