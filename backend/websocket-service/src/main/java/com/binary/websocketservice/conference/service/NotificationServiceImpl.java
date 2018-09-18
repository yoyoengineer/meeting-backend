package com.binary.websocketservice.conference.service;

import com.binary.websocketservice.conference.model.NotificationParcel;
import com.binary.websocketservice.conference.service.NotificationService;
import com.binary.websocketservice.utils.Destinations;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class NotificationServiceImpl implements NotificationService {

    private SimpMessagingTemplate simpMessagingTemplate;

    public NotificationServiceImpl(SimpMessagingTemplate simpMessagingTemplate){
        this.simpMessagingTemplate = simpMessagingTemplate;
    }

    @Override
    public void send(NotificationParcel notificationParcel) {
        log.debug("Received notification {}",notificationParcel);
        if(notificationParcel.isValid()){
            List<String> receivers = notificationParcel.getReceivers();
            Map<String,String> notification = notificationParcel.getNotification();

            if(receivers!=null){
                receivers.forEach( receiver-> {
                    if (receiver != null) {
                        simpMessagingTemplate.convertAndSendToUser(
                                receiver,
                                Destinations.Notification.notification(),
                                notification
                        );
                    }
                });
            }
        }
    }


}
