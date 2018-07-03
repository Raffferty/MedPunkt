package com.gmail.krbashianrafael.medpunkt;


import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.TextView;


public class HomeActivity extends AppCompatActivity  {

    private static final String PREFS_NAME = "PREFS";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Intent intent = getIntent();
        boolean fromUsers = intent.getBooleanExtra("fromUsers", false);


        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // иконка видна, но не нажимается
            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setTitle(R.string.title_activity_home);

            // для версий начиная с Nougat	7.1	API level 25 исползуются круглые икнонки
            if (Build.VERSION.SDK_INT >= 25){
                actionBar.setIcon(R.drawable.med_round);
            }
            else {
                actionBar.setIcon(R.drawable.med_rect);
            }
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
                if (isChecked) {
                    // если отмечено БОЛЬШЕ НЕ ПОКАЗЫВАТЬ, то окно приветсвия будет пропускаться
                    prefsEditor.putBoolean("showGreeting", false);

                    //TODO контроль количества пользователей
                    //если пользователей будет 0, то сразу откроется окно ДОБАВИТЬ ПОЛЬЗОВАТЕЛЯ

                    //если пользователей будет 1, а в ЧЕМ БОЛЕЛ болезней будет 0, то откроется окно ДОБАВИТЬ ЧЕМ БОЛЕЛ

                    // если пользователей будет 1, а в ЧЕМ БОЛЕЛ болезней будет больше, чем 0,
                    // ТО Сразу откроется окно ЧЕМ БОЛЕЛ

                    //если пользователей будет больше, чем 1, то откроется окно ПОЛЬЗОВАТЕЛИ
                    prefsEditor.apply();
                } else {
                    prefsEditor.putBoolean("showGreeting", true);
                    prefsEditor.apply();
                }
            }
        });

        if (!prefs.getBoolean("showGreeting", true) && !fromUsers) {
            Intent intentToUsers = new Intent(HomeActivity.this, UsersActivity.class);
            startActivity(intentToUsers);
        }else if (!prefs.getBoolean("showGreeting", true) && fromUsers){
            checkBox.setChecked(true);
        }

        FrameLayout enter = findViewById(R.id.enter);
        enter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, UsersActivity.class);
                startActivity(intent);

                finish();
            }
        });
    }

    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_enter) {
            Intent intent = new Intent(HomeActivity.this, UsersActivity.class);
            startActivity(intent);

            finish();

            return true;
        }
        return super.onOptionsItemSelected(item);
    }*/

}
