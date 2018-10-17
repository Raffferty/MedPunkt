package com.gmail.krbashianrafael.medpunkt.tablet;


import android.animation.ValueAnimator;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
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
import android.widget.ImageView;
import android.widget.TextView;

import com.gmail.krbashianrafael.medpunkt.DiseaseItem;
import com.gmail.krbashianrafael.medpunkt.R;
import com.gmail.krbashianrafael.medpunkt.data.MedContract.DiseasesEntry;
import com.gmail.krbashianrafael.medpunkt.phone.DiseaseRecyclerViewAdapter;
import com.gmail.krbashianrafael.medpunkt.phone.UsersRecyclerViewAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

import static com.gmail.krbashianrafael.medpunkt.tablet.TabletMainActivity.TABLET_DISEASES_FRAGMENT;
import static com.gmail.krbashianrafael.medpunkt.tablet.TabletMainActivity.TABLET_TREATMENT_FRAGMENT;

public class TabletDiseasesFragment extends Fragment
        implements android.support.v4.app.LoaderManager.LoaderCallbacks<Cursor> {

    private TabletMainActivity tabletMainActivity;

    // шапка, которая видна только на планшете
    public TextView txtTabletDiseases;

    public ImageView imgCancelTabletDiseases;


    private long _idUser = 0;

    private String textUserName = "";

    protected TextView textViewAddDisease;

    public FloatingActionButton fabAddDisease;

    private Animation fabHideAnimation;
    Animation fabShowAnimation;
    Animation fadeInAnimation;

    public ValueAnimator animVerGuideline_1_from_0_to_10,
            animVerGuideline_1_from_10_to_0;

    public ValueAnimator animVerGuideline_2_from_30_to_50,
            animVerGuideline_2_from_50_to_30,
            animVerGuideline_2_from_50_to_90,
            animVerGuideline_2_from_30_to_90,
            animVerGuideline_2_from_90_to_30;

    public ValueAnimator animVerGuideline_3_from_100_to_60,
            animVerGuideline_3_from_90_to_100,
            animVerGuideline_3_from_30_to_60,
            animVerGuideline_3_from_60_to_30,
            animVerGuideline_3_from_0_to_60,
            animVerGuideline_3_from_60_to_0,
            animVerGuideline_3_from_100_to_0,
            animVerGuideline_3_from_0_to_100,
            animVerGuideline_3_from_90_to_60,
            animVerGuideline_3_from_100_to_30;

    public ValueAnimator animVerGuideline_4_from_90_to_100;

    //public static boolean scrollToInsertedDiseasePosition = false;
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

        txtTabletDiseases = view.findViewById(R.id.txt_diseases);
        txtTabletDiseases.setBackgroundColor(getResources().getColor(R.color.my_dark_gray));
        txtTabletDiseases.setTextColor(getResources().getColor(R.color.white));

        imgCancelTabletDiseases = view.findViewById(R.id.img_cancel_tablet_diseases);
        imgCancelTabletDiseases.setVisibility(View.VISIBLE);
        imgCancelTabletDiseases.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //tabletMainActivity.ver_3_Guideline.setGuidelinePercent(0.9f);
                //tabletMainActivity.ver_4_Guideline.setGuidelinePercent(0.9f);

                //tabletMainActivity.ver_1_Guideline.setGuidelinePercent(0.1f);
                //tabletMainActivity.ver_2_Guideline.setGuidelinePercent(0.9f);

                /*animVerGuideline_1_from_0_to_10.start();

                float percentVerGuideline_2 = ((ConstraintLayout.LayoutParams) tabletMainActivity.ver_2_Guideline.getLayoutParams()).guidePercent;

                if (percentVerGuideline_2 == 0.30f) {
                    animVerGuideline_2_from_30_to_90.start();
                }else if (percentVerGuideline_2 == 0.50f){
                    animVerGuideline_2_from_50_to_90.start();
                }*/


                tabletMainActivity.tabletUsersFrame.setBackground(tabletMainActivity.getResources().
                        getDrawable(android.R.drawable.dialog_holo_light_frame));

                tabletMainActivity.blur(TABLET_DISEASES_FRAGMENT);

                tabletMainActivity.blur(TABLET_TREATMENT_FRAGMENT);
                tabletMainActivity.tabletTreatmentFragment.set_idUser(0);

                clearDataFromDiseasesFragment();

                UsersRecyclerViewAdapter.setSelected_user_id(0);
                tabletMainActivity.tabletUsersFragment.initUsersLoader();
            }
        });


        textViewAddDisease = view.findViewById(R.id.txt_empty_diseases);
        textViewAddDisease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //textViewAddDisease.setVisibility(View.INVISIBLE);
                tabletMainActivity.tabletUsersFragment.fabAddUser.startAnimation(fabHideAnimation);
                tabletMainActivity.tabletTreatmentDeleteFrame.setVisibility(View.GONE);
                //tabletMainActivity.ver_3_Guideline.setGuidelinePercent(0.30f);

                /*if (TabletMainActivity.diseaseAndTreatmentInEdit) {
                    if (tabletMainActivity.diseaseAndTreatmentHasNotChanged()) {
                        tabletMainActivity.cancel(false);
                        onAddDiseaseClicked();
                    } else {
                        tabletMainActivity.showUnsavedChangesDialog(null);
                    }
                } else {*/
                onAddDiseaseClicked();
                //}



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

                if (diseaseSelected) {
                    tabletMainActivity.tabletTreatmentFragment.treatmentDescriptionFragment.fabEditTreatmentDescripton.startAnimation(
                            tabletMainActivity.tabletTreatmentFragment.treatmentDescriptionFragment.fabHideAnimation);

                    TabletMainActivity.tempTextDiseaseName = tabletMainActivity.tabletTreatmentFragment.editTextDiseaseName.getText().toString();
                    TabletMainActivity.tempTextDateOfTreatment = tabletMainActivity.tabletTreatmentFragment.editTextDateOfDisease.getText().toString();
                    TabletMainActivity.tempTextTreatment = tabletMainActivity.tabletTreatmentFragment.treatmentDescriptionFragment.editTextTreatment.getText().toString();
                }

                fabAddDisease.startAnimation(fabHideAnimation);
                tabletMainActivity.tabletUsersFragment.fabAddUser.startAnimation(fabHideAnimation);

                tabletMainActivity.tabletTreatmentDeleteFrame.setVisibility(View.GONE);

                /*FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

                params.gravity = Gravity.END;
                params.setMarginEnd(128);

                tabletMainActivity.tabletTreatmentSave.setLayoutParams(params);*/

                //tabletMainActivity.tabletTreatmentSave.setGravity(Gravity.END);
                //tabletMainActivity.tabletTreatmentSaveFrame.setForegroundGravity(LE);



                /*TabletMainActivity.newDiseaseAndTreatment = true;
                TabletMainActivity.diseaseAndTreatmentInEdit = true;*/

                if (TabletMainActivity.diseaseAndTreatmentInEdit) {
                    if (tabletMainActivity.diseaseAndTreatmentHasNotChanged()) {
                        tabletMainActivity.cancel(false);
                        onAddDiseaseClicked();

                    } else {
                        tabletMainActivity.showUnsavedChangesDialog(null);

                    }
                } else {
                    onAddDiseaseClicked();
                }

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

    void onAddDiseaseClicked() {
        //if (!TabletMainActivity.diseaseAndTreatmentInEdit) {


        //tabletMainActivity.tabletUsersFragment.fabAddUser.startAnimation(fabHideAnimation);

        //}


        TabletMainActivity.newDiseaseAndTreatment = true;
        TabletMainActivity.diseaseAndTreatmentInEdit = true;
        tabletMainActivity.tabletTreatmentFragment.editDisease = true;


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                // на планшете показываем клавиатуру
                /*InputMethodManager imm = (InputMethodManager)
                        tabletMainActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, InputMethodManager.HIDE_IMPLICIT_ONLY);
                }*/

                if (textViewAddDisease.getVisibility() == View.VISIBLE) {
                    textViewAddDisease.setVisibility(View.INVISIBLE);
                }

                tabletMainActivity.tabletTreatmentFragment.imgZoomOutTabletTreatment.setVisibility(View.INVISIBLE);

                tabletMainActivity.tabletTreatmentFragment.set_idUser(_idUser);
                tabletMainActivity.tabletTreatmentFragment.editTextDiseaseName.setText("");
                tabletMainActivity.tabletTreatmentFragment.editTextDateOfDisease.setText(getString(R.string.disease_date));
                tabletMainActivity.tabletTreatmentFragment.treatmentDescriptionFragment.editTextTreatment.setText("");

                tabletMainActivity.tabletTreatmentFragment.tabLayout.setVisibility(View.GONE);
                Objects.requireNonNull(tabletMainActivity.tabletTreatmentFragment.tabLayout.getTabAt(0)).select();

                tabletMainActivity.unBlur(TabletMainActivity.TABLET_TREATMENT_FRAGMENT);
                tabletMainActivity.tabletTreatmentFragment.viewPager.setVisibility(View.VISIBLE);

                //tabletMainActivity.ver_2_Guideline.setGuidelinePercent(0.30f);
                //tabletMainActivity.ver_3_Guideline.setGuidelinePercent(0.30f);
                //tabletMainActivity.ver_3_Guideline.setGuidelinePercent(0.00f);


                float percentVerGuideline_3 = ((ConstraintLayout.LayoutParams) tabletMainActivity.ver_3_Guideline.getLayoutParams()).guidePercent;

                if (percentVerGuideline_3 == 0.60f) {
                    animVerGuideline_3_from_60_to_0.start();
                } else if (percentVerGuideline_3 == 1.00f) {
                    animVerGuideline_3_from_100_to_0.start();
                }


                //mTabletMainActivity.tabletTreatmentTitle.setVisibility(View.INVISIBLE);
                tabletMainActivity.LLtabletTreatmentCancelOrSave.setVisibility(View.VISIBLE);
                //tabletMainActivity.tabletTreatmentDelete.setVisibility(View.GONE);


                tabletMainActivity.tabletTreatmentFragment.textInputLayoutDiseaseName.setVisibility(View.VISIBLE);
                tabletMainActivity.tabletTreatmentFragment.editTextDateOfDisease.setVisibility(View.VISIBLE);

                tabletMainActivity.tabletTreatmentFragment.editTextDiseaseName.setEnabled(true);
                tabletMainActivity.tabletTreatmentFragment.editTextDiseaseName.requestFocus();
                //tabletMainActivity.tabletTreatmentFragment.treatmentDescriptionFragment.editTextTreatment.setEnabled(true);
                tabletMainActivity.tabletTreatmentFragment.treatmentDescriptionFragment.editTextTreatment.setFocusable(true);
                tabletMainActivity.tabletTreatmentFragment.treatmentDescriptionFragment.editTextTreatment.setFocusableInTouchMode(true);
                tabletMainActivity.tabletTreatmentFragment.treatmentDescriptionFragment.editTextTreatment.setCursorVisible(true);

                InputMethodManager imm = (InputMethodManager) tabletMainActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.showSoftInput(tabletMainActivity.tabletTreatmentFragment.editTextDiseaseName, InputMethodManager.SHOW_IMPLICIT);
                }
            }
        }, 500);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        tabletMainActivity = (TabletMainActivity) getActivity();

        animVerGuideline_1_from_0_to_10 = ValueAnimator.ofFloat(0.00f, 0.10f);
        animVerGuideline_1_from_0_to_10.setDuration(200);
        animVerGuideline_1_from_0_to_10.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator updatedAnimation) {
                float animatedValue_1 = (float) updatedAnimation.getAnimatedValue();
                tabletMainActivity.ver_1_Guideline.setGuidelinePercent(animatedValue_1);
            }
        });

        animVerGuideline_1_from_10_to_0 = ValueAnimator.ofFloat(0.10f, 0.00f);
        animVerGuideline_1_from_10_to_0.setDuration(200);
        animVerGuideline_1_from_10_to_0.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator updatedAnimation) {
                float animatedValue_1 = (float) updatedAnimation.getAnimatedValue();
                tabletMainActivity.ver_1_Guideline.setGuidelinePercent(animatedValue_1);
            }
        });


        animVerGuideline_2_from_30_to_50 = ValueAnimator.ofFloat(0.30f, 0.50f);
        animVerGuideline_2_from_30_to_50.setDuration(200);
        animVerGuideline_2_from_30_to_50.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator updatedAnimation) {
                float animatedValue_2 = (float) updatedAnimation.getAnimatedValue();
                tabletMainActivity.ver_2_Guideline.setGuidelinePercent(animatedValue_2);
            }
        });

        animVerGuideline_2_from_50_to_30 = ValueAnimator.ofFloat(0.50f, 0.30f);
        animVerGuideline_2_from_50_to_30.setDuration(200);
        animVerGuideline_2_from_50_to_30.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator updatedAnimation) {
                float animatedValue_2 = (float) updatedAnimation.getAnimatedValue();
                tabletMainActivity.ver_2_Guideline.setGuidelinePercent(animatedValue_2);
            }
        });

        animVerGuideline_2_from_30_to_90 = ValueAnimator.ofFloat(0.30f, 0.90f);
        animVerGuideline_2_from_30_to_90.setDuration(200);
        animVerGuideline_2_from_30_to_90.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator updatedAnimation) {
                float animatedValue_2 = (float) updatedAnimation.getAnimatedValue();
                tabletMainActivity.ver_2_Guideline.setGuidelinePercent(animatedValue_2);
            }
        });

        animVerGuideline_2_from_50_to_90 = ValueAnimator.ofFloat(0.50f, 0.90f);
        animVerGuideline_2_from_50_to_90.setDuration(200);
        animVerGuideline_2_from_50_to_90.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator updatedAnimation) {
                float animatedValue_2 = (float) updatedAnimation.getAnimatedValue();
                tabletMainActivity.ver_2_Guideline.setGuidelinePercent(animatedValue_2);
            }
        });

        animVerGuideline_2_from_90_to_30 = ValueAnimator.ofFloat(0.90f, 0.30f);
        animVerGuideline_2_from_90_to_30.setDuration(200);
        animVerGuideline_2_from_90_to_30.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator updatedAnimation) {
                float animatedValue_2 = (float) updatedAnimation.getAnimatedValue();
                tabletMainActivity.ver_2_Guideline.setGuidelinePercent(animatedValue_2);
            }
        });

        animVerGuideline_3_from_90_to_60 = ValueAnimator.ofFloat(0.90f, 0.60f);
        animVerGuideline_3_from_90_to_60.setDuration(200);
        animVerGuideline_3_from_90_to_60.setStartDelay(50);
        animVerGuideline_3_from_90_to_60.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator updatedAnimation) {
                float animatedValue_3 = (float) updatedAnimation.getAnimatedValue();
                tabletMainActivity.ver_3_Guideline.setGuidelinePercent(animatedValue_3);
            }
        });

        animVerGuideline_3_from_100_to_60 = ValueAnimator.ofFloat(1.00f, 0.60f);
        animVerGuideline_3_from_100_to_60.setDuration(200);
        animVerGuideline_3_from_100_to_60.setStartDelay(50);
        animVerGuideline_3_from_100_to_60.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator updatedAnimation) {
                float animatedValue_3 = (float) updatedAnimation.getAnimatedValue();
                tabletMainActivity.ver_3_Guideline.setGuidelinePercent(animatedValue_3);
            }
        });

        animVerGuideline_3_from_100_to_30 = ValueAnimator.ofFloat(1.00f, 0.30f);
        animVerGuideline_3_from_100_to_30.setDuration(200);
        animVerGuideline_3_from_100_to_30.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator updatedAnimation) {
                float animatedValue_3 = (float) updatedAnimation.getAnimatedValue();
                tabletMainActivity.ver_3_Guideline.setGuidelinePercent(animatedValue_3);
            }
        });

        animVerGuideline_3_from_0_to_60 = ValueAnimator.ofFloat(0.00f, 0.60f);
        animVerGuideline_3_from_0_to_60.setDuration(200);
        animVerGuideline_3_from_0_to_60.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator updatedAnimation) {
                float animatedValue_3 = (float) updatedAnimation.getAnimatedValue();
                tabletMainActivity.ver_3_Guideline.setGuidelinePercent(animatedValue_3);
                if (animatedValue_3 == 0.60f) {
                    tabletMainActivity.tabletUsersWideTitle.setVisibility(View.GONE);
                    tabletMainActivity.tabletUsersWideTitle.setText("");
                    tabletMainActivity.tabletTreatmentTitle.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                    //tabletMainActivity.tabletTreatmentTitle.setTextColor(getResources().getColor(R.color.white));
                }
            }
        });

        animVerGuideline_3_from_60_to_0 = ValueAnimator.ofFloat(0.60f, 0.00f);
        animVerGuideline_3_from_60_to_0.setDuration(200);
        animVerGuideline_3_from_60_to_0.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator updatedAnimation) {
                float animatedValue_3 = (float) updatedAnimation.getAnimatedValue();
                tabletMainActivity.ver_3_Guideline.setGuidelinePercent(animatedValue_3);
                if (animatedValue_3 == 0.00f) {
                    tabletMainActivity.tabletUsersWideTitle.setText(tabletMainActivity.tabletDiseasesTitle.getText().toString());
                    tabletMainActivity.tabletUsersWideTitle.setVisibility(View.VISIBLE);
                    tabletMainActivity.tabletTreatmentTitle.setBackgroundColor(getResources().getColor(R.color.blue));
                    //tabletMainActivity.tabletTreatmentTitle.setTextColor(getResources().getColor(R.color.black));
                }
            }
        });

        animVerGuideline_3_from_100_to_0 = ValueAnimator.ofFloat(1.00f, 0.00f);
        animVerGuideline_3_from_100_to_0.setDuration(200);
        animVerGuideline_3_from_100_to_0.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator updatedAnimation) {
                float animatedValue_3 = (float) updatedAnimation.getAnimatedValue();
                tabletMainActivity.ver_3_Guideline.setGuidelinePercent(animatedValue_3);
                if (animatedValue_3 == 0.00f) {
                    tabletMainActivity.tabletUsersWideTitle.setText(tabletMainActivity.tabletDiseasesTitle.getText().toString());
                    tabletMainActivity.tabletUsersWideTitle.setVisibility(View.VISIBLE);
                    tabletMainActivity.tabletTreatmentTitle.setBackgroundColor(getResources().getColor(R.color.blue));
                    //tabletMainActivity.tabletTreatmentTitle.setTextColor(getResources().getColor(R.color.black));
                }
            }
        });

        animVerGuideline_3_from_0_to_100 = ValueAnimator.ofFloat(0.00f, 1.00f);
        animVerGuideline_3_from_0_to_100.setDuration(200);
        animVerGuideline_3_from_0_to_100.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator updatedAnimation) {
                float animatedValue_3 = (float) updatedAnimation.getAnimatedValue();
                tabletMainActivity.ver_3_Guideline.setGuidelinePercent(animatedValue_3);
                if (animatedValue_3 == 1.00f) {
                    tabletMainActivity.tabletUsersWideTitle.setVisibility(View.GONE);
                    tabletMainActivity.tabletUsersWideTitle.setText("");
                    tabletMainActivity.tabletTreatmentTitle.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                    //tabletMainActivity.tabletTreatmentTitle.setTextColor(getResources().getColor(R.color.white));
                }
            }
        });


        animVerGuideline_3_from_60_to_30 = ValueAnimator.ofFloat(0.60f, 0.30f);
        animVerGuideline_3_from_60_to_30.setDuration(200);
        animVerGuideline_3_from_60_to_30.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator updatedAnimation) {
                float animatedValue_3 = (float) updatedAnimation.getAnimatedValue();
                tabletMainActivity.ver_3_Guideline.setGuidelinePercent(animatedValue_3);
            }
        });

        animVerGuideline_3_from_30_to_60 = ValueAnimator.ofFloat(0.30f, 0.60f);
        animVerGuideline_3_from_30_to_60.setDuration(200);
        animVerGuideline_3_from_30_to_60.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator updatedAnimation) {
                float animatedValue_3 = (float) updatedAnimation.getAnimatedValue();
                tabletMainActivity.ver_3_Guideline.setGuidelinePercent(animatedValue_3);
            }
        });


        animVerGuideline_3_from_90_to_100 = ValueAnimator.ofFloat(0.90f, 1.00f);
        animVerGuideline_3_from_90_to_100.setDuration(200);
        animVerGuideline_3_from_90_to_100.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator updatedAnimation) {
                float animatedValue_3 = (float) updatedAnimation.getAnimatedValue();
                tabletMainActivity.ver_3_Guideline.setGuidelinePercent(animatedValue_3);
            }
        });

        animVerGuideline_4_from_90_to_100 = ValueAnimator.ofFloat(0.90f, 1.00f);
        animVerGuideline_4_from_90_to_100.setDuration(200);
        animVerGuideline_4_from_90_to_100.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator updatedAnimation) {
                float animatedValue_4 = (float) updatedAnimation.getAnimatedValue();
                tabletMainActivity.ver_4_Guideline.setGuidelinePercent(animatedValue_4);
            }
        });

        /*animVerGuideline_3_from_60_to_100 = ValueAnimator.ofFloat(0.60f, 1.00f);
        animVerGuideline_3_from_60_to_100.setDuration(100);
        animVerGuideline_3_from_60_to_100.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator updatedAnimation) {
                float animatedValue_3 = (float) updatedAnimation.getAnimatedValue();
                tabletMainActivity.ver_3_Guideline.setGuidelinePercent(animatedValue_3);
            }
        });*/


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

        // оповещаем LayoutManager, что произошли изменения
        // LayoutManager обновляет RecyclerView
        diseaseRecyclerViewAdapter.notifyDataSetChanged();

        int myDataSize = myData.size();


        //if (scrollToInsertedDiseasePosition && myData.size() != 0) {
        // код для показа выделенного заболевания
        if (myData.size() != 0) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                    if (TabletMainActivity.selectedDisease_id != 0) {
                        for (int i = 0; i < myData.size(); i++) {
                            if (myData.get(i).get_diseaseId() == TabletMainActivity.selectedDisease_id) {
                                TabletMainActivity.selectedDisease_position = i;
                            }
                        }
                    }

                    recyclerDiseases.smoothScrollToPosition(TabletMainActivity.selectedDisease_position);
                }
            }, 500);

            //scrollToInsertedDiseasePosition = false;
        }

        if (myDataSize == 0) {
            // если у пользователя нет заболеваний

            TabletMainActivity.diseasesIsEmpty = true;

            /*txtTabletDiseases.setText(R.string.diseases_what_text);
            txtTabletDiseases.setBackgroundColor(getResources().getColor(R.color.colorPrimary));*/

            diseaseSelected = false;

            /*tabletMainActivity.ver_1_Guideline.setGuidelinePercent(0.0f);
            tabletMainActivity.ver_2_Guideline.setGuidelinePercent(0.5f);
            tabletMainActivity.ver_3_Guideline.setGuidelinePercent(1.0f);
            tabletMainActivity.ver_4_Guideline.setGuidelinePercent(1.0f);*/

            float percentVerGuideline_2 = ((ConstraintLayout.LayoutParams) tabletMainActivity.ver_2_Guideline.getLayoutParams()).guidePercent;

            if (percentVerGuideline_2 == 0.90f) {
                // если были видны только пользователи и нажимали на пользователя у которого нет заболеваний

                tabletMainActivity.ver_2_Guideline.setGuidelinePercent(0.5f);

                animVerGuideline_1_from_10_to_0.start();
                animVerGuideline_3_from_90_to_100.start();

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        tabletMainActivity.ver_4_Guideline.setGuidelinePercent(1.0f);
                    }
                }, 300);

            } else if (percentVerGuideline_2 == 0.30f) {
                // если были видны пользователи и у пользователя было одно заболевание, которе удалили
                // в итоге у пользователя нет заболеваний

                tabletMainActivity.ver_3_Guideline.setGuidelinePercent(1.0f);
                animVerGuideline_2_from_30_to_50.start();
            }

            tabletMainActivity.blur(TABLET_TREATMENT_FRAGMENT);
            tabletMainActivity.tabletTreatmentFragment.set_idUser(0);

            // если нет пользователей, то чистим TreatmentFragment
            tabletMainActivity.tabletTreatmentTitle.setText("");

            tabletMainActivity.tabletTreatmentFragment.tabLayout.setVisibility(View.INVISIBLE);
            tabletMainActivity.tabletTreatmentFragment.viewPager.setVisibility(View.INVISIBLE);

            tabletMainActivity.tabletTreatmentFragment.treatmentDescriptionFragment.fabEditTreatmentDescripton.setVisibility(View.INVISIBLE);

            /*if (_idUser != 0) {
             *//*new Handler(Looper.getMainLooper()).
                        postDelayed(new Runnable() {
                            @Override
                            public void run() {*//*
                if (TabletMainActivity.diseaseAndTreatmentInEdit) {
                    tabletMainActivity.tabletTreatmentCancel.performClick();

                } else {*/
            textViewAddDisease.setVisibility(View.VISIBLE);
            textViewAddDisease.startAnimation(fadeInAnimation);
            //}

                            /*}
                        }, 300);*/
            //В}

        } else if (myDataSize == 1) {
            // если у пользователя одно заболевание

            TabletMainActivity.diseasesIsEmpty = false;

            //if (TabletMainActivity.diseaseAndTreatmentInEdit) {
            tabletMainActivity.hideElementsOnTabletTreatmentFragment();
            //}

            tabletMainActivity.unBlur(TABLET_TREATMENT_FRAGMENT);

            float percentVerGuideline_2 = ((ConstraintLayout.LayoutParams) tabletMainActivity.ver_2_Guideline.getLayoutParams()).guidePercent;
            float percentVerGuideline_3 = ((ConstraintLayout.LayoutParams) tabletMainActivity.ver_3_Guideline.getLayoutParams()).guidePercent;

            if (percentVerGuideline_2 == 0.90f) {
                // если были открыты только пользователи
                // и было нажато на пользователя у которого только одно заболевание

                tabletMainActivity.tabletTreatmentFragment.imgZoomInTabletTreatment.setVisibility(View.INVISIBLE);
                tabletMainActivity.tabletTreatmentFragment.imgZoomOutTabletTreatment.setVisibility(View.VISIBLE);

                animVerGuideline_2_from_90_to_30.start();
                animVerGuideline_3_from_90_to_60.start();

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        animVerGuideline_1_from_10_to_0.start();
                        animVerGuideline_4_from_90_to_100.start();
                    }
                }, 200);

            } else if (percentVerGuideline_2 == 0.50f) {
                // если был открыт пользователь, у которого нет заболеваний и
                // был нажат польлзователь, у которого одно заболевание

                tabletMainActivity.tabletTreatmentFragment.imgZoomInTabletTreatment.setVisibility(View.INVISIBLE);
                tabletMainActivity.tabletTreatmentFragment.imgZoomOutTabletTreatment.setVisibility(View.VISIBLE);

                tabletMainActivity.ver_1_Guideline.setGuidelinePercent(0.00f);
                tabletMainActivity.ver_4_Guideline.setGuidelinePercent(1.00f);

                animVerGuideline_2_from_50_to_30.start();
                animVerGuideline_3_from_100_to_60.start();

            } else if (percentVerGuideline_3 == 0.30f) {
                // если у пользователя не было заболеваний и было нажато добавить заболевание
                // и было нажато "сохранить"

                // ИЛИ
                // если у пользователя одно заболевание, которе было открыто для редактирования,
                // и было нажато "сохранить"

                tabletMainActivity.tabletTreatmentFragment.imgZoomInTabletTreatment.setVisibility(View.INVISIBLE);
                tabletMainActivity.tabletTreatmentFragment.imgZoomOutTabletTreatment.setVisibility(View.VISIBLE);

                animVerGuideline_3_from_30_to_60.start();

            } else if (percentVerGuideline_3 == 1.00f) {
                // если у пользователя было два заболевания,
                // при этом одно из них было открыто для удаления и удалилось
                // в итоге осталось одно заболевание

                tabletMainActivity.tabletTreatmentFragment.imgZoomInTabletTreatment.setVisibility(View.INVISIBLE);
                tabletMainActivity.tabletTreatmentFragment.imgZoomOutTabletTreatment.setVisibility(View.VISIBLE);

                animVerGuideline_3_from_100_to_60.start();
            }

            fabAddDisease.startAnimation(fabShowAnimation);

            tabletMainActivity.tabletUsersWideTitle.setVisibility(View.GONE);
            tabletMainActivity.tabletUsersWideTitle.setText("");
            tabletMainActivity.tabletTreatmentTitle.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));

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
            // если у пользователя более одного заболевания

            TabletMainActivity.diseasesIsEmpty = false;

            if (TabletMainActivity.diseaseInserted) {
                // если было добавлено заболевание

                tabletMainActivity.unBlur(TabletMainActivity.TABLET_TREATMENT_FRAGMENT);
                tabletMainActivity.tabletTreatmentFragment.tabLayout.setVisibility(View.VISIBLE);
                tabletMainActivity.tabletTreatmentFragment.viewPager.setVisibility(View.VISIBLE);

                animVerGuideline_3_from_0_to_60.start();
                tabletMainActivity.ver_1_Guideline.setGuidelinePercent(0.00f);
                tabletMainActivity.ver_2_Guideline.setGuidelinePercent(0.30f);
                tabletMainActivity.ver_4_Guideline.setGuidelinePercent(1.00f);
                tabletMainActivity.tabletTreatmentFragment.setTextDiseaseName(tabletMainActivity.tabletTreatmentFragment.textDiseaseName);

                /*if (diseaseSelected) {
                    // и при этом было выделено какое-то заболеванине
                    // то выделенное ранее заболевание остается в поле зрения

                    tabletMainActivity.tabletTreatmentFragment.setTextDiseaseName(TabletMainActivity.tempTextDiseaseName);
                    tabletMainActivity.tabletTreatmentFragment.setTextDateOfDisease(TabletMainActivity.tempTextDateOfTreatment);
                    tabletMainActivity.tabletTreatmentFragment.setTextTreatment(TabletMainActivity.tempTextTreatment);

                    //animVerGuideline_3_from_30_to_60.start();
                    animVerGuideline_3_from_0_to_60.start();

                } else {
                    tabletMainActivity.ver_3_Guideline.setGuidelinePercent(1.0f);
                    tabletMainActivity.tabletUsersWideTitle.setVisibility(View.GONE);
                    tabletMainActivity.tabletUsersWideTitle.setText("");
                    tabletMainActivity.tabletTreatmentTitle.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                    tabletMainActivity.tabletTreatmentFragment.imgZoomOutTabletTreatment.setVisibility(View.VISIBLE);
                    animVerGuideline_2_from_30_to_50.start();
                }*/

            } else if (TabletMainActivity.diseaseUpdated) {
                // если заболевание было обновлено

               /* else if (TabletMainActivity.diseaseUpdated &&
                        TabletMainActivity.disease_IdInEdit ==
                                tabletMainActivity.tabletTreatmentFragment.get_idDisease()) {*/

                //tabletMainActivity.hideElementsOnTabletTreatmentFragment();

                //animVerGuideline_3_from_30_to_60.start();

                tabletMainActivity.tabletTreatmentFragment.imgZoomInTabletTreatment.setVisibility(View.VISIBLE);
                tabletMainActivity.tabletTreatmentFragment.imgZoomOutTabletTreatment.setVisibility(View.INVISIBLE);


                tabletMainActivity.tabletTreatmentFragment.treatmentDescriptionFragment.
                        fabEditTreatmentDescripton.startAnimation(
                        tabletMainActivity.tabletTreatmentFragment.fabShowAnimation
                );


                // если заболевание, которое было в TreatmentFragment обновилось (поменялось название...)
                // то устанавливаем обновленные поля

                /*if (!diseaseSelected) {
             /*tabletMainActivity.unBlur(TABLET_TREATMENT_FRAGMENT);
                    tabletMainActivity.tabletTreatmentFragment.tabLayout.setVisibility(View.VISIBLE);
                    tabletMainActivity.tabletTreatmentFragment.viewPager.setVisibility(View.VISIBLE);*//**//*
                    //tabletMainActivity.tabletTreatmentTitle.setText(TabletMainActivity.diseaseNameAfterUpdate);
                    tabletMainActivity.tabletTreatmentFragment.setTextDiseaseName(TabletMainActivity.diseaseNameAfterUpdate);
                    tabletMainActivity.tabletTreatmentFragment.setTextDateOfDisease(TabletMainActivity.diseaseDateAfterUpdate);
                    tabletMainActivity.tabletTreatmentFragment.setTextTreatment(TabletMainActivity.diseaseTreatmentAfterUpdate);
                }*/

                //tabletMainActivity.hideElementsOnTabletTreatmentFragment();

            } else if (tabletMainActivity.tabletTreatmentFragment.get_idUser() != get_idUser()) {
                //если первый заход в tabletDiseasesFragment и в TreatmentFragment еще не отображаются данные,
                // т.е. tabletMainActivity.tabletTreatmentFragment.get_idUser() = 0
                // или был выбрарн другой пользователь у которого больше одного заболевания

                diseaseSelected = false;


                // то предлагаем сдеалть выбор заболевани для отображения в TreatmentFragment
                /*txtTabletDiseases.setText(R.string.tablet_treatment_select_disease);

                txtTabletDiseases.setBackgroundColor(getResources().getColor(R.color.colorFab));*/

                /*float percentVer_1 = ((ConstraintLayout.LayoutParams) tabletMainActivity.ver_1_Guideline.getLayoutParams()).guidePercent; //0.10
                float percentVer_2 = ((ConstraintLayout.LayoutParams) tabletMainActivity.ver_2_Guideline.getLayoutParams()).guidePercent; //0.90
                float percentVer_3 = ((ConstraintLayout.LayoutParams) tabletMainActivity.ver_3_Guideline.getLayoutParams()).guidePercent; //0.90
                float percentVer_4 = ((ConstraintLayout.LayoutParams) tabletMainActivity.ver_4_Guideline.getLayoutParams()).guidePercent; //0.90
                Log.d("sasa", "percentVer_1 = " + percentVer_1);
                Log.d("sasa", "percentVer_2 = " + percentVer_2);
                Log.d("sasa", "percentVer_3 = " + percentVer_3);
                Log.d("sasa", "percentVer_4 = " + percentVer_4);*/

                /*ValueAnimator animVer_2 = ValueAnimator.ofFloat(0.9f, 0.5f);
                animVer_2.setDuration(100);
                //animVer_2.setStartDelay(200);
                animVer_2.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator updatedAnimation) {
                        float animatedValue_2 = (float) updatedAnimation.getAnimatedValue();
                        tabletMainActivity.ver_2_Guideline.setGuidelinePercent(animatedValue_2);
                    }
                });*/

                /*ValueAnimator animVer_4 = ValueAnimator.ofFloat(0.9f, 1.0f);
                animVer_4.setDuration(200);
                animVer_4.setStartDelay(200);
                animVer_4.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator updatedAnimation) {
                        // You can use the animated value in a property that uses the
                        // same type as the animation. In this case, you can use the
                        // float value in the translationX property.
                        float animatedValue_4 = (float) updatedAnimation.getAnimatedValue();
                        Log.d("sasa", "animatedValue_2 = " + animatedValue_4);
                        tabletMainActivity.ver_4_Guideline.setGuidelinePercent(animatedValue_4);
                    }
                });*/


                //animVer_2.start();
                //animVer_4.start();


                //tabletMainActivity.ver_1_Guideline.setGuidelinePercent(0.0f);
                //tabletMainActivity.ver_3_Guideline.setGuidelinePercent(1.0f);
                //tabletMainActivity.ver_4_Guideline.setGuidelinePercent(1.0f);


                float percentVerGuideline_2 = ((ConstraintLayout.LayoutParams) tabletMainActivity.ver_2_Guideline.getLayoutParams()).guidePercent;

                if (percentVerGuideline_2 == 0.90f) {
                    // если было больше одного пользователя и были видны только пользователи
                    // и был нажат пользователь у которого более одного заболевания

                    tabletMainActivity.ver_2_Guideline.setGuidelinePercent(0.5f);
                    animVerGuideline_1_from_10_to_0.start();
                    animVerGuideline_3_from_90_to_100.start();

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            tabletMainActivity.ver_4_Guideline.setGuidelinePercent(1.0f);
                        }
                    }, 300);
                } else if (percentVerGuideline_2 == 0.30f) {
                    // если программа открылась только с одним пользователем, у которого более одного заболевания

                    tabletMainActivity.ver_3_Guideline.setGuidelinePercent(1.0f);
                    animVerGuideline_2_from_30_to_50.start();
                }

                tabletMainActivity.blur(TABLET_TREATMENT_FRAGMENT);
                tabletMainActivity.tabletTreatmentFragment.set_idUser(0);

                tabletMainActivity.tabletTreatmentTitle.setText("");

                tabletMainActivity.tabletTreatmentFragment.tabLayout.setVisibility(View.INVISIBLE);
                tabletMainActivity.tabletTreatmentFragment.viewPager.setVisibility(View.INVISIBLE);

                tabletMainActivity.tabletTreatmentFragment.treatmentDescriptionFragment.
                        fabEditTreatmentDescripton.setVisibility(View.INVISIBLE);

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

        // убираем тени вокруг tabletUsersFrame
        tabletMainActivity.tabletUsersFrame.setBackgroundResource(0);
        tabletMainActivity.tabletUsersFrame.setPadding(0, 0, 0, 0);

        // после прохождения всех if выставляем флаги в false
        TabletMainActivity.diseaseInserted = false;
        TabletMainActivity.diseaseUpdated = false;
        TabletMainActivity.diseaseDeleted = false;

        TabletMainActivity.diseaseAndTreatmentInEdit = false;
        TabletMainActivity.newDiseaseAndTreatment = false;
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        ArrayList<DiseaseItem> myData = diseaseRecyclerViewAdapter.getDiseaseList();
        myData.clear();
        diseaseRecyclerViewAdapter.notifyDataSetChanged();
    }
}
