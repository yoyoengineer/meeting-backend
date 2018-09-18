package com.meeting.binary.android.binarymeeting.contact;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;

import com.meeting.binary.android.binarymeeting.other.SingleFragmentActivityNoBar;

/**
 * Created by meldi on 3/23/2018.
 */

public class WriteCommentActivity extends SingleFragmentActivityNoBar {

    @Override
    public Fragment createFragment() {
        return WriteCommentFragment.newInstance();
    }



    public static Intent newIntent(Context context){
        Intent intent = new Intent(context, WriteCommentActivity.class);
        return intent;
    }
}
