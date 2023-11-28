package com.dilm.moozi.utils;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatEditText;

import com.dilm.moozi.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

public class Utility {

    private static Utility instance;
    private final InputMethodManager imm;
    private final String uuid;
    private final SimpleDateFormat sdf;

    private Utility(Context context) {
        imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        uuid = UUID.randomUUID().toString();
        sdf = new SimpleDateFormat("hh:mm a", Locale.getDefault());
    }

    public static synchronized Utility getInstance(Context context) {
        if (instance == null) {
            instance = new Utility(context);
        }
        return instance;
    }

    public void openKeyBoard(AppCompatEditText editText) {
        if (imm != null) {
            imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
            editText.requestFocus();
        }
    }

    public static void hideKeyboad(Dialog d, Activity c) {
        if (d.getCurrentFocus() != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) c.getSystemService(c.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(d.getCurrentFocus().getWindowToken(), 0);
        }
    }

    public String getUUID() {
        return uuid;
    }

    public String getTimeHHmmAMPM(Date time) {
        return sdf.format(time);
    }
}
