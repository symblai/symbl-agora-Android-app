package com.example.myapplication.entity;

import java.io.Serializable;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TrackerEntity implements Serializable {
    private String name;
    private boolean isSelected;
    private List<String> vocabulary;
}
