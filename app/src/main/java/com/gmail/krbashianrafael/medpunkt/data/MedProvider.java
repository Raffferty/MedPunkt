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
import android.util.Log;

import com.gmail.krbashianrafael.medpunkt.UsersActivity;
import com.gmail.krbashianrafael.medpunkt.data.MedContract.MedEntry;

public class MedProvider extends ContentProvider {

    /**
     * Tag for the log messages
     */
    public static final String LOG_TAG = "myProvider";

    /**
     * URI matcher code for the content URI for the users table
     */
    private static final int USERS = 100;

    /**
     * URI matcher code for the content URI for a single user in the users table
     */
    private static final int USER_ID = 101;

    /**
     * UriMatcher object to match a content URI to a corresponding code.
     * The input passed into the constructor represents the code to return for the root URI.
     * It's common to use NO_MATCH as the input for this case.
     */
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    // Static initializer.
    // This is run the first time anything is called from this class.
    // в статическом блоке сразу вставляем в  sUriMatcher принимаемые URI и возвращаемые значения при match
    static {
        // The calls to addURI() go here, for all of the content URI patterns that the provider should recognize.
        // All paths added to the UriMatcher have a corresponding code to return when a match is found.

        // The content URI of the form "content://com.gmail.krbashianrafael.medpunkt/users" will map to the
        // integer code {@link #USERS}. This URI is used to provide access to MULTIPLE rows
        // of the users table.

        /*
        addURI
        Added in API level 1
        void addURI (String authority,
                        String path,
                        int code)
        Add a URI to match, and the code to return when this URI is matched.
        URI nodes may be exact match string,
        the token "*" that matches any text,
        or the token "#" that matches only numbers.

        Starting from API level JELLY_BEAN_MR2, this method will accept a leading slash in the path.
        Parameters
        authority
            String: the authority to match
        path
            String: the path to match.
            * may be used as a wild card for any text,
            * and # may be used as a wild card for numbers.
        code
            int: the code that is returned when a URI is matched against the given components. Must be positive.
         */

        // вставляем в sUriMatcher строку URI и возвращаемое значение, в случае match
        // в данном случае для доступа ко всей таблице users:
        // com.gmail.krbashianrafael.medpunkt/users
        // и возвращаемое значение 100
        sUriMatcher.addURI(MedContract.CONTENT_AUTHORITY, MedContract.PATH_USERS, USERS);

        // The content URI of the form "content://com.gmail.krbashianrafael.medpunkt/users/#" will map to the
        // integer code {@link #USER_ID}. This URI is used to provide access to ONE single row
        // of the users table.

        // In this case, the "#" wildcard is used where "#" can be substituted for an integer.
        // For example, "content://com.gmail.krbashianrafael.medpunkt/users/3" matches, but
        // "content://com.gmail.krbashianrafael.medpunkt/users" (without a number at the end) doesn't match.

        // вставляем в sUriMatcher строку URI и возвращаемое значение, в случае match
        // в данном случае для доступа к одной строке из таблицы users:
        // com.gmail.krbashianrafael.medpunkt/users/#
        // и возвращаемое значение 101
        sUriMatcher.addURI(MedContract.CONTENT_AUTHORITY, MedContract.PATH_USERS + "/#", USER_ID);

    }

    /**
     * Database helper that will provide us access to the database
     */
    //это наш Database helper
    private MedDbHelper mDbHelper;


    @Override
    public boolean onCreate() {
        // создаем файл базы "medpunkt.db"
        // с версией 1
        mDbHelper = new MedDbHelper(getContext());
        return true;
    }


