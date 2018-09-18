package com.meeting.binary.android.binarymeeting.other.agenda_contact;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.meeting.binary.android.binarymeeting.R;
import com.meeting.binary.android.binarymeeting.observers.AgendaAddFBClickObserver;
import com.meeting.binary.android.binarymeeting.other.agenda.AgendaTitleFragment;

import java.util.ArrayList;
import java.util.List;

public class ContactAgendaMainActivity extends AppCompatActivity {

    private List<AgendaAddFBClickObserver> agendaAddFBClickObservers = new ArrayList<>();
    public static Intent newIntent(Context context, String eventId){
        Intent intent = new Intent(context, ContactAgendaMainActivity.class);
        intent.putExtra("eventId",eventId);
        return intent;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agenda_main_contact);
        String eventId = getIntent().getStringExtra("eventId");


        getSupportActionBar().setTitle("CampusEvent");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        AgendaTitleFragment agendaTitleFragment = (AgendaTitleFragment) getSupportFragmentManager().findFragmentById(R.id.agenda_title_fragment);
        agendaTitleFragment.setEventId(eventId);

        View view = getWindow().getDecorView();
        int orientation = getResources().getConfiguration().orientation;
        if (Configuration.ORIENTATION_LANDSCAPE == orientation) {
            view.setBackgroundResource (R.drawable.agenda_background);
        } else {
            view.setBackgroundResource (R.drawable.agenda_background);
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onClick(View v) {
                notifyAgendaAddFBClick(v);
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default: return super.onOptionsItemSelected(item);
        }
    }

    public void addAgendaAddFBClickObserver(AgendaAddFBClickObserver agendaAddFBClickObserver){
        agendaAddFBClickObservers.add(agendaAddFBClickObserver);
    }

    private void notifyAgendaAddFBClick(View v){
        for (AgendaAddFBClickObserver a: agendaAddFBClickObservers) {
            a.onNotifyForFBClick(v);
        }
    }
}
