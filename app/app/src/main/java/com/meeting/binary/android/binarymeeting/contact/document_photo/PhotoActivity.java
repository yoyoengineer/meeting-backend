package com.meeting.binary.android.binarymeeting.contact.document_photo;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.meeting.binary.android.binarymeeting.R;
import com.meeting.binary.android.binarymeeting.event.upload_download.PagerAdapter;
import com.meeting.binary.android.binarymeeting.event.upload_download.UploadPhotoFragment;
import com.meeting.binary.android.binarymeeting.event.upload_download.ViewPhotoUploadedFragment;

public class PhotoActivity extends AppCompatActivity {

    private static final String EXTRA_MSG_EI = "event_id";

    public static Intent newIntent(Context context, String eventId){
        Intent intent = new Intent(context, PhotoActivity.class);
        intent.putExtra(EXTRA_MSG_EI, eventId);
        return intent;
    }


    public Fragment createFragment(String id){
        PhotoFragment fragment = PhotoFragment.newInstance(id);
        return fragment;
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.photo_activity);

        String id = getIntent().getStringExtra(EXTRA_MSG_EI);
        setFragment(id);
    }


    /**set corresponding fragment in the layout*/
    private void setFragment(String id) {
        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.photo_fragment);
        if (fragment == null){
            fragment = createFragment(id);
            fm.beginTransaction()
                    .add(R.id.photo_fragment, fragment)
                    .commit();
        }
    }
}
