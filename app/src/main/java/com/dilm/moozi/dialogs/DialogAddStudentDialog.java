package com.dilm.moozi.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatEditText;

import com.dilm.moozi.R;
import com.google.android.material.button.MaterialButton;

public abstract class DialogAddStudentDialog extends Dialog {

    private AppCompatEditText etStudentId;
    private MaterialButton btnSave;

    public DialogAddStudentDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_add_student_dialog);

        initViews();
        initListeners();
    }

    private void initViews() {
        etStudentId = findViewById(R.id.etStudentId);
        btnSave = findViewById(R.id.btnSave);
    }

    private void initListeners() {
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onAdd(etStudentId.getText().toString());
                dismiss();
            }
        });
    }

    public abstract void onAdd(String studentCode);
}
