package com.meeting.binary.android.binarymeeting.event.admin;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
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
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.meeting.binary.android.binarymeeting.R;
import com.meeting.binary.android.binarymeeting.contact.ContactLikeEventDialogFragment;
import com.meeting.binary.android.binarymeeting.model.Bulletin;
import com.meeting.binary.android.binarymeeting.model.Contact;
import com.meeting.binary.android.binarymeeting.service.generetor.GeneralServiceGenerator;
import com.meeting.binary.android.binarymeeting.service.request_interface.RequestWebServiceInterface;
import com.meeting.binary.android.binarymeeting.start.LoginActivity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class PollActivity extends AppCompatActivity {

    List<Bulletin> polls;
    List<Bulletin> questionPollList = new ArrayList<>();
    List<Bulletin> answerPollList = new ArrayList<>();

    private static final String EXTRA_ID_MESSAGE = "event_id";
    private static final String TAG = "poll_tag";
    private String eventId;

    /**open vote*/
    private RecyclerView mOpenVoteRecycler;
    private QuestionPoolAdapter mQuestionPoolAdapter;

    /**close vote*/
    private RecyclerView mAnswerVoteRecycler;
    private AnswerPoolAdapter mAnswerPoolAdapter;



    public static Intent newIntent(Context context, String eventId){
        Intent intent = new Intent(context, PollActivity.class);
        intent.putExtra(EXTRA_ID_MESSAGE, eventId);
        return intent;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_poll);

        getSupportActionBar().setTitle("Poll Event");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        eventId = getIntent().getStringExtra(EXTRA_ID_MESSAGE);
        Log.i(TAG, "onCreate: event id value " + eventId);
        if (eventId != null){
            Log.i(TAG, "onCreate: id is not null");
        } else {
            Log.i(TAG, "onCreate:  id is null");
        }

        mOpenVoteRecycler = (RecyclerView)findViewById(R.id.open_for_voting);
        mOpenVoteRecycler.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        mAnswerVoteRecycler = (RecyclerView)findViewById(R.id.answer_polls);
        mAnswerVoteRecycler.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

    }


    @Override
    protected void onResume() {
        super.onResume();
        requestLoadPoll();
    }




    /**
     * ==========================================
     * load the poll data from the web service
     * ==========================================
     */
    private void requestLoadPoll(){
        /**loading data process*/
        RequestWebServiceInterface requestWebServiceInterface =
                GeneralServiceGenerator.CreateService(RequestWebServiceInterface.class, this);
        Call<List<Bulletin>> listCall = requestWebServiceInterface.getPoll(eventId);
        listCall.enqueue(new Callback<List<Bulletin>>() {
            @Override
            public void onResponse(Call<List<Bulletin>> call, Response<List<Bulletin>> response) {
                if (response.isSuccessful()){
                    polls = response.body();
                    separateData();
                    updateUiQuestionPool();
                    updateUiAnswerPool();
                } else {

                    Toast.makeText(getApplicationContext(), "failed to load contacts", Toast.LENGTH_LONG).show();
                    Log.d(TAG, "onResponse: bad response");
                }
            }

            @Override
            public void onFailure(Call<List<Bulletin>> call, Throwable t) {
                Log.d(TAG, "onFailure: failed to request");
            }
        });
    }





    private void separateData(){
        answerPollList.clear();
        questionPollList.clear();
        if (polls != null){
            for (Bulletin bulletin : polls){
                if (bulletin.isUserVoted()){
                    answerPollList.add(bulletin);
                } else {
                    questionPollList.add(bulletin);
                }
            }
        }

    }






    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_poll, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_refresh:
                ProgressDialog progressDialog = new ProgressDialog(this,
                        R.style.AppTheme_Dark_Dialog);
                progressDialog.setIndeterminate(true);
                progressDialog.setMessage("Login...");
                progressDialog.show();
                if (polls != null){
                    polls.clear();
                    questionPollList.clear();
                    answerPollList.clear();
                    requestLoadPoll();
                    progressDialog.dismiss();
                }
                return true;
            case R.id.action_close:
                onBackPressed();
                return true;
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }



    /**
     * ============================
     * update the question poll ui
     * ============================
     */
    private void updateUiQuestionPool(){
        if (mQuestionPoolAdapter == null){
            mQuestionPoolAdapter = new QuestionPoolAdapter(questionPollList);
            mOpenVoteRecycler.setAdapter(mQuestionPoolAdapter);
        } else {
            mQuestionPoolAdapter.setPollQuestionList(questionPollList);
            mQuestionPoolAdapter.notifyDataSetChanged();
        }
    }



    /**
     * ============================
     * update the answer poll ui
     * ============================
     */
    private void updateUiAnswerPool(){
        if (mAnswerPoolAdapter == null){
            mAnswerPoolAdapter = new AnswerPoolAdapter(answerPollList);
            mAnswerVoteRecycler.setAdapter(mAnswerPoolAdapter);
        } else{
            mAnswerPoolAdapter.setBulletins(answerPollList);
            mAnswerPoolAdapter.notifyDataSetChanged();
        }
    }




    /**
     * ==============================================
     * start inflating the view for the question poll
     * ==============================================
     */

    private class QuestionPoolHolder extends RecyclerView.ViewHolder{
        Bulletin mBulletin;

        private TextView question;
        private Button answer;

        public QuestionPoolHolder(View itemView) {
            super(itemView);
        }

        public QuestionPoolHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.list_poll_question, parent, false));
            question = itemView.findViewById(R.id.poll_question);
            answer = itemView.findViewById(R.id.see_assertions);
        }

        public void bind(Bulletin bulletin){
            mBulletin = bulletin;
            question.setText(mBulletin.getQuestion());
            answer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.i(TAG, "onClick: " + bulletin.getQuestion().toString());
                    Log.i(TAG, "onClick: " + bulletin.getOptions().get(0));
                    Log.i(TAG, "onClick: bulletin id " + bulletin.getId());

                    Intent intent = AnswerPollActivity.newIntent(getApplicationContext(), bulletin.getId());
                    startActivity(intent);

                }
            });
        }
    }


    private class QuestionPoolAdapter extends RecyclerView.Adapter<QuestionPoolHolder>{

        List<Bulletin> mPollQuestionList;

        public QuestionPoolAdapter(List<Bulletin> pollQuestionList) {
            mPollQuestionList = pollQuestionList;
        }

        @NonNull
        @Override
        public QuestionPoolHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
            return new QuestionPoolHolder(inflater, parent);
        }

        @Override
        public void onBindViewHolder(@NonNull QuestionPoolHolder holder, int position) {
            holder.bind(mPollQuestionList.get(position));
        }

        @Override
        public int getItemCount() {
            return mPollQuestionList.size();
        }

        public void setPollQuestionList(List<Bulletin> pollQuestionList) {
            mPollQuestionList = pollQuestionList;
        }
    }

    /**
     * ==============================================
     * end inflating the view for the question poll
     * ==============================================
     */





    /**
     * ==================================================================
     * start inflating the view for the answer poll
     * ==================================================================
     */
    private class AnswerPoolHolder extends RecyclerView.ViewHolder{
        Bulletin mBulletin;

        private TextView title;
        private Button seeResult;

        public AnswerPoolHolder(View itemView) {
            super(itemView);
        }

        public AnswerPoolHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.list_answer_poll, parent, false));

            title = itemView.findViewById(R.id.poll_question);
            seeResult = itemView.findViewById(R.id.see_result);
        }


        public void bind(Bulletin bulletin){
            mBulletin = bulletin;
            title.setText(mBulletin.getQuestion());
            seeResult.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = PollResultActivity.newIntent(getApplicationContext(), bulletin.getId());
                    startActivity(intent);
                }
            });
        }
    }


    private class AnswerPoolAdapter extends RecyclerView.Adapter<AnswerPoolHolder>{

        List<Bulletin> mBulletins;

        public AnswerPoolAdapter(List<Bulletin> bulletins) {
            mBulletins = bulletins;
        }

        @NonNull
        @Override
        public AnswerPoolHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
            return new AnswerPoolHolder(inflater, parent);
        }

        @Override
        public void onBindViewHolder(@NonNull AnswerPoolHolder holder, int position) {
            holder.bind(mBulletins.get(position));
        }

        @Override
        public int getItemCount() {
            return mBulletins.size();
        }

        public void setBulletins(List<Bulletin> bulletins) {
            mBulletins = bulletins;
        }
    }

    /**
     * ==============================================
     * end inflating the view for the question poll
     * ==============================================
     */

}
