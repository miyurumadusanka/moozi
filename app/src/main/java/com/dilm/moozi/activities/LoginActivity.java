package com.dilm.moozi.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.dilm.moozi.fragments.LoginFragment;
import com.dilm.moozi.R;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_login);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frameLayoutLogin, new LoginFragment())
                .commit();
    }
}