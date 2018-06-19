package com.gmail.krbashianrafael.medpunkt;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class TreatmentDescriptionFragment extends Fragment {
    public TreatmentDescriptionFragment(){

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        int containerHeight = container.getHeight();
        Log.d("container", "containerHeight = " + containerHeight);

        final View rootView = inflater.inflate(R.layout.treatment_description_fragment, container, false);

        int rootViewHeight = rootView.getHeight();
        Log.d("container", "rootView = " + rootViewHeight);

        rootView.post(new Runnable() {
            @Override
            public void run() {
                int rootViewRunHeight = rootView.getHeight();
                Log.d("container", "rootViewRunHeight = " + rootViewRunHeight);

            }
        });


        return rootView;
    }

}
