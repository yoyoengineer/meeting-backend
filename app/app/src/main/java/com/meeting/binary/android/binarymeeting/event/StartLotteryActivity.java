
package com.meeting.binary.android.binarymeeting.event;

import android.animation.Animator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.meeting.binary.android.binarymeeting.R;
import com.meeting.binary.android.binarymeeting.model.EventMessage;
import com.meeting.binary.android.binarymeeting.model.GenericResponse;
import com.meeting.binary.android.binarymeeting.model.LotteryPlayer;
import com.meeting.binary.android.binarymeeting.model.LotteryResult;
import com.meeting.binary.android.binarymeeting.model.LotteryWinner;
import com.meeting.binary.android.binarymeeting.service.cookie.CookiePreferences;
import com.meeting.binary.android.binarymeeting.service.generetor.BaseUrlGenerator;
import com.meeting.binary.android.binarymeeting.service.generetor.GeneralServiceGenerator;
import com.meeting.binary.android.binarymeeting.service.request_interface.RequestWebServiceInterface;
import com.xys.libzxing.zxing.activity.CaptureActivity;

import org.java_websocket.WebSocket;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import rx.functions.Action1;
import ua.naiksoftware.stomp.Stomp;
import ua.naiksoftware.stomp.client.StompClient;
import ua.naiksoftware.stomp.client.StompMessage;

public class StartLotteryActivity extends AppCompatActivity implements View.OnClickListener{

    private static final String TAG = "StartLotteryActivity";
    private ImageView startLottery;
    private static final int REQUEST_CODE = 0;
    private RequestWebServiceInterface requestWebServiceInterface;
    private StompClient mStompClient;
    //    private String lotteryId_Lottery;
    private String meetingId_Lottery;
    private ObjectMapper objectMapper;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public static void actionStart(Context context, String meetingId) {
        Intent intent = new Intent(context, StartLotteryActivity.class);
        intent.putExtra("meetingId", meetingId);
        context.startActivity(intent);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        meetingId_Lottery = getIntent().getStringExtra("meetingId");
        setContentView(R.layout.activity_start_lottery);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        startLottery = findViewById(R.id.start_lottery);
        startLottery.setOnClickListener(this);
        String recupCookie = CookiePreferences.getStoredCookie(this);
        Map<String,String> headers = new HashMap<>();
        objectMapper = new ObjectMapper();
        headers.put("Cookie",recupCookie);
        mStompClient = Stomp.over(WebSocket.class, "ws://" + BaseUrlGenerator.BASE_DOMAIN +"/stompwebsocket",headers);
        mStompClient.connect();
        registerToPrivateStompTopic();
        registerToPublicStompTopic();


    }


    /**
     * ===========================================
     * receive data for danmaku from the websocket
     * ============================================
     */

