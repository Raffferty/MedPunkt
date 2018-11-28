package com.gmail.krbashianrafael.medpunkt.shared;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;

import com.gmail.krbashianrafael.medpunkt.MyEditText;
import com.gmail.krbashianrafael.medpunkt.R;
import com.gmail.krbashianrafael.medpunkt.phone.TreatmentActivity;
import com.gmail.krbashianrafael.medpunkt.tablet.TabletMainActivity;

public class TreatmentDescriptionFragment extends Fragment {

    // кастомный EditText у которого клавиатура не перекрывает текст
    public MyEditText editTextTreatment;

    // fabEditTreatmentDescripton
    public FloatingActionButton fabEditTreatmentDescripton;

    public Animation fabHideAnimation;

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
        TreatmentActivity mTreatmentActivity;
        TabletMainActivity mTabletMainActivity;

        if (getActivity() instanceof TabletMainActivity) {
            mTabletMainActivity = (TabletMainActivity) getActivity();
            doWorkWithTabletMainActivity(mTabletMainActivity);
        } else {
            mTreatmentActivity = (TreatmentActivity) getActivity();
            doWorkWithTreatmentActivity(mTreatmentActivity);
        }
    }

    private void doWorkWithTabletMainActivity(final TabletMainActivity mTabletMainActivity) {
        if (mTabletMainActivity != null) {

            // в главном активити инициализируем фрагмент (есл он еще не инициализирован,
            // т.е. если он еще null)
            if (mTabletMainActivity.tabletTreatmentFragment.treatmentDescriptionFragment == null) {
                mTabletMainActivity.tabletTreatmentFragment.initTreatmentDescriptionFragment();
            }

            fabHideAnimation = AnimationUtils.loadAnimation(mTabletMainActivity, R.anim.fab_hide);
            fabHideAnimation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    fabEditTreatmentDescripton.setVisibility(View.INVISIBLE);
                    mTabletMainActivity.tabletDiseasesFragment.fabAddDisease.setVisibility(View.INVISIBLE);
                    mTabletMainActivity.tabletUsersFragment.fabAddUser.setVisibility(View.INVISIBLE);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }
            });

            editTextTreatment.setText(mTabletMainActivity.tabletTreatmentFragment.textTreatment);

            fabEditTreatmentDescripton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    mTabletMainActivity.diseaseAndTreatmentInEdit = true;
                    fabEditTreatmentDescripton.startAnimation(fabHideAnimation);
                    mTabletMainActivity.tabletDiseasesFragment.fabAddDisease.startAnimation(fabHideAnimation);
                    mTabletMainActivity.tabletUsersFragment.fabAddUser.startAnimation(fabHideAnimation);


                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {

                            //TransitionManager.beginDelayedTransition(mTabletMainActivity.mSceneRoot);

                            //mTabletMainActivity.tabletTreatmentFragment.textInputLayoutDiseaseName.setVisibility(View.VISIBLE);
                            mTabletMainActivity.tabletTreatmentFragment.tabLayout.setVisibility(View.GONE);

                            mTabletMainActivity.tabletTreatmentFragment.zoomOutTabletTreatment.setVisibility(View.INVISIBLE);
                            mTabletMainActivity.tabletTreatmentFragment.zoomInTabletTreatment.setVisibility(View.INVISIBLE);


                            // на планшете показываем клавиатуру
                            /*InputMethodManager imm = (InputMethodManager)
                                    mTabletMainActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
                            if (imm != null) {
                                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
                            }*/

                            mTabletMainActivity.tempTextDiseaseName = mTabletMainActivity.tabletTreatmentFragment.editTextDiseaseName.getText().toString();
                            mTabletMainActivity.tempTextDateOfTreatment = mTabletMainActivity.tabletTreatmentFragment.editTextDateOfDisease.getText().toString();
                            mTabletMainActivity.tempTextTreatment = mTabletMainActivity.tabletTreatmentFragment.treatmentDescriptionFragment.editTextTreatment.getText().toString();

                            mTabletMainActivity.tabletTreatmentFragment.editDisease = true;
                            //mTabletMainActivity.tabletTreatmentFragment.editTextDiseaseName.setEnabled(true);
                            //editTextTreatment.setFocusable(true);
                            //editTextTreatment.setFocusableInTouchMode(true);
                            //editTextTreatment.setCursorVisible(true);
                            editTextTreatment.requestFocus();
                            editTextTreatment.setSelection(editTextTreatment.getText().toString().length());

                            //mTabletMainActivity.tabletTreatmentFragment.editTextDateOfDisease.setVisibility(View.VISIBLE);
                            //mTabletMainActivity.tabletTreatmentTitle.setVisibility(View.INVISIBLE);
                            //mTabletMainActivity.LLtabletTreatmentCancelOrSave.setVisibility(View.VISIBLE);
                            mTabletMainActivity.tabletTreatmentDeleteFrame.setVisibility(View.VISIBLE);
                            mTabletMainActivity.tabletTreatmentDelete.setVisibility(View.VISIBLE);

                            //mTabletMainActivity.ver_3_Guideline.setGuidelinePercent(0.30f);
                            //float percentVerGuideline_3 = ((ConstraintLayout.LayoutParams) mTabletMainActivity.ver_3_Guideline.getLayoutParams()).guidePercent;

                            //if (percentVerGuideline_3==0.00f){
                            if (TabletMainActivity.inWideView) {
                                //mTabletMainActivity.inWideView = true;

                                mTabletMainActivity.tabletUsersWideTitle.setText(mTabletMainActivity.tabletDiseasesTitle.getText().toString());
                                mTabletMainActivity.tabletUsersWideTitle.setVisibility(View.VISIBLE);
                                //mTabletMainActivity.tabletTreatmentTitle.setBackgroundColor(getResources().getColor(R.color.blue));

                                mTabletMainActivity.LLtabletTreatmentCancelOrSave.setVisibility(View.VISIBLE);
                                mTabletMainActivity.tabletTreatmentFragment.textInputLayoutDiseaseName.setVisibility(View.VISIBLE);
                                mTabletMainActivity.tabletTreatmentFragment.editTextDateOfDisease.setVisibility(View.VISIBLE);

                                mTabletMainActivity.tabletTreatmentFragment.editTextDiseaseName.setEnabled(true);
                                mTabletMainActivity.tabletTreatmentFragment.editTextDiseaseName.requestFocus();

                                mTabletMainActivity.tabletTreatmentFragment.treatmentDescriptionFragment.editTextTreatment.setFocusable(true);
                                mTabletMainActivity.tabletTreatmentFragment.treatmentDescriptionFragment.editTextTreatment.setFocusableInTouchMode(true);
                                mTabletMainActivity.tabletTreatmentFragment.treatmentDescriptionFragment.editTextTreatment.setCursorVisible(true);

                            } else {
                                //mTabletMainActivity.inWideView = false;

                                //mTabletMainActivity.tabletDiseasesFragment.animVerGuideline_3_from_60_to_0.start();

                                mTabletMainActivity.ver_3_Guideline.setGuidelinePercent(0.00f);

                                mTabletMainActivity.tabletUsersWideTitle.setText(mTabletMainActivity.tabletDiseasesTitle.getText().toString());
                                mTabletMainActivity.tabletUsersWideTitle.setVisibility(View.VISIBLE);
                                //mTabletMainActivity.tabletTreatmentTitle.setBackgroundColor(getResources().getColor(R.color.blue));

                                mTabletMainActivity.LLtabletTreatmentCancelOrSave.setVisibility(View.VISIBLE);
                                mTabletMainActivity.tabletTreatmentFragment.textInputLayoutDiseaseName.setVisibility(View.VISIBLE);
                                mTabletMainActivity.tabletTreatmentFragment.editTextDateOfDisease.setVisibility(View.VISIBLE);

                                mTabletMainActivity.tabletTreatmentFragment.editTextDiseaseName.setEnabled(true);
                                mTabletMainActivity.tabletTreatmentFragment.editTextDiseaseName.requestFocus();

                                mTabletMainActivity.tabletTreatmentFragment.treatmentDescriptionFragment.editTextTreatment.setFocusable(true);
                                mTabletMainActivity.tabletTreatmentFragment.treatmentDescriptionFragment.editTextTreatment.setFocusableInTouchMode(true);
                                mTabletMainActivity.tabletTreatmentFragment.treatmentDescriptionFragment.editTextTreatment.setCursorVisible(true);

                                /*mTabletMainActivity.ver_3_Guideline.setGuidelinePercent(0.00f);
                                mTabletMainActivity.tabletUsersWideTitle.setText(mTabletMainActivity.tabletDiseasesTitle.getText().toString());
                                mTabletMainActivity.tabletUsersWideTitle.setVisibility(View.VISIBLE);
                                mTabletMainActivity.tabletTreatmentTitle.setBackgroundColor(getResources().getColor(R.color.blue));*/

                                TabletMainActivity.inWideView = true;

                                // если есть фото лечения, то в расширенном виде формируем вид окна
                                // и загружаем фото первой позиции
                                //if (treatmentPhotosFragment.txtAddPhotos.getVisibility() != View.VISIBLE) {
                                if ( mTabletMainActivity.tabletTreatmentFragment.treatmentPhotosFragment.treatmentPhotoRecyclerViewAdapter.
                                        getTreatmentPhotosList().size() != 0) {

                                    mTabletMainActivity.tabletTreatmentFragment.treatmentPhotosFragment.verGuideline.setGuidelinePercent(0.4f);
                                    mTabletMainActivity.tabletTreatmentFragment.treatmentPhotosFragment.fabToFullScreen.setVisibility(View.VISIBLE);
                                    /*mTabletMainActivity.tabletTreatmentFragment.treatmentPhotosFragment.fabToFullScreen.
                                            startAnimation(mTabletMainActivity.tabletTreatmentFragment.fabShowAnimation);*/

                                    // это расширяет таб "снимки"
                                    LinearLayout layout = ((LinearLayout) ((LinearLayout)  mTabletMainActivity.tabletTreatmentFragment.tabLayout.
                                            getChildAt(0)).getChildAt(1));
                                    LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) layout.getLayoutParams();
                                    layoutParams.weight = 1.50f;
                                    layout.setLayoutParams(layoutParams);

                                    // получаем данные из первой позиции и грузим фото
                                    TreatmentPhotoItem treatmentPhotoItem =  mTabletMainActivity.tabletTreatmentFragment.treatmentPhotosFragment.
                                            treatmentPhotoRecyclerViewAdapter.getTreatmentPhotosList().get(0);

                                    mTabletMainActivity.tabletTreatmentFragment.treatmentPhotosFragment._idTrPhoto = treatmentPhotoItem.get_trPhotoId();
                                    mTabletMainActivity.tabletTreatmentFragment.treatmentPhotosFragment.treatmentPhotoFilePath = treatmentPhotoItem.getTrPhotoUri();
                                    mTabletMainActivity.tabletTreatmentFragment.treatmentPhotosFragment.textDateOfTreatmentPhoto = treatmentPhotoItem.getTrPhotoDate();
                                    mTabletMainActivity.tabletTreatmentFragment.treatmentPhotosFragment.textPhotoDescription = treatmentPhotoItem.getTrPhotoName();

                                    // код для выделения первого элемента фото заболевания и его загрузки в imgWideView
                                    TabletMainActivity.selectedTreatmentPhoto_id =  mTabletMainActivity.tabletTreatmentFragment.treatmentPhotosFragment._idTrPhoto;

                                    mTabletMainActivity.tabletTreatmentFragment.treatmentPhotosFragment.
                                            treatmentPhotoRecyclerViewAdapter.notifyDataSetChanged();

                                    // загрузка фото происходит в notifyDataSetChanged() в TransitionListener
                                }


                            }
                        }
                    }, 500);

                    /*Intent treatmentIntent = new Intent(mTabletMainActivity, TreatmentActivity.class);
                    treatmentIntent.putExtra("_idDisease", mTabletMainActivity.tabletTreatmentFragment._idDisease);
                    treatmentIntent.putExtra("_idUser", mTabletMainActivity.tabletTreatmentFragment._idUser);
                    treatmentIntent.putExtra("editDisease", true);
                    treatmentIntent.putExtra("diseaseName", mTabletMainActivity.tabletTreatmentFragment.textDiseaseName);
                    treatmentIntent.putExtra("diseaseDate", mTabletMainActivity.tabletTreatmentFragment.textDateOfDisease);
                    treatmentIntent.putExtra("textTreatment", mTabletMainActivity.tabletTreatmentFragment.textTreatment);

                    mTabletMainActivity.startActivity(treatmentIntent);*/
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

    private void doWorkWithTreatmentActivity(final TreatmentActivity mTreatmentActivity) {
        if (mTreatmentActivity != null) {

            // в главном активити инициализируем фрагмент (есл он еще не инициализирован, т.е. если он еще null)
            if (mTreatmentActivity.treatmentDescriptionFragment == null) {
                mTreatmentActivity.initTreatmentDescriptionFragment();
            }

            editTextTreatment.setText(mTreatmentActivity.textTreatment);

            if (HomeActivity.isTablet && !mTreatmentActivity.newDisease) {
                editTextTreatment.setSelection(mTreatmentActivity.textTreatment.length());

                editTextTreatment.requestFocus();
            }

            Animation fabShowAnimation = AnimationUtils.loadAnimation(mTreatmentActivity, R.anim.fab_show);
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

            fabHideAnimation = AnimationUtils.loadAnimation(mTreatmentActivity, R.anim.fab_hide);
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

            fabEditTreatmentDescripton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    mTreatmentActivity.textInputLayoutDiseaseName.setVisibility(View.VISIBLE);
                    mTreatmentActivity.editTextDiseaseName.setEnabled(true);

                    mTreatmentActivity.editDisease = true;

                    mTreatmentActivity.invalidateOptionsMenu();

                    // оставляем только страницу редактирования описания лечения
                    // страницу с фото убираем
                    mTreatmentActivity.tabLayout.setVisibility(View.GONE);

                    editTextTreatment.setFocusable(true);
                    editTextTreatment.setFocusableInTouchMode(true);
                    editTextTreatment.setCursorVisible(true);
                    editTextTreatment.requestFocus();
                    editTextTreatment.setSelection(editTextTreatment.getText().toString().length());

                    fabEditTreatmentDescripton.startAnimation(fabHideAnimation);

                    // показываем клавиатуру если это планшет
                    if (HomeActivity.isTablet) {
                        InputMethodManager imm = (InputMethodManager)
                                mTreatmentActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
                        if (imm != null) {
                            imm.showSoftInput(editTextTreatment, InputMethodManager.SHOW_IMPLICIT);
                        }
                    }

                    // выбор даты делаем видимым
                    mTreatmentActivity.editTextDateOfDisease.setVisibility(View.VISIBLE);

                    mTreatmentActivity.txtTitleDisease.setVisibility(View.GONE);
                }
            });

            if (!mTreatmentActivity.editDisease) {
                editTextTreatment.setFocusable(false);
                editTextTreatment.setFocusableInTouchMode(false);
                editTextTreatment.setCursorVisible(false);

                fabEditTreatmentDescripton.startAnimation(fabShowAnimation);
            }
        }
    }
}
