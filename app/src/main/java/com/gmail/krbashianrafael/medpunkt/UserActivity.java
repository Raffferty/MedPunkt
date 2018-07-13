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
import com.gmail.krbashianrafael.medpunkt.data.MedContract.MedEntry;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;


public class UserActivity extends AppCompatActivity
        implements ActivityCompat.OnRequestPermissionsResultCallback {

    private final Handler myHandler = new Handler(Looper.getMainLooper());

    // загруженный Bitmap фотографии
    private Bitmap loadedBitmap;

    private String pathToUsersPhoto;

    // возможность изменфть пользователя, показывать стрелку обратно, был ли изменен пользователь
    private boolean newUser, goBack, editUser, userHasChangedPhoto;

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

                } else {
                    saveOrUpdateUser();
                }

                return true;

            case R.id.action_delete:
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
        }
    }

    // saveUserPhoto() исполняется в backThread
    private File saveUserPhoto() {

        if (pathToUsersPhoto == null) {
            return null;
        }

        // формируем путь к файлу фото юзера
        //  /data/data/com.gmail.krbashianrafael.medpunkt/files/users_photos/1
        File myDir = new File(pathToUsersPhoto + _idUser);

        if (!myDir.mkdirs()) {
            Log.d("file", "users_photos_dir_Not_created");
        }

        String fileName = "usrImage.jpg";
        File file = new File(myDir, fileName);

        // при этом путь к файлу
        // получается: /data/data/com.gmail.krbashianrafael.medpunkt/files/users_photos/1/usrImage.jpg

        // заменяем файл удалением, т.к. у юзера бдует тольок одно фото
        if (file.exists()) {
            if (!file.delete()) {
                // т.к. это исполняется в backThread
                // Toast.makeText надо делать через myHandler.post (тосты делаются в основном треде)
                myHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(UserActivity.this, R.string.file_not_deleted, Toast.LENGTH_LONG).show();
                    }
                });
            }
        }

        FileOutputStream outputStream;

        try {
            outputStream = new FileOutputStream(file);
            loadedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            outputStream.flush();
            outputStream.close();

        } catch (IOException e) {
            e.printStackTrace();
            loadedBitmap = null;
            return null;
        }

        return file;
    }

    // проверка на изменения пользователя
    private boolean userHasNotChanged() {
        return !userHasChangedPhoto &&
                editTextName.getText().toString().equals(textUserName) &&
                editTextDate.getText().toString().equals(textUserBirthDate);

    }

    private void goToDiseasesActivity() {
        Intent toDiseasesIntent = new Intent(UserActivity.this, DiseasesActivity.class);
        toDiseasesIntent.putExtra("newUser", true);
        toDiseasesIntent.putExtra("_idUser", _idUser);
        toDiseasesIntent.putExtra("UserName", textUserName);
        toDiseasesIntent.putExtra("birthDate", textUserBirthDate);
        toDiseasesIntent.putExtra("userPhotoUri", userPhotoUri);
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
        values.put(MedEntry.COLUMN_USER_NAME, textUserName);
        values.put(MedEntry.COLUMN_USER_DATE, textUserBirthDate);
        values.put(MedEntry.COLUMN_USER_PHOTO, userPhotoUri);

        // при сохранении пользователя делаем сначала insert и получаем Uri вставленной строки
        Uri newUri = getContentResolver().insert(MedEntry.CONTENT_URI, values);

        if (newUri != null) {

            // получаем _idUser из возвращенного newUri при вставке нового пользователя
            _idUser = ContentUris.parseId(newUri);

            // если фото было загружено (loadedBitmap), то сохраняем фото в папку под _idUser
            // insertUserPhotoUriToDataBase(String userPhotoUri) после сохранения файла фото в папку под _idUser,
            // обновляем в бае userPhotoUri этого _idUser

            if (loadedBitmap != null && pathToUsersPhoto != null) {
                new UserPhotoSavingAsyncTask().execute();
            } else {
                Toast.makeText(UserActivity.this, "User Saved To DataBase", Toast.LENGTH_LONG).show();
                afterSaveUser();
            }
        } else {
            Toast.makeText(UserActivity.this, "User NOT Saved To DataBase", Toast.LENGTH_LONG).show();
        }
    }

    // для добавления userPhotoUri нового пользователя после получения _idUser делаем update
    private void insertUserPhotoUriToDataBase(String userPhotoUri) {

        ContentValues values = new ContentValues();

        values.put(MedEntry.COLUMN_USER_PHOTO, userPhotoUri);

        Uri mCurrentUserUri = Uri.withAppendedPath(MedEntry.CONTENT_URI, String.valueOf(_idUser));

        // делаем update
        int rowsAffected = getContentResolver().update(mCurrentUserUri, values, null, null);

        if (rowsAffected == 0) {
            Toast.makeText(UserActivity.this, "User's PhotoUri NOT Saved To DataBase",
                    Toast.LENGTH_LONG).show();

            // если не получилось обновить userPhotoUri нового пользователя,
            // то удаляем папку с сохраненным фото, а userPhotoUri будет No_Photo
            // формируем путь к папке фото юзера и удалем папку с фото
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

        } else {
            // если userPhotoUri обновилось,
            // то идем по нормальному сценарию выхода после сохранения нового пользователя
            Toast.makeText(UserActivity.this, "User's PhotoUri Saved To DataBase",
                    Toast.LENGTH_LONG).show();

            afterSaveUser();
        }

    }

    private void updateUserToDataBase() {

        ContentValues values = new ContentValues();
        values.put(MedEntry.COLUMN_USER_NAME, textUserName);
        values.put(MedEntry.COLUMN_USER_DATE, textUserBirthDate);
        values.put(MedEntry.COLUMN_USER_PHOTO, userPhotoUri);

        // Uri к юзеру, который будет обновляться
        Uri mCurrentUserUri = Uri.withAppendedPath(MedEntry.CONTENT_URI, String.valueOf(_idUser));

        // делаем update
        int rowsAffected = getContentResolver().update(mCurrentUserUri, values, null, null);

        if (rowsAffected == 0) {
            Toast.makeText(UserActivity.this, "User NOT Updated To DataBase",
                    Toast.LENGTH_LONG).show();

            // если обновление было неудачным, то остаемся на месте
            goBack = false;
            afterUpdateUser();
        } else {
            // если была загрузенна новое фото loadedBitmap != null,
            // то в отдельном потоке сохраняем фото в файл в папку юзера
            if (loadedBitmap != null && pathToUsersPhoto != null) {
                new UserPhotoSavingAsyncTask().execute();
            } else {
                // если новое фото не было загружено,
                // то обновление в базе уже было и больше ничего делать не надо
                Toast.makeText(UserActivity.this, "User Updated To DataBase",
                        Toast.LENGTH_LONG).show();
                afterUpdateUser();
            }
        }
    }

    // если при обновлении файла фото во время обновления пользователя
    // фото не сохранилось или возникли ошибки при сохранении файла фото,
    // то удаляем папку со всем содержимым и прописываем в базу userPhotoUri = No_Photo

    // если не получилось прописать в базу userPhotoUri = No_Photo,
    // то оставляем все как есть
    private void updateUserPhotoUriToDataBase(String userPhotoUri) {

        ContentValues values = new ContentValues();

        values.put(MedEntry.COLUMN_USER_PHOTO, userPhotoUri);

        // Uri к юзеру, который будет обновляться
        Uri mCurrentUserUri = Uri.withAppendedPath(MedEntry.CONTENT_URI, String.valueOf(_idUser));

        // делаем update
        int rowsAffected = getContentResolver().update(mCurrentUserUri, values, null, null);

        if (rowsAffected == 0) {
            Toast.makeText(UserActivity.this, "User's PhotoUri NOT Updated To DataBase",
                    Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(UserActivity.this, "User's PhotoUri " + userPhotoUri + " Updated To DataBase",
                    Toast.LENGTH_LONG).show();
        }

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

                goBack = false;
                afterUpdateUser();

            } catch (IOException e) {
                Toast.makeText(this, "User's Photo NOT Deleted", Toast.LENGTH_LONG).show();

                goBack = false;
                afterUpdateUser();

                e.printStackTrace();
            }
        } else {
            Toast.makeText(this, "User's Photo NOT Deleted", Toast.LENGTH_LONG).show();

            goBack = false;
            afterUpdateUser();
        }
    }


    private void deleteUserFromDataBase() {
        // Uri к юзеру, который будет удаляться
        Uri mCurrentUserUri = Uri.withAppendedPath(MedEntry.CONTENT_URI, String.valueOf(_idUser));

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
                Toast.makeText(this, "User's Photo NOT Deleted", Toast.LENGTH_LONG).show();

                goBack = false;
                afterUpdateUser();
            }
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class UserPhotoSavingAsyncTask extends AsyncTask<Void, Void, File> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected File doInBackground(Void... voids) {
            return saveUserPhoto();
        }


        @Override
        protected void onPostExecute(File file) {
            super.onPostExecute(file);

            // обнуляем loadedBitmap
            loadedBitmap = null;

            if (file != null && file.exists()) {
                userPhotoUri = file.toString();

                if (newUser) {
                    Toast.makeText(UserActivity.this, "User's Photo Saved", Toast.LENGTH_LONG).show();
                    insertUserPhotoUriToDataBase(userPhotoUri);
                } else {
                    Toast.makeText(UserActivity.this, "User with Photo Updated", Toast.LENGTH_LONG).show();
                    afterUpdateUser();
                }

            } else {
                userPhotoUri = "No_Photo";
                Toast.makeText(UserActivity.this, R.string.cant_save_photo, Toast.LENGTH_LONG).show();
                updateUserPhotoUriToDataBase(userPhotoUri);
            }
        }
    }
}


