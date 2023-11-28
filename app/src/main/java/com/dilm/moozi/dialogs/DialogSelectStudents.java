package com.dilm.moozi.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dilm.moozi.R;
import com.dilm.moozi.adapters.StudentRecyclerAdapter;
import com.dilm.moozi.models.student.Student;
import com.dilm.moozi.utils.Utility;

import java.util.ArrayList;
import java.util.List;

public class DialogSelectStudents extends Dialog {

    private Activity activity;
    private RecyclerView recyclerViewStudents;
    private TextView confirmStudents;
    private ImageView imgViewClose;
    private EditText etStudentSearch;
    private Button searchClose;
    private List<Student> studentList, selectedStudentList;
    private StudentRecyclerAdapter studentRecyclerAdapter;

    public DialogSelectStudents(@NonNull Context context, Activity activity) {
        super(context);
        this.activity = activity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.layout_students_dialog);
        getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.MATCH_PARENT);
        getWindow().setBackgroundDrawable(new ColorDrawable(getContext().getColor(R.color.white)));

        initViews();
        initObjects();
        initListeners();
    }

    private void initViews() {
        recyclerViewStudents = findViewById(R.id.rv_students);
        confirmStudents = findViewById(R.id.confirm);
        imgViewClose = findViewById(R.id.close);
        etStudentSearch = findViewById(R.id.student_search);
        searchClose = findViewById(R.id.search_close);
    }

    private void initObjects() {
        studentList = new ArrayList<>();
        selectedStudentList = new ArrayList<>();
        for (int i=0; i<8; i++) {
            Student student = new Student();
            student.setName("Student0"+(i+1));
            student.setStudentId("STU0000"+(i+1));
            if (i==2) {
                student.setSelected(true);
            }
            studentList.add(student);
        }
        initAdapters();
    }

    private void initAdapters() {
        studentRecyclerAdapter = new StudentRecyclerAdapter(studentList, selectedStudentList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerViewStudents.setLayoutManager(mLayoutManager);
        recyclerViewStudents.setItemAnimator(new DefaultItemAnimator());
        recyclerViewStudents.setHasFixedSize(true);
        recyclerViewStudents.setAdapter(studentRecyclerAdapter);
        studentRecyclerAdapter.notifyDataSetChanged();
    }

    private void initListeners() {
        imgViewClose. setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        confirmStudents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (Student student :
                        selectedStudentList) {
                    System.out.println(student.getStudentId());
                }
            }
        });
        etStudentSearch.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                searchClose.setVisibility(View.VISIBLE);
                searchClose.setVisibility(View.VISIBLE);
                etStudentSearch.setFocusableInTouchMode(true);
                return false;
            }
        });
        etStudentSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                studentRecyclerAdapter.getFilter().filter(charSequence);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        searchClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utility.hideKeyboad(DialogSelectStudents.this, activity);
                etStudentSearch.getText().clear();
                etStudentSearch.setFocusable(false);
                searchClose.setVisibility(View.GONE);
                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
            }
        });
    }
}
