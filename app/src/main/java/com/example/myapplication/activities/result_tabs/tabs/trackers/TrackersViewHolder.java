package com.example.myapplication.activities.result_tabs.tabs.trackers;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;

import lombok.Getter;

@Getter
public class TrackersViewHolder extends RecyclerView.ViewHolder {

    private final TextView trackerDetails;
    private final TextView name;
    private final TextView offset;
    private final TextView timestamp;

    public TrackersViewHolder(@NonNull View itemView) {
        super(itemView);
        trackerDetails = itemView.findViewById(R.id.tracker_details);
        name = itemView.findViewById(R.id.trackerName);
        offset = itemView.findViewById(R.id.offset);
        timestamp = itemView.findViewById(R.id.timestamp);
    }
}
