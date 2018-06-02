package com.gmail.krbashianrafael.medpunkt;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.util.Log;

import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;

import java.nio.charset.Charset;
import java.security.MessageDigest;

// класс для вращения фото в Glide
public class GetBitmapFromTransformation extends BitmapTransformation {

    private static final String ID = "com.gmail.krbashianrafael.medpunkt.GetBitmapFromTransformation";
    private static final byte[] ID_BYTES = ID.getBytes(Charset.forName("UTF-8"));


    /*private Float rotateRotationAngle = 0f;

    public GetBitmapFromTransformation(float rotateRotationAngle) {

        this.rotateRotationAngle = rotateRotationAngle;
    }*/

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

        // присваиваем FullscreenPhotoActivity.loadedBitmap = toTransform
        FullscreenPhotoActivity.loadedBitmap = null;
        FullscreenPhotoActivity.loadedBitmap = toTransform;

        pool.clearMemory();

        //Log.d("file", "pool.getMaxSize() 2 = " + pool.getMaxSize());



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
        //messageDigest.update(("rotate" + rotateRotationAngle).getBytes());
        messageDigest.update(ID_BYTES);
    }

    @Override
    public boolean equals(Object o) {
        /*if (o instanceof GetBitmapFromTransformation) {
            GetBitmapFromTransformation other = (GetBitmapFromTransformation) o;
            return rotateRotationAngle.equals(other.rotateRotationAngle);
        }*/
        return o instanceof GetBitmapFromTransformation;
    }

    @Override
    public int hashCode() {
        return ID.hashCode();
    }
}
