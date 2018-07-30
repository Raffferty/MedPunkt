package com.gmail.krbashianrafael.medpunkt;

public class TreatmentPhotoItem {
    private long _trPhotoId;
    private long _userId;
    private long _diseaseId;
    private String itemDate;
    private String itemName;

    // путь к фото
    private String itemPhotoUri;

    TreatmentPhotoItem(long _trPhotoId, long _userId, long _diseaseId, String itemDate, String itemName, String itemPhotoUri) {
        this._trPhotoId = _trPhotoId;
        this._userId = _userId;
        this._diseaseId = _diseaseId;

        this.itemDate = itemDate;
        this.itemName = itemName;
        this.itemPhotoUri = itemPhotoUri;
    }

    public long get_trPhotoId() {
        return _trPhotoId;
    }

    public long get_userId() {
        return _userId;
    }

    public long get_diseaseId() {
        return _diseaseId;
    }

    public String getItemDate() {
        return itemDate;
    }

    public String getItemName() {
        return itemName;
    }

    public String getItemPhotoUri() {
        return itemPhotoUri;
    }
}
