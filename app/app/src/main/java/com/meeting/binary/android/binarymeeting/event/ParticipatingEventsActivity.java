package com.meeting.binary.android.binarymeeting.event;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;

import com.meeting.binary.android.binarymeeting.other.SingleFragmentActivityNoBar;

public class ParticipatingEventsActivity extends SingleFragmentActivityNoBar {

    @Override
    public Fragment createFragment() {
        return ParticipatingEventsFragment.newInstance();
    }



    public static Intent newIntent(Context context){
        Intent intent = new Intent(context, ParticipatingEventsActivity.class);
        return intent;
    }

}
