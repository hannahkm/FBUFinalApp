package com.example.fbufinalapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.transition.Explode;
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
import com.example.fbufinalapp.models.AccuweatherForecast;
import com.example.fbufinalapp.models.AccuweatherLocation;
import com.example.fbufinalapp.models.DailyForecast;
import com.example.fbufinalapp.models.Day;
import com.example.fbufinalapp.models.Itinerary;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.maps.model.LatLng;
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

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Screen to show the user details about a given location. Data obtained from the Google Places SDK.
 */
public class DetailedLocationActivity extends AppCompatActivity{
    public static String TAG = "DetailedLocationActivity";
    public static ActivityDetailedLocationBinding binding;
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

        currentUser = CommonValues.CURRENT_USER;
        getWindow().setEnterTransition(new Explode());

        // builds a popup window; to be used when the user adds the current location to an itinerary
        inflater = (LayoutInflater) DetailedLocationActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        layout = inflater.inflate(R.layout.itinerary_popup, null);
        pw = new PopupWindow(DetailedLocationActivity.this);
        pw.setContentView(layout);
        pw.setWidth((int) (screenWidth*0.9));
        pw.setOutsideTouchable(true);

        placeId = getIntent().getStringExtra("placeID");

        // changes drawable of the favorites button if the location is already in the user's favorites
        if (currentUser.getList(CommonValues.KEY_FAVORITES).contains(placeId)){
            fabAddToFav.setImageResource(R.drawable.ic_favorite_filled);
        }

        placesClient = Places.createClient(this);

        queryPageAsync queryPage = new queryPageAsync();
        binding.rotateloading.start();
        queryPage.start();

        try {
            queryPage.join();
        }
        catch (Exception e) {
            System.out.println(e);
        }

        binding.rotateloading.stop();


        /**
         * the user clicked on the favorites button, so we add/remove it from their favorites
         */
        fabAddToFav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<String> currentFavs = currentUser.getList(CommonValues.KEY_FAVORITES);

                if (currentFavs.contains(placeId)){
                    // we're disliking; remove the id from favorites
                    currentFavs.remove(placeId);
                    fabAddToFav.setImageResource(R.drawable.ic_favorite);
                } else {
                    // liking the location; add id
                    currentFavs.add(placeId);
                    fabAddToFav.setImageResource(R.drawable.ic_favorite_filled);
                }
                currentUser.put(CommonValues.KEY_FAVORITES, currentFavs);


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

        /**
         * Allows the user to add the current location to an existing itinerary
         */
        binding.fabAddToItin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // show popup and let user select an itinerary from the list.
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

    /**
     * Queries the details of the current location in the background. In the meantime, the
     * loading progress symbol is shown.
     */
    class queryPageAsync extends Thread{
        @Override
        public void run() {
            populatePage();
            getAllItins();
        }
    }

