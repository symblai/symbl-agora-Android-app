package com.example.myapplication.activities;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;
import com.example.myapplication.activities.result_tabs.ResultTabs;
import com.example.myapplication.config.AgoraConfiguration;
import com.example.myapplication.config.SymblConfiguration;
import com.example.myapplication.constants.SwipeDirection;
import com.example.myapplication.listener.AgoraExtensionObserver;
import com.example.myapplication.listener.SimpleGestureDetector;
import com.example.myapplication.listener.SimpleGestureListener;
import com.example.myapplication.models.results.Results;
import com.example.myapplication.services.PermissionService;
import com.example.myapplication.services.SymblService;

import org.json.JSONObject;

import ai.symbl.android.extension.ExtensionManager;
import io.agora.rtc2.Constants;
import io.agora.rtc2.IRtcEngineEventHandler;
import io.agora.rtc2.RtcEngine;
import io.agora.rtc2.RtcEngineConfig;
import io.agora.rtc2.video.VideoCanvas;
import io.agora.rtc2.video.VideoEncoderConfiguration;

public class VideoCallingActivity extends AppCompatActivity implements SimpleGestureListener {

    private final static String TAG = "Agora_SymblTag java :";
    private static final int PERMISSION_REQUEST_ID = 22;

    private Results results;
    private SimpleGestureDetector simpleGestureDetector;
    private AgoraConfiguration agoraConfiguration;
    private SymblService symblService;
    private PermissionService permissionService;
    private boolean isTrackersEnabled;

