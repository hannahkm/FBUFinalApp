package com.example.fbufinalapp;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
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

import com.example.fbufinalapp.adapters.ItineraryAdapter;
import com.example.fbufinalapp.databinding.FragmentDashboardBinding;
import com.example.fbufinalapp.models.Itinerary;
import com.google.android.gms.common.api.ApiException;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
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

        binding.avi.show();

        return view;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_bar, menu);
        this.menu = menu;
    }

    /**
     * Handles menu item clicks when the user clicks the Edit button on the page
     */
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
            default:
                // Invoke the superclass to handle unknown menu clicks
                return super.onOptionsItemSelected(item);

        }
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
        binding.swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // refreshes the user's timeline
                fetchTimelineAsync(0);
            }
        });

        new queryTripsAsync().execute();

        // Allows the user to create new itineraries
        binding.fabNewItin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, EditItineraryActivity.class);
                startActivity(i);
                adapter.notifyDataSetChanged();
            }
        });
    }


    public void fetchTimelineAsync(int page) {
        adapter.clear();
        new queryTripsAsync().execute();
        binding.swipeContainer.setRefreshing(false);
    }

    private class queryTripsAsync extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... args) {
            ParseQuery<Itinerary> query = ParseQuery.getQuery(Itinerary.class);
            query.include(CommonValues.KEY_USER);
            query.whereEqualTo("authors", CommonValues.CURRENT_USER);

            // order posts by creation date (newest first)
            query.addDescendingOrder("createdAt");
            query.findInBackground(new FindCallback<Itinerary>() {
                @Override
                public void done(List<Itinerary> itineraries, ParseException e) {
                    if (e != null){
                        Log.e("DashboardFragment", String.valueOf(e));
                    } else {
                        // save received posts to list and notify adapter of new data
                        trips.addAll(itineraries);
                        adapter.notifyDataSetChanged();
                    }
                }
            });
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            binding.avi.hide();
            super.onPostExecute(result);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            binding.avi.show();
        }
    }

}