package com.gmail.krbashianrafael.medpunkt;


import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class WhenAndWhatDieseasActivity extends AppCompatActivity implements View.OnClickListener  {

    private static boolean editDieseas = true;
    private static String userName, editDieseasText, txtDieseasDateText;

    Menu menu;
    EditText editDieseasName;
    TextView txt_when_and_what_treatment_add, textDiseasSaveOrEdit, txt_date_of_diseas, txt_diseas;
    ImageButton imageButtonCalendarDiseas;
    FloatingActionButton fabAddTreatment, fabDiseasSaveOrEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_when_and_what_dieseas);

        Intent intent = getIntent();
        editDieseas = intent.getBooleanExtra("editDieseas", true);

        if (intent.hasExtra("userName")){
            userName = intent.getStringExtra("userName");
        }

        editDieseasName = findViewById(R.id.edit_diseas_name);
        txt_when_and_what_treatment_add = findViewById(R.id.txt_when_and_what_treatment_add);
        txt_date_of_diseas = findViewById(R.id.txt_date);
        txt_diseas = findViewById(R.id.txt_diseas);

        imageButtonCalendarDiseas = (ImageButton) findViewById(R.id.imageButtonCalendarDiseas);
        imageButtonCalendarDiseas.setOnClickListener(this);

        fabAddTreatment = (FloatingActionButton) findViewById(R.id.fabAddTreatment);
        fabAddTreatment.setOnClickListener(this);

        textDiseasSaveOrEdit = (TextView) findViewById(R.id.textDiseasSaveOrEdit);
        fabDiseasSaveOrEdit = (FloatingActionButton) findViewById(R.id.fabDiseasSaveOrEdit);
        fabDiseasSaveOrEdit.setOnClickListener(this);

        String whenAndWhatDiseases = getResources().getString(R.string.when_and_what_diseases);
        setTitle(userName + " / " + whenAndWhatDiseases);

        if (editDieseas) {
            fabDiseasSaveOrEdit.setImageResource(R.drawable.ic_action_edit);
            textDiseasSaveOrEdit.setText(R.string.user_edit);

            txt_diseas.setText(R.string.diseas_name);

            fabAddTreatment.setVisibility(View.VISIBLE);
            txt_when_and_what_treatment_add.setVisibility(View.VISIBLE);

            //TODO имя брать из интента
            setTitle(userName + " / " + editDieseasText);
            txt_date_of_diseas.setText(txtDieseasDateText);
            editDieseasName.setText(editDieseasText);

            editOrNot(!editDieseas);
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

        if (!editDieseas){
            MenuItem menuItem = menu.getItem(0);
            menuItem.setIcon(R.drawable.users50x50);
            menuItem.setTitle(R.string.action_home);
        }
        else {
            MenuItem menuItem = menu.getItem(0);
            menuItem.setIcon(R.drawable.recyclebin);
            menuItem.setTitle(R.string.action_delete);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_delete_or_home) {

            if (item.getTitle().equals(getResources().getString(R.string.action_delete))){
                Toast.makeText(this,"Пользователь будет удален",Toast.LENGTH_LONG).show();
            }
            else {
                Toast.makeText(this,"Вы перейдете на начальный экран",Toast.LENGTH_LONG).show();
            }

            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fabDiseasSaveOrEdit:
                if (!editDieseas) {
                    editDieseasText = String.valueOf(editDieseasName.getText());
                    setTitle(editDieseasText);

                    txtDieseasDateText = String.valueOf(txt_date_of_diseas.getText());
                    txt_diseas.setText(R.string.diseas_name);

                    fabDiseasSaveOrEdit.setImageResource(R.drawable.ic_action_edit);
                    textDiseasSaveOrEdit.setText(R.string.user_edit);

                    fabAddTreatment.setVisibility(View.VISIBLE);
                    txt_when_and_what_treatment_add.setVisibility(View.VISIBLE);

                    MenuItem menuItem = menu.getItem(0);
                    menuItem.setIcon(R.drawable.recyclebin);
                    menuItem.setTitle(R.string.action_delete);

                    setTitle(userName + " / " + editDieseasText);

                    editOrNot(editDieseas);

                    editDieseas = true;

                } else {
                    fabDiseasSaveOrEdit.setImageResource(R.drawable.ic_action_accept);
                    textDiseasSaveOrEdit.setText(R.string.user_save);
                    txt_diseas.setText(R.string.diseas);

                    fabAddTreatment.setVisibility(View.INVISIBLE);
                    txt_when_and_what_treatment_add.setVisibility(View.INVISIBLE);

                    MenuItem menuItem = menu.getItem(0);
                    menuItem.setIcon(R.drawable.users50x50);
                    menuItem.setTitle(R.string.action_home);

                    editOrNot(editDieseas);

                    editDieseas = false;
                }

                return;

            case R.id.fabAddTreatment:
                Intent intent = new Intent(WhenAndWhatDieseasActivity.this, WhenAndWhatTreatmentActivity.class);
                intent.putExtra("editTreatment",false);
                intent.putExtra("userNameAndDiseas",getTitle().toString());
                startActivity(intent);
                return;

            case R.id.imageButtonCalendarDiseas:
                DatePickerFragment newFragment = new DatePickerFragment();
                newFragment.show(getSupportFragmentManager(), "datePicker");
                return;
            default:

                return;
        }

    }

    private void editOrNot(boolean edit) {
        editDieseasName.setEnabled(edit);
        imageButtonCalendarDiseas.setClickable(edit);
    }
}