    /**
     * Pulls data about the given destination (by ID) from the Google Places SDK and adds all
     * relevant information to the page. Data that returns as None display "unavailable" or are
     * removed from the page
     */
    public void populatePage(){
        final List<Place.Field> placeFields = Arrays.asList(Place.Field.ID,
                Place.Field.NAME, Place.Field.ADDRESS, Place.Field.RATING, Place.Field.PRICE_LEVEL,
                Place.Field.WEBSITE_URI, Place.Field.PHONE_NUMBER, Place.Field.LAT_LNG);

        final FetchPlaceRequest request = FetchPlaceRequest.newInstance(placeId, placeFields);

        placesClient.fetchPlace(request).addOnSuccessListener((response) -> {
            place = response.getPlace();
            LatLng coords = place.getLatLng();

            getForecast(coords.latitude, coords.longitude);

            binding.tvName.setText(place.getName());
            getSupportActionBar().setTitle(place.getName());
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
                        Toast.makeText(DetailedLocationActivity.this, "Couldn't get directions", Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }).addOnFailureListener((exception) -> {
            if (exception instanceof ApiException) {
                Log.e(TAG, "Place not found: " + exception.getMessage());
            }
        });
    }

    /**
     * Function to retrieve the forecast for a given location from Accuweather
     * Calls two APIs: the Location API and the forecast API. The location API provides the locationKey for
     * the given coordinates, and the forecast API uses this key to return the current forecast
     *
     * Weather forecast values are set in the activity, and a lottie animation is displayed to represent it
     */
    public void getForecast(double lat, double lon) {
        WeatherApplication.getWeatherData service = WeatherApplication.getRetrofitInstance().create(WeatherApplication.getWeatherData.class);
        String latlng = lat + "," + lon;
        Call<AccuweatherLocation> call = service.getLocation(getResources().getString(R.string.weatherKey), latlng);
        call.enqueue(new Callback<AccuweatherLocation>() {
            @Override
            public void onResponse(Call<AccuweatherLocation> call, Response<AccuweatherLocation> response) {
                AccuweatherLocation location = response.body();

                if (location == null){
                    binding.tvDetailedForecast.setText("Sorry, this feature is unavailable in this location.");
                    binding.lavWeather.setAnimation("lottie-error.json");
                } else {
                    String locationKey = location.Key;

                    Call<AccuweatherForecast> callForecast = service.getForecast(locationKey, getResources().getString(R.string.weatherKey));
                    callForecast.enqueue(new Callback<AccuweatherForecast>() {
                        @Override
                        public void onResponse(Call<AccuweatherForecast> call, Response<AccuweatherForecast> response) {
                            if (response == null){
                                Log.e("WeatherApplication", "no response");
                                binding.tvDetailedForecast.setText("Sorry, this feature is unavailable in this location.");
                                binding.lavWeather.setAnimation("lottie-error.json");
                            } else {
                                AccuweatherForecast forecastWrapper = response.body();
                                List<DailyForecast> forecast = forecastWrapper.getDailyForecasts();
                                Day todayForecast = forecast.get(0).getDay();

                                String detailedForecast = todayForecast.getIconPhrase();

                                if (detailedForecast.contains("t-storm")) {
                                    binding.lavWeather.setAnimation("lottie-thunderstorm.json");
                                    detailedForecast.replace("t-storm", "thunderstorm");
                                } else if (detailedForecast.contains("showers")) {
                                    binding.lavWeather.setAnimation("lottie-rainy.json");
                                } else if (detailedForecast.contains("snow")) {
                                    binding.lavWeather.setAnimation("lottie-snow.json");
                                } else if (detailedForecast.contains("cloudy") || detailedForecast.contains("clouds")) {
                                    binding.lavWeather.setAnimation("lottie-cloudy.json");
                                } else {
                                    binding.lavWeather.setAnimation("lottie-sunny.json");
                                }

                                binding.tvDetailedForecast.setText(detailedForecast);

                            }
                        }

                        @Override
                        public void onFailure(Call<AccuweatherForecast> call, Throwable t) {
                            Log.e("WeatherApplication", String.valueOf(t));
                            binding.tvDetailedForecast.setText("Sorry, this feature is unavailable.");
                            binding.lavWeather.setAnimation("lottie-error.json");
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<AccuweatherLocation> call, Throwable t) {
                Log.e("DetailedLocation", String.valueOf(t));
                binding.tvDetailedForecast.setText("Sorry, this feature is unavailable.");
                binding.lavWeather.setAnimation("lottie-error.json");
            }
        });

        binding.lavWeather.setScale(0.3F);
    }

    /**
     * Obtains a list of all the itineraries the current user has made to display in the popup window
     */
    public void getAllItins(){

        ParseQuery<Itinerary> query = ParseQuery.getQuery("Itinerary");
        query.whereEqualTo(CommonValues.KEY_USER, currentUser);

        itinNames = new ArrayList<>();
        itinIds = new ArrayList<>();

        query.findInBackground(new FindCallback<Itinerary>() {
            @Override
            public void done(List<Itinerary> itineraries, ParseException e) {
                if (e != null){
                    Toast.makeText(DetailedLocationActivity.this, "Unable to get itineraries", Toast.LENGTH_SHORT).show();
                    Log.e("DetailedLocation", String.valueOf(e));
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