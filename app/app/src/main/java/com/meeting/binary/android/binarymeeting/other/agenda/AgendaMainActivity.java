package com.meeting.binary.android.binarymeeting.other.agenda;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.meeting.binary.android.binarymeeting.R;
import com.meeting.binary.android.binarymeeting.observers.AgendaAddFBClickObserver;

import java.util.ArrayList;
import java.util.List;

public class AgendaMainActivity extends AppCompatActivity {

    private List<AgendaAddFBClickObserver> agendaAddFBClickObservers = new ArrayList<>();
    public static Intent newIntent(Context context, String eventId){
        Intent intent = new Intent(context, AgendaMainActivity.class);
        intent.putExtra("eventId",eventId);
        return intent;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agenda_main);
        getSupportActionBar().setTitle("Agenda Event");
        String eventId = getIntent().getStringExtra("eventId");
        AgendaTitleFragment agendaTitleFragment = (AgendaTitleFragment) getSupportFragmentManager().findFragmentById(R.id.agenda_title_fragment);
        agendaTitleFragment.setEventId(eventId);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onClick(View v) {
                notifyAgendaAddFBClick(v);
            }
        });
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
