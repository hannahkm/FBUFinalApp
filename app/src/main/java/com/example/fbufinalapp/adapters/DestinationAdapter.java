package com.example.fbufinalapp.adapters;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fbufinalapp.DetailedItineraryActivity;
import com.example.fbufinalapp.DetailedLocationActivity;
import com.example.fbufinalapp.EditDestinationActivity;
import com.example.fbufinalapp.R;
import com.example.fbufinalapp.models.Destination;
import com.example.fbufinalapp.models.Itinerary;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.parse.ParseQuery;

import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Adapter to build a recyclerview with destination items. Displays the destination name and time.
 */
public class DestinationAdapter extends RecyclerView.Adapter<DestinationAdapter.ViewHolder> {
    private Context context;
    private List<Destination> destinations;
    Itinerary currentItin;
    private static final String TAG = "ItineraryAdapter";

    public DestinationAdapter(Context context, List<Destination> destinations, Itinerary currentItin){
        this.context = context;
        this.destinations = destinations;
        this.currentItin = currentItin;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_destination, parent, false);
        return new DestinationAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DestinationAdapter.ViewHolder holder, int position) {
        Destination dest = destinations.get(position);
        holder.bind(dest);
    }

    @Override
    public int getItemCount() {
        return destinations.size();
    }

    public void clear() {
        destinations.clear();
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName;
        TextView tvTime;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvName = itemView.findViewById(R.id.tvName);
            tvTime = itemView.findViewById(R.id.tvTime);

            // user can either click to view details or click to edit destination
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Destination desti = destinations.get(getAdapterPosition());

                    if (DetailedItineraryActivity.getEditing() && !desti.getIsDay()) {
                        Intent i = new Intent(context, EditDestinationActivity.class);
                        i.putExtra("itinId", currentItin.getObjectId());
                        i.putExtra("placeId", desti.getPlaceID());
                        i.putExtra("editing", true);
                        i.putExtra("destinationId", desti.getObjectId());

                        context.startActivity(i, ActivityOptions.makeSceneTransitionAnimation((Activity) context).toBundle());
                    } else if (!DetailedItineraryActivity.getEditing() && !desti.getIsDay()) {
                        Intent i = new Intent(context, DetailedLocationActivity.class);
                        i.putExtra("placeID", desti.getPlaceID());
                        i.putExtra("name", tvName.getText());
                        context.startActivity(i);
                    }
                }
            });

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    int position = getAdapterPosition();
                    Destination deleted = destinations.get(position);

                    if (!deleted.getIsDay()) {
                        destinations.remove(position);
                        notifyItemRemoved(position);

                        showUndoSnackbar(position, deleted);
                    }

                    return true;
                }
            });
        }

        /**
         * Displays a snackbar that prompts the user to undo the deletion if they want to.
         * Pressing undo stops the destination from being deleted.
         * If the snackbar is dismissed naturally, delete the destination from the itinerary and from
         * the Parse backend.
         */
        public boolean showUndoSnackbar(int position, Destination deleted){
            Snackbar snackbar = Snackbar.make(((Activity) context).findViewById(R.id.content), "Destination deleted", Snackbar.LENGTH_SHORT);
            snackbar.setAction("Undo", v -> undoDelete(position, deleted));
            snackbar.show();

            snackbar.addCallback(new Snackbar.Callback() {
                @Override
                public void onDismissed(Snackbar transientBottomBar, int event) {
                super.onDismissed(transientBottomBar, event);

                if (event != BaseTransientBottomBar.BaseCallback.DISMISS_EVENT_ACTION){
                    // the snackbar disappears by itself; the user doesn't want to undo the deletion
                    List<String> destIds = new ArrayList<>();
                    for (Destination i : destinations){
                        destIds.add(i.getObjectId());
                    }
                    currentItin.put("destinations", destIds);

                    currentItin.saveInBackground(e -> {
                        if(e!=null){
                            Toast.makeText(context, "Unable to delete destination", Toast.LENGTH_SHORT).show();
                        } else {
                            ParseQuery<Destination> query = ParseQuery.getQuery("Destination");
                            query.getInBackground(deleted.getObjectId(), (object, e2) -> {
                                if (e2 == null) {
                                    object.deleteInBackground(e3 -> {
                                        if(e2!=null){
                                            Toast.makeText(context, "Unable to delete destination", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }else{
                                    Toast.makeText(context, "Unable to delete destination", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    });
                }
                }
            });

            return true;
        }

        public void undoDelete(int position, Destination deleted){
            destinations.add(position, deleted);
            notifyItemInserted(position);
        }

        public void bind(Destination dest) {
            String name = dest.getName();
            tvName.setText(name);

            if (dest.getIsDay()) {
                Log.i("DestinationAdapter", name);
                tvTime.setVisibility(View.GONE);
                tvName.setTextSize(22);
            } else {
                tvTime.setVisibility(View.VISIBLE);
                tvName.setTextSize(20);

                LocalTime time = dest.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalTime();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("hh:mm a");

                tvTime.setText(formatter.format(time));
            }

        }
    }

}
