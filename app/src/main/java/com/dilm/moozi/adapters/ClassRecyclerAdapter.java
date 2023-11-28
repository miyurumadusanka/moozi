package com.dilm.moozi.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.dilm.moozi.R;
import com.dilm.moozi.fragments.ScheduleFragment;
import com.dilm.moozi.models.Class;
import com.dilm.moozi.models.User;
import com.dilm.moozi.models.history.ResponseScheduleHistory;
import com.dilm.moozi.network.ApiClient;
import com.dilm.moozi.network.ApiService;
import com.dilm.moozi.utils.Constants;
import com.dilm.moozi.utils.MeetConference;
import com.dilm.moozi.utils.SharedPreferenceManager;
import com.google.android.material.button.MaterialButton;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.net.HttpURLConnection;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ClassRecyclerAdapter extends RecyclerView.Adapter<ClassRecyclerAdapter.ClassViewHolder> {

    private final List<Class> classList;
    private boolean isPrevClass;
    private Context context;
    private FragmentActivity activity;
    private User user;

    public ClassRecyclerAdapter(List<Class> classList, User user, FragmentActivity activity) {
        this.classList = classList;
        this.user = user;
        this.activity = activity;
    }

    public ClassRecyclerAdapter(List<Class> classList, boolean isPrevClass, FragmentActivity activity) {
        this.classList = classList;
        this.isPrevClass = isPrevClass;
        this.activity = activity;
    }

    @NonNull
    @Override
    public ClassRecyclerAdapter.ClassViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        this.context = parent.getContext();
        View rootView;
        if (isPrevClass)
            rootView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_prev_class_recycler, parent, false);
        else
            rootView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_class_recycler, parent, false);
        return new ClassRecyclerAdapter.ClassViewHolder(rootView);
    }

    @Override
    public void onBindViewHolder(@NonNull ClassRecyclerAdapter.ClassViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.tvSubject.setText(classList.get(position).getName());
        holder.tvDesc.setText(classList.get(position).getDescription());
        holder.tvDate.setText(classList.get(position).getDate());
        holder.tvTimeDuration.setText(
                classList.get(position).getStartTime()
                        .concat(" - ")
                        .concat(classList.get(position).getEndTime()));

        holder.btnJoinClz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MeetConference meetConference = new MeetConference(
                        context,
                        user.getFullName(),
                        classList.get(position).getMeetingId(),
                        classList.get(position).getName());
                meetConference.initProcess();
                apiCreateStudentAttendance(classList.get(position).getId());
            }
        });

        holder.linearLayoutScheduleWrapper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ScheduleFragment scheduleFragment = new ScheduleFragment();
                Bundle bundle = new Bundle();
                Gson gson = new Gson();
                bundle.putString(Constants.DATA, gson.toJson(classList.get(position)));
                scheduleFragment.setArguments(bundle);
                activity
                        .getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.frameLayoutHome, scheduleFragment)
                        .addToBackStack(Constants.BACK_STACK_TAG)
                        .commit();
            }
        });
    }

    @Override
    public int getItemCount() {
        return classList.size();
    }

    public class ClassViewHolder extends RecyclerView.ViewHolder {

        private final AppCompatTextView tvSubject;
        private final AppCompatTextView tvDesc;
        private final AppCompatTextView tvDate;
        private final AppCompatTextView tvTimeDuration;
        private final MaterialButton btnJoinClz;
        private final LinearLayoutCompat linearLayoutScheduleWrapper;

        public ClassViewHolder(@NonNull View itemView) {
            super(itemView);
            tvSubject = itemView.findViewById(R.id.tvSubject);
            tvDesc = itemView.findViewById(R.id.tvDesc);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvTimeDuration = itemView.findViewById(R.id.tvTimeDuration);
            btnJoinClz = itemView.findViewById(R.id.btnJoinClz);
            linearLayoutScheduleWrapper = itemView.findViewById(R.id.linearLayoutScheduleWrapper);

            if (isPrevClass)
                btnJoinClz.setVisibility(View.GONE);
            else
                btnJoinClz.setVisibility(View.VISIBLE);
        }
    }

    private void apiCreateStudentAttendance(String scheduleId) {

        String token = SharedPreferenceManager.getInstance(context)
                .getStringPreference(
                        Constants.TOKEN,
                        Constants.DEFAULT_VALUE);

        ApiService apiService = ApiClient.getRetrofitClient();

        Call<JsonObject> call = apiService.createStudentScheduleAttendance(token, scheduleId);
        call.enqueue(new Callback<JsonObject>() {

            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                Gson gson = new Gson();
                if (response.code() == HttpURLConnection.HTTP_OK) {
                } else if (response.code() == HttpURLConnection.HTTP_UNAUTHORIZED) {
                    Toast.makeText(context, R.string.invalid_credential, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, R.string.network_error, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(context, R.string.connection_error, Toast.LENGTH_LONG).show();
            }
        });
    }
}
