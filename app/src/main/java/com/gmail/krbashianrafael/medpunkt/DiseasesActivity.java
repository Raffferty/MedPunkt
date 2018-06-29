package com.gmail.krbashianrafael.medpunkt;

import android.content.Intent;
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
import android.widget.TextView;

import java.util.ArrayList;

public class DiseasesActivity extends AppCompatActivity {

    private int _idUser = 0;

    private boolean newUser;

    private static String textUserName;
    private String userPhotoUri, birthDate;

    private RecyclerView recyclerDiseases;
    private LinearLayoutManager linearLayoutManager;
    private DiseaseRecyclerViewAdapter diseaseRecyclerViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diseases);

        Intent intent = getIntent();

        newUser = intent.getBooleanExtra("newUser", false);

        if (intent.hasExtra("UserName")) {
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

        TextView textViewAddDisease = findViewById(R.id.txt_empty_diseases);
        textViewAddDisease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent treatmentIntent = new Intent(DiseasesActivity.this, NewTreatmentActivity.class);
                treatmentIntent.putExtra("newDisease", true);
                treatmentIntent.putExtra("editDisease", true);
                treatmentIntent.putExtra("diseaseName", "");
                treatmentIntent.putExtra("textTreatment", "");
                startActivity(treatmentIntent);
            }
        });

        FloatingActionButton fabAddDisease = findViewById(R.id.fabAddDisease);
        fabAddDisease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent treatmentIntent = new Intent(DiseasesActivity.this, NewTreatmentActivity.class);
                treatmentIntent.putExtra("newDisease", true);
                treatmentIntent.putExtra("editDisease", true);
                treatmentIntent.putExtra("diseaseName", "");
                treatmentIntent.putExtra("textTreatment", "");
                startActivity(treatmentIntent);
            }
        });


        // инициализируем recyclerDiseases
        recyclerDiseases = findViewById(R.id.recycler_diseases);

        // при нажатии на "чем болел" список заболеваний прокручивется вверх
        TextView txtDiseases = findViewById(R.id.txt_diseases);
        txtDiseases.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recyclerDiseases.smoothScrollToPosition(0);
            }
        });

        // инициализируем linearLayoutManager
        linearLayoutManager = new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL,false);

        // инизиализируем разделитель для элементов recyclerTreatmentPhotos
        DividerItemDecoration itemDecoration = new DividerItemDecoration(
                recyclerDiseases.getContext(), linearLayoutManager.getOrientation()
        );

        //инициализируем Drawable, который будет установлен как разделитель между элементами
        Drawable divider_blue = ContextCompat.getDrawable(this, R.drawable.blue_drawable);

        //устанавливаем divider_blue как разделитель между элементами
        if (divider_blue != null) {
            itemDecoration.setDrawable(divider_blue);
        }

        //устанавливаем созданный и настроенный объект DividerItemDecoration нашему recyclerView
        recyclerDiseases.addItemDecoration(itemDecoration);

        // устанавливаем LayoutManager для RecyclerView
        recyclerDiseases.setLayoutManager(linearLayoutManager);

        // инициализируем DiseaseRecyclerViewAdapter
        diseaseRecyclerViewAdapter = new DiseaseRecyclerViewAdapter(this);

        // устанавливаем адаптер для RecyclerView
        recyclerDiseases.setAdapter(diseaseRecyclerViewAdapter);

        ArrayList<DiseaseItem> myData = diseaseRecyclerViewAdapter.getDiseaseList();
        myData.clear();

        String treatment = "Пить чай\nПить чай\nПить чай\nПить чай\nПить чай\n" +
                "Пить чай\nПить чай\nПить чай\nПить чай\nПить чай\nПить чай\n" +
                "Пить чай\nПить чай\nДолго долго, очень долго Пить чай\n" +
                "Долго долго, очень долго Пить чай\n" +
                "Долго долго, очень долго Пить чай\n" +
                "Долго долго, очень долго Пить чай\n" +
                "Долго долго, очень долго Пить чай\n" +
                "Долго долго, очень долго Пить чай\n" +
                "Долго долго, очень очень очень долго Пить\n" +
                "Долго долго, очень очень очень долго Пить\n" +
                "Долго долго, очень очень очень долго Пить\n" +
                "Долго долго, очень очень очень долго Пить\n" +
                "Долго долго, очень очень очень долго Пить\n" +
                "Долго долго, очень очень очень долго Пить\n" +
                "Долго долго, очень очень очень долго Пить\n" +
                "Долго долго, очень очень очень долго Пить\n" +
                "Долго долго, очень очень очень долго Пить чай";

        // временные данные
        if (!newUser){
            myData.add(new DiseaseItem(2,"Грипп","01.02.2018", treatment));
            myData.add(new DiseaseItem(2,"Давление","02.02.2018", treatment));
            myData.add(new DiseaseItem(2,"Ангина","03.02.2018", treatment));
            myData.add(new DiseaseItem(2,"Подколенка","04.02.2018", treatment));
            myData.add(new DiseaseItem(2,"Сопли","01.02.2018", treatment));
            myData.add(new DiseaseItem(2,"Ухо","01.02.2018", treatment));
            myData.add(new DiseaseItem(2,"Горло","01.02.2018", treatment));
            myData.add(new DiseaseItem(2,"Нос","01.02.2018", treatment));
            myData.add(new DiseaseItem(2,"Голова","01.02.2018", treatment));
            myData.add(new DiseaseItem(2,"Рука","01.02.2018", treatment));
            myData.add(new DiseaseItem(2,"Нога","01.02.2018", treatment));
            myData.add(new DiseaseItem(2,"Спина","01.02.2018", treatment));
            myData.add(new DiseaseItem(2,"Живот","01.02.2018", treatment));
        } else {
            textViewAddDisease.setVisibility(View.VISIBLE);
            fabAddDisease.setVisibility(View.INVISIBLE);
        }

        // если еще нет снимков, то делаем txtAddPhotos.setVisibility(View.VISIBLE);
        if (myData.size() == 0) {
            textViewAddDisease.setVisibility(View.VISIBLE);
            fabAddDisease.setVisibility(View.INVISIBLE);
        }
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
