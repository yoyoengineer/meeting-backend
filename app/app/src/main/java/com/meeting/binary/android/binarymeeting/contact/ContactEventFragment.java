package com.meeting.binary.android.binarymeeting.contact;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
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
import com.meeting.binary.android.binarymeeting.contact.document_photo.DocumentActivity;
import com.meeting.binary.android.binarymeeting.contact.document_photo.PhotoActivity;
import com.meeting.binary.android.binarymeeting.event.MoreAnnouncementActivity;
import com.meeting.binary.android.binarymeeting.event.MoreCommentActivity;
import com.meeting.binary.android.binarymeeting.event.MyEventFragment;
import com.meeting.binary.android.binarymeeting.event.ScanCheck;
import com.meeting.binary.android.binarymeeting.event.StartLotteryActivity;
import com.meeting.binary.android.binarymeeting.event.admin.ListAttendeesActivity;
import com.meeting.binary.android.binarymeeting.event.admin.ListAttendeesFragment;
import com.meeting.binary.android.binarymeeting.event.admin.PollActivity;
import com.meeting.binary.android.binarymeeting.model.Announcement;
import com.meeting.binary.android.binarymeeting.model.Comment;
import com.meeting.binary.android.binarymeeting.model.Contact;
import com.meeting.binary.android.binarymeeting.model.DanmakuMessage;
import com.meeting.binary.android.binarymeeting.model.Event;
import com.meeting.binary.android.binarymeeting.model.EventMessage;
import com.meeting.binary.android.binarymeeting.model.Interact;
import com.meeting.binary.android.binarymeeting.model.WinLottery;
import com.meeting.binary.android.binarymeeting.other.agenda_contact.ContactAgendaMainActivity;
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

import io.realm.Realm;
import io.realm.RealmList;
import master.flame.danmaku.controller.DrawHandler;
import master.flame.danmaku.danmaku.model.BaseDanmaku;
import master.flame.danmaku.danmaku.model.DanmakuTimer;
import master.flame.danmaku.danmaku.model.IDanmakus;
import master.flame.danmaku.danmaku.model.IDisplayer;
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
 * Created by meldi on 3/21/2018.
 */

public class ContactEventFragment extends Fragment{

    private StompClient mStompClient;
    private  ObjectMapper objectMapper;

    private Event mEvent;
    private List<Comment> mComments;
    private List<Announcement> mAnnouncements;

    private static final int REQUEST_CODE = 0;
    private static final String TAG_REQUEST = "qr_code";
    private static final String TAG = "MyContactEventFrag_TAG";
    private static final String GET_INFO_CONTACT_EVENT_KEY = "com.meeting.binary.event.MyEventFragment.get_info_event_key";
    private static final String DIALOG_LIKED_EVENT = "DialogLikeEvent";

    private int mLastItemComments = -1;
    private int mLastItemAnnouncement = -1;
    private boolean mCommentCollapsedRecycler = false;
    private boolean mAnnouncementCollapsedRecycler = false;
    private boolean mCollapseWriteComment = true;


    private TextView mCollapseComment;
    private TextView mCollapseCommentText;
    private EditText mLiveInteract;
    private EditText mComment_area;
    private Button mSendCommentButton;
    private Button mSend_interact;
    private CardView mSeePeopleLike;
    private CardView mDisplaySendComment;
    private ImageView mEventAttendees;
    private ImageView mEventPool;
    private ImageView mEventDocuments;
    private ImageView mEventPhotos;
    private ImageView mWriteNote;
    private CardView mEventAbout;
    private CardView mLikeEvent;
    private ImageView eventLottery;
    private ImageView likeCommentPhoto;
    private TextView mCollapseAnnouncement;
    private CardView mViewEventAgensda;
    private CardView viewMoreComment;
    private CardView viewMoreAnnouncement;


    private RecyclerView mEventCommentRecycler;
    private EventContactCommentAdapter mEventContactCommentAdapter;
    private RecyclerView mEventAnnouncementRecycler;
    private EventContactAnnouncementAdapter mEventAnnouncementAdapter;

    private boolean showDanmaku;
    private CardView cardView;

