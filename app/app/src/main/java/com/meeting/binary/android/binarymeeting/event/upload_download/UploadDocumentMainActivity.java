package com.meeting.binary.android.binarymeeting.event.upload_download;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.meeting.binary.android.binarymeeting.R;

public class UploadDocumentMainActivity extends AppCompatActivity {

    private static final String EXTRA_MSG = "msg_id";
    private static final String TAG = "tag_id";
    private String eventId;

    private TabLayout mPhotoTabLayout;
    private ViewPager mViewPager;
    private FileDownloadPager mPager;

    public static Intent newIntent(Context context, String id){
        Intent intent = new Intent(context, UploadDocumentMainActivity.class);
        intent.putExtra(EXTRA_MSG, id);
        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.upload_document_main_activity);

        eventId = getIntent().getStringExtra(EXTRA_MSG);
        if (eventId != null){
            Log.i(TAG, "onCreate: the event id is not null " + eventId);
        } else {
            Log.i(TAG, "onCreate: id is null");
        }

        mPhotoTabLayout = (TabLayout)findViewById(R.id.tab_layout);
        mPager = new FileDownloadPager(getSupportFragmentManager());
        mViewPager = (ViewPager)findViewById(R.id.pager);

        mPager.addFragment(ViewFileUploadedFragment.newInstance(eventId), "event files");
        mPager.addFragment(UploadDocumentFragmentBar.newInstance(eventId), "upload file");

        mViewPager.setAdapter(mPager);
        mPhotoTabLayout.setupWithViewPager(mViewPager);
    }
}
