package com.gmail.krbashianrafael.medpunkt.phone;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.gmail.krbashianrafael.medpunkt.shared.DiseaseItem;
import com.gmail.krbashianrafael.medpunkt.R;
import com.gmail.krbashianrafael.medpunkt.data.MedContract.DiseasesEntry;
import com.gmail.krbashianrafael.medpunkt.shared.DiseaseRecyclerViewAdapter;

import java.util.ArrayList;
import java.util.Collections;

public class DiseasesActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor> {

    private long _idUser = 0;

    static String textUserName;

    private TextView textViewAddDisease;

    private FloatingActionButton fabAddDisease;

    private Animation fabShowAnimation;
    private Animation fadeInAnimation;

    public static boolean mScrollToStart = false;

    private RecyclerView recyclerDiseases;
    private DiseaseRecyclerViewAdapter diseaseRecyclerViewAdapter;

    /**
     * Лоадеров может много (они обрабатываются в case)
     * поэтому устанавливаем инициализатор для каждого лоадера
     * в данном случае private static final int DISEASES_LOADER = 1;
     */
    private static final int DISEASES_LOADER = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diseases);

        Intent intent = getIntent();

        if (intent.hasExtra("UserName")) {
            textUserName = intent.getStringExtra("UserName");
        }

        if (intent.hasExtra("_idUser")) {
            _idUser = intent.getLongExtra("_idUser", 0);
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
                textUserName = "";
            }
        }

        textViewAddDisease = findViewById(R.id.txt_empty_diseases);
        textViewAddDisease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent treatmentIntent = new Intent(DiseasesActivity.this, TreatmentActivity.class);
                treatmentIntent.putExtra("_idUser", _idUser);
                treatmentIntent.putExtra("newDisease", true);
                treatmentIntent.putExtra("editDisease", true);
                treatmentIntent.putExtra("diseaseName", "");
                treatmentIntent.putExtra("textTreatment", "");
                startActivity(treatmentIntent);
            }
        });

        fabAddDisease = findViewById(R.id.fabAddDisease);
        fabAddDisease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent treatmentIntent = new Intent(DiseasesActivity.this, TreatmentActivity.class);

                treatmentIntent.putExtra("_idUser", _idUser);
                treatmentIntent.putExtra("newDisease", true);
                treatmentIntent.putExtra("editDisease", true);
                treatmentIntent.putExtra("diseaseName", "");
                treatmentIntent.putExtra("textTreatment", "");
                startActivity(treatmentIntent);
            }
        });

        fadeInAnimation = AnimationUtils.loadAnimation(this, R.anim.fadein);

        fabShowAnimation = AnimationUtils.loadAnimation(this, R.anim.fab_show);
        fabShowAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                fabAddDisease.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                fabAddDisease.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                fabAddDisease.setVisibility(View.VISIBLE);
            }
        });

        recyclerDiseases = findViewById(R.id.recycler_diseases);

        // при нажатии на "чем болел" список заболеваний прокручивется вверх
        TextView txtDiseases = findViewById(R.id.txt_diseases);
        txtDiseases.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recyclerDiseases.smoothScrollToPosition(0);
            }
        });

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false);

        // устанавливаем LayoutManager для RecyclerView
        recyclerDiseases.setLayoutManager(linearLayoutManager);

        // инициализируем DiseaseRecyclerViewAdapter
        diseaseRecyclerViewAdapter = new DiseaseRecyclerViewAdapter(this);

        // устанавливаем адаптер для RecyclerView
        recyclerDiseases.setAdapter(diseaseRecyclerViewAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // сразу INVISIBLE делаем чтоб не было скачков при смене вида
        textViewAddDisease.setVisibility(View.INVISIBLE);
        fabAddDisease.setVisibility(View.INVISIBLE);

        // Инициализируем Loader
        getLoaderManager().initLoader(DISEASES_LOADER, null, this);
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

    /*
   ниже имплиментация метдов интерфеса LoaderManager.LoaderCallbacks<Cursor>
   которые будет вызываться при активации getLoaderManager().initLoader(DISEASES_LOADER, null, this);
   */

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        // для Loader в projection обязательно нужно указывать поле с _ID
        // здесь мы указываем поля, которые будем брать из Cursor для дальнейшей передачи в RecyclerView
        String[] projection = {
                DiseasesEntry.DIS_ID,
                DiseasesEntry.COLUMN_U_ID,
                DiseasesEntry.COLUMN_DISEASE_NAME,
                DiseasesEntry.COLUMN_DISEASE_DATE,
                DiseasesEntry.COLUMN_DISEASE_TREATMENT};

        // выборку заболеванй делаем по _idUser
        String selection = DiseasesEntry.COLUMN_U_ID + "=?";
        String[] selectionArgs = new String[]{String.valueOf(_idUser)};

        // Loader грузит ВСЕ данные из таблицы users через Provider в diseaseRecyclerViewAdapter и далее в recyclerDiseases
        return new CursorLoader(this,   // Parent activity context
                DiseasesEntry.CONTENT_DISEASES_URI,   // Provider content URI to query = content://com.gmail.krbashianrafael.medpunkt/diseases/
                projection,             // Columns to include in the resulting Cursor
                selection,                   // selection by DiseasesEntry.COLUMN_U_ID
                selectionArgs,                   // selection arguments by _idUser
                null);                  // Default sort order
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

        ArrayList<DiseaseItem> myData = diseaseRecyclerViewAdapter.getDiseaseList();
        myData.clear();

        if (cursor != null) {

            // устанавливаем курсор на исходную (на случай, если курсор используем повторно после прохождения цикла
            cursor.moveToPosition(-1);

            // проходим в цикле курсор и заполняем объектами DiseaseItem наш ArrayList<DiseaseItem> myData
            while (cursor.moveToNext()) {

                int disease_idColumnIndex = cursor.getColumnIndex(DiseasesEntry._ID);
                int diseaseUser_IdColumnIndex = cursor.getColumnIndex(DiseasesEntry.COLUMN_U_ID);
                int disease_nameColumnIndex = cursor.getColumnIndex(DiseasesEntry.COLUMN_DISEASE_NAME);
                int disease_dateColumnIndex = cursor.getColumnIndex(DiseasesEntry.COLUMN_DISEASE_DATE);
                int disease_treatmentColumnIndex = cursor.getColumnIndex(DiseasesEntry.COLUMN_DISEASE_TREATMENT);

                long _diseaseId = cursor.getLong(disease_idColumnIndex);
                long _diseaseUserId = cursor.getLong(diseaseUser_IdColumnIndex);
                String diseaseName = cursor.getString(disease_nameColumnIndex);
                String diseaseDate = cursor.getString(disease_dateColumnIndex);
                String diseaseTreatment = cursor.getString(disease_treatmentColumnIndex);

                myData.add(new DiseaseItem(_diseaseId, _diseaseUserId, diseaseName, diseaseDate, diseaseTreatment));
            }
        }

        // делаем сортировку заболеваний по именеи
        Collections.sort(myData);

        recyclerDiseases.setVisibility(View.VISIBLE);

        // оповещаем LayoutManager, что произошли изменения
        // LayoutManager обновляет RecyclerView
        diseaseRecyclerViewAdapter.notifyDataSetChanged();

        // делаем destroyLoader, чтоб он сам повторно не вызывался,
        // а вызывался при каждом входе в активити
        getLoaderManager().destroyLoader(DISEASES_LOADER);

        if (myData.size() == 0) {
            recyclerDiseases.setVisibility(View.INVISIBLE);

            new Handler(Looper.getMainLooper()).
                    postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            textViewAddDisease.setVisibility(View.VISIBLE);
                            textViewAddDisease.startAnimation(fadeInAnimation);
                        }
                    }, 300);
        } else {
            fabAddDisease.startAnimation(fabShowAnimation);
        }

        if (mScrollToStart && myData.size() != 0) {
            recyclerDiseases.smoothScrollToPosition(0);
            mScrollToStart = false;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        ArrayList<DiseaseItem> myData = diseaseRecyclerViewAdapter.getDiseaseList();
        myData.clear();
        diseaseRecyclerViewAdapter.notifyDataSetChanged();
    }
}
