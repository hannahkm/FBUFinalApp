package com.example.fbufinalapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fbufinalapp.databinding.ActivityEditDestinationBinding;
import com.example.fbufinalapp.models.Destination;
import com.example.fbufinalapp.models.Itinerary;
import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.SimpleTimeZone;
import java.util.TimeZone;

public class EditDestinationActivity extends AppCompatActivity {
//    List<String> tripNames;
    List<String> tripDates;
    String dateSelected;
    String timeSelected;
    Place place;
    TextView tvLocation;
    private static int AUTOCOMPLETE_REQUEST_CODE = 600;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // using view binding
        ActivityEditDestinationBinding binding = ActivityEditDestinationBinding.inflate(getLayoutInflater());

        // layout of activity is stored in a special property called root
        View view = binding.getRoot();
        setContentView(view);

//        tripNames = new ArrayList<>();
        tripDates = new ArrayList<>();
        tvLocation = findViewById(R.id.tvLocation);
        tvLocation.setOnFocusChangeListener(focusListener);

        String itinId = getIntent().getStringExtra("itinId");

        ParseQuery<Itinerary> queryItinerary = ParseQuery.getQuery(Itinerary.class);

        queryItinerary.getInBackground(itinId, (object, e) -> {
            if (e == null) {
                binding.tvTripName.setText(object.getTitle());
//                tripNames.add(object.getTitle());
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
                Destination destination = new Destination();
                destination.setPlaceID(place.getId());

                String timeString = dateSelected + " " + binding.etTime.getText() + " " + timeSelected + " " + TimeZone.getDefault().getDisplayName();
                SimpleDateFormat formatter = new SimpleDateFormat("MMM dd, yyyy hh:mm a zzzz");
                formatter.setTimeZone(TimeZone.getDefault());
                try {
                    destination.setDate(formatter.parse(timeString));
                } catch (java.text.ParseException e) {
                    Log.e("EditDestination", String.valueOf(e));
                    e.printStackTrace();
                }
                destination.setIsDay(false);
                destination.setName(String.valueOf(binding.tvLocation.getText()));

                destination.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null){
                            binding.tvTripName.setText("");
                            tvLocation.setText("");
                            binding.etDateSelect.setText("");
                            binding.etTime.setText("");

                            queryItinerary.getInBackground(itinId, (object, error) -> {
                                if (error != null){
                                    Toast.makeText(EditDestinationActivity.this, "Couldn't save destination", Toast.LENGTH_SHORT).show();
                                } else {
                                    List<String> currentDests = object.getDestinations();
                                    currentDests.add(destination.getObjectId());
                                    object.put("destinations", currentDests);

                                    object.saveInBackground();
                                }
                            });
                        } else {
                            Toast.makeText(EditDestinationActivity.this, "Couldn't save destination", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

    }

    private View.OnFocusChangeListener focusListener = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if (hasFocus){
                List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME);

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
                tvLocation.setText(place.getName());
            } else {
                Status status = Autocomplete.getStatusFromIntent(data);
            }
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}