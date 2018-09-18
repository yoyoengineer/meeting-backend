package com.binary.websocketservice.conference.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor(force=true)
@RequiredArgsConstructor
public class Announcement {
    private final String eventId;
    private final String uuidTime;
    private final String text;
    private Long time;

    @JsonIgnore
    public boolean isValid(){
        return eventId!=null &&
                !eventId.isEmpty() &&
                text!=null &&
                !text.isEmpty();
    }
}
