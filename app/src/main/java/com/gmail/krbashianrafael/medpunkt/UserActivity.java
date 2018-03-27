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
import android.support.annotation.NonNull;
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
    private boolean newUser, goBack, editUser, userHasChangedPhoto = false;

    private ActionBar actionBar;

    // имя и дата рождени пришедшие из DiseasesActivity
    //private String textForUserActivityTitle, textForUserActivitybirthDate;

    // имя и дата рождени полей UserActivity
    private String textUserName, textUserBirthDate;

    // поля имени, ДР и focusHolder
    private TextInputLayout textInputLayoutName, textInputLayoutDate;
    private TextInputEditText editTextDate, editTextName;
    private EditText focusHolder;

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
    /*private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @SuppressLint("ClickableViewAccessibility")
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            userHasChanged = true;
            return false;
        }
    };*/

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
        //goBackArraw = intent.getBooleanExtra("goBackArraw", false);
        textUserName = intent.getStringExtra("UserName");
        textUserBirthDate = intent.getStringExtra("birthDate");
        _id = intent.getIntExtra("_id", 0);
        userPhotoUri = intent.getStringExtra("userPhotoUri");

        //*********
        //Log.d("saveUserPhoto", "intent userPhotoUri = " + userPhotoUri);

        // если клавиатура перекрывает поле ввода, то поле ввода приподнимается
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN | WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);


        // привязка для snackbar
        mLayout = findViewById(R.id.user_layout);
        /*mLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Если окно открылось в состоянии editUser,
                // то mLayout будет перекрывать все окно. Иначе mLayout mLayout.setVisibility(View.GONE);
                Toast.makeText(UserActivity.this,
                        ToastTextWithIcon(getResources().getDrawable(R.drawable.ic_border_color_white_24dp), getResources().getString(R.string.сlick_edit)),
                        Toast.LENGTH_LONG).show();
            }
        });*/

        linearLayoutNoUserPhoto = findViewById(R.id.no_user_photo);
        imagePhoto = findViewById(R.id.image_photo);

        if (!userPhotoUri.equals("No_Photo")) {
            // если есть файл фото для загрузки, то грузим
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
                textDeleteUserPhoto.setVisibility(View.INVISIBLE);
                userHasChangedPhoto = true;
            }
        });

        textInputLayoutName = findViewById(R.id.text_input_layout_name);
        editTextName = findViewById(R.id.editText_name);
        //editTextName.setOnTouchListener(mTouchListener);


        focusHolder = findViewById(R.id.focus_holder);

        focusHolder.requestFocus();

        /*if (!newUser) {
            focusHolder.requestFocus();
        } else {
            // если новый пользователь, то выделяем поле ввода и показываем клавиатуру
            editTextName.requestFocus();
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        }*/

        textInputLayoutDate = findViewById(R.id.text_input_layout_date);
        editTextDate = findViewById(R.id.editText_date);
        //editTextDate.setOnTouchListener(mTouchListener);

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

        actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp);

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

        // если окно отрылось как просмотр профиля,
        // то редактирование запрещено
        // если редактировать можно, но нет фото, то и удалять не нужно
        if (editUser) {
            editTextName.setEnabled(false);
            editTextDate.setEnabled(false);
            imagePhoto.setClickable(false);
            textDeleteUserPhoto.setVisibility(View.INVISIBLE);
            mLayout.setVisibility(View.VISIBLE);
        } else if (!editUser && userPhotoUri.equals("No_Photo")) {
            textDeleteUserPhoto.setVisibility(View.INVISIBLE);
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

                userHasChangedPhoto = true;
                textDeleteUserPhoto.setVisibility(View.VISIBLE);
                linearLayoutNoUserPhoto.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_user, menu);

        /*MenuItem itemSave = menu.getItem(0);
        SpannableString saveTitle = new SpannableString(getResources().getString(R.string.save));
        saveTitle.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.colorAccentSecondary)), 0, saveTitle.length(), 0);
        itemSave.setTitle(saveTitle);

        MenuItem itemEdit = menu.getItem(1);
        SpannableString editTitle = new SpannableString(getResources().getString(R.string.edit));
        editTitle.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.colorAccentSecondary)), 0, editTitle.length(), 0);
        itemEdit.setTitle(editTitle);*/

        menu.removeItem(R.id.action_delete_user);
        // добавление в меню текста с картинкой
        menu.add(0, R.id.action_delete_user, 3, menuIconWithText(getResources().getDrawable(R.drawable.ic_delete_red_24dp), getResources().getString(R.string.delete_user)));

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

    /*private CharSequence ToastTextWithIcon(Drawable r, String title) {
        r.setBounds(0, 0, r.getIntrinsicWidth(), r.getIntrinsicHeight());
        SpannableString sb = new SpannableString(title + "    ");
        ImageSpan imageSpan = new ImageSpan(r, ImageSpan.ALIGN_BOTTOM);
        sb.setSpan(imageSpan, title.length() + 2, title.length() + 3, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        return sb;
    }*/

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
                // Если не было изменений
                if (!userHasChanged()) {
                    goToUsersActivity();
                    return true;
                }

                // Если были изменения
                //Toast.makeText(this, "User Has Changed", Toast.LENGTH_LONG).show();

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

            case R.id.action_save_user:

                if (!userHasChanged() && !newUser) {
                    //goToUsersActivity();
                    //showNoDataChangeddDialog();

                    // скручиваем клавиатуру
                    View viewToHide = this.getCurrentFocus();
                    if (viewToHide != null) {
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        if (imm != null) {
                            imm.hideSoftInputFromWindow(viewToHide
                                    .getWindowToken(), 0);
                        }
                    }

                    focusHolder.requestFocus();

                    editUser = true;
                    editTextName.setEnabled(false);
                    editTextDate.setEnabled(false);
                    imagePhoto.setClickable(false);
                    textDeleteUserPhoto.setVisibility(View.INVISIBLE);
                    invalidateOptionsMenu();

                    return true;
                }

                saveUser();

                return true;

            case R.id.action_edit_user:
                editTextName.setEnabled(true);
                editTextName.requestFocus();
                // устанавливаем курсор в конец строки (cursor)
                editTextName.setSelection(editTextName.getText().length());

                // показываем клавиатуру
                View viewToShow = this.getCurrentFocus();
                if (viewToShow != null) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm != null) {
                        imm.showSoftInput(viewToShow, 0);
                    }
                }

                editTextDate.setEnabled(true);
                imagePhoto.setClickable(true);

                editUser = false;

                if (!userPhotoUri.equals("No_Photo")) {
                    textDeleteUserPhoto.setVisibility(View.VISIBLE);
                } else {
                    textDeleteUserPhoto.setVisibility(View.INVISIBLE);
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
        if (!userHasChanged()) {
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

    /*private void showNoDataEnteredDialog() {
        // если пытаются сохранить нового пользователя и ничего не вводят
        // говорим пользователю, что "Данные для сохранения не введены"
        // спрашиваем "выйти без сохранения или остаться?
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.no_data_entered);

        builder.setPositiveButton(R.string.discard, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                goToUsersActivity();
            }
        });

        builder.setNegativeButton(R.string.stay, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }*/

    /*private void showNoDataChangeddDialog() {
        // если пытаются сохранить нового пользователя и ничего не вводят
        // говорим пользователю, что "Данные для сохранения не введены"
        // спрашиваем "выйти без сохранения или остаться?
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.no_data_Changed);

        builder.setPositiveButton(R.string.discard, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (goBackArraw) {
                    goToDiseasesActivity();
                } else {
                    goToUsersActivity();
                }
            }
        });

        builder.setNegativeButton(R.string.stay, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }*/

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

        String nameToCheck = editTextName.getText().toString().trim();
        String birthDateToCheck = editTextDate.getText().toString();

        // првоерка имени и ДР
        boolean wrongField = false;
        if (TextUtils.isEmpty(nameToCheck)) {
            textInputLayoutName.setError(getString(R.string.error_name));
            editTextName.requestFocus();
            wrongField = true;
        } else {
            textInputLayoutName.setError(null);
        }

        if (TextUtils.isEmpty(birthDateToCheck)) {
            textInputLayoutDate.setError(getString(R.string.error_date));
            if (wrongField) {
                editTextName.requestFocus();
            } else {
                focusHolder.requestFocus();
            }
            wrongField = true;
        } else {
            textInputLayoutDate.setError(null);
        }

        // скручиваем клавиатуру
        View viewToHide = this.getCurrentFocus();
        if (viewToHide != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(viewToHide
                        .getWindowToken(), 0);
            }
        }

        // если поля имени и др были не верными - выходим
        if (wrongField) {
            return;
        }


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
                        //Log.d("saveUserPhoto", " bitmap null");

                        // если новый пользователь, то сохраняем в базу и идем в DiseasesActivity
                        if (newUser) {
                            saveUserToDataBase();
                            Toast.makeText(UserActivity.this, "User Saved To DataBase", Toast.LENGTH_LONG).show();

                            if (goBack) {
                                goToUsersActivity();
                            } else {
                                goToDiseasesActivity();
                            }

                        }
                        // если НЕ новый пользователь, то обновляем в базу и
                        // если goBackArraw идем в DiseasesActivity, иначе - в UsersActivity
                        else {
                            updateUserToDataBase();
                            Toast.makeText(UserActivity.this, "User Updated To DataBase", Toast.LENGTH_LONG).show();

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
                            }


                            /*if (goBackArraw) {
                                goToDiseasesActivity();
                            } else {
                                goToUsersActivity();
                            }*/
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
                Toast.makeText(UserActivity.this, "User Saved To DataBase", Toast.LENGTH_LONG).show();

                if (goBack) {
                    goToUsersActivity();
                } else {
                    goToDiseasesActivity();
                }
            }
            // если НЕ новый пользователь, то обновляем в базу и
            // если goBackArraw идем в DiseasesActivity, иначе - в UsersActivity
            else {
                updateUserToDataBase();
                Toast.makeText(UserActivity.this, "User Updated To DataBase", Toast.LENGTH_LONG).show();

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
                }

                /* if (goBackArraw) {
                    goToDiseasesActivity();
                } else {
                    goToUsersActivity();
                }*/
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
            //Log.d("saveUserPhoto", "userPhotoUri = " + userPhotoUri);
        } else {
            userPhotoUri = "No_Photo";
            Toast.makeText(this, R.string.cant_save_photo, Toast.LENGTH_LONG).show();
        }

        if (newUser) {
            saveUserToDataBase();

            // т.к. Toast.makeText вызывается не с основного треда, надо делать через Looper
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(UserActivity.this, "User Saved To DataBase", Toast.LENGTH_LONG).show();
                }
            });

            if (goBack) {
                goToUsersActivity();
            } else {
                goToDiseasesActivity();
            }
        }
        // если НЕ новый пользователь, то обновляем в базу и
        // если goBackArraw идем в DiseasesActivity, иначе - в UsersActivity
        else {
            updateUserToDataBase();

            // т.к. Toast.makeText вызывается не с основного треда, надо делать через Looper
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(UserActivity.this, "User Updated To DataBase", Toast.LENGTH_LONG).show();
                }
            });

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
                        editTextName.setEnabled(false);
                        editTextDate.setEnabled(false);
                        imagePhoto.setClickable(false);
                        textDeleteUserPhoto.setVisibility(View.INVISIBLE);
                    }
                });
            }

           /* if (goBackArraw) {
                goToDiseasesActivity();
            } else {
                goToUsersActivity();
            }*/
        }

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

    // проверка на изменения пользователя
    private boolean userHasChanged() {
        if (userHasChangedPhoto ||
                !editTextName.getText().toString().equals(textUserName) ||
                !editTextDate.getText().toString().equals(textUserBirthDate)) {
            return true;
        }

        return false;
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


