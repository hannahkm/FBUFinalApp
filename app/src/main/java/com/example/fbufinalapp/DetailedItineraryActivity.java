package com.example.fbufinalapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fbufinalapp.adapters.DestinationAdapter;
import com.example.fbufinalapp.databinding.ActivityDetailedItineraryBinding;
import com.example.fbufinalapp.models.Destination;
import com.example.fbufinalapp.models.Itinerary;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

public class DetailedItineraryActivity extends AppCompatActivity {
    RecyclerView rvDestinations;
    ArrayList<Destination> destinations;
    DestinationAdapter adapter;
    Itinerary currentItinerary;
    String itinId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // using view binding
        ActivityDetailedItineraryBinding binding = ActivityDetailedItineraryBinding.inflate(getLayoutInflater());

        // layout of activity is stored in a special property called root
        View view = binding.getRoot();
        setContentView(view);

        rvDestinations = view.findViewById(R.id.rvDestinations);
        destinations = new ArrayList<>();
        adapter = new DestinationAdapter(this, destinations);

        rvDestinations.setAdapter(adapter);
        rvDestinations.setLayoutManager(new LinearLayoutManager(this));

        itinId = getIntent().getStringExtra("itinId");

        ParseQuery<Itinerary> queryItinerary = ParseQuery.getQuery(Itinerary.class);

        queryItinerary.getInBackground(itinId, (object, e) -> {
            if (e == null) {
                currentItinerary = object;
                binding.tvTitle.setText(object.getTitle());
                getDestinations();
            } else {
                Toast.makeText(this, "Couldn't retrieve itinerary", Toast.LENGTH_SHORT).show();
            }
        });

        binding.fabNewDest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(DetailedItineraryActivity.this, EditDestinationActivity.class);
                i.putExtra("itinId", itinId);
                startActivity(i);
                adapter.notifyDataSetChanged();
            }
        });
    }


    private void getDestinations(){
        ParseQuery<Destination> queryDestination = ParseQuery.getQuery(Destination.class);

        queryDestination.whereEqualTo("itinerary", currentItinerary);
        queryDestination.orderByAscending("date");

        queryDestination.findInBackground(new FindCallback<Destination>() {
            @Override
            public void done(List<Destination> objects, ParseException e) {
                if (e == null){
                    destinations.addAll(objects);
                    adapter.notifyDataSetChanged();
                } else {
                    Log.e("DetailedItinerary", String.valueOf(e));
                    Toast.makeText(DetailedItineraryActivity.this, "Couldn't load destinations", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}