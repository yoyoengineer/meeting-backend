package com.meeting.binary.android.binarymeeting.contact;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class ContactPager extends FragmentPagerAdapter {


    List<Fragment> mFragmentContactList = new ArrayList<>();
    List<String> titles = new ArrayList<>();

    public ContactPager(FragmentManager fm) { super(fm); }

    @Override
    public Fragment getItem(int position) { return mFragmentContactList.get(position); }

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
        mFragmentContactList.add(fragment);
        titles.add(title);
    }
}
