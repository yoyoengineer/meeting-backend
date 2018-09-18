package com.meeting.binary.android.binarymeeting.contact;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.meeting.binary.android.binarymeeting.R;
import com.meeting.binary.android.binarymeeting.model.Contact;
import com.meeting.binary.android.binarymeeting.other.SingleFragmentActivity;
import com.meeting.binary.android.binarymeeting.realm_labs.ContactLab;
import com.meeting.binary.android.binarymeeting.service.cookie.CookiePreferences;
import com.meeting.binary.android.binarymeeting.service.generetor.BaseUrlGenerator;
import com.meeting.binary.android.binarymeeting.service.generetor.GeneralServiceGenerator;
import com.meeting.binary.android.binarymeeting.service.interceptor.AddCookiesInterceptor;
import com.meeting.binary.android.binarymeeting.service.request_interface.RequestWebServiceInterface;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ContactLikeEventDialogFragment extends DialogFragment {

    private static final String ARG_CONTACT = "arg_contact";
    private static final String TAG = "dialog_contact_like";

    List<Contact> mContacts;
    private int mLastItem = -1;

    private RecyclerView mLikedRecycler;
    private LikeContactEventAdapter mLikeContactEventAdapter;

    public static ContactLikeEventDialogFragment newInstance(Serializable contacts) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_CONTACT, contacts);
        ContactLikeEventDialogFragment fragment = new ContactLikeEventDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContacts = (List<Contact>)getArguments().getSerializable(ARG_CONTACT);
        if (mContacts != null){
            Log.d(TAG, "onCreate: recup contacts succeed");
        } else {
            Log.d(TAG, "onCreate: recup contacts failed");
        }
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.contact_fragment_event_like, null);
        mLikedRecycler = (RecyclerView)view.findViewById(R.id.recycler_like);
        mLikedRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        updateUI();

        return new AlertDialog.Builder(getActivity())
                .setView(view)
                .setPositiveButton(android.R.string.ok,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dismiss();
                            }
                        })
                .create();
    }

    /** sets up ContactFragment’s UI */
    private void updateUI(){
        if (mLikeContactEventAdapter == null){
            mLikeContactEventAdapter = new LikeContactEventAdapter(mContacts);
            mLikedRecycler.setAdapter(mLikeContactEventAdapter);
        } else{
            if(mLastItem < 0){
                mLikeContactEventAdapter.setContacts(mContacts);
                mLikeContactEventAdapter.notifyDataSetChanged();
            } else{
                mLikeContactEventAdapter.notifyItemChanged(mLastItem);
                mLastItem = -1;
            }
        }
    }
    



    /**
     * =================================================
     * the holder to the initial hosted contact fragment
     * =================================================
     */
    private class LikeContactEventHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private Contact mContact;
        private TextView mNameTextView;
        private TextView mEmailTextView;
        private ImageView mContactImg;
        private ImageView mLiked;

        public LikeContactEventHolder(View itemView) {
            super(itemView);
        }


        public LikeContactEventHolder(LayoutInflater inflater, ViewGroup viewGroup){
            super(inflater.inflate(R.layout.list_item_contact_like, viewGroup, false));
            itemView.setOnClickListener(this);

            mLiked = itemView.findViewById(R.id.liked);
            mNameTextView = itemView.findViewById(R.id.contact_name);
            mEmailTextView =  itemView.findViewById(R.id.contact_email);
            mContactImg = itemView.findViewById(R.id.contact_img);
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

                Glide.with(ContactLikeEventDialogFragment.this)
                        .load(glideUrl)
                        .asBitmap()
                        .centerCrop()
                        .placeholder(getResources().getDrawable(R.drawable.photo_no_image))
                        .into(new BitmapImageViewTarget(mContactImg) {
                            @Override
                            protected void setResource(Bitmap resource) {
                                RoundedBitmapDrawable drawable = RoundedBitmapDrawableFactory.create(ContactLikeEventDialogFragment.this.getResources(),
                                        Bitmap.createScaledBitmap(resource, 30, 30, false));
                                drawable.setCircular(true);
                                mContactImg.setImageDrawable(drawable);
                            }
                        });
                Log.i(TAG, "bind: " + mContact.getPhoto());
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
    class LikeContactEventAdapter extends RecyclerView.Adapter<LikeContactEventHolder>{

        private List<Contact> mContactList;

        public LikeContactEventAdapter(List<Contact> contacts) {
            mContactList = contacts;
        }

        @Override
        public LikeContactEventHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            return new LikeContactEventHolder(inflater, parent);
        }

        @Override
        public void onBindViewHolder(LikeContactEventHolder holder, int position) {
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
