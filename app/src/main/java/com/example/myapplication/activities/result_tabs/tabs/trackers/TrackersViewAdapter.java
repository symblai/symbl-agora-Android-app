package com.example.myapplication.activities.result_tabs.tabs.trackers;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.models.results.Tracker;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class TrackersViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final List<Tracker> data;

    public TrackersViewAdapter(List<Tracker> data) {
        this.data = data;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.tracker_item, parent, false);
        return new TrackersViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        TrackersViewHolder trackersViewHolder = (TrackersViewHolder) holder;
        Tracker tracker = data.get(position);
        trackersViewHolder.getTrackerDetails().setText(tracker.getData());
        trackersViewHolder.getName().setText(tracker.getName());
        trackersViewHolder.getOffset().setText(tracker.getOffset());
        trackersViewHolder.getTimestamp().setText(getParsedTimestamp(tracker.getTimestamp()));
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    private String getParsedTimestamp(Date date) {
        return new SimpleDateFormat("hh:mm aa").format(date);
    }
}
