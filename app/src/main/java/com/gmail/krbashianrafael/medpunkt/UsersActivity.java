package com.gmail.krbashianrafael.medpunkt;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

import java.io.File;

public class UsersActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_home_white_24dp);
        }

        // это видимо, т.к. добавлен фиктивный пользователь
        // сделать видимым, когда будет хоть одно заболевание
        FloatingActionButton fabAddUser = findViewById(R.id.fabAddUser);
        fabAddUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent userIntent = new Intent(UsersActivity.this, UserActivity.class);
                userIntent.putExtra("userPhotoUri", "No_Photo");
                userIntent.putExtra("newUser", true);
                startActivity(userIntent);
            }
        });

        // это сейчас не видимо, т.к. добавлен фиктивный пользователь
        // сделать не видимым, когда будет хоть один пользователь
        TextView addUsers = findViewById(R.id.txt_empty_users);
        addUsers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent userIntent = new Intent(UsersActivity.this, UserActivity.class);
                startActivity(userIntent);
            }
        });

        // начало ------ Фиктивниый юзер с фото
        LinearLayout linearLayoutRecyclerViewItem = findViewById(R.id.recycler_view_item);
        ImageView userImage = findViewById(R.id.user_image);
        String pathToPhoto = getString(R.string.pathToPhoto);
        File imgFile = new File(pathToPhoto);
        if (imgFile.exists()) {
            Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            userImage.setImageBitmap(myBitmap);
        }
        else {
            pathToPhoto = "No_Photo";
        }

        // конец ------ Фиктивниый юзер с фото

        // это для анонимных классов ниже
        final String finalPathToPhoto = pathToPhoto;

        // нажатие на юзера
        linearLayoutRecyclerViewItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent userIntent = new Intent(UsersActivity.this, DiseasesActivity.class);
                userIntent.putExtra("_idUser", 1);
                userIntent.putExtra("UserName", "Вася");
                userIntent.putExtra("birthDate", "11.03.1968");
                userIntent.putExtra("userPhotoUri", finalPathToPhoto);

                startActivity(userIntent);
            }
        });

        // кнопка редактирования юзера
        FrameLayout userItemEdit = findViewById(R.id.user_item_edit);

        userItemEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent userIntent = new Intent(UsersActivity.this, UserActivity.class);
                userIntent.putExtra("_idUser", 1);
                userIntent.putExtra("editUser", true);
                userIntent.putExtra("UserName", "Вася");
                userIntent.putExtra("birthDate", "11.03.1968");
                userIntent.putExtra("userPhotoUri", finalPathToPhoto);

                startActivity(userIntent);
            }
        });


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                Intent intent = new Intent(UsersActivity.this, HomeActivity.class);
                intent.putExtra("fromUsers", true);
                startActivity(intent);

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
