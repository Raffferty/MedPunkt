package com.gmail.krbashianrafael.medpunkt.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public final class MedContract {

    private MedContract() {

    }

    public static final String CONTENT_AUTHORITY = "com.gmail.krbashianrafael.medpunkt";

    private static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    static final String PATH_USERS = "users";

    static final String PATH_DISEASES = "diseases";

    static final String PATH_TREATMENT_PHOTOS = "treatmentPhotos";

    public static final class UsersEntry implements BaseColumns {

       public static final Uri CONTENT_USERS_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_USERS);

        static final String CONTENT_USERS_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_USERS;

        static final String CONTENT_USERS_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_USERS;

        final static String USERS_TABLE_NAME = "users";

        public final static String U_ID = BaseColumns._ID;

        public final static String COLUMN_USER_NAME = "u_name";

        public final static String COLUMN_USER_DATE = "u_date";

        public final static String COLUMN_USER_PHOTO_PATH = "u_photo";
    }

    public static final class DiseasesEntry implements BaseColumns {

        public static final Uri CONTENT_DISEASES_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_DISEASES);

        static final String CONTENT_DISEASES_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_DISEASES;

        static final String CONTENT_DISEASES_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_DISEASES;

        final static String DISEASES_TABLE_NAME = "diseases";

        public final static String DIS_ID = BaseColumns._ID;

        public final static String COLUMN_U_ID = "u_id";

        public final static String COLUMN_DISEASE_NAME = "dis_name";

        public final static String COLUMN_DISEASE_DATE = "dis_date";

        public final static String COLUMN_DISEASE_TREATMENT = "dis_treatment";
    }

    public static final class TreatmentPhotosEntry implements BaseColumns {

        public static final Uri CONTENT_TREATMENT_PHOTOS_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_TREATMENT_PHOTOS);

        static final String CONTENT_TREATMENT_PHOTOS_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TREATMENT_PHOTOS;

        static final String CONTENT_TREATMENT_PHOTOS_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TREATMENT_PHOTOS;

        final static String TREATMENT_PHOTOS_TABLE_NAME = "treatmentPhotos";

        public final static String TR_PHOTO_ID = BaseColumns._ID;

        public final static String COLUMN_U_ID = "u_id";

        public final static String COLUMN_DIS_ID = "dis_id";

        public final static String COLUMN_TR_PHOTO_NAME = "tr_photo_name";

        public final static String COLUMN_TR_PHOTO_DATE = "tr_photo_date";

        public final static String COLUMN_TR_PHOTO_PATH = "tr_photo";
    }
}
