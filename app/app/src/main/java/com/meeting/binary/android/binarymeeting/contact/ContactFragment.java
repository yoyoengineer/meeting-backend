package com.meeting.binary.android.binarymeeting.contact;


import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
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
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.model.GlideUrl;

import com.bumptech.glide.load.model.LazyHeaders;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.bumptech.glide.request.target.SimpleTarget;
import com.jakewharton.picasso.OkHttp3Downloader;
import com.meeting.binary.android.binarymeeting.R;
import com.meeting.binary.android.binarymeeting.listener.LoadMoreItems;
import com.meeting.binary.android.binarymeeting.model.Contact;
import com.meeting.binary.android.binarymeeting.model.MyContactPage;
import com.meeting.binary.android.binarymeeting.model.TodoItem;
import com.meeting.binary.android.binarymeeting.realm_labs.ContactLab;
import com.meeting.binary.android.binarymeeting.service.cookie.CookiePreferences;
import com.meeting.binary.android.binarymeeting.service.cookie.PicassoLoadCookieGenerator;
import com.meeting.binary.android.binarymeeting.service.generetor.BaseUrlGenerator;
import com.meeting.binary.android.binarymeeting.service.generetor.GeneralServiceGenerator;
import com.meeting.binary.android.binarymeeting.service.interceptor.AddCookiesInterceptor;
import com.meeting.binary.android.binarymeeting.service.request_interface.RequestWebServiceInterface;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;



public class ContactFragment extends Fragment {
    private Realm myRealm;

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView mContactRecyclerView;
    private ContactAdapter mContactAdapter;

    List<Contact> mContacts;
    private int mLastItem = -1;

    private static final String CONTACT_PARSE_EXTRA_MESSAGE = "com.binary.contact_parcelable";
    private static final String TAG = "barier";
    private static final String SAVE_STATE_CONTACT = "save_contact_state";


    public static ContactFragment newInstance(Serializable contacts){
        ContactFragment fragment = new ContactFragment();
        Bundle args = new Bundle();
        args.putSerializable(CONTACT_PARSE_EXTRA_MESSAGE, contacts);
        fragment.setArguments(args);
        return fragment;
    }



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myRealm = Realm.getDefaultInstance();
        setHasOptionsMenu(true);
        if (getArguments() != null){
            mContacts = (List<Contact>) getArguments().getSerializable(CONTACT_PARSE_EXTRA_MESSAGE);
        } else {
            Log.i(TAG, "onCreate: no data in the database and from the server");
            Log.i(TAG, "onCreate: argument null");
        }
        //Toast.makeText(getActivity(), "activity " + getActivity().getSupportFragmentManager().getBackStackEntryCount(), Toast.LENGTH_SHORT).show();
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.contact_fragment, container, false);

        if (savedInstanceState != null){
            mContacts = savedInstanceState.getParcelableArrayList(SAVE_STATE_CONTACT);
        }

        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Campus Event");
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        mSwipeRefreshLayout = (SwipeRefreshLayout)view.findViewById(R.id.swipe_refresh);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        mContactRecyclerView = (RecyclerView)view.findViewById(R.id.recycler);
        mContactRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.d(TAG, "onRefresh: " + mContacts.size());
                requestDataLoader();
