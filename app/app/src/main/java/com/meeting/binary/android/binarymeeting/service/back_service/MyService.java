package com.meeting.binary.android.binarymeeting.service.back_service;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.util.Log;

import com.meeting.binary.android.binarymeeting.service.websocket.WebSocketStomptConfig;

import ua.naiksoftware.stomp.client.StompClient;


/**
 * Created by meldi on 3/23/2018.
 */

public class MyService extends IntentService {
    private StompClient mStompClient;
    public static final String PAYLOAD = "socketPayload";
    public static final String MESSAGE = "socketMessage";


    private static final String TAG = "start_service";

    public MyService() {
        super("my service");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Log.i(TAG, "onHandleIntent: service launched");
        //mStompClient = WebSocketStomptConfig.stompConnect(this);
//        while(true){
//
//            try {
//                Thread.sleep(3000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }


//        Intent messageIntentBroadCast = new Intent(MESSAGE);
//        Bundle args = new Bundle();
//        args.putSerializable(PAYLOAD, (Serializable) mStompClient);
//        messageIntentBroadCast.putExtras(args);
//        LocalBroadcastManager manager  = LocalBroadcastManager.getInstance(getApplicationContext());
//        manager.sendBroadcast(messageIntentBroadCast);

    }
}
