package com.example.myapplication.services;

import com.example.myapplication.models.ApplicationPreferences;

public class ValidationService {

    public boolean isValidRoom(String roomName) {
        return roomName.trim().length() >= 8;
    }

    public boolean isUserDetailsValid(ApplicationPreferences appPreferences) {
        return appPreferences.getUsername() != null &&
                appPreferences.getEmailId() != null &&
                !appPreferences.getEmailId().isEmpty() &&
                !appPreferences.getUsername().isEmpty();
    }

}
