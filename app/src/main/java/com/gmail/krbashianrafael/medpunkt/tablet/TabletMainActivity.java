package com.gmail.krbashianrafael.medpunkt.tablet;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.FrameLayout;

import com.gmail.krbashianrafael.medpunkt.R;

public class TabletMainActivity extends AppCompatActivity {

    TabletUsersFragment tabletUsersFragment;
    TabletDiseasesFragment tabletDiseasesFragment;
    TabletTreatmentFragment tabletTreatmentFragment;

    FrameLayout tabletUsersBlurFrame;
    FrameLayout tabletDiseasesBlurFrame;
    FrameLayout tableTreatmentBlurFrame;

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

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_home_white_30dp);
        }

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

    public void blur() {
        if (tabletUsersFragment.getView() != null) {
            //
        }

        if (tabletDiseasesFragment.getView() != null && !tabletDiseasesBlurFrame.isClickable()) {
            tabletDiseasesBlurFrame.setClickable(true);
            tabletDiseasesBlurFrame.setBackgroundColor(getResources().getColor(R.color.my_gray));

            Log.d("clk", "tabletDiseasesBlurFrame.setClickable(true)");

        }

        if (tabletTreatmentFragment.getView() != null && !tableTreatmentBlurFrame.isClickable()) {
            tableTreatmentBlurFrame.setClickable(true);
            tableTreatmentBlurFrame.setBackgroundColor(getResources().getColor(R.color.my_gray));
        }
    }

    public void unBlur() {
        if (tabletUsersFragment.getView()!=null){
            //
        }

        if (tabletDiseasesFragment.getView()!=null && tabletDiseasesBlurFrame.isClickable()){
            tabletDiseasesBlurFrame.setClickable(false);
            tabletDiseasesBlurFrame.setBackgroundColor(Color.TRANSPARENT);

            Log.d("clk", "tabletDiseasesBlurFrame.setClickable(false)");
        }

        if (tabletTreatmentFragment.getView()!=null && tableTreatmentBlurFrame.isClickable()){
            tableTreatmentBlurFrame.setClickable(false);
            tableTreatmentBlurFrame.setBackgroundColor(Color.TRANSPARENT);
        }
    }

}
