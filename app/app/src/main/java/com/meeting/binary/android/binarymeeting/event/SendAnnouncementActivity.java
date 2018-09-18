package com.meeting.binary.android.binarymeeting.event;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;

import com.meeting.binary.android.binarymeeting.other.SingleFragmentActivityNoBar;

/**
 * Created by meldi on 3/19/2018.
 */

public class SendAnnouncementActivity extends SingleFragmentActivityNoBar {

    private static final String EXTRA_ID_EVENT = "extra_send.id";

    @Override
    public Fragment createFragment() {
        String eventId = getIntent().getStringExtra(EXTRA_ID_EVENT);
        return SendAnnouncementFragment.newInstance(eventId);
    }


    public static Intent newIntent(Context context, String id){
        Intent intent = new Intent(context, SendAnnouncementActivity.class);
        intent.putExtra(EXTRA_ID_EVENT, id);
        return intent;
    }
}
