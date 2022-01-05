# Symbl Agora Demo App


[![Websocket](https://img.shields.io/badge/symbl-websocket-brightgreen)](https://docs.symbl.ai/docs/streamingapi/overview/introduction)

Symbl's APIs empower developers to enable: 
- **Real-time** analysis of free-flowing discussions to automatically surface highly relevant summary discussion topics, contextual insights, suggestive action items, follow-ups, decisions, and questions.\
- **Voice APIs** that makes it easy to add AI-powered conversational intelligence to either [telephony][telephony] or [WebSocket][websocket] interfaces.
- **Conversation APIs** that provide a REST interface for managing and processing your conversation data.
- **Summary UI** with a fully customizable and editable reference experience that indexes a searchable transcript and shows generated actionable insights, topics, timecodes, and speaker information.

<hr />

## Enable Symbl's Extension for Agora Video Calls in Android

<hr />

 * [Introduction](#introduction)
 * [Pre-requisites](#pre-requisites)
 * [Setup and Deploy](#setupanddeploy)
 * [Dependencies](#dependencies)
 * [Conclusion](#conclusion)
 * [Community](#community)

## Introduction

The Android app is a sample application designed to demonstrate how you implement Symbl.ai's Conversation Intelligence APIs into an Android application with Agora.io.

### Pre-requisites

* Android Studio 3.3 or above
* Android device (e.g. Nexus 5X). A real device is recommended because some simulators have missing functionality or lack the performance necessary to run the sample. You will not be able to deploy the app to the Android Studio's emulator, since the emulator does not simulate access to hardware such as the camera or the microphone.
* Java 11
* Android API Level 29
* Symbl.ai account from [Symbl][signup]'s platform.
* Agora Account [Agora](https://sso.agora.io/en/v3/signup)

## Setup and Deploy
1. The first step is to navigate to Agora.io's [console][console] where you sign up for a free developer account. After signing up on the console, navigate to Symbl.ai's Extension in Agora.io's Extension [Marketplace](https://console.agora.io/marketplace). The activation process

2. The second step to getting setup is to [sign up][signup] on both platforms, [Symbl][signup]'s and [Agora](https://sso.agora.io/en/v3/signup)'s. 

3. The third step is to clone the repo. 

4. The fourth step is to download the Symbl.ai [Extension](https://cdn-agora-.symbl.ai/agora-symblai-filter-debug.aar). After downloading, add the Symbl Extension to your dependency. Update your `build.gradle`: 

```java
implementation 'com.squareup.okhttp3:okhttp:3.10.0'
implementation 'org.java-websocket:Java-WebSocket:1.5.1'
implementation 'ai.symbl:android.extension:0.0.2'
```

5. The firth step is to implement the interface `io.agora.rtc2.IMediaExtensionObserver`

```java
public class MainActivity extends AppCompatActivity implements io.agora.rtc2.IMediaExtensionObserver {}
```
6. The sixth step is to add the following method to set all the necessary information to initialize the Symbl configuration. You can find description for the parameters used in the table below:

```java
private void setSymblPluginConfigs(JSONObject pluginParams) throws JSONException {
   try {
       // Symbl main extension config objects
       SymblPluginConfig symblParams = new SymblPluginConfig();

       // Set the Symbl API Configuration
       ApiConfig apiConfig = new ApiConfig();
       apiConfig.setAppId("<symbl_app_id>");
       apiConfig.setAppSecret("<symbl_app_secret>");
       apiConfig.setTokenApi("https://api.symbl.ai/oauth2/token:generate");
       apiConfig.setSymblPlatformUrl("api-agora-1.symbl.ai");
       symblParams.setApiConfig(apiConfig);

       // Set the Symbl Confidence Level and Language Code
       RealtimeStartRequest realtimeFlowInitRequest = new RealtimeStartRequest();
       RealtimeAPIConfig realtimeAPIConfig = new RealtimeAPIConfig();
realtimeAPIConfig.setConfidenceThreshold(Double.parseDouble("<symbl_confidence_threshold>"));
realtimeAPIConfig.setLanguageCode("en-US");

       // Set the Speaker information
       Speaker speaker=new Speaker();
       speaker.setName("<symbl_meeting_user_Name>");
       speaker.setUserId("<symbl_meeting_UserId>");
       realtimeFlowInitRequest.setSpeaker(speaker);

       // Set the meeting encoding and speaker sample rate hertz
       SpeechRecognition speechRecognition = new SpeechRecognition();
       speechRecognition.setEncoding("<symbl_meeting_encoding>"));
speechRecognition.setSampleRateHertz(Double.parseDouble("<symbl_meeting_sampleRateHertz>"));
       realtimeAPIConfig.setSpeechRecognition(speechRecognition);

       // Set the redaction content values
       Redaction redaction = new Redaction();
       redaction.setIdentifyContent(true);
       redaction.setRedactContent(true);
       redaction.setRedactionString("*****");
       realtimeAPIConfig.setRedaction(redaction);

       // Set the Tracker (custom business intent) information
       realtimeFlowInitRequest.setConfig(realtimeAPIConfig);
       Tracker tracker1 = new Tracker();
       tracker1.setName("Budget");
       List<String> vocabulary = new ArrayList<>();
       vocabulary.add("budget");
       vocabulary.add("budget decision");
       tracker1.setVocabulary(vocabulary);
       List<Tracker> trackerList = new ArrayList<>();
       trackerList.add(tracker1);

       // Set the Symbl conversation parameters
       realtimeFlowInitRequest.setTrackers(trackerList);
       realtimeFlowInitRequest.setType("start_request");
       realtimeFlowInitRequest.setId("<symbl_unique_meetingId>");
       realtimeFlowInitRequest.setSentiments(true);
       realtimeFlowInitRequest.setInsightTypes(Arrays.asList("action_item", "question", "follow_up"));
       symblParams.setRealtimeStartRequest(realtimeFlowInitRequest);
       Gson gson = new Gson();
       pluginParams.put("inputRequest", gson.toJson(symblParams));
   } catch (Exception ex) {
       Log.e(TAG, "ERROR while setting Symbl extension configuration");
   }
}
```

7. The seventh step is to update the `strings.xml` file with values for the following:
1. `symbl_app_id`
2. `symbl_app_secret`
3. `symbl_platform_url`
4. `symbl_token_api`
5. `symbl_token_token_timeout_ms`
6. `symbl_meeting_language-code`
7. `symbl_meeting_encoding`
8. `symbl_meeting_sampleRateHertz`
9. `symbl_confidence_threshold`

After updating those strings with values, the next step is to build the project. 

## Dependencies
There is a dependency you should update according to the location of your download for the the `agora-symblai-filter-debug.aar`. Navigate to the `build.gradle` file where there is line within the dependencies for an `implementation files()`. Update the path to your own path. 

```Java
implementation files('/Users/User/Desktop/Symbl/Docs/Agora/Depedencies/agora-symblai-filter-debug.aar')
```

## Conclusion 

Symbl.ai's Extension for the Agora.io marketplace ensures developers have the power to create Android apps with features from its Conversation Intelligence API platform. Although Symbl.ai's Extension is operative for Android, Symbl.ai is open to contributions from the community for iOS. Symbl.ai, however, does have a web app for Agora.io that welcomes contributions from the community. 

## Community

If you have any questions, feel free to reach out to us at devrelations@symbl.ai or thorugh our [Community Slack][slack].

This guide is actively developed, and we love to hear from you! Please feel free to [create an issue][issues] or [open a pull request][pulls] with your questions, comments, suggestions and feedback.  If you liked our integration guide, please star our repo!

This library is released under the [MIT License][license]

[license]: LICENSE.txt
[telephony]: https://docs.symbl.ai/docs/telephony/overview/post-api
[websocket]: https://docs.symbl.ai/docs/streamingapi/overview/introduction
[console]: https:console.agora.io
[slack]: https://join.slack.com/t/symbldotai/shared_invite/
zt-4sic2s11-D3x496pll8UHSJ89cm78CA
[signup]: https://platform.symbl.ai/?_ga=2.90794201.232722623.1641351522-1406598850.1641351522#/signup
[issues]: https://github.com/symblai/symbl-agora-Android-app/issues
[pulls]: https://github.com/symblai/symbl-agora-Android-app/pulls