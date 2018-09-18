package com.binary.websocketservice.event;

import com.binary.websocketservice.conference.model.ActivityEvent;
import com.binary.websocketservice.conference.model.AnnouncementEvent;
import com.binary.websocketservice.conference.model.Notification;
import com.binary.websocketservice.conference.service.ConferenceService;
import com.binary.websocketservice.utils.Destinations;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@EnableBinding(MyChannels.class)
@Slf4j
public class MyEventHandlers {

    private ConferenceService conferenceService;
    private SimpMessagingTemplate simpMessagingTemplate;

    public MyEventHandlers(ConferenceService conferenceService,
                           SimpMessagingTemplate simpMessagingTemplate){
        this.conferenceService = conferenceService;
        this.simpMessagingTemplate = simpMessagingTemplate;
    }
    @StreamListener("activities")
    public void getActivity(ActivityEvent activityEvent){
        log.debug("Here is the  activity event I have received {}",activityEvent);

        if(activityEvent!=null && activityEvent.isValid()){
            Map<String,Object>  payload = new HashMap<>();
            payload.put("type",activityEvent.getType());
            payload.put("content",activityEvent.getContent());
            if(activityEvent.isToAll()){
                log.debug("Sending to the public : {}",Destinations.Event.publicEvent(activityEvent.getEventId()));
                simpMessagingTemplate.convertAndSend(
                        Destinations.Event.publicEvent(activityEvent.getEventId()),
                        payload);
            }

            else {

                List<String> toUsers = activityEvent.getUsernames();

                log.debug("Sending to the private");
                if(toUsers!=null) {
                    toUsers.forEach(toUser-> {
                        log.debug("sending  message {} to user {}",activityEvent.getUsernames(),toUser);
                        // Send the message to each person on the list.
                        simpMessagingTemplate.convertAndSendToUser(
                                toUser,
                                Destinations.Event.privateEvent(activityEvent.getEventId()),
                                payload);

                    });
                }
            }
        }
//        switch(activityEvent.getType()){
//            case BULLET:  conference
//        }
    }
}
