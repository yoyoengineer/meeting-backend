package com.meeting.binary.android.binarymeeting.event.admin;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;

import com.meeting.binary.android.binarymeeting.other.SingleFragmentActivityNoBar;

/**
 * Created by meldi on 3/19/2018.
 */

public class AdminActivity extends SingleFragmentActivityNoBar {

    @Override
    public Fragment createFragment() {
        String id = getIntent().getStringExtra("eventId");
        return AdminFragment.newInsgtance(id);
    }


    public static Intent newIntent(Context context, String eventId){
       Intent intent = new Intent(context, AdminActivity.class);
       intent.putExtra("eventId",eventId);
       return intent;
    }
}
