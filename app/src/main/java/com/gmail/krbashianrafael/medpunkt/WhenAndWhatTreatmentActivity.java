package com.gmail.krbashianrafael.medpunkt;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

public class WhenAndWhatTreatmentActivity extends AppCompatActivity implements View.OnClickListener {

    private static boolean editTreatment = true;
    private static boolean lnrTreatmentHeight = false;
    private static int lnrTreatmentPx;
    private static String userNameAndDiseas, editTreatmentText, txtTreatmentDateText;

    Menu menu;
    EditText editTextTreatment;
    TextView txt_treatment_photo_add, textTreatmentSaveOrEdit, txt_date_of_treatment;
    ImageButton imageButtonCalendarTreatment;
    FloatingActionButton fabAddTreatmentPhoto, fabTreatmentSaveOrEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_when_and_what_treatment);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN | WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        Intent intent = getIntent();
        editTreatment = intent.getBooleanExtra("editTreatment", true);

        if (intent.hasExtra("userNameAndDiseas")) {
            userNameAndDiseas = intent.getStringExtra("userNameAndDiseas");
        }

        String treat = getResources().getString(R.string.treat);
        setTitle(userNameAndDiseas + " / " + treat);

        if (!lnrTreatmentHeight){
            Resources r = getResources();
            int screenHeightDp = r.getConfiguration().screenHeightDp;
            int lnrTreatmentDp = screenHeightDp>500 ? screenHeightDp/3 :screenHeightDp/4;
            lnrTreatmentPx = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, lnrTreatmentDp, r.getDisplayMetrics());

            lnrTreatmentHeight = true;
        }

        LinearLayout lnrTreatment = findViewById(R.id.lnr_treatment);
        LayoutParams lnrTreatmentParams = lnrTreatment.getLayoutParams();
        lnrTreatmentParams.height = lnrTreatmentPx;
        lnrTreatment.setLayoutParams(lnrTreatmentParams);

        editTextTreatment = findViewById(R.id.edit_treatment);

        txt_treatment_photo_add = findViewById(R.id.txt_treatment_photo_add);
        txt_date_of_treatment = findViewById(R.id.txt_date);

        imageButtonCalendarTreatment = (ImageButton) findViewById(R.id.imageButtonCalendarTreatment);
        imageButtonCalendarTreatment.setOnClickListener(this);

        fabAddTreatmentPhoto = (FloatingActionButton) findViewById(R.id.fabAddTreatmentPhoto);
        fabAddTreatmentPhoto.setOnClickListener(this);

        textTreatmentSaveOrEdit = (TextView) findViewById(R.id.textTreatmentSaveOrEdit);
        fabTreatmentSaveOrEdit = (FloatingActionButton) findViewById(R.id.fabTreatmentSaveOrEdit);
        fabTreatmentSaveOrEdit.setOnClickListener(this);

        if (editTreatment) {
            fabTreatmentSaveOrEdit.setImageResource(R.drawable.ic_action_edit);
            textTreatmentSaveOrEdit.setText(R.string.user_edit);

            fabAddTreatmentPhoto.setVisibility(View.VISIBLE);
            txt_treatment_photo_add.setVisibility(View.VISIBLE);

            //TODO имя брать из интента
            setTitle(userNameAndDiseas + " / " + treat);
            txt_date_of_treatment.setText(txtTreatmentDateText);
            editTextTreatment.setText(editTreatmentText);

            editOrNot(!editTreatment);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        if (!editTreatment) {
            MenuItem menuItem = menu.getItem(0);
            menuItem.setIcon(R.drawable.users50x50);
            menuItem.setTitle(R.string.action_home);
        } else {
            MenuItem menuItem = menu.getItem(0);
            menuItem.setIcon(R.drawable.recyclebin);
            menuItem.setTitle(R.string.action_delete);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        /*if (id == R.id.action_delete_or_home) {
            if (item.getTitle().equals(getResources().getString(R.string.action_delete))) {
                Toast.makeText(this, "Пользователь будет удален", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Вы перейдете на начальный экран", Toast.LENGTH_LONG).show();
            }

            return true;
        }*/

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fabTreatmentSaveOrEdit:
                if (!editTreatment) {

                    View view = this.getCurrentFocus();
                    if (view != null) {
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    }

                    editTextTreatment.setSelection(0);
                    editTextTreatment.scrollTo(0, editTextTreatment.getTop());

                    editTreatmentText = String.valueOf(editTextTreatment.getText());

                    txtTreatmentDateText = String.valueOf(txt_date_of_treatment.getText());

                    fabTreatmentSaveOrEdit.setImageResource(R.drawable.ic_action_edit);
                    textTreatmentSaveOrEdit.setText(R.string.user_edit);

                    fabAddTreatmentPhoto.setVisibility(View.VISIBLE);
                    txt_treatment_photo_add.setVisibility(View.VISIBLE);

                    MenuItem menuItem = menu.getItem(0);
                    menuItem.setIcon(R.drawable.recyclebin);
                    menuItem.setTitle(R.string.action_delete);

                    editOrNot(editTreatment);

                    editTreatment = true;

                } else {
                    fabTreatmentSaveOrEdit.setImageResource(R.drawable.ic_action_accept);
                    textTreatmentSaveOrEdit.setText(R.string.user_save);

                    fabAddTreatmentPhoto.setVisibility(View.INVISIBLE);
                    txt_treatment_photo_add.setVisibility(View.INVISIBLE);

                    MenuItem menuItem = menu.getItem(0);
                    menuItem.setIcon(R.drawable.users50x50);
                    menuItem.setTitle(R.string.action_home);

                    editOrNot(editTreatment);

                    editTreatment = false;
                }

                return;

            case R.id.fabAddTreatmentPhoto:
                Intent intent = new Intent(WhenAndWhatTreatmentActivity.this, TreatmentPhotoActivity.class);
                intent.putExtra("editPhoto", false);
                intent.putExtra("userNameAndDiseas", userNameAndDiseas);
                startActivity(intent);
                return;

            case R.id.imageButtonCalendarTreatment:
                DatePickerFragment newFragment = new DatePickerFragment();
                newFragment.show(getSupportFragmentManager(), "datePicker");
                return;
            default:
                return;
        }
    }

    private void editOrNot(boolean edit) {
        if (!edit) {
            editTextTreatment.clearFocus();
            editTextTreatment.setFocusableInTouchMode(false);
            editTextTreatment.setFocusable(true);
        } else {
            editTextTreatment.setFocusableInTouchMode(true);
        }

        imageButtonCalendarTreatment.setClickable(edit);
    }
}
