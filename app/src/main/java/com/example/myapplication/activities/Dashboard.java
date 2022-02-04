package com.example.myapplication.activities;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;
import com.example.myapplication.activities.app_preferences.AppPreferences;
import com.example.myapplication.config.AgoraConfiguration;
import com.example.myapplication.config.SymblConfiguration;
import com.example.myapplication.models.ApplicationPreferences;
import com.example.myapplication.services.ValidationService;
import com.example.myapplication.utils.AppUtils;

import java.util.Collections;
import java.util.Objects;

public class Dashboard extends AppCompatActivity {

    private EditText roomName;
    private ValidationService validationService;
    private ApplicationPreferences appPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        showCustomizedActionBar();

        appPreferences = AppUtils.getAppPreferences(Dashboard.this);
        validationService = new ValidationService();
        roomName = findViewById(R.id.roomName);
        Button joinRoomButton = findViewById(R.id.joinRoomButton);
        TextView createRoomText = findViewById(R.id.createRoomText);

        joinRoomButton.setOnClickListener(this::onClickOfJoinRoomButton);
        createRoomText.setOnClickListener(this::onClickOfCreateRoomButton);
    }

    private void showCustomizedActionBar() {
        Objects.requireNonNull(getSupportActionBar()).setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.app_title_toolbar);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.app_header_color)));
    }

    @Override
    protected void onResume() {
        super.onResume();
        appPreferences = AppUtils.getAppPreferences(Dashboard.this);
    }

    private void onClickOfJoinRoomButton(View view) {
        if (!validationService.isValidRoom(roomName.getText().toString())) {
            roomName.setError("Minimum 8 characters required");
            return;
        }

        if (!validationService.isUserDetailsValid(appPreferences)) {
            Toast.makeText(Dashboard.this, "Please update your app preferences before proceeding.", Toast.LENGTH_SHORT).show();
            return;
        }
        AppUtils.hideKeyboard(view);
        Intent intent = new Intent(Dashboard.this, VideoCallingActivity.class);
        intent.putExtra("roomName", roomName.getText().toString());
        intent.putExtra("symblConfiguration", getSymblConfiguration());
        intent.putExtra("agoraConfiguration", getAgoraConfiguration());
        startActivity(intent);
    }


    private void onClickOfCreateRoomButton(View view) {
        Intent intent = new Intent(Dashboard.this, CreateRoom.class);
        intent.putExtra("roomName", roomName.getText().toString());
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.app_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.preferences) {
            Intent intent = new Intent(Dashboard.this, AppPreferences.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
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
                .redactionEnabled(false)
                .redactionString(appPreferences.getRedactionString())
                .trackers(Collections.emptyList())
                .insightTypes(Collections.emptyList())
                .build();
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