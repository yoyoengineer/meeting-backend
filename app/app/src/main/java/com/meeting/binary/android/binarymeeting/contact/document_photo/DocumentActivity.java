package com.meeting.binary.android.binarymeeting.contact.document_photo;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.meeting.binary.android.binarymeeting.R;
import com.meeting.binary.android.binarymeeting.event.upload_download.FileDownloadPager;
import com.meeting.binary.android.binarymeeting.event.upload_download.UploadDocumentFragment;
import com.meeting.binary.android.binarymeeting.event.upload_download.ViewFileUploadedFragment;

public class DocumentActivity extends AppCompatActivity {

    private static final String EXTRA_MSG = "msg_id";
    private static final String TAG = "tag_id";
    private String eventId;


    public Fragment createFragment(String id){
        DocumentFragment fragment = DocumentFragment.newInstance(id);
        return fragment;
    }

    public static Intent newIntent(Context context, String id){
        Intent intent = new Intent(context, DocumentActivity.class);
        intent.putExtra(EXTRA_MSG, id);
        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.document_activity);
       
        eventId = getIntent().getStringExtra(EXTRA_MSG);
        if (eventId != null){
            Log.i(TAG, "onCreate: the event id is not null " + eventId);
        } else {
            Log.i(TAG, "onCreate: id is null");
        }

        setFragment(eventId);

    }

    /**set corresponding fragment in the layout*/
    private void setFragment(String id) {
        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.document_file);
        if (fragment == null){
            fragment = createFragment(id);
            fm.beginTransaction()
                    .add(R.id.document_file, fragment)
                    .commit();
        }
    }
}
