package com.meeting.binary.android.binarymeeting.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.List;

/**
 * Created by loyck-daryl on 2018/3/16.
 */

public class MyContactPage implements Parcelable , Serializable{

    private String username;
    private String name;
    private String aboutMe;
    private String town;
    private String email;
    private String companyName;
    private String photo;
    private String currentTown;
    private String homeTown;


    public String getCurrentTown() {
        return currentTown;
    }

    public void setCurrentTown(String currentTown) {
        this.currentTown = currentTown;
    }

    public String getHomeTown() {
        return homeTown;
    }

    public void setHomeTown(String homeTown) {
        this.homeTown = homeTown;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAboutMe() {
        return aboutMe;
    }

    public void setAboutMe(String aboutMe) {
        this.aboutMe = aboutMe;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getTown() {
        return town;
    }

    public void setTown(String town) {
        this.town = town;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }


    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.username);
        dest.writeString(this.name);
        dest.writeString(this.aboutMe);
        dest.writeString(this.town);
        dest.writeString(this.email);
        dest.writeString(this.companyName);
        dest.writeString(this.photo);
        dest.writeString(this.currentTown);
        dest.writeString(this.homeTown);
    }

    public MyContactPage() {
    }

    protected MyContactPage(Parcel in) {
        this.username = in.readString();
        this.name = in.readString();
        this.aboutMe = in.readString();
        this.town = in.readString();
        this.email = in.readString();
        this.companyName = in.readString();
        this.photo = in.readString();
        this.currentTown = in.readString();
        this.homeTown = in.readString();
    }

    public static final Parcelable.Creator<MyContactPage> CREATOR = new Parcelable.Creator<MyContactPage>() {
        @Override
        public MyContactPage createFromParcel(Parcel source) {
            return new MyContactPage(source);
        }

        @Override
        public MyContactPage[] newArray(int size) {
            return new MyContactPage[size];
        }
    };
}
