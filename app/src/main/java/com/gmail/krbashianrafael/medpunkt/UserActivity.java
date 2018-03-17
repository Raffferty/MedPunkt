package com.gmail.krbashianrafael.medpunkt;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Spannable;
import android.text.SpannableString;
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
import android.widget.Toast;

import com.squareup.picasso.Picasso;


public class UserActivity extends AppCompatActivity {
    //
    private boolean editUser, goBackArraw, userHasChanged = false;

    // это имя и дата рождени пришедшие из DiseasesActivity
    private String textForUserActivityTitle, textForUserActivitybirthDate;

    // это имя и дата рождени из полей UserActivity
    private String textForTitle, textForBirthDate;

    private EditText editTextName, editTextDate;

    private ImageView imagePhoto;

    // код загрузки фото из галерии
    private static final int RESULT_LOAD_IMAGE = 9002;

    // путь к фото
    private String userPhotoUri = "android.resource://com.gmail.krbashianrafael.medpunkt/" + R.color.colorAccent;

    // для привязки snackbar
    private View mLayout;
    private static boolean showSnackBar = true;
    private static final int PERMISSION_READ_EXTERNAL_STORAGE = 0;

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
        editUser = intent.getBooleanExtra("editUser", false);
        goBackArraw = editUser;

        textForUserActivityTitle = intent.getStringExtra("Title");
        textForUserActivitybirthDate = intent.getStringExtra("birthDate");

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN | WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        // привязка для snackbar
        mLayout = findViewById(R.id.user_layout);

        imagePhoto = findViewById(R.id.image_photo);
        imagePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO загрузить фото
                //Toast.makeText(UserActivity.this, "Load Photo", Toast.LENGTH_LONG).show();


                showSnackBar = PhotoRequesPermissionHandler.getRuntimePhotoPermissionToStorage(UserActivity.this,
                        mLayout,PERMISSION_READ_EXTERNAL_STORAGE,
                        showSnackBar);

                if (!showSnackBar){
                    userHasChanged = true;

                Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                startActivityForResult(galleryIntent, RESULT_LOAD_IMAGE);}

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

                    View view = UserActivity.this.getCurrentFocus();
                    if (view != null) {
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        if (imm != null) {
                            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                        }
                    }

                    focusHolder.requestFocus();

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

