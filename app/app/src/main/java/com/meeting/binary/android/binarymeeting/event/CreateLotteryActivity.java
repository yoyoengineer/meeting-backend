package com.meeting.binary.android.binarymeeting.event;

import android.animation.Animator;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.meeting.binary.android.binarymeeting.R;
import com.meeting.binary.android.binarymeeting.model.GenericResponse;
import com.meeting.binary.android.binarymeeting.model.Lottery;
import com.meeting.binary.android.binarymeeting.model.Prize;
import com.meeting.binary.android.binarymeeting.service.generetor.GeneralServiceGenerator;
import com.meeting.binary.android.binarymeeting.service.request_interface.RequestWebServiceInterface;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.meeting.binary.android.binarymeeting.event.LotteryState.DONE;
import static com.meeting.binary.android.binarymeeting.event.LotteryState.EDITING;

public class CreateLotteryActivity extends AppCompatActivity {

    private static final String TAG = "CreateLotteryActivity";
    private LotteryState lotteryState = DONE;
    private String eventId;

    private LotteryAdapter adapter;
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public static void actionStart(Context context, String eventId) {
        Intent intent = new Intent(context, CreateLotteryActivity.class);
        intent.putExtra("eventId",eventId);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        eventId = getIntent().getStringExtra("eventId");
        setContentView(R.layout.activity_create_lottery);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        final LinearLayout add_prize_input = findViewById(R.id.add_prize_input);
        final EditText prize_name_input = findViewById(R.id.prize_name_input);
        final EditText prize_amount_input = findViewById(R.id.prize_amount_input);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.back);//设置返回键
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
//                finish();
            }
        });//返回监听
        add_prize_input.setVisibility(View.INVISIBLE);

        RecyclerView agendaTitleRecyclerView = (RecyclerView) findViewById(R.id.lotteries);
        LinearLayoutManager layoutManager = new LinearLayoutManager(CreateLotteryActivity.this);
        agendaTitleRecyclerView.setLayoutManager(layoutManager);
//        prizeList.add(new Prize("apple",2));
        adapter = new LotteryAdapter();
        agendaTitleRecyclerView.setAdapter(adapter);
        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (lotteryState){
                    case DONE:
                        add_prize_input.setVisibility(View.VISIBLE);
                        Log.d(TAG, "onClick: " + "vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv" + add_prize_input);
                        fab.setImageDrawable(getResources().getDrawable(R.drawable.ic_done));
                        lotteryState = EDITING;
                        break;
                    case EDITING:
                        if (prize_amount_input.getText().toString().equals("") || prize_amount_input.getText() == null || prize_name_input.getText().toString().equals("") || prize_name_input.getText() == null){
                            Toast.makeText(CreateLotteryActivity.this,"Please enter all information.",Toast.LENGTH_SHORT).show();
                        }else{
                            String prizeName = prize_name_input.getText().toString();
                            try {
                                int prizeAmount = Integer.parseInt(prize_amount_input.getText().toString());
                                prize_name_input.setText("");
                                prize_amount_input.setText("");
//                            prizeList.add(new Prize(prizeName,prizeAmount));
//                            adapter.setPrizeList(prizeList);
                                adapter.add(new Prize(prizeName,prizeAmount));
//                            adapter.notifyItemInserted(prizeList.size());
                                Log.d(TAG, "onClick: " + "blablabal");
                                add_prize_input.setVisibility(View.INVISIBLE);
                                fab.setImageDrawable(getResources().getDrawable(R.drawable.add));
                                lotteryState = DONE;
                            }
                            catch(NumberFormatException e){
                                Toast.makeText(CreateLotteryActivity.this,"Bad number format",Toast.LENGTH_SHORT).show();
                            }

                        }

                        break;
                }

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.lottery_toolbar,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.save_lottery:
                new android.os.Handler().postDelayed(
                        new Runnable() {
                            @Override
                            public void run() {
//                                Log.d(TAG, "run: " + );
                                if(adapter.mPrizeList!=null && !adapter.mPrizeList.isEmpty()) {
                                    Toast.makeText(CreateLotteryActivity.this,"Saving...",Toast.LENGTH_SHORT).show();
                                    createLottery(adapter.mPrizeList);
                                }
                                else
                                    Toast.makeText(CreateLotteryActivity.this,"Your lottery list is empty...",Toast.LENGTH_SHORT).show();

                            }
                        }, 1);
