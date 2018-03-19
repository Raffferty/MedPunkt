package com.gmail.krbashianrafael.medpunkt;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.view.View;

/**
 * Created by raf on 17.03.2018.
 */

public class PhotoRequesPermissionHandler {

    public static void getRuntimePhotoPermissionToStorage(final Activity mActivity, View mLayout,
                                                          final int mPERMISSION_READ_EXTERNAL_STORAGE) {

        if (ActivityCompat.checkSelfPermission(mActivity, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            Snackbar.make(mLayout, R.string.why_need_permission_to_srorage,
                    Snackbar.LENGTH_INDEFINITE).setAction("OK", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ActivityCompat.requestPermissions(mActivity,
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            mPERMISSION_READ_EXTERNAL_STORAGE);
                }
            }).show();
        } else {
            ActivityCompat.requestPermissions(mActivity,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    mPERMISSION_READ_EXTERNAL_STORAGE);
        }
    }
}