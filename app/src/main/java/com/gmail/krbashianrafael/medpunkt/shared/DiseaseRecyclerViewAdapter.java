package com.gmail.krbashianrafael.medpunkt.shared;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gmail.krbashianrafael.medpunkt.R;
import com.gmail.krbashianrafael.medpunkt.phone.TreatmentActivity;
import com.gmail.krbashianrafael.medpunkt.tablet.TabletDiseasesFragment;
import com.gmail.krbashianrafael.medpunkt.tablet.TabletMainActivity;

import java.util.ArrayList;
import java.util.Objects;
//import static com.gmail.krbashianrafael.medpunkt.tablet.TabletMainActivity.tempTextDateOfTreatment;
//import static com.gmail.krbashianrafael.medpunkt.tablet.TabletMainActivity.tempTextDiseaseName;
//import static com.gmail.krbashianrafael.medpunkt.tablet.TabletMainActivity.tempTextTreatment;

public class DiseaseRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final Context mContext;
    private final ArrayList<DiseaseItem> diseaseList;

    //static long TabletMainActivity.selectedDisease_id = 0;

    public DiseaseRecyclerViewAdapter(Context context) {
        mContext = context;
        this.diseaseList = new ArrayList<>();
        //TabletMainActivity.selectedDisease_id = 0;
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

        // если это планшет, то выделенный элемент будет окрашен в голубой цвет,
        // а остальные в TRANSPARENT
        if (HomeActivity.isTablet) {
            // если только один элемент заболевания
            // то его _id и будет selected
            if (diseaseList.size() == 1) {
                //TabletMainActivity.selectedDisease_id = _diseaseId;
                ((DiseaseHolder) holder).diseasesItem.setBackgroundColor(mContext.getResources().getColor(R.color.my_blue));
                TabletDiseasesFragment.diseaseSelected = true;

            } /*else if (TabletMainActivity.insertedDisease_id != 0) {
                // добавленное заболевание будет сразу выделенным
                TabletMainActivity.selectedDisease_id = TabletMainActivity.insertedDisease_id;
                TabletMainActivity.insertedDisease_id = 0;
            }*/ /*else if (TabletMainActivity.insertedDisease_id == _diseaseId) {
                ((DiseaseHolder) holder).diseasesItem.setBackgroundColor(mContext.getResources().getColor(R.color.my_blue));
                TabletDiseasesFragment.diseaseSelected = true;
                TabletMainActivity.selectedDisease_id = TabletMainActivity.insertedDisease_id;
                //TabletMainActivity.insertedDisease_id = 0;

            }*/ else if (TabletMainActivity.selectedDisease_id == _diseaseId) {
                // добавленное заболевание будет сразу выделенным
                // т.к. в MedProvider есть запись TabletMainActivity.selectedDisease_id = TabletMainActivity.insertedDisease_id;
                ((DiseaseHolder) holder).diseasesItem.setBackgroundColor(mContext.getResources().getColor(R.color.my_blue));
                TabletDiseasesFragment.diseaseSelected = true;

            } else {
                ((DiseaseHolder) holder).diseasesItem.setBackgroundColor(Color.TRANSPARENT);
            }

        }


        /*if (TabletMainActivity.selectedDisease_id == _diseaseId) {
            ((DiseaseHolder) holder).diseasesItem.setBackgroundColor(mContext.getResources().getColor(R.color.my_blue));

            TabletDiseasesFragment.diseaseSelected = true;

        } else {
            ((DiseaseHolder) holder).diseasesItem.setBackgroundColor(Color.TRANSPARENT);
        }*/
    }


    @Override
    public int getItemCount() {
        return diseaseList.size();
    }

    public static class DiseaseHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        final Context myContext;
        TabletMainActivity tabletMainActivity;

        final TextView _diseaseId;
        final TextView _diseaseUserId;
        final TextView diseaseDate;
        final TextView diseaseName;
        final TextView treatmentText;

        final LinearLayout diseasesItem;
        //FrameLayout diseaseEdit;

        long clicked_disease_id = 0;

        DiseaseHolder(View itemView, Context context) {
            super(itemView);

            myContext = context;

            if (myContext instanceof TabletMainActivity) {
                tabletMainActivity = (TabletMainActivity) myContext;
            }

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

            clicked_disease_id = Long.valueOf(_diseaseId.getText().toString());

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
                if (TabletMainActivity.selectedDisease_id != clicked_disease_id) {
                    //tabletMainActivity = (TabletMainActivity) myContext;

                    TabletDiseasesFragment.diseaseSelected = true;

                    // ставим на таб "описание"
                    Objects.requireNonNull(tabletMainActivity.tabletTreatmentFragment.tabLayout.getTabAt(0)).select();

                    tabletMainActivity.tabletTreatmentFragment.zoomOutTabletTreatment.setVisibility(View.VISIBLE);

                   /* tabletMainActivity.ver_1_Guideline.setGuidelinePercent(0.0f);
                    tabletMainActivity.ver_2_Guideline.setGuidelinePercent(0.3f);
                    tabletMainActivity.ver_3_Guideline.setGuidelinePercent(0.6f);
                    tabletMainActivity.ver_4_Guideline.setGuidelinePercent(1.0f);*/

                    float percentVerGuideline_2 = ((ConstraintLayout.LayoutParams) tabletMainActivity.ver_2_Guideline.getLayoutParams()).guidePercent;

                    if (percentVerGuideline_2 != 0.30f) {
                        /*tabletMainActivity.ver_2_Guideline.setGuidelinePercent(0.3f);
                        tabletMainActivity.ver_3_Guideline.setGuidelinePercent(0.6f);*/

                        //tabletMainActivity.tabletDiseasesFragment.animVerGuideline_2_from_50_to_30.start();

                        //tabletMainActivity.tabletDiseasesFragment.animVerGuideline_3_from_100_to_60.start();

                        tabletMainActivity.ver_2_Guideline.setGuidelinePercent(0.3f);
                        tabletMainActivity.ver_3_Guideline.setGuidelinePercent(0.6f);


                        /*new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                tabletMainActivity.ver_2_Guideline.setGuidelinePercent(0.3f);
                                tabletMainActivity.ver_3_Guideline.setGuidelinePercent(0.6f);
                            }
                        }, 300);*/
                    }

                    // если описание заболевание с стадии редактирования,
                    // то сначала делается Cancel
                    /*if (tabletMainActivity.diseaseAndTreatmentInEdit) {
                        if (tabletMainActivity.diseaseAndTreatmentHasNotChanged()) {
                            tabletMainActivity.cancel();
                            tabletDiseaseSelected();
                        } else {
                            tabletMainActivity.showUnsavedChangesDialog(this);
                        }
                    } else {*/
                    tabletMainActivity.tabletTreatmentFragment.treatmentDescriptionFragment.fabEditTreatmentDescripton.startAnimation(
                            tabletMainActivity.tabletTreatmentFragment.fabShowAnimation
                    );

                    tabletDiseaseSelected();
                    //}
                }
            }
        }

        void tabletDiseaseSelected() {
            tabletMainActivity.tabletTreatmentFragment.set_idDisease(clicked_disease_id);
            tabletMainActivity.tabletTreatmentFragment.set_idUser(Long.valueOf(_diseaseUserId.getText().toString()));
            tabletMainActivity.tabletTreatmentFragment.setTextDiseaseName(diseaseName.getText().toString());
            tabletMainActivity.tabletTreatmentFragment.setTextDateOfDisease(diseaseDate.getText().toString());
            tabletMainActivity.tabletTreatmentFragment.setTextTreatment(treatmentText.getText().toString());

            // грузим снимки этого заболевания
            tabletMainActivity.tabletTreatmentFragment.treatmentPhotosFragment.initTreatmentPhotosLoader();

            tabletMainActivity.tempTextDiseaseName = tabletMainActivity.tabletTreatmentFragment.editTextDiseaseName.getText().toString();
            tabletMainActivity.tempTextDateOfTreatment = tabletMainActivity.tabletTreatmentFragment.editTextDateOfDisease.getText().toString();
            tabletMainActivity.tempTextTreatment = tabletMainActivity.tabletTreatmentFragment.treatmentDescriptionFragment.editTextTreatment.getText().toString();

            //tabletMainActivity.disease_IdInEdit = clicked_disease_id;

            // устанавливаем новое значение для selected_disease_id
            // и заново отрисовываем все видимые элементы в diseaseRecyclerView
            // чтоб закрасить выделенный элемент
            TabletMainActivity.selectedDisease_id = clicked_disease_id;
            tabletMainActivity.tabletDiseasesFragment.diseaseRecyclerViewAdapter.notifyDataSetChanged();

            // далее отрисовываем нужные поля в фрагментах
                    /*tabletMainActivity.tabletDiseasesFragment.txtTabletDiseases.setBackgroundColor(myContext.getResources().getColor(R.color.colorPrimary));
                    tabletMainActivity.tabletDiseasesFragment.txtTabletDiseases.setText(R.string.diseases_what_text);*/

            //tabletMainActivity.unBlur(TABLET_TREATMENT_FRAGMENT);

            tabletMainActivity.tabletTreatmentFragment.tabLayout.setVisibility(View.VISIBLE);
            tabletMainActivity.tabletTreatmentFragment.viewPager.setVisibility(View.VISIBLE);
        }
    }
}
