package com.example.myapplication.activities.result_tabs.tabs.trackers;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.models.results.Tracker;

import java.util.List;

public class TrackersTab extends Fragment {

    private final List<Tracker> trackers;

    public TrackersTab(List<Tracker> trackers) {
        this.trackers = trackers;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.trackers_tab, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.trackers);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(new TrackersViewAdapter(trackers));
        recyclerView.scrollToPosition(trackers.size() - 1);
        return view;
    }
}
