package com.example.fbufinalapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.ActivityOptions;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.transition.Slide;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.fbufinalapp.adapters.DestinationAdapter;
import com.example.fbufinalapp.databinding.ActivityDetailedItineraryBinding;
import com.example.fbufinalapp.models.Destination;
import com.example.fbufinalapp.models.Itinerary;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Screen where the user can see the details of their itinerary; displays days and destinations in
 * chronological order.
 */
public class DetailedItineraryActivity extends AppCompatActivity {
    RecyclerView rvDestinations;
    ArrayList<Destination> destinations;
    ActivityDetailedItineraryBinding binding;
    DestinationAdapter adapter;
    Itinerary currentItinerary;
    String itinId;
    Menu menu;
    static int USER_SHARED_SUCCESS = 11;
    static boolean editing;
    public static String TAG = "DetailedItinerary";

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

        getWindow().setEnterTransition(new Slide());

        itinId = getIntent().getStringExtra("itinId");

        runQueryThread();

        // Allows the user to create a new destination to add to this itinerary
        binding.fabNewDest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(DetailedItineraryActivity.this, EditDestinationActivity.class);
                i.putExtra("itinId", itinId);
                startActivity(i, ActivityOptions.makeSceneTransitionAnimation(DetailedItineraryActivity.this).toBundle());
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

    /**
     * Queries the destinations of the current itinerary in the background. In the meantime, the
     * loading progress symbol is shown.
     */
    class queryDestinationsAsync extends Thread{
        @Override
        public void run() {

            ParseQuery<Itinerary> queryItinerary = ParseQuery.getQuery(Itinerary.class);

            queryItinerary.getInBackground(itinId, (object, e) -> {
                if (e == null) {
                    currentItinerary = object;
                    binding.tvTitle.setText(object.getTitle());
                    getSupportActionBar().setTitle(object.getTitle());
                    getDestinations();

                    rvDestinations = binding.rvDestinations;
                    destinations = new ArrayList<>();
                    adapter = new DestinationAdapter(DetailedItineraryActivity.this, destinations, currentItinerary);

                    rvDestinations.setAdapter(adapter);
                    rvDestinations.setLayoutManager(new LinearLayoutManager(DetailedItineraryActivity.this));
                } else {
                    Toast.makeText(DetailedItineraryActivity.this, "Couldn't retrieve itinerary", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    public void runQueryThread(){
        queryDestinationsAsync queryDestinations = new queryDestinationsAsync();
        binding.rotateloading.start();
        queryDestinations.start();
        try {
            queryDestinations.join();
        } catch (Exception e){
            Log.e(TAG, String.valueOf(e));
            Log.e("DetailedItinerary", String.valueOf(e));
        }

        binding.rotateloading.stop();
    }

    public void fetchTimelineAsync(int page) {
        adapter.clear();
        runQueryThread();
        binding.swipeContainer.setRefreshing(false);
    }

    /**
     * Queries destinations that belong to the current itinerary from the Parse backend. Displays
     * these destinations on the page in increasing date order.
     */
    private void getDestinations(){
        ParseQuery<Destination> queryDestination = ParseQuery.getQuery(Destination.class);

        queryDestination.whereEqualTo(CommonValues.KEY_ITINERARY_DESTINATION, currentItinerary);
        queryDestination.orderByAscending(CommonValues.KEY_DATE);

        queryDestination.findInBackground(new FindCallback<Destination>() {
            @Override
            public void done(List<Destination> objects, ParseException e) {
                if (e == null){
                    destinations.addAll(objects);
                    adapter.notifyDataSetChanged();
                } else {
                    Log.e(TAG, String.valueOf(e));
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
            case R.id.action_add_person:
                Intent i = new Intent(this, UserSearchActivity.class);
                startActivityForResult(i, USER_SHARED_SUCCESS);
                // find person
            case R.id.action_share:
                shareItinerary();
        }
        return(super.onOptionsItemSelected(item));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == USER_SHARED_SUCCESS) {
            if (data != null){
                String userId = data.getStringExtra("userId");
                List<String> authors = currentItinerary.getAuthor();

                if (authors.contains(userId)){
                    Toast.makeText(getBaseContext(), "User is already in this trip", Toast.LENGTH_SHORT).show();
                } else {
                    authors.add(userId);
                    currentItinerary.setAuthor(authors);
                    currentItinerary.saveInBackground();
                    Toast.makeText(getBaseContext(), "User added to trip!", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    public void shareItinerary(){
        int pageHeight = 1120;
        int pageWidth = 792;

        PdfDocument document = new PdfDocument();

        Paint paint = new Paint();
        Paint textFormat = new Paint();

        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(pageWidth, pageHeight, 1).create();


        PdfDocument.Page page = document.startPage(pageInfo);
        Canvas canvas = page.getCanvas();

        textFormat.setTextSize(28);
        textFormat.setTextAlign(Paint.Align.CENTER);
        canvas.drawText(currentItinerary.getTitle(), 100, 125, textFormat);

        textFormat.setTextSize(20);
        textFormat.setTextAlign(Paint.Align.LEFT);
        String destins = getPDFDestinations();
        int yLoc = 175;
        for (String i : destins.split("\n")) {
            canvas.drawText(i, 100, yLoc, textFormat);
            yLoc += 25;
        }

        document.finishPage(page);

        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_TEXT, destins);
        shareIntent.putExtra(Intent.EXTRA_TITLE, currentItinerary.getTitle());
        shareIntent.setType("application/pdf");
        startActivity(Intent.createChooser(shareIntent, "Share itinerary to..."));

        document.close();
    }

    public String getPDFDestinations(){
        String res = "";
        List<Destination> toExport = adapter.getItems();
        for (Destination d : toExport){
            if (d.getIsDay()){
                res += d.getName();
            } else {
                res += d.reformatTime() + ": " + d.getName();
            }
            res += "\n";
        }
        return res;
    }
}