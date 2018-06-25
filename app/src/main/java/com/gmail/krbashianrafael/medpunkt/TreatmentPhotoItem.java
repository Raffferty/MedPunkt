package com.gmail.krbashianrafael.medpunkt;

public class TreatmentPhotoItem {
    private int _treatmentId;
    private String itemDate;
    private String itemName;

    // путь к фото
    private String itemPhotoUri;

    public TreatmentPhotoItem(int _treatmentId, String itemDate, String itemName, String itemPhotoUri) {
        this._treatmentId = _treatmentId;
        this.itemDate = itemDate;
        this.itemName = itemName;
        this.itemPhotoUri = itemPhotoUri;
    }

    public int get_treatmentId() {
        return _treatmentId;
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
