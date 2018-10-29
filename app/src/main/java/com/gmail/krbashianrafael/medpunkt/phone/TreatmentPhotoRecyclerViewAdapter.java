package com.gmail.krbashianrafael.medpunkt.phone;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gmail.krbashianrafael.medpunkt.shared.FullscreenPhotoActivity;
import com.gmail.krbashianrafael.medpunkt.R;
import com.gmail.krbashianrafael.medpunkt.shared.TreatmentPhotoItem;

import java.util.ArrayList;

class TreatmentPhotoRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final Context mContext;
    private final ArrayList<TreatmentPhotoItem> treatmentPhotosList;

    TreatmentPhotoRecyclerViewAdapter(Context context) {
        mContext = context;
        this.treatmentPhotosList = new ArrayList<>();
    }

    ArrayList<TreatmentPhotoItem> getTreatmentPhotosList() {
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
        long _trPhotoId = treatmentPhotosList.get(position).get_trPhotoId();
        long _userId = treatmentPhotosList.get(position).get_userId();
        long _diseaseId = treatmentPhotosList.get(position).get_diseaseId();

        String itemDate = treatmentPhotosList.get(position).getTrPhotoDate();
        String itemName = treatmentPhotosList.get(position).getTrPhotoName();
        String itemPhotoUri = treatmentPhotosList.get(position).getTrPhotoUri();

        ((TreatmentPhotoHolder) holder)._trPhotoId.setText(String.valueOf(_trPhotoId));
        ((TreatmentPhotoHolder) holder)._userId.setText(String.valueOf(_userId));
        ((TreatmentPhotoHolder) holder)._diseaseId.setText(String.valueOf(_diseaseId));

        ((TreatmentPhotoHolder) holder).itemUri.setText(itemPhotoUri);

        ((TreatmentPhotoHolder) holder).itemDate.setText(itemDate);
        ((TreatmentPhotoHolder) holder).itemName.setText(itemName);
    }

    @Override
    public int getItemCount() {
        return treatmentPhotosList.size();
    }

    static class TreatmentPhotoHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        final Context myContext;

        final LinearLayout treatmentPhotoItem;

        final TextView _trPhotoId;
        final TextView _userId;
        final TextView _diseaseId;
        final TextView itemUri;
        final TextView itemDate;
        final TextView itemName;

        TreatmentPhotoHolder(View itemView, Context context) {
            super(itemView);

            myContext = context;

            _trPhotoId = itemView.findViewById(R.id.tr_photo_id);
            _userId = itemView.findViewById(R.id.user_id);
            _diseaseId = itemView.findViewById(R.id.disease_id);
            itemUri = itemView.findViewById(R.id.recycler_photo_item_uri);
            itemDate = itemView.findViewById(R.id.photo_item_date);
            itemName = itemView.findViewById(R.id.recycler_photo_item_name);

            treatmentPhotoItem = itemView.findViewById(R.id.recycler_treatment_photo_item);

            treatmentPhotoItem.setOnClickListener(this);
        }

        @Override
        public void onClick(final View view) {
            if (myContext == null) {
                return;
            }

            view.setBackgroundColor(myContext.getResources().getColor(R.color.my_blue));

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intentToTreatmentPhoto = new Intent(myContext, FullscreenPhotoActivity.class);

                    intentToTreatmentPhoto.putExtra("_idTrPhoto", Long.valueOf(_trPhotoId.getText().toString()));

                    intentToTreatmentPhoto.putExtra("_idUser", Long.valueOf(_userId.getText().toString()));
                    intentToTreatmentPhoto.putExtra("_idDisease", Long.valueOf(_diseaseId.getText().toString()));

                    intentToTreatmentPhoto.putExtra("treatmentPhotoFilePath", itemUri.getText());
                    intentToTreatmentPhoto.putExtra("textDateOfTreatmentPhoto", itemDate.getText());
                    intentToTreatmentPhoto.putExtra("textPhotoDescription", itemName.getText());

                    myContext.startActivity(intentToTreatmentPhoto);
                }
            }, 250);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    view.setBackgroundColor(Color.TRANSPARENT);
                }
            }, 500);
        }
    }
}
