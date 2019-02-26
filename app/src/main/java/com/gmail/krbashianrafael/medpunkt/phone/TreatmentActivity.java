package com.gmail.krbashianrafael.medpunkt.phone;

import android.annotation.SuppressLint;
import android.app.LoaderManager;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.OperationApplicationException;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.design.widget.TabLayout;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ImageSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.ScaleAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.gmail.krbashianrafael.medpunkt.R;
import com.gmail.krbashianrafael.medpunkt.data.MedContract;
import com.gmail.krbashianrafael.medpunkt.data.MedContract.DiseasesEntry;
import com.gmail.krbashianrafael.medpunkt.data.MedContract.TreatmentPhotosEntry;
import com.gmail.krbashianrafael.medpunkt.shared.DatePickerFragment;
import com.gmail.krbashianrafael.medpunkt.shared.HomeActivity;
import com.gmail.krbashianrafael.medpunkt.shared.TreatmentAdapter;
import com.gmail.krbashianrafael.medpunkt.shared.TreatmentDescriptionFragment;
import com.gmail.krbashianrafael.medpunkt.shared.TreatmentPhotosFragment;
import com.tsongkha.spinnerdatepicker.DatePicker;
import com.tsongkha.spinnerdatepicker.DatePickerDialog;
import com.tsongkha.spinnerdatepicker.SpinnerDatePickerDialogBuilder;

import java.io.File;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.Objects;

