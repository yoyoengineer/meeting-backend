package com.meeting.binary.android.binarymeeting.contact;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.meeting.binary.android.binarymeeting.model.Event;
import com.meeting.binary.android.binarymeeting.other.SingleFragmentActivity;
import com.meeting.binary.android.binarymeeting.other.SingleFragmentActivityNoBar;

import java.io.Serializable;

/**
 * Created by meldi on 3/21/2018.
 */

public class ContactEventActivity extends SingleFragmentActivity {

    private static final String MY_CONTACT_EVENT_ACTIVITY_EXTRA_MESSAGES = "com.meeting.binary.android.binarymeeting.event.extra_messages";


    @Override
    public Fragment createFragment() {
        Event event = getIntent().getParcelableExtra(MY_CONTACT_EVENT_ACTIVITY_EXTRA_MESSAGES);
        return ContactEventFragment.newInstance((Serializable) event);
    }


    public static Intent newIntent(Context context, Event event){
        Intent intent = new Intent(context, ContactEventActivity.class);
        Bundle args = new Bundle();
        args.putParcelable(MY_CONTACT_EVENT_ACTIVITY_EXTRA_MESSAGES, event);
        intent.putExtras(args);
        return intent;
    }
}
