package com.gmail.krbashianrafael.medpunkt;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.design.widget.Snackbar;
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
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;


public class UserActivity extends AppCompatActivity {
    /**
     * Content URI for the existing pet (null if it's a new pet)
     * если в onCreate НЕ пришел Uri, то mCurrentPetUri будет null и откроется окно для добавления новго животного
     * иначе, откроется окно с данными нажатого животного для редактирования или удаления
     */
    private Uri currentUserUri;

    // возможность изменфть пользователя, показывать стрелку обратно, был ли изменен пользователь
    private boolean newUser, editUser, goBackArraw, userHasChanged = false;

    /*// имя и дата рождени пришедшие из DiseasesActivity
    private String textForUserActivityTitle, textForUserActivitybirthDate;
*/
    // имя и дата рождени полей UserActivity
    private String textUserName, textUserBirthDate;

    // поля имени и ДР
    private EditText editTextName, editTextDate;

    // фото пользоватлея
    private ImageView imagePhoto;

    // если нет пользоватлея будет рамка с текстом, что нет фото и можно загрузить
    private LinearLayout linearLayoutNoUserPhoto;

    // TextView удаления фото, при нажатии удаляется фото
    private TextView textDeleteUserPhoto;

    // код загрузки фото из галерии
    private static final int RESULT_LOAD_IMAGE = 9002;

    // путь к загружаемому фото
    private Uri selectedImage;

    // путь к сохраненному фото
    private String userPhotoUri = "No_Photo";

    // id пользователя
    private int _id = 0;

    // View mLayout для привязки snackbar
    private View mLayout;

    // код разрешения на запись и чтение из экстернал
    private static final int PERMISSION_WRITE_EXTERNAL_STORAGE = 0;

