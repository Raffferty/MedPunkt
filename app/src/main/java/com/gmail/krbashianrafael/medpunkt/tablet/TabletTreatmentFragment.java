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

    private static final int TR_PHOTOS_LOADER = 22;

    public TreatmentDescriptionFragment treatmentDescriptionFragment;
    public TreatmentPhotosFragment treatmentPhotosFragment;

    public long _idUser = 0;

    public long _idDisease = 0;

    public boolean editDisease = false;

    public String textDiseaseName = "";
    private String textDateOfDisease = "";
    public String textTreatment = "";

    public ImageView zoomOutTabletTreatment, zoomInTabletTreatment;

    public TextInputLayout textInputLayoutDiseaseName;
    public TextInputEditText editTextDiseaseName;
    public EditText editTextDateOfDisease;
    public EditText focusHolder;

    public Animation fabEditTreatmentDescriptonShowAnimation;

    public ViewPager viewPager;

    public TabLayout tabLayout;

    public FrameLayout adViewFrameTabletTreatmentFragment;
    public AdView adViewInTabletTreatmentFragment;
    public boolean tabletSmallAdOpened = false;

    private AutoTransition adCloseTransition;

    public TabletTreatmentFragment() {
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


        adViewFrameTabletTreatmentFragment = view.findViewById(R.id.adViewFrameTabletTreatment);
        adViewInTabletTreatmentFragment = view.findViewById(R.id.adViewInTabletTreatment);
        adViewInTabletTreatmentFragment.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                if (!TabletMainActivity.inWideView){
                    TransitionManager.beginDelayedTransition(tabletMainActivity.mSceneRoot);
                    adViewFrameTabletTreatmentFragment.setVisibility(View.VISIBLE);
                    TabletMainActivity.adIsShown = true;

                    tabletSmallAdOpened = false;

                } else {
                    adViewFrameTabletTreatmentFragment.setVisibility(View.GONE);
                    adViewInTabletTreatmentFragment.pause();
                    TabletMainActivity.adIsShown = false;

                    tabletSmallAdOpened = false;
                }
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                adViewFrameTabletTreatmentFragment.setVisibility(View.GONE);
                adViewInTabletTreatmentFragment.pause();
                TabletMainActivity.adIsShown = false;

                tabletSmallAdOpened = false;
            }

            @Override
            public void onAdOpened() {
                adViewFrameTabletTreatmentFragment.setVisibility(View.GONE);
                adViewInTabletTreatmentFragment.pause();
                TabletMainActivity.adIsShown = false;

                tabletSmallAdOpened = true;
            }

            @Override
            public void onAdLeftApplication() {
            }

            @Override
            public void onAdClosed() {
            }
        });

        FrameLayout frmDividerGreen = view.findViewById(R.id.divider_frame_white);
        frmDividerGreen.setVisibility(View.GONE);

        FrameLayout frmDividerBlue = view.findViewById(R.id.divider_frame_blue);
        frmDividerBlue.setVisibility(View.GONE);

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

                if (tabletMainActivity.adViewInTabletWideView != null
                        && tabletMainActivity.isNetworkConnected()) {
                    tabletMainActivity.myTabletHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            tabletMainActivity.adViewInTabletWideView.loadAd(tabletMainActivity.adRequest);
                        }
                    }, 600);
                }

                tabletMainActivity.myTabletHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        adViewFrameTabletTreatmentFragment.setVisibility(View.GONE);
                        if (adViewInTabletTreatmentFragment != null) {
                            adViewInTabletTreatmentFragment.pause();
                        }

                        zoomInTabletTreatment.setVisibility(View.VISIBLE);
                        zoomOutTabletTreatment.setVisibility(View.INVISIBLE);

                        tabletMainActivity.ver_3_Guideline.setGuidelinePercent(0.00f);
                        tabletMainActivity.tabletUsersWideTitle.setText(tabletMainActivity.tabletDiseasesTitle.getText().toString());
                        tabletMainActivity.tabletUsersWideTitle.setVisibility(View.VISIBLE);

                        if (treatmentPhotosFragment.treatmentPhotoRecyclerViewAdapter.
                                getTreatmentPhotosList().size() != 0) {

                            treatmentPhotosFragment.verGuideline.setGuidelinePercent(0.4f);
                            treatmentPhotosFragment.fabToFullScreen.startAnimation(treatmentPhotosFragment.fabToFullScreenShowAnimation);

                            LinearLayout layout = ((LinearLayout) ((LinearLayout) tabLayout.getChildAt(0)).getChildAt(1));
                            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) layout.getLayoutParams();
                            layoutParams.weight = 1.50f;
                            layout.setLayoutParams(layoutParams);

                            TreatmentPhotoItem treatmentPhotoItem = treatmentPhotosFragment.treatmentPhotoRecyclerViewAdapter.getTreatmentPhotosList().get(0);

                            treatmentPhotosFragment._idTrPhoto = treatmentPhotoItem.get_trPhotoId();
                            treatmentPhotosFragment.treatmentPhotoFilePath = treatmentPhotoItem.getTrPhotoUri();
                            treatmentPhotosFragment.textDateOfTreatmentPhoto = treatmentPhotoItem.getTrPhotoDate();
                            treatmentPhotosFragment.textPhotoDescription = treatmentPhotoItem.getTrPhotoName();

                            TabletMainActivity.selectedTreatmentPhoto_id = treatmentPhotosFragment._idTrPhoto;

                            treatmentPhotosFragment.treatmentPhotoRecyclerViewAdapter.notifyDataSetChanged();
                        }
                    }
                }, 250);
            }
        });

        zoomInTabletTreatment = view.findViewById(R.id.img_zoom_in_tablet_treatment);
        zoomInTabletTreatment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (tabletMainActivity == null) {
                    return;
                }

                tabletMainActivity.tabletBigAdOpened = false;

                TabletMainActivity.inWideView = false;

                tabletMainActivity.myTabletHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {

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

                        if (adViewInTabletTreatmentFragment != null
                                && tabletMainActivity.isNetworkConnected()) {
                            tabletMainActivity.myTabletHandler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    adViewInTabletTreatmentFragment.loadAd(tabletMainActivity.adRequest);
                                }
                            }, 600);
                        }

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

                        if (treatmentPhotosFragment.txtAddPhotos.getVisibility() != View.VISIBLE) {
                            TabletMainActivity.selectedTreatmentPhoto_id = 0;
                            treatmentPhotosFragment.treatmentPhotoRecyclerViewAdapter.notifyDataSetChanged();

                            Glide.with(tabletMainActivity).clear(treatmentPhotosFragment.imgWideView);

                            treatmentPhotosFragment._idTrPhoto = 0;
                            treatmentPhotosFragment.treatmentPhotoFilePath = "";
                            treatmentPhotosFragment.textDateOfTreatmentPhoto = "";
                            treatmentPhotosFragment.textPhotoDescription = "";

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

                textInputLayoutDiseaseName.setError(null);
                textInputLayoutDiseaseName.setErrorEnabled(false);
                textInputLayoutDiseaseName.setHintTextAppearance(R.style.Lable);

                editTextDateOfDisease.setTextColor(getResources().getColor(R.color.colorPrimaryDark));

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
                    DatePickerFragment newFragment = new DatePickerFragment();
                    newFragment.show(tabletMainActivity.getSupportFragmentManager(), "datePicker");
                }
            }
        });

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
                    tab.setText(menuIconWithText(getResources().getDrawable(R.drawable.ic_edit_orange_24dp),
                            getResources().getString(R.string.treatment_description)));

                    if (!tabletMainActivity.newDiseaseAndTreatment) {
                        treatmentDescriptionFragment.fabEditTreatmentDescripton.startAnimation(fabEditTreatmentDescriptonShowAnimation);
                    }

                } else {
                    tab.setText(menuIconWithText(getResources().getDrawable(R.drawable.ic_camera_alt_orange_24dp),
                            getResources().getString(R.string.treatment_images)));

                    if (treatmentPhotosFragment.txtAddPhotos.getVisibility() != View.VISIBLE) {
                        treatmentPhotosFragment.fabAddTreatmentPhotos.startAnimation(treatmentPhotosFragment.fabAddTreatmentPhotosShowAnimation);

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
                    treatmentDescriptionFragment.editTextTreatment.setFocusable(true);
                    treatmentDescriptionFragment.editTextTreatment.setFocusableInTouchMode(true);
                    treatmentDescriptionFragment.editTextTreatment.requestFocus();
                    treatmentDescriptionFragment.editTextTreatment.setSelection(0);

                    treatmentDescriptionFragment.editTextTreatment.setFocusable(false);
                    treatmentDescriptionFragment.editTextTreatment.setFocusableInTouchMode(false);
                    focusHolder.requestFocus();

                } else {
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
                if (TabletMainActivity.adIsShown) {
                    adViewInTabletTreatmentFragment.resume();
                } else {
                    if (TabletDiseasesFragment.diseaseSelected) {
                        if (!tabletSmallAdOpened){
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    adViewInTabletTreatmentFragment.loadAd(tabletMainActivity.adRequest);
                                }
                            }, 600);
                        }
                    }
                }
            } else {
                if (TabletMainActivity.adIsShown) {
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
        ScaleAnimation scaleAnimation = new ScaleAnimation(0f, 1f, 0f, 0f);
        scaleAnimation.setDuration(200);

        String nameToCheck = Objects.requireNonNull(editTextDiseaseName.getText()).toString().trim();
        String dateOfDiseaseToCheck = editTextDateOfDisease.getText().toString();
        boolean wrongField = false;

        if (TextUtils.isEmpty(nameToCheck)) {
            textInputLayoutDiseaseName.setHintTextAppearance(R.style.Lable_Error);
            textInputLayoutDiseaseName.setError(getString(R.string.disease_error_name));
            editTextDiseaseName.startAnimation(scaleAnimation);
            editTextDiseaseName.requestFocus();

            wrongField = true;
        }

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

        if (wrongField) {
            tabletMainActivity.treatmentOnSavingOrUpdatingOrDeleting = false;
            return;
        } else {
            textInputLayoutDiseaseName.setError(null);
        }

        tabletMainActivity.hideElementsOnTabletTreatmentFragment();

        textDiseaseName = nameToCheck;
        textDateOfDisease = editTextDateOfDisease.getText().toString();
        textTreatment = Objects.requireNonNull(treatmentDescriptionFragment.editTextTreatment.getText()).toString();

        if (tabletMainActivity.newDiseaseAndTreatment) {

            zoomOutTabletTreatment.setVisibility(View.VISIBLE);

            saveDiseaseAndTreatmentToDataBase();
        } else {

            zoomInTabletTreatment.setVisibility(View.VISIBLE);

            tabletMainActivity.tabletBigAdOpened = false;

            tabletMainActivity.myTabletHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    tabletMainActivity.adViewInTabletWideView.loadAd(tabletMainActivity.adRequest);
                }
            }, 600);

            updateDiseaseAndTreatmentToDataBase();
        }
    }

    private void saveDiseaseAndTreatmentToDataBase() {
        ContentValues values = new ContentValues();
        values.put(MedContract.DiseasesEntry.COLUMN_U_ID, _idUser);
        values.put(MedContract.DiseasesEntry.COLUMN_DISEASE_NAME, textDiseaseName);
        values.put(MedContract.DiseasesEntry.COLUMN_DISEASE_DATE, textDateOfDisease);
        values.put(MedContract.DiseasesEntry.COLUMN_DISEASE_TREATMENT, textTreatment);

        Uri newUri = tabletMainActivity.getContentResolver().insert(MedContract.DiseasesEntry.CONTENT_DISEASES_URI, values);

        if (newUri != null) {
            _idDisease = ContentUris.parseId(newUri);

        } else {
            Toast.makeText(tabletMainActivity, R.string.treatment_cant_save, Toast.LENGTH_LONG).show();
        }

        tabletMainActivity.treatmentOnSavingOrUpdatingOrDeleting = false;

        tabletMainActivity.tabletDiseasesFragment.initDiseasesLoader();

        treatmentPhotosFragment.initTreatmentPhotosLoader();

        treatmentDescriptionFragment.fabEditTreatmentDescripton.startAnimation(fabEditTreatmentDescriptonShowAnimation);

        tabletMainActivity.myTabletHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (adViewInTabletTreatmentFragment != null) {
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

        Uri mCurrentUserUri = Uri.withAppendedPath(MedContract.DiseasesEntry.CONTENT_DISEASES_URI, String.valueOf(_idDisease));

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
        String[] projection = {
                MedContract.TreatmentPhotosEntry.TR_PHOTO_ID,
                MedContract.TreatmentPhotosEntry.COLUMN_TR_PHOTO_PATH};

        String selection = MedContract.TreatmentPhotosEntry.COLUMN_DIS_ID + "=?";
        String[] selectionArgs = new String[]{String.valueOf(_idDisease)};

        return new CursorLoader(tabletMainActivity,
                MedContract.TreatmentPhotosEntry.CONTENT_TREATMENT_PHOTOS_URI,
                projection,
                selection,
                selectionArgs,
                null);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor cursor) {
        ArrayList<String> photoFilePathsToBeDeletedList = new ArrayList<>();

        if (cursor != null) {
            cursor.moveToPosition(-1);

            while (cursor.moveToNext()) {
                int trPhoto_pathColumnIndex = cursor.getColumnIndex(MedContract.TreatmentPhotosEntry.COLUMN_TR_PHOTO_PATH);
                String trPhotoUri = cursor.getString(trPhoto_pathColumnIndex);

                photoFilePathsToBeDeletedList.add(trPhotoUri);
            }
        }

        getLoaderManager().destroyLoader(TR_PHOTOS_LOADER);

        new TabletTreatmentFragment.DiseaseAndTreatmentPhotosDeletingAsyncTask(
                tabletMainActivity, photoFilePathsToBeDeletedList).execute(tabletMainActivity.getApplicationContext());
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
    }

    private static class DiseaseAndTreatmentPhotosDeletingAsyncTask extends AsyncTask<Context, Void, Integer> {

        private static final String PREFS_NAME = "PREFS";

        private final WeakReference<TabletMainActivity> asinkTabletMainActivity;
        private final ArrayList<String> mPhotoFilePathsListToBeDeleted;
        private int mRowsFromTreatmentPhotosDeleted = -1;

        DiseaseAndTreatmentPhotosDeletingAsyncTask(TabletMainActivity context, ArrayList<String> photoFilePathesListToBeDeleted) {
            asinkTabletMainActivity = new WeakReference<>(context);
            mPhotoFilePathsListToBeDeleted = new ArrayList<>(photoFilePathesListToBeDeleted);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            TabletMainActivity mTabletMainActivity = asinkTabletMainActivity.get();
            if (mTabletMainActivity == null) {
                return;
            }

            mRowsFromTreatmentPhotosDeleted = deleteDiseaseAndTreatmentPhotosFromDataBase(mTabletMainActivity);
        }

        private int deleteDiseaseAndTreatmentPhotosFromDataBase(TabletMainActivity mTabletMainActivity) {
            ArrayList<ContentProviderOperation> deletingFromDbOperations = new ArrayList<>();

            String selectionTrPhotos = MedContract.TreatmentPhotosEntry.COLUMN_DIS_ID + "=?";
            String[] selectionArgsTrPhotos = new String[]{String.valueOf(mTabletMainActivity.tabletTreatmentFragment._idDisease)};

            ContentProviderOperation deleteTreatmentPhotosFromDbOperation = ContentProviderOperation
                    .newDelete(MedContract.TreatmentPhotosEntry.CONTENT_TREATMENT_PHOTOS_URI)
                    .withSelection(selectionTrPhotos, selectionArgsTrPhotos)
                    .build();

            deletingFromDbOperations.add(deleteTreatmentPhotosFromDbOperation);

            String selectionDisease = MedContract.DiseasesEntry.DIS_ID + "=?";
            String[] selectionArgsDisease = new String[]{String.valueOf(mTabletMainActivity.tabletTreatmentFragment._idDisease)};

            ContentProviderOperation deleteDiseaseFromDbOperation = ContentProviderOperation
                    .newDelete(MedContract.DiseasesEntry.CONTENT_DISEASES_URI)
                    .withSelection(selectionDisease, selectionArgsDisease)
                    .build();

            deletingFromDbOperations.add(deleteDiseaseFromDbOperation);

            int rowsFromTreatmentPhotosDeleted = -1;

            try {
                ContentProviderResult[] results = mTabletMainActivity.getContentResolver().applyBatch(MedContract.CONTENT_AUTHORITY, deletingFromDbOperations);

                if (results.length == 2 && results[0] != null) {
                    rowsFromTreatmentPhotosDeleted = results[0].count;
                } else {
                    return rowsFromTreatmentPhotosDeleted;
                }
            } catch (RemoteException | OperationApplicationException e) {
                e.printStackTrace();
                return rowsFromTreatmentPhotosDeleted;
            }

            return rowsFromTreatmentPhotosDeleted;
        }

        @Override
        protected Integer doInBackground(Context... contexts) {
            if (mRowsFromTreatmentPhotosDeleted == -1) {
                return -1;
            } else if (mRowsFromTreatmentPhotosDeleted == 0) {
                return 1;
            } else {
                Context mContext = contexts[0];

                if (mContext == null) {
                    return 0;
                }

                SharedPreferences prefs = mContext.getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
                final SharedPreferences.Editor prefsEditor = prefs.edit();

                StringBuilder sb = new StringBuilder();

                for (String fPath : mPhotoFilePathsListToBeDeleted) {
                    File toBeDeletedFile = new File(fPath);

                    if (toBeDeletedFile.exists()) {
                        if (!toBeDeletedFile.delete()) {
                            sb.append(fPath).append(",");
                        }
                    }
                }

                if (sb.length() > 0) {
                    String notDeletedFilesPaths = prefs.getString("notDeletedFilesPaths", null);

                    if (notDeletedFilesPaths != null && notDeletedFilesPaths.length() != 0) {
                        sb.append(notDeletedFilesPaths);
                    } else {
                        sb.deleteCharAt(sb.length() - 1);
                    }

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
                Toast.makeText(mTabletMainActivity, R.string.disease_not_deleted, Toast.LENGTH_LONG).show();
            } else {
                mTabletMainActivity.tabletDiseasesFragment.initDiseasesLoader();
                mTabletMainActivity.tabletUsersFragment.fabAddUser.startAnimation(mTabletMainActivity.tabletUsersFragment.fabShowAnimation);
            }

            mTabletMainActivity.treatmentOnSavingOrUpdatingOrDeleting = false;
        }
    }
}
