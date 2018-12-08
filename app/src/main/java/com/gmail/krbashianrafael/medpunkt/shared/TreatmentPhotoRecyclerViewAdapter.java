package com.gmail.krbashianrafael.medpunkt.shared;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.gmail.krbashianrafael.medpunkt.GlideApp;
import com.gmail.krbashianrafael.medpunkt.R;
import com.gmail.krbashianrafael.medpunkt.tablet.TabletMainActivity;

import java.util.ArrayList;

public class TreatmentPhotoRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final Context mContext;
    private final TabletMainActivity tabletMainActivity;
    private final ArrayList<TreatmentPhotoItem> treatmentPhotosList;

    TreatmentPhotoRecyclerViewAdapter(Context context) {
        mContext = context;
        this.treatmentPhotosList = new ArrayList<>();

        if (mContext instanceof TabletMainActivity) {
            tabletMainActivity = (TabletMainActivity) mContext;
        } else {
            tabletMainActivity = null;
        }
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

        if (HomeActivity.isTablet && TabletMainActivity.inWideView) {
            if (TabletMainActivity.selectedTreatmentPhoto_id == _trPhotoId) {
                // добавленное фото заболевания будет сразу выделенным
                // т.к. в MedProvider есть запись TabletMainActivity.selectedTreatmentPhoto_id = TabletMainActivity.insertedtreatmentPhoto_id;
                ((TreatmentPhotoHolder) holder).treatmentPhotoItem.setBackgroundColor(mContext.getResources().getColor(R.color.my_blue));

                if (tabletMainActivity != null) {
                    tabletMainActivity.tabletTreatmentFragment.treatmentPhotosFragment.
                            _idTrPhoto = _trPhotoId;

                    tabletMainActivity.tabletTreatmentFragment.treatmentPhotosFragment.
                            treatmentPhotoFilePath = itemPhotoUri;

                    tabletMainActivity.tabletTreatmentFragment.treatmentPhotosFragment.
                            textDateOfTreatmentPhoto = itemDate;

                    tabletMainActivity.tabletTreatmentFragment.treatmentPhotosFragment.
                            textPhotoDescription = itemName;
                }

                // грузим фото выделенного заболевания
                ((TreatmentPhotoHolder) holder).loadTreamentPhotoInImgWideView(itemPhotoUri);

            } else {
                ((TreatmentPhotoHolder) holder).treatmentPhotoItem.setBackgroundColor(Color.TRANSPARENT);
            }
        } else {
            ((TreatmentPhotoHolder) holder).treatmentPhotoItem.setBackgroundColor(Color.TRANSPARENT);
        }
    }

    @Override
    public int getItemCount() {
        return treatmentPhotosList.size();
    }

    static class TreatmentPhotoHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final Handler myHandler = new Handler(Looper.getMainLooper());

        final Context myContext;
        private final TabletMainActivity tabletMainActivity;

        final LinearLayout treatmentPhotoItem;

        final TextView _trPhotoId;
        final TextView _userId;
        final TextView _diseaseId;
        final TextView itemUri;
        final TextView itemDate;
        final TextView itemName;

        long clicked_treatmentPhoto_id = 0;

        TreatmentPhotoHolder(View itemView, Context context) {
            super(itemView);

            myContext = context;

            if (myContext instanceof TabletMainActivity) {
                tabletMainActivity = (TabletMainActivity) myContext;
            } else {
                tabletMainActivity = null;
            }

            _trPhotoId = itemView.findViewById(R.id.tr_photo_id);
            _userId = itemView.findViewById(R.id.user_id);
            _diseaseId = itemView.findViewById(R.id.disease_id);
            itemUri = itemView.findViewById(R.id.recycler_photo_item_uri);
            itemDate = itemView.findViewById(R.id.photo_item_date);
            itemName = itemView.findViewById(R.id.recycler_photo_item_name);

            treatmentPhotoItem = itemView.findViewById(R.id.recycler_treatment_photo_item);

            treatmentPhotoItem.setOnClickListener(this);
        }

        @SuppressLint("ClickableViewAccessibility")
        private void loadTreamentPhotoInImgWideView(String itemUri) {

            // чтоб после масштабирования старого фото, новое грузилось как FIT_CENTER
            tabletMainActivity.tabletTreatmentFragment.treatmentPhotosFragment.
                    imgWideView.setScaleType(ImageView.ScaleType.FIT_CENTER);

            GlideApp.with(tabletMainActivity)
                    .load(itemUri)
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            //on load failed

                            // чтоб файл освободился (для удаления),
                            // высвобождаем imagePhoto
                            myHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    Glide.with(tabletMainActivity).
                                            clear(tabletMainActivity.tabletTreatmentFragment.treatmentPhotosFragment.
                                                    imgWideView);

                                    tabletMainActivity.tabletTreatmentFragment.treatmentPhotosFragment.
                                            imgWideView.setImageResource(R.color.my_dark_gray);

                                    tabletMainActivity.tabletTreatmentFragment.treatmentPhotosFragment.
                                            widePhotoErrView.setVisibility(View.VISIBLE);

                                    tabletMainActivity.tabletTreatmentFragment.treatmentPhotosFragment.
                                            fabToFullScreen.setImageResource(R.drawable.ic_edit_white_24dp);
                                }
                            });

                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            //on load success

                            if (tabletMainActivity.tabletTreatmentFragment.treatmentPhotosFragment.
                                    widePhotoErrView.getVisibility() == View.VISIBLE) {

                                tabletMainActivity.tabletTreatmentFragment.treatmentPhotosFragment.
                                        widePhotoErrView.setVisibility(View.GONE);

                                tabletMainActivity.tabletTreatmentFragment.treatmentPhotosFragment.
                                        fabToFullScreen.setImageResource(R.drawable.ic_zoom_out_photo_white_24dp);
                            }

                            return false;
                        }
                    })
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(tabletMainActivity.tabletTreatmentFragment.treatmentPhotosFragment.
                            imgWideView);
        }

        @Override
        public void onClick(final View view) {
            if (myContext == null) {
                return;
            }

            clicked_treatmentPhoto_id = Long.valueOf(_trPhotoId.getText().toString());

            view.setBackgroundColor(myContext.getResources().getColor(R.color.my_blue));

            // если это планшет и в расширенном виде и делается клик не на том же элементе
            if (HomeActivity.isTablet
                    && TabletMainActivity.selectedDisease_id != clicked_treatmentPhoto_id
                    && TabletMainActivity.inWideView) {

                loadTreamentPhotoInImgWideView(itemUri.getText().toString());

                TabletMainActivity.selectedTreatmentPhoto_id = clicked_treatmentPhoto_id;
                tabletMainActivity.tabletTreatmentFragment.treatmentPhotosFragment.treatmentPhotoRecyclerViewAdapter.notifyDataSetChanged();

            } else {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent intentToTreatmentPhoto = new Intent(myContext, FullscreenPhotoActivity.class);

                        intentToTreatmentPhoto.putExtra("_idUser", Long.valueOf(_userId.getText().toString()));
                        intentToTreatmentPhoto.putExtra("_idDisease", Long.valueOf(_diseaseId.getText().toString()));

                        intentToTreatmentPhoto.putExtra("_idTrPhoto", Long.valueOf(_trPhotoId.getText().toString()));
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
}
