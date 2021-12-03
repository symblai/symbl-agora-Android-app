package com.example.myapplication.services;

import android.util.Log;

import com.example.myapplication.config.SymblConfiguration;
import com.example.myapplication.entity.TrackerEntity;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.agora.extension.symblai.model.request.ApiConfig;
import io.agora.extension.symblai.model.request.RealtimeAPIConfig;
import io.agora.extension.symblai.model.request.RealtimeStartRequest;
import io.agora.extension.symblai.model.request.Redaction;
import io.agora.extension.symblai.model.request.Speaker;
import io.agora.extension.symblai.model.request.SpeechRecognition;
import io.agora.extension.symblai.model.request.SymblPluginConfig;
import io.agora.extension.symblai.model.request.Tracker;

public class SymblService {

    private static final String TAG = "Symbl Service : ";
    private final SymblConfiguration configuration;

    public SymblService(SymblConfiguration configuration) {
        this.configuration = configuration;
    }

    public JSONObject getSymblPluginConfigs(String customerChannelName) {
        JSONObject pluginParams = new JSONObject();
        try {
            pluginParams.put("secret", configuration.getAppSecret());
            pluginParams.put("appKey", configuration.getAppId());
            pluginParams.put("meetingId", configuration.getUniqueMeetingId());
            pluginParams.put("userId", configuration.getMeetingUserId());
            pluginParams.put("name", configuration.getMeetingUserName());
            pluginParams.put("languageCode", configuration.getLanguageCode());
            pluginParams.put("inputRequest", new Gson().toJson(getSymblPluginConfig()));
        } catch (Exception ex) {
            Log.e(TAG, "ERROR while setting Symbl extension configuration");
        }
        return pluginParams;
    }

    private SymblPluginConfig getSymblPluginConfig() {
        SymblPluginConfig symblPluginConfig = new SymblPluginConfig();
        symblPluginConfig.setApiConfig(getApiConfig());
        symblPluginConfig.setRealtimeStartRequest(getRealTimeStartRequest());
        return symblPluginConfig;
    }

    private ApiConfig getApiConfig() {
        ApiConfig apiConfig = new ApiConfig();
        apiConfig.setAppId(configuration.getAppId());
        apiConfig.setAppSecret(configuration.getAppSecret());
        apiConfig.setTokenApi(configuration.getTokenApiUrl());
        apiConfig.setSymblPlatformUrl(configuration.getPlatformUrl());
        return apiConfig;
    }


    private RealtimeStartRequest getRealTimeStartRequest() {
        RealtimeStartRequest realtimeStartRequest = new RealtimeStartRequest();
        realtimeStartRequest.setSpeaker(getSpeaker());

        RealtimeAPIConfig realTimeApiConfig = getRealTimeApiConfig(getSpeechRecognition(), getRedaction());
        realtimeStartRequest.setConfig(realTimeApiConfig);

        realtimeStartRequest.setTrackers(getTrackers());

        realtimeStartRequest.setType("start_request");
        realtimeStartRequest.setId(configuration.getUniqueMeetingId());
        realtimeStartRequest.setSentiments(true);
        realtimeStartRequest.setInsightTypes(configuration.getInsightTypes());
        return realtimeStartRequest;
    }

    private RealtimeAPIConfig getRealTimeApiConfig(SpeechRecognition speechRecognition, Redaction redaction) {
        RealtimeAPIConfig realtimeAPIConfig = new RealtimeAPIConfig();
        realtimeAPIConfig.setConfidenceThreshold(Double.parseDouble(configuration.getConfidenceThreshold()));
        realtimeAPIConfig.setLanguageCode(configuration.getLanguageCode());
        realtimeAPIConfig.setSpeechRecognition(speechRecognition);
        realtimeAPIConfig.setRedaction(redaction);
        return realtimeAPIConfig;
    }

    private Redaction getRedaction() {
        Redaction redaction = new Redaction();           // Set the redaction content values
        redaction.setIdentifyContent(configuration.isRedactionEnabled());
        redaction.setRedactContent(configuration.isRedactionEnabled());
        redaction.setRedactionString(configuration.getRedactionString());
        return redaction;
    }

    private SpeechRecognition getSpeechRecognition() {
        SpeechRecognition speechRecognition = new SpeechRecognition();      // Set the meeting encoding and speaker sample rate hertz
        speechRecognition.setEncoding(configuration.getMeetingEncoding());
        speechRecognition.setSampleRateHertz(Double.parseDouble(configuration.getMeetingSampleRate()));
        return speechRecognition;
    }

    private Speaker getSpeaker() {
        Speaker speaker = new Speaker();
        speaker.setUserId(configuration.getMeetingUserId());
        speaker.setName(configuration.getMeetingUserName());
        return speaker;
    }

    private List<Tracker> getTrackers() {
        List<Tracker> trackers = new ArrayList<>();
        for (TrackerEntity trackerEntity : configuration.getTrackers()) {
            Tracker tracker = new Tracker();
            tracker.setName(trackerEntity.getName());
            tracker.setVocabulary(trackerEntity.getVocabulary());
            trackers.add(tracker);
        }
        return trackers;
    }
}
