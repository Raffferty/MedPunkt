package com.gmail.krbashianrafael.medpunkt;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.util.Log;

import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;

import java.security.MessageDigest;

// класс для вращения фото в Glide
public class RotateTransformation extends BitmapTransformation {

    private Float rotateRotationAngle = 0f;

    public RotateTransformation(float rotateRotationAngle) {

        this.rotateRotationAngle = rotateRotationAngle;
    }

    @Override
    protected Bitmap transform(@NonNull BitmapPool pool, @NonNull Bitmap toTransform, int outWidth, int outHeight) {
        //Matrix matrix = new Matrix();
        //matrix.postRotate(rotateRotationAngle);

        // toTransform.getWidth(), toTransform.getHeight()

        Log.d("file", "pool.getMaxSize() 1 = " + pool.getMaxSize());


        Log.d("file", "toTransform = " + toTransform);
        Log.d("file", "toTransform.getByteCount() = " + toTransform.getByteCount());
        Log.d("file", "toTransform.getWidth() = " + toTransform.getWidth());
        Log.d("file", "toTransform.getHeight() = " + toTransform.getHeight());



        /*int setWidth = toTransform.getWidth();
        int setHeight = toTransform.getHeight();

        if (setWidth>2000) setWidth = 2000;
        if (setHeight>2000) setHeight = 2000;*/

        //Bitmap bitmap = Bitmap.createScaledBitmap(toTransform, toTransform.getWidth(), toTransform.getHeight(), true);

        //FullscreenPhotoActivity.loadedBitmap = bitmap;
        FullscreenPhotoActivity.loadedBitmap = toTransform;

        pool.clearMemory();

        Log.d("file", "pool.getMaxSize() 2 = " + pool.getMaxSize());



       /* Log.d("file", "bitmap = " + bitmap);
        Log.d("file", "bitmap.getByteCount() = " + bitmap.getByteCount());
        Log.d("file", "bitmap.getWidth() = " + bitmap.getWidth());
        Log.d("file", "bitmap.getHeight() = " + bitmap.getHeight());*/

        //toTransform = null;

        //Bitmap.createBitmap(bitmap, 0, 0, outWidth, outHeight, matrix, true);

        return toTransform;
    }

    @Override
    public void updateDiskCacheKey(@NonNull MessageDigest messageDigest) {
        messageDigest.update(("rotate" + rotateRotationAngle).getBytes());
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
        return rotateRotationAngle.hashCode();
    }
}
