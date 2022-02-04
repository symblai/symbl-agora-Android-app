package com.example.myapplication.activities.result_tabs.tabs.transcripts;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.constants.UserType;
import com.example.myapplication.models.results.Transcript;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class TranscriptsViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final List<Transcript> data;

    public TranscriptsViewAdapter(List<Transcript> data) {
        this.data = data;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int transcript_item = viewType == UserType.SENDER.getValue() ? R.layout.transcript_item_sent : R.layout.transcript_item_recieved;
        View view = LayoutInflater.from(parent.getContext()).inflate(transcript_item, parent, false);
        return new TranscriptsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        TranscriptsViewHolder transcriptsViewHolder = (TranscriptsViewHolder) holder;
        transcriptsViewHolder.getUsername().setText(data.get(position).getUsername());
        transcriptsViewHolder.getDatum().setText(data.get(position).getData());
        transcriptsViewHolder.getTimestamp().setText(getParsedTimestamp(data.get(position).getDate()));
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    @Override
    public int getItemViewType(int position) {
        return data.get(position).getUserType().getValue();
    }

    private String getParsedTimestamp(Date date) {
        return new SimpleDateFormat("hh:mm aa").format(date);
    }
}
