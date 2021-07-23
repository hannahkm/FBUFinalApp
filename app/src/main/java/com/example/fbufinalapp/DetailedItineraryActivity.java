package com.example.fbufinalapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
    ActivityDetailedItineraryBinding binding;
    DestinationAdapter adapter;
    Itinerary currentItinerary;
    String itinId;
    Menu menu;
    static boolean editing;

    public static boolean getEditing() {
        return editing;
    }

    @Override
    protected void onPause() {
        super.onPause();

        editing = false;
        menu.findItem(R.id.action_edit).setTitle("EDIT");

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // using view binding
        binding = ActivityDetailedItineraryBinding.inflate(getLayoutInflater());

        // layout of activity is stored in a special property called root
        View view = binding.getRoot();
        setContentView(view);

        itinId = getIntent().getStringExtra("itinId");

        ParseQuery<Itinerary> queryItinerary = ParseQuery.getQuery(Itinerary.class);

        queryItinerary.getInBackground(itinId, (object, e) -> {
            if (e == null) {
                currentItinerary = object;
                binding.tvTitle.setText(object.getTitle());
                getSupportActionBar().setTitle(object.getTitle());
                getDestinations();

                rvDestinations = view.findViewById(R.id.rvDestinations);
                destinations = new ArrayList<>();
                adapter = new DestinationAdapter(this, destinations, currentItinerary);

                rvDestinations.setAdapter(adapter);
                rvDestinations.setLayoutManager(new LinearLayoutManager(this));
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

        // Setup refresh listener which triggers new data loading
        binding.swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // refreshes the user's timeline
                fetchTimelineAsync(0);
            }
        });
    }

    public void fetchTimelineAsync(int page) {
        adapter.clear();
        getDestinations();
        binding.swipeContainer.setRefreshing(false);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        getMenuInflater().inflate(R.menu.menu_bar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_edit:
                if (editing) {
                    editing = false;
                    item.setTitle("EDIT");
                } else {
                    editing = true;
                    item.setTitle("DONE");
                }
                return true;
        }
        return(super.onOptionsItemSelected(item));
    }
}