package com.meeting.binary.android.binarymeeting.event;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.meeting.binary.android.binarymeeting.R;

public class CreateMeetingActivity extends AppCompatActivity {

    public static Intent newIntent(Context context){
        Intent intent = new Intent(context, CreateMeetingActivity.class);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_meeting);
    }

}
