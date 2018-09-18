package com.meeting.binary.android.binarymeeting.event;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.meeting.binary.android.binarymeeting.R;
import com.meeting.binary.android.binarymeeting.contact.ContactEventFragment;
import com.meeting.binary.android.binarymeeting.model.Announcement;
import com.meeting.binary.android.binarymeeting.model.Comment;
import com.meeting.binary.android.binarymeeting.model.DanmakuMessage;
import com.meeting.binary.android.binarymeeting.model.Event;
import com.meeting.binary.android.binarymeeting.model.EventMessage;
import com.meeting.binary.android.binarymeeting.model.WinLottery;
import com.meeting.binary.android.binarymeeting.service.cookie.CookiePreferences;
import com.meeting.binary.android.binarymeeting.service.generetor.BaseUrlGenerator;
import com.meeting.binary.android.binarymeeting.service.generetor.GeneralServiceGenerator;
import com.meeting.binary.android.binarymeeting.service.request_interface.RequestWebServiceInterface;
import com.meeting.binary.android.binarymeeting.utils.DateUtils;

import org.java_websocket.WebSocket;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import master.flame.danmaku.controller.DrawHandler;
import master.flame.danmaku.danmaku.model.BaseDanmaku;
import master.flame.danmaku.danmaku.model.DanmakuTimer;
import master.flame.danmaku.danmaku.model.IDanmakus;
import master.flame.danmaku.danmaku.model.android.DanmakuContext;
import master.flame.danmaku.danmaku.model.android.Danmakus;
import master.flame.danmaku.danmaku.parser.BaseDanmakuParser;
import master.flame.danmaku.ui.widget.DanmakuView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import rx.functions.Action1;
import ua.naiksoftware.stomp.Stomp;
import ua.naiksoftware.stomp.client.StompClient;
import ua.naiksoftware.stomp.client.StompMessage;

public class MoreAnnouncementActivity extends AppCompatActivity {

    private static final String EXTRA_MSG_ID = "eventidmoreactivity";
    private StompClient mStompClient;
    private ObjectMapper objectMapper;

    private Event mEvent;
    private List<Comment> mComments;

    private static final String TAG = "MoreComment_TAG";
    private static final String GET_INFO_CONTACT_EVENT_KEY = "com.meeting.binary.event.MyEventFragment.get_info_event_key";

    private int mLastItemAnnouncement = -1;

    private RecyclerView mEventAnnouncementRecycler;
    private EventContactAnnouncementAdapter mEventAnnouncementAdapter;

    private boolean showDanmaku;
    private CardView cardView;

    private DanmakuView danmakuView;
    private DanmakuContext danmakuContext;
    private BaseDanmakuParser parser = new BaseDanmakuParser() {@Override protected IDanmakus parse() {return new Danmakus();} };


    private List<Announcement> mAnnouncements;


    public static Intent newIntent(Context context, Serializable event){
        Bundle args = new Bundle();
        args.putSerializable(GET_INFO_CONTACT_EVENT_KEY, event);
        Intent intent = new Intent(context, MoreAnnouncementActivity.class);
        intent.putExtras(args);
        return intent;
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more_announcement);
        if (getIntent() != null){
            mEvent = (Event) getIntent().getSerializableExtra(GET_INFO_CONTACT_EVENT_KEY);
            Log.i(TAG, "onCreate: " + mEvent.getDescription());
        } else {
            Log.i(TAG, "onCreate: argument null");
        }

        ((AppCompatActivity)MoreAnnouncementActivity.this).getSupportActionBar().setTitle(mEvent.getTopic() + " Announcement");
        ((AppCompatActivity)MoreAnnouncementActivity.this).getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        objectMapper = new ObjectMapper();
        String recupCookie = CookiePreferences.getStoredCookie(MoreAnnouncementActivity.this);
        Map<String,String> headers = new HashMap<>();
        headers.put("Cookie",recupCookie);
        mStompClient = Stomp.over(WebSocket.class, "ws://" + BaseUrlGenerator.BASE_DOMAIN +"/stompwebsocket",headers);
        mStompClient.connect();
        registerToPrivateStompTopic();
        registerToPublicStompTopic();

        /*** handle and display the danmaku ***/
//        cardView = (CardView) findViewById(R.id.cc_view);
//        danmakuView =(DanmakuView) findViewById(R.id.danmaku_view);
//        danmakuView.enableDanmakuDrawingCache(true);


