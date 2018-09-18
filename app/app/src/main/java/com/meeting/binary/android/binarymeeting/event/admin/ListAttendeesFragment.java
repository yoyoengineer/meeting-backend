package com.meeting.binary.android.binarymeeting.event.admin;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
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
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.meeting.binary.android.binarymeeting.R;
import com.meeting.binary.android.binarymeeting.event.other_events.LargePhotoFragment;
import com.meeting.binary.android.binarymeeting.event.upload_download.ViewPhotoUploadedFragment;
import com.meeting.binary.android.binarymeeting.model.Contact;
import com.meeting.binary.android.binarymeeting.service.cookie.CookiePreferences;
import com.meeting.binary.android.binarymeeting.service.generetor.BaseUrlGenerator;

import java.io.Serializable;
import java.util.List;

public class ListAttendeesFragment extends Fragment {

    List<Contact> mContacts;

    private static final String ARG_CONTACT = "event_contact_attendees";
    private static final String EXTRA_ID = "event_id";
    private static final String TAG = "attendees_list_tag";

    private int mLastItem = -1;

    private static final int REQUEST_PHOTO = 0;
    private static final String DIALOG_PHOTO = "LargePhotoFragment";

    private String eventId;

    private RecyclerView mAttendeesRecyclerView;
    private AttendeesEventAdapter mAdapter;


    public static ListAttendeesFragment newInstance(Serializable contacts) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_CONTACT, contacts);
        ListAttendeesFragment fragment = new ListAttendeesFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mContacts = (List<Contact>)getArguments().getSerializable(ARG_CONTACT);
        if (mContacts != null){
            Log.d(TAG, "onCreate: recup contacts succeed");
        } else {
            Log.d(TAG, "onCreate: recup contacts failed");
        }
    }

    public static ListAttendeesFragment newInstance(String id) {
        Bundle args = new Bundle();
        args.putString(EXTRA_ID, id);
        ListAttendeesFragment fragment = new ListAttendeesFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_list_attendees, null);

        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Campus Event");
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mAttendeesRecyclerView = (RecyclerView)view.findViewById(R.id.recycler_like);
        mAttendeesRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        updateUI();

        return view;
    }






    /** sets up ContactFragment’s UI */
    private void updateUI(){
        if (mAdapter == null){
            mAdapter = new AttendeesEventAdapter(mContacts);
            mAttendeesRecyclerView.setAdapter(mAdapter);
        } else{
            if(mLastItem < 0){
                mAdapter.setContacts(mContacts);
                mAdapter.notifyDataSetChanged();
            } else{
                mAdapter.notifyItemChanged(mLastItem);
                mLastItem = -1;
            }
        }
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
     * =================================================
     * the holder to the initial hosted contact fragment
     * =================================================
     */
    private class AttendeesEventHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private Contact mContact;
        private TextView mNameTextView;
        private TextView mEmailTextView;
        private ImageView mContactImg;
        private TextView mPrrofile;


        public AttendeesEventHolder(View itemView) {
            super(itemView);
        }


        public AttendeesEventHolder(LayoutInflater inflater, ViewGroup viewGroup){
            super(inflater.inflate(R.layout.item_list_attendees, viewGroup, false));
            itemView.setOnClickListener(this);

            mNameTextView = itemView.findViewById(R.id.contact_name);
            mEmailTextView =  itemView.findViewById(R.id.contact_email);
            mContactImg = itemView.findViewById(R.id.contact_img);
            mPrrofile = itemView.findViewById(R.id.profile);
        }

        public void bind(Contact contact){
            mContact = contact;
            mNameTextView.setText(mContact.getName());
            mEmailTextView.setText(mContact.getTown());


            Log.d(TAG, "bind: photo " + mContact.getPhoto());
            if (mContact.getPhoto() != null){
                String recupCookie = CookiePreferences.getStoredCookie(getActivity());
                Log.i(TAG, "intercept: " + recupCookie);
                LazyHeaders.Builder builder = new LazyHeaders.Builder().addHeader("Cookie", recupCookie);
                GlideUrl glideUrl = new GlideUrl(BaseUrlGenerator.BINARY_BASE_URL + "/profile/picture/" + mContact.getPhoto(), builder.build());

                Glide.with(ListAttendeesFragment.this)
                        .load(glideUrl)
                        .asBitmap()
                        .centerCrop()
                        .placeholder(getResources().getDrawable(R.drawable.photo_no_image))
                        .into(new BitmapImageViewTarget(mContactImg) {
                            @Override
                            protected void setResource(Bitmap resource) {
                                RoundedBitmapDrawable drawable = RoundedBitmapDrawableFactory.create(ListAttendeesFragment.this.getResources(),
                                        Bitmap.createScaledBitmap(resource, 70, 70, false));
                                drawable.setCircular(true);
                                mContactImg.setImageDrawable(drawable);
                            }
                        });
                Log.i(TAG, "bind: " + mContact.getPhoto());


                /**go to his profile*/
                mPrrofile.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                    }
                });



                mContactImg.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        FragmentManager manager = getFragmentManager();
                        LargePhotoFragment dialog = LargePhotoFragment.newInstance(BaseUrlGenerator.BINARY_BASE_URL + "/profile/picture/" + mContact.getPhoto(), mContact.getName());
                        dialog.setTargetFragment(ListAttendeesFragment.this, REQUEST_PHOTO);
                        dialog.show(manager, DIALOG_PHOTO);
                    }
                });
            }

        }

        @Override
        public void onClick(View view) {
            //Intent intent = ContactPageActivity.newIntent(getActivity(), mContact.get)
            mLastItem = this.getAdapterPosition();
        }
    }


    /**
     * ==========================================================================================
     * the adapter class to the hosted fragment recycler view
     * load the contact in a pagination mode perform inside the main class
     * after the onLoadMoreItems() is called in the the main class, the adapter set the change to
     * its own class to the call of the loading pagination method happened
     * ==========================================================================================
     */
    class AttendeesEventAdapter extends RecyclerView.Adapter<AttendeesEventHolder>{

        private List<Contact> mContactList;

        public AttendeesEventAdapter(List<Contact> contacts) {
            mContactList = contacts;
        }

        @Override
        public AttendeesEventHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            return new AttendeesEventHolder(inflater, parent);
        }

        @Override
        public void onBindViewHolder(AttendeesEventHolder holder, int position) {
            holder.bind(mContactList.get(position));
        }

        @Override
        public int getItemCount() {
            return mContactList.size();
        }

        public void setContacts(List<Contact> contacts){
            mContactList = contacts;
        }

    }
}
