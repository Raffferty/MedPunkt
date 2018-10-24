package com.gmail.krbashianrafael.medpunkt.tablet;

import android.annotation.SuppressLint;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.OperationApplicationException;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.RemoteException;
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
import android.text.TextUtils;
import android.text.style.ImageSpan;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.ScaleAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.gmail.krbashianrafael.medpunkt.DiseaseItem;
import com.gmail.krbashianrafael.medpunkt.HomeActivity;
import com.gmail.krbashianrafael.medpunkt.R;
import com.gmail.krbashianrafael.medpunkt.data.MedContract;
import com.gmail.krbashianrafael.medpunkt.phone.DatePickerFragment;
import com.gmail.krbashianrafael.medpunkt.phone.TreatmentAdapter;
import com.gmail.krbashianrafael.medpunkt.phone.TreatmentDescriptionFragment;
import com.gmail.krbashianrafael.medpunkt.phone.TreatmentPhotosFragment;
import com.tsongkha.spinnerdatepicker.DatePickerDialog;
import com.tsongkha.spinnerdatepicker.SpinnerDatePickerDialogBuilder;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Calendar;

import static android.content.Context.MODE_PRIVATE;

public class TabletTreatmentFragment extends Fragment
        implements android.support.v4.app.LoaderManager.LoaderCallbacks<Cursor> {

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
    //private boolean goBack, newDisease, onSavingOrUpdatingOrDeleting = false;
    private boolean newDisease = false;
    public boolean editDisease = false;

    private ActionBar actionBar;

    // название заболевания
    public String textDiseaseName = "";
    public String textDateOfDisease = "";
    public String textTreatment = "";

    public TextView txtTitleDisease;
    protected TextView txtTitleTreatment;

    public ImageView imgZoomOutTabletTreatment, imgZoomInTabletTreatment;

    // поля названия заболевания, описания лечения и focusHolder
    public TextInputLayout textInputLayoutDiseaseName;
    public TextInputEditText editTextDiseaseName;
    public EditText editTextDateOfDisease;
    public EditText focusHolder;

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
        if (HomeActivity.iAmDoctor) {
            txtTitleTreatment.setText(R.string.patient_treatmen_title_text);
        }
        txtTitleTreatment.setBackgroundColor(getResources().getColor(R.color.my_dark_gray));
        txtTitleTreatment.setTextColor(getResources().getColor(R.color.white));

        imgZoomOutTabletTreatment = view.findViewById(R.id.img_zoom_out_tablet_treatment);
        imgZoomOutTabletTreatment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imgZoomInTabletTreatment.setVisibility(View.VISIBLE);
                imgZoomOutTabletTreatment.setVisibility(View.INVISIBLE);

                //tabletMainActivity.tabletUsersWideTitle.setVisibility(View.VISIBLE);

                //tabletMainActivity.tabletDiseasesFragment.animVerGuideline_3_from_60_to_0.start();
                tabletMainActivity.ver_3_Guideline.setGuidelinePercent(0.00f);

                tabletMainActivity.tabletUsersWideTitle.setText(tabletMainActivity.tabletDiseasesTitle.getText().toString());
                tabletMainActivity.tabletUsersWideTitle.setVisibility(View.VISIBLE);
                tabletMainActivity.tabletTreatmentTitle.setBackgroundColor(getResources().getColor(R.color.blue));

                //tabletMainActivity.tabletDiseasesFragment.animVerGuideline_3_from_60_to_0.start();


            }
        });

        imgZoomInTabletTreatment = view.findViewById(R.id.img_zoom_in_tablet_treatment);
        imgZoomInTabletTreatment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //tabletMainActivity.tabletUsersWideTitle.setVisibility(View.GONE);

                // код для показа выделенного заболевания
                if (TabletMainActivity.selectedDisease_id != 0) {

                    final ArrayList<DiseaseItem> myData = tabletMainActivity.tabletDiseasesFragment.diseaseRecyclerViewAdapter.getDiseaseList();

                    if (myData.size() != 0) {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {

                                TabletMainActivity.selectedDisease_position = 0;

                                if (TabletMainActivity.selectedDisease_id != 0) {
                                    for (int i = 0; i < myData.size(); i++) {
                                        if (myData.get(i).get_diseaseId() == TabletMainActivity.selectedDisease_id) {
                                            TabletMainActivity.selectedDisease_position = i;
                                        }
                                    }
                                }

                                tabletMainActivity.tabletDiseasesFragment.recyclerDiseases.smoothScrollToPosition(TabletMainActivity.selectedDisease_position);
                            }
                        }, 500);
                    }
                }

                imgZoomInTabletTreatment.setVisibility(View.INVISIBLE);
                imgZoomOutTabletTreatment.setVisibility(View.VISIBLE);

                tabletMainActivity.ver_3_Guideline.setGuidelinePercent(0.60f);
                tabletMainActivity.tabletUsersWideTitle.setVisibility(View.GONE);
                tabletMainActivity.tabletUsersWideTitle.setText("");
                tabletMainActivity.tabletTreatmentTitle.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));

                 //tabletMainActivity.tabletDiseasesFragment.animVerGuideline_3_from_0_to_60.start();

            }
        });

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

                    DatePickerDialog spinnerDatePickerDialog = new SpinnerDatePickerDialogBuilder()
                            .context(tabletMainActivity)
                            .callback(tabletMainActivity)
                            .spinnerTheme(R.style.NumberPickerStyle)
                            .defaultDate(mYear, mMonth, mDay)
                            .build();

                    spinnerDatePickerDialog.setCanceledOnTouchOutside(false);
                    spinnerDatePickerDialog.show();

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
        /*if (tabletMainActivity != null) {
            tabletMainActivity.getWindow().setSoftInputMode(
                    WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN |
                            WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        }*/

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
        View viewToHide = tabletMainActivity.getCurrentFocus();
        if (viewToHide != null) {
            InputMethodManager imm = (InputMethodManager) tabletMainActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(viewToHide.getWindowToken(), 0);
            }
        }

        //tabletMainActivity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
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


    public void saveDiseaseAndTreatment() {
        // устанавливаем анимацию на случай Error
        ScaleAnimation scaleAnimation = new ScaleAnimation(0f, 1f, 0f, 0f);
        scaleAnimation.setDuration(200);

        String nameToCheck = editTextDiseaseName.getText().toString().trim();
        String dateOfDiseaseToCheck = editTextDateOfDisease.getText().toString();
        boolean wrongField = false;

        // првоерка названия заболевания
        if (TextUtils.isEmpty(nameToCheck)) {
            textInputLayoutDiseaseName.setHintTextAppearance(R.style.Lable_Error);
            textInputLayoutDiseaseName.setError(getString(R.string.disease_error_name));
            editTextDiseaseName.startAnimation(scaleAnimation);
            editTextDiseaseName.requestFocus();

            wrongField = true;
        }

        // проверка Даты заболевания
        if (TextUtils.equals(dateOfDiseaseToCheck, getString(R.string.disease_date))) {
            if (wrongField) {
                textInputLayoutDiseaseName.setError(
                        getString(R.string.disease_error_name) + "\n" +
                                getString(R.string.disease_error_date)
                );
            } else {
                textInputLayoutDiseaseName.setError(getString(R.string.disease_error_date));
            }

            editTextDateOfDisease.setTextColor(getResources().getColor(R.color.colorFab));
            editTextDateOfDisease.startAnimation(scaleAnimation);
            editTextDiseaseName.requestFocus();

            wrongField = true;
        }

        // если поля описания и Дата фото не верные - выходим
        if (wrongField) {
            tabletMainActivity.treatmentOnSavingOrUpdatingOrDeleting = false;
            return;
        } else {
            textInputLayoutDiseaseName.setError(null);
        }

        // проверка окончена, начинаем сохранение

        /*if (TabletDiseasesFragment.diseaseSelected) {
            tabletMainActivity.tabletDiseasesFragment.animVerGuideline_3_from_30_to_60.start();
        } else {
            tabletMainActivity.ver_3_Guideline.setGuidelinePercent(1.00f);
            tabletMainActivity.tabletDiseasesFragment.animVerGuideline_2_from_30_to_50.start();
        }*/

        tabletMainActivity.hideElementsOnTabletTreatmentFragment();

        // присваиваем стрингам textDateOfDisease, textDiseaseName и textTreatment
        // значения полей editTextDateOfDisease, editTextDiseaseName и editTextTreatment
        // для дальнейшей проверки на их изменения
        textDiseaseName = nameToCheck;
        textDateOfDisease = editTextDateOfDisease.getText().toString();
        textTreatment = treatmentDescriptionFragment.editTextTreatment.getText().toString();

        // если было нажато идти обратно
        /*if (goBack) {
            if (newDisease) {
                // сохранять в базу в отдельном треде
                saveDiseaseAndTreatmentToDataBase();
            } else {
                // обновлять в базу в отдельном треде
                updateDiseaseAndTreatmentToDataBase();
            }

            onSavingOrUpdatingOrDeleting = false;

            //и идем в DiseasesActivity
            goToDiseasesActivity();

        } else {*/
        if (TabletMainActivity.newDiseaseAndTreatment) {

            imgZoomOutTabletTreatment.setVisibility(View.VISIBLE);

            /*TabletMainActivity.newDiseaseAndTreatment = false;
            TabletMainActivity.diseaseAndTreatmentInEdit = false;*/

            // сохранять в базу в отдельном треде
            saveDiseaseAndTreatmentToDataBase();
        } else {

            imgZoomInTabletTreatment.setVisibility(View.VISIBLE);

            /*TabletMainActivity.newDiseaseAndTreatment = false;
            TabletMainActivity.diseaseAndTreatmentInEdit = false;*/
            // обновлять в базу в отдельном треде
            updateDiseaseAndTreatmentToDataBase();
        }

        /*tabletMainActivity.tabletDiseasesTitle.setText(textDiseaseName);
        tabletMainActivity.tabletDiseasesTitle.setVisibility(View.VISIBLE);*/

        //editDisease = false;
        /*textInputLayoutDiseaseName.setVisibility(View.GONE);
        editTextDateOfDisease.setVisibility(View.GONE);

        treatmentDescriptionFragment.editTextTreatment.setSelection(0);
        treatmentDescriptionFragment.editTextTreatment.setFocusable(false);
        treatmentDescriptionFragment.editTextTreatment.setFocusableInTouchMode(false);
        treatmentDescriptionFragment.editTextTreatment.setCursorVisible(false);

        focusHolder.requestFocus();*/

        /*tabletMainActivity.tabletDiseasesFragment.initDiseasesLoader();

        treatmentDescriptionFragment.fabEditTreatmentDescripton.startAnimation(fabShowAnimation);

        tabletMainActivity.treatmentOnSavingOrUpdatingOrDeleting = false;*/
        //}
    }

    private void saveDiseaseAndTreatmentToDataBase() {
        ContentValues values = new ContentValues();
        values.put(MedContract.DiseasesEntry.COLUMN_U_ID, _idUser);
        values.put(MedContract.DiseasesEntry.COLUMN_DISEASE_NAME, textDiseaseName);
        values.put(MedContract.DiseasesEntry.COLUMN_DISEASE_DATE, textDateOfDisease);
        values.put(MedContract.DiseasesEntry.COLUMN_DISEASE_TREATMENT, textTreatment);

        // при сохранении пользователя в Базу делаем insert и получаем Uri вставленной строки
        Uri newUri = tabletMainActivity.getContentResolver().insert(MedContract.DiseasesEntry.CONTENT_DISEASES_URI, values);

        if (newUri != null) {
            // получаем _idDisease из возвращенного newUri
            _idDisease = ContentUris.parseId(newUri);

            // здесь устанавливаем флаг scrollToInsertedUserPosition в классе DiseasesActivity в true
            // чтоб после вставки новой строки в Базу и посел оповещения об изменениях
            // заново загрузился курсор и RecyclerView прокрутился вниз до последней позиции

            //TabletDiseasesFragment.scrollToInsertedDiseasePosition = true;
        } else {
            Toast.makeText(tabletMainActivity, R.string.treatment_cant_save, Toast.LENGTH_LONG).show();
        }

        tabletMainActivity.treatmentOnSavingOrUpdatingOrDeleting = false;

        tabletMainActivity.tabletDiseasesFragment.initDiseasesLoader();

        treatmentDescriptionFragment.fabEditTreatmentDescripton.startAnimation(fabShowAnimation);
    }

    private void updateDiseaseAndTreatmentToDataBase() {
        ContentValues values = new ContentValues();
        values.put(MedContract.DiseasesEntry.COLUMN_DISEASE_NAME, textDiseaseName);
        values.put(MedContract.DiseasesEntry.COLUMN_DISEASE_DATE, textDateOfDisease);
        values.put(MedContract.DiseasesEntry.COLUMN_DISEASE_TREATMENT, textTreatment);

        // Uri к заболеванию, которое будет обновляться
        Uri mCurrentUserUri = Uri.withAppendedPath(MedContract.DiseasesEntry.CONTENT_DISEASES_URI, String.valueOf(_idDisease));

        // делаем update в Базе
        int rowsAffected = tabletMainActivity.getContentResolver().update(mCurrentUserUri, values, null, null);

        if (rowsAffected == 0) {
            Toast.makeText(tabletMainActivity, R.string.treatment_cant_update, Toast.LENGTH_LONG).show();
        } else {
            //if (!TabletDiseasesFragment.diseaseSelected){
            tabletMainActivity.tabletTreatmentTitle.setText(tabletMainActivity.tabletTreatmentFragment.editTextDiseaseName.getText().toString());
            //}
        }

        tabletMainActivity.treatmentOnSavingOrUpdatingOrDeleting = false;

        tabletMainActivity.tabletDiseasesFragment.initDiseasesLoader();

        //treatmentDescriptionFragment.fabEditTreatmentDescripton.startAnimation(fabShowAnimation);
    }

    public void initLoaderToDiseaseAndTreatmentPhotos() {
        getLoaderManager().initLoader(TR_PHOTOS_LOADER, null, this);
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
        new TabletTreatmentFragment.DiseaseAndTreatmentPhotosDeletingAsyncTask(
                tabletMainActivity, photoFilePathesToBeDeletedList).execute(tabletMainActivity.getApplicationContext());
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        //
    }

    // класс DiseaseAndTreatmentPhotosDeletingAsyncTask делаем статическим,
    // чтоб не было утечки памяти при его работе
    private static class DiseaseAndTreatmentPhotosDeletingAsyncTask extends AsyncTask<Context, Void, Integer> {

        private static final String PREFS_NAME = "PREFS";

        private final WeakReference<TabletMainActivity> asinkTabletMainActivity;
        private final ArrayList<String> mPhotoFilePathesListToBeDeleted;
        private int mRowsFromTreatmentPhotosDeleted = -1;

        // в конструкторе получаем WeakReference<TreatmentActivity>
        // и образовываем список ArrayList<String> mPhotoFilePathesListToBeDeleted на основании полученного photoFilePathesListToBeDeleted
        // это список путей к файлам, которые необходимо будет удалить
        // тоесть наш mPhotoFilePathesListToBeDeleted НЕ зависим от полученного photoFilePathesListToBeDeleted
        DiseaseAndTreatmentPhotosDeletingAsyncTask(TabletMainActivity context, ArrayList<String> photoFilePathesListToBeDeleted) {
            asinkTabletMainActivity = new WeakReference<>(context);
            mPhotoFilePathesListToBeDeleted = new ArrayList<>(photoFilePathesListToBeDeleted);
        }

        // в onPreExecute получаем  TreatmentActivity treatmentActivity
        // и если он null, то никакое удаление не происходит
        // если же treatmentActivity не null,
        // то в основном треде удаляем строки из таблиц treatmentPhotos и diseases в одной транзакции
        // при этом, получаем (как резульат удаления строк из таблицы treatmentPhotos) количество удаленных строк
        // по сути, это количество должно совпадать с количеством элементов в mPhotoFilePathesListToBeDeleted
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            TabletMainActivity mTabletMainActivity = asinkTabletMainActivity.get();
            if (mTabletMainActivity == null) {
                return;
            }

            mRowsFromTreatmentPhotosDeleted = deleteDiseaseAndTreatmentPhotosFromDataBase(mTabletMainActivity);
        }

        // метод удаления строк из таблиц treatmentPhotos и diseases в одной транзакции
        // возвращает количество удаленных строк из таблицы treatmentPhotos или -1
        private int deleteDiseaseAndTreatmentPhotosFromDataBase(TabletMainActivity mTabletMainActivity) {
            // ArrayList для операций по удалению строк из таблиц treatmentPhotos и diseases
            // в одной транзакции
            ArrayList<ContentProviderOperation> deletingFromDbOperations = new ArrayList<>();

            // пишем операцию удаления строк ИЗ ТАБЛИЦЫ treatmentPhotos
            String selectionTrPhotos = MedContract.TreatmentPhotosEntry.COLUMN_DIS_ID + "=?";
            String[] selectionArgsTrPhotos = new String[]{String.valueOf(mTabletMainActivity.tabletTreatmentFragment._idDisease)};

            ContentProviderOperation deleteTreatmentPhotosFromDbOperation = ContentProviderOperation
                    .newDelete(MedContract.TreatmentPhotosEntry.CONTENT_TREATMENT_PHOTOS_URI)
                    .withSelection(selectionTrPhotos, selectionArgsTrPhotos)
                    .build();

            // добавляем операцию удаления строк ИЗ ТАБЛИЦЫ treatmentPhotos в список операций deletingFromDbOperations
            deletingFromDbOperations.add(deleteTreatmentPhotosFromDbOperation);

            // пишем операцию удаления строки заболевания ИЗ ТАБЛИЦЫ diseases
            String selectionDisease = MedContract.DiseasesEntry.DIS_ID + "=?";
            String[] selectionArgsDisease = new String[]{String.valueOf(mTabletMainActivity.tabletTreatmentFragment._idDisease)};

            ContentProviderOperation deleteDiseaseFromDbOperation = ContentProviderOperation
                    .newDelete(MedContract.DiseasesEntry.CONTENT_DISEASES_URI)
                    .withSelection(selectionDisease, selectionArgsDisease)
                    .build();

            // добавляем операцию удаления строки заболевания ИЗ ТАБЛИЦЫ diseases в список операций deletingFromDbOperations
            deletingFromDbOperations.add(deleteDiseaseFromDbOperation);

            // переменная количества удаленных строк из таблицы treatmentPhotos
            int rowsFromTreatmentPhotosDeleted = -1;

            try {
                // запускаем транзакцию удаления строк из таблиц treatmentPhotos и diseases
                // и получаем результат
                ContentProviderResult[] results = mTabletMainActivity.getContentResolver().applyBatch(MedContract.CONTENT_AUTHORITY, deletingFromDbOperations);

                // если транзакция прошла успешно
                if (results.length == 2 && results[0] != null) {
                    // записываем в rowsFromTreatmentPhotosDeleted количество удаленных строк из аблицы treatmentPhotos
                    rowsFromTreatmentPhotosDeleted = results[0].count;
                } else {
                    return rowsFromTreatmentPhotosDeleted;
                }
            } catch (RemoteException | OperationApplicationException e) {
                e.printStackTrace();
                // если транзакция НЕ прошла успешно, то возвращаем -1
                return rowsFromTreatmentPhotosDeleted;
            }

            // возвращаем количество удаленных строк из аблицы treatmentPhotos
            return rowsFromTreatmentPhotosDeleted;
        }

        // в doInBackground осуществляем удаление файлов фотографий
        // по списку путей к фотографиям из mPhotoFilePathesListToBeDeleted
        @Override
        protected Integer doInBackground(Context... contexts) {
            if (mRowsFromTreatmentPhotosDeleted == -1) {
                // если были ошибки во время удаления строк из таблиц treatmentPhotos и diseases
                // возвращаем -1
                // и выводим сообщение, что заболевания не удалилось и оставляем все как есть (не удаляем файлы)
                return -1;
            } else if (mRowsFromTreatmentPhotosDeleted == 0) {
                // если у заболевания не было фотографий,
                // то ограничиваемся удалением заболевания из таблицы diseases
                // без дальнейшего удаления каких либо файлов фото
                return 1;
            } else {
                // если у заболевания были снимки по лечению,
                // mRowsFromTreatmentPhotosDeleted > 0,
                // то удаляем соответствующие файлы фотографий

                // в этом блоке ошибки возвращают 0
                Context mContext = contexts[0];

                if (mContext == null) {
                    return 0;
                }

                // получаем SharedPreferences, чтоб писать в файл "PREFS"
                SharedPreferences prefs = mContext.getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
                final SharedPreferences.Editor prefsEditor = prefs.edit();

                StringBuilder sb = new StringBuilder();

                for (String fPath : mPhotoFilePathesListToBeDeleted) {
                    File toBeDeletedFile = new File(fPath);

                    if (toBeDeletedFile.exists()) {
                        if (!toBeDeletedFile.delete()) {
                            // если файл не удалился,
                            // то дописываем в sb его путь и ставим запятую,
                            // чтоб потом по запятой делать split
                            sb.append(fPath).append(",");
                        }
                    }
                }

                // если есть висячие файлы
                if (sb.length() > 0) {
                    // ытягиваем в String notDeletedFilesPathes из prefs пути к ранее не удаленным файлам
                    String notDeletedFilesPathes = prefs.getString("notDeletedFilesPathes", null);

                    // если из prefs вытянулись пути к ранее не удаленным файлам,
                    // то цепляем их в конец sb за запятой
                    if (notDeletedFilesPathes != null && notDeletedFilesPathes.length() != 0) {
                        sb.append(notDeletedFilesPathes);
                    } else {
                        // если в prefs не было путей к ранее не удаленным файлам,
                        // то убираем с конца sb запятую
                        sb.deleteCharAt(sb.length() - 1);
                    }

                    // пишем в поле notDeletedFilesPathes новую строку путей к неудаленным файлам, разделенных запятой
                    // при этом старая строка в prefs заменится новой строкой
                    // и выходим с return 0,
                    // что означает, что были файлы, которые не удалились

                    prefsEditor.putString("notDeletedFilesPathes", sb.toString());
                    prefsEditor.apply();

                    return 0;
                }
            }

            return 1;
        }

        @Override
        protected void onPostExecute(Integer result) {
            super.onPostExecute(result);

            final TabletMainActivity mTabletMainActivity = asinkTabletMainActivity.get();

            if (mTabletMainActivity == null) {
                return;
            }

            if (result == -1) {
                // если заболевание не удалилось из базы и фото не были удалены
                Toast.makeText(mTabletMainActivity, R.string.disease_not_deleted, Toast.LENGTH_LONG).show();
            } else {
                // result == 0 или result == 1
                // если не было снимков для удаления или заболевание удалилось и снимки удалены (или отсутствуют)
                mTabletMainActivity.tabletDiseasesFragment.initDiseasesLoader();
                mTabletMainActivity.tabletUsersFragment.fabAddUser.startAnimation(mTabletMainActivity.tabletUsersFragment.fabShowAnimation);
            }

            mTabletMainActivity.treatmentOnSavingOrUpdatingOrDeleting = false;
        }
    }
}