        /**create the danmaku view*/
//        danmakuContext = DanmakuContext.create();

//get recycler comment view id
        mEventAnnouncementRecycler = (RecyclerView) findViewById(R.id.recycler_event_list_announcement);
        mEventAnnouncementRecycler.setLayoutManager(new LinearLayoutManager(MoreAnnouncementActivity.this));

        requestDataAnnouncement();
    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default: return super.onOptionsItemSelected(item);
        }
    }



    /**add danmaku*/
//    private void addDanmakuCallBack(String content){
//        danmakuView.enableDanmakuDrawingCache(true);
//        danmakuView.setCallback(new DrawHandler.Callback() {
//            @Override
//            public void prepared() {
//                showDanmaku = true;
//                danmakuView.start();
//                Log.i(TAG, "prepared: in the danmaku");
//                addDanmaku(content, false);
//            }
//
//            @Override
//            public void updateTimer(DanmakuTimer timer) {
//
//            }
//
//            @Override
//            public void danmakuShown(BaseDanmaku danmaku) {
//
//            }
//
//            @Override
//            public void drawingFinished() {
//
//            }
//        });
//
//        /**create the danmaku view*/
//        danmakuContext = DanmakuContext.create();
//        danmakuView.prepare(parser, danmakuContext);
//    }


    /**
     * ===========================================
     * receive data for danmaku from the websocket
     * ============================================
     */

    private void registerToPrivateStompTopic() {
        mStompClient.topic("/user/queue/event/"+mEvent.getId()).subscribe(new Action1<StompMessage>() {
            @Override
            public void call(StompMessage stompMessage) {
                Log.d(TAG, "call: " +stompMessage.getPayload() );
                try {
                    EventMessage eventMessage = objectMapper.readValue(stompMessage.getPayload(), EventMessage.class);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                showMessage(eventMessage);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    private void registerToPublicStompTopic() {
        mStompClient.topic("/topic/event/"+mEvent.getId()).subscribe(new Action1<StompMessage>() {
            @Override
            public void call(StompMessage stompMessage) {
                Log.d(TAG, "call: " +stompMessage.getPayload() );
                try {
                    EventMessage eventMessage = objectMapper.readValue(stompMessage.getPayload(), EventMessage.class);
                    try {
                        showMessage(eventMessage);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }



    private void showMessage(EventMessage message) throws IOException {

        String content = message.getContent();

        if(content!=null && !content.isEmpty()) {
            switch (message.getType().toUpperCase()) {

                case "BULLET":
                    DanmakuMessage danmakuMessage
                            = objectMapper.readValue(content,DanmakuMessage.class);
                    Log.i(TAG, "showMessage: " + danmakuMessage.getText());
                    /*** Danmaku code here ***/
//                    addDanmakuCallBack(danmakuMessage.getText());
                    break;

                case "ANNOUNCEMENT":
                        requestDataAnnouncement();
                    break;

                case "COMMENT":
                    break;

                case "VOTE":
                    break;
                case "FILE":
                    break;
                case "LOTTERY":
                    break;
                case "LOTTERY_WIN":
                    WinLottery lottery = objectMapper.readValue(content, WinLottery.class);
                    String win = lottery.getName() + " wins the " + lottery.getPrize();
//                    addDanmakuCallBack(win);
                    break;
                case "AGENDA":
                    break;
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        requestDataAnnouncement();
//        if (danmakuView != null && danmakuView.isPrepared() && danmakuView.isPaused()) {
//            danmakuView.resume();
//        }
    }



    /**
     * ===========================================
     * load the data Announcement from the web service
     * ===========================================
     */
    public void requestDataAnnouncement(){
        RequestWebServiceInterface requestWebServiceInterface =
                GeneralServiceGenerator.CreateService(RequestWebServiceInterface.class, MoreAnnouncementActivity.this);
        Call<List<Announcement>> call = requestWebServiceInterface.getEventAnnouncemennt(mEvent.getId());
        call.enqueue(new Callback<List<Announcement>>() {
            @Override
            public void onResponse(Call<List<Announcement>> call, Response<List<Announcement>> response) {
                mAnnouncements = response.body();
                if (mAnnouncements != null){
                    updateUIAnnouncement(mAnnouncements);
                } else {
                    Log.d(TAG, "onResponse: announcement is null");
                }
            }

            @Override
            public void onFailure(Call<List<Announcement>> call, Throwable t) {
                Log.i(TAG, "onFailure: requestDataListAnnouncement failed");
            }
        });
    }



    /**
     * =========================================
     * sets up Announcement AddTodoFragment’s UI
     * =========================================
     */
    private void updateUIAnnouncement(List<Announcement> announcements){
        if (mEventAnnouncementAdapter == null){
            mEventAnnouncementAdapter = new EventContactAnnouncementAdapter(announcements);
            mEventAnnouncementRecycler.setAdapter(mEventAnnouncementAdapter);
        } else{
            if(mLastItemAnnouncement < 0){
                mEventAnnouncementAdapter.setAnnouncements(announcements);
                mEventAnnouncementAdapter.notifyDataSetChanged();
            } else{
                mEventAnnouncementAdapter.notifyItemChanged(mLastItemAnnouncement);
                mLastItemAnnouncement = -1;
            }
        }
    }




    @Override
    public void onPause() {
        super.onPause();
//        if (danmakuView != null && danmakuView.isPrepared()) {
//            danmakuView.pause();
//        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        showDanmaku = false;
//        if (danmakuView != null) {
//            danmakuView.release();
//            danmakuView = null;
//        }
    }



    /**handle the  danmaku*/
//    private void addDanmaku(String content, boolean withBorder) {
//        BaseDanmaku danmaku = danmakuContext.mDanmakuFactory.createDanmaku(BaseDanmaku.TYPE_SCROLL_RL);
//        danmaku.text = content;
//        danmaku.padding = 5;
//        danmaku.textSize = sp2px(20);
//        danmaku.textColor = Color.WHITE;
//        danmaku.setTime(danmakuView.getCurrentTime());
//        if (withBorder) {
//            danmaku.borderColor = Color.GREEN;
//        }
//        danmakuView.addDanmaku(danmaku);
//    }

    /**
     * 随机生成一些弹幕内容以供测试
     */
//    private void generateSomeDanmaku() {
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                while(showDanmaku) {
//                    int time = new Random().nextInt(300);
//                    String content = "" + time + time;
//                    addDanmaku(content, false);
//                    try {
//                        Thread.sleep(time);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//        }).start();
//    }

    /**
     * sp转px的方法。
     */
    public int sp2px(float spValue) {
        final float fontScale = getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }



    /**
     *              The Beginning of the Announcement recycler view
     * =====================================================================
     * start classes that describe and set the view for the Announcement
     * announcement adapter to bind the announcement to the announcement recycler view
     * and announcement holder to set the announcement view to announcement adapter
     * =====================================================================
     */

    /**
     * ====================================================
     * holder to create the view and be bind to the adapter
     * ====================================================
     */
    class EventContactAnnouncementHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private Announcement mAnnouncement;

        private TextView mAnnounceText;
        private TextView mAnnounceDate;

        public EventContactAnnouncementHolder(View itemView) {
            super(itemView);
        }

        public EventContactAnnouncementHolder(LayoutInflater inflater, ViewGroup parent){
            super(inflater.inflate(R.layout.list_more_announcement, parent, false));
            mAnnounceText =  itemView.findViewById(R.id.announce_text);
            mAnnounceDate = itemView.findViewById(R.id.announced_date);
        }

        @Override
        public void onClick(View view) {
            mLastItemAnnouncement = this.getAdapterPosition();
        }

        public void bind(Announcement announcement){
            mAnnouncement = announcement;
            mAnnounceDate.setText(DateUtils.getReadableModifyDate(mAnnouncement.getTime()));
            mAnnounceText.setText(mAnnouncement.getText());
        }
    }


    /**
     * ==================================
     * adapter to be set in the recycler
     * ==================================
     */
    class EventContactAnnouncementAdapter extends RecyclerView.Adapter<EventContactAnnouncementHolder>{

        List<Announcement> mAnnouncements;

        public EventContactAnnouncementAdapter(List<Announcement> announcements) {
            mAnnouncements = announcements;
        }

        @Override
        public EventContactAnnouncementHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(MoreAnnouncementActivity.this);
            return new EventContactAnnouncementHolder(inflater, parent);
        }

        @Override
        public void onBindViewHolder(EventContactAnnouncementHolder holder, int position) {
            holder.bind(mAnnouncements.get(position));
        }

        @Override
        public int getItemCount() {
            return mAnnouncements.size();
        }

        public void setAnnouncements(List<Announcement> announcements) {
            mAnnouncements = announcements;
        }
    }

    /**
     *                   The End of the second recycler view
     * =====================================================================
     * end classes that describe and set the view for the Announcement
     * announcement adapter to bind the announcement to the announcement recycler view
     * and announcement holder to set the announcement view to announcement adapter
     * =====================================================================
     */
}
