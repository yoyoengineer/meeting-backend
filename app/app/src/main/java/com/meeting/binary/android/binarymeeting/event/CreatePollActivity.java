package com.meeting.binary.android.binarymeeting.event;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.meeting.binary.android.binarymeeting.other.SingleFragmentActivityNoBar;

/**
 * Created by meldi on 3/20/2018.
 */

public class CreatePollActivity extends SingleFragmentActivityNoBar {
    private static final String EXTRA_ID = "idtag";
    private static final String TAG = CreatePollActivity.class.getCanonicalName() ;

    @Override
    public Fragment createFragment() {
        String id = getIntent().getStringExtra(EXTRA_ID);
        Log.i(TAG, "onCreate: the event id is not null " + id);
        return CreatePollFragment.newInstance(id);
    }



    public static Intent newIntent(Context context, String id){
        Intent intent = new Intent(context, CreatePollActivity .class);
        intent.putExtra(EXTRA_ID, id);
        return intent;
    }
}
