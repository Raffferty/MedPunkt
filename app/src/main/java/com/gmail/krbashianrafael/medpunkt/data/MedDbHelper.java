package com.gmail.krbashianrafael.medpunkt.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.gmail.krbashianrafael.medpunkt.data.MedContract.MedEntry;

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
        //имя таблицы и название колонок берутся из класса Контракта MedContract.MedEntry

        // полная строка для создания таблицы:
        // "CREATE TABLE pets (_ID INTEGER PRIMARY KEY AUTOINCREMENT,
        //                          u_name TEXT NOT NULL,
        //                              u_date TEXT NOT NULL,
        //                                          u_photo TEXT);

        String SQL_CREATE_USERS_TABLE = "CREATE TABLE " + MedEntry.USERS_TABLE_NAME + " ("
                + MedEntry.U_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + MedEntry.COLUMN_USER_NAME + " TEXT NOT NULL, "
                + MedEntry.COLUMN_USER_DATE + " TEXT NOT NULL, "
                + MedEntry.COLUMN_USER_PHOTO + " TEXT);";

        // Execute the SQL statement
        //команда, создающая таблицу users в Базе
        db.execSQL(SQL_CREATE_USERS_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        //
    }
}
