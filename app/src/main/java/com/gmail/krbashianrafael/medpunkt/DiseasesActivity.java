package com.gmail.krbashianrafael.medpunkt;

import android.app.LoaderManager;
import android.content.CursorLoader;
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
import android.widget.TextView;

import com.gmail.krbashianrafael.medpunkt.data.MedContract.DiseasesEntry;

import java.util.ArrayList;

public class DiseasesActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor> {

    private long _idUser = 0;

    private String textUserName;

    private TextView textViewAddDisease;

    private FloatingActionButton fabAddDisease;

    // boolean mScrollToEnd статическая переменная для выставления флага в true после вставки нового элемента в список
    // этот флаг необходим для прокрутки списка вниз до последнего элемента, чтоб был виден вставленный элемент
    // переменная статическая, т.к. будет меняться из класса MedProvider в методе insertDisease
    public static boolean mScrollToEnd = false;

    /**
     * Identifier for the user data loader
     * Лоадеров может много (они обрабатываются в case)
     * поэтому устанавливаем инициализатор для каждого лоадера
     * в данном случае private static final int DISEASE_LOADER = 1;
     */
    private static final int DISEASE_LOADER = 1;

    private RecyclerView recyclerDiseases;
    private DiseaseRecyclerViewAdapter diseaseRecyclerViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diseases);

        Intent intent = getIntent();

        boolean newUser = intent.getBooleanExtra("newUser", false);

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
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false);

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

        // Инициализируем Loader
        getLoaderManager().initLoader(DISEASE_LOADER, null, this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // для возобновления данных
        if (diseaseRecyclerViewAdapter!=null){
            diseaseRecyclerViewAdapter.notifyDataSetChanged();
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

    /*
   ниже имплиментация метдов интерфеса LoaderManager.LoaderCallbacks<Cursor>
   которые будет вызываться при активации getLoaderManager().initLoader(DISEASE_LOADER, null, this);
   */

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        // Define a projection that specifies the columns from the table we care about.
        // для Loader в projection обязательно нужно указывать поле с _ID
        // здесь мы указываем поля, которые будем брать из Cursor для дальнейшей передачи в RecyclerView
        String[] projection = {
                DiseasesEntry.DIS_ID,
                DiseasesEntry.COLUMN_U_ID,
                DiseasesEntry.COLUMN_DISEASE_NAME,
                DiseasesEntry.COLUMN_DISEASE_DATE,
                DiseasesEntry.COLUMN_DISEASE_TREATMENT};


        // выборку заболеванй делаем по _idUser
        String  selection = DiseasesEntry.COLUMN_U_ID + "=?";
        String[] selectionArgs = new String[]{String.valueOf(_idUser)};

        // This loader will execute the ContentProvider's query method on a background thread
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

                // Find the columns of disease attributes that we're interested in
                int disease_idColumnIndex = cursor.getColumnIndex(DiseasesEntry._ID);
                int diseaseUser_IdColumnIndex = cursor.getColumnIndex(DiseasesEntry.COLUMN_U_ID);
                int disease_nameColumnIndex = cursor.getColumnIndex(DiseasesEntry.COLUMN_DISEASE_NAME);
                int disease_dateColumnIndex = cursor.getColumnIndex(DiseasesEntry.COLUMN_DISEASE_DATE);
                int disease_treatmentColumnIndex = cursor.getColumnIndex(DiseasesEntry.COLUMN_DISEASE_TREATMENT);

                // Read the disease attributes from the Cursor for the current disease
                long _diseaseId = cursor.getLong(disease_idColumnIndex);
                long _diseaseUserId = cursor.getLong(diseaseUser_IdColumnIndex);
                String diseaseName = cursor.getString(disease_nameColumnIndex);
                String diseaseDate = cursor.getString(disease_dateColumnIndex);
                String diseaseTreatment = cursor.getString(disease_treatmentColumnIndex);


                // добавляем новый DiseaseItem в ArrayList<DiseaseItem> myData
                myData.add(new DiseaseItem(_diseaseId, _diseaseUserId, diseaseName, diseaseDate, diseaseTreatment));
            }
        }

        // если нет заболеваний, то делаем textViewAddDisease.setVisibility(View.VISIBLE);
        // и fabAddDisease.setVisibility(View.INVISIBLE);
        if (myData.size() == 0) {
            textViewAddDisease.setVisibility(View.VISIBLE);
            fabAddDisease.setVisibility(View.INVISIBLE);
        } else {
            textViewAddDisease.setVisibility(View.INVISIBLE);
            fabAddDisease.setVisibility(View.VISIBLE);
        }

        // оповещаем LayoutManager, что произошли изменения
        // LayoutManager обновляет RecyclerView
        diseaseRecyclerViewAdapter.notifyDataSetChanged();

        // если флаг scrollToEnd выставлен в true, то прокручиваем RecyclerView вниз до конца,
        // чтоб увидеть новый вставленный элемент
        // и снова scrollToEnd выставляем в false
        if (mScrollToEnd  && myData.size() != 0) {
            recyclerDiseases.smoothScrollToPosition(myData.size() - 1);
            mScrollToEnd = false;
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        ArrayList<DiseaseItem> myData = diseaseRecyclerViewAdapter.getDiseaseList();
        myData.clear();
        diseaseRecyclerViewAdapter.notifyDataSetChanged();
    }
}
