package com.example.fbufinalapp;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.fbufinalapp.adapters.LocationsAdapter;
import com.example.fbufinalapp.databinding.FragmentFavoritesBinding;
import com.google.android.gms.common.api.ApiException;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FavoritesFragment extends Fragment {
    FragmentFavoritesBinding binding;
    Context context;
    List<String> favorites;
    LocationsAdapter favAdapter;
    RecyclerView rvFavorites;
    ParseUser currentUser;
    public static String TAG = "FavoritesFragment";
    List<Place.Field> placeFields;
    PlacesClient placesClient;

    public FavoritesFragment() {
        // Required empty public constructor
    }

    public static FavoritesFragment newInstance() {
        FavoritesFragment fragment = new FavoritesFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((MainActivity) getActivity()).setActionBarTitle("Favorite Locations");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        context = container.getContext();
        binding = FragmentFavoritesBinding.inflate(getLayoutInflater(), container, false);
        View view = binding.getRoot();

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rvFavorites = view.findViewById(R.id.rvFavorites);

        currentUser = ParseUser.getCurrentUser();

        placesClient = Places.createClient(context);
        placeFields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS);

        favorites = new ArrayList<>();
        favAdapter = new LocationsAdapter(context, favorites);

        rvFavorites.setAdapter(favAdapter);
        rvFavorites.setLayoutManager(new LinearLayoutManager(context));

        // Setup refresh listener which triggers new data loading
        binding.swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // refreshes the user's page
                fetchTimelineAsync(0);
            }
        });

        queryFavorites();

    }

    public void fetchTimelineAsync(int page) {
        favAdapter.clear();
        queryFavorites();
        binding.swipeContainer.setRefreshing(false);
    }

    private void queryFavorites(){
        for (Object fav: currentUser.getList("favorites")) {
            String placeId = String.valueOf(fav);

            final FetchPlaceRequest request = FetchPlaceRequest.newInstance(placeId, placeFields);

            placesClient.fetchPlace(request).addOnSuccessListener((response) -> {
                Place place = response.getPlace();
                String message = place.getName() + "\n" + place.getAddress() + "\n" + placeId;
                favorites.add(message);
                favAdapter.notifyItemInserted(favorites.size());
            }).addOnFailureListener((exception) -> {
                if (exception instanceof ApiException) {
                    Log.e(TAG, "Place not found: " + exception.getMessage());
                }
            });
        }

//        favAdapter.notifyDataSetChanged();
    }


}