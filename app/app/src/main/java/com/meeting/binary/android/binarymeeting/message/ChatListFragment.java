package com.meeting.binary.android.binarymeeting.message;

import android.content.Intent;
import android.content.res.Configuration;
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
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.google.zxing.oned.ITFReader;
import com.meeting.binary.android.binarymeeting.R;
import com.meeting.binary.android.binarymeeting.contact.ContactFragment;
import com.meeting.binary.android.binarymeeting.model.Contact;
import com.meeting.binary.android.binarymeeting.model.Message;
import com.meeting.binary.android.binarymeeting.service.cookie.CookiePreferences;
import com.meeting.binary.android.binarymeeting.service.generetor.BaseUrlGenerator;
import com.meeting.binary.android.binarymeeting.service.generetor.GeneralServiceGenerator;
import com.meeting.binary.android.binarymeeting.service.interceptor.AddCookiesInterceptor;
import com.meeting.binary.android.binarymeeting.service.request_interface.RequestWebServiceInterface;
import com.meeting.binary.android.binarymeeting.utils.DateUtils;


import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatListFragment extends Fragment {

    private static final String TAG = "MyContactChatHistory";
    private int mLastItem = -1;

    private RecyclerView mChatHistoryRecyclerView;
    private MessageChatHistoryAdapter mChatHistoryAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list_chat, container, false);

        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Chat");
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);


