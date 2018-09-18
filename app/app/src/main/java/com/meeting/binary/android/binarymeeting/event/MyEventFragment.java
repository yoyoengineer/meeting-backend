package com.meeting.binary.android.binarymeeting.event;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
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
import com.meeting.binary.android.binarymeeting.contact.ContactLikeEventDialogFragment;
import com.meeting.binary.android.binarymeeting.contact.ContactPageActivity;
import com.meeting.binary.android.binarymeeting.contact.GenerateQrCodeDialogFragment;
import com.meeting.binary.android.binarymeeting.contact.GenerateQrCodeDialogFragmentAdmin;
import com.meeting.binary.android.binarymeeting.event.admin.AdminActivity;
import com.meeting.binary.android.binarymeeting.event.admin.ListAttendeesActivity;
import com.meeting.binary.android.binarymeeting.event.admin.PollActivity;
import com.meeting.binary.android.binarymeeting.event.upload_download.UploadDocumentMainActivity;
import com.meeting.binary.android.binarymeeting.event.upload_download.UploadPhotoMainActivity;
import com.meeting.binary.android.binarymeeting.model.Announcement;
import com.meeting.binary.android.binarymeeting.model.Comment;
import com.meeting.binary.android.binarymeeting.model.Contact;
import com.meeting.binary.android.binarymeeting.model.DanmakuMessage;
import com.meeting.binary.android.binarymeeting.model.Event;
import com.meeting.binary.android.binarymeeting.model.EventMessage;
import com.meeting.binary.android.binarymeeting.model.WinLottery;
import com.meeting.binary.android.binarymeeting.other.notes.ListNoteActivity;
import com.meeting.binary.android.binarymeeting.service.cookie.CookiePreferences;
import com.meeting.binary.android.binarymeeting.service.generetor.BaseUrlGenerator;
import com.meeting.binary.android.binarymeeting.service.generetor.GeneralServiceGenerator;
import com.meeting.binary.android.binarymeeting.service.request_interface.RequestWebServiceInterface;
import com.meeting.binary.android.binarymeeting.service.websocket.WebSocketStomptConfig;
import com.meeting.binary.android.binarymeeting.utils.DateUtils;
import com.xys.libzxing.zxing.activity.CaptureActivity;

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
import master.flame.danmaku.danmaku.model.Duration;
import master.flame.danmaku.danmaku.model.IDanmakus;
import master.flame.danmaku.danmaku.model.IDisplayer;
import master.flame.danmaku.danmaku.model.R2LDanmaku;
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

/**
 * Created by meldi on 3/17/2018.
 */

public class MyEventFragment extends Fragment implements View.OnClickListener{

    private StompClient mStompClient;
    private ObjectMapper objectMapper;

    private Event mEvent;
    private List<Comment> mComments;
    private List<Announcement> mAnnouncements;

    private static final int REQUEST_CODE = 0;
    private static final String TAG_REQUEST = "qr_code";

    private static final String DIALOG_LIKED_EVENT = "DialogLikeEvent";
    private static final String TAG = "MyEventFragment.TAG";
    private static final String GET_INFO_EVENT_KEY = "com.meeting.binary.event.MyEventFragment.get_info_event_key";

    private int mLastItemComments = -1;
    private int mLastItemAnnouncement = -1;

    private boolean mCommentCollapsed = false;
    private boolean mCollapseWriteComment = true;
    private boolean mAnnouncementCollapsedRecycler = false;

    /** comment recycler*/
    private RecyclerView mEventCommentRecycler;
    private EventCommentAdapter mEventCommentAdapter;

    /** announce recycler*/
    private RecyclerView mEventAnnouncementRecycler;
    private EventAnnouncementAdapter mEventAnnouncementAdapter;

    /**danmaku variable*/
    private boolean showDanmaku;
    private CardView cardView;
    private DanmakuView danmakuView;
    private DanmakuContext danmakuContext;
    private BaseDanmakuParser parser = new BaseDanmakuParser() {@Override protected IDanmakus parse() {return new Danmakus();} };