    private DanmakuView danmakuView;
    private DanmakuContext danmakuContext;
    private BaseDanmakuParser parser = new BaseDanmakuParser() {@Override protected IDanmakus parse() {return new Danmakus();} };



    public static ContactEventFragment newInstance(Serializable event) {
        Bundle args = new Bundle();
        ContactEventFragment fragment = new ContactEventFragment();
        args.putSerializable(GET_INFO_CONTACT_EVENT_KEY, event);
        fragment.setArguments(args);
        return fragment;
    }



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        objectMapper = new ObjectMapper();

        if (getArguments() != null){
            mEvent = (Event) getArguments().getSerializable(GET_INFO_CONTACT_EVENT_KEY);
            Log.i(TAG, "onCreate: " + mEvent.isEventPublicState());
        } else {
            Log.i(TAG, "onCreate: argument null");
        }

    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contact_event, container, false);

        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(mEvent.getTopic());
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        InputMethodManager lManager = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);


        //View v = getActivity().getWindow().getDecorView();
        int orientation = getResources().getConfiguration().orientation;
        if (Configuration.ORIENTATION_LANDSCAPE == orientation) {
            view.setBackgroundResource (R.drawable.happy_back);
        } else {
            view.setBackgroundResource (R.drawable.happy_back);
        }

        String recupCookie = CookiePreferences.getStoredCookie(getActivity());
        Map<String,String> headers = new HashMap<>();
        headers.put("Cookie",recupCookie);
        mStompClient = Stomp.over(WebSocket.class, "ws://" + BaseUrlGenerator.BASE_DOMAIN +"/stompwebsocket",headers);
        mStompClient.connect();
        registerToPrivateStompTopic();
        registerToPublicStompTopic();

        mEventAbout = view.findViewById(R.id.view_detail);
        mEventAttendees = view.findViewById(R.id.event_attendees);
        mEventPool = view.findViewById(R.id.event_pool);
        mEventDocuments = view.findViewById(R.id.event_documents);
        mEventPhotos = view.findViewById(R.id.event_photos);
        mWriteNote = view.findViewById(R.id.write_notes);
        mLiveInteract = view.findViewById(R.id.live_interact);
        mCollapseCommentText = view.findViewById(R.id.collapse_comment_textview);
        mSend_interact = view.findViewById(R.id.send_interact);
        mComment_area = view.findViewById(R.id.comment_area);
        mSendCommentButton = view.findViewById(R.id.send_comment_button);
        mDisplaySendComment = view.findViewById(R.id.send_comment);
        mCollapseComment = view.findViewById(R.id.collapse_comment);
        mCollapseAnnouncement = view.findViewById(R.id.collapse_announcement);
        mSeePeopleLike = view.findViewById(R.id.view_people_like);
        mLikeEvent = view.findViewById(R.id.like_event);
        likeCommentPhoto = view.findViewById(R.id.like_comment_photo);
        eventLottery = view.findViewById(R.id.event_lottery);
        mViewEventAgensda =  view.findViewById(R.id.view_agenda);
        viewMoreComment = view.findViewById(R.id.view_more_comment);
        viewMoreAnnouncement = view.findViewById(R.id.view_more_announcement);




        /**view more announcement*/
        viewMoreAnnouncement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = MoreAnnouncementActivity.newIntent(getActivity(), mEvent);
                startActivity(intent);
            }
        });


        /**go to see all the comments*/
        viewMoreComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = MoreCommentActivity.newIntent(getActivity(), mEvent);
                startActivity(intent);
            }
        });



        /**go the lottery*/
        eventLottery.setOnClickListener(view1 -> StartLotteryActivity.actionStart(getActivity(),mEvent.getId()));

        /** go to notepad activity*/
        mWriteNote.setOnClickListener(view1 -> {
            Intent intent = ListNoteActivity.newIntent(getActivity());
            startActivity(intent);
        });

        /** open a dialog that display the number of people that liked that event*/
        mSeePeopleLike.setOnClickListener(view1 -> {
            requestLoadContactLike();
        });


        /**start the poll activity*/
        mEventPool.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = PollActivity.newIntent(getActivity(), mEvent.getId());
                startActivity(intent);
            }
        });


        /**view agenda event*/
        mViewEventAgensda.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = ContactAgendaMainActivity.newIntent(getActivity(), mEvent.getId());
                startActivity(intent);
            }
        });


        /**see the event photos*/
        mEventPhotos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = PhotoActivity.newIntent(getActivity(), mEvent.getId());
                startActivity(intent);
            }
        });


        /**view the document of the event*/
        mEventDocuments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = DocumentActivity.newIntent(getActivity(), mEvent.getId());
                startActivity(intent);
            }
        });


        /**check on attendees*/
        mEventAttendees.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestLoadContactAttendees();
            }
        });


        /**like the event*/
        mLikeEvent.setOnClickListener(view1 -> requestSendLike());

        /** allow touch on comment text*/
        mComment_area.setOnClickListener(view1 -> {
            mComment_area.setFocusable(true);
            mComment_area.requestFocusFromTouch();
//            mComment_area.setFocusableInTouchMode(true);
            lManager.showSoftInput(mComment_area, 0);
        });

        /** allow touch on live interact danmuku area*/
        mLiveInteract.setOnClickListener(view1 -> {
            mLiveInteract.setFocusable(true);
            mLiveInteract.requestFocusFromTouch();
            lManager.showSoftInput(mLiveInteract, 0);

        });


        /**send danmaku message to the server**/
        mSend_interact.setOnClickListener(view1 ->  {
            String content = mLiveInteract.getText().toString();
            if (!TextUtils.isEmpty(content)) {
                requestSendLiveInteract(content);
                mLiveInteract.setText("");
            }
        });


        /**about the event*/
        mEventAbout.setOnClickListener(view1 -> {
            Intent intent = InfoEventActivity.newIntent(getActivity(), mEvent);
            startActivity(intent);
        });


        /*** handle and display the danmaku ***/
        cardView = view.findViewById(R.id.cc_view);
        danmakuView = view.findViewById(R.id.danmaku_view);
