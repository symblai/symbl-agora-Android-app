package io.agora.tutorials1v1vcall;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.UUID;

import io.agora.advancedvideo.rawdata.MediaDataAudioObserver;
import io.agora.advancedvideo.rawdata.MediaDataObserverPlugin;
import io.agora.advancedvideo.rawdata.MediaPreProcessing;
import io.agora.rtc.IAudioFrameObserver;
import io.agora.rtc.IRtcEngineEventHandler;
import io.agora.rtc.RtcEngine;
import io.agora.rtc.video.VideoCanvas;
import io.agora.rtc.video.VideoEncoderConfiguration;
import io.agora.uikit.logger.LoggerRecyclerView;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;




public class VideoChatViewActivity extends AppCompatActivity implements MediaDataAudioObserver  {

    public static String symblAccesstoken=null;
    public static WebSocket ws=null;
    public boolean isSymblConnected=false;
    boolean isVisualizerAttached = true;
    private MediaDataObserverPlugin mediaDataObserverPlugin;

    /**
     * Extending websocket listener to implement methods for Symbl websocket events
     */


public class SymblWebSocketListener extends WebSocketListener {

    private static final int NORMAL_CLOSURE_STATUS = 1000;
    private LoggerRecyclerView mLogView;





    @Override
    public void onOpen(WebSocket webSocket, Response response) {


        String startRequest = "{\"type\":\"start_request\",\"insightTypes\":[\"action_item\"],\"config\":{\"confidenceThreshold\":0.5,\"timezoneOffset\":420,\"languageCode\":\"en-US\",\"speechRecognition\":{\"encoding\":\"LINEAR16\",\"sampleRateHertz\":44100}},\"speaker\":{\"userId\":\"james@symbl.ai\",\"name\":\"James\"}}";
        System.out.println(startRequest);
        webSocket.send(startRequest);


    }

    @Override
    public void onMessage(WebSocket webSocket, String text) {
        System.out.println("Receiving: ******" + text);


        runOnUiThread(new Runnable() {

            @Override
            public void run() {

                displayTranscript(text);

            }
        });





    }

    @Override
    public void onMessage(WebSocket webSocket, ByteString bytes) {
        System.out.println("Receiving: **************" + bytes.hex());

    }

    @Override
    public void onClosing(WebSocket webSocket, int code, String reason) {
        webSocket.close(NORMAL_CLOSURE_STATUS, null);
        System.out.println("Closing: " + code + " " + reason);
    }

    @Override
    public void onFailure(WebSocket webSocket, Throwable t, Response response) {
        t.printStackTrace();
    }

}


    /**
     * Method to display Symbl transcript on the UI
     */



