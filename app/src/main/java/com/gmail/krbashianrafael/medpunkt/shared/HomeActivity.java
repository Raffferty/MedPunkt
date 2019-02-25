package com.gmail.krbashianrafael.medpunkt.shared;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.gmail.krbashianrafael.medpunkt.R;
import com.gmail.krbashianrafael.medpunkt.phone.UsersActivity;
import com.gmail.krbashianrafael.medpunkt.tablet.TabletMainActivity;
import com.google.android.gms.ads.MobileAds;

import java.io.File;

public class HomeActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "PREFS";
    public static boolean iAmDoctor = false;
    public static boolean isTablet = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Initialize the Mobile Ads SDK.
        // инициализируем MobileAds с id приложения MedPunkt
        // мой app_id = ca-app-pub-5926695077684771~8182565017
        MobileAds.initialize(this, getResources().getString(R.string.app_id));


        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // иконка видна, но не нажимается
            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setTitle("\t" + getResources().getString(R.string.title_activity_home));

            // для версий начиная с Nougat	7.1	API level 25 исползуются круглые икнонки
            if (Build.VERSION.SDK_INT >= 25) {
                actionBar.setIcon(R.drawable.med_round);
            } else {
                actionBar.setIcon(R.drawable.med_rect_42dp);
            }
        }

        TextView greetingTextView = findViewById(R.id.txt_greeting);

        String greetingStartText = getText(R.string.greeting_start).toString();
        String greetingMidText = getText(R.string.greeting_mid).toString();
        String greetingEndText = getText(R.string.greeting_end).toString();

        Spannable spannableStartText = new SpannableString(greetingStartText);
        Spannable spannableEndText = new SpannableString(greetingEndText);

        spannableStartText.setSpan(new ForegroundColorSpan(Color.RED), 0, spannableStartText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableEndText.setSpan(new ForegroundColorSpan(Color.RED), 0, spannableEndText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        greetingTextView.setText(TextUtils.concat(spannableStartText, "\n", greetingMidText, " ", spannableEndText), TextView.BufferType.SPANNABLE);

        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        final SharedPreferences.Editor prefsEditor = prefs.edit();

        // узнаем Планшет это или нет
        if (!prefs.contains("isTablet")) {

            DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
            float dpHeight = displayMetrics.heightPixels / displayMetrics.density;
            float dpWidth = displayMetrics.widthPixels / displayMetrics.density;

            float maxSize;
            float minSize;

            if (dpHeight >= dpWidth) {
                maxSize = dpHeight;
                minSize = dpWidth;
            } else {
                maxSize = dpWidth;
                minSize = dpHeight;
            }

            // при размерах 600 на 960, устройстово будет считаться планшетом
            if (maxSize >= 960 && minSize >= 600) {
                isTablet = true;
                prefsEditor.putBoolean("isTablet", true);
            } else {
                prefsEditor.putBoolean("isTablet", false);
            }

            prefsEditor.apply();

        } else {
            isTablet = prefs.getBoolean("isTablet", false);
        }

        final CheckBox checkBoxIamDoctor = findViewById(R.id.checkbox_doctor);

        iAmDoctor = prefs.getBoolean("iAmDoctor", false);
        if (iAmDoctor) {
            checkBoxIamDoctor.setChecked(true);
        }

        checkBoxIamDoctor.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // если отмечено
                    iAmDoctor = true;
                    prefsEditor.putBoolean("iAmDoctor", true);
                    prefsEditor.apply();
                } else {
                    iAmDoctor = false;
                    prefsEditor.putBoolean("iAmDoctor", false);
                    prefsEditor.apply();
                }
            }
        });

        final CheckBox checkBoxShowGreeting = findViewById(R.id.checkbox_show_greeting);

        // при первом заходе проверяем была ли ранее выставлена галочка "Не показывать больше"
        // и если была, то идем в UsersActivity, при этом ставим галочку
        if (!prefs.getBoolean("showGreeting", true)) {

            checkBoxShowGreeting.setChecked(true);

            if (!isTablet) {
                Intent intentToUsers = new Intent(HomeActivity.this, UsersActivity.class);
                startActivity(intentToUsers);
            } else {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                Intent intentToTablet = new Intent(HomeActivity.this, TabletMainActivity.class);
                startActivity(intentToTablet);
            }
        } else {
            if (!isTablet) {
                UsersActivity.onResumeCounter = 1;
            } else {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            }
        }

        // checkBox.setOnCheckedChangeListener инициализируем после проверки на галочку (вверху)
        // чтоб лишний раз не записывать prefsEditor.putBoolean
        checkBoxShowGreeting.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
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
        final boolean finalIsTablet = isTablet;
        enter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!finalIsTablet) {
                    Intent intentToUsers = new Intent(HomeActivity.this, UsersActivity.class);
                    startActivity(intentToUsers);
                } else {
                    Intent intentToTabalet = new Intent(HomeActivity.this, TabletMainActivity.class);
                    startActivity(intentToTabalet);
                }
            }
        });

        // при первой загрузке проверяем есть ли висячие файлы фотографий,
        // которые должны были быть удалены, но не удалились
        // и пытаемся их снова удалить в doInBackground класса CleanNotDeletedFilesAsyncTask
        String notDeletedFilesPaths = prefs.getString("notDeletedFilesPaths", null);

        if (notDeletedFilesPaths != null && notDeletedFilesPaths.length() != 0) {
            new CleanNotDeletedFilesAsyncTask(notDeletedFilesPaths).execute(getApplicationContext());
        }
    }

    private static class CleanNotDeletedFilesAsyncTask extends AsyncTask<Context, Void, Boolean> {

        private final String mNotDeletedFilesPaths;

        CleanNotDeletedFilesAsyncTask(String notDeletedFilesPaths) {
            mNotDeletedFilesPaths = notDeletedFilesPaths;
        }

        @Override
        protected Boolean doInBackground(Context... contexts) {


            if (contexts == null || contexts[0] == null || mNotDeletedFilesPaths == null) {
                return false;
            }

            Context mContext = contexts[0];

            // получаем SharedPreferences, чтоб писать в файл "PREFS"
            SharedPreferences mPrefs = mContext.getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
            final SharedPreferences.Editor mPrefsEditor = mPrefs.edit();

            String[] splitedFilesPaths = mNotDeletedFilesPaths.split(",");

            StringBuilder sb = new StringBuilder();

            for (String fPath : splitedFilesPaths) {

                File toBeDeletedFile = new File(fPath);
                if (toBeDeletedFile.exists()) {
                    if (!toBeDeletedFile.delete()) {
                        sb.append(fPath).append(",");
                    }
                }
            }

            if (sb.length() > 0) {
                sb.deleteCharAt(sb.length() - 1);

                mPrefsEditor.putString("notDeletedFilesPaths", sb.toString());
                mPrefsEditor.apply();

                return false;
            }

            // если после повторной попытки удаления не осталось висячих файлов sb = 0,
            // то очищаем поле notDeletedFilesPaths
            mPrefsEditor.putString("notDeletedFilesPaths", null);
            mPrefsEditor.apply();

            return true;
        }
    }
}
