package com.meeting.binary.android.binarymeeting.event.other_events;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.meeting.binary.android.binarymeeting.R;
import com.meeting.binary.android.binarymeeting.contact.document_photo.DocumentFragment;
import com.meeting.binary.android.binarymeeting.event.admin.PollActivity;
import com.meeting.binary.android.binarymeeting.other.SingleFragmentActivityNoBar;

public class AttendedEventActivity extends SingleFragmentActivityNoBar {

    private static final String EXTRA_ID_MESSAGE = "event_id";

    public static Intent newIntent(Context context){
        Intent intent = new Intent(context, AttendedEventActivity.class);
        return intent;
    }

    public Fragment createFragment(){
        AttendedEventFragment fragment = AttendedEventFragment.newInstance();
        return fragment;
    }


}