    private TextView mCollapseCommentText;
    private EditText mLiveInteract;
    private EditText mComment_area;
    private Button mSendCommentButton;
    private Button mSend_interact;
    private TextView mEventDateCreated;
    private TextView mEventTopic;
    private CardView mEventAbout;
    private ImageView mEventAdmin;
    private ImageView mEventAttendees;
    private ImageView mEventPool;
    private ImageView mEventDocuments;
    private ImageView mEventPhotos;
    private ImageView mEventNotes;
    private TextView mCollapseComment;
    private ImageView mSeePeopleLike;
    private ImageView mDisplaySendComment;
    private CardView mViewMoreComment;
    private CardView mViewMoreAnnouncement;
    private TextView mCollapseAnnouncement;



    public static MyEventFragment newInstance(Serializable event){
        Bundle args = new Bundle();
        MyEventFragment eventFragment = new MyEventFragment();
        args.putSerializable(GET_INFO_EVENT_KEY, event);
        eventFragment.setArguments(args);
        return eventFragment;
    }



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        objectMapper = new ObjectMapper();

        if (getArguments() != null){
            mEvent = (Event) getArguments().getSerializable(GET_INFO_EVENT_KEY);
        } else {
            Log.i(TAG, "onCreate: argument null");
        }


        /**
         * load data before action require
         */

    }




    /**
     * ===========================================
     * load the data comment from the web service
     * ===========================================
     */
    public void requestDataComment(){
        RequestWebServiceInterface requestWebServiceInterface =
                GeneralServiceGenerator.CreateService(RequestWebServiceInterface.class, getActivity());
        Call<List<Comment>> call = requestWebServiceInterface.getEventComment(mEvent.getId());
        call.enqueue(new Callback<List<Comment>>() {
            @Override
            public void onResponse(Call<List<Comment>> call, Response<List<Comment>> response) {
                mComments = response.body();
                if (mComments != null){
                    if(mComments.size() > 6){
                        List<Comment> comments = new ArrayList<>();
                        for (int i=0; i < 6; i++){
                            comments.add(mComments.get(i));
                        }
                        updateUIComment(comments);
                    }else {
                        updateUIComment(mComments);
                    }
                    collapseCommentView();
                } else {
                    Log.d(TAG, "onResponse: not response from server");
                }
            }

            @Override
            public void onFailure(Call<List<Comment>> call, Throwable t) {
                Log.i(TAG, "onFailure: requestDataListComment failed");
            }
        });
    }




    /**
     * ===========================================
     * load the data Announcement from the web service
     * ===========================================
     */
    public void requestDataAnnouncement(){
        RequestWebServiceInterface requestWebServiceInterface =
                GeneralServiceGenerator.CreateService(RequestWebServiceInterface.class, getActivity());
        Call<List<Announcement>> call = requestWebServiceInterface.getEventAnnouncemennt(mEvent.getId());
        call.enqueue(new Callback<List<Announcement>>() {
            @Override
            public void onResponse(Call<List<Announcement>> call, Response<List<Announcement>> response) {
                mAnnouncements = response.body();
                if (mAnnouncements != null){
                    if(mAnnouncements.size() > 6){
                        List<Announcement> announcements = new ArrayList<>();
                        for (int i=0; i < 6; i++){
                            announcements.add(mAnnouncements.get(i));
                        }
                        updateUIAnnouncement(announcements);
                    }else {
                        updateUIAnnouncement(mAnnouncements);
                    }
                    collapseAnnouncementRecyclerView();
                } else {
                    Log.i(TAG, "onResponse: failed to receive from server");
                }
            }

            @Override
            public void onFailure(Call<List<Announcement>> call, Throwable t) {
                Log.i(TAG, "onFailure: requestDataListAnnouncement failed");
            }
        });
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.my_event_fragment, container, false);

        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(mEvent.getTopic());
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        InputMethodManager lManager = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);


        //View v = getActivity().getWindow().getDecorView();
        int orientation = getResources().getConfiguration().orientation;
        if (Configuration.ORIENTATION_LANDSCAPE == orientation) {
            view.setBackgroundResource (R.drawable.otherbackcampus);
        } else {
            view.setBackgroundResource (R.drawable.otherbackcampus);
        }

        String recupCookie = CookiePreferences.getStoredCookie(getActivity());
        Map<String,String> headers = new HashMap<>();
        headers.put("Cookie",recupCookie);
        mStompClient = Stomp.over(WebSocket.class, "ws://" + BaseUrlGenerator.BASE_DOMAIN +"/stompwebsocket",headers);
        mStompClient.connect();
        registerToPrivateStompTopic();
        registerToPublicStompTopic();


        /** get widget id*/
        mEventDateCreated = view.findViewById(R.id.event_date_created);
        mEventTopic = view.findViewById(R.id.event_topic);
        mEventAdmin = view.findViewById(R.id.event_admin);
        mEventAttendees = view.findViewById(R.id.event_attendees);
        mEventPool = view.findViewById(R.id.event_pool);
        mEventDocuments = view.findViewById(R.id.event_documents);
        mEventPhotos = view.findViewById(R.id.event_photos);
        mEventNotes = view.findViewById(R.id.event_notes);
        mCollapseComment = view.findViewById(R.id.collapse_comment);
        mSeePeopleLike = view.findViewById(R.id.view_people_like);
        mLiveInteract = view.findViewById(R.id.live_interact);
        mCollapseCommentText = view.findViewById(R.id.collapse_comment_textview);
        mSend_interact = view.findViewById(R.id.send_interact);
        mComment_area = view.findViewById(R.id.comment_area);
        mSendCommentButton = view.findViewById(R.id.send_comment_button);
        mDisplaySendComment = view.findViewById(R.id.send_comment);
        mCollapseAnnouncement = view.findViewById(R.id.collapse_announcement);
        mViewMoreComment = view.findViewById(R.id.view_more_comment);
        mViewMoreAnnouncement = view.findViewById(R.id.view_more_announcement);

        mEventDateCreated.setText(mEvent.getReadableModifyDate(mEvent.getTime()));
        mEventTopic.setText(mEvent.getTopic());


        /**view more comment*/
        mViewMoreComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = MoreCommentActivity.newIntent(getActivity(), mEvent);
                startActivity(intent);
            }
        });


        /**view more announcement*/
        mViewMoreAnnouncement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = MoreAnnouncementActivity.newIntent(getActivity(), mEvent);
                startActivity(intent);
            }
        });


        /**collapse, display the commented area and send the comment*/
        mCollapseCommentText.setOnClickListener(view1 -> collapseWithText());
        mDisplaySendComment.setOnClickListener(view1 -> collapseWriteCommentView());
        mSendCommentButton.setOnClickListener(view1 -> {
            Toast.makeText(getActivity(), "clicked", Toast.LENGTH_SHORT).show();
            String commented_text = mComment_area.getText().toString();
            sendComment(commented_text);
            mComment_area.setText("");
        });

        mLiveInteract.setOnClickListener(view1 -> {
            mLiveInteract.setFocusable(true);
            mLiveInteract.requestFocusFromTouch();
            lManager.showSoftInput(mLiveInteract, 0);
        });


        /** open a dialog that display the number of people that liked that event*/
        /** open a dialog that display the number of people that liked that event*/
        mSeePeopleLike.setOnClickListener(view1 -> {
            requestLoadContactLike();
        });

        /**send danmaku*/
        mSend_interact.setOnClickListener(view1 ->  {
            String content = mLiveInteract.getText().toString();
            if (!TextUtils.isEmpty(content)) {
                requestSendLiveInteract(content);
                mLiveInteract.setText("");
            }
        });

        /** allow touch on comment text*/
        mComment_area.setOnClickListener(view1 -> {
            mComment_area.setFocusable(true);
            mComment_area.requestFocusFromTouch();
            lManager.showSoftInput(mComment_area, 0);
        });


        /**get recycler comment view id*/
        mEventCommentRecycler = view.findViewById(R.id.recycler_event_list_comment);
        mEventCommentRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));

        /**get recycler comment view id*/
        mEventAnnouncementRecycler = view.findViewById(R.id.recycler_event_list_announcement);
        mEventAnnouncementRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));

        requestDataAnnouncement();
        requestDataComment();

        /**load widget after getting the dada*/
        mEventAdmin.setOnClickListener(this);
        mEventAttendees.setOnClickListener(this);
        mEventPool.setOnClickListener(this);
        mEventDocuments.setOnClickListener(this);
        mEventPhotos.setOnClickListener(this);
        mEventNotes.setOnClickListener(this);


        /**handle and display the danmaku*/
        cardView = view.findViewById(R.id.cc_view);
        danmakuView = view.findViewById(R.id.danmaku_view);
