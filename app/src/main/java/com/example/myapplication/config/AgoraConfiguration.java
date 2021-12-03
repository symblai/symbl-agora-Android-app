package com.example.myapplication.config;

import java.io.Serializable;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AgoraConfiguration implements Serializable {
    private String customerAppId;
    private String customerChannelName;
    private String tokenValue;
    private String meetingId;
}
