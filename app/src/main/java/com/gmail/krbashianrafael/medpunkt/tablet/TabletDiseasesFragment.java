package com.gmail.krbashianrafael.medpunkt.tablet;


import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.transition.AutoTransition;
import android.support.transition.TransitionManager;
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
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.gmail.krbashianrafael.medpunkt.R;
import com.gmail.krbashianrafael.medpunkt.data.MedContract.DiseasesEntry;
import com.gmail.krbashianrafael.medpunkt.shared.DiseaseItem;
import com.gmail.krbashianrafael.medpunkt.shared.DiseaseRecyclerViewAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

@SuppressWarnings("deprecation")
@SuppressLint("RestrictedApi")
public class TabletDiseasesFragment extends Fragment
        implements android.support.v4.app.LoaderManager.LoaderCallbacks<Cursor> {

    private TabletMainActivity tabletMainActivity;

    private long _idUser = 0;

    TextView textViewAddDisease;

    public FloatingActionButton fabAddDisease;

    private Animation fabHideAnimation;
    Animation fabShowAnimation;
    Animation fadeInAnimation;

    public static boolean diseaseSelected = false;

    public RecyclerView recyclerDiseases;
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

        // этот фрейм вден только в телефонном режиме
        FrameLayout mAdViewFrame= view.findViewById(R.id.adViewFrame);
        mAdViewFrame.setVisibility(View.GONE);

        // шапка, которая видна только на планшете
        TextView txtTabletDiseases = view.findViewById(R.id.txt_diseases);
        txtTabletDiseases.setBackgroundColor(getResources().getColor(R.color.my_dark_gray));

        ImageView imgCancelTabletDiseases = view.findViewById(R.id.img_cancel_tablet_diseases);
        imgCancelTabletDiseases.setVisibility(View.VISIBLE);

        imgCancelTabletDiseases.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // скрываем МАЛЫЙ рекламный блок
                tabletMainActivity.tabletTreatmentFragment.adViewFrameTabletTreatmentFragment.setVisibility(View.GONE);
                TabletMainActivity.adIsShown = false;

                if (tabletMainActivity.tabletTreatmentFragment != null &&
                        tabletMainActivity.tabletTreatmentFragment.adViewInTabletTreatmentFragment != null) {
                    tabletMainActivity.tabletTreatmentFragment.adViewInTabletTreatmentFragment.pause();
                }

                clearDataFromDiseasesFragment();

                TabletMainActivity.selectedUser_id = 0;

                // сначала сробатывает Ripple эфект на imgCancelTabletDiseases
                // потом с задержкой в пол-секунды запускается код ниже
                tabletMainActivity.myTabletHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        tabletMainActivity.tabletUsersFragment.initUsersLoader();
                    }
                }, 200);
            }
        });


        textViewAddDisease = view.findViewById(R.id.txt_empty_diseases);
        textViewAddDisease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (tabletMainActivity == null) {
                    return;
                }

                // убираем МАЛЫЙ рекламный блок
                tabletMainActivity.tabletTreatmentFragment.adViewFrameTabletTreatmentFragment.setVisibility(View.GONE);
                if (tabletMainActivity.tabletTreatmentFragment.adViewInTabletTreatmentFragment != null) {
                    tabletMainActivity.tabletTreatmentFragment.adViewInTabletTreatmentFragment.pause();
                }

                tabletMainActivity.newDiseaseAndTreatment = true;
                tabletMainActivity.diseaseAndTreatmentInEdit = true;
                tabletMainActivity.tabletTreatmentFragment.editDisease = true;

                tabletMainActivity.tabletTreatmentFragment.treatmentDescriptionFragment.fabEditTreatmentDescripton.setVisibility(View.INVISIBLE);
                tabletMainActivity.tabletUsersFragment.fabAddUser.startAnimation(fabHideAnimation);
                tabletMainActivity.tabletTreatmentDeleteFrame.setVisibility(View.GONE);

                tabletMainActivity.myTabletHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        textViewAddDisease.setVisibility(View.INVISIBLE);
                        onAddDiseaseClicked();
                    }
                }, 300);
            }
        });

        fabAddDisease = view.findViewById(R.id.fabAddDisease);
        fabAddDisease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (tabletMainActivity == null) {
                    return;
                }

                // убираем МАЛЫЙ рекламный блок
                tabletMainActivity.tabletTreatmentFragment.adViewFrameTabletTreatmentFragment.setVisibility(View.GONE);
                if (tabletMainActivity.tabletTreatmentFragment.adViewInTabletTreatmentFragment != null) {
                    tabletMainActivity.tabletTreatmentFragment.adViewInTabletTreatmentFragment.pause();
                }

                tabletMainActivity.newDiseaseAndTreatment = true;
                tabletMainActivity.diseaseAndTreatmentInEdit = true;
                tabletMainActivity.tabletTreatmentFragment.editDisease = true;

                if (diseaseSelected) {
                    tabletMainActivity.tabletTreatmentFragment.treatmentDescriptionFragment.fabEditTreatmentDescripton.startAnimation(
                            tabletMainActivity.tabletTreatmentFragment.treatmentDescriptionFragment.fabHideAnimation);

                } else {
                    tabletMainActivity.tabletTreatmentFragment.treatmentDescriptionFragment.fabEditTreatmentDescripton.setVisibility(View.INVISIBLE);
                }

                fabAddDisease.startAnimation(fabHideAnimation);
                tabletMainActivity.tabletUsersFragment.fabAddUser.startAnimation(fabHideAnimation);
                tabletMainActivity.tabletTreatmentDeleteFrame.setVisibility(View.GONE);

                tabletMainActivity.myTabletHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        onAddDiseaseClicked();
                    }
                }, 300);
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
        tabletMainActivity.tempTextDiseaseName = Objects.requireNonNull(tabletMainActivity.tabletTreatmentFragment.editTextDiseaseName.getText()).toString();
        tabletMainActivity.tempTextDateOfTreatment = tabletMainActivity.tabletTreatmentFragment.editTextDateOfDisease.getText().toString();
        tabletMainActivity.tempTextTreatment = Objects.requireNonNull(tabletMainActivity.tabletTreatmentFragment.treatmentDescriptionFragment.editTextTreatment.getText()).toString();

        tabletMainActivity.tabletTreatmentFragment.zoomOutTabletTreatment.setVisibility(View.INVISIBLE);

        tabletMainActivity.tabletTreatmentFragment.set_idUser(_idUser);
        tabletMainActivity.tabletTreatmentFragment.editTextDiseaseName.setText("");
        tabletMainActivity.tabletTreatmentFragment.editTextDateOfDisease.setText(getString(R.string.disease_date));
        tabletMainActivity.tabletTreatmentFragment.treatmentDescriptionFragment.editTextTreatment.setText("");

        tabletMainActivity.tabletTreatmentFragment.tabLayout.setVisibility(View.GONE);
        tabletMainActivity.tabletTreatmentFragment.viewPager.setVisibility(View.VISIBLE);

        Objects.requireNonNull(tabletMainActivity.tabletTreatmentFragment.tabLayout.getTabAt(0)).select();

        tabletMainActivity.ver_3_Guideline.setGuidelinePercent(0.00f);

        tabletMainActivity.tabletUsersWideTitle.setText(tabletMainActivity.tabletDiseasesTitle.getText().toString());
        tabletMainActivity.tabletUsersWideTitle.setVisibility(View.VISIBLE);

        tabletMainActivity.LLtabletTreatmentCancelOrSave.setVisibility(View.VISIBLE);
        tabletMainActivity.tabletTreatmentFragment.textInputLayoutDiseaseName.setVisibility(View.VISIBLE);
        tabletMainActivity.tabletTreatmentFragment.editTextDateOfDisease.setVisibility(View.VISIBLE);

        tabletMainActivity.tabletTreatmentFragment.editTextDiseaseName.setEnabled(true);
        tabletMainActivity.tabletTreatmentFragment.editTextDiseaseName.requestFocus();

        tabletMainActivity.tabletTreatmentFragment.treatmentDescriptionFragment.editTextTreatment.setEnabled(true);
        tabletMainActivity.tabletTreatmentFragment.treatmentDescriptionFragment.editTextTreatment.setFocusable(true);
        tabletMainActivity.tabletTreatmentFragment.treatmentDescriptionFragment.editTextTreatment.setFocusableInTouchMode(true);
        tabletMainActivity.tabletTreatmentFragment.treatmentDescriptionFragment.editTextTreatment.setCursorVisible(true);

        InputMethodManager imm = (InputMethodManager) tabletMainActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.showSoftInput(tabletMainActivity.tabletTreatmentFragment.editTextDiseaseName, InputMethodManager.SHOW_IMPLICIT);
        }
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
                tabletMainActivity.tabletUsersFragment.fabAddUser.setVisibility(View.INVISIBLE);
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
    void clearDataFromDiseasesFragment() {
        ArrayList<DiseaseItem> myData = diseaseRecyclerViewAdapter.getDiseaseList();
        myData.clear();

        _idUser = 0;
        setTextUserName("");
    }

    public void set_idUser(long _idUser) {
        this._idUser = _idUser;
    }

    // сразу устанавливается имя пользователя в tabletDiseasesTitle
    public void setTextUserName(String textUserName) {
        tabletMainActivity.tabletDiseasesTitle.setText(textUserName);
    }

    public long get_idUser() {
        return _idUser;
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
        final ArrayList<DiseaseItem> myData = diseaseRecyclerViewAdapter.getDiseaseList();
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

        // делаем destroyLoader, чтоб он сам повторно не вызывался,
        // а вызывался при каждом входе в активити
        getLoaderManager().destroyLoader(TABLET_DISEASES_LOADER);

        // делаем сортировку заболеваний по именеи
        Collections.sort(myData);

        recyclerDiseases.setVisibility(View.VISIBLE);

        // оповещаем LayoutManager, что произошли изменения
        // LayoutManager обновляет RecyclerView
        diseaseRecyclerViewAdapter.notifyDataSetChanged();

        int myDataSize = myData.size();

        // код для показа выделенного заболевания
        if (myDataSize != 0) {
            tabletMainActivity.myTabletHandler.postDelayed(new Runnable() {
                @Override
                public void run() {

                    tabletMainActivity.selectedDisease_position = 0;

                    if (TabletMainActivity.selectedDisease_id != 0) {

                        for (int i = 0; i < myData.size(); i++) {
                            if (myData.get(i).get_diseaseId() == TabletMainActivity.selectedDisease_id) {
                                tabletMainActivity.selectedDisease_position = i;
                            }
                        }
                    }

                    recyclerDiseases.smoothScrollToPosition(tabletMainActivity.selectedDisease_position);
                }
            }, 500);
        }

        if (myDataSize == 0) {

            // если у пользователя нет заболеваний
            recyclerDiseases.setVisibility(View.INVISIBLE);

            tabletMainActivity.diseasesIsEmpty = true;

            diseaseSelected = false;

            float percentVerGuideline_2 = ((ConstraintLayout.LayoutParams) tabletMainActivity.ver_2_Left_Guideline.getLayoutParams()).guidePercent;

            if (percentVerGuideline_2 == 0.90f) {
                // если были видны только пользователи и нажимали на пользователя у которого нет заболеваний

                tabletMainActivity.myTabletHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        AutoTransition autoTransition1 = new AutoTransition();
                        autoTransition1.setDuration(240L);
                        TransitionManager.beginDelayedTransition(tabletMainActivity.mSceneRoot, autoTransition1);

                        tabletMainActivity.ver_1_Guideline.setGuidelinePercent(0.00f);
                        tabletMainActivity.ver_2_Left_Guideline.setGuidelinePercent(0.50f);
                        tabletMainActivity.ver_2_Right_Guideline.setGuidelinePercent(0.50f);
                        tabletMainActivity.ver_3_Guideline.setGuidelinePercent(1.00f);
                        tabletMainActivity.ver_4_Guideline.setGuidelinePercent(1.0f);
                    }
                }, 100);

            } else if (percentVerGuideline_2 == 0.30f) {
                // если были видны пользователи и у пользователя было одно заболевание, которе удалили
                // в итоге у пользователя нет заболеваний

                // передергиваем (сначала INVISIBLE, а по окончании анимации VISIBLE),
                // чтоб не было искажений в тексте после анимации
                tabletMainActivity.tabletTreatmentFragment.treatmentDescriptionFragment.
                        editTextTreatment.setVisibility(View.INVISIBLE);

                AutoTransition autoTransition2 = new AutoTransition();
                autoTransition2.setDuration(240L);
                TransitionManager.beginDelayedTransition(tabletMainActivity.mSceneRoot, autoTransition2);

                tabletMainActivity.ver_2_Left_Guideline.setGuidelinePercent(0.50f);
                tabletMainActivity.ver_2_Right_Guideline.setGuidelinePercent(0.50f);
                tabletMainActivity.ver_3_Guideline.setGuidelinePercent(1.00f);

                // передергиваем (сначала INVISIBLE, а по окончании анимации VISIBLE),
                // чтоб не было искажений в тексте после анимации
                tabletMainActivity.tabletTreatmentFragment.treatmentDescriptionFragment.
                        editTextTreatment.setVisibility(View.VISIBLE);
            }

            tabletMainActivity.tabletTreatmentFragment.set_idUser(0);

            // если нет пользователей, то чистим TreatmentFragment
            tabletMainActivity.tabletTreatmentTitle.setText("");

            tabletMainActivity.tabletTreatmentFragment.tabLayout.setVisibility(View.INVISIBLE);
            tabletMainActivity.tabletTreatmentFragment.viewPager.setVisibility(View.INVISIBLE);

            tabletMainActivity.tabletTreatmentFragment.treatmentDescriptionFragment.fabEditTreatmentDescripton.setVisibility(View.INVISIBLE);

            textViewAddDisease.setVisibility(View.VISIBLE);
            textViewAddDisease.startAnimation(fadeInAnimation);

        } else if (myDataSize == 1) {
            // если у пользователя одно заболевание

            tabletMainActivity.diseasesIsEmpty = false;

            tabletMainActivity.hideElementsOnTabletTreatmentFragment();

            fabAddDisease.startAnimation(fabShowAnimation);

            tabletMainActivity.tabletUsersWideTitle.setVisibility(View.GONE);
            tabletMainActivity.tabletUsersWideTitle.setText("");

            tabletMainActivity.tabletTreatmentFragment.tabLayout.setVisibility(View.VISIBLE);
            tabletMainActivity.tabletTreatmentFragment.viewPager.setVisibility(View.VISIBLE);

            diseaseSelected = true;

            // если одино заболевание, то сразу загружаем его леченин
            Long _diseaseId = myData.get(0).get_diseaseId();
            Long _diseaseUserId = myData.get(0).get_diseaseUserId();
            String diseaseName = myData.get(0).getDiseaseName();
            String diseaseDate = myData.get(0).getDiseaseDate();
            String treatmentText = myData.get(0).getTreatmentText();

            TabletMainActivity.selectedDisease_id = _diseaseId;

            tabletMainActivity.tabletTreatmentFragment.set_idDisease(_diseaseId);
            tabletMainActivity.tabletTreatmentFragment.set_idUser(_diseaseUserId);
            tabletMainActivity.tabletTreatmentFragment.setTextDiseaseName(diseaseName);
            tabletMainActivity.tabletTreatmentFragment.setTextDateOfDisease(diseaseDate);
            tabletMainActivity.tabletTreatmentFragment.setTextTreatment(treatmentText);

            // грузим снимки этого заболевания
            tabletMainActivity.tabletTreatmentFragment.treatmentPhotosFragment.initTreatmentPhotosLoader();

            tabletMainActivity.tabletTreatmentFragment.treatmentDescriptionFragment.
                    fabEditTreatmentDescripton.startAnimation(
                    tabletMainActivity.tabletTreatmentFragment.fabEditTreatmentDescriptonShowAnimation
            );

            float percentVerGuideline_2 = ((ConstraintLayout.LayoutParams) tabletMainActivity.ver_2_Left_Guideline.getLayoutParams()).guidePercent;

            if (percentVerGuideline_2 == 0.90f) {
                // если были открыты только пользователи
                // и было нажато на пользователя у которого только одно заболевание

                tabletMainActivity.tabletTreatmentFragment.zoomInTabletTreatment.setVisibility(View.INVISIBLE);
                tabletMainActivity.tabletTreatmentFragment.zoomOutTabletTreatment.setVisibility(View.VISIBLE);

                AutoTransition autoTransition3 = new AutoTransition();
                autoTransition3.setDuration(240L);
                TransitionManager.beginDelayedTransition(tabletMainActivity.mSceneRoot, autoTransition3);

                tabletMainActivity.ver_1_Guideline.setGuidelinePercent(0.00f);
                tabletMainActivity.ver_2_Left_Guideline.setGuidelinePercent(0.30f);
                tabletMainActivity.ver_2_Right_Guideline.setGuidelinePercent(0.30f);
                tabletMainActivity.ver_3_Guideline.setGuidelinePercent(0.60f);
                tabletMainActivity.ver_4_Guideline.setGuidelinePercent(1.00f);

            } else if (percentVerGuideline_2 == 0.50f) {
                // если был открыт пользователь, у которого нет заболеваний и
                // был нажат польлзователь, у которого одно заболевание

                tabletMainActivity.tabletTreatmentFragment.zoomInTabletTreatment.setVisibility(View.INVISIBLE);
                tabletMainActivity.tabletTreatmentFragment.zoomOutTabletTreatment.setVisibility(View.VISIBLE);

                TransitionManager.beginDelayedTransition(tabletMainActivity.mSceneRoot);

                tabletMainActivity.ver_2_Left_Guideline.setGuidelinePercent(0.30f);
                tabletMainActivity.ver_2_Right_Guideline.setGuidelinePercent(0.30f);
                tabletMainActivity.ver_3_Guideline.setGuidelinePercent(0.60f);
            }

        } else {
            // если у пользователя более одного заболевания

            tabletMainActivity.diseasesIsEmpty = false;

            if (TabletMainActivity.diseaseInserted) {
                // если было добавлено заболевание

                tabletMainActivity.tabletTreatmentFragment.tabLayout.setVisibility(View.VISIBLE);
                tabletMainActivity.tabletTreatmentFragment.viewPager.setVisibility(View.VISIBLE);

                tabletMainActivity.ver_1_Guideline.setGuidelinePercent(0.00f);
                tabletMainActivity.ver_2_Left_Guideline.setGuidelinePercent(0.30f);
                tabletMainActivity.ver_2_Right_Guideline.setGuidelinePercent(0.30f);
                tabletMainActivity.ver_4_Guideline.setGuidelinePercent(1.00f);

                tabletMainActivity.ver_3_Guideline.setGuidelinePercent(0.60f);
                tabletMainActivity.tabletUsersWideTitle.setVisibility(View.GONE);
                tabletMainActivity.tabletUsersWideTitle.setText("");

                tabletMainActivity.tabletTreatmentFragment.setTextDiseaseName(tabletMainActivity.tabletTreatmentFragment.textDiseaseName);

                TabletMainActivity.insertedDisease_id = 0;

            } else if (TabletMainActivity.diseaseUpdated) {
                // если заболевание было обновлено

                tabletMainActivity.tabletTreatmentFragment.treatmentDescriptionFragment.
                        fabEditTreatmentDescripton.startAnimation(
                        tabletMainActivity.tabletTreatmentFragment.fabEditTreatmentDescriptonShowAnimation
                );

            } else if (tabletMainActivity.tabletTreatmentFragment.get_idUser() != get_idUser()) {
                //если первый заход в tabletDiseasesFragment и в TreatmentFragment еще не отображаются данные,
                // т.е. tabletMainActivity.tabletTreatmentFragment.get_idUser() = 0
                // или был выбрарн другой пользователь у которого больше одного заболевания

                diseaseSelected = false;

                float percentVerGuideline_2 = ((ConstraintLayout.LayoutParams) tabletMainActivity.ver_2_Left_Guideline.getLayoutParams()).guidePercent;

                if (percentVerGuideline_2 == 0.90f) {
                    // если были видны только пользователи
                    // и был нажат пользователь у которого более одного заболевания

                    tabletMainActivity.myTabletHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            TransitionManager.beginDelayedTransition(tabletMainActivity.mSceneRoot);

                            tabletMainActivity.ver_1_Guideline.setGuidelinePercent(0.00f);
                            tabletMainActivity.ver_2_Left_Guideline.setGuidelinePercent(0.50f);
                            tabletMainActivity.ver_2_Right_Guideline.setGuidelinePercent(0.50f);

                            tabletMainActivity.ver_3_Guideline.setGuidelinePercent(1.00f);
                            tabletMainActivity.ver_4_Guideline.setGuidelinePercent(1.0f);
                        }
                    }, 100);

                    tabletMainActivity.tabletTreatmentFragment.set_idUser(0);

                    tabletMainActivity.tabletTreatmentTitle.setText("");

                    tabletMainActivity.tabletTreatmentFragment.tabLayout.setVisibility(View.INVISIBLE);
                    tabletMainActivity.tabletTreatmentFragment.viewPager.setVisibility(View.INVISIBLE);

                    tabletMainActivity.tabletTreatmentFragment.treatmentDescriptionFragment.
                            fabEditTreatmentDescripton.setVisibility(View.INVISIBLE);

                } else if (percentVerGuideline_2 == 0.30f) {
                    // если были видны пользователи и было выбрано у одного из пользовотелей заболевание с раскрытим описание лечения
                    // и был нажат пользователь у которого более одного заболевания

                    // передергиваем (сначала INVISIBLE, а по окончании анимации VISIBLE),
                    // чтоб не было искажений в тексте после анимации
                    tabletMainActivity.tabletTreatmentFragment.treatmentDescriptionFragment.
                            editTextTreatment.setVisibility(View.INVISIBLE);

                    TransitionManager.beginDelayedTransition(tabletMainActivity.mSceneRoot);

                    tabletMainActivity.ver_2_Left_Guideline.setGuidelinePercent(0.50f);
                    tabletMainActivity.ver_2_Right_Guideline.setGuidelinePercent(0.50f);
                    tabletMainActivity.ver_3_Guideline.setGuidelinePercent(1.00f);

                    // чтоб не было искажений в тексте после анимации
                    tabletMainActivity.tabletTreatmentFragment.treatmentDescriptionFragment.
                            editTextTreatment.setVisibility(View.VISIBLE);
                }
            }

            fabAddDisease.startAnimation(fabShowAnimation);
        }

        // после прохождения всех if выставляем флаги в false
        TabletMainActivity.diseaseInserted = false;
        TabletMainActivity.diseaseUpdated = false;

        tabletMainActivity.diseaseAndTreatmentInEdit = false;
        tabletMainActivity.newDiseaseAndTreatment = false;
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        ArrayList<DiseaseItem> myData = diseaseRecyclerViewAdapter.getDiseaseList();
        myData.clear();
        diseaseRecyclerViewAdapter.notifyDataSetChanged();
    }
}
