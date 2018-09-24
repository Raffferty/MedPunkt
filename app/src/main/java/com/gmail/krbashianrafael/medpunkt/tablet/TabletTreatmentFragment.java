package com.gmail.krbashianrafael.medpunkt.tablet;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.gmail.krbashianrafael.medpunkt.HomeActivity;
import com.gmail.krbashianrafael.medpunkt.R;
import com.gmail.krbashianrafael.medpunkt.data.MedContract;
import com.gmail.krbashianrafael.medpunkt.phone.DatePickerFragment;
import com.gmail.krbashianrafael.medpunkt.phone.TreatmentAdapter;
import com.gmail.krbashianrafael.medpunkt.phone.TreatmentDescriptionFragment;
import com.gmail.krbashianrafael.medpunkt.phone.TreatmentPhotosFragment;
import com.tsongkha.spinnerdatepicker.SpinnerDatePickerDialogBuilder;

import java.util.ArrayList;
import java.util.Calendar;

public class TabletTreatmentFragment extends Fragment
        implements android.support.v4.app.LoaderManager.LoaderCallbacks<Cursor>{

    private TabletMainActivity tabletMainActivity;


    /**
     * Лоадеров может много (они обрабатываются в case)
     * поэтому устанавливаем инициализатор для каждого лоадера
     * в данном случае private static final int TR_PHOTOS_LOADER = 22;
     */
    private static final int TR_PHOTOS_LOADER = 22;

    // Фрагменты
    public TreatmentDescriptionFragment treatmentDescriptionFragment;
    public TreatmentPhotosFragment treatmentPhotosFragment;

    // id пользователя
    public long _idUser = 0;

    // id заболеввания
    public long _idDisease = 0;

    // возможность изменять пользователя, показывать стрелку обратно, был ли изменен пользователь
    private boolean goBack, newDisease, onSavingOrUpdatingOrDeleting = false;
    public boolean editDisease = false;

    private ActionBar actionBar;

    // название заболевания
    public String textDiseaseName = "";
    public String textDateOfDisease = "";
    public String textTreatment = "";

    public TextView txtTitleDisease;
    protected TextView txtTitleTreatment;

    // поля названия заболевания, описания лечения и focusHolder
    public TextInputLayout textInputLayoutDiseaseName;
    public TextInputEditText editTextDiseaseName;
    public EditText editTextDateOfDisease;
    private EditText focusHolder;

    // Animation fabShowAnimation
    public Animation fabShowAnimation;

    public ViewPager viewPager;

    public TreatmentAdapter categoryAdapter;

    public TabLayout tabLayout;

    public TabletTreatmentFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_tablet_treatment, container, false);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // убираем фреймы, которые нужны только на телефоне
        FrameLayout frmDividerGreen = view.findViewById(R.id.divider_frame_white);
        frmDividerGreen.setVisibility(View.GONE);

        FrameLayout frmDividerBlue = view.findViewById(R.id.divider_frame_blue);
        frmDividerBlue.setVisibility(View.GONE);

        // устанавливаем txtTitleTreatment
        txtTitleTreatment = view.findViewById(R.id.txt_title_treatment);
        if (HomeActivity.iAmDoctor){
            txtTitleTreatment.setText(R.string.patient_treatmen_title_text);
        }

        textInputLayoutDiseaseName = view.findViewById(R.id.text_input_layout_disease_name);
        editTextDiseaseName = view.findViewById(R.id.editText_disease_name);

        editTextDateOfDisease = view.findViewById(R.id.editText_date);
        if (textDateOfDisease != null) {
            editTextDateOfDisease.setText(textDateOfDisease);
        }

        editTextDateOfDisease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                hideSoftInput();

                // убираем показ ошибок в textInputLayoutPhotoDescription
                textInputLayoutDiseaseName.setError(null);
                textInputLayoutDiseaseName.setErrorEnabled(false);
                textInputLayoutDiseaseName.setHintTextAppearance(R.style.Lable);

                editTextDateOfDisease.setTextColor(getResources().getColor(R.color.colorPrimaryDark));

                // выбираем дату фото
                // в версии Build.VERSION_CODES.N нет календаря с прокруткой
                // поэтому для вывода календаря с прокруткой пользуемся стронней библиетекой
                // слушатель прописываем в нашем же классе .callback(TreatmentActivity.this)
                // com.tsongkha.spinnerdatepicker.SpinnerDatePickerDialogBuilder
                // используем эту библиотеку для
                // Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    String dateInEditTextDate = editTextDateOfDisease.getText().toString().trim();

                    int mYear;
                    int mMonth;
                    int mDay;

                    if (dateInEditTextDate.contains("-")) {
                        String[] mDayMonthYear = dateInEditTextDate.split("-");
                        mYear = Integer.valueOf(mDayMonthYear[2]);
                        mMonth = Integer.valueOf(mDayMonthYear[1]) - 1;
                        mDay = Integer.valueOf(mDayMonthYear[0]);
                    } else {
                        final Calendar c = Calendar.getInstance();
                        mYear = c.get(Calendar.YEAR);
                        mMonth = c.get(Calendar.MONTH);
                        mDay = c.get(Calendar.DAY_OF_MONTH);
                    }

                    new SpinnerDatePickerDialogBuilder()
                            .context(tabletMainActivity)
                            .callback(tabletMainActivity)
                            .spinnerTheme(R.style.NumberPickerStyle)
                            .defaultDate(mYear, mMonth, mDay)
                            .build().show();
                } else {
                    // в остальных случаях пользуемся классом DatePickerFragment
                    DatePickerFragment newFragment = new DatePickerFragment();
                    newFragment.show(tabletMainActivity.getSupportFragmentManager(), "datePicker");
                }
            }
        });

        // при OnTouch editTextPhotoDescription убираем ошибку
        editTextDiseaseName.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                textInputLayoutDiseaseName.setError(null);
                textInputLayoutDiseaseName.setErrorEnabled(false);
                textInputLayoutDiseaseName.setHintTextAppearance(R.style.Lable);
                editTextDateOfDisease.setTextColor(getResources().getColor(R.color.colorPrimaryDark));

                return false;
            }
        });

        focusHolder = view.findViewById(R.id.focus_holder);
        focusHolder.requestFocus();

        viewPager = view.findViewById(R.id.viewpager);
        viewPager.setVisibility(View.INVISIBLE);

        tabLayout = view.findViewById(R.id.tabs);
        tabLayout.setVisibility(View.INVISIBLE);

        if (newDisease) {
            editTextDiseaseName.requestFocus();
            editTextDiseaseName.setSelection(0);
            categoryAdapter.setPagesCount(1);
            tabLayout.setVisibility(View.GONE);
        } else {
            textInputLayoutDiseaseName.setVisibility(View.GONE);
            editTextDateOfDisease.setVisibility(View.GONE);
            focusHolder.requestFocus();
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        tabletMainActivity = (TabletMainActivity) getActivity();

        // если клавиатура перекрывает поле ввода, то поле ввода приподнимается
        if (tabletMainActivity != null) {
            tabletMainActivity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN | WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        }

        categoryAdapter = new TreatmentAdapter(tabletMainActivity, this.getChildFragmentManager());

        viewPager.setAdapter(categoryAdapter);

        tabLayout.setupWithViewPager(viewPager);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 0) {
                    // при нажатии на табы формируем внешний вид табов
                    tab.setText(menuIconWithText(getResources().getDrawable(R.drawable.ic_edit_orange_24dp),
                            getResources().getString(R.string.treatment_description)));

                    // и делаем анимацию fab
                    treatmentDescriptionFragment.fabEditTreatmentDescripton.startAnimation(fabShowAnimation);

                } else {
                    tab.setText(menuIconWithText(getResources().getDrawable(R.drawable.ic_camera_alt_orange_24dp),
                            getResources().getString(R.string.treatment_images)));
                    // и делаем анимацию fab если txtAddPhotos не видим
                    if (treatmentPhotosFragment.txtAddPhotos.getVisibility() != View.VISIBLE) {
                        treatmentPhotosFragment.fabAddTreatmentPhotos.startAnimation(fabShowAnimation);
                    }
                }

                tabLayout.setTabTextColors(getResources().getColor(android.R.color.black),
                        getResources().getColor(R.color.colorFab));
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                // при отжатии табов формируем внешний вид табов
                if (tab.getPosition() == 0) {
                    tab.setText(menuIconWithText(getResources().getDrawable(R.drawable.ic_edit_black_24dp),
                            getResources().getString(R.string.treatment_description)));

                } else {
                    tab.setText(menuIconWithText(getResources().getDrawable(R.drawable.ic_camera_alt_black_24dp),
                            getResources().getString(R.string.treatment_images)));
                }

                tabLayout.setTabTextColors(getResources().getColor(android.R.color.black),
                        getResources().getColor(R.color.colorFab));
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                if (tab.getPosition() == 0) {
                    // при повторном нажатии на таб "ОПИСАНИЕ" прокручиваем вверх описание лечения
                    treatmentDescriptionFragment.editTextTreatment.setFocusable(true);
                    treatmentDescriptionFragment.editTextTreatment.setFocusableInTouchMode(true);
                    treatmentDescriptionFragment.editTextTreatment.requestFocus();
                    treatmentDescriptionFragment.editTextTreatment.setSelection(0);

                    treatmentDescriptionFragment.editTextTreatment.setFocusable(false);
                    treatmentDescriptionFragment.editTextTreatment.setFocusableInTouchMode(false);
                    focusHolder.requestFocus();

                } else {
                    // при повторном нажатии на таб "СНИМКИ" прокручиваем вверх список снимков
                    treatmentPhotosFragment.recyclerTreatmentPhotos.smoothScrollToPosition(0);
                }
            }
        });

        // анимация для показа fabEditTreatmentDescripton
        fabShowAnimation = AnimationUtils.loadAnimation(tabletMainActivity, R.anim.fab_show);
        fabShowAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                treatmentDescriptionFragment.fabEditTreatmentDescripton.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                treatmentDescriptionFragment.fabEditTreatmentDescripton.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                treatmentDescriptionFragment.fabEditTreatmentDescripton.setVisibility(View.VISIBLE);
            }
        });
    }

    // инициализация Фрагментов если они null
    // вызов этого метода и проверка происходит в самих фрагментах
    public void initTreatmentDescriptionFragment() {
        treatmentDescriptionFragment = (TreatmentDescriptionFragment) this.getChildFragmentManager().getFragments().get(0);
    }

    public void initTreatmentPhotosFragment() {
        treatmentPhotosFragment = (TreatmentPhotosFragment) this.getChildFragmentManager().getFragments().get(1);
    }

    private void hideSoftInput() {

        tabletMainActivity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        /*View viewToHide = tabletMainActivity.getCurrentFocus();
        if (viewToHide != null) {
            InputMethodManager imm = (InputMethodManager) tabletMainActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(viewToHide.getWindowToken(), 0);
            }
        }*/
    }

    // SpannableString с картикной для элеменов меню
    private CharSequence menuIconWithText(Drawable r, String title) {
        r.setBounds(0, 0, r.getIntrinsicWidth(), r.getIntrinsicHeight());
        SpannableString sb = new SpannableString("    " + title);
        ImageSpan imageSpan = new ImageSpan(r, ImageSpan.ALIGN_BOTTOM);
        sb.setSpan(imageSpan, 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        return sb;
    }

    public void set_idUser(long _idUser) {
        this._idUser = _idUser;
    }

    public void set_idDisease(long _idDisease) {
        this._idDisease = _idDisease;
    }

    public void setTextDiseaseName(String textDiseaseName) {
        this.textDiseaseName = textDiseaseName;
        editTextDiseaseName.setText(textDiseaseName);
        tabletMainActivity.tabletTreatmentTitle.setText(textDiseaseName);
    }

    public void setTextDateOfDisease(String textDateOfDisease) {
        this.textDateOfDisease = textDateOfDisease;
        editTextDateOfDisease.setText(textDateOfDisease);
    }

    public void setTextTreatment(String textTreatment) {
        this.textTreatment = textTreatment;
        treatmentDescriptionFragment.editTextTreatment.setText(textTreatment);
    }

    public void setNewDisease(boolean newDisease) {
        this.newDisease = newDisease;
    }

    public void setEditDisease(boolean editDisease) {
        this.editDisease = editDisease;
    }

    public long get_idDisease() {
        return _idDisease;
    }

    public long get_idUser() {
        return _idUser;
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        // для Loader в projection обязательно нужно указывать поле с _ID
        // здесь мы указываем поля таблицы treatmentPhotos , которые будем брать из Cursor для дальнейшей обработки
        String[] projection = {
                MedContract.TreatmentPhotosEntry.TR_PHOTO_ID,
                MedContract.TreatmentPhotosEntry.COLUMN_TR_PHOTO_PATH};

        // выборку фото делаем по _idDisease, который будет удаляться
        String selection = MedContract.TreatmentPhotosEntry.COLUMN_DIS_ID + "=?";
        String[] selectionArgs = new String[]{String.valueOf(_idDisease)};

        // This loader will execute the ContentProvider's query method on a background thread
        // Loader грузит ВСЕ данные из таблицы users через Provider
        return new CursorLoader(tabletMainActivity,   // Parent activity context
                MedContract.TreatmentPhotosEntry.CONTENT_TREATMENT_PHOTOS_URI,   // Provider content URI to query = content://com.gmail.krbashianrafael.medpunkt/treatmentPhotos/
                projection,
                selection,
                selectionArgs,
                null);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor cursor) {
        // ArrayList для путей к файлам фото, которые нужно будет удалить
        ArrayList<String> photoFilePathesToBeDeletedList = new ArrayList<>();

        if (cursor != null) {
            // устанавливаем курсор на исходную (на случай, если курсор используем повторно после прохождения цикла
            cursor.moveToPosition(-1);

            // проходим в цикле курсор
            // и добаляем пути к удаляемым файлам в ArrayList<String> photoFilePathesToBeDeletedList
            while (cursor.moveToNext()) {
                int trPhoto_pathColumnIndex = cursor.getColumnIndex(MedContract.TreatmentPhotosEntry.COLUMN_TR_PHOTO_PATH);
                String trPhotoUri = cursor.getString(trPhoto_pathColumnIndex);

                photoFilePathesToBeDeletedList.add(trPhotoUri);
            }
        }

        // делаем destroyLoader, чтоб он сам повторно не вызывался
        getLoaderManager().destroyLoader(TR_PHOTOS_LOADER);

        // Запускаем AsyncTask для удаления строк из таблиц treatmentPhotos и diseases
        // а далее, и для удаления файлов
        //new TreatmentActivity.DiseaseAndTreatmentPhotosDeletingAsyncTask(this, photoFilePathesToBeDeletedList).execute(getApplicationContext());
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        //
    }

}
