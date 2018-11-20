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


public class FullscreenPhotoActivity extends AppCompatActivity implements
        ActivityCompat.OnRequestPermissionsResultCallback,
        DatePickerDialog.OnDateSetListener {

    private static final String PREFS_NAME = "PREFS";

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

    /*private final Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            // скрывает UI
            hide();
        }
    };*/

    private final Runnable mtapedRunnable = new Runnable() {
        @Override
        public void run() {
            // выставляет tapped = false (вызывается с задержкой 100 мс)
            tapped = false;
        }
    };

    private final Runnable mToToggleRunnable = new Runnable() {
        @Override
        public void run() {
            // вызывает toggle() при исполнении условий (вызывается с задержкой 400 мс)
            if (!tapped && !inZoom[0] && !onLoading) {

                // если это телеон, то в режиме landscape не вызываем toggle();
                if (!HomeActivity.isTablet && landscape) {
                    return;
                }

                toggle();
            }
        }
    };

    // проверка в состоянии зума или нет
    private final boolean[] inZoom = {false};
    // onLoading - в процессе загрузки или нет
    private boolean mVisible, landscape, goBack, editTreatmentPhoto, newTreatmentPhoto, treatmentPhotoHasChanged, tapped;
    private boolean onLoading, onSavingOrUpdatingOrDeleting;

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

    // путь к сохраненному фото
    private String treatmentPhotoFilePath;

    // id фото лечения
    private long _idTrPhoto = 0;
    // id пользователя
    private long _idUser = 0;
    // id заболевания
    private long _idDisease = 0;

    // ImageView
    private ImageView imagePhoto;

    // TextView для ошибок при загрузке снимка
    private TextView txtErr;

    // код загрузки фото из галерии
    private static final int RESULT_LOAD_IMAGE = 9002;

    // код разрешения на запись и чтение из экстернал
    private static final int PERMISSION_WRITE_EXTERNAL_STORAGE = 0;

    // OrientationEventListener реагирует на угол наклона телефона
    private OrientationEventListener mOrientationListener;

    // размеры экрана для вставки снимка
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

        // editTreatmentPhoto приходит только из планшета из WideView
        //editTreatmentPhoto = intent.getBooleanExtra("editTreatmentPhoto", false);

        // если это планшет, то ориентация все время LANDSCAPE
        if (HomeActivity.isTablet) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        } else if (newTreatmentPhoto) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

        // получаем ориентацию экрана
        int myScreenOrientation = getResources().getConfiguration().orientation;

        // получаем размеры экрана
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        // если это телефон с шириной экрана меньше 1000
        // то снимки будут загружаться на базу увеличенную вдвое, чтоб не терялось качество при зумме
        if (!HomeActivity.isTablet && size.x < 1000) {
            displayWidth = size.x * 2;
            displayheight = size.y * 2;
        } else {
            // в остальных случаях (телефон и планшет)
            // size.x - 1 и size.y - 1, чтоб снимок не залипал по краям imagePhoto при зумме
            displayWidth = size.x - 1;
            displayheight = size.y - 1;
        }


        // инициализируем все View
        findViewsById();

        // устанавливаем слушатели
        setMyListeners();

        // если при первом вхождении иориентация LANDSCAPE, то делаем  hide();
        if (myScreenOrientation == Configuration.ORIENTATION_LANDSCAPE) {
            // если это телефон и в горизонтальном положении, то делаем hide();
            if (!HomeActivity.isTablet) {
                hide();
            } else {
                if (!newTreatmentPhoto) {
                    // если это планшет (планшет только в горизантальном) и не устанавливается новое фото
                    fab.startAnimation(fabShowAnimation);
                } else {
                    show();
                }
            }

            landscape = true;

        } else if (!newTreatmentPhoto) {
            // если это телефон (т.к. толлько телефон может быть в вертикальном положении)
            fab.startAnimation(fabShowAnimation);
        }

        // если пришел путь к сохраненному ранее фото, то грузим фото
        if (!newTreatmentPhoto && treatmentPhotoFilePath != null && new File(treatmentPhotoFilePath).exists()) {

            // скрываем UI
            //hide();

            //imagePhoto.setScaleType(ImageView.ScaleType.FIT_CENTER);


            //if (HomeActivity.isTablet) {
                /*Log.d("AAAAA", "displayWidth = " + displayWidth);
                Log.d("AAAAA", "displayheight = " + displayheight);*/


            // получаем размеры снимка из файла
            // здесь Uri.encode НЕ надо делать, т.к. мы знаем, что путь к файлу без спец символов
                /*BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeFile(new File(URI.create(treatmentPhotoFilePath).getPath()).getAbsolutePath(), options);
                int imageHeight = options.outHeight;
                int imageWidth = options.outWidth;*/

            /*Log.d("AAAAA", "treatmentPhotoFilePath = " + treatmentPhotoFilePath);

            // trImage-2-3-115442903.jpg это белая собачка из папки Медпункта
            // тестируем ошибку при загрузке фото
            if (treatmentPhotoFilePath.endsWith("trImage-2-3-115442903.jpg")) {
                treatmentPhotoFilePath = "";
            }

            Log.d("AAAAA", "test treatmentPhotoFilePath = " + treatmentPhotoFilePath);*/

            // грузим картинку в imagePhoto
            // в случае, если это планшет
            // здесь .override(displayWidth, displayheight),
            // чтоб не было залипания по краям imagePhoto (т.к. imagePhoto FullScreen) и зумминг работал нормально
            // при этом .dontTransform() убираем


            GlideApp.with(this)
                    .load(new File(treatmentPhotoFilePath))
                    //.dontTransform()
                    //.override(imageWidth, imageHeight)
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            //on load failed

                            // чтоб файл освободился (для удаления),
                            // высвобождаем imagePhoto
                            /*myHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    Glide.with(FullscreenPhotoActivity.this).clear(imagePhoto);
                                    //Glide.get(FullscreenPhotoActivity.this).clearMemory();

                                    *//*GlideApp.with(FullscreenPhotoActivity.this).
                                            load(R.drawable.eda).into(imagePhoto);*//*

                                    imagePhoto.setImageResource(R.color.my_dark_gray);
                                    txtErr.setVisibility(View.VISIBLE);
                                }
                            });*/

                            //imagePhoto.setImageResource(R.color.my_dark_gray);
                            txtErr.setVisibility(View.VISIBLE);

                            /*new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    Glide.get(FullscreenPhotoActivity.this).clearDiskCache();
                                }
                            }).start();*/


                            /*frm_save.setVisibility(View.INVISIBLE);
                            LL_title.startAnimation(LL_title_showAnimation);

                            mDescriptionView.setVisibility(View.INVISIBLE);
                            editTextDateOfTreatmentPhoto.setVisibility(View.INVISIBLE);*/

                            if (!HomeActivity.isTablet) {
                                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                                landscape = false;
                            }

                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            //on load success

                            if (txtErr.getVisibility() == View.VISIBLE) {

                                txtErr.setVisibility(View.GONE);
                                /*frm_save.setVisibility(View.VISIBLE);

                                mDescriptionView.setVisibility(View.VISIBLE);
                                editTextDateOfTreatmentPhoto.setVisibility(View.VISIBLE);*/
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

            /*final TextView txtErr = findViewById(R.id.err_view);


            GlideApp.with(this)
                    .load(R.drawable.error_camera_alt_gray_128dp)
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            //on load failed
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            //on load success
                            txtErr.setVisibility(View.VISIBLE);
                            return false;
                        }
                    })
                    .override(displayWidth, displayheight)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .into(imagePhoto);*/

            /*} else {
                // грузим картинку в imagePhoto
                // для телефона грузим .override(displayWidth * 2, displayheight * 2)
                GlideApp.with(this)
                        .load(treatmentPhotoFilePath)
                        //.dontTransform()
                        .override(displayWidth * 2, displayheight * 2)
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .skipMemoryCache(true)
                        .error(R.drawable.error_camera_alt_gray_128dp)
                        .transition(DrawableTransitionOptions.withCrossFade())
                        .into(imagePhoto);
            }*/

            imagePhoto.setScaleType(ImageView.ScaleType.FIT_CENTER);

            // скрываем UI
            //hide();
            //delayedHide();

            // если не новое фото и не может загрузить
        } else if (!newTreatmentPhoto) {
            Toast.makeText(this, R.string.treatment_cant_load_image, Toast.LENGTH_LONG).show();
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
            textDateOfTreatmentPhoto = getString(R.string.fullscreen_date_of_image);
        }

        // если было нажато добваить новое фото
        // перед загрузкой фото получаем разреншение на чтение (и запись) из экстернал
        if (newTreatmentPhoto) {

            editTreatmentPhoto = true;

            if (ActivityCompat.checkSelfPermission(FullscreenPhotoActivity.this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)
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
                //hide();

                // запрашиваем картинку
                Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(galleryIntent, RESULT_LOAD_IMAGE);
            }

        } else {
            mDescriptionView.setVisibility(View.INVISIBLE);
            editTextDateOfTreatmentPhoto.setVisibility(View.INVISIBLE);
            frm_save.setVisibility(View.GONE);

            /*// если при первом вхождении иориентация LANDSCAPE, то делаем  hide();
            if (myScreenOrientation == Configuration.ORIENTATION_LANDSCAPE) {
                // если это телефон, то делаем hide();
                if (!HomeActivity.isTablet) {
                    hide();
                }

                landscape = true;
            }*/
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
        txtErr = findViewById(R.id.treatment_photo_err_view);
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
                if (photoAndDescriptionHasNotChanged() || txtErr.getVisibility() == View.VISIBLE) {
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

                // убираем показ ошибок в textInputLayoutPhotoDescription
                textInputLayoutPhotoDescription.setError(null);
                textInputLayoutPhotoDescription.setErrorEnabled(false);
                editTextDateOfTreatmentPhoto.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                textInputLayoutPhotoDescription.setHintTextAppearance(R.style.Lable);

                // выбираем дату фото
                // в версии Build.VERSION_CODES.N нет календаря с прокруткой
                // поэтому для вывода календаря с прокруткой пользуемся стронней библиетекой
                // слушатель прописываем в нашем же классе .callback(FullscreenPhotoActivity.this)
                // com.tsongkha.spinnerdatepicker.SpinnerDatePickerDialogBuilder
                // используем эту библиотеку для
                // Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    String dateInEditTextDate = editTextDateOfTreatmentPhoto.getText().toString().trim();

                    int mYear;
                    int mMonth;
                    int mDay;

                    // если в поле dateInEditTextDate уже была установленна дата, то
                    // получаем ее и открываем диалог с этой датой
                    if (dateInEditTextDate.contains("-")) {
                        String[] mDayMonthYear = dateInEditTextDate.split("-");
                        mYear = Integer.valueOf(mDayMonthYear[2]);
                        mMonth = Integer.valueOf(mDayMonthYear[1]) - 1;
                        mDay = Integer.valueOf(mDayMonthYear[0]);
                    } else {
                        // если в поле dateInEditTextDate не была установлена дата
                        // то открываем диалог с текущей датой
                        final Calendar c = Calendar.getInstance();
                        mYear = c.get(Calendar.YEAR);
                        mMonth = c.get(Calendar.MONTH);
                        mDay = c.get(Calendar.DAY_OF_MONTH);
                    }

                    // используем стороннюю библиотеку для диалога SpinnerDatePickerDialog
                    DatePickerDialog spinnerDatePickerDialog = new SpinnerDatePickerDialogBuilder()
                            .context(FullscreenPhotoActivity.this)
                            .callback(FullscreenPhotoActivity.this)
                            .spinnerTheme(R.style.NumberPickerStyle)
                            .defaultDate(mYear, mMonth, mDay)
                            .build();

                    spinnerDatePickerDialog.setCanceledOnTouchOutside(false);
                    spinnerDatePickerDialog.show();
                } else {
                    // в остальных случаях пользуемся классом DatePickerFragment
                    DatePickerFragment newFragment = new DatePickerFragment();
                    newFragment.show(getSupportFragmentManager(), "datePicker");
                }
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

    // слушатель по установке даты для Build.VERSION_CODES.LOLIPOP
    @SuppressLint("SetTextI18n")
    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
        GregorianCalendar date = new GregorianCalendar(year, monthOfYear, dayOfMonth);
        editTextDateOfTreatmentPhoto.setText(simpleDateFormat.format(date.getTime()) + " ");
    }

    /*@Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        *//*if (!editTreatmentPhoto) {
            delayedHide();
            //hide();
        }*//*
    }*/


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        // изменение оринтации только для телефона
        if (HomeActivity.isTablet || txtErr.getVisibility() == View.VISIBLE) {
            return;
        }

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

    /*@Override
    protected void onResume() {
        super.onResume();
        // если клавиатура была открыта для редактирования названия заболевания, то она снова откроется
        // если нет - то не откроется
        if (editTextPhotoDescription.hasFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        } else {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        }

        if (!editTreatmentPhoto) {
            delayedHide();
        }

        Log.d("AAAAA", "imagePhoto Width = " + imagePhoto.getWidth());
        Log.d("AAAAA", "imagePhoto height = " + imagePhoto.getHeight());

    }*/

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mOrientationListener.disable();
    }

    /*private void delayedHide() {
        myHandler.removeCallbacks(mHideRunnable);
        myHandler.postDelayed(mHideRunnable, 0);
    }*/


    // Диалог "Удалить фото заболевания или отменить удаление"
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

                //deleteTreatmentPhotoFromDataBase();

                /*if (deleteTreatmentPhoto()) {
                    deleteTreatmentPhotoFromDataBase();
                } else {
                    onSavingOrUpdatingOrDeleting = false;
                    Toast.makeText(FullscreenPhotoActivity.this, "TreatmentPhoto has NOT been Deleted from DataBase", Toast.LENGTH_LONG).show();
                }*/
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
        // Hide UI first
        LL_title.startAnimation(LL_title_hideAnimation);
        fab.startAnimation(fabHideAnimation);

        mDescriptionView.setVisibility(View.INVISIBLE);
        editTextDateOfTreatmentPhoto.setVisibility(View.INVISIBLE);
        mVisible = false;

        // Schedule a runnable to remove the status and navigation bar after a delay
        myHandler.removeCallbacks(mHidePart2Runnable);
        myHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
    }

    @SuppressLint("InlinedApi")
    private void show() {
        // Show the system bar
        imagePhoto.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);

        mVisible = true;

        // Schedule a runnable to display UI elements after a delay
        myHandler.removeCallbacks(mShowPart2Runnable);
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

                // это, перед выходом, показывает сверху статус бар,
                // чтоб при открывании предыдущего окна не было белой полосы сверху
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

    // здесь грузим фотку в imagePhoto
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && data != null) {

            // делаем imagePhoto.setEnabled(true) чтоб реагировал на клик
            imagePhoto.setEnabled(true);

            // показываем UI
            /*if (HomeActivity.isTablet) {
                show();
            }*/

            Uri newSelectedImageUri = data.getData();

            if (newSelectedImageUri != null) {
                // если выбрали то же фото, которое уже было загружено ИЗ ГАЛЕРИИ,
                // то повторно не грузим
                if (imageUriInView != null && imageUriInView.equals(newSelectedImageUri)) {
                    onLoading = false;
                    treatmentPhotoHasChanged = false;

                } else {
                    // чистим imagePhoto
                    Glide.with(FullscreenPhotoActivity.this).clear(imagePhoto);


                    // если это новое фото, то сначала делали hide() перед загрузкой фото
                    // а после загрузки фото show()
                    /*if (newTreatmentPhoto && !landscape) {
                        show();
                    }*/

                    // получаем путь на девайсе к загруженному фото
                    loadedImageFilePath = getLoadedImageFilePath(this, newSelectedImageUri);

                    /*Log.d("AAAAA", "displayWidth = " + displayWidth);
                    Log.d("AAAAA", "displayheight = " + displayheight);*/

                    //f (HomeActivity.isTablet) {
                    // в случае, если это планшет

                        /*String encodedLoadedImageFilePath = Uri.encode(loadedImageFilePath);

                        // получаем размеры снимка из файла
                        // loadedImageFilePath.replace(" ", "%20") меняет текстовый пробел в пути файла на HTML пробел
                        // иначе выскочит ошибка в URI.create
                        BitmapFactory.Options options = new BitmapFactory.Options();
                        options.inJustDecodeBounds = true;
                        BitmapFactory.decodeFile(
                                //new File(URI.create(loadedImageFilePath).getPath()).getAbsolutePath(),
                                //new File(URI.create(loadedImageFilePath.replace(" ", "%20")).getPath()).getAbsolutePath(),
                                new File(URI.create(encodedLoadedImageFilePath).getPath()).getAbsolutePath(),
                                options
                        );

                        int imageWidth = options.outWidth;
                        int imageHeight = options.outHeight;

                        Log.d("AAAAA", "imgWidth = " + imageWidth);
                        Log.d("AAAAA", "imgheight = " + imageHeight);*/

                    // получаем размеры экрана

                        /*Display display = getWindowManager().getDefaultDisplay();
                        Point size = new Point();
                        display.getSize(size);
                        int displayWidth = size.x;
                        int displayheight = size.y;

                        Log.d("AAAAA", "displayWidth = " + displayWidth);
                        Log.d("AAAAA", "displayheight = " + displayheight);

                        Log.d("AAAAA", "imagePhoto Width = " + imagePhoto.getWidth());
                        Log.d("AAAAA", "imagePhoto height = " + imagePhoto.getHeight());*/

                    /*Log.d("AAAAA", "loadedImageFilePath = " + loadedImageFilePath);
                    Log.d("AAAAA", "newSelectedImageUri = " + newSelectedImageUri);

                    // DSC_0001.JPG это кот из галереи
                    // // тестируем ошибку при загрузке фото
                    if (loadedImageFilePath.endsWith("DSC_0001.JPG")) {
                        newSelectedImageUri = Uri.parse("");
                    }

                    Log.d("AAAAA", "test newSelectedImageUri = " + newSelectedImageUri);*/

                    // грузим картинку в imagePhoto
                    // здесь .override(idisplayWidth - 1, displayheight - 1),
                    // чтоб не было залипания по краям imagePhoto (т.к. imagePhoto FullScreen) и зумминг работал нормально
                    // при этом .dontTransform() убираем
                    // если во время загрузки возникла ошибка,
                    // то оставляем возможность либо выйти без сохранения, либо заново загрузить снимок (этот или любой другой)
                    GlideApp.with(this)
                            .load(newSelectedImageUri)
                            .listener(new RequestListener<Drawable>() {
                                @Override
                                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                    //on load failed

                                    txtErr.setVisibility(View.VISIBLE);

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
                                    //on load success

                                    if (txtErr.getVisibility() == View.VISIBLE) {

                                        txtErr.setVisibility(View.GONE);
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
                            //.dontTransform()
                            //.override(imageWidth, imageHeight)
                            .override(displayWidth, displayheight)
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .skipMemoryCache(true)
                            .error(R.color.my_dark_gray)
                            .transition(DrawableTransitionOptions.withCrossFade())
                            .into(imagePhoto);

                    //final TextView txtErr = findViewById(R.id.err_view);

                    /*if (txtErr.getVisibility() == View.VISIBLE) {

                        txtErr.setVisibility(View.GONE);
                        frm_save.setVisibility(View.VISIBLE);

                        mDescriptionView.setVisibility(View.VISIBLE);
                        editTextDateOfTreatmentPhoto.setVisibility(View.VISIBLE);
                    }

                    GlideApp.with(this)
                            .load(newSelectedImageUri)
                            .listener(new RequestListener<Drawable>() {
                                @Override
                                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                    //on load failed
                                    return false;
                                }

                                @Override
                                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                    //on load success
                                    txtErr.setVisibility(View.VISIBLE);
                                    frm_save.setVisibility(View.INVISIBLE);

                                    mDescriptionView.setVisibility(View.INVISIBLE);
                                    editTextDateOfTreatmentPhoto.setVisibility(View.INVISIBLE);

                                    return false;
                                }
                            })
                            .override(displayWidth, displayheight)
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .skipMemoryCache(true)
                            .into(imagePhoto);*/

                    /*} else {
                        // для телефона грузим .override(displayWidth * 2, displayheight * 2)
                        GlideApp.with(this)
                                .load(newSelectedImageUri)
                                //.dontTransform()
                                .override(displayWidth * 2, displayheight * 2)
                                .diskCacheStrategy(DiskCacheStrategy.NONE)
                                .skipMemoryCache(true)
                                .error(R.drawable.error_camera_alt_gray_128dp)
                                .transition(DrawableTransitionOptions.withCrossFade())
                                .into(imagePhoto);
                    }*/

                    imagePhoto.setScaleType(ImageView.ScaleType.FIT_CENTER);
                    treatmentPhotoHasChanged = true;
                    imageUriInView = newSelectedImageUri;
                    onLoading = false;

                }
            }
        } else {
            // если отказались выбирать фото (нашажали "обратно")
            // если хотели создать новое фото - идем обратно
            // если приходили из уже существующего фото - остаемся с onLoading = false;

            onLoading = false;

            if (newTreatmentPhoto) {
                goToTreatmentActivity();
            }
        }

        // если это не планшет, то после загрузки нового фото ставим экран на SCREEN_ORIENTATION_SENSOR
        if (!HomeActivity.isTablet) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
        }
    }

    // метод для получения пути к загружаемому фото (loadedImageFilePath) для дальнейшего сохранения этого фото
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

    // в toggle
    private void toggle() {

        // если это не планшет, а телефон, то в ORIENTATION_LANDSCAPE) только просмотр
        if (!HomeActivity.isTablet &&
                getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            return;
        }

        // либо скрываем-показываем элементы UI
        if (!editTreatmentPhoto) {
            if (mVisible) {
                hide();
            } else {
                show();
            }
            // либо обращаемся к галерее фото
        } else {

            if (onLoading || onSavingOrUpdatingOrDeleting) {
                return;
            }

            // если это телефон (а не планшет), то перед загрузкой нового фото ставим экран в PORTRAIT
            if (!HomeActivity.isTablet) {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            }

            onLoading = true;

            /*if (txtErr.getVisibility() == View.VISIBLE) {

                txtErr.setVisibility(View.GONE);
                frm_save.setVisibility(View.VISIBLE);

                mDescriptionView.setVisibility(View.VISIBLE);
                editTextDateOfTreatmentPhoto.setVisibility(View.VISIBLE);
            }*/

            // если это новое фото, то сначала делаем hide() перед загрузкой фото
            // а далее будет show()
            /*if (newTreatmentPhoto) {
                hide();
            }*/

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

        //getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    @Override
    public void onBackPressed() {
        if (photoAndDescriptionHasNotChanged() || txtErr.getVisibility() == View.VISIBLE) {
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

    // Диалог "сохранить или выйти без сохранения"
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

    // проверка на изменения описания, даты и фото
    private boolean photoAndDescriptionHasNotChanged() {
        return !treatmentPhotoHasChanged &&
                TextUtils.equals(editTextPhotoDescription.getText().toString(), textPhotoDescription) &&
                TextUtils.equals(editTextDateOfTreatmentPhoto.getText().toString(), textDateOfTreatmentPhoto);
    }

    private void goToTreatmentActivity() {

        // это, перед выходом, показывают сверху статус бар,
        // чтоб при открывании предыдущего окна не было белой полосы сверху
        //getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //getWindow().addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);

        if (imagePhoto.getSystemUiVisibility() == 0) {
            // если не видны системные флаги, то сначала показываем status bar, потом выходим
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
        } else {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
        }

        hideSoftInput();

        // а это закрывает текущее окно после задержки в 300мс
        myHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();
            }
        }, UI_ANIMATION_DELAY);
    }

    private void saveOrUpdateTreatmentPhoto() {

        // устанавливаем анимацию на случай Error
        ScaleAnimation scaleAnimation = new ScaleAnimation(0f, 1f, 0f, 0f);
        scaleAnimation.setDuration(500);

        String photoDescriptionToCheck = editTextPhotoDescription.getText().toString().trim();
        String dateOfTreatmentPhotoToCheck = editTextDateOfTreatmentPhoto.getText().toString();
        boolean wrongField = false;

        // првоерка описания фото
        if (TextUtils.isEmpty(photoDescriptionToCheck)) {
            textInputLayoutPhotoDescription.setHintTextAppearance(R.style.Lable_Error);
            textInputLayoutPhotoDescription.setError(getString(R.string.fullscreen_error_image_description));
            editTextPhotoDescription.startAnimation(scaleAnimation);
            editTextPhotoDescription.requestFocus();
            wrongField = true;
        }

        // првоерка Даты фото
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

        // если поля описания и Дата фото не верные - выходим
        if (wrongField) {

            // если это телефон и не все поля заполнены и сохранение происходит в LANDSCAPE
            // (а это возможно только после свайпа сверху при фулскриин и нажатии "идти обратно",
            // после чего предлагается сохранить изменения)
            // то выставляем экран в PORTRAIT, чтоб можно было увидеть незаполненные поля
            // и включаем mOrientationListener, который будет мониторить повор экрана и
            // при углах между 315 и 45 вернет возможность реагирования на сенсор при повороте на LANDSCAPE
            if (!HomeActivity.isTablet && getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);

                mOrientationListener.enable();
            }

            onSavingOrUpdatingOrDeleting = false;

            return;
        } else {
            textInputLayoutPhotoDescription.setError(null);
        }

        // проверка окончена, начинаем сохранение
        textPhotoDescription = photoDescriptionToCheck;
        textDateOfTreatmentPhoto = dateOfTreatmentPhotoToCheck;

        // в отдельном потоке пишем файл фотки в интернал
        if (imageUriInView != null) {

            copyTreatmentPhotoAndSaveOrUpdateToDataBase();

            // imageUriInView = null, чтоб повторно не сохранять то же фото
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

            // выставляем флаг treatmentPhotoHasChanged = false
            treatmentPhotoHasChanged = false;
        }
    }

    private void copyTreatmentPhotoAndSaveOrUpdateToDataBase() {
        // сохранение загруженного фото
        if (loadedImageFilePath != null) {

            File fileOfPhoto = new File(loadedImageFilePath);

            // Получаем путь к файлам для интернал
            String root = getFilesDir().toString();


            String pathToTreatmentPhotos = root + getString(R.string.path_to_treatment_photos);

            //  /data/data/com.gmail.krbashianrafael.medpunkt/files/treatment_photos
            File myDir = new File(pathToTreatmentPhotos);

            if (!myDir.exists()) {
                if (!myDir.mkdirs()) {
                    Toast.makeText(this, R.string.treatment_cant_save_image, Toast.LENGTH_LONG).show();
                    return;
                }
            }

            // для нумерации сохраняемых файлов берем время SystemClock.elapsedRealtime();
            String destinationFileName = getString(R.string.treatment_photo_nameStart) +
                    _idUser + "-" +
                    _idDisease + "-" + SystemClock.elapsedRealtime() +
                    getString(R.string.treatment_photo_nameEnd);

            File destinationFile = new File(myDir, destinationFileName);

            if (newTreatmentPhoto) {
                // если новая (не обновляемая) фотография
                treatmentPhotoFilePath = destinationFile.toString();

                // если в Базу сохранилось удачно,
                // то копируем файл снимка в TreatmentPhotoCopyAsyncTask в отдельном потоке
                // и даем новому файлу имя
                // "Image-" + _idUser + "-" + _idDisease + "-" + SystemClock.elapsedRealtime() + ".jpg"
                if (saveTreatmentPhotoToDataBase()) {
                    // копируем fileOfPhoto в destinationFile
                    new TreatmentPhotoCopyAsyncTask(this).execute(fileOfPhoto, destinationFile);

                    // после сохранения в TreatmentPhotoCopyAsyncTask выставляем флаги = false
                    /*newTreatmentPhoto = false;
                    treatmentPhotoHasChanged = false;
                    onSavingOrUpdatingOrDeleting = false;

                    afterSaveOrUpdate();*/
                } else {
                    onSavingOrUpdatingOrDeleting = false;
                    Toast.makeText(this, R.string.treatment_cant_save_image, Toast.LENGTH_LONG).show();
                }

            } else {
                // если фотография обновляется,
                // то сначала удаляем старую фотографию
                File oldFile = new File(treatmentPhotoFilePath);
                if (oldFile.exists()) {
                    if (!oldFile.delete()) {
                        Toast.makeText(this, R.string.treatment_old_image_not_deleted, Toast.LENGTH_LONG).show();

                        // если файл не удалился, то
                        // получаем SharedPreferences, чтоб писать путь к неудаленному файлу в "PREFS"
                        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
                        final SharedPreferences.Editor prefsEditor = prefs.edit();

                        // вытягиваем в String notDeletedFilesPaths из prefs пути к ранее не удаленным файлам
                        String notDeletedFilesPaths = prefs.getString("notDeletedFilesPaths", null);
                        // дописываем путь (за запятой) к неудаленному файлу фото польлзователя

                        String updatedNotDeletedFilesPaths;
                        if (notDeletedFilesPaths == null) {
                            updatedNotDeletedFilesPaths = oldFile.getPath();
                        } else {
                            updatedNotDeletedFilesPaths = notDeletedFilesPaths + "," + oldFile.getPath();
                        }

                        // пишем заново в в "PREFS" обновленную строку
                        prefsEditor.putString("notDeletedFilesPaths", updatedNotDeletedFilesPaths);
                        prefsEditor.apply();
                    }
                }

                // после удаления старого файла
                // присваиваем новый путь обновляемому снимку
                treatmentPhotoFilePath = destinationFile.toString();

                // обновляем путь к файлу в базе
                if (updateTreatmentPhotoToDataBase()) {
                    // если в Базу сохранилось удачно,
                    // то копируем файл снимка в TreatmentPhotoCopyAsyncTask в отдельном потоке
                    // и даем новому файлу имя
                    // "Image-" + _idUser + "-" + _idDisease + "-" + SystemClock.elapsedRealtime() + ".jpg"
                    new TreatmentPhotoCopyAsyncTask(this).execute(fileOfPhoto, destinationFile);

                    /// после сохранения в TreatmentPhotoCopyAsyncTask выставляем флаги = false
                    /*newTreatmentPhoto = false;
                    treatmentPhotoHasChanged = false;
                    onSavingOrUpdatingOrDeleting = false;

                    afterSaveOrUpdate();*/

                } else {
                    // если путь к обновленному файлу в базу не сохранился
                    Toast.makeText(this, R.string.treatment_cant_save_image, Toast.LENGTH_LONG).show();

                    // выставляем флаги false
                    treatmentPhotoHasChanged = false;
                    onSavingOrUpdatingOrDeleting = false;
                    afterSaveOrUpdate();
                }

                // после сохранения выставляем флаги false
                /*treatmentPhotoHasChanged = false;
                onSavingOrUpdatingOrDeleting = false;
                afterSaveOrUpdate();*/
            }
        } else {
            // если снимок не загрузился
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

        // при сохранении снимка в Базу делаем insert и получаем Uri вставленной строки
        Uri newUri = getContentResolver().insert(TreatmentPhotosEntry.CONTENT_TREATMENT_PHOTOS_URI, values);

        if (newUri != null) {
            // получаем _idTrPhoto из возвращенного newUri
            _idTrPhoto = ContentUris.parseId(newUri);

            // здесь устанавливаем флаг scrollToInsertedUserPosition в классе DiseasesActivity в true
            // чтоб после вставки новой строки в Базу и посел оповещения об изменениях
            // заново загрузился курсор и RecyclerView прокрутился вниз до последней позиции

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

        // Uri к снимку, который будет обновляться
        Uri mCurrentUserUri = Uri.withAppendedPath(TreatmentPhotosEntry.CONTENT_TREATMENT_PHOTOS_URI, String.valueOf(_idTrPhoto));

        // делаем update в Базе
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
                // если файл не удалился
                Toast.makeText(this, R.string.treatment_image_not_deleted, Toast.LENGTH_LONG).show();

                // получаем SharedPreferences, чтоб писать путь к неудаленному файлу в "PREFS"
                SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
                final SharedPreferences.Editor prefsEditor = prefs.edit();

                // вытягиваем в String notDeletedFilesPaths из prefs пути к ранее не удаленным файлам
                String notDeletedFilesPaths = prefs.getString("notDeletedFilesPaths", null);
                // дописываем путь (за запятой) к неудаленному файлу фото польлзователя

                String updatedNotDeletedFilesPaths;
                if (notDeletedFilesPaths == null) {
                    updatedNotDeletedFilesPaths = toBeDeletedFile.getPath();
                } else {
                    updatedNotDeletedFilesPaths = notDeletedFilesPaths + "," + toBeDeletedFile.getPath();
                }

                // пишем заново в в "PREFS" обновленную строку
                prefsEditor.putString("notDeletedFilesPaths", updatedNotDeletedFilesPaths);
                prefsEditor.apply();
            }
        }

        deleteTreatmentPhotoFromDataBase();
    }

    private void deleteTreatmentPhotoFromDataBase() {

        // Uri к снимку, который будет удаляться
        Uri mCurrentTrPhotoUri = Uri.withAppendedPath(TreatmentPhotosEntry.CONTENT_TREATMENT_PHOTOS_URI, String.valueOf(_idTrPhoto));

        int rowsDeleted = 0;

        // удаляем снимок из Базы
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

    // мой Zoom класс
    private class MyImageMatrixTouchHandler extends ImageMatrixTouchHandler {

        MyImageMatrixTouchHandler(Context context) {
            super(context);
            //setDoubleTapZoomFactor(2f); // здесь устанавливаем кратность увеличение при DoubleTap, по умолчанию = 2,5
            ImageViewerCorrector crr = (ImageViewerCorrector) this.getImageMatrixCorrector();
            //Log.d("AAAA", "crr.getMaxScale() = " + crr.getMaxScale());
            crr.setMaxScale(20f); // здесь устанавливаем максимальную кратность увеличения, по умолчанию = 4
        }

        // этот конструктор не используется
        /*MyImageMatrixTouchHandler(Context context, ImageMatrixCorrector corrector) {
            super(context, corrector);
        }*/

        // для DoubleTap
        boolean firstTouch = false;
        long touchTime = 0;

        @Override
        public boolean onTouch(final View view, final MotionEvent event) {

            /*Log. d("AAAAA", "imagePhoto.getWidth()" + imagePhoto.getWidth());
            Log. d("AAAAA", "imagePhoto.getHeight() = " + imagePhoto.getHeight());*/

            //Log.d("SSSS", "getImageMatrixCorrector() = " + getImageMatrixCorrector());
            //ImageViewerCorrector crr = (ImageViewerCorrector) this.getImageMatrixCorrector();
            //Log.d("SSSS", "crr = " + crr);
            //Log.d("SSSS", "crr.getMaxScale() = " + crr.getMaxScale());
            //Log.d("SSSS", "crr.getMaxScale() = " + crr.isMaxScaleRelative());
            //crr.setMaxScaleRelative(true);
            //crr.setMaxScale(8f);


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

                // через 100 мс выставляем tapped = false
                // чтоб при ACTION_DOWN с реакцией экрана на скольжение пальца (ACTION_MOVE) inZoom[0] был false
                myHandler.postDelayed(mtapedRunnable, 100);

                // првоерка DoubleTap (в промежутке 300 мс)
                // если DoubleTap, то не вызывается toggle()
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

    // класс TreatmentPhotoCopyAsyncTask (для копирования файла фото) делаем статическим,
    // чтоб не было утечки памяти при его работе
    private static class TreatmentPhotoCopyAsyncTask extends AsyncTask<File, Void, Void> {

        //private static final String PREFS_NAME = "PREFS";

        // получаем WeakReference на FullscreenPhotoActivity,
        // чтобы GC мог его собрать
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
                // если при копировании возникли ошибки, но файл образовался, то удаляем его
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
                        // получаем SharedPreferences, чтоб писать путь к неудаленному файлу в "PREFS"
                        SharedPreferences prefs = fullscreenPhotoActivity.getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
                        final SharedPreferences.Editor prefsEditor = prefs.edit();

                        // вытягиваем в String notDeletedFilesPaths из prefs пути к ранее не удаленным файлам
                        String notDeletedFilesPaths = prefs.getString("notDeletedFilesPaths", null);
                        // дописываем путь (за запятой) к неудаленному файлу фото польлзователя

                        String updatedNotDeletedFilesPaths;
                        if (notDeletedFilesPaths == null) {
                            updatedNotDeletedFilesPaths = destination.getPath();
                        } else {
                            updatedNotDeletedFilesPaths = notDeletedFilesPaths + "," + destination.getPath();
                        }

                        // пишем заново в в "PREFS" обновленную строку
                        prefsEditor.putString("notDeletedFilesPaths", updatedNotDeletedFilesPaths);
                        prefsEditor.apply();
                    }
                }

                //e.printStackTrace();

                return null;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            final FullscreenPhotoActivity fullscreenPhotoActivity = fullscreenPhotoActivityReference.get();

            // после сохранения в TreatmentPhotoCopyAsyncTask выставляем флаги = false
            fullscreenPhotoActivity.newTreatmentPhoto = false;
            fullscreenPhotoActivity.treatmentPhotoHasChanged = false;
            fullscreenPhotoActivity.onSavingOrUpdatingOrDeleting = false;

            fullscreenPhotoActivity.afterSaveOrUpdate();
        }
    }
}