package com.meeting.binary.android.binarymeeting.model;

import java.util.Map;

public class Lottery {
    private String id;
    private Map<String,Integer> items;
    private String eventId;
    private String qrCodeId;

    public Lottery() {
    }

    public Lottery(String id, Map<String, Integer> items, String eventId, String qrCodeId) {
        this.id = id;
        this.items = items;
        this.eventId = eventId;
        this.qrCodeId = qrCodeId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Map<String, Integer> getItems() {
        return items;
    }

    public void setItems(Map<String, Integer> items) {
        this.items = items;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getQrCodeId() {
        return qrCodeId;
    }

    public void setQrCodeId(String qrCodeId) {
        this.qrCodeId = qrCodeId;
    }

    @Override
    public String toString() {
        return "Lottery{" +
                "id='" + id + '\'' +
                ", items=" + items +
                ", eventId='" + eventId + '\'' +
                ", qrCodeId='" + qrCodeId + '\'' +
                '}';
    }
}
