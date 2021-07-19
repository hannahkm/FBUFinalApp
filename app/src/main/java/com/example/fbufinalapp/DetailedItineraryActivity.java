package com.example.fbufinalapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fbufinalapp.adapters.DestinationAdapter;
import com.example.fbufinalapp.databinding.ActivityDetailedItineraryBinding;
import com.example.fbufinalapp.models.Destination;
import com.example.fbufinalapp.models.Itinerary;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

public class DetailedItineraryActivity extends AppCompatActivity {
    RecyclerView rvDestinations;
    ArrayList<Destination> destinations;
    DestinationAdapter adapter;
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
                binding.tvTitle.setText(object.getTitle());
                getDestinations(object.getDestinations());
            } else {
                Toast.makeText(this, "Couldn't retrieve itinerary", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void getDestinations(List<String> destinationIds){
        ParseQuery<Destination> queryDestination = ParseQuery.getQuery(Destination.class);

        queryDestination.addAscendingOrder("createdAt");

        for (String destId : destinationIds) {
            queryDestination.getInBackground(destId, (object, e) -> {
               if (e == null){
                   destinations.add(object);
                   adapter.notifyItemInserted(0);
               } else {
                   Toast.makeText(this, "Couldn't retrieve destinations", Toast.LENGTH_SHORT).show();
               }
            });
        }
    }
}