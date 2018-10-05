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
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.gmail.krbashianrafael.medpunkt.HomeActivity;
import com.gmail.krbashianrafael.medpunkt.R;
import com.gmail.krbashianrafael.medpunkt.UserActivity;
import com.gmail.krbashianrafael.medpunkt.UserItem;
import com.gmail.krbashianrafael.medpunkt.data.MedContract.UsersEntry;
import com.gmail.krbashianrafael.medpunkt.phone.UsersRecyclerViewAdapter;

import java.util.ArrayList;
import java.util.Collections;

import static com.gmail.krbashianrafael.medpunkt.tablet.TabletMainActivity.TABLET_DISEASES_FRAGMENT;
import static com.gmail.krbashianrafael.medpunkt.tablet.TabletMainActivity.TABLET_TREATMENT_FRAGMENT;

public class TabletUsersFragment extends Fragment
        implements android.support.v4.app.LoaderManager.LoaderCallbacks<Cursor> {

    private TabletMainActivity tabletMainActivity;

    // шапка, которая видна только на планшете
    public TextView txtTabletUsers;

    protected TextView txtAddUsers;
    public FloatingActionButton fabAddUser;
    private RecyclerView recyclerUsers;
    public UsersRecyclerViewAdapter usersRecyclerViewAdapter;

    // Animation fabShowAnimation
    public Animation fabShowAnimation;
    private Animation fadeInAnimation;

    public static boolean mScrollToStart = false;

    private static final int TABLET_USERS_LOADER = 1000;

    public TabletUsersFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_tablet_users, container, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        txtTabletUsers = view.findViewById(R.id.txt_tablet_users);
        if (HomeActivity.iAmDoctor) {
            txtTabletUsers.setText(R.string.patients_title_activity);
        }

        //FrameLayout dividerTabletFrame = view.findViewById(R.id.divider_tablet_frame);

        // Все это для выравнивания txtAddUsers по центру
        //этот FrameLayout виден только на планшере
        //dividerTabletFrame.setVisibility(View.VISIBLE);
        //этот TextView виден только на планшере
        txtTabletUsers.setVisibility(View.VISIBLE);

        txtAddUsers = view.findViewById(R.id.txt_empty_users);

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
        ArrayList<UserItem> myData = usersRecyclerViewAdapter.getUsersList();
        myData.clear();

        if (cursor != null) {

            // устанавливаем курсор на исходную (на случай, если курсор используем повторно после прохождения цикла
            cursor.moveToPosition(-1);

            // проходим в цикле курсор и заполняем объектами UserItem наш ArrayList<UserItem> myData
            while (cursor.moveToNext()) {

                // Find the columns of user attributes that we're interested in
                int user_idColumnIndex = cursor.getColumnIndex(UsersEntry._ID);
                int user_nameColumnIndex = cursor.getColumnIndex(UsersEntry.COLUMN_USER_NAME);
                int user_dateColumnIndex = cursor.getColumnIndex(UsersEntry.COLUMN_USER_DATE);
                int user_photoColumnIndex = cursor.getColumnIndex(UsersEntry.COLUMN_USER_PHOTO_PATH);

                // Read the user attributes from the Cursor for the current user
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

        // оповещаем LayoutManager, что произошли изменения
        // LayoutManager обновляет RecyclerView
        usersRecyclerViewAdapter.notifyDataSetChanged();

        // делаем destroyLoader, чтоб он сам повторно не вызывался,
        // а вызывался при каждом входе в активити
        getLoaderManager().destroyLoader(TABLET_USERS_LOADER);

        int myDataSize = myData.size();

        if (myDataSize == 0) {
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

            // делаем blur на TABLET_DISEASES_FRAGMENT и TABLET_TREATMENT_FRAGMENT
            tabletMainActivity.blur(TABLET_TREATMENT_FRAGMENT);
            tabletMainActivity.tabletTreatmentFragment.set_idUser(0);


            // если нет пользователей, то чистим DiseasesFragment
            tabletMainActivity.blur(TABLET_DISEASES_FRAGMENT);
            tabletMainActivity.tabletDiseasesFragment.clearDataFromDiseasesFragment();
            tabletMainActivity.tabletDiseasesFragment.textViewAddDisease.setVisibility(View.INVISIBLE);
            tabletMainActivity.tabletDiseasesFragment.fabAddDisease.setVisibility(View.INVISIBLE);

            // если не осталось пользователей после удаления единственного пользователя, то
            // загружаем данные в tabletDiseasesFragment с помощю tabletMainActivity.tabletDiseasesFragment.initDiseasesLoader();
            // т.к. заболеваний у удаленного пользовател нет, то будет очищено окно tabletDiseasesFragment
            // при этом tabletMainActivity.tabletDiseasesFragment.textViewAddDisease будет не видимым, т.к.
            // в tabletDiseasesFragment idUser = 0

            // если был первый заход и не было пользователей, то этот медод не вызывается
            if (TabletMainActivity.userDeleted) {
                tabletMainActivity.tabletDiseasesFragment.initDiseasesLoader();
            }

        } else if (myDataSize == 1) {
            // если один пользователь, то делаем fabShowAnimation
            fabAddUser.startAnimation(fabShowAnimation);

            //txtTabletUsers.setBackgroundColor(getResources().getColor(R.color.colorPrimary));

            /*if (HomeActivity.iAmDoctor) {
                txtTabletUsers.setText(R.string.patients_title_activity);
            } else {
                txtTabletUsers.setText(R.string.users_title_activity);
            }*/

            // если один пользователь, то сразу загружаем его заболевания
            Long _userId = myData.get(0).get_userId();
            String userName = myData.get(0).getUserName();

            // делаем unBlur(TABLET_DISEASES_FRAGMENT)
            tabletMainActivity.unBlur(TABLET_DISEASES_FRAGMENT);

            // готовим tabletDiseasesTitle для рамещения в нем имени пользователя
            // прописываем туда имя пользоватлея
            tabletMainActivity.tabletDiseasesFragment.setTextUserName(userName);

            // устанавливаем idUser
            tabletMainActivity.tabletDiseasesFragment.set_idUser(_userId);

            // иниициализируем Loader заболеваний для загрузки заболеваний
            tabletMainActivity.tabletDiseasesFragment.initDiseasesLoader();

        } else {
            // если больше одного пользователя
            fabAddUser.startAnimation(fabShowAnimation);

            if (tabletMainActivity.tabletDiseasesFragment.get_idUser() == 0) {
                //если первый заход и в DiseasesFragment еще не отображаются данные,
                // то предлагаем сдеалть выбор пользоватля для отображения его заболеваний

                /*if (HomeActivity.iAmDoctor) {
                    txtTabletUsers.setText(R.string.tablet_diseases_select_patient);
                } else {
                    txtTabletUsers.setText(R.string.tablet_diseases_select_user);
                }

                txtTabletUsers.setBackgroundColor(getResources().getColor(R.color.colorFab));*/

                tabletMainActivity.blur(TABLET_DISEASES_FRAGMENT);
                tabletMainActivity.tabletDiseasesFragment.set_idUser(0);

                tabletMainActivity.blur(TABLET_TREATMENT_FRAGMENT);
                tabletMainActivity.tabletTreatmentFragment.set_idUser(0);

            } else if (TabletMainActivity.userUpdated &&
                    TabletMainActivity.user_IdInEdit == tabletMainActivity.tabletDiseasesFragment.get_idUser()) {
                // если пользовоатлеь, который был в DiseasesFragment обновился (поменял имя...)
                // то устанавливаем user_IdInEdit и userNameAfterUpdate в DiseasesFragment
                // и иниициализируем Loader заболеваний

                tabletMainActivity.tabletDiseasesFragment.set_idUser(TabletMainActivity.user_IdInEdit);
                tabletMainActivity.tabletDiseasesFragment.setTextUserName(TabletMainActivity.userNameAfterUpdate);

                // иниициализируем Loader заболеваний
                tabletMainActivity.tabletDiseasesFragment.initDiseasesLoader();

            } else if (TabletMainActivity.userDeleted &&
                    TabletMainActivity.user_IdInEdit == tabletMainActivity.tabletDiseasesFragment.get_idUser()) {
                // если пользовоатлеь, который был в DiseasesFragment удалился
                // то очищаем DiseasesFragment
                // и предлагаем сдеалть выбор пользоватля для отображения его заболеваний

                /*if (HomeActivity.iAmDoctor) {
                    txtTabletUsers.setText(R.string.tablet_diseases_select_patient);
                } else {
                    txtTabletUsers.setText(R.string.tablet_diseases_select_user);
                }

                txtTabletUsers.setBackgroundColor(getResources().getColor(R.color.colorFab));*/

                tabletMainActivity.blur(TABLET_DISEASES_FRAGMENT);

                tabletMainActivity.blur(TABLET_TREATMENT_FRAGMENT);
                tabletMainActivity.tabletTreatmentFragment.set_idUser(0);

                // в методе tabletMainActivity.tabletDiseasesFragment.clearDataFromDiseasesFragment();
                // происходит tabletMainActivity.tabletDiseasesFragment.set_idUser(0);
                tabletMainActivity.tabletDiseasesFragment.clearDataFromDiseasesFragment();

                // после удаления пользователя и загрузки данных в tabletUsersFragment
                // загружаем данные в tabletDiseasesFragment с помощю tabletMainActivity.tabletDiseasesFragment.initDiseasesLoader();
                // т.к. заболеваний у удаленного пользовател нет, то будет очищено окно tabletDiseasesFragment
                // при этом tabletMainActivity.tabletDiseasesFragment.textViewAddDisease будет не видимым, т.к.
                // в tabletDiseasesFragment idUser = 0
                tabletMainActivity.tabletDiseasesFragment.initDiseasesLoader();
            }
        }

        // после прохождения всех if выставляем флаги в false
        TabletMainActivity.userInserted = false;
        TabletMainActivity.userUpdated = false;
        TabletMainActivity.userDeleted = false;

        // прокручиваем пользователей вверх
        if (mScrollToStart && myData.size() != 0) {
            recyclerUsers.smoothScrollToPosition(0);
            mScrollToStart = false;
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        ArrayList<UserItem> myData = usersRecyclerViewAdapter.getUsersList();
        myData.clear();
        usersRecyclerViewAdapter.notifyDataSetChanged();
    }
}
