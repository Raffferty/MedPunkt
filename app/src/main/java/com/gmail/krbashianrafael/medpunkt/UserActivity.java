package com.gmail.krbashianrafael.medpunkt;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;


public class UserActivity extends AppCompatActivity {
    private boolean editUser, goBack, userHasChanged = false;

    private String textForUserActivityTitle, birthDate;

    private EditText editTextName, editTextDate;

    private ImageView imagePhoto;

    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            userHasChanged = true;
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        Intent intent = getIntent();
        editUser = intent.getBooleanExtra("editUser", false);
        goBack = editUser;

        textForUserActivityTitle = intent.getStringExtra("Title");
        birthDate = intent.getStringExtra("birthDate");

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN | WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        imagePhoto = findViewById(R.id.image_photo);
        imagePhoto.setOnTouchListener(mTouchListener);
        imagePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO загрузить фото
                Toast.makeText(UserActivity.this,"Load Photo",Toast.LENGTH_LONG).show();


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
            if (goBack){
                actionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp);
            }
            else {
                actionBar.setHomeAsUpIndicator(R.drawable.ic_group_white_24dp);
            }

            if (textForUserActivityTitle!=null){
                actionBar.setTitle(textForUserActivityTitle);
                editTextName.setText(textForUserActivityTitle);
            }
        }

        if (birthDate!=null){
            editTextDate.setText(birthDate);
        }

        if(editUser){
            editTextName.setEnabled(false);
            editTextDate.setEnabled(false);
            imagePhoto.setClickable(false);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_user, menu);

        menu.removeItem(R.id.action_delete_user);
        menu.add(0, R.id.action_delete_user, 3, menuIconWithText(getResources().getDrawable(R.drawable.ic_delete_blue_24dp), getResources().getString(R.string.delete_user)));

        return true;
    }

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
                if (goBack){
                    // Если не было изменений
                    if (!userHasChanged) {
                        Intent intent = new Intent(UserActivity.this, DiseasesActivity.class);
                        intent.putExtra("Title",textForUserActivityTitle);
                        intent.putExtra("birthDate",birthDate);
                        startActivity(intent);

                        return true;
                    }

                    // Если были изменения
                    Toast.makeText(this,"User Has Changed",Toast.LENGTH_LONG).show();

                    DialogInterface.OnClickListener discardButtonClickListener =
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Intent intent = new Intent(UserActivity.this, DiseasesActivity.class);
                                    intent.putExtra("Title",textForUserActivityTitle);
                                    intent.putExtra("birthDate",birthDate);
                                    startActivity(intent);
                                }
                            };

                    boolean toUsers = false;

                    showUnsavedChangesDialog(discardButtonClickListener, toUsers);
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
                    Toast.makeText(this,"User Has Changed",Toast.LENGTH_LONG).show();

                    DialogInterface.OnClickListener discardButtonClickListener =
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Intent intent = new Intent(UserActivity.this, UsersActivity.class);
                                    startActivity(intent);
                                }
                            };

                    boolean toUsers = true;

                    showUnsavedChangesDialog(discardButtonClickListener, toUsers);
                    return true;

                }

            case R.id.action_save_user:
                //TODO реализовать сохранение пользователя в базу

                Toast.makeText(this,"User Saved",Toast.LENGTH_LONG).show();

                Intent intent = new Intent(UserActivity.this, DiseasesActivity.class);
                String textForTitle = editTextName!=null ? editTextName.getText().toString(): getResources().getString(R.string.txt_no_title);
                String textForBirthDate = editTextDate!=null ? editTextDate.getText().toString(): getResources().getString(R.string.txt_no_title);

                intent.putExtra("Title",textForTitle);
                intent.putExtra("birthDate",textForBirthDate);
                startActivity(intent);
                return true;
            case R.id.action_edit_user:
                Toast.makeText(this,"Edit",Toast.LENGTH_LONG).show();

                editTextName.setEnabled(true);
                editTextDate.setEnabled(true);
                imagePhoto.setClickable(true);

                editUser = false;
                goBack = true;

                invalidateOptionsMenu();

                return true;

            case R.id.action_delete_user:
                //TODO реализовать удаление пользователя из базы
                Toast.makeText(this,"Delete",Toast.LENGTH_LONG).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void showUnsavedChangesDialog(DialogInterface.OnClickListener discardButtonClickListener, final boolean toUsers) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage(R.string.unsaved_changes_dialog_msg);

        builder.setPositiveButton(R.string.discard, discardButtonClickListener);

        builder.setNegativeButton(R.string.save, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                Toast.makeText(UserActivity.this,"User Saved",Toast.LENGTH_LONG).show();

                if (toUsers){
                    //TODO реализовать сохранение пользователя в базу
                    Intent intent = new Intent(UserActivity.this, UsersActivity.class);
                    startActivity(intent);
                }
                else {
                    //TODO реализовать сохранение пользователя в базу
                    Intent intent = new Intent(UserActivity.this, DiseasesActivity.class);
                    String textForTitle = editTextName!=null ? editTextName.getText().toString(): getResources().getString(R.string.txt_no_title);
                    String textForBirthDate = editTextDate!=null ? editTextDate.getText().toString(): getResources().getString(R.string.txt_no_title);

                    intent.putExtra("Title",textForTitle);
                    intent.putExtra("birthDate",textForBirthDate);
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
