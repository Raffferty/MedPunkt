package com.gmail.krbashianrafael.medpunkt;

import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class UsersActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor> {

    private ImageView userImage;
    private final String[] pathToPhoto = new String[1];

    private RecyclerView recyclerUsers;
    private LinearLayoutManager linearLayoutManager;
    private UsersRecyclerViewAdapter usersRecyclerViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_home_white_30dp);
        }

        // это видимо, т.к. добавлен фиктивный пользователь
        // сделать видимым, когда будет хоть одно заболевание
        FloatingActionButton fabAddUser = findViewById(R.id.fabAddUser);
        fabAddUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent userIntent = new Intent(UsersActivity.this, UserActivity.class);
                userIntent.putExtra("_idUser", 1);
                userIntent.putExtra("userPhotoUri", "No_Photo");
                userIntent.putExtra("newUser", true);
                startActivity(userIntent);
            }
        });

        // это сейчас невидимо, т.к. добавлен фиктивный пользователь
        // TODO сделать невидимым, когда будет хоть один пользователь
        TextView txtAddUsers = findViewById(R.id.txt_empty_users);
        txtAddUsers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent userIntent = new Intent(UsersActivity.this, UserActivity.class);
                userIntent.putExtra("userPhotoUri", "No_Photo");
                userIntent.putExtra("newUser", true);
                startActivity(userIntent);
            }
        });

        // начало ------ Фиктивниый юзер с фото
        /*LinearLayout linearLayoutRecyclerViewItem = findViewById(R.id.recycler_view_item);
        userImage = findViewById(R.id.user_image);
        pathToPhoto[0] = getString(R.string.path_to_user_photo);

        File imgFile = new File(pathToPhoto[0]);
        if (imgFile.exists()) {
            GlideApp.with(this)
                    .load(pathToPhoto[0])
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .error(R.drawable.ic_camera_alt_gray_24dp)
                    .transition(DrawableTransitionOptions.withCrossFade(800))
                    .into(userImage);
        } else {
            pathToPhoto[0] = "No_Photo";
        }*/
        // конец ------ Фиктивниый юзер с фото

        // нажатие на юзера
        /*linearLayoutRecyclerViewItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent userDiseasIntent = new Intent(UsersActivity.this, DiseasesActivity.class);
                userDiseasIntent.putExtra("_idUser", 1);
                userDiseasIntent.putExtra("UserName", "Вася");
                userDiseasIntent.putExtra("birthDate", "11.03.1968");
                userDiseasIntent.putExtra("userPhotoUri", pathToPhoto[0]);

                startActivity(userDiseasIntent);
            }
        });*/

        // кнопка редактирования юзера
        /*FrameLayout userItemEdit = findViewById(R.id.user_item_edit);

        userItemEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent userEditIntent = new Intent(UsersActivity.this, UserActivity.class);
                userEditIntent.putExtra("_idUser", 1);
                userEditIntent.putExtra("editUser", true);
                userEditIntent.putExtra("UserName", "Вася");
                userEditIntent.putExtra("birthDate", "11.03.1968");
                userEditIntent.putExtra("userPhotoUri", pathToPhoto[0]);

                startActivity(userEditIntent);
            }
        });*/


        // инициализируем recyclerUsers
        recyclerUsers = findViewById(R.id.recycler_users);

        // инициализируем linearLayoutManager
        linearLayoutManager = new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL,false);

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

        ArrayList<UserItem> myData = usersRecyclerViewAdapter.getUsersList();
        myData.clear();

        String pathToPhoto = getString(R.string.path_to_user_photo);

        myData.add(new UserItem(0,"11.03.1968","Я",pathToPhoto));
        myData.add(new UserItem(0,"11.03.1948","Мама",pathToPhoto));
        myData.add(new UserItem(0,"11.03.1938","Папа",pathToPhoto));
        myData.add(new UserItem(0,"11.03.1973","Брат",pathToPhoto));
        myData.add(new UserItem(0,"11.03.1968","Вася",pathToPhoto));
        myData.add(new UserItem(0,"11.03.1968","Петя",pathToPhoto));
        myData.add(new UserItem(0,"11.03.1968","Саша",pathToPhoto));
        myData.add(new UserItem(0,"11.03.1968","Рая",pathToPhoto));
        myData.add(new UserItem(0,"11.03.1968","Роза",pathToPhoto));
        myData.add(new UserItem(0,"11.03.1968","Федя",pathToPhoto));
        myData.add(new UserItem(0,"11.03.1968","Степа",pathToPhoto));
        myData.add(new UserItem(0,"11.03.1968","Гриша",pathToPhoto));

        // если еще нет снимков, то делаем txtAddPhotos.setVisibility(View.VISIBLE);
        if (myData.size() == 0) {
            txtAddUsers.setVisibility(View.VISIBLE);
            fabAddUser.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        // для возобновления фото при рестарте
        usersRecyclerViewAdapter.notifyDataSetChanged();

        // для возобновления фото при рестарте
       /* pathToPhoto[0] = getString(R.string.path_to_user_photo);

        File imgFile = new File(pathToPhoto[0]);
        if (imgFile.exists()) {
            GlideApp.with(this)
                    .load(pathToPhoto[0])
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .error(R.drawable.ic_camera_alt_gray_24dp)
                    .transition(DrawableTransitionOptions.withCrossFade(500))
                    .into(userImage);
        } else {
            userImage.setImageResource(R.drawable.ic_camera_alt_gray_24dp);
            pathToPhoto[0] = "No_Photo";
        }*/

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent toHomeIntent = new Intent(UsersActivity.this, HomeActivity.class);
        toHomeIntent.putExtra("fromUsers", true);
        startActivity(toHomeIntent);

        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                Intent toHomeIntent = new Intent(UsersActivity.this, HomeActivity.class);
                toHomeIntent.putExtra("fromUsers", true);
                startActivity(toHomeIntent);

                finish();

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // методы Лоадера
    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
