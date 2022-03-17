package com.example.myapplication.listener;

import android.app.Activity;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.myapplication.constants.SymblResultType;
import com.example.myapplication.constants.UserType;
import com.example.myapplication.models.ApplicationPreferences;
import com.example.myapplication.models.results.Insight;
import com.example.myapplication.models.results.Results;
import com.example.myapplication.models.results.Topic;
import com.example.myapplication.models.results.Tracker;
import com.example.myapplication.models.results.Transcript;
import com.example.myapplication.models.symbl.results.Match;
import com.example.myapplication.models.symbl.results.MessageReference;
import com.example.myapplication.models.symbl.results.SymblResult;
import com.example.myapplication.models.symbl.results.TrackerDetails;
import com.example.myapplication.models.symbl.results.User;
import com.example.myapplication.utils.AppUtils;
import com.google.gson.Gson;

import java.util.Date;

import ai.symbl.android.extension.SymblAIFilterManager;
import ai.symbl.android.extension.model.response.SymblResponse;
import io.agora.rtc2.IMediaExtensionObserver;

public class AgoraExtensionObserver implements IMediaExtensionObserver {

    private static final String TAG = "AgoraExtensionObserver:";

    private final Results results;
    private final Activity context;
    private final TextView captions;
    private final ApplicationPreferences applicationPreferences;
    private Transcript transcript;

    public AgoraExtensionObserver(Activity context, Results results, TextView captions) {
        this.context = context;
        this.results = results;
        this.captions = captions;
        this.applicationPreferences = AppUtils.getAppPreferences(context);
    }

    @Override
    public void onEvent(String vendor, String extension, String key, String value) {
        Log.i(TAG, "Symbl conversation Event \n \n  " + vendor + "  extension: " + extension + "  key: " + key + "  value: " + value);
        final StringBuilder sb = new StringBuilder();
        sb.append(value);
        if ("result".equals(key)) {
            try {
                Gson json = new Gson();
                SymblResponse symblResponse = json.fromJson(value, SymblResponse.class);
                if (symblResponse.getEvent() != null && symblResponse.getEvent().length() > 0) {
                    switch (symblResponse.getEvent()) {
                        //this conversation response from Symbl platform
                        case SymblAIFilterManager.SYMBL_START_PLUGIN_REQUEST:
                            //   sb.append("SYMBL_START_PLUGIN_REQUEST");
                           /* if(symblResponse.getErrorMessage()!=null && symblResponse.getErrorMessage().length()>0) {
                                sb.append(""+symblResponse.getErrorMessage() +" \n");
                            }else{
                                sb.append(""+symblResponse.getResult());
                            }*/
                            break;
                        case SymblAIFilterManager.SYMBL_ON_MESSAGE:
                            try {
                                if (symblResponse.getErrorMessage() != null && symblResponse.getErrorMessage().length() > 0) {
                                    sb.append("ERROR for on message ")
                                            .append(symblResponse.getErrorMessage())
                                            .append("\n");
                                } else {
                                    SymblResult symblResult = new Gson().fromJson(symblResponse.getResult(), SymblResult.class);
                                    addResults(symblResult);
                                }
                            } catch (Exception ex) {
                                Log.e(TAG, "ERROR on Symbl message on message transform error " + ex.getMessage());
                            }
                            break;
                        case SymblAIFilterManager.SYMBL_CONNECTION_ERROR:
                            Log.i(TAG, "SYMBL_CONNECTION_ERROR error code %s , error message " + symblResponse.getErrorCode());
                            break;
                        case SymblAIFilterManager.SYMBL_WEBSOCKETS_CLOSED:
                            Log.i(TAG, "SYMBL_CONNECTION_ERROR " + symblResponse.getErrorCode());
                            break;
                        case SymblAIFilterManager.SYMBL_TOKEN_EXPIRED:
                            break;
                        case SymblAIFilterManager.SYMBL_STOP_REQUEST:
                            break;
                        case SymblAIFilterManager.SYMBL_ON_CLOSE:
                            break;
                        case SymblAIFilterManager.SYMBL_SEND_REQUEST:
                            break;
                        case SymblAIFilterManager.SYMBL_ON_ERROR:
                            break;
                    }
                } else {// all error cases handle here
                    sb.append("\n Symbl event :")
                            .append(symblResponse.getEvent())
                            .append("\n Error Message :")
                            .append(symblResponse.getErrorMessage())
                            .append("\n Error code :")
                            .append(symblResponse.getErrorCode());
                }

            } catch (Exception exception) {
                System.out.println("result parse error ");
            }
        }
        context.runOnUiThread(() -> {
            if (transcript != null) {
                captions.setVisibility(View.VISIBLE);
                captions.setText(String.format("%s: %s", transcript.getUsername(), transcript.getData()));

                new Handler().postDelayed(() -> {
                    if (new Date().getTime() - transcript.getDate().getTime() >= 2000) {
                        captions.setVisibility(View.GONE);
                    }
                }, 2000);
            }
        });
    }

    @Override
    public void onStarted(String s, String s1) {
        Log.i(TAG, "onStarted -> " + s + " : " + s1);
    }

    @Override
    public void onStopped(String s, String s1) {
        Log.i(TAG, "onStopped -> " + s + " : " + s1);
    }

    @Override
    public void onError(String s, String s1, int i, String s2) {
        Log.i(TAG, "onError -> " + s + " : " + s1 + " : " + i + " : " + s2);
    }

    private void addResults(SymblResult symblResult) {
        SymblResultType type = getResultType(symblResult);
        switch (type) {
            case TOPIC_RESPONSE:
                for (ai.symbl.android.extension.model.response.topic.Topic topic : symblResult.getTopics()) {
                    results.add(new Topic(topic.getId(), topic.getPhrases()));
                }
                break;
            case INSIGHT_RESPONSE:
                for (ai.symbl.android.extension.model.response.actionitem.Insight insight : symblResult.getInsights()) {
                    results.add(new Insight(insight.getId(), insight.getPayload().getContent(), insight.getType(), insight.getAssignee().getName()));
                }
                break;

            case RECOGNITION_RESULT:
                UserType userType = getUserType(symblResult.getMessage().getUser());
                transcript = new Transcript(symblResult.getMessage().getUser().getName(), userType, symblResult.getMessage().getPunctuated().getTranscript());
                if (symblResult.getMessage().isFinal()) {
                    results.add(transcript);
                }
                break;
            case TRACKER_RESPONSE:
                for (TrackerDetails tracker : symblResult.getTrackers()) {
                    for (Match match : tracker.getMatches()) {
                        for (MessageReference messageReference : match.getMessageRefs()) {
                            results.add(new Tracker(messageReference.getId(), tracker.getName(), messageReference.getText(), messageReference.getOffset()));
                        }
                    }
                }
                break;
        }
    }

    private UserType getUserType(User user) {
        if (applicationPreferences.getEmailId().equals(user.getUserId()) && applicationPreferences.getUsername().equals(user.getName())) {
            return UserType.SENDER;
        }
        return UserType.RECEIVER;
    }

    private SymblResultType getResultType(SymblResult symblResult) {
        String type = symblResult.getMessage() != null && symblResult.getMessage().getType() != null ? symblResult.getMessage().getType() : symblResult.getType();
        return SymblResultType.fromValue(type);
    }
}
