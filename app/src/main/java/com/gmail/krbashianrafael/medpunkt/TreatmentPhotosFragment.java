package com.gmail.krbashianrafael.medpunkt;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class TreatmentPhotosFragment extends Fragment {
    public TreatmentPhotosFragment(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.treatment_photos_fragment, container, false);

        return rootView;
    }
}
