package com.meeting.binary.android.binarymeeting.event;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.meeting.binary.android.binarymeeting.R;
import com.meeting.binary.android.binarymeeting.model.Conference;
import com.meeting.binary.android.binarymeeting.model.Contact;
import com.meeting.binary.android.binarymeeting.model.Location;
import com.meeting.binary.android.binarymeeting.service.generetor.GeneralServiceGenerator;
import com.meeting.binary.android.binarymeeting.service.request_interface.RequestWebServiceInterface;
import com.meeting.binary.android.binarymeeting.start.HomeActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateMeetingFragment extends Fragment implements View.OnClickListener, CompoundButton.OnCheckedChangeListener{
    private Conference conference = new Conference();
    private Location location;
    private static final String DIALOG_MEETING_DATE = "DialogAgendaDate";
    private static final String DIALOG_MEETING_TIME = "DialogAgendaStartTime";
    private static final int REQUEST_MEETING_DATE = 0;
    private static final int REQUEST_MEETING_TIME = 1;
    private static final int REQUEST_MEETING_LOCATION = 2;

    private static final String TAG = "create_meeting";

    //    String[] listItems;
    private String conferenceDate = "";
    private String conferenceTime = "";
//    List<String> listItems;

    private EditText topic;
    private EditText description;
    private TextView time;
    private TextView address;
    private EditText specificAddress;
    private TextView invitees;
    private TextView otherSpeakers;
    private Switch isPublic;
    private Switch meSpeaker;
    private Button createMeeting;
    private List<Contact> friends = new ArrayList<>();
    private String[] names;
    private EditText capacity;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_create_meeting, container, false);
        /**
         * 请求朋友列表
         */
        getFriends();
        topic =view.findViewById(R.id.topic_createmeeting);
        description = view.findViewById(R.id.description_createmeeting);
        time = view.findViewById(R.id.time_createmeeting);
        address = view.findViewById(R.id.adress_createmeeting);
        specificAddress = view.findViewById(R.id.specificAddress_createmeeting);
        invitees = view.findViewById(R.id.invitees_createmeeting);
        otherSpeakers = view.findViewById(R.id.otherSpeakers_createmeeting);
        isPublic = view.findViewById(R.id.isPrivate_createmeeting);
        meSpeaker = view.findViewById(R.id.meSpeaker_createmeeting);
        createMeeting = view.findViewById(R.id.createMeeting_createmeeting);
        capacity = view.findViewById(R.id.capacity_createmeeting);

        invitees.setOnClickListener(this);
        otherSpeakers.setOnClickListener(this);
        isPublic.setOnCheckedChangeListener(this);
        meSpeaker.setOnCheckedChangeListener(this);
        meSpeaker.setChecked(true);
        time.setOnClickListener(this);
        address.setOnClickListener(this);
        createMeeting.setOnClickListener(this);
