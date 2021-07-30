package com.example.fbufinalapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.transition.Explode;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fbufinalapp.databinding.ActivityEditDestinationBinding;
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
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Page where user can edit/create a new destination for their itinerary. Allows them to put in
 * information regarding the location and save to an itinerary
 */
public class EditDestinationActivity extends AppCompatActivity {
    List<String> tripDates;
    String dateSelected;
    String timeSelected;
    Place place;
    Itinerary currentItinerary;
    ActivityEditDestinationBinding binding;
    Destination editingDestination;
    TextView tvLocation;
    String itinId;
    boolean editing = false;
    private static int AUTOCOMPLETE_REQUEST_CODE = 600;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // using view binding
        binding = ActivityEditDestinationBinding.inflate(getLayoutInflater());

        // layout of activity is stored in a special property called root
        View view = binding.getRoot();
        setContentView(view);

        getWindow().setEnterTransition(new Explode());
        getWindow().setExitTransition(new Explode());

        tripDates = new ArrayList<>();
        tvLocation = findViewById(R.id.tvLocation);

        itinId = getIntent().getStringExtra("itinId");

        // populates page with existing values, if there are any
        new queryDefaultValues().execute();

        ParseQuery<Itinerary> queryItinerary = ParseQuery.getQuery(Itinerary.class);

        queryItinerary.getInBackground(itinId, (object, e) -> {
            if (e == null) {
                currentItinerary = object;
                binding.tvTripName.setText(object.getTitle());
                Date start = object.getStartDate();
                Date end = object.getEndDate();

                Calendar cStart = Calendar.getInstance();
                cStart.setTime(start);
                Calendar cEnd = Calendar.getInstance();
                cEnd.setTime(end);
                cEnd.add(Calendar.DATE, 1);

                for (Calendar date = cStart; date.before(cEnd); date.add(Calendar.DATE, 1)){
                    tripDates.add(object.reformatDate(date.getTime()));
                }
            } else {
                Toast.makeText(this, "Couldn't retrieve itinerary", Toast.LENGTH_SHORT).show();
            }
        });