//        danmakuView.enableDanmakuDrawingCache(true);


        /**create the danmaku view*/
//        danmakuContext = DanmakuContext.create();

        mSendCommentButton.setOnClickListener(view1 -> {
            String commented_text = mComment_area.getText().toString();
            sendComment(commented_text);
            mComment_area.setText("");
        });



        mCollapseCommentText.setOnClickListener(view1 -> collapseWithText());
        mDisplaySendComment.setOnClickListener(view1 -> collapseWriteCommentView());

        //get recycler comment view id
        mEventCommentRecycler = view.findViewById(R.id.recycler_event_list_comment);
        mEventCommentRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        //get recycler comment view id
        mEventAnnouncementRecycler = view.findViewById(R.id.recycler_event_list_announcement);
        mEventAnnouncementRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));

        requestDataAnnouncement();
        requestDataComment();

        init();
        return view;
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

                .setMaximumLines(maxLinesPair)
                .preventOverlapping(overlappingEnablePair);

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
     * ==========================================
     * send like event
     * ==========================================
     */
    private void requestSendLike(){
        /**loading data process*/
        RequestWebServiceInterface requestWebServiceInterface =
                GeneralServiceGenerator.CreateService(RequestWebServiceInterface.class, getActivity());
        Call<ResponseBody> listCall = requestWebServiceInterface.sendLike(mEvent.getId());
        listCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()){
                    Log.i(TAG, "onResponse: uploqd like succed");
                    likeCommentPhoto.setImageDrawable(getResources().getDrawable(R.drawable.like_red));
                } else {
                    Toast toast = Toast.makeText(getActivity(), "failed to load contacts", Toast.LENGTH_LONG);
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




    @Override
    public void onResume() {
        super.onResume();
        requestDataComment();
        requestDataAnnouncement();
        mLiveInteract.requestFocus();
        mComment_area.requestFocus();
        if (danmakuView != null && danmakuView.isPrepared() && danmakuView.isPaused()) {
            danmakuView.resume();
        }
    }





    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_contact_event, menu);

        /**
         * retrieve the MenuItem in charge of the icon
         * keep track of the event state visibility
         */
        MenuItem iconItem = menu.findItem(R.id.action_scan_generate);
        iconItem.setIcon(R.drawable.scan_icon_white);
