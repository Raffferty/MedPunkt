package com.gmail.krbashianrafael.medpunkt;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
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

import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;


public class UserActivity extends AppCompatActivity {

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

    // угол поворота фотографии
    private float rotate = 0;

    // если нет пользоватлея будет рамка с текстом, что нет фото и можно загрузить
    private TextView textViewNoUserPhoto;

    // TextView удаления фото, при нажатии удаляется фото
    private TextView textDeleteUserPhoto;

    // код загрузки фото из галерии
    private static final int RESULT_LOAD_IMAGE = 9002;

    // путь к загружаемому фото
    private Uri selectedImage;

    // путь к сохраненному фото
    private String userPhotoUri, userSetNoPhotoUri = "";

    // id пользователя
    private int _idUser = 0;

    // View mLayout для привязки snackbar
    private View mLayout;

    // fab
    private FloatingActionButton fab;

    // Animation fabHideAnimation
    private Animation fabHideAnimation;

    // Animation fabShowAnimation
    private Animation fabShowAnimation;

    // элемент меню "сохранить"
    TextView menuItemSaveView;

    // Animation saveShowAnimation
    private Animation saveShowAnimation;

    // код разрешения на запись и чтение из экстернал
    private static final int PERMISSION_WRITE_EXTERNAL_STORAGE = 0;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        Intent intent = getIntent();

