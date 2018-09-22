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

import com.gmail.krbashianrafael.medpunkt.DiseaseItem;
import com.gmail.krbashianrafael.medpunkt.HomeActivity;
import com.gmail.krbashianrafael.medpunkt.R;
import com.gmail.krbashianrafael.medpunkt.tablet.TabletMainActivity;

import java.util.ArrayList;

import static com.gmail.krbashianrafael.medpunkt.tablet.TabletMainActivity.TABLET_TREATMENT_FRAGMENT;

public class DiseaseRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private ArrayList<DiseaseItem> diseaseList;

    public static long selected_disease_id = 0;

    public DiseaseRecyclerViewAdapter(Context context) {
        mContext = context;
        this.diseaseList = new ArrayList<>();
        selected_disease_id = 0;
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

        // если это планшет, то выделенный элемент будет красится в зеленый цвет,
        // а остальные в TRANSPARENT
        if (HomeActivity.isTablet) {
            // если только один элемент заболевания
            // то его _id и будет selected
            if (diseaseList.size() == 1) {
                selected_disease_id = _diseaseId;
            }

            if (selected_disease_id == _diseaseId) {
                ((DiseaseHolder) holder).diseasesItem.setBackgroundColor(mContext.getResources().getColor(R.color.light_green));
            } else {
                ((DiseaseHolder) holder).diseasesItem.setBackgroundColor(Color.TRANSPARENT);
            }
        }
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

        LinearLayout diseasesItem;
        //FrameLayout diseaseEdit;

        DiseaseHolder(View itemView, Context context) {
            super(itemView);

            myContext = context;

            _diseaseId = itemView.findViewById(R.id.disease_item_id);
            _diseaseUserId = itemView.findViewById(R.id.disease_item_user_id);
            diseaseDate = itemView.findViewById(R.id.disease_item_date);
            diseaseName = itemView.findViewById(R.id.disease_item_name);
            treatmentText = itemView.findViewById(R.id.treatment_text);

            diseasesItem = itemView.findViewById(R.id.recycler_diseases_item);

            diseasesItem.setOnClickListener(this);
        }

        @Override
        public void onClick(final View view) {
            if (myContext == null) {
                return;
            }

            long disease_id_inEdit = Long.valueOf(_diseaseId.getText().toString());

            if (!HomeActivity.isTablet) {
                // если нажат элемент с названием заболевания
                // если это телефон
                view.setBackgroundColor(myContext.getResources().getColor(R.color.my_blue));

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent treatmentIntent = new Intent(myContext, TreatmentActivity.class);
                        treatmentIntent.putExtra("_idDisease", Long.valueOf(_diseaseId.getText().toString()));
                        treatmentIntent.putExtra("_idUser", Long.valueOf(_diseaseUserId.getText().toString()));
                        treatmentIntent.putExtra("diseaseName", diseaseName.getText().toString());
                        treatmentIntent.putExtra("diseaseDate", diseaseDate.getText().toString());
                        treatmentIntent.putExtra("textTreatment", treatmentText.getText().toString());

                        myContext.startActivity(treatmentIntent);

                    }
                }, 250);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        view.setBackgroundColor(Color.TRANSPARENT);
                    }
                }, 500);
            } else {
                //если это планшет и делается клик НЕ на том же элементе (чтоб дважды не грузить ту же информацию)
                if (selected_disease_id != disease_id_inEdit) {

                    TabletMainActivity tabletMainActivity = (TabletMainActivity) myContext;

                    // устанавливаем новое значение для selected_disease_id
                    // и заново отрисовываем все видимые элементы в diseaseRecyclerView
                    // чтоб закрасить выделенный элемент
                    selected_disease_id = disease_id_inEdit;
                    tabletMainActivity.tabletDiseasesFragment.diseaseRecyclerViewAdapter.notifyDataSetChanged();

                    // далее отрисовываем нужные поля в фрагментах
                    tabletMainActivity.tabletDiseasesFragment.txtTabletDiseases.setBackgroundColor(myContext.getResources().getColor(R.color.colorPrimary));
                    tabletMainActivity.tabletDiseasesFragment.txtTabletDiseases.setText(R.string.diseases_what_text);

                    tabletMainActivity.unBlur(TABLET_TREATMENT_FRAGMENT);

                    tabletMainActivity.tabletTreatmentFragment.treatmentDescriptionFragment.fabEditTreatmentDescripton.startAnimation(
                            tabletMainActivity.tabletTreatmentFragment.fabShowAnimation
                    );

                    tabletMainActivity.tabletTreatmentFragment.tabLayout.setVisibility(View.VISIBLE);
                    tabletMainActivity.tabletTreatmentFragment.viewPager.setVisibility(View.VISIBLE);

                    tabletMainActivity.tabletTreatmentFragment.set_idDisease(disease_id_inEdit);
                    tabletMainActivity.tabletTreatmentFragment.set_idUser(Long.valueOf(_diseaseUserId.getText().toString()));
                    tabletMainActivity.tabletTreatmentFragment.setTextDiseaseName(diseaseName.getText().toString());
                    tabletMainActivity.tabletTreatmentFragment.setTextDateOfDisease(diseaseDate.getText().toString());
                    tabletMainActivity.tabletTreatmentFragment.setTextTreatment(treatmentText.getText().toString());

                    TabletMainActivity.disease_IdInEdit = disease_id_inEdit;
                }
            }
        }
    }
}