@SuppressLint("RestrictedApi")
public class TreatmentActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor>,
        DatePickerDialog.OnDateSetListener {

    private static final int TR_PHOTOS_LOADER = 22;

    public TreatmentDescriptionFragment treatmentDescriptionFragment;
    public TreatmentPhotosFragment treatmentPhotosFragment;

    public long _idUser = 0;

    public long _idDisease = 0;

    private boolean goBack, onSavingOrUpdatingOrDeleting;
    public boolean editDisease;
    public boolean newDisease;

    private ActionBar actionBar;

    private String textDiseaseName = "";
    private String textDateOfDisease = "";
    public String textTreatment = "";

    public TextView txtTitleDisease;

    public TextInputLayout textInputLayoutDiseaseName;
    public TextInputEditText editTextDiseaseName;
    public EditText editTextDateOfDisease;
    private EditText focusHolder;

    private Animation fabShowAnimation;

    private ViewPager viewPager;

    private TreatmentAdapter categoryAdapter;

    public TabLayout tabLayout;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (HomeActivity.isTablet) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

            getWindow().setSoftInputMode(
                    WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN |
                            WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN
            );
        }

        setContentView(R.layout.activity_treatment);

        Intent intent = getIntent();

        _idUser = intent.getLongExtra("_idUser", 0);

        _idDisease = intent.getLongExtra("_idDisease", 0);

        if (intent.hasExtra("diseaseDate")) {
            textDateOfDisease = intent.getStringExtra("diseaseDate");
        } else {
            textDateOfDisease = getString(R.string.disease_date);
        }

        if (intent.hasExtra("diseaseName")) {
            textDiseaseName = intent.getStringExtra("diseaseName");
        }

        if (intent.hasExtra("textTreatment")) {
            textTreatment = intent.getStringExtra("textTreatment");
        }

        editDisease = intent.getBooleanExtra("editDisease", false);

        newDisease = intent.getBooleanExtra("newDisease", false);

        txtTitleDisease = findViewById(R.id.txt_title_disease);
        if (!newDisease) {
            txtTitleDisease.setText(textDiseaseName);
            txtTitleDisease.setVisibility(View.VISIBLE);
        }

        TextView txtTitleTreatment = findViewById(R.id.txt_title_treatment);

        if (HomeActivity.iAmDoctor) {
            txtTitleTreatment.setText(R.string.patient_treatment_title_text);
        } else {
            txtTitleTreatment.setText(R.string.treatment_title_text);
        }

        textInputLayoutDiseaseName = findViewById(R.id.text_input_layout_disease_name);
        editTextDiseaseName = findViewById(R.id.editText_disease_name);

        editTextDateOfDisease = findViewById(R.id.editText_date);
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
                            .context(TreatmentActivity.this)
                            .callback(TreatmentActivity.this)
                            .spinnerTheme(R.style.NumberPickerStyle)
                            .defaultDate(mYear, mMonth, mDay)
                            .build();

                    spinnerDatePickerDialog.setCanceledOnTouchOutside(false);
                    spinnerDatePickerDialog.show();
                } else {
                    DatePickerFragment newFragment = new DatePickerFragment();
                    newFragment.show(getSupportFragmentManager(), "datePicker");
                }
            }
        });

        actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_30dp);

            if (!newDisease) {
                actionBar.setTitle(DiseasesActivity.textUserName);
            }
        }

        editTextDiseaseName.setText(textDiseaseName);

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

        focusHolder = findViewById(R.id.focus_holder);
        focusHolder.requestFocus();

        fabShowAnimation = AnimationUtils.loadAnimation(this, R.anim.fab_show);
        fabShowAnimation.setAnimationListener(new Animation.AnimationListener() {
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

        categoryAdapter = new TreatmentAdapter(this, getSupportFragmentManager());

        viewPager = findViewById(R.id.viewpager);

        tabLayout = findViewById(R.id.tabs);

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

        viewPager.setAdapter(categoryAdapter);
        tabLayout.setupWithViewPager(viewPager);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 0) {
                    tab.setText(menuIconWithText(getResources().getDrawable(R.drawable.ic_edit_orange_24dp),
                            getResources().getString(R.string.treatment_description)));

                    treatmentDescriptionFragment.fabEditTreatmentDescripton.startAnimation(fabShowAnimation);

                } else {
                    tab.setText(menuIconWithText(getResources().getDrawable(R.drawable.ic_camera_alt_orange_24dp),
                            getResources().getString(R.string.treatment_images)));

                    if (treatmentPhotosFragment.txtAddPhotos.getVisibility() != View.VISIBLE) {
                        treatmentPhotosFragment.fabAddTreatmentPhotos.startAnimation(treatmentPhotosFragment.fabAddTreatmentPhotosShowAnimation);
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
    }

    @SuppressLint("SetTextI18n")
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
        GregorianCalendar date = new GregorianCalendar(year, monthOfYear, dayOfMonth);
        editTextDateOfDisease.setText(simpleDateFormat.format(date.getTime()) + " ");
    }

    public void initTreatmentDescriptionFragment() {
        treatmentDescriptionFragment = (TreatmentDescriptionFragment) getSupportFragmentManager().getFragments().get(0);
    }

    public void initTreatmentPhotosFragment() {
        treatmentPhotosFragment = (TreatmentPhotosFragment) getSupportFragmentManager().getFragments().get(1);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);

        menu.removeItem(R.id.action_delete);
        menu.add(0, R.id.action_delete, 3, menuIconWithText(getResources().getDrawable(R.drawable.ic_delete_red_24dp),
                getResources().getString(R.string.disease_delete)));

        return true;
    }

    private CharSequence menuIconWithText(Drawable r, String title) {
        r.setBounds(0, 0, r.getIntrinsicWidth(), r.getIntrinsicHeight());
        SpannableString sb = new SpannableString("    " + title);
        ImageSpan imageSpan = new ImageSpan(r, ImageSpan.ALIGN_BOTTOM);
        sb.setSpan(imageSpan, 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        return sb;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        if (!editDisease) {
            MenuItem menuItemSave = menu.getItem(1);
            menuItemSave.setVisible(false);
        } else {
            MenuItem menuItemDelete = menu.getItem(0);
            menuItemDelete.setVisible(false);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                if (diseaseAndTreatmentHasNotChanged()) {
                    goToDiseasesActivity();
                    return true;
                }

                hideSoftInput();

                textInputLayoutDiseaseName.setError(null);

                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                goToDiseasesActivity();
                            }
                        };

                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
            case R.id.action_save:

                View viewToHide = this.getCurrentFocus();
                if (viewToHide != null) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm != null) {
                        imm.hideSoftInputFromWindow(viewToHide.getWindowToken(), 0);
                    }
                }

                if (onSavingOrUpdatingOrDeleting) {
                    return true;
                }

                onSavingOrUpdatingOrDeleting = true;

                if (newDisease) {
                    actionBar.setTitle(DiseasesActivity.textUserName);
                }

                if (diseaseAndTreatmentHasNotChanged() && !newDisease) {

                    txtTitleDisease.setVisibility(View.VISIBLE);

                    if (!HomeActivity.isTablet) {
                        categoryAdapter.setPagesCount(2);
                        viewPager.setAdapter(categoryAdapter);
                        tabLayout.setVisibility(View.VISIBLE);
                    }

                    editDisease = false;

                    invalidateOptionsMenu();

                    treatmentDescriptionFragment.fabEditTreatmentDescripton.startAnimation(fabShowAnimation);

                    onSavingOrUpdatingOrDeleting = false;

                    textInputLayoutDiseaseName.setVisibility(View.GONE);
                    editTextDateOfDisease.setVisibility(View.GONE);

                    treatmentDescriptionFragment.editTextTreatment.requestFocus();
                    treatmentDescriptionFragment.editTextTreatment.setSelection(0);
                    treatmentDescriptionFragment.editTextTreatment.setFocusable(false);
                    treatmentDescriptionFragment.editTextTreatment.setFocusableInTouchMode(false);
                    treatmentDescriptionFragment.editTextTreatment.setCursorVisible(false);

                    focusHolder.requestFocus();

                } else {
                    focusHolder.requestFocus();
                    saveDiseaseAndTreatment();
                }

                return true;
            case R.id.action_delete:
                if (onSavingOrUpdatingOrDeleting) {
                    return true;
                }

                hideSoftInput();

                showDeleteConfirmationDialog();

                return true;

            default:
                super.onOptionsItemSelected(item);
                hideSoftInput();
                finish();
                return true;
        }
    }

    @Override
    public void onBackPressed() {
        if (diseaseAndTreatmentHasNotChanged()) {
            super.onBackPressed();
            goToDiseasesActivity();
            return;
        }

        hideSoftInput();

        textInputLayoutDiseaseName.setError(null);

        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        hideSoftInput();
                        finish();
                    }
                };

        showUnsavedChangesDialog(discardButtonClickListener);
    }

    private void showUnsavedChangesDialog(DialogInterface.OnClickListener discardButtonClickListener) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AlertDialogCustom);
        builder.setMessage(R.string.dialog_msg_unsaved_changes);

        builder.setNegativeButton(R.string.dialog_no, discardButtonClickListener);

        builder.setPositiveButton(R.string.dialog_yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                goBack = true;
                saveDiseaseAndTreatment();

                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();
    }

    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.DeleteAlertDialogCustom);
        builder.setMessage(getString(R.string.disease_delete) + " " + editTextDiseaseName.getText() + "?");
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (onSavingOrUpdatingOrDeleting) {
                    if (dialog != null) {
                        dialog.dismiss();
                    }
                } else {
                    onSavingOrUpdatingOrDeleting = true;
                    deleteDiseaseAndTreatmentPhotos();
                }
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    onSavingOrUpdatingOrDeleting = false;
                    dialog.dismiss();
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();
    }

    private void saveDiseaseAndTreatment() {

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
            onSavingOrUpdatingOrDeleting = false;
            return;
        } else {
            textInputLayoutDiseaseName.setError(null);
        }

        textDiseaseName = nameToCheck;
        textDateOfDisease = editTextDateOfDisease.getText().toString();
        textTreatment = Objects.requireNonNull(treatmentDescriptionFragment.editTextTreatment.getText()).toString();

        if (goBack) {
            if (newDisease) {
                saveDiseaseAndTreatmentToDataBase();
            } else {
                updateDiseaseAndTreatmentToDataBase();
            }

            onSavingOrUpdatingOrDeleting = false;

            goToDiseasesActivity();

        } else {
            if (newDisease) {
                newDisease = false;

                saveDiseaseAndTreatmentToDataBase();
            } else {
                updateDiseaseAndTreatmentToDataBase();
            }

            onSavingOrUpdatingOrDeleting = false;

            if (!HomeActivity.isTablet) {
                categoryAdapter.setPagesCount(2);
                viewPager.setAdapter(categoryAdapter);
                tabLayout.setVisibility(View.VISIBLE);
            }

            txtTitleDisease.setText(textDiseaseName);
            txtTitleDisease.setVisibility(View.VISIBLE);

            editDisease = false;
            textInputLayoutDiseaseName.setVisibility(View.GONE);
            editTextDateOfDisease.setVisibility(View.GONE);

            treatmentDescriptionFragment.editTextTreatment.setSelection(0);
            treatmentDescriptionFragment.editTextTreatment.setFocusable(false);
            treatmentDescriptionFragment.editTextTreatment.setFocusableInTouchMode(false);
            treatmentDescriptionFragment.editTextTreatment.setCursorVisible(false);

            focusHolder.requestFocus();

            invalidateOptionsMenu();

            treatmentDescriptionFragment.fabEditTreatmentDescripton.startAnimation(fabShowAnimation);
        }
    }

    private boolean diseaseAndTreatmentHasNotChanged() {
        return TextUtils.equals(Objects.requireNonNull(editTextDiseaseName.getText()).toString(), textDiseaseName) &&
                TextUtils.equals(editTextDateOfDisease.getText(), textDateOfDisease) &&
                TextUtils.equals(treatmentDescriptionFragment.editTextTreatment.getText(), textTreatment);
    }

    private void goToDiseasesActivity() {
        hideSoftInput();
        finish();
    }

    private void hideSoftInput() {
        View viewToHide = this.getCurrentFocus();
        if (viewToHide != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(viewToHide.getWindowToken(), 0);
            }
        }
    }

    private void saveDiseaseAndTreatmentToDataBase() {
        ContentValues values = new ContentValues();
        values.put(DiseasesEntry.COLUMN_U_ID, _idUser);
        values.put(DiseasesEntry.COLUMN_DISEASE_NAME, textDiseaseName);
        values.put(DiseasesEntry.COLUMN_DISEASE_DATE, textDateOfDisease);
        values.put(DiseasesEntry.COLUMN_DISEASE_TREATMENT, textTreatment);

        Uri newUri = getContentResolver().insert(DiseasesEntry.CONTENT_DISEASES_URI, values);

        if (newUri != null) {
            _idDisease = ContentUris.parseId(newUri);
            DiseasesActivity.mScrollToStart = true;
        } else {
            Toast.makeText(this, R.string.treatment_cant_save, Toast.LENGTH_LONG).show();
        }
    }

    private void updateDiseaseAndTreatmentToDataBase() {
        ContentValues values = new ContentValues();
        values.put(DiseasesEntry.COLUMN_DISEASE_NAME, textDiseaseName);
        values.put(DiseasesEntry.COLUMN_DISEASE_DATE, textDateOfDisease);
        values.put(DiseasesEntry.COLUMN_DISEASE_TREATMENT, textTreatment);

        Uri mCurrentUserUri = Uri.withAppendedPath(DiseasesEntry.CONTENT_DISEASES_URI, String.valueOf(_idDisease));

        int rowsAffected = getContentResolver().update(mCurrentUserUri, values, null, null);

        if (rowsAffected == 0) {
            Toast.makeText(this, R.string.treatment_cant_update, Toast.LENGTH_LONG).show();
        }
    }

    private void deleteDiseaseAndTreatmentPhotos() {
        getLoaderManager().initLoader(TR_PHOTOS_LOADER, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String[] projection = {
                TreatmentPhotosEntry.TR_PHOTO_ID,
                TreatmentPhotosEntry.COLUMN_TR_PHOTO_PATH};

        String selection = TreatmentPhotosEntry.COLUMN_DIS_ID + "=?";
        String[] selectionArgs = new String[]{String.valueOf(_idDisease)};

        return new CursorLoader(this,
                TreatmentPhotosEntry.CONTENT_TREATMENT_PHOTOS_URI,
                projection,
                selection,
                selectionArgs,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        ArrayList<String> photoFilePathsToBeDeletedList = new ArrayList<>();

        if (cursor != null) {
            cursor.moveToPosition(-1);

            while (cursor.moveToNext()) {
                int trPhoto_pathColumnIndex = cursor.getColumnIndex(TreatmentPhotosEntry.COLUMN_TR_PHOTO_PATH);
                String trPhotoUri = cursor.getString(trPhoto_pathColumnIndex);

                photoFilePathsToBeDeletedList.add(trPhotoUri);
            }
        }

        getLoaderManager().destroyLoader(TR_PHOTOS_LOADER);

        new DiseaseAndTreatmentPhotosDeletingAsyncTask(this, photoFilePathsToBeDeletedList).execute(getApplicationContext());
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }

    private static class DiseaseAndTreatmentPhotosDeletingAsyncTask extends AsyncTask<Context, Void, Integer> {

        private static final String PREFS_NAME = "PREFS";

        private final WeakReference<TreatmentActivity> treatmentActivityReference;
        private final ArrayList<String> mPhotoFilePathsListToBeDeleted;
        private int mRowsFromTreatmentPhotosDeleted = -1;

        DiseaseAndTreatmentPhotosDeletingAsyncTask(TreatmentActivity context, ArrayList<String> photoFilePathsListToBeDeleted) {
            treatmentActivityReference = new WeakReference<>(context);
            mPhotoFilePathsListToBeDeleted = new ArrayList<>(photoFilePathsListToBeDeleted);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            TreatmentActivity treatmentActivity = treatmentActivityReference.get();
            if (treatmentActivity == null) {
                return;
            }

            mRowsFromTreatmentPhotosDeleted = deleteDiseaseAndTreatmentPhotosFromDataBase(treatmentActivity);
        }

        private int deleteDiseaseAndTreatmentPhotosFromDataBase(TreatmentActivity treatmentActivity) {
            ArrayList<ContentProviderOperation> deletingFromDbOperations = new ArrayList<>();

            String selectionTrPhotos = TreatmentPhotosEntry.COLUMN_DIS_ID + "=?";
            String[] selectionArgsTrPhotos = new String[]{String.valueOf(treatmentActivity._idDisease)};

            ContentProviderOperation deleteTreatmentPhotosFromDbOperation = ContentProviderOperation
                    .newDelete(TreatmentPhotosEntry.CONTENT_TREATMENT_PHOTOS_URI)
                    .withSelection(selectionTrPhotos, selectionArgsTrPhotos)
                    .build();

            deletingFromDbOperations.add(deleteTreatmentPhotosFromDbOperation);

            String selectionDisease = DiseasesEntry.DIS_ID + "=?";
            String[] selectionArgsDisease = new String[]{String.valueOf(treatmentActivity._idDisease)};

            ContentProviderOperation deleteDiseaseFromDbOperation = ContentProviderOperation
                    .newDelete(DiseasesEntry.CONTENT_DISEASES_URI)
                    .withSelection(selectionDisease, selectionArgsDisease)
                    .build();

            deletingFromDbOperations.add(deleteDiseaseFromDbOperation);

            int rowsFromTreatmentPhotosDeleted = -1;

            try {
                ContentProviderResult[] results = treatmentActivity.getContentResolver().applyBatch(MedContract.CONTENT_AUTHORITY, deletingFromDbOperations);

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

            final TreatmentActivity treatmentActivity = treatmentActivityReference.get();

            if (treatmentActivity == null) {
                return;
            }

            if (result == -1) {
                treatmentActivity.onSavingOrUpdatingOrDeleting = false;
                Toast.makeText(treatmentActivity, R.string.disease_not_deleted, Toast.LENGTH_LONG).show();
            } else {
                treatmentActivity.goToDiseasesActivity();
            }
        }
    }
}