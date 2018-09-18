package com.meeting.binary.android.binarymeeting.event;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.meeting.binary.android.binarymeeting.R;

public class EventHomeBoardingSlider extends PagerAdapter{

    private Context mContext;
    private LayoutInflater mLayoutInflater;

    public int[] slide_images = {R.drawable.reslife_splatterdance,
            R.drawable.images2,
            R.drawable.student_events,
            R.drawable.images4};


    public EventHomeBoardingSlider(Context context) {
        mContext = context;
    }

    @Override
    public int getCount() {
        return slide_images.length;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == (RelativeLayout)object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        mLayoutInflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = mLayoutInflater.inflate(R.layout.slide_home_event_boarding, container, false);
        //ImageView slideImageView = (ImageView)view.findViewById(R.id.image_event);

        view.setBackgroundResource(slide_images[position]);
        //slideImageView.setImageResource(slide_images[position]);


        container.addView(view);
        return view;
    }


    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((RelativeLayout)object);
    }
}
