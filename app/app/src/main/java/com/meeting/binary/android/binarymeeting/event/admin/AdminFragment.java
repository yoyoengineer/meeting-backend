package com.meeting.binary.android.binarymeeting.event.admin;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.meeting.binary.android.binarymeeting.R;
import com.meeting.binary.android.binarymeeting.event.CreateLotteryActivity;
import com.meeting.binary.android.binarymeeting.event.CreatePollActivity;
import com.meeting.binary.android.binarymeeting.event.MyEventFragment;
import com.meeting.binary.android.binarymeeting.event.SendAnnouncementActivity;
import com.meeting.binary.android.binarymeeting.event.other_events.LargePhotoFragment;
import com.meeting.binary.android.binarymeeting.event.upload_download.UploadPhotoActivity;
import com.meeting.binary.android.binarymeeting.event.upload_download.ViewPhotoUploadedFragment;
import com.meeting.binary.android.binarymeeting.event.upload_download.upLoadDocumentActivity;
import com.meeting.binary.android.binarymeeting.other.agenda.AgendaMainActivity;
import com.meeting.binary.android.binarymeeting.service.cookie.CookiePreferences;
import com.meeting.binary.android.binarymeeting.service.generetor.BaseUrlGenerator;

/**
 * Created by meldi on 3/19/2018.
 */

public class AdminFragment extends Fragment {

    private static final String TAG = "admin";
    private static final String MESSAGE = "event_id";

    private CardView mAnnouncement;
    private CardView mDocuments;
    private CardView mPhotos;
    private CardView mLivePoll;
    private CardView mAgenda;
    private Button mGenerateQrCode;
    private ImageView mQrCodeImageView;
    private ImageView adminPhoto;
    private CardView event_lottery;
    private String eventId;


    private String name;
    private static final int REQUEST_PHOTO = 0;
    private static final String DIALOG_PHOTO = "LargePhotoFragment";


    public static AdminFragment newInsgtance(String id){
        Bundle args = new Bundle();
        args.putString(MESSAGE,id);
        AdminFragment fragment = new AdminFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        eventId = (String) getArguments().getString(MESSAGE);
        name = CookiePreferences.getStoredName(getActivity());
        if (eventId != null){
            Log.i(TAG, "onCreate: id is not null");
        }else {
            Log.i(TAG, "onCreate: id is null");
        }

    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.admin_fragment, container, false);

//        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Event Admin");

        mAnnouncement = view.findViewById(R.id.admin_announcement);
        mDocuments = view.findViewById(R.id.admin_document);
        mPhotos = view.findViewById(R.id.admin_event_photos);
        mLivePoll = view.findViewById(R.id.admin_event_live);
        mGenerateQrCode = view.findViewById(R.id.generate_qr_code);
        mQrCodeImageView = view.findViewById(R.id.qr_code_image);
        mAgenda = view.findViewById(R.id.event_agenda_cardview);
        event_lottery = view.findViewById(R.id.event_lotteries_cardview);
        adminPhoto = view.findViewById(R.id.adminimage);

        event_lottery.setOnClickListener(e -> CreateLotteryActivity.actionStart(getActivity(),eventId));

        /**start announcement*/
        mAnnouncement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = SendAnnouncementActivity.newIntent(getActivity(), eventId);
                startActivity(intent);
            }
        });

        /**view agenda*/
        mAgenda.setOnClickListener(view1 -> {
            Intent intent = AgendaMainActivity.newIntent(getActivity(),eventId);
            startActivity(intent);
        });

        mDocuments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = upLoadDocumentActivity.newIntent(getActivity(), eventId);
                startActivity(intent);
            }
        });


        mPhotos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = UploadPhotoActivity.newIntent(getActivity(), eventId);
                startActivity(intent);
            }
        });



        /**create the pool*/
        mLivePoll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = CreatePollActivity.newIntent(getActivity(), eventId);
                startActivity(intent);
            }
        });


        mGenerateQrCode.setOnClickListener(view1 -> {});



        adminPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // display large image
                String url = BaseUrlGenerator.BINARY_BASE_URL + "/profile/mypicture/" + name;
                FragmentManager manager = getFragmentManager();
                LargePhotoFragment dialog = LargePhotoFragment.newInstance(url, name);
                dialog.setTargetFragment(AdminFragment.this, REQUEST_PHOTO);
                dialog.show(manager, DIALOG_PHOTO);
            }
        });

        return view;
    }


    @Override
    public void onResume() {
        super.onResume();
        setImageAdmin();
    }

    private void setImageAdmin() {
        String recupCookie = CookiePreferences.getStoredCookie(getActivity());
        Log.i(TAG, "intercept: " + recupCookie);
        LazyHeaders.Builder builder = new LazyHeaders.Builder().addHeader("Cookie", recupCookie);
        GlideUrl glideUrl = new GlideUrl(BaseUrlGenerator.BINARY_BASE_URL + "/profile/mypicture/" + name, builder.build());

        Glide.with(AdminFragment.this)
                .load(glideUrl)
                .asBitmap()
                .centerCrop()
                .placeholder(getResources().getDrawable(R.drawable.photo_no_image))
                .into(new BitmapImageViewTarget(adminPhoto) {
                    @Override
                    protected void setResource(Bitmap resource) {
                        RoundedBitmapDrawable drawable = RoundedBitmapDrawableFactory.create(AdminFragment.this.getResources(),
                                Bitmap.createScaledBitmap(resource, 45, 45, false));
                        drawable.setCircular(true);
                        adminPhoto.setImageDrawable(drawable);
                    }
                });
        Log.i(TAG, "bind: " + name);
    }


}
