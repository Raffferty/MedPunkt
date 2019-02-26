package com.gmail.krbashianrafael.medpunkt.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.gmail.krbashianrafael.medpunkt.data.MedContract.UsersEntry;
import com.gmail.krbashianrafael.medpunkt.data.MedContract.DiseasesEntry;
import com.gmail.krbashianrafael.medpunkt.data.MedContract.TreatmentPhotosEntry;

class MedDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "medpunkt.db";

    private static final int DATABASE_VERSION = 1;

    MedDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String SQL_CREATE_USERS_TABLE = "CREATE TABLE " + UsersEntry.USERS_TABLE_NAME + " ("
                + UsersEntry.U_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + UsersEntry.COLUMN_USER_NAME + " TEXT NOT NULL, "
                + UsersEntry.COLUMN_USER_DATE + " TEXT NOT NULL, "
                + UsersEntry.COLUMN_USER_PHOTO_PATH + " TEXT);";

        String SQL_CREATE_DISEASES_TABLE = "CREATE TABLE " + DiseasesEntry.DISEASES_TABLE_NAME + " ("
                + DiseasesEntry.DIS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + DiseasesEntry.COLUMN_U_ID + " INTEGER NOT NULL, "
                + DiseasesEntry.COLUMN_DISEASE_NAME + " TEXT NOT NULL, "
                + DiseasesEntry.COLUMN_DISEASE_DATE + " TEXT NOT NULL, "
                + DiseasesEntry.COLUMN_DISEASE_TREATMENT + " TEXT);";

        String SQL_CREATE_TREATMENT_PHOTOS_TABLE = "CREATE TABLE " + TreatmentPhotosEntry.TREATMENT_PHOTOS_TABLE_NAME + " ("
                + TreatmentPhotosEntry.TR_PHOTO_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + TreatmentPhotosEntry.COLUMN_U_ID + " INTEGER NOT NULL, "
                + TreatmentPhotosEntry.COLUMN_DIS_ID + " INTEGER NOT NULL, "
                + TreatmentPhotosEntry.COLUMN_TR_PHOTO_NAME + " TEXT NOT NULL, "
                + TreatmentPhotosEntry.COLUMN_TR_PHOTO_DATE + " TEXT NOT NULL, "
                + TreatmentPhotosEntry.COLUMN_TR_PHOTO_PATH + " TEXT);";

        db.execSQL(SQL_CREATE_USERS_TABLE);

        db.execSQL(SQL_CREATE_DISEASES_TABLE);

        db.execSQL(SQL_CREATE_TREATMENT_PHOTOS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
