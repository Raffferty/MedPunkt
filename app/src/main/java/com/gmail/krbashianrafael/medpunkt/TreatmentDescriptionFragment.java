package com.gmail.krbashianrafael.medpunkt;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class TreatmentDescriptionFragment extends Fragment {
    public TreatmentDescriptionFragment(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.treatment_description_fragment, container, false);

        return rootView;
    }

}
