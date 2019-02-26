package com.gmail.krbashianrafael.medpunkt.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.gmail.krbashianrafael.medpunkt.data.MedContract.DiseasesEntry;
import com.gmail.krbashianrafael.medpunkt.data.MedContract.TreatmentPhotosEntry;
import com.gmail.krbashianrafael.medpunkt.data.MedContract.UsersEntry;
import com.gmail.krbashianrafael.medpunkt.shared.HomeActivity;
import com.gmail.krbashianrafael.medpunkt.tablet.TabletMainActivity;

public class MedProvider extends ContentProvider {

    private static final int USERS = 100;

    private static final int USER_ID = 101;

    private static final int DISEASES = 200;

    private static final int DISEASES_ID = 201;

    private static final int TREATMENT_PHOTOS = 300;

    private static final int TREATMENT_PHOTOS_ID = 301;

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sUriMatcher.addURI(MedContract.CONTENT_AUTHORITY, MedContract.PATH_USERS, USERS);

        sUriMatcher.addURI(MedContract.CONTENT_AUTHORITY, MedContract.PATH_USERS + "/#", USER_ID);

        sUriMatcher.addURI(MedContract.CONTENT_AUTHORITY, MedContract.PATH_DISEASES, DISEASES);

        sUriMatcher.addURI(MedContract.CONTENT_AUTHORITY, MedContract.PATH_DISEASES + "/#", DISEASES_ID);

        sUriMatcher.addURI(MedContract.CONTENT_AUTHORITY, MedContract.PATH_TREATMENT_PHOTOS, TREATMENT_PHOTOS);

