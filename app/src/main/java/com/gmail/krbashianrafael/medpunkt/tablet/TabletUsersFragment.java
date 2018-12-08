package com.gmail.krbashianrafael.medpunkt.tablet;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.gmail.krbashianrafael.medpunkt.R;
import com.gmail.krbashianrafael.medpunkt.data.MedContract.UsersEntry;
import com.gmail.krbashianrafael.medpunkt.shared.DiseaseItem;
import com.gmail.krbashianrafael.medpunkt.shared.HomeActivity;
import com.gmail.krbashianrafael.medpunkt.shared.UserActivity;
import com.gmail.krbashianrafael.medpunkt.shared.UserItem;
import com.gmail.krbashianrafael.medpunkt.shared.UsersRecyclerViewAdapter;

import java.util.ArrayList;
import java.util.Collections;

public class TabletUsersFragment extends Fragment
        implements android.support.v4.app.LoaderManager.LoaderCallbacks<Cursor> {

    private TabletMainActivity tabletMainActivity;

    private TextView txtAddUsers;
    public TextView txtTabletUsers;
    public FloatingActionButton fabAddUser;
    public RecyclerView recyclerUsers;
    public UsersRecyclerViewAdapter usersRecyclerViewAdapter;

    public Animation fabShowAnimation;
    private Animation fadeInAnimation;
    private Animation onlyUsersAnimation;

    private static final int TABLET_USERS_LOADER = 1000;

    public TabletUsersFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_tablet_users, container, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        txtAddUsers = view.findViewById(R.id.txt_empty_users);

        txtTabletUsers = view.findViewById(R.id.txt_tablet_users);
        txtTabletUsers.setBackgroundColor(getResources().getColor(R.color.my_dark_gray));

        ImageView imgCancelTabletUsers = view.findViewById(R.id.img_cancel_tablet_users);
        imgCancelTabletUsers.setVisibility(View.VISIBLE);
        imgCancelTabletUsers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tabletMainActivity.finish();
            }
        });

        if (HomeActivity.iAmDoctor) {
            txtAddUsers.setText(R.string.patient_title_activity);
            txtTabletUsers.setText(R.string.patients_title_activity);
        }

        fabAddUser = view.findViewById(R.id.fabAddUser);
        fabAddUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent userIntent = new Intent(tabletMainActivity, UserActivity.class);
                userIntent.putExtra("userPhotoUri", "No_Photo");
                userIntent.putExtra("newUser", true);
                userIntent.putExtra("iAmDoctor", HomeActivity.iAmDoctor);
                startActivity(userIntent);
            }
        });

        txtAddUsers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent userIntent = new Intent(tabletMainActivity, UserActivity.class);
                userIntent.putExtra("userPhotoUri", "No_Photo");
                userIntent.putExtra("newUser", true);
                userIntent.putExtra("iAmDoctor", HomeActivity.iAmDoctor);
                startActivity(userIntent);
            }
        });

        // инициализируем recyclerUsers
        recyclerUsers = view.findViewById(R.id.recycler_users);

        // при нажатии на txtTabletUsers показываем выделенного пользователя
        txtTabletUsers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final ArrayList<UserItem> myData = usersRecyclerViewAdapter.getUsersList();

                if (myData.size() != 0) {

                    tabletMainActivity.selectedUser_position = 0;

                    if (TabletMainActivity.selectedUser_id != 0) {
                        for (int i = 0; i < myData.size(); i++) {
                            if (myData.get(i).get_userId() == TabletMainActivity.selectedUser_id) {
                                tabletMainActivity.selectedUser_position = i;
                            }
                        }
                    }

                    recyclerUsers.smoothScrollToPosition(tabletMainActivity.selectedUser_position);
                }
            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof Activity) {
            tabletMainActivity = (TabletMainActivity) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        tabletMainActivity = null;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (tabletMainActivity == null) {
            tabletMainActivity = (TabletMainActivity) getActivity();
        }

        fadeInAnimation = AnimationUtils.loadAnimation(tabletMainActivity, R.anim.fadein);

        fabShowAnimation = AnimationUtils.loadAnimation(tabletMainActivity, R.anim.fab_show);
        fabShowAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                fabAddUser.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                fabAddUser.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                fabAddUser.setVisibility(View.VISIBLE);
            }
        });

        // создаем пустую анимацию, чтоб отделить движение tabletMainActivity.ver_2_Right_Guideline.setGuidelinePercent(1.0f);
        // от движения:
        // tabletMainActivity.ver_1_Guideline.setGuidelinePercent(0.1f);
        // tabletMainActivity.ver_2_Left_Guideline.setGuidelinePercent(0.9f);
        onlyUsersAnimation = new TranslateAnimation(tabletMainActivity, null);
        onlyUsersAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {

                TransitionManager.beginDelayedTransition(tabletMainActivity.mSceneRoot);

                tabletMainActivity.ver_1_Guideline.setGuidelinePercent(0.1f);
                tabletMainActivity.ver_2_Left_Guideline.setGuidelinePercent(0.9f);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });

        // инициализируем linearLayoutManager
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(tabletMainActivity,
                LinearLayoutManager.VERTICAL, false);

        // устанавливаем LayoutManager для RecyclerView
        recyclerUsers.setLayoutManager(linearLayoutManager);

        // инициализируем usersRecyclerViewAdapter
        usersRecyclerViewAdapter = new UsersRecyclerViewAdapter(tabletMainActivity);

        // устанавливаем адаптер для RecyclerView
        recyclerUsers.setAdapter(usersRecyclerViewAdapter);
    }

    public void initUsersLoader() {
        // сразу INVISIBLE делаем чтоб не было скачков при смене вида
        fabAddUser.setVisibility(View.INVISIBLE);
        txtAddUsers.setVisibility(View.INVISIBLE);

        // Инициализируем Loader
        getLoaderManager().initLoader(TABLET_USERS_LOADER, null, this);
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        String[] projection = {
                UsersEntry.U_ID,
                UsersEntry.COLUMN_USER_NAME,
                UsersEntry.COLUMN_USER_DATE,
                UsersEntry.COLUMN_USER_PHOTO_PATH};

        return new CursorLoader(tabletMainActivity,   // Parent activity context
                UsersEntry.CONTENT_USERS_URI,   // Provider content URI to query = content://com.gmail.krbashianrafael.medpunkt/users/
                projection,             // Columns to include in the resulting Cursor
                null,                   // No selection clause
                null,                   // No selection arguments
                null);                  // Default sort order
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor cursor) {
        final ArrayList<UserItem> myData = usersRecyclerViewAdapter.getUsersList();
        myData.clear();

        if (cursor != null) {

            // устанавливаем курсор на исходную (на случай, если курсор используем повторно после прохождения цикла
            cursor.moveToPosition(-1);

            // проходим в цикле курсор и заполняем объектами UserItem наш ArrayList<UserItem> myData
            while (cursor.moveToNext()) {

                int user_idColumnIndex = cursor.getColumnIndex(UsersEntry._ID);
                int user_nameColumnIndex = cursor.getColumnIndex(UsersEntry.COLUMN_USER_NAME);
                int user_dateColumnIndex = cursor.getColumnIndex(UsersEntry.COLUMN_USER_DATE);
                int user_photoColumnIndex = cursor.getColumnIndex(UsersEntry.COLUMN_USER_PHOTO_PATH);

                long _userId = cursor.getLong(user_idColumnIndex);
                String userName = cursor.getString(user_nameColumnIndex);

                String userBirthDate = cursor.getString(user_dateColumnIndex);
                String userPhotoUri = cursor.getString(user_photoColumnIndex);

                // добавляем новый user в ArrayList<UserItem> myData
                myData.add(new UserItem(_userId, userBirthDate, userName, userPhotoUri));
            }
        }

        // делаем сортировку пользователей по именеи
        Collections.sort(myData);

        recyclerUsers.setVisibility(View.VISIBLE);

        // оповещаем LayoutManager, что произошли изменения
        // LayoutManager обновляет RecyclerView
        usersRecyclerViewAdapter.notifyDataSetChanged();

        // делаем destroyLoader, чтоб он сам повторно не вызывался,
        // а вызывался при каждом входе в активити
        getLoaderManager().destroyLoader(TABLET_USERS_LOADER);

        int myDataSize = myData.size();

        // код для показа выделенного пользователя
        if (myData.size() != 0) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                    tabletMainActivity.selectedUser_position = 0;

                    if (TabletMainActivity.selectedUser_id != 0) {

                        for (int i = 0; i < myData.size(); i++) {
                            if (myData.get(i).get_userId() == TabletMainActivity.selectedUser_id) {
                                tabletMainActivity.selectedUser_position = i;
                            }
                        }
                    }

                    recyclerUsers.smoothScrollToPosition(tabletMainActivity.selectedUser_position);
                }
            }, 500);
        }

        if (myDataSize == 0) {

            recyclerUsers.setVisibility(View.INVISIBLE);

            tabletMainActivity.ver_1_Guideline.setGuidelinePercent(0.1f);
            tabletMainActivity.ver_2_Left_Guideline.setGuidelinePercent(0.9f);
            tabletMainActivity.ver_2_Right_Guideline.setGuidelinePercent(0.9f);
            tabletMainActivity.ver_3_Guideline.setGuidelinePercent(0.9f);
            tabletMainActivity.ver_4_Guideline.setGuidelinePercent(0.9f);

            // если нет пользователей, то делаем txtAddUsers.setVisibility(View.VISIBLE);
            // и fabAddUser.setVisibility(View.INVISIBLE);
            new Handler(Looper.getMainLooper()).
                    postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            txtAddUsers.setVisibility(View.VISIBLE);
                            txtAddUsers.startAnimation(fadeInAnimation);
                        }
                    }, 300);

            tabletMainActivity.tabletTreatmentFragment.set_idUser(0);

            tabletMainActivity.tabletDiseasesFragment.clearDataFromDiseasesFragment();
            tabletMainActivity.tabletDiseasesFragment.textViewAddDisease.setVisibility(View.INVISIBLE);
            tabletMainActivity.tabletDiseasesFragment.fabAddDisease.setVisibility(View.INVISIBLE);

        } else {
            // если больше одного пользователя

            fabAddUser.startAnimation(fabShowAnimation);

            if (TabletMainActivity.userInserted) {

                fabAddUser.startAnimation(fabShowAnimation);

                tabletMainActivity.ver_1_Guideline.setGuidelinePercent(0.00f);
                tabletMainActivity.ver_2_Left_Guideline.setGuidelinePercent(0.50f);
                tabletMainActivity.ver_2_Right_Guideline.setGuidelinePercent(0.50f);
                tabletMainActivity.ver_3_Guideline.setGuidelinePercent(1.00f);
                tabletMainActivity.ver_4_Guideline.setGuidelinePercent(1.0f);

                tabletMainActivity.tabletDiseasesFragment.set_idUser(TabletMainActivity.insertedUser_id);
                tabletMainActivity.tabletDiseasesFragment.setTextUserName(TabletMainActivity.userNameAfterInsert);

                tabletMainActivity.tabletDiseasesFragment.initDiseasesLoader();

                TabletMainActivity.insertedUser_id = 0;

            } else if (TabletMainActivity.userUpdated) {

                tabletMainActivity.tabletDiseasesFragment.set_idUser(tabletMainActivity.user_IdInEdit);
                tabletMainActivity.tabletDiseasesFragment.setTextUserName(TabletMainActivity.userNameAfterUpdate);

                // иниициализируем Loader заболеваний
                tabletMainActivity.tabletDiseasesFragment.initDiseasesLoader();

            } else if (TabletMainActivity.userDeleted) {

                tabletMainActivity.ver_1_Guideline.setGuidelinePercent(0.1f);
                tabletMainActivity.ver_2_Left_Guideline.setGuidelinePercent(0.9f);
                tabletMainActivity.ver_2_Right_Guideline.setGuidelinePercent(0.9f);
                tabletMainActivity.ver_3_Guideline.setGuidelinePercent(0.9f);
                tabletMainActivity.ver_4_Guideline.setGuidelinePercent(0.9f);

                tabletMainActivity.tabletTreatmentFragment.set_idUser(0);

                // в методе tabletMainActivity.tabletDiseasesFragment.clearDataFromDiseasesFragment();
                // происходит tabletMainActivity.tabletDiseasesFragment.set_idUser(0);
                tabletMainActivity.tabletDiseasesFragment.clearDataFromDiseasesFragment();

            } else if (tabletMainActivity.tabletDiseasesFragment.get_idUser() == 0) {

                //если первый заход и в DiseasesFragment еще не отображаются данные,
                // или нажат крестик на DiseasesFragment

                float percentVerGuideline_2 = ((ConstraintLayout.LayoutParams) tabletMainActivity.ver_2_Left_Guideline.getLayoutParams()).guidePercent;

                if (percentVerGuideline_2 == 0.30f) {

                    tabletMainActivity.ver_2_Right_Guideline.setGuidelinePercent(1.0f);
                    tabletMainActivity.ver_3_Guideline.setGuidelinePercent(1.0f);

                    // т.к. анимацию пустая, то присваиваем ее любому объекту, в данном случае ver_2_Right_Guideline
                    tabletMainActivity.ver_2_Right_Guideline.startAnimation(onlyUsersAnimation);

                } else if (percentVerGuideline_2 == 0.50f) {

                    tabletMainActivity.ver_2_Right_Guideline.setGuidelinePercent(1.0f);

                    // т.к. анимацию пустая, то присваиваем ее любому объекту, в данном случае ver_2_Right_Guideline
                    tabletMainActivity.ver_2_Right_Guideline.startAnimation(onlyUsersAnimation);
                }

                tabletMainActivity.tabletTreatmentFragment.set_idUser(0);

            } else {

                // если вернулись в окно без измениний (после просмотра информации о пользователе)
                // код для показа выделенного заболевания
                if (TabletMainActivity.selectedDisease_id != 0) {

                    final ArrayList<DiseaseItem> myDiseaseData = tabletMainActivity.tabletDiseasesFragment.diseaseRecyclerViewAdapter.getDiseaseList();

                    if (myDiseaseData.size() != 0) {
                        tabletMainActivity.selectedDisease_position = 0;

                        for (int i = 0; i < myDiseaseData.size(); i++) {
                            if (myDiseaseData.get(i).get_diseaseId() == TabletMainActivity.selectedDisease_id) {
                                tabletMainActivity.selectedDisease_position = i;
                            }
                        }

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                tabletMainActivity.tabletDiseasesFragment.recyclerDiseases.smoothScrollToPosition(tabletMainActivity.selectedDisease_position);
                            }
                        }, 500);
                    }
                }
            }
        }

        // после прохождения всех if выставляем флаги в false
        TabletMainActivity.userInserted = false;
        TabletMainActivity.userUpdated = false;
        TabletMainActivity.userDeleted = false;
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        ArrayList<UserItem> myData = usersRecyclerViewAdapter.getUsersList();
        myData.clear();
        usersRecyclerViewAdapter.notifyDataSetChanged();
    }
}
