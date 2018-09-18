package com.meeting.binary.android.binarymeeting.event;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.meeting.binary.android.binarymeeting.R;


public class ScanCheck extends DialogFragment {

    private TextView mDialogText;

    private static final String EXTRA_ID = "event_id";
    private static final String TAG = "dialog_check_validate";

    private String message;
    private boolean scanRecheck = false;

    public static ScanCheck newInstance(String id) {
        Bundle args = new Bundle();
        args.putString(EXTRA_ID, id);
        ScanCheck fragment = new ScanCheck();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        message = getArguments().getString(EXTRA_ID);
        if (message != null){
            Log.d(TAG, "onCreate: recup event succeed");
        } else {
            Log.d(TAG, "onCreate: recup event failed");
        }
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.scan_layout, null);
        mDialogText = view.findViewById(R.id.dialog_text);
        mDialogText.setText(message);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
        .setView(view)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dismiss();
                    }
                });
        return builder.create();
    }

}
