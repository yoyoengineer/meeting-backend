package com.yoyo.authenticationservice.event.messagedriven;

import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;

public interface MyChannels {


    @Output("newUserEvent")
    MessageChannel publishNewUseEvent();


}
