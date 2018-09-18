package com.meeting.binary.android.binarymeeting.message;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.meeting.binary.android.binarymeeting.R;
import com.meeting.binary.android.binarymeeting.model.Contact;

import java.text.MessageFormat;
import java.util.List;

public class PickFileActionDialogFragment extends DialogFragment {
    public static final String EXTRA_TYPE = "extra_type_file_to send";
    private String type = "";

    private ImageView photo;
    private ImageView video;
    private ImageView document;


    public static PickFileActionDialogFragment newInstance() {
        Bundle args = new Bundle();
        PickFileActionDialogFragment fragment = new PickFileActionDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.pickfileactiondialog, null);
        photo = (ImageView) view.findViewById(R.id.photo);
        video = (ImageView) view.findViewById(R.id.video);
        document = (ImageView) view.findViewById(R.id.document);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(view);

        photo.setOnClickListener(view1 -> {
            type = "PHOTO";
            sendResult(Activity.RESULT_OK, type);
            dismiss();
        });

        video.setOnClickListener(view1 -> {
            type = "VIDEO";
            sendResult(Activity.RESULT_OK, type);
            dismiss();
        });

        document.setOnClickListener(view1 -> {
            type = "VIDEO";
            sendResult(Activity.RESULT_OK, type);
            dismiss();
        });


        return builder.create();
    }


    private void sendResult(int resultCode, String type){

        if (getTargetFragment() == null){
            return;
        }

        Intent intent = new Intent();
        intent.putExtra(EXTRA_TYPE, type);
        getTargetFragment().onActivityResult(getTargetRequestCode(), resultCode, intent);
    }
}
