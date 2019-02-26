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
import android.view.animation.AccelerateDecelerateInterpolator;
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

    private AutoTransition autoTransition;

    public TabletDiseasesFragment() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_tablet_diseases, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        autoTransition = new AutoTransition();
        autoTransition.setDuration(280L);
        autoTransition.setInterpolator(new AccelerateDecelerateInterpolator());

        FrameLayout mAdViewFrame = view.findViewById(R.id.adViewFrame);
        mAdViewFrame.setVisibility(View.GONE);

        TextView txtTabletDiseases = view.findViewById(R.id.txt_diseases);
        txtTabletDiseases.setBackgroundColor(getResources().getColor(R.color.my_dark_gray));

        ImageView imgCancelTabletDiseases = view.findViewById(R.id.img_cancel_tablet_diseases);
        imgCancelTabletDiseases.setVisibility(View.VISIBLE);

        imgCancelTabletDiseases.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tabletMainActivity.tabletTreatmentFragment.adViewFrameTabletTreatmentFragment.setVisibility(View.GONE);
                TabletMainActivity.adIsShown = false;

                if (tabletMainActivity.tabletTreatmentFragment != null &&
                        tabletMainActivity.tabletTreatmentFragment.adViewInTabletTreatmentFragment != null) {
                    tabletMainActivity.tabletTreatmentFragment.adViewInTabletTreatmentFragment.pause();
                }

                clearDataFromDiseasesFragment();

                TabletMainActivity.selectedUser_id = 0;

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

        recyclerDiseases = view.findViewById(R.id.recycler_diseases);

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

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(tabletMainActivity,
                LinearLayoutManager.VERTICAL, false);


        recyclerDiseases.setLayoutManager(linearLayoutManager);

        diseaseRecyclerViewAdapter = new DiseaseRecyclerViewAdapter(tabletMainActivity);

        recyclerDiseases.setAdapter(diseaseRecyclerViewAdapter);
    }

    void clearDataFromDiseasesFragment() {
        ArrayList<DiseaseItem> myData = diseaseRecyclerViewAdapter.getDiseaseList();
        myData.clear();

        _idUser = 0;
        setTextUserName("");
    }

    public void set_idUser(long _idUser) {
        this._idUser = _idUser;
    }

    public void setTextUserName(String textUserName) {
        tabletMainActivity.tabletDiseasesTitle.setText(textUserName);
    }

    public long get_idUser() {
        return _idUser;
    }

    public void initDiseasesLoader() {

        textViewAddDisease.setVisibility(View.INVISIBLE);
        fabAddDisease.setVisibility(View.INVISIBLE);

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

        String selection = DiseasesEntry.COLUMN_U_ID + "=?";
        String[] selectionArgs = new String[]{String.valueOf(_idUser)};

        return new CursorLoader(tabletMainActivity,
                DiseasesEntry.CONTENT_DISEASES_URI,
                projection,
                selection,
                selectionArgs,
                null);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor cursor) {
        final ArrayList<DiseaseItem> myData = diseaseRecyclerViewAdapter.getDiseaseList();
        myData.clear();

        if (cursor != null) {
            cursor.moveToPosition(-1);

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

        getLoaderManager().destroyLoader(TABLET_DISEASES_LOADER);

        Collections.sort(myData);

        recyclerDiseases.setVisibility(View.VISIBLE);

        diseaseRecyclerViewAdapter.notifyDataSetChanged();

        int myDataSize = myData.size();

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

            recyclerDiseases.setVisibility(View.INVISIBLE);

            tabletMainActivity.diseasesIsEmpty = true;

            diseaseSelected = false;

            float percentVerGuideline_2 = ((ConstraintLayout.LayoutParams) tabletMainActivity.ver_2_Left_Guideline.getLayoutParams()).guidePercent;

            if (percentVerGuideline_2 == 0.90f) {

                tabletMainActivity.myTabletHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        TransitionManager.beginDelayedTransition(tabletMainActivity.mSceneRoot, autoTransition);
                        tabletMainActivity.ver_1_Guideline.setGuidelinePercent(0.00f);
                        tabletMainActivity.ver_2_Left_Guideline.setGuidelinePercent(0.50f);
                        tabletMainActivity.ver_2_Right_Guideline.setGuidelinePercent(0.50f);
                        tabletMainActivity.ver_3_Guideline.setGuidelinePercent(1.00f);
                        tabletMainActivity.ver_4_Guideline.setGuidelinePercent(1.0f);
                    }
                }, 100);

            } else if (percentVerGuideline_2 == 0.30f) {
                tabletMainActivity.tabletTreatmentFragment.treatmentDescriptionFragment.
                        editTextTreatment.setVisibility(View.INVISIBLE);

                TransitionManager.beginDelayedTransition(tabletMainActivity.mSceneRoot, autoTransition);
                tabletMainActivity.ver_2_Left_Guideline.setGuidelinePercent(0.50f);
                tabletMainActivity.ver_2_Right_Guideline.setGuidelinePercent(0.50f);
                tabletMainActivity.ver_3_Guideline.setGuidelinePercent(1.00f);

                tabletMainActivity.tabletTreatmentFragment.treatmentDescriptionFragment.
                        editTextTreatment.setVisibility(View.VISIBLE);
            }

            tabletMainActivity.tabletTreatmentFragment.set_idUser(0);

            tabletMainActivity.tabletTreatmentTitle.setText("");

            tabletMainActivity.tabletTreatmentFragment.tabLayout.setVisibility(View.INVISIBLE);
            tabletMainActivity.tabletTreatmentFragment.viewPager.setVisibility(View.INVISIBLE);

            tabletMainActivity.tabletTreatmentFragment.treatmentDescriptionFragment.fabEditTreatmentDescripton.setVisibility(View.INVISIBLE);

            textViewAddDisease.setVisibility(View.VISIBLE);
            textViewAddDisease.startAnimation(fadeInAnimation);

        } else {
            tabletMainActivity.diseasesIsEmpty = false;

            if (TabletMainActivity.diseaseInserted) {
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
                tabletMainActivity.tabletTreatmentFragment.treatmentDescriptionFragment.
                        fabEditTreatmentDescripton.startAnimation(
                        tabletMainActivity.tabletTreatmentFragment.fabEditTreatmentDescriptonShowAnimation
                );

            } else if (tabletMainActivity.tabletTreatmentFragment.get_idUser() != get_idUser()) {

                diseaseSelected = false;

                float percentVerGuideline_2 = ((ConstraintLayout.LayoutParams) tabletMainActivity.ver_2_Left_Guideline.getLayoutParams()).guidePercent;

                if (percentVerGuideline_2 == 0.90f) {

                    tabletMainActivity.myTabletHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {

                            TransitionManager.beginDelayedTransition(tabletMainActivity.mSceneRoot, autoTransition);
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
                    tabletMainActivity.tabletTreatmentFragment.treatmentDescriptionFragment.
                            editTextTreatment.setVisibility(View.INVISIBLE);

                    TransitionManager.beginDelayedTransition(tabletMainActivity.mSceneRoot, autoTransition);

                    tabletMainActivity.ver_2_Left_Guideline.setGuidelinePercent(0.50f);
                    tabletMainActivity.ver_2_Right_Guideline.setGuidelinePercent(0.50f);
                    tabletMainActivity.ver_3_Guideline.setGuidelinePercent(1.00f);

                    tabletMainActivity.tabletTreatmentFragment.treatmentDescriptionFragment.
                            editTextTreatment.setVisibility(View.VISIBLE);
                }
            }

            fabAddDisease.startAnimation(fabShowAnimation);
        }

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
