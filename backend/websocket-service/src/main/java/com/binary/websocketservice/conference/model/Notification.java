package com.binary.websocketservice.conference.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.List;

@Getter
@NoArgsConstructor(force=true)
@RequiredArgsConstructor
@ToString
public class Notification {
    public enum Type{
        PARTICIPATES_TO,
        ATTENDS,
        LIKES,
        INVITED_TO
    }

    private final Type type;
    private final String eventId;
    private final List<String> usernames;

    @JsonIgnore
    public boolean isValid(){
        return type!=null
                && eventId!=null && !eventId.isEmpty()
                && usernames!=null && !usernames.isEmpty();
    }
}
