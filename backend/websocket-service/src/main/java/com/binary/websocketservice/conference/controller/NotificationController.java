package com.binary.websocketservice.conference.controller;

import com.binary.websocketservice.conference.model.NotificationParcel;
import com.binary.websocketservice.conference.service.NotificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class NotificationController {

    private NotificationService notificationService;

    public NotificationController(NotificationService notificationService){
        this.notificationService = notificationService;
    }

    @PostMapping("/websocket/notifications")
    public void notify(@RequestBody NotificationParcel parcel){
        log.debug("In the notify method");
        notificationService.send(parcel);
    }
}
