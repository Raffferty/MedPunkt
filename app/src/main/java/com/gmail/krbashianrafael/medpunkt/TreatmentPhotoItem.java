package com.gmail.krbashianrafael.medpunkt;

import android.support.annotation.NonNull;

import java.util.Date;

public class TreatmentPhotoItem implements Comparable<TreatmentPhotoItem> {
    private long _trPhotoId;
    private long _userId;
    private long _diseaseId;
    private String trPhotoName;
    private String trPhotoDate;

    private Date dateToCompare;

    // путь к фото
    private String trPhotoUri;

    public TreatmentPhotoItem(long _trPhotoId, long _userId, long _diseaseId, String trPhotoName, String trPhotoDate, String trPhotoUri) {
        this._trPhotoId = _trPhotoId;
        this._userId = _userId;
        this._diseaseId = _diseaseId;

        this.trPhotoDate = trPhotoDate;
        this.trPhotoName = trPhotoName;
        this.trPhotoUri = trPhotoUri;

        dateToCompare = getDate(trPhotoDate);
    }

    private Date getDate(String stringTrPhotoDate) {
        int mYear;
        int mMonth;
        int mDay;

        if (stringTrPhotoDate != null && stringTrPhotoDate.contains("-")) {
            String[] mDayMonthYear = stringTrPhotoDate.trim().split("-");
            mYear = Integer.valueOf(mDayMonthYear[2]);
            mMonth = Integer.valueOf(mDayMonthYear[1]) - 1;
            mDay = Integer.valueOf(mDayMonthYear[0]);

            return new Date(mYear, mMonth, mDay);
        }

        return null;
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

    @Override
    public int compareTo(@NonNull TreatmentPhotoItem o) {
        if (dateToCompare != null && o.dateToCompare != null) {
            return this.dateToCompare.compareTo(o.dateToCompare);
        }
        return 0;
    }
}
