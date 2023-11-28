package com.dilm.moozi.fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.dilm.moozi.R;
import com.dilm.moozi.activities.HomeActivity;
import com.dilm.moozi.dialogs.CommonLoadingProgressDialog;
import com.dilm.moozi.models.login.LoginRequest;
import com.dilm.moozi.models.login.LoginResponse;
import com.dilm.moozi.models.registration.ErrorResponse;
import com.dilm.moozi.models.registration.SignupRequest;
import com.dilm.moozi.models.registration.SignupResponse;
import com.dilm.moozi.network.ApiClient;
import com.dilm.moozi.network.ApiService;
import com.dilm.moozi.utils.Constants;
import com.dilm.moozi.utils.SharedPreferenceManager;
import com.google.android.material.button.MaterialButton;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegistrationFragment extends Fragment {

    private View rootView;
    private AppCompatTextView tvLogin;
    private MaterialButton btnSignUp;
    private AppCompatEditText etEmail, etUsername, etPassword;
    private RadioGroup rdGroupRole;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_registration, container, false);
        initViews(rootView);
        initListeners();
        return rootView;
    }

    private void initViews(View view) {
        tvLogin = view.findViewById(R.id.tvLogin);
        btnSignUp = view.findViewById(R.id.btnSignUp);
        etUsername = view.findViewById(R.id.etUsername);
        etEmail = view.findViewById(R.id.etEmail);
        etPassword = view.findViewById(R.id.etPassword);
        rdGroupRole = view.findViewById(R.id.rdGroupRole);
    }

    private void initListeners() {
        tvLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity()
                        .getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.frameLayoutLogin, new LoginFragment())
                        .commit();
            }
        });

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (etUsername.getText().toString().isEmpty() || etEmail.getText().toString().isEmpty() || etPassword.getText().toString().isEmpty()) {
                    Toast.makeText(getContext(), R.string.msg_empty_fields, Toast.LENGTH_SHORT).show();
                } else {
                    List<String> roles = new ArrayList<>();
                    RadioButton selectedRadioButton = (RadioButton) rootView.findViewById(rdGroupRole.getCheckedRadioButtonId());
                    roles.add(selectedRadioButton.getText().toString().toLowerCase());
                    apiRegisterUser(new SignupRequest(etUsername.getText().toString(), etEmail.getText().toString(), etPassword.getText().toString(), roles));
                }
            }
        });
    }

    private void apiRegisterUser(SignupRequest signupRequest) {

        ProgressDialog progressDialog = CommonLoadingProgressDialog.getInstance()
                .createProgress(getContext(), Constants.EMP_VALUE);

        ApiService apiService = ApiClient.getRetrofitClient();

        Call<JsonObject> call = apiService.postRegistration(signupRequest);
        call.enqueue(new Callback<JsonObject>() {

            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                Gson gson = new Gson();
                if (response.code() == HttpURLConnection.HTTP_OK) {
                    SignupResponse signupResponse = gson.fromJson(response.body(), SignupResponse.class);
                    getActivity()
                            .getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.frameLayoutLogin, new LoginFragment())
                            .commit();
                } else if (response.code() == HttpURLConnection.HTTP_BAD_REQUEST) {
                    try {
                        ErrorResponse errorResponse = gson.fromJson(response.errorBody().string(), ErrorResponse.class);
                        Toast.makeText(getContext(), errorResponse.getMessageBody(), Toast.LENGTH_SHORT).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
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