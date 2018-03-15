package com.gmail.krbashianrafael.medpunkt;


import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;


public class HomeActivity extends AppCompatActivity {

    public static final String PREFS_NAME = "PREFS";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);


        ActionBar actionBar =  getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.hospital);
        }

        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        final SharedPreferences.Editor prefsEditor = prefs.edit();

        TextView greetingTextView = findViewById(R.id.txt_greeting);

        String greetingText = getText(R.string.greeting).toString();
        Spannable spannable = new SpannableString(greetingText);
        spannable.setSpan(new ForegroundColorSpan(Color.RED), 11, 19, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        greetingTextView.setText(spannable, TextView.BufferType.SPANNABLE);

        final CheckBox checkBox = findViewById(R.id.checkbox_show_greeting);

        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    // если отмечено БОЛЬШЕ НЕ ПОКАЗЫВАТЬ, то окно приветсвия будет пропускаться
                    prefsEditor.putBoolean("showGreeting", false);

                    //TODO контроль количества пользователей
                    //если пользователей будет 0, то сразу откроется окно ДОБАВИТЬ ПОЛЬЗОВАТЕЛЯ

                    //если пользователей будет 1, а в ЧЕМ БОЛЕЛ болезней будет 0, то откроется окно ДОБАВИТЬ ЧЕМ БОЛЕЛ

                    // если пользователей будет 1, а в ЧЕМ БОЛЕЛ болезней будет больше, чем 0,
                    // ТО Сразу откроется окно ЧЕМ БОЛЕЛ

                    //если пользователей будет больше, чем 1, то откроется окно ПОЛЬЗОВАТЕЛИ
                    prefsEditor.apply();
                }
                else {
                    prefsEditor.putBoolean("showGreeting", true);
                    prefsEditor.apply();
                }            }
        });

        if (!prefs.getBoolean("showGreeting",true)){
            Intent intent = new Intent(HomeActivity.this, UsersActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_enter) {
            Intent intent = new Intent(HomeActivity.this, UsersActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
