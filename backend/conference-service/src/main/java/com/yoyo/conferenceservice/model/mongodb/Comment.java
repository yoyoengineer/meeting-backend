package com.yoyo.conferenceservice.model.mongodb;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.kanz.conferenceservice.ustils.UtilsClass;
import com.yoyo.conferenceservice.ustils.UtilsClass;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@ToString
@Getter
@Setter
@Document
public class Comment {
    @Id private String id;
    private String username;
    private String name;
    @Indexed private String eventId;
    private String text;
    private long time;

    public Comment(){
        time = new Date().getTime();
    }

    @JsonIgnore
    public void setTime(){
        time = new Date().getTime();
    }

    @JsonIgnore
    public boolean isValid(){
        return UtilsClass.isStringNotNullOrEmpty(username,eventId,text);
    }

}
