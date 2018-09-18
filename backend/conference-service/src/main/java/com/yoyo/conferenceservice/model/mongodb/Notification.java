package com.yoyo.conferenceservice.model.mongodb;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.kanz.conferenceservice.ustils.UtilsClass;
import com.yoyo.conferenceservice.ustils.UtilsClass;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
@Getter
@NoArgsConstructor
@ToString
public class Notification {
    public enum Type{
        INVITATION,
        MODIFICATION
    }

    @Id
    private String id;
    @Indexed
    private String username;
    private String eventId;
    private String fieldName;
    private Object fieldValue;
    private Type type;

    @Transient
    private String topic;

    @JsonIgnore
    public Notification withUsername(String username){
        this.username = username;
        return this;
    }

    @JsonIgnore
    public Notification withEventId(String eventId){
        this.eventId = eventId;
        return this;
    }

    @JsonIgnore
    public Notification withFieldName(String fieldName){
        this.fieldName = fieldName;
        return this;
    }

    @JsonIgnore
    public Notification withFieldValue(Object fieldValue){
        this.fieldValue = fieldValue;
        return this;
    }

    @JsonIgnore
    public Notification withType(Type type){
        this.type = type;
        return this;
    }

    @JsonIgnore
    public Notification withTopic(String topic){
        this.topic = topic;
        return this;
    }
    @JsonIgnore
    public boolean isValid(){
        boolean valid = UtilsClass.isStringNotNullOrEmpty(eventId,username);

        if(valid && UtilsClass.isStringNotNullOrEmpty(fieldName)){
            valid = fieldValue!=null;
        }

        return valid;
    }


}
