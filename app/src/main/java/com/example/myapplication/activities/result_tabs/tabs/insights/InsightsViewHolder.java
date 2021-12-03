package com.example.myapplication.activities.result_tabs.tabs.insights;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;

import lombok.Getter;

@Getter
public class InsightsViewHolder extends RecyclerView.ViewHolder {

    private final TextView datum;
    private final TextView type;
    private final TextView assignee;
    private final TextView timestamp;

    public InsightsViewHolder(@NonNull View itemView) {
        super(itemView);
        datum = itemView.findViewById(R.id.datum);
        type = itemView.findViewById(R.id.insightType);
        assignee = itemView.findViewById(R.id.assignee);
        timestamp = itemView.findViewById(R.id.timestamp);
    }
}
