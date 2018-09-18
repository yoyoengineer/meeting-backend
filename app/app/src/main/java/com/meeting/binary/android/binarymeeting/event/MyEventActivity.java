package com.meeting.binary.android.binarymeeting.event;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;

import com.meeting.binary.android.binarymeeting.model.Announcement;
import com.meeting.binary.android.binarymeeting.model.Comment;
import com.meeting.binary.android.binarymeeting.model.Event;
import com.meeting.binary.android.binarymeeting.other.SingleFragmentActivity;
import com.meeting.binary.android.binarymeeting.other.SingleFragmentActivityNoBar;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by meldi on 3/17/2018.
 */

public class MyEventActivity extends SingleFragmentActivity {

    private static final String MY_EVENT_ACTIVITY_EXTRA_MESSAGES = "com.meeting.binary.android.binarymeeting.event.extra_messages";

    @Override
    public Fragment createFragment() {
        Event event = getIntent().getParcelableExtra(MY_EVENT_ACTIVITY_EXTRA_MESSAGES);
        return MyEventFragment.newInstance((Serializable) event);
    }


//    public static Intent newIntent(Context context, Event event, List<Comment> comments, List<Announcement> announcements){
//        Intent intent = new Intent(context, MyEventActivity.class);
//        Bundle args = new Bundle();
//        args.putParcelable(MY_EVENT_ACTIVITY_EXTRA_MESSAGES, event);
//        intent.putExtras(args);
//        return intent;
//    }



    public static Intent newIntent(Context context, Event event){
        Intent intent = new Intent(context, MyEventActivity.class);
        Bundle args = new Bundle();
        args.putParcelable(MY_EVENT_ACTIVITY_EXTRA_MESSAGES, event);
        intent.putExtras(args);
        return intent;
    }
}
