package com.gmail.krbashianrafael.medpunkt.tablet;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.constraint.Guideline;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gmail.krbashianrafael.medpunkt.R;
import com.gmail.krbashianrafael.medpunkt.phone.DiseaseRecyclerViewAdapter;
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

    public int firstLoad = 0;

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

    private FrameLayout tabletUsersBlurFrame;
    public FrameLayout tabletDiseasesBlurFrame;
    private FrameLayout tableTreatmentBlurFrame;
    public FrameLayout tabletUsersFrame, tabletDiseasesFrame;


    public FrameLayout tabletTreatmentDeleteFrame, tabletTreatmentSaveFrame;

    public Guideline ver_1_Guideline;
    public Guideline ver_2_Guideline;
    public Guideline ver_3_Guideline;
    public Guideline ver_4_Guideline;

    // это значения для Blur соответствующих фрагментов
    public static final int TABLET_USERS_FRAGMENT = 1;
    public static final int TABLET_DISEASES_FRAGMENT = 2;
    public static final int TABLET_TREATMENT_FRAGMENT = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tablet_main);

        firstLoad = 1;
        diseaseAndTreatmentInEdit = false;
        newDiseaseAndTreatment = false;
        treatmentOnSavingOrUpdatingOrDeleting = false;

        tempTextDiseaseName = "";
        tempTextTreatment = "";
        tempTextDateOfTreatment = "";

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
        /*tabletUsersTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });*/
        tabletUsersFrame = findViewById(R.id.tablet_users_frame);


        tabletDiseasesTitle = findViewById(R.id.tablet_diseases_title);
        tabletDiseasesFrame = findViewById(R.id.tablet_diseases_frame);

        ver_1_Guideline = findViewById(R.id.ver_1_guideline);
        ver_2_Guideline = findViewById(R.id.ver_2_guideline);
        ver_3_Guideline = findViewById(R.id.ver_3_guideline);
        ver_4_Guideline = findViewById(R.id.ver_4_guideline);

        tabletTreatmentTitle = findViewById(R.id.tablet_treatment_title);

        LLtabletTreatmentCancelOrSave = findViewById(R.id.tablet_treatment_cancel_or_save);

        tabletTreatmentCancel = findViewById(R.id.tablet_treatment_cancel);
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
                        cancel(false);
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

        tabletTreatmentSaveFrame = findViewById(R.id.tablet_treatment_save_frame);

        tabletTreatmentSave = findViewById(R.id.tablet_treatment_save);
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

    public boolean diseaseAndTreatmentHasNotChanged() {

        String diseaseDate = tabletTreatmentFragment.editTextDateOfDisease.getText().toString();
        if (TextUtils.equals(tabletTreatmentFragment.editTextDateOfDisease.getText(), getString(R.string.disease_date)) &&
                !TextUtils.equals(tempTextDateOfTreatment, getString(R.string.disease_date))) {
            diseaseDate = "";
        }


        Log.d("Changs", "editTextDiseaseName = " + tabletTreatmentFragment.editTextDiseaseName.getText().toString());
        Log.d("Changs", "tempTextDiseaseName = " + tempTextDiseaseName);


        Log.d("Changs", "editTextTreatment = " + tabletTreatmentFragment.treatmentDescriptionFragment.editTextTreatment.getText());
        Log.d("Changs", "tempTextTreatment = " + tempTextTreatment);


        Log.d("Changs", "editTextDateOfDisease = " + tabletTreatmentFragment.editTextDateOfDisease.getText());
        Log.d("Changs", "tempTextDateOfTreatment = " + tempTextDateOfTreatment);
        Log.d("Changs", "diseaseDate = " + diseaseDate);

        if (TextUtils.equals(tabletTreatmentFragment.editTextDiseaseName.getText().toString(), "") &&
                TextUtils.equals(diseaseDate, "") &&
                TextUtils.equals(tabletTreatmentFragment.treatmentDescriptionFragment.editTextTreatment.getText(), "")) {
            Log.d("Changs", "return true");
            return true;
        }


        boolean r = TextUtils.equals(tabletTreatmentFragment.editTextDiseaseName.getText().toString(), tempTextDiseaseName) &&
                TextUtils.equals(diseaseDate, tempTextDateOfTreatment) &&
                TextUtils.equals(tabletTreatmentFragment.treatmentDescriptionFragment.editTextTreatment.getText(), tempTextTreatment);

        Log.d("Changs", "returnX " + r);

        return r;
    }

    // Диалог "сохранить или выйти без сохранения"
    public void showUnsavedChangesDialog(final RecyclerView.ViewHolder holder) {

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
                        cancel(true);
                        ((DiseaseRecyclerViewAdapter.DiseaseHolder) holder).tabletDiseaseSelected();
                    }
                } else {
                    cancel(false);
                    if (newDiseaseAndTreatment) {
                        tabletDiseasesFragment.onAddDiseaseClicked();
                    }
                }


            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();
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

                    if (dialog != null) {
                        dialog.dismiss();
                    }

                    // если подтверждаем удаление заболевания

                    ver_3_Guideline.setGuidelinePercent(0.60f);

                    treatmentOnSavingOrUpdatingOrDeleting = true;
                    tabletTreatmentFragment.editDisease = false;

                    blur(TABLET_TREATMENT_FRAGMENT);

                    LLtabletTreatmentCancelOrSave.setVisibility(View.GONE);
                    tabletTreatmentDelete.setVisibility(View.GONE);

                    tabletTreatmentFragment.editTextDateOfDisease.setVisibility(View.GONE);
                    tabletTreatmentFragment.textInputLayoutDiseaseName.setVisibility(View.GONE);

                    tabletTreatmentFragment.tabLayout.setVisibility(View.INVISIBLE);
                    tabletTreatmentFragment.viewPager.setVisibility(View.INVISIBLE);
                    tabletTreatmentFragment.treatmentDescriptionFragment.
                            fabEditTreatmentDescripton.setVisibility(View.INVISIBLE);

                    /*tabletTreatmentFragment.editTextDiseaseName.setEnabled(false);
                    tabletTreatmentFragment.treatmentDescriptionFragment.editTextTreatment.setFocusable(false);
                    tabletTreatmentFragment.treatmentDescriptionFragment.editTextTreatment.setFocusableInTouchMode(false);
                    tabletTreatmentFragment.treatmentDescriptionFragment.editTextTreatment.setCursorVisible(false);*/

                    tabletTreatmentFragment.set_idUser(0);
                    tabletTreatmentTitle.setText("");
                    tabletTreatmentFragment.editTextDiseaseName.setText("");
                    tabletTreatmentFragment.editTextDateOfDisease.setText("");
                    tabletTreatmentFragment.treatmentDescriptionFragment.editTextTreatment.setText("");

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

    public void cancel(boolean newdiseaseSelected) {

        ver_3_Guideline.setGuidelinePercent(0.60f);

        hideElementsOnTabletTreatmentFragment();

        if (diseasesIsEmpty) {
            blur(TABLET_TREATMENT_FRAGMENT);
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
        if (!newdiseaseSelected) {
            tabletTreatmentFragment.editTextDiseaseName.setText(tempTextDiseaseName);
            tabletTreatmentFragment.editTextDateOfDisease.setText(tempTextDateOfTreatment);
            tabletTreatmentFragment.treatmentDescriptionFragment.editTextTreatment.setText(tempTextTreatment);
        }

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

        Log.d("sasa", "diseaseSelected = " + TabletDiseasesFragment.diseaseSelected);

        if (!TabletDiseasesFragment.diseaseSelected) {
            tabletTreatmentTitle.setText("");
            blur(TABLET_TREATMENT_FRAGMENT);
            tabletTreatmentFragment.tabLayout.setVisibility(View.INVISIBLE);
            tabletTreatmentFragment.viewPager.setVisibility(View.INVISIBLE);
            tabletTreatmentFragment.treatmentDescriptionFragment.fabEditTreatmentDescripton.setVisibility(View.INVISIBLE);

        } else {
            tabletTreatmentFragment.tabLayout.setVisibility(View.VISIBLE);
            tabletTreatmentFragment.viewPager.setVisibility(View.VISIBLE);

            tabletTreatmentFragment.treatmentDescriptionFragment.fabEditTreatmentDescripton.startAnimation(
                    tabletTreatmentFragment.fabShowAnimation
            );
        }

        tabletUsersFragment.fabAddUser.startAnimation(tabletUsersFragment.fabShowAnimation);

        LLtabletTreatmentCancelOrSave.setVisibility(View.GONE);
        tabletTreatmentDelete.setVisibility(View.GONE);
        tabletTreatmentFragment.editTextDateOfDisease.setVisibility(View.GONE);
        tabletTreatmentFragment.textInputLayoutDiseaseName.setVisibility(View.GONE);

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

        if (firstLoad == 1 || userInserted || userUpdated || userDeleted) {
            // если просто смотрели на карточку юзера (без изменений), то и грузить не надо
            // иначе, загружаем данные в окно tabletUsersFragment
            tabletUsersFragment.initUsersLoader();

            usersLoaded = true;
            //firstLoad++;
        }

        if (!usersLoaded && (diseaseInserted || diseaseUpdated || diseaseDeleted)) {
            // если просто смотрели на карточку заболевания (без изменений), то и грузить не надо
            // иначе, загружаем данные в окно tabletDiseasesFragment
            tabletDiseasesFragment.initDiseasesLoader();
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

    public void blur(int fragmentNumber) {

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
