package com.example.fbufinalapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.transition.Explode;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fbufinalapp.databinding.ActivityEditItineraryBinding;
import com.example.fbufinalapp.models.Destination;
import com.example.fbufinalapp.models.Itinerary;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.parse.DeleteCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * User can create an itinerary or edit an existing one. If they are editing an existing itinerary,
 * the page is populated with its data.
 */
public class EditItineraryActivity extends AppCompatActivity {
    TextView tvTripName;
    TextView tvLocation;
    TextView etStartDate;
    TextView etEndDate;
    TextView etNotes;
    Place place;
    String itinId;
    Itinerary itin;
    PlacesClient placesClient;
    ParseQuery<Itinerary> query;
    boolean editing = false;

    private static int AUTOCOMPLETE_REQUEST_CODE = 1;
    private static String TAG = "EditItineraryActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // using view binding
        ActivityEditItineraryBinding binding = ActivityEditItineraryBinding.inflate(getLayoutInflater());

        // layout of activity is stored in a special property called root
        View view = binding.getRoot();
        setContentView(view);

        getWindow().setEnterTransition(new Explode());

        tvTripName = findViewById(R.id.tvTripName);
        tvLocation = findViewById(R.id.tvLocation);
        etStartDate = findViewById(R.id.etStartDate);
        etEndDate = findViewById(R.id.etEndDate);
        etNotes = findViewById(R.id.etNotes);

        tvLocation.setOnFocusChangeListener(focusListener);

        itinId = getIntent().getStringExtra("itinId");

