package com.binary.profileservice.event;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.messaging.SubscribableChannel;

public interface MyChannels {

    @Input("conferenceEvent")
    SubscribableChannel conferenceEvents();

    @Input("newUserEvent")
    SubscribableChannel newUserEvents();

}
