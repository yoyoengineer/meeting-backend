package com.meeting.binary.android.binarymeeting.event;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;

import com.meeting.binary.android.binarymeeting.other.SingleFragmentActivity;
import com.meeting.binary.android.binarymeeting.other.SingleFragmentActivityNoBar;

/**
 * Created by meldi on 3/19/2018.
 */

public class HomeEventActivity extends SingleFragmentActivity {

    @Override
    public Fragment createFragment() {
        return new HomeEventFragment();
    }

    public static Intent newIntent(Context context){
        Intent intent = new Intent(context, HomeEventActivity.class);
        return intent;
    }
}
