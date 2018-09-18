package com.meeting.binary.android.binarymeeting.contact;

import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.Menu;
import android.view.MenuItem;

import com.meeting.binary.android.binarymeeting.other.SingleFragmentActivity;
import com.meeting.binary.android.binarymeeting.other.SingleFragmentActivityNoBar;
import com.meeting.binary.android.binarymeeting.model.Contact;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import io.realm.RealmList;

public class ContactActivity extends SingleFragmentActivityNoBar {

    private static final String CONTACT_EXTRA_MESSAGE = "com.binary.contact";
    private static final String TAG = "getnameinca";


    @Override
    public Fragment createFragment() {
        List<Contact> contacts = getIntent().getParcelableArrayListExtra(CONTACT_EXTRA_MESSAGE);
        return ContactFragment.newInstance((Serializable) contacts);
    }


    public static Intent newIntent(Context context, List<Contact> contacts){
        Intent intent = new Intent(context, ContactActivity.class);
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(CONTACT_EXTRA_MESSAGE, (ArrayList<? extends Parcelable>) contacts);
        intent.putExtras(bundle);
        return intent;
    }


}
