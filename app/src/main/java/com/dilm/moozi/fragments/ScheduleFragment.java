package com.dilm.moozi.fragments;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.appcompat.widget.AppCompatEditText;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.dilm.moozi.R;
import com.dilm.moozi.dialogs.CommonDialog;
import com.dilm.moozi.dialogs.CommonLoadingProgressDialog;
import com.dilm.moozi.dialogs.DatePickerDialog;
import com.dilm.moozi.dialogs.DialogStudentListDialog;
import com.dilm.moozi.dialogs.TimePickerDialog;
import com.dilm.moozi.models.Class;
import com.dilm.moozi.models.login.LoginResponse;
import com.dilm.moozi.models.schedule.Schedule;
import com.dilm.moozi.network.ApiClient;
import com.dilm.moozi.network.ApiService;
import com.dilm.moozi.utils.Constants;
import com.dilm.moozi.utils.SharedPreferenceManager;
import com.google.android.material.button.MaterialButton;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.net.HttpURLConnection;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ScheduleFragment extends Fragment {

    private AppCompatEditText editTextSubject, etSubDescription, etDate, etStartTime, etEndTime;
    private MaterialButton btnCreateSchedule, btnDeleteSchedule;
    private ImageView imgViewAddStudent;
    private String scheduleId;
    private ProgressDialog progressDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_schedule, container, false);
        initViews(rootView);
        initObjects();
        initListeners();
        return rootView;
    }

    private void initViews(View view) {
        editTextSubject = view.findViewById(R.id.etSubject);
        etSubDescription = view.findViewById(R.id.etSubDescription);
        etDate = view.findViewById(R.id.etDate);
        etStartTime = view.findViewById(R.id.etStartTime);
        etEndTime = view.findViewById(R.id.etEndTime);
        btnCreateSchedule = view.findViewById(R.id.btnCreateSchedule);
        imgViewAddStudent = view.findViewById(R.id.imgViewAddStudent);
        btnDeleteSchedule = view.findViewById(R.id.btnDeleteSchedule);
    }

    private void initObjects() {
        Gson gson = new Gson();
        LoginResponse loginResponse = gson.fromJson(SharedPreferenceManager.getInstance(getContext()).getStringPreference(Constants.USER, Constants.DEFAULT_VALUE), LoginResponse.class);
        boolean isTeacher = loginResponse.getRoles().contains(Constants.ROLE_TEACHER);

        if (isTeacher) {
            btnCreateSchedule.setVisibility(View.VISIBLE);
        } else {
            btnCreateSchedule.setVisibility(View.GONE);
        }

        if (getArguments() != null) {
            Class clz = gson.fromJson(getArguments().getString(Constants.DATA), Class.class);
            scheduleId = clz.getId();
            editTextSubject.setText(clz.getName());
            etSubDescription.setText(clz.getDescription());
            etDate.setText(clz.getDate());
            etStartTime.setText(clz.getStartTime());
            etEndTime.setText(clz.getEndTime());
            if (isTeacher) {
                imgViewAddStudent.setVisibility(View.VISIBLE);
                btnDeleteSchedule.setVisibility(View.VISIBLE);
            }
            btnCreateSchedule.setText(getString(R.string.btn_txt_update_schedule));
        }
    }

    private void initListeners() {
        etDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(requireActivity().getSupportFragmentManager()) {
                    @Override
                    public void onPositiveButtonClick(String date) {
                        etDate.setText(date);
                    }
                };
                datePickerDialog.showPicker();
            }
        });

        etStartTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(), requireActivity().getSupportFragmentManager()) {
                    @Override
                    public void onPositiveButtonClick(String time) {
                        etStartTime.setText(time);
                    }
                };
                timePickerDialog.showPicker();
            }
        });

        etEndTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(), requireActivity().getSupportFragmentManager()) {
                    @Override
                    public void onPositiveButtonClick(String time) {
                        etEndTime.setText(time);
                    }
                };
                timePickerDialog.showPicker();
            }
        });

        btnCreateSchedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String subject = editTextSubject.getText().toString();
                String description = etSubDescription.getText().toString();
                String date = etDate.getText().toString();
                String startTime = etStartTime.getText().toString();
                String endTime = etEndTime.getText().toString();

                if (!subject.isEmpty() || !description.isEmpty() || !date.isEmpty() || !startTime.isEmpty() || !endTime.isEmpty()) {
                    Schedule schedule = new Schedule();
                    if (scheduleId != null) {
                        schedule.setId(scheduleId);
                    }
                    schedule.setName(subject);
                    schedule.setDescription(description);
                    schedule.setDate(date);
                    schedule.setStartTime(startTime);
                    schedule.setEndTime(endTime);

                    progressDialog = CommonLoadingProgressDialog.getInstance()
                            .createProgress(getContext(), Constants.EMP_VALUE);

                    apiCreateUpdateSchedule(schedule);
                }
            }
        });

        imgViewAddStudent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogStudentListDialog dialogStudentListDialog = new DialogStudentListDialog(getContext(), scheduleId);
                dialogStudentListDialog.show();
            }
        });

        btnDeleteSchedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog = CommonLoadingProgressDialog.getInstance()
                        .createProgress(getContext(), Constants.EMP_VALUE);
                apiDeleteSchedule(scheduleId);
            }
        });
    }

    private void apiCreateUpdateSchedule(Schedule schedule) {

        String token = SharedPreferenceManager.getInstance(getContext())
                .getStringPreference(
                        Constants.TOKEN,
                        Constants.DEFAULT_VALUE);

        ApiService apiService = ApiClient.getRetrofitClient();

        Call<JsonObject> call;
        if (schedule.getId() != null) {
            call = apiService.updateSchedule(token, schedule);
        } else {
            call = apiService.createSchedule(token, schedule);
        }
        call.enqueue(new Callback<JsonObject>() {

            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.code() == HttpURLConnection.HTTP_CREATED) {
                    CommonDialog commonDialog = new CommonDialog(getContext(),
                            scheduleId != null ? getString(R.string.title_update): getString(R.string.title_create),
                        scheduleId != null ? getString(R.string.body_update): getString(R.string.body_create),
                        getString(R.string.btn_txt_done), Constants.EMP_VALUE) {
                    @Override
                    public void onClick() {
                        if (scheduleId == null) {
                            requireActivity().getSupportFragmentManager().popBackStack();
                        }
                    }
                };
                commonDialog.open();
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

    private void apiDeleteSchedule(String scheduleId) {

        String token = SharedPreferenceManager.getInstance(getContext())
                .getStringPreference(
                        Constants.TOKEN,
                        Constants.DEFAULT_VALUE);

        ApiService apiService = ApiClient.getRetrofitClient();

        Call<JsonObject> call = apiService.deleteSchedule(token, scheduleId);
        call.enqueue(new Callback<JsonObject>() {

            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.code() == HttpURLConnection.HTTP_OK) {
                    CommonDialog commonDialog = new CommonDialog(getContext(),
                            getString(R.string.title_delete),
                            getString(R.string.body_delete),
                            getString(R.string.btn_txt_done), Constants.EMP_VALUE) {
                        @Override
                        public void onClick() {
                            requireActivity().getSupportFragmentManager().popBackStack();
                        }
                    };
                    commonDialog.open();
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
}