package com.gmail.krbashianrafael.medpunkt;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.widget.DatePicker;
import android.widget.EditText;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.Objects;


public class DatePickerFragment extends DialogFragment
        implements DatePickerDialog.OnDateSetListener {

    String dateInEditTextDate;
    EditText editTextDate;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        editTextDate = Objects.requireNonNull(getActivity()).findViewById(R.id.editText_date);
        dateInEditTextDate = editTextDate.getText().toString().trim();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        int mYear;
        int mMonth;
        int mDay;

        if (dateInEditTextDate != null && dateInEditTextDate.contains("-")) {
            String[] mDayMonthYear = dateInEditTextDate.split("-");
            mYear = Integer.valueOf(mDayMonthYear[2]);
            mMonth = Integer.valueOf(mDayMonthYear[1]) - 1;
            mDay = Integer.valueOf(mDayMonthYear[0]);
        } else {
            final Calendar c = Calendar.getInstance();
            mYear = c.get(Calendar.YEAR);
            mMonth = c.get(Calendar.MONTH);
            mDay = c.get(Calendar.DAY_OF_MONTH);
        }

        return new DatePickerDialog(Objects.requireNonNull(getActivity()),
                this, mYear, mMonth, mDay);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onDateSet(@NonNull DatePicker view, int year, int month, int day) {
        //EditText editTextDate = Objects.requireNonNull(getActivity()).findViewById(R.id.editText_date);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
        GregorianCalendar date = new GregorianCalendar(year, month, day);
        String formatedDate = simpleDateFormat.format(date.getTime()) + " ";
        editTextDate.setText(formatedDate);
    }
}
