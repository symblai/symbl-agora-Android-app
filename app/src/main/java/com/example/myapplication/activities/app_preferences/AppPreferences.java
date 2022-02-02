package com.example.myapplication.activities.app_preferences;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.entity.TrackerEntity;
import com.example.myapplication.models.ApplicationPreferences;
import com.example.myapplication.utils.AppUtils;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.regex.Pattern;

public class AppPreferences extends AppCompatActivity {
    private static final String EMAIL_REGEX = "^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$";
    private ApplicationPreferences appPreferences;
    private boolean[] selectedTrackers;
    private Gson gson;

    private TextView profilePic;
    private EditText usernameText;
    private EditText emailIdText;
    private EditText redactionText;
    private TextView trackersView;
    private int profilePicColor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_preferences);

        showCustomizedActionBar();

        gson = new Gson();
        appPreferences = getAppPreferences();
        selectedTrackers = getSelectedTrackersArray();

        profilePic = findViewById(R.id.profile_pic_view);
        trackersView = findViewById(R.id.trackers_text);
        trackersView.setOnClickListener(this::showMultiSelectTrackers);

        usernameText = findViewById(R.id.username_text);
        usernameText.addTextChangedListener(getUsernameWatcher());
        emailIdText = findViewById(R.id.email_text);
        redactionText = findViewById(R.id.redaction_text);

        Button savePreferences = findViewById(R.id.savePreferences);
        savePreferences.setOnClickListener(this::onClickOfSavePreferences);

        updateUIFields();
    }

    private void showCustomizedActionBar() {
        Objects.requireNonNull(getSupportActionBar()).setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.app_title_toolbar);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.app_header_color)));
    }

    @NonNull
    private TextWatcher getUsernameWatcher() {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence username, int start, int before, int count) {
                if (start == 0 && username.length() <= 1) {
                    updateProfilePic(username, null);
                }
            }

            @Override
            public void afterTextChanged(Editable username) {
            }
        };
    }

    private void updateUIFields() {
        usernameText.setText(appPreferences.getUsername());
        if (appPreferences.getUsername() != null && appPreferences.getUsername().length() > 0) {
            profilePic.setText(appPreferences.getUsername().subSequence(0, 1));
        }
        emailIdText.setText(appPreferences.getEmailId());
        redactionText.setText(appPreferences.getRedactionString());
        updateProfilePic(appPreferences.getUsername(), appPreferences.getProfilePicColor());
        updateAndDisplayTrackers();
    }

    private ApplicationPreferences getAppPreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.app_name), MODE_PRIVATE);
        String value = sharedPreferences.getString(getString(R.string.app_preferences), getString(R.string.empty_json));
        return gson.fromJson(value, ApplicationPreferences.class);
    }

    private void onClickOfSavePreferences(View view) {
        if (!isValidPreferences()) {
            return;
        }
        updateAppPreferences();
        saveAppPreferences();

        Toast.makeText(AppPreferences.this, "Saved Successfully", Toast.LENGTH_SHORT).show();
        finish();
    }

    private boolean isValidPreferences() {
        boolean isValidEmail = Pattern.compile(EMAIL_REGEX, Pattern.CASE_INSENSITIVE)
                .matcher(emailIdText.getText().toString())
                .find();

        if (!isValidEmail) {
            emailIdText.setError("Please enter a valid email");
            return false;
        }

        return true;
    }

    private void updateAppPreferences() {
        appPreferences.setUsername(usernameText.getText().toString());
        appPreferences.setEmailId(emailIdText.getText().toString());
        appPreferences.setRedactionString(redactionText.getText().toString());
        appPreferences.setProfilePicColor(String.valueOf(profilePicColor));
    }

    private void saveAppPreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.app_name), MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(getString(R.string.app_preferences), gson.toJson(appPreferences));
        editor.apply();
    }

    private void showMultiSelectTrackers(View view) {
        AlertDialog alertDialog = new MaterialAlertDialogBuilder(AppPreferences.this)
                .setTitle("Select Trackers")
                .setCancelable(false)
                .setMultiChoiceItems(getTrackerNames(), selectedTrackers, (dialog, itemIndex, isChecked) -> selectedTrackers[itemIndex] = isChecked)
                .setPositiveButton("OK", (dialog, itemIndex) -> updateAndDisplayTrackers())
                .setNegativeButton("Cancel", (dialog, itemIndex) -> dialog.dismiss())
                .setNeutralButton("Create", this::onClickOfNeutralDialogButton)
                .show();
    }

    private void updateAndDisplayTrackers() {
        StringBuilder stringBuilder = new StringBuilder();
        boolean anItemSelected = false;
        for (int i = 0; i < appPreferences.getTrackers().size(); i++) {
            appPreferences.getTrackers().get(i).setSelected(selectedTrackers[i]);
            if (appPreferences.getTrackers().get(i).isSelected()) {
                if (anItemSelected) {
                    stringBuilder.append(", ");
                }
                stringBuilder.append(appPreferences.getTrackers().get(i).getName());
                anItemSelected = true;
            }
        }

        if (!anItemSelected && !trackersView.getText().toString().equals(getString(R.string.trackers))) {
            trackersView.setText(getString(R.string.trackers));
        }
        if (anItemSelected) {
            trackersView.setText(stringBuilder.toString());
        }
    }

    private void onClickOfNeutralDialogButton(DialogInterface dialog, int itemIndex) {
        new MaterialAlertDialogBuilder(this)
                .setView(displayCreateTrackerScreen(dialog))
                .show();
    }

    @NonNull
    private View displayCreateTrackerScreen(DialogInterface dialog) {
        List<String> vocabulary = new ArrayList<>();
        View view = getLayoutInflater().inflate(R.layout.create_tracker_layout, findViewById(R.id.create_tracker_container));

        RecyclerView recyclerView = view.findViewById(R.id.vocabularyList);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(AppPreferences.this));
        recyclerView.setAdapter(new VocabularyViewAdapter(vocabulary));

        ImageView addVocabularyButton = view.findViewById(R.id.addVocabularyButton);
        EditText trackerName = view.findViewById(R.id.trackerName);
        EditText vocabularyText = view.findViewById(R.id.vocabularyText);
        addVocabularyButton.setOnClickListener(v -> onClickOfAddVocabularyButton(vocabulary, vocabularyText));

        Button createTrackerButton = view.findViewById(R.id.createTrackerButton);
        createTrackerButton.setOnClickListener(v -> onClickOfCreateTrackerButton(dialog, trackerName.getText().toString(), vocabulary));
        return view;
    }

    private void onClickOfAddVocabularyButton(List<String> vocabulary, EditText vocabularyText) {
        String value = vocabularyText.getText().toString();
        if (value.length() > 0 && !vocabulary.contains(value)) {
            vocabulary.add(value);
        }
        AppUtils.hideKeyboard(vocabularyText);
        vocabularyText.clearFocus();
        vocabularyText.getText().clear();
    }

    private void onClickOfCreateTrackerButton(DialogInterface dialog, String trackerName, List<String> vocabulary) {
        appPreferences.getTrackers().add(new TrackerEntity(trackerName, false, vocabulary));
        saveAppPreferences();
        dialog.dismiss();
        reloadActivity();
    }

    private void reloadActivity() {
        finish();
        overridePendingTransition(0, 0);
        startActivity(getIntent());
        overridePendingTransition(0, 0);
    }

    private String[] getTrackerNames() {
        String[] trackerNames = new String[appPreferences.getTrackers().size()];

        for (int i = 0; i < appPreferences.getTrackers().size(); i++) {
            trackerNames[i] = appPreferences.getTrackers().get(i).getName();
        }

        return trackerNames;
    }

    private boolean[] getSelectedTrackersArray() {
        boolean[] isSelected = new boolean[appPreferences.getTrackers().size()];

        for (int i = 0; i < appPreferences.getTrackers().size(); i++) {
            isSelected[i] = appPreferences.getTrackers().get(i).isSelected();
        }

        return isSelected;
    }

    private void updateProfilePic(CharSequence username, String picBackgroundColor) {
        if (username == null || username.length() < 1) {
            profilePic.setText("");
            profilePic.setBackground(ContextCompat.getDrawable(AppPreferences.this, R.drawable.default_profile_pic));
            return;
        }

        CharSequence usernameFirstCharacter = username.subSequence(0, 1);
        profilePic.setText(usernameFirstCharacter);

        Random randomBackgroundColor = new Random();
        int color = Color.argb(255, randomBackgroundColor.nextInt(256), randomBackgroundColor.nextInt(256), randomBackgroundColor.nextInt(256));
        profilePicColor = picBackgroundColor != null ? Integer.parseInt(picBackgroundColor) : color;

        GradientDrawable drawable = new GradientDrawable();
        drawable.setColor(profilePicColor);
        profilePic.setBackground(drawable);
    }
}