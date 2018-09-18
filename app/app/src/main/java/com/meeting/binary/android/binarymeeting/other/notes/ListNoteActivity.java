package com.meeting.binary.android.binarymeeting.other.notes;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import com.meeting.binary.android.binarymeeting.R;
import com.meeting.binary.android.binarymeeting.other.todo.ListTaskActivity;

public class ListNoteActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_task);

        setFragment();

    }

    private void setFragment() {
        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.container);

        if (fragment == null){
            fragment = ListNoteFragment.newInstance();
            fm.beginTransaction()
                    .add(R.id.container, fragment)
                    .commit();
        }
    }

    public static final Intent newIntent(Context context){
        Intent intent = new Intent(context, ListNoteActivity.class);
        return intent;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }
}
