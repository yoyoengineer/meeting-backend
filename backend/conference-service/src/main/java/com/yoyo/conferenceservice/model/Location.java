package com.yoyo.conferenceservice.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.kanz.conferenceservice.ustils.UtilsClass;
import com.yoyo.conferenceservice.ustils.UtilsClass;
import lombok.*;


@Getter
@Setter
@NoArgsConstructor(force=true)
@AllArgsConstructor
@ToString
public class Location {
    private  String address;
    private  Double longitude;
    private  Double latitude;
    private  String town;


    @Override
    public Location clone(){
        return new Location(address, latitude, longitude, town);
    }

    @JsonIgnore
    public boolean isValid(){
        return UtilsClass.isStringNotNullOrEmpty(address,town)
                && hasValidateCoordinates();

    }

    @JsonIgnore
    public boolean hasValidateCoordinates(){

        return   longitude!=null
                && latitude!=null
                &&( -90.0 <= latitude) && (latitude <=90.0)
                && (-180.0 <=longitude) && (longitude <=180.0);
    }



}
