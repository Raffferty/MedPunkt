package com.gmail.krbashianrafael.medpunkt;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

public class DiseasesActivity extends AppCompatActivity {

    private String textForDiseasesActivityTitle = null;
    private String birthDate = null;
    private String userPhotoUri = "No_Photo";
    // id пользователя
    private int _id = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diseases);

        Intent intent = getIntent();
        textForDiseasesActivityTitle = intent.getStringExtra("Title");
        birthDate = intent.getStringExtra("birthDate");
        if (intent.hasExtra("userPhotoUri")) {
            userPhotoUri = intent.getStringExtra("userPhotoUri");
        }
        if (intent.hasExtra("_id")) {
            _id = intent.getIntExtra("_id", 0);
        }

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_group_white_24dp);

            if (textForDiseasesActivityTitle != null) {
                actionBar.setTitle(textForDiseasesActivityTitle);
            } else {
                actionBar.setTitle(R.string.txt_no_title);
            }
        }

        ImageView imageViewAddDiseas = findViewById(R.id.imageViewAddDiseas);
        imageViewAddDiseas.setOnClickListener(new View.OnClickListener() {
            //TODO открывать окно добавить заболевание
            @Override
            public void onClick(View v) {
                Intent userIntent = new Intent(DiseasesActivity.this, UserActivity.class);
                startActivity(userIntent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //getMenuInflater().inflate(R.menu.menu_users, menu);

        //добавляем в меню надпись с иконкой удалить
        menu.add(0, R.id.action_edit_profile, 1, menuIconWithText(getResources().getDrawable(R.drawable.ic_mode_edit_blue_24dp), getResources().getString(R.string.edit_profile)));
        menu.add(0, R.id.action_add_diseas, 2, menuIconWithText(getResources().getDrawable(R.drawable.ic_add_blue_24dp), getResources().getString(R.string.add_diseas)));
        return true;
    }

    private CharSequence menuIconWithText(Drawable r, String title) {
        r.setBounds(0, 0, r.getIntrinsicWidth(), r.getIntrinsicHeight());
        SpannableString sb = new SpannableString("    " + title);
        ImageSpan imageSpan = new ImageSpan(r, ImageSpan.ALIGN_BOTTOM);
        sb.setSpan(imageSpan, 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        return sb;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                Intent intent = new Intent(DiseasesActivity.this, UsersActivity.class);
                startActivity(intent);
                return true;

            case R.id.action_edit_profile:
                Intent userIntent = new Intent(DiseasesActivity.this, UserActivity.class);
                userIntent.putExtra("_id", _id);
                userIntent.putExtra("editUser", true);
                userIntent.putExtra("Title", textForDiseasesActivityTitle);
                userIntent.putExtra("birthDate", birthDate);
                userIntent.putExtra("userPhotoUri", userPhotoUri);

                Log.d("saveUserPhoto", " from DiseasesActivity userPhotoUri = " + userPhotoUri);
                Log.d("saveUserPhoto", " from DiseasesActivity _id = " + _id);

                startActivity(userIntent);

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
