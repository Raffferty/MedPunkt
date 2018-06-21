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

    private NewTreatmentActivity newTreaymentActivity;

    protected MyEditText editTextTreatment;

    // fabEditTreatmentDescripton
    protected FloatingActionButton fabEditTreatmentDescripton;

    // Animation fabHideAnimation
    private Animation fabHideAnimation;


    public TreatmentDescriptionFragment() {
        // нужен конструктор
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.treatment_description_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        editTextTreatment = view.findViewById(R.id.editTextTreatment);

        fabEditTreatmentDescripton = view.findViewById(R.id.fabEditTreatmentDescripton);

        fabHideAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.fab_hide);
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
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        newTreaymentActivity = (NewTreatmentActivity) getActivity();

        if (newTreaymentActivity != null) {

            editTextTreatment.setText(newTreaymentActivity.textTreatment);

            fabEditTreatmentDescripton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    fabEditTreatmentDescripton.startAnimation(fabHideAnimation);

                    newTreaymentActivity.textInputLayoutDiseaseName.setVisibility(View.VISIBLE);
                    newTreaymentActivity.editTextDiseaseName.setEnabled(true);

                    newTreaymentActivity.editDisease = true;

                    newTreaymentActivity.invalidateOptionsMenu();

                    newTreaymentActivity.categoryAdapter.setPagesCount(1);
                    newTreaymentActivity.viewPager.setAdapter(newTreaymentActivity.categoryAdapter);
                    newTreaymentActivity.tabLayout.setVisibility(View.GONE);

                    editTextTreatment.setFocusable(true);
                    editTextTreatment.setFocusableInTouchMode(true);
                    editTextTreatment.setCursorVisible(true);
                    editTextTreatment.setSelection(editTextTreatment.getText().toString().length());
                    editTextTreatment.requestFocus();
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
