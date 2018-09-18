package com.meeting.binary.android.binarymeeting.event;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.meeting.binary.android.binarymeeting.R;
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
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import rx.functions.Action1;
import ua.naiksoftware.stomp.Stomp;
import ua.naiksoftware.stomp.client.StompClient;
import ua.naiksoftware.stomp.client.StompMessage;

public class MoreCommentActivity extends AppCompatActivity {

    private StompClient mStompClient;
    private ObjectMapper objectMapper;

    private Event mEvent;
    private List<Comment> mComments;

    private static final String TAG = "MoreComment_TAG";
    private static final String GET_INFO_CONTACT_EVENT_KEY = "com.meeting.binary.event.MyEventFragment.get_info_event_key";

    private int mLastItemComments = -1;


    private RecyclerView mEventCommentRecycler;
    private EventContactCommentAdapter mEventContactCommentAdapter;


    private boolean showDanmaku;
    private CardView cardView;

    private DanmakuView danmakuView;
    private DanmakuContext danmakuContext;
    private BaseDanmakuParser parser = new BaseDanmakuParser() {@Override protected IDanmakus parse() {return new Danmakus();} };

    private EditText mComment_area;
    private Button mSendCommentButton;

    public static Intent newIntent(Context context, Serializable event){
        Bundle args = new Bundle();
        args.putSerializable(GET_INFO_CONTACT_EVENT_KEY, event);
        Intent intent = new Intent(context, MoreCommentActivity.class);
        intent.putExtras(args);
        return intent;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more_comment);

        if (getIntent() != null){
            mEvent = (Event) getIntent().getSerializableExtra(GET_INFO_CONTACT_EVENT_KEY);
            Log.i(TAG, "onCreate: " + mEvent.getDescription());
        } else {
            Log.i(TAG, "onCreate: argument null");
        }

