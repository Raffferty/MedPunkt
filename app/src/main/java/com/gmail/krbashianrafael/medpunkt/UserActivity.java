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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class UserActivity extends AppCompatActivity implements View.OnClickListener {

    private static boolean editUser = true;
    private static String editNameText, txtUserDateText;

    Menu menu;
    EditText editName;
    ImageView imagePhoto;
    TextView txt_when_and_what_diseasees_add, textUserSaveOrEdit, txt_birthdate, txt_name;
    ImageButton imageButtonCalendarBirthdate;
    FloatingActionButton fabAddDieseases, fabUserSaveOrEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        Intent intent = getIntent();
        editUser = intent.getBooleanExtra("editUser", true);

        editName = findViewById(R.id.edit_name);
        imagePhoto = findViewById(R.id.imagePhoto);
        txt_when_and_what_diseasees_add = findViewById(R.id.txt_when_and_what_diseases_add);
        txt_birthdate = findViewById(R.id.txt_date);
        txt_name = findViewById(R.id.txt_name);

        imageButtonCalendarBirthdate = (ImageButton) findViewById(R.id.imageButtonCalendarBirthdate);
        imageButtonCalendarBirthdate.setOnClickListener(this);

        fabAddDieseases = (FloatingActionButton) findViewById(R.id.fabAddDiseases);
        fabAddDieseases.setOnClickListener(this);

        textUserSaveOrEdit = (TextView) findViewById(R.id.textUserSaveOrEdit);
        fabUserSaveOrEdit = (FloatingActionButton) findViewById(R.id.fabUserSaveOrEdit);
        fabUserSaveOrEdit.setOnClickListener(this);

        if (editUser) {
            fabUserSaveOrEdit.setImageResource(R.drawable.ic_action_edit);
            textUserSaveOrEdit.setText(R.string.user_edit);
            txt_name.setText(R.string.user_name);

            fabAddDieseases.setVisibility(View.VISIBLE);
            txt_when_and_what_diseasees_add.setVisibility(View.VISIBLE);

            //TODO имя брать из интента
            setTitle(editNameText);
            txt_birthdate.setText(txtUserDateText);
            editName.setText(editNameText);

            editOrNot(!editUser);
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

        if (!editUser){
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
            case R.id.fabUserSaveOrEdit:
                if (!editUser) {
                    editNameText = String.valueOf(editName.getText());
                    setTitle(editNameText);

                    txtUserDateText = String.valueOf(txt_birthdate.getText());
                    txt_name.setText(R.string.user_name);

                    fabUserSaveOrEdit.setImageResource(R.drawable.ic_action_edit);
                    textUserSaveOrEdit.setText(R.string.user_edit);

                    fabAddDieseases.setVisibility(View.VISIBLE);
                    txt_when_and_what_diseasees_add.setVisibility(View.VISIBLE);

                    MenuItem menuItem = menu.getItem(0);
                    menuItem.setIcon(R.drawable.recyclebin);
                    menuItem.setTitle(R.string.action_delete);

                    editOrNot(editUser);

                    editUser = true;

                } else {
                    fabUserSaveOrEdit.setImageResource(R.drawable.ic_action_accept);
                    textUserSaveOrEdit.setText(R.string.user_save);
                    txt_name.setText(R.string.name);

                    fabAddDieseases.setVisibility(View.INVISIBLE);
                    txt_when_and_what_diseasees_add.setVisibility(View.INVISIBLE);

                    MenuItem menuItem = menu.getItem(0);
                    menuItem.setIcon(R.drawable.users50x50);
                    menuItem.setTitle(R.string.action_home);

                    editOrNot(editUser);

                    editUser = false;
                }

                return;

            case R.id.fabAddDiseases:
                Intent intent = new Intent(UserActivity.this, WhenAndWhatDieseasActivity.class);
                intent.putExtra("editDieseas",false);
                intent.putExtra("userName",editNameText);
                startActivity(intent);
                return;

            case R.id.imageButtonCalendarBirthdate:
                DatePickerFragment newFragment = new DatePickerFragment();
                newFragment.show(getSupportFragmentManager(), "datePicker");
                return;
            default:

                return;
        }
    }

    private void editOrNot(boolean edit) {
        editName.setEnabled(edit);
        imagePhoto.setClickable(edit);
        imageButtonCalendarBirthdate.setClickable(edit);
    }
}
