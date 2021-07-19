package com.example.fbufinalapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fbufinalapp.DetailedLocationActivity;
import com.example.fbufinalapp.R;

import java.util.List;

public class LocationsAdapter extends RecyclerView.Adapter<LocationsAdapter.ViewHolder>{

    private Context context;
    private List<String> places;
    private static final String TAG = "LocationsAdapter";

    public LocationsAdapter(Context context, List<String> places){
        this.context = context;
        this.places = places;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.items_places, parent, false);
        return new LocationsAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LocationsAdapter.ViewHolder holder, int position) {
        String place = places.get(position);
        holder.bind(place);
    }

    @Override
    public int getItemCount() {
        return places.size();
    }

    public void clear() {
        places.clear();
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvPrimary;
        TextView tvSecondary;
        String placeId;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvPrimary = itemView.findViewById(R.id.tvPrimary);
            tvSecondary = itemView.findViewById(R.id.tvSecondary);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(context, DetailedLocationActivity.class);
                    i.putExtra("placeID", placeId);
                    i.putExtra("name", tvPrimary.getText());
                    context.startActivity(i);
                }
            });
        }

        public void bind(String place) {
            int lastIndex = place.lastIndexOf("\n");
            String description = place.substring(0, lastIndex);
            placeId = place.substring(lastIndex+1);

            lastIndex = place.indexOf("\n");
            String primary = description.substring(0, lastIndex);
            String secondary = description.substring(lastIndex+1);

            tvPrimary.setText(primary);
            tvSecondary.setText(secondary);
        }


    }
}
