package com.meeting.binary.android.binarymeeting.service.websocket;

import android.app.Activity;
import android.content.Context;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.meeting.binary.android.binarymeeting.service.cookie.CookiePreferences;
import com.meeting.binary.android.binarymeeting.service.generetor.BaseUrlGenerator;

import org.java_websocket.WebSocket;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rx.functions.Action1;
import ua.naiksoftware.stomp.LifecycleEvent;
import ua.naiksoftware.stomp.Stomp;
import ua.naiksoftware.stomp.StompHeader;
import ua.naiksoftware.stomp.client.StompClient;

/**
 * Created by meldi on 3/31/2018.
 */

public class WebSocketStomptConfig {



    private static StompClient sStompClient = null;
    public static final String TAG = "stompClient";
    private static boolean mConnected;



   private synchronized  static void getStompClient(Context context, Map<String,String> headers){

        if(sStompClient ==null || !mConnected){
            sStompClient = Stomp.over(WebSocket.class, "ws://" + BaseUrlGenerator.BASE_DOMAIN +"/stompwebsocket",headers);
            sStompClient.connect();
//            Log.d(TAG,"headers: "+headers.get(0).getKey()+": "+headers.get(0).getValue());
            Toast.makeText(context,"Connecting to the websocket",Toast.LENGTH_SHORT).show();
            sStompClient.lifecycle().subscribe(new Action1<LifecycleEvent>() {
                @Override
                public void call(LifecycleEvent lifecycleEvent) {
                    switch (lifecycleEvent.getType()) {
                        case OPENED:
                            Log.d(TAG, "Stomp connection opened");
                            mConnected=true;
                            break;

                        case ERROR:
                            Log.e(TAG, "Stomp Error", lifecycleEvent.getException());
                            mConnected=false;
                            break;
                        case CLOSED:
                            Log.d(TAG, "Stomp connection closed");
                            mConnected=false;
                            break;
                    }
                }
            });
        }


    }


    public static StompClient stompConnect(Context context){

//       getStompClient(context);
//        String PREF_COOKIES = "PREF_COOKIES";
//        PreferenceManager.getDefaultSharedPreferences(context)
//                .getString(PREF_COOKIES, null);


        String recupCookie = CookiePreferences.getStoredCookie(context);
        Map<String,String> headers = new HashMap<>();
        headers.put("Cookie",recupCookie);

        getStompClient(context, headers);



        return sStompClient;

    }


}
