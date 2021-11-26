package com.example.myapplication.constants;

import androidx.annotation.NonNull;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
public enum InsightType {
    ACTION_ITEM("action_item"),
    QUESTION("question"),
    FOLLOW_UP("follow_up");

    private String value;

    @NonNull
    @Override
    public String toString() {
        return value;
    }
}
