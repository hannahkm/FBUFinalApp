package com.example.fbufinalapp.adapters;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fbufinalapp.CommonValues;
import com.example.fbufinalapp.DashboardFragment;
import com.example.fbufinalapp.DetailedItineraryActivity;
import com.example.fbufinalapp.EditItineraryActivity;
import com.example.fbufinalapp.R;
import com.example.fbufinalapp.models.Itinerary;
import com.google.android.gms.common.api.ApiException;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Adapter to build recyclerview with itineraries for items.
 */
public class ItineraryAdapter extends RecyclerView.Adapter<ItineraryAdapter.ViewHolder>{
    private Context context;
    private List<Itinerary> itins;
    private static final String TAG = "ItineraryAdapter";
    ParseUser currentUser;

    public ItineraryAdapter(Context context, List<Itinerary> itins){
        this.context = context;
        this.itins = itins;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_itinerary, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItineraryAdapter.ViewHolder holder, int position) {
        Itinerary itin = itins.get(position);
        holder.bind(itin);

    }

    @Override
    public int getItemCount() {
        return itins.size();
    }

    public void clear() {
        itins.clear();
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle;
        TextView tvDates;
        TextView tvLocation;
        TextView tvPeople;
        ImageView dropdown;
        RelativeLayout hiddenView;
        CardView cardView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvDates = itemView.findViewById(R.id.tvDates);
            tvLocation = itemView.findViewById(R.id.tvLocation);
            tvPeople = itemView.findViewById(R.id.tvPeople);
            dropdown = itemView.findViewById(R.id.iv_dropdown);
            hiddenView = itemView.findViewById(R.id.expand_cardview);
            cardView = itemView.findViewById(R.id.base_cardview);

            // allow user to click on itineraries to either edit info or view detailed page
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Itinerary selected = itins.get(getAdapterPosition());
                    String itinId = selected.getObjectId();
                    Intent i;

                    if (DashboardFragment.editing){
                        i = new Intent(context, EditItineraryActivity.class);
                        i.putExtra("editing", true);
                    } else {
                        i = new Intent(context, DetailedItineraryActivity.class);
                    }

                    i.putExtra("itinId", itinId);
                    context.startActivity(i, ActivityOptions.makeSceneTransitionAnimation((Activity) context).toBundle());
                }
            });

            // user can delete itineraries
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    int position = getAdapterPosition();
                    Itinerary deletedItin = itins.get(position);
                    itins.remove(position);
                    notifyItemRemoved(position);

                    showUndoSnackbar(position, deletedItin);

                    return true;
                }
            });

            dropdown.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TransitionManager.beginDelayedTransition(cardView, new AutoTransition());
                    if (hiddenView.getVisibility() == View.GONE){
                        hiddenView.setVisibility(View.VISIBLE);
                        dropdown.setImageResource(R.drawable.ic_dropup);
                    } else {
                        hiddenView.setVisibility(View.GONE);
                        dropdown.setImageResource(R.drawable.ic_dropdown);
                    }
                }
            });
        }

        /**
         * Displays a snackbar that prompts the user to undo the deletion if they want to.
         * Pressing undo stops the itinerary from being deleted.
         * If the snackbar is dismissed naturally, delete the itinerary from the Parse backend.
         */
        public boolean showUndoSnackbar(int position, Itinerary deletedItin){
            Snackbar snackbar = Snackbar.make(((Activity) context).findViewById(R.id.content), "Itinerary deleted", Snackbar.LENGTH_SHORT);
            snackbar.setAction("Undo", v -> undoDelete(position, deletedItin));
            snackbar.show();

            snackbar.addCallback(new Snackbar.Callback() {
                @Override
                public void onDismissed(Snackbar transientBottomBar, int event) {
                super.onDismissed(transientBottomBar, event);
                // the snackbar disappears by itself; the user doesn't want to undo the deletion
                if (event != BaseTransientBottomBar.BaseCallback.DISMISS_EVENT_ACTION) {
                    List<String> itinIds = new ArrayList<>();
                    for (Itinerary i : itins){
                        itinIds.add(i.getObjectId());
                    }
                    currentUser = ParseUser.getCurrentUser();
                    currentUser.put(CommonValues.KEY_ITINERARY_USER, itinIds);

                    currentUser.saveInBackground(e -> {
                        if(e!=null){
                            Toast.makeText(context, "Unable to delete itinerary", Toast.LENGTH_SHORT).show();
                        } else {
                            ParseQuery<Itinerary> query = ParseQuery.getQuery("Itinerary");
                            query.getInBackground(deletedItin.getObjectId(), (object, e2) -> {
                                if (e2 == null) {
                                    object.deleteInBackground(e3 -> {
                                        if(e2!=null){
                                            Toast.makeText(context, "Unable to delete itinerary", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }else{
                                    Toast.makeText(context, "Unable to delete itinerary", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    });
                }

                }
            });

            return true;
        }

        public void undoDelete(int position, Itinerary deletedItin){
            itins.add(position, deletedItin);
            notifyItemInserted(position);
        }

        public void bind(Itinerary itin) {
            String title = itin.getTitle();
            Date start = itin.getStartDate();
            Date end = itin.getEndDate();

            String dates = "";
            if (start.equals(end)) {
                dates = itin.reformatDate(start);
            } else {
                dates = itin.reformatDate(start) + " - " + itin.reformatDate(end);
            }

            tvTitle.setText(title);
            tvDates.setText(dates);

            int numAuthors = itin.getAuthor().size();
            if (numAuthors == 2){
                tvPeople.setText("Shared with 1 other user");
            } else {
                tvPeople.setText("Shared with " + (numAuthors-1) + " other users");
            }

            setLocation(itin.getPlaceID());
        }

        public void setLocation(String placeId){
            PlacesClient placesClient = Places.createClient(context);
            final List<Place.Field> placeFields = Arrays.asList(Place.Field.ID, Place.Field.NAME);

            final FetchPlaceRequest request = FetchPlaceRequest.newInstance(placeId, placeFields);

            placesClient.fetchPlace(request).addOnSuccessListener((response) -> {
                Place place = response.getPlace();
                tvLocation.setText(place.getName());
            }).addOnFailureListener((exception) -> {
                if (exception instanceof ApiException) {
                    Log.e(TAG, "Place not found: " + exception.getMessage());
                }
                tvLocation.setText("ERROR");
            });
        }
    }
}
