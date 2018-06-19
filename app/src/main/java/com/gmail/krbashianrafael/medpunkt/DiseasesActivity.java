package com.gmail.krbashianrafael.medpunkt;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class DiseasesActivity extends AppCompatActivity {

    private int _idUser = 0;

    private static String textUserName;
    private String userPhotoUri, birthDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diseases);

        Intent intent = getIntent();

        if (intent.hasExtra("UserName")){
            textUserName = intent.getStringExtra("UserName");
        }

        birthDate = intent.getStringExtra("birthDate");

        if (intent.hasExtra("userPhotoUri")) {
            userPhotoUri = intent.getStringExtra("userPhotoUri");
        }

        if (intent.hasExtra("_idUser")) {
            _idUser = intent.getIntExtra("_idUser", 0);
        }

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_group_white_30dp);
            actionBar.setElevation(0);

            if (textUserName != null) {
                actionBar.setTitle(textUserName);
            } else {
                actionBar.setTitle(R.string.txt_no_title);
            }
        }

        // фиктивное заболевание
        LinearLayout recyclerDiseasesItem = findViewById(R.id.recycler_diseases_item);
        recyclerDiseasesItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Intent treatmentIntent = new Intent(DiseasesActivity.this, TreatmentActivity.class);
                Intent treatmentIntent = new Intent(DiseasesActivity.this, NewTreatmentActivity.class);
                treatmentIntent.putExtra("_idDisease", 2);
                treatmentIntent.putExtra("newDisease", false);
                treatmentIntent.putExtra("editDisease", true);
                treatmentIntent.putExtra("diseaseName", "Грипп");
                /*treatmentIntent.putExtra("textTreatment", "Пить чай\nПить чай\nПить чай\nПить чай\nПить чай\n" +
                        "Пить чай\nПить чай\nПить чай\nПить чай\nПить чай\nПить чай\nПить чай\nПить чай\nПить чай\n" +
                        "Пить чай\nПить чай\n");*/

                treatmentIntent.putExtra("textTreatment", "Пить чай");

                startActivity(treatmentIntent);
            }
        });


        FloatingActionButton fabAddDisease = findViewById(R.id.fabAddDisease);
        fabAddDisease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent treatmentIntent = new Intent(DiseasesActivity.this, NewTreatmentActivity.class);
                treatmentIntent.putExtra("newDisease", true);
                treatmentIntent.putExtra("editDisease", false);
                treatmentIntent.putExtra("diseaseName", "");
                treatmentIntent.putExtra("textTreatment", "");
                startActivity(treatmentIntent);
            }
        });


        TextView textViewAddDisease = findViewById(R.id.txt_empty_diseases);
        textViewAddDisease.setOnClickListener(new View.OnClickListener() {
            //TODO открывать окно добавить заболевание
            @Override
            public void onClick(View v) {
                Intent treatmentIntent = new Intent(DiseasesActivity.this, TreatmentActivity.class);
                treatmentIntent.putExtra("newDisease", true);
                treatmentIntent.putExtra("editDisease", false);
                treatmentIntent.putExtra("diseaseName", "");
                treatmentIntent.putExtra("textTreatment", "");
                startActivity(treatmentIntent);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                finish();
                return true;
            default:
                super.onOptionsItemSelected(item);
                finish();
                return true;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
