package com.gmail.krbashianrafael.medpunkt.shared;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.signature.ObjectKey;
import com.gmail.krbashianrafael.medpunkt.GlideApp;
import com.gmail.krbashianrafael.medpunkt.R;
import com.gmail.krbashianrafael.medpunkt.phone.DiseasesActivity;
import com.gmail.krbashianrafael.medpunkt.tablet.TabletMainActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.Objects;

public class UsersRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final Context mContext;
    private final ArrayList<UserItem> usersList;

    public UsersRecyclerViewAdapter(Context context) {
        mContext = context;
        usersList = new ArrayList<>();
    }

    public ArrayList<UserItem> getUsersList() {
        return usersList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View mView = LayoutInflater.from(parent.getContext()).inflate(R.layout.users_recycleview_item, parent, false);
        return new UserHolder(mView, mContext);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        long _userId = usersList.get(position).get_userId();
        String userBirthDate = usersList.get(position).getUserBirthDate();
        String userName = usersList.get(position).getUserName();
        String userPhotoUri = usersList.get(position).getUserPhotoUri();

        ((UserHolder) holder)._userId.setText(String.valueOf(_userId));
        ((UserHolder) holder).userPhotoUri.setText(userPhotoUri);

        ((UserHolder) holder).userBirthDate.setText(userBirthDate);
        ((UserHolder) holder).userName.setText(userName);

        if (HomeActivity.isTablet) {

            if (TabletMainActivity.selectedUser_id == _userId) {
                ((UserHolder) holder).container.setBackgroundColor(mContext.getResources().getColor(R.color.my_blue));
            } else {
                ((UserHolder) holder).container.setBackgroundColor(Color.TRANSPARENT);
            }
        }

        File imgFile = new File(userPhotoUri);

        if (imgFile.exists()) {
            GlideApp.with(mContext)
                    .load(userPhotoUri)
                    .centerCrop()
                    .signature(new ObjectKey(imgFile.lastModified()))
                    .override(120, 120)
                    .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                    .error(R.drawable.ic_camera_alt_gray_54dp)
                    .transition(DrawableTransitionOptions.withCrossFade(300))
                    .into(((UserHolder) holder).userImage);
        } else {
            ((UserHolder) holder).userPhotoUri.setText("No_Photo");

            GlideApp.with(mContext).clear(((UserHolder) holder).userImage);

            GlideApp.with(mContext)
                    .load(R.drawable.ic_camera_alt_gray_54dp)
                    .centerInside()
                    .into(((UserHolder) holder).userImage);
        }
    }

    @Override
    public int getItemCount() {
        return usersList.size();
    }

    static class UserHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final Handler myHandler = new Handler(Looper.getMainLooper());
        private final TabletMainActivity tabletMainActivity;

        final Context myContext;

        final TextView _userId;
        final TextView userPhotoUri;
        final TextView userBirthDate;
        final TextView userName;

        final ImageView userImage;

        final LinearLayout container;
        final LinearLayout usersItem;
        final FrameLayout userEdit;

        UserHolder(View itemView, Context context) {
            super(itemView);

            myContext = context;

            if (myContext instanceof TabletMainActivity) {
                tabletMainActivity = (TabletMainActivity) myContext;
            } else {
                tabletMainActivity = null;
            }

            _userId = itemView.findViewById(R.id.user_item_id);
            userPhotoUri = itemView.findViewById(R.id.user_item_photo_uri);
            userBirthDate = itemView.findViewById(R.id.recycler_users_item_date);
            userName = itemView.findViewById(R.id.recycler_users_item_name);
            userImage = itemView.findViewById(R.id.user_image);

            container = itemView.findViewById(R.id.LLcontainer);
            usersItem = itemView.findViewById(R.id.recycler_users_item);
            userEdit = itemView.findViewById(R.id.user_item_edit);

            usersItem.setOnClickListener(this);
            userEdit.setOnClickListener(this);
        }

        @Override
        public void onClick(final View view) {
            if (myContext == null) {
                return;
            }

            final long user_id_inEdit = Long.valueOf(_userId.getText().toString());

            if (view.getId() == R.id.user_item_edit) {

                view.setBackgroundColor(myContext.getResources().getColor(R.color.colorPrimaryLight));

                if (!HomeActivity.isTablet) {
                    myHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Intent userEditIntent = new Intent(myContext, UserActivity.class);
                            userEditIntent.putExtra("_idUser", user_id_inEdit);
                            userEditIntent.putExtra("editUser", true);
                            userEditIntent.putExtra("UserName", userName.getText());
                            userEditIntent.putExtra("birthDate", userBirthDate.getText());
                            userEditIntent.putExtra("userPhotoUri", userPhotoUri.getText());

                            myContext.startActivity(userEditIntent);
                        }
                    }, 250);

                    myHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            view.setBackgroundColor(Color.TRANSPARENT);
                        }
                    }, 500);

                } else {
                    Intent userEditIntent = new Intent(myContext, UserActivity.class);
                    userEditIntent.putExtra("_idUser", user_id_inEdit);
                    userEditIntent.putExtra("editUser", true);
                    userEditIntent.putExtra("UserName", userName.getText());
                    userEditIntent.putExtra("birthDate", userBirthDate.getText());
                    userEditIntent.putExtra("userPhotoUri", userPhotoUri.getText());

                    tabletMainActivity.user_IdInEdit = user_id_inEdit;

                    tabletMainActivity.startActivity(userEditIntent);

                    myHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            tabletUserSelected(user_id_inEdit);
                            view.setBackgroundColor(Color.TRANSPARENT);
                        }
                    }, 1000);
                }
            } else {
                if (!HomeActivity.isTablet) {
                    container.setBackgroundColor(myContext.getResources().getColor(R.color.my_blue));

                    myHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Intent userDiseasIntent = new Intent(myContext, DiseasesActivity.class);
                            userDiseasIntent.putExtra("_idUser", Long.valueOf(_userId.getText().toString()));
                            userDiseasIntent.putExtra("UserName", userName.getText().toString());

                            myContext.startActivity(userDiseasIntent);
                        }
                    }, 250);

                    myHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            container.setBackgroundColor(Color.TRANSPARENT);
                        }
                    }, 500);

                } else {
                    tabletMainActivity.tabletTreatmentFragment.adViewFrameTabletTreatmentFragment.setVisibility(View.GONE);
                    TabletMainActivity.adIsShown = false;

                    if (tabletMainActivity.tabletTreatmentFragment != null &&
                            tabletMainActivity.tabletTreatmentFragment.adViewInTabletTreatmentFragment != null) {
                        tabletMainActivity.tabletTreatmentFragment.adViewInTabletTreatmentFragment.pause();
                    }

                    tabletUserSelected(user_id_inEdit);
                }
            }
        }

        private void tabletUserSelected(long user_id_inEdit) {
            final ArrayList<DiseaseItem> myDiseaseData = tabletMainActivity.tabletDiseasesFragment.diseaseRecyclerViewAdapter.getDiseaseList();

            if (myDiseaseData.size() != 0) {
                tabletMainActivity.selectedDisease_position = 0;

                if (TabletMainActivity.selectedDisease_id != 0) {

                    for (int i = 0; i < myDiseaseData.size(); i++) {
                        if (myDiseaseData.get(i).get_diseaseId() == TabletMainActivity.selectedDisease_id) {
                            tabletMainActivity.selectedDisease_position = i;
                        }
                    }
                }

                tabletMainActivity.myTabletHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        tabletMainActivity.tabletDiseasesFragment.recyclerDiseases.smoothScrollToPosition(tabletMainActivity.selectedDisease_position);
                    }
                }, 500);
            }

            if (TabletMainActivity.selectedUser_id != user_id_inEdit) {
                Objects.requireNonNull(tabletMainActivity.tabletTreatmentFragment.tabLayout.getTabAt(0)).select();

                TabletMainActivity.selectedUser_id = user_id_inEdit;

                tabletMainActivity.tabletUsersFragment.usersRecyclerViewAdapter.notifyDataSetChanged();

                tabletMainActivity.tabletDiseasesFragment.set_idUser(user_id_inEdit);
                tabletMainActivity.tabletDiseasesFragment.setTextUserName(userName.getText().toString());
                TabletMainActivity.selectedDisease_id = 0;

                tabletMainActivity.tabletDiseasesFragment.initDiseasesLoader();

                if (tabletMainActivity.diseaseAndTreatmentInEdit) {
                    tabletMainActivity.hideElementsOnTabletTreatmentFragment();
                }
            }
        }
    }
}
