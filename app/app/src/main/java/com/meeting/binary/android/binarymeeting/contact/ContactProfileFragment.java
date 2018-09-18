package com.meeting.binary.android.binarymeeting.contact;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.meeting.binary.android.binarymeeting.R;
import com.meeting.binary.android.binarymeeting.model.MyContactPage;

public class ContactProfileFragment extends Fragment {

    private MyContactPage mMyContactPage;

    private static final String MY_CONTACT_PAGE_PARSE_EXTRA_MESSAGE = "com.binary.contact_page_parcelable";
    public static final String TAG = "";


    private TextView name;
    private TextView description;
    private TextView currentTown;
    private TextView school;
    private TextView email;


    public static ContactProfileFragment newInstance(MyContactPage contactPage) {
        Bundle args = new Bundle();
        args.putSerializable(MY_CONTACT_PAGE_PARSE_EXTRA_MESSAGE, contactPage);
        ContactProfileFragment fragment = new ContactProfileFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null){
            mMyContactPage = (MyContactPage) getArguments().getSerializable(MY_CONTACT_PAGE_PARSE_EXTRA_MESSAGE);
            if (mMyContactPage != null) {
                Log.i(TAG, "onCreate: contact not null");
            } else {
                Log.i(TAG, "onCreate: contact page null");
            }
        }
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contact_page_more, container, false);

        name = view.findViewById(R.id.user_name);
        description = view.findViewById(R.id.description);
        currentTown = view.findViewById(R.id.address);
        school = view.findViewById(R.id.school);
        email = view.findViewById(R.id.email);


        name.setText("About " + mMyContactPage.getName());
//        description.setText(mMyContactPage.getAboutMe());
        currentTown.setText("Actually living in " + mMyContactPage.getCurrentTown());
        school.setText(mMyContactPage.getCompanyName() + " University of Science and Technology");
        //email.setText(mMyContactPage.getEmail());
        return view;
    }
}
