package com.example.fbufinalapp;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.PlacesClient;

import java.util.Arrays;
import java.util.List;

public class CommonFunctions {
    static Place place;

    public static Place getPlace(String placeId, Context context) {
        PlacesClient placesClient = Places.createClient(context);

        // Specify the fields to return.
        List<Place.Field> placeFields = Arrays.asList(Place.Field.ID,
                Place.Field.NAME, Place.Field.ADDRESS, Place.Field.RATING, Place.Field.PRICE_LEVEL,
                Place.Field.WEBSITE_URI, Place.Field.PHONE_NUMBER);

        // Construct a request object, passing the place ID and fields array.
        FetchPlaceRequest request = FetchPlaceRequest.newInstance(placeId, placeFields);

        placesClient.fetchPlace(request).addOnSuccessListener((response) -> {
            setPlace(response.getPlace());
            Log.i("CommonFunctions", "inside: " + String.valueOf(place));
        }).addOnFailureListener((exception) -> {
            Log.e("CommonFunctions", String.valueOf(exception));
            Toast.makeText(context, "Error retrieving location", Toast.LENGTH_SHORT).show();
        });

        Log.i("CommonFunctions", "outside: " + String.valueOf(place));
        return place;

    }

    public static void setPlace(Place placeFound){
        place = placeFound;
        Log.i("CommonFunctions", "setting: " + String.valueOf(place));
    }

}
