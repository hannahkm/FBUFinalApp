package com.example.fbufinalapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.ApiException;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.PlacesClient;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class DetailedLocationActivity extends AppCompatActivity {
    public static String TAG = "DetailedLocationActivity";
    TextView tvName;
    TextView tvAddress;
    TextView tvOpenHours;
    TextView tvPriceLevel;
    RatingBar ratingBar;
    Button btWebsite;
    Button btPhoneNumber;
    PlacesClient placesClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_location);

        tvName = findViewById(R.id.tvName);
        tvAddress = findViewById(R.id.tvAddress);
        tvOpenHours = findViewById(R.id.tvOpenHours);
        tvPriceLevel = findViewById(R.id.tvPriceLevel);
        ratingBar = findViewById(R.id.ratingBar);
        btWebsite = findViewById(R.id.btWebsite);
        btPhoneNumber = findViewById(R.id.btPhoneNumber);

        String placeId = getIntent().getStringExtra("placeID");
        String name = getIntent().getStringExtra("name");

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
            tvName.setText(place.getName());
            tvAddress.setText(place.getAddress());

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

            btWebsite.setOnClickListener(new View.OnClickListener() {
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

            btPhoneNumber.setOnClickListener(new View.OnClickListener() {
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

        }).addOnFailureListener((exception) -> {
            if (exception instanceof ApiException) {
                Log.e(TAG, "Place not found: " + exception.getMessage());
            }
        });

    }
}