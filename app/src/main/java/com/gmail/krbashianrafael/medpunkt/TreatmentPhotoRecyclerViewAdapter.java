package com.gmail.krbashianrafael.medpunkt;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class TreatmentPhotoRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private ArrayList<TreatmentPhotoItem> treatmentPhotosList;

    TreatmentPhotoRecyclerViewAdapter(Context context) {
        mContext = context;
        this.treatmentPhotosList = new ArrayList<>();
    }

    public ArrayList<TreatmentPhotoItem> getTreatmentPhotosList() {
        return treatmentPhotosList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View mView = LayoutInflater.from(parent.getContext()).inflate(R.layout.treatmet_photo_recyclerview_item, parent, false);
        return new TreatmentPhotoHolder(mView, mContext);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        int _treatmentId = treatmentPhotosList.get(position).get_treatmentId();
        String itemDate = treatmentPhotosList.get(position).getItemDate();
        String itemName = treatmentPhotosList.get(position).getItemName();
        String itemPhotoUri = treatmentPhotosList.get(position).getItemPhotoUri();

        // _treatmentId прописываем в "невидимое" _treatment_id (т.к. размеры этого TextView в нулях)
        // для его дальнейшего использования при onClick на itemView
        ((TreatmentPhotoHolder) holder)._treatmentId.setText(String.valueOf(_treatmentId));

        // itemUri прописываем в "невидимое" recycler_photo_item_uri (т.к. размеры этого TextView в нулях)
        // для его дальнейшего использования при onClick на itemView
        ((TreatmentPhotoHolder) holder).itemUri.setText(itemPhotoUri);

        ((TreatmentPhotoHolder) holder).itemDate.setText(itemDate);
        ((TreatmentPhotoHolder) holder).itemName.setText(itemName);
    }

    @Override
    public int getItemCount() {
        return treatmentPhotosList.size();
    }

    public static class TreatmentPhotoHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        Context myContext;
        TextView _treatmentId;
        TextView itemUri;
        TextView itemDate;
        TextView itemName;

        TreatmentPhotoHolder(View itemView, Context context) {
            super(itemView);

            myContext = context;

            _treatmentId = itemView.findViewById(R.id._treatment_id);
            itemUri = itemView.findViewById(R.id.recycler_photo_item_uri);
            itemDate = itemView.findViewById(R.id.photo_item_date);
            itemName = itemView.findViewById(R.id.recycler_photo_item_name);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (myContext==null) {
                return;
            }

            Intent intentToTreatmentPhoto = new Intent(myContext, FullscreenPhotoActivity.class);
            intentToTreatmentPhoto.putExtra("_idDisease", _treatmentId.getText());
            intentToTreatmentPhoto.putExtra("treatmentPhotoFilePath", itemUri.getText());
            intentToTreatmentPhoto.putExtra("textDateOfTreatmentPhoto", itemDate.getText());
            intentToTreatmentPhoto.putExtra("textPhotoDescription", itemName.getText());

            myContext.startActivity(intentToTreatmentPhoto);
        }
    }
}
