package com.example.myapplication.models.symbl.results;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageReference {
    private String id;
    private String text;
    private String offset;
}
