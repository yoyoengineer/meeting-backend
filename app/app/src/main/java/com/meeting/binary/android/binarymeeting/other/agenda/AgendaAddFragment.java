package com.meeting.binary.android.binarymeeting.other.agenda;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.meeting.binary.android.binarymeeting.R;
import com.meeting.binary.android.binarymeeting.event.DatePickerFragment;
import com.meeting.binary.android.binarymeeting.event.TimePickerFragment;
import com.meeting.binary.android.binarymeeting.model.Agenda;
import com.meeting.binary.android.binarymeeting.service.generetor.GeneralServiceGenerator;
import com.meeting.binary.android.binarymeeting.service.request_interface.RequestWebServiceInterface;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AgendaAddFragment extends Fragment implements View.OnClickListener{
    private static final String DIALOG_AGENDA_START_TIME = "DialogAgendaStartTime";
    private static final String DIALOG_AGENDA_START_DATE = "DialogAgendaStartDate";
    private static final String DIALOG_AGENDA_END_TIME = "DialogAgendaEndTime";
    private static final String DIALOG_AGENDA_END_DATE = "DialogAgendaEndDate";
    private static final int REQUEST_AGENDA_START_DATE = 0;
    private static final int REQUEST_AGENDA_START_TIME = 1;
    private static final int REQUEST_AGENDA_END_DATE = 2;
    private static final int REQUEST_AGENDA_END_TIME = 3;
    private View view;
    //    private TextView date;
    private TextView agendaStartTimeTextView;
    private TextView agendaEndTimeTextView;
    private EditText agendaTitle;
    private EditText agendaContent;
    private EditText agendaLocation;
    private Calendar mCalendar;
    private String agendaStartDate="";
    private String agendaStartTime="";
    private String agendaEndDate="";
    private String agendaEndTime="";
    private static final String TAG = "AgendaAddFragment.TAG";
    private String eventId;

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    private Agenda agenda = new Agenda();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_agenda_add, container, false);
//        date = view.findViewById(R.id.agenda_date);

        mCalendar = Calendar.getInstance();
        agendaTitle =  view.findViewById(R.id.agenda_title);
        agendaContent = view.findViewById(R.id.agenda_content);
        agendaStartTimeTextView = view.findViewById(R.id.agenda_start_time);
        agendaEndTimeTextView = view.findViewById(R.id.agenda_end_time);
        agendaLocation = view.findViewById(R.id.agenda_location);
//        date.setOnClickListener(this);
        agendaStartTimeTextView.setOnClickListener(this);
        agendaEndTimeTextView.setOnClickListener(this);
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode != Activity.RESULT_OK){
            return;
        }

        Log.d(TAG, "onActivityResult: " + "::::::::::::::::");



