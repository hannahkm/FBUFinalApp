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

/**
 * Fragment subclass; shows the user a list of the places they liked
 */
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

        currentUser = CommonValues.CURRENT_USER;

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
                fetchTimelineAsync(0);
            }
        });

        queryFavoritesAsync queryFavorites = new queryFavoritesAsync();
        binding.rotateloading.start();
        queryFavorites.start();
        try {
            queryFavorites.join();
        } catch (Exception e){
            Log.e("Favorites", String.valueOf(e));
        }

        binding.rotateloading.stop();

    }

    public void fetchTimelineAsync(int page) {
        favAdapter.clear();
        queryFavoritesAsync queryFavorites = new queryFavoritesAsync();
        binding.rotateloading.start();
        queryFavorites.start();
        try {
            queryFavorites.join();
        } catch (Exception e){
            Log.e("Favorites", String.valueOf(e));
        }

        binding.rotateloading.stop();
        binding.swipeContainer.setRefreshing(false);
    }

    /**
     * Queries the favorite locations of the current user in the background. In the meantime, the
     * loading progress symbol is shown.
     */
    class queryFavoritesAsync extends Thread{
        @Override
        public void run() {
            for (Object fav: currentUser.getList(CommonValues.KEY_FAVORITES)) {
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
        }
    }


}