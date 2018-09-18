package com.meeting.binary.android.binarymeeting.message;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

import com.meeting.binary.android.binarymeeting.other.SingleFragmentActivity;
import com.meeting.binary.android.binarymeeting.other.SingleFragmentActivityNoBar;

public class ChatListActivity extends SingleFragmentActivityNoBar{


    @Override
    public Fragment createFragment() {
        return new ChatListFragment();
    }

    public static Intent newIntent(Context context){
        Intent intent = new Intent(context, ChatListActivity.class);
        return intent;
    }

}
