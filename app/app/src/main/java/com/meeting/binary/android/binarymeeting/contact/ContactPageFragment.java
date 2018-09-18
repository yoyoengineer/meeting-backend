package com.meeting.binary.android.binarymeeting.contact;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.meeting.binary.android.binarymeeting.R;
import com.meeting.binary.android.binarymeeting.event.MyEventListActivity;
import com.meeting.binary.android.binarymeeting.model.Event;
import com.meeting.binary.android.binarymeeting.model.MyContactPage;
import com.meeting.binary.android.binarymeeting.other.EventImages;
import com.meeting.binary.android.binarymeeting.service.cookie.CookiePreferences;
import com.meeting.binary.android.binarymeeting.service.generetor.BaseUrlGenerator;
import com.meeting.binary.android.binarymeeting.service.generetor.GeneralServiceGenerator;
import com.meeting.binary.android.binarymeeting.service.request_interface.RequestWebServiceInterface;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ContactPageFragment extends Fragment {


    private RecyclerView mContactEventRecyclerView;

    private MyContactPage mMyContactPage;
    private List<Event> mEvents;
    private ContactPageAdapter mContactPageAdapter;
    private int mLastItem = -1;

    private static final String MY_CONTACT_PAGE_PARSE_EXTRA_MESSAGE = "com.binary.contact_page_parcelable";

    private static final String TAG = "ContactFragmentbinnn";

    public static ContactPageFragment newInstance(MyContactPage contactPage){
        Bundle args = new Bundle();
        args.putSerializable(MY_CONTACT_PAGE_PARSE_EXTRA_MESSAGE, contactPage);
        ContactPageFragment fragment = new ContactPageFragment();
        fragment.setArguments(args);
        return fragment;

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null){
            mMyContactPage = (MyContactPage) getArguments().getSerializable(MY_CONTACT_PAGE_PARSE_EXTRA_MESSAGE);
            if (mMyContactPage != null) {
                Log.i(TAG, "onCreate: contact not null");
            } else {
                Log.i(TAG, "onCreate: contact page null");
            }
        }
}

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contact_page, container, false);


        mContactEventRecyclerView = view.findViewById(R.id.recycler_contact_page_event);
        mContactEventRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false){
            @Override
            public boolean canScrollHorizontally() {
                return false;
            }

            @Override
            public boolean canScrollVertically() {
                return false;
            }
        });



        requestData();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        requestData();
    }

    public void requestData(){
        RequestWebServiceInterface requestWebServiceInterface =
                GeneralServiceGenerator.CreateService(RequestWebServiceInterface.class, getActivity());
        Call<List<Event>> call = requestWebServiceInterface.getContactEvent(mMyContactPage.getUsername());
        call.enqueue(new Callback<List<Event>>() {
            @Override
            public void onResponse(Call<List<Event>> call, Response<List<Event>> response) {
                if (response.isSuccessful()){
                    mEvents = response.body();
                    if (mEvents != null){
                        updateUI();
                    }
                } else {
                    Log.i(TAG, "onResponse: bad response from the sever");
                }
            }
            @Override
            public void onFailure(Call<List<Event>> call, Throwable t) {
                Toast.makeText(getActivity(), "failed to connect to the server check your internet connection", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * ============================
     * sets up ContactFragment’s UI
     * ============================
     */
    private void updateUI(){
        if (mContactPageAdapter == null){
            mContactPageAdapter = new ContactPageAdapter(mEvents);
            mContactEventRecyclerView.setAdapter(mContactPageAdapter);
        } else{
            if(mLastItem < 0){
                mContactPageAdapter.setContactEvent(mEvents);
                mContactPageAdapter.notifyDataSetChanged();
            } else{
                mContactPageAdapter.notifyItemChanged(mLastItem);
                mLastItem = -1;
            }

        }
    }





        /**
         * =================================
         * the holder to the hosted fragment
         * =================================
         */
    class ContactPageHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private Event mEvent;

        private TextView mAttendedPeople;
        private TextView mMyTopic;
        private TextView mTimeCreated;
        private TextView mTopicDescription;
        private TextView mLike_number;
        private CardView mContactEvent;
        private ImageView event_bac;



        public ContactPageHolder(View itemView) {
            super(itemView);
        }


        public ContactPageHolder(LayoutInflater inflater, ViewGroup viewGroup){
            super(inflater.inflate(R.layout.list_item_contact_page_profile_event, viewGroup, false));

            mMyTopic = itemView.findViewById(R.id.my_topic_contact_page_profile);
            mAttendedPeople =itemView.findViewById(R.id.attented_people_contact_page_profile);
            mTimeCreated = itemView.findViewById(R.id.time_created_contact_page_profile);
            mTopicDescription = itemView.findViewById(R.id.topic_description_contact_page_profile);
            mLike_number = itemView.findViewById(R.id.like_number_contact_page_profile);
            mContactEvent = itemView.findViewById(R.id.contact_page_fragment_event);
            event_bac = itemView.findViewById(R.id.event_bac_pic);

        }

        public void bind(Event event, int position){
            mEvent = event;
            mMyTopic.setText(mEvent.getTopic());
            mAttendedPeople.setText(""+mEvent.getAttendees()+"");
            mTimeCreated.setText( mEvent.getReadableModifyDate(mEvent.getTime()));
            mTopicDescription.setText(mEvent.getDescription());
            mLike_number.setText("" + mEvent.getLikes() + "");
            event_bac.setBackgroundResource(EventImages.eventPics[position]);

            mContactEvent.setOnClickListener(view -> {
                Log.i(TAG, "ContactPageHolder: mEvent in the holder" + mEvents.get(this.getAdapterPosition()).getTopic());
                Intent intent = ContactEventActivity.newIntent(getActivity(), mEvent);
                startActivity(intent);

            });















            event_bac.setOnClickListener(view -> {
                Log.i(TAG, "ContactPageHolder: mEvent in the holder" + mEvents.get(this.getAdapterPosition()).getTopic());
                Intent intent = ContactEventActivity.newIntent(getActivity(), mEvent);
                startActivity(intent);

            });
        }



        @Override
        public void onClick(View view) {
            mLastItem = this.getAdapterPosition();
        }
    }



    /**
     * ======================================================
     * the adapter class to the hosted fragment recycler view
     * ======================================================
     */
    class ContactPageAdapter extends RecyclerView.Adapter<ContactPageHolder>{

        List<Event> mEvents;

        public ContactPageAdapter(List<Event> events){
            mEvents = events;
        }

        @Override
        public ContactPageHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            return new ContactPageHolder(inflater, parent);
        }

        @Override
        public void onBindViewHolder(ContactPageHolder holder, int position) {
            holder.bind(mEvents.get(position), position);
        }

        @Override
        public int getItemCount() {
            return mEvents.size();
        }

        public void setContactEvent(List<Event> events){
            mEvents = events;
        }

        public List<Event> getEvents() {
            return mEvents;
        }
    }
}
