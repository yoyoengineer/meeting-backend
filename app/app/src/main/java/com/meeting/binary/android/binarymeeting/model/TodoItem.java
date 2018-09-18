package com.meeting.binary.android.binarymeeting.model;


import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.UUID;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by meldi on 3/25/2018.
 */

public class TodoItem extends RealmObject implements Parcelable {


    @PrimaryKey
    private String id;
    private long taskDate;
    private String title;
    private boolean done;

    public TodoItem(String id) {
        this.id = id;
        taskDate = new Date().getTime();
    }

    public TodoItem() {
        this(UUID.randomUUID().toString());
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public long getTaskDate() {
        return taskDate;
    }

    public void setTaskDate(long taskDate) {
        this.taskDate = taskDate;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }


    public String getReadableModifyDate(long taskDate){
        Calendar calendar = new GregorianCalendar();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM dd, yyyy -h:mm a", Locale.getDefault());
        simpleDateFormat.setTimeZone(calendar.getTimeZone());
        calendar.setTimeInMillis(taskDate);
        Date modifiedDate = calendar.getTime();
        return simpleDateFormat.format(modifiedDate);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeLong(this.taskDate);
        dest.writeString(this.title);
        dest.writeByte(this.done ? (byte) 1 : (byte) 0);
    }

    protected TodoItem(Parcel in) {
        this.id = in.readString();
        this.taskDate = in.readLong();
        this.title = in.readString();
        this.done = in.readByte() != 0;
    }

    public static final Parcelable.Creator<TodoItem> CREATOR = new Parcelable.Creator<TodoItem>() {
        @Override
        public TodoItem createFromParcel(Parcel source) {
            return new TodoItem(source);
        }

        @Override
        public TodoItem[] newArray(int size) {
            return new TodoItem[size];
        }
    };
}
