package com.gmail.krbashianrafael.medpunkt.phone;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.gmail.krbashianrafael.medpunkt.HomeActivity;
import com.gmail.krbashianrafael.medpunkt.R;
import com.gmail.krbashianrafael.medpunkt.tablet.TabletMainActivity;

public class TreatmentDescriptionFragment extends Fragment {

    // кастомный EditText у которого клавиатура не перекрывает текст
    public MyEditText editTextTreatment;

    // fabEditTreatmentDescripton
    public FloatingActionButton fabEditTreatmentDescripton;

    private Animation fabHideAnimation;

    public TreatmentDescriptionFragment() {
        // нужен ПУСТОЙ конструктор
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.treatment_description_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        editTextTreatment = view.findViewById(R.id.editTextTreatment);
        if (HomeActivity.iAmDoctor) {
            editTextTreatment.setHint(R.string.patient_treatment_description_hint_text);
        }

        fabEditTreatmentDescripton = view.findViewById(R.id.fabEditTreatmentDescripton);

    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Активити в котором может находится этот фрагмент
        // TreatmentActivity - если телефорн
        // TabletMainActivity - если планшет
        TreatmentActivity mTreaymentActivity = null;
        TabletMainActivity mTabletMainActivity = null;

        /*if (HomeActivity.isTablet) {
            mTabletMainActivity = (TabletMainActivity) getActivity();
            doWorkWithTabletMainActivity(mTabletMainActivity);
        } else {
            mTreaymentActivity = (TreatmentActivity) getActivity();
            doWorkWithTreaymentActivity(mTreaymentActivity);
        }*/

        if (getActivity() instanceof TabletMainActivity) {
            mTabletMainActivity = (TabletMainActivity) getActivity();
            doWorkWithTabletMainActivity(mTabletMainActivity);
        } else {
            mTreaymentActivity = (TreatmentActivity) getActivity();
            doWorkWithTreaymentActivity(mTreaymentActivity);
        }
    }

