package com.yoyo.authenticationservice.event.messagedriven;

import com.yoyo.authenticationservice.event.NewUserEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@Slf4j
public class NewUserEventDispatcher {

    private MyChannels myChannels;

    public NewUserEventDispatcher(MyChannels myChannels){
        this.myChannels = myChannels;
    }


    public void publishEvent(NewUserEvent newUserEvent){
        log.debug("received conference event: {}",newUserEvent);

            if(newUserEvent!=null && newUserEvent.getUsername()!=null && !newUserEvent.getUsername().isEmpty()) {
                Message<Map<String, String>> message = MessageBuilder.withPayload(newUserEvent.getUsername()).build();
                log.debug("sending the message to the new User Event channel {}", message);
                myChannels.publishNewUseEvent().send(message);
            }

    }
}
