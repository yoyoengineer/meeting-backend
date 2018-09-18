package com.meeting.binary.android.binarymeeting.model;

import com.orm.SugarRecord;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by yosir on 2018/3/25.
 */

//public class Agenda extends SugarRecord implements Serializable{
public class Agenda implements Serializable{

    private String id;
    private String text;
    private Long Time;
    private String eventId;
    private Long startTime;
    private Long endTime;
    private String title;
    private String content;
    private String location;

    public Agenda(String id, String text, Long time, String eventId, Long startTime, Long endTime, String title, String content, String location) {
        this.id = id;
        this.text = text;
        Time = time;
        this.eventId = eventId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.title = title;
        this.content = content;
        this.location = location;
    }

    public Agenda() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Long getTime() {
        return Time;
    }

    public void setTime(Long time) {
        Time = time;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public Long getStartTime() {
        return startTime;
    }

    public void setStartTime(Long startTime) {
        this.startTime = startTime;
    }

    public Long getEndTime() {
        return endTime;
    }

    public void setEndTime(Long endTime) {
        this.endTime = endTime;
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

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
