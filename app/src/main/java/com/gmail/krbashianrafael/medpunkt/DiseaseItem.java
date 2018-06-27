package com.gmail.krbashianrafael.medpunkt;

public class DiseaseItem {
    private int _diseaseId;
    private String diseaseDate;
    private String diseaseName;
    private String treatmentText;

    public DiseaseItem(int _diseaseId, String diseaseName, String diseaseDate, String treatmentText) {
        this._diseaseId = _diseaseId;
        this.diseaseDate = diseaseDate;
        this.diseaseName = diseaseName;
        this.treatmentText = treatmentText;
    }

    public int get_diseaseId() {
        return _diseaseId;
    }

    public String getDiseaseDate() {
        return diseaseDate;
    }

    public String getDiseaseName() {
        return diseaseName;
    }

    public String getTreatmentText() {
        return treatmentText;
    }
}
