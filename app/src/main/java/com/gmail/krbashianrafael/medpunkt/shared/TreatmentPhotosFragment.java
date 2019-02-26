package com.gmail.krbashianrafael.medpunkt.shared;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.Guideline;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bogdwellers.pinchtozoom.ImageMatrixTouchHandler;
import com.bumptech.glide.Glide;
import com.gmail.krbashianrafael.medpunkt.R;
import com.gmail.krbashianrafael.medpunkt.data.MedContract.TreatmentPhotosEntry;
import com.gmail.krbashianrafael.medpunkt.phone.TreatmentActivity;
import com.gmail.krbashianrafael.medpunkt.tablet.TabletMainActivity;

import java.util.ArrayList;
import java.util.Collections;

@SuppressWarnings("deprecation")
@SuppressLint("RestrictedApi")
public class TreatmentPhotosFragment extends Fragment
        implements android.support.v4.app.LoaderManager.LoaderCallbacks<Cursor> {

    private final Handler myHandler = new Handler(Looper.getMainLooper());

    private TreatmentActivity mTreatmentActivity;
    private TabletMainActivity mTabletMainActivity;

    private long _idUser = 0;
    private long _idDisease = 0;
    public long _idTrPhoto = 0;
    public String treatmentPhotoFilePath = "";
    public String textDateOfTreatmentPhoto = "";
    public String textPhotoDescription = "";

    public TextView txtAddPhotos, widePhotoErrView;

    public Guideline verGuideline;

    public FloatingActionButton fabToFullScreen;

    public FloatingActionButton fabAddTreatmentPhotos;

    public ImageView imgWideView;

    public Animation fabAddTreatmentPhotosShowAnimation;
    public Animation fabToFullScreenShowAnimation;

    public RecyclerView recyclerTreatmentPhotos;

    public TreatmentPhotoRecyclerViewAdapter treatmentPhotoRecyclerViewAdapter;

    public static boolean mScrollToStart = false;

    private static final int TR_PHOTOS_IN_FRAGMENT_LOADER = 2;

    public TreatmentPhotosFragment() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if (HomeActivity.isTablet) {
            return inflater.inflate(R.layout.tablet_treatment_photos_fragment, container, false);
        }

        return inflater.inflate(R.layout.treatment_photos_fragment, container, false);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerTreatmentPhotos = view.findViewById(R.id.recycler_treatment_photos);

        txtAddPhotos = view.findViewById(R.id.txt_empty_photos);
        txtAddPhotos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mTabletMainActivity != null) {
                    mTabletMainActivity.tabletBigAdOpened = false;
                    mTabletMainActivity.tabletTreatmentFragment.tabletSmallAdOpened = false;
                }

                Intent intentToTreatmentPhoto = new Intent(getContext(), FullscreenPhotoActivity.class);

                intentToTreatmentPhoto.putExtra("_idUser", _idUser);
                intentToTreatmentPhoto.putExtra("_idDisease", _idDisease);

                intentToTreatmentPhoto.putExtra("newTreatmentPhoto", true);

                startActivity(intentToTreatmentPhoto);
            }
        });

        verGuideline = view.findViewById(R.id.ver_guideline);

        imgWideView = view.findViewById(R.id.img_wide_view);

        imgWideView.setOnTouchListener(new ImageMatrixTouchHandler(mTreatmentActivity));

        widePhotoErrView = view.findViewById(R.id.wide_photo_err_view);

        fabToFullScreen = view.findViewById(R.id.fab_to_full_screen);
        fabToFullScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intentToTreatmentPhoto = new Intent(getContext(), FullscreenPhotoActivity.class);

                intentToTreatmentPhoto.putExtra("_idUser", Long.valueOf(_idUser));
                intentToTreatmentPhoto.putExtra("_idDisease", Long.valueOf(_idDisease));

                intentToTreatmentPhoto.putExtra("_idTrPhoto", _idTrPhoto);
                intentToTreatmentPhoto.putExtra("treatmentPhotoFilePath", treatmentPhotoFilePath);
                intentToTreatmentPhoto.putExtra("textDateOfTreatmentPhoto", textDateOfTreatmentPhoto);
                intentToTreatmentPhoto.putExtra("textPhotoDescription", textPhotoDescription);

                startActivity(intentToTreatmentPhoto);
            }
        });

        fabAddTreatmentPhotos = view.findViewById(R.id.fabAddTreatmentPhotos);
        fabAddTreatmentPhotos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (mTabletMainActivity != null) {
                    mTabletMainActivity.tabletBigAdOpened = false;
                    mTabletMainActivity.tabletTreatmentFragment.tabletSmallAdOpened = false;
                }

                Intent intentToTreatmentPhoto = new Intent(getContext(), FullscreenPhotoActivity.class);

                intentToTreatmentPhoto.putExtra("_idUser", _idUser);
                intentToTreatmentPhoto.putExtra("_idDisease", _idDisease);

                intentToTreatmentPhoto.putExtra("newTreatmentPhoto", true);

                startActivity(intentToTreatmentPhoto);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        txtAddPhotos.setVisibility(View.INVISIBLE);
        fabAddTreatmentPhotos.setVisibility(View.INVISIBLE);

        getLoaderManager().initLoader(TR_PHOTOS_IN_FRAGMENT_LOADER, null, this);
    }

    public void initTreatmentPhotosLoader() {
        txtAddPhotos.setVisibility(View.INVISIBLE);
        fabAddTreatmentPhotos.setVisibility(View.INVISIBLE);

        _idUser = mTabletMainActivity.tabletTreatmentFragment._idUser;
        _idDisease = mTabletMainActivity.tabletTreatmentFragment._idDisease;

        getLoaderManager().initLoader(TR_PHOTOS_IN_FRAGMENT_LOADER, null, this);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (getActivity() instanceof TabletMainActivity) {
            mTabletMainActivity = (TabletMainActivity) getActivity();
            doWorkWithTabletMainActivity(mTabletMainActivity);
        } else {
            mTreatmentActivity = (TreatmentActivity) getActivity();
            doWorkWithTreatmentActivity(mTreatmentActivity);
        }
    }

    private void doWorkWithTabletMainActivity(final TabletMainActivity mTabletMainActivity) {
        if (mTabletMainActivity != null) {

            if (mTabletMainActivity.tabletTreatmentFragment.treatmentPhotosFragment == null) {
                mTabletMainActivity.tabletTreatmentFragment.initTreatmentPhotosFragment();
            }

            fabAddTreatmentPhotosShowAnimation = AnimationUtils.loadAnimation(mTabletMainActivity, R.anim.fab_show);
            fabAddTreatmentPhotosShowAnimation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    fabAddTreatmentPhotos.setVisibility(View.VISIBLE);
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }
            });

            fabToFullScreenShowAnimation = AnimationUtils.loadAnimation(mTabletMainActivity, R.anim.fab_show);
            fabToFullScreenShowAnimation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    fabToFullScreen.setVisibility(View.VISIBLE);
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }
            });

            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mTabletMainActivity,
                    LinearLayoutManager.VERTICAL, false);

            recyclerTreatmentPhotos.setLayoutManager(linearLayoutManager);

            treatmentPhotoRecyclerViewAdapter = new TreatmentPhotoRecyclerViewAdapter(mTabletMainActivity);

            recyclerTreatmentPhotos.setAdapter(treatmentPhotoRecyclerViewAdapter);
        }
    }

    private void doWorkWithTreatmentActivity(final TreatmentActivity mTreaymentActivity) {
        if (mTreaymentActivity != null) {

            fabAddTreatmentPhotosShowAnimation = AnimationUtils.loadAnimation(mTreaymentActivity, R.anim.fab_show);
            fabAddTreatmentPhotosShowAnimation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    fabAddTreatmentPhotos.setVisibility(View.VISIBLE);
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }
            });

            if (mTreaymentActivity.treatmentPhotosFragment == null) {
                mTreaymentActivity.initTreatmentPhotosFragment();
            }

            _idUser = mTreaymentActivity._idUser;
            _idDisease = mTreaymentActivity._idDisease;

            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mTreaymentActivity,
                    LinearLayoutManager.VERTICAL, false);

            recyclerTreatmentPhotos.setLayoutManager(linearLayoutManager);

            treatmentPhotoRecyclerViewAdapter = new TreatmentPhotoRecyclerViewAdapter(mTreaymentActivity);

            recyclerTreatmentPhotos.setAdapter(treatmentPhotoRecyclerViewAdapter);
        }
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        String[] projection = {
                TreatmentPhotosEntry.TR_PHOTO_ID,
                TreatmentPhotosEntry.COLUMN_U_ID,
                TreatmentPhotosEntry.COLUMN_DIS_ID,
                TreatmentPhotosEntry.COLUMN_TR_PHOTO_NAME,
                TreatmentPhotosEntry.COLUMN_TR_PHOTO_DATE,
                TreatmentPhotosEntry.COLUMN_TR_PHOTO_PATH};

        String selection = TreatmentPhotosEntry.COLUMN_U_ID + "=? AND " + TreatmentPhotosEntry.COLUMN_DIS_ID + "=?";
        String[] selectionArgs = new String[]{String.valueOf(_idUser), String.valueOf(_idDisease)};

        Context mContext;

        if (getActivity() instanceof TabletMainActivity) {
            mContext = mTabletMainActivity;
        } else {
            mContext = mTreatmentActivity;
        }

        return new CursorLoader(mContext,
                TreatmentPhotosEntry.CONTENT_TREATMENT_PHOTOS_URI,
                projection,
                selection,
                selectionArgs,
                null);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor cursor) {

        ArrayList<TreatmentPhotoItem> myData = treatmentPhotoRecyclerViewAdapter.getTreatmentPhotosList();
        myData.clear();

        if (cursor != null) {

            cursor.moveToPosition(-1);

            while (cursor.moveToNext()) {

                int trPhoto_IdColumnIndex = cursor.getColumnIndex(TreatmentPhotosEntry.TR_PHOTO_ID);
                int trPhotoUser_IdColumnIndex = cursor.getColumnIndex(TreatmentPhotosEntry.COLUMN_U_ID);
                int trPhotoDisease_IdColumnIndex = cursor.getColumnIndex(TreatmentPhotosEntry.COLUMN_DIS_ID);
                int trPhoto_nameColumnIndex = cursor.getColumnIndex(TreatmentPhotosEntry.COLUMN_TR_PHOTO_NAME);
                int trPhoto_dateColumnIndex = cursor.getColumnIndex(TreatmentPhotosEntry.COLUMN_TR_PHOTO_DATE);
                int trPhoto_pathColumnIndex = cursor.getColumnIndex(TreatmentPhotosEntry.COLUMN_TR_PHOTO_PATH);

                long _trPhotoId = cursor.getInt(trPhoto_IdColumnIndex);
                long _userId = cursor.getInt(trPhotoUser_IdColumnIndex);
                long _diseaseId = cursor.getInt(trPhotoDisease_IdColumnIndex);
                String trPhotoName = cursor.getString(trPhoto_nameColumnIndex);
                String trPhotoDate = cursor.getString(trPhoto_dateColumnIndex);
                String trPhotoUri = cursor.getString(trPhoto_pathColumnIndex);

                myData.add(new TreatmentPhotoItem(_trPhotoId, _userId, _diseaseId, trPhotoName, trPhotoDate, trPhotoUri));
            }
        }

        Collections.sort(myData);

        recyclerTreatmentPhotos.setVisibility(View.VISIBLE);

        getLoaderManager().destroyLoader(TR_PHOTOS_IN_FRAGMENT_LOADER);

        int myDataSize = myData.size();

        if (myDataSize == 0) {
            recyclerTreatmentPhotos.setVisibility(View.INVISIBLE);
            txtAddPhotos.setVisibility(View.VISIBLE);

            if (HomeActivity.isTablet) {

                fabToFullScreen.setVisibility(View.INVISIBLE);

                verGuideline.setGuidelinePercent(1.0f);

                LinearLayout layout = ((LinearLayout) ((LinearLayout) mTabletMainActivity.tabletTreatmentFragment.tabLayout.getChildAt(0)).getChildAt(1));
                LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) layout.getLayoutParams();
                layoutParams.weight = 1.00f;
                layout.setLayoutParams(layoutParams);

                Glide.with(mTabletMainActivity).clear(mTabletMainActivity.tabletTreatmentFragment.treatmentPhotosFragment.imgWideView);
            }

            treatmentPhotoRecyclerViewAdapter.notifyDataSetChanged();

        } else {
            if (HomeActivity.isTablet) {
                if (TabletMainActivity.inWideView) {
                    if (TabletMainActivity.treatmentPhotoDeleted) {
                        TreatmentPhotoItem treatmentPhotoItem = myData.get(0);
                        TabletMainActivity.selectedTreatmentPhoto_id = treatmentPhotoItem.get_trPhotoId();

                        TabletMainActivity.treatmentPhotoDeleted = false;
                    }

                    LinearLayout layout = ((LinearLayout) ((LinearLayout) mTabletMainActivity.tabletTreatmentFragment.tabLayout.getChildAt(0)).getChildAt(1));
                    LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) layout.getLayoutParams();
                    layoutParams.weight = 1.50f;
                    layout.setLayoutParams(layoutParams);

                    myHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            verGuideline.setGuidelinePercent(0.4f);
                            fabToFullScreen.startAnimation(fabToFullScreenShowAnimation);

                            treatmentPhotoRecyclerViewAdapter.notifyDataSetChanged();
                        }
                    }, 200);

                } else {
                    fabToFullScreen.setVisibility(View.INVISIBLE);
                    verGuideline.setGuidelinePercent(1.0f);

                    LinearLayout layout = ((LinearLayout) ((LinearLayout) mTabletMainActivity.tabletTreatmentFragment.tabLayout.getChildAt(0)).getChildAt(1));
                    LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) layout.getLayoutParams();
                    layoutParams.weight = 1.00f;
                    layout.setLayoutParams(layoutParams);

                    treatmentPhotoRecyclerViewAdapter.notifyDataSetChanged();

                    Glide.with(mTabletMainActivity).clear(mTabletMainActivity.tabletTreatmentFragment.treatmentPhotosFragment.imgWideView);
                }

            } else {
                treatmentPhotoRecyclerViewAdapter.notifyDataSetChanged();
            }

            fabAddTreatmentPhotos.startAnimation(fabAddTreatmentPhotosShowAnimation);
        }

        if (mScrollToStart && myData.size() != 0) {
            recyclerTreatmentPhotos.smoothScrollToPosition(0);
            mScrollToStart = false;
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        ArrayList<TreatmentPhotoItem> myData = treatmentPhotoRecyclerViewAdapter.getTreatmentPhotosList();
        myData.clear();
        treatmentPhotoRecyclerViewAdapter.notifyDataSetChanged();
    }
}
