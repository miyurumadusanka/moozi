package com.dilm.moozi.dialogs;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.dilm.moozi.R;

public class CommonLoadingProgressDialog {

    private static CommonLoadingProgressDialog instance;

    private CommonLoadingProgressDialog() {
    }

    public static synchronized CommonLoadingProgressDialog getInstance() {
        if (instance == null) {
            instance = new CommonLoadingProgressDialog();
        }
        return instance;
    }

    public ProgressDialog createProgress(Context context, String title){
        View v = LayoutInflater.from(context).inflate(R.layout.layout_progress, null);
        TextView text = v.findViewById(R.id.text);
        text.setText(title);
        ProgressDialog pDialog = new ProgressDialog(context);
        pDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(false);
        pDialog.show();
        pDialog.setContentView(v);
        return pDialog;
    }
}