        // получаем переданный в интенте Uri,
        // есл intent.getData(); вернул null, значит это новый пользователь
        // в дальнейшем значение currentUserUri заменит значение newUser
        /*
      Content URI for the existing user (null if it's a new user)
      если в onCreate НЕ пришел Uri, то mCurrentPetUri будет null и откроется окно для добавления новго юзера
      иначе, откроется окно с данными существующего юзера для редактирования или удаления
     */
        Uri currentUserUri = intent.getData();
        _idUser = intent.getIntExtra("_idUser", 0);
        newUser = intent.getBooleanExtra("newUser", false);
        editUser = intent.getBooleanExtra("editUser", false);
        textUserName = intent.getStringExtra("UserName");
        textUserBirthDate = intent.getStringExtra("birthDate");
        userPhotoUri = intent.getStringExtra("userPhotoUri");

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
                Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                imagePhoto.setImageBitmap(myBitmap);
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
                selectedImage = null;
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
            actionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_36dp);

            if (textUserName != null) {
                actionBar.setTitle(textUserName);
                editTextName.setText(textUserName);
            }
            else {
                textUserName = "";
            }
        }

        if (textUserBirthDate != null) {
            editTextDate.setText(textUserBirthDate);
        }
        else {
            textUserBirthDate = "";
        }

        // анимация для элемента меню "сохранить"
        saveShowAnimation = AnimationUtils.loadAnimation(this, R.anim.save_show);

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
            selectedImage = data.getData();

            if (selectedImage != null) {
                // получаем угол поворота фотки
                rotate = getRotation(this, selectedImage);




                /*Picasso picasso = new Picasso.Builder(this).listener(new Picasso.Listener() {
                    @Override
                    public void onImageLoadFailed(Picasso picasso, Uri uri, Exception exception) {
                        exception.printStackTrace();
                    }
                }).build();*/

                Picasso.get().load(selectedImage).
                        placeholder(R.color.colorAccent).
                        error(R.color.colorAccentSecondary).
                        resize(imagePhoto.getWidth(), imagePhoto.getHeight()).
                        rotate(rotate).
                        centerInside().
                        into(imagePhoto);

                userHasChangedPhoto = true;
                textDeleteUserPhoto.setVisibility(View.VISIBLE);
                textViewNoUserPhoto.setVisibility(View.GONE);
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

        int orientation = cursor.getInt(0);
        cursor.close();
        cursor = null;
        return orientation;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_user_treatment_fullphoto, menu);

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

        // если в состоянии editUser (тоесть есть кнопка fab со значком редактирования)
        // то в меню элемент "сохранить" делаем не видимым
        // видимым остается "удалить"
        if (editUser) {
            MenuItem menuItemSave = menu.getItem(1);
            menuItemSave.setVisible(false);
        } else {

            // иначе, делаем невидимым "удалить"
            MenuItem menuItemDelete = menu.getItem(0);
            menuItemDelete.setVisible(false);

            // и создаем ActionView на основе элемента меню "сохранить" для применени анимации save_show
            // т.к. в menu_user_treatment_fullphoto элемент "сохранить" имеет атрибут
            // app:actionViewClass="android.widget.TextView"
            // то menuItemSave.getActionView() возвращает TextView
            // с которым и проделываем дальнейшие трансформации:
            // устанавливаем текст, размер шрифта, цвет шрифта, анимацию и слушатель нажатия
            // текст берем из R.string.save, где присутствует юникодовский пробел \u2000
            // иначе после слова "сохранить" обычные пробелы автоматически убираются
            // и слово вплотную прилегает к краю экрана
            MenuItem menuItemSave = menu.getItem(1);
            menuItemSaveView = (TextView) menuItemSave.getActionView();
            menuItemSaveView.setText(R.string.save);
            menuItemSaveView.setTextSize(18f);
            menuItemSaveView.setTextColor(getResources().getColor(R.color.colorAccentThird));

            menuItemSaveView.startAnimation(saveShowAnimation);

            menuItemSaveView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (userHasNotChanged() && !newUser) {
                        // скручиваем клавиатуру
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
                        saveUser();
                    }
                }
            });
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
                            public void onClick(DialogInterface dialogInterface, int i) {
                                goToUsersActivity();
                            }
                        };

                // если выходим с сохранением изменений
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;

            case R.id.action_delete:
                deleteUserFromDataBase();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        if (userHasNotChanged()) {
            super.onBackPressed();
            return;
        }

        textInputLayoutName.setError(null);
        textInputLayoutDate.setError(null);

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
    private void showUnsavedChangesDialog(DialogInterface.OnClickListener discardButtonClickListener) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);

        builder.setNegativeButton(R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                goBack = true;
                saveUser();

                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        builder.setPositiveButton(R.string.no, discardButtonClickListener);

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void saveUser() {
        // устанавливаем анимацию на случай Error
        ScaleAnimation scaleAnimation = new ScaleAnimation(0f, 1f, 0f, 0f);
        scaleAnimation.setDuration(200);

        String nameToCheck = editTextName.getText().toString().trim();
        String birthDateToCheck = editTextDate.getText().toString();

        // првоерка имени и ДР
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

        //TODO сохранение пути к фото в базу
        // когда сохраняем НОВОГО пользователя в базу, вместо пути к фото пишем "No_Photo" на случай,
        // если фото не будет установленно
        // при сохранении пользователя в базу получаем его _id
        // далее, если фото будет выбрано, то дописываем путь к фото в базу с именем файла содержащим _id пользователя
        // в данном случае присваиваем фейковый _id = 1

        _idUser = 1;

        // в отдельном потоке пишем файл фотки в интернал сторидж
        Thread t = new Thread(new Runnable() {
            Bitmap bitmap = null;

            @Override
            public void run() {
                try {
                    // получаем bitmap
                    bitmap = Picasso.get().load(selectedImage).
                            resize(imagePhoto.getWidth(), imagePhoto.getHeight()).
                            centerInside().
                            rotate(rotate).
                            get();
                    if (bitmap != null) {
                        saveUserPhoto(bitmap);
                    } else {
                        // если новый пользователь, то сохраняем в базу и идем в DiseasesActivity
                        if (newUser) {
                            saveUserToDataBase();
                            newUser =false;

                            if (goBack) {
                                goToUsersActivity();
                            } else {
                                goToDiseasesActivity();
                            }
                        }
                        // если НЕ новый пользователь, то обновляем в базу и
                        else {
                            updateUserToDataBase();

                            if (goBack) {
                                goToUsersActivity();
                            } else {
                                editUser = true;
                                userHasChangedPhoto = false;
                                editTextName.setEnabled(false);
                                editTextDate.setEnabled(false);
                                imagePhoto.setClickable(false);
                                textDeleteUserPhoto.setVisibility(View.INVISIBLE);

                                invalidateOptionsMenu();
                                fab.startAnimation(fabShowAnimation);
                            }
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        if (selectedImage != null) {
            t.start();

        } else {
            // если фото было удалено, то удалить файл фото (если он есть)
            if (userSetNoPhotoUri.equals("Set_No_Photo")) {
                //TODO здесь нужно будет формировать путь к фото по id
                String pathToPhoto = getString(R.string.pathToPhoto);
                File imgFile = new File(pathToPhoto);
                if (imgFile.exists()) {
                    if (!imgFile.delete()) {
                        Toast.makeText(UserActivity.this, R.string.file_not_deleted, Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(UserActivity.this, R.string.photo_deleted, Toast.LENGTH_LONG).show();
                    }
                }

                userPhotoUri = "No_Photo";
            }

            // если новый пользователь, то сохраняем в базу и идем в DiseasesActivity
            if (newUser) {
                saveUserToDataBase();
                newUser = false;

                // если была нажата стрелка "обратно" - идем обратно
                if (goBack) {
                    goToUsersActivity();
                } else {
                    goToDiseasesActivity();
                }
            }
            // если НЕ новый пользователь, то обновляем в базу и
            else {
                updateUserToDataBase();

                // если была нажата стрелка "обратно" - идем обратно
                if (goBack) {
                    goToUsersActivity();
                } else {
                    editUser = true;
                    userHasChangedPhoto = false;
                    editTextName.setEnabled(false);
                    editTextDate.setEnabled(false);
                    imagePhoto.setClickable(false);
                    textDeleteUserPhoto.setVisibility(View.INVISIBLE);

                    invalidateOptionsMenu();
                    fab.startAnimation(fabShowAnimation);
                }
            }
        }
    }

    private void saveUserPhoto(Bitmap bitmap) {
        // для экстернал
        // String root = Environment.getExternalStorageDirectory().toString();
        // File myDir = new File(root + "/Medpunkt/users_photos");
        // при этом получится File imgFile = new File("/storage/emulated/0/Medpunkt/users_photos/Image-1.jpg");

        // для интернал
        String root = getFilesDir().toString();
        File myDir = new File(root + "/users_photos"); //  /data/data/com.gmail.krbashianrafael.medpunkt/files/users_photos

        if (!myDir.mkdirs()) {
            Log.d("myDir.mkdirs", "users_photos_dir_Not_created");
        }

        String fileName = "Image-" + _idUser + ".jpg";
        File file = new File(myDir, fileName);

        //Log.d("file", "file = " + file);
        // при этом путь к файлу
        // получается: /data/data/com.gmail.krbashianrafael.medpunkt/files/users_photos/Image-1.jpg

        // заменяем файл удалением, т.к. у юзера бдует тольок одно фото
        if (file.exists()) {
            if (!file.delete()) {
                Toast.makeText(UserActivity.this, R.string.file_not_deleted, Toast.LENGTH_LONG).show();
            }
        }

        FileOutputStream outputStream;
        try {
            outputStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            outputStream.flush();
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (file.exists()) {
            userPhotoUri = file.toString();
        } else {
            userPhotoUri = "No_Photo";
            Toast.makeText(this, R.string.cant_save_photo, Toast.LENGTH_LONG).show();
        }

        if (newUser) {
            saveUserToDataBase();
            newUser = false;

            if (goBack) {
                goToUsersActivity();
            } else {
                goToDiseasesActivity();
            }
        }
        // если НЕ новый пользователь, то обновляем в базу и
        else {
            updateUserToDataBase();

            if (goBack) {
                goToUsersActivity();
            } else {
                editUser = true;
                userHasChangedPhoto = false;
                invalidateOptionsMenu();

                // только основной тред может прикасаться к созданным им View
                // поэтому обарачиваем в runOnUiThread
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        fab.startAnimation(fabShowAnimation);
                        editTextName.setEnabled(false);
                        editTextDate.setEnabled(false);
                        imagePhoto.setClickable(false);
                        textDeleteUserPhoto.setVisibility(View.INVISIBLE);
                    }
                });
            }
        }
    }

    // проверка на изменения пользователя
    private boolean userHasNotChanged() {
        return !userHasChangedPhoto &&
                editTextName.getText().toString().equals(textUserName) &&
                editTextDate.getText().toString().equals(textUserBirthDate);

    }

    private void goToDiseasesActivity() {
        Intent intent = new Intent(UserActivity.this, DiseasesActivity.class);
        intent.putExtra("_idUser", _idUser);
        intent.putExtra("UserName", textUserName);
        intent.putExtra("birthDate", textUserBirthDate);
        intent.putExtra("userPhotoUri", userPhotoUri);
        startActivity(intent);
    }

    private void goToUsersActivity() {
        Intent intent = new Intent(UserActivity.this, UsersActivity.class);
        startActivity(intent);
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
        //TODO реализовать сохранение пользователя в базу
        // т.к. Toast.makeText вызывается не с основного треда, надо делать через Looper
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(UserActivity.this, "User Saved To DataBase", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void updateUserToDataBase() {
        //TODO реализовать обновление пользователя в базу
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(UserActivity.this, "User Updated To DataBase", Toast.LENGTH_LONG).show();
            }
        });
    }


    private void deleteUserFromDataBase() {
        //TODO реализовать удаление пользователя из базы
        Toast.makeText(this, "User Deleted from DataBase", Toast.LENGTH_LONG).show();
    }
}


