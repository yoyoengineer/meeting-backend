package com.meeting.binary.android.binarymeeting.model;

import java.io.Serializable;

/**
 * Created by loyck-daryl on 19/4/2018.
 */

public class Choice implements Serializable{

    private String bulletinId;
    private String choice;

    public String getBulletinId() {
        return bulletinId;
    }

    public void setBulletinId(String bulletinId) {
        this.bulletinId = bulletinId;
    }

    public String getChoice() {
        return choice;
    }

    public void setChoice(String choice) {
        this.choice = choice;
    }
}
