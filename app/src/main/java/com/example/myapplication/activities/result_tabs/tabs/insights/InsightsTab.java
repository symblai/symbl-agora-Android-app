package com.example.myapplication.activities.result_tabs.tabs.insights;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.models.results.Insight;

import java.util.List;

public class InsightsTab extends Fragment {

    private List<Insight> insights;

    public InsightsTab(List<Insight> insights) {
        this.insights = insights;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.insights_tab, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.insights);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(new InsightsViewAdapter(insights));
        recyclerView.scrollToPosition(insights.size() - 1);
        return view;
    }
}
