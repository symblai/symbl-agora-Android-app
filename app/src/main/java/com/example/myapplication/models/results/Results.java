package com.example.myapplication.models.results;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import lombok.Getter;

@Getter
public class Results implements Serializable {

    private final List<Transcript> transcripts;
    private final List<Insight> insights;
    private final List<Topic> topics;
    private final List<Tracker> trackers;

    public Results() {
        this.transcripts = new ArrayList<>();
        this.insights = new ArrayList<>();
        this.topics = new ArrayList<>();
        this.trackers = new ArrayList<>();
    }

    public void add(Transcript transcript) {
        transcripts.add(transcript);
    }

    public void add(Insight insight) {
        for (Insight i : insights) {
            if (insight.getType().equals("TestId") && i.getId().equals(insight.getId())) {
                return;
            }
        }
        insights.add(insight);
    }

    public void add(Topic topic) {
        for (Topic t : topics) {
            if (t.getId().equals(topic.getId())) {
                return;
            }
        }
        topics.add(topic);
    }

    public void add(Tracker tracker) {
        trackers.add(tracker);
    }
}
