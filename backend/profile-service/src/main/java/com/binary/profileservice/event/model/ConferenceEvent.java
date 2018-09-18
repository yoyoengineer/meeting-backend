package com.binary.profileservice.event.model;

import com.binary.profileservice.ustils.UtilsClass;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.context.ApplicationEvent;


@ToString
@Getter
@Setter
public class ConferenceEvent{
    public enum Type{
        NEW_CONFERENCE,
        UPDATE, // To be implemented with the controller
        PARTICIPATE,
        ATTEND,
        DISENGAGE
    }

    private Type type;
    private String content;

    @JsonIgnore
    public boolean isValid(){
        return UtilsClass.isStringNotNullOrEmpty(content)
                && type!=null;
    }


}
