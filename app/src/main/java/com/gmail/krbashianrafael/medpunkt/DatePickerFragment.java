package com.gmail.krbashianrafael.medpunkt;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
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

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);

        return new DatePickerDialog(Objects.requireNonNull(getActivity()),
                this, mYear, mMonth, mDay);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onDateSet(@NonNull DatePicker view, int year, int month, int day) {
        EditText txtDate = Objects.requireNonNull(getActivity()).findViewById(R.id.editText_date);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
        GregorianCalendar date = new GregorianCalendar(year, month, day);
        String formatedDate = simpleDateFormat.format(date.getTime());
        txtDate.setText(simpleDateFormat.format(formatedDate + " "));
    }
}
