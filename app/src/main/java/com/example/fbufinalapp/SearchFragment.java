package com.example.fbufinalapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;

import java.util.Arrays;
import java.util.List;

import static android.app.Activity.RESULT_OK;
import static android.app.Activity.RESULT_CANCELED;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SearchFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SearchFragment extends Fragment {
    public static final String TAG = "SearchFragment";
    public static int AUTOCOMPLETE_REQUEST_CODE = 1;
    private EditText etSearch;
    private TextView searchResults;
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        context = container.getContext();
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        etSearch = view.findViewById(R.id.etSearch);
        searchResults = view.findViewById(R.id.tvResults);

        etSearch.addTextChangedListener(searchListener);

        placesClient = Places.createClient(context);
    }

    private TextWatcher searchListener = new TextWatcher () {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String query = String.valueOf(etSearch.getText());

            // Create a new token for the autocomplete session
            AutocompleteSessionToken token = AutocompleteSessionToken.newInstance();

            FindAutocompletePredictionsRequest request = FindAutocompletePredictionsRequest.builder()
                    .setSessionToken(token)
                    .setQuery(query)
                    .build();

            placesClient.findAutocompletePredictions(request).addOnSuccessListener(response -> {
                mResult = new StringBuilder();

                for (AutocompletePrediction prediction : response.getAutocompletePredictions()) {
                    mResult.append(prediction.getPrimaryText(null) + "\n");
                    mResult.append(prediction.getSecondaryText(null) + "\n\n");
                }

                searchResults.setText(String.valueOf(mResult));
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