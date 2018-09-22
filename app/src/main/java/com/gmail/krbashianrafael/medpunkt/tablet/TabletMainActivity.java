package com.gmail.krbashianrafael.medpunkt.tablet;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.gmail.krbashianrafael.medpunkt.R;
import com.tsongkha.spinnerdatepicker.DatePicker;
import com.tsongkha.spinnerdatepicker.DatePickerDialog;

import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;
import java.util.Locale;

public class TabletMainActivity extends AppCompatActivity
        implements DatePickerDialog.OnDateSetListener {

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

    // это поле берется из UsersRecyclerViewAdapter
    public static long user_IdInEdit = 0;

    // это поле берется из DiseaseURecyclerViewAdapter
    public static long disease_IdInEdit = 0;

    public TabletUsersFragment tabletUsersFragment;
    public TabletDiseasesFragment tabletDiseasesFragment;
    public TabletTreatmentFragment tabletTreatmentFragment;

    public TextView tabletUsersTitle;
    public TextView tabletDiseasesTitle;
    public TextView tabletTreatmentTitle;

    private FrameLayout tabletUsersBlurFrame;
    private FrameLayout tabletDiseasesBlurFrame;
    private FrameLayout tableTreatmentBlurFrame;

    public static final int TABLET_USERS_FRAGMENT = 1;
    public static final int TABLET_DISEASES_FRAGMENT = 2;
    public static final int TABLET_TREATMENT_FRAGMENT = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tablet_main);

        firstLoad = true;

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

        tabletUsersTitle = findViewById(R.id.tablet_users_title);
        tabletUsersTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        tabletDiseasesTitle = findViewById(R.id.tablet_diseases_title);
        tabletTreatmentTitle = findViewById(R.id.tablet_treatment_title);

        tabletUsersBlurFrame = findViewById(R.id.tablet_users_blur);
        tabletDiseasesBlurFrame = findViewById(R.id.tablet_diseases_blur);
        tableTreatmentBlurFrame = findViewById(R.id.tablet_treatment_blur);
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

            Log.d("yyy", "TMainUserLoad");


            // если просто смотрели на карточку юзера (без изменений), то и грузить не надо
            // иначе, загружаем данные в окно tabletUsersFragment
            tabletUsersFragment.initUsersLoader();

            usersLoaded = true;
        }

        if (!usersLoaded && (diseaseInserted || diseaseUpdated || diseaseDeleted)) {

            Log.d("yyy", "TMainDiseaseLoad");

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
