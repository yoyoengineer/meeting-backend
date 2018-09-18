package com.yoyo.conferenceservice.event.messagedriven;

import com.kanz.conferenceservice.event.model.ConferenceEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ConferenceEventDispatcher {

    private MyChannels myChannels;

    public ConferenceEventDispatcher(MyChannels myChannels){
        this.myChannels = myChannels;
    }


    public void publishEvent(ConferenceEvent conferenceEvent){
        log.debug("received conference event: {}",conferenceEvent);

            Message<ConferenceEvent> message = MessageBuilder.withPayload(conferenceEvent).build();
            log.debug("sending the message to the conference Event channel {}",message);
            myChannels.publishConferenceEvent().send(message);

    }
}
