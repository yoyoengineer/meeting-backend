package com.yoyo.conferenceservice.model;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class RequestConferenceConfirm {
    private String username;
    private String eventId;
}
