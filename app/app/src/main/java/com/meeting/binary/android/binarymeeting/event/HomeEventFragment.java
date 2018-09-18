package com.meeting.binary.android.binarymeeting.event;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.meeting.binary.android.binarymeeting.R;
import com.meeting.binary.android.binarymeeting.event.other_events.AttendedEventActivity;
import com.meeting.binary.android.binarymeeting.event.other_events.OtherEventsActivity;
import com.meeting.binary.android.binarymeeting.model.Event;
import com.meeting.binary.android.binarymeeting.service.generetor.GeneralServiceGenerator;
import com.meeting.binary.android.binarymeeting.service.request_interface.RequestWebServiceInterface;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by meldi on 3/19/2018.
 */

public class HomeEventFragment extends Fragment{

    private CardView mMyEvents;
    private CardView mOtherEvents;
    private CardView mCreateEvent;
    private CardView mParticipatingEvent;
    private CardView mAttendedEvent;
    private ImageView participatingEvent;

    /**handle the view pager adapter*/
    private int mCurrentPage;
    private EventHomeBoardingSlider mBoardingSlider;
    private ViewPager mSliderViewPager;
    private TextView[] mDots;
    private LinearLayout mDotLayout;

    private static final String TAG = "home_event_tag";
    private List<Event> mEvents;

    // timer
    private Timer timer = new Timer();

    public static HomeEventFragment newInstance() {
        Bundle args = new Bundle();
        HomeEventFragment fragment = new HomeEventFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }


    ViewPager.OnPageChangeListener viewLister = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            dotsIndicator(position);
            mCurrentPage = position;
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_event_home, container, false);

        // ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Campus Event");
        Toolbar toolbar = (Toolbar)view.findViewById(R.id.home_bar);
        toolbar.setTitle("Campus Event");

        mMyEvents = view.findViewById(R.id.home_events_my);
        mOtherEvents = view.findViewById(R.id.home_events_other);
        mCreateEvent = view.findViewById(R.id.home_create_event);
        mParticipatingEvent = view.findViewById(R.id.participating_event);
        mAttendedEvent = view.findViewById(R.id.history_events);

        participatingEvent = view.findViewById(R.id.upcomingView);

        /** handle the view pager adapter*/
        mBoardingSlider = new EventHomeBoardingSlider(getActivity());
        mSliderViewPager = (ViewPager)view.findViewById(R.id.sliderViewPager);
        mSliderViewPager.setAdapter(mBoardingSlider);
        mDotLayout = (LinearLayout) view.findViewById(R.id.dotsLayout);
        dotsIndicator(0);
        mSliderViewPager.addOnPageChangeListener(viewLister);


        // set gif image for participating events
        Glide.with(getActivity()).load(R.drawable.upcoming).asGif().diskCacheStrategy(DiskCacheStrategy.SOURCE).crossFade().into(participatingEvent);

        //auto slider
        timer.scheduleAtFixedRate(new MyTimerClass(), 2000, 4000);



        mParticipatingEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /**
                 * Todo
                 * 前往我要参加的会议的Activity.
                 */
                Intent intent = ParticipatingEventsActivity.newIntent(getActivity());
                startActivity(intent);
            }
        });

        mAttendedEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /**
                 * Todo
                 * 前往历史会议的Activity
                 */
                Intent intent = AttendedEventActivity.newIntent(getActivity());
                startActivity(intent);
            }
        });

        mCreateEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = CreateMeetingActivity.newIntent(getActivity());
                startActivity(intent);
            }
        });


        mOtherEvents.setOnClickListener(view1 -> {
            //go to other events
            Intent intent = OtherEventsActivity.newIntent(getActivity());
            startActivity(intent);
        });

        mMyEvents.setOnClickListener(view1 -> {
            requestDataEvent();
        });

        return view;
    }


    @Override
    public void onResume() {
        super.onResume();
        // timer.scheduleAtFixedRate(new MyTimerClass(), 2000, 4000);
    }




    @Override
    public void onPause() {
        super.onPause();
        timer.cancel();
    }





    /**
     * set timer for auto side in viewpager
     */
    public class MyTimerClass extends TimerTask{
        @Override
        public void run() {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mSliderViewPager.getCurrentItem() == 0) {
                        mSliderViewPager.setCurrentItem(1);
                    } else if (mSliderViewPager.getCurrentItem() == 1) {
                        mSliderViewPager.setCurrentItem(2);
                    } else if (mSliderViewPager.getCurrentItem() == 2) {
                        mSliderViewPager.setCurrentItem(3);
                    } else if (mSliderViewPager.getCurrentItem() == 3) {
                        mSliderViewPager.setCurrentItem(0);
                    }
                }
            });
        }
    }






    /**handle the dots in for the images*/
    public void dotsIndicator(int position){
        mDots = new TextView[4];
        mDotLayout.removeAllViews();

        for (int i = 0; i < mDots.length; i++){
            mDots[i] = new TextView(getActivity());
            mDots[i].setText(Html.fromHtml("&#8226"));
            mDots[i].setTextSize(35);
            mDots[i].setTextColor(getResources().getColor(R.color.transparentColor));

            mDotLayout.addView(mDots[i]);
        }

        if (mDots.length > 0){
            mDots[position].setTextColor(getResources().getColor(R.color.colorAccent));
        }
    }






    /**
     * ==================================
     * load the data from the web service
     * ==================================
     */
    public void requestDataEvent(){
        RequestWebServiceInterface requestWebServiceInterface =
                GeneralServiceGenerator.CreateService(RequestWebServiceInterface.class, getActivity());
        Call<List<Event>> call = requestWebServiceInterface.getAllEvents();
        call.enqueue(new Callback<List<Event>>() {
            @Override
            public void onResponse(Call<List<Event>> call, Response<List<Event>> response) {
                mEvents = response.body();
//                Log.i(TAG, "onResponse: " + mEvents.size() + " " + mEvents.get(0).getTopic());
                Intent intent = MyEventListActivity.newIntent(getActivity(), mEvents);
                startActivity(intent);
            }

            @Override
            public void onFailure(Call<List<Event>> call, Throwable t) {

            }
        });

    }




    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }
}
