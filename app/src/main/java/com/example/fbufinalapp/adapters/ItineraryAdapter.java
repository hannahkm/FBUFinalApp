package com.example.fbufinalapp.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fbufinalapp.R;
import com.example.fbufinalapp.models.Itinerary;
import com.google.android.gms.common.api.ApiException;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.PlacesClient;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class ItineraryAdapter extends RecyclerView.Adapter<ItineraryAdapter.ViewHolder>{
    private Context context;
    private List<Itinerary> itins;
    private static final String TAG = "ItineraryAdapter";

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

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle;
        TextView tvDates;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvDates = itemView.findViewById(R.id.tvDates);
        }

        public void bind(Itinerary itin) {
            String title = itin.getTitle();
            Date start = itin.getStartDate();
            Date end = itin.getEndDate();

            String dates = "";
            if (start == null && end == null) {
                dates = "Date undecided";
            } else if (start != null && end == null) {
                dates = "Starting " + itin.reformatDate(start);
            } else if (start == null && end != null){
                dates = "Ending " + itin.reformatDate(end);
            } else {
                dates = itin.reformatDate(start) + " - " + itin.reformatDate(end);
            }

            tvTitle.setText(title);
            tvDates.setText(dates);
        }
    }
}