        ((AppCompatActivity)MoreCommentActivity.this).getSupportActionBar().setTitle(mEvent.getTopic() + " Comment Historic");
        ((AppCompatActivity)MoreCommentActivity.this).getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        InputMethodManager lManager = (InputMethodManager)MoreCommentActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);

        mComment_area = findViewById(R.id.comment_area);
        mSendCommentButton = findViewById(R.id.send_comment_button);


        objectMapper = new ObjectMapper();
        String recupCookie = CookiePreferences.getStoredCookie(MoreCommentActivity.this);
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


        mSendCommentButton.setOnClickListener(view1 -> {
            Toast.makeText(this, "clicked", Toast.LENGTH_SHORT).show();
            String commented_text = mComment_area.getText().toString();
            sendComment(commented_text);
            mComment_area.setText("");
        });


        /** allow touch on comment text*/
        mComment_area.setOnClickListener(view1 -> {

            mComment_area.setFocusable(true);
            mComment_area.requestFocusFromTouch();
            lManager.showSoftInput(mComment_area, 0);
        });

        /**create the danmaku view*/
        danmakuContext = DanmakuContext.create();

        //get recycler comment view id
        mEventCommentRecycler = (RecyclerView) findViewById(R.id.recycler_event_list_comment);
        mEventCommentRecycler.setLayoutManager(new LinearLayoutManager(MoreCommentActivity.this));

        requestDataComment();

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

    /**
     * =============================
     * send the comment to be posted
     * =============================
     */
    private void sendComment(String commented_text){

        Comment comment = new Comment();
        comment.setEventId(mEvent.getId());
        comment.setText(commented_text);

        //post comment
        RequestWebServiceInterface requestWebServiceInterface =
                GeneralServiceGenerator.CreateService(RequestWebServiceInterface.class, MoreCommentActivity.this);
        Call<ResponseBody> listCall = requestWebServiceInterface.sendComment(comment);
        listCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()){
                    Toast.makeText(MoreCommentActivity.this, "comment sent", Toast.LENGTH_SHORT).show();
                    Log.i(TAG, "onResponse: comment sent successfully in the server");
                } else {
                    Toast toast = Toast.makeText(MoreCommentActivity.this, "failed to load message", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    Log.d(TAG, "onResponse: bad response");
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
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
                    // addDanmakuCallBack(danmakuMessage.getText());
                    break;

                case "ANNOUNCEMENT":

                    break;

                case "COMMENT":
                    requestDataComment();
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
                    // addDanmakuCallBack(win);
                    break;
                case "AGENDA":
                    break;
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        requestDataComment();
        mComment_area.requestFocus();
//        if (danmakuView != null && danmakuView.isPrepared() && danmakuView.isPaused()) {
//            danmakuView.resume();
//        }
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
        showDanmaku = false;
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
     * ===========================================
     * load the data comment from the web service
     * ===========================================
     */
    public void requestDataComment(){
        RequestWebServiceInterface requestWebServiceInterface =
                GeneralServiceGenerator.CreateService(RequestWebServiceInterface.class, MoreCommentActivity.this);
        Call<List<Comment>> call = requestWebServiceInterface.getEventComment(mEvent.getId());
        call.enqueue(new Callback<List<Comment>>() {
            @Override
            public void onResponse(Call<List<Comment>> call, Response<List<Comment>> response) {
                mComments = response.body();
                if (mComments != null){
                    updateUIComment(mComments);
                } else {
                    Log.d(TAG, "onResponse: comments is null");
                }

            }

            @Override
            public void onFailure(Call<List<Comment>> call, Throwable t) {
                Log.i(TAG, "onFailure: requestDataListComment failed");
            }
        });
    }

    /**
     * ============================
     * sets up Comment AddTodoFragment’s UI
     * ============================
     */
    private void updateUIComment(List<Comment> comments){
        if (mEventContactCommentAdapter == null){
            mEventContactCommentAdapter = new EventContactCommentAdapter(comments);
            mEventCommentRecycler.setAdapter(mEventContactCommentAdapter);
        } else{
            if(mLastItemComments < 0){
                mEventContactCommentAdapter.setComments(comments);
                mEventContactCommentAdapter.notifyDataSetChanged();
            } else{
                mEventContactCommentAdapter.notifyItemChanged(mLastItemComments);
                mLastItemComments = -1;
            }
        }
    }



    /**
     *              The Beginning of the Comment recycler view
     * =====================================================================
     * start classes that describe and set the view for the comment
     * comment adapter to bind the comments to the comment recycler view
     * and comment holder to set the comment view to comment adapter
     * =====================================================================
     */


    /**
     * ====================================================
     * holder to create the view and be bind to the adapter
     * ====================================================
     */
    abstract class EventContactCommentHolderMORE extends RecyclerView.ViewHolder implements View.OnClickListener{

        Comment mComment;

        private TextView mDateCommented;
        private TextView mContactName;
        private TextView mCommentText;
        private ImageView mContactImgMore;


        public EventContactCommentHolderMORE(View itemView) {
            super(itemView);
        }

        public EventContactCommentHolderMORE(LayoutInflater inflater, ViewGroup viewGroup, int layout){
            super(inflater.inflate(layout, viewGroup, false));

            mDateCommented = itemView.findViewById(R.id.event_date_commented);
            mContactName = itemView.findViewById(R.id.name_contact_comment_event);
            mContactImgMore = itemView.findViewById(R.id.contact_image_comment_event);
            mCommentText = itemView.findViewById(R.id.topic_comment_page_event);
        }

        @Override
        public void onClick(View view) {
            mLastItemComments = this.getAdapterPosition();
        }

        public void bind(Comment comment){
            mComment = comment;
            mDateCommented.setText(DateUtils.getReadableModifyDate(mComment.getTime()));
            mContactName.setText(mComment.getName());
            mCommentText.setText(mComment.getText());

            if (comment.getUsername() != null){
                String recupCookie = CookiePreferences.getStoredCookie(MoreCommentActivity.this);
                Log.i(TAG, "intercept: " + recupCookie);
                LazyHeaders.Builder builder = new LazyHeaders.Builder().addHeader("Cookie", recupCookie);
                GlideUrl glideUrl = new GlideUrl(BaseUrlGenerator.BINARY_BASE_URL + "/profile/mypicture/" + comment.getUsername(), builder.build());

                Glide.with(MoreCommentActivity.this)
                        .load(glideUrl)
                        .asBitmap()
                        .centerCrop()
                        .placeholder(getResources().getDrawable(R.drawable.photo_no_image))
                        .into(new BitmapImageViewTarget(mContactImgMore) {
                            @Override
                            protected void setResource(Bitmap resource) {
                                RoundedBitmapDrawable drawable = RoundedBitmapDrawableFactory.create(MoreCommentActivity.this.getResources(),
                                        Bitmap.createScaledBitmap(resource, 45, 45, false));
                                drawable.setCircular(true);
                                mContactImgMore.setImageDrawable(drawable);
                            }
                        });
                Log.i(TAG, "bind: " + comment.getUsername());
            }
        }
    }



    /**
     * the holder that is purchased when i am commented
     */
    class EventCommentHolderMe extends EventContactCommentHolderMORE {
        public EventCommentHolderMe(LayoutInflater inflater, ViewGroup viewGroup) {
            super(inflater, viewGroup, R.layout.list_more_comment_list_me);
        }
    }




    /**
     * the holder that is purchased when i am commented
     */
    class EventCommentHolderOther extends EventContactCommentHolderMORE {
        public EventCommentHolderOther(LayoutInflater inflater, ViewGroup viewGroup) {
            super(inflater, viewGroup, R.layout.list_more_comment_list);
        }
    }




    /**
     * ==================================
     * adapter to be set in the recycler
     * ==================================
     */
    class EventContactCommentAdapter extends RecyclerView.Adapter<EventContactCommentHolderMORE>{

        List<Comment> mComments;

        public EventContactCommentAdapter(List<Comment> comments) {
            mComments = comments;
        }

        @Override
        public EventContactCommentHolderMORE onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(MoreCommentActivity.this);
            if (viewType == 0){
                return new EventCommentHolderMe(inflater, parent);
            } else {
                return new EventCommentHolderOther(inflater, parent);
            }
        }

        @Override
        public void onBindViewHolder(EventContactCommentHolderMORE holder, int position) {
            holder.bind(mComments.get(position));
        }

        @Override
        public int getItemCount() {
            return mComments.size();
        }

        public void setComments(List<Comment> comments) {
            mComments = comments;
        }

        @Override
        public int getItemViewType(int position) {
            Log.i("special", "getItemViewType: " + mComments.get(position).getUsername());
            Log.i("special", "getItemViewType: " + CookiePreferences.getStoredName(MoreCommentActivity.this));
            if (mComments.get(position).getUsername().equals(CookiePreferences.getStoredName(MoreCommentActivity.this))){
                return 0;
            } else{
                return 1;
            }
        }
    }

    /**
     *                   The End of the first recycler view
     * =====================================================================
     * end of the  classes that describe and set the view for the comment
     * comment adapter to bind the comments to the comment recycler view
     * and comment holder to set the comment view to comment adapter
     * =====================================================================
     */



}
