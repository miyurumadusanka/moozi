package com.dilm.moozi.fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.widget.AppCompatEditText;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.dilm.moozi.R;
import com.dilm.moozi.activities.LoginActivity;
import com.dilm.moozi.dialogs.CommonDialog;
import com.dilm.moozi.dialogs.CommonLoadingProgressDialog;
import com.dilm.moozi.models.User;
import com.dilm.moozi.models.profile.Profile;
import com.dilm.moozi.models.profile.ProfileRequest;
import com.dilm.moozi.network.ApiClient;
import com.dilm.moozi.network.ApiService;
import com.dilm.moozi.utils.Constants;
import com.dilm.moozi.utils.SharedPreferenceManager;
import com.google.android.material.button.MaterialButton;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.net.HttpURLConnection;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileFragment extends Fragment {

    private ImageView imgViewLogout;
    private AppCompatEditText etName;
    private AppCompatEditText etAddress;
    private AppCompatEditText etPhoneNum;
    private AppCompatEditText etGrade;
    private MaterialButton btnSave;
    private ProgressDialog progressDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        initViews(view);
        initObjects();
        initListeners();
        return view;
    }

    private void initViews(View view) {
        imgViewLogout = view.findViewById(R.id.imgViewLogout);
        etName = view.findViewById(R.id.etName);
        etAddress = view.findViewById(R.id.etAddress);
        etPhoneNum = view.findViewById(R.id.etPhoneNum);
        etGrade = view.findViewById(R.id.etGrade);
        btnSave = view.findViewById(R.id.btnSave);
    }

    private void initObjects() {
//        String userString = SharedPreferenceManager.getInstance(getContext())
//                .getStringPreference(Constants.USER_INFO, Constants.DEFAULT_VALUE);
//        if (!userString.isEmpty()) {
//            Gson gson = new Gson();
//            User user = gson.fromJson(userString, User.class);
//            etName.setText(user.getFullName());
//            etAddress.setText(user.getAddress());
//            etPhoneNum.setText(user.getPhoneNum());
//            etGrade.setText(user.getGrade());
//        }
        progressDialog = CommonLoadingProgressDialog.getInstance()
                .createProgress(getContext(), Constants.EMP_VALUE);
        apiGetProfile();
    }

    private void initListeners() {
        imgViewLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommonDialog commonDialog = new CommonDialog(
                        getContext(),
                        Constants.TITLE_LOGOUT, Constants.MESSAGE_LOGOUT,
                        Constants.BTN_POSITIVE, Constants.BTN_NEGATIVE) {
                    @Override
                    public void onClick() {
                        SharedPreferenceManager.getInstance(getContext())
                                .setStringPreference(Constants.TOKEN, Constants.DEFAULT_VALUE);
                        Intent intent = new Intent(getContext(), LoginActivity.class);
                        startActivity(intent);
                        getActivity().finish();
                    }
                };
                commonDialog.open();
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProfileRequest profileRequest = new ProfileRequest();
                profileRequest.setName(etName.getText().toString());
                profileRequest.setAddress(etAddress.getText().toString());
                profileRequest.setPhoneNumber(etPhoneNum.getText().toString());
                profileRequest.setGrade(etGrade.getText().toString());

                progressDialog.show();
                apiCreateProfile(profileRequest);
            }
        });
    }

    private void apiCreateProfile(ProfileRequest profileRequest) {

        String token = SharedPreferenceManager.getInstance(getContext())
                .getStringPreference(
                        Constants.TOKEN,
                        Constants.DEFAULT_VALUE);

        ApiService apiService = ApiClient.getRetrofitClient();

        Call<JsonObject> call = apiService.createProfile(token, profileRequest);
        call.enqueue(new Callback<JsonObject>() {

            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.code() == HttpURLConnection.HTTP_CREATED) {
                    Gson gson = new Gson();
                    User user = new User(
                            Objects.requireNonNull(etName.getText()).toString(),
                            Objects.requireNonNull(etAddress.getText()).toString(),
                            Objects.requireNonNull(etPhoneNum.getText()).toString(),
                            Objects.requireNonNull(etGrade.getText()).toString());
                    SharedPreferenceManager.getInstance(getContext())
                            .setStringPreference(Constants.USER_INFO, gson.toJson(user));
                    Toast.makeText(getContext(), getString(R.string.msg_create_profile), Toast.LENGTH_SHORT).show();
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

    private void apiGetProfile() {

        String token = SharedPreferenceManager.getInstance(getContext())
                .getStringPreference(
                        Constants.TOKEN,
                        Constants.DEFAULT_VALUE);

        ApiService apiService = ApiClient.getRetrofitClient();

        Call<JsonObject> call = apiService.getProfile(token);
        call.enqueue(new Callback<JsonObject>() {

            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                Gson gson = new Gson();
                if (response.code() == HttpURLConnection.HTTP_OK) {
                    Profile profile = gson.fromJson(response.body(), Profile.class);
                    etName.setText(profile.getFullName());
                    etAddress.setText(profile.getAddress());
                    etPhoneNum.setText(profile.getPhoneNumber());
                    etGrade.setText(profile.getGrade());
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