        sUriMatcher.addURI(MedContract.CONTENT_AUTHORITY, MedContract.PATH_TREATMENT_PHOTOS + "/#", TREATMENT_PHOTOS_ID);
    }

    private MedDbHelper mDbHelper;

    @Override
    public boolean onCreate() {
        mDbHelper = new MedDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri,
                        @Nullable String[] projection,
                        @Nullable String selection,
                        @Nullable String[] selectionArgs,
                        @Nullable String sortOrder) {

        SQLiteDatabase database = mDbHelper.getReadableDatabase();

        Cursor cursor;

        int match = sUriMatcher.match(uri);

        switch (match) {
            case USERS:
                cursor = database.query(UsersEntry.USERS_TABLE_NAME, projection, selection, selectionArgs,
                        null, null, null);
                break;
            case USER_ID:
                selection = UsersEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

                cursor = database.query(UsersEntry.USERS_TABLE_NAME, projection, selection, selectionArgs,
                        null, null, null);
                break;
            case DISEASES:
                cursor = database.query(DiseasesEntry.DISEASES_TABLE_NAME, projection, selection, selectionArgs,
                        null, null, null);

                break;
            case DISEASES_ID:
                selection = DiseasesEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

                cursor = database.query(DiseasesEntry.DISEASES_TABLE_NAME, projection, selection, selectionArgs,
                        null, null, null);

                break;
            case TREATMENT_PHOTOS:
                cursor = database.query(TreatmentPhotosEntry.TREATMENT_PHOTOS_TABLE_NAME, projection, selection, selectionArgs,
                        null, null, null);
                break;
            case TREATMENT_PHOTOS_ID:
                selection = TreatmentPhotosEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

                cursor = database.query(TreatmentPhotosEntry.TREATMENT_PHOTOS_TABLE_NAME, projection, selection, selectionArgs,
                        null, null, null);

                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }

        if (getContext() != null) {
            cursor.setNotificationUri(getContext().getContentResolver(), uri);
        }

        return cursor;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {

        if (contentValues == null) {
            return null;
        }

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case USERS:
                return insertUser(uri, contentValues);
            case DISEASES:
                return insertDisease(uri, contentValues);
            case TREATMENT_PHOTOS:
                return insertTreatmentPhoto(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    private Uri insertUser(Uri uri, ContentValues values) {
        String userName = values.getAsString(MedContract.UsersEntry.COLUMN_USER_NAME);
        if (userName == null) {
            throw new IllegalArgumentException("user requires a name");
        }

        String userDate = values.getAsString(UsersEntry.COLUMN_USER_DATE);
        if (userDate == null) {
            throw new IllegalArgumentException("user requires a birthday");
        }

        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        long id = database.insert(UsersEntry.USERS_TABLE_NAME, null, values);

        if (id == -1) {
            return null;
        }

        if (getContext() != null) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        if (HomeActivity.isTablet) {
            TabletMainActivity.userInserted = true;
            TabletMainActivity.insertedUser_id = id;
            TabletMainActivity.selectedUser_id = TabletMainActivity.insertedUser_id;
            TabletMainActivity.userNameAfterInsert = userName;
        }

        return ContentUris.withAppendedId(uri, id);
    }

    private Uri insertDisease(Uri uri, ContentValues values) {

        Long userId = values.getAsLong(DiseasesEntry.COLUMN_U_ID);
        if (userId == null || userId == 0) {
            throw new IllegalArgumentException("disease requires userId");
        }

        String diseaseName = values.getAsString(DiseasesEntry.COLUMN_DISEASE_NAME);
        if (diseaseName == null) {
            throw new IllegalArgumentException("disease requires a name");
        }

        String diseaseDate = values.getAsString(DiseasesEntry.COLUMN_DISEASE_DATE);
        if (diseaseDate == null) {
            throw new IllegalArgumentException("disease requires a registration day");
        }

        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        long id = database.insert(DiseasesEntry.DISEASES_TABLE_NAME, null, values);

        if (id == -1) {
            return null;
        }

        if (getContext() != null) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        if (HomeActivity.isTablet) {
            TabletMainActivity.diseaseInserted = true;
            TabletMainActivity.insertedDisease_id = id;
            TabletMainActivity.selectedDisease_id = TabletMainActivity.insertedDisease_id;
        }

        return ContentUris.withAppendedId(uri, id);
    }

    private Uri insertTreatmentPhoto(Uri uri, ContentValues values) {

        Long userId = values.getAsLong(TreatmentPhotosEntry.COLUMN_U_ID);
        if (userId == null || userId == 0) {
            throw new IllegalArgumentException("disease requires userId");
        }

        Long disId = values.getAsLong(TreatmentPhotosEntry.COLUMN_DIS_ID);
        if (disId == null || disId == 0) {
            throw new IllegalArgumentException("disease requires userId");
        }

        String trPhotoName = values.getAsString(TreatmentPhotosEntry.COLUMN_TR_PHOTO_NAME);
        if (trPhotoName == null) {
            throw new IllegalArgumentException("disease requires a name");
        }

        String trPhotoDate = values.getAsString(TreatmentPhotosEntry.COLUMN_TR_PHOTO_DATE);
        if (trPhotoDate == null) {
            throw new IllegalArgumentException("disease requires a registration day");
        }

        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        long id = database.insert(TreatmentPhotosEntry.TREATMENT_PHOTOS_TABLE_NAME, null, values);

        if (id == -1) {
            return null;
        }

        if (getContext() != null) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        if (HomeActivity.isTablet && TabletMainActivity.inWideView) {
            TabletMainActivity.insertedTreatmentPhoto_id = id;
            TabletMainActivity.selectedTreatmentPhoto_id = TabletMainActivity.insertedTreatmentPhoto_id;
        }

        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int update(@NonNull Uri uri,
                      @Nullable ContentValues contentValues, @Nullable String selection, @Nullable String[] selectionArgs) {

        if (contentValues == null) {
            return 0;
        }

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case USERS:
                return updateUser(uri, contentValues, selection, selectionArgs);
            case USER_ID:
                selection = UsersEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateUser(uri, contentValues, selection, selectionArgs);
            case DISEASES:
                return updateDisease(uri, contentValues, selection, selectionArgs);
            case DISEASES_ID:
                selection = DiseasesEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateDisease(uri, contentValues, selection, selectionArgs);
            case TREATMENT_PHOTOS:
                return updateTreatmentPhoto(uri, contentValues, selection, selectionArgs);
            case TREATMENT_PHOTOS_ID:
                selection = TreatmentPhotosEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateTreatmentPhoto(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    private int updateUser(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        if (values == null || values.size() == 0) {
            return 0;
        }

        String userName = null;
        if (values.containsKey(UsersEntry.COLUMN_USER_NAME)) {
            userName = values.getAsString(UsersEntry.COLUMN_USER_NAME);
            if (userName == null) {
                throw new IllegalArgumentException("User requires a name");
            }
        }

        if (values.containsKey(UsersEntry.COLUMN_USER_DATE)) {
            String userDate = values.getAsString(UsersEntry.COLUMN_USER_DATE);
            if (userDate == null) {
                throw new IllegalArgumentException("User requires a birthday");
            }
        }

        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        int rowsUpdated = database.update(UsersEntry.USERS_TABLE_NAME, values, selection, selectionArgs);

        if (rowsUpdated != 0) {
            if (getContext() != null) {
                getContext().getContentResolver().notifyChange(uri, null);
            }
        }

        if (HomeActivity.isTablet) {
            TabletMainActivity.userUpdated = true;
            TabletMainActivity.userNameAfterUpdate = userName;
        }

        return rowsUpdated;
    }

    private int updateDisease(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        if (values == null || values.size() == 0) {
            return 0;
        }

        String diseaseName;

        if (values.containsKey(DiseasesEntry.COLUMN_DISEASE_NAME)) {
            diseaseName = values.getAsString(DiseasesEntry.COLUMN_DISEASE_NAME);
            if (diseaseName == null) {
                throw new IllegalArgumentException("disease requires a name");
            }
        }

        String diseaseDate;

        if (values.containsKey(DiseasesEntry.COLUMN_DISEASE_DATE)) {
            diseaseDate = values.getAsString(DiseasesEntry.COLUMN_DISEASE_DATE);
            if (diseaseDate == null) {
                throw new IllegalArgumentException("disease requires a registration day");
            }
        }

        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        int rowsUpdated = database.update(DiseasesEntry.DISEASES_TABLE_NAME, values, selection, selectionArgs);

        if (rowsUpdated != 0) {
            if (getContext() != null) {
                getContext().getContentResolver().notifyChange(uri, null);
            }
        }

        if (HomeActivity.isTablet) {
            TabletMainActivity.diseaseUpdated = true;
        }

        return rowsUpdated;
    }

    private int updateTreatmentPhoto(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        if (values == null || values.size() == 0) {
            return 0;
        }

        if (values.containsKey(TreatmentPhotosEntry.COLUMN_TR_PHOTO_NAME)) {
            String trPhotoName = values.getAsString(TreatmentPhotosEntry.COLUMN_TR_PHOTO_NAME);
            if (trPhotoName == null) {
                throw new IllegalArgumentException("disease requires a name");
            }
        }

        if (values.containsKey(TreatmentPhotosEntry.COLUMN_TR_PHOTO_DATE)) {
            String trPhotoDate = values.getAsString(TreatmentPhotosEntry.COLUMN_TR_PHOTO_DATE);
            if (trPhotoDate == null) {
                throw new IllegalArgumentException("disease requires a registration day");
            }
        }

        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        int rowsUpdated = database.update(TreatmentPhotosEntry.TREATMENT_PHOTOS_TABLE_NAME, values, selection, selectionArgs);

        if (rowsUpdated != 0) {
            if (getContext() != null) {
                getContext().getContentResolver().notifyChange(uri, null);
            }
        }

        return rowsUpdated;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {

        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        int rowsDeleted;

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case USERS:
                rowsDeleted = database.delete(UsersEntry.USERS_TABLE_NAME, selection, selectionArgs);

                if (rowsDeleted != 0) {
                    if (HomeActivity.isTablet) {
                        TabletMainActivity.userDeleted = true;
                    }
                }

                break;
            case USER_ID:
                selection = UsersEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = database.delete(UsersEntry.USERS_TABLE_NAME, selection, selectionArgs);
                break;
            case DISEASES:
                rowsDeleted = database.delete(DiseasesEntry.DISEASES_TABLE_NAME, selection, selectionArgs);
                break;
            case DISEASES_ID:
                selection = DiseasesEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = database.delete(DiseasesEntry.DISEASES_TABLE_NAME, selection, selectionArgs);
                break;
            case TREATMENT_PHOTOS:
                rowsDeleted = database.delete(TreatmentPhotosEntry.TREATMENT_PHOTOS_TABLE_NAME, selection, selectionArgs);
                break;
            case TREATMENT_PHOTOS_ID:
                selection = TreatmentPhotosEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = database.delete(TreatmentPhotosEntry.TREATMENT_PHOTOS_TABLE_NAME, selection, selectionArgs);

                if (rowsDeleted != 0) {
                    if (HomeActivity.isTablet && TabletMainActivity.inWideView) {
                        TabletMainActivity.treatmentPhotoDeleted = true;
                    }
                }
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }

        if (rowsDeleted != 0) {
            if (getContext() != null) {
                getContext().getContentResolver().notifyChange(uri, null);
            }
        }

        return rowsDeleted;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case USERS:
                return UsersEntry.CONTENT_USERS_LIST_TYPE;
            case USER_ID:
                return UsersEntry.CONTENT_USERS_ITEM_TYPE;
            case DISEASES:
                return DiseasesEntry.CONTENT_DISEASES_LIST_TYPE;
            case DISEASES_ID:
                return DiseasesEntry.CONTENT_DISEASES_ITEM_TYPE;
            case TREATMENT_PHOTOS:
                return TreatmentPhotosEntry.CONTENT_TREATMENT_PHOTOS_LIST_TYPE;
            case TREATMENT_PHOTOS_ID:
                return TreatmentPhotosEntry.CONTENT_TREATMENT_PHOTOS_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }
}
