package com.example.fbufinalapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fbufinalapp.R;
import com.example.fbufinalapp.models.Itinerary;

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

    // Clean all elements of the recycler
    public void clear() {
        itins.clear();
        notifyDataSetChanged();
    }

    public List<Itinerary> getItems() {
        return itins;
    }

    public void setItems(List<Itinerary> newItins) {
        itins = newItins;
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
