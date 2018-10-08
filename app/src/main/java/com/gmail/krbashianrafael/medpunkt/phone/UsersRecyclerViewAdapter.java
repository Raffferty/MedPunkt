package com.gmail.krbashianrafael.medpunkt.phone;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
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
import com.bumptech.glide.signature.ObjectKey;
import com.gmail.krbashianrafael.medpunkt.GlideApp;
import com.gmail.krbashianrafael.medpunkt.HomeActivity;
import com.gmail.krbashianrafael.medpunkt.R;
import com.gmail.krbashianrafael.medpunkt.UserActivity;
import com.gmail.krbashianrafael.medpunkt.UserItem;
import com.gmail.krbashianrafael.medpunkt.tablet.TabletMainActivity;

import java.io.File;
import java.util.ArrayList;

import static com.gmail.krbashianrafael.medpunkt.tablet.TabletMainActivity.TABLET_DISEASES_FRAGMENT;

public class UsersRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private ArrayList<UserItem> usersList;

    private static long selected_user_id = 0;

    public UsersRecyclerViewAdapter(Context context) {
        mContext = context;
        usersList = new ArrayList<>();
        selected_user_id = 0;
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

        // если это планшет, то выделенных элемент будет красится в зеленый цвет,
        // а остальные в TRANSPARENT
        if (HomeActivity.isTablet) {
            // если только один элемент пользователя
            // то его _id и будет selected
            if (usersList.size() == 1) {
                selected_user_id = _userId;
            }
            if (selected_user_id == _userId) {
                ((UserHolder) holder).container.setBackgroundColor(mContext.getResources().getColor(R.color.my_gray));
            } else {
                ((UserHolder) holder).container.setBackgroundColor(Color.TRANSPARENT);
            }
        }

        File imgFile = new File(userPhotoUri);
        if (imgFile.exists()) {
            GlideApp.with(mContext)
                    .load(userPhotoUri)
                    .centerCrop()
                    .signature(new ObjectKey(imgFile.lastModified()))   // signature, чтоб при обновлении фото грузилось из файла, а не из кеша
                    .override(120, 120)
                    .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                    .error(R.drawable.ic_camera_alt_gray_24dp)
                    .transition(DrawableTransitionOptions.withCrossFade(300))
                    .into(((UserHolder) holder).userImage);
        } else {
            // если без фото, то пишем "No_Photo"
            ((UserHolder) holder).userPhotoUri.setText("No_Photo");

            // чистим userImage
            GlideApp.with(mContext).clear(((UserHolder) holder).userImage);

            // ставим в userImage R.drawable.ic_camera_alt_gray_24dp
            GlideApp.with(mContext)
                    .load(R.drawable.ic_camera_alt_gray_24dp)
                    .centerInside()
                    .into(((UserHolder) holder).userImage);
        }
    }

    @Override
    public int getItemCount() {
        return usersList.size();
    }

    public static class UserHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        Context myContext;

        TextView _userId;
        TextView userPhotoUri;
        TextView userBirthDate;
        TextView userName;

        ImageView userImage;

        LinearLayout container;
        LinearLayout usersItem;
        FrameLayout userEdit;

        UserHolder(View itemView, Context context) {
            super(itemView);

            myContext = context;

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

            // если нажали на кружок с буквой i (информация по пользователю)
            if (view.getId() == R.id.user_item_edit) {

                // закрашиваем выделенный элемент (i) в голубой
                view.setBackgroundColor(myContext.getResources().getColor(R.color.my_gray));

                // с задержкой в 250 мс открываем UserActivity для просмотра/изменения данных пользователя
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent userEditIntent = new Intent(myContext, UserActivity.class);
                        userEditIntent.putExtra("_idUser", user_id_inEdit);
                        userEditIntent.putExtra("editUser", true);
                        userEditIntent.putExtra("UserName", userName.getText());
                        userEditIntent.putExtra("birthDate", userBirthDate.getText());
                        userEditIntent.putExtra("userPhotoUri", userPhotoUri.getText());

                        // если это планшет, то получаем user_id_inEdit для контроля над изменениями пользователя
                        if (HomeActivity.isTablet) {
                            TabletMainActivity.user_IdInEdit = user_id_inEdit;
                        }

                        myContext.startActivity(userEditIntent);
                    }
                }, 250);

                // через 500 мс закрашиваем выделенный элемент (i) в TRANSPARENT
                // чтоб по возвращении он не был голубым
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        view.setBackgroundColor(Color.TRANSPARENT);
                    }
                }, 500);

            } else {
                // если нажат сам элемент с именем пользователя
                // если это телефон
                if (!HomeActivity.isTablet) {
                    container.setBackgroundColor(myContext.getResources().getColor(R.color.my_gray));

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Intent userDiseasIntent = new Intent(myContext, DiseasesActivity.class);
                            userDiseasIntent.putExtra("_idUser", Long.valueOf(_userId.getText().toString()));
                            userDiseasIntent.putExtra("UserName", userName.getText().toString());

                            myContext.startActivity(userDiseasIntent);
                        }
                    }, 250);

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            container.setBackgroundColor(Color.TRANSPARENT);
                        }
                    }, 500);

                } else {
                    TabletMainActivity tabletMainActivity = (TabletMainActivity) myContext;
                    //если это планшет и делается клик НЕ на том же элементе (чтоб дважды не грузить ту же информацию)

                    if (selected_user_id != user_id_inEdit) {

                        // устанавливаем новое значение для selected_user_id
                        // и заново отрисовываем все видимые элементы в usersRecyclerView
                        // чтоб закрасить выделенный элемент
                        selected_user_id = user_id_inEdit;
                        tabletMainActivity.tabletUsersFragment.usersRecyclerViewAdapter.notifyDataSetChanged();

                        // далее отрисовываем нужные поля в фрагментах
                        //tabletMainActivity.tabletUsersFragment.txtTabletUsers.setBackgroundColor(myContext.getResources().getColor(R.color.colorPrimary));
                        /*if (HomeActivity.iAmDoctor) {
                            tabletMainActivity.tabletUsersFragment.txtTabletUsers.setText(R.string.patients_title_activity);
                        } else {
                            tabletMainActivity.tabletUsersFragment.txtTabletUsers.setText(R.string.users_title_activity);
                        }*/

                        tabletMainActivity.tabletDiseasesFragment.set_idUser(user_id_inEdit);
                        tabletMainActivity.tabletDiseasesFragment.setTextUserName(userName.getText().toString());
                        DiseaseRecyclerViewAdapter.selected_disease_id = 0;
                        tabletMainActivity.tabletDiseasesFragment.initDiseasesLoader();
                        tabletMainActivity.unBlur(TABLET_DISEASES_FRAGMENT);

                        if (TabletMainActivity.diseaseAndTreatmentInEdit) {
                            //tabletMainActivity.tabletTreatmentCancel.performClick();
                            tabletMainActivity.hideElementsOnTabletTreatmentFragment();
                        }
                    }
                }
            }
        }
    }
}
