package com.meeting.binary.android.binarymeeting.event;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.meeting.binary.android.binarymeeting.R;

/**
 * Created by meldi on 3/19/2018.
 */

public class AttendeesFragment extends Fragment {

    public static AttendeesFragment newInstance() {

        Bundle args = new Bundle();

        AttendeesFragment fragment = new AttendeesFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_attendees, container, false);

        return view;
    }
}
