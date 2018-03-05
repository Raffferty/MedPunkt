package com.gmail.krbashianrafael.medpunkt;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class TreatmentPhotoActivity extends AppCompatActivity {

    private static String userNameAndDiseasPhoto;

    private static boolean editPhoto = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_treatment_photo);

        Intent intent = getIntent();
        editPhoto = intent.getBooleanExtra("editPhoto", true);

        String userNameAndDiseas = "";
        if (intent.hasExtra("userNameAndDiseas")){
            userNameAndDiseas = intent.getStringExtra("userNameAndDiseas");
        }

        userNameAndDiseasPhoto = userNameAndDiseas + " / " + getResources().getString(R.string.title_treatment_photo);
        setTitle(userNameAndDiseasPhoto);
    }
}