//        danmakuView.enableDanmakuDrawingCache(true);


      init();

      return view;
    }




    @Override
    public void onResume() {
        super.onResume();
        mLiveInteract.requestFocus();
        mComment_area.requestFocus();
        requestDataComment();
        requestDataAnnouncement();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }


    //    @Override
//    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
//        super.onCreateOptionsMenu(menu, inflater);
//        inflater.inflate(R.menu.my_event_fragment, menu);
//
//        /**
//         * retrieve the MenuItem in charge of the icon
//         * keep track of the event state visibility
//         */
//        MenuItem iconItem = menu.findItem(R.id.action_scan_generate);
//        if (mEvent.isEventPublicState()){
//            iconItem.setIcon(R.drawable.scan_icon_white);
//        } else {
//            iconItem.setIcon(R.drawable.generate);
//        }
//    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_scan_generate:
                if (mEvent.isEventPublicState()){
                    generate();
                } else{
                    scan();
                }
                return true;
            case android.R.id.home:
                getActivity().onBackPressed();
                return true;
            default: return super.onOptionsItemSelected(item);
        }
    }


    private void generate(){
        FragmentManager fm = getFragmentManager();
        GenerateQrCodeDialogFragmentAdmin fragment = GenerateQrCodeDialogFragmentAdmin.newInstance(mEvent.getId());
        fragment.show(fm, TAG_REQUEST);
    }


    private void scan(){
        startActivityForResult(new Intent(getActivity(), CaptureActivity.class), REQUEST_CODE);
    }


    /**the result we got back from the scan*/
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult: " + resultCode);
        if (resultCode== Activity.RESULT_OK){
            Bundle bundle = data.getExtras();
            Log.d(TAG, "onActivityResult: bundle" + bundle.toString());
            if (bundle != null) {
                String result=bundle.getString("result");
                Log.i(TAG, "onActivityResult: " + result);
                //launch corresponding event
            }
        } else {

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
                GeneralServiceGenerator.CreateService(RequestWebServiceInterface.class, getActivity());
        Call<ResponseBody> listCall = requestWebServiceInterface.sendComment(comment);
        listCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()){
                    Toast.makeText(getActivity(), "comment sent", Toast.LENGTH_SHORT).show();
                    Log.i(TAG, "onResponse: comment sent successfully in the server");
                } else {
                    Toast toast = Toast.makeText(getActivity(), "failed to load message", Toast.LENGTH_LONG);
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




    private void init() {
        danmakuContext = DanmakuContext.create();

        // 设置最大显示行数
        HashMap<Integer, Integer> maxLinesPair = new HashMap<>();
        maxLinesPair.put(BaseDanmaku.TYPE_SCROLL_RL, 2); // 滚动弹幕最大显示5行

        // 设置是否禁止重叠
        HashMap<Integer, Boolean> overlappingEnablePair = new HashMap<>();
        overlappingEnablePair.put(BaseDanmaku.TYPE_SCROLL_RL, true);
        overlappingEnablePair.put(BaseDanmaku.TYPE_FIX_TOP, true);

        danmakuContext.setDanmakuStyle(IDisplayer.DANMAKU_STYLE_STROKEN, 10) //描边的厚度
                .setDuplicateMergingEnabled(false)
                .setScrollSpeedFactor(1.2f) //弹幕的速度。注意！此值越小，速度越快！值越大，速度越慢。// by phil
                .setScaleTextSize(1.2f)  //缩放的值
                //.setCacheStuffer(new SpannedCacheStuffer(), mCacheStufferAdapter) // 图文混排使用SpannedCacheStuffer
//        .setCacheStuffer(new BackgroundCacheStuffer())  // 绘制背景使用BackgroundCacheStuffer
                .setMaximumLines(maxLinesPair)
                .preventOverlapping(overlappingEnablePair);

//        mParser = new AcFunDanmakuParser();
        danmakuView.prepare(parser, danmakuContext);

        //mDanmakuView.showFPS(true);
        danmakuView.enableDanmakuDrawingCache(true);

        if (danmakuView != null) {
            danmakuView.setCallback(new DrawHandler.Callback() {
                @Override
                public void updateTimer(DanmakuTimer timer) {
                }

                @Override
                public void drawingFinished() {

                }

                @Override
                public void danmakuShown(BaseDanmaku danmaku) {
                    Log.d("弹幕文本", "danmakuShown text=" + danmaku.text);
                }

                @Override
                public void prepared() {
                    danmakuView.start();
                }
            });
        }
    }

//

    /**
     * ===============
     *Danmaku reaquest
     * ===============
     */
    private void requestSendLiveInteract(String content){
        /**loading data process*/
        DanmakuMessage message = new DanmakuMessage();
        message.setText(content);
        RequestWebServiceInterface requestWebServiceInterface =
                GeneralServiceGenerator.CreateService(RequestWebServiceInterface.class, getActivity());
        Call<ResponseBody> listCall = requestWebServiceInterface.bullet(message, mEvent.getId());
        listCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()){
                    Log.i(TAG, "onResponse: message sent successfully in the server");
                } else {
                    Toast toast = Toast.makeText(getActivity(), "failed to load message", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    Log.d(TAG, "onResponse: bad response");
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d(TAG, "onFailure: failed to request");
            }
        });
    }

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
                    getActivity().runOnUiThread(new Runnable() {
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
                    addDanmakuCallBack(danmakuMessage.getText());
                    break;

                case "ANNOUNCEMENT":
                    requestDataAnnouncement();
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
                    addDanmakuCallBack(win);
                    break;
                case "AGENDA":
                    break;
            }
        }
    }


    /**
     * ==========================================
     * load the contact data from the web service
     * ==========================================
     */
    private void requestLoadContactLike(){
        /**loading data process*/
        RequestWebServiceInterface requestWebServiceInterface =
                GeneralServiceGenerator.CreateService(RequestWebServiceInterface.class, getActivity());
        Call<List<Contact>> listCall = requestWebServiceInterface.getContactsLike(mEvent.getId());
        listCall.enqueue(new Callback<List<Contact>>() {
            @Override
            public void onResponse(Call<List<Contact>> call, Response<List<Contact>> response) {
                if (response.isSuccessful()){
                    List<Contact> contacts = response.body();
                    FragmentManager manager = getFragmentManager();
                    ContactLikeEventDialogFragment dialogFragment = ContactLikeEventDialogFragment.newInstance((Serializable)contacts);
                    dialogFragment.show(manager, DIALOG_LIKED_EVENT);
                } else {
                    Toast toast = Toast.makeText(getActivity(), "failed to load contacts", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    Log.d(TAG, "onResponse: bad response");
                }
            }

            @Override
            public void onFailure(Call<List<Contact>> call, Throwable t) {
                Log.d(TAG, "onFailure: failed to request");
            }
        });
    }


    /**
     * ==========================================
     * load the contact data from the web service
     * ==========================================
     */
    private void requestLoadContactAttendees(){
        /**loading data process*/
        RequestWebServiceInterface requestWebServiceInterface =
                GeneralServiceGenerator.CreateService(RequestWebServiceInterface.class, getActivity());
        Call<List<Contact>> listCall = requestWebServiceInterface.getContactsAttendes(mEvent.getId());
        listCall.enqueue(new Callback<List<Contact>>() {
            @Override
            public void onResponse(Call<List<Contact>> call, Response<List<Contact>> response) {
                if (response.isSuccessful()){
                    List<Contact> contacts = response.body();
                    Intent intent = ListAttendeesActivity.newIntent(getActivity(), (Serializable)contacts);
                    startActivity(intent);
                } else {
                    Toast toast = Toast.makeText(getActivity(), "failed to load contacts", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    Log.d(TAG, "onResponse: bad response");
                }
            }

            @Override
            public void onFailure(Call<List<Contact>> call, Throwable t) {
                Log.d(TAG, "onFailure: failed to request");
            }
        });
    }


    /**
     *                      Start collapsing
     * ===============================================================================
     * collapse the comment view to give a better perspective view to the announcement
     * ===============================================================================
     */
    private void collapseCommentView() {
        mCollapseComment.setOnClickListener(view1 -> {
            if (!mCommentCollapsed){
                mCollapseComment.setText(R.string.view_comment);
                mCollapseComment.setTextColor(getResources().getColor(R.color.primary_blue));
                mEventCommentRecycler.setVisibility(View.GONE);
                mCommentCollapsed = true;
            } else {
                mCollapseComment.setText(R.string.collaspse_comment);
                mCollapseComment.setTextColor(getResources().getColor(R.color.purple));
                mEventCommentRecycler.setVisibility(View.VISIBLE);
                mCommentCollapsed = false;
            }
        });
    }

    private void collapseAnnouncementRecyclerView() {
        mCollapseAnnouncement.setOnClickListener(view1 -> {
            if (!mAnnouncementCollapsedRecycler){
                mCollapseAnnouncement.setText(R.string.view_announcement);
                mCollapseAnnouncement.setTextColor(getResources().getColor(R.color.primary_blue));
                mEventAnnouncementRecycler.setVisibility(View.GONE);
                mAnnouncementCollapsedRecycler = true;
            } else {
                mCollapseAnnouncement.setText(R.string.collaspse_announcement);
                mCollapseAnnouncement.setTextColor(getResources().getColor(R.color.purple));
                mEventAnnouncementRecycler.setVisibility(View.VISIBLE);
                mAnnouncementCollapsedRecycler = false;
            }
        });
    }


    private void collapseWithText(){
        mComment_area.setVisibility(View.GONE);
        mSendCommentButton.setVisibility(View.GONE);
        mCollapseCommentText.setVisibility(View.GONE);
        mCollapseWriteComment = true;
    }


    private void collapseWriteCommentView(){
        if (mCollapseWriteComment){
            mCollapseWriteComment = false;
            mComment_area.setVisibility(View.VISIBLE);
            mSendCommentButton.setVisibility(View.VISIBLE);
            mCollapseCommentText.setVisibility(View.VISIBLE);
//            mComment_area.setFocusable(true);

        } else {
            mCollapseWriteComment = true;
            mComment_area.setVisibility(View.GONE);
            mSendCommentButton.setVisibility(View.GONE);
            mCollapseCommentText.setVisibility(View.GONE);
//            mComment_area.setFocusable(false);
        }

    }
/**
 * =============================
 * send the comment to be posted
 * =============================
 */






    /**
     * ====================================
     * sets up Comment AddTodoFragment’s UI
     * ====================================
     */
    private void updateUIComment(List<Comment> comments){
        if (mEventCommentAdapter == null){
            mEventCommentAdapter = new EventCommentAdapter(comments);
            mEventCommentRecycler.setAdapter(mEventCommentAdapter);
        } else{
            if(mLastItemComments < 0){
                mEventCommentAdapter.setComments(comments);
                mEventCommentAdapter.notifyDataSetChanged();
            } else{
                mEventCommentAdapter.notifyItemChanged(mLastItemComments);
                mLastItemComments = -1;
            }
        }
    }




    /**
     * =========================================
     * sets up Announcement AddTodoFragment’s UI
     * =========================================
     */
    private void updateUIAnnouncement(List<Announcement> announcements){
        if (mEventAnnouncementAdapter == null){
            mEventAnnouncementAdapter = new  EventAnnouncementAdapter(announcements);
            mEventAnnouncementRecycler.setAdapter(mEventAnnouncementAdapter);
        } else{
            if(mLastItemAnnouncement < 0){
                mEventAnnouncementAdapter.setAnnouncements(announcements);
                mEventAnnouncementAdapter.notifyDataSetChanged();
            } else{
                mEventAnnouncementAdapter.notifyItemChanged(mLastItemComments);
                mLastItemAnnouncement = -1;
            }
        }
    }



    /**
     * =========================================
     * getting the action from the admin console
     * @param view
     * =========================================
     */
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.event_admin : {
                if (mEvent.getId() != null){
                    Log.i(TAG, "onClick: does the id exist " + mEvent.getId());
                    Intent intent = AdminActivity.newIntent(getActivity(), mEvent.getId());
                    startActivity(intent);
                }
                break;
            }

            case R.id.event_attendees : {
                requestLoadContactAttendees();
                break;
            }

            case R.id.event_pool : {
//                if (mEvent.getId() != null){
//                    Log.i(TAG, "onClick: does the id exist " + mEvent.getId());
//                    Intent intent = AdminActivity.newIntent(getActivity(), mEvent.getId());
//                    startActivity(intent);
//                }
                //start pool activity
                if (mEvent.getId() != null){
                    Log.i(TAG, "onClick: does the id exist " + mEvent.getId());
                    Intent intent = PollActivity.newIntent(getActivity(), mEvent.getId());
                    startActivity(intent);
                } else {
                    Toast.makeText(getActivity(), "null id", Toast.LENGTH_SHORT).show();
                }
                break;
            }

            case R.id.event_documents : {
                Intent intent = UploadDocumentMainActivity.newIntent(getActivity(), mEvent.getId());
                startActivity(intent);
                break;
            }

            case R.id.event_photos : {
                Intent intent = UploadPhotoMainActivity.newIntent(getActivity(), mEvent.getId());
                startActivity(intent);
                break;
            }

            case R.id.event_notes : {
                Intent intent = ListNoteActivity.newIntent(getActivity());
                startActivity(intent);
                break;
            }

            case R.id.event_agenda_cardview : {
                //start agenda activity
                break;
            }
        }
    }






    private void addDanmakuCallBack(String content) {
        BaseDanmaku danmaku = danmakuContext.mDanmakuFactory.createDanmaku(BaseDanmaku.TYPE_SCROLL_RL);
        if (danmaku == null || danmakuView == null) {
            return;
        }

        danmaku.text = content;
        danmaku.padding = 5;
        danmaku.priority = 0;  // 可能会被各种过滤器过滤并隐藏显示
//        danmaku.isLive = islive;
        danmaku.setTime(danmakuView.getCurrentTime());
        danmaku.textSize = sp2px(20) ;// 20f * (parser.getDisplayer().getDensity() - 0.6f); //文本弹幕字体大小
        danmaku.textColor = getRandomColor(); //文本的颜色
//        danmaku.textShadowColor = getRandomColor(); //文本弹幕描边的颜色
        //danmaku.underlineColor = Color.DKGRAY; //文本弹幕下划线的颜色
//        danmaku.borderColor = getRandomColor(); //边框的颜色

        danmakuView.addDanmaku(danmaku);
    }


    private int getRandomColor() {
        int[] colors = {Color.parseColor("#FF9800"),
                Color.parseColor("#5D4037"),
                Color.parseColor("#FF5252"),
                Color.parseColor("#512DA8"),
                Color.parseColor("#512DA8"),
                Color.parseColor("#388E3C"),
                Color.parseColor("#FF5722")};
        int i = ((int) (Math.random() * 10)) % colors.length;
        return colors[i];
    }

    /**
     * sp转px的方法。
     */
    public int sp2px(float spValue) {
        final float fontScale = getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }




    /**
     *              The Beginning of the first recycler view
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
    abstract class EventCommentHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        Comment mComment;

        private TextView mDateCommented;
        private TextView mContactName;
        private TextView mCommentText;
        private ImageView mContactImg;

        public EventCommentHolder(View itemView) {
            super(itemView);
        }

        public EventCommentHolder(LayoutInflater inflater, ViewGroup parent, int layout){
            super(inflater.inflate(layout, parent, false));

            mDateCommented = itemView.findViewById(R.id.event_date_commented);
            mContactName = itemView.findViewById(R.id.name_contact_comment_event);
            mContactImg = itemView.findViewById(R.id.contact_image_comment_event);
            mCommentText = itemView.findViewById(R.id.topic_comment_page_event);
        }

        public void bind(Comment comment){
            mComment = comment;
            mDateCommented.setText(DateUtils.getReadableModifyDate(mComment.getTime()));
            mContactName.setText(mComment.getName());
            mCommentText.setText(mComment.getText());

            if (comment.getUsername() != null){
                String recupCookie = CookiePreferences.getStoredCookie(getActivity());
                Log.i(TAG, "intercept: " + recupCookie);
                LazyHeaders.Builder builder = new LazyHeaders.Builder().addHeader("Cookie", recupCookie);
                GlideUrl glideUrl = new GlideUrl(BaseUrlGenerator.BINARY_BASE_URL + "/profile/mypicture/" + comment.getUsername(), builder.build());

                Glide.with(MyEventFragment.this)
                        .load(glideUrl)
                        .asBitmap()
                        .centerCrop()
                        .placeholder(getResources().getDrawable(R.drawable.photo_no_image))
                        .into(new BitmapImageViewTarget(mContactImg) {
                            @Override
                            protected void setResource(Bitmap resource) {
                                RoundedBitmapDrawable drawable = RoundedBitmapDrawableFactory.create(MyEventFragment.this.getResources(),
                                        Bitmap.createScaledBitmap(resource, 45, 45, false));
                                drawable.setCircular(true);
                                mContactImg.setImageDrawable(drawable);
                            }
                        });
                Log.i(TAG, "bind: " + comment.getUsername());
            }

        }

        @Override
        public void onClick(View view) {
            mLastItemComments = this.getAdapterPosition();
        }
    }


    /**
     * the holder that is purchased when i am commented
     */
    class EventCommentHolderMe extends EventCommentHolder {
        public EventCommentHolderMe(LayoutInflater inflater, ViewGroup viewGroup) {
            super(inflater, viewGroup, R.layout.item_list_contact_comment_me);
        }
    }




    /**
     * the holder that is purchased when i am commented
     */
    class EventCommentHolderOther extends EventCommentHolder {
        public EventCommentHolderOther(LayoutInflater inflater, ViewGroup viewGroup) {
            super(inflater, viewGroup, R.layout.list_item_contact_comment_list);
        }
    }





    /**
     * ==================================
     * adapter to be set in the recycler
     * ==================================
     */
    class EventCommentAdapter extends RecyclerView.Adapter<EventCommentHolder>{

        List<Comment> mComments;

        public EventCommentAdapter(List<Comment> comments) {
            mComments = comments;
        }

        @Override
        public EventCommentHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            if (viewType == 0){
                return new EventCommentHolderMe(inflater, parent);
            } else {
                return new EventCommentHolderOther(inflater, parent);
            }
        }

        @Override
        public void onBindViewHolder(EventCommentHolder holder, int position) {
            holder.bind(mComments.get(position));
        }

        @Override
        public int getItemCount() {
            return mComments.size();
        }

        @Override
        public int getItemViewType(int position) {
            Log.i("special", "getItemViewType: " + mComments.get(position).getUsername());
            Log.i("special", "getItemViewType: " + CookiePreferences.getStoredName(getActivity()));
            if (mComments.get(position).getUsername().equals(CookiePreferences.getStoredName(getActivity()))){
                return 0;
            } else {
                return 1;
            }
        }

        private void setComments(List<Comment> comments){
            mComments = comments;
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





/**
 * =================================================================================================
 */






    /**
     *              The Beginning of the Second recycler view
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
    class EventAnnouncementHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private Announcement mAnnouncement;

        private TextView mAnnounceText;
        private TextView mAnnounceDate;

        public EventAnnouncementHolder(View itemView) {
            super(itemView);
        }

        public EventAnnouncementHolder(LayoutInflater inflater, ViewGroup parent){
            super(inflater.inflate(R.layout.list_item_announcement_layout, parent, false));
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
    class EventAnnouncementAdapter extends RecyclerView.Adapter<EventAnnouncementHolder>{

        List<Announcement> mAnnouncements;

        public EventAnnouncementAdapter(List<Announcement> announcements) {
            mAnnouncements = announcements;
        }

        @Override
        public EventAnnouncementHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            return new EventAnnouncementHolder(inflater, parent);
        }

        @Override
        public void onBindViewHolder(EventAnnouncementHolder holder, int position) {
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
