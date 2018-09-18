package com.meeting.binary.android.binarymeeting.contact;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.meeting.binary.android.binarymeeting.model.Event;
import com.meeting.binary.android.binarymeeting.other.SingleFragmentActivityNoBar;

import java.io.Serializable;

public class InfoEventActivity extends SingleFragmentActivityNoBar{


    private static final String INFO_MESSAGE = "com.meeting.binary.android.binarymeeting.infos message";

    public static final Intent newIntent(Context context, Event event){
        Bundle args = new Bundle();
        args.putSerializable(INFO_MESSAGE, event);
        Intent intent = new Intent(context, InfoEventActivity.class);
        intent.putExtras(args);
        return intent;
    }

    @Override
    public Fragment createFragment() {
        Event event = (Event) getIntent().getSerializableExtra(INFO_MESSAGE);
        return InfoEventFragment.newInstance((Serializable) event);
    }


}
