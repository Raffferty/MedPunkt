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
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ImageSpan;
import android.transition.AutoTransition;
import android.transition.Transition;
import android.transition.TransitionManager;
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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.gmail.krbashianrafael.medpunkt.R;
import com.gmail.krbashianrafael.medpunkt.data.MedContract;
import com.gmail.krbashianrafael.medpunkt.shared.DatePickerFragment;
import com.gmail.krbashianrafael.medpunkt.shared.DiseaseItem;
import com.gmail.krbashianrafael.medpunkt.shared.HomeActivity;
import com.gmail.krbashianrafael.medpunkt.shared.TreatmentAdapter;
import com.gmail.krbashianrafael.medpunkt.shared.TreatmentDescriptionFragment;
import com.gmail.krbashianrafael.medpunkt.shared.TreatmentPhotoItem;
import com.gmail.krbashianrafael.medpunkt.shared.TreatmentPhotosFragment;
import com.gmail.krbashianrafael.medpunkt.shared.UserItem;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdView;
import com.tsongkha.spinnerdatepicker.DatePickerDialog;
import com.tsongkha.spinnerdatepicker.SpinnerDatePickerDialogBuilder;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Objects;

import static android.content.Context.MODE_PRIVATE;

@SuppressWarnings("deprecation")
@SuppressLint("RestrictedApi")
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

    public boolean editDisease = false;

    // название заболевания
    public String textDiseaseName = "";
    private String textDateOfDisease = "";
    public String textTreatment = "";

    public ImageView zoomOutTabletTreatment, zoomInTabletTreatment;

    // поля названия заболевания, описания лечения и focusHolder
    public TextInputLayout textInputLayoutDiseaseName;
    public TextInputEditText editTextDiseaseName;
    public EditText editTextDateOfDisease;
    public EditText focusHolder;

    public Animation fabEditTreatmentDescriptonShowAnimation;

    public ViewPager viewPager;

    public TabLayout tabLayout;

    public FrameLayout adViewFrameTabletTreatmentFragment;
    public AdView adViewInTabletTreatmentFragment;

    private AutoTransition adCloseTransition;

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


        // рекламный блок МАЛЫЙ в TabletTreatmentFragment
        adViewFrameTabletTreatmentFragment = view.findViewById(R.id.adViewFrameTabletTreatment);
        adViewInTabletTreatmentFragment = view.findViewById(R.id.adViewInTabletTreatment);
        adViewInTabletTreatmentFragment.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                // если реклама загрузилась - показываем
                TransitionManager.beginDelayedTransition(tabletMainActivity.mSceneRoot);
                adViewFrameTabletTreatmentFragment.setVisibility(View.VISIBLE);
                TabletMainActivity.adIsShown = true;
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                // если реклама не загрузилась - скрываем
                adViewFrameTabletTreatmentFragment.setVisibility(View.GONE);
                TabletMainActivity.adIsShown = false;
            }

            @Override
            public void onAdOpened() {
            }

            @Override
            public void onAdLeftApplication() {
            }

            @Override
            public void onAdClosed() {
            }
        });

        // убираем фреймы, которые нужны только на телефоне
        FrameLayout frmDividerGreen = view.findViewById(R.id.divider_frame_white);
        frmDividerGreen.setVisibility(View.GONE);

        FrameLayout frmDividerBlue = view.findViewById(R.id.divider_frame_blue);
        frmDividerBlue.setVisibility(View.GONE);

        // устанавливаем txtTitleTreatment
        TextView txtTitleTreatment = view.findViewById(R.id.txt_title_treatment);

        if (HomeActivity.iAmDoctor) {
            txtTitleTreatment.setText(R.string.patient_treatment_title_text);
        }

        txtTitleTreatment.setBackgroundColor(getResources().getColor(R.color.my_dark_gray));
        txtTitleTreatment.setTextColor(getResources().getColor(R.color.white));

        zoomOutTabletTreatment = view.findViewById(R.id.img_zoom_out_tablet_treatment);
        zoomOutTabletTreatment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (tabletMainActivity == null) {
                    return;
                }

                TabletMainActivity.inWideView = true;

                // показываем БОЛЬШОЙ рекламный блок
                if (tabletMainActivity.adViewInTabletWideView != null) {
                    // рекламу грузим с задержкой, чтоб успела отрисоваться
                    tabletMainActivity.myTabletHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            tabletMainActivity.adViewInTabletWideView.loadAd(tabletMainActivity.adRequest);
                        }
                    }, 600);
                }

                // сначала сробатывает Ripple эфект на zoomOutTabletTreatment
                // и выставляется inWideView = true
                // потом с задержкой в пол-секунды запускается код ниже
                tabletMainActivity.myTabletHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        // скрываем МАЛЫЙ рекламный блок
                        adViewFrameTabletTreatmentFragment.setVisibility(View.GONE);
                        if (adViewInTabletTreatmentFragment != null) {
                            adViewInTabletTreatmentFragment.pause();
                        }

                        zoomInTabletTreatment.setVisibility(View.VISIBLE);
                        zoomOutTabletTreatment.setVisibility(View.INVISIBLE);

                        tabletMainActivity.ver_3_Guideline.setGuidelinePercent(0.00f);
                        tabletMainActivity.tabletUsersWideTitle.setText(tabletMainActivity.tabletDiseasesTitle.getText().toString());
                        tabletMainActivity.tabletUsersWideTitle.setVisibility(View.VISIBLE);

                        // если есть фото лечения, то в расширенном виде формируем вид окна
                        // и загружаем фото первой позиции
                        if (treatmentPhotosFragment.treatmentPhotoRecyclerViewAdapter.
                                getTreatmentPhotosList().size() != 0) {

                            treatmentPhotosFragment.verGuideline.setGuidelinePercent(0.4f);
                            treatmentPhotosFragment.fabToFullScreen.startAnimation(treatmentPhotosFragment.fabToFullScreenShowAnimation);

                            // это расширяет таб "снимки"
                            LinearLayout layout = ((LinearLayout) ((LinearLayout) tabLayout.getChildAt(0)).getChildAt(1));
                            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) layout.getLayoutParams();
                            layoutParams.weight = 1.50f;
                            layout.setLayoutParams(layoutParams);

                            // получаем данные из первой позиции и грузим фото
                            TreatmentPhotoItem treatmentPhotoItem = treatmentPhotosFragment.treatmentPhotoRecyclerViewAdapter.getTreatmentPhotosList().get(0);

                            treatmentPhotosFragment._idTrPhoto = treatmentPhotoItem.get_trPhotoId();
                            treatmentPhotosFragment.treatmentPhotoFilePath = treatmentPhotoItem.getTrPhotoUri();
                            treatmentPhotosFragment.textDateOfTreatmentPhoto = treatmentPhotoItem.getTrPhotoDate();
                            treatmentPhotosFragment.textPhotoDescription = treatmentPhotoItem.getTrPhotoName();

                            // код для выделения первого элемента фото заболевания и его загрузки в imgWideView
                            TabletMainActivity.selectedTreatmentPhoto_id = treatmentPhotosFragment._idTrPhoto;

                            treatmentPhotosFragment.treatmentPhotoRecyclerViewAdapter.notifyDataSetChanged();

                            // загрузка фото происходит в notifyDataSetChanged() в TransitionListener
                        }
                    }
                }, 250);
            }
        });

        // сначала сробатывает Ripple эфект на zoomInTabletTreatment
        // и выставляется inWideView = false
        // потом с задержкой в пол-секунды запускается код ниже
        zoomInTabletTreatment = view.findViewById(R.id.img_zoom_in_tablet_treatment);
        zoomInTabletTreatment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (tabletMainActivity == null) {
                    return;
                }

                TabletMainActivity.inWideView = false;

                tabletMainActivity.myTabletHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        // код для показа выделенного пользователя
                        if (TabletMainActivity.selectedUser_id != 0) {

                            final ArrayList<UserItem> myUsersData = tabletMainActivity.tabletUsersFragment.usersRecyclerViewAdapter.getUsersList();

                            if (myUsersData.size() != 0) {
                                tabletMainActivity.selectedUser_position = 0;

                                for (int i = 0; i < myUsersData.size(); i++) {
                                    if (myUsersData.get(i).get_userId() == TabletMainActivity.selectedUser_id) {
                                        tabletMainActivity.selectedUser_position = i;
                                    }
                                }

                                tabletMainActivity.myTabletHandler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        tabletMainActivity.tabletUsersFragment.recyclerUsers.smoothScrollToPosition(tabletMainActivity.selectedUser_position);
                                    }
                                }, 250);
                            }
                        }

                        // код для показа выделенного заболевания
                        if (TabletMainActivity.selectedDisease_id != 0) {

                            final ArrayList<DiseaseItem> myDiseasesData = tabletMainActivity.tabletDiseasesFragment.diseaseRecyclerViewAdapter.getDiseaseList();

                            if (myDiseasesData.size() != 0) {
                                tabletMainActivity.selectedDisease_position = 0;

                                for (int i = 0; i < myDiseasesData.size(); i++) {
                                    if (myDiseasesData.get(i).get_diseaseId() == TabletMainActivity.selectedDisease_id) {
                                        tabletMainActivity.selectedDisease_position = i;
                                    }
                                }

                                tabletMainActivity.myTabletHandler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        tabletMainActivity.tabletDiseasesFragment.recyclerDiseases.smoothScrollToPosition(tabletMainActivity.selectedDisease_position);
                                    }
                                }, 500);
                            }
                        }

                        // показываем МАЛЫЙ рекламный блок
                        if (adViewInTabletTreatmentFragment != null) {
                            // загружаем МАЛЫЙ рекламный блок с задержкой, чтоб успел отрисоваться
                            tabletMainActivity.myTabletHandler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    adViewInTabletTreatmentFragment.loadAd(tabletMainActivity.adRequest);
                                }
                            }, 600);
                        }

                        // скрываем БОЛЬШОЙ рекламный блок
                        if (tabletMainActivity.adViewInTabletWideView != null
                                && tabletMainActivity.adViewInTabletWideView.getVisibility() != View.GONE) {
                            TransitionManager.beginDelayedTransition(tabletMainActivity.mSceneRoot, adCloseTransition);
                            tabletMainActivity.adViewInTabletWideView.setVisibility(View.GONE);
                            tabletMainActivity.adViewInTabletWideView.pause();
                        } else {
                            tabletMainActivity.tabletUsersWideTitle.setVisibility(View.GONE);
                            treatmentPhotosFragment.verGuideline.setGuidelinePercent(1.0f);
                            tabletMainActivity.ver_3_Guideline.setGuidelinePercent(0.60f);

                            tabletMainActivity.tabletUsersWideTitle.setText("");
                            treatmentPhotosFragment.fabToFullScreen.setVisibility(View.INVISIBLE);
                            zoomInTabletTreatment.setVisibility(View.INVISIBLE);
                            zoomOutTabletTreatment.setVisibility(View.VISIBLE);
                        }

                        // код для очистки выделения фото заболевания и очистки imgWideView
                        if (treatmentPhotosFragment.txtAddPhotos.getVisibility() != View.VISIBLE) {
                            TabletMainActivity.selectedTreatmentPhoto_id = 0;
                            treatmentPhotosFragment.treatmentPhotoRecyclerViewAdapter.notifyDataSetChanged();

                            Glide.with(tabletMainActivity).clear(treatmentPhotosFragment.imgWideView);

                            treatmentPhotosFragment._idTrPhoto = 0;
                            treatmentPhotosFragment.treatmentPhotoFilePath = "";
                            treatmentPhotosFragment.textDateOfTreatmentPhoto = "";
                            treatmentPhotosFragment.textPhotoDescription = "";

                            // ширину табов делаем одинаковыми
                            LinearLayout layout = ((LinearLayout) ((LinearLayout) tabLayout.getChildAt(0)).getChildAt(1));
                            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) layout.getLayoutParams();
                            layoutParams.weight = 1.00f;
                            layout.setLayoutParams(layoutParams);
                        }
                    }
                }, 250);
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

        textInputLayoutDiseaseName.setVisibility(View.GONE);
        editTextDateOfDisease.setVisibility(View.GONE);
        focusHolder.requestFocus();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        tabletMainActivity = (TabletMainActivity) getActivity();

        TreatmentAdapter categoryAdapter = new TreatmentAdapter(tabletMainActivity, this.getChildFragmentManager());

        viewPager.setAdapter(categoryAdapter);

        tabLayout.setupWithViewPager(viewPager);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 0) {
                    // при нажатии на табы формируем внешний вид табов
                    tab.setText(menuIconWithText(getResources().getDrawable(R.drawable.ic_edit_orange_24dp),
                            getResources().getString(R.string.treatment_description)));

                    // и делаем анимацию fabEditTreatmentDescriptonShowAnimation
                    // если заболевание не в состоянии добавления или редактирования
                    if (!tabletMainActivity.newDiseaseAndTreatment) {
                        treatmentDescriptionFragment.fabEditTreatmentDescripton.startAnimation(fabEditTreatmentDescriptonShowAnimation);
                    }

                } else {
                    tab.setText(menuIconWithText(getResources().getDrawable(R.drawable.ic_camera_alt_orange_24dp),
                            getResources().getString(R.string.treatment_images)));

                    // и делаем анимацию fab если txtAddPhotos не видим
                    if (treatmentPhotosFragment.txtAddPhotos.getVisibility() != View.VISIBLE) {
                        treatmentPhotosFragment.fabAddTreatmentPhotos.startAnimation(treatmentPhotosFragment.fabAddTreatmentPhotosShowAnimation);

                        // показываем fabToFullScreen, если находимся в расширенном варианте окна
                        if (TabletMainActivity.inWideView) {
                            treatmentPhotosFragment.fabToFullScreen.startAnimation(treatmentPhotosFragment.fabToFullScreenShowAnimation);
                        }
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

        adCloseTransition = new AutoTransition();
        adCloseTransition.addListener(new Transition.TransitionListener() {
            @Override
            public void onTransitionStart(Transition transition) {

            }

            @Override
            public void onTransitionEnd(Transition transition) {
                tabletMainActivity.myTabletHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        tabletMainActivity.tabletUsersWideTitle.setVisibility(View.GONE);
                        treatmentPhotosFragment.verGuideline.setGuidelinePercent(1.0f);
                        tabletMainActivity.ver_3_Guideline.setGuidelinePercent(0.60f);
                        tabletMainActivity.tabletUsersWideTitle.setText("");
                        treatmentPhotosFragment.fabToFullScreen.setVisibility(View.INVISIBLE);
                        zoomInTabletTreatment.setVisibility(View.INVISIBLE);
                        zoomOutTabletTreatment.setVisibility(View.VISIBLE);
                    }
                }, 300);
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
        });

        // анимация для показа fabEditTreatmentDescripton
        fabEditTreatmentDescriptonShowAnimation = AnimationUtils.loadAnimation(tabletMainActivity, R.anim.fab_show);
        fabEditTreatmentDescriptonShowAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                treatmentDescriptionFragment.fabEditTreatmentDescripton.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });

        Animation fabHideAnimation = AnimationUtils.loadAnimation(tabletMainActivity, R.anim.fab_hide);
        fabHideAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                treatmentDescriptionFragment.fabEditTreatmentDescripton.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        if (adViewInTabletTreatmentFragment != null
                && !TabletMainActivity.inWideView
                && !tabletMainActivity.diseaseAndTreatmentInEdit) {

            if (tabletMainActivity.isNetworkConnected()) {
                if (adViewFrameTabletTreatmentFragment.getVisibility() == View.VISIBLE) {
                    adViewInTabletTreatmentFragment.resume();
                } else {
                    // загружаем МАЛЫЙ рекламный блок с задержкой, чтоб успел отрисоваться
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            adViewInTabletTreatmentFragment.loadAd(tabletMainActivity.adRequest);
                        }
                    }, 600);
                }
            } else {
                if (adViewFrameTabletTreatmentFragment.getVisibility() == View.VISIBLE) {
                    adViewFrameTabletTreatmentFragment.setVisibility(View.GONE);
                    adViewInTabletTreatmentFragment.pause();
                }

                TabletMainActivity.adIsShown = false;
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (adViewInTabletTreatmentFragment != null) {
            adViewInTabletTreatmentFragment.pause();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (adViewInTabletTreatmentFragment != null) {
            adViewInTabletTreatmentFragment.destroy();
        }
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

    public long get_idUser() {
        return _idUser;
    }

    public void saveDiseaseAndTreatment() {
        // устанавливаем анимацию на случай Error
        ScaleAnimation scaleAnimation = new ScaleAnimation(0f, 1f, 0f, 0f);
        scaleAnimation.setDuration(200);

        String nameToCheck = Objects.requireNonNull(editTextDiseaseName.getText()).toString().trim();
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
        tabletMainActivity.hideElementsOnTabletTreatmentFragment();

        // присваиваем стрингам textDateOfDisease, textDiseaseName и textTreatment
        // значения полей editTextDateOfDisease, editTextDiseaseName и editTextTreatment
        // для дальнейшей проверки на их изменения
        textDiseaseName = nameToCheck;
        textDateOfDisease = editTextDateOfDisease.getText().toString();
        textTreatment = Objects.requireNonNull(treatmentDescriptionFragment.editTextTreatment.getText()).toString();

        if (tabletMainActivity.newDiseaseAndTreatment) {

            zoomOutTabletTreatment.setVisibility(View.VISIBLE);

            // сохранять в базу в отдельном треде
            saveDiseaseAndTreatmentToDataBase();
        } else {

            zoomInTabletTreatment.setVisibility(View.VISIBLE);

            // т.к. после обновления заболевания в планшетном виде остаемся в inWideView, открываем БОЛЬШОЙ рекламный блок
            // рекламу грузим с задержкой, чтоб успела отрисоваться
            tabletMainActivity.myTabletHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    tabletMainActivity.adViewInTabletWideView.loadAd(tabletMainActivity.adRequest);
                }
            }, 600);

            // обновлять в базу в отдельном треде
            updateDiseaseAndTreatmentToDataBase();
        }
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

        } else {
            Toast.makeText(tabletMainActivity, R.string.treatment_cant_save, Toast.LENGTH_LONG).show();
        }

        tabletMainActivity.treatmentOnSavingOrUpdatingOrDeleting = false;

        tabletMainActivity.tabletDiseasesFragment.initDiseasesLoader();

        // здесь обновляются _idUser и _idDisease в treatmentPhotosFragment
        treatmentPhotosFragment.initTreatmentPhotosLoader();

        treatmentDescriptionFragment.fabEditTreatmentDescripton.startAnimation(fabEditTreatmentDescriptonShowAnimation);

        // т.к. после сохранения нового заболевания в планшетном виде переходим в НЕ inWideView, открываем МАЛЫЙ рекламный блок
        tabletMainActivity.myTabletHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (adViewInTabletTreatmentFragment != null) {
                    // загружаем МАЛЫЙ рекламный блок с задержкой, чтоб успел отрисоваться
                    adViewInTabletTreatmentFragment.loadAd(tabletMainActivity.adRequest);
                }
            }
        }, 600);
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
            tabletMainActivity.tabletTreatmentTitle.setText(Objects.requireNonNull(tabletMainActivity.tabletTreatmentFragment.editTextDiseaseName.getText()).toString());
        }

        tabletMainActivity.treatmentOnSavingOrUpdatingOrDeleting = false;

        tabletMainActivity.tabletDiseasesFragment.initDiseasesLoader();
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
        ArrayList<String> photoFilePathsToBeDeletedList = new ArrayList<>();

        if (cursor != null) {
            // устанавливаем курсор на исходную (на случай, если курсор используем повторно после прохождения цикла
            cursor.moveToPosition(-1);

            // проходим в цикле курсор
            // и добаляем пути к удаляемым файлам в ArrayList<String> photoFilePathsToBeDeletedList
            while (cursor.moveToNext()) {
                int trPhoto_pathColumnIndex = cursor.getColumnIndex(MedContract.TreatmentPhotosEntry.COLUMN_TR_PHOTO_PATH);
                String trPhotoUri = cursor.getString(trPhoto_pathColumnIndex);

                photoFilePathsToBeDeletedList.add(trPhotoUri);
            }
        }

        // делаем destroyLoader, чтоб он сам повторно не вызывался
        getLoaderManager().destroyLoader(TR_PHOTOS_LOADER);

        // Запускаем AsyncTask для удаления строк из таблиц treatmentPhotos и diseases
        // а далее, и для удаления файлов
        new TabletTreatmentFragment.DiseaseAndTreatmentPhotosDeletingAsyncTask(
                tabletMainActivity, photoFilePathsToBeDeletedList).execute(tabletMainActivity.getApplicationContext());
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
        private final ArrayList<String> mPhotoFilePathsListToBeDeleted;
        private int mRowsFromTreatmentPhotosDeleted = -1;

        // в конструкторе получаем WeakReference<TreatmentActivity>
        // и образовываем список ArrayList<String> mPhotoFilePathsListToBeDeleted на основании полученного photoFilePathesListToBeDeleted
        // это список путей к файлам, которые необходимо будет удалить
        // тоесть наш mPhotoFilePathsListToBeDeleted НЕ зависим от полученного photoFilePathesListToBeDeleted
        DiseaseAndTreatmentPhotosDeletingAsyncTask(TabletMainActivity context, ArrayList<String> photoFilePathesListToBeDeleted) {
            asinkTabletMainActivity = new WeakReference<>(context);
            mPhotoFilePathsListToBeDeleted = new ArrayList<>(photoFilePathesListToBeDeleted);
        }

        // в onPreExecute получаем  TreatmentActivity treatmentActivity
        // и если он null, то никакое удаление не происходит
        // если же treatmentActivity не null,
        // то в основном треде удаляем строки из таблиц treatmentPhotos и diseases в одной транзакции
        // при этом, получаем (как резульат удаления строк из таблицы treatmentPhotos) количество удаленных строк
        // по сути, это количество должно совпадать с количеством элементов в mPhotoFilePathsListToBeDeleted
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
        // по списку путей к фотографиям из mPhotoFilePathsListToBeDeleted
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

                for (String fPath : mPhotoFilePathsListToBeDeleted) {
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
                    // ытягиваем в String notDeletedFilesPaths из prefs пути к ранее не удаленным файлам
                    String notDeletedFilesPaths = prefs.getString("notDeletedFilesPaths", null);

                    // если из prefs вытянулись пути к ранее не удаленным файлам,
                    // то цепляем их в конец sb за запятой
                    if (notDeletedFilesPaths != null && notDeletedFilesPaths.length() != 0) {
                        sb.append(notDeletedFilesPaths);
                    } else {
                        // если в prefs не было путей к ранее не удаленным файлам,
                        // то убираем с конца sb запятую
                        sb.deleteCharAt(sb.length() - 1);
                    }

                    // пишем в поле notDeletedFilesPaths новую строку путей к неудаленным файлам, разделенных запятой
                    // при этом старая строка в prefs заменится новой строкой
                    // и выходим с return 0,
                    // что означает, что были файлы, которые не удалились

                    prefsEditor.putString("notDeletedFilesPaths", sb.toString());
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
