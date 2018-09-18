package com.meeting.binary.android.binarymeeting.model;


import android.os.Parcel;
import android.os.Parcelable;



import java.io.Serializable;

import io.realm.RealmObject;

public class Contact extends RealmObject implements Parcelable, Serializable{


    private String name;
    private String username;
    private String town;
    private String photo;
    private long lastModified;




    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getTown() {
        return town;
    }

    public void setTown(String town) {
        this.town = town;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public long getLastModified() {
        return lastModified;
    }

    public void setLastModified(long lastModified) {
        this.lastModified = lastModified;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeString(this.username);
        dest.writeString(this.town);
        dest.writeString(this.photo);
        dest.writeLong(this.lastModified);
    }

    public Contact() {
    }

    protected Contact(Parcel in) {
        this.name = in.readString();
        this.username = in.readString();
        this.town = in.readString();
        this.photo = in.readString();
        this.lastModified = in.readLong();
    }

    public static final Parcelable.Creator<Contact> CREATOR = new Parcelable.Creator<Contact>() {
        @Override
        public Contact createFromParcel(Parcel source) {
            return new Contact(source);
        }

        @Override
        public Contact[] newArray(int size) {
            return new Contact[size];
        }
    };
}