//        if (mEvent.isEventPublicState()){
//            iconItem.setIcon(R.drawable.scan_icon_white);
//        } else {
//            iconItem.setIcon(R.drawable.generate);
//        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                getActivity().onBackPressed();
                return true;
            case R.id.action_scan_generate:
                scan();
//                if (mEvent.isEventPublicState()){
//                    scan();
//                } else{
//                    generate();
//                }
                return true;
            default: return super.onOptionsItemSelected(item);
        }
    }


    private void generate(){
        FragmentManager fm = getFragmentManager();
        GenerateQrCodeDialogFragment fragment = GenerateQrCodeDialogFragment.newInstance(mEvent.getId());
        fragment.show(fm, TAG_REQUEST);
    }


    private void scan(){
        startActivityForResult(new Intent(getActivity(), CaptureActivity.class),REQUEST_CODE);
    }




    @Override
    public void onPause() {
        super.onPause();
        if (danmakuView != null && danmakuView.isPrepared()) {
            danmakuView.pause();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        showDanmaku = false;
        if (danmakuView != null) {
            danmakuView.release();
            danmakuView = null;
        }
    }



    /**the result we got back from the scan*/
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE){
            Log.d(TAG, "onActivityResult: " + resultCode);
            if (resultCode== Activity.RESULT_OK){
                Bundle bundle = data.getExtras();
                Log.d(TAG, "onActivityResult: bundle" + bundle.toString());
                if (bundle != null) {
                    String result = bundle.getString("result");
                    Log.i(TAG, "onActivityResult: " + result);
                    if (result != null && result.equals(mEvent.getId())){
                        requestScan();
                    } else
                        Log.d(TAG, "onActivityResult: result null or not equal to event id");
                } else
                    Log.d(TAG, "onActivityResult: null bundle");
            } else
                Log.d(TAG, "onActivityResult: got different result");
        }
    }


    private void requestScan(){
        FragmentManager fm = getFragmentManager();
        final String[] message = new String[1];
        RequestWebServiceInterface requestWebServiceInterface =
                GeneralServiceGenerator.CreateService(RequestWebServiceInterface.class, getActivity());
        Call<ResponseBody> listCall = requestWebServiceInterface.getScanResponce(mEvent.getId());
        listCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()){
                    message[0] = "request succeed \n press ok to keep going on the event";
                    ScanCheck fragment = ScanCheck.newInstance(message[0]);
                    fragment.show(fm, TAG_REQUEST);
                } else{
                    Log.d(TAG, "onActivityResult: result null or not equal to event id");
                    message[0] = "request failed \n try again later to keep going on the event";
                    ScanCheck fragment = ScanCheck.newInstance(message[0]);
                    fragment.show(fm, TAG_REQUEST);
                }
                Log.i("response_code", "onResponse: " + response.code());
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
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
                    collapseCommentRecyclerView();
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
                mEventAnnouncementAdapter.notifyItemChanged(mLastItemComments);
                mLastItemAnnouncement = -1;
            }
        }
    }





    /**
     *                      Start collapsing
     * ===============================================================================
     * collapse the comment view to give a better perspective view to the announcement
     * ===============================================================================
     */
    private void collapseCommentRecyclerView() {
        mCollapseComment.setOnClickListener(view1 -> {
            if (!mCommentCollapsedRecycler){
                mCollapseComment.setText(R.string.view_comment);
                mCollapseComment.setTextColor(getResources().getColor(R.color.primary_blue));
                mEventCommentRecycler.setVisibility(View.GONE);
                mCommentCollapsedRecycler = true;
            } else {
                mCollapseComment.setText(R.string.collaspse_comment);
                mCollapseComment.setTextColor(getResources().getColor(R.color.purple));
                mEventCommentRecycler.setVisibility(View.VISIBLE);
                mCommentCollapsedRecycler = false;
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

    private void collapseWriteCommentView(){
        if (mCollapseWriteComment){
            mCollapseWriteComment = false;
            mComment_area.setVisibility(View.VISIBLE);
            mSendCommentButton.setVisibility(View.VISIBLE);
            mCollapseCommentText.setVisibility(View.VISIBLE);
            mComment_area.setFocusable(true);

        } else {
            mCollapseWriteComment = true;
            mComment_area.setVisibility(View.GONE);
            mSendCommentButton.setVisibility(View.GONE);
            mCollapseCommentText.setVisibility(View.GONE);
            mComment_area.setFocusable(false);
        }

    }

    private void collapseWithText(){
        mComment_area.setVisibility(View.GONE);
        mSendCommentButton.setVisibility(View.GONE);
        mCollapseCommentText.setVisibility(View.GONE);
        mCollapseWriteComment = true;
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
    abstract class EventContactCommentHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        Comment mComment;

        private TextView mDateCommented;
        private TextView mContactName;
        private TextView mCommentText;
        private ImageView mContactImg;


        public EventContactCommentHolder(View itemView) {
            super(itemView);
        }

        public EventContactCommentHolder(LayoutInflater inflater, ViewGroup viewGroup, int layout){
            super(inflater.inflate(layout, viewGroup, false));
            mDateCommented = itemView.findViewById(R.id.event_date_commented);
            mContactName = itemView.findViewById(R.id.name_contact_comment_event);
            mContactImg = itemView.findViewById(R.id.contact_image_comment_event);
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
                String recupCookie = CookiePreferences.getStoredCookie(getActivity());
                Log.i(TAG, "intercept: " + recupCookie);
                LazyHeaders.Builder builder = new LazyHeaders.Builder().addHeader("Cookie", recupCookie);
                GlideUrl glideUrl = new GlideUrl(BaseUrlGenerator.BINARY_BASE_URL + "/profile/mypicture/" + comment.getUsername(), builder.build());

                Glide.with(ContactEventFragment.this)
                        .load(glideUrl)
                        .asBitmap()
                        .centerCrop()
                        .placeholder(getResources().getDrawable(R.drawable.photo_no_image))
                        .into(new BitmapImageViewTarget(mContactImg) {
                            @Override
                            protected void setResource(Bitmap resource) {
                                RoundedBitmapDrawable drawable = RoundedBitmapDrawableFactory.create(ContactEventFragment.this.getResources(),
                                        Bitmap.createScaledBitmap(resource, 45, 45, false));
                                drawable.setCircular(true);
                                mContactImg.setImageDrawable(drawable);
                            }
                        });
                Log.i(TAG, "bind: " + comment.getUsername());
            }
        }
    }



    /**
     * the holder that is purchased when i am commented
     */
    class EventContactCommentHolderMe extends EventContactCommentHolder {
        public EventContactCommentHolderMe(LayoutInflater inflater, ViewGroup viewGroup) {
            super(inflater, viewGroup, R.layout.list_item_contact_comment_list_me);
        }
    }




    /**
     * the holder that is purchased when i am commented
     */
    class EventContactCommentHolderOther extends EventContactCommentHolder {
        public EventContactCommentHolderOther(LayoutInflater inflater, ViewGroup viewGroup) {
            super(inflater, viewGroup, R.layout.list_item_contact_comment_list);
        }
    }



    /**
     * ==================================
     * adapter to be set in the recycler
     * ==================================
     */
    class EventContactCommentAdapter extends RecyclerView.Adapter<EventContactCommentHolder>{

        List<Comment> mComments;

        public EventContactCommentAdapter(List<Comment> comments) {
            mComments = comments;
        }

        @Override
        public EventContactCommentHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            if (viewType == 0){
                return new EventContactCommentHolderMe(inflater, parent);
            } else {
                return new EventContactCommentHolderOther(inflater, parent);
            }
        }

        @Override
        public void onBindViewHolder(EventContactCommentHolder holder, int position) {
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

        public void setComments(List<Comment> comments) {
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
            super(inflater.inflate(R.layout.list_item_contact_announcement_list, parent, false));
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
            LayoutInflater inflater = LayoutInflater.from(getActivity());
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
