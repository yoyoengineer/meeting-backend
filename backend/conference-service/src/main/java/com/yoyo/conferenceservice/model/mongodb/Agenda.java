package com.yoyo.conferenceservice.model.mongodb;

import com.datastax.driver.core.utils.UUIDs;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.kanz.conferenceservice.ustils.UtilsClass;
import com.yoyo.conferenceservice.ustils.UtilsClass;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;


@Document
@Getter
@Setter
@NoArgsConstructor
@ToString
public class Agenda {

    @Id
    private String id;
    private  String eventId;
    private  String text;
    private Long time;
    private Long startTime;
    private Long endTime;
    private String title;
    private String content;
    private String location;


    @JsonIgnore
    public boolean isValid(){
        return UtilsClass.isStringNotNullOrEmpty(eventId,title)
                && startTime!=null && endTime!=null;
    }
}
