package com.dilm.moozi.fragments;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.dilm.moozi.R;
import com.dilm.moozi.adapters.ClassRecyclerAdapter;
import com.dilm.moozi.dialogs.CommonLoadingProgressDialog;
import com.dilm.moozi.models.Class;
import com.dilm.moozi.models.history.ResponseScheduleHistory;
import com.dilm.moozi.models.login.LoginResponse;
import com.dilm.moozi.network.ApiClient;
import com.dilm.moozi.network.ApiService;
import com.dilm.moozi.utils.Constants;
import com.dilm.moozi.utils.SharedPreferenceManager;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HistoryFragment extends Fragment {

    private RecyclerView rvClassesHistory;
    private List<Class> classList;
    private ClassRecyclerAdapter todayClassRecyclerAdapter;
    private ProgressDialog progressDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history, container, false);
        initViews(view);
        initObjects();
        return view;
    }

    private void initViews(View view) {
        rvClassesHistory = view.findViewById(R.id.rvClassesHistory);
    }

    private void initObjects() {
        classList = new ArrayList<>();
        Gson gson = new Gson();
        LoginResponse loginResponse = gson.fromJson(SharedPreferenceManager.getInstance(getContext()).getStringPreference(Constants.USER, Constants.DEFAULT_VALUE), LoginResponse.class);
        boolean isTeacher = loginResponse.getRoles().contains(Constants.ROLE_TEACHER);

        progressDialog = CommonLoadingProgressDialog.getInstance()
                .createProgress(getContext(), Constants.EMP_VALUE);

        if (isTeacher) {
            apiGetSchedule();
        } else {
            apiGetStudentSchedule();
        }
        initAdapters();
    }

    private void initAdapters() {
        todayClassRecyclerAdapter = new ClassRecyclerAdapter(classList, true, getActivity());
        RecyclerView.LayoutManager mLayoutManager1 = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        rvClassesHistory.setLayoutManager(mLayoutManager1);
        rvClassesHistory.setItemAnimator(new DefaultItemAnimator());
        rvClassesHistory.setHasFixedSize(true);
        rvClassesHistory.setAdapter(todayClassRecyclerAdapter);
    }

    private void apiGetSchedule() {

        String token = SharedPreferenceManager.getInstance(getContext())
                .getStringPreference(
                        Constants.TOKEN,
                        Constants.DEFAULT_VALUE);

        ApiService apiService = ApiClient.getRetrofitClient();

        Call<JsonObject> call = apiService.getScheduleHistory(token);
        call.enqueue(new Callback<JsonObject>() {

            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                Gson gson = new Gson();
                if (response.code() == HttpURLConnection.HTTP_OK) {
                    ResponseScheduleHistory responseScheduleHistory = gson.fromJson(response.body(), ResponseScheduleHistory.class);
                    classList.clear();
                    classList.addAll(responseScheduleHistory.getClassList());

                    todayClassRecyclerAdapter.notifyItemRangeInserted(todayClassRecyclerAdapter.getItemCount(), classList.size());
                    rvClassesHistory.scrollToPosition(todayClassRecyclerAdapter.getItemCount());
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

    private void apiGetStudentSchedule() {

        String token = SharedPreferenceManager.getInstance(getContext())
                .getStringPreference(
                        Constants.TOKEN,
                        Constants.DEFAULT_VALUE);

        ApiService apiService = ApiClient.getRetrofitClient();

        Call<JsonObject> call = apiService.getStudentScheduleHistory(token);
        call.enqueue(new Callback<JsonObject>() {

            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                Gson gson = new Gson();
                if (response.code() == HttpURLConnection.HTTP_OK) {
                    ResponseScheduleHistory responseScheduleHistory = gson.fromJson(response.body(), ResponseScheduleHistory.class);
                    classList.clear();
                    classList.addAll(responseScheduleHistory.getClassList());

                    todayClassRecyclerAdapter.notifyItemRangeInserted(todayClassRecyclerAdapter.getItemCount(), classList.size());
                    rvClassesHistory.scrollToPosition(todayClassRecyclerAdapter.getItemCount());
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