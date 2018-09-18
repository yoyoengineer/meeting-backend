package com.binary.websocketservice.websocketconfiguration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

@Component
@Slf4j
public class WebSocketEvents {

    @EventListener
    private void handleSessionConnected(SessionConnectEvent event){
        String username = event.getUser().getName();
        log.debug("{} is connected",username);
    }

    @EventListener
    private void handleSessionDisconnect(SessionDisconnectEvent event){
        String username = event.getUser().getName();
        log.debug("{}  has just left",username);
    }

    @EventListener
    private void handleSubscription(SessionSubscribeEvent event){
        log.debug("Subscription {}",event);
    }
}
