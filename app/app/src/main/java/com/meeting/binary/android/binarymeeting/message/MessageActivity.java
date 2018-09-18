package com.meeting.binary.android.binarymeeting.message;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.meeting.binary.android.binarymeeting.R;
import com.meeting.binary.android.binarymeeting.model.Message;
import com.meeting.binary.android.binarymeeting.other.SingleFragmentActivity;
import com.meeting.binary.android.binarymeeting.other.SingleFragmentActivityNoBar;
import com.meeting.binary.android.binarymeeting.service.websocket.WebSocketStomptConfig;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import rx.functions.Action1;
import ua.naiksoftware.stomp.client.StompClient;
import ua.naiksoftware.stomp.client.StompMessage;

public class MessageActivity extends SingleFragmentActivityNoBar {

    private static final String EXTRA_ID = "extra_message";


    public static Intent newIntent(Context context, Message message){
        Intent intent = new Intent(context, MessageActivity.class);
        Bundle args = new Bundle();
        args.putSerializable(EXTRA_ID, message);
        intent.putExtras(args);
        return intent;
    }

    @Override
    public Fragment createFragment() {
        return MessasgeFragment.newInstance((Message) getIntent().getSerializableExtra(EXTRA_ID));
    }


//    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            mStompClient = (StompClient) intent.getSerializableExtra(MyService.PAYLOAD);
//        }
//    };




// in oncrerate
//        LocalBroadcastManager.getInstance(getApplicationContext()).
//                registerReceiver(mBroadcastReceiver, new IntentFilter(MyService.MESSAGE));








   //in onDestroy
//        LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(mBroadcastReceiver);










}
