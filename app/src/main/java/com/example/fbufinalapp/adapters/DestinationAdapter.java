package com.example.fbufinalapp.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fbufinalapp.R;
import com.example.fbufinalapp.models.Destination;
import com.example.fbufinalapp.models.Itinerary;

import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class DestinationAdapter extends RecyclerView.Adapter<DestinationAdapter.ViewHolder> {
    private Context context;
    private List<Destination> destinations;
    private static final String TAG = "ItineraryAdapter";

    public DestinationAdapter(Context context, List<Destination> destinations){
        this.context = context;
        this.destinations = destinations;
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

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName;
        TextView tvTime;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvName = itemView.findViewById(R.id.tvName);
            tvTime = itemView.findViewById(R.id.tvTime);
        }

        public void bind(Destination dest) {
            String name = dest.getName();
            Log.i("DestinationAdapter", name);

            if (dest.getIsDay()) {
                tvTime.setVisibility(View.GONE);
                tvName.setTextSize(22);
            } else {
                LocalTime time = dest.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalTime();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("hh:mm a");

                tvTime.setText(formatter.format(time));
                tvName.setText(name);
            }

        }
    }

}
