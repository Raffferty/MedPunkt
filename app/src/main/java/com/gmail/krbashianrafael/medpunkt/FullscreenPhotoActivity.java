package com.gmail.krbashianrafael.medpunkt;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.hardware.SensorManager;
import android.support.media.ExifInterface;
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
import android.widget.Toast;

import com.bogdwellers.pinchtozoom.ImageMatrixCorrector;
import com.bogdwellers.pinchtozoom.ImageMatrixTouchHandler;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;


public class FullscreenPhotoActivity extends AppCompatActivity
        implements ActivityCompat.OnRequestPermissionsResultCallback {

    private static final int UI_ANIMATION_DELAY = 300;
    private final Handler myHandler = new Handler(Looper.getMainLooper());

    // Мой zoom класс
    private MyImageMatrixTouchHandler myImageMatrixTouchHandler;

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
            // показывает Title и остальные UI
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
            // скрывает UI
            hide();
        }
    };

    private final Runnable mtapedRunnable = new Runnable() {
        @Override
        public void run() {
            // выставляет taped = false (вызывается с задержкой 100 мс)
            taped = false;
        }
    };

    private final Runnable mToToggleRunnable = new Runnable() {
        @Override
        public void run() {
            // вызывает toggle() при исполнении условий (вызывается с задержкой 400 мс)
            if (!taped && !inZoom[0] && !landscape && !onLoading) {
                toggle();
            }
        }
    };

    private final Runnable mTretmentPhotoSavingRunnable = new Runnable() {
        @Override
        public void run() {
            // сохранение загруженного фото
            if (loadedImageFilePath != null) {
                try {
                    fileOfPhoto = new File(loadedImageFilePath);
                    // TODO создать логику имени сохраняемого фото
                    // SystemClock.elapsedRealtime(); - для нумерации сохраняемых файлов

                    // для интернал
                    /*String root = getFilesDir().toString();
                    File myDir = new File(root + "/treatment_photos"); //  /data/data/com.gmail.krbashianrafael.medpunkt/files/treatment_photos
                    if (!myDir.mkdirs()) {
                        Log.d("file", "users_photos_dir_Not_created");
                    }
                    //String fileName = "Image-" + _idUser + "-" + SystemClock.elapsedRealtime() + ".jpg";
                    String fileName = "Image-" + 2 + "-" + SystemClock.elapsedRealtime() + ".jpg";
                    File destination = new File(myDir, fileName);*/

                    // при этом путь к файлу:
                    //File destination = new File("/data/data/com.gmail.krbashianrafael.medpunkt/files/treatment_photos/Image-2.jpg");
                    File destination = new File(getString(R.string.path_to_treatment_photo));

                    // пока удаляем существующий файл, чтоб не плодить
                    if (destination.exists()) {
                        if (!destination.delete()) {
                            Toast.makeText(FullscreenPhotoActivity.this, R.string.file_not_deleted, Toast.LENGTH_LONG).show();
                        }
                    }

                    FileUtils.copyFile(fileOfPhoto, destination);

                    if (destination.exists()) {
                        treatmentPhotoFilePath = destination.toString();
                    } else {
                        Toast.makeText(FullscreenPhotoActivity.this, R.string.cant_save_photo, Toast.LENGTH_LONG).show();
                        return;
                    }

                    if (newTreatmentPhoto) {
                        saveTreatmentPhotoToDataBase();
                        newTreatmentPhoto = false;

                        if (goBack) {
                            goToTreatmentActivity();
                        } else {
                            editTreatmentPhoto = false;
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mDescriptionView.setVisibility(View.INVISIBLE);
                                    editTextDateOfTreatmentPhoto.setVisibility(View.INVISIBLE);
                                    fab.setVisibility(View.VISIBLE);
                                    frm_save.setVisibility(View.GONE);
                                    frm_delete.setVisibility(View.VISIBLE);
                                }
                            });
                        }

                    } else {
                        updateTreatmentPhotoToDataBase();

                        if (goBack) {
                            goToTreatmentActivity();
                        } else {
                            editTreatmentPhoto = false;
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mDescriptionView.setVisibility(View.INVISIBLE);
                                    editTextDateOfTreatmentPhoto.setVisibility(View.INVISIBLE);
                                    fab.setVisibility(View.VISIBLE);
                                    frm_save.setVisibility(View.GONE);
                                    frm_delete.setVisibility(View.VISIBLE);
                                }
                            });
                        }
                    }

                    // после сохранения выставляем флаг treatmentPhotoHasChanged = false
                    treatmentPhotoHasChanged = false;

                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                //  Toast должен делаться в основном потоке
                myHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(FullscreenPhotoActivity.this, R.string.cant_save_photo, Toast.LENGTH_LONG).show();
                    }
                });
            }
        }
    };

    // проверка в состоянии зума или нет
    final boolean[] inZoom = {false};
    // onLoading - в процессе загрузки или нет
    private boolean mVisible, landscape, goBack, editTreatmentPhoto, newTreatmentPhoto, treatmentPhotoHasChanged, taped, onLoading;

    private View mDescriptionView, LL_title, frm_back, frm_blank, frm_save, frm_delete;
    private EditText focusHolder, editTextDateOfTreatmentPhoto;
    private TextInputLayout textInputLayoutPhotoDescription;
    private TextInputEditText editTextPhotoDescription;
    private String textPhotoDescription, textDateOfTreatmentPhoto;
    private FloatingActionButton fab;

    private Animation LL_title_hideAnimation, LL_title_showAnimation, fabHideAnimation, fabShowAnimation;

    // контентный путь к загруженному фото из Галерии
    private Uri imageUriInView;

    // файловый путь к загружаемому фото
    private String loadedImageFilePath;

    // загруженный файл фотографии
    private File fileOfPhoto = null;

    // путь к сохраненному фото
    private String treatmentPhotoFilePath;

    // id заболевания
    private int _idDisease = 0;

    // ImageView
    private ImageView imagePhoto;

    // код загрузки фото из галерии
    private static final int RESULT_LOAD_IMAGE = 9002;

    // код разрешения на запись и чтение из экстернал
    private static final int PERMISSION_WRITE_EXTERNAL_STORAGE = 0;

    // OrientationEventListener реагирует на угол наклона телефона
    private OrientationEventListener mOrientationListener;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullscreen);

        int myScreenOrientation = getResources().getConfiguration().orientation;

        Intent intent = getIntent();

        _idDisease = intent.getIntExtra("_idDisease", 0);
        treatmentPhotoFilePath = intent.getStringExtra("treatmentPhotoFilePath");
        textPhotoDescription = intent.getStringExtra("textPhotoDescription");
        textDateOfTreatmentPhoto = intent.getStringExtra("textDateOfTreatmentPhoto");
        newTreatmentPhoto = intent.getBooleanExtra("newTreatmentPhoto", false);

        // инициализируем все View
        findViewsById();

        // устанавливаем слушатели
        setMyListeners();

        // если пришел путь к сохраненному ранее фото, то грузим фото
        if (treatmentPhotoFilePath != null) {

            hide();

            // получаем угол поворота картинки из файла
            float rotate = 0f;
            try {
                ExifInterface exifInterface = new ExifInterface(treatmentPhotoFilePath);
                int IMAGE_ORIENTATION = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, 0);

                switch (IMAGE_ORIENTATION) {
                    case ExifInterface.ORIENTATION_ROTATE_180: // 3
                        rotate = 180f;
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_90: // 6
                        rotate = -90f;
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_270: // 8
                        rotate = 90f;
                        break;
                    default:
                        break;
                }
            } catch (IOException e) {
                Toast.makeText(this, R.string.no_image_orientation, Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }

            // если угол = 0 - вставляем картинку без трансформации
            // если не 0 - с трансформацией (поворот в обратную сторону для выравнивания)
            if (rotate == 0) {
                GlideApp.with(this)
                        .load(treatmentPhotoFilePath)
                        .dontTransform()
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .skipMemoryCache(true)
                        .error(R.drawable.error_camera_alt_gray_128dp)
                        .transition(DrawableTransitionOptions.withCrossFade())
                        .into(imagePhoto);
            } else {
                GlideApp.with(this)
                        .load(treatmentPhotoFilePath)
                        .transform(new RotateTransformation(rotate))
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .skipMemoryCache(true)
                        .error(R.drawable.error_camera_alt_gray_128dp)
                        .transition(DrawableTransitionOptions.withCrossFade())
                        .into(imagePhoto);
            }

            imagePhoto.setScaleType(ImageView.ScaleType.FIT_CENTER);
        }

        // записываем в поля описание и дату пришедшего снимка
        if (textPhotoDescription != null) {
            editTextPhotoDescription.setText(textPhotoDescription);
        } else {
            textPhotoDescription = "";
        }

        if (textDateOfTreatmentPhoto != null) {
            editTextDateOfTreatmentPhoto.setText(textDateOfTreatmentPhoto);
        } else {
            textDateOfTreatmentPhoto = getString(R.string.date_of_treatment_photo);
        }

        // если было нажато добваить фото
        // перед загрузкой фото получаем разреншение на чтение (и запись) из экстернал
        if (newTreatmentPhoto) {
            editTreatmentPhoto = true;

            if (ActivityCompat.checkSelfPermission(FullscreenPhotoActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {


                // убираем UI, кроме SYSTEM_UI_FLAG_HIDE_NAVIGATION
                mDescriptionView.setVisibility(View.INVISIBLE);
                editTextDateOfTreatmentPhoto.setVisibility(View.INVISIBLE);
                mVisible = false;
                LL_title.startAnimation(LL_title_hideAnimation);
                fab.startAnimation(fabHideAnimation);

                // делаем imagePhoto.setEnabled(false) чтоб не реагировал на клик
                imagePhoto.setEnabled(false);

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

        LL_title_hideAnimation = AnimationUtils.loadAnimation(this, R.anim.fullscreenphoto_title_hide);
        LL_title_showAnimation = AnimationUtils.loadAnimation(this, R.anim.fullscreenphoto_title_show);
        fabShowAnimation = AnimationUtils.loadAnimation(this, R.anim.fab_show);
        fabHideAnimation = AnimationUtils.loadAnimation(this, R.anim.fab_hide);

        // Мой zoomer
        myImageMatrixTouchHandler = new MyImageMatrixTouchHandler(this);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setMyListeners() {

        // если угол наколона телефона между 315 и 45, то востанавливаем возможность реагировать на сенсор
        // и отключаем mOrientationListener
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

        // чтоб LL_title не реагировал на Click
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

        // при OnTouch editTextPhotoDescription убираем ошибку
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

        // на OnTouch imagePhoto устанавливаем myImageMatrixTouchHandler
        // здесь обрабытываются zoom and pinch
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
        if (!editTreatmentPhoto) {
            delayedHide(300);
        }
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mOrientationListener.disable();
    }

    private void delayedHide(int delayMillis) {
        myHandler.removeCallbacks(mHideRunnable);
        myHandler.postDelayed(mHideRunnable, delayMillis);
    }

    private void hide() {
        // Hide UI first
        LL_title.startAnimation(LL_title_hideAnimation);
        fab.startAnimation(fabHideAnimation);

        mDescriptionView.setVisibility(View.INVISIBLE);
        editTextDateOfTreatmentPhoto.setVisibility(View.INVISIBLE);
        mVisible = false;

        // Schedule a runnable to remove the status and navigation bar after a delay
        myHandler.removeCallbacks(mShowPart2Runnable);
        myHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
    }

    @SuppressLint("InlinedApi")
    private void show() {
        // Show the system bar
        imagePhoto.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);

        mVisible = true;

        // Schedule a runnable to display UI elements after a delay
        myHandler.removeCallbacks(mHidePart2Runnable);
        myHandler.postDelayed(mShowPart2Runnable, UI_ANIMATION_DELAY);
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

                // это, перед выходом, показывают сверху статус бар,
                // чтоб при открывании предыдущего окна не было белой полосы сверху
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);

                myHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        finish();
                    }
                }, UI_ANIMATION_DELAY * 7);
            }
        }
    }

    // здесь грузим фотку в imagePhoto
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && data != null) {

            // делаем imagePhoto.setEnabled(true) чтоб реагировал на клик
            imagePhoto.setEnabled(true);

            // показываем UI
            show();

            Uri newSelectedImageUri = data.getData();

            if (newSelectedImageUri != null) {
                if (imageUriInView != null && imageUriInView.equals(newSelectedImageUri)) {
                    onLoading = false;
                    treatmentPhotoHasChanged = false;

                } else {
                    // чистим imagePhoto
                    Glide.with(this).clear(imagePhoto);

                    // если это новое фото, то сначала делали hide() перед загрузкой фото
                    // а после загрузки фото show()
                    if (newTreatmentPhoto && !landscape) {
                        show();
                    }

                    // получаем угол обратного (-1*) поворота картинки для приведения ее в вертикальное положение
                    float rotate = -1 * getRotation(this, newSelectedImageUri);

                    // грузим картинку в imagePhoto
                    if (rotate == 0f) {
                        GlideApp.with(this)
                                .load(newSelectedImageUri)
                                .dontTransform()
                                .diskCacheStrategy(DiskCacheStrategy.NONE)
                                .skipMemoryCache(true)
                                .error(R.drawable.error_camera_alt_gray_128dp)
                                .transition(DrawableTransitionOptions.withCrossFade())
                                .into(imagePhoto);
                    } else {
                        GlideApp.with(this)
                                .load(newSelectedImageUri)
                                .transform(new RotateTransformation(rotate))
                                .diskCacheStrategy(DiskCacheStrategy.NONE)
                                .skipMemoryCache(true)
                                .error(R.drawable.error_camera_alt_gray_128dp)
                                .transition(DrawableTransitionOptions.withCrossFade())
                                .into(imagePhoto);
                    }

                    imagePhoto.setScaleType(ImageView.ScaleType.FIT_CENTER);
                    treatmentPhotoHasChanged = true;
                    imageUriInView = newSelectedImageUri;
                    onLoading = false;
                }
            }
        }
        // если отказались выбирать фото (нашажали "обратно")
        // если хотели создать новое фото - идем обратно
        // если приходили из уже существующего фото - остаемся с onLoading = false;
        else {
            onLoading = false;

            if (newTreatmentPhoto) {
                goToTreatmentActivity();
            }
        }
    }

    // метод для получения оринетации (угол поворота) фотографии
    // и, в этом же методе получаем путь к загружаемому фото (loadedImageFilePath) для дальнейшего сохранения этого фото
    private int getRotation(Context context, Uri photoUri) {
        Cursor cursor = context.getContentResolver().query(photoUri,
                new String[]{MediaStore.Images.ImageColumns.ORIENTATION, MediaStore.Images.Media.DATA}, null, null, null);

        if (cursor != null) {
            if (cursor.getCount() != 1) {
                cursor.close();
                return -1;
            }
            cursor.moveToFirst();
        }

        int imageOrientation = 0;

        if (cursor != null) {
            imageOrientation = cursor.getInt(0);
            loadedImageFilePath = cursor.getString(1);

            cursor.close();
            cursor = null;
        }

        return imageOrientation;
    }

    // в toggle
    public void toggle() {
        // либо скрываем-показываем элементы UI
        if (!editTreatmentPhoto) {
            if (mVisible) {
                hide();
            } else {
                show();
            }
            // либо обращаемся к галерее фото
        } else {

            onLoading = true;
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

    // метод для скрытия клавиатуры
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
        if (photoAndDescriptionHasNotChanged()) {
            goToTreatmentActivity();
            return;
        }

        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        goToTreatmentActivity();
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
        // это, перед выходом, показывают сверху статус бар,
        // чтоб при открывании предыдущего окна не было белой полосы сверху
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);

        // а это закрывает текущее окно после небольшой задержки
        myHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();
            }
        }, UI_ANIMATION_DELAY);
    }

    private void saveTreatmentPhoto() {

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

            // если не все поля заполнены и сохранение происходит в LANDSCAPE (а это возможно только после свайпа сверху при фулскриин)
            // то выставляем экран в PORTRAIT, чтоб можно было увидеть незаполненные поля
            // и включаем mOrientationListener, который будет мониторить повор экрана и
            // при углах между 315 и 45 вернет возможность реагирования на сенсор при повороте на LANDSCAPE
            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);

                mOrientationListener.enable();
            }

            return;
        }

        // проверка окончена, начинаем сохранение
        textPhotoDescription = photoDescriptionToCheck;
        textDateOfTreatmentPhoto = dateOfTreatmentPhotoToCheck;

        // в отдельном потоке пишем файл фотки в интернал
        if (imageUriInView != null) {
            myHandler.removeCallbacks(mTretmentPhotoSavingRunnable);
            myHandler.post(mTretmentPhotoSavingRunnable);
        } else {
            if (newTreatmentPhoto) {
                saveTreatmentPhotoToDataBase();
                newTreatmentPhoto = false;

                if (goBack) {
                    goToTreatmentActivity();
                } else {
                    editTreatmentPhoto = false;

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mDescriptionView.setVisibility(View.INVISIBLE);
                            editTextDateOfTreatmentPhoto.setVisibility(View.INVISIBLE);
                            fab.setVisibility(View.VISIBLE);
                            frm_save.setVisibility(View.GONE);
                            frm_delete.setVisibility(View.VISIBLE);
                        }
                    });
                }

            } else {
                updateTreatmentPhotoToDataBase();

                if (goBack) {
                    goToTreatmentActivity();
                } else {
                    editTreatmentPhoto = false;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mDescriptionView.setVisibility(View.INVISIBLE);
                            editTextDateOfTreatmentPhoto.setVisibility(View.INVISIBLE);
                            fab.setVisibility(View.VISIBLE);
                            frm_save.setVisibility(View.GONE);
                            frm_delete.setVisibility(View.VISIBLE);
                        }
                    });
                }
            }

            // выставляем флаг treatmentPhotoHasChanged = false
            treatmentPhotoHasChanged = false;

        }
    }

    private void saveTreatmentPhotoToDataBase() {
        //TODO реализовать сохранение пользователя в базу
        // т.к. Toast.makeText вызывается не с основного треда, надо делать через Looper
        myHandler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(FullscreenPhotoActivity.this, "TreatmentPhoto Saved To DataBase", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void updateTreatmentPhotoToDataBase() {
        //TODO реализовать обновление пользователя в базу
        myHandler.post(new Runnable() {
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

        // для DoubleTap
        //boolean taped = false;
        boolean firstTouch = false;
        //boolean firstMove = false;
        long touchTime = 0;
        //long moveTime = 0;

        @Override
        public boolean onTouch(final View view, final MotionEvent event) {

            view.performClick();

            if (event.getActionMasked() == MotionEvent.ACTION_UP) {
                taped = false;
                if (firstTouch) inZoom[0] = false;
            }

            super.onTouch(view, event);

            if (event.getActionMasked() == MotionEvent.ACTION_POINTER_DOWN) {
                inZoom[0] = true;
            }

            if (event.getActionMasked() == MotionEvent.ACTION_MOVE) {
                if (taped) {
                    inZoom[0] = false;
                } else {
                    inZoom[0] = true;
                }
            }

            if (event.getActionMasked() == MotionEvent.ACTION_DOWN) {

                taped = true;

                myHandler.removeCallbacks(mtapedRunnable);

                // через 100 мс выставляем taped = false
                // чтоб при ACTION_DOWN с реакцией экрана на скольжение пальца (ACTION_MOVE) inZoom[0] был false
                myHandler.postDelayed(mtapedRunnable, 100);

                // првоерка DoubleTap (в промежутке 300 мс)
                // если DoubleTap, то не вызывается toggle()
                if (firstTouch && (System.currentTimeMillis() - touchTime) <= 300) {
                    taped = false;
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
}