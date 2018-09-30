package com.gmail.krbashianrafael.medpunkt.tablet;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gmail.krbashianrafael.medpunkt.R;
import com.tsongkha.spinnerdatepicker.DatePicker;
import com.tsongkha.spinnerdatepicker.DatePickerDialog;

import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;
import java.util.Locale;

public class TabletMainActivity extends AppCompatActivity
        implements DatePickerDialog.OnDateSetListener {

    public TabletUsersFragment tabletUsersFragment;
    public TabletDiseasesFragment tabletDiseasesFragment;
    public TabletTreatmentFragment tabletTreatmentFragment;

    private boolean firstLoad = false;

    // эти поля получают свои значения в классе MedProvider в соответствующих методах
    public static boolean userInserted = false;
    public static boolean userUpdated = false;
    public static boolean userDeleted = false;
    public static String userNameAfterUpdate = "";

    public static boolean diseaseInserted = false;
    public static boolean diseaseUpdated = false;
    public static boolean diseaseDeleted = false;
    public static String diseaseNameAfterUpdate = "";
    public static String diseaseDateAfterUpdate = "";
    public static String diseaseTreatmentAfterUpdate = "";

    // это поле берется из TabletDiseasesFragment.
    // если заболеваний нет, то diseasesIsEmpty = true
    public static boolean diseasesIsEmpty = false;

    // это поле берется из UsersRecyclerViewAdapter
    public static long user_IdInEdit = 0;

    // это поле берется из DiseaseURecyclerViewAdapter
    public static long disease_IdInEdit = 0;

    // это поля, которые хранят соответствующие значения String перед редактированием,
    // чтоб при нажатии CANCEL вернуть их обратно
    public static String tempTextDiseaseName, tempTextTreatment, tempTextDateOfTreatment = "";

    // эти поля устанавливаются в TabletDiseasesFragment и TreatmentDescriptionFragment
    // при нажатии на fab добавления заболевания или при нажатии на fab редактирования заболевания
    public static boolean diseaseAndTreatmentInEdit = false;
    public static boolean newDiseaseAndTreatment = false;
    public boolean treatmentOnSavingOrUpdatingOrDeleting = false;

    public TextView tabletUsersTitle, tabletDiseasesTitle, tabletTreatmentTitle;

    // LLtabletTreatmentCancelOrSave в себе содержит tabletTreatmentCancel, tabletTreatmentSave
    public LinearLayout LLtabletTreatmentCancelOrSave;
    public TextView tabletTreatmentCancel, tabletTreatmentSave, tabletTreatmentDelete;

    private FrameLayout tabletUsersBlurFrame, tabletDiseasesBlurFrame, tableTreatmentBlurFrame;

    // это значения для Blur соответствующих фрагментов
    public static final int TABLET_USERS_FRAGMENT = 1;
    public static final int TABLET_DISEASES_FRAGMENT = 2;
    public static final int TABLET_TREATMENT_FRAGMENT = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tablet_main);

        firstLoad = true;
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

        tabletUsersBlurFrame = findViewById(R.id.tablet_users_blur);
        tabletDiseasesBlurFrame = findViewById(R.id.tablet_diseases_blur);
        tableTreatmentBlurFrame = findViewById(R.id.tablet_treatment_blur);

        tabletUsersTitle = findViewById(R.id.tablet_users_title);
        tabletUsersTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        tabletDiseasesTitle = findViewById(R.id.tablet_diseases_title);
        tabletTreatmentTitle = findViewById(R.id.tablet_treatment_title);

        LLtabletTreatmentCancelOrSave = findViewById(R.id.tablet_treatment_cancel_or_save);

        tabletTreatmentCancel = findViewById(R.id.tablet_treatment_cancel);
        tabletTreatmentCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (TextUtils.equals(
                        tabletDiseasesFragment.txtTabletDiseases.getText(),
                        getString(R.string.tablet_treatment_select_disease))) {

                    blur(TABLET_TREATMENT_FRAGMENT);
                    tabletTreatmentFragment.tabLayout.setVisibility(View.INVISIBLE);
                    tabletTreatmentFragment.viewPager.setVisibility(View.INVISIBLE);
                    tabletTreatmentFragment.treatmentDescriptionFragment.fabEditTreatmentDescripton.setVisibility(View.INVISIBLE);

                } else {
                    tabletTreatmentFragment.tabLayout.setVisibility(View.VISIBLE);
                    tabletTreatmentFragment.viewPager.setVisibility(View.VISIBLE);
                }

                // скручиваем клавиатуру
                hideSoftInput();

                LLtabletTreatmentCancelOrSave.setVisibility(View.GONE);
                tabletTreatmentFragment.editTextDateOfDisease.setVisibility(View.GONE);
                tabletTreatmentFragment.textInputLayoutDiseaseName.setVisibility(View.GONE);

                tabletTreatmentFragment.editDisease = false;
                tabletTreatmentFragment.editTextDiseaseName.setEnabled(false);
                tabletTreatmentFragment.treatmentDescriptionFragment.editTextTreatment.setFocusable(false);
                tabletTreatmentFragment.treatmentDescriptionFragment.editTextTreatment.setFocusableInTouchMode(false);
                tabletTreatmentFragment.treatmentDescriptionFragment.editTextTreatment.setCursorVisible(false);

                tabletTreatmentFragment.editTextDiseaseName.setText(tempTextDiseaseName);
                tabletTreatmentFragment.editTextDateOfDisease.setText(tempTextDateOfTreatment);
                tabletTreatmentFragment.treatmentDescriptionFragment.editTextTreatment.setText(tempTextTreatment);

                if (diseasesIsEmpty) {
                    blur(TABLET_TREATMENT_FRAGMENT);
                    tabletTreatmentFragment.tabLayout.setVisibility(View.INVISIBLE);
                    tabletTreatmentFragment.viewPager.setVisibility(View.INVISIBLE);

                    tabletDiseasesFragment.textViewAddDisease.setVisibility(View.VISIBLE);
                    tabletDiseasesFragment.textViewAddDisease.startAnimation(tabletDiseasesFragment.fadeInAnimation);
                } else if (newDiseaseAndTreatment) {
                    tabletDiseasesFragment.fabAddDisease.startAnimation(tabletDiseasesFragment.fabShowAnimation);
                }

                tabletTreatmentDelete.setVisibility(View.GONE);

                diseaseAndTreatmentInEdit = false;
                newDiseaseAndTreatment = false;

                tabletTreatmentFragment.treatmentDescriptionFragment.fabEditTreatmentDescripton.startAnimation(
                        tabletTreatmentFragment.fabShowAnimation
                );
            }
        });

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

        tabletTreatmentSave = findViewById(R.id.tablet_treatment_save);
        tabletTreatmentSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                hideSoftInput();

                // флаг, чтоб клик не работал,
                // пока идет сохранения
                if (treatmentOnSavingOrUpdatingOrDeleting) {
                    return;
                }

                treatmentOnSavingOrUpdatingOrDeleting = true;

                tabletTreatmentFragment.focusHolder.requestFocus();
                tabletTreatmentFragment.saveDiseaseAndTreatment();
            }
        });
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

    // Диалог "Удалить заболевание или отменить удаление"
    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AlertDialogCustom);
        builder.setMessage(getString(R.string.disease_delete) + " " + tabletTreatmentFragment.editTextDiseaseName.getText() + "?");
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (treatmentOnSavingOrUpdatingOrDeleting) {
                    if (dialog != null) {
                        dialog.dismiss();
                    }
                } else {
                    treatmentOnSavingOrUpdatingOrDeleting = true;
                    deleteDiseaseAndTreatmentPhotos();
                }
            }
        });

        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    treatmentOnSavingOrUpdatingOrDeleting = false;
                    dialog.dismiss();
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();
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
        if (tabletTreatmentFragment != null && tabletTreatmentFragment.treatmentDescriptionFragment != null) {
            if (tabletTreatmentFragment.treatmentDescriptionFragment.editTextTreatment.hasFocus() ||
                    tabletTreatmentFragment.editTextDiseaseName.hasFocus()) {

                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
            } else {
                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
            }
        }

        boolean usersLoaded = false;

        if (firstLoad || userInserted || userUpdated || userDeleted) {
            // если просто смотрели на карточку юзера (без изменений), то и грузить не надо
            // иначе, загружаем данные в окно tabletUsersFragment
            tabletUsersFragment.initUsersLoader();

            usersLoaded = true;
        }

        if (!usersLoaded && (diseaseInserted || diseaseUpdated || diseaseDeleted)) {
            // если просто смотрели на карточку заболевания (без изменений), то и грузить не надо
            // иначе, загружаем данные в окно tabletDiseasesFragment
            tabletDiseasesFragment.initDiseasesLoader();
        }

        firstLoad = false;
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

    public void blur(int fragmentNumber) {

        switch (fragmentNumber) {
            case TABLET_USERS_FRAGMENT:
                if (!tabletUsersBlurFrame.isClickable()) {

                    tabletUsersBlurFrame.setClickable(true);
                    tabletUsersBlurFrame.setBackgroundColor(getResources().getColor(R.color.my_blue));
                }
                break;
            case TABLET_DISEASES_FRAGMENT:
                if (!tabletDiseasesBlurFrame.isClickable()) {

                    tabletDiseasesBlurFrame.setClickable(true);
                    tabletDiseasesBlurFrame.setBackgroundColor(getResources().getColor(R.color.my_blue));
                }
                break;
            case TABLET_TREATMENT_FRAGMENT:
                if (!tableTreatmentBlurFrame.isClickable()) {

                    tableTreatmentBlurFrame.setClickable(true);
                    tableTreatmentBlurFrame.setBackgroundColor(getResources().getColor(R.color.my_blue));
                }
                break;
            default:
                break;
        }
    }

    public void unBlur(int fragmentNumber) {

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
    }
}
