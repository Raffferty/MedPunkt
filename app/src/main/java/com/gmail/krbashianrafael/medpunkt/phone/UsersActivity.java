package com.gmail.krbashianrafael.medpunkt.phone;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.gmail.krbashianrafael.medpunkt.HomeActivity;
import com.gmail.krbashianrafael.medpunkt.R;
import com.gmail.krbashianrafael.medpunkt.UserItem;
import com.gmail.krbashianrafael.medpunkt.data.MedContract;
import com.gmail.krbashianrafael.medpunkt.data.MedContract.UsersEntry;

import java.util.ArrayList;
import java.util.Collections;

public class UsersActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor> {

    private TextView txtAddUsers;
    private FloatingActionButton fabAddUser;
    private RecyclerView recyclerUsers;
    private UsersRecyclerViewAdapter usersRecyclerViewAdapter;

    // Animation fabShowAnimation
    private Animation fabShowAnimation;
    private Animation fadeInAnimation;

    // boolean mScrollToEnd статическая переменная для выставления флага в true после вставки нового элемента в список
    // этот флаг необходим для прокрутки списка вниз до последнего элемента, чтоб был виден вставленный элемент
    // переменная статическая, т.к. будет меняться из класса MedProvider в методе insertUser
    public static boolean mScrollToEnd = false;

    public static int onResumeCounter = 0;

