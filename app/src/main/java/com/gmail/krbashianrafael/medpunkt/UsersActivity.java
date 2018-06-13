package com.gmail.krbashianrafael.medpunkt;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;

import java.io.File;

public class UsersActivity extends AppCompatActivity {

    private ImageView userImage;
    private final String[] pathToPhoto = new String[1];

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
        TextView addUsers = findViewById(R.id.txt_empty_users);
        addUsers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent userIntent = new Intent(UsersActivity.this, UserActivity.class);
                userIntent.putExtra("userPhotoUri", "No_Photo");
                userIntent.putExtra("newUser", true);
                startActivity(userIntent);
            }
        });

        // начало ------ Фиктивниый юзер с фото
        LinearLayout linearLayoutRecyclerViewItem = findViewById(R.id.recycler_view_item);
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
        }
        // конец ------ Фиктивниый юзер с фото

        // нажатие на юзера
        linearLayoutRecyclerViewItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent userDiseasIntent = new Intent(UsersActivity.this, DiseasesActivity.class);
                userDiseasIntent.putExtra("_idUser", 1);
                userDiseasIntent.putExtra("UserName", "Вася");
                userDiseasIntent.putExtra("birthDate", "11.03.1968");
                userDiseasIntent.putExtra("userPhotoUri", pathToPhoto[0]);

                startActivity(userDiseasIntent);
            }
        });

        // кнопка редактирования юзера
        FrameLayout userItemEdit = findViewById(R.id.user_item_edit);

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
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        // для возобновления фото при рестарте
        pathToPhoto[0] = getString(R.string.path_to_user_photo);

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
        }

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
}