        ArrayAdapter<String> dateAdapter = new ArrayAdapter<>(EditDestinationActivity.this, R.layout.item_spinner, tripDates);
        binding.etDateSelect.setAdapter(dateAdapter);
        binding.etDateSelect.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                dateSelected = String.valueOf(parent.getItemAtPosition(position));
            }
        });

        String[] timeArray = new String[]{"AM", "PM"};
        ArrayAdapter<String> timeAdapter = new ArrayAdapter<>(EditDestinationActivity.this, R.layout.item_spinner, timeArray);
        binding.etTimeSelect.setAdapter(timeAdapter);
        binding.etTimeSelect.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                timeSelected = String.valueOf(parent.getItemAtPosition(position));
            }
        });

        binding.btFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Destination destination;

                if (editing) {
                    destination = editingDestination;
                } else {
                    destination = new Destination();
                }

                if (place != null) {
                    destination.setPlaceID(place.getId());
                }

                destination.setItinerary(currentItinerary);

                SimpleDateFormat timezoneFormat = new SimpleDateFormat("zzzz");

                String timeString = dateSelected + " " + binding.etTime.getText() + " " + timeSelected + " " + timezoneFormat.format(new Date());
                SimpleDateFormat formatter = new SimpleDateFormat("MMM dd, yyyy hh:mm a zzzz");

                boolean timeValid = true;
                Date selectedDate;
                try {
                    selectedDate = formatter.parse(timeString);
                } catch (java.text.ParseException e) {
                    e.printStackTrace();
                    selectedDate = null;
                    timeValid = false;
                }

                try {
                    if (place.isOpen(selectedDate.getTime())){
                        destination.setDate(selectedDate);
                    } else {
                        timeValid = false;
                    }
                } catch (NullPointerException e) {
                    e.printStackTrace();
                    destination.setDate(selectedDate);
                }

                destination.setIsDay(false);

                String placeName = place.getName();
                String inputtedName = String.valueOf(binding.etDestName.getText());
                if (placeName.equals(inputtedName)){
                    destination.setName(inputtedName);
                } else {
                    destination.setName(inputtedName + "\n" + placeName);
                }

                if (timeValid){
                    destination.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null){
                                binding.tvTripName.setText("");
                                tvLocation.setText("");
                                binding.etDateSelect.setText("");
                                binding.etTime.setText("");

                                if (!editing) {
                                    queryItinerary.getInBackground(itinId, (object, error) -> {
                                        if (error != null){
                                            Toast.makeText(EditDestinationActivity.this, "Couldn't save destination", Toast.LENGTH_SHORT).show();
                                        } else {
                                            List<String> currentDests = object.getDestinations();
                                            currentDests.add(destination.getObjectId());
                                            object.put(CommonValues.KEY_DESTINATIONS, currentDests);

                                            object.saveInBackground();
                                        }
                                    });
                                }
                            } else {
                                Toast.makeText(EditDestinationActivity.this, "Couldn't save destination", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                    finishAfterTransition();
                } else {
                    Toast.makeText(EditDestinationActivity.this, "This location won't be open at that time.", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private class queryDefaultValues extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... args) {
            populateDefaultValues();
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            binding.avi.hide();
        }

        @Override
        protected void onPreExecute() {
            binding.avi.show();
        }
    }

    private void populateDefaultValues() {
        if (getIntent().hasExtra("placeId")){
            String placeId = getIntent().getStringExtra("placeId");

            tvLocation.setFocusable(false);

            if (placeId != null){
                final List<Place.Field> placeFields = Arrays.asList(Place.Field.ID, Place.Field.NAME);

                final FetchPlaceRequest request = FetchPlaceRequest.newInstance(placeId, placeFields);

                PlacesClient placesClient = Places.createClient(EditDestinationActivity.this);

                placesClient.fetchPlace(request).addOnSuccessListener((response) -> {
                    place = response.getPlace();
                    tvLocation.setText(place.getName());
                    getSupportActionBar().setTitle(place.getName());
                }).addOnFailureListener((exception) -> {
                    if (exception instanceof ApiException) {
                        Toast.makeText(EditDestinationActivity.this, "Error retrieving location", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            if (getIntent().hasExtra("editing")){
                editing = true;
                tvLocation.setOnFocusChangeListener(focusListener);

                ParseQuery<Destination> query = ParseQuery.getQuery("Destination");
                query.getInBackground(getIntent().getStringExtra("destinationId"), (object, e) -> {
                    if (e == null) {
                        editingDestination = object;
                        Date date = editingDestination.getDate();

                        SimpleDateFormat dateFormatter = new SimpleDateFormat("MMMM dd, yyyy");
                        SimpleDateFormat timeFormatter = new SimpleDateFormat("hh:mm");
                        SimpleDateFormat amPMFormatter = new SimpleDateFormat("a");

                        dateSelected = dateFormatter.format(date);
                        timeSelected = amPMFormatter.format(date);
                        binding.etDateSelect.setHint(dateSelected);
                        binding.etTime.setText(timeFormatter.format(date));
                        binding.etTimeSelect.setHint(timeSelected);

                        String name = object.getName();
                        if (name.contains("\n")) {
                            String destiName = name.substring(0, name.indexOf("\n"));
                            binding.etDestName.setText(destiName);
                            getSupportActionBar().setTitle(destiName);
                        } else {
                            binding.etDestName.setText(name);
                            getSupportActionBar().setTitle(name);
                        }
                    } else {
                        // something went wrong
                        Toast.makeText(this, "Couldn't retrieve destination", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        } else {
            tvLocation.setOnFocusChangeListener(focusListener);
        }
    }

    /**
     * Opens Google Places SDK Autocomplete page when the user focuses on the EditText box. Lets them
     * search for a location.
     */
    private View.OnFocusChangeListener focusListener = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if (hasFocus){
                List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME,
                        Place.Field.OPENING_HOURS, Place.Field.UTC_OFFSET);

                Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
                        .build(EditDestinationActivity.this);
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
                String name = place.getName();
                tvLocation.setText(name);
                if (String.valueOf(binding.etDestName.getText()).isEmpty()) {
                    binding.etDestName.setText(name);
                }
            } else {
                Status status = Autocomplete.getStatusFromIntent(data);
            }
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}