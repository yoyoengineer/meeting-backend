package com.meeting.binary.android.binarymeeting.start;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.meeting.binary.android.binarymeeting.R;

/**
 * Created by daryl on 2018/2/24.
 */

public class BoardingSlideAdapter extends PagerAdapter {

    public int[] slide_images = {R.drawable.ic_explicit_black_24dp,
            R.drawable.ic_event_seat_black_24dp,
            R.drawable.ic_chat_bubble_black_24dp,
            R.drawable.ic_directions_bike_black_24dp};



    public String[] slide_headings = {"EVENT", "MEETINGS", "CHAT", "ACTIVITIES"};

    public int[] slide_descriptions = {R.string.descriptionEvent,
            R.string.descriptionMeetings,
            R.string.descriptionChat,
            R.string.descriptionActivities
            };

    Context mContext;
    LayoutInflater mLayoutInflater;

    public BoardingSlideAdapter(Context context) {
        mContext = context;
    }

    @Override
    public int getCount() {
        return slide_headings.length;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == (RelativeLayout)object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        mLayoutInflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = mLayoutInflater.inflate(R.layout.slide_onboard, container, false);


        ImageView slideImageView = (ImageView)view.findViewById(R.id.slide_image);
        TextView slideHeading = (TextView)view.findViewById(R.id.slide_heading);
        TextView slideDescription = (TextView)view.findViewById(R.id.slide_description);

        slideImageView.setImageResource(slide_images[position]);
        slideHeading.setText(slide_headings[position]);
        slideDescription.setText(slide_descriptions[position]);

        if (position == 3){
            // slideDescription.setTextColor(ContextCompat.getColor(mContext, R.color.white));
        }

        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((RelativeLayout)object);
    }
}
