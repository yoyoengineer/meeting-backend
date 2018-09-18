package com.meeting.binary.android.binarymeeting.profile;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.meeting.binary.android.binarymeeting.other.SingleFragmentActivityNoBar;

import java.io.Serializable;

public class ProfileActivity extends SingleFragmentActivityNoBar {

    public static final String EXTRA_NAME = "com.profile.name";

    public static Intent newIntent(Context context, Serializable info){
        Bundle args = new Bundle();
        args.putSerializable(EXTRA_NAME, info);
        Intent intent = new Intent(context, ProfileActivity.class);
        intent.putExtras(args);
        return intent;
    }


    @Override
    public Fragment createFragment() {

        return ProfileFragment.newInstance(getIntent().getSerializableExtra(EXTRA_NAME));
    }

}
