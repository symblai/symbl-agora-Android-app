package com.example.myapplication.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum UserType {
    SENDER(101),
    RECEIVER(102);

    private int value;

    public static UserType fromValue(int value) {
        for (UserType type : values()) {
            if (type.getValue() == value) {
                return type;
            }
        }
        throw new IllegalArgumentException(String.format("User type '%s' is not configured", value));
    }
}
