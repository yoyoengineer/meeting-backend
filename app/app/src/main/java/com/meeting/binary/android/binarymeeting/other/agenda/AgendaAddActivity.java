package com.meeting.binary.android.binarymeeting.other.agenda;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.meeting.binary.android.binarymeeting.R;

public class AgendaAddActivity extends AppCompatActivity {
    public static final String TAG = "AgendaAddActivity";
    private boolean saved = false;
    private String eventId;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public static void actionStart(Context context, ActivityOptionsCompat options,String eventId) {
        Intent intent = new Intent(context, AgendaAddActivity.class);
        intent.putExtra("eventId",eventId);
        context.startActivity(intent,options.toBundle());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agenda_add);
        eventId = getIntent().getStringExtra("eventId");
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.back);//设置返回键
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
//                finish();
            }
        });//返回监听
        final AgendaAddFragment agendaAddFragment = (AgendaAddFragment) getSupportFragmentManager().findFragmentById(R.id.agenda_add_fragment);
        agendaAddFragment.setEventId(eventId);
        agendaAddFragment.refresh(); // 刷新AgendaContentFragment界面
        final FloatingActionButton fab = findViewById(R.id.save_agenda_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (saved){
                    onBackPressed();
//                    AgendaAddActivity.this.finish();
                }else {
                    if (agendaAddFragment.saveAgenda() > 0){
                        fab.setImageDrawable(getResources().getDrawable(R.drawable.back_fab));
                        saved = true;
                    }
                    /**
                     * 取得当前会议所有的Agenda
                     * Todo
                     */
//                    List<Agenda> agendaList = new ArrayList<>();
//                    Agenda agenda = new Agenda();
//                    agenda.setContent("blabal");
//                    agenda.setDate(new Date());
//                    agenda.setEndTime(new Date());
//                    agenda.setLocation("blabla");
//                    agenda.setTitle("blabla");
//                    agendaList.add(agenda);
//                    List<Agenda> agendaList = Agenda.listAll(Agenda.class);
//                    for (Agenda a:agendaList) {
//                        Log.d(TAG, "onClick: " + a.toString());
//                    }
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        AgendaAddFragment agendaAddFragment = (AgendaAddFragment) getSupportFragmentManager().findFragmentById(R.id.agenda_add_fragment);
        agendaAddFragment.setEventId(eventId);
    }
}
