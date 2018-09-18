package com.meeting.binary.android.binarymeeting.contact;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.meeting.binary.android.binarymeeting.R;

/**
 * Created by loyck-daryl on 2018/3/14.
 */

public class AddContactFragment extends Fragment {

    private ImageView mContactImg;
    private TextView mContactName;
    private TextView mContactPhone;
    private TextView mContactEmail;
    private Button mSave;




    public static AddContactFragment newInstance(){
        return new AddContactFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_contact, container, false);


        /**
         * get widget ui
         */
        mContactImg = view.findViewById(R.id.add_contact_img);
        mContactName = view.findViewById(R.id.add_contact_name);
        mContactPhone = view.findViewById(R.id.add_contact_contact);
        mContactEmail = view.findViewById(R.id.add_contact_email);
        mSave = view.findViewById(R.id.save_contact);


        /**
         * wire up widget
         */
        mContactName.addTextChangedListener(new ChangeTheTextName());
        mContactPhone.addTextChangedListener(new ChangeTheTextContacts());
        mContactEmail.addTextChangedListener(new ChangeTheTextEmail());

        mSave.setOnClickListener(view1 -> updateDataContact());
        return view;
    }


    @Override
    public void onPause() {
        super.onPause();
    }


    /**
     * update the contact to the database
     */
    private void updateDataContact(){

    }







    /**
     * refer to 'mContactName' on text changing
     */
    class ChangeTheTextName implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    }


    /**
     * refer to 'mContactPhone' on text changing
     */
    class ChangeTheTextContacts implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    }


    /**
     * refer to 'mContactPhone' on text changing
     */
    class ChangeTheTextEmail implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    }
}
