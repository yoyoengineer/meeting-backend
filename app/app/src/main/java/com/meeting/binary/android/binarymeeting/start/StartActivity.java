package com.meeting.binary.android.binarymeeting.start;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.meeting.binary.android.binarymeeting.R;

public class StartActivity extends AppCompatActivity {

    private static int SPLASH_TIME_OUT = 5000;

    private TextView mWelcomeTextImageView;
    private ImageView homeLogoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);


        mWelcomeTextImageView = (TextView)findViewById(R.id.welcome_text);
        homeLogoView = (ImageView)findViewById(R.id.homelogo);

        Animation myAnimation = AnimationUtils.loadAnimation(this, R.anim.mytransition);
        mWelcomeTextImageView.startAnimation(myAnimation);
        homeLogoView.startAnimation(myAnimation);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(StartActivity.this, OnboardingActivity.class);
                startActivity(intent);
                finish();
            }
        }, SPLASH_TIME_OUT);

    }
}
