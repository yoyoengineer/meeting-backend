package com.meeting.binary.android.binarymeeting.event.other_events;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.meeting.binary.android.binarymeeting.R;

public class OtherEventsActivity extends AppCompatActivity {

    private TabLayout otherEventsTabLayout;
    private ViewPager mViewPager;
    private OtherEventsPagerAdapter mPagerAdapter;

//    private static final String EXTRA_MSG_EI = "event_id";

    public static Intent newIntent(Context context){
        Intent intent = new Intent(context, OtherEventsActivity.class);
//        intent.putExtra(EXTRA_MSG_EI, eventId);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other_events);
//        String id = getIntent().getStringExtra(EXTRA_MSG_EI);

        otherEventsTabLayout = (TabLayout)findViewById(R.id.tab_layout);
        mPagerAdapter = new OtherEventsPagerAdapter(getSupportFragmentManager());
        mViewPager = (ViewPager)findViewById(R.id.pager);

        mPagerAdapter.addFragment(LatestEventsFragment.newInstance(), "最新");
        //TODO
        /**
         * 当Tab超过2个时，会出现bug
         */
//        mPagerAdapter.addFragment(SameCityEventsFragment.newInstance(), "同城推荐");
//        mPagerAdapter.addFragment(RecommendedEventsFragment.newInstance(), "最受欢迎");

        mPagerAdapter.addFragment(MostCaredSpeakersEventsFragment.newInstance(), "最关心的主持人");

        mViewPager.setAdapter(mPagerAdapter);
        otherEventsTabLayout.setupWithViewPager(mViewPager);
    }

}
