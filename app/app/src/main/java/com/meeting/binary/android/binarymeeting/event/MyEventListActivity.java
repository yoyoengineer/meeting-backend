package com.meeting.binary.android.binarymeeting.event;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;

import com.meeting.binary.android.binarymeeting.model.Event;
import com.meeting.binary.android.binarymeeting.other.SingleFragmentActivity;
import com.meeting.binary.android.binarymeeting.other.SingleFragmentActivityNoBar;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by meldi on 3/18/2018.
 */

public class MyEventListActivity extends SingleFragmentActivityNoBar {

    private static final String MY_EVENT_ALL_EXTRA_MESSAGE = "com.binary.all_events";

    @Override
    public Fragment createFragment() {
        List<Event> events = getIntent().getParcelableArrayListExtra(MY_EVENT_ALL_EXTRA_MESSAGE);
        return MyEventListFragment.newInstance((Serializable) events);
    }



    public static Intent newIntent(Context context, List<Event> events){
        Intent intent = new Intent(context, MyEventListActivity.class);
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(MY_EVENT_ALL_EXTRA_MESSAGE, (ArrayList<? extends Parcelable>) events);
        intent.putExtras(bundle);
        return intent;
    }

}
