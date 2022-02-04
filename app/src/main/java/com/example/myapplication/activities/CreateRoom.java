package com.example.myapplication.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;
import com.example.myapplication.config.AgoraConfiguration;
import com.example.myapplication.config.SymblConfiguration;
import com.example.myapplication.constants.InsightType;
import com.example.myapplication.entity.TrackerEntity;
import com.example.myapplication.models.ApplicationPreferences;
import com.example.myapplication.services.ValidationService;
import com.example.myapplication.utils.AppUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CreateRoom extends AppCompatActivity {

    private EditText roomName;
    private CheckBox actionItems;
    private CheckBox questions;
    private CheckBox followUps;
    private CheckBox trackers;
    private CheckBox redaction;

    private ValidationService validationService;
    private ApplicationPreferences appPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_room);

        appPreferences = AppUtils.getAppPreferences(CreateRoom.this);
        String passedRoomName = getIntent().getExtras().getString("roomName");
        validationService = new ValidationService();
        roomName = findViewById(R.id.roomName);
        roomName.setText(passedRoomName);

        actionItems = findViewById(R.id.action_items_checkbox);
        questions = findViewById(R.id.questions_checkbox);
        followUps = findViewById(R.id.follow_up_checkbox);
        trackers = findViewById(R.id.trackers_checkbox);
        redaction = findViewById(R.id.redaction_checkbox);

        Button createRoomButton = findViewById(R.id.createRoomButton);
        createRoomButton.setOnClickListener(this::onCreateButtonClick);
    }

    private void onCreateButtonClick(View view) {
        if (!validationService.isValidRoom(roomName.getText().toString())) {
            roomName.setError("Minimum 8 characters required");
            return;
        }

        if (!validationService.isUserDetailsValid(appPreferences)) {
            Toast.makeText(CreateRoom.this, "Please update your app preferences before proceeding.", Toast.LENGTH_SHORT).show();
            return;
        }
        AppUtils.hideKeyboard(view);
        Intent intent = new Intent(CreateRoom.this, VideoCallingActivity.class);
        intent.putExtra("roomName", roomName.getText().toString());
        intent.putExtra("symblConfiguration", getSymblConfiguration());
        intent.putExtra("agoraConfiguration", getAgoraConfiguration());
        intent.putExtra("isTrackersEnabled", trackers.isChecked());
        startActivity(intent);
        finish();
    }

    private SymblConfiguration getSymblConfiguration() {
        return SymblConfiguration.builder()
                .appId(getString(R.string.symbl_app_id))
                .appSecret(getString(R.string.symbl_app_secret))
                .confidenceThreshold(getString(R.string.symbl_confidence_threshold))
                .languageCode(getString(R.string.symbl_meeting_language_code))
                .meetingEncoding(getString(R.string.symbl_meeting_encoding))
                .meetingSampleRate(getString(R.string.symbl_meeting_sampleRateHertz))
                .platformUrl(getString(R.string.symbl_platform_url))
                .tokenApiUrl(getString(R.string.symbl_token_api))
                .meetingUserId(appPreferences.getEmailId())
                .meetingUserName(appPreferences.getUsername())
                .uniqueMeetingId(roomName.getText().toString())  // TODO :- check if it is mandatory to keep this same in both Symbl + Agora Configuration
                .redactionEnabled(redaction.isChecked())
                .redactionString(appPreferences.getRedactionString())
                .trackers(getTrackers())
                .insightTypes(getInsightTypes())
                .build();
    }

    private List<TrackerEntity> getTrackers() {
        return trackers.isChecked() ? appPreferences.getTrackers() : Collections.emptyList();
    }

    private List<String> getInsightTypes() {
        List<String> insightTypes = new ArrayList<>();
        if (actionItems.isChecked()) {
            insightTypes.add(InsightType.ACTION_ITEM.toString());
        }
        if (questions.isChecked()) {
            insightTypes.add(InsightType.QUESTION.toString());
        }
        if (followUps.isChecked()) {
            insightTypes.add(InsightType.FOLLOW_UP.toString());
        }
        return insightTypes;
    }

    private AgoraConfiguration getAgoraConfiguration() {
        return AgoraConfiguration.builder()
                .customerAppId(getString(R.string.agora_customer_app_id))
                .customerChannelName(roomName.getText().toString())
                .meetingId(roomName.getText().toString())
                .tokenValue(getString(R.string.agora_token_value))
                .build();
    }
}