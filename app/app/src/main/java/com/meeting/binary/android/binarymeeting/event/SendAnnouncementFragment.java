package com.meeting.binary.android.binarymeeting.event;

import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
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
import android.widget.Toast;

import com.meeting.binary.android.binarymeeting.R;
import com.meeting.binary.android.binarymeeting.model.Announcement;
import com.meeting.binary.android.binarymeeting.model.Comment;
import com.meeting.binary.android.binarymeeting.service.generetor.GeneralServiceGenerator;
import com.meeting.binary.android.binarymeeting.service.request_interface.RequestWebServiceInterface;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by meldi on 3/19/2018.
 */

public class SendAnnouncementFragment extends Fragment {

    private EditText mTitle;
    private EditText mMessage;
    private Button mSend;
    private Button mCancel;

    private static final String TAG = "send_announce";
    private static final String EVENT_ID = "send_event_id";

    String eventId;

    public static SendAnnouncementFragment newInstance(String eventId) {
        Bundle args = new Bundle();
        args.putString(EVENT_ID, eventId);
        SendAnnouncementFragment fragment = new SendAnnouncementFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if(getArguments() != null){
            eventId = getArguments().getString(EVENT_ID);
            if (eventId != null){
                Log.d(TAG, "onCreate: id not null");
            } else {
                Log.d(TAG, "onCreate: id is null");
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.send_announcement, container, false);

        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("send Announcement");
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mTitle = view.findViewById(R.id.announce_title);
        mMessage = view.findViewById(R.id.announce_message);
        mSend = view.findViewById(R.id.announce_send);
        mCancel = view.findViewById(R.id.announce_cancel);

        InputMethodManager lManager = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);


        mTitle.setOnClickListener(view1 -> {
            mTitle.requestFocusFromTouch();
            mTitle.setFocusable(true);
//            mTitle.setFocusableInTouchMode(true);
            lManager.showSoftInput(mTitle, 0);
        });

        mMessage.setOnClickListener(view1 -> {
            mMessage.requestFocusFromTouch();
            mMessage.setFocusable(true);
//            mMessage.setFocusableInTouchMode(true);
            lManager.showSoftInput(mMessage, 0);
        });

        mSend.setOnClickListener(view1 -> {
            String announcement_text = mMessage.getText().toString();
            sendAnnouncement(announcement_text);
            mTitle.setText("");
            mMessage.setText("");
        });
        mCancel.setOnClickListener(view1 -> cancel());
        return view;
    }


    @Override
    public void onResume() {
        super.onResume();
        mTitle.requestFocus();
        mMessage.requestFocus();
    }


    private void cancel() {
        getActivity().onBackPressed();
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }






    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                getActivity().onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }



    /**
     * ==================================
     * send the announcement to be posted
     * ==================================
     */
    private void sendAnnouncement(String announcementText_text){

        if(announcementText_text == null || announcementText_text.isEmpty())
            return;
        Announcement announcement = new Announcement();
        announcement.setText(announcementText_text);
        announcement.setEventId(eventId);

        //post comment
        RequestWebServiceInterface requestWebServiceInterface =
                GeneralServiceGenerator.CreateService(RequestWebServiceInterface.class, getActivity());
        Call<ResponseBody> listCall = requestWebServiceInterface.sendEventAnnouncement(announcement);
        listCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()){
                    Toast.makeText(getActivity(), "announcement sent", Toast.LENGTH_LONG).show();
                    Log.i(TAG, "onResponse: announcement sent successfully in the server");
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
}