//        listItems = friends.toArray(new String[friends.size()]);
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK){
            return;
        }

        //handle the date picker fragment
        if (requestCode == REQUEST_MEETING_DATE){
            if (data == null){
                return;
            }
            Date date = (Date)data.getSerializableExtra(DatePickerFragment.EXTRA_DATE);

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            conferenceDate = simpleDateFormat.format(date);
        }


        //handle the time picker fragment
        if (requestCode == REQUEST_MEETING_TIME){
            if (data == null){
                return;
            }
            Date date = (Date)data.getSerializableExtra(TimePickerFragment.EXTRA_DATE_TIME);
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
            conferenceTime = simpleDateFormat.format(date);
            if (!(conferenceDate.isEmpty()||conferenceTime.isEmpty())){
                time.setText(conferenceDate + " " + conferenceTime);
                SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                try {
                    conference.setTime(f.parse(conferenceDate + " " + conferenceTime).getTime());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }

        if (data != null && requestCode == REQUEST_MEETING_LOCATION){
            Location location = (Location)data.getSerializableExtra("location");
            Log.i(TAG, "onCreate: " + location.getAddress());
            conference.setLocation(location);
            address.setText(location.getAddress());
        }
    }

    @Override
    public void onClick(View view) {
        FragmentManager manager = getFragmentManager();
        switch (view.getId()) {
            case R.id.time_createmeeting:
                TimePickerFragment dialog = TimePickerFragment.newInstance();
                dialog.setTargetFragment(CreateMeetingFragment.this, REQUEST_MEETING_TIME);
                dialog.show(manager, DIALOG_MEETING_TIME);
                DatePickerFragment datePickerFragment = DatePickerFragment.newInstance();
                datePickerFragment.setTargetFragment(CreateMeetingFragment.this, REQUEST_MEETING_DATE);
                datePickerFragment.show(manager, DIALOG_MEETING_DATE);
                break;
            case R.id.invitees_createmeeting:
                boolean[] checkedItems;
                checkedItems = new boolean[friends.size()];
                ArrayList<Integer> mUserItems = new ArrayList<>();
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(getActivity());
                mBuilder.setTitle("Choose Invitees");
                final ArrayList<Integer> finalMUserItems = mUserItems;

                if (names!=null && names.length > 0 ) {
                    mBuilder.setMultiChoiceItems(names, checkedItems, new DialogInterface.OnMultiChoiceClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int position, boolean isChecked) {
                            if (isChecked) {
                                if (!finalMUserItems.contains(position)) {
                                    finalMUserItems.add(position);
                                }
                            } else {
                                if (finalMUserItems.contains(position)) {
                                    finalMUserItems.remove(finalMUserItems.indexOf(position));
                                }
                            }
                        }
                    });

                }
                mBuilder.setCancelable(false);
                final ArrayList<Integer> finalMUserItems2 = mUserItems;
                if(names!=null &&  names.length > 0 ) {
                    mBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int which) {
                            String item = "";
                            List<String> inviteesInsert = new ArrayList<>();
                            for (int i = 0; i < finalMUserItems2.size(); i++) {
                                item = item + names[finalMUserItems2.get(i)];
                                inviteesInsert.add(names[finalMUserItems2.get(i)]);
                                if (i != finalMUserItems2.size() - 1) {
                                    item = item + ", ";
                                }
                            }
                            invitees.setText(item);
                            conference.setInvitees(inviteesInsert);
                        }
                    });
                }


                mBuilder.setNegativeButton("DISMISS", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });

                final boolean[] finalCheckedItems = checkedItems;
                final ArrayList<Integer> finalMUserItems1 = mUserItems;
                mBuilder.setNeutralButton("CLEAR_ALL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        for (int i = 0; i < finalCheckedItems.length; i++) {
                            finalCheckedItems[i] = false;
                            finalMUserItems1.clear();
                            invitees.setText("");
                        }
                    }
                });

                AlertDialog mDialog = mBuilder.create();
                mDialog.show();
                break;
            case R.id.otherSpeakers_createmeeting:
                final boolean[] checkedItems1;
                checkedItems1 = new boolean[friends.size()];
                final ArrayList<Integer> mUserItems1 = new ArrayList<>();
                AlertDialog.Builder mBuilder1 = new AlertDialog.Builder(getActivity());
                mBuilder1.setTitle("Choose Other Speakers");
                if(names!=null && names.length > 0) {
                    mBuilder1.setMultiChoiceItems(names, checkedItems1, new DialogInterface.OnMultiChoiceClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int position, boolean isChecked) {
                            if (isChecked) {
                                if (!mUserItems1.contains(position)) {
                                    mUserItems1.add(position);
                                }
                            } else {
                                if (mUserItems1.contains(position)) {
                                    mUserItems1.remove(mUserItems1.indexOf(position));
                                }
                            }

                        }
                    });
                }

                mBuilder1.setCancelable(false);

                if(names!=null && names.length > 0) {
                    mBuilder1.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int which) {
                            String item = "";
                            List<String> otherSpeakersInsert = new ArrayList<>();
                            for (int i = 0; i < mUserItems1.size(); i++) {
                                item = item + names[mUserItems1.get(i)];
                                otherSpeakersInsert.add(names[mUserItems1.get(i)]);
                                if (i != mUserItems1.size() - 1) {
                                    item = item + ", ";
                                }
                            }
                            otherSpeakers.setText(item);
                            conference.setOtherSpeakers(otherSpeakersInsert);
                        }
                    });
                }

                mBuilder1.setNegativeButton("DISMISS", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });

                mBuilder1.setNeutralButton("CLEAR_ALL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        for (int i = 0; i < checkedItems1.length; i++) {
                            checkedItems1[i] = false;
                            mUserItems1.clear();
                            otherSpeakers.setText("");
                        }
                    }
                });

                AlertDialog mDialog1 = mBuilder1.create();
                mDialog1.show();
                break;
            case R.id.createMeeting_createmeeting:
                if (validate()){
                    startCreateMeeting();

                }
                else
                    Toast.makeText(getActivity(), "Provide all the details please", Toast.LENGTH_SHORT).show();

                break;
            case R.id.adress_createmeeting:
                Intent intent = new Intent(getActivity(),BaiduMapActivity.class);
