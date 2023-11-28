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
import android.widget.LinearLayout;
import android.widget.Toast;

import com.dilm.moozi.R;
import com.dilm.moozi.adapters.ClassRecyclerAdapter;
import com.dilm.moozi.dialogs.CommonLoadingProgressDialog;
import com.dilm.moozi.models.Class;
import com.dilm.moozi.models.User;
import com.dilm.moozi.models.login.LoginResponse;
import com.dilm.moozi.models.schedule.BaseResponseSchedules;
import com.dilm.moozi.network.ApiClient;
import com.dilm.moozi.network.ApiService;
import com.dilm.moozi.utils.Constants;
import com.dilm.moozi.utils.SharedPreferenceManager;
import com.dilm.moozi.utils.Utility;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {

    private RecyclerView rvTodayClasses;
    private RecyclerView rvUpcomingClasses;
    private FloatingActionButton fabAddSchedule;
    private LinearLayout wrapperEmptyTodaySchedules, wrapperEmptyUpcomingSchedules;
    private List<Class> todayClassList;
    private List<Class> upcomingClassList;
    ClassRecyclerAdapter todayClassRecyclerAdapter, upComingClassRecyclerAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        initViews(view);
        initObjects();
        initListeners();
        return view;
    }

    private void initViews(View view) {
        rvTodayClasses = view.findViewById(R.id.rvTodayClasses);
        rvUpcomingClasses = view.findViewById(R.id.rvUpcomingClasses);
        fabAddSchedule = view.findViewById(R.id.add_schedule);
        wrapperEmptyTodaySchedules = view.findViewById(R.id.wrapper_empty_today_schedules);
        wrapperEmptyUpcomingSchedules = view.findViewById(R.id.wrapper_empty_upcoming_schedules);
    }

    private void initObjects() {
        Gson gson = new Gson();
        List<String> roles = gson.fromJson(SharedPreferenceManager.getInstance(getContext()).getStringPreference(Constants.USER, Constants.DEFAULT_VALUE), LoginResponse.class).getRoles();
        if (roles.contains(Constants.ROLE_TEACHER)) {
            fabAddSchedule.setVisibility(View.VISIBLE);
        } else {
            fabAddSchedule.setVisibility(View.GONE);
        }
        User user;
        String userString = SharedPreferenceManager.getInstance(getContext()).getStringPreference(Constants.USER_INFO, Constants.DEFAULT_VALUE);
        if (!userString.isEmpty()) {
            user = gson.fromJson(userString, User.class);
        } else {
            user = new User("N/A",Constants.EMP_VALUE,Constants.EMP_VALUE,Constants.EMP_VALUE);
        }
        todayClassList = new ArrayList<>();
        upcomingClassList = new ArrayList<>();
        initAdapters(user);
        apiGetSchedule();
    }

    private void initAdapters(User user) {
        todayClassRecyclerAdapter = new ClassRecyclerAdapter(todayClassList, user, getActivity());
        RecyclerView.LayoutManager mLayoutManager1 = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        rvTodayClasses.setLayoutManager(mLayoutManager1);
        rvTodayClasses.setItemAnimator(new DefaultItemAnimator());
        rvTodayClasses.setHasFixedSize(true);
        rvTodayClasses.setAdapter(todayClassRecyclerAdapter);

        upComingClassRecyclerAdapter = new ClassRecyclerAdapter(upcomingClassList, user, getActivity());
        RecyclerView.LayoutManager mLayoutManager2 = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        rvUpcomingClasses.setLayoutManager(mLayoutManager2);
        rvUpcomingClasses.setItemAnimator(new DefaultItemAnimator());
        rvUpcomingClasses.setHasFixedSize(true);
        rvUpcomingClasses.setAdapter(upComingClassRecyclerAdapter);
    }

    private void initListeners() {
        fabAddSchedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity()
                        .getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.frameLayoutHome, new ScheduleFragment())
                        .addToBackStack(Constants.BACK_STACK_TAG)
                        .commit();
            }
        });
    }

    private void apiGetSchedule() {

        ProgressDialog progressDialog = CommonLoadingProgressDialog.getInstance().createProgress(getContext(), Constants.EMP_VALUE);

        Gson gson = new Gson();
        LoginResponse loginResponse = gson.fromJson(SharedPreferenceManager.getInstance(getContext()).getStringPreference(Constants.USER, Constants.DEFAULT_VALUE), LoginResponse.class);
        boolean isTeacher = loginResponse.getRoles().contains(Constants.ROLE_TEACHER);

        String token = SharedPreferenceManager.getInstance(getContext())
                .getStringPreference(
                        Constants.TOKEN,
                        Constants.DEFAULT_VALUE);

        ApiService apiService = ApiClient.getRetrofitClient();

        Call<JsonObject> call;
        if (isTeacher) {
            call = apiService.getSchedules(token);
        } else {
            call = apiService.getStudentSchedules(token);
        }
        call.enqueue(new Callback<JsonObject>() {

            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.code() == HttpURLConnection.HTTP_OK) {
                    BaseResponseSchedules responseSchedules = gson.fromJson(response.body(), BaseResponseSchedules.class);
                    todayClassList.clear();
                    upcomingClassList.clear();
                    todayClassList.addAll(responseSchedules.getResponseSchedule().getTodaySchedules());
                    upcomingClassList.addAll(responseSchedules.getResponseSchedule().getUpComingSchedules());

                    if (!todayClassList.isEmpty()) {
                        wrapperEmptyTodaySchedules.setVisibility(View.GONE);
                        rvTodayClasses.setVisibility(View.VISIBLE);
                    }

                    if (!upcomingClassList.isEmpty()) {
                        wrapperEmptyUpcomingSchedules.setVisibility(View.GONE);
                        rvUpcomingClasses.setVisibility(View.VISIBLE);
                    }

                    todayClassRecyclerAdapter.notifyItemRangeInserted(todayClassRecyclerAdapter.getItemCount(), todayClassList.size());
                    upComingClassRecyclerAdapter.notifyItemRangeInserted(upComingClassRecyclerAdapter.getItemCount(), upcomingClassList.size());
                    rvTodayClasses.scrollToPosition(todayClassRecyclerAdapter.getItemCount());
                    rvUpcomingClasses.scrollToPosition(todayClassRecyclerAdapter.getItemCount());
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