    public String trans2="123";
    public void displayTranscript(String text)  {
        try {
            JSONObject jsonText = new JSONObject(text);

            if((jsonText.get("type")).toString().equalsIgnoreCase("message")){

               String punc= (( (JSONObject)(jsonText.get("message"))).get("punctuated")).toString();
               JSONObject trans= new JSONObject(punc);

                if(!this.trans2.equalsIgnoreCase(trans.get("transcript").toString()))
                {
                    this.trans2=trans.get("transcript").toString();

                    TextView textView = (TextView)findViewById(R.id.textView2);
                    textView.setText(trans.get("transcript").toString());
                    //mLogView.logI(trans.get("transcript").toString());


                }

            }
            if((jsonText.get("type")).toString().equalsIgnoreCase("message_response")){



                JSONArray jsonArray= (JSONArray) jsonText.get("messages");
                System.out.println("json array"+jsonArray.toString());

                for(int k=0;k<jsonArray.length();k++)
                {
                    JSONObject obj = jsonArray.getJSONObject(k);
                    JSONObject obj2= (JSONObject) obj.get("payload");
                    mLogView.logI(obj2.get("content").toString());
                }



            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        //mLogView.logW(text);

    }


    /**
     * class Async task to generate symbl access token
     */

    public class TestAsyncTask extends AsyncTask<Void, Void, Void> {



        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        public void getSymblToken() throws IOException, JSONException {


            URL url= new URL("https://api.symbl.ai/oauth2/token:generate");
            HttpURLConnection con = (HttpURLConnection)url.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json");
            con.setDoOutput(true);

            JSONObject json = new JSONObject();
            json.put("type","application");
            json.put("appId",getString(R.string.symbl_app_id));
            json.put("appSecret",getString(R.string.symbl_app_secret));

            Log.i("JSON", json.toString());
            DataOutputStream os = new DataOutputStream(con.getOutputStream());
            //os.writeBytes(URLEncoder.encode(jsonParam.toString(), "UTF-8"));
            os.writeBytes(json.toString());

            os.flush();
            os.close();

            Log.i("STATUS", String.valueOf(con.getResponseCode()));
            Log.i("MSG" , con.getResponseMessage());
            if (con.getResponseCode() == 200) { //success
                BufferedReader in = new BufferedReader(new InputStreamReader(
                        con.getInputStream()));
                String inputLine;
                StringBuffer response = new StringBuffer();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();
                JSONObject jsonObj = new JSONObject(response.toString());
                UUID uuid=UUID.randomUUID();
                String websocketUrl=  "wss://api.symbl.ai/v1/realtime/insights/"+uuid.toString()+"?access_token="+jsonObj.get("accessToken");
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder().url(websocketUrl).build();
                SymblWebSocketListener listener = new SymblWebSocketListener();
                VideoChatViewActivity.ws = client.newWebSocket(request, listener);
                VideoChatViewActivity.symblAccesstoken=(jsonObj.get("accessToken")).toString();
            }

            con.disconnect();

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        protected Void doInBackground(Void... voids) {

            try {
                getSymblToken();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        protected void onPostExecute(Void aVoid) {

            super.onPostExecute(aVoid);

        }

    }


    private static final String TAG = VideoChatViewActivity.class.getSimpleName();

    private static final int PERMISSION_REQ_ID = 22;

    // Permission WRITE_EXTERNAL_STORAGE is not mandatory
    // for Agora RTC SDK, just in case if you wanna save
    // logs to external sdcard.
    private static final String[] REQUESTED_PERMISSIONS = {
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    private RtcEngine mRtcEngine;
    private boolean mCallEnd;
    private boolean mMuted;

    private FrameLayout mLocalContainer;
    private RelativeLayout mRemoteContainer;
    private VideoCanvas mLocalVideo;
    private VideoCanvas mRemoteVideo;

    private ImageView mCallBtn;
    private ImageView mMuteBtn;
    private ImageView mSwitchCameraBtn;

    // Customized logger view
    public LoggerRecyclerView mLogView;

    /**
     * Event handler registered into RTC engine for RTC callbacks.
     * Note that UI operations needs to be in UI thread because RTC
     * engine deals with the events in a separate thread.
     */
    private final IRtcEngineEventHandler mRtcEventHandler = new IRtcEngineEventHandler() {
        /**
         * Occurs when the local user joins a specified channel.
         * The channel name assignment is based on channelName specified in the joinChannel method.
         * If the uid is not specified when joinChannel is called, the server automatically assigns a uid.
         *
         * @param channel Channel name.
         * @param uid User ID.
         * @param elapsed Time elapsed (ms) from the user calling joinChannel until this callback is triggered.
         */
        @Override
        public void onJoinChannelSuccess(String channel, final int uid, int elapsed) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    mLogView.logI("Join channel success, uid: " + (uid & 0xFFFFFFFFL));
                }
            });
        }

        /**
         * Occurs when the first remote video frame is received and decoded.
         * This callback is triggered in either of the following scenarios:
         *
         *     The remote user joins the channel and sends the video stream.
         *     The remote user stops sending the video stream and re-sends it after 15 seconds. Possible reasons include:
         *         The remote user leaves channel.
         *         The remote user drops offline.
         *         The remote user calls the muteLocalVideoStream method.
         *         The remote user calls the disableVideo method.
         *
         * @param uid User ID of the remote user sending the video streams.
         * @param width Width (pixels) of the video stream.
         * @param height Height (pixels) of the video stream.
         * @param elapsed Time elapsed (ms) from the local user calling the joinChannel method until this callback is triggered.
         */
        @Override
        public void onFirstRemoteVideoDecoded(final int uid, int width, int height, int elapsed) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mLogView.logI("First remote video decoded, uid: " + (uid & 0xFFFFFFFFL));
                    setupRemoteVideo(uid);
                }
            });
        }

        /**
         * Occurs when a remote user (Communication)/host (Live Broadcast) leaves the channel.
         *
         * There are two reasons for users to become offline:
         *
         *     Leave the channel: When the user/host leaves the channel, the user/host sends a
         *     goodbye message. When this message is received, the SDK determines that the
         *     user/host leaves the channel.
         *
         *     Drop offline: When no data packet of the user or host is received for a certain
         *     period of time (20 seconds for the communication profile, and more for the live
         *     broadcast profile), the SDK assumes that the user/host drops offline. A poor
         *     network connection may lead to false detections, so we recommend using the
         *     Agora RTM SDK for reliable offline detection.
         *
         * @param uid ID of the user or host who leaves the channel or goes offline.
         * @param reason Reason why the user goes offline:
         *
         *     USER_OFFLINE_QUIT(0): The user left the current channel.
         *     USER_OFFLINE_DROPPED(1): The SDK timed out and the user dropped offline because no data packet was received within a certain period of time. If a user quits the call and the message is not passed to the SDK (due to an unreliable channel), the SDK assumes the user dropped offline.
         *     USER_OFFLINE_BECOME_AUDIENCE(2): (Live broadcast only.) The client role switched from the host to the audience.
         */
        @Override
        public void onUserOffline(final int uid, int reason) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mLogView.logI("User offline, uid: " + (uid & 0xFFFFFFFFL));
                    onRemoteUserLeft(uid);
                }
            });
        }
    };

    private void setupRemoteVideo(int uid) {
        ViewGroup parent = mRemoteContainer;
        if (parent.indexOfChild(mLocalVideo.view) > -1) {
            parent = mLocalContainer;
        }

        // Only one remote video view is available for this
        // tutorial. Here we check if there exists a surface
        // view tagged as this uid.
        if (mRemoteVideo != null) {
            return;
        }

        /*
          Creates the video renderer view.
          CreateRendererView returns the SurfaceView type. The operation and layout of the view
          are managed by the app, and the Agora SDK renders the view provided by the app.
          The video display view must be created using this method instead of directly
          calling SurfaceView.
         */
        SurfaceView view = RtcEngine.CreateRendererView(getBaseContext());
        view.setZOrderMediaOverlay(parent == mLocalContainer);
        parent.addView(view);
        mRemoteVideo = new VideoCanvas(view, VideoCanvas.RENDER_MODE_HIDDEN, uid);
        // Initializes the video view of a remote user.
        mRtcEngine.setupRemoteVideo(mRemoteVideo);
    }

    private void onRemoteUserLeft(int uid) {
        if (mRemoteVideo != null && mRemoteVideo.uid == uid) {
            removeFromParent(mRemoteVideo);
            // Destroys remote view
            mRemoteVideo = null;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_chat_view);
        initUI();
        System.out.println("inside onCreate");
        new TestAsyncTask().execute();



        // Ask for permissions at runtime.
        // This is just an example set of permissions. Other permissions
        // may be needed, and please refer to our online documents.
        if (checkSelfPermission(REQUESTED_PERMISSIONS[0], PERMISSION_REQ_ID) &&
                checkSelfPermission(REQUESTED_PERMISSIONS[1], PERMISSION_REQ_ID) &&
                checkSelfPermission(REQUESTED_PERMISSIONS[2], PERMISSION_REQ_ID)) {
            try {
                initEngineAndJoinChannel();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }



    private void initUI() {
        mLocalContainer = findViewById(R.id.local_video_view_container);
        mRemoteContainer = findViewById(R.id.remote_video_view_container);

        mCallBtn = findViewById(R.id.btn_call);
        mMuteBtn = findViewById(R.id.btn_mute);
        mSwitchCameraBtn = findViewById(R.id.btn_switch_camera);

        mLogView = findViewById(R.id.log_recycler_view);

        // Sample logs are optional.
        showSampleLogs();
    }
    //Symbl aut token








    private void showSampleLogs() {
        mLogView.logI("Welcome to Agora 1v1 video call");
        mLogView.logW("You will see custom logs here");
        mLogView.logE("You can also use this to show errors");
    }

    private boolean checkSelfPermission(String permission, int requestCode) {
        if (ContextCompat.checkSelfPermission(this, permission) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, REQUESTED_PERMISSIONS, requestCode);
            return false;
        }

        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_REQ_ID) {
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED ||
                    grantResults[1] != PackageManager.PERMISSION_GRANTED ||
                    grantResults[2] != PackageManager.PERMISSION_GRANTED) {
                showLongToast("Need permissions " + Manifest.permission.RECORD_AUDIO +
                        "/" + Manifest.permission.CAMERA + "/" + Manifest.permission.WRITE_EXTERNAL_STORAGE);
                finish();
                return;
            }

            // Here we continue only if all permissions are granted.
            // The permissions can also be granted in the system settings manually.
            try {
                initEngineAndJoinChannel();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void showLongToast(final String msg) {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void initEngineAndJoinChannel() throws IOException, JSONException {
        // This is our usual steps for joining
        // a channel and starting a call.
        initializeEngine();

        setupVideoConfig();
        setupLocalVideo();
        joinChannel();
    }

    private void initializeEngine() {
        try {
            mRtcEngine = RtcEngine.create(getBaseContext(), getString(R.string.agora_app_id), mRtcEventHandler);

            mRtcEngine.registerAudioFrameObserver(new IAudioFrameObserver() {
                @Override
                public boolean onRecordFrame(byte[] samples, int numOfSamples, int bytesPerSample, int channels, int samplesPerSec) {
                    //System.out.println("number of samples "+ numOfSamples);
                   //System.out.println("actual samples in bytes "+ samples);

                    ws.send(ByteString.of(samples));

                    return false;
                }

                @Override
                public boolean onPlaybackFrame(byte[] samples, int numOfSamples, int bytesPerSample, int channels, int samplesPerSec) {
                    return false;
                }

                @Override
                public boolean onPlaybackFrameBeforeMixing(byte[] samples, int numOfSamples, int bytesPerSample, int channels, int samplesPerSec, int uid) {
                    return false;
                }

                @Override
                public boolean onMixedFrame(byte[] samples, int numOfSamples, int bytesPerSample, int channels, int samplesPerSec) {
                    return false;
                }

                @Override
                public boolean isMultipleChannelFrameWanted() {
                    return false;
                }

                @Override
                public boolean onPlaybackFrameBeforeMixingEx(byte[] samples, int numOfSamples, int bytesPerSample, int channels, int samplesPerSec, int uid, String channelId) {
                    return false;
                }
            });

        } catch (Exception e) {
            Log.e(TAG, Log.getStackTraceString(e));
            throw new RuntimeException("NEED TO check rtc sdk init fatal error\n" + Log.getStackTraceString(e));
        }
    }

    private void setupVideoConfig() {
        // In simple use cases, we only need to enable video capturing
        // and rendering once at the initialization step.
        // Note: audio recording and playing is enabled by default.
        mRtcEngine.enableVideo();

        // Please go to this page for detailed explanation
        // https://docs.agora.io/en/Video/API%20Reference/java/classio_1_1agora_1_1rtc_1_1_rtc_engine.html#af5f4de754e2c1f493096641c5c5c1d8f
        mRtcEngine.setVideoEncoderConfiguration(new VideoEncoderConfiguration(
                VideoEncoderConfiguration.VD_640x360,
                VideoEncoderConfiguration.FRAME_RATE.FRAME_RATE_FPS_15,
                VideoEncoderConfiguration.STANDARD_BITRATE,
                VideoEncoderConfiguration.ORIENTATION_MODE.ORIENTATION_MODE_FIXED_PORTRAIT));
    }

    private void setupLocalVideo() {
        // This is used to set a local preview.
        // The steps setting local and remote view are very similar.
        // But note that if the local user do not have a uid or do
        // not care what the uid is, he can set his uid as ZERO.
        // Our server will assign one and return the uid via the event
        // handler callback function (onJoinChannelSuccess) after
        // joining the channel successfully.
        SurfaceView view = RtcEngine.CreateRendererView(getBaseContext());
        view.setZOrderMediaOverlay(true);
        mLocalContainer.addView(view);
        // Initializes the local video view.
        // RENDER_MODE_HIDDEN: Uniformly scale the video until it fills the visible boundaries. One dimension of the video may have clipped contents.
        mLocalVideo = new VideoCanvas(view, VideoCanvas.RENDER_MODE_HIDDEN, 0);
        mRtcEngine.setupLocalVideo(mLocalVideo);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void joinChannel() throws IOException, JSONException {
        // 1. Users can only see each other after they join the
        // same channel successfully using the same app id.
        // 2. One token is only valid for the channel name that
        // you use to generate this token.
        String token = getString(R.string.agora_access_token);
        String channelName=getString(R.string.agora_channel);
        if (TextUtils.isEmpty(token) || TextUtils.equals(token, "#YOUR ACCESS TOKEN#")) {
            token = null; // default, no token
        }
        System.out.println("hi I am connnected now");

        mRtcEngine.joinChannel(token, channelName, "Extra Optional Data", 0);


        UUID uuid=UUID.randomUUID();


       /* String websocketUrl=  "wss://api.symbl.ai/v1/realtime/insights/"+uuid.toString()+"?access_token="+this.symblAccesstoken;

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder().url(websocketUrl).build();
        SymblWebSocketListener listener = new SymblWebSocketListener();
        WebSocket ws = client.newWebSocket(request, listener);*/



    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (!mCallEnd) {
            leaveChannel();
        }
        /*
          Destroys the RtcEngine instance and releases all resources used by the Agora SDK.

          This method is useful for apps that occasionally make voice or video calls,
          to free up resources for other operations when not making calls.
         */
        RtcEngine.destroy();
    }

    private void leaveChannel() {
        mRtcEngine.leaveChannel();
    }

    public void onLocalAudioMuteClicked(View view) {
        mMuted = !mMuted;
        // Stops/Resumes sending the local audio stream.
        mRtcEngine.muteLocalAudioStream(mMuted);
        int res = mMuted ? R.drawable.btn_mute : R.drawable.btn_unmute;
        mMuteBtn.setImageResource(res);
    }

    public void onSwitchCameraClicked(View view) {
        // Switches between front and rear cameras.
        mRtcEngine.switchCamera();
        System.out.println("camera clicked");
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void onCallClicked(View view) throws IOException, JSONException {
        if (mCallEnd) {
            startCall();
            mCallEnd = false;
            mCallBtn.setImageResource(R.drawable.btn_endcall);
        } else {
            endCall();
            mCallEnd = true;
            mCallBtn.setImageResource(R.drawable.btn_startcall);
        }

        showButtons(!mCallEnd);
    }


    private void startCall() throws IOException, JSONException {
        setupLocalVideo();
        joinChannel();
        System.out.println("Start call clicked");

    }

    private void endCall() {
        removeFromParent(mLocalVideo);
        mLocalVideo = null;
        removeFromParent(mRemoteVideo);
        mRemoteVideo = null;
        leaveChannel();
    }

    private void showButtons(boolean show) {
        int visibility = show ? View.VISIBLE : View.GONE;
        mMuteBtn.setVisibility(visibility);
        mSwitchCameraBtn.setVisibility(visibility);
    }

    private ViewGroup removeFromParent(VideoCanvas canvas) {
        if (canvas != null) {
            ViewParent parent = canvas.view.getParent();
            if (parent != null) {
                ViewGroup group = (ViewGroup) parent;
                group.removeView(canvas.view);
                return group;
            }
        }
        return null;
    }

    private void switchView(VideoCanvas canvas) {
        ViewGroup parent = removeFromParent(canvas);
        if (parent == mLocalContainer) {
            if (canvas.view instanceof SurfaceView) {
                ((SurfaceView) canvas.view).setZOrderMediaOverlay(false);
            }
            mRemoteContainer.addView(canvas.view);
        } else if (parent == mRemoteContainer) {
            if (canvas.view instanceof SurfaceView) {
                ((SurfaceView) canvas.view).setZOrderMediaOverlay(true);
            }
            mLocalContainer.addView(canvas.view);
        }
    }

    public void onLocalContainerClick(View view) {
        switchView(mLocalVideo);
        switchView(mRemoteVideo);
    }
    private void setupObserver() {
        System.out.println("inside setupObserver");
        mediaDataObserverPlugin = MediaDataObserverPlugin.the();
        MediaPreProcessing.setCallback(mediaDataObserverPlugin);
        MediaPreProcessing.setAudioPlayByteBuffer(mediaDataObserverPlugin.byteBufferAudioPlay);
        mediaDataObserverPlugin.addAudioObserver(this);
    }

    @Override
    public void onRecordAudioFrame(byte[] data, int audioFrameType, int samples, int bytesPerSample, int channels, int samplesPerSec, long renderTimeMs, int bufferLength) {
        Log.e(TAG, "onRecordAudioFrame: ");
    }

    private int maxAmplitude = 0;

    @Override
    public void onPlaybackAudioFrame(byte[] data, int audioFrameType, int samples, int bytesPerSample, int channels, int samplesPerSec, long renderTimeMs, int bufferLength) {
        if (isVisualizerAttached) {
            short[] rawAudio = new short[data.length / 2];
            ByteBuffer.wrap(data).order(ByteOrder.BIG_ENDIAN).asShortBuffer().get(rawAudio);
            short amplitude = 0;
            for (short num : rawAudio) {
                if (num > amplitude)
                    amplitude = num;
            }
            Log.e(TAG, "onPlaybackAudioFrame: Supposedly we have data -> max: " + amplitude);
        }
        Log.e(TAG, "onPlaybackAudioFrame:");
    }

    @Override
    public void onPlaybackAudioFrameBeforeMixing(int uid, byte[] data, int audioFrameType, int samples, int bytesPerSample, int channels, int samplesPerSec, long renderTimeMs, int bufferLength) {
        Log.e(TAG, "onPlaybackAudioFrameBeforeMixing: ");
    }

    @Override
    public void onMixedAudioFrame(byte[] data, int audioFrameType, int samples, int bytesPerSample, int channels, int samplesPerSec, long renderTimeMs, int bufferLength) {
        Log.e(TAG, "onMixedAudioFrame: ");
    }


}
