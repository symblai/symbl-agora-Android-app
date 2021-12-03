package com.example.myapplication.models;

import com.example.myapplication.entity.TrackerEntity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApplicationPreferences implements Serializable {
    private String profilePicColor;
    private String username;
    private String emailId;
    private List<TrackerEntity> trackers;
    private String redactionString;

    public ApplicationPreferences() {
        trackers = new ArrayList<>();
    }
}
