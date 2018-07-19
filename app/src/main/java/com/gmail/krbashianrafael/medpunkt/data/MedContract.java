package com.gmail.krbashianrafael.medpunkt.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;


/**
 * Created by raf on 02.07.2018.
 *
 * 1. для работы с БАЗЫОЙ ДАННЫХ SQL
 * создаем класс Контракта
 * делаем его final, т.к. в нем будут представленны констатнты и от него наследоваться не будут
 * конструктор делаем private, т.к. его объекты не буду созданы
 *
 * для работы с content provider прописываем в нем констатные названия:
 * CONTENT_AUTHORITY = "com.gmail.krbashianrafael.medpunkt"
 * BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY)
 * PATH_PETS = "pets"
 *
 * создаем иннер (внутренний) статический класс PetEntry для таблицы (table) pets
 *
 * для таблицы pets прописываем в нем констатные названия:
 * CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_PETS);
 * имени, колонок и гендерных типов.
 */

public final class MedContract {

    private MedContract(){

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
     *
     * parse
     Added in API level 1
     Uri parse (String uriString)
     Creates a Uri which parses the given encoded URI string.
     Parameters
     uriString
     String: an RFC 2396-compliant, encoded URI
     Returns
     Uri
     Uri for this given uri string
     Throws
     NullPointerException
     if uriString is null
     */
    static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    /**
     * Possible path (appended to base content URI for possible URI's)
     * For instance, content://com.gmail.krbashianrafael.medpunkt/users/ is a valid path for looking at user data.
     * content://com.gmail.krbashianrafael.medpunkt/staff/ will fail,
     * as the ContentProvider hasn't been given any information on what to do with "staff".
     */
    public static final String PATH_USERS = "users";

    /**
     * Inner class that defines constant values for the pets database table.
     * Each entry in the table represents a single pet.
     */
    public static final class MedEntry implements BaseColumns {

        /** The content URI to access the med data in the provider */
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
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_USERS);

        /**
         * The MIME (Multipurpose Internet Mail Extensions) type of the {@link #CONTENT_URI} for a list of users.
         *
         * в итоге получается CONTENT_LIST_TYPE = "vnd.android.cursor.dir/com.gmail.krbashianrafael.medpunkt/users/"
         */
        public static final String CONTENT_USERS_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_USERS;

        /**
         * The MIME (Multipurpose Internet Mail Extensions) type of the {@link #CONTENT_URI} for a single user.
         *
         * в итоге получается CONTENT_ITEM_TYPE = "vnd.android.cursor.item/om.gmail.krbashianrafael.medpunkt/users/"
         */
        public static final String CONTENT_USERS_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_USERS;

        // имя таблицы users
        public final static String USERS_TABLE_NAME = "users";

        // колонки таблицы users
        /**
         * Unique ID number for the pet (only for use in the database table).
         *
         * Type: INTEGER
         */
        public final static String _ID = BaseColumns._ID;

        /**
         * Name of the user.
         *
         * Type: TEXT
         */
        public final static String COLUMN_USER_NAME ="u_name";

        /**
         * Birthday of the user.
         *
         * Type: TEXT
         */
        public final static String COLUMN_USER_DATE = "u_date";

        /**
         * Photo of the user.
         *
         * Type: TEXT
         *
         * хранится будет ссылка на фото
         */
        public final static String COLUMN_USER_PHOTO = "u_photo";
    }
}
