package com.gmail.krbashianrafael.medpunkt;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.support.annotation.NonNull;
import android.util.Log;

import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;

import java.nio.charset.Charset;
import java.security.MessageDigest;

// класс для вращения фото в Glide
public class RotateTransformation extends BitmapTransformation {

    private static final String ID = "com.gmail.krbashianrafael.medpunkt.GetBitmapFromTransformation";

    private Float rotateRotationAngle = 0f;

    public RotateTransformation(float rotateRotationAngle) {
        this.rotateRotationAngle = rotateRotationAngle;
    }

    @Override
    protected Bitmap transform(@NonNull BitmapPool pool, @NonNull Bitmap toTransform, int outWidth, int outHeight) {

        Log.d("rotation", "toTransform =" + toTransform.getAllocationByteCount());

        if (rotateRotationAngle != 0) {
            Matrix matrix = new Matrix();
            matrix.postRotate(rotateRotationAngle);

            return Bitmap.createBitmap(toTransform, 0, 0, toTransform.getWidth(), toTransform.getHeight(), matrix, true);
        }

        return toTransform;
    }

    @Override
    public void updateDiskCacheKey(@NonNull MessageDigest messageDigest) {
        messageDigest.update((ID + rotateRotationAngle).getBytes(Charset.forName("UTF-8")));
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof RotateTransformation) {
            RotateTransformation other = (RotateTransformation) o;
            return rotateRotationAngle.equals(other.rotateRotationAngle);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return (ID + rotateRotationAngle).hashCode();
    }
}
