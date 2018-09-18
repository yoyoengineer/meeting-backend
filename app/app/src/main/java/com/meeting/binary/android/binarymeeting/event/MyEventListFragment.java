package com.meeting.binary.android.binarymeeting.event;

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

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.meeting.binary.android.binarymeeting.R;
import com.meeting.binary.android.binarymeeting.model.Event;
import com.meeting.binary.android.binarymeeting.other.EventImages;
import com.meeting.binary.android.binarymeeting.service.generetor.GeneralServiceGenerator;
import com.meeting.binary.android.binarymeeting.service.request_interface.RequestWebServiceInterface;


import java.io.Serializable;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by meldi on 3/18/2018.
 */

public class MyEventListFragment extends Fragment {

    private static final String TAG = "my_all_event_list_tag";
    private static final String EVENT_ALL_PARSE_EXTRA_MESSAGE = "com.binary.event_all_parcelable";

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView mEventListRecycler;
    private EventListAdapter mEventListAdapter;

    private List<Event> mEvents;
    private Event mEventToSend;
    private int mLastItem = -1;

    private TextView noEvents;


    public static MyEventListFragment newInstance(Serializable events){
        MyEventListFragment fragment = new MyEventListFragment();
        Bundle args = new Bundle();
        args.putSerializable(EVENT_ALL_PARSE_EXTRA_MESSAGE, events);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null){
            mEvents = (List<Event>) getArguments().getSerializable(EVENT_ALL_PARSE_EXTRA_MESSAGE);
        } else {
            Log.i(TAG, "onCreate: argument null");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.my_event_list_fragment, container, false);

        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("List of Events");
        ((AppCompatActivity) getActivity()).getSupportActionBar().setIcon(getResources().getDrawable(R.drawable.bm));

        int orientation = getResources().getConfiguration().orientation;
        if (Configuration.ORIENTATION_LANDSCAPE == orientation) {
            view.setBackgroundResource(R.color.white);
        } else {
            view.setBackgroundResource(R.color.white);
        }

        mSwipeRefreshLayout = view.findViewById(R.id.swipe_refresh);
        noEvents = view.findViewById(R.id.no_meet_text);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestDataEvent();
            }
        });

        
        mEventListRecycler = view.findViewById(R.id.event_list_recycler);
        mEventListRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));

        updateUI();
        return view;
    }


    @Override
    public void onResume() {
        super.onResume();
        requestDataEvent();
    }


    public void requestDataEvent(){
        RequestWebServiceInterface requestWebServiceInterface =
                GeneralServiceGenerator.CreateService(RequestWebServiceInterface.class, getActivity());
        Call<List<Event>> call = requestWebServiceInterface.getAllEvents();
        call.enqueue(new Callback<List<Event>>() {
            @Override
            public void onResponse(Call<List<Event>> call, Response<List<Event>> response) {
                if (response.isSuccessful()){
                    mEvents = response.body();
                    if (mEvents != null && !mEvents.isEmpty()){

                        updateUI();

                        mSwipeRefreshLayout.setRefreshing(false);
                    } else {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                noEvents.setVisibility(View.VISIBLE);
                            }
                        });
                        Toast.makeText(getActivity(), "refresh failed", Toast.LENGTH_SHORT).show();
                        Log.i(TAG, "onResponse: in isSuccessful mEvents is null");
                        mSwipeRefreshLayout.setRefreshing(false);
                    }
                } else {
                    Log.i(TAG, "onResponse: bad response");
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
                Log.i(TAG, "onFailure: request failed");
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });

    }


    /**
     * ===========
     * sets up UI
     * ===========
     */
    private void updateUI(){
        if (mEventListAdapter == null){
            mEventListAdapter = new EventListAdapter(mEvents);
            mEventListRecycler.setAdapter(mEventListAdapter);
        } else{
            if(mLastItem < 0){
                mEventListAdapter.setEventList(mEvents);
                mEventListAdapter.notifyDataSetChanged();
            } else{
                mEventListAdapter.notifyItemChanged(mLastItem);
                mLastItem = -1;
            }

        }
    }



    /**
     * =========================================
     * load the data event from the web service
     * =========================================
     */
    public void requestDataEvent(int position){
        RequestWebServiceInterface requestWebServiceInterface =
                GeneralServiceGenerator.CreateService(RequestWebServiceInterface.class, getActivity());
        Log.d(TAG, "requestDataEvent: " + "mEvent:" + mEvents.get(position));
        Log.d(TAG, "requestDataEvent: " + "mEvents.get(position).getId():" + mEvents.get(position).getId());
        Call<Event> call = requestWebServiceInterface.getEvent(mEvents.get(position).getId());
        call.enqueue(new Callback<Event>() {
            @Override
            public void onResponse(Call<Event> call, Response<Event> response) {
                mEventToSend = response.body();
                if (mEventToSend != null){
                    Intent intent = MyEventActivity.newIntent(getActivity(), mEventToSend);
                    startActivity(intent);
                } else {
                }
                Log.i(TAG, "onResponse: " + mEventToSend.getId() + " " + mEventToSend.getTopic());
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
    class EventListHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private Event mEvent;

        private TextView mEventDateCreated;
        private TextView mEventTopic;
        private android.widget.ImageView eventImg;


        public EventListHolder(View itemView) {
            super(itemView);
        }

        public EventListHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.list_item_my_event_list, parent, false));
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
            mEventDateCreated.setText(mEvent.getReadableModifyDate(mEvent.getTime()));
        }
    }




    /**
     * =========================================
     * adapter to be set in the recycler
     * =========================================
     */
    class EventListAdapter extends RecyclerView.Adapter<EventListHolder>{

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
