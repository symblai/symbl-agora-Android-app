package com.example.myapplication.constants;

import androidx.annotation.NonNull;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
public enum SymblResultType {
    RECOGNITION_RESULT("recognition_result"),
    TOPIC_MESSAGE("topic_message"),
    TOPIC_RESPONSE("topic_response"),
    INSIGHT_RESPONSE("insight_response"),
    TRACKER_RESPONSE("tracker_response");

    private String value;

    public static SymblResultType fromValue(String value) {
        for (SymblResultType type : values()) {
            if (type.toString().equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException(String.format("Result type %s is not configured", value));
    }

    @NonNull
    @Override
    public String toString() {
        return value;
    }
}
