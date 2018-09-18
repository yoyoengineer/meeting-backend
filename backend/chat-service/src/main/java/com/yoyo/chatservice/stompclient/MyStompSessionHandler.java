package com.yoyo.chatservice.stompclient;

import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;

@Slf4j
public class MyStompSessionHandler  extends StompSessionHandlerAdapter{

    @Override
    public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
        log.info("New session established: " + session.getSessionId());
        System.out.println("new session is established " + session.getSessionId());

        System.out.println(connectedHeaders);
    }

    @Override
    public void handleException(StompSession session, StompCommand command, StompHeaders headers, byte[] payload, Throwable exception) {
        log.info("Got an exception",exception);
        System.out.println("Got an exception " + exception);
    }

    @Override
    public void handleTransportError(StompSession session, Throwable exception) {
        log.debug("Transport error ", exception);
    }
}
