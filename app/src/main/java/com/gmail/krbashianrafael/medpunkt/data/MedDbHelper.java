package com.gmail.krbashianrafael.medpunkt.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.gmail.krbashianrafael.medpunkt.data.MedContract.UsersEntry;
import com.gmail.krbashianrafael.medpunkt.data.MedContract.DiseasesEntry;
import com.gmail.krbashianrafael.medpunkt.data.MedContract.TreatmentPhotosEntry;

public class MedDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "medpunkt.db"; //имя файла Базы данных

    private static final int DATABASE_VERSION = 1; // версия Базы данных

    MedDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create a String that contains the SQL statement to create the users table

        //это строка для создания таблицы users в Базе
        //имя таблицы и название колонок берутся из класса Контракта MedContract.UsersEntry

        // строка для создания таблицы users:
        String SQL_CREATE_USERS_TABLE = "CREATE TABLE " + UsersEntry.USERS_TABLE_NAME + " ("
                + UsersEntry.U_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + UsersEntry.COLUMN_USER_NAME + " TEXT NOT NULL, "
                + UsersEntry.COLUMN_USER_DATE + " TEXT NOT NULL, "
                + UsersEntry.COLUMN_USER_PHOTO_PATH + " TEXT);";

        // строка для создания таблицы diseases:
        String SQL_CREATE_DISEASES_TABLE = "CREATE TABLE " + DiseasesEntry.DISEASES_TABLE_NAME + " ("
                + DiseasesEntry.DIS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + DiseasesEntry.COLUMN_U_ID + " LONG NOT NULL, "
                + DiseasesEntry.COLUMN_DISEASE_NAME + " TEXT NOT NULL, "
                + DiseasesEntry.COLUMN_DISEASE_DATE + " TEXT NOT NULL, "
                + DiseasesEntry.COLUMN_DISEASE_TREATMENT + " TEXT);";

        // строка для создания таблицы treatmentPhotos:
        String SQL_CREATE_TREATMENT_PHOTOS_TABLE = "CREATE TABLE " + TreatmentPhotosEntry.TREATMENT_PHOTOS_TABLE_NAME + " ("
                + TreatmentPhotosEntry.TR_PHOTO_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + TreatmentPhotosEntry.COLUMN_U_ID + " LONG NOT NULL, "
                + TreatmentPhotosEntry.COLUMN_DIS_ID + " LONG NOT NULL, "
                + TreatmentPhotosEntry.COLUMN_TR_PHOTO_NAME + " TEXT NOT NULL, "
                + TreatmentPhotosEntry.COLUMN_TR_PHOTO_DATE + " TEXT NOT NULL, "
                + TreatmentPhotosEntry.COLUMN_TR_PHOTO_PATH + " TEXT);";


        //команда, создающая таблицу users в Базе
        db.execSQL(SQL_CREATE_USERS_TABLE);

        //команда, создающая таблицу diseases в Базе
        db.execSQL(SQL_CREATE_DISEASES_TABLE);

        //команда, создающая таблицу treatmentPhotos в Базе
        db.execSQL(SQL_CREATE_TREATMENT_PHOTOS_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        //
    }
}
