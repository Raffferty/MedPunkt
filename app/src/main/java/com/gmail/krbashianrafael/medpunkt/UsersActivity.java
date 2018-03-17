package com.gmail.krbashianrafael.medpunkt;

import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.ImageView;

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

        ImageView imageViewAddUsers = findViewById(R.id.imageViewAddUsers);
        imageViewAddUsers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent userIntent = new Intent(UsersActivity.this, UserActivity.class);
                startActivity(userIntent);
            }
        });

        // привязка для snackbar
        //mLayout = findViewById(R.id.usersLayout);

        // получаем runtimePermission к READ_EXTERNAL_STORAGE через специальный класс PhotoRequesPermissionHandler
        // для SDK меньше, чем Marshmallow достаточно прописать только в Манифесте

        /*showSnackBar = PhotoRequesPermissionHandler.getRuntimePhotoPermissionToStorage(this,
                mLayout,PERMISSION_READ_EXTERNAL_STORAGE,
                showSnackBar);*/
    }

    /*@Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        // BEGIN_INCLUDE(onRequestPermissionsResult)
        if (requestCode == PERMISSION_READ_EXTERNAL_STORAGE) {
            // Request for camera permission.
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission has been granted. Start camera preview Activity.
                Snackbar.make(mLayout, R.string.permission_was_granted,
                        Snackbar.LENGTH_LONG)
                        .show();

                //TODO после получения разрешения грузим фотки в UsersActyvity
                //getLoaderManager().initLoader(PET_LOADER,null, this);

            } else {
                // Permission request was denied.
                Snackbar.make(mLayout, R.string.permission_was_denied,
                        Snackbar.LENGTH_LONG)
                        .show();
            }
        }

    }*/


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
                startActivity(userIntent);

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
