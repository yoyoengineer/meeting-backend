package com.yoyo.conferenceservice.event.messagedriven;

import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;

public interface MyChannels {


    @Output("conferenceEvent")
    MessageChannel publishConferenceEvent();

    @Output("activities")
    MessageChannel announceToUser();

}
