package com.meeting.binary.android.binarymeeting.event;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.meeting.binary.android.binarymeeting.R;
import com.meeting.binary.android.binarymeeting.event.admin.AdminActivity;
import com.meeting.binary.android.binarymeeting.event.admin.PollActivity;
import com.meeting.binary.android.binarymeeting.model.Bulletin;
import com.meeting.binary.android.binarymeeting.model.Event;
import com.meeting.binary.android.binarymeeting.service.generetor.GeneralServiceGenerator;
import com.meeting.binary.android.binarymeeting.service.request_interface.RequestWebServiceInterface;

import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * Created by meldi on 3/20/2018.
 */

public class CreatePollFragment extends Fragment implements View.OnClickListener{

    private EditText mPoolQuestionTitle;
    private EditText mPoolQuestionNumber;
    private Button mPoolSubmit;
    private TextView mInfos;
    private TextView mDisplay;

    private RecyclerView mPoolRecyclerQuestion;
    private PollOptionAdapter mOptionAdapter;

    private boolean mIsNumberOfQuestionEntered = false;

    private static final String TAG = "cbm.pollquestion";
    private static final String PUT_EXTRA_ID = "idtag";
    
    private int mQuestionNumber = 0;
    private String eventId;

    private Bulletin bulletin = new Bulletin();

    int count;

    private List<String> editOptionString = new ArrayList<>();
    private List<EditText> editoptions = new ArrayList<>();

    public static CreatePollFragment newInstance(String id) {
        Bundle args = new Bundle();
        args.putString(PUT_EXTRA_ID, id);
        CreatePollFragment fragment = new CreatePollFragment();
        fragment.setArguments(args);
        return fragment;
    }
    


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        eventId = getArguments().getString(PUT_EXTRA_ID);
        if (eventId != null){
            Log.i(TAG, "onCreate: the event id is not null " + eventId);
        } else {
            Log.i(TAG, "onCreate: id is null");
        }
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_poll, container, false);

        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Poll");

        mPoolQuestionTitle = view.findViewById(R.id.poll_title);
        mPoolQuestionNumber = view.findViewById(R.id.poll_question_number);
        mPoolRecyclerQuestion = view.findViewById(R.id.recycler_pool_question);
        mPoolSubmit = view.findViewById(R.id.pool_submit);
        mDisplay = view.findViewById(R.id.display);
        mInfos = view.findViewById(R.id.number_info);
        mInfos.setTextColor(Color.BLACK);

        mPoolRecyclerQuestion.setLayoutManager(new LinearLayoutManager(getActivity()));
        mPoolRecyclerQuestion.setVisibility(View.GONE);

        mPoolSubmit.setOnClickListener(this);

        mDisplay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                count = Integer.parseInt(mPoolQuestionNumber.getText().toString());
                if (count != 0){
                    mInfos.setVisibility(View.GONE);
                    mPoolRecyclerQuestion.setVisibility(View.VISIBLE);
                    updateUI(count);
                    if (!TextUtils.isEmpty(mPoolQuestionNumber.getText().toString())){
                        mQuestionNumber = Integer.parseInt(mPoolQuestionNumber.getText().toString());
                    }
                }
            }
        });






        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUI(count);
    }


    /**
     * sets up  UI
     */
    private void updateUI(int count){
        if (mOptionAdapter == null){
            mOptionAdapter = new PollOptionAdapter(count);
            mPoolRecyclerQuestion.setAdapter(mOptionAdapter);
        } else{
            mOptionAdapter.setSize(count);
            mOptionAdapter.notifyDataSetChanged();
        }
    }



    @Override
    public void onClick(View view) {
        boolean request = false;
       switch (view.getId()){
           case R.id.pool_submit :
               if (!TextUtils.isEmpty(mPoolQuestionTitle.getText().toString())){
                   Log.d(TAG, "onClick: call request");
                   for (EditText editText : editoptions){
                       Log.d(TAG, "requestDataSubmitPool: " + editText.getText().toString());
                       if (!TextUtils.isEmpty(editText.getText().toString())){
//                           editText.setText("");
                           request = true;
                       } else {
//                           editText.setText("");
                           editText.setError("fill this field please");
                           request = false;
                       }
                   }
                   if (request){
                       requestDataSubmitPool();
                   }
               } else {
                   Log.d(TAG, "onClick: request not call");
                   Toast.makeText(getActivity(), "Enter a question please", Toast.LENGTH_SHORT).show();
               }
               //start intent with that as a data
               break;
       }

    }



    /**request data submit*/
    public void requestDataSubmitPool(){

        for (EditText editText : editoptions){
            Log.d(TAG, "requestDataSubmitPool: " + editText.getText().toString());
            editOptionString.add(editText.getText().toString());
            editText.setText("");
        }

        updateUI(count);
        bulletin.setQuestion(mPoolQuestionTitle.getText().toString());
        bulletin.setOptions(editOptionString);
        bulletin.setEventId(eventId);
        bulletin.setUserVoted(false);
        bulletin.setId(null);
        bulletin.setResults(null);

        RequestWebServiceInterface requestWebServiceInterface =
                GeneralServiceGenerator.CreateService(RequestWebServiceInterface.class, getActivity());
        Call<ResponseBody> call = requestWebServiceInterface.createPoll(bulletin);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.d(TAG, "onResponse: enter in the onresponse ");
                if (response.isSuccessful()){
                    Toast.makeText(getActivity(), "poll saved", Toast.LENGTH_LONG).show();
                    Log.d(TAG, "onResponse: get reponse and it is successful");
                    Intent intent = AdminActivity.newIntent(getActivity(), eventId);
                    startActivity(intent);
                } else {
                    Log.d(TAG, "onResponse: get reponse and but it is not successful");
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d(TAG, "onFailure: no response");
            }
        });

    }





    /**
     * holder to create the view and be bind to the adapter
     */
   class PoolQuestionHolder extends RecyclerView.ViewHolder{


       EditText option;

        public PoolQuestionHolder(View itemView) {
            super(itemView);
        }

        public PoolQuestionHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.list_item_pool_question, parent, false));
            option = itemView.findViewById(R.id.option);
        }

        public String bind(){
            editoptions.add(option);
            return option.getText().toString();
        }


    }



    /**
     * adapter to be set in the recycler
     */
    class PollOptionAdapter extends RecyclerView.Adapter<PoolQuestionHolder>{

        List<String> mOptionList;
        int mSize;

        public PollOptionAdapter(int size) {
            mSize = size;
            mOptionList = new ArrayList<>();
        }

        @Override
        public PoolQuestionHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            return new PoolQuestionHolder(inflater, parent);
        }

        @Override
        public void onBindViewHolder(PoolQuestionHolder holder, int position) {
           String s = holder.bind();

           add(s);
        }

        @Override
        public int getItemCount() {
            return mSize;
        }

        public void add(String s){
            mOptionList.add(s);
        }

        public List<String> getOptionList() {
            return mOptionList;
        }

        public void setOptionList(List<String> optionList) {
            mOptionList = optionList;
        }

        public void setSize(int size) {
            mSize = size;
        }
    }
}
