package com.gmail.krbashianrafael.medpunkt;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.ScaleAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bogdwellers.pinchtozoom.ImageMatrixCorrector;
import com.bogdwellers.pinchtozoom.ImageMatrixTouchHandler;
import com.squareup.picasso.Picasso;


public class FullscreenPhotoActivity extends AppCompatActivity {

    private static final int UI_ANIMATION_DELAY = 300;
    private final Handler mHideHandler = new Handler();
    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            // Delayed removal of status and navigation bar
            // Note that some of these constants are new as of API 16 (Jelly Bean)
            // and API 19 (KitKat). It is safe to use them, as they are inlined
            // at compile-time and do nothing on earlier devices.
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

    private final Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };

    private View mDescriptionView;
    private FloatingActionButton fab;
    private boolean mVisible, landscape, goBack, editTreatmentPhoto, newTreatmentPhoto, treatmentPhotoHasChanged;

    private View LL_title, frm_back, frm_blank, frm_save, frm_delete;
    private EditText focusHolder, editTextDateOfTreatmentPhoto;
    private TextInputLayout textInputLayoutPhotoDescription;
    private TextInputEditText editTextPhotoDescription;
    private String textPhotoDescription, textDateOfTreatmentPhoto;

    private Animation LL_title_hideAnimation;
    private Animation LL_title_showAnimation;

    private Animation fabHideAnimation;
    private Animation fabShowAnimation;

    // путь к загружаемому фото
    private Uri selectedImage;

    // фото
    private ImageView imagePhoto;

    private int myScreenWidthPx;
    private int myScreenHeightPx;

    // zoom
    private MyImageMatrixTouchHandler myImageMatrixTouchHandler;

    // код загрузки фото из галерии
    private static final int RESULT_LOAD_IMAGE = 9002;

    // код разрешения на запись и чтение из экстернал
    private static final int PERMISSION_WRITE_EXTERNAL_STORAGE = 0;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullscreen);

        Resources r = getResources();
        int myScreenHeightDp = r.getConfiguration().screenHeightDp;
        myScreenHeightPx = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, myScreenHeightDp, r.getDisplayMetrics());

        int myScreenWidthDp = r.getConfiguration().screenWidthDp;
        myScreenWidthPx = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, myScreenWidthDp, r.getDisplayMetrics());

        int myScreenOrientation = r.getConfiguration().orientation;

        Intent intent = getIntent();

        //_id_disease = intent.getIntExtra("_id_disease", 0);
        textPhotoDescription = intent.getStringExtra("textPhotoDescription");
        textDateOfTreatmentPhoto = intent.getStringExtra("textDateOfTreatmentPhoto");
        newTreatmentPhoto = intent.getBooleanExtra("newTreatmentPhoto", false);

        // инициализируем все View
        findViewsById();

        // устанавливаем слушатели
        setOnClickAndOnTouchListeners();

        // записываем в поля описание и дату пришедшего снимка
        if (textPhotoDescription != null) {
            editTextPhotoDescription.setText(textPhotoDescription);
        }
        else {
            textPhotoDescription = "";
        }

        if (textDateOfTreatmentPhoto != null) {
            editTextDateOfTreatmentPhoto.setText(textDateOfTreatmentPhoto);
        }
        else {
            textDateOfTreatmentPhoto = getString(R.string.date_of_treatment_photo);
        }

        // если было нажато добваить фото
        // перед загрузкой фото получаем разреншение на чтение (и запись) из экстернал
        if (newTreatmentPhoto) {

            editTreatmentPhoto = true;

            if (ActivityCompat.checkSelfPermission(FullscreenPhotoActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                // Запрашиваем разрешение на чтение и запись фото
                MyReadWritePermissionHandler.getReadWritePermission(FullscreenPhotoActivity.this, imagePhoto, PERMISSION_WRITE_EXTERNAL_STORAGE);
            } else {


                // устанавливаем вид в зависимости от ориентации экрана при первом вхождении
                if (myScreenOrientation == Configuration.ORIENTATION_PORTRAIT) {
                    editTextPhotoDescription.requestFocus();
                    editTextPhotoDescription.setSelection(editTextPhotoDescription.getText().toString().length());
                    frm_delete.setVisibility(View.GONE);
                } else {
                    mDescriptionView.setVisibility(View.INVISIBLE);
                    editTextDateOfTreatmentPhoto.setVisibility(View.INVISIBLE);
                    frm_save.setVisibility(View.GONE);
                    landscape = true;
                }

                // т.к. это новое фото, то сначала делаем hide() перед загрузкой фото
                // а после загрузки фото show()
                hide();

                // запрашиваем картинку
                Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(galleryIntent, RESULT_LOAD_IMAGE);
            }
        } else {
            mDescriptionView.setVisibility(View.INVISIBLE);
            editTextDateOfTreatmentPhoto.setVisibility(View.INVISIBLE);
            frm_save.setVisibility(View.GONE);

            // если при первом вхождении иориентация LANDSCAPE, то делаем  hide();
            if (myScreenOrientation == Configuration.ORIENTATION_LANDSCAPE) {
                hide();
                landscape = true;
            }
        }
    }

    private void findViewsById() {
        mVisible = true;
        LL_title = findViewById(R.id.LL_title);
        frm_back = findViewById(R.id.frm_back);
        frm_blank = findViewById(R.id.frm_blank);
        frm_save = findViewById(R.id.frm_save);
        frm_delete = findViewById(R.id.frm_delete);
        focusHolder = findViewById(R.id.focus_holder);
        editTextDateOfTreatmentPhoto = findViewById(R.id.editText_date);
        mDescriptionView = findViewById(R.id.fullscreen_content_description);
        imagePhoto = findViewById(R.id.fullscreen_image);
        textInputLayoutPhotoDescription = findViewById(R.id.text_input_layout_photo_description);
        editTextPhotoDescription = findViewById(R.id.editText_photo_description);
        fab = findViewById(R.id.fabEditTreatmentPhoto);

        // zoomer
        myImageMatrixTouchHandler = new MyImageMatrixTouchHandler(this);

        LL_title_hideAnimation = AnimationUtils.loadAnimation(this, R.anim.fullscreenphoto_title_hide);
        LL_title_showAnimation = AnimationUtils.loadAnimation(this, R.anim.fullscreenphoto_title_show);
        fabShowAnimation = AnimationUtils.loadAnimation(this, R.anim.fab_show);
        fabHideAnimation = AnimationUtils.loadAnimation(this, R.anim.fab_hide);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setOnClickAndOnTouchListeners() {

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
                if (photoAndDescriptionHasNotChanged()) {
                    goToTreatmentActivity();
                } else {
                    // Если были изменения
                    // если выходим без сохранения изменений
                    DialogInterface.OnClickListener discardButtonClickListener =
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    goToTreatmentActivity();
                                }
                            };

                    // если выходим с сохранением изменений
                    showUnsavedChangesDialog(discardButtonClickListener);
                }
            }
        });

        frm_blank.setOnClickListener(new View.OnClickListener() {
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
                if (photoAndDescriptionHasNotChanged() && !newTreatmentPhoto) {

                    hideSoftInput();

                    editTreatmentPhoto = false;
                    mDescriptionView.setVisibility(View.INVISIBLE);
                    editTextDateOfTreatmentPhoto.setVisibility(View.INVISIBLE);
                    fab.setVisibility(View.VISIBLE);
                    frm_save.setVisibility(View.GONE);
                    frm_delete.setVisibility(View.VISIBLE);
                } else {
                    focusHolder.requestFocus();
                    saveTreatmentPhoto();
                }
            }
        });

        frm_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteTreatmentPhotoFromDataBase();
            }
        });

        editTextDateOfTreatmentPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                hideSoftInput();

                // убираем показ ошибок в textInputLayoutPhotoDescription
                textInputLayoutPhotoDescription.setError(null);
                textInputLayoutPhotoDescription.setErrorEnabled(false);
                editTextDateOfTreatmentPhoto.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                textInputLayoutPhotoDescription.setHintTextAppearance(R.style.Lable);

                // выбираем дату фото
                DatePickerFragment newFragment = new DatePickerFragment();
                newFragment.show(getSupportFragmentManager(), "datePicker");
            }
        });

        // чтоб textInputLayoutPhotoDescription не реагировал на Click
        textInputLayoutPhotoDescription.setOnClickListener(null);

        // при editTextPhotoDescription OnTouch убираем ошибку
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

        // на imagePhoto устанавливаем мой MatrixTouchHandler
        imagePhoto.setOnTouchListener(myImageMatrixTouchHandler);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDescriptionView.setVisibility(View.VISIBLE);
                editTextDateOfTreatmentPhoto.setVisibility(View.VISIBLE);
                editTextPhotoDescription.requestFocus();
                editTextPhotoDescription.setSelection(editTextPhotoDescription.getText().toString().length());
                fab.setVisibility(View.INVISIBLE);
                editTreatmentPhoto = true;
                frm_save.setVisibility(View.VISIBLE);
                frm_delete.setVisibility(View.GONE);

                imagePhoto.setScaleType(ImageView.ScaleType.FIT_CENTER);
            }
        });
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // если грузится с фото, то скрываем элементы UI
        // TODO проверить загрузку фото
        if (!editTreatmentPhoto) {
            delayedHide(300);
        }
    }

    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }

    private void hide() {
        // Hide UI first
        LL_title.startAnimation(LL_title_hideAnimation);
        fab.startAnimation(fabHideAnimation);

        mDescriptionView.setVisibility(View.INVISIBLE);
        editTextDateOfTreatmentPhoto.setVisibility(View.INVISIBLE);
        mVisible = false;

        // Schedule a runnable to remove the status and navigation bar after a delay
        mHideHandler.removeCallbacks(mShowPart2Runnable);
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
    }

    @SuppressLint("InlinedApi")
    private void show() {
        // Show the system bar
        imagePhoto.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);

        mVisible = true;

        // Schedule a runnable to display UI elements after a delay
        mHideHandler.removeCallbacks(mHidePart2Runnable);
        mHideHandler.postDelayed(mShowPart2Runnable, UI_ANIMATION_DELAY);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        // при поворотах скрываем клавиатуру
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

    // результат запроса на загрузку фото
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
            }
        }
    }

    // здесь грузим фотку в imagePhoto
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && data != null) {

            selectedImage = data.getData();

            if (selectedImage != null) {

                // если это новое фото, то сначала делали hide() перед загрузкой фото
                // а после загрузки фото show()
                if (newTreatmentPhoto && !landscape) {
                    show();
                }

                float rotate = getRotation(this, selectedImage);

                Picasso.get().load(selectedImage).
                        placeholder(R.color.colorAccent).
                        error(R.color.colorAccentSecondary).
                        resize(myScreenWidthPx, myScreenHeightPx).
                        rotate(rotate).
                        centerInside().
                        into(imagePhoto);

                textInputLayoutPhotoDescription.setError(null);
                textInputLayoutPhotoDescription.setErrorEnabled(false);

                imagePhoto.setScaleType(ImageView.ScaleType.FIT_CENTER);

                // флаг об изменении фото
                treatmentPhotoHasChanged = true;
            }

            // TODO реализовать сохранение фото
            // SystemClock.elapsedRealtime(); - для нумерации сохраняемых файлов
            // для экстернал
            // String root = Environment.getExternalStorageDirectory().toString();
            // File myDir = new File(root + "/Medpunkt/users_photos");
            // при этом получится File imgFile = new File("/storage/emulated/0/Medpunkt/users_photos/Image-1.jpg");

            // для интернал
                /*
                String root = getFilesDir().toString();
                File myDir = new File(root + "/treatment_photos");

                if (!myDir.mkdirs()) {
                    Log.d("myDir.mkdirs", "users_photos_dir_Not_created");
                }

                String fileName = "Image-" + 2 + ".jpg";
                final File file = new File(myDir, fileName);

                //Log.d("file", "file = " + file);
                // при этом путь к файлу
                // получается: /data/data/com.gmail.krbashianrafael.medpunkt/files/treatment_photos/Image-2.jpg

                // заменяем файл удалением, т.к. у юзера бдует тольок одно фото
                if (file.exists()) {
                    if (!file.delete()) {
                        Toast.makeText(FullscreenPhotoActivity.this, R.string.file_not_deleted, Toast.LENGTH_LONG).show();
                    }
                }

                new Thread(new Runnable() {
                    FileOutputStream outputStream;

                    @Override
                    public void run() {
                        try {
                            Bitmap bitmap = Picasso.get().load(selectedImage).
                                    resize(myScreenHeightDp, myScreenWidthDp).
                                    centerInside().
                                    get();
                            outputStream = new FileOutputStream(file);
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                            outputStream.flush();
                            outputStream.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
                ).start();
                */

        }
        // если не выбрали фото идем обратно
        else {
            if (newTreatmentPhoto) {
                goToTreatmentActivity();
            }
        }
    }

    // метод для получения оринетации (угол поворота) фотографии
    // т.к. Picasso все фото вставляет боком, то нужно поворачивать на нужный угол
    private int getRotation(Context context, Uri photoUri) {
        Cursor cursor = context.getContentResolver().query(photoUri,
                new String[]{MediaStore.Images.ImageColumns.ORIENTATION}, null, null, null);

        if (cursor != null) {
            if (cursor.getCount() != 1) {
                cursor.close();
                return -1;
            }
            cursor.moveToFirst();
        }

        int orientation = 0;
        if (cursor != null) {
            orientation = cursor.getInt(0);
            cursor.close();
            cursor = null;
        }

        return orientation;
    }

    public void toggle() {
        if (!editTreatmentPhoto) {
            if (mVisible) {
                hide();
            } else {
                show();
            }
        } else {
            // если это новое фото, то сначала делаем hide() перед загрузкой фото
            // а далее будет show()
            if (newTreatmentPhoto) {
                hide();
            }

            // перед загрузкой фото получаем разреншение на чтение (и запись) из экстернал
            if (ActivityCompat.checkSelfPermission(FullscreenPhotoActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                // Запрашиваем разрешение на чтение и запись фото
                MyReadWritePermissionHandler.getReadWritePermission(FullscreenPhotoActivity.this, imagePhoto, PERMISSION_WRITE_EXTERNAL_STORAGE);
            } else {
                Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(galleryIntent, RESULT_LOAD_IMAGE);
            }
        }
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

    @Override
    public void onBackPressed() {
        if (photoAndDescriptionHasNotChanged()) {
            super.onBackPressed();
            return;
        }

        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                };

        showUnsavedChangesDialog(discardButtonClickListener);
    }

    // Диалог "сохранить или выйти без сохранения"
    private void showUnsavedChangesDialog(DialogInterface.OnClickListener
                                                  discardButtonClickListener) {

        hideSoftInput();

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);

        builder.setNegativeButton(R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                goBack = true;

                if (dialog != null) {
                    dialog.dismiss();
                }

                saveTreatmentPhoto();
            }
        });

        builder.setPositiveButton(R.string.no, discardButtonClickListener);

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    // проверка на изменения описания, даты и фото
    private boolean photoAndDescriptionHasNotChanged() {
        return !treatmentPhotoHasChanged &&
                TextUtils.equals(editTextPhotoDescription.getText().toString(), textPhotoDescription) &&
                TextUtils.equals(editTextDateOfTreatmentPhoto.getText().toString(), textDateOfTreatmentPhoto);
    }

    private void goToTreatmentActivity() {
        Intent intent = new Intent(FullscreenPhotoActivity.this, TreatmentActivity.class);
        startActivity(intent);
    }

    private void saveTreatmentPhoto() {

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_USER_PORTRAIT);
        }

        hideSoftInput();

        // устанавливаем анимацию на случай Error
        ScaleAnimation scaleAnimation = new ScaleAnimation(0f, 1f, 0f, 0f);
        scaleAnimation.setDuration(500);

        String photoDescriptionToCheck = editTextPhotoDescription.getText().toString().trim();
        String dateOfTreatmentPhotoToCheck = editTextDateOfTreatmentPhoto.getText().toString();
        boolean wrongField = false;

        // првоерка описания фото
        if (TextUtils.isEmpty(photoDescriptionToCheck)) {
            textInputLayoutPhotoDescription.setHintTextAppearance(R.style.Lable_Error);
            textInputLayoutPhotoDescription.setError(getString(R.string.error_photo_description));
            editTextPhotoDescription.startAnimation(scaleAnimation);
            editTextPhotoDescription.requestFocus();
            wrongField = true;
        }

        // првоерка Даты фото
        if (TextUtils.equals(dateOfTreatmentPhotoToCheck, getString(R.string.date_of_treatment_photo))) {
            if (wrongField) {
                textInputLayoutPhotoDescription.setError(
                        getString(R.string.error_photo_description) + ".\n" +
                                getString(R.string.error_date_of_treatment_photo)
                );
            } else {
                textInputLayoutPhotoDescription.setError(getString(R.string.error_date_of_treatment_photo));
            }

            editTextDateOfTreatmentPhoto.setTextColor(getResources().getColor(R.color.colorFab));
            editTextDateOfTreatmentPhoto.startAnimation(scaleAnimation);
            wrongField = true;
        }

        // если поля описания и Дата фото не верные - выходим
        if (wrongField) {
            return;
        }

        // проверка окончена, начинаем сохранение
        textPhotoDescription = photoDescriptionToCheck;
        textDateOfTreatmentPhoto = dateOfTreatmentPhotoToCheck;

        // TODO это перенести в сохранение фото
        treatmentPhotoHasChanged = false;

        if (newTreatmentPhoto) {
            saveTreatmentPhotoToDataBase();
            newTreatmentPhoto = false;

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

        } else {
            updateTreatmentPhotoToDataBase();

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
    }

    private void saveTreatmentPhotoToDataBase() {
        //TODO реализовать сохранение пользователя в базу
        // т.к. Toast.makeText вызывается не с основного треда, надо делать через Looper
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(FullscreenPhotoActivity.this, "TreatmentPhoto Saved To DataBase", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void updateTreatmentPhotoToDataBase() {
        //TODO реализовать обновление пользователя в базу
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(FullscreenPhotoActivity.this, "TreatmentPhoto Updated To DataBase", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void deleteTreatmentPhotoFromDataBase() {
        //TODO реализовать удаление пользователя из базы
        Toast.makeText(this, "TreatmentPhoto Deleted from DataBase", Toast.LENGTH_LONG).show();
    }


    // мой Zoom класс
    private class MyImageMatrixTouchHandler extends ImageMatrixTouchHandler {

        MyImageMatrixTouchHandler(Context context) {
            super(context);
        }

        MyImageMatrixTouchHandler(Context context, ImageMatrixCorrector corrector) {
            super(context, corrector);
        }

        // проверка в состоянии зума или нет
        final boolean[] inZoom = {false};

        // для DoubleTap
        boolean firstTouch = false;
        long time = 0;

        @Override
        public boolean onTouch(final View view, final MotionEvent event) {

            view.performClick();

            boolean result = super.onTouch(view, event);

            if (event.getActionMasked() == MotionEvent.ACTION_DOWN) {
                // првоерка DoubleTap (в промежутке 300 мс)
                // если DoubleTap, то не вызывается toggle()
                if (firstTouch && (System.currentTimeMillis() - time) <= 300) {
                    inZoom[0] = true;
                    firstTouch = false;
                } else {
                    firstTouch = true;
                    inZoom[0] = false;
                    time = System.currentTimeMillis();
                }

                // в отдельном потоке ожидаем 0.5 сек после ACTION_DOWN
                // в это время основной поток продолжает свою работу
                // если будут какие-то движения - они пойдут дальше
                // а, если не было никаких движений (увеличение, перемещение картинки)
                // то делаем  toggle()
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(500);
                            if (!inZoom[0] && !landscape) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        toggle();
                                    }
                                });
                            }
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }

            // в остальных случаях не вызывается  toggle() блокировкой  inZoom[0] = true;
            if (event.getActionMasked() == MotionEvent.ACTION_POINTER_DOWN ||
                    event.getActionMasked() == MotionEvent.ACTION_POINTER_UP ||
                    event.getActionMasked() == MotionEvent.ACTION_MOVE) {

                inZoom[0] = true;
            }

            return result;
        }
    }
}