package com.darkness.cameraexample;

import android.media.AudioManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.sinch.android.rtc.PushPair;
import com.sinch.android.rtc.Sinch;
import com.sinch.android.rtc.SinchClient;
import com.sinch.android.rtc.calling.Call;
import com.sinch.android.rtc.calling.CallClient;
import com.sinch.android.rtc.calling.CallClientListener;
import com.sinch.android.rtc.calling.CallListener;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String APP_KEY = "";
    private static final String APP_SECRET = "";
    private static final String HOST = "";
    private static final String USER_ID = "";

    private Button btnCall;
    private TextView tvState;

    private Call call;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();

        final SinchClient mSinchClient = Sinch.getSinchClientBuilder()
                .context(MainActivity.this)
                .userId(USER_ID)
                .applicationKey(APP_KEY)
                .applicationSecret(APP_SECRET)
                .environmentHost(HOST)
                .build();

        mSinchClient.setSupportCalling(true);
        mSinchClient.startListeningOnActiveConnection();
        mSinchClient.start();

        mSinchClient.getCallClient().addCallClientListener(new SinchCallClientListener());

        btnCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (call == null) {
                    call = mSinchClient.getCallClient().callPhoneNumber("");
                    call.addCallListener(new SinchCallListener());
                    btnCall.setText("Hang Up");
                } else {
                    call.hangup();
                }
            }
        });
    }

    private void initView() {
        btnCall = findViewById(R.id.btnCall);
        tvState = findViewById(R.id.tvState);
    }

    private class SinchCallListener implements CallListener {
        @Override
        public void onCallEnded(Call endedCall) {
            call = null;
            btnCall.setText("Call");
            tvState.setText("");
            setVolumeControlStream(AudioManager.USE_DEFAULT_STREAM_TYPE);
        }

        @Override
        public void onCallEstablished(Call establishedCall) {
            tvState.setText("connected");
            setVolumeControlStream(AudioManager.STREAM_VOICE_CALL);
        }

        @Override
        public void onCallProgressing(Call progressingCall) {
            tvState.setText("ringing");
        }

        @Override
        public void onShouldSendPushNotification(Call call, List<PushPair> pushPairs) {}
    }

    private class SinchCallClientListener implements CallClientListener {
        @Override
        public void onIncomingCall(CallClient callClient, Call incomingCall) {
            call = incomingCall;
            call.answer();
            call.addCallListener(new SinchCallListener());
            btnCall.setText("Hang Up");
        }
    }
}
