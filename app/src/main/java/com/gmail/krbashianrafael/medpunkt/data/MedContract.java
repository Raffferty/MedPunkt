package com.gmail.krbashianrafael.medpunkt.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;


/**
 * Created by raf on 02.07.2018.
 * <p>
 * 1. для работы с БАЗЫОЙ ДАННЫХ SQL
 * создаем класс Контракта
 * делаем его final, т.к. в нем будут представленны констатнты и от него наследоваться не будут
 * конструктор делаем private, т.к. его объекты не буду созданы
 * <p>
 * для работы с content provider прописываем в нем констатные названия:
 * CONTENT_AUTHORITY = "com.gmail.krbashianrafael.medpunkt"
 * BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY)
 */

public final class MedContract {

    private MedContract() {

    }

    /**
     * The "Content authority" is a name for the entire content provider, similar to the
     * relationship between a domain name and its website.  A convenient string to use for the
     * content authority is the package name for the app, which is guaranteed to be unique on the
     * device.
     */
    public static final String CONTENT_AUTHORITY = "com.gmail.krbashianrafael.medpunkt";

    /**
     * Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
     * the content provider.
     * <p>
     * parse
     * Added in API level 1
     * Uri parse (String uriString)
     * Creates a Uri which parses the given encoded URI string.
     * Parameters
     * uriString
     * String: an RFC 2396-compliant, encoded URI
     * Returns
     * Uri
     * Uri for this given uri string
     * Throws
     * NullPointerException
     * if uriString is null
     */
    private static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    /**
     * прописываем пути к таблицам
     * Possible path (appended to base content URI for possible URI's)
     * For instance, content://com.gmail.krbashianrafael.medpunkt/users/ is a valid path for looking at user data.
     * content://com.gmail.krbashianrafael.medpunkt/staff/ will fail,
     * as the ContentProvider hasn't been given any information on what to do with "staff".
     */
    // путь к таблице users
    static final String PATH_USERS = "users";

    // путь к таблице diseases
    static final String PATH_DISEASES = "diseases";

    // путь к таблице treatmentPhotos
    static final String PATH_TREATMENT_PHOTOS = "treatmentPhotos";

    /**
     * Inner class that defines constant values for the users database table.
     * Each entry in the table represents a single user.
     */
    public static final class UsersEntry implements BaseColumns {

        /**
         * The content URI to access the med data in the provider
         */
        /*
        withAppendedPath
        Added in API level 1
        Uri withAppendedPath (Uri baseUri,
                        String pathSegment)
        Creates a new Uri by appending an already-encoded path segment to a base Uri.
        Parameters
        baseUri
        Uri: Uri to append path segment to
        pathSegment
        String: encoded path segment to append
        Returns
        Uri
        a new Uri based on baseUri with the given segment appended to the path
        Throws
        NullPointerException
        if baseUri is null
         */
        //итоговая строка Uri для доступа к таблице users через provider:
        // content://com.gmail.krbashianrafael.medpunkt/users/
        public static final Uri CONTENT_USERS_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_USERS);

        /**
         * The MIME (Multipurpose Internet Mail Extensions) type of the {@link #CONTENT_USERS_URI} for a list of users.
         * <p>
         * в итоге получается CONTENT_LIST_TYPE = "vnd.android.cursor.dir/com.gmail.krbashianrafael.medpunkt/users/"
         */
        static final String CONTENT_USERS_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_USERS;

        /**
         * The MIME (Multipurpose Internet Mail Extensions) type of the {@link #CONTENT_USERS_URI} for a single user.
         * <p>
         * в итоге получается CONTENT_ITEM_TYPE = "vnd.android.cursor.item/om.gmail.krbashianrafael.medpunkt/users/"
         */
        static final String CONTENT_USERS_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_USERS;

        // имя таблицы users
        final static String USERS_TABLE_NAME = "users";

        // колонки таблицы users
        /**
         * Unique ID number for the user.
         * <p>
         * Type: INTEGER
         */
        public final static String U_ID = BaseColumns._ID;

        /**
         * Name of the user.
         * <p>
         * Type: TEXT
         */
        public final static String COLUMN_USER_NAME = "u_name";

        /**
         * Birthday of the user.
         * <p>
         * Type: TEXT
         */
        public final static String COLUMN_USER_DATE = "u_date";

