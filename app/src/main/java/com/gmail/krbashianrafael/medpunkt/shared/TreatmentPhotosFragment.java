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
import com.gmail.krbashianrafael.medpunkt.R;
import com.gmail.krbashianrafael.medpunkt.data.MedContract.TreatmentPhotosEntry;
import com.gmail.krbashianrafael.medpunkt.phone.TreatmentActivity;
import com.gmail.krbashianrafael.medpunkt.tablet.TabletMainActivity;

import java.util.ArrayList;
import java.util.Collections;

public class TreatmentPhotosFragment extends Fragment
        implements android.support.v4.app.LoaderManager.LoaderCallbacks<Cursor> {

    private final Handler myHandler = new Handler(Looper.getMainLooper());

    // Активити в котором может находится этот фрагмент
    private TreatmentActivity mTreatmentActivity;
    private TabletMainActivity mTabletMainActivity;

    // id пользователя
    private long _idUser = 0;

    // id заболеввания
    private long _idDisease = 0;

    public long _idTrPhoto = 0;
    public String treatmentPhotoFilePath = "";
    public String textDateOfTreatmentPhoto = "";
    public String textPhotoDescription = "";
    //public boolean errorOnPhotoLoading = false;

    // TextView добавления фотоснимка лечения
    public TextView txtAddPhotos, widePhotoErrView;

    public Guideline verGuideline;

    public FloatingActionButton fabToFullScreen;

    public FloatingActionButton fabAddTreatmentPhotos;

    // ImageView для загрузки фото в расширенном виде на планшете
    public ImageView imgWideView;

    private Animation fabShowAnimation;

    public RecyclerView recyclerTreatmentPhotos;

    public TreatmentPhotoRecyclerViewAdapter treatmentPhotoRecyclerViewAdapter;

    public static boolean mScrollToStart = false;

    /**
     * Identifier for the user data loader
     * Лоадеров может много (они обрабатываются в case)
     * поэтому устанавливаем инициализатор для каждого лоадера
     * в данном случае private static final int TR_PHOTOS_LOADER = 2;
     */
    private static final int TR_PHOTOS_IN_FRAGMENT_LOADER = 2;

    //public MyImageMatrixTouchHandler myTrPhotosImageMatrixTouchHandler;

    public TreatmentPhotosFragment() {
        // нужен ПУСТОЙ конструктор
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
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
                Intent intentToTreatmentPhoto = new Intent(getContext(), FullscreenPhotoActivity.class);

                intentToTreatmentPhoto.putExtra("_idUser", _idUser);
                intentToTreatmentPhoto.putExtra("_idDisease", _idDisease);

                intentToTreatmentPhoto.putExtra("newTreatmentPhoto", true);

                startActivity(intentToTreatmentPhoto);
            }
        });

        // verGuideline только для планшета
        verGuideline = view.findViewById(R.id.ver_guideline);

        // Мой zoomer
        //myTrPhotosImageMatrixTouchHandler = new MyImageMatrixTouchHandler(mTreatmentActivity);

        // imgWideView только для планшета
        imgWideView = view.findViewById(R.id.img_wide_view);

        imgWideView.setOnTouchListener(new ImageMatrixTouchHandler(mTreatmentActivity));

        // widePhotoErrView только для планшета
        widePhotoErrView = view.findViewById(R.id.wide_photo_err_view);

        // fabToFullScreen только для планшета
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

                /*Log.d("XZX", "_idUser = " + _idUser);
                Log.d("XZX", "_idDisease = " + _idDisease);
                Log.d("XZX", "_idTrPhoto = " + _idTrPhoto);
                Log.d("XZX", "treatmentPhotoFilePath = " + treatmentPhotoFilePath);
                Log.d("XZX", "textDateOfTreatmentPhoto = " + textDateOfTreatmentPhoto);
                Log.d("XZX", "textPhotoDescription = " + textPhotoDescription);*/

                /*if (errorOnPhotoLoading){
                    intentToTreatmentPhoto.putExtra("editTreatmentPhoto", true);
                }*/

                startActivity(intentToTreatmentPhoto);
            }
        });

        fabAddTreatmentPhotos = view.findViewById(R.id.fabAddTreatmentPhotos);
        fabAddTreatmentPhotos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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

        // сразу INVISIBLE делаем чтоб не было скачков при смене вида
        txtAddPhotos.setVisibility(View.INVISIBLE);
        fabAddTreatmentPhotos.setVisibility(View.INVISIBLE);

        // Инициализируем Loader
        getLoaderManager().initLoader(TR_PHOTOS_IN_FRAGMENT_LOADER, null, this);
    }

    // этот метод вызывается только в планшетном виде
    // при нажатии на закладку "Снимки" в TabletTreatmentFragment
    // при сохранении нового заболевания пользователя
    public void initTreatmentPhotosLoader() {

        // сразу INVISIBLE делаем чтоб не было скачков при смене вида
        txtAddPhotos.setVisibility(View.INVISIBLE);
        fabAddTreatmentPhotos.setVisibility(View.INVISIBLE);

        // берем _idUser и _idDisease из tabletTreatmentFragment
        _idUser = mTabletMainActivity.tabletTreatmentFragment._idUser;
        _idDisease = mTabletMainActivity.tabletTreatmentFragment._idDisease;

        // Инициализируем Loader
        getLoaderManager().initLoader(TR_PHOTOS_IN_FRAGMENT_LOADER, null, this);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        /*if (HomeActivity.isTablet){
            mTabletMainActivity = (TabletMainActivity) getActivity();
            doWorkWithTabletMainActivity(mTabletMainActivity);
        }
        else {
            mTreatmentActivity = (TreatmentActivity) getActivity();
            doWorkWithTreatmentActivity(mTreatmentActivity);
        }*/

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

            // в главном активити инициализируем фрагмент (есл он еще не инициализирован, т.е. если он еще null)
            if (mTabletMainActivity.tabletTreatmentFragment.treatmentPhotosFragment == null) {
                mTabletMainActivity.tabletTreatmentFragment.initTreatmentPhotosFragment();
            }

            fabShowAnimation = AnimationUtils.loadAnimation(mTabletMainActivity, R.anim.fab_show);
            fabShowAnimation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    fabAddTreatmentPhotos.setVisibility(View.VISIBLE);
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    fabAddTreatmentPhotos.setVisibility(View.VISIBLE);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                    fabAddTreatmentPhotos.setVisibility(View.VISIBLE);
                }
            });

            // инициализация этих полей для планшета происходит в методе initTreatmentPhotosLoader()
            /*_idUser = mTabletMainActivity.tabletTreatmentFragment._idUser;
            _idDisease = mTabletMainActivity.tabletTreatmentFragment._idDisease;*/

            // инициализируем linearLayoutManager
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mTabletMainActivity,
                    LinearLayoutManager.VERTICAL, false);

            // устанавливаем LayoutManager для RecyclerView
            recyclerTreatmentPhotos.setLayoutManager(linearLayoutManager);

            // инициализируем TreatmentPhotoRecyclerViewAdapter
            treatmentPhotoRecyclerViewAdapter = new TreatmentPhotoRecyclerViewAdapter(mTabletMainActivity);

            // устанавливаем адаптер для RecyclerView
            recyclerTreatmentPhotos.setAdapter(treatmentPhotoRecyclerViewAdapter);
        }

    }

    private void doWorkWithTreatmentActivity(final TreatmentActivity mTreaymentActivity) {
        if (mTreaymentActivity != null) {

            fabShowAnimation = AnimationUtils.loadAnimation(mTreaymentActivity, R.anim.fab_show);
            fabShowAnimation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    fabAddTreatmentPhotos.setVisibility(View.VISIBLE);
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    fabAddTreatmentPhotos.setVisibility(View.VISIBLE);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                    fabAddTreatmentPhotos.setVisibility(View.VISIBLE);
                }
            });

            // в главном активити инициализируем фрагмент (есл он еще не инициализирован, т.е. если он еще null)
            if (mTreaymentActivity.treatmentPhotosFragment == null) {
                mTreaymentActivity.initTreatmentPhotosFragment();
            }

            _idUser = mTreaymentActivity._idUser;
            _idDisease = mTreaymentActivity._idDisease;

            // инициализируем linearLayoutManager
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mTreaymentActivity,
                    LinearLayoutManager.VERTICAL, false);

            // устанавливаем LayoutManager для RecyclerView
            recyclerTreatmentPhotos.setLayoutManager(linearLayoutManager);

            // инициализируем TreatmentPhotoRecyclerViewAdapter
            treatmentPhotoRecyclerViewAdapter = new TreatmentPhotoRecyclerViewAdapter(mTreaymentActivity);

            // устанавливаем адаптер для RecyclerView
            recyclerTreatmentPhotos.setAdapter(treatmentPhotoRecyclerViewAdapter);
        }

    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        // Define a projection that specifies the columns from the table we care about.
        // для Loader в projection обязательно нужно указывать поле с _ID
        // здесь мы указываем поля, которые будем брать из Cursor для дальнейшей передачи в RecyclerView
        String[] projection = {
                TreatmentPhotosEntry.TR_PHOTO_ID,
                TreatmentPhotosEntry.COLUMN_U_ID,
                TreatmentPhotosEntry.COLUMN_DIS_ID,
                TreatmentPhotosEntry.COLUMN_TR_PHOTO_NAME,
                TreatmentPhotosEntry.COLUMN_TR_PHOTO_DATE,
                TreatmentPhotosEntry.COLUMN_TR_PHOTO_PATH};

        // выборку заболеванй делаем по _idUser
        String selection = TreatmentPhotosEntry.COLUMN_U_ID + "=? AND " + TreatmentPhotosEntry.COLUMN_DIS_ID + "=?";
        String[] selectionArgs = new String[]{String.valueOf(_idUser), String.valueOf(_idDisease)};

        Context mContext;
        /*if (HomeActivity.isTablet) {
            mContext = mTabletMainActivity;
        } else {
            mContext = mTreatmentActivity;

        }*/

        if (getActivity() instanceof TabletMainActivity) {
            mContext = mTabletMainActivity;
        } else {
            mContext = mTreatmentActivity;
        }

        // This loader will execute the ContentProvider's query method on a background thread
        // Loader грузит ВСЕ данные из таблицы users через Provider в diseaseRecyclerViewAdapter и далее в recyclerDiseases
        return new CursorLoader(mContext,   // Parent activity context
                TreatmentPhotosEntry.CONTENT_TREATMENT_PHOTOS_URI,   // Provider content URI to query = content://com.gmail.krbashianrafael.medpunkt/treatmentPhotos/
                projection,             // Columns to include in the resulting Cursor
                selection,                   // selection by TreatmentPhotosEntry.COLUMN_U_ID AND TreatmentPhotosEntry.COLUMN_DIS_ID
                selectionArgs,                   // selection arguments by _idUser AND _idDisease
                null);                  // Default sort order
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor cursor) {

        ArrayList<TreatmentPhotoItem> myData = treatmentPhotoRecyclerViewAdapter.getTreatmentPhotosList();
        myData.clear();

        if (cursor != null) {

            // устанавливаем курсор на исходную (на случай, если курсор используем повторно после прохождения цикла
            cursor.moveToPosition(-1);

            // проходим в цикле курсор и заполняем объектами DiseaseItem наш ArrayList<TreatmentPhotoItem> myData
            while (cursor.moveToNext()) {

                // Find the columns of disease attributes that we're interested in
                int trPhoto_IdColumnIndex = cursor.getColumnIndex(TreatmentPhotosEntry.TR_PHOTO_ID);
                int trPhotoUser_IdColumnIndex = cursor.getColumnIndex(TreatmentPhotosEntry.COLUMN_U_ID);
                int trPhotoDisease_IdColumnIndex = cursor.getColumnIndex(TreatmentPhotosEntry.COLUMN_DIS_ID);
                int trPhoto_nameColumnIndex = cursor.getColumnIndex(TreatmentPhotosEntry.COLUMN_TR_PHOTO_NAME);
                int trPhoto_dateColumnIndex = cursor.getColumnIndex(TreatmentPhotosEntry.COLUMN_TR_PHOTO_DATE);
                int trPhoto_pathColumnIndex = cursor.getColumnIndex(TreatmentPhotosEntry.COLUMN_TR_PHOTO_PATH);

                // Read the disease attributes from the Cursor for the current disease
                long _trPhotoId = cursor.getInt(trPhoto_IdColumnIndex);
                long _userId = cursor.getInt(trPhotoUser_IdColumnIndex);
                long _diseaseId = cursor.getInt(trPhotoDisease_IdColumnIndex);
                String trPhotoName = cursor.getString(trPhoto_nameColumnIndex);
                String trPhotoDate = cursor.getString(trPhoto_dateColumnIndex);
                String trPhotoUri = cursor.getString(trPhoto_pathColumnIndex);


                // добавляем новый DiseaseItem в ArrayList<DiseaseItem> myData
                myData.add(new TreatmentPhotoItem(_trPhotoId, _userId, _diseaseId, trPhotoName, trPhotoDate, trPhotoUri));
            }
        }

        // делаем сортировку снимков по дате
        Collections.sort(myData);

        recyclerTreatmentPhotos.setVisibility(View.VISIBLE);


        // делаем destroyLoader, чтоб он сам повторно не вызывался,
        // а вызывался при каждом входе в активити
        getLoaderManager().destroyLoader(TR_PHOTOS_IN_FRAGMENT_LOADER);

        //Log.d("2222", "myData.size() =" + myData.size());

        int myDataSize = myData.size();

        // если нет фото лечения
        if (myDataSize == 0) {
            recyclerTreatmentPhotos.setVisibility(View.INVISIBLE);
            txtAddPhotos.setVisibility(View.VISIBLE);

            // если в расширенном варианте окна на планшете

            /*if (HomeActivity.isTablet &&
                    ((ConstraintLayout.LayoutParams) mTabletMainActivity.ver_3_Guideline.getLayoutParams()).guidePercent == 0.00f) {*/

            if (HomeActivity.isTablet && TabletMainActivity.inWideView) {
                fabToFullScreen.setVisibility(View.INVISIBLE);
                verGuideline.setGuidelinePercent(1.0f);

                // ширину табов делаем одинаковыми
                LinearLayout layout = ((LinearLayout) ((LinearLayout) mTabletMainActivity.tabletTreatmentFragment.tabLayout.getChildAt(0)).getChildAt(1));
                LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) layout.getLayoutParams();
                layoutParams.weight = 1.00f;
                layout.setLayoutParams(layoutParams);
            }

        } else {

            // если это планшет и расширенном виде, то грзуим первое фото заболевания в расширенное окно

            /*if (HomeActivity.isTablet && mTabletMainActivity.inWideView) {

                TreatmentPhotoItem treatmentPhotoItem = myData.get(0);

                _idTrPhoto = treatmentPhotoItem.get_trPhotoId();
                treatmentPhotoFilePath = treatmentPhotoItem.getTrPhotoUri();
                textDateOfTreatmentPhoto = treatmentPhotoItem.getTrPhotoDate();
                textPhotoDescription = treatmentPhotoItem.getTrPhotoName();

                GlideApp.with(mTabletMainActivity)
                        .load(treatmentPhotoFilePath)
                        .listener(new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                //on load failed
                                *//*tabletMainActivity.tabletTreatmentFragment.treatmentPhotosFragment.
                                        fabToFullScreen.setVisibility(View.INVISIBLE);*//*

                                // чтоб файл освободился (для удаления),
                                // высвобождаем imagePhoto
                                myHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        Glide.with(mTabletMainActivity).
                                                clear(imgWideView);

                                        imgWideView.setImageResource(R.color.my_dark_gray);

                                        widePhotoErrView.setVisibility(View.VISIBLE);

                                        fabToFullScreen.setImageResource(R.drawable.ic_edit_white_24dp);
                                    }
                                });

                                //tabletMainActivity.tabletTreatmentFragment.treatmentPhotosFragment.errorOnPhotoLoading = true;

                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                //on load success

                                if (widePhotoErrView.getVisibility() == View.VISIBLE) {

                                    widePhotoErrView.setVisibility(View.GONE);

                                    fabToFullScreen.setImageResource(R.drawable.ic_zoom_out_photo_white_24dp);
                                }

                                //tabletMainActivity.tabletTreatmentFragment.treatmentPhotosFragment.errorOnPhotoLoading = false;

                                return false;
                            }
                        })
                        //.override(displayWidth, displayheight)
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .skipMemoryCache(true)
                        //.error(R.color.my_dark_gray)
                        .transition(DrawableTransitionOptions.withCrossFade())
                        .into(imgWideView);

            }*/
            // если есть фото лечения
            fabAddTreatmentPhotos.startAnimation(fabShowAnimation);

            // если в расширенном варианте окна на планшете
            /*if (HomeActivity.isTablet &&
                    ((ConstraintLayout.LayoutParams) mTabletMainActivity.ver_3_Guideline.getLayoutParams()).guidePercent == 0.00f) {*/

            if (HomeActivity.isTablet && TabletMainActivity.inWideView) {
                verGuideline.setGuidelinePercent(0.4f);
                fabToFullScreen.setVisibility(View.VISIBLE);
                fabToFullScreen.startAnimation(fabShowAnimation);

                // это расширяет таб "снимки"
                LinearLayout layout = ((LinearLayout) ((LinearLayout) mTabletMainActivity.tabletTreatmentFragment.tabLayout.getChildAt(0)).getChildAt(1));
                LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) layout.getLayoutParams();
                layoutParams.weight = 1.50f;
                layout.setLayoutParams(layoutParams);

                // если фото заболевание было удалено, но остались еще фото, то
                // выделяем первый элемент и после treatmentPhotoRecyclerViewAdapter.notifyDataSetChanged();
                // грузится его фото
                if (TabletMainActivity.treatmentPhotoDeleted) {
                    TreatmentPhotoItem treatmentPhotoItem = myData.get(0);
                    TabletMainActivity.selectedTreatmentPhoto_id = treatmentPhotoItem.get_trPhotoId();

                    TabletMainActivity.treatmentPhotoDeleted = false;
                }
            }
        }

        // оповещаем LayoutManager, что произошли изменения
        // LayoutManager обновляет RecyclerView
        treatmentPhotoRecyclerViewAdapter.notifyDataSetChanged();

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

    /*private class MyImageMatrixTouchHandler extends ImageMatrixTouchHandler {

        MyImageMatrixTouchHandler(Context context) {
            super(context);
            //setDoubleTapZoomFactor(2f); // здесь устанавливаем кратность увеличение при DoubleTap, по умолчанию = 2,5
            //ImageViewerCorrector crr = (ImageViewerCorrector) this.getImageMatrixCorrector();
            //crr.setMaxScale(20f); // здесь устанавливаем максимальную кратность увеличения, по умолчанию = 4
        }



        @Override
        public boolean onTouch(final View view, final MotionEvent event) {

            view.performClick();
            super.onTouch(view, event);

            return true;
        }
    }*/


}
