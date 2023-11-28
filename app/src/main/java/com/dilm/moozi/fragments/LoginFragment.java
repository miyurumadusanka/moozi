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
import android.widget.Toast;

import com.dilm.moozi.R;
import com.dilm.moozi.activities.HomeActivity;
import com.dilm.moozi.dialogs.CommonLoadingProgressDialog;
import com.dilm.moozi.models.login.LoginRequest;
import com.dilm.moozi.models.login.LoginResponse;
import com.dilm.moozi.network.ApiClient;
import com.dilm.moozi.network.ApiService;
import com.dilm.moozi.utils.Constants;
import com.dilm.moozi.utils.SharedPreferenceManager;
import com.dilm.moozi.utils.Utility;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.net.HttpURLConnection;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginFragment extends Fragment {

    private MaterialButton btnLogin;
    private AppCompatTextView tvNewAccount;
    private TextInputLayout txtInputLayoutUsername;
    private TextInputLayout txtInputLayoutPassword;
    private AppCompatEditText etUsername;
    private AppCompatEditText etPassword;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_login, container, false);
        initViews(rootView);
        initListeners();
        return rootView;
    }

    private void initViews(View view) {
        btnLogin = view.findViewById(R.id.btnLogin);
        tvNewAccount = view.findViewById(R.id.tvNewAccount);
        txtInputLayoutUsername = view.findViewById(R.id.txtInputLayoutUsername);
        txtInputLayoutPassword = view.findViewById(R.id.txtInputLayoutPassword);
        etUsername = view.findViewById(R.id.etUsername);
        etPassword = view.findViewById(R.id.etPassword);
        etUsername.setFocusable(false);
        etPassword.setFocusable(false);
        etUsername.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etUsername.setFocusableInTouchMode(true);
                etUsername.requestFocus();
                Utility.getInstance(getContext()).openKeyBoard(etUsername);
            }
        });
        etPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etPassword.setFocusableInTouchMode(true);
                etPassword.requestFocus();
                Utility.getInstance(getContext()).openKeyBoard(etPassword);
            }
        });
    }

    private void initListeners() {
        tvNewAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity()
                        .getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.frameLayoutLogin, new RegistrationFragment())
                        .commit();
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (etUsername.getText().toString().isEmpty() || etPassword.getText().toString().isEmpty()) {
                    Toast.makeText(getContext(), R.string.msg_empty_fields, Toast.LENGTH_SHORT).show();
                } else {
                    apiLoginUser(new LoginRequest(etUsername.getText().toString(), etPassword.getText().toString()));
                }
            }
        });
    }

    private void apiLoginUser(LoginRequest loginRequest) {

        ProgressDialog progressDialog = CommonLoadingProgressDialog.getInstance().
                createProgress(getContext(), Constants.EMP_VALUE);

        ApiService apiService = ApiClient.getRetrofitClient();

        Call<JsonObject> call = apiService.postLogin(loginRequest);
        call.enqueue(new Callback<JsonObject>() {

            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.code() == HttpURLConnection.HTTP_OK) {
                    Gson gson = new Gson();
                    LoginResponse loginResponse = gson.fromJson(response.body(), LoginResponse.class);
                    SharedPreferenceManager.getInstance(getContext())
                            .setStringPreference(
                                    Constants.TOKEN,
                                    "Bearer "+loginResponse.getToken());
                    SharedPreferenceManager.getInstance(getContext())
                            .setStringPreference(
                                    Constants.USER,
                                    gson.toJson(loginResponse));
                    Intent intent = new Intent(getContext(), HomeActivity.class);
                    startActivity(intent);
                    getActivity().finish();
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