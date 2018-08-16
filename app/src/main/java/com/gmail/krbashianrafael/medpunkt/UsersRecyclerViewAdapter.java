package com.gmail.krbashianrafael.medpunkt;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;

import java.io.File;
import java.util.ArrayList;

public class UsersRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private UsersActivity mUsersActivity;
    private ArrayList<UserItem> usersList;

    UsersRecyclerViewAdapter(UsersActivity context) {
        mUsersActivity = context;

        this.usersList = new ArrayList<>();
    }

    public ArrayList<UserItem> getUsersList() {
        return usersList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View mView = LayoutInflater.from(parent.getContext()).inflate(R.layout.users_recycleview_item, parent, false);
        return new UserHolder(mView, mUsersActivity);
    }

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

        File imgFile = new File(userPhotoUri);
        if (imgFile.exists()) {
            GlideApp.with(mUsersActivity)
                    .load(userPhotoUri)
                    .override(90,90)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .error(R.drawable.ic_camera_alt_gray_24dp)
                    .transition(DrawableTransitionOptions.withCrossFade(800))
                    .into(((UserHolder) holder).userImage);
        } else {
            // если без фото, то пишем "No_Photo"
            ((UserHolder) holder).userPhotoUri.setText("No_Photo");

            // чистим userImage
            GlideApp.with(mUsersActivity).clear(((UserHolder) holder).userImage);

            // ставим в userImage R.drawable.ic_camera_alt_gray_24dp
            GlideApp.with(mUsersActivity).
                    load(R.drawable.ic_camera_alt_gray_24dp).
                    into(((UserHolder) holder).userImage);
        }
    }

    @Override
    public int getItemCount() {
        return usersList.size();
    }

    public static class UserHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        UsersActivity myUsersActivity;

        TextView _userId;
        TextView userPhotoUri;
        TextView userBirthDate;
        TextView userName;

        ImageView userImage;
        LinearLayout usersItem;
        FrameLayout userEdit;

        UserHolder(View itemView, UsersActivity context) {
            super(itemView);

            myUsersActivity = context;

            _userId = itemView.findViewById(R.id.user_item_id);
            userPhotoUri = itemView.findViewById(R.id.user_item_photo_uri);
            userBirthDate = itemView.findViewById(R.id.recycler_users_item_date);
            userName = itemView.findViewById(R.id.recycler_users_item_name);

            userImage = itemView.findViewById(R.id.user_image);

            usersItem = itemView.findViewById(R.id.recycler_users_item);
            userEdit = itemView.findViewById(R.id.user_item_edit);

            usersItem.setOnClickListener(this);
            userEdit.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (myUsersActivity==null){
                return;
            }

            if (view.getId() == R.id.user_item_edit){
                Intent userEditIntent = new Intent(myUsersActivity, UserActivity.class);
                userEditIntent.putExtra("_idUser", Long.valueOf(_userId.getText().toString()));
                userEditIntent.putExtra("editUser", true);
                userEditIntent.putExtra("iAmDoctor", myUsersActivity.iAmDoctor);
                userEditIntent.putExtra("UserName", userName.getText());
                userEditIntent.putExtra("birthDate", userBirthDate.getText());
                userEditIntent.putExtra("userPhotoUri", userPhotoUri.getText());

                myUsersActivity.startActivity(userEditIntent);

            } else {
                Intent userDiseasIntent = new Intent(myUsersActivity, DiseasesActivity.class);
                userDiseasIntent.putExtra("_idUser", Long.valueOf(_userId.getText().toString()));
                userDiseasIntent.putExtra("UserName", userName.getText());

                myUsersActivity.startActivity(userDiseasIntent);
            }
        }
    }
}
