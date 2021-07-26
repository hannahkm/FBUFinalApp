package com.example.fbufinalapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.fbufinalapp.databinding.ActivityMainBinding;
import com.google.android.libraries.places.api.Places;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.parse.ParseUser;

import java.util.ArrayList;

/**
 * Main Activity of the app; builds the bottom navigation to allow the user to move to other fragments
 */
public class MainActivity extends AppCompatActivity {
    // define your fragments here
    Fragment dashboardFragment, searchFragment, favoritesFragment, profileFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // using view binding
        ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        // creates default guest user if current user isn't logged in
        if (ParseUser.getCurrentUser() == null){
            ParseUser user = new ParseUser();
            user.setUsername("Guest");
            user.setPassword("default");
            user.put("favorites", new ArrayList<>());
            user.put("itineraries", new ArrayList<>());

            user.signUpInBackground(e -> {
                if (e == null) {
                    Log.i("MainActivity", "signing up!");
                    Toast.makeText(MainActivity.this, "Welcome!", Toast.LENGTH_SHORT).show();
                } else {
                    Log.i("SignUp Failed", String.valueOf(e));
                }
            });
        }

        // Initialize the SDK
        Places.initialize(getApplicationContext(), getResources().getString(R.string.apiKey));

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