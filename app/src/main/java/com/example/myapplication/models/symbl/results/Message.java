package com.example.myapplication.models.symbl.results;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Message {
    private String type;
    private boolean isFinal;
    private Punctuated punctuated;
    private User user;
}
