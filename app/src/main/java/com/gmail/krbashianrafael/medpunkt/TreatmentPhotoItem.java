package com.gmail.krbashianrafael.medpunkt;

public class TreatmentPhotoItem {
    private long _trPhotoId;
    private long _userId;
    private long _diseaseId;
    private String trPhotoName;
    private String trPhotoDate;

    // путь к фото
    private String trPhotoUri;

    TreatmentPhotoItem(long _trPhotoId, long _userId, long _diseaseId, String trPhotoName, String trPhotoDate, String trPhotoUri) {
        this._trPhotoId = _trPhotoId;
        this._userId = _userId;
        this._diseaseId = _diseaseId;

        this.trPhotoDate = trPhotoDate;
        this.trPhotoName = trPhotoName;
        this.trPhotoUri = trPhotoUri;
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

    public String getTrPhotoDate() {
        return trPhotoDate;
    }

    public String getTrPhotoName() {
        return trPhotoName;
    }

    public String getTrPhotoUri() {
        return trPhotoUri;
    }
}
