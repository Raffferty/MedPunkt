package com.gmail.krbashianrafael.medpunkt;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

public class DiseasesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diseases);

        Intent intent = getIntent();
        String textForTitle = intent.getStringExtra("Title");

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_group_white_24dp);
            actionBar.setElevation(2);
            if (textForTitle!=null){
                actionBar.setTitle(textForTitle);
            }
            else {
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
