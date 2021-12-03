package com.example.myapplication.activities.result_tabs.tabs.insights;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.models.results.Insight;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class InsightsViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final List<Insight> data;

    public InsightsViewAdapter(List<Insight> data) {
        this.data = data;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.insight_item, parent, false);
        return new InsightsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        InsightsViewHolder insightsViewHolder = (InsightsViewHolder) holder;
        insightsViewHolder.getDatum().setText(data.get(position).getData());
        insightsViewHolder.getType().setText(data.get(position).getType());
        insightsViewHolder.getAssignee().setText(data.get(position).getAssignee());
        insightsViewHolder.getTimestamp().setText(getParsedTimestamp(data.get(position).getTimestamp()));
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    private String getParsedTimestamp(Date date) {
        return new SimpleDateFormat("hh:mm aa").format(date);
    }
}
