package com.gmail.krbashianrafael.medpunkt;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;


public class UserActivity extends AppCompatActivity {
    private boolean editUser = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        Intent intent = getIntent();
        editUser = intent.getBooleanExtra("editUser", false);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN | WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_group_white_24dp);

        final myEditText editTextName = (myEditText) findViewById(R.id.editText_name);
        final EditText editTextDate = findViewById(R.id.editText_date);

        editTextDate.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {

                    //при переходе фокуса на editTextDate открывается клавиатура
                    View view = UserActivity.this.getCurrentFocus();
                    if (view != null) {
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    }

                    editTextDate.setCursorVisible(false);
                    editTextName.setCursorVisible(false);

                    DatePickerFragment newFragment = new DatePickerFragment();
                    newFragment.show(getSupportFragmentManager(), "datePicker");

                    editTextDate.clearFocus();
                }
            }
        });

        // кастомный класс myEditText реализовываем в себе performClick()
        // поэтому нет предупреждения от компилятора, что нет реализации performClick()
        editTextName.setOnTouchListener(new myEditText(UserActivity.this){
            public boolean onTouch(View v, MotionEvent event) {
                editTextName.setCursorVisible(true);
                editTextName.setFocusableInTouchMode(true);

                //при касании к editTextName открывается клавиатура
                View view = UserActivity.this.getCurrentFocus();
                    if (view != null) {
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.showSoftInput(view, 0);
                    }
                return true;
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_user, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        if (editUser) {
            MenuItem menuItemSave = menu.getItem(0);
            menuItemSave.setVisible(false);

        } else {
            MenuItem menuItemEdit = menu.getItem(1);
            menuItemEdit.setVisible(false);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                Intent intent = new Intent(UserActivity.this, UsersActivity.class);
                startActivity(intent);
                return true;
            case R.id.action_save_user:
                Toast.makeText(this,"Save",Toast.LENGTH_LONG).show();

                return true;
            case R.id.action_edit_user:
                Toast.makeText(this,"Edit",Toast.LENGTH_LONG).show();

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