        /**
         * Photo of the user.
         * <p>
         * Type: TEXT
         * <p>
         * хранится будет ссылка на фото
         */
        public final static String COLUMN_USER_PHOTO_PATH = "u_photo";
    }

    /**
     * Inner class that defines constant values for the diseases database table.
     * Each entry in the table represents a single disease.
     */
    public static final class DiseasesEntry implements BaseColumns {

        //итоговая строка Uri для доступа к таблице diseases через provider:
        // content://com.gmail.krbashianrafael.medpunkt/diseases/
        public static final Uri CONTENT_DISEASES_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_DISEASES);

        /**
         * The MIME (Multipurpose Internet Mail Extensions) type of the {@link #CONTENT_DISEASES_URI} for a list of diseases.
         * <p>
         * в итоге получается CONTENT_LIST_TYPE = "vnd.android.cursor.dir/com.gmail.krbashianrafael.medpunkt/diseases/"
         */
        static final String CONTENT_DISEASES_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_DISEASES;

        /**
         * The MIME (Multipurpose Internet Mail Extensions) type of the {@link #CONTENT_DISEASES_URI} for a single disease.
         * <p>
         * в итоге получается CONTENT_ITEM_TYPE = "vnd.android.cursor.item/om.gmail.krbashianrafael.medpunkt/diseases/"
         */
        static final String CONTENT_DISEASES_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_DISEASES;

        // имя таблицы users
        final static String DISEASES_TABLE_NAME = "diseases";

        // колонки таблицы diseases
        /**
         * Unique ID number for the disease.
         * <p>
         * Type: INTEGER
         */
        public final static String DIS_ID = BaseColumns._ID;

        /**
         * Unique ID number for the user.
         * <p>
         * Type: LONG
         */
        public final static String COLUMN_U_ID = "u_id";

        /**
         * Name of the disease.
         * <p>
         * Type: TEXT
         */
        public final static String COLUMN_DISEASE_NAME = "dis_name";

        /**
         * Date of the disease.
         * <p>
         * Type: TEXT
         */
        public final static String COLUMN_DISEASE_DATE = "dis_date";

        /**
         * Treatment of the disease.
         * <p>
         * Type: TEXT
         */
        public final static String COLUMN_DISEASE_TREATMENT = "dis_treatment";
    }

    /**
     * Inner class that defines constant values for the treatmentPhotos database table.
     * Each entry in the table represents a single treatmentPhoto.
     */
    public static final class TreatmentPhotosEntry implements BaseColumns {

        //итоговая строка Uri для доступа к таблице treatmentPhotos через provider:
        // content://com.gmail.krbashianrafael.medpunkt/treatmentPhotos/
        public static final Uri CONTENT_TREATMENT_PHOTOS_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_TREATMENT_PHOTOS);

        /**
         * The MIME (Multipurpose Internet Mail Extensions) type of the {@link #CONTENT_TREATMENT_PHOTOS_URI} for a list of treatmentPhotos.
         * <p>
         * в итоге получается CONTENT_LIST_TYPE = "vnd.android.cursor.dir/com.gmail.krbashianrafael.medpunkt/treatmentPhotos/"
         */
        static final String CONTENT_TREATMENT_PHOTOS_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TREATMENT_PHOTOS;

        /**
         * The MIME (Multipurpose Internet Mail Extensions) type of the {@link #CONTENT_TREATMENT_PHOTOS_URI} for a single treatmentPhoto.
         * <p>
         * в итоге получается CONTENT_ITEM_TYPE = "vnd.android.cursor.item/om.gmail.krbashianrafael.medpunkt/treatmentPhotos/"
         */
        static final String CONTENT_TREATMENT_PHOTOS_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TREATMENT_PHOTOS;

        // имя таблицы treatmentPhotos
        final static String TREATMENT_PHOTOS_TABLE_NAME = "treatmentPhotos";

        // колонки таблицы treatmentPhotos
        /**
         * Unique ID number for the treatmentPhoto.
         * <p>
         * Type: INTEGER
         */
        public final static String TR_PHOTO_ID = BaseColumns._ID;

        /**
         * Unique ID number for the user.
         * <p>
         * Type: LONG
         */
        public final static String COLUMN_U_ID = "u_id";

        /**
         * Unique ID number for the disease.
         * <p>
         * Type: LONG
         */
        public final static String COLUMN_DIS_ID = "dis_id";

        /**
         * Name of the treatmentPhoto.
         * <p>
         * Type: TEXT
         */
        public final static String COLUMN_TR_PHOTO_NAME = "tr_photo_name";

        /**
         * Date of the treatmentPhoto.
         * <p>
         * Type: TEXT
         */
        public final static String COLUMN_TR_PHOTO_DATE = "tr_photo_date";

        /**
         * Photo of the treatment.
         * <p>
         * Type: TEXT
         * <p>
         * хранится будет ссылка на фото
         */
        public final static String COLUMN_TR_PHOTO_PATH = "tr_photo";
    }
}
