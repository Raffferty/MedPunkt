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

public class DiseaseRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static Context mContext;
    private ArrayList<DiseaseItem> diseaseList;

    DiseaseRecyclerViewAdapter(Context context) {
        mContext = context;
        this.diseaseList = new ArrayList<>();
    }

    public ArrayList<DiseaseItem> getDiseaseList() {
        return diseaseList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View mView = LayoutInflater.from(parent.getContext()).inflate(R.layout.diseases_recyclerview_item, parent, false);
        return new DiseaseHolder(mView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        int _diseaseId = diseaseList.get(position).get_diseaseId();
        String diseaseDate = diseaseList.get(position).getDiseaseDate();
        String diseaseName = diseaseList.get(position).getDiseaseName();
        String treatmentText = diseaseList.get(position).getTreatmentText();

        // _diseaseId прописываем в "невидимое" _treatment_id (т.к. размеры этого TextView в нулях)
        // для его дальнейшего использования при onClick на itemView
        ((DiseaseHolder) holder)._diseaseId.setText(String.valueOf(_diseaseId));

        ((DiseaseHolder) holder).diseaseDate.setText(diseaseDate);
        ((DiseaseHolder) holder).diseaseName.setText(diseaseName);
        ((DiseaseHolder) holder).treatmentText.setText(treatmentText);
    }

    @Override
    public int getItemCount() {
        return diseaseList.size();
    }

    public static class DiseaseHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView _diseaseId;
        TextView diseaseDate;
        TextView diseaseName;
        TextView treatmentText;

        DiseaseHolder(View itemView) {
            super(itemView);

            _diseaseId = itemView.findViewById(R.id.disease_item_id);
            diseaseDate = itemView.findViewById(R.id.disease_item_date);
            diseaseName = itemView.findViewById(R.id.disease_item_name);
            treatmentText = itemView.findViewById(R.id.treatment_text);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            Intent treatmentIntent = new Intent(mContext, TreatmentActivity.class);
            treatmentIntent.putExtra("_idDisease", _diseaseId.getText());

            treatmentIntent.putExtra("diseaseDate", diseaseDate.getText());

            treatmentIntent.putExtra("diseaseName", diseaseName.getText());
            treatmentIntent.putExtra("textTreatment",treatmentText.getText());

            mContext.startActivity(treatmentIntent);
        }
    }
}
