package com.gmail.krbashianrafael.medpunkt.tablet;


import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.gmail.krbashianrafael.medpunkt.DiseaseItem;
import com.gmail.krbashianrafael.medpunkt.R;
import com.gmail.krbashianrafael.medpunkt.data.MedContract.DiseasesEntry;
import com.gmail.krbashianrafael.medpunkt.phone.DiseaseRecyclerViewAdapter;
import com.gmail.krbashianrafael.medpunkt.phone.TreatmentActivity;

import java.util.ArrayList;
import java.util.Collections;

import static com.gmail.krbashianrafael.medpunkt.tablet.TabletMainActivity.TABLET_TREATMENT_FRAGMENT;

public class TabletDiseasesFragment extends Fragment
        implements android.support.v4.app.LoaderManager.LoaderCallbacks<Cursor> {

    private TabletMainActivity tabletMainActivity;

    // шапка, которая видна только на планшете
    public TextView txtTabletDiseases;

    private long _idUser = 0;

    private String textUserName = "";

    protected TextView textViewAddDisease;

    protected FloatingActionButton fabAddDisease;

    private Animation fabShowAnimation;
    private Animation fadeInAnimation;

    public static boolean mScrollToStart = false;

    private RecyclerView recyclerDiseases;
    public DiseaseRecyclerViewAdapter diseaseRecyclerViewAdapter;

    private static final int TABLET_DISEASES_LOADER = 1001;


    public TabletDiseasesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_tablet_diseases, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        txtTabletDiseases = view.findViewById(R.id.txt_diseases);

        textViewAddDisease = view.findViewById(R.id.txt_empty_diseases);
        textViewAddDisease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent treatmentIntent = new Intent(tabletMainActivity, TreatmentActivity.class);
                treatmentIntent.putExtra("_idUser", _idUser);
                treatmentIntent.putExtra("newDisease", true);
                treatmentIntent.putExtra("editDisease", true);
                treatmentIntent.putExtra("diseaseName", "");
                treatmentIntent.putExtra("textTreatment", "");
                startActivity(treatmentIntent);
            }
        });

        fabAddDisease = view.findViewById(R.id.fabAddDisease);
        fabAddDisease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent treatmentIntent = new Intent(tabletMainActivity, TreatmentActivity.class);
                treatmentIntent.putExtra("_idUser", _idUser);
                treatmentIntent.putExtra("newDisease", true);
                treatmentIntent.putExtra("editDisease", true);
                treatmentIntent.putExtra("diseaseName", "");
                treatmentIntent.putExtra("textTreatment", "");
                startActivity(treatmentIntent);
            }
        });

        // инициализируем recyclerDiseases
        recyclerDiseases = view.findViewById(R.id.recycler_diseases);

        // при нажатии на "Заболевания" список заболеваний прокручивется вверх
        txtTabletDiseases.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recyclerDiseases.smoothScrollToPosition(0);
            }
        });
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        tabletMainActivity = (TabletMainActivity) getActivity();

        fadeInAnimation = AnimationUtils.loadAnimation(tabletMainActivity, R.anim.fadein);

        fabShowAnimation = AnimationUtils.loadAnimation(tabletMainActivity, R.anim.fab_show);
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

        // инициализируем linearLayoutManager
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(tabletMainActivity,
                LinearLayoutManager.VERTICAL, false);


        // устанавливаем LayoutManager для RecyclerView
        recyclerDiseases.setLayoutManager(linearLayoutManager);

        // инициализируем DiseaseRecyclerViewAdapter
        diseaseRecyclerViewAdapter = new DiseaseRecyclerViewAdapter(tabletMainActivity);

        // устанавливаем адаптер для RecyclerView
        recyclerDiseases.setAdapter(diseaseRecyclerViewAdapter);
    }

    public void initDiseasesLoader() {

        // сразу INVISIBLE делаем чтоб не было скачков при смене вида
        textViewAddDisease.setVisibility(View.INVISIBLE);
        fabAddDisease.setVisibility(View.INVISIBLE);

        // Инициализируем Loader
        getLoaderManager().initLoader(TABLET_DISEASES_LOADER, null, this);
    }

    // метод для очистки данных из DiseasesFragment
    protected void clearDataFromDiseasesFragment() {
        ArrayList<DiseaseItem> myData = diseaseRecyclerViewAdapter.getDiseaseList();
        myData.clear();

        _idUser = 0;
        setTextUserName("");

        //diseaseRecyclerViewAdapter.notifyDataSetChanged();
    }

    public void set_idUser(long _idUser) {
        this._idUser = _idUser;
    }

    // сразу устанавливается имя пользователя в tabletDiseasesTitle
    public void setTextUserName(String textUserName) {
        this.textUserName = textUserName;
        tabletMainActivity.tabletDiseasesTitle.setText(this.textUserName);
    }

    public long get_idUser() {
        return _idUser;
    }

    public String getTextUserName() {
        return textUserName;
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        String[] projection = {
                DiseasesEntry.DIS_ID,
                DiseasesEntry.COLUMN_U_ID,
                DiseasesEntry.COLUMN_DISEASE_NAME,
                DiseasesEntry.COLUMN_DISEASE_DATE,
                DiseasesEntry.COLUMN_DISEASE_TREATMENT};

        // выборку заболеванй делаем по _idUser
        String selection = DiseasesEntry.COLUMN_U_ID + "=?";
        String[] selectionArgs = new String[]{String.valueOf(_idUser)};

        return new CursorLoader(tabletMainActivity,   // Parent activity context
                DiseasesEntry.CONTENT_DISEASES_URI,   // Provider content URI to query = content://com.gmail.krbashianrafael.medpunkt/diseases/
                projection,             // Columns to include in the resulting Cursor
                selection,                   // selection by DiseasesEntry.COLUMN_U_ID
                selectionArgs,                   // selection arguments by _idUser
                null);                  // Default sort order
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor cursor) {
        ArrayList<DiseaseItem> myData = diseaseRecyclerViewAdapter.getDiseaseList();
        myData.clear();

        Log.d("yyy", "TdiseaseOnLoadFinished");


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

        // делаем сортировку заболеваний по именеи
        Collections.sort(myData);

        // оповещаем LayoutManager, что произошли изменения
        // LayoutManager обновляет RecyclerView
        diseaseRecyclerViewAdapter.notifyDataSetChanged();

        // делаем destroyLoader, чтоб он сам повторно не вызывался,
        // а вызывался при каждом входе в активити
        getLoaderManager().destroyLoader(TABLET_DISEASES_LOADER);

        int myDataSize = myData.size();

        // если нет заболеваний, то делаем textViewAddDisease.setVisibility(View.VISIBLE);
        // и fabAddDisease.setVisibility(View.INVISIBLE);
        if (myDataSize == 0) {

            Log.d("yyy","Tdisease myDataSize == 0");

            if (_idUser != 0) {
                new Handler(Looper.getMainLooper()).
                        postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                textViewAddDisease.setVisibility(View.VISIBLE);
                                textViewAddDisease.startAnimation(fadeInAnimation);
                            }
                        }, 300);
            }

            txtTabletDiseases.setText(R.string.diseases_what_text);
            txtTabletDiseases.setBackgroundColor(getResources().getColor(R.color.colorPrimary));

            tabletMainActivity.blur(TABLET_TREATMENT_FRAGMENT);
            tabletMainActivity.tabletTreatmentFragment.set_idUser(0);

            // если нет пользователей, то чистим TreatmentFragment
            tabletMainActivity.tabletTreatmentTitle.setBackgroundColor(getResources().getColor(R.color.colorPrimaryLight));
            tabletMainActivity.tabletTreatmentTitle.setText("");

            tabletMainActivity.tabletTreatmentFragment.tabLayout.setVisibility(View.INVISIBLE);
            tabletMainActivity.tabletTreatmentFragment.viewPager.setVisibility(View.INVISIBLE);

            Log.d("xxx", "tabletMainActivity = " + tabletMainActivity);
            Log.d("xxx", "tabletMainActivity.tabletTreatmentFragment = " + tabletMainActivity.tabletTreatmentFragment);
            Log.d("xxx", "tabletMainActivity.tabletTreatmentFragment.treatmentDescriptionFragment = " + tabletMainActivity.tabletTreatmentFragment.treatmentDescriptionFragment);
            Log.d("xxx", "tabletMainActivity.tabletTreatmentFragment.treatmentDescriptionFragment.fabEditTreatmentDescripton = " +
                    tabletMainActivity.tabletTreatmentFragment.treatmentDescriptionFragment.fabEditTreatmentDescripton);

            tabletMainActivity.tabletTreatmentFragment.treatmentDescriptionFragment.fabEditTreatmentDescripton.setVisibility(View.INVISIBLE);


        } else if (myDataSize == 1) {

            tabletMainActivity.unBlur(TABLET_TREATMENT_FRAGMENT);

            fabAddDisease.startAnimation(fabShowAnimation);

            tabletMainActivity.tabletTreatmentFragment.tabLayout.setVisibility(View.VISIBLE);
            tabletMainActivity.tabletTreatmentFragment.viewPager.setVisibility(View.VISIBLE);

            txtTabletDiseases.setBackgroundColor(getResources().getColor(R.color.colorPrimary));

            txtTabletDiseases.setText(R.string.diseases_what_text);

            // если одино заболевание, то сразу загружаем его леченин
            Long _diseaseId = myData.get(0).get_diseaseId();
            Long _diseaseUserId = myData.get(0).get_diseaseUserId();
            String diseaseName = myData.get(0).getDiseaseName();
            String diseaseDate = myData.get(0).getDiseaseDate();
            String treatmentText = myData.get(0).getTreatmentText();

            tabletMainActivity.tabletTreatmentTitle.setBackgroundColor(getResources().getColor(R.color.light_green));
            tabletMainActivity.tabletTreatmentFragment.set_idDisease(_diseaseId);
            tabletMainActivity.tabletTreatmentFragment.set_idUser(_diseaseUserId);
            tabletMainActivity.tabletTreatmentFragment.setTextDiseaseName(diseaseName);
            tabletMainActivity.tabletTreatmentFragment.setTextDateOfDisease(diseaseDate);
            tabletMainActivity.tabletTreatmentFragment.setTextTreatment(treatmentText);

            tabletMainActivity.tabletTreatmentFragment.treatmentDescriptionFragment.
                    fabEditTreatmentDescripton.startAnimation(
                    tabletMainActivity.tabletTreatmentFragment.fabShowAnimation
            );

        } else {

            if (tabletMainActivity.tabletTreatmentFragment.get_idUser() != get_idUser()) {
                //если первый заход и в TreatmentFragment еще не отображаются данные,
                // т.е. tabletMainActivity.tabletTreatmentFragment.get_idUser() = 0
                // или был выбрарн другой пользователь у которого больше одного заболевания
                // то предлагаем сдеалть выбор заболевани для отображения в TreatmentFragment

                txtTabletDiseases.setText(R.string.tablet_treatment_select_disease);

                txtTabletDiseases.setBackgroundColor(getResources().getColor(R.color.colorFab));

                tabletMainActivity.blur(TABLET_TREATMENT_FRAGMENT);
                tabletMainActivity.tabletTreatmentFragment.set_idUser(0);

                tabletMainActivity.tabletTreatmentTitle.setBackgroundColor(getResources().getColor(R.color.colorPrimaryLight));
                tabletMainActivity.tabletTreatmentTitle.setText("");

                tabletMainActivity.tabletTreatmentFragment.tabLayout.setVisibility(View.INVISIBLE);
                tabletMainActivity.tabletTreatmentFragment.viewPager.setVisibility(View.INVISIBLE);

                tabletMainActivity.tabletTreatmentFragment.treatmentDescriptionFragment.
                        fabEditTreatmentDescripton.setVisibility(View.INVISIBLE);

            } else if (TabletMainActivity.diseaseUpdated &&
                    TabletMainActivity.disease_IdInEdit ==
                            tabletMainActivity.tabletTreatmentFragment.get_idDisease()) {
                // если заболевание, которое было в TreatmentFragment обновилось (поменялось название...)
                // то устанавливаем обновленные поля

                tabletMainActivity.tabletTreatmentTitle.setText(TabletMainActivity.diseaseNameAfterUpdate);
                tabletMainActivity.tabletTreatmentFragment.setTextDiseaseName(TabletMainActivity.diseaseNameAfterUpdate);
                tabletMainActivity.tabletTreatmentFragment.setTextDateOfDisease(TabletMainActivity.diseaseDateAfterUpdate);
                tabletMainActivity.tabletTreatmentFragment.setTextTreatment(TabletMainActivity.diseaseTreatmentAfterUpdate);

            } else if (TabletMainActivity.diseaseDeleted &&
                    TabletMainActivity.disease_IdInEdit ==
                            tabletMainActivity.tabletTreatmentFragment.get_idDisease()) {
                // если заболевание, которое было в TreatmentFragment удалилиось
                // то очищаем TreatmentFragment и предлагаем сдеалть выбор заболевани для отображения в TreatmentFragment

                Log.d("yyy","TdiseaseDeleted");


                txtTabletDiseases.setText(R.string.tablet_treatment_select_disease);

                txtTabletDiseases.setBackgroundColor(getResources().getColor(R.color.colorFab));

                tabletMainActivity.blur(TABLET_TREATMENT_FRAGMENT);
                tabletMainActivity.tabletTreatmentFragment.set_idUser(0);

                tabletMainActivity.tabletTreatmentTitle.setBackgroundColor(getResources().getColor(R.color.colorPrimaryLight));
                tabletMainActivity.tabletTreatmentTitle.setText("");

                tabletMainActivity.tabletTreatmentFragment.tabLayout.setVisibility(View.INVISIBLE);
                tabletMainActivity.tabletTreatmentFragment.viewPager.setVisibility(View.INVISIBLE);

                tabletMainActivity.tabletTreatmentFragment.treatmentDescriptionFragment.
                        fabEditTreatmentDescripton.setVisibility(View.INVISIBLE);
            }

            fabAddDisease.startAnimation(fabShowAnimation);

        }

        // после прохождения всех if выставляем флаги в false
        TabletMainActivity.diseaseInserted = false;
        TabletMainActivity.diseaseUpdated = false;
        TabletMainActivity.diseaseDeleted = false;

        if (mScrollToStart && myData.size() != 0) {
            recyclerDiseases.smoothScrollToPosition(0);
            mScrollToStart = false;
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        ArrayList<DiseaseItem> myData = diseaseRecyclerViewAdapter.getDiseaseList();
        myData.clear();
        diseaseRecyclerViewAdapter.notifyDataSetChanged();
    }
}
