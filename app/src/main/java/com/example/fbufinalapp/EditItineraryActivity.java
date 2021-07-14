package com.example.fbufinalapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fbufinalapp.models.Itinerary;
import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class EditItineraryActivity extends AppCompatActivity {
    TextView tvTripName;
    TextView tvLocation;
    TextView etStartDate;
    TextView etEndDate;
    TextView etNotes;
    Button btFinish;
    Place place;

    private static int AUTOCOMPLETE_REQUEST_CODE = 1;
    private static String TAG = "EditItineraryActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_itinerary);

        tvTripName = findViewById(R.id.tvTripName);
        tvLocation = findViewById(R.id.tvLocation);
        etStartDate = findViewById(R.id.etStartDate);
        etEndDate = findViewById(R.id.etEndDate);
        etNotes = findViewById(R.id.etNotes);
        btFinish = findViewById(R.id.btFinish);

        tvLocation.setOnFocusChangeListener(focusListener);

        btFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = String.valueOf(tvTripName.getText());
                String notes = String.valueOf(etNotes.getText());

                Itinerary itin = new Itinerary();

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
                        Log.i("EditItinerary", String.valueOf(e));
                    }
                }

                if (!endInput.equals("")){
                    try {
                        end = sdf.parse(String.valueOf(etEndDate.getText()));
                        itin.setEndDate(end);
                    } catch (java.text.ParseException e) {
                        Log.i("EditItinerary", String.valueOf(e));
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
                itin.setPlaceID(place.getId());

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

                finish();
            }
        });
    }

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