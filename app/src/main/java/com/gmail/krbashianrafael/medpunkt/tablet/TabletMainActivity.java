package com.gmail.krbashianrafael.medpunkt.tablet;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

import com.gmail.krbashianrafael.medpunkt.R;

public class TabletMainActivity extends AppCompatActivity {

    private TabletUsersFragment tabletUsersFragment;
    private TabletDiseasesFragment tabletDiseasesFragment;
    private TabletTreatmentFragment tabletTreatmentFragment;

    private FrameLayout tabletUsersBlurFrame;
    private FrameLayout tabletDiseasesBlurFrame;
    private FrameLayout tableTreatmentBlurFrame;

    protected static final int TABLET_USERS_FRAGMENT = 1;
    protected static final int TABLET_DISEASES_FRAGMENT = 2;
    protected static final int TABLET_TREATMENT_FRAGMENT = 3;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tablet_main);

        if (savedInstanceState == null) {
            tabletUsersFragment = (TabletUsersFragment)
                    getSupportFragmentManager().findFragmentById(R.id.tablet_users_fragment);

            tabletDiseasesFragment = (TabletDiseasesFragment)
                    getSupportFragmentManager().findFragmentById(R.id.tablet_diseases_fragment);

            tabletTreatmentFragment = (TabletTreatmentFragment)
                    getSupportFragmentManager().findFragmentById(R.id.tablet_treatment_fragment);
        }

        /*ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_home_white_30dp);
        }*/

        tabletUsersBlurFrame = findViewById(R.id.tablet_users_blur);
        tabletDiseasesBlurFrame = findViewById(R.id.tablet_diseases_blur);
        tableTreatmentBlurFrame = findViewById(R.id.tablet_treatment_blur);
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

    public void blur(int fragmentNumber) {

        switch (fragmentNumber) {
            case TABLET_USERS_FRAGMENT:
                if (!tabletUsersBlurFrame.isClickable()) {
                    tabletUsersBlurFrame.setClickable(true);
                    tabletUsersBlurFrame.setBackgroundColor(getResources().getColor(R.color.my_gray));
                    tabletUsersFragment.txtAddUsers.setVisibility(View.INVISIBLE);
                    tabletUsersFragment.fabAddUser.setVisibility(View.INVISIBLE);
                }
                break;
            case TABLET_DISEASES_FRAGMENT:
                if (!tabletDiseasesBlurFrame.isClickable()) {
                    tabletDiseasesBlurFrame.setClickable(true);
                    tabletDiseasesBlurFrame.setBackgroundColor(getResources().getColor(R.color.my_gray));
                    tabletDiseasesFragment.textViewAddDisease.setVisibility(View.INVISIBLE);
                    tabletDiseasesFragment.fabAddDisease.setVisibility(View.INVISIBLE);
                }
                break;
            case TABLET_TREATMENT_FRAGMENT:
                if (!tableTreatmentBlurFrame.isClickable()) {
                    tableTreatmentBlurFrame.setClickable(true);
                    tableTreatmentBlurFrame.setBackgroundColor(getResources().getColor(R.color.my_gray));
                }
                break;
            default:
                break;
        }
    }

    public void unBlur(int fragmentNumber) {

        switch (fragmentNumber) {
            case TABLET_USERS_FRAGMENT:
                if (tabletUsersBlurFrame.isClickable()) {
                    tabletUsersFragment.txtAddUsers.setVisibility(View.VISIBLE);
                    tabletUsersFragment.fabAddUser.setVisibility(View.VISIBLE);
                    tabletUsersBlurFrame.setClickable(false);
                    tabletUsersBlurFrame.setBackgroundColor(Color.TRANSPARENT);
                }
                break;
            case TABLET_DISEASES_FRAGMENT:
                if (tabletDiseasesBlurFrame.isClickable()) {
                    tabletDiseasesBlurFrame.setClickable(false);
                    tabletDiseasesBlurFrame.setBackgroundColor(Color.TRANSPARENT);
                    tabletDiseasesFragment.textViewAddDisease.setVisibility(View.VISIBLE);
                    tabletDiseasesFragment.fabAddDisease.setVisibility(View.VISIBLE);
                }
                break;
            case TABLET_TREATMENT_FRAGMENT:
                if (tableTreatmentBlurFrame.isClickable()) {
                    tableTreatmentBlurFrame.setClickable(false);
                    tableTreatmentBlurFrame.setBackgroundColor(Color.TRANSPARENT);
                }
                break;
            default:
                break;
        }
    }
}
