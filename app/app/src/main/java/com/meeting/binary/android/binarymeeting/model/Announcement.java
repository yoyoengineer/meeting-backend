package com.meeting.binary.android.binarymeeting.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by meldi on 3/20/2018.
 */

public class Announcement implements Parcelable, Serializable{

    private String eventId;
    private String text;
    private long time;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.eventId);
        dest.writeString(this.text);
        dest.writeLong(this.time);
    }

    public Announcement() {
    }

    protected Announcement(Parcel in) {
        this.eventId = in.readString();
        this.text = in.readString();
        this.time = in.readLong();
    }

    public static final Parcelable.Creator<Announcement> CREATOR = new Parcelable.Creator<Announcement>() {
        @Override
        public Announcement createFromParcel(Parcel source) {
            return new Announcement(source);
        }

        @Override
        public Announcement[] newArray(int size) {
            return new Announcement[size];
        }
    };
}
