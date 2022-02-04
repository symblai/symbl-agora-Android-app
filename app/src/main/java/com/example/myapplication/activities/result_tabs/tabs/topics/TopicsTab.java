package com.example.myapplication.activities.result_tabs.tabs.topics;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.models.results.Topic;

import java.util.List;

public class TopicsTab extends Fragment {

    private final List<Topic> topics;

    public TopicsTab(List<Topic> topics) {
        this.topics = topics;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.topics_tab, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.topics);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(new TopicsViewAdapter(topics));
        recyclerView.scrollToPosition(topics.size() - 1);
        return view;
    }
}
