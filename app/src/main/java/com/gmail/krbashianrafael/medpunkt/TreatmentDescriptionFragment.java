package com.gmail.krbashianrafael.medpunkt;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

public class TreatmentDescriptionFragment extends Fragment {

    // Активити в котором находися этот фрагмент
    private TreatmentActivity newTreaymentActivity;

    // кастомный EditText у которого клавиатура не перекрывает текст
    protected MyEditText editTextTreatment;

    // fabEditTreatmentDescripton
    protected FloatingActionButton fabEditTreatmentDescripton;

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
        if (HomeActivity.iAmDoctor){
            editTextTreatment.setHint(R.string.patient_treatment_description_hint_text);
        }

        fabEditTreatmentDescripton = view.findViewById(R.id.fabEditTreatmentDescripton);

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        newTreaymentActivity = (TreatmentActivity) getActivity();

        if (newTreaymentActivity != null) {

            Animation fabShowAnimation = AnimationUtils.loadAnimation(newTreaymentActivity, R.anim.fab_show);
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


            if (!newTreaymentActivity.editDisease) {
                fabEditTreatmentDescripton.startAnimation(fabShowAnimation);
            }

            fabHideAnimation = AnimationUtils.loadAnimation(newTreaymentActivity, R.anim.fab_hide);
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
            if (newTreaymentActivity.treatmentDescriptionFragment == null) {
                newTreaymentActivity.initTreatmentDescriptionFragment();
            }

            editTextTreatment.setText(newTreaymentActivity.textTreatment);

            fabEditTreatmentDescripton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    newTreaymentActivity.textInputLayoutDiseaseName.setVisibility(View.VISIBLE);
                    newTreaymentActivity.editTextDiseaseName.setEnabled(true);

                    fabEditTreatmentDescripton.startAnimation(fabHideAnimation);

                    newTreaymentActivity.editDisease = true;

                    newTreaymentActivity.invalidateOptionsMenu();

                    // оставляем только страницу редактирования описания лечения
                    // страницу с фото убираем
                    newTreaymentActivity.categoryAdapter.setPagesCount(1);
                    newTreaymentActivity.viewPager.setAdapter(newTreaymentActivity.categoryAdapter);
                    newTreaymentActivity.tabLayout.setVisibility(View.GONE);

                    editTextTreatment.setFocusable(true);
                    editTextTreatment.setFocusableInTouchMode(true);
                    editTextTreatment.setCursorVisible(true);
                    editTextTreatment.requestFocus();
                    editTextTreatment.setSelection(editTextTreatment.getText().toString().length());

                    // выбор даты делаем видимым
                    newTreaymentActivity.editTextDateOfDisease.setVisibility(View.VISIBLE);

                    newTreaymentActivity.txtTitleDisease.setVisibility(View.GONE);
                }
            });

            if (!newTreaymentActivity.editDisease) {
                editTextTreatment.setFocusable(false);
                editTextTreatment.setFocusableInTouchMode(false);
                editTextTreatment.setCursorVisible(false);

                fabEditTreatmentDescripton.setVisibility(View.VISIBLE);
            }
        }
    }
}
