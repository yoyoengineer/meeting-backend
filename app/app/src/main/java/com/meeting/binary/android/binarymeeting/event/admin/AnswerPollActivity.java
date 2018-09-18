package com.meeting.binary.android.binarymeeting.event.admin;

import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.meeting.binary.android.binarymeeting.R;
import com.meeting.binary.android.binarymeeting.model.Bulletin;
import com.meeting.binary.android.binarymeeting.model.Choice;
import com.meeting.binary.android.binarymeeting.service.generetor.GeneralServiceGenerator;
import com.meeting.binary.android.binarymeeting.service.request_interface.RequestWebServiceInterface;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.realm.annotations.PrimaryKey;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AnswerPollActivity extends AppCompatActivity {

    private TextView questionPoll;
    private Button submitPoll;

    private RecyclerView mPollRecyclerView;
    private AnswerPoolAdapter mAnswerPoolAdapter;

    private String question;
    private List<String> mOptions = new ArrayList<>();
    private String bulletinId;
    private boolean hasVoted = false;
    private Choice mChoice;

    private int selectedPosition = -1;

    private static final String TAG = "poll_bulletin";
    private static final String MESSAGE = "the_bulletin";
    private static final String MESSAGE_STR = "the_bulletin";


    public static Intent newIntent(Context context, String bulletinId){
        Intent intent = new Intent(context, AnswerPollActivity.class);
        intent.putExtra(MESSAGE, bulletinId);
        return intent;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_answer_poll);

        getSupportActionBar().setTitle("answer the poll question");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mChoice = new Choice();

        bulletinId = getIntent().getStringExtra(MESSAGE);
        if (bulletinId != null){
            Log.i(TAG, "onCreate: optionMapper is not null");
            mChoice.setBulletinId(bulletinId);
        } else {
            Log.i(TAG, "onCreate: bulletin is null");
        }

        questionPoll = (TextView)findViewById(R.id.question_poll);
        submitPoll = (Button) findViewById(R.id.submit_poll);


        mPollRecyclerView = (RecyclerView)findViewById(R.id.recycler_answer);
        mPollRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        requestOptions();

        submitPoll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selectedPosition < 0 || !hasVoted){
                    Toast toast = Toast.makeText(AnswerPollActivity.this, "click on a text to select your answer", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                } else {
                    submitPoll();
                    onBackPressed();
                }

            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }



    public void submitPoll(){
        RequestWebServiceInterface requestWebServiceInterface =
                GeneralServiceGenerator.CreateService(RequestWebServiceInterface.class, AnswerPollActivity.this);
        Call<ResponseBody> listCall = requestWebServiceInterface.sendVotes(mChoice);
        listCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()){
                    Log.i(TAG, "onResponse: get the response ");
                } else {
                    Toast toast = Toast.makeText(AnswerPollActivity.this, "failed to load contacts", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    Log.d(TAG, "onResponse: bad response");
                }
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d(TAG, "onFailure: failed to request");
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
        requestOptions();
    }

    private void updateUi(List<String> options){
        if (mAnswerPoolAdapter == null){
            mAnswerPoolAdapter = new AnswerPoolAdapter(options);
            mPollRecyclerView.setAdapter(mAnswerPoolAdapter);
        } else {
            mAnswerPoolAdapter.setOptions(options);
            mAnswerPoolAdapter.notifyDataSetChanged();
        }
    }



    private void requestOptions(){
        /**loading data process*/
        RequestWebServiceInterface requestWebServiceInterface =
                GeneralServiceGenerator.CreateService(RequestWebServiceInterface.class, AnswerPollActivity.this);
        Call<Bulletin> listCall = requestWebServiceInterface.getAssertions(bulletinId);
        listCall.enqueue(new Callback<Bulletin>() {
            @Override
            public void onResponse(Call<Bulletin> call, Response<Bulletin> response) {
                if (response.isSuccessful()){
                    Log.i(TAG, "onResponse: uploqd like succed");
                    Bulletin bulletin = response.body();
                    if (bulletin != null){
                        Log.i(TAG, "onResponse: bulletin is not null");
                        mOptions = bulletin.getOptions();
                        Log.i(TAG, "onResponse: options" + mOptions.size());
                        for (int i = 0; i< mOptions.size(); i++){
                            Log.i(TAG, "onResponse: " + mOptions.get(i));
                        }
                        question = bulletin.getQuestion();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (question != null){
                                    questionPoll.setText(question);
                                }

                            }
                        });
                        updateUi(mOptions);
                    }
                } else {
                    Log.i(TAG, "onResponse: null");
                    Toast toast = Toast.makeText(AnswerPollActivity.this, "failed to load data", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    Log.d(TAG, "onResponse: bad response");
                }
            }

            @Override
            public void onFailure(Call<Bulletin> call, Throwable t) {
                Log.d(TAG, "onFailure: failed to request");
            }
        });
    }






    /**
     * ==============================================
     * start inflating the view for the question poll
     * ==============================================
     */

    private class AnswerPoolHolder extends RecyclerView.ViewHolder{
        String mOption;
        private TextView item;
        private RadioButton mRadioButton;
        int count = 1;

        public AnswerPoolHolder(View itemView) {
            super(itemView);
        }

        public AnswerPoolHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.list_poll_answer, parent, false));
            item = itemView.findViewById(R.id.check_assertion_poll);
            mRadioButton = itemView.findViewById(R.id.select_item);
        }

        public void bind(String option){
            mOption = option;
            selectedPosition = getAdapterPosition();
            item.setText(mOption);


            if (hasVoted) {
                mRadioButton.setClickable(false);
                mRadioButton.setEnabled(false);
                mRadioButton.setChecked(false);

            }

            mRadioButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if (b){
                        hasVoted = true;
                        mRadioButton.setEnabled(false);
                        mChoice.setChoice(mOption);
                    } else {
                        Toast toast = Toast.makeText(AnswerPollActivity.this, "can not select anymore", Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                        item.setTextColor(getResources().getColor(R.color.blue));
                        hasVoted = false;
                    }
                }
            });

        }
    }


    private class AnswerPoolAdapter extends RecyclerView.Adapter<AnswerPoolHolder>{

        List<String> optionsList;

        public AnswerPoolAdapter(List<String> options) {
            optionsList = options;
        }

        @NonNull
        @Override
        public AnswerPoolHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
            return new AnswerPoolHolder(inflater, parent);
        }

        @Override
        public void onBindViewHolder(@NonNull AnswerPoolHolder holder, int position) {
            holder.bind(optionsList.get(position));
        }

        @Override
        public int getItemCount() {
            return optionsList.size();
        }

        public void setOptions(List<String> options) {
            optionsList = options;
        }
    }
}
