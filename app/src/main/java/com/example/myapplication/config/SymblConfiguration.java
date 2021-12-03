package com.example.myapplication.config;

import com.example.myapplication.entity.TrackerEntity;

import java.io.Serializable;
import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SymblConfiguration implements Serializable {
    private String appId;
    private String appSecret;
    private String tokenApiUrl;
    private String platformUrl;
    private String uniqueMeetingId;
    private String confidenceThreshold;
    private String languageCode;
    private String meetingEncoding;
    private String meetingSampleRate;
    private String meetingUserId;
    private String meetingUserName;
    private List<TrackerEntity> trackers;
    private boolean redactionEnabled;
    private String redactionString;
    private List<String> insightTypes;
}
