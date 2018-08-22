package com.gmail.krbashianrafael.medpunkt;

import android.Manifest;
import android.app.Activity;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.view.View;

/**
 * Created by raf on 17.03.2018.
 * <p>
 * Вынесен в отдельный класс запрос на ReadWritePermission
 */

public class MyReadWritePermissionHandler {

    public static void getReadWritePermission(final Activity mActivity, final View mLayout,
                                              final int PERMISSION_WRITE_EXTERNAL_STORAGE) {

        if (ActivityCompat.shouldShowRequestPermissionRationale(mActivity,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            Snackbar.make(mLayout, R.string.permission_to_storage_need,
                    Snackbar.LENGTH_INDEFINITE).setAction("OK", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ActivityCompat.requestPermissions(mActivity,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            PERMISSION_WRITE_EXTERNAL_STORAGE);
                }
            }).show();
        } else {
            Snackbar.make(mLayout,
                    R.string.permission_not_available,
                    Snackbar.LENGTH_LONG).show();

            ActivityCompat.requestPermissions(mActivity,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    PERMISSION_WRITE_EXTERNAL_STORAGE);
        }
    }
}