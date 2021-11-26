package com.example.myapplication.activities.result_tabs.tabs.topics;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;

import lombok.Getter;

@Getter
public class TopicsViewHolder extends RecyclerView.ViewHolder {

    private final TextView datum;
    private final TextView timestamp;

    public TopicsViewHolder(@NonNull View itemView) {
        super(itemView);
        datum = itemView.findViewById(R.id.datum);
        timestamp = itemView.findViewById(R.id.timestamp);
    }
}
