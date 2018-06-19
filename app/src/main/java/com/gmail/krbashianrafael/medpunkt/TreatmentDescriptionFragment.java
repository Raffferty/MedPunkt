package com.gmail.krbashianrafael.medpunkt;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;

public class TreatmentDescriptionFragment extends Fragment {

    // fab
    private FloatingActionButton fab;

    // Animation fabHideAnimation
    private Animation fabHideAnimation;

    // Animation fabShowAnimation
    private Animation fabShowAnimation;

    private NewTreatmentActivity newTreaymentActivity;

    private MyEditText editTextTreatment;


    public TreatmentDescriptionFragment(){

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.treatment_description_fragment, container, false);

        newTreaymentActivity = (NewTreatmentActivity) getActivity();

        editTextTreatment = rootView.findViewById(R.id.editTextTreatment);
        editTextTreatment.setText(newTreaymentActivity.textTreatment);

        fab = rootView.findViewById(R.id.fabEditTreatmentDescripton);

        fabHideAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.fab_hide);
        fabHideAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                fab.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });

        fabShowAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.fab_show);
        fabShowAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                fab.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                fab.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                fab.setVisibility(View.VISIBLE);
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fab.startAnimation(fabHideAnimation);

                newTreaymentActivity.textInputLayoutDiseaseName.setVisibility(View.VISIBLE);
                newTreaymentActivity.editTextDiseaseName.setEnabled(true);

                newTreaymentActivity.editDisease = false;

                newTreaymentActivity.invalidateOptionsMenu();

                editTextTreatment.setFocusable(true);
                editTextTreatment.setFocusableInTouchMode(true);
                editTextTreatment.setCursorVisible(true);
                editTextTreatment.setSelection(editTextTreatment.getText().toString().length());
                editTextTreatment.requestFocus();

                View viewToShow = newTreaymentActivity.getCurrentFocus();

                if (viewToShow != null) {
                    InputMethodManager imm = (InputMethodManager) newTreaymentActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm != null) {
                        imm.showSoftInput(viewToShow, 0);
                    }
                }
            }
        });

        if (newTreaymentActivity.editDisease) {
            editTextTreatment.setFocusable(false);
            editTextTreatment.setFocusableInTouchMode(false);
            editTextTreatment.setCursorVisible(false);

            fab.startAnimation(fabShowAnimation);
        }

        return rootView;
    }

}
