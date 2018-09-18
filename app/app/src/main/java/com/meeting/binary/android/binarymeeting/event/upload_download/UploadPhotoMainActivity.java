package com.meeting.binary.android.binarymeeting.event.upload_download;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.meeting.binary.android.binarymeeting.R;

public class UploadPhotoMainActivity extends AppCompatActivity {

    private TabLayout mPhotoTabLayout;
    private ViewPager mViewPager;
    private PagerAdapter mPagerAdapter;

    private static final String EXTRA_MSG_EI = "event_id";

    public static Intent newIntent(Context context, String eventId){
        Intent intent = new Intent(context, UploadPhotoMainActivity.class);
        intent.putExtra(EXTRA_MSG_EI, eventId);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.upload_photo_activity_main);

        String id = getIntent().getStringExtra(EXTRA_MSG_EI);

        mPhotoTabLayout = (TabLayout)findViewById(R.id.tab_layout);
        mPagerAdapter = new PagerAdapter(getSupportFragmentManager());
        mViewPager = (ViewPager)findViewById(R.id.pager);

        mPagerAdapter.addFragment(ViewPhotoUploadedFragment.newInstance(id), "event photos");
        mPagerAdapter.addFragment(UploadPhotoFragmentBar.newInstance(id), "upload photo");

        mViewPager.setAdapter(mPagerAdapter);
        mPhotoTabLayout.setupWithViewPager(mViewPager);
    }
}
