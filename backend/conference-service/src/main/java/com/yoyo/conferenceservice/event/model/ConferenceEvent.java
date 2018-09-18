package com.yoyo.conferenceservice.event.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.yoyo.conferenceservice.ustils.UtilsClass;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.context.ApplicationEvent;


@ToString
@Getter
@Setter
public class ConferenceEvent extends ApplicationEvent{
    public enum Type{
        NEW_CONFERENCE,
        UPDATE, // To be implemented with the controller
        PARTICIPATE,
        ATTEND,
        DISENGAGE
    }


    private Type type;
    private String content;

    public ConferenceEvent(Object source, String content,Type type){
        super(source);
        this.content = content;
        this.type = type;
    }

    @JsonIgnore
    public boolean isValid(){
        return UtilsClass.isStringNotNullOrEmpty(content)
                && type!=null;
    }


}
