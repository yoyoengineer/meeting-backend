package com.meeting.binary.android.binarymeeting.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by loyck-daryl on 2018/3/16.
 */

public class Statistics implements Parcelable , Serializable{
    private int pageView;
    private int nbrOfEvent;
    private int expectedParticipants;
    private int attendees;

    public int getPageView() {
        return pageView;
    }

    public void setPageView(int pageView) {
        this.pageView = pageView;
    }

    public int getNbrOfEvent() {
        return nbrOfEvent;
    }

    public void setNbrOfEvent(int nbrOfEvent) {
        this.nbrOfEvent = nbrOfEvent;
    }

    public int getExpectedParticipants() {
        return expectedParticipants;
    }

    public void setExpectedParticipants(int expectedParticipants) {
        this.expectedParticipants = expectedParticipants;
    }

    public int getAttendees() {
        return attendees;
    }

    public void setAttendees(int attendees) {
        this.attendees = attendees;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.pageView);
        dest.writeInt(this.nbrOfEvent);
        dest.writeInt(this.expectedParticipants);
        dest.writeInt(this.attendees);
    }

    public Statistics() {
    }

    protected Statistics(Parcel in) {
        this.pageView = in.readInt();
        this.nbrOfEvent = in.readInt();
        this.expectedParticipants = in.readInt();
        this.attendees = in.readInt();
    }

    public static final Parcelable.Creator<Statistics> CREATOR = new Parcelable.Creator<Statistics>() {
        @Override
        public Statistics createFromParcel(Parcel source) {
            return new Statistics(source);
        }

        @Override
        public Statistics[] newArray(int size) {
            return new Statistics[size];
        }
    };
}
