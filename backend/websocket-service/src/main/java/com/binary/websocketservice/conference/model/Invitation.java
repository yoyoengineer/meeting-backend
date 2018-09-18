package com.binary.websocketservice.conference.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Getter
@NoArgsConstructor(force=true)
@RequiredArgsConstructor
public class Invitation {
    private final String username;
    private final String eventId;

}
