package com.dilm.moozi.dialogs;

import android.content.Context;
import android.content.DialogInterface;

import androidx.appcompat.app.AlertDialog;

public abstract class CommonDialog {

    private AlertDialog.Builder builder;

    public CommonDialog(Context context, String title, String message, String txtPosBtn, String txtNegBtn) {
        builder = new AlertDialog.Builder(context);
        builder = new AlertDialog.Builder(context);
        builder.setMessage(message);
        builder.setTitle(title);
        builder.setCancelable(false);
        builder.setPositiveButton(txtPosBtn, (DialogInterface.OnClickListener) (dialog, which) -> {
            onClick();
        });
        builder.setNegativeButton(txtNegBtn, (DialogInterface.OnClickListener) (dialog, which) -> {
            dialog.cancel();
        });
    }

    public void open() {
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public abstract void onClick();
}
