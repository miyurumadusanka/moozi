package com.dilm.moozi.dialogs;

import android.content.Context;

import androidx.fragment.app.FragmentManager;

import com.dilm.moozi.utils.Utility;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;

import java.util.Calendar;

public abstract class TimePickerDialog {

    MaterialTimePicker timePicker;
    FragmentManager supportFragmentManager;

    public TimePickerDialog(Context context, FragmentManager supportFragmentManager) {
        this.supportFragmentManager = supportFragmentManager;

        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        timePicker = new MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_12H)
                .setTitleText("Select a time")
                .setHour(hour)
                .setMinute(minute)
                .build();
        timePicker.addOnPositiveButtonClickListener(v -> {
            int selectedHour = timePicker.getHour();
            int selectedMinute = timePicker.getMinute();
            calendar.set(Calendar.HOUR_OF_DAY, selectedHour);
            calendar.set(Calendar.MINUTE, selectedMinute);
            onPositiveButtonClick(Utility.getInstance(context).getTimeHHmmAMPM(calendar.getTime()));
        });
    }

    public void showPicker() {
        timePicker.show(supportFragmentManager, "TIME_PICKER");
    }

    public abstract void onPositiveButtonClick(String time);
}