    private FrameLayout localVideoContainer;
    private FrameLayout remoteVideoContainer;
    private RtcEngine mRtcEngine;
    private SurfaceView mLocalView;
    private SurfaceView mRemoteView;
    private TextView captions;
    private ImageView toggleMuteButton;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_calling);

        permissionService = new PermissionService();
        simpleGestureDetector = new SimpleGestureDetector(VideoCallingActivity.this, VideoCallingActivity.this);

        isTrackersEnabled = getIntent().getBooleanExtra("isTrackersEnabled", false);
        agoraConfiguration = (AgoraConfiguration) getIntent().getExtras().getSerializable("agoraConfiguration");
        SymblConfiguration symblConfiguration = (SymblConfiguration) getIntent().getExtras().getSerializable("symblConfiguration");
        symblService = new SymblService(symblConfiguration);

        if (results == null) {
            results = new Results();
        }

        initializeUI();
        permissionService.requestPermissionsIfNotGranted(VideoCallingActivity.this, PERMISSION_REQUEST_ID);

        if (permissionService.requiredPermissionGranted(VideoCallingActivity.this)) {
            initializeAgoraEngine();
        }
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy");
        super.onDestroy();
        mRtcEngine.leaveChannel();
        RtcEngine.destroy();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_ID && permissionService.requiredPermissionGranted(VideoCallingActivity.this)) {
            initializeAgoraEngine();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        disableEffect();
        finish();
    }

    private void initializeUI() {
        localVideoContainer = findViewById(R.id.local_video_view_container);
        remoteVideoContainer = findViewById(R.id.remote_video_view_container);

        remoteVideoContainer.setOnClickListener(v -> switchCameraViews());

        captions = findViewById(R.id.captions);
        captions.setVisibility(View.INVISIBLE);

        toggleMuteButton = findViewById(R.id.muteButton);
        toggleMuteButton.setOnClickListener(this::onClickOfMuteButton);
        toggleMuteButton.setSelected(false);

        ImageView leaveRoomButton = findViewById(R.id.leaveRoomButton);
        leaveRoomButton.setOnClickListener(this::onClickOfLeaveRoomButton);

        ImageView switchCameraButton = findViewById(R.id.switchCameraButton);
        switchCameraButton.setOnClickListener(this::onClickOfSwitchCameraButton);

        ImageView swipeRightButton = findViewById(R.id.swipeRightIcon);
        swipeRightButton.setOnClickListener(v -> displayResults());
    }

    private void onClickOfSwitchCameraButton(View view) {
        mRtcEngine.switchCamera();
    }

    private void onClickOfLeaveRoomButton(View view) {
        disableEffect();
        finish();
    }

    private void onClickOfMuteButton(View view) {
        if (toggleMuteButton.isSelected()) {
            toggleMuteButton.setSelected(false);
            toggleMuteButton.setImageResource(R.drawable.unmuted_button);
            mRtcEngine.enableAudio();
        } else {
            toggleMuteButton.setSelected(true);
            toggleMuteButton.setImageResource(R.drawable.muted_button);
            mRtcEngine.disableAudio();
        }
    }

    private RtcEngineConfig getRtcEngineConfig() {
        RtcEngineConfig config = new RtcEngineConfig();
        config.mContext = VideoCallingActivity.this;
        config.mAppId = agoraConfiguration.getCustomerAppId();
        config.addExtension(ExtensionManager.EXTENSION_NAME);
        config.mExtensionObserver = new AgoraExtensionObserver(VideoCallingActivity.this, results, captions);
        config.mEventHandler = getRtcEventHandler();
        return config;
    }

    private void initializeAgoraEngine() {
        RtcEngineConfig config = getRtcEngineConfig();
        createRtcEngine(config);
        //extension is enabled by default
        mRtcEngine.enableExtension(ExtensionManager.EXTENSION_VENDOR_NAME, ExtensionManager.EXTENSION_FILTER_NAME, true);
        setupLocalVideo();
        setupVideoStreamingSession();
        Log.d(TAG, "Channel Name : " + agoraConfiguration.getCustomerChannelName());
        mRtcEngine.joinChannel(agoraConfiguration.getTokenValue(), agoraConfiguration.getCustomerChannelName(), agoraConfiguration.getMeetingId(), 0);
    }

    private void setupVideoStreamingSession() {
        mRtcEngine.setChannelProfile(Constants.CHANNEL_PROFILE_COMMUNICATION);
        VideoEncoderConfiguration configuration = new VideoEncoderConfiguration(VideoEncoderConfiguration.VD_640x480,
                VideoEncoderConfiguration.FRAME_RATE.FRAME_RATE_FPS_24,
                VideoEncoderConfiguration.STANDARD_BITRATE,
                VideoEncoderConfiguration.ORIENTATION_MODE.ORIENTATION_MODE_FIXED_PORTRAIT);
        mRtcEngine.setVideoEncoderConfiguration(configuration);
        mRtcEngine.setClientRole(Constants.AUDIO_ENCODED_FRAME_OBSERVER_POSITION_MIC);
        mRtcEngine.enableLocalAudio(true);
        mRtcEngine.setEnableSpeakerphone(true);
        mRtcEngine.setAudioProfile(1);
        mRtcEngine.enableAudio();
        mRtcEngine.enableVideo();
        mRtcEngine.startPreview();
    }

    private void createRtcEngine(RtcEngineConfig config) {
        try {
            mRtcEngine = RtcEngine.create(config);
        } catch (Exception e) {
            Log.e(TAG, " ERROR:: RTC engine startup error " + e.getMessage(), e);
        }
    }

    @NonNull
    private IRtcEngineEventHandler getRtcEventHandler() {
        return new IRtcEngineEventHandler() {
            @Override
            public void onJoinChannelSuccess(String channel, final int uid, int elapsed) {
                Log.d(TAG, "Join channel success, uid: " + (uid & 0xFFFFFFFFL) + " channel: " + channel);
                mRtcEngine.startPreview();
                new Thread(() -> enableEffect()).start();

            }

            @Override
            public void onUserJoined(final int uid, int elapsed) {
                Log.d(TAG, "First remote video decoded, uid: " + (uid & 0xFFFFFFFFL));
                runOnUiThread(() -> setupRemoteVideo(uid));
            }

            @Override
            public void onUserOffline(final int uid, int reason) {
                Log.d(TAG, "User offline, uid: " + (uid & 0xFFFFFFFFL));
                runOnUiThread(() -> onRemoteUserLeft(uid));
            }
        };
    }

    private void setupLocalVideo() {
        mLocalView = RtcEngine.CreateRendererView(this);
        localVideoContainer.addView(mLocalView);
        mRtcEngine.setupLocalVideo(new VideoCanvas(mLocalView, VideoCanvas.RENDER_MODE_HIDDEN, 0));
        mRtcEngine.setLocalRenderMode(Constants.RENDER_MODE_HIDDEN);
    }

    private void setupRemoteVideo(int uid) {
        // Only one remote video view is available for this
        // tutorial. Here we check if there exists a surface
        // view tagged as this uid.
        Log.d(TAG, " setupRemoteVideo uid: " + (uid & 0xFFFFFFFFL));
        int count = remoteVideoContainer.getChildCount();
        View view = null;
        for (int i = 0; i < count; i++) {
            View v = remoteVideoContainer.getChildAt(i);
            if (v.getTag() instanceof Integer && ((int) v.getTag()) == uid) {
                view = v;
            }
        }

        if (view != null) {
            return;
        }

        Log.d(TAG, " setupRemoteVideo uid = " + uid);
        mRemoteView = RtcEngine.CreateRendererView(getBaseContext());
        mRemoteView.setTag(uid);
        remoteVideoContainer.addView(mRemoteView);
        mRtcEngine.setupRemoteVideo(new VideoCanvas(mRemoteView, VideoCanvas.RENDER_MODE_HIDDEN, uid));
        mRtcEngine.setRemoteRenderMode(uid, Constants.RENDER_MODE_HIDDEN);
        switchCameraViews();
    }

    private void onRemoteUserLeft(int uid) {
        switchCameraViews();
        removeRemoteVideo();
    }

    private void removeRemoteVideo() {
        if (mRemoteView != null) {
            remoteVideoContainer.removeView(mRemoteView);
        }
        mRemoteView = null;
    }

    private ViewGroup removeFromParent(SurfaceView view) {
        if (view == null || view.getParent() == null) {
            return null;
        }

        ViewGroup parent = (ViewGroup) view.getParent();
        parent.removeView(view);
        return parent;
    }

    private void switchView(SurfaceView view) {
        ViewGroup parent = removeFromParent(view);
        if (parent == localVideoContainer) {
            view.setZOrderMediaOverlay(true);
            remoteVideoContainer.addView(view);
        } else if (parent == remoteVideoContainer) {
            view.setZOrderOnTop(false);
            localVideoContainer.addView(view);
        }
    }

    private void switchCameraViews() {
        if (mLocalView == null || mRemoteView == null) {
            return;
        }

        switchView(mLocalView);
        switchView(mRemoteView);
    }

    private void initializeEffect() {
        JSONObject symblPluginConfigs = symblService.getSymblPluginConfigs(agoraConfiguration.getCustomerChannelName());
        mRtcEngine.setExtensionProperty(ExtensionManager.EXTENSION_VENDOR_NAME, ExtensionManager.EXTENSION_FILTER_NAME, "init", symblPluginConfigs.toString());
    }

    private void enableEffect() {
        JSONObject symblPluginConfigs = symblService.getSymblPluginConfigs(agoraConfiguration.getCustomerChannelName());
        mRtcEngine.setExtensionProperty(ExtensionManager.EXTENSION_VENDOR_NAME, ExtensionManager.EXTENSION_FILTER_NAME, "start", symblPluginConfigs.toString());
    }

    private void disableEffect() {
        JSONObject symblPluginConfigs = new JSONObject();
        mRtcEngine.setExtensionProperty(ExtensionManager.EXTENSION_VENDOR_NAME, ExtensionManager.EXTENSION_FILTER_NAME, "stop", symblPluginConfigs.toString());
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent me) {
        simpleGestureDetector.onTouchEvent(me);
        return super.dispatchTouchEvent(me);
    }

    @Override
    public void onSwipe(SwipeDirection swipeDirection) {
        switch (swipeDirection) {
            case LEFT:
                displayResults();
                break;

            case RIGHT:
            case UP:
            case DOWN:
                System.out.println("on Dashboard Activity -  Swipe : " + swipeDirection.name()); // TODO :- Log gesture
                break;
        }
    }

    private void displayResults() {
        Intent intent = new Intent(VideoCallingActivity.this, ResultTabs.class);
        intent.putExtra("results", results);
        intent.putExtra("isTrackersEnabled", isTrackersEnabled);
        startActivity(intent);
    }
}