//                updateUI();
            }
        });

        return view;
    }
    


    @Override
    public void onResume() {
        super.onResume();
        updateUI();
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(SAVE_STATE_CONTACT, (ArrayList<? extends Parcelable>) mContacts);
    }

    /** sets up ContactFragment’s UI */
    private void updateUI(){
//        mContacts = ContactLab.get(getActivity()).getItems(myRealm);
        Log.d(TAG, "updateUI: " + mContacts.size());
        if (mContactAdapter == null){
            mContactAdapter = new ContactAdapter(mContacts, mContactRecyclerView);
            mContactRecyclerView.setAdapter(mContactAdapter);
        } else{
            if(mLastItem < 0){
                mContactAdapter.setContacts(mContacts);
                mContactAdapter.notifyDataSetChanged();
                mContactAdapter.setMoreItems(new LoadMoreItems() {
                    @Override
                    public void onLoadMoreItems() {
                        mContacts.add(null);
                        mContactAdapter.notifyItemInserted(mContacts.size() - 1);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                mContacts.remove(mContacts.size() - 1);
                                mContactAdapter.notifyItemRemoved(mContacts.size());
                                requestDataLoader();
                            }
                        }, 3000);
                    }
                });
            } else{
                mContactAdapter.notifyItemChanged(mLastItem);
                mLastItem = -1;
            }

        }
        //update the count of crime when the activity get the focus back
        updateSubtitle(mContacts);
    }


    /**
     * get the number of contacts in the menu
     * @param menu
     * @param inflater
     */
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        //inflater.inflate(R.menu.contact_fragment, menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:{
                getActivity().onBackPressed();
                return true;
            }

            default: return super.onOptionsItemSelected(item);
        }
    }

    /**
     * update the subtitle of the toolbar
     */
    private void updateSubtitle(List<Contact> contacts){
        int count = contacts.size();
        String subTitle = getResources()
                .getQuantityString(R.plurals.subtitle_plural, count, count);
        // if the subtitle on the action bar is not visible
        //set its value to null
        AppCompatActivity activity = (AppCompatActivity)getActivity();
        activity.getSupportActionBar().setSubtitle(subTitle);
    }






    /**
     * ==================================
     * load the data from the web service
     * ==================================
     */
    public void requestData(int position){
        RequestWebServiceInterface requestWebServiceInterface =
                GeneralServiceGenerator.CreateService(RequestWebServiceInterface.class, getActivity());
        Call<MyContactPage> call = requestWebServiceInterface.getContactProfile(mContacts.get(position).getUsername());
        call.enqueue(new Callback<MyContactPage>() {
            @Override
            public void onResponse(Call<MyContactPage> call, Response<MyContactPage> response) {
                MyContactPage mMyContactPage = response.body();
                if (response.isSuccessful()){
                    if (mMyContactPage != null){
                        Toast.makeText(getActivity(), "received " + mMyContactPage.getName() + "contacts " +
                                        mMyContactPage.getName(),
                                Toast.LENGTH_LONG).show();
                        Intent intent = ContactPageActivity.newIntent(getActivity(), mMyContactPage);
                        startActivity(intent);
                    }
                } else {
                    Log.i(TAG, "onResponse: reponse failed");
                }


            }

            @Override
            public void onFailure(Call<MyContactPage> call, Throwable t) {
                Log.d(TAG, "onFailure: connection to server failed");
            }
        });
    }


    /**
     * ==================================
     * Request Data for updating the list
     * ==================================
     */
    public void requestDataLoader(){
        RequestWebServiceInterface requestWebServiceInterface =
                GeneralServiceGenerator.CreateService(RequestWebServiceInterface.class, getActivity());
//        Call<List<Contact>> listCall = requestWebServiceInterface.getContactPagination(mContacts.get(mContacts.size() - 1).getUsername());
        Call<List<Contact>> listCall = requestWebServiceInterface.getContacts();
        listCall.enqueue(new Callback<List<Contact>>() {
            @Override
            public void onResponse(Call<List<Contact>> call, Response<List<Contact>> response) {
                List<Contact> contacts = response.body();
                if (contacts != null){

                    mContacts = new ArrayList<>();
                    mContacts.addAll(contacts);
//                    ContactLab.get(getActivity()).updateOrInsertContact(contacts, myRealm);
                    mContactAdapter.setContacts(mContacts);
                    mContactAdapter.notifyDataSetChanged();
                    mContactAdapter.setLoaded();
                    mSwipeRefreshLayout.setRefreshing(false);
                } else {
                    Log.i(TAG, "onResponse: here the contact from the pagination is null");
//                    Toast.makeText(getActivity(), "received " + contacts.size() + "contacts " + contacts.get(0).getName(),Toast.LENGTH_LONG).show();
                    mSwipeRefreshLayout.setRefreshing(false);
                }
            }

            @Override
            public void onFailure(Call<List<Contact>> call, Throwable t) {
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });
    }


    /**
     * ===============================
     * General abstract holder class
     * ===============================
     */
    abstract class AbstractContactHolder extends RecyclerView.ViewHolder{

        public AbstractContactHolder(View itemView) {
            super(itemView);
        }

    }




    /**
     * =================================================
     * the holder to the initial hosted contact fragment
     * =================================================
     */
    class ContactHolder extends AbstractContactHolder implements View.OnClickListener{

        private Contact mContact;
        private TextView mNameTextView;
        private TextView mEmailTextView;
        private ImageView mContactImg;

        public ContactHolder(View itemView) {
            super(itemView);
        }


        public ContactHolder(LayoutInflater inflater, ViewGroup viewGroup){
            super(inflater.inflate(R.layout.list_item_contact, viewGroup, false));
            itemView.setOnClickListener(this);

            mNameTextView = itemView.findViewById(R.id.contact_name);
            mEmailTextView =  itemView.findViewById(R.id.contact_email);
            mContactImg = itemView.findViewById(R.id.contact_img);
        }

        public void bind(Contact contact){
            mContact = contact;
            mNameTextView.setText(mContact.getName());
            mEmailTextView.setText(mContact.getTown());

            OkHttpClient.Builder mBuilder = new OkHttpClient.Builder();
            mBuilder.addInterceptor(new AddCookiesInterceptor(getActivity()));
            OkHttpClient client = mBuilder.build();

            List<Interceptor> in = client.interceptors();

            for(Interceptor i: in){
                if(i instanceof AddCookiesInterceptor){
                    Log.i(TAG, "Interceptor found");
                }
            }


            if (mContact.getPhoto() != null){
                String recupCookie = CookiePreferences.getStoredCookie(getActivity());
                Log.i(TAG, "intercept: " + recupCookie);
                LazyHeaders.Builder builder = new LazyHeaders.Builder()
                        .addHeader("Cookie", recupCookie);
                GlideUrl glideUrl = new GlideUrl(BaseUrlGenerator.BINARY_BASE_URL + "/profile/picture/" + mContact.getPhoto(), builder.build());
//                Glide.with(ContactFragment.this).load(glideUrl)
//                        .placeholder(R.drawable.ic_person_black_24dp)
//                        .into(mContactImg);

                Glide.with(ContactFragment.this)
                        .load(glideUrl)
                        .asBitmap()
                        .centerCrop()
                        .placeholder(getResources().getDrawable(R.drawable.photo_no_image))
                        .into(new BitmapImageViewTarget(mContactImg) {
                            @Override
                            protected void setResource(Bitmap resource) {
                                RoundedBitmapDrawable drawable = RoundedBitmapDrawableFactory.create(ContactFragment.this.getResources(),
                                        Bitmap.createScaledBitmap(resource, 65, 65, false));
                                drawable.setCircular(true);
                                mContactImg.setImageDrawable(drawable);
                            }
                        });
                Log.i(TAG, "bind: " + mContact.getPhoto());
            }

        }

        @Override
        public void onClick(View view) {
            requestData(this.getAdapterPosition());
            mLastItem = this.getAdapterPosition();
        }
    }



    /**
     * ==================================================
     * holder load the progress bar in the recycler view
     * ==================================================
     */
    class ContactHolderProgress extends AbstractContactHolder{

        private ProgressBar mProgressBar;

        public ContactHolderProgress(View itemView) {
            super(itemView);
        }

        public ContactHolderProgress(LayoutInflater inflater, ViewGroup parent){
            super(inflater.inflate(R.layout.item_progress_bar_item, parent, false));
            mProgressBar = itemView.findViewById(R.id.progress_bar);
        }

        public void bind(){
            mProgressBar.setIndeterminate(true);
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
    class ContactAdapter extends RecyclerView.Adapter<AbstractContactHolder>{

        private final int VIEW_TYPE_ITEM = 0 , VIEW_TYPE_LOADING = 1;
        private boolean isLoading;
        private int visibleHold = 5;
        private int lastVisibleItem, totalCount;

        private LoadMoreItems mMoreItems;

        private List<Contact> mContactList;

        public ContactAdapter(List<Contact> contacts, RecyclerView recyclerView){
            mContactList = contacts;
            final LinearLayoutManager linearLayoutManager = (LinearLayoutManager)recyclerView.getLayoutManager();
            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
                    totalCount = linearLayoutManager.getItemCount();
                    lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
                    if (!isLoading && totalCount <= (lastVisibleItem + visibleHold)){
                        if (mMoreItems != null){
                            mMoreItems.onLoadMoreItems();
                        }
                        isLoading = true;
                    }
                }

                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                }
            });
        }

        @Override
        public AbstractContactHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (viewType == VIEW_TYPE_ITEM){
                LayoutInflater inflater = LayoutInflater.from(getActivity());
                return new ContactHolder(inflater, parent);
            } else if (viewType == VIEW_TYPE_LOADING){
                LayoutInflater inflater = LayoutInflater.from(getActivity());
                return new ContactHolderProgress(inflater, parent);
            }
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            return new ContactHolder(inflater, parent);
        }

        @Override
        public void onBindViewHolder(AbstractContactHolder holder, int position) {
            if (holder instanceof  ContactHolder){
                ((ContactHolder)holder).bind(mContactList.get(position));
            } else if (holder instanceof ContactHolderProgress){
                ((ContactHolderProgress)holder).bind();
            }
        }

        @Override
        public int getItemCount() {
            return mContactList.size();
        }

        public void setContacts(List<Contact> contacts){
            mContactList = contacts;
        }

        @Override
        public int getItemViewType(int position) {
            return mContactList.get(position) == null ?  VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
        }

        public void setMoreItems(LoadMoreItems moreItems) {
            mMoreItems = moreItems;
        }

        public void setLoaded() {
            isLoading = false;
        }
    }
}
