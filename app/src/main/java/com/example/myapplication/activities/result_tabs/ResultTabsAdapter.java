package com.example.myapplication.activities.result_tabs;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.myapplication.activities.result_tabs.tabs.insights.InsightsTab;
import com.example.myapplication.activities.result_tabs.tabs.topics.TopicsTab;
import com.example.myapplication.activities.result_tabs.tabs.trackers.TrackersTab;
import com.example.myapplication.activities.result_tabs.tabs.transcripts.TranscriptTab;
import com.example.myapplication.models.results.Results;

import java.util.ArrayList;
import java.util.List;

public class ResultTabsAdapter extends FragmentStateAdapter {

    private final int totalTabs;
    private final List<Fragment> fragments;

    public ResultTabsAdapter(FragmentActivity fragmentActivity, int totalTabs, Results results, boolean isTrackersEnabled) {
        super(fragmentActivity);
        this.totalTabs = totalTabs;
        this.fragments = getFragments(results, isTrackersEnabled);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position >= fragments.size()) {
            throw new IllegalArgumentException("Invalid position :- " + position);
        }
        return fragments.get(position);
    }

    @Override
    public int getItemCount() {
        return totalTabs;
    }

    @NonNull
    private List<Fragment> getFragments(Results results, boolean isTrackersEnabled) {
        List<Fragment> fragments = new ArrayList<>();
        fragments.add(new TranscriptTab(results.getTranscripts()));
        fragments.add(new InsightsTab(results.getInsights()));
        fragments.add(new TopicsTab(results.getTopics()));
        if (isTrackersEnabled) {
            fragments.add(new TrackersTab(results.getTrackers()));
        }
        return fragments;
    }
}
