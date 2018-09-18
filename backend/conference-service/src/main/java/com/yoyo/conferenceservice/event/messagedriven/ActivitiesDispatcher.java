package com.yoyo.conferenceservice.event.messagedriven;

import com.kanz.conferenceservice.event.model.ActivityEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ActivitiesDispatcher {

    private MyChannels myChannels;

    public ActivitiesDispatcher(MyChannels myChannels){
        this.myChannels = myChannels;
    }


    public void publishActivity(ActivityEvent activityEvent){
        log.debug("received activity event: {}",activityEvent);

        if(activityEvent.isValid()){
            Message<ActivityEvent> message = MessageBuilder.withPayload(activityEvent).build();
            log.debug("sending the message to the activity channel (websocket) {}",message);
            myChannels.announceToUser().send(message);
        }
    }



}
