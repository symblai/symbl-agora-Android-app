package com.example.myapplication.activities.result_tabs.tabs.transcripts;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;

import lombok.Getter;

@Getter
public class TranscriptsViewHolder extends RecyclerView.ViewHolder {

    private final TextView username;
    private final TextView datum;
    private final TextView timestamp;

    public TranscriptsViewHolder(@NonNull View itemView) {
        super(itemView);
        username = itemView.findViewById(R.id.username);
        datum = itemView.findViewById(R.id.datum);
        timestamp = itemView.findViewById(R.id.timestamp);
    }
}
