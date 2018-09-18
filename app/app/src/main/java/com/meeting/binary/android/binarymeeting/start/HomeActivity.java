package com.meeting.binary.android.binarymeeting.start;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;

import com.meeting.binary.android.binarymeeting.other.SingleFragmentActivity;

public class HomeActivity extends SingleFragmentActivity {


    public static Intent newIntent(Context context){
        Intent intent = new Intent(context, HomeActivity.class);
        return intent;
    }


    @Override
    public Fragment createFragment() {
        return HomeFragment.newInstance();
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(startMain);
    }
}
