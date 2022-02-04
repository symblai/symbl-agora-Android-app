package com.example.myapplication.activities.result_tabs.tabs.transcripts;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.models.results.Transcript;

import java.util.List;

public class TranscriptTab extends Fragment {

    private List<Transcript> transcripts;

    public TranscriptTab(List<Transcript> transcripts) {
        this.transcripts = transcripts;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.transcript_tab, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.transcripts);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(new TranscriptsViewAdapter(transcripts));
        recyclerView.scrollToPosition(transcripts.size() - 1);
        return view;
    }
}
