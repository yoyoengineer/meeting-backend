package com.meeting.binary.android.binarymeeting.start;

import android.content.Intent;
import android.os.Handler;
import android.os.PersistableBundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.meeting.binary.android.binarymeeting.R;
import com.meeting.binary.android.binarymeeting.service.cookie.CookiePreferences;
import com.meeting.binary.android.binarymeeting.service.generetor.LoginServiceGenerator;
import com.meeting.binary.android.binarymeeting.service.request_interface.RequestWebServiceInterface;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OnboardingActivity extends AppCompatActivity {

    private static final String TAG = "bording_tag";
    private static final String INSTANCE_PAGE = "currentpage";


    private ViewPager mSliderViewPager;
    private LinearLayout mDotLayout;

    private TextView[] mDots;
    private ImageButton mPrevious;
    private ImageButton mNext;

    private RelativeLayout mainBoarding;

    private BoardingSlideAdapter mSlideAdapter;

    private int mCurrentPage = 0;
    private boolean isRequestSucceed;

    private static int SPLASH_TIME_OUT = 3000;

    public int[] backgroundImage = {
            R.drawable.girl_event,
            R.drawable.meeting_girl,
            R.drawable.chatpeople,
            R.drawable.activitiesevent
    };


    @Override
    protected void onStart() {
        super.onStart();
        if (mCurrentPage == 3) {
            postDelayLogin();
        } else {
            Log.i(TAG, "onStart: current pa");
        }
    }





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            Log.i(TAG, "onCreate: " + savedInstanceState.getInt(INSTANCE_PAGE));
            if (savedInstanceState.getInt(INSTANCE_PAGE) == 3){
                postDelayLogin();
            }
        }

        setContentView(R.layout.activity_mainboarding);

        mainBoarding = (RelativeLayout)findViewById(R.id.main_board);

        mSliderViewPager = (ViewPager)findViewById(R.id.sliderViewPager);
        mDotLayout = (LinearLayout)findViewById(R.id.dotsLayout);
        mSlideAdapter = new BoardingSlideAdapter(this);
        mSliderViewPager.setAdapter(mSlideAdapter);
        mPrevious = (ImageButton) findViewById(R.id.previous);
        mNext = (ImageButton) findViewById(R.id.next);

        dotsIndicator(0);
        mSliderViewPager.addOnPageChangeListener(viewLister);

        //onClickListener
        mNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mCurrentPage == 3){
                } else {
                    mSliderViewPager.setCurrentItem(mCurrentPage + 1);
                }
            }
        });

        mPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSliderViewPager.setCurrentItem(mCurrentPage - 1);
            }
        });
    }





    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        outState.putInt(INSTANCE_PAGE, mCurrentPage);
    }






    private void postDelayLogin() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(OnboardingActivity.this, "starting the app...", Toast.LENGTH_LONG).show();
            }
        }, 250);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                requestLogin();

            }
        }, SPLASH_TIME_OUT);
    }





    public void dotsIndicator(int position){
        mDots = new TextView[4];
        mDotLayout.removeAllViews();

        for (int i = 0; i < mDots.length; i++){
            mDots[i] = new TextView(this);
            mDots[i].setText(Html.fromHtml("&#8226"));
            mDots[i].setTextSize(35);
            mDots[i].setTextColor(getResources().getColor(R.color.transparentColor));

            mDotLayout.addView(mDots[i]);
        }

        if (mDots.length > 0){
            mDots[position].setTextColor(getResources().getColor(R.color.colorAccent));
        }

        mainBoarding.setBackgroundResource(backgroundImage[position]);
    }



    ViewPager.OnPageChangeListener viewLister = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            Log.d(TAG, "onPageSelected: position " + position);
            dotsIndicator(position);
            mCurrentPage = position;

            if (position == 0){
                mPrevious.setEnabled(false);
                mPrevious.setVisibility(View.GONE);
                mNext.setEnabled(true);
                mNext.setVisibility(View.VISIBLE);
            } else if(position > 0 && position < 3){
                mPrevious.setEnabled(true);
                mPrevious.setVisibility(View.VISIBLE);
                mNext.setEnabled(true);
                mNext.setVisibility(View.VISIBLE);
            } else {
                mNext.setEnabled(false);
                mNext.setVisibility(View.GONE);
                postDelayLogin();
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };


    /**
     * check the credentials provided by the user and
     * grant or not the access to the application
     */
    private void requestLogin(){
        RequestWebServiceInterface requestWebServiceInterface = LoginServiceGenerator.createService(RequestWebServiceInterface.class, getApplicationContext());
        Call<Void> call = requestWebServiceInterface.login();
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()){
                    Log.i(TAG, "onResponse isSuccessful: " + response.message() + " response from login");
                    Intent intent = HomeActivity.newIntent(OnboardingActivity.this);
                    startActivity(intent);
                } else {
                    Log.i(TAG, "onResponse is not successful: " + response.message() + " response from login");
                    Intent intent = LoginActivity.newIntent(OnboardingActivity.this);
                    startActivity(intent);
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                isRequestSucceed = false;
                Toast.makeText(OnboardingActivity.this, "request failed...", Toast.LENGTH_LONG).show();
                Log.d(TAG, "onFailure: " + t.getMessage());
            }
        });
    }

}
