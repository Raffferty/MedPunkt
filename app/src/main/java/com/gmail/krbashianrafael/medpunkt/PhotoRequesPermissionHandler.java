package com.gmail.krbashianrafael.medpunkt;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.view.View;

/**
 * Created by raf on 17.03.2018.
 */

public class PhotoRequesPermissionHandler {

    private static int mPERMISSION_READ_EXTERNAL_STORAGE = 0;

    private static View mLayout;
    private static Activity mactivity;
    private static boolean mShowSnackBar;


    public static boolean getRuntimePhotoPermissionToStorage(Activity activity, View layout, int PERMISSION_READ_EXTERNAL_STORAGE, boolean showSnackBar){

        mShowSnackBar = showSnackBar;
        mactivity = activity;
        mPERMISSION_READ_EXTERNAL_STORAGE = PERMISSION_READ_EXTERNAL_STORAGE;
        mLayout = layout;


        mShowSnackBar = false;
        requestStoragePermission();

        return mShowSnackBar;
    }


    private static void requestStoragePermission() {
        // Permission has not been granted and must be requested.
        if (ActivityCompat.shouldShowRequestPermissionRationale(mactivity,
                Manifest.permission.READ_EXTERNAL_STORAGE)) {
            // Provide an additional rationale to the user if the permission was not granted
            // and the user would benefit from additional context for the use of the permission.
            // Display a SnackBar with a button to request the missing permission.
            Snackbar.make(mLayout, R.string.why_need_permission_to_srorage,
                    Snackbar.LENGTH_INDEFINITE).setAction("OK", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Request the permission
                    ActivityCompat.requestPermissions(mactivity,
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            mPERMISSION_READ_EXTERNAL_STORAGE);
                }
            }).show();

        } else {
            Snackbar.make(mLayout,
                    R.string.permission_not_available,
                    Snackbar.LENGTH_LONG).show();
            // Request the permission. The result will be received in onRequestPermissionResult().
            ActivityCompat.requestPermissions(mactivity,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    mPERMISSION_READ_EXTERNAL_STORAGE);
        }
    }
}
