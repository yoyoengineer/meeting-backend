package com.meeting.binary.android.binarymeeting.other.agenda_contact;

import android.animation.Animator;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.widget.TextView;

import com.meeting.binary.android.binarymeeting.R;
import com.meeting.binary.android.binarymeeting.model.Agenda;
import com.meeting.binary.android.binarymeeting.observers.AgendaAddFBClickObserver;
import com.meeting.binary.android.binarymeeting.other.agenda.AgendaAddActivity;
import com.meeting.binary.android.binarymeeting.other.agenda.AgendaContentActivity;
import com.meeting.binary.android.binarymeeting.other.agenda.AgendaMainActivity;
import com.meeting.binary.android.binarymeeting.service.generetor.GeneralServiceGenerator;
import com.meeting.binary.android.binarymeeting.service.request_interface.RequestWebServiceInterface;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

//import static com.meeting.binary.android.binarymeeting.event.AgendaTitleFragment.AgendaAdapter.TAG;

public class ContactAgendaTitleFragment extends Fragment implements AgendaAddFBClickObserver {
//    private boolean isTwoPane;
    public static final String TRANSITION_FAB = "fab_transition";
    private Calendar mCalendar;
    private View titleFragmentView;
    private String eventId;

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        titleFragmentView = inflater.inflate(R.layout.fragment_agenda_title, container, false);
        AppCompatActivity activity= (AppCompatActivity) getActivity();
        if (activity instanceof AgendaMainActivity){
            AgendaMainActivity mainActivity = (AgendaMainActivity) activity;
            mainActivity.addAgendaAddFBClickObserver(this);
        }
        return titleFragmentView;
    }

    @Override
    public void onResume() {
        super.onResume();
        mCalendar = Calendar.getInstance();
        getAgendas();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
//        if (getActivity().findViewById(R.id.agenda_content_layout) != null) {
//            isTwoPane = true; // 可以找到agenda_content_layout布局时，为双页模式
//        } else {
//            isTwoPane = false; // 找不到agenda_content_layout布局时，为单页模式
//        }
    }

    private void getAgendas() {
//        List<Agenda> agendaList = Agenda.listAll(Agenda.class);
        /**
         * 取得当前会议所有的Agenda
         */

        RequestWebServiceInterface requestWebServiceInterface =
                GeneralServiceGenerator.CreateService(RequestWebServiceInterface.class, getActivity());
        Call<List<Agenda>> call = requestWebServiceInterface.getAgendas(eventId);
        call.enqueue(new Callback<List<Agenda>>() {
            @Override
            public void onResponse(Call<List<Agenda>> call, Response<List<Agenda>> response) {
                List<Agenda> agendaList = new ArrayList<>();
                if (response.isSuccessful()) {
                    RecyclerView agendaTitleRecyclerView = (RecyclerView) titleFragmentView.findViewById(R.id.agenda_title_recycler_view);
                    LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
                    agendaTitleRecyclerView.setLayoutManager(layoutManager);

                    agendaList = response.body();
                    if (agendaList == null) {
                        agendaList = Collections.emptyList();
                    }

//                    Log.d(TAG, "onResponse: friends:" + agendaList);

                    AgendaAdapter adapter = new AgendaAdapter(agendaList);
                    agendaTitleRecyclerView.setAdapter(adapter);

                }

            }
            @Override
            public void onFailure(Call<List<Agenda>> call, Throwable t) {

            }
        });
//        Agenda agenda = new Agenda();
//        agenda.setContent("blabal");
//        agenda.setDate(new Date());
//        agenda.setStartTime(new Date());
//        agenda.setEndTime(new Date());
//        agenda.setLocation("blabla");
//        agenda.setTitle("blabla");
//        agendaList.add(agenda);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onNotifyForFBClick(View view) {
        Pair<View, String> pair = Pair.create(view.findViewById(R.id.fab), TRANSITION_FAB);

        ActivityOptionsCompat options;
        Activity act = getActivity();
        options = ActivityOptionsCompat.makeSceneTransitionAnimation(act, pair);


//        if (isTwoPane) {
//            AgendaAddFragment agendaAddFragment = (AgendaAddFragment)
//                    getFragmentManager().findFragmentById(R.id.agenda_add_fragment);
//            agendaAddFragment.refresh();
//        } else {
            AgendaAddActivity.actionStart(getActivity(),options,eventId);
//        }
    }

    class AgendaAdapter extends RecyclerView.Adapter<AgendaAdapter.ViewHolder> {

        private static final String TAG = "AgendaAdapter";
        private List<Agenda> magendaList;

        class ViewHolder extends RecyclerView.ViewHolder implements View.OnTouchListener{

            TextView agendaTitleText;
            TextView agendaStartTimeText;
            TextView agendaEndTimeText;
//            TextView agendaDateText;

            public ViewHolder(View view) {
                super(view);
                agendaTitleText = (TextView) view.findViewById(R.id.agenda_title);
                agendaStartTimeText = (TextView) view.findViewById(R.id.start_time);
                agendaEndTimeText = (TextView) view.findViewById(R.id.end_time);
//                agendaDateText = (TextView) view.findViewById(R.id.date);
            }

            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return false;
            }


        }

        public AgendaAdapter(List<Agenda> newsList) {
            magendaList = newsList;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.agenda_view_holder, parent, false);
            final ViewHolder holder = new ViewHolder(view);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Agenda agenda = magendaList.get(holder.getAdapterPosition());
