package com.example.myapplication.activities.app_preferences;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;

import java.util.List;

public class VocabularyViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final List<String> data;

    public VocabularyViewAdapter(List<String> data) {
        this.data = data;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.vocabulary_item, parent, false);
        return new VocabularyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        VocabularyViewHolder vocabularyViewHolder = (VocabularyViewHolder) holder;
        vocabularyViewHolder.getDatum().setText(data.get(position));
    }

    @Override
    public int getItemCount() {
        return data.size();
    }
}
