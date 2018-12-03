package com.gmail.krbashianrafael.medpunkt.tablet;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.Guideline;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gmail.krbashianrafael.medpunkt.R;
import com.gmail.krbashianrafael.medpunkt.shared.DiseaseItem;
import com.gmail.krbashianrafael.medpunkt.shared.UserItem;
import com.tsongkha.spinnerdatepicker.DatePicker;
import com.tsongkha.spinnerdatepicker.DatePickerDialog;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.Locale;

public class TabletMainActivity extends AppCompatActivity
        implements DatePickerDialog.OnDateSetListener {

    public TabletUsersFragment tabletUsersFragment;
    public TabletDiseasesFragment tabletDiseasesFragment;
    public TabletTreatmentFragment tabletTreatmentFragment;

    private boolean firstLoad = false;

    // флаг чтоб вернуться к тому виду, в котором был нажат fab редактирования
    public static boolean inWideView = false;

    // позиции выделенных элементов
    public int selectedUser_position = 0;
    public int selectedDisease_position = 0;
    public int selectedTreatmentPhoto_position = 0;

    // эти поля получают свои значения в классе MedProvider в соответствующих методах selectedUser_id
    public static boolean userInserted = false;
    public static long insertedUser_id = 0;
    public static long selectedUser_id = 0;
    public static String userNameAfterInsert = "";

    public static boolean userUpdated = false;
    public static String userNameAfterUpdate = "";

    public static boolean userDeleted = false;

    public static boolean diseaseInserted = false;
    public static long insertedDisease_id = 0;
    public static long selectedDisease_id = 0;

    public static boolean treatmentPhotoInserted = false;
    public static boolean treatmentPhotoDeleted = false;
    public static long insertedTreatmentPhoto_id = 0;
    public static long selectedTreatmentPhoto_id = 0;

    public static boolean diseaseUpdated = false;
    /*public static boolean diseaseDeleted = false;
    public static String diseaseNameAfterUpdate = "";
    public static String diseaseDateAfterUpdate = "";
    public static String diseaseTreatmentAfterUpdate = "";*/

    // это поле берется из TabletDiseasesFragment.
    // если заболеваний нет, то diseasesIsEmpty = true
    public boolean diseasesIsEmpty = false;

    // это поле берется из UsersRecyclerViewAdapter
    public long user_IdInEdit = 0;

// --Commented out by Inspection START (28.10.2018 22:12):
//    // это поле берется из DiseaseURecyclerViewAdapter
//    public long disease_IdInEdit = 0;
// --Commented out by Inspection STOP (28.10.2018 22:12)

    // это поля, которые хранят соответствующие значения String перед редактированием,
    // чтоб при нажатии CANCEL вернуть их обратно
    public String tempTextDiseaseName, tempTextTreatment, tempTextDateOfTreatment = "";

    // эти поля устанавливаются в TabletDiseasesFragment и TreatmentDescriptionFragment
    // при нажатии на fab добавления заболевания или при нажатии на fab редактирования заболевания
    public boolean diseaseAndTreatmentInEdit = false;
    public boolean newDiseaseAndTreatment = false;
    public boolean treatmentOnSavingOrUpdatingOrDeleting = false;

    public TextView tabletUsersWideTitle;
    public TextView tabletDiseasesTitle;
    public TextView tabletTreatmentTitle;

    // LLtabletTreatmentCancelOrSave в себе содержит tabletTreatmentCancel, tabletTreatmentSave
    public LinearLayout LLtabletTreatmentCancelOrSave;
    public FrameLayout tabletTreatmentDelete;

    //private FrameLayout tabletUsersBlurFrame;
    //public FrameLayout tabletDiseasesBlurFrame;
    //private FrameLayout tableTreatmentBlurFrame;
    public FrameLayout tabletUsersFrame;
    //private FrameLayout tabletDiseasesFrame;

    public FrameLayout tabletTreatmentDeleteFrame;
    //private FrameLayout tabletTreatmentSaveFrame;

    //AutoTransition deleteTransition;

    public ViewGroup mSceneRoot;

    public Guideline ver_1_Guideline;
    public Guideline ver_2_Guideline;
    public Guideline ver_3_Guideline;
    public Guideline ver_4_Guideline;

    // это значения для Blur соответствующих фрагментов
    /*public static final int TABLET_USERS_FRAGMENT = 1;
    public static final int TABLET_DISEASES_FRAGMENT = 2;
    public static final int TABLET_TREATMENT_FRAGMENT = 3;*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tablet_main);

        firstLoad = true;
        inWideView = false;
        userInserted = false;
        insertedUser_id = 0;
        selectedUser_id = 0;
        selectedUser_position = 0;
        userNameAfterInsert = "";
        userUpdated = false;
        userNameAfterUpdate = "";
        userDeleted = false;
        diseaseInserted = false;
        insertedDisease_id = 0;
        selectedDisease_id = 0;
        selectedDisease_position = 0;
        diseaseUpdated = false;
        selectedTreatmentPhoto_position = 0;
        treatmentPhotoInserted = false;
        treatmentPhotoDeleted = false;
        insertedTreatmentPhoto_id = 0;
        selectedTreatmentPhoto_id = 0;
        /*diseaseDeleted = false;
        diseaseNameAfterUpdate = "";
        diseaseDateAfterUpdate = "";
        diseaseTreatmentAfterUpdate = "";*/
        diseasesIsEmpty = false;
        user_IdInEdit = 0;
        //disease_IdInEdit = 0;
        tempTextDiseaseName = "";
        tempTextTreatment = "";
        tempTextDateOfTreatment = "";
        diseaseAndTreatmentInEdit = false;
        newDiseaseAndTreatment = false;
        treatmentOnSavingOrUpdatingOrDeleting = false;

        tabletUsersFragment = (TabletUsersFragment)
                getSupportFragmentManager().findFragmentById(R.id.tablet_users_fragment);

        tabletDiseasesFragment = (TabletDiseasesFragment)
                getSupportFragmentManager().findFragmentById(R.id.tablet_diseases_fragment);

        tabletTreatmentFragment = (TabletTreatmentFragment)
                getSupportFragmentManager().findFragmentById(R.id.tablet_treatment_fragment);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        //tabletUsersBlurFrame = findViewById(R.id.tablet_users_blur);
        //tabletDiseasesBlurFrame = findViewById(R.id.tablet_diseases_blur);
        //tableTreatmentBlurFrame = findViewById(R.id.tablet_treatment_blur);

        tabletUsersWideTitle = findViewById(R.id.tablet_users_wide_title);

        //TextView tabletUsersTitle = findViewById(R.id.tablet_users_title);
        /*tabletUsersTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });*/
        tabletUsersFrame = findViewById(R.id.tablet_users_frame);

        tabletDiseasesTitle = findViewById(R.id.tablet_diseases_title);
        //tabletDiseasesFrame = findViewById(R.id.tablet_diseases_frame);

        mSceneRoot = findViewById(R.id.scene_root);

        ver_1_Guideline = findViewById(R.id.ver_1_guideline);
        ver_2_Guideline = findViewById(R.id.ver_2_guideline);
        ver_3_Guideline = findViewById(R.id.ver_3_guideline);
        ver_4_Guideline = findViewById(R.id.ver_4_guideline);

        ver_1_Guideline.setGuidelinePercent(0.1f);
        ver_2_Guideline.setGuidelinePercent(0.9f);
        ver_3_Guideline.setGuidelinePercent(0.9f);
        ver_4_Guideline.setGuidelinePercent(0.9f);

        tabletTreatmentTitle = findViewById(R.id.tablet_treatment_title);

        LLtabletTreatmentCancelOrSave = findViewById(R.id.tablet_treatment_cancel_or_save);

        FrameLayout tabletTreatmentCancel = findViewById(R.id.tablet_treatment_cancel);
        tabletTreatmentCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //newDiseaseAndTreatment = false;
                //diseaseAndTreatmentInEdit = false;

                //if (diseaseAndTreatmentHasNotChanged()) {

                hideSoftInput();

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        cancel();
                    }
                }, 500);

                /*} else {

                    if (treatmentOnSavingOrUpdatingOrDeleting) {
                        return;
                    }

                    showUnsavedChangesDialog(null);
                }*/
            }
        });

        //deleteTransition = new AutoTransition();
        /*deleteTransition.addListener(new Transition.TransitionListener() {
            @Override
            public void onTransitionStart(Transition transition) {


                //TransitionManager.beginDelayedTransition(mSceneRoot);

                //tabletUsersWideTitle.setVisibility(View.GONE);
                *//*tabletUsersWideTitle.setText("");
                LLtabletTreatmentCancelOrSave.setVisibility(View.GONE);
                tabletTreatmentDelete.setVisibility(View.GONE);

                tabletTreatmentFragment.editTextDateOfDisease.setVisibility(View.GONE);
                tabletTreatmentFragment.textInputLayoutDiseaseName.setVisibility(View.GONE);*//*

            }

            @Override
            public void onTransitionEnd(Transition transition) {

                *//*tabletTreatmentFragment.editTextDateOfDisease.setVisibility(View.GONE);
                tabletTreatmentFragment.textInputLayoutDiseaseName.setVisibility(View.GONE);

                LLtabletTreatmentCancelOrSave.setVisibility(View.GONE);
                tabletTreatmentDelete.setVisibility(View.GONE);

                //tabletUsersWideTitle.setVisibility(View.GONE);
                tabletUsersWideTitle.setText("");*//*

                //TransitionManager.beginDelayedTransition(mSceneRoot);

                *//*ver_1_Guideline.setGuidelinePercent(0.0f);
                ver_2_Guideline.setGuidelinePercent(0.5f);
                ver_3_Guideline.setGuidelinePercent(1.0f);
                ver_4_Guideline.setGuidelinePercent(1.0f);*//*

                //tabletUsersWideTitle.setVisibility(View.GONE);

                *//*tabletTreatmentTitle.setText("");
                tabletTreatmentFragment.editTextDiseaseName.setText("");
                tabletTreatmentFragment.editTextDateOfDisease.setText("");
                tabletTreatmentFragment.treatmentDescriptionFragment.editTextTreatment.setText("");*//*

                *//*tabletUsersWideTitle.setText("");
                LLtabletTreatmentCancelOrSave.setVisibility(View.GONE);
                tabletTreatmentDelete.setVisibility(View.GONE);

                tabletTreatmentFragment.editTextDateOfDisease.setVisibility(View.GONE);
                tabletTreatmentFragment.textInputLayoutDiseaseName.setVisibility(View.GONE);*//*

                //Log.d("qazx"," 3 tabLayout.setVisibility(View.INVISIBLE");


                *//*tabletTreatmentFragment.tabLayout.setVisibility(View.INVISIBLE);
                tabletTreatmentFragment.viewPager.setVisibility(View.INVISIBLE);
                tabletTreatmentFragment.treatmentDescriptionFragment.
                        fabEditTreatmentDescripton.setVisibility(View.INVISIBLE);*//*

            }

            @Override
            public void onTransitionCancel(Transition transition) {

            }

            @Override
            public void onTransitionPause(Transition transition) {

            }

            @Override
            public void onTransitionResume(Transition transition) {

            }
        });*/

        tabletTreatmentDeleteFrame = findViewById(R.id.tablet_treatment_delete_frame);
        tabletTreatmentDelete = findViewById(R.id.tablet_treatment_delete);
        tabletTreatmentDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                hideSoftInput();

                // флаг, чтоб клик не работал,
                // пока идет сохранения
                if (treatmentOnSavingOrUpdatingOrDeleting) {
                    return;
                }

                showDeleteConfirmationDialog();
            }
        });

        //tabletTreatmentSaveFrame = findViewById(R.id.tablet_treatment_save_frame);

        FrameLayout tabletTreatmentSave = findViewById(R.id.tablet_treatment_save);
        tabletTreatmentSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (treatmentOnSavingOrUpdatingOrDeleting) {
                    return;
                }

                //TabletDiseasesFragment.diseaseSelected = false;

                hideSoftInput();

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        save();
                    }
                }, 500);
            }
        });
    }

    /*public boolean diseaseAndTreatmentHasNotChanged() {

        String diseaseDate = tabletTreatmentFragment.editTextDateOfDisease.getText().toString();
        if (TextUtils.equals(tabletTreatmentFragment.editTextDateOfDisease.getText(), getString(R.string.disease_date)) &&
                !TextUtils.equals(tempTextDateOfTreatment, getString(R.string.disease_date))) {
            diseaseDate = "";
        }

        if (TextUtils.equals(tabletTreatmentFragment.editTextDiseaseName.getText().toString(), "") &&
                TextUtils.equals(diseaseDate, "") &&
                TextUtils.equals(tabletTreatmentFragment.treatmentDescriptionFragment.editTextTreatment.getText(), "")) {
            return true;
        }

        return TextUtils.equals(tabletTreatmentFragment.editTextDiseaseName.getText().toString(), tempTextDiseaseName) &&
                TextUtils.equals(diseaseDate, tempTextDateOfTreatment) &&
                TextUtils.equals(tabletTreatmentFragment.treatmentDescriptionFragment.editTextTreatment.getText(), tempTextTreatment);
    }*/

    // Диалог "сохранить или выйти без сохранения"
    /*public void showUnsavedChangesDialog(final RecyclerView.ViewHolder holder) {

        hideSoftInput();

        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AlertDialogCustom);
        builder.setMessage(R.string.dialog_msg_unsaved_changes);

        builder.setPositiveButton(R.string.dialog_yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                if (treatmentOnSavingOrUpdatingOrDeleting) {
                    if (dialog != null) {
                        dialog.dismiss();
                    }
                } else {
                    if (dialog != null) {
                        dialog.dismiss();
                    }

                    save();
                }

                if (holder != null) {
                    if (holder instanceof DiseaseRecyclerViewAdapter.DiseaseHolder) {
                        ((DiseaseRecyclerViewAdapter.DiseaseHolder) holder).tabletDiseaseSelected();
                    }
                } else {
                    if (newDiseaseAndTreatment) {
                        tabletDiseasesFragment.onAddDiseaseClicked();
                    }
                }
            }
        });

        builder.setNegativeButton(R.string.dialog_no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }

                if (holder != null) {
                    if (holder instanceof DiseaseRecyclerViewAdapter.DiseaseHolder) {
                        cancel();
                        ((DiseaseRecyclerViewAdapter.DiseaseHolder) holder).tabletDiseaseSelected();
                    }
                } else {
                    cancel();
                    if (newDiseaseAndTreatment) {
                        tabletDiseasesFragment.onAddDiseaseClicked();
                    }
                }


            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();
    }*/

    // Диалог "Удалить заболевание или отменить удаление"
    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.DeleteAlertDialogCustom);
        builder.setMessage(getString(R.string.disease_delete) + " " + tabletTreatmentFragment.editTextDiseaseName.getText() + "?");

        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (treatmentOnSavingOrUpdatingOrDeleting) {
                    if (dialog != null) {
                        dialog.dismiss();
                    }
                } else {

                    if (dialog != null) {
                        dialog.dismiss();
                    }

                    // если подтверждаем удаление заболевания

                    treatmentOnSavingOrUpdatingOrDeleting = true;
                    tabletTreatmentFragment.editDisease = false;

                    TabletMainActivity.inWideView = false;

                    //tabletTreatmentFragment.set_idUser(0);
                    //TabletMainActivity.selectedDisease_id = 0;


                    /*int DiseaseListSize = tabletDiseasesFragment.diseaseRecyclerViewAdapter.
                            getDiseaseList().size();

                    Log.d("ZSXC", "DiseaseListSize = " + DiseaseListSize);

                    DiseaseItem diseaseItem = tabletDiseasesFragment.diseaseRecyclerViewAdapter.
                            getDiseaseList().get(0);

                    Log.d("ZSXC", "diseaseItem.get_diseaseId() = " + diseaseItem.get_diseaseId());
                    Log.d("ZSXC", "diseaseItem.get_diseaseUserId() = " + diseaseItem.get_diseaseUserId());
                    Log.d("ZSXC", "diseaseItem.getDiseaseName() = " + diseaseItem.getDiseaseName());
                    Log.d("ZSXC", "diseaseItem.getDiseaseDate() = " + diseaseItem.getDiseaseDate());
                    Log.d("ZSXC", "diseaseItem.getTreatmentText( = " + diseaseItem.getTreatmentText());

                    tabletTreatmentFragment.set_idDisease(diseaseItem.get_diseaseId());
                    tabletTreatmentFragment.set_idUser(diseaseItem.get_diseaseUserId());
                    tabletTreatmentFragment.setTextDiseaseName(diseaseItem.getDiseaseName());
                    tabletTreatmentFragment.setTextDateOfDisease(diseaseItem.getDiseaseDate());
                    tabletTreatmentFragment.setTextTreatment(diseaseItem.getTreatmentText());*/


                    tabletUsersWideTitle.setVisibility(View.GONE);

                    //tabletTreatmentTitle.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));

                    //TransitionManager.beginDelayedTransition(mSceneRoot, deleteTransition);

                    ver_1_Guideline.setGuidelinePercent(0.0f);
                    ver_2_Guideline.setGuidelinePercent(0.5f);
                    ver_3_Guideline.setGuidelinePercent(1.0f);
                    ver_4_Guideline.setGuidelinePercent(1.0f);

                    tabletTreatmentFragment.editTextDateOfDisease.setVisibility(View.GONE);
                    tabletTreatmentFragment.textInputLayoutDiseaseName.setVisibility(View.GONE);

                    LLtabletTreatmentCancelOrSave.setVisibility(View.GONE);
                    tabletTreatmentDelete.setVisibility(View.GONE);

                    //tabletUsersWideTitle.setVisibility(View.GONE);
                    tabletUsersWideTitle.setText("");

                    /*tabletTreatmentFragment.editTextDateOfDisease.setVisibility(View.GONE);
                    tabletTreatmentFragment.textInputLayoutDiseaseName.setVisibility(View.GONE);

                    LLtabletTreatmentCancelOrSave.setVisibility(View.GONE);
                    tabletTreatmentDelete.setVisibility(View.GONE);

                    tabletUsersWideTitle.setVisibility(View.GONE);
                    tabletUsersWideTitle.setText("");*/



                    /*ver_1_Guideline.setGuidelinePercent(0.0f);
                    ver_2_Guideline.setGuidelinePercent(0.5f);
                    ver_3_Guideline.setGuidelinePercent(1.0f);
                    ver_4_Guideline.setGuidelinePercent(1.0f);*/

                    // удаляем заболевание и связанные фото
                    deleteDiseaseAndTreatmentPhotos();

                    //if (inWideView){
                    //tabletTreatmentFragment.zoomInTabletTreatment.performClick();
                    //}

                    //ver_3_Guideline.setGuidelinePercent(1.00f);
                    //tabletDiseasesFragment.animVerGuideline_2_from_30_to_50.start();
                    //tabletTreatmentFragment.zoomOutTabletTreatment.setVisibility(View.VISIBLE);



                    //blur(TABLET_TREATMENT_FRAGMENT);



                    /*tabletTreatmentFragment.editTextDiseaseName.setEnabled(false);
                    tabletTreatmentFragment.treatmentDescriptionFragment.editTextTreatment.setFocusable(false);
                    tabletTreatmentFragment.treatmentDescriptionFragment.editTextTreatment.setFocusableInTouchMode(false);
                    tabletTreatmentFragment.treatmentDescriptionFragment.editTextTreatment.setCursorVisible(false);*/


                }
            }
        });

        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }

                treatmentOnSavingOrUpdatingOrDeleting = false;
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();
    }

    private void save() {

        // флаг, чтоб клик не работал,
        // пока идет сохранения
        if (treatmentOnSavingOrUpdatingOrDeleting) {
            return;
        }

        treatmentOnSavingOrUpdatingOrDeleting = true;

        tabletTreatmentFragment.focusHolder.requestFocus();
        tabletTreatmentFragment.saveDiseaseAndTreatment();
    }

    private void cancel() {

        // код для показа выделенного пользователя
        if (TabletMainActivity.selectedUser_id != 0) {
            final ArrayList<UserItem> myUsersData = tabletUsersFragment.usersRecyclerViewAdapter.getUsersList();

            if (myUsersData.size() != 0) {

                selectedUser_position = 0;

                for (int i = 0; i < myUsersData.size(); i++) {
                    if (myUsersData.get(i).get_userId() == TabletMainActivity.selectedUser_id) {
                        selectedUser_position = i;
                    }
                }

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        tabletUsersFragment.recyclerUsers.smoothScrollToPosition(selectedUser_position);
                    }
                }, 250);
            }
        }

        // код для показа выделенного заболевания
        if (TabletMainActivity.selectedDisease_id != 0) {
            final ArrayList<DiseaseItem> myDiseasesData = tabletDiseasesFragment.diseaseRecyclerViewAdapter.getDiseaseList();

            if (myDiseasesData.size() != 0) {

                selectedDisease_position = 0;

                for (int i = 0; i < myDiseasesData.size(); i++) {
                    if (myDiseasesData.get(i).get_diseaseId() == TabletMainActivity.selectedDisease_id) {
                        selectedDisease_position = i;
                    }
                }

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        tabletDiseasesFragment.recyclerDiseases.smoothScrollToPosition(selectedDisease_position);
                    }
                }, 500);
            }
        }

        /*if (TabletDiseasesFragment.diseaseSelected) {
            //tabletDiseasesFragment.animVerGuideline_3_from_30_to_60.start();
            //tabletDiseasesFragment.animVerGuideline_3_from_0_to_60.start();
            tabletDiseasesFragment.animVerGuideline_3_from_0_to_60.start();

            //tabletTreatmentFragment.zoomInTabletTreatment.setVisibility(View.VISIBLE);
            tabletTreatmentFragment.zoomOutTabletTreatment.setVisibility(View.VISIBLE);

        } else {
            ver_3_Guideline.setGuidelinePercent(1.00f);
            tabletUsersWideTitle.setVisibility(View.GONE);
            tabletUsersWideTitle.setText("");
            tabletTreatmentTitle.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
            tabletTreatmentFragment.zoomOutTabletTreatment.setVisibility(View.VISIBLE);

            //tabletDiseasesFragment.animVerGuideline_3_from_0_to_100.start();
            tabletDiseasesFragment.animVerGuideline_2_from_30_to_50.start();
        }*/

        tabletTreatmentFragment.editTextDiseaseName.setText(tempTextDiseaseName);
        tabletTreatmentFragment.editTextDateOfDisease.setText(tempTextDateOfTreatment);
        tabletTreatmentFragment.treatmentDescriptionFragment.editTextTreatment.setText(tempTextTreatment);

        tempTextDiseaseName = "";
        tempTextDateOfTreatment = getString(R.string.disease_date);
        tempTextTreatment = "";

        //TransitionManager.beginDelayedTransition(mSceneRoot);

        if (newDiseaseAndTreatment) {
            if (TabletDiseasesFragment.diseaseSelected) {
                //tabletDiseasesFragment.animVerGuideline_3_from_0_to_60.start();
                ver_3_Guideline.setGuidelinePercent(0.60f);
                tabletUsersWideTitle.setVisibility(View.GONE);
                tabletUsersWideTitle.setText("");
                //tabletTreatmentTitle.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));

                tabletTreatmentFragment.zoomOutTabletTreatment.setVisibility(View.VISIBLE);

                tabletTreatmentFragment.treatmentDescriptionFragment.fabEditTreatmentDescripton.startAnimation(
                        tabletTreatmentFragment.fabEditTreatmentDescriptonShowAnimation
                );

            } else {
                // если добавляли новое заболевание при невыделенном другом заболевании
                ver_3_Guideline.setGuidelinePercent(1.00f);
                //tabletDiseasesFragment.animVerGuideline_2_from_30_to_50.start();
                ver_2_Guideline.setGuidelinePercent(0.50f);
                tabletUsersWideTitle.setVisibility(View.GONE);
                tabletUsersWideTitle.setText("");
                //tabletTreatmentTitle.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));

                tabletTreatmentFragment.treatmentDescriptionFragment.fabEditTreatmentDescripton.setVisibility(View.INVISIBLE);
            }
        } else if (!inWideView) {
            //tabletDiseasesFragment.animVerGuideline_3_from_0_to_60.start();
            ver_3_Guideline.setGuidelinePercent(0.60f);
            tabletUsersWideTitle.setVisibility(View.GONE);
            tabletUsersWideTitle.setText("");
            //tabletTreatmentTitle.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));

            tabletTreatmentFragment.zoomOutTabletTreatment.setVisibility(View.VISIBLE);

            tabletTreatmentFragment.treatmentDescriptionFragment.fabEditTreatmentDescripton.startAnimation(
                    tabletTreatmentFragment.fabEditTreatmentDescriptonShowAnimation
            );
        } else {
            tabletTreatmentFragment.zoomInTabletTreatment.setVisibility(View.VISIBLE);

            tabletTreatmentFragment.treatmentDescriptionFragment.fabEditTreatmentDescripton.startAnimation(
                    tabletTreatmentFragment.fabEditTreatmentDescriptonShowAnimation
            );
        }

        hideElementsOnTabletTreatmentFragment();

        if (diseasesIsEmpty) {
            //blur(TABLET_TREATMENT_FRAGMENT);

            tabletTreatmentFragment.tabLayout.setVisibility(View.INVISIBLE);
            tabletTreatmentFragment.viewPager.setVisibility(View.INVISIBLE);

            tabletUsersFragment.fabAddUser.startAnimation(tabletUsersFragment.fabShowAnimation);
            tabletDiseasesFragment.textViewAddDisease.setVisibility(View.VISIBLE);
            tabletDiseasesFragment.textViewAddDisease.startAnimation(tabletDiseasesFragment.fadeInAnimation);
        } else {
            tabletDiseasesFragment.fabAddDisease.startAnimation(tabletDiseasesFragment.fabShowAnimation);
            tabletUsersFragment.fabAddUser.startAnimation(tabletUsersFragment.fabShowAnimation);
        }

        //if (!newDiseaseAndTreatment && !newdiseaseSelected) {
        /*if (!newdiseaseSelected) {
            tabletTreatmentFragment.editTextDiseaseName.setText(tempTextDiseaseName);
            tabletTreatmentFragment.editTextDateOfDisease.setText(tempTextDateOfTreatment);
            tabletTreatmentFragment.treatmentDescriptionFragment.editTextTreatment.setText(tempTextTreatment);
        }*/

        /*if (newDiseaseAndTreatment && diseaseAndTreatmentInEdit){
            tabletDiseasesFragment.onAddDiseaseClicked();
        }*/

        diseaseAndTreatmentInEdit = false;
        newDiseaseAndTreatment = false;
        treatmentOnSavingOrUpdatingOrDeleting = false;
    }

    public void hideElementsOnTabletTreatmentFragment() {
        // скручиваем клавиатуру
        hideSoftInput();

        //if (newDiseaseAndTreatment || diseaseAndTreatmentInEdit) {
        tabletUsersFragment.fabAddUser.startAnimation(tabletUsersFragment.fabShowAnimation);
        //} else {

        /*if (!TabletDiseasesFragment.diseaseSelected) {
            tabletTreatmentTitle.setText("");
            blur(TABLET_TREATMENT_FRAGMENT);
            tabletTreatmentFragment.tabLayout.setVisibility(View.INVISIBLE);
            tabletTreatmentFragment.viewPager.setVisibility(View.INVISIBLE);
            tabletTreatmentFragment.treatmentDescriptionFragment.fabEditTreatmentDescripton.setVisibility(View.INVISIBLE);

        } else {
            tabletTreatmentFragment.tabLayout.setVisibility(View.VISIBLE);
            tabletTreatmentFragment.viewPager.setVisibility(View.VISIBLE);

            tabletTreatmentFragment.treatmentDescriptionFragment.fabEditTreatmentDescripton.startAnimation(
                    tabletTreatmentFragment.fabEditTreatmentDescriptonShowAnimation
            );
        }*/

        LLtabletTreatmentCancelOrSave.setVisibility(View.GONE);
        tabletTreatmentDelete.setVisibility(View.GONE);
        tabletTreatmentFragment.editTextDateOfDisease.setVisibility(View.GONE);
        tabletTreatmentFragment.textInputLayoutDiseaseName.setVisibility(View.GONE);


        tabletTreatmentFragment.tabLayout.setVisibility(View.VISIBLE);
        tabletTreatmentFragment.viewPager.setVisibility(View.VISIBLE);

            /*tabletTreatmentFragment.treatmentDescriptionFragment.fabEditTreatmentDescripton.startAnimation(
                    tabletTreatmentFragment.fabEditTreatmentDescriptonShowAnimation
            );*/
        //}

        tabletTreatmentFragment.editDisease = false;
        tabletTreatmentFragment.editTextDiseaseName.setEnabled(false);
        tabletTreatmentFragment.treatmentDescriptionFragment.editTextTreatment.setFocusable(false);
        tabletTreatmentFragment.treatmentDescriptionFragment.editTextTreatment.setFocusableInTouchMode(false);
        tabletTreatmentFragment.treatmentDescriptionFragment.editTextTreatment.setCursorVisible(false);

        /*tabletTreatmentFragment.zoomOutTabletTreatment.setVisibility(View.VISIBLE);
        tabletTreatmentFragment.zoomInTabletTreatment.setVisibility(View.INVISIBLE);*/
    }


    private void hideSoftInput() {
        // скручиваем клавиатуру (эдесь срабатывает этот метод)
        // hideSoftInput(); не срабатывает
        View viewToHide = TabletMainActivity.this.getCurrentFocus();
        if (viewToHide != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(viewToHide.getWindowToken(), 0);
            }
        }
    }

    private void deleteDiseaseAndTreatmentPhotos() {
        // Инициализируем Loader для загрузки строк из таблицы treatmentPhotos,
        // которые будут удаляться вместе с удалением заболевания из таблицы diseases
        // кроме того, после удаления строк из таблиц treatmentPhotos и diseases будут удаляться соответствующие фото
        tabletTreatmentFragment.initLoaderToDiseaseAndTreatmentPhotos();
    }

    @Override
    protected void onResume() {
        super.onResume();

        // если клавиатура была открыта для редактирования названия заболевания или текста лечения, то она снова откроется
        // если нет - то не откроется
        /*if (tabletTreatmentFragment != null && tabletTreatmentFragment.treatmentDescriptionFragment != null) {
            if (tabletTreatmentFragment.treatmentDescriptionFragment.editTextTreatment.hasFocus() ||
                    tabletTreatmentFragment.editTextDiseaseName.hasFocus()) {

                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
            } else {
                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
            }
        }*/

        boolean usersLoaded = false;

        if (firstLoad || userInserted || userUpdated || userDeleted) {
            // если просто смотрели на карточку юзера (без изменений), то и грузить не надо
            // иначе, загружаем данные в окно tabletUsersFragment
            tabletUsersFragment.initUsersLoader();

            usersLoaded = true;
            firstLoad = false;
        }

        if (!usersLoaded) {
            float percentVer_2 = ((ConstraintLayout.LayoutParams) ver_2_Guideline.getLayoutParams()).guidePercent;

            if (percentVer_2 != 0.90f) {
                tabletDiseasesFragment.initDiseasesLoader();
            }

            // код для показа выделенного пользователя

            final ArrayList<UserItem> myUsersData = tabletUsersFragment.usersRecyclerViewAdapter.getUsersList();

            if (myUsersData.size() != 0) {

                selectedUser_position = 0;

                if (TabletMainActivity.selectedUser_id != 0) {

                    for (int i = 0; i < myUsersData.size(); i++) {
                        if (myUsersData.get(i).get_userId() == TabletMainActivity.selectedUser_id) {
                            selectedUser_position = i;
                        }
                    }
                }

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        tabletUsersFragment.recyclerUsers.smoothScrollToPosition(selectedUser_position);
                    }
                }, 500);
            }

            /*if (diseaseInserted || diseaseUpdated || diseaseDeleted) {
                // если просто смотрели на карточку заболевания (без изменений), то и грузить не надо
                // иначе, загружаем данные в окно tabletDiseasesFragment
                tabletDiseasesFragment.initDiseasesLoader();
            } else {*/
            // код для показа выделенного заболевания
                /*final ArrayList<DiseaseItem> myData = tabletDiseasesFragment.diseaseRecyclerViewAdapter.getDiseaseList();

                if (myData.size() != 0) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            int pos = 0;

                            if (TabletMainActivity.selectedDisease_id != 0) {
                                for (int i = 0; i < myData.size(); i++) {
                                    if (myData.get(i).get_diseaseId() == TabletMainActivity.selectedDisease_id) {
                                        TabletMainActivity.selectedDisease_position = i;
                                    }
                                }
                            }

                            Log.d("xxxx", " Onres TabletMainActivity.selectedDisease_id = " + TabletMainActivity.selectedDisease_id);

                            tabletDiseasesFragment.recyclerDiseases.smoothScrollToPosition(TabletMainActivity.selectedDisease_position);
                        }
                    }, 1000);*/
            //}
            //}

        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // убираем клавиатуру
        View viewToHide = this.getCurrentFocus();
        if (viewToHide != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(viewToHide.getWindowToken(), 0);
            }
        }
    }


    // слушатель по установке даты для Build.VERSION_CODES.LOLIPOP
    @SuppressLint("SetTextI18n")
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
        GregorianCalendar date = new GregorianCalendar(year, monthOfYear, dayOfMonth);
        tabletTreatmentFragment.editTextDateOfDisease.setText(simpleDateFormat.format(date.getTime()) + " ");
    }

    /*public void blur(int fragmentNumber) {

        switch (fragmentNumber) {
            case TABLET_USERS_FRAGMENT:
                if (!tabletUsersBlurFrame.isClickable()) {

                    tabletUsersBlurFrame.setClickable(true);
                    tabletUsersBlurFrame.setBackgroundColor(getResources().getColor(R.color.my_gray));
                }
                break;
            case TABLET_DISEASES_FRAGMENT:
                if (!tabletDiseasesBlurFrame.isClickable()) {

                    tabletDiseasesBlurFrame.setClickable(true);
                    tabletDiseasesBlurFrame.setBackgroundColor(getResources().getColor(R.color.my_gray));
                }
                break;
            case TABLET_TREATMENT_FRAGMENT:
                if (!tableTreatmentBlurFrame.isClickable()) {

                    tableTreatmentBlurFrame.setClickable(true);
                    tableTreatmentBlurFrame.setBackgroundColor(getResources().getColor(R.color.my_gray));
                }
                break;
            default:
                break;
        }
    }*/

    /*public void unBlur(int fragmentNumber) {

        switch (fragmentNumber) {
            case TABLET_USERS_FRAGMENT:
                if (tabletUsersBlurFrame.isClickable()) {

                    tabletUsersBlurFrame.setClickable(false);
                    tabletUsersBlurFrame.setBackgroundColor(Color.TRANSPARENT);
                }
                break;
            case TABLET_DISEASES_FRAGMENT:
                if (tabletDiseasesBlurFrame.isClickable()) {

                    tabletDiseasesBlurFrame.setClickable(false);
                    tabletDiseasesBlurFrame.setBackgroundColor(Color.TRANSPARENT);
                }
                break;
            case TABLET_TREATMENT_FRAGMENT:
                if (tableTreatmentBlurFrame.isClickable()) {

                    tableTreatmentBlurFrame.setClickable(false);
                    tableTreatmentBlurFrame.setBackgroundColor(Color.TRANSPARENT);
                }
                break;
            default:
                break;
        }
    }*/
}
