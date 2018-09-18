package com.meeting.binary.android.binarymeeting.event.other_events;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class OtherEventsPagerAdapter extends FragmentPagerAdapter {
    List<Fragment> otherEventsFragmentList = new ArrayList<>();
    List<String> titles = new ArrayList<>();

    public OtherEventsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return otherEventsFragmentList.get(position);
    }

    @Override
    public int getCount() {
        return titles.size();
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return titles.get(position);
    }


    public void addFragment(Fragment fragment, String title){
        otherEventsFragmentList.add(fragment);
        titles.add(title);
    }
}
