package com.gmail.krbashianrafael.medpunkt.phone;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.gmail.krbashianrafael.medpunkt.DiseaseItem;
import com.gmail.krbashianrafael.medpunkt.R;

import java.util.ArrayList;

public class DiseaseRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
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
        return new DiseaseHolder(mView, mContext);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        long _diseaseId = diseaseList.get(position).get_diseaseId();
        long _diseaseUserId = diseaseList.get(position).get_diseaseUserId();
        String diseaseDate = diseaseList.get(position).getDiseaseDate();
        String diseaseName = diseaseList.get(position).getDiseaseName();
        String treatmentText = diseaseList.get(position).getTreatmentText();

        ((DiseaseHolder) holder)._diseaseId.setText(String.valueOf(_diseaseId));
        ((DiseaseHolder) holder)._diseaseUserId.setText(String.valueOf(_diseaseUserId));

        ((DiseaseHolder) holder).diseaseDate.setText(diseaseDate);
        ((DiseaseHolder) holder).diseaseName.setText(diseaseName);
        ((DiseaseHolder) holder).treatmentText.setText(treatmentText);
    }

    @Override
    public int getItemCount() {
        return diseaseList.size();
    }

    public static class DiseaseHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        Context myContext;

        TextView _diseaseId;
        TextView _diseaseUserId;
        TextView diseaseDate;
        TextView diseaseName;
        TextView treatmentText;

        DiseaseHolder(View itemView, Context context) {
            super(itemView);

            myContext = context;

            _diseaseId = itemView.findViewById(R.id.disease_item_id);
            _diseaseUserId = itemView.findViewById(R.id.disease_item_user_id);
            diseaseDate = itemView.findViewById(R.id.disease_item_date);
            diseaseName = itemView.findViewById(R.id.disease_item_name);
            treatmentText = itemView.findViewById(R.id.treatment_text);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (myContext==null){
                return;
            }

            Intent treatmentIntent = new Intent(myContext, TreatmentActivity.class);
            treatmentIntent.putExtra("_idDisease", Long.valueOf(_diseaseId.getText().toString()));
            treatmentIntent.putExtra("_idUser", Long.valueOf(_diseaseUserId.getText().toString()));
            treatmentIntent.putExtra("diseaseName", diseaseName.getText());
            treatmentIntent.putExtra("diseaseDate", diseaseDate.getText());
            treatmentIntent.putExtra("textTreatment",treatmentText.getText());

            myContext.startActivity(treatmentIntent);
        }
    }
}
