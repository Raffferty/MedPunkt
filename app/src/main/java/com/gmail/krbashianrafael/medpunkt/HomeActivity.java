package com.gmail.krbashianrafael.medpunkt;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.io.File;


public class HomeActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "PREFS";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // иконка видна, но не нажимается
            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setTitle(R.string.title_activity_home);

            // для версий начиная с Nougat	7.1	API level 25 исползуются круглые икнонки
            if (Build.VERSION.SDK_INT >= 25) {
                actionBar.setIcon(R.drawable.med_round);
            } else {
                actionBar.setIcon(R.drawable.med_rect);
            }
        }

        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        final SharedPreferences.Editor prefsEditor = prefs.edit();

        TextView greetingTextView = findViewById(R.id.txt_greeting);

        String greetingText = getText(R.string.greeting).toString();
        Spannable spannable = new SpannableString(greetingText);
        spannable.setSpan(new ForegroundColorSpan(Color.RED), 0, 8, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        greetingTextView.setText(spannable, TextView.BufferType.SPANNABLE);

        final CheckBox checkBox = findViewById(R.id.checkbox_show_greeting);

        // при первом заходе проверяем была ли ранее выставлена галочка "Не показывать больше"
        // и если была, то идем в UsersActivity, при этом ставим галочку
        if (!prefs.getBoolean("showGreeting", true)) {

            checkBox.setChecked(true);

            Intent intentToUsers = new Intent(HomeActivity.this, UsersActivity.class);
            startActivity(intentToUsers);
        }

        // checkBox.setOnCheckedChangeListener инициализируем после проверки на галочку (вверху)
        // чтоб лишний раз не записывать prefsEditor.putBoolean
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // если отмечено БОЛЬШЕ НЕ ПОКАЗЫВАТЬ, то окно приветсвия будет пропускаться
                    prefsEditor.putBoolean("showGreeting", false);
                    prefsEditor.apply();
                } else {
                    prefsEditor.putBoolean("showGreeting", true);
                    prefsEditor.apply();
                }
            }
        });

        FrameLayout enter = findViewById(R.id.enter);
        enter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentToUsers = new Intent(HomeActivity.this, UsersActivity.class);
                startActivity(intentToUsers);
            }
        });

        // при первой загрузке проверяем есть ли висячие файлы фотографий,
        // которые должны были быть удалены, но не удалились
        // и пытаемся их снова удалить в doInBackground класса CleanNotDeletedFilesAsyncTask
        String notDeletedFilesPathes = prefs.getString("notDeletedFilesPathes", null);

        if (notDeletedFilesPathes != null && notDeletedFilesPathes.length() != 0) {
            new CleanNotDeletedFilesAsyncTask(notDeletedFilesPathes).execute(getApplicationContext());
        }
    }

    private static class CleanNotDeletedFilesAsyncTask extends AsyncTask<Context, Void, Boolean> {

        private final String mNotDeletedFilesPathes;

        CleanNotDeletedFilesAsyncTask(String notDeletedFilesPathes) {
            mNotDeletedFilesPathes = notDeletedFilesPathes;
        }

        @Override
        protected Boolean doInBackground(Context... contexts) {
            if (contexts == null || contexts[0] == null || mNotDeletedFilesPathes == null) {
                return false;
            }

            Context mContext = contexts[0];

            // получаем SharedPreferences, чтоб писать в файл "PREFS"
            SharedPreferences mPrefs = mContext.getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
            final SharedPreferences.Editor mPrefsEditor = mPrefs.edit();

            String[] splitedFilesPathes = mNotDeletedFilesPathes.split(",");

            StringBuilder sb = new StringBuilder();

            for (String fPath : splitedFilesPathes) {
                File toBeDeletedFile = new File(fPath);
                if (toBeDeletedFile.exists()) {
                    if (!toBeDeletedFile.delete()) {
                        sb.append(fPath).append(",");
                    }
                }
            }

            if (sb.length() > 0) {
                sb.deleteCharAt(sb.length() - 1);

                mPrefsEditor.putString("notDeletedFilesPathes", sb.toString());
                mPrefsEditor.apply();

                return false;
            }

            // если после повторной попытки удаления не осталось висячих файлов sb = 0,
            // то очищаем поле notDeletedFilesPathes
            mPrefsEditor.putString("notDeletedFilesPathes", null);
            mPrefsEditor.apply();

            return true;
        }

        @Override
        protected void onPostExecute(Boolean filesDeleted) {
            super.onPostExecute(filesDeleted);
            if (!filesDeleted) {
                Log.d("mOnLoadFinished", "Files NOT Re-Deleted");
            }
        }
    }
}
