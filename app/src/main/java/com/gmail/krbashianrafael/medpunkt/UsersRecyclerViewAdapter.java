package com.gmail.krbashianrafael.medpunkt;

import android.content.Context;
import android.content.Intent;
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

import java.io.File;
import java.util.ArrayList;

public class UsersRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static Context mContext;
    private ArrayList<UserItem> usersList;

    UsersRecyclerViewAdapter(Context context) {
        mContext = context;
        this.usersList = new ArrayList<>();
    }

    public ArrayList<UserItem> getUsersList() {
        return usersList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View mView = LayoutInflater.from(parent.getContext()).inflate(R.layout.users_recycleview_item, parent, false);
        return new UserHolder(mView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        int _userId = usersList.get(position).get_userId();
        String userBirthDate = usersList.get(position).getUserBirthDate();
        String userName = usersList.get(position).getUserName();
        String userPhotoUri = usersList.get(position).getUserPhotoUri();

        // _treatmentId прописываем в "невидимое" _treatment_id (т.к. размеры этого TextView в нулях)
        // для его дальнейшего использования при onClick на itemView
        ((UserHolder) holder)._userId.setText(String.valueOf(_userId));

        // itemUri прописываем в "невидимое" recycler_photo_item_uri (т.к. размеры этого TextView в нулях)
        // для его дальнейшего использования при onClick на itemView
        ((UserHolder) holder).userPhotoUri.setText(userPhotoUri);

        ((UserHolder) holder).userBirthDate.setText(userBirthDate);
        ((UserHolder) holder).userName.setText(userName);

        File imgFile = new File(userPhotoUri);
        if (imgFile.exists()) {
            GlideApp.with(mContext)
                    .load(userPhotoUri)
                    .override(90,90)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .error(R.drawable.ic_camera_alt_gray_24dp)
                    .transition(DrawableTransitionOptions.withCrossFade(800))
                    .into(((UserHolder) holder).userImage);
        } else {
            // если без фото, то пишем "No_Photo"
            ((UserHolder) holder).userPhotoUri.setText(R.string.no_photo);

            // чистим userImage
            GlideApp.with(mContext).clear(((UserHolder) holder).userImage);

            // ставим в userImage R.drawable.ic_camera_alt_gray_24dp
            GlideApp.with(mContext).
                    load(R.drawable.ic_camera_alt_gray_24dp).
                    into(((UserHolder) holder).userImage);
        }
    }

    @Override
    public int getItemCount() {
        return usersList.size();
    }

    public static class UserHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView _userId;
        TextView userPhotoUri;
        TextView userBirthDate;
        TextView userName;

        ImageView userImage;
        LinearLayout usersItem;
        FrameLayout userEdit;

        UserHolder(View itemView) {
            super(itemView);

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
            if (view.getId() == R.id.user_item_edit){

                Log.d("toUser","toUser _userId = " + _userId.getText());

                Intent userEditIntent = new Intent(mContext, UserActivity.class);
                userEditIntent.putExtra("_idUser", Long.valueOf(_userId.getText().toString()));
                userEditIntent.putExtra("editUser", true);
                userEditIntent.putExtra("UserName", userName.getText());
                userEditIntent.putExtra("birthDate", userBirthDate.getText());
                userEditIntent.putExtra("userPhotoUri", userPhotoUri.getText());

                mContext.startActivity(userEditIntent);

            } else {
                Intent userDiseasIntent = new Intent(mContext, DiseasesActivity.class);
                userDiseasIntent.putExtra("_idUser", Long.valueOf(_userId.getText().toString()));
                userDiseasIntent.putExtra("UserName", userName.getText());
                userDiseasIntent.putExtra("birthDate", userBirthDate.getText());
                userDiseasIntent.putExtra("userPhotoUri", userPhotoUri.getText());

                mContext.startActivity(userDiseasIntent);
            }
        }
    }
}
