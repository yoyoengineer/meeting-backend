package com.meeting.binary.android.binarymeeting.profile;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.meeting.binary.android.binarymeeting.R;

/**
 * Created by loyck-daryl on 2018/3/13.
 */

public class EditProfileFragment extends Fragment {

    public EditText mEditName;
    public EditText mEditContact;
    public EditText mEditMail;
    public EditText mEditSite;
    public EditText mEditCompany;


    public static Fragment newInstance(){
        return new EditProfileFragment();
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_profile, container, false);

        /**
         * set personal toolbar
         */
        Toolbar myToolbar = (Toolbar)view.findViewById(R.id.my_toolbar);
        myToolbar.setTitle("Edit profile");
        ((AppCompatActivity)getActivity()).setSupportActionBar(myToolbar);


        /**
         * get reference to the ui widget
         */
        mEditName = view.findViewById(R.id.edit_name);
        mEditContact  = view.findViewById(R.id.edit_contact);
        mEditCompany = view.findViewById(R.id.edit_compagnie);
        mEditMail = view.findViewById(R.id.edit_email);
        mEditSite = view.findViewById(R.id.edit_website);



        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_edit_profile, menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_save :
                //save into the database
                return true;
            default: return super.onOptionsItemSelected(item);
        }
    }
}
