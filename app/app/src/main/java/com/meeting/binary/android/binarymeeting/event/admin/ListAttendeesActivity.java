package com.meeting.binary.android.binarymeeting.event.admin;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.meeting.binary.android.binarymeeting.event.upload_download.UploadDocumentFragment;
import com.meeting.binary.android.binarymeeting.event.upload_download.UploadPhotoActivity;
import com.meeting.binary.android.binarymeeting.other.SingleFragmentActivityNoBar;

import java.io.Serializable;

public class ListAttendeesActivity extends SingleFragmentActivityNoBar {

    private static final String EXTRA_ID = "eventid";
    private static final String EXTRA_Contact = "eventid";

    public static Intent newIntent(Context context, Serializable contact){
        Bundle args = new Bundle();
        Intent intent = new Intent(context, ListAttendeesActivity.class);
        args.putSerializable(EXTRA_Contact, contact);
        intent.putExtras(args);
        return intent;
    }


    @Override
    public Fragment createFragment() {
        return ListAttendeesFragment.newInstance(getIntent().getSerializableExtra(EXTRA_Contact));
    }

}
