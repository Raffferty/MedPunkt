package com.gmail.krbashianrafael.medpunkt;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

public class DiseasesActivity extends AppCompatActivity {

    private String textUserName = null;
    private String birthDate = null;
    private String userPhotoUri = "No_Photo";
    // id пользователя
    private int _id = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diseases);

        Intent intent = getIntent();
        textUserName = intent.getStringExtra("UserName");
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

            if (textUserName != null) {
                actionBar.setTitle(textUserName);
            } else {
                actionBar.setTitle(R.string.txt_no_title);
            }
        }

        // это сейчас не видимо
        // сделать видимым, когда будет хоть одно заболевание
        FloatingActionButton fabAddDisease = findViewById(R.id.fabAddDisease);
        fabAddDisease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO открывать окно добавить заболевание

            }
        });

        // это сейчас видимо
        // сделать не видимым, когда будет хоть одно заболевание
        TextView textViewAddDiseas = findViewById(R.id.txt_empty_diseases);
        textViewAddDiseas.setOnClickListener(new View.OnClickListener() {
            //TODO открывать окно добавить заболевание
            @Override
            public void onClick(View v) {

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                Intent intent = new Intent(DiseasesActivity.this, UsersActivity.class);
                startActivity(intent);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
