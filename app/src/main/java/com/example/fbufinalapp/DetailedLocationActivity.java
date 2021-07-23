package com.example.fbufinalapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fbufinalapp.databinding.ActivityDetailedLocationBinding;
import com.example.fbufinalapp.models.Itinerary;
import com.google.android.gms.common.api.ApiException;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DetailedLocationActivity extends AppCompatActivity {
    public static String TAG = "DetailedLocationActivity";
    ActivityDetailedLocationBinding binding;
    View layout;
    TextView tvOpenHours;
    TextView tvPriceLevel;
    RatingBar ratingBar;
    PlacesClient placesClient;
    Place place;
    FloatingActionButton fabAddToFav;
    ParseUser currentUser;
    LayoutInflater inflater;
    PopupWindow pw;
    String placeId;
    int screenWidth;
    List<String> itinNames;
    List<String> itinIds;
    String itinSelected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // using view binding
        binding = ActivityDetailedLocationBinding.inflate(getLayoutInflater());

        // layout of activity is stored in a special property called root
        View view = binding.getRoot();
        setContentView(view);

        DisplayMetrics displayMetrics = this.getResources().getDisplayMetrics();
        screenWidth = displayMetrics.widthPixels;

        tvOpenHours = findViewById(R.id.tvOpenHours);
        tvPriceLevel = findViewById(R.id.tvPriceLevel);
        ratingBar = findViewById(R.id.ratingBar);
        fabAddToFav = findViewById(R.id.fabAddToFav);

        currentUser = ParseUser.getCurrentUser();

        inflater = (LayoutInflater) DetailedLocationActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        layout = inflater.inflate(R.layout.itinerary_popup, null);
        pw = new PopupWindow(DetailedLocationActivity.this);
        pw.setContentView(layout);
        pw.setWidth((int) (screenWidth*0.9));
        pw.setOutsideTouchable(true);

        placeId = getIntent().getStringExtra("placeID");
        String name = getIntent().getStringExtra("name");

        if (currentUser.getList("favorites").contains(placeId)){
            fabAddToFav.setImageResource(R.drawable.ic_favorite_filled);
        }

        getSupportActionBar().setTitle(name);

        placesClient = Places.createClient(this);

        populatePage();
        getAllItins();

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

        binding.fabAddToItin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pw.showAtLocation(binding.main, Gravity.CENTER, 0, 0);
                AutoCompleteTextView etItinSelect = layout.findViewById(R.id.etItinSelect);

                ArrayAdapter<String> dateAdapter = new ArrayAdapter<>(DetailedLocationActivity.this, R.layout.item_spinner, itinNames);
                etItinSelect.setAdapter(dateAdapter);
                etItinSelect.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        itinSelected = itinIds.get(position);
                    }
                });

                Button finished = layout.findViewById(R.id.btContinue);
                finished.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (itinSelected == null){
                            Toast.makeText(v.getContext(), "Please select an itinerary", Toast.LENGTH_SHORT).show();
                        } else {
                            pw.dismiss();
                            Intent i = new Intent(DetailedLocationActivity.this, EditDestinationActivity.class);
                            i.putExtra("itinId", itinSelected);
                            i.putExtra("placeName", place.getName());
                            i.putExtra("placeId", place.getId());
                            startActivity(i);
                        }
                    }
                });

            }
        });

    }

    public void populatePage(){
        // Specify the fields to return.
        final List<Place.Field> placeFields = Arrays.asList(Place.Field.ID,
                Place.Field.NAME, Place.Field.ADDRESS, Place.Field.RATING, Place.Field.PRICE_LEVEL,
                Place.Field.WEBSITE_URI, Place.Field.PHONE_NUMBER);

        // Construct a request object, passing the place ID and fields array.
        final FetchPlaceRequest request = FetchPlaceRequest.newInstance(placeId, placeFields);

        placesClient.fetchPlace(request).addOnSuccessListener((response) -> {
            place = response.getPlace();

            Log.i("DetailedLocation", String.valueOf(place));

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



    }

    public void getAllItins(){
        ParseQuery<Itinerary> query = ParseQuery.getQuery("Itinerary");
        query.whereEqualTo("authors", currentUser);

        itinNames = new ArrayList<>();
        itinIds = new ArrayList<>();

        query.findInBackground(new FindCallback<Itinerary>() {
            @Override
            public void done(List<Itinerary> itineraries, ParseException e) {
                if (e != null){
                    Toast.makeText(DetailedLocationActivity.this, "Unable to get itineraries", Toast.LENGTH_SHORT).show();
                    Log.e("DashboardFragment", String.valueOf(e));
                } else {
                    // save received posts to list and notify adapter of new data
                    for (Itinerary itin : itineraries){
                        itinNames.add(itin.getTitle());
                        itinIds.add(itin.getObjectId());
                    }
                }
            }
        });
    }
}