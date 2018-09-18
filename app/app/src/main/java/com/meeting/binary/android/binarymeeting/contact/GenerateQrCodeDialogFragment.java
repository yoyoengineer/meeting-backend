package com.meeting.binary.android.binarymeeting.contact;

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
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;
import com.meeting.binary.android.binarymeeting.R;
import com.meeting.binary.android.binarymeeting.model.Contact;
import com.meeting.binary.android.binarymeeting.service.cookie.CookiePreferences;
import com.meeting.binary.android.binarymeeting.service.generetor.BaseUrlGenerator;

import java.util.List;

public class GenerateQrCodeDialogFragment extends DialogFragment {

    private ImageView mQrCodeImage;

    private static final String EXTRA_ID = "event_id";
    private static final String TAG = "dialog_qr_code";

    private String eventId;

    public static GenerateQrCodeDialogFragment newInstance(String id) {
        Bundle args = new Bundle();
        args.putString(EXTRA_ID, id);
        GenerateQrCodeDialogFragment fragment = new GenerateQrCodeDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        eventId = getArguments().getString(EXTRA_ID);
        if (eventId != null){
            Log.d(TAG, "onCreate: recup event succeed");
        } else {
            Log.d(TAG, "onCreate: recup event failed");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.generate_qr_code_dialog, null);
        mQrCodeImage = view.findViewById(R.id.qr_code_dialog);
        requestQrCode();
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


    private void requestQrCode(){
        String recupCookie = CookiePreferences.getStoredCookie(getActivity());
        LazyHeaders.Builder builder = new LazyHeaders.Builder()
                .addHeader("Cookie", recupCookie);
        GlideUrl glideUrl = new GlideUrl(BaseUrlGenerator.BINARY_BASE_URL + "/event/attendance/" + eventId + "/qrcode" , builder.build());
        Glide.with(GenerateQrCodeDialogFragment.this)
                .load(glideUrl)
                .placeholder(getResources().getDrawable(R.drawable.photo_no_image))
                .into(mQrCodeImage);
    }
}
