package com.example.fbufinalapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.fbufinalapp.adapters.ItineraryAdapter;
import com.example.fbufinalapp.databinding.FragmentDashboardBinding;
import com.example.fbufinalapp.models.Itinerary;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;
/**
 * A Fragment subclass to create the user's dashboard page
 * Builds the list of itineraries that the user has created and presents it as a list
 * Allows the user to move to the EditItinerary page to create new trips/edit current ones
 */
public class DashboardFragment extends Fragment {
    Context context;
    RecyclerView rvItineraries;
    List<Itinerary> trips;
    ItineraryAdapter adapter;
    FragmentDashboardBinding binding;
    Menu menu;
    public static String TAG = "DashboardFragment";
    public static boolean editing;

    public DashboardFragment() {
        // Required empty public constructor
    }

    public static DashboardFragment newInstance() {
        DashboardFragment fragment = new DashboardFragment();
        return fragment;
    }

    /**
     * Resets the appearance of the edit button when the user leaves this page
     */
    @Override
    public void onPause() {
        super.onPause();

        editing = false;
        menu.findItem(R.id.action_edit).setTitle("EDIT");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
        editing = false;
        ((MainActivity) getActivity()).setActionBarTitle("Dashboard");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        context = container.getContext();
        binding = FragmentDashboardBinding.inflate(getLayoutInflater(), container, false);
        View view = binding.getRoot();

        binding.rotateloading.start();

        return view;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_bar, menu);
        this.menu = menu;

        menu.findItem(R.id.action_add_person).setVisible(false);
        menu.findItem(R.id.action_share).setVisible(false);
    }

    /**
     * Handles menu item clicks when the user clicks the Edit button on the page
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_edit) {
            if (editing) {
                editing = false;
                item.setTitle("EDIT");
            } else {
                editing = true;
                item.setTitle("DONE");
            }
            return true;
        }// Invoke the superclass to handle unknown menu clicks
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rvItineraries = view.findViewById(R.id.rvFavorites);
        trips = new ArrayList<>();
        adapter = new ItineraryAdapter(context, trips);

        rvItineraries.setAdapter(adapter);
        rvItineraries.setLayoutManager(new LinearLayoutManager(context));

        // Setup refresh listener which triggers new data loading
        binding.swipeContainer.setOnRefreshListener(() -> {
            // refreshes the user's timeline
            fetchTimelineAsync(0);
        });

        runQueryThread();

        // Allows the user to create new itineraries
        binding.fabNewItin.setOnClickListener(v -> {
            Intent i = new Intent(context, EditItineraryActivity.class);
            startActivity(i);
            adapter.notifyDataSetChanged();
        });
    }

    public void runQueryThread(){
        try {
            queryTripsAsync queryTrips = new queryTripsAsync();
            binding.rotateloading.start();
            queryTrips.start();
            try {
                queryTrips.join();
            } catch (Exception e){
                Log.e(TAG, String.valueOf(e));
            }
            binding.rotateloading.stop();
        } catch (NullPointerException e){
            Toast.makeText(getActivity(), "Couldn't load feed, try again later", Toast.LENGTH_SHORT).show();
        }

    }


    public void fetchTimelineAsync(int page) {
        adapter.clear();
        runQueryThread();
        binding.swipeContainer.setRefreshing(false);
    }

    class queryTripsAsync extends Thread{
        @Override
        public void run() {
            try {
                ParseQuery<Itinerary> query = ParseQuery.getQuery(Itinerary.class);
                query.include(CommonValues.KEY_USER);
                query.whereEqualTo(CommonValues.KEY_USER, ParseUser.getCurrentUser().getObjectId());

                // order posts by creation date (newest first)
                query.addDescendingOrder("createdAt");
                query.findInBackground((itineraries, e) -> {
                    if (e != null){
                        Log.e(TAG, String.valueOf(e));
                    } else {
                        trips.addAll(itineraries);
                        adapter.notifyDataSetChanged();
                    }
                });
            } catch (NullPointerException e){
                Log.e(TAG, String.valueOf(e));
            }
        }
    }

}