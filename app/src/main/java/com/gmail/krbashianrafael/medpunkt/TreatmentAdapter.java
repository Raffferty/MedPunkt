package com.gmail.krbashianrafael.medpunkt;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;

public class TreatmentAdapter extends FragmentPagerAdapter {

    private Context mContext;

    private int pagesCount = 2;

    TreatmentAdapter(Context context, FragmentManager fm) {
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
        return pagesCount;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if (position == 0) {
            //return mContext.getString(R.string.description);
            return iconWithText(mContext.getResources().getDrawable(R.drawable.ic_edit_orange_24dp),
                    mContext.getResources().getString(R.string.treatment_description));
        } else if (position == 1) {
            //return mContext.getString(R.string.photos);
            return iconWithText(mContext.getResources().getDrawable(R.drawable.ic_camera_alt_black_24dp),
                    mContext.getResources().getString(R.string.treatment_images));
        }
        return "---";
    }

    private CharSequence iconWithText(Drawable r, String title) {
        r.setBounds(0, 0, r.getIntrinsicWidth(), r.getIntrinsicHeight());
        SpannableString sb = new SpannableString("    " + title);
        ImageSpan imageSpan = new ImageSpan(r, ImageSpan.ALIGN_BOTTOM);
        sb.setSpan(imageSpan, 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        return sb;
    }

    public void setPagesCount(int pagesCount) {
        this.pagesCount = pagesCount;
    }
}
