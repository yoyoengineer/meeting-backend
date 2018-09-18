package com.meeting.binary.android.binarymeeting.start;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.meeting.binary.android.binarymeeting.R;
import com.meeting.binary.android.binarymeeting.event.HomeEventActivity;
import com.meeting.binary.android.binarymeeting.other.notes.ListNoteActivity;
import com.meeting.binary.android.binarymeeting.other.todo.ListTaskActivity;
import com.meeting.binary.android.binarymeeting.service.back_service.MyService;
import com.xys.libzxing.zxing.activity.*;

public class HomeFragment extends Fragment{

    private static final int REQUEST_CODE = 0;
    private CardView mEventCard;
    private CardView mNotesCard;
    private CardView mTaskTodoCard;
    private CardView mAboutUsCard;
    private ImageView mScanQrCode;


    private AppBarLayout appBarLayout;
    private NestedScrollView neestedView;


    private static final String TAG = "homeFragement";


    public static Fragment newInstance(){
        return new HomeFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_home, container, false);

        Toolbar toolbar = (Toolbar)view.findViewById(R.id.home_bar);
        toolbar.setTitle("Campus Event");

        appBarLayout = view.findViewById(R.id.appBar);
        neestedView = view.findViewById(R.id.scroll_id);

        mEventCard = view.findViewById(R.id.event_card);
        mNotesCard = view.findViewById(R.id.note_card);
        mTaskTodoCard = view.findViewById(R.id.task_card);
        mAboutUsCard = view.findViewById(R.id.about_card);
        mScanQrCode = view.findViewById(R.id.scan);

        mEventCard.setOnClickListener(view1 -> {
            Intent intent = HomeEventActivity.newIntent(getActivity());
            startActivity(intent);
        });

        mNotesCard.setOnClickListener(view1 -> {
            Intent intent = ListNoteActivity.newIntent(getActivity());
            startActivity(intent);
        });
        mTaskTodoCard.setOnClickListener(view1 -> {
            Intent intent = ListTaskActivity.newIntent(getActivity());
            startActivity(intent);
        });
        mAboutUsCard.setOnClickListener(view1 -> {});

        mScanQrCode.setOnClickListener(view1 -> {
            Log.i(TAG, "onCreateView: clicked scan icon");
            startActivityForResult(new Intent(getActivity(), CaptureActivity.class),REQUEST_CODE);

        });



        /**listen to collapse change**/
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {

                if (Math.abs(verticalOffset)-appBarLayout.getTotalScrollRange() == 0) {
                    neestedView.setBackgroundResource(R.drawable.presentation);
                } else {
                    neestedView.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.white));
                }
            }
        });


        Intent intent = new Intent(getActivity(), MyService.class);
        getActivity().startService(intent);

        return view;

    }

    @Override
    public void onResume() {
        super.onResume();
    }






    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult: " + resultCode);
        if (resultCode== Activity.RESULT_OK){
            Bundle bundle = data.getExtras();
            Log.d(TAG, "onActivityResult: bundle" + bundle.toString());
            if (bundle != null) {
                String result=bundle.getString("result");
                Log.i(TAG, "onActivityResult: " + result);
                //launch corresponding event
            }
        } else {

        }

    }
}
