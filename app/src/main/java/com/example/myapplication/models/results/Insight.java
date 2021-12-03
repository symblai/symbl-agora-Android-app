package com.example.myapplication.models.results;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Insight implements ResultItem {
    private String id;
    private String data;
    private String type;
    private String assignee;
    private Date timestamp;

    public Insight(String id, String data, String type, String assignee) {
        this.id = id;
        this.data = data;
        this.type = type;
        this.assignee = assignee;
        this.timestamp = new Date();
    }
}
