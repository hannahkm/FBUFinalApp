package com.example.fbufinalapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fbufinalapp.R;
import com.example.fbufinalapp.models.Destination;
import com.example.fbufinalapp.models.Itinerary;

import java.util.Date;
import java.util.List;

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
            tvName.setText(name);

            if (dest.getIsDay()) {
                tvTime.setVisibility(View.GONE);
                tvName.setTextSize(22);
            } else {
                String time = dest.reformatTime(dest.getTime());
                tvTime.setText(time);
            }

        }
    }

}
