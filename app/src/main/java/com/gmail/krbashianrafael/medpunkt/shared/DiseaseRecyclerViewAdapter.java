package com.gmail.krbashianrafael.medpunkt.shared;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.transition.AutoTransition;
import android.transition.Transition;
import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gmail.krbashianrafael.medpunkt.R;
import com.gmail.krbashianrafael.medpunkt.phone.TreatmentActivity;
import com.gmail.krbashianrafael.medpunkt.tablet.TabletDiseasesFragment;
import com.gmail.krbashianrafael.medpunkt.tablet.TabletMainActivity;

import java.util.ArrayList;
import java.util.Objects;

public class DiseaseRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final Context mContext;
    private final ArrayList<DiseaseItem> diseaseList;

    public DiseaseRecyclerViewAdapter(Context context) {
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

        // если это планшет, то выделенный элемент будет окрашен в голубой цвет,
        // а остальные в TRANSPARENT
        if (HomeActivity.isTablet) {
            if (TabletMainActivity.selectedDisease_id == _diseaseId) {
                // добавленное заболевание будет сразу выделенным
                // т.к. в MedProvider есть запись TabletMainActivity.selectedDisease_id = TabletMainActivity.insertedDisease_id;
                ((DiseaseHolder) holder).diseasesItem.setBackgroundColor(mContext.getResources().getColor(R.color.my_blue));
                TabletDiseasesFragment.diseaseSelected = true;

            } else {
                ((DiseaseHolder) holder).diseasesItem.setBackgroundColor(Color.TRANSPARENT);
            }
        }
    }


    @Override
    public int getItemCount() {
        return diseaseList.size();
    }

    static class DiseaseHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        final Context myContext;
        TabletMainActivity tabletMainActivity;

        final TextView _diseaseId;
        final TextView _diseaseUserId;
        final TextView diseaseDate;
        final TextView diseaseName;
        final TextView treatmentText;

        final LinearLayout diseasesItem;

        long clicked_disease_id = 0;

        final AutoTransition tabletDiseaseItemClickTransition;

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

            tabletDiseaseItemClickTransition = new AutoTransition();
            tabletDiseaseItemClickTransition.setDuration(280L);
            tabletDiseaseItemClickTransition.setInterpolator(new LinearInterpolator());

            tabletDiseaseItemClickTransition.addListener(new Transition.TransitionListener() {
                @Override
                public void onTransitionStart(Transition transition) {

                }

                @Override
                public void onTransitionEnd(Transition transition) {
                    tabletDiseaseSelected();
                }

                @Override
                public void onTransitionCancel(Transition transition) {

                }

                @Override
                public void onTransitionPause(Transition transition) {

                }

                @Override
                public void onTransitionResume(Transition transition) {

                }
            });
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

                    TabletDiseasesFragment.diseaseSelected = true;

                    // ставим на таб "описание"
                    if (tabletMainActivity.tabletTreatmentFragment.tabLayout.getSelectedTabPosition() == 1) {
                        Objects.requireNonNull(tabletMainActivity.tabletTreatmentFragment.tabLayout.getTabAt(0)).select();
                    } else {
                        tabletMainActivity.tabletTreatmentFragment.treatmentDescriptionFragment.fabEditTreatmentDescripton.startAnimation(
                                tabletMainActivity.tabletTreatmentFragment.fabEditTreatmentDescriptonShowAnimation
                        );
                    }

                    tabletMainActivity.tabletTreatmentFragment.zoomOutTabletTreatment.setVisibility(View.VISIBLE);

                    float percentVerGuideline_2 = ((ConstraintLayout.LayoutParams) tabletMainActivity.ver_2_Left_Guideline.getLayoutParams()).guidePercent;

                    if (percentVerGuideline_2 != 0.30f) {

                        // показываем лечение
                        TransitionManager.beginDelayedTransition(tabletMainActivity.mSceneRoot, tabletDiseaseItemClickTransition);

                        tabletMainActivity.ver_2_Left_Guideline.setGuidelinePercent(0.3f);
                        tabletMainActivity.ver_2_Right_Guideline.setGuidelinePercent(0.3f);
                        tabletMainActivity.ver_3_Guideline.setGuidelinePercent(0.6f);
                    } else {

                        tabletDiseaseSelected();
                    }

                    // реклама Малая
                    if (tabletMainActivity.tabletTreatmentFragment != null
                            && tabletMainActivity.tabletTreatmentFragment.adViewInTabletTreatmentFragment != null) {

                        // если реклама не показывалась, но соединение есть
                        if (!TabletMainActivity.adIsShown) {
                            if (tabletMainActivity.isNetworkConnected()) {
                                // загружаем МАЛЫЙ рекламный блок с задержкой, чтоб успел отрисоваться
                                tabletMainActivity.myTabletHandler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        tabletMainActivity.tabletTreatmentFragment.adViewInTabletTreatmentFragment.loadAd(
                                                tabletMainActivity.adRequest
                                        );
                                    }
                                }, 600);
                            }
                        } else {
                            // если реклама паказывалась, но соедининеия нет
                            if (!tabletMainActivity.isNetworkConnected()) {
                                tabletMainActivity.tabletTreatmentFragment.adViewFrameTabletTreatmentFragment.setVisibility(View.GONE);
                                tabletMainActivity.tabletTreatmentFragment.adViewInTabletTreatmentFragment.pause();
                                TabletMainActivity.adIsShown = false;
                            }
                        }
                    }
                }
            }
        }

        void tabletDiseaseSelected() {

            tabletMainActivity.tempTextDiseaseName = Objects.requireNonNull(tabletMainActivity.tabletTreatmentFragment.editTextDiseaseName.getText()).toString();
            tabletMainActivity.tempTextDateOfTreatment = tabletMainActivity.tabletTreatmentFragment.editTextDateOfDisease.getText().toString();
            tabletMainActivity.tempTextTreatment = Objects.requireNonNull(tabletMainActivity.tabletTreatmentFragment.treatmentDescriptionFragment.editTextTreatment.getText()).toString();

            // устанавливаем новое значение для selected_disease_id
            // и заново отрисовываем все видимые элементы в diseaseRecyclerView
            // чтоб закрасить выделенный элемент
            TabletMainActivity.selectedDisease_id = clicked_disease_id;
            tabletMainActivity.tabletDiseasesFragment.diseaseRecyclerViewAdapter.notifyDataSetChanged();

            // далее отрисовываем нужные поля в фрагментах
            tabletMainActivity.tabletTreatmentFragment.tabLayout.setVisibility(View.VISIBLE);
            tabletMainActivity.tabletTreatmentFragment.viewPager.setVisibility(View.VISIBLE);

            tabletMainActivity.tabletTreatmentFragment.set_idDisease(clicked_disease_id);
            tabletMainActivity.tabletTreatmentFragment.set_idUser(Long.valueOf(_diseaseUserId.getText().toString()));
            tabletMainActivity.tabletTreatmentFragment.setTextDiseaseName(diseaseName.getText().toString());
            tabletMainActivity.tabletTreatmentFragment.setTextDateOfDisease(diseaseDate.getText().toString());
            tabletMainActivity.tabletTreatmentFragment.setTextTreatment(treatmentText.getText().toString());

            // грузим снимки этого заболевания
            tabletMainActivity.tabletTreatmentFragment.treatmentPhotosFragment.initTreatmentPhotosLoader();
        }
    }
}
