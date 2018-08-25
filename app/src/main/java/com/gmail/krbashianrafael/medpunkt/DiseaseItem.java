package com.gmail.krbashianrafael.medpunkt;

import android.support.annotation.NonNull;

import java.util.Calendar;

public class DiseaseItem implements Comparable<DiseaseItem> {
    private long _diseaseId;
    private long _diseaseUserId;
    private String diseaseName;
    private String diseaseDate;
    private String diseaseTreatment;

    private Calendar dateToCompare;

    public DiseaseItem(long _diseaseId, long _diseaseUserId, String diseaseName, String diseaseDate, String diseaseTreatment) {
        this._diseaseId = _diseaseId;
        this._diseaseUserId = _diseaseUserId;
        this.diseaseDate = diseaseDate;
        this.diseaseName = diseaseName;
        this.diseaseTreatment = diseaseTreatment;

        dateToCompare = getDate(diseaseDate);
    }

    private Calendar getDate(String stringdiseaseDate) {
        int mYear;
        int mMonth;
        int mDay;

        if (stringdiseaseDate != null && stringdiseaseDate.contains("-")) {
            String[] mDayMonthYear = stringdiseaseDate.trim().split("-");
            mYear = Integer.valueOf(mDayMonthYear[2]);
            mMonth = Integer.valueOf(mDayMonthYear[1]) - 1;
            mDay = Integer.valueOf(mDayMonthYear[0]);

            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.YEAR, mYear);
            cal.set(Calendar.MONTH, mMonth);
            cal.set(Calendar.DAY_OF_MONTH, mDay);

            return cal;
        }

        return null;
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

    public String getDiseaseName() {
        return diseaseName;
    }

    public String getTreatmentText() {
        return diseaseTreatment;
    }

    @Override
    public int compareTo(@NonNull DiseaseItem o) {
        if (diseaseName != null && o.diseaseName != null) {
            return this.dateToCompare.compareTo(o.dateToCompare);
        }
        return 0;
    }
}
