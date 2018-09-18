package com.meeting.binary.android.binarymeeting.event;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.NavUtils;
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

public class ParticipatingEventsFragment extends Fragment {
    private static final String TAG = "participating_event";
    private static final String EVENT_ALL_PARSE_EXTRA_MESSAGE = "com.binary.event_all_parcelable";

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView participatingEventListRecycler;
    private EventListAdapter participatingEventListAdapter;

    private List<Event> participatingEvents;
    private Event participatingEventToSend;
    private int participatingLastItem = -1;

    private TextView noEvents;

    public static ParticipatingEventsFragment newInstance() {
        ParticipatingEventsFragment fragment = new ParticipatingEventsFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    /**
     * ==================================
     * load the data from the web service
     * ==================================
     */
    public void requestData(){
        RequestWebServiceInterface requestWebServiceInterface =
                GeneralServiceGenerator.CreateService(RequestWebServiceInterface.class, getActivity());
        Call<List<Event>> call = requestWebServiceInterface.getParticipatingEvents();
        call.enqueue(new Callback<List<Event>>() {
            @Override
            public void onResponse(Call<List<Event>> call, Response<List<Event>> response) {
                if (response.isSuccessful()){
                    participatingEvents = response.body();
                    if (participatingEvents!= null && !participatingEvents.isEmpty()){


                        updateUi();
                        mSwipeRefreshLayout.setRefreshing(false);
                    } else {
                        //Toast.makeText(getActivity(), "refresh failed", Toast.LENGTH_SHORT).show();
                        Log.i(TAG, "onResponse: in isSuccessful participatingEventListAdapter is null");
                        mSwipeRefreshLayout.setRefreshing(false);
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                noEvents.setVisibility(View.VISIBLE);
                            }
                        });
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


    private void updateUi() {
        if (participatingEventListAdapter == null){
            participatingEventListAdapter = new EventListAdapter(participatingEvents);
            participatingEventListRecycler.setAdapter(participatingEventListAdapter);
        } else{
            if(participatingLastItem < 0){
                participatingEventListAdapter.setEventList(participatingEvents);
                participatingEventListAdapter.notifyDataSetChanged();
            } else{
                participatingEventListAdapter.notifyItemChanged(participatingLastItem);
                participatingLastItem = -1;
            }

        }
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_participating_events, container, false);
        // Inflate the layout for this fragment
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Participating Events");
        ((AppCompatActivity) getActivity()).getSupportActionBar().setIcon(getResources().getDrawable(R.drawable.bm));
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


        participatingEventListRecycler = view.findViewById(R.id.event_list_recycler);
        participatingEventListRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));

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
            default: return super.onOptionsItemSelected(item);
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
//        Log.d(TAG, "requestDataEvent: " + "mEvent:" + otherEvents.get(position));
//        Log.d(TAG, "requestDataEvent: " + "mEvents.get(position).getId():" + otherEvents.get(position).getId());
        Call<Event> call = requestWebServiceInterface.getEvent(participatingEvents.get(position).getId());
        call.enqueue(new Callback<Event>() {
            @Override
            public void onResponse(Call<Event> call, Response<Event> response) {
                participatingEventToSend = response.body();
                if (participatingEventToSend != null){
                    Intent intent = ContactEventActivity.newIntent(getActivity(), participatingEventToSend);
                    startActivity(intent);
                } else {
                }
//                Log.i(TAG, "onResponse: " + otherEventToSend.getId() + " " + otherEventToSend.getTopic());
            }

            @Override
            public void onFailure(Call<Event> call, Throwable t) {
//                Log.i(TAG, "onFailure: requestDataEvent failed");
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
            super(inflater.inflate(R.layout.item_list_participating, parent, false));
            itemView.setOnClickListener(this);
            eventImg = itemView.findViewById(R.id.event_pic);
            mEventDateCreated = itemView.findViewById(R.id.event_date_created);
            mEventTopic = itemView.findViewById(R.id.event_topic);
        }

        @Override
        public void onClick(View view) {
            participatingLastItem = this.getAdapterPosition();
//            Log.d(TAG, "onClick: " + "mLastItem:"+ participatingLastItem);
            Toast.makeText(getActivity(), "item cliced", Toast.LENGTH_LONG).show();
            requestDataEvent(participatingLastItem);
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
    class EventListAdapter extends RecyclerView.Adapter<EventListHolder>{

        List<Event> mEvents;

        public EventListAdapter(List<Event> events) {
            mEvents = events;
        }

        @Override
        public ParticipatingEventsFragment.EventListHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            return new ParticipatingEventsFragment.EventListHolder(inflater, parent);
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