//                for (Prize p:adapter.mPrizeList) {
//                    Log.d(TAG, "onOptionsItemSelected: " + p.getName());
//                }
        }
        return true;
    }

    /**
     * check the credentials provided by the user and
     * grant or not the access to the application
     */
    private void createLottery(List<Prize> prizeList){
        RequestWebServiceInterface requestWebServiceInterface = GeneralServiceGenerator.CreateService(RequestWebServiceInterface.class, getApplicationContext());
        Map<String,Integer> lotteryItems = new HashMap<>();
        Log.d(TAG, "createLottery: size:" + prizeList.size());
        for (Prize p: prizeList) {
            lotteryItems.put(p.getName(),p.getAmount());
        }
        Log.d(TAG, "createLottery: " + lotteryItems);
        Lottery lottery = new Lottery();
        lottery.setItems(lotteryItems);
        lottery.setEventId(eventId);
        Log.d(TAG, "createLottery: eventId:" + eventId);
        Log.d(TAG, "createLottery: lottery:" + lottery);
        Call<Void> call = requestWebServiceInterface.createLottery(lottery);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {

                if(response.isSuccessful()){
                    Toast.makeText(CreateLotteryActivity.this, "Congratulations! you have created the lottery successfully.", Toast.LENGTH_SHORT).show();
                    finish();
                }else {
                    Toast.makeText(CreateLotteryActivity.this, "Sorry! you didn't create lottery successfully. Please try again.", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(CreateLotteryActivity.this, "onFailure: " +
                        t.getMessage() + " response from failure", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "onFailure: " + t.getMessage());
            }
        });
    }

    class LotteryAdapter extends RecyclerView.Adapter<LotteryAdapter.ViewHolder> {

        private List<Prize> mPrizeList;

        class ViewHolder extends RecyclerView.ViewHolder implements View.OnTouchListener{

            TextView prizeName;
            TextView prizeAmount;
            ImageView prizeImage;

            public ViewHolder(View view) {
                super(view);
                prizeName = (TextView) view.findViewById(R.id.prize_name);
                prizeAmount = (TextView) view.findViewById(R.id.prize_amount);
                prizeImage = view.findViewById(R.id.delete_prize_item);
            }

            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return false;
            }


        }

        public LotteryAdapter() {
            mPrizeList = new ArrayList<>();
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.create_lottery_view_holder, parent, false);
            ViewHolder holder = new ViewHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, final int position) {
            final Prize prize = mPrizeList.get(position);
            if (prize != null){
                holder.prizeName.setText(prize.getName());
                holder.prizeAmount.setText(String.valueOf(prize.getAmount()));
            }
            holder.prizeImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.d(TAG, "onClick: " + "pppp:" + position);
                    adapter.remove(position);
                }
            });
        }

        @Override
        public int getItemCount() {
            return mPrizeList.size();
        }

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void onViewAttachedToWindow(ViewHolder viewHolder) {
            super.onViewAttachedToWindow(viewHolder);
            animateCircularReveal(viewHolder.itemView);
        }

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        public void animateCircularReveal(View view) {
            int centerX = 0;
            int centerY = 0;
            int startRadius = 0;
            int endRadius = Math.max(view.getWidth(), view.getHeight());
            Animator animation = ViewAnimationUtils.createCircularReveal(view, centerX, centerY, startRadius, endRadius);
            view.setVisibility(View.VISIBLE);
            animation.start();
        }

        public void remove(int position){
            Log.d(TAG, "remove: " + position);
            mPrizeList.remove(position);
            notifyItemRemoved(position);
        }

        public void add(Prize prize){
            mPrizeList.add(prize);
            Log.d(TAG, "add: " + prize + " position+1:" + mPrizeList.size());
            notifyItemInserted(mPrizeList.size()-1);
        }

    }
}