            if (textForUserActivityTitle != null) {
                actionBar.setTitle(textForUserActivityTitle);
                editTextName.setText(textForUserActivityTitle);
            }
        }

        if (textForUserActivitybirthDate != null) {
            editTextDate.setText(textForUserActivitybirthDate);
        }

        // если окно отрылось как просмотр профиля,
        // то редактирование запрещено
        if (editUser) {
            editTextName.setEnabled(false);
            editTextDate.setEnabled(false);
            imagePhoto.setClickable(false);
        }
    }

     @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        // BEGIN_INCLUDE(onRequestPermissionsResult)
        if (requestCode == PERMISSION_READ_EXTERNAL_STORAGE) {
            // Request for camera permission.
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission has been granted. Start camera preview Activity.
                Snackbar.make(mLayout, R.string.permission_was_granted,
                        Snackbar.LENGTH_LONG)
                        .show();

                //TODO после получения разрешения грузим фотки в UserActyvity
                userHasChanged = true;

                Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                //galleryIntent.setType("image/*");

                startActivityForResult(galleryIntent, RESULT_LOAD_IMAGE);


            } else {
                // Permission request was denied.
                Snackbar.make(mLayout, R.string.permission_was_denied,
                        Snackbar.LENGTH_LONG)
                        .show();
            }
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && data != null) {
            Uri selectedImage = data.getData();

            Picasso.with(this).load(selectedImage).
                    placeholder(R.mipmap.ic_launcher).

                    // если возникнет ошибка - будет красный квадрат
                            error(R.color.colorAccentSecondary).

                    resize(200, 200).
                    centerCrop().
                    into(imagePhoto);

            if (selectedImage != null) {
                userPhotoUri = selectedImage.toString();

                Log.d("userPhotoUri",userPhotoUri);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_user, menu);

        menu.removeItem(R.id.action_delete_user);
        // добавление в меню текста с картинкой
        menu.add(0, R.id.action_delete_user, 3, menuIconWithText(getResources().getDrawable(R.drawable.ic_delete_blue_24dp), getResources().getString(R.string.delete_user)));

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
                // Если стрелка обратно
                if (goBackArraw) {
                    // Если не было изменений
                    if (!userHasChanged) {
                        Intent intent = new Intent(UserActivity.this, DiseasesActivity.class);
                        intent.putExtra("Title", textForUserActivityTitle);
                        intent.putExtra("birthDate", textForUserActivitybirthDate);
                        startActivity(intent);
                        return true;
                    }

                    // Если были изменения
                    Toast.makeText(this, "User Has Changed", Toast.LENGTH_LONG).show();

                    DialogInterface.OnClickListener discardButtonClickListener =
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Intent intent = new Intent(UserActivity.this, DiseasesActivity.class);
                                    intent.putExtra("Title", textForUserActivityTitle);
                                    intent.putExtra("birthDate", textForUserActivitybirthDate);
                                    startActivity(intent);
                                }
                            };

                    boolean toUsers = goBackArraw;
                    showUnsavedChangesDialog(discardButtonClickListener, !toUsers);
                    return true;
                }
                // если вместо стрелки обратно показывает "группа пользователей"
                else {
                    // Если не было изменений
                    if (!userHasChanged) {
                        Intent intent = new Intent(UserActivity.this, UsersActivity.class);
                        startActivity(intent);
                        return true;
                    }
                    // Если были изменения
                    Toast.makeText(this, "User Has Changed", Toast.LENGTH_LONG).show();

                    DialogInterface.OnClickListener discardButtonClickListener =
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Intent intent = new Intent(UserActivity.this, UsersActivity.class);
                                    startActivity(intent);
                                }
                            };

                    boolean toUsers = !goBackArraw;
                    showUnsavedChangesDialog(discardButtonClickListener, toUsers);
                    return true;
                }

            case R.id.action_save_user:
                saveUser();
                Intent intent = new Intent(UserActivity.this, DiseasesActivity.class);
                intent.putExtra("Title", textForTitle);
                intent.putExtra("birthDate", textForBirthDate);
                startActivity(intent);
                return true;

            case R.id.action_edit_user:
                Toast.makeText(this, "Edit", Toast.LENGTH_LONG).show();
                editTextName.setEnabled(true);
                editTextDate.setEnabled(true);
                imagePhoto.setClickable(true);
                editUser = false;
                goBackArraw = true;

                invalidateOptionsMenu();
                return true;

            case R.id.action_delete_user:
                deleteUser();
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

        showUnsavedChangesDialog(discardButtonClickListener, !goBackArraw);
    }

    private void saveUser() {
        //TODO реализовать сохранение пользователя в базу
        textForTitle = editTextName != null ? editTextName.getText().toString() : getResources().getString(R.string.txt_no_title);
        textForBirthDate = editTextDate != null ? editTextDate.getText().toString() : getResources().getString(R.string.txt_no_title);

        Toast.makeText(this, "User Saved", Toast.LENGTH_LONG).show();
    }

    private void deleteUser() {
        //TODO реализовать удаление пользователя из базы

        Toast.makeText(this, "Delete", Toast.LENGTH_LONG).show();
    }

    // Диалог "сохранить или выйти без сохранения"
    private void showUnsavedChangesDialog(DialogInterface.OnClickListener discardButtonClickListener, final Boolean toUsers) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.save, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                Toast.makeText(UserActivity.this, "User Saved", Toast.LENGTH_LONG).show();

                // если возвращаемся в окно UsersActivity
                if (toUsers) {
                    saveUser();

                    Intent intent = new Intent(UserActivity.this, UsersActivity.class);
                    startActivity(intent);
                }
                // если возвращаемся в окно DiseasesActivity
                else {
                    saveUser();

                    Intent intent = new Intent(UserActivity.this, DiseasesActivity.class);
                    intent.putExtra("Title", textForTitle);
                    intent.putExtra("birthDate", textForBirthDate);
                    startActivity(intent);
                }

                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}
