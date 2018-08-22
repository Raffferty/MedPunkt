package com.gmail.krbashianrafael.medpunkt;

import android.support.annotation.NonNull;

public class UserItem implements Comparable<UserItem> {
    private long _userId;
    private String userBirthDate;
    private String userName;

    // путь к фото
    private String userPhotoUri;

    public UserItem(long _userId, String userBirthDate, String userName, String userPhotoUri) {
        this._userId = _userId;
        this.userBirthDate = userBirthDate;
        this.userName = userName;
        this.userPhotoUri = userPhotoUri;
    }

    public long get_userId() {
        return _userId;
    }

    public String getUserBirthDate() {
        return userBirthDate;
    }

    public String getUserName() {
        return userName;
    }

    public String getUserPhotoUri() {
        return userPhotoUri;
    }

    @Override
    public int compareTo(@NonNull UserItem o) {
        if (userName!=null && o.userName!=null){
            return this.userName.compareTo(o.userName);
        }
        return 0;
    }
}
