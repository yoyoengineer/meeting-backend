package com.meeting.binary.android.binarymeeting.event.other_events;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.meeting.binary.android.binarymeeting.R;
import com.meeting.binary.android.binarymeeting.event.upload_download.ViewPhotoUploadedFragment;
import com.meeting.binary.android.binarymeeting.service.cookie.CookiePreferences;
import com.meeting.binary.android.binarymeeting.service.generetor.BaseUrlGenerator;


public class LargePhotoFragment extends DialogFragment {

    private static final String ARG_URL = "fileurl";
    private static final String ARG_FILENAME = "filename";

    public static final String EXTRA_URL = "com.bignerdranch.android.campus.event.url";


    private ImageView largePhoto;


    public static LargePhotoFragment newInstance(String url, String fileName){
        Bundle args = new Bundle();
        args.putString(ARG_URL, url);
        args.putString(ARG_FILENAME, fileName);


        LargePhotoFragment pickerFragment = new LargePhotoFragment();
        pickerFragment.setArguments(args);

        return pickerFragment;
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View v = LayoutInflater.from(getActivity()).inflate(R.layout.large_photo_layout, null);


        String name = getArguments().getString(ARG_FILENAME);
        String url = getArguments().getString(ARG_URL);

        largePhoto = v.findViewById(R.id.photo_view);

        displayLargeImage(url);

        /**
         * retrieving the view of the date picker and set the date sent by the lunching fragment
         */



        return new AlertDialog.Builder(getActivity()).setTitle(name)
                .setView(v)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dismiss();
                    }
                })
                .create();
    }




    private void displayLargeImage(String url) {

        String recupCookie = CookiePreferences.getStoredCookie(getActivity());
        LazyHeaders.Builder builder = new LazyHeaders.Builder().addHeader("Cookie", recupCookie);
        GlideUrl glideUrl = new GlideUrl(url, builder.build());

        Glide.with(LargePhotoFragment.this)
                .load(glideUrl)
                .asBitmap()
                .centerCrop()
                .placeholder(getResources().getDrawable(R.drawable.photo_icon_placeholder))
                .into(largePhoto);
    }
}
