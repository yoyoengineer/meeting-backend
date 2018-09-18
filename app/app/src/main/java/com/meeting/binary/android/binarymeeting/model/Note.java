package com.meeting.binary.android.binarymeeting.model;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.UUID;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by meldi on 4/4/2018.
 */

public class Note extends RealmObject{

    @PrimaryKey
    private String id;
    private String title;
    private String content;
    private long date;


    public Note() {
        this(UUID.randomUUID().toString());
    }

    public Note(String id) {
        this.id = UUID.randomUUID().toString();
        date = new Date().getTime();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
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
