package com.example.myapplication.activities;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
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
            enableEffect();
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
            enableEffect();
        }
    }

    private void initializeUI() {
        localVideoContainer = findViewById(R.id.local_video_view_container);
        remoteVideoContainer = findViewById(R.id.remote_video_view_container);

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

    private void initializeAgoraEngine() {
        RtcEngineConfig config = new RtcEngineConfig();
        config.mContext = VideoCallingActivity.this;
        config.mAppId = agoraConfiguration.getCustomerAppId();
        config.addExtension(ExtensionManager.EXTENSION_NAME);
        config.mExtensionObserver = new AgoraExtensionObserver(VideoCallingActivity.this, results, captions);
        config.mEventHandler = getRtcEventHandler();
        createRtcEngine(config);
        //extension is enabled by default
        mRtcEngine.enableExtension(ExtensionManager.EXTENSION_VENDOR_NAME, ExtensionManager.EXTENSION_FILTER_NAME, true);
        setupLocalVideo();
        VideoEncoderConfiguration configuration = new VideoEncoderConfiguration(640, 360,
                VideoEncoderConfiguration.FRAME_RATE.FRAME_RATE_FPS_24,
                VideoEncoderConfiguration.STANDARD_BITRATE,
                VideoEncoderConfiguration.ORIENTATION_MODE.ORIENTATION_MODE_FIXED_PORTRAIT);
        mRtcEngine.setVideoEncoderConfiguration(configuration);
        mRtcEngine.setClientRole(Constants.AUDIO_ENCODED_FRAME_OBSERVER_POSITION_MIC);
        mRtcEngine.enableLocalAudio(true);
        mRtcEngine.setEnableSpeakerphone(true);
        mRtcEngine.setAudioProfile(1);
        mRtcEngine.enableAudio();
        Log.d(TAG, "Channel Name : " + agoraConfiguration.getCustomerChannelName());
        mRtcEngine.joinChannel(agoraConfiguration.getTokenValue(), agoraConfiguration.getCustomerChannelName(), agoraConfiguration.getMeetingId(), 0);
        mRtcEngine.startPreview();

    }

    private void createRtcEngine(RtcEngineConfig config) {
        try {
            mRtcEngine = RtcEngine.create(config);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, " ERROR:: RTC engine startup error " + e.getMessage());
        }
    }

    @NonNull
    private IRtcEngineEventHandler getRtcEventHandler() {
        return new IRtcEngineEventHandler() {
            @Override
            public void onJoinChannelSuccess(String s, int i, int i1) {
                Log.d(TAG, "on Join Channel Success");
                mRtcEngine.startPreview();
                JSONObject symblPluginConfigs = symblService.getSymblPluginConfigs(agoraConfiguration.getCustomerChannelName());
                mRtcEngine.setExtensionProperty(ExtensionManager.EXTENSION_VENDOR_NAME, ExtensionManager.EXTENSION_FILTER_NAME, "init", symblPluginConfigs.toString());
            }

            @Override
            public void onFirstRemoteVideoDecoded(final int i, int i1, int i2, int i3) {
                super.onFirstRemoteVideoDecoded(i, i1, i2, i3);
                Log.d(TAG, "onFirstRemoteVideoDecoded  uid = " + i);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setupRemoteVideo(i);
                    }
                });
            }

            @Override
            public void onUserJoined(int i, int i1) {
                super.onUserJoined(i, i1);
                Log.d(TAG, "onUserJoined  uid = " + i);
            }

            @Override
            public void onUserOffline(final int i, int i1) {
                super.onUserOffline(i, i1);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        onRemoteUserLeft();
                    }
                });
            }
        };
    }

    private void setupLocalVideo() {
        SurfaceView view = RtcEngine.CreateRendererView(this);
        view.setZOrderMediaOverlay(true);
        localVideoContainer.addView(view);
        mRtcEngine.setupLocalVideo(new VideoCanvas(view, VideoCanvas.RENDER_MODE_HIDDEN, 0));
        mRtcEngine.setLocalRenderMode(Constants.RENDER_MODE_HIDDEN);
    }

    private void setupRemoteVideo(int uid) {
        // Only one remote video view is available for this
        // tutorial. Here we check if there exists a surface
        // view tagged as this uid.
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
        remoteVideoContainer.addView(mRemoteView);
        mRtcEngine.setupRemoteVideo(new VideoCanvas(mRemoteView, VideoCanvas.RENDER_MODE_HIDDEN, uid));
        mRtcEngine.setRemoteRenderMode(uid, Constants.RENDER_MODE_HIDDEN);
        mRemoteView.setTag(uid);
    }

    private void onRemoteUserLeft() {
        removeRemoteVideo();
    }

    private void removeRemoteVideo() {
        if (mRemoteView != null) {
            remoteVideoContainer.removeView(mRemoteView);
        }
        mRemoteView = null;
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