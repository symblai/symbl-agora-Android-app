package com.example.myapplication.models.symbl.results;

import java.util.List;

import io.agora.extension.symblai.model.response.actionitem.Insight;
import io.agora.extension.symblai.model.response.topic.Topic;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SymblResult {
    private String type;
    private Message message;
    private List<Topic> topics;
    private List<Insight> insights;
    private List<TrackerDetails> trackers;
}

