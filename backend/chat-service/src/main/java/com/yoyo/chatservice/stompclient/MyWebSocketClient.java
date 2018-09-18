package com.yoyo.chatservice.stompclient;

import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompSessionHandler;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import java.util.Base64;

@Slf4j
public class MyWebSocketClient{

    private final static String URL = "ws://localhost:8080/stompwebsocket";
    private static WebSocketStompClient  stompClient = null;

    public  synchronized static WebSocketStompClient getInstance() {

        if(stompClient!=null && stompClient.isRunning())
            return stompClient;

        System.out.println("Running for the chat service");

        WebSocketClient client = new StandardWebSocketClient();
        WebSocketStompClient newStompClient = new WebSocketStompClient(client);

        WebSocketHttpHeaders headers = new WebSocketHttpHeaders();
        String auth = "websocket" + ":" + "password";
        headers.add("Authorization", "Basic " + new String(Base64.getEncoder().encode(auth.getBytes())));

        newStompClient.setMessageConverter(new MappingJackson2MessageConverter());
        log.debug("Connecting to the ws");
        StompSessionHandler sessionHandler = new MyStompSessionHandler();
        newStompClient.connect(URL,headers,sessionHandler);
        return  newStompClient;
    }

//    public synchronized static boolean disconnect(){
//        if(stompClient!=null)
//            stompClient.stop();
//    }
}
