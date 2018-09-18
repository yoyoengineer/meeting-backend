package com.meeting.binary.android.binarymeeting.model;

public class Interact {
    private String liveInteract;
    private String commeentText;

    public Interact(String liveInteract, String commeentText) {
        this.liveInteract = liveInteract;
        this.commeentText = commeentText;
    }

    public Interact() {
    }

    public String getLiveInteract() {
        return liveInteract;
    }

    public void setLiveInteract(String liveInteract) {
        this.liveInteract = liveInteract;
    }

    public String getCommeentText() {
        return commeentText;
    }

    public void setCommeentText(String commeentText) {
        this.commeentText = commeentText;
    }
}
