package com.meeting.binary.android.binarymeeting.event.admin;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.meeting.binary.android.binarymeeting.R;
import com.meeting.binary.android.binarymeeting.model.Bulletin;
import com.meeting.binary.android.binarymeeting.service.generetor.GeneralServiceGenerator;
import com.meeting.binary.android.binarymeeting.service.request_interface.RequestWebServiceInterface;


import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PollResultActivity extends AppCompatActivity {

    private Bulletin mBulletin;
    private String bulletinId;
    private Map<String, Integer> result = new HashMap<>();
    private String question;
    private int numberAnswer;

    private static final String TAG = "poll_bulletin";
    private static final String MESSAGE = "the_bulletin";

    private TextView questionPoll;
    private TextView answerPoll;

    private RecyclerView recycler_bar;
    private PollResultAdapter mPollResultAdapter;


    public static Intent newIntent(Context context, String bulletinId){
        Intent intent = new Intent(context, PollResultActivity.class);
        intent.putExtra(MESSAGE, bulletinId);
        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_poll_result);

        getSupportActionBar().setTitle("Poll Result");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        bulletinId = getIntent().getStringExtra(MESSAGE);
        if (bulletinId != null){
            Log.i(TAG, "onCreate: optionMapper is not null");

        } else {
            Log.i(TAG, "onCreate: bulletin is null");
        }

        questionPoll = findViewById(R.id.poll_question);
        answerPoll = findViewById(R.id.poll_answer);


        requestOptions();

        recycler_bar = findViewById(R.id.recycler_progress_bar);
        recycler_bar.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

    }







    @Override
    protected void onResume() {
        super.onResume();
        requestOptions();
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





    private void updateUi(List<String> resultList, Map<String, Integer> results, int totalAns){
        if (mPollResultAdapter == null){
            mPollResultAdapter = new PollResultAdapter(resultList, results, totalAns);
            recycler_bar.setAdapter(mPollResultAdapter);
        } else {
            mPollResultAdapter.setResultData(resultList);
            mPollResultAdapter.notifyDataSetChanged();
        }
    }




    private void requestOptions(){
        /**loading data process*/
        RequestWebServiceInterface requestWebServiceInterface =
                GeneralServiceGenerator.CreateService(RequestWebServiceInterface.class, PollResultActivity.this);
        Call<Bulletin> listCall = requestWebServiceInterface.getAnswerStat(bulletinId);
        listCall.enqueue(new Callback<Bulletin>() {
            @Override
            public void onResponse(Call<Bulletin> call, Response<Bulletin> response) {
                if (response.isSuccessful()){
                    Log.i(TAG, "onResponse: uploqd like succed");
                    mBulletin = response.body();
                    List<String> allTheAnswer = new LinkedList<>();
                    if (mBulletin != null){
                        Log.i(TAG, "onResponse: bulletin is not null");
                        result = mBulletin.getResults();
                        question = mBulletin.getQuestion();


                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (question != null){
                                    questionPoll.setText(question);
                                    answerPoll.setText(mBulletin.getMyChoice());
                                }
                            }
                        });

                        Set<String> key = new HashSet<>(result.keySet());
                        allTheAnswer.addAll(result.keySet());

                        for (String s: key){
                            numberAnswer += result.get(s);
                        }
                        updateUi(allTheAnswer, result, numberAnswer);
                    }
                } else {
                    Log.i(TAG, "onResponse: null");
                    Toast toast = Toast.makeText(PollResultActivity.this, "failed to load data", Toast.LENGTH_LONG);
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
     * start inflating the view for the poll result
     * ==============================================
     */
    private class PollResultHolder extends RecyclerView.ViewHolder{
        private TextView mTextViewQuestion;
        private ProgressBar pgrPercentage;

        private String questionR;
        private int number;
        int totalNum;

        public PollResultHolder(View itemView) {
            super(itemView);
        }


        public PollResultHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.item_poll_result_bar, parent, false));
            mTextViewQuestion = itemView.findViewById(R.id.total_answer);
            pgrPercentage = itemView.findViewById(R.id.progress_bar_result);

        }


        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        public void bind(String question, int number, int totalNum) {
            int progress = (number * 100) / totalNum;
            this.questionR = question;
            this.number = number;
            this.totalNum = totalNum;

            mTextViewQuestion.setText(questionR + " : " + this.number + " >>");


            pgrPercentage.setProgress(progress);

            // pgrPercentage.setProgressTintList(ColorStateList.valueOf(Color.RED));


            if (progress > 50)
                pgrPercentage.setProgressTintList(ColorStateList.valueOf(getResources().getColor(R.color.light_green)));
            else
                pgrPercentage.setProgressTintList(ColorStateList.valueOf(Color.RED));

        }
    }




    private class PollResultAdapter extends RecyclerView.Adapter<PollResultHolder>{
        private List<String> resultData;
        private int numberTotalAnswers;
        private Map<String, Integer> results;

        public PollResultAdapter(List<String> resultData, Map<String, Integer> results, int numberTotalAnswers) {
            this.resultData = resultData;
            this.numberTotalAnswers = numberTotalAnswers;
            this.results = results;
        }

        @Override
        public PollResultHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
            return new PollResultHolder(inflater, parent);
        }

        @Override
        public void onBindViewHolder(PollResultHolder holder, int position) {

            holder.bind(resultData.get(position), this.results.get(resultData.get(position)), numberTotalAnswers);
        }

        @Override
        public int getItemCount() {
            return resultData.size();
        }


        public void setResultData(List<String> resultData) {
            this.resultData = resultData;
        }
    }
}