//                startActivity(intent);
                startActivityForResult(intent,REQUEST_MEETING_LOCATION);
                break;
        }
    }

    //    private List<String> getFriends(){
//        List<String> friends = new ArrayList<>();
//        friends.add("Jack");
//        friends.add("Bob");
//        friends.add("Tom");
//        friends.add("John");
//        friends.add("Tony");
//        friends.add("Sam");
//        friends.add("David");
//        return friends;
//    }
    private void getFriends(){
        RequestWebServiceInterface requestWebServiceInterface =
                GeneralServiceGenerator.CreateService(RequestWebServiceInterface.class, getActivity());
        Call<List<Contact>> call = requestWebServiceInterface.getFriends();
        call.enqueue(new Callback<List<Contact>>() {
            @Override
            public void onResponse(Call<List<Contact>> call, Response<List<Contact>> response) {
                friends = response.body();
                if(friends==null){
                    friends = Collections.emptyList();
                }
                if(!friends.isEmpty()){
                    names = new String[friends.size()];
                    for(int i =0; i < friends.size() ; i++){
                        names[i] = friends.get(i).getName();
                    }
                }
                Log.d(TAG, "onResponse: friends:" + friends);
            }

            @Override
            public void onFailure(Call<List<Contact>> call, Throwable t) {

            }
        });
    }

    private void createMeeting(Conference conference){

        RequestWebServiceInterface requestWebServiceInterface =
                GeneralServiceGenerator.CreateService(RequestWebServiceInterface.class, getActivity());
        Call<Void> call = requestWebServiceInterface.createMeeting(conference);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if(response.isSuccessful()){
                    Toast.makeText(getActivity(), "Event created successfully", Toast.LENGTH_SHORT).show();
                    Intent intent0 = HomeActivity.newIntent(getActivity());
                    startActivity(intent0);

                }
                else{
                    Toast.makeText(getActivity(), "Event creation failed", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(getActivity(), "Event creation failed", Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void startCreateMeeting() {
        conference.setCapacity(Integer.parseInt(capacity.getText().toString()));
        conference.setTopic(topic.getText().toString());
        if (!description.getText().toString().isEmpty()){
            conference.setDescription(description.getText().toString());
        }
        else {
            Toast.makeText(getActivity(), "Give a description for your event", Toast.LENGTH_SHORT).show();
        }
        if (!specificAddress.getText().toString().isEmpty()){
            conference.setSpecificAddress(specificAddress.getText().toString());
        }
        Log.d(TAG, "startCreateMeeting: " + conference);
        //发送请求
        /**
         * Todo
         */
        createMeeting(conference);
    }

    public boolean validate() {
        boolean valid = true;

        String topicText = topic.getText().toString();
        String timeText = time.getText().toString();
        String addressText = address.getText().toString();
        String capacityText = capacity.getText().toString();
        int capacity = 0;
        try{
            capacity = Integer.parseInt(capacityText);
        }
        catch(NumberFormatException e){

        }

        if (topicText.isEmpty() || timeText.isEmpty() || addressText.isEmpty() || capacity < 1 ) {
            valid = false;
        }
        return valid;
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        if (meSpeaker.isChecked()){
            conference.setMeSpeaker(true);
        }else {
            conference.setMeSpeaker(false);
        }
        if (isPublic.isChecked()){
            conference.setPrivate(true);
        }else{
            conference.setPrivate(false);
        }
    }
}
