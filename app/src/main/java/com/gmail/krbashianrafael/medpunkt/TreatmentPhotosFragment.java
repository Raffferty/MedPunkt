package com.gmail.krbashianrafael.medpunkt;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class TreatmentPhotosFragment extends Fragment {

    // TextView добавления фотоснимка лечения
    private TextView txtAddPhotos;

    private FloatingActionButton fabAddTreatmentPhotos;

    protected RecyclerView recyclerTreatmentPhotos;

    private LinearLayoutManager linearLayoutManager;

    private TreatmentPhotoRecyclerViewAdapter treatmentPhotoRecyclerViewAdapter;

    // boolean scrollToEnd статическая переменная для выставления флага в true после вставки нового элемента в список
    // этот флаг необходим для прокрутки списка вниз до последнего элемента, чтоб был виден вставленный элемент
    // переменная статическая, т.к. будет меняться из класса MedProvider в методе insertTreatmentPhoto
    public static boolean scrollToEnd = false;

    // временный элемент, далее будет в RecyclerView
    //private ScrollView scrollViewPhotos;

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
                intentToTreatmentPhoto.putExtra("newTreatmentPhoto", true);

                startActivity(intentToTreatmentPhoto);
            }
        });

        fabAddTreatmentPhotos = view.findViewById(R.id.fabAddTreatmentPhotos);
        fabAddTreatmentPhotos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentToTreatmentPhoto = new Intent(getContext(), FullscreenPhotoActivity.class);
                intentToTreatmentPhoto.putExtra("newTreatmentPhoto", true);

                startActivity(intentToTreatmentPhoto);
            }
        });
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        TreatmentActivity newTreaymentActivity = (TreatmentActivity) getActivity();

        if (newTreaymentActivity != null) {

            // в главном активити инициализируем фрагмент (есл он еще не инициализирован, т.е. если он еще null)
            if (newTreaymentActivity.treatmentPhotosFragment == null) {
                newTreaymentActivity.initTreatmentPhotosFragment();
            }

            // инициализируем linearLayoutManager
            linearLayoutManager = new LinearLayoutManager(newTreaymentActivity,
                    LinearLayoutManager.VERTICAL,false);

            // инизиализируем разделитель для элементов recyclerTreatmentPhotos
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
            recyclerTreatmentPhotos.addItemDecoration(itemDecoration);

            // устанавливаем LayoutManager для RecyclerView
            recyclerTreatmentPhotos.setLayoutManager(linearLayoutManager);

            // инициализируем TreatmentPhotoRecyclerViewAdapter
            treatmentPhotoRecyclerViewAdapter = new TreatmentPhotoRecyclerViewAdapter(newTreaymentActivity);

            // устанавливаем адаптер для RecyclerView
            recyclerTreatmentPhotos.setAdapter(treatmentPhotoRecyclerViewAdapter);

            ArrayList<TreatmentPhotoItem> myData = treatmentPhotoRecyclerViewAdapter.getTreatmentPhotosList();
            myData.clear();

            String pathToPhoto = getString(R.string.path_to_treatment_photo);

            // tempNewDisease - это временно для отработки в treatmentPhotoRecyclerView пустого листа
            if (!newTreaymentActivity.tempNewDisease){
                myData.add(new TreatmentPhotoItem(2,"25.06.2018","Кардиограмма",pathToPhoto));
                myData.add(new TreatmentPhotoItem(2,"26.06.2018","Узи",pathToPhoto));
                myData.add(new TreatmentPhotoItem(2,"27.06.2018","Давление",pathToPhoto));
                myData.add(new TreatmentPhotoItem(2,"28.06.2018","Анализы Кровь",pathToPhoto));
                myData.add(new TreatmentPhotoItem(2,"28.06.2018","Анализы Моча",pathToPhoto));
                myData.add(new TreatmentPhotoItem(2,"28.06.2018","Анализы Кал",pathToPhoto));
                myData.add(new TreatmentPhotoItem(2,"28.06.2018","Анализы 1",pathToPhoto));
                myData.add(new TreatmentPhotoItem(2,"28.06.2018","Анализы 2",pathToPhoto));
                myData.add(new TreatmentPhotoItem(2,"28.06.2018","Анализы 3",pathToPhoto));
                myData.add(new TreatmentPhotoItem(2,"28.06.2018","Анализы 4",pathToPhoto));
                myData.add(new TreatmentPhotoItem(2,"28.06.2018","Анализы 5",pathToPhoto));
                myData.add(new TreatmentPhotoItem(2,"28.06.2018","Анализы 6",pathToPhoto));
                myData.add(new TreatmentPhotoItem(2,"28.06.2018","Анализы 7",pathToPhoto));
                myData.add(new TreatmentPhotoItem(2,"28.06.2018","Анализы 8",pathToPhoto));
                myData.add(new TreatmentPhotoItem(2,"28.06.2018","Анализы 9",pathToPhoto));
                myData.add(new TreatmentPhotoItem(2,"28.06.2018","Анализы 10",pathToPhoto));
            }

            // если еще нет снимков, то делаем txtAddPhotos.setVisibility(View.VISIBLE);
            if (myData.size() == 0) {
                txtAddPhotos.setVisibility(View.VISIBLE);
                fabAddTreatmentPhotos.setVisibility(View.INVISIBLE);
            }
        }
    }
}
