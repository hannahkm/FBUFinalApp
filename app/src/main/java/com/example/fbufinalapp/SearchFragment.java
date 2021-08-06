package com.example.fbufinalapp;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.example.fbufinalapp.adapters.LocationsAdapter;
import com.example.fbufinalapp.databinding.FragmentSearchBinding;
import com.google.android.gms.common.api.ApiException;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.PlacesClient;

import java.util.ArrayList;
import java.util.List;

/**
 * Fragment subclass for the search page, where the user can search for locations using the
 * Google Places SDK
 */
public class SearchFragment extends Fragment {
    public static final String TAG = "SearchFragment";
    public static int AUTOCOMPLETE_REQUEST_CODE = 1;
    private EditText etSearch;
    private RecyclerView searchResults;
    FragmentSearchBinding binding;
    List<String> places;
    LocationsAdapter adapter;
    private StringBuilder mResult;
    PlacesClient placesClient;
    Context context;

    public SearchFragment() {
        // Required empty public constructor
    }

    public static SearchFragment newInstance() {
        SearchFragment fragment = new SearchFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((MainActivity) getActivity()).setActionBarTitle("Find Destinations");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        context = container.getContext();
        binding = FragmentSearchBinding.inflate(getLayoutInflater(), container, false);
        View view = binding.getRoot();

        binding.rotateloading.start();

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        searchResults = view.findViewById(R.id.rvResults);

        places = new ArrayList<>();
        adapter = new LocationsAdapter(context, places);

        searchResults.setAdapter(adapter);
        searchResults.setLayoutManager(new LinearLayoutManager(context));

        binding.etSearch.addTextChangedListener(searchListener);

        binding.rotateloading.stop();

        placesClient = Places.createClient(context);
    }

    /**
     * Listens to when the user types into the search bar and displays matching results underneath
     */
    private TextWatcher searchListener = new TextWatcher () {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String query = String.valueOf(binding.etSearch.getText());

            // Create a new token for the autocomplete session
            AutocompleteSessionToken token = AutocompleteSessionToken.newInstance();

            FindAutocompletePredictionsRequest request = FindAutocompletePredictionsRequest.builder()
                    .setSessionToken(token)
                    .setQuery(query)
                    .build();

            placesClient.findAutocompletePredictions(request).addOnSuccessListener(response -> {
                places.clear();

                for (AutocompletePrediction prediction : response.getAutocompletePredictions()) {
                    mResult = new StringBuilder();
                    mResult.append(prediction.getPrimaryText(null) + "\n");
                    mResult.append(prediction.getSecondaryText(null) + "\n");
                    mResult.append(prediction.getPlaceId());
                    places.add(String.valueOf(mResult));
                }

                adapter.notifyDataSetChanged();
            }).addOnFailureListener((exception) -> {
                if (exception instanceof ApiException) {
                    ApiException apiException = (ApiException) exception;
                    Toast.makeText(context, "Place not found", Toast.LENGTH_SHORT).show();
                }
            });
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

}