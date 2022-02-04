package com.example.myapplication.models.results;

import com.example.myapplication.constants.UserType;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Transcript implements ResultItem {
    private String username;
    private UserType userType;
    private String data;
    private Date date;

    public Transcript(String username, UserType userType, String data) {
        this.username = username;
        this.userType = userType;
        this.data = data;
        this.date = new Date();
    }
}
