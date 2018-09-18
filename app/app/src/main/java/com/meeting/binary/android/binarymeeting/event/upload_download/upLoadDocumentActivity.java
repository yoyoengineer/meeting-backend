package com.meeting.binary.android.binarymeeting.event.upload_download;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;

import com.meeting.binary.android.binarymeeting.other.SingleFragmentActivity;

public class upLoadDocumentActivity extends SingleFragmentActivity {



    private static final String EXTRA_ID = "eventid";


    public static Intent newIntent(Context context, String eventId){
        Intent intent = new Intent(context, upLoadDocumentActivity.class);
        intent.putExtra(EXTRA_ID, eventId);
        return intent;
    }


    @Override
    public Fragment createFragment() {
        return UploadDocumentFragment.newInstance(getIntent().getStringExtra(EXTRA_ID));
    }
}
