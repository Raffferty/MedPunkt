package com.gmail.krbashianrafael.medpunkt;

public class UserItem {
    private long _userId;
    private String userBirthDate;
    private String userName;

    // путь к фото
    private String userPhotoUri;

    UserItem(int _userId, String userBirthDate, String userName, String userPhotoUri) {
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
}