    private void registerToPrivateStompTopic() {
        mStompClient.topic("/user/queue/event/"+meetingId_Lottery).subscribe(new Action1<StompMessage>() {
            @Override
            public void call(StompMessage stompMessage) {
                Log.d(TAG, "call: " +stompMessage.getPayload() );
                try {
                    EventMessage eventMessage = objectMapper.readValue(stompMessage.getPayload(), EventMessage.class);
                    if(eventMessage!=null && eventMessage.getType()!=null ){
                        if(eventMessage.getType().equalsIgnoreCase("LOTTERY_WIN")){
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    updatePlayersResults();
                                }
                            });
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

    }


    private void registerToPublicStompTopic() {
        mStompClient.topic("/topic/event/"+meetingId_Lottery).subscribe(new Action1<StompMessage>() {
            @Override
            public void call(StompMessage stompMessage) {
                Log.d(TAG, "call: " +stompMessage.getPayload() );
                try {
                    EventMessage eventMessage = objectMapper.readValue(stompMessage.getPayload(), EventMessage.class);
                    if(eventMessage!=null && eventMessage.getType()!=null ){
                        Log.d(TAG, "call: about to make a call");
                        if(eventMessage.getType().equalsIgnoreCase("LOTTERY_WIN")){
                            Log.d(TAG, "call: making a call");
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    updatePlayersResults();
                                }
                            });

                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

    }



    @Override
    protected void onStart() {
        super.onStart();
        updatePlayersResults();

    }



    private void updatePlayersResults(){
        RecyclerView lotterywinnersRecycleView = (RecyclerView) findViewById(R.id.lotterywinners);
        LinearLayoutManager layoutManager = new LinearLayoutManager(StartLotteryActivity.this);
        lotterywinnersRecycleView.setLayoutManager(layoutManager);
        Log.d(TAG, "onStart: " + meetingId_Lottery);
        requestWebServiceInterface = GeneralServiceGenerator.CreateService(RequestWebServiceInterface.class, getApplicationContext());
        Call<List<LotteryPlayer>> callLotteryResult = requestWebServiceInterface.getUserLotteryResult(meetingId_Lottery);

        callLotteryResult.enqueue(new Callback<List<LotteryPlayer>>() {
            @Override
            public void onResponse(Call<List<LotteryPlayer>> call, Response<List<LotteryPlayer>> response) {

                LotteryWinnerAdapter adapter;
                if (response.isSuccessful()) {
//                    Log.i(TAG, "createLottery: " + "1111111111111111");
                    List<LotteryPlayer> lotteryPlayers = response.body();
                    adapter = new LotteryWinnerAdapter(lotteryPlayers);
                    Log.i(TAG, "onResponse is  successful: " + lotteryPlayers.size());
                }else {
                    Log.d(TAG, "onResponse: Nothing received");
                    adapter = new LotteryWinnerAdapter(new ArrayList<>());
                }
                lotterywinnersRecycleView.setAdapter(adapter);
            }

            @Override
            public void onFailure(Call<List<LotteryPlayer>> call, Throwable t) {
                Toast.makeText(StartLotteryActivity.this, "onFailure: " +
                        t.getMessage() + " response from failure", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "onFailure: " + t.getMessage());
                LotteryWinnerAdapter adapter = new LotteryWinnerAdapter(new ArrayList<>());
                lotterywinnersRecycleView.setAdapter(adapter);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult: " + resultCode);
        if (resultCode== Activity.RESULT_OK){
            Bundle bundle = data.getExtras();
//            Log.d(TAG, "onActivityResult: bundle" + bundle.toString());
            if (bundle != null) {
                String result=bundle.getString("result");
                Log.i(TAG, "onActivityResult: " + result);

                Call<LotteryResult> call = requestWebServiceInterface.getLotteryResult(result);
                call.enqueue(new Callback<LotteryResult>() {
                    @Override
                    public void onResponse(Call<LotteryResult> call, Response<LotteryResult> response) {
                        Log.i(TAG, "createLottery: " + "1111111111111111");
                        if (response.isSuccessful()){

                            LotteryResult lotteryResult = response.body();
                            if (lotteryResult!=null && lotteryResult.getMessage()!=null && !lotteryResult.getMessage().isEmpty()){
                                if (lotteryResult.getMessage().startsWith("WIN")){
                                    String[] s = lotteryResult.getMessage().split(" ");

                                    if(s.length>1){
                                        String prize = s[1];
                                        Toast.makeText(StartLotteryActivity.this, "onResponse isSuccessful:" +
                                                " Congratulations! the prize you got is " + prize, Toast.LENGTH_SHORT).show();
                                    }
                                }else {
                                    Toast.makeText(StartLotteryActivity.this, "onResponse isSuccessful:" +
                                            " Sorry, you don't get a prize. Good Luck Next Time.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }else {

                            Toast.makeText(StartLotteryActivity.this, "Played already.", Toast.LENGTH_SHORT).show();
                        }

                    }

                    @Override
                    public void onFailure(Call<LotteryResult> call, Throwable t) {
                        Toast.makeText(StartLotteryActivity.this, "onFailure: " +
                                t.getMessage() + " response from failure", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "onFailure: " + t.getMessage());
                    }

                });
                //launch corresponding event
            }
        } else {

        }

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.start_lottery:
                Log.d(TAG, "onClick: " + "ssssssssssss");
                startActivityForResult(new Intent(StartLotteryActivity.this, CaptureActivity.class),REQUEST_CODE);
                break;
        }
    }


    class LotteryWinnerAdapter extends RecyclerView.Adapter<LotteryWinnerAdapter.ViewHolder> {

        private List<LotteryPlayer> lotteryWinnerList;


        class ViewHolder extends RecyclerView.ViewHolder implements View.OnTouchListener{

            TextView username;
            TextView prize;

            public ViewHolder(View view) {
                super(view);
                username = (TextView) view.findViewById(R.id.lotterywinner_username);
                prize = (TextView) view.findViewById(R.id.lotterywinner_prize);
            }

            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return false;
            }


        }

        public LotteryWinnerAdapter(List<LotteryPlayer> newsList) {
            lotteryWinnerList = newsList;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.lotterywinners_view_holder, parent, false);
            final ViewHolder holder = new ViewHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            LotteryPlayer lotteryPlayer = lotteryWinnerList.get(position);
            if (lotteryPlayer != null){
                holder.username.setText(lotteryPlayer.getName());
                holder.prize.setText(lotteryPlayer.getPrize());
            }
        }

        @Override
        public int getItemCount() {
            return lotteryWinnerList.size();
        }


        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void onViewAttachedToWindow(ViewHolder viewHolder) {
            super.onViewAttachedToWindow(viewHolder);
            animateCircularReveal(viewHolder.itemView);
        }

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        public void animateCircularReveal(View view) {
            int centerX = 0;
            int centerY = 0;
            int startRadius = 0;
            int endRadius = Math.max(view.getWidth(), view.getHeight());
            Animator animation = ViewAnimationUtils.createCircularReveal(view, centerX, centerY, startRadius, endRadius);
            view.setVisibility(View.VISIBLE);
            animation.start();
        }
    }

}

