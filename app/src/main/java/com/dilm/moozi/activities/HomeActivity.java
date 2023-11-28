package com.dilm.moozi.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;
import android.view.MenuItem;

import com.dilm.moozi.R;
import com.dilm.moozi.fragments.ProfileFragment;
import com.dilm.moozi.fragments.HomeFragment;
import com.dilm.moozi.fragments.HistoryFragment;
import com.dilm.moozi.utils.Constants;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                // Handle item selection here
                switch (item.getItemId()) {
                    case R.id.home:
                        loadFragment(new HomeFragment());
                        return true;

                    case R.id.history:
                        loadFragment(new HistoryFragment());
                        return true;

                    case R.id.profile:
                        loadFragment(new ProfileFragment());
                        return true;
                }
                return false;
            }
        });

        loadFragment(new HomeFragment());
    }

    void loadFragment(Fragment fragment) {
        FragmentManager supportFragmentManager = getSupportFragmentManager();
        supportFragmentManager.popBackStack(Constants.BACK_STACK_TAG, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        supportFragmentManager
                .beginTransaction()
                .replace(R.id.frameLayoutHome, fragment)
                .commit();
    }
}