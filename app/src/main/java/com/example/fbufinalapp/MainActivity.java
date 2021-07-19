package com.example.fbufinalapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.fbufinalapp.databinding.ActivityMainBinding;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.net.PlacesClient;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {
    // define your fragments here
    Fragment dashboardFragment, searchFragment, favoritesFragment, profileFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // using view binding
        ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());

        // layout of activity is stored in a special property called root
        View view = binding.getRoot();
        setContentView(view);

        // Initialize the SDK
        Places.initialize(getApplicationContext(), getResources().getString(R.string.apiKey));

        // Create a new PlacesClient instance
        PlacesClient placesClient = Places.createClient(this);

        final FragmentManager fragmentManager = getSupportFragmentManager();

        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Fragment fragment;
                    switch (item.getItemId()) {
                        case R.id.dashboard:
                            if (dashboardFragment == null){
                                dashboardFragment = new DashboardFragment();
                            }
                            fragment = dashboardFragment;
                            break;
                        case R.id.search:
                            if (searchFragment == null){
                                searchFragment = new SearchFragment();
                            }
                            fragment = searchFragment;
                            break;
                        case R.id.favorites:
                            if (favoritesFragment == null){
                                favoritesFragment = new FavoritesFragment();
                            }
                            fragment = favoritesFragment;
                            break;
                        case R.id.profile:
                        default:
                            if (profileFragment == null){
                                profileFragment = new ProfileFragment();
                            }
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

    public void setActionBarTitle(String title) {
        getSupportActionBar().setTitle(title);
    }
}