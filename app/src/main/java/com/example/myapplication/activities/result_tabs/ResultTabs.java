package com.example.myapplication.activities.result_tabs;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.example.myapplication.R;
import com.example.myapplication.models.results.Results;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import lombok.NonNull;

public class ResultTabs extends AppCompatActivity {

    private static final String TRANSCRIPTS = "Transcripts";
    private static final String INSIGHTS = "Insights";
    private static final String TOPICS = "Topics";
    private static final String TRACKERS = "Trackers";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result_tabs);

        showCustomizedActionBar();

        ViewPager2 viewPager = findViewById(R.id.resultsViewPager);
        TabLayout tabLayout = findViewById(R.id.resultTabs);
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        Results results = (Results) getIntent().getSerializableExtra("results");
        boolean isTrackersEnabled = getIntent().getBooleanExtra("isTrackersEnabled", false);

        List<String> tabHeaders = getTabHeaders(isTrackersEnabled);
        viewPager.setAdapter(new ResultTabsAdapter(this, tabHeaders.size(), results, isTrackersEnabled));
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> tab.setText(tabHeaders.get(position))).attach();
    }

    private void showCustomizedActionBar() {
        Objects.requireNonNull(getSupportActionBar()).setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.app_title_toolbar);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.app_header_color)));
        getSupportActionBar().setElevation(0);
    }

    @NonNull
    private List<String> getTabHeaders(boolean isTrackersEnabled) {
        List<String> tabHeaders = new ArrayList<>();
        tabHeaders.add(TRANSCRIPTS);
        tabHeaders.add(INSIGHTS);
        tabHeaders.add(TOPICS);
        if (isTrackersEnabled) {
            tabHeaders.add(TRACKERS);
        }
        return tabHeaders;
    }
}