//handle the date picker fragment
        if (requestCode == REQUEST_AGENDA_START_DATE){
            if (data == null){
                return;
            }
            Date date = (Date)data.getSerializableExtra(DatePickerFragment.EXTRA_DATE);

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            agendaStartDate = simpleDateFormat.format(date);

            Log.d(TAG, "onActivityResult:agendaStartDate: " + agendaStartDate);
            if (!(agendaStartDate.isEmpty()||agendaStartTime.isEmpty())){
                Log.d(TAG, "onActivityResult: " + agendaStartTime + "::::::" + agendaStartDate);
                agendaStartTimeTextView.setText(agendaStartDate + " " + agendaStartTime);
                SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                try {
                    agenda.setStartTime(f.parse(agendaStartDate + " " + agendaStartTime).getTime());
//                    updateStartTime();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }


        //handle the time picker fragment
        if (requestCode == REQUEST_AGENDA_START_TIME){
            if (data == null){
                return;
            }
            Date date = (Date)data.getSerializableExtra(TimePickerFragment.EXTRA_DATE_TIME);
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
            agendaStartTime = simpleDateFormat.format(date);
//            Log.d(TAG, "onActivityResult:agendaStartTime: " + agendaStartTime);
//            Log.d(TAG, "onActivityResult: " + agendaStartTime + "+++++++++++++" + agendaStartDate);
            if (!(agendaStartDate.isEmpty()||agendaStartTime.isEmpty())){
//                Log.d(TAG, "onActivityResult: " + agendaStartTime + "::::::" + agendaStartDate);
                agendaStartTimeTextView.setText(agendaStartDate + " " + agendaStartTime);
                SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                try {
                    agenda.setStartTime(f.parse(agendaStartDate + " " + agendaStartTime).getTime());
//                    updateStartTime();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }



        if (requestCode == REQUEST_AGENDA_END_DATE){
            if (data == null){
                return;
            }
            Date date = (Date)data.getSerializableExtra(DatePickerFragment.EXTRA_DATE);

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            agendaEndDate = simpleDateFormat.format(date);
        }


        //handle the time picker fragment
        if (requestCode == REQUEST_AGENDA_END_TIME){
            if (data == null){
                return;
            }
            Date date = (Date)data.getSerializableExtra(TimePickerFragment.EXTRA_DATE_TIME);
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
            agendaEndTime = simpleDateFormat.format(date);
            if (!(agendaEndDate.isEmpty()||agendaEndTime.isEmpty())){
                agendaEndTimeTextView.setText(agendaEndDate + " " + agendaEndTime);
                SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                try {
                    agenda.setEndTime(f.parse(agendaEndDate + " " + agendaEndTime).getTime());
//                    updateEndTime();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }




//        //handle the date picker fragment
//        if (requestCode == REQUEST_AGENDA_DATE){
//            if (data == null){
//                return;
//            }
//            Date date = (Date)data.getSerializableExtra(DatePickerFragment.EXTRA_DATE);
//
//
//            agenda.setDate(date);
//            updateDate();
//        }
//
//
//        //handle the time picker fragment
//        if (requestCode == REQUEST_AGENDA_START_TIME){
//            if (data == null){
//                return;
//            }
//            Date date = (Date)data.getSerializableExtra(TimePickerFragment.EXTRA_DATE_TIME);
//            agenda.setStartTime(date);
//            updateStartTime();
//        }
//
//        if (requestCode == REQUEST_AGENDA_END_TIME){
//            if (data == null){
//                return;
//            }
//            Date date = (Date)data.getSerializableExtra(TimePickerFragment.EXTRA_DATE_TIME);
//            agenda.setEndTime(date);
//            updateEndTime();
//        }
    }

    public void refresh() {
        View visibilityLayout = view.findViewById(R.id.visibility_layout);
        visibilityLayout.setVisibility(View.VISIBLE);
    }

//    private void updateDate() {
//        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEEE, MM dd, yyyy.");
//        date.setText(simpleDateFormat.format(agenda.getDate()));
//    }

//    private void updateStartTime(){
//        long asTime = agenda.getStartTime();
//        Calendar calendar = new GregorianCalendar();
//        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
//        simpleDateFormat.setTimeZone(calendar.getTimeZone());
//        calendar.setTimeInMillis(asTime);
//        agendaStartTimeTextView.setText(simpleDateFormat.format(calendar.getTime()));
//    }

//    private void updateEndTime(){
//
//        long aeTime = agenda.getStartTime();
//        Calendar calendar = new GregorianCalendar();
//        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
//        simpleDateFormat.setTimeZone(calendar.getTimeZone());
//        calendar.setTimeInMillis(aeTime);
//        agendaEndTimeTextView.setText(simpleDateFormat.format(calendar.getTime()));
//
//    }

    public long saveAgenda() {
        long agendaAffected = 0;
        agenda.setLocation(agendaLocation.getText().toString());
        agenda.setTitle(agendaTitle.getText().toString());
        agenda.setContent(agendaContent.getText().toString());
        agenda.setEventId(eventId);
        if (agenda.getContent() == null || agenda.getContent().isEmpty() || agenda.getLocation() == null || agenda.getLocation().isEmpty() || agenda.getTitle() == null || agenda.getTitle().isEmpty() || agenda.getStartTime()==0 || agenda.getStartTime() == null || agenda.getEndTime() == null || agenda.getEndTime() == 0){
            Toast.makeText(getActivity(),"Please enter all the information.",Toast.LENGTH_SHORT).show();
        }else {
            /**
             * 存储Agendda
             * Todo
             */
            Log.d(TAG, "saveAgenda: " + agenda);
            RequestWebServiceInterface requestWebServiceInterface =
                    GeneralServiceGenerator.CreateService(RequestWebServiceInterface.class, getActivity());
            Call<Void> call = requestWebServiceInterface.createAgenda(agenda);
            call.enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {

                }
            });
//            agendaAffected = agenda.save();
            agendaAffected = 1;
        }
        return agendaAffected;
    }

    @Override
    public void onClick(View view) {
        FragmentManager manager = getFragmentManager();
        switch (view.getId()){
            case R.id.agenda_start_time:
                TimePickerFragment startTimeDialog = TimePickerFragment.newInstance();
                startTimeDialog.setTargetFragment(AgendaAddFragment.this, REQUEST_AGENDA_START_TIME);
                startTimeDialog.show(manager, DIALOG_AGENDA_START_TIME);
                DatePickerFragment dateStartPickerFragment = DatePickerFragment.newInstance();
                dateStartPickerFragment.setTargetFragment(AgendaAddFragment.this, REQUEST_AGENDA_START_DATE);
                dateStartPickerFragment.show(manager, DIALOG_AGENDA_START_DATE);
                break;
            case R.id.agenda_end_time:
                TimePickerFragment endTimeDialog = TimePickerFragment.newInstance();
                endTimeDialog.setTargetFragment(AgendaAddFragment.this, REQUEST_AGENDA_END_TIME);
                endTimeDialog.show(manager, DIALOG_AGENDA_END_TIME);
                DatePickerFragment dateEndPickerFragment = DatePickerFragment.newInstance();
                dateEndPickerFragment.setTargetFragment(AgendaAddFragment.this, REQUEST_AGENDA_END_DATE);
                dateEndPickerFragment.show(manager, DIALOG_AGENDA_END_DATE);
                break;
            default:
                break;
        }
    }
}
