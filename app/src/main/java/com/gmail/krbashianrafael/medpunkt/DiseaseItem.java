package com.gmail.krbashianrafael.medpunkt;

public class DiseaseItem {
    private long _diseaseId;
    private long _diseaseUserId;
    private String userName;
    private String diseaseName;
    private String diseaseDate;
    private String diseaseTreatment;

    DiseaseItem(long _diseaseId, long _diseaseUserId, String userName, String diseaseName, String diseaseDate, String diseaseTreatment) {
        this._diseaseId = _diseaseId;
        this._diseaseUserId = _diseaseUserId;
        this.diseaseDate = diseaseDate;
        this.userName = userName;
        this.diseaseName = diseaseName;
        this.diseaseTreatment = diseaseTreatment;
    }

    public long get_diseaseId() {
        return _diseaseId;
    }

    public long get_diseaseUserId() {
        return _diseaseUserId;
    }

    public String getDiseaseDate() {
        return diseaseDate;
    }

    public String getUserName() {
        return userName;
    }

    public String getDiseaseName() {
        return diseaseName;
    }

    public String getTreatmentText() {
        return diseaseTreatment;
    }
}
