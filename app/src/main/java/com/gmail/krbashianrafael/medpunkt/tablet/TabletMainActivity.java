package com.gmail.krbashianrafael.medpunkt.tablet;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
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

    public final Handler myTabletHandler = new Handler(Looper.getMainLooper());


    public TabletUsersFragment tabletUsersFragment;
    public TabletDiseasesFragment tabletDiseasesFragment;
    public TabletTreatmentFragment tabletTreatmentFragment;

    private boolean firstLoad = false;

    public static boolean inWideView = false;

    // позиции выделенных элементов
    public int selectedUser_position = 0;
    public int selectedDisease_position = 0;

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

    // это поле берется из TabletDiseasesFragment.
    // если заболеваний нет, то diseasesIsEmpty = true
    public boolean diseasesIsEmpty = false;

    // это поле берется из UsersRecyclerViewAdapter
    public long user_IdInEdit = 0;

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

    public FrameLayout tabletUsersFrame;

    public FrameLayout tabletTreatmentDeleteFrame;

    public ViewGroup mSceneRoot;

    public Guideline ver_1_Guideline;
    public Guideline ver_2_Left_Guideline;
    public Guideline ver_2_Right_Guideline;
    public Guideline ver_3_Guideline;
    public Guideline ver_4_Guideline;

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
        treatmentPhotoInserted = false;
        treatmentPhotoDeleted = false;
        insertedTreatmentPhoto_id = 0;
        selectedTreatmentPhoto_id = 0;
        diseasesIsEmpty = false;
        user_IdInEdit = 0;
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

        tabletUsersWideTitle = findViewById(R.id.tablet_users_wide_title);

        tabletUsersFrame = findViewById(R.id.tablet_users_frame);

        tabletDiseasesTitle = findViewById(R.id.tablet_diseases_title);

        mSceneRoot = findViewById(R.id.scene_root);

        ver_1_Guideline = findViewById(R.id.ver_1_guideline);
        ver_2_Left_Guideline = findViewById(R.id.ver_2_Left_guideline);
        ver_2_Right_Guideline = findViewById(R.id.ver_2_Right_guideline);
        ver_3_Guideline = findViewById(R.id.ver_3_guideline);
        ver_4_Guideline = findViewById(R.id.ver_4_guideline);

        ver_1_Guideline.setGuidelinePercent(0.1f);
        ver_2_Left_Guideline.setGuidelinePercent(0.9f);
        ver_2_Right_Guideline.setGuidelinePercent(0.9f);
        ver_3_Guideline.setGuidelinePercent(0.9f);
        ver_4_Guideline.setGuidelinePercent(0.9f);

        tabletTreatmentTitle = findViewById(R.id.tablet_treatment_title);

        LLtabletTreatmentCancelOrSave = findViewById(R.id.tablet_treatment_cancel_or_save);

        FrameLayout tabletTreatmentCancel = findViewById(R.id.tablet_treatment_cancel);
        tabletTreatmentCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                hideSoftInput();

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        cancel();
                    }
                }, 500);

            }
        });

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

        FrameLayout tabletTreatmentSave = findViewById(R.id.tablet_treatment_save);
        tabletTreatmentSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (treatmentOnSavingOrUpdatingOrDeleting) {
                    return;
                }

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

                    tabletUsersWideTitle.setVisibility(View.GONE);

                    ver_1_Guideline.setGuidelinePercent(0.0f);
                    ver_2_Left_Guideline.setGuidelinePercent(0.5f);
                    ver_2_Right_Guideline.setGuidelinePercent(0.5f);
                    ver_3_Guideline.setGuidelinePercent(1.0f);
                    ver_4_Guideline.setGuidelinePercent(1.0f);

                    tabletTreatmentFragment.editTextDateOfDisease.setVisibility(View.GONE);
                    tabletTreatmentFragment.textInputLayoutDiseaseName.setVisibility(View.GONE);

                    LLtabletTreatmentCancelOrSave.setVisibility(View.GONE);
                    tabletTreatmentDelete.setVisibility(View.GONE);

                    tabletUsersWideTitle.setText("");

                    // удаляем заболевание и связанные фото
                    deleteDiseaseAndTreatmentPhotos();
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

        tabletTreatmentFragment.editTextDiseaseName.setText(tempTextDiseaseName);
        tabletTreatmentFragment.editTextDateOfDisease.setText(tempTextDateOfTreatment);
        tabletTreatmentFragment.treatmentDescriptionFragment.editTextTreatment.setText(tempTextTreatment);

        tempTextDiseaseName = "";
        tempTextDateOfTreatment = getString(R.string.disease_date);
        tempTextTreatment = "";

        if (newDiseaseAndTreatment) {
            if (TabletDiseasesFragment.diseaseSelected) {

                ver_3_Guideline.setGuidelinePercent(0.60f);
                tabletUsersWideTitle.setVisibility(View.GONE);
                tabletUsersWideTitle.setText("");

                tabletTreatmentFragment.zoomOutTabletTreatment.setVisibility(View.VISIBLE);

                tabletTreatmentFragment.treatmentDescriptionFragment.fabEditTreatmentDescripton.startAnimation(
                        tabletTreatmentFragment.fabEditTreatmentDescriptonShowAnimation
                );

            } else {
                // если добавляли новое заболевание при невыделенном другом заболевании
                ver_3_Guideline.setGuidelinePercent(1.00f);
                ver_2_Left_Guideline.setGuidelinePercent(0.50f);
                ver_2_Right_Guideline.setGuidelinePercent(0.50f);
                tabletUsersWideTitle.setVisibility(View.GONE);
                tabletUsersWideTitle.setText("");

                tabletTreatmentFragment.treatmentDescriptionFragment.fabEditTreatmentDescripton.setVisibility(View.INVISIBLE);
            }
        } else if (!inWideView) {

            ver_3_Guideline.setGuidelinePercent(0.60f);
            tabletUsersWideTitle.setVisibility(View.GONE);
            tabletUsersWideTitle.setText("");

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

            tabletTreatmentFragment.tabLayout.setVisibility(View.INVISIBLE);
            tabletTreatmentFragment.viewPager.setVisibility(View.INVISIBLE);

            tabletUsersFragment.fabAddUser.startAnimation(tabletUsersFragment.fabShowAnimation);
            tabletDiseasesFragment.textViewAddDisease.setVisibility(View.VISIBLE);
            tabletDiseasesFragment.textViewAddDisease.startAnimation(tabletDiseasesFragment.fadeInAnimation);

        } else {
            tabletDiseasesFragment.fabAddDisease.startAnimation(tabletDiseasesFragment.fabShowAnimation);
            tabletUsersFragment.fabAddUser.startAnimation(tabletUsersFragment.fabShowAnimation);
        }

        diseaseAndTreatmentInEdit = false;
        newDiseaseAndTreatment = false;
        treatmentOnSavingOrUpdatingOrDeleting = false;
    }

    public void hideElementsOnTabletTreatmentFragment() {
        // скручиваем клавиатуру
        hideSoftInput();

        tabletUsersFragment.fabAddUser.startAnimation(tabletUsersFragment.fabShowAnimation);

        LLtabletTreatmentCancelOrSave.setVisibility(View.GONE);
        tabletTreatmentDelete.setVisibility(View.GONE);
        tabletTreatmentFragment.editTextDateOfDisease.setVisibility(View.GONE);
        tabletTreatmentFragment.textInputLayoutDiseaseName.setVisibility(View.GONE);


        tabletTreatmentFragment.tabLayout.setVisibility(View.VISIBLE);
        tabletTreatmentFragment.viewPager.setVisibility(View.VISIBLE);

        tabletTreatmentFragment.editDisease = false;
        tabletTreatmentFragment.editTextDiseaseName.setEnabled(false);
        tabletTreatmentFragment.treatmentDescriptionFragment.editTextTreatment.setFocusable(false);
        tabletTreatmentFragment.treatmentDescriptionFragment.editTextTreatment.setFocusableInTouchMode(false);
        tabletTreatmentFragment.treatmentDescriptionFragment.editTextTreatment.setCursorVisible(false);

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

        boolean usersLoaded = false;

        if (firstLoad || userInserted || userUpdated || userDeleted) {
            // если просто смотрели на карточку юзера (без изменений), то и грузить не надо
            // иначе, загружаем данные в окно tabletUsersFragment
            tabletUsersFragment.initUsersLoader();

            usersLoaded = true;
            firstLoad = false;
        }

        if (!usersLoaded) {
            float percentVer_2 = ((ConstraintLayout.LayoutParams) ver_2_Left_Guideline.getLayoutParams()).guidePercent;

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
}
