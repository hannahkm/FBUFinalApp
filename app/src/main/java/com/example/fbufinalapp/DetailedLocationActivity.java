package com.example.fbufinalapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fbufinalapp.databinding.ActivityDetailedLocationBinding;
import com.google.android.gms.common.api.ApiException;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DetailedLocationActivity extends AppCompatActivity {
    public static String TAG = "DetailedLocationActivity";
    TextView tvOpenHours;
    TextView tvPriceLevel;
    RatingBar ratingBar;
    PlacesClient placesClient;
    FloatingActionButton fabAddToFav;
    ParseUser currentUser;
    String placeId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // using view binding
        ActivityDetailedLocationBinding binding = ActivityDetailedLocationBinding.inflate(getLayoutInflater());

        // layout of activity is stored in a special property called root
        View view = binding.getRoot();
        setContentView(view);

        tvOpenHours = findViewById(R.id.tvOpenHours);
        tvPriceLevel = findViewById(R.id.tvPriceLevel);
        ratingBar = findViewById(R.id.ratingBar);
        fabAddToFav = findViewById(R.id.fabAddToFav);

        currentUser = ParseUser.getCurrentUser();

        placeId = getIntent().getStringExtra("placeID");
        String name = getIntent().getStringExtra("name");

        if (currentUser.getList("favorites").contains(placeId)){
            fabAddToFav.setImageResource(R.drawable.ic_favorite_filled);
        }

        getSupportActionBar().setTitle(name);

        placesClient = Places.createClient(this);

        // Specify the fields to return.
        final List<Place.Field> placeFields = Arrays.asList(Place.Field.ID,
                Place.Field.NAME, Place.Field.ADDRESS, Place.Field.RATING, Place.Field.PRICE_LEVEL,
                Place.Field.WEBSITE_URI, Place.Field.PHONE_NUMBER);

        // Construct a request object, passing the place ID and fields array.
        final FetchPlaceRequest request = FetchPlaceRequest.newInstance(placeId, placeFields);

        placesClient.fetchPlace(request).addOnSuccessListener((response) -> {
            Place place = response.getPlace();
            binding.tvName.setText(place.getName());
            binding.tvAddress.setText(place.getAddress());

            try {
                List<String> openHours = place.getOpeningHours().getWeekdayText();
                tvOpenHours.setText("Open hours: " + String.join(" ", openHours));
            } catch (NullPointerException e) {
                tvOpenHours.setText("Open hours: unavailable");
            }

            Integer priceLevel = place.getPriceLevel();
            if (priceLevel != null) {
                tvPriceLevel.setText("Average price level: " + priceLevel);
            } else {
                tvPriceLevel.setText("Average price level: unavailable");
            }

            try {
                double rating = place.getRating();
                ratingBar.setIsIndicator(true);
                ratingBar.setMax(5);
                ratingBar.setRating((float) rating);
            } catch(NullPointerException e) {
                ratingBar.setVisibility(View.GONE);
            }

            binding.btWebsite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        Uri url = place.getWebsiteUri();
                        Intent launch = new Intent(Intent.ACTION_VIEW, url);
                        startActivity(launch);
                    } catch (NullPointerException e){
                        Toast.makeText(DetailedLocationActivity.this, "This location has no website", Toast.LENGTH_SHORT).show();
                    }

                }
            });

            binding.btPhoneNumber.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        String phone = "tel:" + place.getPhoneNumber();
                        Intent launch = new Intent(Intent.ACTION_DIAL);
                        launch.setData(Uri.parse(phone));
                        startActivity(launch);
                    } catch (NullPointerException e) {
                        Toast.makeText(DetailedLocationActivity.this, "This location has no phone number", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            binding.btDirections.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        String address = place.getAddress().replace(" ", "+");
                        Uri gmmIntentUri = Uri.parse("google.navigation:q="+address);
                        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                        mapIntent.setPackage("com.google.android.apps.maps");
                        startActivity(mapIntent);
                    } catch (NullPointerException e) {

                    }
                }
            });

        }).addOnFailureListener((exception) -> {
            if (exception instanceof ApiException) {
                Log.e(TAG, "Place not found: " + exception.getMessage());
            }
        });

        fabAddToFav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<String> currentFavs = currentUser.getList("favorites");
                if (currentFavs == null){
                    currentFavs = new ArrayList<String>();
                }

                if (currentFavs.contains(placeId)){
                    // we're disliking; remove the id from favorites
                    currentFavs.remove(placeId);
                    fabAddToFav.setImageResource(R.drawable.ic_favorite);
                } else {
                    // liking the location; add id
                    currentFavs.add(placeId);
                    fabAddToFav.setImageResource(R.drawable.ic_favorite_filled);
                }
                currentUser.put("favorites", currentFavs);


                currentUser.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e != null){
                            Log.e("Saving user", String.valueOf(e));
                            Toast.makeText(DetailedLocationActivity.this, "An error occurred. Please try again later!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

    }
}