    /**
     * Identifier for the user data loader
     * Лоадеров может много (они обрабатываются в case)
     * поэтому устанавливаем инициализатор для каждого лоадера
     * в данном случае private static final int USERS_LOADER = 0;
     */
    private static final int USERS_LOADER = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_home_white_30dp);
            if (HomeActivity.iAmDoctor) {
                actionBar.setTitle(R.string.patients_title_activity);
            }
        }

        fabAddUser = findViewById(R.id.fabAddUser);
        fabAddUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent userIntent = new Intent(UsersActivity.this, UserActivity.class);
                userIntent.putExtra("userPhotoUri", "No_Photo");
                userIntent.putExtra("newUser", true);
                userIntent.putExtra("iAmDoctor", HomeActivity.iAmDoctor);
                startActivity(userIntent);
            }
        });

        fadeInAnimation = AnimationUtils.loadAnimation(this, R.anim.fadein);

        fabShowAnimation = AnimationUtils.loadAnimation(this, R.anim.fab_show);
        fabShowAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                fabAddUser.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                fabAddUser.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                fabAddUser.setVisibility(View.VISIBLE);
            }
        });

        txtAddUsers = findViewById(R.id.txt_empty_users);

        if (HomeActivity.iAmDoctor) {
            txtAddUsers.setText(R.string.patient_title_activity);
        }
        txtAddUsers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent userIntent = new Intent(UsersActivity.this, UserActivity.class);
                userIntent.putExtra("userPhotoUri", "No_Photo");
                userIntent.putExtra("newUser", true);
                userIntent.putExtra("iAmDoctor", HomeActivity.iAmDoctor);
                startActivity(userIntent);
            }
        });

        // инициализируем recyclerUsers
        recyclerUsers = findViewById(R.id.recycler_users);

        // инициализируем linearLayoutManager
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false);

        // инизиализируем разделитель для элементов recyclerTreatmentPhotos
        DividerItemDecoration itemDecoration = new DividerItemDecoration(
                recyclerUsers.getContext(), linearLayoutManager.getOrientation()
        );

        //инициализируем Drawable, который будет установлен как разделитель между элементами
        Drawable divider_blue = ContextCompat.getDrawable(this, R.drawable.blue_drawable);

        //устанавливаем divider_blue как разделитель между элементами
        if (divider_blue != null) {
            itemDecoration.setDrawable(divider_blue);
        }

        //устанавливаем созданный и настроенный объект DividerItemDecoration нашему recyclerView
        recyclerUsers.addItemDecoration(itemDecoration);

        // устанавливаем LayoutManager для RecyclerView
        recyclerUsers.setLayoutManager(linearLayoutManager);

        // инициализируем usersRecyclerViewAdapter
        usersRecyclerViewAdapter = new UsersRecyclerViewAdapter(this);

        // устанавливаем адаптер для RecyclerView
        recyclerUsers.setAdapter(usersRecyclerViewAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();

        onResumeCounter++;

        // сразу INVISIBLE делаем чтоб не было скачков при смене вида
        fabAddUser.setVisibility(View.INVISIBLE);
        txtAddUsers.setVisibility(View.INVISIBLE);

        // Инициализируем Loader
        // если НЕТ permission.READ_EXTERNAL_STORAGE, то будет грузиться @drawable/ic_camera_alt_gray_24dp
        getLoaderManager().initLoader(USERS_LOADER, null, this);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /*
   ниже имплиментация метдов интерфеса LoaderManager.LoaderCallbacks<Cursor>
   которые будет вызываться при активации getLoaderManager().initLoader(USERS_LOADER, null, this);
   --------------------------------------------------------------

   public Loader<Cursor> onCreateLoader(int id, Bundle args)

   Instantiate and return a new Loader for the given ID.
   Specified by:
   onCreateLoader in interface LoaderCallbacks

   Parameters:
   id - The ID whose loader is to be created.
   args - Any arguments supplied by the caller.

   Returns:
   Return a new Loader instance that is ready to start loading.
    */
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // Define a projection that specifies the columns from the table we care about.
        // для Loader в projection обязательно нужно указывать поле с _ID
        // здесь мы указываем поля, которые будем брать из Cursor для дальнейшей передачи в RecyclerView
        String[] projection = {
                UsersEntry.U_ID,
                UsersEntry.COLUMN_USER_NAME,
                MedContract.UsersEntry.COLUMN_USER_DATE,
                MedContract.UsersEntry.COLUMN_USER_PHOTO_PATH};

        // This loader will execute the ContentProvider's query method on a background thread
        // Loader грузит ВСЕ данные из таблицы users через Provider в usersRecyclerViewAdapter и далее в recyclerUsers
        return new CursorLoader(this,   // Parent activity context
                UsersEntry.CONTENT_USERS_URI,   // Provider content URI to query = content://com.gmail.krbashianrafael.medpunkt/users/
                projection,             // Columns to include in the resulting Cursor
                null,                   // No selection clause
                null,                   // No selection arguments
                null);                  // Default sort order
    }

    /*
    public void onLoadFinished(Loader<Cursor> loader, Cursor data)

    Called when a previously created loader has finished its load.
    Note that normally an application is not allowed to commit fragment transactions while in this call,
    since it can happen after an activity's state is saved.
    See FragmentManager.openTransaction() for further discussion on this.

    This function is guaranteed to be called prior to the release of the last data that was supplied for this Loader.
    At this point you should remove all use of the old data (since it will be released soon),
    but should not do your own release of the data since its Loader owns it and will take care of that.
    The Loader will take care of management of its data so you don't have to.
    In particular:
    The Loader will monitor for changes to the data, and report them to you through new calls here.
    You should not monitor the data yourself.

    For example, if the data is a android.database.Cursor and you place it in a android.widget.CursorAdapter,
    use the android.widget.CursorAdapter.CursorAdapter(android.content.Context, android.database.Cursor, int) constructor
    without passing in either android.widget.CursorAdapter.FLAG_AUTO_REQUERY
    or android.widget.CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER (that is, use 0 for the flags argument).
    This prevents the CursorAdapter from doing its own observing of the Cursor,
    which is not needed since when a change happens you will get a new Cursor throw another call here.

    The Loader will release the data once it knows the application is no longer using it.
    For example, if the data is a android.database.Cursor from a android.content.CursorLoader,
    you should not call close() on it yourself.
    If the Cursor is being placed in a android.widget.CursorAdapter,
    you should use the android.widget.CursorAdapter.swapCursor(android.database.Cursor) method
    so that the old Cursor is not closed.

    Specified by:
    onLoadFinished in interface LoaderCallbacks

    Parameters:
    loader - The Loader that has finished.
    data - The data generated by the Loader.
     */
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

        // получаем ссылку на данные в usersRecyclerViewAdapter
        // и очищаем ArrayList<UserItem> myData от данных
        ArrayList<UserItem> myData = usersRecyclerViewAdapter.getUsersList();
        myData.clear();

        if (cursor != null) {

            // устанавливаем курсор на исходную (на случай, если курсор используем повторно после прохождения цикла
            cursor.moveToPosition(-1);

            // проходим в цикле курсор и заполняем объектами UserItem наш ArrayList<UserItem> myData
            while (cursor.moveToNext()) {

                // Find the columns of user attributes that we're interested in
                int user_idColumnIndex = cursor.getColumnIndex(UsersEntry._ID);
                int user_nameColumnIndex = cursor.getColumnIndex(UsersEntry.COLUMN_USER_NAME);
                int user_dateColumnIndex = cursor.getColumnIndex(UsersEntry.COLUMN_USER_DATE);
                int user_photoColumnIndex = cursor.getColumnIndex(UsersEntry.COLUMN_USER_PHOTO_PATH);

                // Read the user attributes from the Cursor for the current user
                long _userId = cursor.getLong(user_idColumnIndex);
                String userName = cursor.getString(user_nameColumnIndex);

                String userBirthDate = cursor.getString(user_dateColumnIndex);
                String userPhotoUri = cursor.getString(user_photoColumnIndex);


                // добавляем новый user в ArrayList<UserItem> myData
                myData.add(new UserItem(_userId, userBirthDate, userName, userPhotoUri));
            }
        }

        // делаем сортировку пользователей по именеи
        Collections.sort(myData);

        /*
        public final void notifyDataSetChanged()
        Notify any registered observers that the data set has changed.

        There are two different classes of data change events, item changes and structural changes.

        Item changes are when a single item has its data updated but no positional changes have occurred.

        Structural changes are when items are inserted, removed or moved within the data set.
        This event does not specify what about the data set has changed,
        forcing any observers to assume that all existing items and structure may no longer be valid.

        LayoutManagers will be forced to fully rebind and relayout all visible views.

        RecyclerView will attempt to synthesize visible structural change events for adapters
        that report that they have stable IDs when this method is used.
        This can help for the purposes of animation and visual object persistence
        but individual item views will still need to be rebound and relaid out.

        If you are writing an adapter it will always be more efficient to use the more specific change events if you can.

        Rely on notifyDataSetChanged() as a last resort.

        See Also:
        notifyItemChanged(int), notifyItemInserted(int), notifyItemRemoved(int), notifyItemRangeChanged(int, int),
        notifyItemRangeInserted(int, int), notifyItemRangeRemoved(int, int)
         */

        // оповещаем LayoutManager, что произошли изменения
        // LayoutManager обновляет RecyclerView
        usersRecyclerViewAdapter.notifyDataSetChanged();

        // делаем destroyLoader, чтоб он сам повторно не вызывался,
        // а вызывался при каждом входе в активити
        getLoaderManager().destroyLoader(USERS_LOADER);

        // если нет пользователей, то делаем txtAddUsers.setVisibility(View.VISIBLE);
        // и fabAddUser.setVisibility(View.INVISIBLE);
        int myDataSize = myData.size();

        if (myDataSize == 0) {
            // если нет пользоватлей, то делаем видимым txtAddUsers
            new Handler(Looper.getMainLooper()).
                    postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            txtAddUsers.setVisibility(View.VISIBLE);
                            txtAddUsers.startAnimation(fadeInAnimation);
                        }
                    }, 300);
        } else if (myDataSize == 1 && onResumeCounter == 1 && !HomeActivity.iAmDoctor) {
            // если есть только один пользователь,
            // и влделец приложения НЕ доктор,
            // то идем сразу к его заболеваниям
            Intent userDiseasIntent = new Intent(this, DiseasesActivity.class);
            userDiseasIntent.putExtra("_idUser", myData.get(0).get_userId());
            userDiseasIntent.putExtra("UserName", myData.get(0).getUserName());

            startActivity(userDiseasIntent);
        } else {
            // если больше одного пользователя, то остаемся в окне "Пользователи"
            fabAddUser.startAnimation(fabShowAnimation);
        }

        // если флаг scrollToEnd выставлен в true, то прокручиваем RecyclerView вниз до конца,
        // чтоб увидеть новый вставленный элемент
        // и снова scrollToEnd выставляем в false
        if (mScrollToEnd && myData.size() != 0) {
            recyclerUsers.smoothScrollToPosition(myData.size() - 1);
            mScrollToEnd = false;
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        ArrayList<UserItem> myData = usersRecyclerViewAdapter.getUsersList();
        myData.clear();
        usersRecyclerViewAdapter.notifyDataSetChanged();
    }
}
