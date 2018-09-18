package com.meeting.binary.android.binarymeeting.contact;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.meeting.binary.android.binarymeeting.R;

/**
 * Created by meldi on 3/23/2018.
 */

public class WriteCommentFragment extends Fragment {

    public static WriteCommentFragment newInstance() {

        Bundle args = new Bundle();

        WriteCommentFragment fragment = new WriteCommentFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_write_comment, container, false);

        return view;
    }
}
