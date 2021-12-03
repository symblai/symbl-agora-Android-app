package com.example.myapplication.models.results;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Topic implements ResultItem {
    private String id;
    private String data;
    private Date timestamp;

    public Topic(String id, String data) {
        this.id = id;
        this.data = data;
        this.timestamp = new Date();
    }
}
