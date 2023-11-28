package com.dilm.moozi.dialogs;

import androidx.fragment.app.FragmentManager;
import com.google.android.material.datepicker.MaterialDatePicker;

import java.util.Calendar;
import java.util.Locale;

public abstract class DatePickerDialog {

    MaterialDatePicker<Long> datePicker;
    FragmentManager supportFragmentManager;

    public DatePickerDialog(FragmentManager supportFragmentManager) {
        this.supportFragmentManager = supportFragmentManager;
        Calendar calendar = Calendar.getInstance();
        datePicker = MaterialDatePicker
                .Builder
                .datePicker()
                .setSelection(calendar.getTimeInMillis())
                .setTitleText("Select date of birth")
                .build();
        datePicker.addOnPositiveButtonClickListener(selection -> {
            calendar.setTimeInMillis(selection);
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            onPositiveButtonClick(String.format(Locale.getDefault(), "%04d-%02d-%02d", year, month + 1, day));
        });
    }
    public void showPicker() {
        datePicker.show(supportFragmentManager, "DATE_PICKER");
    }

    public abstract void onPositiveButtonClick(String date);
}
