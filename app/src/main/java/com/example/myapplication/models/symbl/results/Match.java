package com.example.myapplication.models.symbl.results;


import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Match {
    private String type;
    private String value;
    private List<MessageReference> messageRefs;
}
