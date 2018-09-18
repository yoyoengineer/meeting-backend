package com.meeting.binary.android.binarymeeting.contact;

import android.support.v4.app.Fragment;

import com.meeting.binary.android.binarymeeting.other.SingleFragmentActivityNoBar;

public class AddContactActivity extends SingleFragmentActivityNoBar {


    @Override
    public Fragment createFragment() {
        return AddContactFragment.newInstance();
    }


}
