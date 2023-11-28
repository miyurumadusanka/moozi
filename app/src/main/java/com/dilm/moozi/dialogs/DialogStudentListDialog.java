package com.dilm.moozi.dialogs;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dilm.moozi.R;
import com.dilm.moozi.adapters.StudentRecyclerAdapter2;
import com.dilm.moozi.models.profile.Profile;
import com.dilm.moozi.models.profile.StudentProfile;
import com.dilm.moozi.models.schedule.ResponseAddStudent;
import com.dilm.moozi.models.schedule.ScheduleStudents;
import com.dilm.moozi.models.student.Student;
import com.dilm.moozi.network.ApiClient;
import com.dilm.moozi.network.ApiService;
import com.dilm.moozi.utils.Constants;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DialogStudentListDialog extends Dialog {

    private RecyclerView recyclerViewStudents;
    private ImageView imgClose;
    private TextView tvAdd;
    private List<Student> studentList;
    private StudentRecyclerAdapter2 studentRecyclerAdapter;
    private String scheduleId;
    private ProgressDialog progressDialog;

    public DialogStudentListDialog(@NonNull Context context, String scheduleId) {
        super(context);
        this.scheduleId = scheduleId;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.layout_student_list_dialog);
        getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);

        initViews();
        initObjects();
        initListeners();
    }

    private void initViews() {
        recyclerViewStudents = findViewById(R.id.rv_students);
        imgClose = findViewById(R.id.close);
        tvAdd = findViewById(R.id.tv_add);
    }

    private void initObjects() {
        studentList = new ArrayList<>();
        initAdapters();
        apiScheduleStudents(scheduleId);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                progressDialog = CommonLoadingProgressDialog.getInstance()
                        .createProgress(getContext(), Constants.EMP_VALUE);
            }
        },100);
    }

    private void initAdapters() {
        studentRecyclerAdapter = new StudentRecyclerAdapter2(studentList) {
            @Override
            public void onRemoved(int position) {
                progressDialog.show();
                apiRemoveScheduleStudent(scheduleId, position);
            }
        };
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerViewStudents.setLayoutManager(mLayoutManager);
        recyclerViewStudents.setItemAnimator(new DefaultItemAnimator());
        recyclerViewStudents.setHasFixedSize(true);
        recyclerViewStudents.setAdapter(studentRecyclerAdapter);
        studentRecyclerAdapter.notifyDataSetChanged();
    }

    private void initListeners() {
        imgClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        tvAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogAddStudentDialog dialogAddStudentDialog = new DialogAddStudentDialog(getContext()) {
                    @Override
                    public void onAdd(String studentCode) {
                        progressDialog.show();
                        apiAddScheduleStudent(scheduleId, studentCode);
                    }
                };
                dialogAddStudentDialog.show();
            }
        });
    }

    private void apiScheduleStudents(String scheduleId) {
        ApiService apiService = ApiClient.getRetrofitClient();

        Call<JsonObject> call = apiService.getScheduleStudentList(scheduleId);
        call.enqueue(new Callback<JsonObject>() {

            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                Gson gson = new Gson();
                if (response.code() == HttpURLConnection.HTTP_OK) {
                    ScheduleStudents scheduleStudents = gson.fromJson(response.body(), ScheduleStudents.class);
                    notifyDataSetChanged(scheduleStudents);
                } else if (response.code() == HttpURLConnection.HTTP_UNAUTHORIZED) {
                    Toast.makeText(getContext(), R.string.invalid_credential, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), R.string.network_error, Toast.LENGTH_SHORT).show();
                }
                progressDialog.dismiss();
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(getContext(), R.string.connection_error, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void apiAddScheduleStudent(String scheduleId, String studentId) {
        ApiService apiService = ApiClient.getRetrofitClient();

        Call<JsonObject> call = apiService.addScheduleStudent(scheduleId, studentId);
        call.enqueue(new Callback<JsonObject>() {

            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                Gson gson = new Gson();
                if (response.code() == HttpURLConnection.HTTP_OK) {
                    ResponseAddStudent responseAddStudent = gson.fromJson(response.body(), ResponseAddStudent.class);
                    Student student = new Student();
                    student.setName(responseAddStudent.getProfile().getFullName());
                    student.setStudentId(responseAddStudent.getProfile().getUserCode());
                    studentList.add(student);
                    studentRecyclerAdapter.notifyDataSetChanged();
                } else if (response.code() == HttpURLConnection.HTTP_UNAUTHORIZED) {
                    Toast.makeText(getContext(), R.string.invalid_credential, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), R.string.network_error, Toast.LENGTH_SHORT).show();
                }
                progressDialog.dismiss();
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(getContext(), R.string.connection_error, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void apiRemoveScheduleStudent(String scheduleId, int position) {
        ApiService apiService = ApiClient.getRetrofitClient();

        Call<JsonObject> call = apiService.removeScheduleStudent(scheduleId, studentList.get(position).getStudentId());
        call.enqueue(new Callback<JsonObject>() {

            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.code() == HttpURLConnection.HTTP_OK) {
                    studentList.remove(position);
                    studentRecyclerAdapter.notifyItemRemoved(position);
                } else if (response.code() == HttpURLConnection.HTTP_UNAUTHORIZED) {
                    Toast.makeText(getContext(), R.string.invalid_credential, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), R.string.network_error, Toast.LENGTH_SHORT).show();
                }
                progressDialog.dismiss();
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(getContext(), R.string.connection_error, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void notifyDataSetChanged(ScheduleStudents scheduleStudents) {
        for (StudentProfile profile :
                scheduleStudents.getStudentProfileList()) {
            Student student = new Student();
            student.setStudentId(profile.getUserCode());
            student.setName(profile.getFullName());
            student.setAttendance(profile.isAttendance());
            studentList.add(student);
        }
        if (studentRecyclerAdapter != null) {
            studentRecyclerAdapter.notifyDataSetChanged();
        }
    }
}
