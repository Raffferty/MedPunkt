package com.gmail.krbashianrafael.medpunkt.shared;

import android.Manifest;
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
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.RemoteException;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ImageSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.ScaleAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.gmail.krbashianrafael.medpunkt.GlideApp;
import com.gmail.krbashianrafael.medpunkt.R;
import com.gmail.krbashianrafael.medpunkt.data.MedContract;
import com.gmail.krbashianrafael.medpunkt.data.MedContract.DiseasesEntry;
import com.gmail.krbashianrafael.medpunkt.data.MedContract.TreatmentPhotosEntry;
import com.gmail.krbashianrafael.medpunkt.data.MedContract.UsersEntry;
import com.gmail.krbashianrafael.medpunkt.phone.DiseasesActivity;
import com.gmail.krbashianrafael.medpunkt.tablet.TabletMainActivity;
import com.tsongkha.spinnerdatepicker.DatePicker;
import com.tsongkha.spinnerdatepicker.DatePickerDialog;
import com.tsongkha.spinnerdatepicker.SpinnerDatePickerDialogBuilder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.Objects;


@SuppressWarnings("UnusedAssignment")
@SuppressLint("RestrictedApi")
public class UserActivity extends AppCompatActivity
        implements ActivityCompat.OnRequestPermissionsResultCallback,
        LoaderManager.LoaderCallbacks<Cursor>,
        DatePickerDialog.OnDateSetListener {

    private final Handler myHandler = new Handler(Looper.getMainLooper());

    private static final String PREFS_NAME = "PREFS";

    private static final int USER_TR_PHOTOS_LOADER = 202;

    private Bitmap loadedBitmap;

    private String pathToUsersPhoto;

    private boolean newUser, goBack, editUser, userHasChangedPhoto, onSavingOrUpdatingOrDeleting, onLoading;

    private ActionBar actionBar;

    private String textUserName, textUserBirthDate;

    private TextInputLayout textInputLayoutName, textInputLayoutDate;
    private TextInputEditText editTextDate, editTextName;
    private EditText focusHolder;

    private ImageView imagePhoto;

    private TextView txtTabletUserTitle;

    private FrameLayout tabletFrmSave;
    private FrameLayout tabletFrmDelete;

    private TextView textViewNoUserPhoto;

    private TextView txtErr;

    private TextView textDeleteUserPhoto;

    private String userPhotoUri, userSetNoPhotoUri = "";

    private long _idUser = 0;

    private View mLayout;

    private FloatingActionButton fab;

    private Animation fabHideAnimation;

    private static final int PERMISSION_WRITE_EXTERNAL_STORAGE = 0;

    private static final int RESULT_LOAD_IMAGE = 9002;

    private Uri imageUriInView;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (HomeActivity.isTablet) {
            setContentView(R.layout.tablet_activity_user);
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        } else {
            setContentView(R.layout.activity_user);
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

        Intent intent = getIntent();

        _idUser = intent.getLongExtra("_idUser", 0);
        newUser = intent.getBooleanExtra("newUser", false);
        editUser = intent.getBooleanExtra("editUser", false);
        textUserName = intent.getStringExtra("UserName");
        textUserBirthDate = intent.getStringExtra("birthDate");

        if (intent.hasExtra("userPhotoUri")) {
            userPhotoUri = intent.getStringExtra("userPhotoUri");
        } else {
            userPhotoUri = "No_Photo";
        }

        actionBar = getSupportActionBar();

        if (!HomeActivity.isTablet) {
            if (actionBar != null) {
                actionBar.setDisplayHomeAsUpEnabled(true);
                actionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_30dp);

                if (textUserName != null) {
                    actionBar.setTitle(textUserName);
                } else {
                    if (HomeActivity.iAmDoctor) {
                        actionBar.setTitle(R.string.patient_title_activity);
                    }
                }
            }
        } else {
            if (actionBar != null) {
                actionBar.hide();
            }

            txtTabletUserTitle = findViewById(R.id.txt_tablet_user_title);

            FrameLayout tabletFrmBack = findViewById(R.id.tablet_frm_back);
            tabletFrmBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onHomeClick();
                }
            });

            tabletFrmSave = findViewById(R.id.tablet_frm_save);
            tabletFrmSave.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onSaveClick();
                }
            });

            tabletFrmDelete = findViewById(R.id.tablet_frm_delete);
            tabletFrmDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PopupMenu popup = new PopupMenu(UserActivity.this, tabletFrmDelete);

                    popup.getMenuInflater().inflate(R.menu.menu, popup.getMenu());

                    String deleteString = HomeActivity.iAmDoctor ? getResources().getString(R.string.patient_delete) : getResources().getString(R.string.user_delete);

                    popup.getMenu().removeItem(R.id.action_delete);
                    popup.getMenu().removeItem(R.id.action_save);

                    popup.getMenu().add(0, R.id.action_delete, 3, menuIconWithText(getResources().getDrawable(R.drawable.ic_delete_red_24dp),
                            deleteString));

                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        public boolean onMenuItemClick(MenuItem item) {
                            onDeleteClick();
                            return true;
                        }
                    });

                    popup.show();
                }
            });

            if (textUserName != null) {
                txtTabletUserTitle.setText(textUserName);
            } else {
                if (HomeActivity.iAmDoctor) {
                    txtTabletUserTitle.setText(R.string.patient_title_activity);
                }
            }
        }

        if (getFilesDir() != null) {
            pathToUsersPhoto = getFilesDir().toString() + getString(R.string.path_to_users_photos);
        }

        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN |
                        WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        mLayout = findViewById(R.id.user_layout);

        textViewNoUserPhoto = findViewById(R.id.no_user_photo);

        txtErr = findViewById(R.id.user_photo_err_view);

        imagePhoto = findViewById(R.id.image_photo);

        if (!userPhotoUri.equals("No_Photo")) {
            textViewNoUserPhoto.setVisibility(View.GONE);

            File imgFile = new File(userPhotoUri);

            if (imgFile.exists()) {
                GlideApp.with(this)
                        .load(userPhotoUri)
                        .listener(new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                myHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        Glide.with(UserActivity.this).clear(imagePhoto);

                                        imagePhoto.setImageResource(R.color.my_dark_gray);
                                        txtErr.setVisibility(View.VISIBLE);
                                    }
                                });

                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                if (txtErr.getVisibility() == View.VISIBLE) {

                                    txtErr.setVisibility(View.GONE);
                                }

                                return false;
                            }
                        })
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .skipMemoryCache(true)
                        .transition(DrawableTransitionOptions.withCrossFade())
                        .into(imagePhoto);
            }
        } else {
            textViewNoUserPhoto.setVisibility(View.VISIBLE);
        }

        imagePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (onLoading || onSavingOrUpdatingOrDeleting) {
                    return;
                }

                onLoading = true;

                hideSoftInput();

                if (ActivityCompat.checkSelfPermission(UserActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    MyReadWritePermissionHandler.getReadWritePermission(UserActivity.this, mLayout, PERMISSION_WRITE_EXTERNAL_STORAGE);
                } else {
                    Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(galleryIntent, RESULT_LOAD_IMAGE);
                }

                if (editUser) {
                    fab.performClick();
                }
            }
        });

        textDeleteUserPhoto = findViewById(R.id.text_delete_photo);
        textDeleteUserPhoto.setText(menuIconWithText(getResources().getDrawable(R.drawable.ic_delete_red_24dp), getResources().getString(R.string.user_delete_photo)));
        textDeleteUserPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imagePhoto.setImageResource(R.color.colorPrimaryLight);
                imageUriInView = null;
                loadedBitmap = null;
                textViewNoUserPhoto.setVisibility(View.VISIBLE);
                textDeleteUserPhoto.setVisibility(View.INVISIBLE);

                if (userPhotoUri.equals("No_Photo")) {
                    userHasChangedPhoto = false;
                } else {
                    userHasChangedPhoto = true;
                    userSetNoPhotoUri = "Set_No_Photo";
                }
            }
        });

        focusHolder = findViewById(R.id.focus_holder);

        textInputLayoutName = findViewById(R.id.text_input_layout_name);

        if (HomeActivity.iAmDoctor) {
            textInputLayoutName.setHint(getString(R.string.patient_name));
        }

        editTextName = findViewById(R.id.editText_name);
        editTextName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    textInputLayoutName.setError(null);
                }

            }
        });

        textInputLayoutDate = findViewById(R.id.text_input_layout_date);
        editTextDate = findViewById(R.id.editText_date);
        editTextDate.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    hideSoftInput();

                    textInputLayoutDate.setError(null);

                    focusHolder.requestFocus();

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

                        String dateInEditTextDate = Objects.requireNonNull(editTextDate.getText()).toString().trim();

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
                                .context(UserActivity.this)
                                .callback(UserActivity.this)
                                .spinnerTheme(R.style.NumberPickerStyle)
                                .defaultDate(mYear, mMonth, mDay)
                                .build();

                        spinnerDatePickerDialog.setCanceledOnTouchOutside(false);
                        spinnerDatePickerDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                            @Override
                            public void onDismiss(DialogInterface dialogInterface) {
                                editTextDate.clearFocus();
                            }
                        });
                        spinnerDatePickerDialog.show();
                    } else {
                        DatePickerFragment newFragment = new DatePickerFragment();
                        newFragment.show(getSupportFragmentManager(), "datePicker");
                    }
                }
            }
        });

        if (textUserName != null) {
            editTextName.setText(textUserName);
        } else {
            textUserName = "";
        }

        if (textUserBirthDate != null) {
            editTextDate.setText(textUserBirthDate);
        } else {
            textUserBirthDate = "";
        }

        fab = findViewById(R.id.fabEditUser);

        fabHideAnimation = AnimationUtils.loadAnimation(this, R.anim.fab_hide);
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

        Animation fabShowAnimation = AnimationUtils.loadAnimation(this, R.anim.fab_show);
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

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fab.startAnimation(fabHideAnimation);

                editTextName.setEnabled(true);
                editTextName.requestFocus();

                editTextName.setSelection(Objects.requireNonNull(editTextName.getText()).length());
                editTextDate.setEnabled(true);
                imagePhoto.setClickable(true);

                editUser = false;

                if (userPhotoUri.equals("No_Photo")) {

                    textDeleteUserPhoto.setVisibility(View.INVISIBLE);

                } else if (txtErr.getVisibility() == View.VISIBLE) {

                    GlideApp.with(imagePhoto).clear(imagePhoto);

                    txtErr.setVisibility(View.GONE);

                    imagePhoto.setImageResource(R.color.colorPrimaryLight);
                    imageUriInView = null;
                    loadedBitmap = null;
                    textViewNoUserPhoto.setVisibility(View.VISIBLE);
                    textDeleteUserPhoto.setVisibility(View.INVISIBLE);
                    userHasChangedPhoto = true;
                    userSetNoPhotoUri = "Set_No_Photo";

                } else {
                    textDeleteUserPhoto.setVisibility(View.VISIBLE);
                }

                if (!HomeActivity.isTablet) {
                    invalidateOptionsMenu();
                } else {
                    tabletFrmSave.setVisibility(View.VISIBLE);
                    tabletFrmDelete.setVisibility(View.GONE);
                }
            }
        });

        if (editUser) {
            if (HomeActivity.isTablet) {
                tabletFrmSave.setVisibility(View.GONE);
                tabletFrmDelete.setVisibility(View.VISIBLE);
            }

            editTextName.setEnabled(false);
            editTextDate.setEnabled(false);
            textDeleteUserPhoto.setVisibility(View.INVISIBLE);
            fab.startAnimation(fabShowAnimation);

            if (userPhotoUri.equals("No_Photo")) {
                imagePhoto.setClickable(true);
            } else {
                imagePhoto.setClickable(false);
            }
        } else if (userPhotoUri.equals("No_Photo")) {
            textDeleteUserPhoto.setVisibility(View.INVISIBLE);

        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
        GregorianCalendar date = new GregorianCalendar(year, monthOfYear, dayOfMonth);
        editTextDate.setText(simpleDateFormat.format(date.getTime()) + " ");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_WRITE_EXTERNAL_STORAGE) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                userHasChangedPhoto = true;
                Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(galleryIntent, RESULT_LOAD_IMAGE);
            } else {
                Snackbar.make(mLayout, R.string.permission_was_denied,
                        Snackbar.LENGTH_LONG).show();
                onLoading = false;
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && data != null) {
            Uri newSelectedImageUri = data.getData();

            if (newSelectedImageUri == null) {
                Toast.makeText(UserActivity.this, R.string.user_cant_load_photo, Toast.LENGTH_LONG).show();
                onLoading = false;
            } else {
                if (imageUriInView == null || !imageUriInView.equals(newSelectedImageUri)) {
                    loadPhotoIntoViewAndGetBitmap(newSelectedImageUri);
                } else {
                    onLoading = false;
                }
            }
        } else {
            onLoading = false;
        }
    }

    private void loadPhotoIntoViewAndGetBitmap(final Uri newSelectedImageUri) {
        GlideApp.with(this)
                .load(newSelectedImageUri)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        txtErr.setVisibility(View.VISIBLE);
                        userSetNoPhotoUri = "Set_No_Photo";
                        imageUriInView = null;
                        userHasChangedPhoto = true;
                        textDeleteUserPhoto.setVisibility(View.INVISIBLE);
                        textViewNoUserPhoto.setVisibility(View.GONE);

                        loadedBitmap = null;

                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        userSetNoPhotoUri = "";

                        if (txtErr.getVisibility() == View.VISIBLE) {

                            txtErr.setVisibility(View.GONE);

                        }

                        imageUriInView = newSelectedImageUri;
                        userHasChangedPhoto = true;
                        textDeleteUserPhoto.setVisibility(View.VISIBLE);
                        textViewNoUserPhoto.setVisibility(View.GONE);

                        loadedBitmap = null;

                        GlideApp.with(UserActivity.this)
                                .asBitmap()
                                .load(imageUriInView)
                                .into(new SimpleTarget<Bitmap>(imagePhoto.getWidth(), imagePhoto.getHeight()) {
                                    @Override
                                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                        loadedBitmap = resource;
                                    }
                                });

                        return false;
                    }
                })
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .error(R.color.my_dark_gray)
                .transition(DrawableTransitionOptions.withCrossFade(500))
                .into(imagePhoto);

        onLoading = false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        loadedBitmap = null;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        if (HomeActivity.isTablet) {
            return false;
        }

        getMenuInflater().inflate(R.menu.menu, menu);

        String deleteString = HomeActivity.iAmDoctor ? getResources().getString(R.string.patient_delete) : getResources().getString(R.string.user_delete);

        menu.removeItem(R.id.action_delete);
        menu.add(0, R.id.action_delete, 3, menuIconWithText(getResources().getDrawable(R.drawable.ic_delete_red_24dp),
                deleteString));

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

        if (editUser) {
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
                return onHomeClick();

            case R.id.action_save:
                return onSaveClick();

            case R.id.action_delete:
                return onDeleteClick();

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @SuppressWarnings("SameReturnValue")
    private boolean onHomeClick() {
        if (userHasNotChanged()) {
            goToUsersActivity();
            return true;
        }

        textInputLayoutName.setError(null);
        textInputLayoutDate.setError(null);

        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        if (dialog != null) {
                            dialog.dismiss();
                        }

                        goToUsersActivity();
                    }
                };

        showUnsavedChangesDialog(discardButtonClickListener);

        return true;
    }

    @SuppressWarnings("SameReturnValue")
    private boolean onSaveClick() {
        if (onSavingOrUpdatingOrDeleting) {
            return true;
        }

        onSavingOrUpdatingOrDeleting = true;

        if (userHasNotChanged() && !newUser) {

            goToUsersActivity();
            onSavingOrUpdatingOrDeleting = false;

            return true;

        } else {
            saveOrUpdateUser();
        }

        return true;
    }

    @SuppressWarnings("SameReturnValue")
    private boolean onDeleteClick() {
        if (onSavingOrUpdatingOrDeleting) {
            return true;
        }

        showDeleteConfirmationDialog();

        return true;
    }

    @Override
    public void onBackPressed() {
        if (userHasNotChanged()) {
            super.onBackPressed();

            goToUsersActivity();
            return;
        }

        textInputLayoutName.setError(null);
        textInputLayoutDate.setError(null);

        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        if (dialog != null) {
                            dialog.dismiss();
                        }

                        goToUsersActivity();
                    }
                };

        showUnsavedChangesDialog(discardButtonClickListener);
    }

    private void showDeleteConfirmationDialog() {

        String deleteString = HomeActivity.iAmDoctor ? getResources().getString(R.string.patient_delete) : getResources().getString(R.string.user_delete);

        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.DeleteAlertDialogCustom);
        builder.setMessage(deleteString + " " + editTextName.getText() + "?");
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (onSavingOrUpdatingOrDeleting) {
                    if (dialog != null) {
                        dialog.dismiss();
                    }
                } else {
                    onSavingOrUpdatingOrDeleting = true;

                    if (HomeActivity.isTablet){
                        TabletMainActivity.userDeletting = true;
                    }

                    deleteUserAndPhotos();
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

    private void showUnsavedChangesDialog(DialogInterface.OnClickListener discardButtonClickListener) {

        hideSoftInput();

        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AlertDialogCustom);
        builder.setMessage(R.string.dialog_msg_unsaved_changes);

        builder.setNegativeButton(R.string.dialog_no, discardButtonClickListener);

        builder.setPositiveButton(R.string.dialog_yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                goBack = true;
                saveOrUpdateUser();

                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();
    }

    private void saveOrUpdateUser() {
        ScaleAnimation scaleAnimation = new ScaleAnimation(0f, 1f, 0f, 0f);
        scaleAnimation.setDuration(500);

        String nameToCheck = Objects.requireNonNull(editTextName.getText()).toString().trim();
        String birthDateToCheck = Objects.requireNonNull(editTextDate.getText()).toString();
        boolean wrongField = false;

        if (TextUtils.isEmpty(nameToCheck)) {
            textInputLayoutName.setError(getString(R.string.user_error_name));
            focusHolder.requestFocus();
            editTextName.startAnimation(scaleAnimation);
            wrongField = true;
        } else {
            textInputLayoutName.setError(null);
        }

        if (TextUtils.isEmpty(birthDateToCheck)) {
            textInputLayoutDate.setError(getString(R.string.user_error_date));
            focusHolder.requestFocus();
            editTextDate.startAnimation(scaleAnimation);
            wrongField = true;
        } else {
            textInputLayoutDate.setError(null);
        }

        if (wrongField) {
            hideSoftInput();
            onSavingOrUpdatingOrDeleting = false;
            return;
        }

        hideSoftInput();

        focusHolder.requestFocus();

        textUserName = nameToCheck;
        textUserBirthDate = birthDateToCheck;

        if (!HomeActivity.isTablet) {
            actionBar.setTitle(textUserName);
        } else {
            txtTabletUserTitle.setText(textUserName);
        }

        if (userSetNoPhotoUri.equals("Set_No_Photo") && !userPhotoUri.equals("No_Photo")) {

            File fileToDelete = new File(userPhotoUri);

            if (fileToDelete.exists()) {

                if (!fileToDelete.delete()) {

                    Toast.makeText(this, R.string.user_photo_not_deleted, Toast.LENGTH_LONG).show();

                    SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
                    final SharedPreferences.Editor prefsEditor = prefs.edit();

                    String notDeletedFilesPaths = prefs.getString("notDeletedFilesPaths", null);
                    String updatedNotDeletedFilesPaths = notDeletedFilesPaths + "," + userPhotoUri;

                    prefsEditor.putString("notDeletedFilesPaths", updatedNotDeletedFilesPaths);
                    prefsEditor.apply();
                }
            }

            userPhotoUri = "No_Photo";
            userSetNoPhotoUri = "";
        }

        if (newUser) {
            saveUserToDataBase();
        }
        else {
            updateUserToDataBase();
        }
    }

    private void afterSaveUser() {
        if (!HomeActivity.isTablet) {
            if (goBack) {
                goToUsersActivity();
            } else {
                goToDiseasesActivity();
            }
        } else {
            goToUsersActivity();
        }
    }

    private void afterUpdateUser() {
        goToUsersActivity();
    }

    private boolean userHasNotChanged() {
        return !userHasChangedPhoto &&
                Objects.requireNonNull(editTextName.getText()).toString().equals(textUserName) &&
                Objects.requireNonNull(editTextDate.getText()).toString().equals(textUserBirthDate);
    }

    private void goToDiseasesActivity() {
        Intent toDiseasesIntent = new Intent(this, DiseasesActivity.class);
        toDiseasesIntent.putExtra("newUser", true);
        toDiseasesIntent.putExtra("_idUser", _idUser);
        toDiseasesIntent.putExtra("UserName", textUserName);
        startActivity(toDiseasesIntent);

        hideSoftInput();

        finish();
    }

    private void goToUsersActivity() {
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

    private void saveUserToDataBase() {

        ContentValues values = new ContentValues();
        values.put(UsersEntry.COLUMN_USER_NAME, textUserName);
        values.put(UsersEntry.COLUMN_USER_DATE, textUserBirthDate);
        values.put(UsersEntry.COLUMN_USER_PHOTO_PATH, userPhotoUri);

        Uri newUri = getContentResolver().insert(UsersEntry.CONTENT_USERS_URI, values);

        if (newUri != null) {

            _idUser = ContentUris.parseId(newUri);

            if (pathToUsersPhoto != null && loadedBitmap != null) {
                userPhotoUri = pathToUsersPhoto + _idUser + "-" + SystemClock.elapsedRealtime() + getString(R.string.user_photo_nameEnd);

                int rowsAffected = insertUserPhotoUriToDataBase(userPhotoUri);

                if (rowsAffected == 0) {
                    userPhotoUri = "No_Photo";
                    onSavingOrUpdatingOrDeleting = false;
                    Toast.makeText(UserActivity.this, R.string.user_cant_save_photo, Toast.LENGTH_LONG).show();

                    afterSaveUser();

                } else {
                    if (loadedBitmap != null) {

                        new UserPhotoSavingAsyncTask(this, loadedBitmap, null, userPhotoUri).execute();

                    } else {
                        onSavingOrUpdatingOrDeleting = false;
                        afterSaveUser();
                    }
                }
            } else {
                onSavingOrUpdatingOrDeleting = false;
                afterSaveUser();
            }
        } else {
            onSavingOrUpdatingOrDeleting = false;
            Toast.makeText(UserActivity.this, HomeActivity.iAmDoctor ? R.string.patient_cant_save : R.string.user_cant_save, Toast.LENGTH_LONG).show();
        }
    }

    private int insertUserPhotoUriToDataBase(String userPhotoUri) {

        ContentValues values = new ContentValues();

        values.put(UsersEntry.COLUMN_USER_PHOTO_PATH, userPhotoUri);

        Uri mCurrentUserUri = Uri.withAppendedPath(UsersEntry.CONTENT_USERS_URI, String.valueOf(_idUser));

        return getContentResolver().update(mCurrentUserUri, values, null, null);
    }

    private void updateUserToDataBase() {
        String userOldPhotoUri = null;

        if (pathToUsersPhoto != null && loadedBitmap != null) {
            userOldPhotoUri = userPhotoUri;

            userPhotoUri = pathToUsersPhoto + _idUser + "-" + SystemClock.elapsedRealtime() + getString(R.string.user_photo_nameEnd);
        }

        ContentValues values = new ContentValues();
        values.put(MedContract.UsersEntry.COLUMN_USER_NAME, textUserName);
        values.put(MedContract.UsersEntry.COLUMN_USER_DATE, textUserBirthDate);
        values.put(MedContract.UsersEntry.COLUMN_USER_PHOTO_PATH, userPhotoUri);

        Uri mCurrentUserUri = Uri.withAppendedPath(UsersEntry.CONTENT_USERS_URI, String.valueOf(_idUser));

        int rowsAffected = getContentResolver().update(mCurrentUserUri, values, null, null);

        if (rowsAffected == 0) {
            Toast.makeText(UserActivity.this, HomeActivity.iAmDoctor ? R.string.patient_cant_update : R.string.user_cant_update, Toast.LENGTH_LONG).show();

            userOldPhotoUri = null;
            goBack = false;
            afterUpdateUser();
        } else {
            if (loadedBitmap != null) {
                new UserPhotoSavingAsyncTask(this, loadedBitmap, userOldPhotoUri, userPhotoUri).execute();
            } else {
                userOldPhotoUri = null;
                afterUpdateUser();
            }
        }
    }

    private void deleteUserAndPhotos() {
        getLoaderManager().initLoader(USER_TR_PHOTOS_LOADER, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String[] projection = {
                TreatmentPhotosEntry.TR_PHOTO_ID,
                TreatmentPhotosEntry.COLUMN_TR_PHOTO_PATH};

        String selection = TreatmentPhotosEntry.COLUMN_U_ID + "=?";
        String[] selectionArgs = new String[]{String.valueOf(_idUser)};

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

        if (!TextUtils.equals(userPhotoUri, "No_Photo")) {
            photoFilePathsToBeDeletedList.add(userPhotoUri);
        }

        if (cursor != null) {
            cursor.moveToPosition(-1);

            while (cursor.moveToNext()) {
                int trPhoto_pathColumnIndex = cursor.getColumnIndex(TreatmentPhotosEntry.COLUMN_TR_PHOTO_PATH);
                String trPhotoUri = cursor.getString(trPhoto_pathColumnIndex);

                photoFilePathsToBeDeletedList.add(trPhotoUri);
            }
        }

        getLoaderManager().destroyLoader(USER_TR_PHOTOS_LOADER);

        new UserActivity.UserAndTreatmentPhotosDeletingAsyncTask(this, photoFilePathsToBeDeletedList).execute(getApplicationContext());
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }

    private static class UserAndTreatmentPhotosDeletingAsyncTask extends AsyncTask<Context, Void, Integer> {

        private static final String PREFS_NAME = "PREFS";

        private final WeakReference<UserActivity> userActivityReference;
        private final ArrayList<String> mPhotoFilePathsListToBeDeleted;
        private int mRowsFromUsersAndTreatmentPhotosDeleted = -1;

        UserAndTreatmentPhotosDeletingAsyncTask(UserActivity context, ArrayList<String> photoFilePathsListToBeDeleted) {
            userActivityReference = new WeakReference<>(context);
            mPhotoFilePathsListToBeDeleted = new ArrayList<>(photoFilePathsListToBeDeleted);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            UserActivity userActivity = userActivityReference.get();
            if (userActivity == null) {
                return;
            }

            mRowsFromUsersAndTreatmentPhotosDeleted = deleteUserAndDiseaseAndTreatmentPhotosFromDataBase(userActivity);
        }

        private int deleteUserAndDiseaseAndTreatmentPhotosFromDataBase(UserActivity userActivity) {
            ArrayList<ContentProviderOperation> deletingFromDbOperations = new ArrayList<>();

            String selectionTrPhotos = TreatmentPhotosEntry.COLUMN_U_ID + "=?";
            String[] selectionArgsTrPhotos = new String[]{String.valueOf(userActivity._idUser)};

            ContentProviderOperation deleteTreatmentPhotosFromDbOperation = ContentProviderOperation
                    .newDelete(TreatmentPhotosEntry.CONTENT_TREATMENT_PHOTOS_URI)
                    .withSelection(selectionTrPhotos, selectionArgsTrPhotos)
                    .build();

            deletingFromDbOperations.add(deleteTreatmentPhotosFromDbOperation);

            String selectionDiseases = DiseasesEntry.COLUMN_U_ID + "=?";
            String[] selectionArgsDiseases = new String[]{String.valueOf(userActivity._idUser)};

            ContentProviderOperation deleteDiseaseFromDbOperation = ContentProviderOperation
                    .newDelete(DiseasesEntry.CONTENT_DISEASES_URI)
                    .withSelection(selectionDiseases, selectionArgsDiseases)
                    .build();

            deletingFromDbOperations.add(deleteDiseaseFromDbOperation);

            String selectionUser = UsersEntry.U_ID + "=?";
            String[] selectionArgsUser = new String[]{String.valueOf(userActivity._idUser)};

            ContentProviderOperation deleteUserFromDbOperation = ContentProviderOperation
                    .newDelete(UsersEntry.CONTENT_USERS_URI)
                    .withSelection(selectionUser, selectionArgsUser)
                    .build();

            deletingFromDbOperations.add(deleteUserFromDbOperation);

            int rowsFromUsersAndTreatmentPhotosDeleted = -1;

            try {
                ContentProviderResult[] results = userActivity.getContentResolver().applyBatch(MedContract.CONTENT_AUTHORITY, deletingFromDbOperations);

                if (results.length == 3 && results[0] != null && results[2] != null) {
                    rowsFromUsersAndTreatmentPhotosDeleted = results[0].count + results[2].count;
                } else {
                    return rowsFromUsersAndTreatmentPhotosDeleted;
                }
            } catch (RemoteException | OperationApplicationException e) {
                e.printStackTrace();
                return rowsFromUsersAndTreatmentPhotosDeleted;
            }

            return rowsFromUsersAndTreatmentPhotosDeleted;
        }

        @Override
        protected Integer doInBackground(Context... contexts) {
            if (mRowsFromUsersAndTreatmentPhotosDeleted == -1) {
                return -1;
            } else if (mRowsFromUsersAndTreatmentPhotosDeleted == 0) {
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

            final UserActivity userActivity = userActivityReference.get();

            if (userActivity == null) {
                return;
            }

            if (result == -1) {
                userActivity.onSavingOrUpdatingOrDeleting = false;
                Toast.makeText(userActivity, HomeActivity.iAmDoctor ? R.string.patient_not_deleted : R.string.user_not_deleted, Toast.LENGTH_LONG).show();
            } else if (result == 0) {
                Toast.makeText(userActivity, R.string.treatment_image_not_deleted, Toast.LENGTH_LONG).show();
                userActivity.goToUsersActivity();
            } else {
                userActivity.goToUsersActivity();
            }
        }
    }

    private static class UserPhotoSavingAsyncTask extends AsyncTask<Void, Void, File> {
        private final WeakReference<UserActivity> userActivityReference;
        private final WeakReference<Bitmap> loadedBitmapReference;
        private final WeakReference<String> oldFileToDeletePathReference;
        private final WeakReference<String> fileToSavePathReference;

        UserPhotoSavingAsyncTask(UserActivity context, Bitmap loadedBitmap, String oldFileToDeletePath, String fileToSavePath) {
            userActivityReference = new WeakReference<>(context);
            loadedBitmapReference = new WeakReference<>(loadedBitmap);
            oldFileToDeletePathReference = new WeakReference<>(oldFileToDeletePath);
            fileToSavePathReference = new WeakReference<>(fileToSavePath);
        }

        @Override
        protected File doInBackground(Void... params) {

            String oldFileToDeletePath = oldFileToDeletePathReference.get();

            if (oldFileToDeletePath != null) {
                File oldFile = new File(oldFileToDeletePath);
                if (oldFile.exists()) {
                    if (!oldFile.delete()) {
                        final UserActivity userActivity = userActivityReference.get();

                        userActivity.myHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(userActivity, R.string.user_updated_photo_not_deleted, Toast.LENGTH_LONG).show();
                            }
                        });

                        SharedPreferences prefs = userActivity.getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
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
            }

            String fileToSavePath = fileToSavePathReference.get();

            if (fileToSavePath == null) {
                return null;
            }

            File fileToSave = new File(fileToSavePath);

            if (fileToSave.getParentFile().exists()) {
                try {
                    if (!fileToSave.createNewFile()) {
                        return null;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
            } else {
                if (fileToSave.getParentFile().mkdir()) {
                    try {
                        if (!fileToSave.createNewFile()) {
                            return null;
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        return null;
                    }
                } else {
                    return null;
                }
            }

            Bitmap loadedBitmap = loadedBitmapReference.get();
            if (loadedBitmap == null) {
                return null;
            }

            try (FileOutputStream outputStream = new FileOutputStream(fileToSave)) {
                loadedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                outputStream.flush();
                loadedBitmap = null;
            } catch (IOException e) {
                e.printStackTrace();
                loadedBitmap = null;
                return null;
            }

            return fileToSave;
        }

        @Override
        protected void onPostExecute(File savedFile) {
            super.onPostExecute(savedFile);

            UserActivity userActivity = userActivityReference.get();
            if (userActivity == null || userActivity.isFinishing()) {
                return;
            }

            if (savedFile == null) {
                Toast.makeText(userActivity, R.string.user_cant_save_photo, Toast.LENGTH_LONG).show();
                userActivity.goBack = false;
                userActivity.afterUpdateUser();
                return;
            }

            if (savedFile.exists()) {

                userActivity.userPhotoUri = savedFile.toString();

                if (userActivity.newUser) {
                    userActivity.afterSaveUser();
                } else {
                    userActivity.afterUpdateUser();
                }
            } else {
                userActivity.userPhotoUri = "No_Photo";
                userActivity.goBack = false;
                userActivity.afterUpdateUser();
                Toast.makeText(userActivity, R.string.user_cant_save_photo, Toast.LENGTH_LONG).show();
            }
        }
    }
}


