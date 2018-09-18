package com.meeting.binary.android.binarymeeting.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by meldi on 3/22/2018.
 */

public class Comment implements Parcelable, Serializable{

    private String id;
    private String username;
    private String name;
    private String eventId;
    private String text;
    private long time;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.username);
        dest.writeString(this.name);
        dest.writeString(this.eventId);
        dest.writeString(this.text);
        dest.writeLong(this.time);
    }

    public Comment() {
    }

    protected Comment(Parcel in) {
        this.id = in.readString();
        this.username = in.readString();
        this.name = in.readString();
        this.eventId = in.readString();
        this.text = in.readString();
        this.time = in.readLong();
    }

    public static final Parcelable.Creator<Comment> CREATOR = new Parcelable.Creator<Comment>() {
        @Override
        public Comment createFromParcel(Parcel source) {
            return new Comment(source);
        }

        @Override
        public Comment[] newArray(int size) {
            return new Comment[size];
        }
    };
}
