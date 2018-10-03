package com.gmail.krbashianrafael.medpunkt.tablet;


import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.gmail.krbashianrafael.medpunkt.DiseaseItem;
import com.gmail.krbashianrafael.medpunkt.R;
import com.gmail.krbashianrafael.medpunkt.data.MedContract.DiseasesEntry;
import com.gmail.krbashianrafael.medpunkt.phone.DiseaseRecyclerViewAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

import static com.gmail.krbashianrafael.medpunkt.tablet.TabletMainActivity.TABLET_TREATMENT_FRAGMENT;
import static com.gmail.krbashianrafael.medpunkt.tablet.TabletMainActivity.tempTextDateOfTreatment;
import static com.gmail.krbashianrafael.medpunkt.tablet.TabletMainActivity.tempTextDiseaseName;
import static com.gmail.krbashianrafael.medpunkt.tablet.TabletMainActivity.tempTextTreatment;

public class TabletDiseasesFragment extends Fragment
        implements android.support.v4.app.LoaderManager.LoaderCallbacks<Cursor> {

    private TabletMainActivity tabletMainActivity;

    // шапка, которая видна только на планшете
    public TextView txtTabletDiseases;

    private long _idUser = 0;

    private String textUserName = "";

    protected TextView textViewAddDisease;

    public FloatingActionButton fabAddDisease;

    private Animation fabHideAnimation;
    public Animation fabShowAnimation;
    public Animation fadeInAnimation;

    public static boolean mScrollToStart = false;
    public static boolean diseaseSelected = false;

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
        txtTabletDiseases.setBackgroundColor(getResources().getColor(R.color.my_dark_gray));
        txtTabletDiseases.setTextColor(getResources().getColor(R.color.white));


        textViewAddDisease = view.findViewById(R.id.txt_empty_diseases);
        textViewAddDisease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                textViewAddDisease.setVisibility(View.INVISIBLE);

                onAddDiseaseClicked();

                /*Intent treatmentIntent = new Intent(tabletMainActivity, TreatmentActivity.class);
                treatmentIntent.putExtra("_idUser", _idUser);
                treatmentIntent.putExtra("newDisease", true);
                treatmentIntent.putExtra("editDisease", true);
                treatmentIntent.putExtra("diseaseName", "");
                treatmentIntent.putExtra("textTreatment", "");
                startActivity(treatmentIntent);*/
            }
        });

        fabAddDisease = view.findViewById(R.id.fabAddDisease);
        fabAddDisease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                fabAddDisease.startAnimation(fabHideAnimation);

                onAddDiseaseClicked();

                /*Intent treatmentIntent = new Intent(tabletMainActivity, TreatmentActivity.class);
                treatmentIntent.putExtra("_idUser", _idUser);
                treatmentIntent.putExtra("newDisease", true);
                treatmentIntent.putExtra("editDisease", true);
                treatmentIntent.putExtra("diseaseName", "");
                treatmentIntent.putExtra("textTreatment", "");
                startActivity(treatmentIntent);*/
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

    private void onAddDiseaseClicked() {
        tabletMainActivity.unBlur(TabletMainActivity.TABLET_TREATMENT_FRAGMENT);

        if (!TabletMainActivity.diseaseAndTreatmentInEdit) {
            tabletMainActivity.tabletTreatmentFragment.treatmentDescriptionFragment.fabEditTreatmentDescripton.startAnimation(
                    tabletMainActivity.tabletTreatmentFragment.treatmentDescriptionFragment.fabHideAnimation);
        }

        TabletMainActivity.newDiseaseAndTreatment = true;
        TabletMainActivity.diseaseAndTreatmentInEdit = true;

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                tabletMainActivity.tabletTreatmentFragment.textInputLayoutDiseaseName.setVisibility(View.VISIBLE);
                tabletMainActivity.tabletTreatmentFragment.viewPager.setVisibility(View.VISIBLE);
                Objects.requireNonNull(tabletMainActivity.tabletTreatmentFragment.tabLayout.getTabAt(0)).select();

                tabletMainActivity.tabletTreatmentFragment.tabLayout.setVisibility(View.GONE);

                // на планшете показываем клавиатуру
                InputMethodManager imm = (InputMethodManager)
                        tabletMainActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
                }

                tempTextDiseaseName = tabletMainActivity.tabletTreatmentFragment.editTextDiseaseName.getText().toString();
                tempTextDateOfTreatment = tabletMainActivity.tabletTreatmentFragment.editTextDateOfDisease.getText().toString();
                tempTextTreatment = tabletMainActivity.tabletTreatmentFragment.treatmentDescriptionFragment.editTextTreatment.getText().toString();

                tabletMainActivity.tabletTreatmentFragment.set_idUser(_idUser);
                tabletMainActivity.tabletTreatmentFragment.editTextDiseaseName.setText("");
                tabletMainActivity.tabletTreatmentFragment.editTextDateOfDisease.setText(getString(R.string.disease_date));
                tabletMainActivity.tabletTreatmentFragment.treatmentDescriptionFragment.editTextTreatment.setText("");

                tabletMainActivity.tabletTreatmentFragment.editDisease = true;
                tabletMainActivity.tabletTreatmentFragment.editTextDiseaseName.setEnabled(true);
                tabletMainActivity.tabletTreatmentFragment.editTextDiseaseName.requestFocus();

                //tabletMainActivity.tabletTreatmentFragment.treatmentDescriptionFragment.editTextTreatment.setEnabled(true);
                tabletMainActivity.tabletTreatmentFragment.treatmentDescriptionFragment.editTextTreatment.setFocusable(true);
                tabletMainActivity.tabletTreatmentFragment.treatmentDescriptionFragment.editTextTreatment.setFocusableInTouchMode(true);
                tabletMainActivity.tabletTreatmentFragment.treatmentDescriptionFragment.editTextTreatment.setCursorVisible(true);

                tabletMainActivity.tabletTreatmentFragment.editTextDateOfDisease.setVisibility(View.VISIBLE);
                //mTabletMainActivity.tabletTreatmentTitle.setVisibility(View.INVISIBLE);
                tabletMainActivity.LLtabletTreatmentCancelOrSave.setVisibility(View.VISIBLE);
                tabletMainActivity.tabletTreatmentDelete.setVisibility(View.GONE);
            }
        }, 600);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        tabletMainActivity = (TabletMainActivity) getActivity();

        fadeInAnimation = AnimationUtils.loadAnimation(tabletMainActivity, R.anim.fadein);

        fabHideAnimation = AnimationUtils.loadAnimation(tabletMainActivity, R.anim.fab_hide);
        fabHideAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                fabAddDisease.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });

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

    public void initDiseasesLoader() {

        // сразу INVISIBLE делаем чтоб не было скачков при смене вида
        textViewAddDisease.setVisibility(View.INVISIBLE);
        fabAddDisease.setVisibility(View.INVISIBLE);

        // Инициализируем Loader
        getLoaderManager().initLoader(TABLET_DISEASES_LOADER, null, this);
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

            TabletMainActivity.diseasesIsEmpty = true;

            /*txtTabletDiseases.setText(R.string.diseases_what_text);
            txtTabletDiseases.setBackgroundColor(getResources().getColor(R.color.colorPrimary));*/

            diseaseSelected = true;

            tabletMainActivity.blur(TABLET_TREATMENT_FRAGMENT);
            tabletMainActivity.tabletTreatmentFragment.set_idUser(0);

            // если нет пользователей, то чистим TreatmentFragment
            tabletMainActivity.tabletTreatmentTitle.setText("");

            tabletMainActivity.tabletTreatmentFragment.tabLayout.setVisibility(View.INVISIBLE);
            tabletMainActivity.tabletTreatmentFragment.viewPager.setVisibility(View.INVISIBLE);

            tabletMainActivity.tabletTreatmentFragment.treatmentDescriptionFragment.fabEditTreatmentDescripton.setVisibility(View.INVISIBLE);

            if (_idUser != 0) {
                /*new Handler(Looper.getMainLooper()).
                        postDelayed(new Runnable() {
                            @Override
                            public void run() {*/
                if (TabletMainActivity.diseaseAndTreatmentInEdit) {
                    tabletMainActivity.tabletTreatmentCancel.performClick();

                } else {
                    textViewAddDisease.setVisibility(View.VISIBLE);
                    textViewAddDisease.startAnimation(fadeInAnimation);
                }

                            /*}
                        }, 300);*/
            }

        } else if (myDataSize == 1) {

            TabletMainActivity.diseasesIsEmpty = false;

            //if (TabletMainActivity.diseaseAndTreatmentInEdit) {
            tabletMainActivity.hideElementsOnTabletTreatmentFragment();
            //}

            tabletMainActivity.unBlur(TABLET_TREATMENT_FRAGMENT);

            fabAddDisease.startAnimation(fabShowAnimation);

            tabletMainActivity.tabletTreatmentFragment.tabLayout.setVisibility(View.VISIBLE);
            tabletMainActivity.tabletTreatmentFragment.viewPager.setVisibility(View.VISIBLE);

            //txtTabletDiseases.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            //txtTabletDiseases.setText(R.string.diseases_what_text);

            diseaseSelected = true;

            // если одино заболевание, то сразу загружаем его леченин
            Long _diseaseId = myData.get(0).get_diseaseId();
            Long _diseaseUserId = myData.get(0).get_diseaseUserId();
            String diseaseName = myData.get(0).getDiseaseName();
            String diseaseDate = myData.get(0).getDiseaseDate();
            String treatmentText = myData.get(0).getTreatmentText();

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

            TabletMainActivity.diseasesIsEmpty = false;

            if (tabletMainActivity.tabletTreatmentFragment.get_idUser() != get_idUser()) {

                //если первый заход и в TreatmentFragment еще не отображаются данные,
                // т.е. tabletMainActivity.tabletTreatmentFragment.get_idUser() = 0
                // или был выбрарн другой пользователь у которого больше одного заболевания
                // то предлагаем сдеалть выбор заболевани для отображения в TreatmentFragment

                /*txtTabletDiseases.setText(R.string.tablet_treatment_select_disease);

                txtTabletDiseases.setBackgroundColor(getResources().getColor(R.color.colorFab));*/

                diseaseSelected = false;

                tabletMainActivity.blur(TABLET_TREATMENT_FRAGMENT);
                tabletMainActivity.tabletTreatmentFragment.set_idUser(0);

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

                if (!diseaseSelected){
                    tabletMainActivity.tabletTreatmentTitle.setText(TabletMainActivity.diseaseNameAfterUpdate);
                    tabletMainActivity.tabletTreatmentFragment.setTextDiseaseName(TabletMainActivity.diseaseNameAfterUpdate);
                    tabletMainActivity.tabletTreatmentFragment.setTextDateOfDisease(TabletMainActivity.diseaseDateAfterUpdate);
                    tabletMainActivity.tabletTreatmentFragment.setTextTreatment(TabletMainActivity.diseaseTreatmentAfterUpdate);
                }

                //tabletMainActivity.hideElementsOnTabletTreatmentFragment();

            } else if (TabletMainActivity.diseaseInserted){
                tabletMainActivity.tabletTreatmentFragment.setTextDiseaseName(TabletMainActivity.tempTextDiseaseName);
                tabletMainActivity.tabletTreatmentFragment.setTextDateOfDisease(TabletMainActivity.tempTextDateOfTreatment);
                tabletMainActivity.tabletTreatmentFragment.setTextTreatment(TabletMainActivity.tempTextTreatment);
            }

            /*else if (TabletMainActivity.diseaseDeleted &&
                    TabletMainActivity.disease_IdInEdit ==
                            tabletMainActivity.tabletTreatmentFragment.get_idDisease()) {
                // если заболевание, которое было в TreatmentFragment удалилиось
                // то очищаем TreatmentFragment и предлагаем сдеалть выбор заболевани для отображения в TreatmentFragment

                *//*txtTabletDiseases.setText(R.string.tablet_treatment_select_disease);
                txtTabletDiseases.setBackgroundColor(getResources().getColor(R.color.colorFab));*//*

                //diseaseSelected = false;

                //tabletMainActivity.blur(TABLET_TREATMENT_FRAGMENT);
                *//*tabletMainActivity.tabletTreatmentFragment.tabLayout.setVisibility(View.INVISIBLE);
                tabletMainActivity.tabletTreatmentFragment.viewPager.setVisibility(View.INVISIBLE);
                tabletMainActivity.tabletTreatmentFragment.treatmentDescriptionFragment.
                        fabEditTreatmentDescripton.setVisibility(View.INVISIBLE);


                tabletMainActivity.tabletTreatmentFragment.set_idUser(0);
                tabletMainActivity.tabletTreatmentTitle.setText("");*//*
            }*/


            fabAddDisease.startAnimation(fabShowAnimation);

        }

        /*if (TabletMainActivity.diseaseAndTreatmentInEdit) {
            tabletMainActivity.tabletTreatmentCancel.performClick();
        }*/

        // после прохождения всех if выставляем флаги в false
        TabletMainActivity.diseaseInserted = false;
        TabletMainActivity.diseaseUpdated = false;
        TabletMainActivity.diseaseDeleted = false;

        TabletMainActivity.diseaseAndTreatmentInEdit = false;


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
