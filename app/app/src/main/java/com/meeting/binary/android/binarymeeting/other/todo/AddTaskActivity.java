package com.meeting.binary.android.binarymeeting.other.todo;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import com.meeting.binary.android.binarymeeting.R;

/**
 * Created by meldi on 4/8/2018.
 */

public class AddTaskActivity extends AppCompatActivity {

    private static final String EXTRA_MESSAGE = "extra_id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_task_activity);

        setFragment();

    }

    private void setFragment() {
        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.add_container);

        if (fragment == null){
            String id = getIntent().getStringExtra(EXTRA_MESSAGE);
            fragment = AddTaskFragment.newInstance(id);
            fm.beginTransaction()
                    .add(R.id.add_container, fragment)
                    .commit();
        }
    }

    public static final Intent newIntent(Context context, String id){
        Intent intent = new Intent(context, AddTaskActivity.class);
        intent.putExtra(EXTRA_MESSAGE, id);
        return intent;
    }
}
