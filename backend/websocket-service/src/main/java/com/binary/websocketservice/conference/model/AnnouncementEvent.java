package com.binary.websocketservice.conference.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.List;

@ToString
@Getter
@NoArgsConstructor(force=true)
@RequiredArgsConstructor
public class AnnouncementEvent {
    private final List<String> usernames;
    private  final boolean toAll;
    private  final Announcement announcement;

    @JsonIgnore
    public boolean isValid(){
        return announcement!=null && announcement.isValid();
    }
}