    /**
     * public Cursor query(
     * Uri uri,
     * String[] projection,
     * String selection,
     * String[] selectionArgs,
     * String sortOrder
     * )
     * Perform the query for the given URI. Use the given projection, selection, selection arguments, and sort order.
     * <p>
     * <p>
     * Parameters:
     * uri - The URI to query.
     * This will be the full URI sent by the client;
     * if the client is requesting a specific record,
     * the URI will end in a record number that the implementation should parse and add to a WHERE or HAVING clause,
     * specifying that _id value.
     * projection - The list of columns to put into the cursor. If null all columns are included.
     * selection - A selection criteria to apply when filtering rows. If null then all rows are included.
     * selectionArgs - You may include ?s in selection, which will be replaced by the values from selectionArgs,
     * in order that they appear in the selection. The values will be bound as Strings.
     * sortOrder - How the rows in the cursor should be sorted. If null then the provider is free to define the sort order.
     * <p>
     * Returns:
     * a Cursor or null.
     */

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri,
                        @Nullable String[] projection,
                        @Nullable String selection,
                        @Nullable String[] selectionArgs,
                        @Nullable String sortOrder) {

        // Get readable database
        SQLiteDatabase database = mDbHelper.getReadableDatabase();

        // This cursor will hold the result of the query
        //здесь cursor НЕ инициализируем в null, т.к. все case в switch определены
        Cursor cursor;

        // Figure out if the URI matcher can match the URI to a specific code
        int match = sUriMatcher.match(uri);


        switch (match) {
            case USERS:
                /*
                query
                Added in API level 1
                Cursor query (String table,
                                String[] columns,
                                String selection,
                                String[] selectionArgs,
                                String groupBy,
                                String having,
                                String orderBy)
                Query the given table, returning a Cursor over the result set.
                Parameters
                table
                String: The table name to compile the query against.
                columns
                String: A list of which columns to return.
                Passing null will return all columns, which is discouraged to prevent reading data from storage that isn't going to be used.
                selection
                String: A filter declaring which rows to return, formatted as an SQL WHERE clause (excluding the WHERE itself).
                Passing null will return all rows for the given table.
                selectionArgs
                String: You may include ?s in selection, which will be replaced by the values from selectionArgs,
                in order that they appear in the selection. The values will be bound as Strings.
                groupBy
                String: A filter declaring how to group rows, formatted as an SQL GROUP BY clause (excluding the GROUP BY itself).
                Passing null will cause the rows to not be grouped.
                having
                String: A filter declare which row groups to include in the cursor,
                if row grouping is being used, formatted as an SQL HAVING clause (excluding the HAVING itself).
                Passing null will cause all row groups to be included, and is required when row grouping is not being used.
                orderBy
                String: How to order the rows, formatted as an SQL ORDER BY clause (excluding the ORDER BY itself).
                Passing null will use the default sort order, which may be unordered.
                Returns
                Cursor
                A Cursor object, which is positioned before the first entry.
                Note that Cursors are not synchronized, see the documentation for more details.
                 */

                //т.к. case USERS, то и таблица для запроса будет users

                cursor = database.query(MedEntry.USERS_TABLE_NAME, projection, selection, selectionArgs,
                        null, null, null);
                break;
            case USER_ID:
                // For the USER_ID code, extract out the ID from the URI.
                // For an example URI such as "content://com.gmail.krbashianrafael.medpunkt/users/3",
                // the selection will be "_id=?"
                // and the selection argument will be a String array containing the actual ID of 3 in this case.

                // For every "?" in the selection, we need to have an element in the selection
                // arguments that will fill in the "?". Since we have 1 question mark in the
                // selection, we have 1 String in the selection arguments' String array.

                // ниже переопредделяем selection и selectionArgs в зависимости от входящего URI
                selection = MedEntry._ID + "=?";
                // ContentUris.parseId(uri) возвращает Id из uri
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

                // This will perform a query on the users table where the _id equals 3 to return a
                // Cursor containing that row of the table.

                cursor = database.query(MedEntry.USERS_TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }

        // Set notification URI on the Cursor,
        // so we know what content URI the Cursor was created for.
        // If the data at this URI changes, then we know we need to update the Cursor

        /*
        setNotificationUri
        Added in API level 1
        void setNotificationUri (ContentResolver cr,
                        Uri uri)
        Register to watch a content URI for changes.
        This can be the URI of a specific data row (for example, "content://my_provider_type/23"),
        or a a generic URI for a content type.

        Parameters
        cr
        ContentResolver: The content resolver from the caller's context.
                        The listener attached to this resolver will be notified.
        uri
        Uri: The content URI to watch.
         */

        /*
        т.к. обращение к Базе происходит из разных потоков,
        то, чтобы вернуть актуальный курсор, система должна проверить были ли изменения после образования этого курсора
        поэтому здесь мы устанавливаем слушателя на изменения в данном курсоре по данному uri
        и лишь после этого возвращаем актуальный курсор
         */
        if (getContext()!=null){
            cursor.setNotificationUri(getContext().getContentResolver(), uri);
        }

        return cursor;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        //проверяем входящий Uri
        //т.к. вставка может быть только в таблицу (а не в строку),
        //то используем только case USERS:
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case USERS:
                //вызываем вспомогатлеьный метод insertUser
                if (contentValues==null) {
                    return null;
                }
                return insertUser(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    /**
     * Insert user into the database with the given content values.
     * Return the new content URI for that specific row in the database.
     */
    private Uri insertUser(Uri uri, ContentValues values) {
        // Check that the name is not null
        //values.getAsString(MedEntry.COLUMN_USER_NAME);
        //возвращает значение по ключу из ContentValues
        // проверка имени на null
        String userName = values.getAsString(MedEntry.COLUMN_USER_NAME);
        if (userName == null) {
            throw new IllegalArgumentException("user requires a name");
        }

        // Check that the birthday is valid
        // проверка даты рождения на null
        String userDate = values.getAsString(MedEntry.COLUMN_USER_DATE);
        if (userDate == null) {
            throw new IllegalArgumentException("user requires a birthday");
        }

        // photo - это путь к файлу, либо, по умолчанию, No_Photo
        // не проверяем на null

        // Get writeable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // Insert the new user with the given values
        /*
        insert
        Added in API level 1
        long insert (String table,
                        String nullColumnHack,
                        ContentValues values)
        Convenience method for inserting a row into the database.
        Parameters
        table
        String: the table to insert the row into
        nullColumnHack
        String: optional; may be null.
            SQL doesn't allow inserting a completely empty row without naming at least one column name.
            If your provided values is empty, no column names are known and an empty row can't be inserted.
            If not set to null, the nullColumnHack parameter provides the name of nullable column name
            to explicitly insert a NULL into in the case where your values is empty.
        values
        ContentValues: this map contains the initial column values for the row.
        The keys should be the column names and the values the column values
        Returns
        long
        the row ID of the newly inserted row, or -1 if an error occurred
         */
        long id = database.insert(MedEntry.USERS_TABLE_NAME, null, values);

        // If the ID is -1, then the insertion failed. Log an error and return null.
        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }

        // здесь устанавливаем флаг mScrollToEnd в классе UsersActivity в true
        // чтоб после вставки новой строки в Базу и посел оповещения об изменениях
        // заново загрузился курсор и RecyclerView прокрутился вниз до последней позиции

        UsersActivity.mScrollToEnd = true;

        // Notify all listeners that the data has changed for the user content URI

        /*
        notifyChange
        Added in API level 1
        void notifyChange (Uri uri,
                        ContentObserver observer)
        Notify registered observers that a row was updated and attempt to sync changes to the network.
        To register, call registerContentObserver().
        By default, CursorAdapter objects will get this notification.

        Parameters
        uri
        Uri: The uri of the content that was changed.
        observer
        ContentObserver: The observer that originated the change, may be null .
        The observer that originated the change will only receive the notification
        if it has requested to receive self-change notifications by implementing deliverSelfNotifications() to return true.
         */

        if ( getContext()!=null){
            getContext().getContentResolver().notifyChange(uri, null);
        }

        // Once we know the ID of the new row in the table,
        // return the new URI with the ID appended to the end of it

        /*
        ContentUris
        public class ContentUris  extends Object
        Utility methods useful for working with Uri objects that use the "content" (content://) scheme.
        Content URIs have the syntax
        content://authority/path/id
        content:
        The scheme portion of the URI. This is always set to ContentResolver.SCHEME_CONTENT (value content://).
        authority
        A string that identifies the entire content provider.
        All the content URIs for the provider start with this string.
        To guarantee a unique authority, providers should consider using an authority
        that is the same as the provider class' package identifier.
        path
        Zero or more segments, separated by a forward slash (/), that identify some subset of the provider's data.
        Most providers use the path part to identify individual tables.
        Individual segments in the path are often called "directories" although they do not refer to file directories.
        The right-most segment in a path is often called a "twig"
        id
        A unique numeric identifier for a single row in the subset of data identified by the preceding path part.
        Most providers recognize content URIs that contain an id part and give them special handling.
        A table that contains a column named _ID often expects the id part to be a particular value for that column.
         */

        /*
        Uri withAppendedId (Uri contentUri, long id)
        Appends the given ID to the end of the path.
         */

        Log.d(LOG_TAG, "insirt");

        // возвращаем полный ContentUri с id вставленной строки
        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int update(@NonNull Uri uri,
                      @Nullable ContentValues contentValues, @Nullable String selection, @Nullable String[] selectionArgs) {
        Log.d(LOG_TAG, "update, " + uri.toString());

        final int match = sUriMatcher.match(uri);
        switch (match) {
            // это если обновлятся будут несколько строк во всей таблице users
            case USERS:
                if (contentValues==null){
                    return 0;
                }
                return updateUser(uri, contentValues, selection, selectionArgs);
            case USER_ID:
                if (contentValues==null){
                    return 0;
                }
                // это, если будет обновлятся одна строка в таблице users
                // For the USER_ID code, extract out the ID from the URI,
                // so we know which row to update. Selection will be "_id=?"
                // and selection arguments will be a String array containing the actual ID.
                selection = MedEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateUser(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    /**
     * это всмопомогательный метод для обновления строк
     * Update users in the database with the given content values. Apply the changes to the rows
     * specified in the selection and selection arguments (which could be 0 or 1 or more users).
     * Return the number of rows that were successfully updated.
     */
    private int updateUser(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        // If the {@link MedEntry#COLUMN_USER_NAME} key is present,
        // check that the name value is not null.
        // проверка имени на null
        if (values.containsKey(MedEntry.COLUMN_USER_NAME)) {
            String userName = values.getAsString(MedEntry.COLUMN_USER_NAME);
            if (userName == null) {
                throw new IllegalArgumentException("User requires a name");
            }
        }

        // If the {@link MedEntry.COLUMN_USER_DATE} key is present,
        // check that the birthday value is valid.
        // проверка birthday на null
        if (values.containsKey(MedEntry.COLUMN_USER_DATE)) {
            String userDate = values.getAsString(MedEntry.COLUMN_USER_DATE);
            if (userDate == null) {
                throw new IllegalArgumentException("User requires a birthday");
            }
        }

        // photo - это путь к файлу, либо, по умолчанию, No_Photo
        // не проверяем на null

        // If there are no values to update, then don't try to update the database
        // если ничего не передано для обновления - базу не обновляем и сразу возвращаем ноль (количество обновленных строк)
        if (values.size() == 0) {
            return 0;
        }

        // Otherwise, get writeable database to update the data
        // иначе, открываем базу для записи
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // Perform the update on the database and get the number of rows affected
        int rowsUpdated = database.update(MedEntry.USERS_TABLE_NAME, values, selection, selectionArgs);

        //If 1 or more rows were updated, then notify all listeners that the data at the
        // given URI has changed
        if (rowsUpdated != 0) {
            if (getContext()!=null){
                getContext().getContentResolver().notifyChange(uri, null);
            }
        }

        // Returns the number of database rows affected by the update statement
        // обновляем базу, при этом, возвращается колиество обновленных строк
        return rowsUpdated;
    }

    /**
     * public int delete(
     * Uri uri,
     * String selection,
     * String[] selectionArgs
     * )
     * Delete the data at the given selection and selection arguments.
     * <p>
     * Specified by:
     * delete in class ContentProvider
     * <p>
     * Parameters:
     * uri - The full URI to query, including a row ID (if a specific record is requested).
     * selection - An optional restriction to apply to rows when deleting.
     * <p>
     * Returns:
     * The number of rows affected.
     * возвращает количество успешно удаленных строк!
     */

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        Log.d(LOG_TAG, "delete, " + uri.toString());

        // Get writeable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // Track the number of rows that were deleted
        int rowsDeleted;

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case USERS:
                // Delete all rows that match the selection and selection args

                /*
                delete
                Added in API level 1
                int delete (String table,
                                String whereClause,
                                String[] whereArgs)
                Convenience method for deleting rows in the database.
                Parameters
                table
                String: the table to delete from
                whereClause
                String: the optional WHERE clause to apply when deleting. Passing null will delete all rows.
                whereArgs
                String: You may include ?s in the where clause, which will be replaced by the values from whereArgs.
                The values will be bound as Strings.
                Returns
                int
                the number of rows affected if a whereClause is passed in, 0 otherwise.
                To remove all rows and get a count pass "1" as the whereClause.
                 */
                rowsDeleted = database.delete(MedEntry.USERS_TABLE_NAME, selection, selectionArgs);
                break;
            case USER_ID:
                // Delete a single row given by the ID in the URI
                selection = MedEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

                rowsDeleted = database.delete(MedEntry.USERS_TABLE_NAME, selection, selectionArgs);

                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }

        Log.d(LOG_TAG, "deleted " + rowsDeleted + " rows");

        // If 1 or more rows were deleted, then notify all listeners that the data at the
        // given URI has changed
        if (rowsDeleted != 0) {
            if (getContext()!=null){
                getContext().getContentResolver().notifyChange(uri, null);
            }
        }
        // Return the number of rows deleted
        return rowsDeleted;
    }

    /**
     * public String getType(
     * Uri uri
     * )
     * Returns the MIME (Multipurpose Internet Mail Extensions) type of data for the content URI.
     * <p>
     * One use case where this functionality is important is if you’re sending an intent with a URI set on the data field.
     * The Android system will check the MIME type of that URI
     * to determine which app component on the device can best handle your request.
     * (If the URI happens to be a content URI, then the system will check with the corresponding ContentProvider
     * to ask for the MIME type using the getType() method.)
     * <p>
     * Specified by:
     * getType in class ContentProvider
     * <p>
     * Parameters:
     * uri - the URI to query.
     * <p>
     * Returns:
     * a MIME (Multipurpose Internet Mail Extensions) type string, or null if there is no type.
     */

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        Log.d(LOG_TAG, "getType, " + uri.toString());

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case USERS:
                return MedEntry.CONTENT_USERS_LIST_TYPE; //"vnd.android.cursor.dir/com.gmail.krbashianrafael.medpunkt/users/"
            case USER_ID:
                return MedEntry.CONTENT_USERS_ITEM_TYPE; // "vnd.android.cursor.item/om.gmail.krbashianrafael.medpunkt/users/"
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }
}
