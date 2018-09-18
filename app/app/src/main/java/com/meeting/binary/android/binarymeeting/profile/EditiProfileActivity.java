package com.meeting.binary.android.binarymeeting.profile;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;

import com.meeting.binary.android.binarymeeting.other.SingleFragmentActivityNoBar;

public class EditiProfileActivity extends SingleFragmentActivityNoBar {


    @Override
    public Fragment createFragment() {
        return EditProfileFragment.newInstance();
    }



    public static Intent newIntent(Context context){
        Intent intent = new Intent(context, EditiProfileActivity.class);
        return intent;
    }
}
