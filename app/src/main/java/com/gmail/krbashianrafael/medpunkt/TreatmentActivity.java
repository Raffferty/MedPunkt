package com.gmail.krbashianrafael.medpunkt;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.util.Linkify;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

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

        final TextView textViewAddPhoto1 = findViewById(R.id.textViewAddPhoto1);
        final TextView textViewAddPhoto2 = findViewById(R.id.textViewAddPhoto2);

        final EditText editText1 = findViewById(R.id.EditText1);
        final EditText editText2 = findViewById(R.id.EditText2);

        editText1.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus){
                    textViewAddPhoto1.setVisibility(View.VISIBLE);
                }
                else {
                    textViewAddPhoto1.setVisibility(View.INVISIBLE);
                }
            }
        });

        editText2.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus){
                    textViewAddPhoto2.setVisibility(View.VISIBLE);
                }
                else {
                    textViewAddPhoto2.setVisibility(View.INVISIBLE);
                }
            }
        });

        int unicode = 0x1F4F7;
        final String unicodeString = new String(Character.toChars(unicode));

        //editText1.setText(unicodeString);
        editText1.setText(unicodeString + " Photo");
        /*Drawable drawable = (ColorDrawable)editText1.getBackground();
        editText1.getBackground().setColorFilter(drawable.getColor(), PorterDuff.Mode.SRC_IN);*/
        //editText1.setText(menuIconWithText(getResources().getDrawable(R.drawable.ic_delete_red_24dp), getResources().getString(R.string.delete_photo)));


        final SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        final SharedPreferences.Editor prefsEditor = prefs.edit();



        textViewAddPhoto1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                textViewAddPhoto1.setVisibility(View.INVISIBLE);
                editText2.setVisibility(View.VISIBLE);
                editText2.requestFocus();

                prefsEditor.putString("testText", editText1.getText().toString());
                prefsEditor.apply();

                editText2.setText(prefs.getString("testText","yyy"));

                //Pattern p = Pattern.compile("\\b" + unicode + "[A-Za-z0-9]*\\b");
                Pattern p = Pattern.compile(unicodeString+ " [A-Za-z0-9]*\\b");

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
