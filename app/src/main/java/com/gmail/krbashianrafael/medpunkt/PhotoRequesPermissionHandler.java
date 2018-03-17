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

    private static Activity mactivity;
    private static boolean mShowSnackBar;
    private static View mLayout;

    public static boolean getRuntimePhotoPermissionToStorage(Activity activity, View layout, int PERMISSION_READ_EXTERNAL_STORAGE, boolean showSnackBar){

        mShowSnackBar = showSnackBar;
        mLayout = layout;
        mactivity = activity;
        mPERMISSION_READ_EXTERNAL_STORAGE = PERMISSION_READ_EXTERNAL_STORAGE;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            getRuntimePermissionToStorage();
        }
        else {
            if (ActivityCompat.checkSelfPermission(mactivity, Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {

                if (mShowSnackBar){
                    // Permission is already available, start camera preview
                    Snackbar.make(mLayout,
                            R.string.permission_to_photoes_is_available,
                            Snackbar.LENGTH_SHORT).show();

                    // чтоб больше не показывать это сообщение
                    mShowSnackBar = false;
                }

            } else {
                // Permission is missing and must be requested.
                mShowSnackBar = false;
                requestStoragePermission();
            }
        }
        return mShowSnackBar;
    }

    private static void getRuntimePermissionToStorage(){
        if (ActivityCompat.checkSelfPermission(mactivity, Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {
            if (mShowSnackBar){
                // Permission is already available
                Snackbar.make(mLayout,
                        R.string.permission_to_photoes_is_available,
                        Snackbar.LENGTH_SHORT).show();

                // чтоб больше не показывать это сообщение
                // при возввращении в то Активити откуда запрашивалось сообщение
                mShowSnackBar = false;
            }
        }
        else {
            // Permission is missing and must be requested.
            mShowSnackBar = false;
            requestStoragePermission();
        }
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
