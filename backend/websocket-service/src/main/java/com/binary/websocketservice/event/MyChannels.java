package com.binary.websocketservice.event;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.messaging.SubscribableChannel;

public interface MyChannels {


    @Input("activities")
    SubscribableChannel announceToUser();
}
