package com.meeting.binary.android.binarymeeting.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

/**
 * Created by loyck-daryl on 2018/3/16.
 */

public class Event implements Parcelable, Serializable{

    private String id;
    private String topic;
    private long time;
    private int likes;
    private int attendees;
    private String description;
    private boolean eventPublicState;



    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }


    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }


    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public int getAttendees() {
        return attendees;
    }

    public void setAttendees(int attendees) {
        this.attendees = attendees;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isEventPublicState() {
        return eventPublicState;
    }

    public void setEventPublicState(boolean eventPublicState) {
        this.eventPublicState = eventPublicState;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.topic);
        dest.writeLong(this.time);
        dest.writeInt(this.likes);
        dest.writeInt(this.attendees);
        dest.writeString(this.description);
        dest.writeByte(this.eventPublicState ? (byte) 1 : (byte) 0);
    }

    public Event() {
    }

    protected Event(Parcel in) {
        this.id = in.readString();
        this.topic = in.readString();
        this.time = in.readLong();
        this.likes = in.readInt();
        this.attendees = in.readInt();
        this.description = in.readString();
        this.eventPublicState = in.readByte() != 0;
    }

    public static final Parcelable.Creator<Event> CREATOR = new Parcelable.Creator<Event>() {
        @Override
        public Event createFromParcel(Parcel source) {
            return new Event(source);
        }

        @Override
        public Event[] newArray(int size) {
            return new Event[size];
        }
    };

    @Override
    public String toString() {
        return "Event{" +
                "id='" + id + '\'' +
                ", topic='" + topic + '\'' +
                ", time='" + time + '\'' +
                ", likes=" + likes +
                ", attendees=" + attendees +
                ", description='" + description + '\'' +
                ", eventPublicState=" + eventPublicState +
                '}';
    }

    public String getReadableModifyDate(long noteDate){
        Calendar calendar = new GregorianCalendar();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM dd, yyyy -h:mm a", Locale.getDefault());
        simpleDateFormat.setTimeZone(calendar.getTimeZone());
        calendar.setTimeInMillis(noteDate);
        Date modifiedDate = calendar.getTime();
        return simpleDateFormat.format(modifiedDate);
    }
}
