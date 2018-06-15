package com.gmail.krbashianrafael.medpunkt;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class TreatmentAdapter extends FragmentPagerAdapter {

    private Context mContext;

    public TreatmentAdapter(Context context, FragmentManager fm) {
        super(fm);
        mContext = context;
    }

    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            return new TreatmentDescriptionFragment();
        } else if (position == 1) {
            return new TreatmentPhotosFragment();
        }
        return null;
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if (position == 0) {
            return mContext.getString(R.string.description);
        } else if (position == 1) {
            return mContext.getString(R.string.photos);
        }
        return "---";
    }
}