//                    if (isTwoPane) {
//                        AgendaContentFragment agendaContentFragment = (AgendaContentFragment)
//                                getFragmentManager().findFragmentById(R.id.agenda_content_fragment);
//                        agendaContentFragment.refresh(agenda);
//                    } else {
                        AgendaContentActivity.actionStart(getActivity(), agenda);
//                    }
                }
            });
            return holder;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            Agenda agenda = magendaList.get(position);
            System.out.println(agenda);
            if (agenda != null){
                Log.d(TAG, "onBindViewHolder: " + agenda);
//                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEEE, MM dd, yyyy.");

                Calendar scalendar = new GregorianCalendar();
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                simpleDateFormat.setTimeZone(scalendar.getTimeZone());
                scalendar.setTimeInMillis(agenda.getStartTime());


                Calendar ecalendar = new GregorianCalendar();
                simpleDateFormat.setTimeZone(ecalendar.getTimeZone());
                ecalendar.setTimeInMillis(agenda.getEndTime());
//                agendaStartTimeTextView.setText(simpleDateFormat.format(calendar.getTime()));


//                mCalendar.setTime(agenda.getStartTime());
//                int hour = mCalendar.get(Calendar.HOUR_OF_DAY);
//                int minutes = mCalendar.get(Calendar.MINUTE);
//                String startTime = ""+hour + ":" + minutes+"";
//                mCalendar.setTime(agenda.getEndTime());
//                int endHour = mCalendar.get(Calendar.HOUR_OF_DAY);
//                int endMinutes = mCalendar.get(Calendar.MINUTE);
//                String endTime = ""+endHour + ":" + endMinutes+"";
                holder.agendaTitleText.setText(agenda.getTitle());
//                holder.agendaDateText.setText(simpleDateFormat.format(agenda.getDate()));
                holder.agendaStartTimeText.setText(simpleDateFormat.format(scalendar.getTime()));
                holder.agendaEndTimeText.setText(simpleDateFormat.format(ecalendar.getTime()));
            }
        }

        @Override
        public int getItemCount() {
            return magendaList.size();
        }


        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void onViewAttachedToWindow(ViewHolder viewHolder) {
            super.onViewAttachedToWindow(viewHolder);
            animateCircularReveal(viewHolder.itemView);
        }

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        public void animateCircularReveal(View view) {
            int centerX = 0;
            int centerY = 0;
            int startRadius = 0;
            int endRadius = Math.max(view.getWidth(), view.getHeight());
            Animator animation = ViewAnimationUtils.createCircularReveal(view, centerX, centerY, startRadius, endRadius);
            view.setVisibility(View.VISIBLE);
            animation.start();
        }
    }
}
