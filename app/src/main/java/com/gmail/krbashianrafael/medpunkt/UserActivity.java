package com.gmail.krbashianrafael.medpunkt;

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
import android.os.RemoteException;
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
import android.util.Log;
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

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.gmail.krbashianrafael.medpunkt.data.MedContract;
import com.gmail.krbashianrafael.medpunkt.data.MedContract.DiseasesEntry;
import com.gmail.krbashianrafael.medpunkt.data.MedContract.TreatmentPhotosEntry;
import com.gmail.krbashianrafael.medpunkt.data.MedContract.UsersEntry;
import com.gmail.krbashianrafael.medpunkt.phone.DatePickerFragment;
import com.gmail.krbashianrafael.medpunkt.phone.DiseasesActivity;
import com.gmail.krbashianrafael.medpunkt.phone.UsersActivity;
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


public class UserActivity extends AppCompatActivity
        implements ActivityCompat.OnRequestPermissionsResultCallback,
        LoaderManager.LoaderCallbacks<Cursor>,
        DatePickerDialog.OnDateSetListener {

    private static final String PREFS_NAME = "PREFS";

    /**
     * Лоадеров может много (они обрабатываются в case)
     * поэтому устанавливаем инициализатор для каждого лоадера
     * private static final int USER_TR_PHOTOS_LOADER = 202
     */
    // Лоадер для загрузки путей снимков связанных с лечением юзера
    private static final int USER_TR_PHOTOS_LOADER = 202;

    // загруженный Bitmap фотографии
    private Bitmap loadedBitmap;

    private String pathToUsersPhoto;

    // новый ли пользователь, возможность изменфть пользователя,
    // показывать стрелку обратно, был ли изменен пользователь,
    // в процессе сохранения или нет
    private boolean newUser, goBack, editUser, userHasChangedPhoto, onSavingOrUpdatingOrDeleting, onLoading;

    private ActionBar actionBar;

    // имя и дата рождени полей UserActivity
    private String textUserName, textUserBirthDate;

    // поля имени, ДР и focusHolder
    private TextInputLayout textInputLayoutName, textInputLayoutDate;
    private TextInputEditText editTextDate, editTextName;
    private EditText focusHolder;

    // фото пользоватлея
    private ImageView imagePhoto;

    //для планшета:

    // TextView для указания UserTitle
    private TextView txtTabletUserTitle;

    private FrameLayout tabletFrmBack;
    private FrameLayout tabletFrmSave;
    private FrameLayout tabletFrmDelete;

    // если нет пользоватлея будет рамка с текстом, что нет фото и можно загрузить
    private TextView textViewNoUserPhoto;

    // TextView удаления фото, при нажатии удаляется фото
    private TextView textDeleteUserPhoto;

    // путь к сохраненному фото
    private String userPhotoUri, userSetNoPhotoUri = "";

    // id пользователя
    private long _idUser = 0;

    // View mLayout для привязки snackbar
    private View mLayout;

    // fabEditTreatmentDescripton
    private FloatingActionButton fab;

    // Animation fabHideAnimation
    private Animation fabHideAnimation;

    // Animation fabShowAnimation
    private Animation fabShowAnimation;

    // код разрешения на запись и чтение из экстернал
    private static final int PERMISSION_WRITE_EXTERNAL_STORAGE = 0;

    // код загрузки фото из галерии
    private static final int RESULT_LOAD_IMAGE = 9002;

    // путь к загружаемому фото
    private Uri imageUriInView;


    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (HomeActivity.isTablet) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

        setContentView(R.layout.activity_user);

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

        // если это телефон устанавливаме actionBar для телефона
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
            // если это Плншет прячем actionBar
            // и инициализируем вьюшки, которые относятся к планшету

            // эти вюшки обязатльно должны присутствовать в телефонном виде,
            // иначе findViewById вернет null
            if (actionBar != null) {
                actionBar.hide();
            }

            txtTabletUserTitle = findViewById(R.id.txt_tablet_user_title);

            tabletFrmBack = findViewById(R.id.tablet_frm_back);
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
                    // привязываем PopupMenu к tabletFrmDelete
                    PopupMenu popup = new PopupMenu(UserActivity.this, tabletFrmDelete);

                    // создаем PopupMenu
                    popup.getMenuInflater().inflate(R.menu.menu, popup.getMenu());

                    // создаем строку для зацепки к символу "удалить"
                    String deletString = HomeActivity.iAmDoctor ? getResources().getString(R.string.patient_delete) : getResources().getString(R.string.user_delete);

                    // убираем не нужные Item
                    popup.getMenu().removeItem(R.id.action_delete);
                    popup.getMenu().removeItem(R.id.action_save);

                    // добавление в меню текста с картинкой символа "удалить"
                    popup.getMenu().add(0, R.id.action_delete, 3, menuIconWithText(getResources().getDrawable(R.drawable.ic_delete_red_24dp),
                            deletString));

                    //registering popup with OnMenuItemClickListener
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        public boolean onMenuItemClick(MenuItem item) {
                            onDeleteClick();
                            return true;
                        }
                    });

                    //showing popup menu
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

        //  /data/data/com.gmail.krbashianrafael.medpunkt/files/users_photos/
        if (getFilesDir() != null) {
            pathToUsersPhoto = getFilesDir().toString() + getString(R.string.path_to_users_photos);
        }

        // если клавиатура перекрывает поле ввода, то поле ввода приподнимается
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN | WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        // привязка для snackbar
        mLayout = findViewById(R.id.user_layout);

        textViewNoUserPhoto = findViewById(R.id.no_user_photo);

        imagePhoto = findViewById(R.id.image_photo);

        if (!userPhotoUri.equals("No_Photo")) {
            // если есть файл фото для загрузки, то грузим
            textViewNoUserPhoto.setVisibility(View.GONE);

            File imgFile = new File(userPhotoUri);

            if (imgFile.exists()) {
                GlideApp.with(this)
                        .load(userPhotoUri)
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .skipMemoryCache(true)
                        .error(R.drawable.error_camera_alt_gray_128dp)
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

                // скручиваем клавиатуру
                hideSoftInput();

                // перед загрузкой фото получаем разреншение на чтение (и запись) из экстернал
                if (ActivityCompat.checkSelfPermission(UserActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    // Запрашиваем разрешение на чтение и запись фото
                    MyReadWritePermissionHandler.getReadWritePermission(UserActivity.this, mLayout, PERMISSION_WRITE_EXTERNAL_STORAGE);
                } else {
                    Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(galleryIntent, RESULT_LOAD_IMAGE);
                }
            }
        });

        // при нажатии на textDeleteUserPhoto убирается фото из рамки
        // и далее, при сохранении (если ранее было фото) удаляется файл фото
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

                // если до удаления фото не было, то изменений нет
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
                    // скручиваем клавиатуру
                    hideSoftInput();

                    textInputLayoutDate.setError(null);

                    // передаем фокус, чтоб поля имени и ДР не были в фокусе
                    focusHolder.requestFocus();

                    // выбираем дату ДР

                    // в версии Build.VERSION_CODES.N нет календаря с прокруткой
                    // поэтому для вывода календаря с прокруткой пользуемся стронней библиетекой
                    // слушатель прописываем в нашем же классе .callback(UserActivity.this)
                    // com.tsongkha.spinnerdatepicker.SpinnerDatePickerDialogBuilder
                    // используем эту библиотеку для
                    // Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        String dateInEditTextDate = editTextDate.getText().toString().trim();

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

                        new SpinnerDatePickerDialogBuilder()
                                .context(UserActivity.this)
                                .callback(UserActivity.this)
                                .spinnerTheme(R.style.NumberPickerStyle)
                                .defaultDate(mYear, mMonth, mDay)
                                .build().show();
                    } else {
                        // в остальных случаях пользуемся классом DatePickerFragment
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

        fabShowAnimation = AnimationUtils.loadAnimation(this, R.anim.fab_show);
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

                // устанавливаем курсор в конец строки (cursor)
                editTextName.setSelection(editTextName.getText().length());
                editTextDate.setEnabled(true);
                imagePhoto.setClickable(true);

                editUser = false;

                if (userPhotoUri.equals("No_Photo")) {
                    textDeleteUserPhoto.setVisibility(View.INVISIBLE);
                } else {
                    textDeleteUserPhoto.setVisibility(View.VISIBLE);
                }

                // если это телефон (не Планшет), то обновляем меню
                if (!HomeActivity.isTablet) {
                    invalidateOptionsMenu();
                } else {
                    // если это планшет, то показываем "сохранить" и убираем "удалить"
                    tabletFrmSave.setVisibility(View.VISIBLE);
                    tabletFrmDelete.setVisibility(View.GONE);
                }
            }
        });

        // если окно отрылось как просмотр профиля,
        // то редактирование запрещено
        // если редактировать можно, но нет фото, то и удалять не нужно
        if (editUser) {
            if (HomeActivity.isTablet) {
                // если это планшет, то показываем "сохранить" и убираем "удалить"
                tabletFrmSave.setVisibility(View.GONE);
                tabletFrmDelete.setVisibility(View.VISIBLE);
            }

            editTextName.setEnabled(false);
            editTextDate.setEnabled(false);
            imagePhoto.setClickable(false);
            textDeleteUserPhoto.setVisibility(View.INVISIBLE);
            fab.startAnimation(fabShowAnimation);
        } else if (userPhotoUri.equals("No_Photo")) {
            textDeleteUserPhoto.setVisibility(View.INVISIBLE);
        }
    }

    // слушатель по установке даты для Build.VERSION_CODES.LOLIPOP
    @SuppressLint("SetTextI18n")
    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
        GregorianCalendar date = new GregorianCalendar(year, monthOfYear, dayOfMonth);
        editTextDate.setText(simpleDateFormat.format(date.getTime()) + " ");
    }

    // результат запроса на загрузку фото
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

    // здесь грузим фотку в imagePhoto
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && data != null) {
            Uri newSelectedImageUri = data.getData();

            if (newSelectedImageUri == null) {
                Toast.makeText(UserActivity.this, R.string.user_cant_load_photo, Toast.LENGTH_LONG).show();
                onLoading = false;
            } else {
                // если грузим фотку в первый раз
                // или если грузим другую фотку вместо уже загруженной (ту же фотку повторно не грузим)
                if (imageUriInView == null || !imageUriInView.equals(newSelectedImageUri)) {
                    loadPhotoIntoViewAndGetBitmap(newSelectedImageUri);
                } else {
                    // если грузим ту же фотку, которую только что грузили
                    onLoading = false;
                }
            }
        } else {
            // если вернулись не сделав выбор
            onLoading = false;
        }
    }

    private void loadPhotoIntoViewAndGetBitmap(Uri newSelectedImageUri) {
        GlideApp.with(this)
                .load(newSelectedImageUri)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .error(R.drawable.error_camera_alt_gray_128dp)
                .transition(DrawableTransitionOptions.withCrossFade(500))
                .into(imagePhoto);

        if (pathToUsersPhoto != null && _idUser != 0) {
            userPhotoUri = pathToUsersPhoto + _idUser + getString(R.string.user_photo_nameEnd);
        }

        imageUriInView = newSelectedImageUri;
        userHasChangedPhoto = true;
        textDeleteUserPhoto.setVisibility(View.VISIBLE);
        textViewNoUserPhoto.setVisibility(View.GONE);

        loadedBitmap = null;

        GlideApp.with(this)
                .asBitmap()
                .load(imageUriInView)
                .into(new SimpleTarget<Bitmap>(imagePhoto.getWidth(), imagePhoto.getHeight()) {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        // здесь получаем Bitmap
                        loadedBitmap = resource;
                    }
                });

        onLoading = false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        loadedBitmap = null;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Планшет идет без меню
        if (HomeActivity.isTablet) {
            return false;
        }

        getMenuInflater().inflate(R.menu.menu, menu);

        String deletString = HomeActivity.iAmDoctor ? getResources().getString(R.string.patient_delete) : getResources().getString(R.string.user_delete);

        menu.removeItem(R.id.action_delete);
        // добавление в меню текста с картинкой
        menu.add(0, R.id.action_delete, 3, menuIconWithText(getResources().getDrawable(R.drawable.ic_delete_red_24dp),
                deletString));

        return true;
    }

    // SpannableString с картикной для элеменов меню
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

        // если в состоянии editUser (тоесть есть кнопка fabEditTreatmentDescripton со значком редактирования)
        // то в меню элемент "сохранить" делаем не видимым
        // видимым остается "удалить"
        if (editUser) {
            MenuItem menuItemSave = menu.getItem(1);
            menuItemSave.setVisible(false);
        } else {
            // иначе, делаем невидимым "удалить"
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

    // если было нажато "домой" (обратно)
    private boolean onHomeClick() {
        // Если не было изменений
        if (userHasNotChanged()) {
            goToUsersActivity();
            return true;
        }

        textInputLayoutName.setError(null);
        textInputLayoutDate.setError(null);

        // Если были изменения
        // если выходим без сохранения изменений
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

        // если выходим с сохранением изменений
        showUnsavedChangesDialog(discardButtonClickListener);

        return true;
    }

    // если было нажато "сохранить"
    private boolean onSaveClick() {
        // флаг, чтоб повторный клик не работал,
        // пока идет сохранения
        if (onSavingOrUpdatingOrDeleting) {
            return true;
        }

        onSavingOrUpdatingOrDeleting = true;

        // если ничего не менялось
        if (userHasNotChanged() && !newUser) {

            // то сразу выходим
            goToUsersActivity();
            onSavingOrUpdatingOrDeleting = false;
            return true;

            // далее если это телефон
            /*hideSoftInput();

            focusHolder.requestFocus();

            editUser = true;
            editTextName.setEnabled(false);
            editTextDate.setEnabled(false);
            imagePhoto.setClickable(false);
            textDeleteUserPhoto.setVisibility(View.INVISIBLE);

            invalidateOptionsMenu();

            fab.startAnimation(fabShowAnimation);

            onSavingOrUpdatingOrDeleting = false;*/

        } else {
            // если что-то менялось
            saveOrUpdateUser();
        }

        return true;
    }

    // если было нажато "удалить"
    private boolean onDeleteClick() {
        // флаг, чтоб клик не работал,
        // пока идет сохранения
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
                        //hideSoftInput();
                        if (dialog != null) {
                            dialog.dismiss();
                        }

                        goToUsersActivity();
                    }
                };

        showUnsavedChangesDialog(discardButtonClickListener);
    }

    // Диалог "Удалить пользователя или отменить удаление"
    private void showDeleteConfirmationDialog() {

        String deletString = HomeActivity.iAmDoctor ? getResources().getString(R.string.patient_delete) : getResources().getString(R.string.user_delete);

        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AlertDialogCustom);
        builder.setMessage(deletString + " " + editTextName.getText() + "?");
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (onSavingOrUpdatingOrDeleting) {
                    if (dialog != null) {
                        dialog.dismiss();
                    }
                } else {
                    onSavingOrUpdatingOrDeleting = true;
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
        alertDialog.show();
    }

    // Диалог "сохранить или выйти без сохранения"
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
        alertDialog.show();
    }

    private void saveOrUpdateUser() {
        // устанавливаем анимацию на случай Error
        ScaleAnimation scaleAnimation = new ScaleAnimation(0f, 1f, 0f, 0f);
        scaleAnimation.setDuration(500);

        // првоерка имени и ДР
        String nameToCheck = editTextName.getText().toString().trim();
        String birthDateToCheck = editTextDate.getText().toString();
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

        // если поля имени и др были не верными - выходим
        if (wrongField) {
            hideSoftInput();
            onSavingOrUpdatingOrDeleting = false;
            return;
        }

        // проверка окончена, начинаем сохранение

        // скручиваем клавиатуру
        hideSoftInput();

        focusHolder.requestFocus();

        textUserName = nameToCheck;
        textUserBirthDate = birthDateToCheck;

        if (!HomeActivity.isTablet) {
            actionBar.setTitle(textUserName);
        } else {
            txtTabletUserTitle.setText(textUserName);
        }

        // когда сохраняем НОВОГО пользователя в базу, вместо пути к фото пишем "No_Photo" на случай,
        // если фото не будет установленно
        // при сохранении пользователя в базу получаем его _id
        // далее, если фото будет выбрано, то дописываем (обновляем) путь к фото в базу в папку под номером _id пользователя

        // если фото было удалено нажатием на "удалить фото", то удалить фото (если оно есть)
        if (userSetNoPhotoUri.equals("Set_No_Photo") && pathToUsersPhoto != null) {

            // формируем путь к папке фото юзера
            File fileToDelete = new File(pathToUsersPhoto + _idUser + getString(R.string.user_photo_nameEnd));

            if (fileToDelete.exists()) {
                if (!fileToDelete.delete()) {
                    // если файл не удалился
                    Toast.makeText(this, R.string.user_photo_not_deleted, Toast.LENGTH_LONG).show();

                    // получаем SharedPreferences, чтоб писать путь к неудаленному файлу в "PREFS"
                    SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
                    final SharedPreferences.Editor prefsEditor = prefs.edit();

                    // ытягиваем в String notDeletedFilesPathes из prefs пути к ранее не удаленным файлам
                    String notDeletedFilesPathes = prefs.getString("notDeletedFilesPathes", null);
                    // дописываем путь (за запятой) к неудаленному файлу фото польлзователя
                    String updatedNotDeletedFilesPathes = notDeletedFilesPathes + "," + userPhotoUri;

                    // пишем заново в в "PREFS" обновленную строку
                    prefsEditor.putString("notDeletedFilesPathes", updatedNotDeletedFilesPathes);
                    prefsEditor.apply();
                }
            }

            userPhotoUri = "No_Photo";
            userSetNoPhotoUri = "";
        }

        // если новый пользователь, то сохраняем в базу и идем в DiseasesActivity
        if (newUser) {
            saveUserToDataBase();
        }
        // если НЕ новый пользователь, то обновляем в базу и
        else {
            updateUserToDataBase();
        }
    }

    private void afterSaveUser() {
        if (!HomeActivity.isTablet) {
            // если это телефон
            if (goBack) {
                goToUsersActivity();
            } else {
                goToDiseasesActivity();
            }
        } else {
            // если это планшет, то идем в UsersActivity
            goToUsersActivity();

                /*editUser = true;
                newUser = false;
                userHasChangedPhoto = false;

                tabletFrmDelete.setVisibility(View.VISIBLE);
                tabletFrmSave.setVisibility(View.GONE);

                fab.startAnimation(fabShowAnimation);
                editTextName.setEnabled(false);
                editTextDate.setEnabled(false);
                imagePhoto.setClickable(false);
                textDeleteUserPhoto.setVisibility(View.INVISIBLE);

                onSavingOrUpdatingOrDeleting = false;*/
        }
    }

    private void afterUpdateUser() {
        // идем в UsersActivity
        goToUsersActivity();

        // если была нажата стрелка "обратно" - идем обратно
        /*if (goBack) {
            goToUsersActivity();
        } else {
            editUser = true;
            userHasChangedPhoto = false;

            // если это телефон (а не Планшет), то обновляем меню
            if (!HomeActivity.isTablet) {
                invalidateOptionsMenu();
            } else {
                // если это Планшет, то делаем видимым "удалить" и невидимым "сохранить"
                tabletFrmDelete.setVisibility(View.VISIBLE);
                tabletFrmSave.setVisibility(View.GONE);
            }

            fab.startAnimation(fabShowAnimation);
            editTextName.setEnabled(false);
            editTextDate.setEnabled(false);
            imagePhoto.setClickable(false);
            textDeleteUserPhoto.setVisibility(View.INVISIBLE);

            onSavingOrUpdatingOrDeleting = false;
        }*/
    }

    // проверка на изменения пользователя
    private boolean userHasNotChanged() {
        return !userHasChangedPhoto &&
                editTextName.getText().toString().equals(textUserName) &&
                editTextDate.getText().toString().equals(textUserBirthDate);

    }

    private void goToDiseasesActivity() {
        Intent toDiseasesIntent = new Intent(this, DiseasesActivity.class);
        toDiseasesIntent.putExtra("newUser", true);
        toDiseasesIntent.putExtra("_idUser", _idUser);
        toDiseasesIntent.putExtra("UserName", textUserName);
        startActivity(toDiseasesIntent);

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
        // при вставке нового пользователя в Базу его userPhotoUri = "No_Photo";
        values.put(UsersEntry.COLUMN_USER_PHOTO_PATH, userPhotoUri);

        // при сохранении пользователя в Базу делаем сначала insert и получаем Uri вставленной строки
        Uri newUri = getContentResolver().insert(UsersEntry.CONTENT_USERS_URI, values);

        // если первичное сохранение нового пользователя в Базу было успешным
        if (newUri != null) {

            // получаем и присваиваем _idUser из возвращенного newUri
            _idUser = ContentUris.parseId(newUri);

            // здесь устанавливаем флаг mScrollToStart в классе UsersActivity в true
            // чтоб после вставки новой строки в Базу и посел оповещения об изменениях
            // заново загрузился курсор и RecyclerView прокрутился вниз до последней позиции

            UsersActivity.mScrollToStart = true;


            // если есть что сохранять в файл фото пользователя loadedBitmap != null
            if (pathToUsersPhoto != null && loadedBitmap != null) {
                // пирсваиваем userPhotoUri путь к файлу фото пользователя для сохранения
                // userPhotoUri = /data/data/com.gmail.krbashianrafael.medpunkt/files/users_photos/1-usrImage.jpg
                userPhotoUri = pathToUsersPhoto + _idUser + getString(R.string.user_photo_nameEnd);

                // обновляем данные по пути к файлу фото пользовател в базе
                int rowsAffected = insertUserPhotoUriToDataBase(userPhotoUri);

                // если обновление пути к файлу в базе НЕ произошло
                if (rowsAffected == 0) {
                    userPhotoUri = "No_Photo";
                    onSavingOrUpdatingOrDeleting = false;
                    Toast.makeText(UserActivity.this, R.string.user_cant_save_photo, Toast.LENGTH_LONG).show();

                    afterSaveUser();

                } else {
                    // если userPhotoUri обновилось,
                    // сохраняем файл фото (если фото было загружено loadedBitmap != null)
                    // и идем по нормальному сценарию выхода после сохранения нового пользователя

                    // сохранение файла фото происходит асинхронно
                    // в UserPhotoSavingAsyncTask
                    // в конструктор передаются actyvity, loadedBitmap, и путь к файлу фото
                    if (loadedBitmap != null) {
                        new UserPhotoSavingAsyncTask(this, loadedBitmap, userPhotoUri).execute();
                    } else {
                        // если после указания в базе пути к фото
                        // по каким-то причинам loadedBitmap == null
                        // то оставляем в базе путь, т.к. он все равно ничего не вытянет
                        // по причине отсутствия файла по этому пути
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

    // для добавления userPhotoUri нового пользователя после получения _idUser делаем update
    private int insertUserPhotoUriToDataBase(String userPhotoUri) {

        ContentValues values = new ContentValues();

        values.put(UsersEntry.COLUMN_USER_PHOTO_PATH, userPhotoUri);

        Uri mCurrentUserUri = Uri.withAppendedPath(UsersEntry.CONTENT_USERS_URI, String.valueOf(_idUser));

        // делаем update
        return getContentResolver().update(mCurrentUserUri, values, null, null);
    }

    private void updateUserToDataBase() {

        if (pathToUsersPhoto != null && loadedBitmap != null) {
            userPhotoUri = pathToUsersPhoto + _idUser + getString(R.string.user_photo_nameEnd);
        }

        ContentValues values = new ContentValues();
        values.put(MedContract.UsersEntry.COLUMN_USER_NAME, textUserName);
        values.put(MedContract.UsersEntry.COLUMN_USER_DATE, textUserBirthDate);
        values.put(MedContract.UsersEntry.COLUMN_USER_PHOTO_PATH, userPhotoUri);

        // Uri к юзеру, который будет обновляться
        Uri mCurrentUserUri = Uri.withAppendedPath(UsersEntry.CONTENT_USERS_URI, String.valueOf(_idUser));

        // делаем update в Базе
        int rowsAffected = getContentResolver().update(mCurrentUserUri, values, null, null);

        // если update в Базе был НЕ успешным
        if (rowsAffected == 0) {
            Toast.makeText(UserActivity.this, HomeActivity.iAmDoctor ? R.string.patient_cant_update : R.string.user_cant_update, Toast.LENGTH_LONG).show();

            // если обновление было неудачным, то остаемся на месте
            goBack = false;
            afterUpdateUser();

            // update в Базе был успешным
        } else {
            // если была загрузенна новое фото loadedBitmap != null,
            // то в отдельном потоке сохраняем фото в файл в папку юзера
            if (loadedBitmap != null) {
                new UserPhotoSavingAsyncTask(this, loadedBitmap, userPhotoUri).execute();
            } else {
                // если новое фото не было загружено,
                // то обновление в базе уже было и больше ничего делать не надо

                afterUpdateUser();
            }
        }
    }

    private void deleteUserAndPhotos() {
        // Инициализируем Loader для загрузки строк из таблицы treatmentPhotos,
        // которые будут удаляться вместе с удалением юзера из таблицы users
        // кроме того, после удаления строк из таблиц users, treatmentPhotos и diseases будут удаляться соответствующие фото
        getLoaderManager().initLoader(USER_TR_PHOTOS_LOADER, null, this);

    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        // для Loader в projection обязательно нужно указывать поле с _ID
        // здесь мы указываем поля таблицы treatmentPhotos , которые будем брать из Cursor для дальнейшей обработки
        String[] projection = {
                TreatmentPhotosEntry.TR_PHOTO_ID,
                TreatmentPhotosEntry.COLUMN_TR_PHOTO_PATH};

        // выборку фото делаем по _idUser, который будет удаляться
        String selection = TreatmentPhotosEntry.COLUMN_U_ID + "=?";
        String[] selectionArgs = new String[]{String.valueOf(_idUser)};

        // This loader will execute the ContentProvider's query method on a background thread
        // Loader грузит ВСЕ данные из таблицы users через Provider
        return new CursorLoader(this,   // Parent activity context
                TreatmentPhotosEntry.CONTENT_TREATMENT_PHOTOS_URI,   // Provider content URI to query = content://com.gmail.krbashianrafael.medpunkt/treatmentPhotos/
                projection,
                selection,
                selectionArgs,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        // ArrayList для путей к файлам фото, которые нужно будет удалить
        ArrayList<String> photoFilePathesToBeDeletedList = new ArrayList<>();

        // если у пользователя есть фото, то
        // путь к этому фото доабавляем в photoFilePathesToBeDeletedList
        if (!TextUtils.equals(userPhotoUri, "No_Photo")) {
            photoFilePathesToBeDeletedList.add(userPhotoUri);
        }

        if (cursor != null) {
            // устанавливаем курсор на исходную (на случай, если курсор используем повторно после прохождения цикла
            cursor.moveToPosition(-1);

            // проходим в цикле курсор
            // и добаляем пути к удаляемым файлам в ArrayList<String> photoFilePathesToBeDeletedList
            while (cursor.moveToNext()) {
                int trPhoto_pathColumnIndex = cursor.getColumnIndex(TreatmentPhotosEntry.COLUMN_TR_PHOTO_PATH);
                String trPhotoUri = cursor.getString(trPhoto_pathColumnIndex);

                photoFilePathesToBeDeletedList.add(trPhotoUri);
            }
        }

        // делаем destroyLoader, чтоб он сам повторно не вызывался
        getLoaderManager().destroyLoader(USER_TR_PHOTOS_LOADER);

        // Запускаем AsyncTask для удаления строк из таблиц users, treatmentPhotos и diseases
        // а далее, и для удаления файлов
        new UserActivity.UserAndTreatmentPhotosDeletingAsyncTask(this, photoFilePathesToBeDeletedList).execute(getApplicationContext());
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        //
    }

    // класс DiseaseAndTreatmentPhotosDeletingAsyncTask делаем статическим,
    // чтоб не было утечки памяти при его работе
    private static class UserAndTreatmentPhotosDeletingAsyncTask extends AsyncTask<Context, Void, Integer> {

        private static final String PREFS_NAME = "PREFS";

        private final WeakReference<UserActivity> userActivityReference;
        private final ArrayList<String> mPhotoFilePathesListToBeDeleted;
        private int mRowsFromUsersAndTreatmentPhotosDeleted = -1;

        // в конструкторе получаем WeakReference<UserActivity>
        // и образовываем список ArrayList<String> mPhotoFilePathesListToBeDeleted на основании полученного photoFilePathesListToBeDeleted
        // это список путей к файлам, которые необходимо будет удалить
        // тоесть наш mPhotoFilePathesListToBeDeleted НЕ зависим от полученного photoFilePathesListToBeDeleted
        UserAndTreatmentPhotosDeletingAsyncTask(UserActivity context, ArrayList<String> photoFilePathesListToBeDeleted) {
            userActivityReference = new WeakReference<>(context);
            mPhotoFilePathesListToBeDeleted = new ArrayList<>(photoFilePathesListToBeDeleted);
        }

        // в onPreExecute получаем  UserActivity userActivity
        // и если он null, то никакое удаление не происходит
        // если же userActivity не null,
        // то в основном треде удаляем строки из таблиц users, treatmentPhotos и diseases в одной транзакции
        // при этом, получаем (как резульат удаления строк из таблиц users и treatmentPhotos) количество удаленных строк
        // по сути, это количество должно совпадать с количеством элементов в mPhotoFilePathesListToBeDeleted
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            UserActivity userActivity = userActivityReference.get();
            if (userActivity == null) {
                return;
            }

            mRowsFromUsersAndTreatmentPhotosDeleted = deleteUserAndDiseaseAndTreatmentPhotosFromDataBase(userActivity);
        }

        // метод удаления строк из таблиц treatmentPhotos и diseases в одной транзакции
        // возвращает количество удаленных строк из таблицы treatmentPhotos или -1
        private int deleteUserAndDiseaseAndTreatmentPhotosFromDataBase(UserActivity userActivity) {
            // ArrayList для операций по удалению строк из таблиц users, treatmentPhotos и diseases
            // в одной транзакции
            ArrayList<ContentProviderOperation> deletingFromDbOperations = new ArrayList<>();

            // пишем операцию удаления строк снимков пользователя ИЗ ТАБЛИЦЫ treatmentPhotos
            String selectionTrPhotos = TreatmentPhotosEntry.COLUMN_U_ID + "=?";
            String[] selectionArgsTrPhotos = new String[]{String.valueOf(userActivity._idUser)};

            ContentProviderOperation deleteTreatmentPhotosFromDbOperation = ContentProviderOperation
                    .newDelete(TreatmentPhotosEntry.CONTENT_TREATMENT_PHOTOS_URI)
                    .withSelection(selectionTrPhotos, selectionArgsTrPhotos)
                    .build();

            // добавляем операцию удаления строк ИЗ ТАБЛИЦЫ treatmentPhotos в список операций deletingFromDbOperations
            deletingFromDbOperations.add(deleteTreatmentPhotosFromDbOperation);

            // пишем операцию удаления строк заболеваний пользователя ИЗ ТАБЛИЦЫ diseases
            String selectionDiseases = DiseasesEntry.COLUMN_U_ID + "=?";
            String[] selectionArgsDiseases = new String[]{String.valueOf(userActivity._idUser)};

            ContentProviderOperation deleteDiseaseFromDbOperation = ContentProviderOperation
                    .newDelete(DiseasesEntry.CONTENT_DISEASES_URI)
                    .withSelection(selectionDiseases, selectionArgsDiseases)
                    .build();

            // добавляем операцию удаления строки заболевания ИЗ ТАБЛИЦЫ diseases в список операций deletingFromDbOperations
            deletingFromDbOperations.add(deleteDiseaseFromDbOperation);

            // пишем операцию удаления строки пользователя ИЗ ТАБЛИЦЫ users
            String selectionUser = UsersEntry.U_ID + "=?";
            String[] selectionArgsUser = new String[]{String.valueOf(userActivity._idUser)};

            ContentProviderOperation deleteUserFromDbOperation = ContentProviderOperation
                    .newDelete(UsersEntry.CONTENT_USERS_URI)
                    .withSelection(selectionUser, selectionArgsUser)
                    .build();

            // добавляем операцию удаления строки заболевания ИЗ ТАБЛИЦЫ diseases в список операций deletingFromDbOperations
            deletingFromDbOperations.add(deleteUserFromDbOperation);


            // переменная количества удаленных строк из таблицы treatmentPhotos
            int rowsFromUsersAndTreatmentPhotosDeleted = -1;

            try {
                // запускаем транзакцию удаления строк из таблиц treatmentPhotos и diseases
                // и получаем результат
                ContentProviderResult[] results = userActivity.getContentResolver().applyBatch(MedContract.CONTENT_AUTHORITY, deletingFromDbOperations);

                // если транзакция прошла успешно
                // results[0] - результат запроса из TreatmentPhotos
                // results[2] - результат запроса из Users
                if (results.length == 3 && results[0] != null && results[2] != null) {
                    // записываем в rowsFromUsersAndTreatmentPhotosDeleted
                    // сумму удаленных строк из аблицы treatmentPhotos и users
                    rowsFromUsersAndTreatmentPhotosDeleted = results[0].count + results[2].count;
                } else {
                    return rowsFromUsersAndTreatmentPhotosDeleted;
                }
            } catch (RemoteException | OperationApplicationException e) {
                e.printStackTrace();
                // если транзакция НЕ прошла успешно, то возвращаем -1
                return rowsFromUsersAndTreatmentPhotosDeleted;
            }

            // возвращаем количество удаленных строк из аблицы treatmentPhotos
            return rowsFromUsersAndTreatmentPhotosDeleted;
        }

        // в doInBackground осуществляем удаление файлов фотографий
        // по списку путей к фотографиям из mPhotoFilePathesListToBeDeleted
        @Override
        protected Integer doInBackground(Context... contexts) {
            if (mRowsFromUsersAndTreatmentPhotosDeleted == -1) {
                // если были ошибки во время удаления строк из таблиц users, treatmentPhotos и diseases
                // возвращаем -1
                // и выводим сообщение, что удалить пользователя не удалилось и оставляем все как есть (не удаляем файлы)
                return -1;
            } else if (mRowsFromUsersAndTreatmentPhotosDeleted == 0) {
                // если у пользователя не было фотографий и его по его лечению не было снимков,
                // то ограничиваемся удалением пользователя из таблиц users и diseases,
                // без дальнейшего удаления каких либо файлов фото
                return 1;
            } else {
                // если у пользователя были фотографи или были снимки по его лечению,
                // mRowsFromUsersAndTreatmentPhotosDeleted > 0,
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

            final UserActivity userActivity = userActivityReference.get();

            if (userActivity == null) {
                return;
            }

            if (result == -1) {
                // если заболевание не удалилось из базы и фото не были удалены
                userActivity.onSavingOrUpdatingOrDeleting = false;
                Toast.makeText(userActivity, HomeActivity.iAmDoctor ? R.string.patient_not_deleted : R.string.user_not_deleted, Toast.LENGTH_LONG).show();
            } else if (result == 0) {
                // если не было фото пользователя и снимков для удаления
                userActivity.goToDiseasesActivity();
            } else {
                // result == 1
                // заболевание удалилось и снимки удалены (или отсутствуют)
                userActivity.goToUsersActivity();
            }
        }
    }

    // класс UserPhotoSavingAsyncTask делаем статическим,
    // чтоб не было утечки памяти при его работе
    private static class UserPhotoSavingAsyncTask extends AsyncTask<Void, Void, File> {

        // получаем WeakReference на объекты,
        // чтобы GC мог их собрать
        private WeakReference<UserActivity> userActivityReference;
        private WeakReference<Bitmap> loadedBitmapReference;
        private WeakReference<String> fileToSavePathReference;

        UserPhotoSavingAsyncTask(UserActivity context, Bitmap loadedBitmap, String fileToSavePath) {
            userActivityReference = new WeakReference<>(context);
            loadedBitmapReference = new WeakReference<>(loadedBitmap);
            fileToSavePathReference = new WeakReference<>(fileToSavePath);
        }

        // в Background делаем сохранение Bitmap в File
        @Override
        protected File doInBackground(Void... params) {
            String fileToSavePath = fileToSavePathReference.get();

            if (fileToSavePath == null) {
                return null;
            }

            File fileToSave = new File(fileToSavePath);

            // перед сохранение удаляем существующий файл,
            // т.к. фото должно быть одно
            if (fileToSave.exists() && !fileToSave.delete()) {
                return null;
            }

            // если папка для файла существует
            if (fileToSave.getParentFile().exists()) {
                try {
                    // создаем файл
                    if (!fileToSave.createNewFile()) {
                        return null;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
            } else {
                // если папка для файла НЕ существует создаем сначала папку, потом файл
                if (fileToSave.getParentFile().mkdir()) {
                    try {
                        // создаем файл
                        if (!fileToSave.createNewFile()) {
                            return null;
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        return null;
                    }
                } else {
                    // если папки не было и она не создалась
                    if (!fileToSave.getParentFile().exists()) {
                        return null;
                    }
                }
            }

            // В созданный файл пишем loadedBitmap
            Bitmap loadedBitmap;
            // The try-with-resources Statement JDK 8
            try (FileOutputStream outputStream = new FileOutputStream(fileToSave)) {
                // получаем из WeakReference<Bitmap> loadedBitmap
                // и проверяем его на null, т.к. GC мог его собрать
                // если таокое произошло, то файл не сохраняяем

                loadedBitmap = loadedBitmapReference.get();
                if (loadedBitmap != null) {
                    loadedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                    outputStream.flush();
                    outputStream.close();
                } else {
                    outputStream.close();
                    return null;
                }

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

            // обнуляем loadedBitmap
            userActivity.loadedBitmap = null;

            if (savedFile == null) {
                Toast.makeText(userActivity, R.string.user_cant_save_photo, Toast.LENGTH_LONG).show();
                userActivity.goBack = false;
                userActivity.afterUpdateUser();
                return;
            }

            // если сохранение или обновление файла фото было удачным
            if (savedFile.exists()) {

                userActivity.userPhotoUri = savedFile.toString();

                if (userActivity.newUser) {
                    userActivity.afterSaveUser();
                } else {
                    userActivity.afterUpdateUser();
                }
                // если сохранение или обновление файла фото было НЕ удачным
            } else {
                userActivity.userPhotoUri = "No_Photo";
                userActivity.goBack = false;
                userActivity.afterUpdateUser();
                Toast.makeText(userActivity, R.string.user_cant_save_photo, Toast.LENGTH_LONG).show();
            }
        }
    }
}


