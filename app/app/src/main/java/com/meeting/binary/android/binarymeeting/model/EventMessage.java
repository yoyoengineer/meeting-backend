package com.meeting.binary.android.binarymeeting.model;

/**
 * Created by loyck-daryl on 18/4/2018.
 */

public class EventMessage {
    private String type;
    private String content;

    public EventMessage() {
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