    private void doWorkWithTabletMainActivity(final TabletMainActivity mTabletMainActivity) {
        if (mTabletMainActivity != null) {

            // в главном активити инициализируем фрагмент (есл он еще не инициализирован,
            // т.е. если он еще null)
            if (mTabletMainActivity.tabletTreatmentFragment.treatmentDescriptionFragment == null) {
                mTabletMainActivity.tabletTreatmentFragment.initTreatmentDescriptionFragment();
            }

            Animation fabShowAnimation = AnimationUtils.loadAnimation(mTabletMainActivity, R.anim.fab_show);
            fabShowAnimation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    fabEditTreatmentDescripton.setVisibility(View.VISIBLE);
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    fabEditTreatmentDescripton.setVisibility(View.VISIBLE);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                    fabEditTreatmentDescripton.setVisibility(View.VISIBLE);
                }
            });

            if (!mTabletMainActivity.tabletTreatmentFragment.editDisease) {
                fabEditTreatmentDescripton.startAnimation(fabShowAnimation);
            }

            fabHideAnimation = AnimationUtils.loadAnimation(mTabletMainActivity, R.anim.fab_hide);
            fabHideAnimation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    fabEditTreatmentDescripton.setVisibility(View.INVISIBLE);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }
            });

            editTextTreatment.setText(mTabletMainActivity.tabletTreatmentFragment.textTreatment);

            fabEditTreatmentDescripton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    //fabEditTreatmentDescripton.startAnimation(fabHideAnimation);

                    Intent treatmentIntent = new Intent(mTabletMainActivity, TreatmentActivity.class);
                    treatmentIntent.putExtra("_idDisease", mTabletMainActivity.tabletTreatmentFragment._idDisease);
                    treatmentIntent.putExtra("_idUser", mTabletMainActivity.tabletTreatmentFragment._idUser);
                    treatmentIntent.putExtra("diseaseName", mTabletMainActivity.tabletTreatmentFragment.textDiseaseName);
                    treatmentIntent.putExtra("diseaseDate", mTabletMainActivity.tabletTreatmentFragment.textDateOfDisease);
                    treatmentIntent.putExtra("textTreatment",mTabletMainActivity.tabletTreatmentFragment.textTreatment);

                    mTabletMainActivity.startActivity(treatmentIntent);

                    /*mTabletMainActivity.tabletTreatmentFragment.textInputLayoutDiseaseName.setVisibility(View.VISIBLE);
                    mTabletMainActivity.tabletTreatmentFragment.editTextDiseaseName.setEnabled(true);

                    mTabletMainActivity.tabletTreatmentFragment.editDisease = true;*/

                    //mTabletMainActivity.tabletTreatmentFragment.invalidateOptionsMenu();

                    // оставляем только страницу редактирования описания лечения
                    // страницу с фото убираем
                    /*mTabletMainActivity.tabletTreatmentFragment.categoryAdapter.setPagesCount(1);
                    mTabletMainActivity.tabletTreatmentFragment.viewPager.setAdapter(mTabletMainActivity.tabletTreatmentFragment.categoryAdapter);
                    mTabletMainActivity.tabletTreatmentFragment.tabLayout.setVisibility(View.GONE);

                    editTextTreatment.setFocusable(true);
                    editTextTreatment.setFocusableInTouchMode(true);
                    editTextTreatment.setCursorVisible(true);
                    editTextTreatment.requestFocus();
                    editTextTreatment.setSelection(editTextTreatment.getText().toString().length());*/

                    // выбор даты делаем видимым
                    /*mTabletMainActivity.tabletTreatmentFragment.editTextDateOfDisease.setVisibility(View.VISIBLE);

                    mTabletMainActivity.tabletTreatmentFragment.txtTitleDisease.setVisibility(View.GONE);*/
                }
            });

            if (!mTabletMainActivity.tabletTreatmentFragment.editDisease) {
                editTextTreatment.setFocusable(false);
                editTextTreatment.setFocusableInTouchMode(false);
                editTextTreatment.setCursorVisible(false);

                fabEditTreatmentDescripton.setVisibility(View.VISIBLE);
            }
        }
    }

    private void doWorkWithTreaymentActivity(final TreatmentActivity mTreaymentActivity) {
        if (mTreaymentActivity != null) {

            Animation fabShowAnimation = AnimationUtils.loadAnimation(mTreaymentActivity, R.anim.fab_show);
            fabShowAnimation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    fabEditTreatmentDescripton.setVisibility(View.VISIBLE);
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    fabEditTreatmentDescripton.setVisibility(View.VISIBLE);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                    fabEditTreatmentDescripton.setVisibility(View.VISIBLE);
                }
            });


            if (!mTreaymentActivity.editDisease) {
                fabEditTreatmentDescripton.startAnimation(fabShowAnimation);
            }

            fabHideAnimation = AnimationUtils.loadAnimation(mTreaymentActivity, R.anim.fab_hide);
            fabHideAnimation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    fabEditTreatmentDescripton.setVisibility(View.INVISIBLE);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }
            });

            // в главном активити инициализируем фрагмент (есл он еще не инициализирован, т.е. если он еще null)
            if (mTreaymentActivity.treatmentDescriptionFragment == null) {
                mTreaymentActivity.initTreatmentDescriptionFragment();
            }

            editTextTreatment.setText(mTreaymentActivity.textTreatment);

            fabEditTreatmentDescripton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    mTreaymentActivity.textInputLayoutDiseaseName.setVisibility(View.VISIBLE);
                    mTreaymentActivity.editTextDiseaseName.setEnabled(true);

                    fabEditTreatmentDescripton.startAnimation(fabHideAnimation);

                    mTreaymentActivity.editDisease = true;

                    mTreaymentActivity.invalidateOptionsMenu();

                    // оставляем только страницу редактирования описания лечения
                    // страницу с фото убираем
                    mTreaymentActivity.categoryAdapter.setPagesCount(1);
                    mTreaymentActivity.viewPager.setAdapter(mTreaymentActivity.categoryAdapter);
                    mTreaymentActivity.tabLayout.setVisibility(View.GONE);

                    editTextTreatment.setFocusable(true);
                    editTextTreatment.setFocusableInTouchMode(true);
                    editTextTreatment.setCursorVisible(true);
                    editTextTreatment.requestFocus();
                    editTextTreatment.setSelection(editTextTreatment.getText().toString().length());

                    // выбор даты делаем видимым
                    mTreaymentActivity.editTextDateOfDisease.setVisibility(View.VISIBLE);

                    mTreaymentActivity.txtTitleDisease.setVisibility(View.GONE);
                }
            });

            if (!mTreaymentActivity.editDisease) {
                editTextTreatment.setFocusable(false);
                editTextTreatment.setFocusableInTouchMode(false);
                editTextTreatment.setCursorVisible(false);

                fabEditTreatmentDescripton.setVisibility(View.VISIBLE);
            }
        }

    }
}
