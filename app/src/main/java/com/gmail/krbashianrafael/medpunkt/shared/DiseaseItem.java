package com.gmail.krbashianrafael.medpunkt.shared;

import android.support.annotation.NonNull;

import java.util.Calendar;

public class DiseaseItem implements Comparable<DiseaseItem> {
    private final long _diseaseId;
    private final long _diseaseUserId;
    private final String diseaseName;
    private final String diseaseDate;
    private final String diseaseTreatment;

    private final Calendar dateToCompare;

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

    long get_diseaseUserId() {
        return _diseaseUserId;
    }

    String getDiseaseDate() {
        return diseaseDate;
    }

    String getDiseaseName() {
        return diseaseName;
    }

    String getTreatmentText() {
        return diseaseTreatment;
    }

    @Override
    public int compareTo(@NonNull DiseaseItem o) {
        if (dateToCompare != null && o.dateToCompare != null) {
            return o.dateToCompare.compareTo(this.dateToCompare);
        }
        return 0;
    }
}
