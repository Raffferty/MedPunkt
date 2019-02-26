package com.gmail.krbashianrafael.medpunkt.shared;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Display;
import android.view.MotionEvent;
import android.view.OrientationEventListener;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.ScaleAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bogdwellers.pinchtozoom.ImageMatrixTouchHandler;
import com.bogdwellers.pinchtozoom.ImageViewerCorrector;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.gmail.krbashianrafael.medpunkt.GlideApp;
import com.gmail.krbashianrafael.medpunkt.R;
import com.gmail.krbashianrafael.medpunkt.data.MedContract.TreatmentPhotosEntry;
import com.tsongkha.spinnerdatepicker.DatePicker;
import com.tsongkha.spinnerdatepicker.DatePickerDialog;
import com.tsongkha.spinnerdatepicker.SpinnerDatePickerDialogBuilder;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.Objects;

@SuppressLint("RestrictedApi")
public class FullscreenPhotoActivity extends AppCompatActivity implements
        ActivityCompat.OnRequestPermissionsResultCallback,
        DatePickerDialog.OnDateSetListener {

    private static final String PREFS_NAME = "PREFS";

    private static final int UI_ANIMATION_DELAY = 300;
    private final Handler myHandler = new Handler(Looper.getMainLooper());

    private MyImageMatrixTouchHandler myImageMatrixTouchHandler;

    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            imagePhoto.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    };

    private final Runnable mShowPart2Runnable = new Runnable() {
        @Override
        public void run() {
            LL_title.startAnimation(LL_title_showAnimation);

            if (editTreatmentPhoto) {
                mDescriptionView.setVisibility(View.VISIBLE);
                editTextDateOfTreatmentPhoto.setVisibility(View.VISIBLE);
                frm_save.setVisibility(View.VISIBLE);
                frm_delete.setVisibility(View.GONE);

            } else {
                fab.startAnimation(fabShowAnimation);
                mDescriptionView.setVisibility(View.INVISIBLE);
                editTextDateOfTreatmentPhoto.setVisibility(View.INVISIBLE);
                frm_save.setVisibility(View.GONE);
                frm_delete.setVisibility(View.VISIBLE);
            }
        }
    };

    private final Runnable mtapedRunnable = new Runnable() {
        @Override
        public void run() {
            tapped = false;
        }
    };

    private final Runnable mToToggleRunnable = new Runnable() {
        @Override
        public void run() {
            if (!tapped && !inZoom[0] && !onLoading) {

                if (!HomeActivity.isTablet && landscape) {
                    return;
                }

                toggle();
            }
        }
    };

    private final boolean[] inZoom = {false};
    private boolean mVisible, landscape, goBack, editTreatmentPhoto, newTreatmentPhoto, treatmentPhotoHasChanged, tapped;
    private boolean onLoading, onSavingOrUpdatingOrDeleting;

    private View mDescriptionView, LL_title, frm_back, frm_save, frm_delete;
    private TextView txtViewPhotoTitle;
    private EditText focusHolder, editTextDateOfTreatmentPhoto;
    private TextInputLayout textInputLayoutPhotoDescription;
    private TextInputEditText editTextPhotoDescription;
    private String textPhotoDescription, textDateOfTreatmentPhoto;
    private FloatingActionButton fab;

    private Animation LL_title_hideAnimation, LL_title_showAnimation, fabHideAnimation, fabShowAnimation;

    private Uri imageUriInView;

    private String loadedImageFilePath;

    private String treatmentPhotoFilePath;

    private long _idTrPhoto = 0;
    private long _idUser = 0;
    private long _idDisease = 0;

    private ImageView imagePhoto;

    private TextView txtViewErr;

    private static final int RESULT_LOAD_IMAGE = 9002;

    private static final int PERMISSION_WRITE_EXTERNAL_STORAGE = 0;

    private OrientationEventListener mOrientationListener;

    private int displayWidth = 0;
    private int displayheight = 0;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullscreen);

        Intent intent = getIntent();

        _idTrPhoto = intent.getLongExtra("_idTrPhoto", 0);

        _idUser = intent.getLongExtra("_idUser", 0);

        _idDisease = intent.getLongExtra("_idDisease", 0);

        treatmentPhotoFilePath = intent.getStringExtra("treatmentPhotoFilePath");
        textPhotoDescription = intent.getStringExtra("textPhotoDescription");
        textDateOfTreatmentPhoto = intent.getStringExtra("textDateOfTreatmentPhoto");

        newTreatmentPhoto = intent.getBooleanExtra("newTreatmentPhoto", false);

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        if (!HomeActivity.isTablet && size.x < 1000) {
            displayWidth = size.x * 2;
            displayheight = size.y * 2;
        } else {
            displayWidth = size.x - 1;
            displayheight = size.y - 1;
        }

        findViewsById();

        txtViewPhotoTitle.setText(textPhotoDescription);

        myImageMatrixTouchHandler = new MyImageMatrixTouchHandler(this);

        setMyListeners();

        if (HomeActivity.isTablet) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }

        int myScreenOrientation = getResources().getConfiguration().orientation;

        if (!newTreatmentPhoto) {
            if (!HomeActivity.isTablet) {
                if (myScreenOrientation == Configuration.ORIENTATION_PORTRAIT) {
                    hide();
                } else {
                    show();
                }
            } else {
                hide();
            }
        } else if (HomeActivity.isTablet) {
            show();
        }


        if (!newTreatmentPhoto
                && treatmentPhotoFilePath != null
                && new File(treatmentPhotoFilePath).exists()) {

            GlideApp.with(this)
                    .load(new File(treatmentPhotoFilePath))
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            txtViewErr.setVisibility(View.VISIBLE);

                            if (!HomeActivity.isTablet) {
                                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                                landscape = false;
                            }

                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            if (txtViewErr.getVisibility() == View.VISIBLE) {
                                txtViewErr.setVisibility(View.GONE);
                            }

                            if (!HomeActivity.isTablet) {
                                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);

                                mOrientationListener.enable();
                            }

                            return false;
                        }
                    })
                    .override(displayWidth, displayheight)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .error(R.color.my_dark_gray)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(imagePhoto);

            imagePhoto.setScaleType(ImageView.ScaleType.FIT_CENTER);

        } else if (!newTreatmentPhoto) {
            Toast.makeText(this, R.string.treatment_cant_load_image, Toast.LENGTH_LONG).show();
        }

        if (textPhotoDescription != null) {
            editTextPhotoDescription.setText(textPhotoDescription);
        } else {
            textPhotoDescription = "";
        }

        if (textDateOfTreatmentPhoto != null) {
            editTextDateOfTreatmentPhoto.setText(textDateOfTreatmentPhoto);
        } else {
            textDateOfTreatmentPhoto = getString(R.string.fullscreen_date_of_image);
        }

        if (newTreatmentPhoto) {

            editTreatmentPhoto = true;

            if (ActivityCompat.checkSelfPermission(FullscreenPhotoActivity.this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {

                mDescriptionView.setVisibility(View.INVISIBLE);
                editTextDateOfTreatmentPhoto.setVisibility(View.INVISIBLE);
                mVisible = false;
                LL_title.startAnimation(LL_title_hideAnimation);
                fab.startAnimation(fabHideAnimation);

                imagePhoto.setEnabled(false);

                MyReadWritePermissionHandler.getReadWritePermission(FullscreenPhotoActivity.this, imagePhoto, PERMISSION_WRITE_EXTERNAL_STORAGE);

            } else {
                if (myScreenOrientation == Configuration.ORIENTATION_PORTRAIT) {
                    editTextPhotoDescription.requestFocus();
                    editTextPhotoDescription.setSelection(Objects.requireNonNull(editTextPhotoDescription.getText()).toString().length());
                    frm_delete.setVisibility(View.GONE);
                } else {
                    mDescriptionView.setVisibility(View.INVISIBLE);
                    editTextDateOfTreatmentPhoto.setVisibility(View.INVISIBLE);
                    frm_save.setVisibility(View.GONE);
                    landscape = true;
                }

                Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(galleryIntent, RESULT_LOAD_IMAGE);
            }

        } else {
            mDescriptionView.setVisibility(View.INVISIBLE);
            editTextDateOfTreatmentPhoto.setVisibility(View.INVISIBLE);
            frm_save.setVisibility(View.GONE);
        }
    }

    private void findViewsById() {
        mVisible = true;
        LL_title = findViewById(R.id.LL_title);
        frm_back = findViewById(R.id.frm_back);
        txtViewPhotoTitle = findViewById(R.id.txt_photo_title);
        frm_save = findViewById(R.id.frm_save);
        frm_delete = findViewById(R.id.frm_delete);
        focusHolder = findViewById(R.id.focus_holder);
        editTextDateOfTreatmentPhoto = findViewById(R.id.editText_date);
        mDescriptionView = findViewById(R.id.fullscreen_content_description);
        imagePhoto = findViewById(R.id.fullscreen_image);
        txtViewErr = findViewById(R.id.treatment_photo_err_view);
        textInputLayoutPhotoDescription = findViewById(R.id.text_input_layout_photo_description);
        editTextPhotoDescription = findViewById(R.id.editText_photo_description);
        fab = findViewById(R.id.fabEditTreatmentPhoto);

        LL_title_hideAnimation = AnimationUtils.loadAnimation(this, R.anim.fullscreenphoto_title_hide);
        LL_title_showAnimation = AnimationUtils.loadAnimation(this, R.anim.fullscreenphoto_title_show);
        fabShowAnimation = AnimationUtils.loadAnimation(this, R.anim.fab_show);
        fabHideAnimation = AnimationUtils.loadAnimation(this, R.anim.fab_hide);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setMyListeners() {
        mOrientationListener = new OrientationEventListener(this,
                SensorManager.SENSOR_DELAY_NORMAL) {

            @Override
            public void onOrientationChanged(int angle) {
                if ((angle > 315 && angle < 360) || (angle >= 0 && angle < 45)) {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
                    mOrientationListener.disable();
                }
            }
        };

        LL_title.setOnClickListener(null);

        fabShowAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                fab.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                fab.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                fab.setVisibility(View.VISIBLE);
            }
        });

        fabHideAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                fab.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });

        frm_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (photoAndDescriptionHasNotChanged() || txtViewErr.getVisibility() == View.VISIBLE) {
                    goToTreatmentActivity();
                } else {
                    DialogInterface.OnClickListener discardButtonClickListener =
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    goToTreatmentActivity();
                                }
                            };

                    showUnsavedChangesDialog(discardButtonClickListener);
                }
            }
        });

        txtViewPhotoTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!editTreatmentPhoto) {
                    toggle();
                }
            }
        });

        frm_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                hideSoftInput();

                if (onSavingOrUpdatingOrDeleting) {
                    return;
                }

                onSavingOrUpdatingOrDeleting = true;

                if (photoAndDescriptionHasNotChanged() && !newTreatmentPhoto) {
                    editTreatmentPhoto = false;
                    mDescriptionView.setVisibility(View.INVISIBLE);
                    editTextDateOfTreatmentPhoto.setVisibility(View.INVISIBLE);
                    fab.setVisibility(View.VISIBLE);
                    frm_save.setVisibility(View.GONE);
                    frm_delete.setVisibility(View.VISIBLE);
                } else {
                    focusHolder.requestFocus();
                    saveOrUpdateTreatmentPhoto();
                }

                onSavingOrUpdatingOrDeleting = false;
            }
        });

        frm_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onSavingOrUpdatingOrDeleting) {
                    return;
                }

                hideSoftInput();

                showDeleteConfirmationDialog();
            }
        });

        editTextDateOfTreatmentPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                hideSoftInput();

                textInputLayoutPhotoDescription.setError(null);
                textInputLayoutPhotoDescription.setErrorEnabled(false);
                editTextDateOfTreatmentPhoto.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                textInputLayoutPhotoDescription.setHintTextAppearance(R.style.Lable);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    String dateInEditTextDate = editTextDateOfTreatmentPhoto.getText().toString().trim();

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
                            .context(FullscreenPhotoActivity.this)
                            .callback(FullscreenPhotoActivity.this)
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

        textInputLayoutPhotoDescription.setOnClickListener(null);

        editTextPhotoDescription.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                textInputLayoutPhotoDescription.setError(null);
                textInputLayoutPhotoDescription.setErrorEnabled(false);
                textInputLayoutPhotoDescription.setHintTextAppearance(R.style.Lable);
                editTextDateOfTreatmentPhoto.setTextColor(getResources().getColor(R.color.colorPrimaryDark));

                return false;
            }
        });

        imagePhoto.setOnTouchListener(myImageMatrixTouchHandler);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDescriptionView.setVisibility(View.VISIBLE);
                editTextDateOfTreatmentPhoto.setVisibility(View.VISIBLE);
                editTextPhotoDescription.requestFocus();
                editTextPhotoDescription.setSelection(Objects.requireNonNull(editTextPhotoDescription.getText()).toString().length());
                fab.setVisibility(View.INVISIBLE);
                editTreatmentPhoto = true;
                frm_save.setVisibility(View.VISIBLE);
                frm_delete.setVisibility(View.GONE);

                imagePhoto.setScaleType(ImageView.ScaleType.FIT_CENTER);
            }
        });
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
        GregorianCalendar date = new GregorianCalendar(year, monthOfYear, dayOfMonth);
        editTextDateOfTreatmentPhoto.setText(simpleDateFormat.format(date.getTime()) + " ");
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if (HomeActivity.isTablet || txtViewErr.getVisibility() == View.VISIBLE) {
            return;
        }

        hideSoftInput();

        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            landscape = true;
            hide();
            imagePhoto.setScaleType(ImageView.ScaleType.FIT_CENTER);

        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            landscape = false;
            show();
            imagePhoto.setScaleType(ImageView.ScaleType.FIT_CENTER);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mOrientationListener.disable();
    }

    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.DeleteAlertDialogCustom);
        builder.setMessage(getString(R.string.fullscreen_dialog_msg_delete_image) + " " + editTextPhotoDescription.getText() + "?");
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (onSavingOrUpdatingOrDeleting) {
                    return;
                }

                onSavingOrUpdatingOrDeleting = true;

                deleteTreatmentPhoto();
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

    private void hide() {
        LL_title.startAnimation(LL_title_hideAnimation);

        if (fab.getVisibility() == View.VISIBLE) {
            fab.startAnimation(fabHideAnimation);
        }

        mDescriptionView.setVisibility(View.INVISIBLE);
        editTextDateOfTreatmentPhoto.setVisibility(View.INVISIBLE);
        mVisible = false;

        myHandler.removeCallbacks(mHidePart2Runnable);
        myHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
    }

    @SuppressLint("InlinedApi")
    private void show() {
        imagePhoto.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);

        mVisible = true;

        myHandler.removeCallbacks(mShowPart2Runnable);
        myHandler.postDelayed(mShowPart2Runnable, UI_ANIMATION_DELAY);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_WRITE_EXTERNAL_STORAGE) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(galleryIntent, RESULT_LOAD_IMAGE);
            } else {
                Snackbar.make(imagePhoto, R.string.permission_was_denied,
                        Snackbar.LENGTH_LONG).show();

                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);

                myHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        hideSoftInput();
                        finish();
                    }
                }, UI_ANIMATION_DELAY * 7);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && data != null) {

            imagePhoto.setEnabled(true);

            Uri newSelectedImageUri = data.getData();

            if (newSelectedImageUri != null) {
                if (imageUriInView != null && imageUriInView.equals(newSelectedImageUri)) {
                    onLoading = false;
                    treatmentPhotoHasChanged = false;

                } else {
                    Glide.with(FullscreenPhotoActivity.this).clear(imagePhoto);

                    loadedImageFilePath = getLoadedImageFilePath(this, newSelectedImageUri);

                    GlideApp.with(this)
                            .load(newSelectedImageUri)
                            .listener(new RequestListener<Drawable>() {
                                @Override
                                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                    txtViewErr.setVisibility(View.VISIBLE);

                                    frm_save.setVisibility(View.INVISIBLE);
                                    LL_title.startAnimation(LL_title_showAnimation);

                                    mDescriptionView.setVisibility(View.INVISIBLE);
                                    editTextDateOfTreatmentPhoto.setVisibility(View.INVISIBLE);

                                    if (!HomeActivity.isTablet) {
                                        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                                        landscape = false;
                                    }

                                    return false;
                                }

                                @Override
                                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                    if (txtViewErr.getVisibility() == View.VISIBLE) {

                                        txtViewErr.setVisibility(View.GONE);
                                        frm_save.setVisibility(View.VISIBLE);

                                        mDescriptionView.setVisibility(View.VISIBLE);
                                        editTextDateOfTreatmentPhoto.setVisibility(View.VISIBLE);
                                    }

                                    if (!HomeActivity.isTablet) {
                                        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);

                                        mOrientationListener.enable();
                                    }

                                    return false;
                                }
                            })
                            .override(displayWidth, displayheight)
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .skipMemoryCache(true)
                            .error(R.color.my_dark_gray)
                            .transition(DrawableTransitionOptions.withCrossFade())
                            .into(imagePhoto);

                    imagePhoto.setScaleType(ImageView.ScaleType.FIT_CENTER);
                    treatmentPhotoHasChanged = true;
                    imageUriInView = newSelectedImageUri;
                    onLoading = false;
                }
            }
        } else {
            onLoading = false;

            if (newTreatmentPhoto) {
                goToTreatmentActivity();
            }
        }

        if (!HomeActivity.isTablet) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
        }
    }

    private String getLoadedImageFilePath(Context context, Uri photoUri) {
        Cursor cursor = context.getContentResolver().query(photoUri,
                new String[]{MediaStore.Images.Media.DATA}, null, null, null);

        if (cursor != null) {

            if (cursor.getCount() != 1) {
                cursor.close();
                return null;
            }

            cursor.moveToFirst();

            String mLoadedImageFilePath = cursor.getString(0);
            cursor.close();
            //noinspection UnusedAssignment
            cursor = null;
            return mLoadedImageFilePath;
        }

        return null;
    }

    private void toggle() {

        if (!HomeActivity.isTablet &&
                getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            return;
        }

        if (!editTreatmentPhoto) {
            if (mVisible) {
                hide();
            } else {
                show();
            }
        } else {

            if (onLoading || onSavingOrUpdatingOrDeleting) {
                return;
            }

            if (!HomeActivity.isTablet) {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            }

            onLoading = true;

            if (ActivityCompat.checkSelfPermission(FullscreenPhotoActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                MyReadWritePermissionHandler.getReadWritePermission(FullscreenPhotoActivity.this, imagePhoto, PERMISSION_WRITE_EXTERNAL_STORAGE);
            } else {
                Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(galleryIntent, RESULT_LOAD_IMAGE);
            }
        }
    }

    private void hideSoftInput() {
        View viewToHide = FullscreenPhotoActivity.this.getCurrentFocus();
        if (viewToHide != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(viewToHide.getWindowToken(), 0);
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (photoAndDescriptionHasNotChanged() || txtViewErr.getVisibility() == View.VISIBLE) {
            goToTreatmentActivity();
            return;
        }

        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        if (dialog != null) {
                            dialog.dismiss();
                        }

                        goToTreatmentActivity();
                    }
                };

        showUnsavedChangesDialog(discardButtonClickListener);
    }

    private void showUnsavedChangesDialog(DialogInterface.OnClickListener
                                                  discardButtonClickListener) {

        hideSoftInput();

        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AlertDialogCustom);
        builder.setMessage(R.string.dialog_msg_unsaved_changes);

        builder.setNegativeButton(R.string.dialog_no, discardButtonClickListener);

        builder.setPositiveButton(R.string.dialog_yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                goBack = true;

                if (dialog != null) {
                    dialog.dismiss();
                }

                saveOrUpdateTreatmentPhoto();
            }
        });


        AlertDialog alertDialog = builder.create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();
    }

    private boolean photoAndDescriptionHasNotChanged() {
        return !treatmentPhotoHasChanged &&
                TextUtils.equals(Objects.requireNonNull(editTextPhotoDescription.getText()).toString(), textPhotoDescription) &&
                TextUtils.equals(editTextDateOfTreatmentPhoto.getText().toString(), textDateOfTreatmentPhoto);
    }

    private void goToTreatmentActivity() {
        if (imagePhoto.getSystemUiVisibility() == 0) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
        } else {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
        }

        hideSoftInput();

        myHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();
            }
        }, UI_ANIMATION_DELAY);
    }

    private void saveOrUpdateTreatmentPhoto() {

        ScaleAnimation scaleAnimation = new ScaleAnimation(0f, 1f, 0f, 0f);
        scaleAnimation.setDuration(500);

        String photoDescriptionToCheck = Objects.requireNonNull(editTextPhotoDescription.getText()).toString().trim();
        String dateOfTreatmentPhotoToCheck = editTextDateOfTreatmentPhoto.getText().toString();
        boolean wrongField = false;

        if (TextUtils.isEmpty(photoDescriptionToCheck)) {
            textInputLayoutPhotoDescription.setHintTextAppearance(R.style.Lable_Error);
            textInputLayoutPhotoDescription.setError(getString(R.string.fullscreen_error_image_description));
            editTextPhotoDescription.startAnimation(scaleAnimation);
            editTextPhotoDescription.requestFocus();
            wrongField = true;
        }

        if (TextUtils.equals(dateOfTreatmentPhotoToCheck, getString(R.string.fullscreen_date_of_image))) {
            if (wrongField) {
                textInputLayoutPhotoDescription.setError(
                        getString(R.string.fullscreen_error_image_description) + "\n" +
                                getString(R.string.fullscreen_error_date_of_image)
                );
            } else {
                textInputLayoutPhotoDescription.setError(getString(R.string.fullscreen_error_date_of_image));
            }

            editTextDateOfTreatmentPhoto.setTextColor(getResources().getColor(R.color.colorFab));
            editTextDateOfTreatmentPhoto.startAnimation(scaleAnimation);
            wrongField = true;
        }

        if (wrongField) {

            if (!HomeActivity.isTablet && getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);

                mOrientationListener.enable();
            }

            onSavingOrUpdatingOrDeleting = false;

            return;
        } else {
            textInputLayoutPhotoDescription.setError(null);
        }

        textPhotoDescription = photoDescriptionToCheck;
        textDateOfTreatmentPhoto = dateOfTreatmentPhotoToCheck;

        txtViewPhotoTitle.setText(textPhotoDescription);

        if (imageUriInView != null) {

            copyTreatmentPhotoAndSaveOrUpdateToDataBase();

            imageUriInView = null;

        } else {
            if (newTreatmentPhoto) {
                saveTreatmentPhotoToDataBase();
                newTreatmentPhoto = false;
                afterSaveOrUpdate();
            } else {
                updateTreatmentPhotoToDataBase();
                afterSaveOrUpdate();
            }

            treatmentPhotoHasChanged = false;
        }
    }

    private void copyTreatmentPhotoAndSaveOrUpdateToDataBase() {
        if (loadedImageFilePath != null) {

            File fileOfPhoto = new File(loadedImageFilePath);

            String root = getFilesDir().toString();

            String pathToTreatmentPhotos = root + getString(R.string.path_to_treatment_photos);

            File myDir = new File(pathToTreatmentPhotos);

            if (!myDir.exists()) {
                if (!myDir.mkdirs()) {
                    Toast.makeText(this, R.string.treatment_cant_save_image, Toast.LENGTH_LONG).show();
                    return;
                }
            }

            String destinationFileName = getString(R.string.treatment_photo_nameStart) +
                    _idUser + "-" +
                    _idDisease + "-" + SystemClock.elapsedRealtime() +
                    getString(R.string.treatment_photo_nameEnd);

            File destinationFile = new File(myDir, destinationFileName);

            if (newTreatmentPhoto) {
                treatmentPhotoFilePath = destinationFile.toString();

                if (saveTreatmentPhotoToDataBase()) {
                    new TreatmentPhotoCopyAsyncTask(this).execute(fileOfPhoto, destinationFile);

                } else {
                    onSavingOrUpdatingOrDeleting = false;
                    Toast.makeText(this, R.string.treatment_cant_save_image, Toast.LENGTH_LONG).show();
                }

            } else {
                File oldFile = new File(treatmentPhotoFilePath);
                if (oldFile.exists()) {
                    if (!oldFile.delete()) {
                        Toast.makeText(this, R.string.treatment_old_image_not_deleted, Toast.LENGTH_LONG).show();

                        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
                        final SharedPreferences.Editor prefsEditor = prefs.edit();

                        String notDeletedFilesPaths = prefs.getString("notDeletedFilesPaths", null);

                        String updatedNotDeletedFilesPaths;
                        if (notDeletedFilesPaths == null) {
                            updatedNotDeletedFilesPaths = oldFile.getPath();
                        } else {
                            updatedNotDeletedFilesPaths = notDeletedFilesPaths + "," + oldFile.getPath();
                        }

                        prefsEditor.putString("notDeletedFilesPaths", updatedNotDeletedFilesPaths);
                        prefsEditor.apply();
                    }
                }

                treatmentPhotoFilePath = destinationFile.toString();

                if (updateTreatmentPhotoToDataBase()) {
                    new TreatmentPhotoCopyAsyncTask(this).execute(fileOfPhoto, destinationFile);

                } else {
                    Toast.makeText(this, R.string.treatment_cant_save_image, Toast.LENGTH_LONG).show();

                    treatmentPhotoHasChanged = false;
                    onSavingOrUpdatingOrDeleting = false;
                    afterSaveOrUpdate();
                }
            }
        } else {
            onSavingOrUpdatingOrDeleting = false;
            Toast.makeText(this, R.string.treatment_cant_save_image, Toast.LENGTH_LONG).show();
        }
    }

    private void afterSaveOrUpdate() {
        if (goBack) {
            goToTreatmentActivity();
        } else {
            editTreatmentPhoto = false;
            mDescriptionView.setVisibility(View.INVISIBLE);
            editTextDateOfTreatmentPhoto.setVisibility(View.INVISIBLE);
            fab.setVisibility(View.VISIBLE);
            frm_save.setVisibility(View.GONE);
            frm_delete.setVisibility(View.VISIBLE);
        }
    }

    private boolean saveTreatmentPhotoToDataBase() {

        ContentValues values = new ContentValues();
        values.put(TreatmentPhotosEntry.COLUMN_U_ID, _idUser);
        values.put(TreatmentPhotosEntry.COLUMN_DIS_ID, _idDisease);
        values.put(TreatmentPhotosEntry.COLUMN_TR_PHOTO_NAME, textPhotoDescription);
        values.put(TreatmentPhotosEntry.COLUMN_TR_PHOTO_DATE, textDateOfTreatmentPhoto);
        values.put(TreatmentPhotosEntry.COLUMN_TR_PHOTO_PATH, treatmentPhotoFilePath);

        Uri newUri = getContentResolver().insert(TreatmentPhotosEntry.CONTENT_TREATMENT_PHOTOS_URI, values);

        if (newUri != null) {
            _idTrPhoto = ContentUris.parseId(newUri);
            TreatmentPhotosFragment.mScrollToStart = true;
            return true;

        } else {
            Toast.makeText(this, R.string.treatment_cant_save_image, Toast.LENGTH_LONG).show();
            return false;
        }
    }

    private boolean updateTreatmentPhotoToDataBase() {

        ContentValues values = new ContentValues();
        values.put(TreatmentPhotosEntry.COLUMN_TR_PHOTO_NAME, textPhotoDescription);
        values.put(TreatmentPhotosEntry.COLUMN_TR_PHOTO_DATE, textDateOfTreatmentPhoto);
        values.put(TreatmentPhotosEntry.COLUMN_TR_PHOTO_PATH, treatmentPhotoFilePath);

        Uri mCurrentUserUri = Uri.withAppendedPath(TreatmentPhotosEntry.CONTENT_TREATMENT_PHOTOS_URI, String.valueOf(_idTrPhoto));

        int rowsAffected = getContentResolver().update(mCurrentUserUri, values, null, null);

        if (rowsAffected == 0) {
            Toast.makeText(this, R.string.treatment_cant_update_image, Toast.LENGTH_LONG).show();

            return false;
        } else {
            return true;
        }
    }

    private void deleteTreatmentPhoto() {
        File toBeDeletedFile = new File(treatmentPhotoFilePath);

        if (toBeDeletedFile.exists()) {
            if (!toBeDeletedFile.delete()) {
                Toast.makeText(this, R.string.treatment_image_not_deleted, Toast.LENGTH_LONG).show();

                SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
                final SharedPreferences.Editor prefsEditor = prefs.edit();

                String notDeletedFilesPaths = prefs.getString("notDeletedFilesPaths", null);

                String updatedNotDeletedFilesPaths;
                if (notDeletedFilesPaths == null) {
                    updatedNotDeletedFilesPaths = toBeDeletedFile.getPath();
                } else {
                    updatedNotDeletedFilesPaths = notDeletedFilesPaths + "," + toBeDeletedFile.getPath();
                }

                prefsEditor.putString("notDeletedFilesPaths", updatedNotDeletedFilesPaths);
                prefsEditor.apply();
            }
        }

        deleteTreatmentPhotoFromDataBase();
    }

    private void deleteTreatmentPhotoFromDataBase() {

        Uri mCurrentTrPhotoUri = Uri.withAppendedPath(TreatmentPhotosEntry.CONTENT_TREATMENT_PHOTOS_URI, String.valueOf(_idTrPhoto));

        int rowsDeleted = 0;

        if (_idTrPhoto != 0) {
            rowsDeleted = getContentResolver().delete(mCurrentTrPhotoUri, null, null);
        }

        if (rowsDeleted == 0) {
            onSavingOrUpdatingOrDeleting = false;
            Toast.makeText(FullscreenPhotoActivity.this, "TreatmentPhoto has NOT been Deleted from DataBase", Toast.LENGTH_LONG).show();
        } else {
            goToTreatmentActivity();
        }
    }

    private class MyImageMatrixTouchHandler extends ImageMatrixTouchHandler {

        MyImageMatrixTouchHandler(Context context) {
            super(context);

            ImageViewerCorrector crr = (ImageViewerCorrector) this.getImageMatrixCorrector();
            crr.setMaxScale(20f);
        }

        boolean firstTouch = false;
        long touchTime = 0;

        @Override
        public boolean onTouch(final View view, final MotionEvent event) {

            view.performClick();

            if (event.getActionMasked() == MotionEvent.ACTION_UP) {
                tapped = false;
                if (firstTouch) inZoom[0] = false;
            }

            super.onTouch(view, event);

            if (event.getActionMasked() == MotionEvent.ACTION_POINTER_DOWN) {
                inZoom[0] = true;
            }

            if (event.getActionMasked() == MotionEvent.ACTION_MOVE) {
                inZoom[0] = !tapped;
            }

            if (event.getActionMasked() == MotionEvent.ACTION_DOWN) {

                tapped = true;

                myHandler.removeCallbacks(mtapedRunnable);

                myHandler.postDelayed(mtapedRunnable, 100);

                if (firstTouch && (System.currentTimeMillis() - touchTime) <= 300) {
                    tapped = false;
                    inZoom[0] = true;
                    firstTouch = false;
                } else {
                    firstTouch = true;
                    inZoom[0] = false;
                    touchTime = System.currentTimeMillis();
                }

                myHandler.removeCallbacks(mToToggleRunnable);
                myHandler.postDelayed(mToToggleRunnable, 400);
            }

            return true;
        }
    }

    private static class TreatmentPhotoCopyAsyncTask extends AsyncTask<File, Void, Void> {

        private final WeakReference<FullscreenPhotoActivity> fullscreenPhotoActivityReference;

        TreatmentPhotoCopyAsyncTask(FullscreenPhotoActivity context) {
            fullscreenPhotoActivityReference = new WeakReference<>(context);
        }

        @Override
        protected Void doInBackground(File... files) {
            File fileOfPhoto = files[0];
            File destination = files[1];

            final FullscreenPhotoActivity fullscreenPhotoActivity = fullscreenPhotoActivityReference.get();

            try {
                FileUtils.copyFile(fileOfPhoto, destination);

            } catch (NullPointerException | IOException e) {
                if (fullscreenPhotoActivity != null) {
                    fullscreenPhotoActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(fullscreenPhotoActivity, R.string.treatment_image_copy_error, Toast.LENGTH_LONG).show();
                        }
                    });
                }

                if (destination.exists()) {
                    if (!destination.delete() && fullscreenPhotoActivity != null) {
                        SharedPreferences prefs = fullscreenPhotoActivity.getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
                        final SharedPreferences.Editor prefsEditor = prefs.edit();

                        String notDeletedFilesPaths = prefs.getString("notDeletedFilesPaths", null);

                        String updatedNotDeletedFilesPaths;
                        if (notDeletedFilesPaths == null) {
                            updatedNotDeletedFilesPaths = destination.getPath();
                        } else {
                            updatedNotDeletedFilesPaths = notDeletedFilesPaths + "," + destination.getPath();
                        }

                        prefsEditor.putString("notDeletedFilesPaths", updatedNotDeletedFilesPaths);
                        prefsEditor.apply();
                    }
                }

                return null;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            final FullscreenPhotoActivity fullscreenPhotoActivity = fullscreenPhotoActivityReference.get();

            fullscreenPhotoActivity.newTreatmentPhoto = false;
            fullscreenPhotoActivity.treatmentPhotoHasChanged = false;
            fullscreenPhotoActivity.onSavingOrUpdatingOrDeleting = false;

            fullscreenPhotoActivity.afterSaveOrUpdate();
        }
    }
}