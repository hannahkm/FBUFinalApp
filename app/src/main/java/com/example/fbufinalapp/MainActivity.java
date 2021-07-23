package com.example.fbufinalapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.fbufinalapp.databinding.ActivityMainBinding;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.PlacesClient;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    // define your fragments here
    Fragment dashboardFragment, searchFragment, favoritesFragment, profileFragment;
    static Place place;

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

    public static Place getPlace(String placeId, Context context) {
        PlacesClient placesClient = Places.createClient(context);

        // Specify the fields to return.
        List<Place.Field> placeFields = Arrays.asList(Place.Field.ID,
                Place.Field.NAME, Place.Field.ADDRESS, Place.Field.RATING, Place.Field.PRICE_LEVEL,
                Place.Field.WEBSITE_URI, Place.Field.PHONE_NUMBER);

        // Construct a request object, passing the place ID and fields array.
        FetchPlaceRequest request = FetchPlaceRequest.newInstance(placeId, placeFields);

        placesClient.fetchPlace(request).addOnSuccessListener((response) -> {
            place = response.getPlace();
            Log.i("CommonFunctions", "inside: " + String.valueOf(place));
        }).addOnFailureListener((exception) -> {
            Log.e("CommonFunctions", String.valueOf(exception));
            Toast.makeText(context, "Error retrieving location", Toast.LENGTH_SHORT).show();
        });

        Log.i("CommonFunctions", "outside: " + String.valueOf(place));
        return place;

    }
}