    // OnTouchListener для проверки изменений пользователя
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @SuppressLint("ClickableViewAccessibility")
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            userHasChanged = true;
            return false;
        }
    };

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        Intent intent = getIntent();

        // получаем переданный в интенте Uri,
        // есл intent.getData(); вернул null, значит это новый пользователь
        // в дальнейшем значение currentUserUri заменит значение newUser
        currentUserUri = intent.getData();
        newUser = intent.getBooleanExtra("newUser", false);

        editUser = intent.getBooleanExtra("editUser", false);
        goBackArraw = intent.getBooleanExtra("goBackArraw", false);
        textUserName = intent.getStringExtra("UserName");
        textUserBirthDate = intent.getStringExtra("birthDate");
        _id = intent.getIntExtra("_id", 0);
        userPhotoUri = intent.getStringExtra("userPhotoUri");

        //*********
        Log.d("saveUserPhoto", "intent userPhotoUri = " + userPhotoUri);

        // если клавиатура перекрывает поле ввода, то поле ввода приподнимается
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN | WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        // привязка для snackbar
        mLayout = findViewById(R.id.user_layout);

        linearLayoutNoUserPhoto = findViewById(R.id.no_user_photo);
        imagePhoto = findViewById(R.id.image_photo);

        // если есть файл фото для загрузки, то грузим
        if (!userPhotoUri.equals("No_Photo")) {
            linearLayoutNoUserPhoto.setVisibility(View.GONE);
            File imgFile = new File(userPhotoUri);
            if (imgFile.exists()) {
                Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                imagePhoto.setImageBitmap(myBitmap);
            }
        }

        // перед загрузкой фото получаем разреншение на чтение (и запись) из экстернал
        imagePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ActivityCompat.checkSelfPermission(UserActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    requestStoragePermission();
                } else {
                    userHasChanged = true;
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
                imagePhoto.setImageResource(R.color.colorAccent);
                userPhotoUri = "No_Photo";
                linearLayoutNoUserPhoto.setVisibility(View.VISIBLE);
                textDeleteUserPhoto.setVisibility(View.GONE);
            }
        });

        editTextName = findViewById(R.id.editText_name);
        editTextName.setOnTouchListener(mTouchListener);

        final EditText focusHolder = findViewById(R.id.focus_holder);
        editTextDate = findViewById(R.id.editText_date);
        editTextDate.setOnTouchListener(mTouchListener);

        editTextDate.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {

                    // скручиваем клавиатуру
                    View view = UserActivity.this.getCurrentFocus();
                    if (view != null) {
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        if (imm != null) {
                            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                        }
                    }

                    // передаем фокус, чтоб поля имени и ДР не были в фокусе
                    focusHolder.requestFocus();

                    // выбираем дату ДР
                    DatePickerFragment newFragment = new DatePickerFragment();
                    newFragment.show(getSupportFragmentManager(), "datePicker");
                }
            }
        });

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            if (goBackArraw) {
                actionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp);
            } else {
                actionBar.setHomeAsUpIndicator(R.drawable.ic_group_white_24dp);
            }

            if (textUserName != null) {
                actionBar.setTitle(textUserName);
                editTextName.setText(textUserName);
            }
        }

        if (textUserBirthDate != null) {
            editTextDate.setText(textUserBirthDate);
        }

        // если окно отрылось как просмотр профиля,
        // то редактирование запрещено
        // если редактировать можно, но нет фото, то и удалять не нужно
        if (editUser) {
            editTextName.setEnabled(false);
            editTextDate.setEnabled(false);
            imagePhoto.setClickable(false);
            textDeleteUserPhoto.setVisibility(View.GONE);
        } else if (!editUser && userPhotoUri.equals("No_Photo")) {
            textDeleteUserPhoto.setVisibility(View.GONE);
        }
    }

    // запрос разрешения на запись и чтение из и запись в экстернал
    private void requestStoragePermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            Snackbar.make(mLayout, R.string.why_need_permission_to_srorage,
                    Snackbar.LENGTH_INDEFINITE).setAction("OK", new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    ActivityCompat.requestPermissions(UserActivity.this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            PERMISSION_WRITE_EXTERNAL_STORAGE);
                }
            }).show();

        } else {
            Snackbar.make(mLayout,
                    R.string.permission_not_available,
                    Snackbar.LENGTH_LONG).show();

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    PERMISSION_WRITE_EXTERNAL_STORAGE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        if (requestCode == PERMISSION_WRITE_EXTERNAL_STORAGE) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                userHasChanged = true;
                Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(galleryIntent, RESULT_LOAD_IMAGE);
            } else {
                Snackbar.make(mLayout, R.string.permission_was_denied,
                        Snackbar.LENGTH_LONG)
                        .show();
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
                Picasso.with(this).load(selectedImage).
                        placeholder(R.color.colorAccent).
                        error(R.color.colorAccentSecondary).
                        resize(imagePhoto.getWidth(), imagePhoto.getHeight()).
                        centerInside().
                        into(imagePhoto);

                textDeleteUserPhoto.setVisibility(View.VISIBLE);
                linearLayoutNoUserPhoto.setVisibility(View.GONE);
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_user, menu);

        menu.removeItem(R.id.action_delete_user);
        // добавление в меню текста с картинкой
        menu.add(0, R.id.action_delete_user, 3, menuIconWithText(getResources().getDrawable(R.drawable.ic_delete_red_24dp), getResources().getString(R.string.delete_user)));

        return true;
    }

    // для SpannableString
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
            MenuItem menuItemEdit = menu.getItem(2);
            MenuItem menuItemDelete = menu.getItem(0);
            menuItemEdit.setVisible(false);
            menuItemDelete.setVisible(false);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                // Если стрелка обратно, то идем в DiseasesActivity
                if (goBackArraw) {
                    // Если не было изменений
                    if (!userHasChanged) {
                        goToDiseasesActivity();
                        return true;
                    }

                    // Если были изменения
                    Toast.makeText(this, "User Has Changed", Toast.LENGTH_LONG).show();

                    // если выходим без сохранения изменений
                    DialogInterface.OnClickListener discardButtonClickListener =
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    goToDiseasesActivity();
                                }
                            };

                    // если выходим с сохранением изменений
                    showUnsavedChangesDialog(discardButtonClickListener);
                    return true;
                }
                // если вместо стрелки обратно показывает "группа пользователей", то идем в UsersActivity
                else {
                    // Если не было изменений
                    if (!userHasChanged) {
                        goToUsersActivity();
                        return true;
                    }

                    // Если были изменения
                    Toast.makeText(this, "User Has Changed", Toast.LENGTH_LONG).show();

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
                }

            case R.id.action_save_user:

                if (goBackArraw) {
                    if (!userHasChanged) {
                        goToDiseasesActivity();
                        return true;
                    }


                } else {
                    if (!userHasChanged) {
                        goToUsersActivity();
                        return true;
                    }

                }

                saveUser();

                return true;

            case R.id.action_edit_user:
                Toast.makeText(this, "Edit", Toast.LENGTH_LONG).show();
                editTextName.setEnabled(true);
                editTextDate.setEnabled(true);
                imagePhoto.setClickable(true);
                editUser = false;

                if (!userPhotoUri.equals("No_Photo")) {
                    textDeleteUserPhoto.setVisibility(View.VISIBLE);
                } else {
                    textDeleteUserPhoto.setVisibility(View.GONE);
                }

                invalidateOptionsMenu();
                return true;

            case R.id.action_delete_user:
                deleteUserFromDataBase();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        if (!userHasChanged) {
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
    private void showUnsavedChangesDialog(DialogInterface.OnClickListener discardButtonClickListener) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);

        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.save, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                saveUser();

                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void saveUser() {

        textUserName = editTextName.getText().toString();
        textUserBirthDate = editTextDate.getText().toString();

        // првоерка имени и ДР
        if (TextUtils.isEmpty(textUserName.trim()) || TextUtils.isEmpty(textUserBirthDate)) {
            Toast.makeText(this, "Укажите, пожалуйста имя и дату рождения", Toast.LENGTH_LONG).show();
            return;
        }

        //TODO сохранение пути к фото в базу
        // когда сохраняем НОВОГО пользователя в базу, вместо пути к фото пишем "No_Photo" на случай,
        // если фото не будет установленно
        // при сохранении пользователя в базу получаем его _id
        // далее, если фото будет выбрано, то дописываем путь к фото в базу с именем файла содержащим _id пользователя
        // в данном случае присваиваем фейковый _id = 1

        _id = 1;

        // в отдельном потоке пишем файл фотки в интернал сторидж
        Thread t = new Thread(new Runnable() {

            Bitmap bitmap = null;

            @Override
            public void run() {
                try {
                    bitmap = Picasso.with(UserActivity.this).
                            load(selectedImage).
                            resize(imagePhoto.getWidth(), imagePhoto.getHeight()).
                            centerInside().
                            get();
                    if (bitmap != null) {
                        saveUserPhoto(bitmap);
                    } else {
                        //*********
                        Log.d("saveUserPhoto", " bitmap null");

                        // если новый пользователь, то сохраняем в базу и идем в DiseasesActivity
                        if (newUser) {
                            saveUserToDataBase();
                            goToDiseasesActivity();
                        }
                        // если НЕ новый пользователь, то обновляем в базу и
                        // если goBackArraw идем в DiseasesActivity, иначе - в UsersActivity
                        else {
                            updateUserToDataBase();

                            if (goBackArraw) {
                                goToDiseasesActivity();
                            } else {
                                goToUsersActivity();
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
            // если новый пользователь, то сохраняем в базу и идем в DiseasesActivity
            if (newUser) {
                saveUserToDataBase();
                goToDiseasesActivity();
            }
            // если НЕ новый пользователь, то обновляем в базу и
            // если goBackArraw идем в DiseasesActivity, иначе - в UsersActivity
            else {
                updateUserToDataBase();

                if (goBackArraw) {
                    goToDiseasesActivity();
                } else {
                    goToUsersActivity();
                }
            }

            Toast.makeText(this, "User Saved", Toast.LENGTH_LONG).show();
        }
    }

    private void saveUserPhoto(Bitmap bitmap) {
        // для экстернал
        // String root = Environment.getExternalStorageDirectory().toString();
        // File myDir = new File(root + "/Medpunkt/users_photos");
        // при этом получится File imgFile = new File("/storage/emulated/0/Medpunkt/users_photos/Image-1.jpg");

        // для интернал
        String root = getFilesDir().toString();
        File myDir = new File(root + "/users_photos");

        myDir.mkdirs();

        /*long currentTime = Calendar.getInstance().getTimeInMillis();

        //*********
        Log.d("saveUserPhoto", "currentTime = " + currentTime);*/


        String fname = "Image-" + _id + ".jpg";
        File file = new File(myDir, fname);

        //*********
        //Log.d("saveUserPhoto", " file = " + file);

        // заменяем файл удалением, т.к. у юзера бдует тольок одно фото
        if (file.exists()) {
            file.delete();
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
            //*********
            Log.d("saveUserPhoto", "userPhotoUri = " + userPhotoUri);
        } else {
            userPhotoUri = "No_Photo";
            Toast.makeText(this, R.string.cant_save_photo, Toast.LENGTH_LONG);
        }

        if (newUser) {
            saveUserToDataBase();
            goToDiseasesActivity();
        }
        // если НЕ новый пользователь, то обновляем в базу и
        // если goBackArraw идем в DiseasesActivity, иначе - в UsersActivity
        else {
            updateUserToDataBase();

            if (goBackArraw) {
                goToDiseasesActivity();
            } else {
                goToUsersActivity();
            }
        }

        // т.к. Toast.makeText вызывается не с основного треда, надо делать через Looper
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(UserActivity.this, "User Saved", Toast.LENGTH_LONG).show();
            }
        });


        // удаление файла и папки
        /*if (file.exists()) {
            Log.d("saveUserPhoto", " file exists");
            file.delete();
            Log.d("saveUserPhoto", "file Deleted");
            if (file.exists()) {
                Log.d("saveUserPhoto", "file exists");
            } else {
                Log.d("saveUserPhoto", "file NOT exists");
            }
        } else {
            Log.d("saveUserPhoto", " file NOT exists");
        }


        if (myDir.exists()) {
            Log.d("saveUserPhoto", "myDir exists");
            myDir.delete();
            Log.d("saveUserPhoto", "myDir Deleted");
            if (myDir.exists()) {
                Log.d("saveUserPhoto", "myDir exists");
            } else {
                Log.d("saveUserPhoto", "myDir NOT exists");
            }
        } else {
            Log.d("saveUserPhoto", "myDir NOT exists");
        }*/
    }

    private void saveUserToDataBase() {
        //TODO реализовать сохранение пользователя в базу

    }

    private void updateUserToDataBase() {
        //TODO реализовать обновление пользователя в базу

    }


    private void deleteUserFromDataBase() {
        //TODO реализовать удаление пользователя из базы

        Toast.makeText(this, "Delete", Toast.LENGTH_LONG).show();
    }

    private void goToDiseasesActivity() {
        Intent intent = new Intent(UserActivity.this, DiseasesActivity.class);
        intent.putExtra("_id", _id);
        intent.putExtra("UserName", textUserName);
        intent.putExtra("birthDate", textUserBirthDate);
        intent.putExtra("userPhotoUri", userPhotoUri);
        startActivity(intent);
    }

    private void goToUsersActivity() {
        Intent intent = new Intent(UserActivity.this, UsersActivity.class);
        startActivity(intent);
    }
}


