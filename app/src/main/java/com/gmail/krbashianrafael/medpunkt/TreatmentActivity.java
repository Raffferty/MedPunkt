package com.gmail.krbashianrafael.medpunkt;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.util.Linkify;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TreatmentActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "PREFS";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_treatment);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_36dp);
        }

        final EditText editText1 = findViewById(R.id.EditText1);

        final EditText editText2 = findViewById(R.id.EditText2);

        final SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        final SharedPreferences.Editor prefsEditor = prefs.edit();

        Button buttonToLink = findViewById(R.id.buttonToLink);

        buttonToLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                prefsEditor.putString("testText", editText1.getText().toString());
                prefsEditor.apply();

                editText2.setText(prefs.getString("testText","yyy"));

                Pattern p = Pattern.compile("\\b_._ [A-Za-z0-9]*\\b");

                Linkify.TransformFilter dataTransformer = new Linkify.TransformFilter() {
                    @Override
                    public String transformUrl(Matcher match, String url) {
                        return "+/data/user/0/com.gmail.krbashianrafael.medpunkt/files/users_photos/Image-1.jpg";
                    }
                };

                Linkify.addLinks(editText2, p,
                        "com.gmail.krbashianrafael.medpunkt://treatment/",
                        null, dataTransformer);
            }
        });


    }

    // SpannableString с картикной для элеменов меню
   /* private CharSequence menuIconWithText(Drawable r, String title) {
        r.setBounds(0, 0, r.getIntrinsicWidth(), r.getIntrinsicHeight());
        SpannableString sb = new SpannableString("    " + title);
        ImageSpan imageSpan = new ImageSpan(r, ImageSpan.ALIGN_BOTTOM);
        sb.setSpan(imageSpan, 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        return sb;
    }*/

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                Intent intent = new Intent(TreatmentActivity.this, DiseasesActivity.class);
                startActivity(intent);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
