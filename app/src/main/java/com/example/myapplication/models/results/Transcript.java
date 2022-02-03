package com.example.myapplication.models.results;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Transcript implements ResultItem {
    private String username;
    private String data;
    private Date date;

    public Transcript(String username, String data) {
        this.username = username;
        this.data = data;
        this.date = new Date();
    }
}
