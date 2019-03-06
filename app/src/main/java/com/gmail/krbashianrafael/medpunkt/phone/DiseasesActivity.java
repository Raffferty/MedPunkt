package com.gmail.krbashianrafael.medpunkt.phone;

import android.annotation.SuppressLint;
import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.transition.TransitionManager;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gmail.krbashianrafael.medpunkt.R;
import com.gmail.krbashianrafael.medpunkt.data.MedContract.DiseasesEntry;
import com.gmail.krbashianrafael.medpunkt.shared.DiseaseItem;
import com.gmail.krbashianrafael.medpunkt.shared.DiseaseRecyclerViewAdapter;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.util.ArrayList;
import java.util.Collections;

@SuppressWarnings("SpellCheckingInspection")
@SuppressLint("RestrictedApi")
public class DiseasesActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor> {

    private long _idUser = 0;

    static String textUserName;

    private TextView textViewAddDisease;

    private FloatingActionButton fabAddDisease;

    private Animation fabShowAnimation;
    private Animation fadeInAnimation;

    public static boolean mScrollToStart = false;

    private RecyclerView recyclerDiseases;
    private DiseaseRecyclerViewAdapter diseaseRecyclerViewAdapter;

    private static final int DISEASES_LOADER = 1;

    private RelativeLayout adRoot;
    private AdView adViewInDiseasesActivity;
    private AdRequest adRequest;
    public boolean phoneAdOpened = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diseases);

        adRoot = findViewById(R.id.adRoot);

        adViewInDiseasesActivity = findViewById(R.id.adViewInDiseasesActivity);

        adRequest = new AdRequest.Builder().build();

        adViewInDiseasesActivity.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                if (adViewInDiseasesActivity.getVisibility() != View.VISIBLE) {
                    TransitionManager.beginDelayedTransition(adRoot);
                    adViewInDiseasesActivity.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                if (adViewInDiseasesActivity.getVisibility() != View.GONE) {
                    adViewInDiseasesActivity.setVisibility(View.GONE);
                }
            }

            @Override
            public void onAdOpened() {
                if (adViewInDiseasesActivity.getVisibility() != View.GONE) {
                    adViewInDiseasesActivity.setVisibility(View.GONE);
                    phoneAdOpened = true;
                    adViewInDiseasesActivity.pause();
                }
            }

            @Override
            public void onAdLeftApplication() {
            }

            @Override
            public void onAdClosed() {
            }
        });

        Intent intent = getIntent();

        if (intent.hasExtra("UserName")) {
            textUserName = intent.getStringExtra("UserName");
        }

        if (intent.hasExtra("_idUser")) {
            _idUser = intent.getLongExtra("_idUser", 0);
        }

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_group_white_30dp);

            actionBar.setElevation(0);

            if (textUserName != null) {
                actionBar.setTitle(textUserName);
            } else {
                actionBar.setTitle(R.string.txt_no_title);
                textUserName = "";
            }
        }

        textViewAddDisease = findViewById(R.id.txt_empty_diseases);
        textViewAddDisease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                phoneAdOpened = false;

                Intent treatmentIntent = new Intent(DiseasesActivity.this, TreatmentActivity.class);
                treatmentIntent.putExtra("_idUser", _idUser);
                treatmentIntent.putExtra("newDisease", true);
                treatmentIntent.putExtra("editDisease", true);
                treatmentIntent.putExtra("diseaseName", "");
                treatmentIntent.putExtra("textTreatment", "");
                startActivity(treatmentIntent);
            }
        });

        fabAddDisease = findViewById(R.id.fabAddDisease);
        fabAddDisease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                phoneAdOpened = false;

                Intent treatmentIntent = new Intent(DiseasesActivity.this, TreatmentActivity.class);

                treatmentIntent.putExtra("_idUser", _idUser);
                treatmentIntent.putExtra("newDisease", true);
                treatmentIntent.putExtra("editDisease", true);
                treatmentIntent.putExtra("diseaseName", "");
                treatmentIntent.putExtra("textTreatment", "");
                startActivity(treatmentIntent);
            }
        });

        fadeInAnimation = AnimationUtils.loadAnimation(this, R.anim.fadein);

        fabShowAnimation = AnimationUtils.loadAnimation(this, R.anim.fab_show);
        fabShowAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                fabAddDisease.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                fabAddDisease.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                fabAddDisease.setVisibility(View.VISIBLE);
            }
        });

        recyclerDiseases = findViewById(R.id.recycler_diseases);

        TextView txtDiseases = findViewById(R.id.txt_diseases);
        txtDiseases.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recyclerDiseases.smoothScrollToPosition(0);
            }
        });

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false);

        recyclerDiseases.setLayoutManager(linearLayoutManager);

        diseaseRecyclerViewAdapter = new DiseaseRecyclerViewAdapter(this);

        recyclerDiseases.setAdapter(diseaseRecyclerViewAdapter);
    }

    @Override
    public void onPause() {
        if (adViewInDiseasesActivity != null) {
            adViewInDiseasesActivity.pause();
        }
        super.onPause();
    }

    @Override
    protected void onResume() {

        if (adViewInDiseasesActivity != null) {
            if (isNetworkConnected()) {
                if (adViewInDiseasesActivity.getVisibility() == View.VISIBLE) {
                    adViewInDiseasesActivity.resume();
                } else {
                    if(!phoneAdOpened){
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                adViewInDiseasesActivity.loadAd(adRequest);
                            }
                        }, 600);
                    }
                }
            } else {
                if (adViewInDiseasesActivity.getVisibility() == View.VISIBLE) {
                    adViewInDiseasesActivity.setVisibility(View.GONE);
                    adViewInDiseasesActivity.pause();
                }
            }
        }

        textViewAddDisease.setVisibility(View.INVISIBLE);
        fabAddDisease.setVisibility(View.INVISIBLE);

        getLoaderManager().initLoader(DISEASES_LOADER, null, this);

        super.onResume();
    }

    private boolean isNetworkConnected() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    public void onDestroy() {
        if (adViewInDiseasesActivity != null) {
            adViewInDiseasesActivity.destroy();
        }
        super.onDestroy();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                finish();
                return true;
            default:
                super.onOptionsItemSelected(item);
                finish();
                return true;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String[] projection = {
                DiseasesEntry.DIS_ID,
                DiseasesEntry.COLUMN_U_ID,
                DiseasesEntry.COLUMN_DISEASE_NAME,
                DiseasesEntry.COLUMN_DISEASE_DATE,
                DiseasesEntry.COLUMN_DISEASE_TREATMENT};

        String selection = DiseasesEntry.COLUMN_U_ID + "=?";
        String[] selectionArgs = new String[]{String.valueOf(_idUser)};

        return new CursorLoader(this,
                DiseasesEntry.CONTENT_DISEASES_URI,
                projection,
                selection,
                selectionArgs,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

        ArrayList<DiseaseItem> myData = diseaseRecyclerViewAdapter.getDiseaseList();
        myData.clear();

        if (cursor != null) {

            cursor.moveToPosition(-1);

            while (cursor.moveToNext()) {

                int disease_idColumnIndex = cursor.getColumnIndex(DiseasesEntry._ID);
                int diseaseUser_IdColumnIndex = cursor.getColumnIndex(DiseasesEntry.COLUMN_U_ID);
                int disease_nameColumnIndex = cursor.getColumnIndex(DiseasesEntry.COLUMN_DISEASE_NAME);
                int disease_dateColumnIndex = cursor.getColumnIndex(DiseasesEntry.COLUMN_DISEASE_DATE);
                int disease_treatmentColumnIndex = cursor.getColumnIndex(DiseasesEntry.COLUMN_DISEASE_TREATMENT);

                long _diseaseId = cursor.getLong(disease_idColumnIndex);
                long _diseaseUserId = cursor.getLong(diseaseUser_IdColumnIndex);
                String diseaseName = cursor.getString(disease_nameColumnIndex);
                String diseaseDate = cursor.getString(disease_dateColumnIndex);
                String diseaseTreatment = cursor.getString(disease_treatmentColumnIndex);

                myData.add(new DiseaseItem(_diseaseId, _diseaseUserId, diseaseName, diseaseDate, diseaseTreatment));
            }
        }

        Collections.sort(myData);

        recyclerDiseases.setVisibility(View.VISIBLE);

        diseaseRecyclerViewAdapter.notifyDataSetChanged();

        getLoaderManager().destroyLoader(DISEASES_LOADER);

        if (myData.size() == 0) {
            recyclerDiseases.setVisibility(View.INVISIBLE);

            new Handler(Looper.getMainLooper()).
                    postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            textViewAddDisease.setVisibility(View.VISIBLE);
                            textViewAddDisease.startAnimation(fadeInAnimation);
                        }
                    }, 300);
        } else {
            fabAddDisease.startAnimation(fabShowAnimation);
        }

        if (mScrollToStart && myData.size() != 0) {
            recyclerDiseases.smoothScrollToPosition(0);
            mScrollToStart = false;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        ArrayList<DiseaseItem> myData = diseaseRecyclerViewAdapter.getDiseaseList();
        myData.clear();
        diseaseRecyclerViewAdapter.notifyDataSetChanged();
    }
}
