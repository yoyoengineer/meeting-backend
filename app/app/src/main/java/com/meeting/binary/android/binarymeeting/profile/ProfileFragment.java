package com.meeting.binary.android.binarymeeting.profile;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.meeting.binary.android.binarymeeting.R;
import com.meeting.binary.android.binarymeeting.model.MyContactPage;
import com.meeting.binary.android.binarymeeting.other.SingleFragmentActivity;
import com.meeting.binary.android.binarymeeting.other.SingleFragmentActivityNoBar;
import com.meeting.binary.android.binarymeeting.service.cookie.CookiePreferences;
import com.meeting.binary.android.binarymeeting.service.generetor.BaseUrlGenerator;

import java.io.Serializable;

/**
 * Created by loyck-daryl on 2018/3/13.
 */

public class ProfileFragment extends Fragment {

    private static final String INFO_PROFILE = "info_profile";
    private static final String TAG = "profile_fragment";

    private ImageView mPhotoProfile;
    private LinearLayout mEditProfile;
    private TextView mProfileName;


    private TextView username;
    private TextView description;
    private TextView currentTown;
    private TextView school;
    private TextView email;


    private MyContactPage mMyContactPage;

    public static ProfileFragment newInstance(Serializable infoProfile) {

        Bundle args = new Bundle();
        args.putSerializable(INFO_PROFILE, infoProfile);
        ProfileFragment fragment = new ProfileFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mMyContactPage = (MyContactPage) getArguments().getSerializable(INFO_PROFILE);
        }
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        /**
         * retrieve reference of the ui view in the profile
         */
        mEditProfile = view.findViewById(R.id.edit_profile);
        mPhotoProfile = view.findViewById(R.id.image_contact_page);
        mProfileName = view.findViewById(R.id.name_contact_page_profile);


        username = view.findViewById(R.id.user_name);
        description = view.findViewById(R.id.description);
        currentTown = view.findViewById(R.id.address);
        school = view.findViewById(R.id.school);
        email = view.findViewById(R.id.email);



        mProfileName.setText(mMyContactPage.getName());
        username.setText(mMyContactPage.getName());
        description.setText(mMyContactPage.getAboutMe());
        currentTown.setText(mMyContactPage.getCurrentTown());
        school.setText("University of science and technology");


        /**
         * wire them
         */
        mEditProfile.setOnClickListener(e -> {
//            Intent intent = EditiProfileActivity.newIntent(getActivity());
//            startActivity(intent);
        });


        mPhotoProfile.setOnClickListener(e -> {
            ////do action
        });

        return view;
    }


    @Override
    public void onResume() {
        super.onResume();
        loadImageProfile();
    }

    private void loadImageProfile() {
        String recupCookie = CookiePreferences.getStoredCookie(getActivity());
        Log.i(TAG, "intercept: " + recupCookie);
        LazyHeaders.Builder builder = new LazyHeaders.Builder().addHeader("Cookie", recupCookie);
        GlideUrl glideUrl = new GlideUrl(BaseUrlGenerator.BINARY_BASE_URL + "/profile/mypicture/" + CookiePreferences.getStoredName(getActivity()), builder.build());

        Glide.with(ProfileFragment.this)
                .load(glideUrl)
                .asBitmap()
                .centerCrop()
                .placeholder(getResources().getDrawable(R.drawable.photo_no_image))
                .into(new BitmapImageViewTarget(mPhotoProfile) {
                    @Override
                    protected void setResource(Bitmap resource) {
                        RoundedBitmapDrawable drawable = RoundedBitmapDrawableFactory.create(ProfileFragment.this.getResources(),
                                Bitmap.createScaledBitmap(resource, 100, 100, false));
                        drawable.setCircular(true);
                        mPhotoProfile.setImageDrawable(drawable);
                    }
                });
    }
}
