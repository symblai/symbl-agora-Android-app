package com.example.myapplication.models.results;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Tracker implements ResultItem {
    private String id;
    private String name;
    private String data;
    private String offset;
    private Date timestamp;

    public Tracker(String id, String name, String data, String offset) {
        this.id = id;
        this.name = name;
        this.data = data;
        this.offset = offset;
        this.timestamp = new Date();
    }
}
