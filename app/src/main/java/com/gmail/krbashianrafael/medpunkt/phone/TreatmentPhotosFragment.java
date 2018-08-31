package com.gmail.krbashianrafael.medpunkt.phone;


import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
import android.widget.TextView;

import com.gmail.krbashianrafael.medpunkt.FullscreenPhotoActivity;
import com.gmail.krbashianrafael.medpunkt.R;
import com.gmail.krbashianrafael.medpunkt.TreatmentPhotoItem;
import com.gmail.krbashianrafael.medpunkt.data.MedContract.TreatmentPhotosEntry;

import java.util.ArrayList;
import java.util.Collections;

public class TreatmentPhotosFragment extends Fragment
        implements android.support.v4.app.LoaderManager.LoaderCallbacks<Cursor> {

    // parent Activity
    private TreatmentActivity newTreaymentActivity;

    // id пользователя
    private long _idUser = 0;

    // id заболеввания
    private long _idDisease = 0;

    // TextView добавления фотоснимка лечения
    private TextView txtAddPhotos;

    private FloatingActionButton fabAddTreatmentPhotos;

    private Animation fabShowAnimation;

    protected RecyclerView recyclerTreatmentPhotos;

    private TreatmentPhotoRecyclerViewAdapter treatmentPhotoRecyclerViewAdapter;

    // boolean mScrollToStart статическая переменная для выставления флага в true после вставки нового элемента в список
    // переменная статическая, т.к. будет меняться из класса MedProvider в методе insertTreatmentPhoto
    public static boolean mScrollToStart = false;

    /**
     * Identifier for the user data loader
     * Лоадеров может много (они обрабатываются в case)
     * поэтому устанавливаем инициализатор для каждого лоадера
     * в данном случае private static final int TR_PHOTOS_LOADER = 2;
     */
    private static final int TR_PHOTOS_IN_FRAGMENT_LOADER = 2;

    public TreatmentPhotosFragment() {
        // нужен ПУСТОЙ конструктор
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.treatment_photos_fragment, container, false);
    }

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

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        newTreaymentActivity = (TreatmentActivity) getActivity();

        if (newTreaymentActivity != null) {

            fabShowAnimation = AnimationUtils.loadAnimation(newTreaymentActivity, R.anim.fab_show);
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
            if (newTreaymentActivity.treatmentPhotosFragment == null) {
                newTreaymentActivity.initTreatmentPhotosFragment();
            }

            _idUser = newTreaymentActivity._idUser;
            _idDisease = newTreaymentActivity._idDisease;

            // инициализируем linearLayoutManager
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(newTreaymentActivity,
                    LinearLayoutManager.VERTICAL, false);

            /*// инизиализируем разделитель для элементов recyclerTreatmentPhotos
            DividerItemDecoration itemDecoration = new DividerItemDecoration(
                    recyclerTreatmentPhotos.getContext(), linearLayoutManager.getOrientation()
            );

            //инициализируем Drawable, который будет установлен как разделитель между элементами
            Drawable divider_blue = ContextCompat.getDrawable(newTreaymentActivity, R.drawable.blue_drawable);

            //устанавливаем divider_blue как разделитель между элементами
            if (divider_blue != null) {
                itemDecoration.setDrawable(divider_blue);
            }

            //устанавливаем созданный и настроенный объект DividerItemDecoration нашему recyclerView
            recyclerTreatmentPhotos.addItemDecoration(itemDecoration);*/

            // устанавливаем LayoutManager для RecyclerView
            recyclerTreatmentPhotos.setLayoutManager(linearLayoutManager);

            // инициализируем TreatmentPhotoRecyclerViewAdapter
            treatmentPhotoRecyclerViewAdapter = new TreatmentPhotoRecyclerViewAdapter(newTreaymentActivity);

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

        // This loader will execute the ContentProvider's query method on a background thread
        // Loader грузит ВСЕ данные из таблицы users через Provider в diseaseRecyclerViewAdapter и далее в recyclerDiseases
        return new CursorLoader(newTreaymentActivity,   // Parent activity context
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

        // оповещаем LayoutManager, что произошли изменения
        // LayoutManager обновляет RecyclerView
        treatmentPhotoRecyclerViewAdapter.notifyDataSetChanged();

        // делаем destroyLoader, чтоб он сам повторно не вызывался,
        // а вызывался при каждом входе в активити
        getLoaderManager().destroyLoader(TR_PHOTOS_IN_FRAGMENT_LOADER);

        // если нет фото лечений, то делаем txtAddPhotos.setVisibility(View.VISIBLE);
        // fabAddTreatmentPhotos.setVisibility(View.VISIBLE);
        if (myData.size() == 0) {
            txtAddPhotos.setVisibility(View.VISIBLE);
        } else {
            //fabAddTreatmentPhotos.setVisibility(View.VISIBLE);
            fabAddTreatmentPhotos.startAnimation(fabShowAnimation);
        }

        // если флаг mScrollToStart выставлен в true, то прокручиваем RecyclerView вверх до первого элемента,
        // и снова scrollToEnd выставляем в false
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
