package com.gmail.krbashianrafael.medpunkt.shared;

import android.support.annotation.NonNull;

import java.util.Calendar;

public class TreatmentPhotoItem implements Comparable<TreatmentPhotoItem> {
    private final long _trPhotoId;
    private final long _userId;
    private final long _diseaseId;
    private final String trPhotoName;
    private final String trPhotoDate;

    private final Calendar dateToCompare;

    private final String trPhotoUri;

    TreatmentPhotoItem(long _trPhotoId, long _userId, long _diseaseId, String trPhotoName, String trPhotoDate, String trPhotoUri) {
        this._trPhotoId = _trPhotoId;
        this._userId = _userId;
        this._diseaseId = _diseaseId;

        this.trPhotoDate = trPhotoDate;
        this.trPhotoName = trPhotoName;
        this.trPhotoUri = trPhotoUri;

        dateToCompare = getDate(trPhotoDate);
    }

    private Calendar getDate(String stringTrPhotoDate) {
        int mYear;
        int mMonth;
        int mDay;

        if (stringTrPhotoDate != null && stringTrPhotoDate.contains("-")) {
            String[] mDayMonthYear = stringTrPhotoDate.trim().split("-");
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

    public long get_trPhotoId() {
        return _trPhotoId;
    }

    long get_userId() {
        return _userId;
    }

    long get_diseaseId() {
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

    @Override
    public int compareTo(@NonNull TreatmentPhotoItem o) {
        if (dateToCompare != null && o.dateToCompare != null) {
            return o.dateToCompare.compareTo(this.dateToCompare);
        }
        return 0;
    }
}