        placesClient = Places.createClient(this);
        if (getIntent().hasExtra("editing")){
            query = ParseQuery.getQuery("Itinerary");
            editing = true;
            query.getInBackground(itinId, (object, e) -> {
                if (e == null){
                    itin = object;
                    tvTripName.setText(object.getTitle());

                    final List<Place.Field> placeFields = Arrays.asList(Place.Field.ID, Place.Field.NAME);
                    final FetchPlaceRequest request = FetchPlaceRequest.newInstance(object.getPlaceID(), placeFields);

                    placesClient.fetchPlace(request).addOnSuccessListener((response) -> {
                        Place place = response.getPlace();
                        tvLocation.setText(place.getName());
                    }).addOnFailureListener((exception) -> {
                        if (exception instanceof ApiException) {
                            final ApiException apiException = (ApiException) exception;
                            Log.e(TAG, "Place not found: " + exception.getMessage());
                        }
                    });

                    String pattern = "MM/dd/yyy";
                    SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);

                    etStartDate.setText(dateFormat.format(object.getStartDate()));
                    etEndDate.setText(dateFormat.format(object.getEndDate()));
                } else {
                    Toast.makeText(EditItineraryActivity.this, "Couldn't retrive itinerary", Toast.LENGTH_SHORT).show();
                }
            });
        }

        binding.btFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!editing) {
                    itin = new Itinerary();
                }
                parseItinerary();
                finishAfterTransition();
            }
        });
    }

    /**
     * Process and save the itinerary. Info that's left blank are given default values or warn the user.
     */
    private void parseItinerary(){
        String title = String.valueOf(tvTripName.getText());
        String notes = String.valueOf(etNotes.getText());

        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
        Date start = null;
        Date end = null;
        String startInput = String.valueOf(etStartDate.getText());
        String endInput = String.valueOf(etEndDate.getText());

        if (!startInput.equals("")){
            try {
                start = sdf.parse(String.valueOf(etStartDate.getText()));
                itin.setStartDate(start);
            } catch (java.text.ParseException e) {
                Log.e("EditItinerary", String.valueOf(e));
            }
        }

        if (!endInput.equals("")){
            try {
                end = sdf.parse(String.valueOf(etEndDate.getText()));
                itin.setEndDate(end);
            } catch (java.text.ParseException e) {
                Log.e("EditItinerary", String.valueOf(e));
            }
        }

        if (title == null || title.equals("")){
            if (place != null){
                title = "Trip to " + place.getName();
            } else {
                title = "New Trip";
            }
        }
        itin.setTitle(title);

        ParseUser currentUser = ParseUser.getCurrentUser();
        itin.setAuthor(currentUser);

        itin.setDescription(notes);

        if (place != null){
            itin.setPlaceID(place.getId());
        } else if (editing){
            itin.setPlaceID(itin.getPlaceID());
        }

        List<String> destinations;

        if (editing){
            destinations = removeDates(start, end);
        } else {
            destinations = new ArrayList<>();
        }

        Calendar cStart = Calendar.getInstance();
        cStart.setTime(start);
        Calendar cEnd = Calendar.getInstance();
        cEnd.setTime(end);
        cEnd.add(Calendar.DATE, 1);

        String pattern = "MMMM dd, yyyy";
        SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);

        for (Calendar date = cStart; date.before(cEnd); date.add(Calendar.DATE, 1)){
            Destination newDest = new Destination();
            Date dateVar = date.getTime();

            newDest.setDate(dateVar);
            newDest.setName(dateFormat.format(dateVar));
            newDest.setIsDay(true);
            newDest.setItinerary(itin);

            newDest.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null){
                        destinations.add(newDest.getObjectId());
                    } else {
                        Log.e("EditItinerary", String.valueOf(e));
                        Toast.makeText(EditItineraryActivity.this, "Error saving dates", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

        itin.setDestinations(destinations);


        itin.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null){
                    Log.e("New Trip", String.valueOf(e));
                    Toast.makeText(EditItineraryActivity.this, "Error posting", Toast.LENGTH_SHORT).show();
                } else {
                    tvTripName.setText("");
                    tvLocation.setText("");
                    etNotes.setText("");
                    etStartDate.setText("");
                    etEndDate.setText("");

                    List<String> currentItins = currentUser.getList("itineraries");
                    if (currentItins == null){
                        currentItins = new ArrayList<String>();
                    }
                    currentItins.add(itin.getObjectId());
                    currentUser.put("itineraries", currentItins);

                    currentUser.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e != null){
                                Log.e("Saving user", String.valueOf(e));
                                Toast.makeText(EditItineraryActivity.this, "Error saving your trip", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }

    /**
     * Helper method: removes the date markers from the current itinerary so that new ones can be made
     */
    private List<String> removeDates(Date start, Date end){
        ParseQuery<Destination> query = ParseQuery.getQuery("Destination");

        query.whereEqualTo("itinerary", itin);

        List<String> itinDestinations = itin.getDestinations();
        List<String> toDelete = new ArrayList<>();

        query.findInBackground((objects, e) -> {
            if (e == null){
                for (Destination d : objects) {
                    Date day = d.getDate();
                    if (day.before(start) || day.after(end) || d.getIsDay()){
                        toDelete.add(d.getObjectId());

                        d.deleteInBackground(new DeleteCallback() {
                            @Override
                            public void done(ParseException e2) {
                                if (e2 != null){
                                    Log.e("EditItinerary", String.valueOf(e2));
                                    Toast.makeText(EditItineraryActivity.this, "Error updating dates", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                }
            } else {
                Log.e("EditItinerary", String.valueOf(e));
                Toast.makeText(EditItineraryActivity.this, "Error updating dates", Toast.LENGTH_SHORT).show();
            }

        });

        itinDestinations.removeAll(toDelete);
        return itinDestinations;
    }

    /**
     * Opens the Google Places SDK Autocomplete search page when the user focuses on the edittext;
     * lets them search for locations.
     */
    private View.OnFocusChangeListener focusListener = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if (hasFocus){
                List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME);

                Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
                        .build(EditItineraryActivity.this);
                startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);
            }
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place location = Autocomplete.getPlaceFromIntent(data);
                place = location;
                tvLocation.setText(place.getName());
            } else {
                Status status = Autocomplete.getStatusFromIntent(data);
                Log.i(TAG, status.getStatusMessage());
            }
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}