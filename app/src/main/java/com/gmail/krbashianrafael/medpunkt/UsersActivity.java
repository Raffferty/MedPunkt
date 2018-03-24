package com.gmail.krbashianrafael.medpunkt;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.File;

import static com.gmail.krbashianrafael.medpunkt.HomeActivity.PREFS_NAME;

public class UsersActivity extends AppCompatActivity {

    //private static final int PERMISSION_READ_EXTERNAL_STORAGE = 0;

    // для привязки snackbar
    //private View mLayout;
    //private static boolean showSnackBar = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_home_white_24dp);
        }

        // сейчас это невидимо
        TextView addUsers = findViewById(R.id.txt_empty_users);
        addUsers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent userIntent = new Intent(UsersActivity.this, UserActivity.class);
                startActivity(userIntent);
            }
        });

        // начало ------ Фиктивниый юзер с фото
        LinearLayout linearLayoutReciclerViewItem = findViewById(R.id.recicler_view_item);
        ImageView userImage = findViewById(R.id.user_image);
        final String pathToPhoto = getString(R.string.pathToPhoto);
        File imgFile = new File(pathToPhoto);
        if (imgFile.exists()) {
            Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            userImage.setImageBitmap(myBitmap);
        }

        // конец ------ Фиктивниый юзер с фото

        linearLayoutReciclerViewItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent userIntent = new Intent(UsersActivity.this, DiseasesActivity.class);
                userIntent.putExtra("_id", 1);
                userIntent.putExtra("UserName", "Вася");
                userIntent.putExtra("birthDate", "11.03.1968");
                userIntent.putExtra("userPhotoUri", pathToPhoto);

                startActivity(userIntent);
            }
        });

        // кнопка редактирования юзера
        FrameLayout userItemEdit = findViewById(R.id.user_item_edit);
        userItemEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent userIntent = new Intent(UsersActivity.this, UserActivity.class);
                userIntent.putExtra("_id", 1);
                userIntent.putExtra("editUser", true);
                userIntent.putExtra("UserName", "Вася");
                userIntent.putExtra("birthDate", "11.03.1968");
                userIntent.putExtra("userPhotoUri", pathToPhoto);

                startActivity(userIntent);
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //getMenuInflater().inflate(R.menu.menu_users, menu);

        //добавляем в меню надпись с иконкой удалить
        menu.add(0, R.id.action_add_user, 1, menuIconWithText(getResources().getDrawable(R.drawable.ic_add_blue_24dp), getResources().getString(R.string.add_user)));
        return true;
    }

    private CharSequence menuIconWithText(Drawable r, String title) {
        r.setBounds(0, 0, r.getIntrinsicWidth(), r.getIntrinsicHeight());
        SpannableString sb = new SpannableString("    " + title);
        ImageSpan imageSpan = new ImageSpan(r, ImageSpan.ALIGN_BOTTOM);
        sb.setSpan(imageSpan, 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        return sb;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
                SharedPreferences.Editor prefsEditor = prefs.edit();

                prefsEditor.putBoolean("showGreeting", true);
                prefsEditor.apply();

                Intent intent = new Intent(UsersActivity.this, HomeActivity.class);
                startActivity(intent);

                return true;

            case R.id.action_add_user:
                Intent userIntent = new Intent(UsersActivity.this, UserActivity.class);
                userIntent.putExtra("userPhotoUri", "No_Photo");
                userIntent.putExtra("newUser", true);
                startActivity(userIntent);

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
