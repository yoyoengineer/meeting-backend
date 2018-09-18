package com.meeting.binary.android.binarymeeting.event.other_events;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.meeting.binary.android.binarymeeting.R;
import com.meeting.binary.android.binarymeeting.contact.ContactEventActivity;
import com.meeting.binary.android.binarymeeting.model.Event;
import com.meeting.binary.android.binarymeeting.other.EventImages;
import com.meeting.binary.android.binarymeeting.service.generetor.GeneralServiceGenerator;
import com.meeting.binary.android.binarymeeting.service.request_interface.RequestWebServiceInterface;
import com.meeting.binary.android.binarymeeting.utils.DateUtils;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AttendedEventFragment extends Fragment {

    private static final String TAG = "latest_event_list_tag";
    private static final String EVENT_ALL_PARSE_EXTRA_MESSAGE = "com.binary.event_all_parcelable";

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView recommendedEventListRecycler;
    private EventListAdapter recommendedEventListAdapter;

    private List<Event> recommendedEvents;
    private Event recommendedEventToSend;
    private int mLastItem = -1;


    private TextView noEvents;

    public static AttendedEventFragment newInstance() {
        Bundle args = new Bundle();
        AttendedEventFragment fragment = new AttendedEventFragment();
        fragment.setArguments(args);
        return fragment;
    }



    /**
     * ==================================
     * load the data from the web service
     * ==================================
     */
    public void requestData(){
        RequestWebServiceInterface requestWebServiceInterface =
                GeneralServiceGenerator.CreateService(RequestWebServiceInterface.class, getActivity());
        Call<List<Event>> call = requestWebServiceInterface.getAttendedEvents();
        call.enqueue(new Callback<List<Event>>() {
            @Override
            public void onResponse(Call<List<Event>> call, Response<List<Event>> response) {
                if (response.isSuccessful()){
                    recommendedEvents = response.body();
                    if (recommendedEvents != null && !recommendedEvents.isEmpty()){

                        updateUi();
                        mSwipeRefreshLayout.setRefreshing(false);
                        Log.i(TAG, "onResponse: recommendedEvents is " + recommendedEvents.size());
//                        Log.i(TAG, "onResponse: recommendedEvents is id : " + recommendedEvents.get(0).getId());
                    } else {
                        Log.i(TAG, "onResponse: recommendedEvents is null");
                        mSwipeRefreshLayout.setRefreshing(false);
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                noEvents.setVisibility(View.VISIBLE);
                            }
                        });

                    }
                } else {
                    Log.d(TAG, "onResponse: bad response");
                    mSwipeRefreshLayout.setRefreshing(false);
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            noEvents.setVisibility(View.VISIBLE);
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<List<Event>> call, Throwable t) {
                Log.e(TAG, "onFailure: fail to connect" + t.getMessage());
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    private void updateUi() {
        if (recommendedEventListAdapter == null){
            recommendedEventListAdapter = new EventListAdapter(recommendedEvents);
            recommendedEventListRecycler.setAdapter(recommendedEventListAdapter);
        } else{
            if(mLastItem < 0){
                recommendedEventListAdapter.setEventList(recommendedEvents);
                recommendedEventListAdapter.notifyDataSetChanged();
            } else{
                recommendedEventListAdapter.notifyItemChanged(mLastItem);
                mLastItem = -1;
            }

        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        setHasOptionsMenu(true);
        Log.d(TAG, "onCreate: " + recommendedEvents);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.historic_event_attended_fragment_layout, container, false);

        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Attended Events");
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        int orientation = getResources().getConfiguration().orientation;
        if (Configuration.ORIENTATION_LANDSCAPE == orientation) {
            view.setBackgroundResource (R.color.white);
        } else {
            view.setBackgroundResource (R.color.white);
        }

        mSwipeRefreshLayout = view.findViewById(R.id.swipe_refresh);
        noEvents = view.findViewById(R.id.no_meet_text);

        mSwipeRefreshLayout.setProgressBackgroundColorSchemeColor(getResources().getColor(R.color.primary_blue));
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestData();
            }
        });

        recommendedEventListRecycler = view.findViewById(R.id.event_list_recycler);
        recommendedEventListRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));


        //requestData();
        return view;
    }


    @Override
    public void onResume() {
        super.onResume();
        requestData();
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                getActivity().onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * =========================================
     * load the data event from the web service
     * =========================================
     */
    public void requestDataEvent(int position){
        Log.d(TAG, "requestDataEvent: " + "mEvent:" + recommendedEvents.get(position));
        Log.d(TAG, "requestDataEvent: " + "mEvents.get(position).getId():" + recommendedEvents.get(position).getId());
        RequestWebServiceInterface requestWebServiceInterface =
                GeneralServiceGenerator.CreateService(RequestWebServiceInterface.class, getActivity());

        Call<Event> call = requestWebServiceInterface.getEvent(recommendedEvents.get(position).getId());
        call.enqueue(new Callback<Event>() {
            @Override
            public void onResponse(Call<Event> call, Response<Event> response) {
                recommendedEventToSend = response.body();
                if (recommendedEventToSend != null){
                    Intent intent = ContactEventActivity.newIntent(getActivity(), recommendedEventToSend);
                    startActivity(intent);
                } else {
                }
                Log.i(TAG, "onResponse: " + recommendedEventToSend.getId() + " " + recommendedEventToSend.getTopic());
            }

            @Override
            public void onFailure(Call<Event> call, Throwable t) {
                Log.i(TAG, "onFailure: requestDataEvent failed");
            }
        });

    }




    /**
     * ====================================================
     * holder to create the view and be bind to the adapter
     * ====================================================
     */
    private class EventListHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private Event mEvent;

        private TextView mEventDateCreated;
        private TextView mEventTopic;
        private android.widget.ImageView eventImg;

        public EventListHolder(View itemView) {
            super(itemView);
        }

        public EventListHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.list_item_historic, parent, false));
            itemView.setOnClickListener(this);

            mEventDateCreated = itemView.findViewById(R.id.event_date_created);
            mEventTopic = itemView.findViewById(R.id.event_topic);
            eventImg = itemView.findViewById(R.id.event_pic);
        }

        @Override
        public void onClick(View view) {
            mLastItem = this.getAdapterPosition();
            Log.d(TAG, "onClick: " + "mLastItem:"+ mLastItem);
            Toast.makeText(getActivity(), "item cliced", Toast.LENGTH_LONG).show();
            requestDataEvent(mLastItem);
        }

        public void bind(Event event, int position){
            mEvent = event;
            mEventTopic.setText(mEvent.getTopic());
            eventImg.setImageResource(EventImages.eventImages[position]);
            mEventDateCreated.setText(DateUtils.getReadableModifyDate(mEvent.getTime()));
        }
    }




    /**
     * =========================================
     * adapter to be set in the recycler
     * =========================================
     */
    private class EventListAdapter extends RecyclerView.Adapter<EventListHolder>{

        List<Event> mEvents;

        public EventListAdapter(List<Event> events) {
            mEvents = events;
        }

        @Override
        public EventListHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            return new EventListHolder(inflater, parent);
        }

        @Override
        public void onBindViewHolder(EventListHolder holder, int position) {
            holder.bind(mEvents.get(position), position);
        }

        @Override
        public int getItemCount() {
            return mEvents.size();
        }

        public void setEventList(List<Event> events){
            mEvents = events;
        }
    }
}
