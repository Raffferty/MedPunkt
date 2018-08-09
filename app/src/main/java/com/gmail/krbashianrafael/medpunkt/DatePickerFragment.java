package com.gmail.krbashianrafael.medpunkt;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Build;
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
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        /*DatePickerDialog datePickerDialog = new DatePickerDialog(Objects.requireNonNull(getActivity()),
                android.R.style.Theme_Holo_Light_Dialog,
                this, year, month, day);

        datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        return datePickerDialog;*/

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return new DatePickerDialog(Objects.requireNonNull(getActivity()),
                    android.R.style.Theme_Material_Light_Dialog_Alert,
                    this, year, month, day);
        }else {
            return new DatePickerDialog(Objects.requireNonNull(getActivity()),
                    this, year, month, day);
        }

    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onDateSet(@NonNull DatePicker view, int year, int month, int day) {
        EditText txtDate = Objects.requireNonNull(getActivity()).findViewById(R.id.editText_date);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
        GregorianCalendar date = new GregorianCalendar(year,month,day);
        txtDate.setText(simpleDateFormat.format(date.getTime())+" ");
    }
}