//        int orientation = getResources().getConfiguration().orientation;
//
//        if (Configuration.ORIENTATION_LANDSCAPE == orientation) {
//            view.setBackgroundResource (R.drawable.chat_list_back);
//        } else {
//            view.setBackgroundResource (R.drawable.chat_list_back);
//        }
        mChatHistoryRecyclerView = view.findViewById(R.id.chat_list_recycler);
        mChatHistoryRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        return view;
    }


    @Override
    public void onResume() {
        super.onResume();
        requestLoadChatHistory();
    }

    /**
     * ==========================================
     * load the contact data from the web service
     * ==========================================
     */
    private void requestLoadChatHistory(){
        /**loading data process*/
        RequestWebServiceInterface requestWebServiceInterface =
                GeneralServiceGenerator.CreateService(RequestWebServiceInterface.class, getActivity());
        Call<List<Message>> listCall = requestWebServiceInterface.getChatHistory();
        listCall.enqueue(new Callback<List<Message>>() {
            @Override
            public void onResponse(Call<List<Message>> call, Response<List<Message>> response) {
                if (response.isSuccessful()){
                    List<Message> messages = response.body();
                    if (messages!=null)
                        updateUI(messages);
                    else Log.d(TAG, "onResponse: message list is null");
                } else {
                    Toast toast = Toast.makeText(getActivity(), "failed to load messages", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    Log.d(TAG, "onResponse: bad response");
                }
            }

            @Override
            public void onFailure(Call<List<Message>> call, Throwable t) {
                Log.d(TAG, "onFailure: failed to request");
            }
        });
    }


    /**
     * ============================
     * sets up Comment AddTodoFragment’s UI
     * ============================
     */
    private void updateUI(List<Message> messages){
        if (mChatHistoryAdapter == null){
            mChatHistoryAdapter = new MessageChatHistoryAdapter(messages);
            mChatHistoryRecyclerView.setAdapter(mChatHistoryAdapter);
        } else{
            if(mLastItem < 0){
                mChatHistoryAdapter.setMessages(messages);
                mChatHistoryAdapter.notifyDataSetChanged();
            } else{
                mChatHistoryAdapter.notifyItemChanged(mLastItem);
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
        switch (item.getItemId()) {
            case android.R.id.home:
                getActivity().onBackPressed();
                return true;
            default: return super.onOptionsItemSelected(item);
        }
    }





    /**
     * ====================================================
     * holder to create the view and be bind to the adapter
     * ====================================================
     */
    class MessageChatHistoryHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private Message mMessage;
        private String messageId;

        private ImageView mContactImg;
        private TextView mNameTextView;
        private TextView mLastMessageTextView;
        private TextView mMessageLastMessageArrivedTime;

        public MessageChatHistoryHolder(View itemView) {
            super(itemView);
        }

        public MessageChatHistoryHolder(LayoutInflater inflater, ViewGroup parent){
            super(inflater.inflate(R.layout.list_item_chat_layout, parent, false));
            itemView.setOnClickListener(this);
            mContactImg =  itemView.findViewById(R.id.contact_img);
            mNameTextView = itemView.findViewById(R.id.contact_name);
            mLastMessageTextView =  itemView.findViewById(R.id.last_message);
            mMessageLastMessageArrivedTime = itemView.findViewById(R.id.last_message_time);
        }

        @Override
        public void onClick(View view) {
            mLastItem = this.getAdapterPosition();
            Intent intent = MessageActivity.newIntent(getActivity(), mMessage);
            startActivity(intent);
        }

        public void bind(Message message){
            String fileName = null;
            mLastItem = getAdapterPosition();
            mMessage = message;
            messageId = mMessage.getId();
            mNameTextView.setText(mMessage.getName());
            SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
            mMessageLastMessageArrivedTime.setText(dateFormat.format(new Date(mMessage.getTime())));
            if (mMessage.getType().equalsIgnoreCase("TEXT"))
                mLastMessageTextView.setText(mMessage.getText());
            else {
                Map<String, String> mapMesssage = mMessage.getExtra();
                if (mapMesssage!=null)
                    fileName = mapMesssage.get("fileName");
                if (fileName!=null)
                    mLastMessageTextView.setText(fileName);
                else {
                    mLastMessageTextView.setText("");
                    Log.i(TAG, "file name is null");
                }

            }

//            OkHttpClient.Builder mBuilder = new OkHttpClient.Builder();
//            mBuilder.addInterceptor(new AddCookiesInterceptor(getActivity()));
//            OkHttpClient client = mBuilder.build();
//            List<Interceptor> in = client.interceptors();
//
//            for(Interceptor i: in){
//                if(i instanceof AddCookiesInterceptor){
//                    Log.i(TAG, "Interceptor found");
//                }
//            }


            String userName = mMessage.getFromUser();
            if (userName.equals(CookiePreferences.getStoredName(getActivity()))){
                userName = mMessage.getToUser();
            }


            String recupCookie = CookiePreferences.getStoredCookie(getActivity());
            Log.i(TAG, "intercept: " + recupCookie);
            LazyHeaders.Builder builder = new LazyHeaders.Builder()
                    .addHeader("Cookie", recupCookie);
            GlideUrl glideUrl = new GlideUrl(BaseUrlGenerator.BINARY_BASE_URL + "/profile/mypicture/" + userName, builder.build());

            Glide.with(ChatListFragment.this)
                    .load(glideUrl)
                    .asBitmap()
                    .centerCrop()
                    .placeholder(getResources().getDrawable(R.drawable.photo_no_image))
                    .into(new BitmapImageViewTarget(mContactImg) {
                        @Override
                        protected void setResource(Bitmap resource) {
                            RoundedBitmapDrawable drawable = RoundedBitmapDrawableFactory.create(ChatListFragment.this.getResources(),
                                    Bitmap.createScaledBitmap(resource, 45, 45, false));
                            drawable.setCircular(true);
                            mContactImg.setImageDrawable(drawable);
                        }
                    });
            Log.i(TAG, "bind: " + userName);
        }
    }


    /**
     * ==================================
     * adapter to be set in the recycler
     * ==================================
     */
    class MessageChatHistoryAdapter extends RecyclerView.Adapter<MessageChatHistoryHolder>{

        List<Message> mMessages;

        public MessageChatHistoryAdapter(List<Message> messages) {
            mMessages = messages;
        }

        @Override
        public MessageChatHistoryHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            return new MessageChatHistoryHolder(inflater, parent);
        }

        @Override
        public void onBindViewHolder(MessageChatHistoryHolder holder, int position) {
            holder.bind(mMessages.get(position));
        }

        @Override
        public int getItemCount() {
            return mMessages.size();
        }

        public void setMessages(List<Message> messages) {
            mMessages = messages;
        }
    }
}
