package com.gmail.krbashianrafael.medpunkt;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.gmail.krbashianrafael.medpunkt.data.MedContract;
import com.gmail.krbashianrafael.medpunkt.data.MedContract.UsersEntry;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;


public class UserActivity extends AppCompatActivity
        implements ActivityCompat.OnRequestPermissionsResultCallback {

    private final Handler myHandler = new Handler(Looper.getMainLooper());

    // загруженный Bitmap фотографии
    private Bitmap loadedBitmap;

    private String pathToUsersPhoto;

    // новый ли пользователь, возможность изменфть пользователя,
    // показывать стрелку обратно, был ли изменен пользователь,
    // в процессе сохранения или нет
    private boolean newUser, goBack, editUser, userHasChangedPhoto, onSavingOrUpdating;

    private ActionBar actionBar;

    // имя и дата рождени полей UserActivity
    private String textUserName, textUserBirthDate;

    // поля имени, ДР и focusHolder
    private TextInputLayout textInputLayoutName, textInputLayoutDate;
    private TextInputEditText editTextDate, editTextName;
    private EditText focusHolder;

    // фото пользоватлея
    private ImageView imagePhoto;

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

        //  /data/data/com.gmail.krbashianrafael.medpunkt/files/users_photos/
        if (getFilesDir() != null) {
            pathToUsersPhoto = getFilesDir().toString() + "/users_photos/";
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
        textDeleteUserPhoto = findViewById(R.id.text_delete_photo);
        textDeleteUserPhoto.setText(menuIconWithText(getResources().getDrawable(R.drawable.ic_delete_red_24dp), getResources().getString(R.string.delete_photo)));
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
                    DatePickerFragment newFragment = new DatePickerFragment();
                    newFragment.show(getSupportFragmentManager(), "datePicker");
                }
            }
        });

        actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_30dp);

            if (textUserName != null) {
                actionBar.setTitle(textUserName);
                editTextName.setText(textUserName);
            } else {
                textUserName = "";
            }
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

                invalidateOptionsMenu();
            }
        });

        // если окно отрылось как просмотр профиля,
        // то редактирование запрещено
        // если редактировать можно, но нет фото, то и удалять не нужно
        if (editUser) {
            editTextName.setEnabled(false);
            editTextDate.setEnabled(false);
            imagePhoto.setClickable(false);
            textDeleteUserPhoto.setVisibility(View.INVISIBLE);
            fab.startAnimation(fabShowAnimation);
        } else if (userPhotoUri.equals("No_Photo")) {
            textDeleteUserPhoto.setVisibility(View.INVISIBLE);
        }
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
                Toast.makeText(UserActivity.this, R.string.cant_load_photo, Toast.LENGTH_LONG).show();
            } else {
                // если грузим фотку в первый раз
                if (imageUriInView == null) {
                    loadPhotoIntoViewAndGetBitmap(newSelectedImageUri);

                    // если грузим другую фотку вместо уже загруженной (ту же фотку повторно не грузим)
                } else if (!imageUriInView.equals(newSelectedImageUri)) {
                    loadPhotoIntoViewAndGetBitmap(newSelectedImageUri);
                }
            }
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
            userPhotoUri = pathToUsersPhoto + _idUser + "/usrImage.jpg";
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
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        loadedBitmap = null;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);

        menu.removeItem(R.id.action_delete);
        // добавление в меню текста с картинкой
        menu.add(0, R.id.action_delete, 3, menuIconWithText(getResources().getDrawable(R.drawable.ic_delete_red_24dp),
                getResources().getString(R.string.delete_user)));

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

            case R.id.action_save:

                // флаг, чтоб повторный клик не работал,
                // пока идет сохранения
                if (onSavingOrUpdating) {
                    return true;
                }

                onSavingOrUpdating = true;

                if (userHasNotChanged() && !newUser) {

                    hideSoftInput();

                    focusHolder.requestFocus();

                    editUser = true;
                    editTextName.setEnabled(false);
                    editTextDate.setEnabled(false);
                    imagePhoto.setClickable(false);
                    textDeleteUserPhoto.setVisibility(View.INVISIBLE);

                    invalidateOptionsMenu();

                    fab.startAnimation(fabShowAnimation);

                    onSavingOrUpdating = false;

                } else {
                    saveOrUpdateUser();
                }

                return true;

            case R.id.action_delete:
                // флаг, чтоб клик не работал,
                // пока идет сохранения
                if (onSavingOrUpdating) {
                    return true;
                }

                showDeleteConfirmationDialog();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        if (userHasNotChanged()) {
            super.onBackPressed();
            finish();
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
                        //finish();
                    }
                };

        showUnsavedChangesDialog(discardButtonClickListener);
    }

    // Диалог "Удалить пользователя или отменить удаление"
    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.delete_dialog_msg) + " " + editTextName.getText() + "?");
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                deleteUserFromDataBase();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    // Диалог "сохранить или выйти без сохранения"
    private void showUnsavedChangesDialog(DialogInterface.OnClickListener discardButtonClickListener) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);

        builder.setNegativeButton(R.string.no, discardButtonClickListener);

        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
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
            textInputLayoutName.setError(getString(R.string.error_name));
            focusHolder.requestFocus();
            editTextName.startAnimation(scaleAnimation);
            wrongField = true;
        } else {
            textInputLayoutName.setError(null);
        }

        if (TextUtils.isEmpty(birthDateToCheck)) {
            textInputLayoutDate.setError(getString(R.string.error_date));
            focusHolder.requestFocus();
            editTextDate.startAnimation(scaleAnimation);
            wrongField = true;
        } else {
            textInputLayoutDate.setError(null);
        }

        // если поля имени и др были не верными - выходим
        if (wrongField) {
            onSavingOrUpdating = false;
            return;
        }

        // проверка окончена, начинаем сохранение

        // скручиваем клавиатуру
        hideSoftInput();

        focusHolder.requestFocus();

        textUserName = nameToCheck;
        textUserBirthDate = birthDateToCheck;

        actionBar.setTitle(textUserName);

        // когда сохраняем НОВОГО пользователя в базу, вместо пути к фото пишем "No_Photo" на случай,
        // если фото не будет установленно
        // при сохранении пользователя в базу получаем его _id
        // далее, если фото будет выбрано, то дописываем (обновляем) путь к фото в базу в папку под номером _id пользователя

        // если фото было удалено нажатием на "удалить фото", то удалить папка с (единственным) фото (если она есть)
        if (userSetNoPhotoUri.equals("Set_No_Photo") && pathToUsersPhoto != null) {

            // формируем путь к папке фото юзера
            File myDir = new File(pathToUsersPhoto + _idUser); //  /data/data/com.gmail.krbashianrafael.medpunkt/files/users_photos/1

            if (myDir.exists()) {
                try {
                    //  use Apache Commons IO
                    FileUtils.deleteDirectory(myDir);
                    Toast.makeText(this, "User's Photo Deleted", Toast.LENGTH_LONG).show();
                } catch (IOException e) {
                    Toast.makeText(this, "User's Photo NOT Deleted", Toast.LENGTH_LONG).show();
                    e.printStackTrace();
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
        if (goBack) {
            goToUsersActivity();
        } else {
            goToDiseasesActivity();
        }
    }

    private void afterUpdateUser() {
        // если была нажата стрелка "обратно" - идем обратно
        if (goBack) {
            goToUsersActivity();
        } else {
            editUser = true;
            userHasChangedPhoto = false;

            invalidateOptionsMenu();

            fab.startAnimation(fabShowAnimation);
            editTextName.setEnabled(false);
            editTextDate.setEnabled(false);
            imagePhoto.setClickable(false);
            textDeleteUserPhoto.setVisibility(View.INVISIBLE);

            onSavingOrUpdating = false;
        }
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

            // получаем _idUser из возвращенного newUri
            _idUser = ContentUris.parseId(newUri);

            // создаем папку и получаем путь к папке файла фото пользователя для сохранения
            // пирсваиваем userPhotoUri путь к файлу фото пользователя для сохранения
            File fileDir = null;
            int rowsAffected = 0;

            if (pathToUsersPhoto != null && loadedBitmap != null) {
                // формируем путь к папке и файлу фото юзера
                fileDir = new File(pathToUsersPhoto + _idUser);
                userPhotoUri = fileDir.toString() + "/usrImage.jpg";

                // обновляем данные по пути к файлу фото пользовател в базе
                // теперь userPhotoUri = /data/data/com.gmail.krbashianrafael.medpunkt/files/users_photos/1
                rowsAffected = insertUserPhotoUriToDataBase(userPhotoUri);
            }

            // если обновить даныые о пути к фото НОВОГО пользователя в Базе не получилось
            if (rowsAffected == 0 && loadedBitmap != null) {
                Toast.makeText(UserActivity.this, "User's PhotoUri NOT Saved To DataBase",
                        Toast.LENGTH_LONG).show();

                // то удаляем папку для фото, а userPhotoUri присваиваем No_Photo
                userPhotoUri = "No_Photo";

                // формируем путь к папке фото юзера и удалем папку
                File myDir = null;

                if (pathToUsersPhoto != null) {
                    //  /data/data/com.gmail.krbashianrafael.medpunkt/files/users_photos/1
                    myDir = new File(pathToUsersPhoto + _idUser);
                }

                if (myDir != null && myDir.exists()) {
                    try {
                        //  use Apache Commons IO
                        FileUtils.deleteDirectory(myDir);
                        Toast.makeText(this, "User's Photo Deleted", Toast.LENGTH_LONG).show();

                        // после проблемного обновленя userPhotoUri и удаления файла фото
                        // остаемся в UserActivity
                        goBack = false;
                        newUser = false;
                        afterUpdateUser();

                    } catch (IOException e) {
                        Toast.makeText(this, "User's Photo NOT Deleted", Toast.LENGTH_LONG).show();

                        goBack = false;
                        newUser = false;
                        afterUpdateUser();

                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(this, "User's Photo NOT Deleted", Toast.LENGTH_LONG).show();

                    goBack = false;
                    newUser = false;
                    afterUpdateUser();
                }

                // если все прошло удачно
            } else {
                Toast.makeText(UserActivity.this, "User Saved To DataBase", Toast.LENGTH_LONG).show();

                // если userPhotoUri обновилось,
                // сохраняем файл фото (если фото было загружено)
                // и идем по нормальному сценарию выхода после сохранения нового пользователя

                // если фото было загружено (loadedBitmap != null)
                // то сохраняем фото в папку под _idUser

                // сохранение файла фото происходит асинхронно
                // в UserPhotoSavingAsyncTask
                // в конструктор передаются actyvity, loadedBitmap, и путь к папке для фото (в виде файла)
                if (loadedBitmap != null) {
                    new UserPhotoSavingAsyncTask(this, loadedBitmap, fileDir).execute();
                } else {
                    afterSaveUser();
                }
            }
            // если первичное сохранение нового пользователя в Базу НЕ было успешным
        } else {
            Toast.makeText(UserActivity.this, "User NOT Saved To DataBase", Toast.LENGTH_LONG).show();
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

        ContentValues values = new ContentValues();
        values.put(MedContract.UsersEntry.COLUMN_USER_NAME, textUserName);
        values.put(MedContract.UsersEntry.COLUMN_USER_DATE, textUserBirthDate);
        //userPhotoUri = /data/data/com.gmail.krbashianrafael.medpunkt/files/users_photos/1
        values.put(MedContract.UsersEntry.COLUMN_USER_PHOTO_PATH, userPhotoUri);

        // Uri к юзеру, который будет обновляться
        Uri mCurrentUserUri = Uri.withAppendedPath(UsersEntry.CONTENT_USERS_URI, String.valueOf(_idUser));

        // создаем папку и получаем путь к папке файла фото пользователя для обновления
        File fileDir = null;
        if (pathToUsersPhoto != null && loadedBitmap != null) {
            // формируем путь к папке и файлу фото юзера
            fileDir = new File(pathToUsersPhoto + _idUser);
        }

        // делаем update в Базе
        int rowsAffected = getContentResolver().update(mCurrentUserUri, values, null, null);

        // если update в Базе был НЕ успешным
        if (rowsAffected == 0) {
            Toast.makeText(UserActivity.this, "User NOT Updated To DataBase",
                    Toast.LENGTH_LONG).show();

            // если обновление было неудачным, то остаемся на месте
            goBack = false;
            afterUpdateUser();

            // update в Базе был успешным
        } else {
            Toast.makeText(UserActivity.this, "User Updated To DataBase",
                    Toast.LENGTH_LONG).show();
            // если была загрузенна новое фото loadedBitmap != null,
            // то в отдельном потоке сохраняем фото в файл в папку юзера
            if (loadedBitmap != null) {
                new UserPhotoSavingAsyncTask(this, loadedBitmap, fileDir).execute();
            } else {
                // если новое фото не было загружено,
                // то обновление в базе уже было и больше ничего делать не надо

                afterUpdateUser();
            }
        }
    }

    private void deleteUserFromDataBase() {
        // Uri к юзеру, который будет удаляться
        Uri mCurrentUserUri = Uri.withAppendedPath(UsersEntry.CONTENT_USERS_URI, String.valueOf(_idUser));

        int rowsDeleted = 0;

        // делаем удаление пользователя из Базы
        if (_idUser != 0) {
            rowsDeleted = getContentResolver().delete(mCurrentUserUri, null, null);
        }

        if (rowsDeleted == 0) {
            Toast.makeText(this, "User NOT Deleted from DataBase", Toast.LENGTH_LONG).show();
        } else {

            Toast.makeText(this, "User Deleted from DataBase", Toast.LENGTH_LONG).show();

            // формируем путь к папке фото юзера и удалем папку с фото
            File myDir = null;

            if (pathToUsersPhoto != null) {
                //  /data/data/com.gmail.krbashianrafael.medpunkt/files/users_photos/1
                myDir = new File(pathToUsersPhoto + _idUser);
            }

            if (myDir != null && myDir.exists()) {
                try {
                    FileUtils.deleteDirectory(myDir);
                    Toast.makeText(this, "User's Photo Deleted", Toast.LENGTH_LONG).show();

                    goToUsersActivity();

                } catch (IOException e) {
                    Toast.makeText(this, "User's Photo NOT Deleted", Toast.LENGTH_LONG).show();

                    goBack = false;
                    afterUpdateUser();

                    e.printStackTrace();
                }
            } else {
                // значит, у юзера уже не было папки с фото
                // и удалять нечего
                goToUsersActivity();
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
        private WeakReference<File> fileToSaveDirReference;

        UserPhotoSavingAsyncTask(UserActivity context, Bitmap loadedBitmap, File fileToSaveDir) {
            userActivityReference = new WeakReference<>(context);
            loadedBitmapReference = new WeakReference<>(loadedBitmap);
            fileToSaveDirReference = new WeakReference<>(fileToSaveDir);
        }

        // в Background делаем сохранение Bitmap в File
        @Override
        protected File doInBackground(Void... params) {

            // получаем из WeakReference<File> fileToSaveDir путь к папке для файла фото
            // и проверяем его на null, т.к. GC мог его собрать
            // если таокое произошло, то файл не сохраняяем
            File fileToSaveDir = fileToSaveDirReference.get();
            if (fileToSaveDir == null) {
                return null;
            }

            // создаем папку для фото юзера (если папка уже есть, то повторно она не создается
            if (!fileToSaveDir.mkdirs()) {
                Log.d("file", "users_photos_dir_Not_created");
            }

            // даем имя файлу фото "usrImage.jpg" и создаем обект File fileToSave
            String fileName = "usrImage.jpg";
            File fileToSave = new File(fileToSaveDir, fileName);

            // при этом путь к файлу
            // получается: /data/data/com.gmail.krbashianrafael.medpunkt/files/users_photos/1/usrImage.jpg

            // если файл уже есть, то заменяем файл удалением, т.к. у юзера бдует тольок одно фото
            if (fileToSave.exists()) {
                // удаляем файл
                if (!fileToSave.delete()) {

                    // если файл не удалился
                    final UserActivity activity = userActivityReference.get();
                    // если activity уже null, то возвращаем null без Toast.makeText
                    if (activity == null || activity.isFinishing()) {
                        return null;
                    }
                    // если activity НЕ null, то возвращаем null с Toast.makeText
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(activity, R.string.file_not_deleted, Toast.LENGTH_LONG).show();
                        }
                    });

                    return null;
                }
            }

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

            // если при обновлении файла фото во время обновления пользователя
            // фото не сохранилось или возникли ошибки при сохранении файла фото,
            // то удаляем папку со всем содержимым и прописываем в базу userPhotoUri = No_Photo
            if (!fileToSave.exists()) {
                Uri fileToSaveDirUri = Uri.fromFile(fileToSaveDir);
                long _idUser = ContentUris.parseId(fileToSaveDirUri);

                final UserActivity activity = userActivityReference.get();
                if (activity != null) {
                    int rowsAffected = setUserPhotoUriToNoPhotoInDataBase(activity, _idUser);

                    if (rowsAffected == 0) {
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(activity, "User's PhotoUri NOT Updated To DataBase", Toast.LENGTH_LONG).show();
                            }
                        });
                    } else {
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(activity, "User's PhotoUri set to No_Photo in DataBase", Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                }

                if (fileToSaveDir.exists()) {
                    try {
                        FileUtils.deleteDirectory(fileToSaveDir);

                        if (activity != null) {
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(activity, "User's Photo Deleted", Toast.LENGTH_LONG).show();
                                    activity.goBack = false;
                                    activity.afterUpdateUser();
                                }
                            });
                        }


                    } catch (IOException e) {
                        if (activity != null) {
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(activity, "User's Photo NOT Deleted", Toast.LENGTH_LONG).show();
                                    activity.goBack = false;
                                    activity.afterUpdateUser();
                                }
                            });
                        }

                        e.printStackTrace();
                    }
                } else {
                    if (activity != null) {
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(activity, "User's Photo NOT Deleted", Toast.LENGTH_LONG).show();
                                activity.goBack = false;
                                activity.afterUpdateUser();
                            }
                        });
                    }
                }
            }

            return fileToSave;
        }


        @Override
        protected void onPostExecute(File savedFile) {
            super.onPostExecute(savedFile);

            UserActivity activity = userActivityReference.get();
            if (activity == null || activity.isFinishing() || savedFile == null) {
                return;
            }

            // обнуляем loadedBitmap
            activity.loadedBitmap = null;

            // если сохранение или обновление файла фото было удачным
            if (savedFile.exists()) {

                activity.userPhotoUri = savedFile.toString();

                if (activity.newUser) {
                    Toast.makeText(activity, "User's Photo Saved", Toast.LENGTH_LONG).show();
                    activity.afterSaveUser();
                } else {
                    Toast.makeText(activity, "User's Photo Updated", Toast.LENGTH_LONG).show();
                    activity.afterUpdateUser();
                }
                // если сохранение или обновление файла фото было НЕ удачным
            } else {
                activity.userPhotoUri = "No_Photo";
                Toast.makeText(activity, R.string.cant_save_photo, Toast.LENGTH_LONG).show();
            }
        }

        // если при обновлении файла фото во время обновления пользователя
        // фото не сохранилось или возникли ошибки при сохранении файла фото,
        // то удаляем папку со всем содержимым и прописываем в базу userPhotoUri = No_Photo
        private int setUserPhotoUriToNoPhotoInDataBase(Context context, long _idUser) {

            ContentValues values = new ContentValues();

            String userPhotoUriNoPhoto = "No_Photo";

            values.put(UsersEntry.COLUMN_USER_PHOTO_PATH, userPhotoUriNoPhoto);

            // Uri к юзеру, который будет обновляться
            Uri mCurrentUserUri = Uri.withAppendedPath(MedContract.UsersEntry.CONTENT_USERS_URI, String.valueOf(_idUser));

            // делаем update прописываем userPhotoUri = "No_Photo";
            return context.getContentResolver().update(mCurrentUserUri, values, null, null);
        }
    }
}


