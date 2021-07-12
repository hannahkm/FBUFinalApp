package com.example.fbufinalapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import com.parse.Parse;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.parse.ParseUser;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final FragmentManager fragmentManager = getSupportFragmentManager();

        // define your fragments here
        final Fragment dashboardFragment = new DashboardFragment();
        final Fragment searchFragment = new SearchFragment();
        final Fragment favoritesFragment = new FavoritesFragment();
        final Fragment profileFragment = new ProfileFragment();

        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Fragment fragment;
                    switch (item.getItemId()) {
                        case R.id.dashboard:
                            fragment = dashboardFragment;
                            break;
                        case R.id.search:
                            fragment = searchFragment;
                            break;
                        case R.id.favorites:
                            fragment = favoritesFragment;
                            break;
                        case R.id.profile:
                        default:
                            fragment = profileFragment;
                            break;
                    }
                    fragmentManager.beginTransaction().replace(R.id.fragment_container, fragment).commit();
                    return true;
                }
            });
        // Set default selection
        bottomNavigationView.setSelectedItemId(R.id.dashboard);
    }
}