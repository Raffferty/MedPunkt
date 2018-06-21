package com.gmail.krbashianrafael.medpunkt;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

public class TreatmentPhotosFragment extends Fragment {

    TextView txtAddPhotos;
    FloatingActionButton fabAddTreatmentPhotos;
    ScrollView scrollViewPhotos;

    public TreatmentPhotosFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.treatment_photos_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView recyclerTreatmentPhotos = view.findViewById(R.id.recycler_treatment_photos);

        scrollViewPhotos= view.findViewById(R.id.scrollViewPhotos);

        txtAddPhotos = view.findViewById(R.id.txt_empty_photos);
        txtAddPhotos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //textDiseaseName = editTextDiseaseName.getText().toString().trim();
                //textTreatment = editTextTreatment.getText().toString();

                Intent intentToTreatmentPhoto = new Intent(getContext(), FullscreenPhotoActivity.class);
                intentToTreatmentPhoto.putExtra("newTreatmentPhoto", true);

                startActivity(intentToTreatmentPhoto);
            }
        });

        fabAddTreatmentPhotos = view.findViewById(R.id.fabAddTreatmentPhotos);
        fabAddTreatmentPhotos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //textDiseaseName = editTextDiseaseName.getText().toString().trim();
                //textTreatment = editTextTreatment.getText().toString();

                Intent intentToTreatmentPhoto = new Intent(getContext(), FullscreenPhotoActivity.class);
                intentToTreatmentPhoto.putExtra("newTreatmentPhoto", true);

                startActivity(intentToTreatmentPhoto);
            }
        });


        // это фиктивное фото заболевания
        final LinearLayout recyclerTreatmentPhotoItem = view.findViewById(R.id.recycler_treatment_photo_item);
        recyclerTreatmentPhotoItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentToTreatmentPhoto = new Intent(getContext(), FullscreenPhotoActivity.class);
                intentToTreatmentPhoto.putExtra("_idDisease", 2);
                intentToTreatmentPhoto.putExtra("treatmentPhotoFilePath", getString(R.string.path_to_treatment_photo));
                intentToTreatmentPhoto.putExtra("textPhotoDescription", "Рентген");
                intentToTreatmentPhoto.putExtra("textDateOfTreatmentPhoto", "01.02.2018 ");
                startActivity(intentToTreatmentPhoto);
            }
        });


    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        NewTreatmentActivity newTreaymentActivity = (NewTreatmentActivity) getActivity();

        if (newTreaymentActivity != null) {
            if (newTreaymentActivity.newDisease){
                txtAddPhotos.setVisibility(View.VISIBLE);
                fabAddTreatmentPhotos.setVisibility(View.INVISIBLE);
                scrollViewPhotos.setVisibility(View.INVISIBLE);
            }
        }
    }
}
