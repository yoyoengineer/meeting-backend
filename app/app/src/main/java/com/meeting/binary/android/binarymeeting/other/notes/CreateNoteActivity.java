package com.meeting.binary.android.binarymeeting.other.notes;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import com.meeting.binary.android.binarymeeting.R;

public class CreateNoteActivity extends AppCompatActivity {

    private static final String EXTRA_MESSAGE = "extra_id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_note_activity);

        setFragment();

    }

    private void setFragment() {
        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.add_container);

        if (fragment == null){
            String id = getIntent().getStringExtra(EXTRA_MESSAGE);
            fragment = CreateNoteFragment.newInstance(id);
            fm.beginTransaction()
                    .add(R.id.add_note_container, fragment)
                    .commit();
        }
    }

    public static final Intent newIntent(Context context, String id){
        Intent intent = new Intent(context, CreateNoteActivity.class);
        intent.putExtra(EXTRA_MESSAGE, id);
        return intent;
    }
}
