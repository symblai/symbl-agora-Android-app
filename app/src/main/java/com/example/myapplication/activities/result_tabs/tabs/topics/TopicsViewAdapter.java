package com.example.myapplication.activities.result_tabs.tabs.topics;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.models.results.Topic;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class TopicsViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final List<Topic> data;

    public TopicsViewAdapter(List<Topic> data) {
        this.data = data;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.topic_item, parent, false);
        return new TopicsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        TopicsViewHolder topicsViewHolder = (TopicsViewHolder) holder;
        topicsViewHolder.getDatum().setText(data.get(position).getData());
        topicsViewHolder.getTimestamp().setText(getParsedTimestamp(data.get(position).getTimestamp()));
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    private String getParsedTimestamp(Date date) {
        return new SimpleDateFormat("hh:mm aa").format(date);
    }
}
