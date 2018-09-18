package com.yoyo.conferenceservice.model.mongodb;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.kanz.conferenceservice.model.Location;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexType;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexed;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@NoArgsConstructor(force=true)
@ToString
@Setter
@AllArgsConstructor
@Document
public class Conference {

    @Id
    private String id;
    private String topic="";
    private boolean meSpeaker;

    @Indexed
    private String username;   // To be added later

    private List<String> otherSpeakers = new ArrayList<>();
    private String description ="";
    private Long time = 0L;
    private String specificAddress;


    @JsonIgnore
    private String address;

    @GeoSpatialIndexed(type= GeoSpatialIndexType.GEO_2DSPHERE)
    @JsonIgnore
    private GeoJsonPoint loc;

    @JsonIgnore
    private Set<String> pendingParticipations = new HashSet<>();

    @JsonIgnore
    private Set<String> pendingAttendances = new HashSet<>();

    @Transient
    private Location location = null;


    private Integer capacity = 0;
    private boolean eventPublicState = true;

    @Transient private List<String> invitees;
//    private Integer nbOfAttendees = 0; // To be collected from the relationship service
    @JsonIgnore private Integer remainingNbOfParticipants =0;



    @JsonIgnore
    public boolean isValid(){

        return (meSpeaker || (otherSpeakers!=null && !otherSpeakers.isEmpty()))
                && topic!=null
                && !topic.isEmpty()
                && time !=0
                && location!=null && location.isValid()
                && capacity!=0;
    }


    @JsonIgnore
    public void initializeAddressAndGeoPoint(){
        if(location.isValid()){
            address = location.getAddress();
            loc = new GeoJsonPoint(location.getLongitude(),location.getLatitude());
        }
    }


    @JsonIgnore
    public void initializeLocation(){
        location = new Location();
        location.setAddress(address);
        if(loc!=null) {
            location.setLongitude(loc.getX());
            location.setLatitude(loc.getY());
        }
    }

}
