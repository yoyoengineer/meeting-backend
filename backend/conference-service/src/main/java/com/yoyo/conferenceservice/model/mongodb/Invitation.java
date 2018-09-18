package com.yoyo.conferenceservice.model.mongodb;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
@Getter
@Setter
@NoArgsConstructor
public class Invitation {

    @Id private String id;
    private String username;
    private String eventId;

    public Invitation withUsername(String username){
        this.username = username;
        return this;
    }

    public Invitation withEventId(String eventId){
        this.eventId = eventId;
        return this;
    }
}
