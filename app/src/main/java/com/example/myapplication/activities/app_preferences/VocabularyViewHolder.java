package com.example.myapplication.activities.app_preferences;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;

import lombok.Getter;

@Getter
public class VocabularyViewHolder extends RecyclerView.ViewHolder {

    private final TextView datum;

    public VocabularyViewHolder(@NonNull View itemView) {
        super(itemView);
        datum = itemView.findViewById(R.id.datum);
    }
}
