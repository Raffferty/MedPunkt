package com.gmail.krbashianrafael.medpunkt.phone;

import android.annotation.SuppressLint;
import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.gmail.krbashianrafael.medpunkt.R;
import com.gmail.krbashianrafael.medpunkt.data.MedContract;
import com.gmail.krbashianrafael.medpunkt.data.MedContract.UsersEntry;
import com.gmail.krbashianrafael.medpunkt.shared.HomeActivity;
import com.gmail.krbashianrafael.medpunkt.shared.UserActivity;
import com.gmail.krbashianrafael.medpunkt.shared.UserItem;
import com.gmail.krbashianrafael.medpunkt.shared.UsersRecyclerViewAdapter;

import java.util.ArrayList;
import java.util.Collections;

@SuppressLint("RestrictedApi")
public class UsersActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor> {

    private TextView txtAddUsers;
    private FloatingActionButton fabAddUser;
    private RecyclerView recyclerUsers;
    private UsersRecyclerViewAdapter usersRecyclerViewAdapter;

    private Animation fabShowAnimation;
    private Animation fadeInAnimation;

    public static int onResumeCounter = 0;

    private static final int USERS_LOADER = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(R.string.title_activity_home);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_home_white_30dp);
        }

        TextView txtTabletUsers = findViewById(R.id.txt_tablet_users);

        if (HomeActivity.iAmDoctor) {
            txtTabletUsers.setText(R.string.patients_title_activity);
        }

        txtTabletUsers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (usersRecyclerViewAdapter.getUsersList().size() != 0) {
                    recyclerUsers.smoothScrollToPosition(0);
                }
            }
        });

        fabAddUser = findViewById(R.id.fabAddUser);
        fabAddUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent userIntent = new Intent(UsersActivity.this, UserActivity.class);
                userIntent.putExtra("userPhotoUri", "No_Photo");
                userIntent.putExtra("newUser", true);
                userIntent.putExtra("iAmDoctor", HomeActivity.iAmDoctor);
                startActivity(userIntent);
            }
        });

        fadeInAnimation = AnimationUtils.loadAnimation(this, R.anim.fadein);

        fabShowAnimation = AnimationUtils.loadAnimation(this, R.anim.fab_show);
        fabShowAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                fabAddUser.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                fabAddUser.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                fabAddUser.setVisibility(View.VISIBLE);
            }
        });

        txtAddUsers = findViewById(R.id.txt_empty_users);

        if (HomeActivity.iAmDoctor) {
            txtAddUsers.setText(R.string.patient_title_activity);
        }
        txtAddUsers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent userIntent = new Intent(UsersActivity.this, UserActivity.class);
                userIntent.putExtra("userPhotoUri", "No_Photo");
                userIntent.putExtra("newUser", true);
                userIntent.putExtra("iAmDoctor", HomeActivity.iAmDoctor);
                startActivity(userIntent);
            }
        });

        recyclerUsers = findViewById(R.id.recycler_users);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false);

        recyclerUsers.setLayoutManager(linearLayoutManager);

        usersRecyclerViewAdapter = new UsersRecyclerViewAdapter(this);

        recyclerUsers.setAdapter(usersRecyclerViewAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();

        View viewToHide = this.getCurrentFocus();
        if (viewToHide != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(viewToHide.getWindowToken(), 0);
            }
        }

        onResumeCounter++;

        fabAddUser.setVisibility(View.INVISIBLE);
        txtAddUsers.setVisibility(View.INVISIBLE);

        getLoaderManager().initLoader(USERS_LOADER, null, this);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {
                UsersEntry.U_ID,
                UsersEntry.COLUMN_USER_NAME,
                MedContract.UsersEntry.COLUMN_USER_DATE,
                MedContract.UsersEntry.COLUMN_USER_PHOTO_PATH};

        return new CursorLoader(this,
                UsersEntry.CONTENT_USERS_URI,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

        ArrayList<UserItem> myData = usersRecyclerViewAdapter.getUsersList();
        myData.clear();

        if (cursor != null) {

            cursor.moveToPosition(-1);

            while (cursor.moveToNext()) {

                int user_idColumnIndex = cursor.getColumnIndex(UsersEntry._ID);
                int user_nameColumnIndex = cursor.getColumnIndex(UsersEntry.COLUMN_USER_NAME);
                int user_dateColumnIndex = cursor.getColumnIndex(UsersEntry.COLUMN_USER_DATE);
                int user_photoColumnIndex = cursor.getColumnIndex(UsersEntry.COLUMN_USER_PHOTO_PATH);

                long _userId = cursor.getLong(user_idColumnIndex);
                String userName = cursor.getString(user_nameColumnIndex);

                String userBirthDate = cursor.getString(user_dateColumnIndex);
                String userPhotoUri = cursor.getString(user_photoColumnIndex);

                myData.add(new UserItem(_userId, userBirthDate, userName, userPhotoUri));
            }
        }

        Collections.sort(myData);

        recyclerUsers.setVisibility(View.VISIBLE);

        usersRecyclerViewAdapter.notifyDataSetChanged();

        getLoaderManager().destroyLoader(USERS_LOADER);

        int myDataSize = myData.size();

        if (myDataSize == 0) {

            recyclerUsers.setVisibility(View.INVISIBLE);

            new Handler(Looper.getMainLooper()).
                    postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            txtAddUsers.setVisibility(View.VISIBLE);
                            txtAddUsers.startAnimation(fadeInAnimation);
                        }
                    }, 300);
        } else if (myDataSize == 1 && onResumeCounter == 1 && !HomeActivity.iAmDoctor) {
            Intent userDiseasIntent = new Intent(this, DiseasesActivity.class);
            userDiseasIntent.putExtra("_idUser", myData.get(0).get_userId());
            userDiseasIntent.putExtra("UserName", myData.get(0).getUserName());

            startActivity(userDiseasIntent);
        } else {
            fabAddUser.startAnimation(fabShowAnimation);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        ArrayList<UserItem> myData = usersRecyclerViewAdapter.getUsersList();
        myData.clear();
        usersRecyclerViewAdapter.notifyDataSetChanged();
    }
}
