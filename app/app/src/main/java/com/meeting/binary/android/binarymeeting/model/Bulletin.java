package com.meeting.binary.android.binarymeeting.model;


import android.os.Parcel;
import android.os.Parcelable;

import com.meeting.binary.android.binarymeeting.utils.UtilsClass;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Bulletin implements Parcelable, Serializable {

    private String id;
    private String question;
    private List<String> options;
    private String eventId;
    private String myChoice;
    private Map<String,Integer> results;
    private boolean userVoted;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public List<String> getOptions() {
        return options;
    }

    public void setOptions(List<String> options) {
        this.options = options;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getMyChoice() {
        return myChoice;
    }

    public void setMyChoice(String myChoice) {
        this.myChoice = myChoice;
    }

    public Map<String, Integer> getResults() {
        return results;
    }

    public void setResults(Map<String, Integer> results) {
        this.results = results;
    }

    public boolean isUserVoted() {
        return userVoted;
    }

    public void setUserVoted(boolean userVoted) {
        this.userVoted = userVoted;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.question);
        dest.writeStringList(this.options);
        dest.writeString(this.eventId);
        dest.writeString(this.myChoice);
        dest.writeInt(this.results.size());
        for (Map.Entry<String, Integer> entry : this.results.entrySet()) {
            dest.writeString(entry.getKey());
            dest.writeValue(entry.getValue());
        }
        dest.writeByte(this.userVoted ? (byte) 1 : (byte) 0);
    }

    public Bulletin() {
    }

    protected Bulletin(Parcel in) {
        this.id = in.readString();
        this.question = in.readString();
        this.options = in.createStringArrayList();
        this.eventId = in.readString();
        this.myChoice = in.readString();
        int resultsSize = in.readInt();
        this.results = new HashMap<String, Integer>(resultsSize);
        for (int i = 0; i < resultsSize; i++) {
            String key = in.readString();
            Integer value = (Integer) in.readValue(Integer.class.getClassLoader());
            this.results.put(key, value);
        }
        this.userVoted = in.readByte() != 0;
    }

    public static final Parcelable.Creator<Bulletin> CREATOR = new Parcelable.Creator<Bulletin>() {
        @Override
        public Bulletin createFromParcel(Parcel source) {
            return new Bulletin(source);
        }

        @Override
        public Bulletin[] newArray(int size) {
            return new Bulletin[size];
        }
    };
}
