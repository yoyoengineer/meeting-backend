package com.meeting.binary.android.binarymeeting.model;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

public class FileModel {

    private String fileName;
    private String fileId;
    private String contentType;
    private Long size;
    private Long time;
    private String owner;
    private String eventId;

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
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
